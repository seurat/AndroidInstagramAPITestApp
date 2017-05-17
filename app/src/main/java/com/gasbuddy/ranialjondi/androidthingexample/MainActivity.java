/**
 * Libraries and Jars used for app: AndroidInstagram by lorensiuswlt and
 * Android-Universal-Image-Loader by nostra13
 */

package com.gasbuddy.ranialjondi.androidthingexample;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.londatiga.android.instagram.Instagram;
import net.londatiga.android.instagram.InstagramRequest;
import net.londatiga.android.instagram.InstagramSession;
import net.londatiga.android.instagram.InstagramUser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Instagram authentication.
 *  
 * @author Lorensius W. L. T
 *
 */
public class MainActivity extends Activity {
	private InstagramSession mInstagramSession;
	private Instagram mInstagram;
	private ArrayList<String> mImageList;
	private JSONArray mImageDetails;
	private DownloadTask mTask;
	private static final String CLIENT_ID = "87c0d8595b25432c9c4b6c7c7fbbd473";
    private static final String CLIENT_SECRET = "8d692911f5404356b68338194b2b9bda";
    private static final String REDIRECT_URI = "http://localhost";

	public boolean isSessionActive() {
		return mInstagramSession.isActive();
	}

	public String getAccessToken() {
		return mInstagramSession.getAccessToken();
	}

	public void startLoginProcess() {
		mInstagram.authorize(mAuthListener);
	}

	public void resetSession() {
		mInstagramSession.reset();
	}

	public InstagramUser getUser() {
		return mInstagramSession.getUser();
	}

	public void runAsyncTask(AsyncTask<URL, Integer, Long>  task) {
		task.execute();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mInstagram  		= new Instagram(this, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);
		mInstagramSession	= mInstagram.getSession();


		//there must not be a way for someone to get to the main page from login page.
		if (mInstagramSession.isActive()) {
			InstagramUser instagramUser = mInstagramSession.getUser();

			mTask = new DownloadTask();
			mTask.execute();



			//gallery fragment

		} else {
			this.getFragmentManager()
					.beginTransaction()
					.add(R.id.login_container,
							LoginFragment.newInstance(),
							LoginFragment.FRAGMENT_TAG)
					.addToBackStack(LoginFragment.FRAGMENT_TAG)
					.commit();
		}
	}
	
	private void showToast(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}
	
	private Instagram.InstagramAuthListener mAuthListener = new Instagram.InstagramAuthListener() {			
		@Override
		public void onSuccess(InstagramUser user) {
			finish();

			startActivity(new Intent(MainActivity.this, MainActivity.class));

		}
					
		@Override
		public void onError(String error) {		
			showToast(error);
		}

		@Override
		public void onCancel() {
			showToast("OK. Maybe later?");
			
		}
	};
			
	public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	public ArrayList<String> getmImageList() {
		return mImageList;
	}

	public class DownloadTask extends AsyncTask<URL, Integer, Long> {
		ArrayList<String> photoList;

		protected void onCancelled() {

		}

		protected void onPreExecute() {

		}

		protected Long doInBackground(URL... urls) {
			long result = 0;

			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>(1);
				params.add(new BasicNameValuePair("count", "10"));

				InstagramRequest request = new InstagramRequest(mInstagramSession.getAccessToken());
				String response			 = request.createRequest("GET", "/users/self/media/recent", params);

				if (!response.equals("")) {
					JSONObject jsonObj  = (JSONObject) new JSONTokener(response).nextValue();
					JSONArray jsonData	= jsonObj.getJSONArray("data");
					mImageDetails = jsonData;
					int length = jsonData.length();

					if (length > 0) {
						photoList = new ArrayList<String>();

						for (int i = 0; i < length; i++) {
							JSONObject jsonPhoto = jsonData.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution");
							photoList.add(jsonPhoto.getString("url"));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(Long result) {
			//mLoadingPb.setVisibility(View.GONE);
			if (photoList == null) {
				Toast.makeText(getApplicationContext(), "No Photos Available", Toast.LENGTH_LONG).show();
			} else {
				mImageList = photoList;
			}

			GalleryFragment galleryFragment = new GalleryFragment();
			galleryFragment.getView().setOnKeyListener( new View.OnKeyListener()
			{
				@Override
				public boolean onKey( View v, int keyCode, KeyEvent event )
				{
					if( keyCode == KeyEvent.KEYCODE_BACK )
					{
						finish();
					}
					return false;
				}
			} );

			getFragmentManager()
					.beginTransaction()
					.replace(R.id.gallery_container,
							galleryFragment)
					.addToBackStack(GalleryFragment.FRAGMENT_TAG)
					.commit();

		}
	}

	public JSONArray getImageDetailArray() {
		return mImageDetails;
	}

	@Override
	public void onDestroy() {
		if (mTask != null) {
			mTask.cancel(true);
		}
		super.onDestroy();

	}


}