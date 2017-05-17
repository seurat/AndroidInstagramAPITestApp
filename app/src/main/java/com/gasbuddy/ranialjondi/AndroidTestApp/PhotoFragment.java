package com.gasbuddy.ranialjondi.AndroidTestApp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by ranialjondi on 5/16/17.
 */

public class PhotoFragment extends Fragment {

    View mView;
    MainActivity mainActivity;
    Bundle bundle;
    private ProgressBar mLoadingPb;
    private ImageView imageIv;
    private ImageLoader mImageLoader;
    private MainActivity.AnimateFirstDisplayListener mAnimator;


    public static String FRAGMENT_TAG = "PhotoFragment";

    public static PhotoFragment newInstance() {

        return new PhotoFragment();
    }

    public PhotoFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_photo, container, false);

        Bundle bundle = this.getArguments();

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

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);

        MainActivity.AnimateFirstDisplayListener animate  = new MainActivity.AnimateFirstDisplayListener();

        imageIv = (ImageView) mView.findViewById(R.id.iv_image_detail);

        mImageLoader.displayImage(bundle.getString("url"), imageIv, mAnimator);


        ((TextView)(mView.findViewById(R.id.tv_image_caption))).setText(bundle.getString("caption"));

        String tagsString = bundle.getString("tags");

        ((TextView)(mView.findViewById(R.id.tv_image_tags))).setText(tagsString);

        int likesCount = bundle.getInt("likes");

        ((TextView)(mView.findViewById(R.id.tv_image_likes))).setText(mView.getResources().
                getString(R.string.like_count_format_string, likesCount+""));

        return mView;
    }

}
