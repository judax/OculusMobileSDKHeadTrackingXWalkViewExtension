package com.judax.oculusmobilesdkheadtracking.xwalk.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

import com.judax.oculusmobilesdkheadtracking.xwalk.OculusMobileSDKHeadTrackingXWalkViewExtension;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class OculusMobileSDKHeadTrackingXWalkViewActivity extends Activity
{
  private XWalkView crosswalkView = null;
	private OculusMobileSDKHeadTrackingXWalkViewExtension oculusMobileSDKHeadTrackingXWalkViewExtension = null;
	private String oculusMobileSDKHeadTrackingWebVRJSCode = null;
	
	private class CrosswalkResourceClientInjectJSCode extends XWalkResourceClient 
	{
		private String url;
		private String jsCode;
		
		public CrosswalkResourceClientInjectJSCode(XWalkView xwalkView, String url, String jsCode)
		{
			super(xwalkView);
			this.url = url;
			this.jsCode = jsCode;
		}
		
		@Override
		public void onLoadStarted(XWalkView view, String url) 
		{
      super.onLoadStarted(view, url);
      if (url.equals(this.url))
      {
      	view.evaluateJavascript(jsCode, null);
      	System.out.println("JUDAX: JSCode  injected!");
			}
		}				
	}
	
	public class ReadFromURL extends AsyncTask<Void, Void, Void>
	{
		private String url;
		private Runnable executeOnPost;
		
		public ReadFromURL(String url, Runnable executeOnPost)
		{
			this.url = url;
			this.executeOnPost = executeOnPost;
		}
		
		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				oculusMobileSDKHeadTrackingWebVRJSCode = readFromURL(url);
			}
			catch (MalformedURLException e)
			{
				System.err.println("JUDAX: MalformedURLException while reading the '" + url + "' file.");
				e.printStackTrace();
			}
			catch (IOException e)
			{
				System.err.println("JUDAX: IOException while reading the '" + url + "' file.");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			executeOnPost.run();
		}
	}
	
	private static String readFromInputStream(InputStream inputStream) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		// do reading, usually loop until end of file reading
		StringBuilder sb = new StringBuilder();
		String mLine = reader.readLine();
		while (mLine != null)
		{
			sb.append(mLine + System.getProperty("line.separator")); // process line
			mLine = reader.readLine();
		}
		reader.close();
		return sb.toString();
	}
	
	private static String readFromAssets(Context context, String filename)
			throws IOException
	{
		return readFromInputStream(context.getAssets().open(filename));
	}	
	
	private static String readFromURL(String url) throws IOException
	{
		return readFromInputStream(new URL(url).openStream());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

    FrameLayout layout = new FrameLayout(this);

    // Create the crosswalk webview
  	crosswalkView = new XWalkView(this);

  	// Clear the cache
  	crosswalkView.clearCache(true);

    // The crosswalk extension to provide the head tracking to JS
  	oculusMobileSDKHeadTrackingXWalkViewExtension = new OculusMobileSDKHeadTrackingXWalkViewExtension();
  	oculusMobileSDKHeadTrackingXWalkViewExtension.start(this);
  	
    layout.addView(oculusMobileSDKHeadTrackingXWalkViewExtension.getView());
    layout.addView(crosswalkView);
    
    setContentView(layout);
		
		// Force the screen to stay on, rather than letting it dim and shut off
		// while the user is watching a movie.
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );

		// Force screen brightness to stay at maximum
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.screenBrightness = 1.0f;
		getWindow().setAttributes(params);
		
		// Load the extension JS file from github
		new ReadFromURL("https://raw.githubusercontent.com/judax/OculusMobileSDKHeadTrackingWebVR/master/js/OculusMobileSDKHeadTrackingWebVR.js", new Runnable()
		{
			// When the file is read, launch the crosswalk webview listening to the resources to inject the read js code.
			@Override
			public void run()
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						// Get the URL to be loaded from the intent
						String url = "";
						Intent intent = getIntent();
						if (intent != null)
						{
							Bundle extras = intent.getExtras(); 
							if (extras != null) 
							{
								url = extras.getString("url");
							}
						}
						
						crosswalkView.setResourceClient(new CrosswalkResourceClientInjectJSCode(crosswalkView, url, oculusMobileSDKHeadTrackingWebVRJSCode));
						
						crosswalkView.load(url, null);		
					}
				});
			}
		}).execute();

	}
	
	@Override protected void onResume()
	{
		super.onResume();
		oculusMobileSDKHeadTrackingXWalkViewExtension.resume();
	}

	@Override protected void onPause()
	{
		oculusMobileSDKHeadTrackingXWalkViewExtension.pause();
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		oculusMobileSDKHeadTrackingXWalkViewExtension.stop();
		super.onDestroy();
	}
}
