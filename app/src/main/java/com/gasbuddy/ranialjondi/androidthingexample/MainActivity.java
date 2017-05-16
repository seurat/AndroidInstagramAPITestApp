package com.gasbuddy.ranialjondi.androidthingexample;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.londatiga.android.instagram.Instagram;
import net.londatiga.android.instagram.InstagramRequest;
import net.londatiga.android.instagram.InstagramSession;
import net.londatiga.android.instagram.InstagramUser;
import net.londatiga.android.instagram.util.Cons;
import net.londatiga.android.instagram.util.Debug;
import net.londatiga.android.instagram.util.StringUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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
	
	private ProgressBar mLoadingPb;
	private GridView mGridView;
	
	private static final String CLIENT_ID = "87c0d8595b25432c9c4b6c7c7fbbd473";
    private static final String CLIENT_SECRET = "8d692911f5404356b68338194b2b9bda";
    private static final String REDIRECT_URI = "http://localhost";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mInstagram  		= new Instagram(this, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);
		
		mInstagramSession	= mInstagram.getSession();

		if (mInstagramSession.isActive()) {
		//gallery fragment
			InstagramUser instagramUser = mInstagramSession.getUser();

			setContentView(R.layout.activity_user);

			mLoadingPb 	= (ProgressBar) findViewById(R.id.pb_loading);
			mGridView	= (GridView) findViewById(R.id.gridView);
			mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					getFragmentManager().beginTransaction()
							.add(R.id.gallery_container,
									GalleryFragment.newInstance(),
									GalleryFragment.FRAGMENT_TAG)
							.addToBackStack(GalleryFragment.FRAGMENT_TAG)
							.commit();

				}
			});
			((TextView) findViewById(R.id.tv_name)).setText(instagramUser.fullName);
			((TextView) findViewById(R.id.tv_username)).setText(instagramUser.username);

			((Button) findViewById(R.id.btn_logout)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mInstagramSession.reset();

					startActivity(new Intent(MainActivity.this, MainActivity.class));

					finish();
				}
			});

			ImageView userIv = (ImageView) findViewById(R.id.iv_user);

			DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_user)
					.showImageForEmptyUri(R.drawable.ic_user)
					.showImageOnFail(R.drawable.ic_user)
					.cacheInMemory(true)
					.cacheOnDisc(false)
					.considerExifParams(true)
					.build();
    
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)                		                       
			        .writeDebugLogs()
			        .defaultDisplayImageOptions(displayOptions)		        
			        .build();
		
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);

			AnimateFirstDisplayListener animate  = new AnimateFirstDisplayListener();

			imageLoader.displayImage(instagramUser.profilPicture, userIv, animate);

			new DownloadTask().execute();
			
		} else {

			//fragment login
			setContentView(R.layout.activity_main);
			
			((Button) findViewById(R.id.btn_connect)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {					
					mInstagram.authorize(mAuthListener);	
				}
			});
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

				//String requestUri = Cons.API_BASE_URL + ((endpoint.indexOf("/") == 0) ? endpoint : "/" + endpoint);
				//-->post(requestUrl, params);
				//-->URL url = new URL("http://some-server");
				//-->HttpURL
				/*

				//replacement zone:
				public String createRequest(String method, String endpoint, List<NameValuePair> params) throws Exception {
					if (method.equals("POST")) {
						return requestPost(endpoint, params);
					} else {
						return requestGet(endpoint, params);
					}
				}

				private String requestPost(String endpoint, List<NameValuePair> params) throws Exception {
					String requestUri = Cons.API_BASE_URL + ((endpoint.indexOf("/") == 0) ? endpoint : "/" + endpoint);

					return post(requestUri, params);
				}

				public String post(String requestUrl, List<NameValuePair> params) throws Exception {
					InputStream stream 	= null;
					String response	= "";

					try {
						if (!mAccessToken.equals("")) {
							if (params == null) {
								params = new ArrayList<NameValuePair>(1);

								params.add(new BasicNameValuePair("access_token", mAccessToken));
							} else {
								params.add(new BasicNameValuePair("access_token", mAccessToken));
							}
						}

						Debug.i("POST " + requestUrl);

						HttpClient httpClient 	= new DefaultHttpClient();
						HttpPost httpPost 		= new HttpPost(requestUrl);

						httpPost.setEntity(new UrlEncodedFormEntity(params));
						URL url = new URL("http://localhost");
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.set
						HttpResponse httpResponse 	= httpClient.execute(httpPost);
						HttpEntity httpEntity 		= httpResponse.getEntity();

						if (httpEntity == null) {
							throw new Exception("Request returns empty result");
						}

						stream		= httpEntity.getContent();
						response	= StringUtil.streamToString(stream);

						Debug.i("Response " + response);

						if (httpResponse.getStatusLine().getStatusCode() != 200) {
							throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
						}
					} catch (Exception e) {
						throw e;
					}

					return response;
				}

				*/

				if (!response.equals("")) {
    				JSONObject jsonObj  = (JSONObject) new JSONTokener(response).nextValue();    				
    				JSONArray jsonData	= jsonObj.getJSONArray("data");
    				
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
        	mLoadingPb.setVisibility(View.GONE);
        	
        	if (photoList == null) {
        		Toast.makeText(getApplicationContext(), "No Photos Available", Toast.LENGTH_LONG).show();
        	} else {
        		DisplayMetrics dm = new DisplayMetrics();
        		
        		getWindowManager().getDefaultDisplay().getMetrics(dm);
        	       
        		int width 	= (int) Math.ceil((double) dm.widthPixels / 2);
        		width=width-50;
        		int height	= width;
        	
        		PhotoListAdapter adapter = new PhotoListAdapter(MainActivity.this);
        		
        		adapter.setData(photoList);
        		adapter.setLayoutParam(width, height);

        	
        		mGridView.setAdapter(adapter);
        	}
        }                
    }
}