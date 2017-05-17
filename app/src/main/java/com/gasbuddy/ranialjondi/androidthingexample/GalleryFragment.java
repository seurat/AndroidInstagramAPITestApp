package com.gasbuddy.ranialjondi.androidthingexample;

import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ranialjondi on 5/16/17.
 */

public class GalleryFragment extends Fragment {


    View mView;
    private GridView mGridView;
    private JSONArray imageDetails;

    public static String FRAGMENT_TAG = "GalleryFragment";

    public static GalleryFragment newInstance() {

        return new GalleryFragment();
    }

    public GalleryFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGridView	= (GridView) mView.findViewById(R.id.gridView);



        //For the purposes of drilling into detailed view of photos
        imageDetails = ((MainActivity)getActivity()).getImageDetailArray();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int length = imageDetails.length();

                if (length > 0) {
                    try {
                        //Image URL set
                        JSONObject jsonPhoto = imageDetails.getJSONObject(position).getJSONObject("images")
                                .getJSONObject("standard_resolution");
                        String imageURL = jsonPhoto.getString("url");

                        //Caption value set
                        String imageCaption = imageDetails.getJSONObject(position).
                                get("caption")!=JSONObject.NULL?
                                imageDetails.getJSONObject(position).
                                        getJSONObject("caption").getString("text"):
                                "No caption";

                        //Tags value set
                        JSONArray imageTags = imageDetails.getJSONObject(position).get("tags")!=null?
                                imageDetails.getJSONObject(position).getJSONArray("tags"):
                                new JSONArray();

                        String tagsString;
                        if(imageTags.length()>0) {

                            int tagsLength = imageTags.length();
                            StringBuilder tagsSb = new StringBuilder();

                            for (int i = 0; i < tagsLength; i++) {
                                JSONObject jsonTag = imageTags.getJSONObject(i);
                                tagsSb.append(jsonTag.toString());
                                tagsSb.append(" ");
                            }

                            tagsString = tagsSb.toString();
                        }
                        else tagsString = "No tags";

                        int likesCount = imageDetails.getJSONObject(position).
                                getJSONObject("likes").getInt("count");

                        Bundle bundle = new Bundle();

                        bundle.putString("url", imageURL);
                        bundle.putString("caption", imageCaption);
                        bundle.putString("tags", tagsString);
                        bundle.putInt("likes", likesCount);

                        PhotoFragment photoFragment = new PhotoFragment();
                        photoFragment.setArguments(bundle);

                        getFragmentManager().beginTransaction()
                                .replace(R.id.photo_container, photoFragment)
                                .addToBackStack(PhotoFragment.FRAGMENT_TAG)
                                .commit();

                    } catch(JSONException json) {
                        Log.e("GalleryFragment", ""+json.getMessage());
                        Toast.makeText(getActivity(), "One of your details was not available",Toast.LENGTH_SHORT).show();
                    }

            }
        }});


        ((TextView) mView.findViewById(R.id.tv_name)).setText(((MainActivity)getActivity()).getUser().fullName);
        ((TextView) mView.findViewById(R.id.tv_username)).setText(((MainActivity)getActivity()).getUser().username);


        ArrayList<String> imageList = ((MainActivity)getActivity()).getmImageList();

        //Where gallery is made.
        if(imageList!=null) {

            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = (int) Math.ceil((double) dm.widthPixels / 2);
            width = width - 85;
            int height = width;
            PhotoListAdapter adapter = new PhotoListAdapter(getActivity());
            adapter.setData(((MainActivity) getActivity()).getmImageList());
            adapter.setLayoutParam(width, height);
            mGridView.setAdapter(adapter);

        }


        ((Button) mView.findViewById(R.id.btn_logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ((MainActivity) getActivity()).resetSession(); //then clear back stack and go to log out screen.

                getFragmentManager().beginTransaction()
                        .add(R.id.login_container,
                                LoginFragment.newInstance(),
                                LoginFragment.FRAGMENT_TAG)
                        .addToBackStack(LoginFragment.FRAGMENT_TAG)
                        .commit();

                getActivity().finish();
            }
        });

        ImageView userIv = (ImageView) mView.findViewById(R.id.iv_user);

        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_user)
                .showImageForEmptyUri(R.drawable.ic_user)
                .showImageOnFail(R.drawable.ic_user)
                .cacheInMemory(true)
                .cacheOnDisc(false)
                .considerExifParams(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .writeDebugLogs()
                .defaultDisplayImageOptions(displayOptions)
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        MainActivity.AnimateFirstDisplayListener animate  = new MainActivity.AnimateFirstDisplayListener();

        imageLoader.displayImage(((MainActivity)getActivity()).getUser().profilPicture, userIv, animate);

        return mView;
    }

}
