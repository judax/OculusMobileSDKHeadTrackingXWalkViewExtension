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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class OculusMobileSDKHeadTrackingXWalkViewActivity extends Activity
{
  private XWalkView crosswalkView = null;
	private OculusMobileSDKHeadTrackingXWalkViewExtension oculusMobileSDKHeadTrackingXWalkViewExtension = null;
	
	private static AlertDialog createAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener, int numberOfButtons, String yesButtonText, String noButtonText, String cancelButtonText)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, yesButtonText, onClickListener);
		if (numberOfButtons > 1)
		{
			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, noButtonText, onClickListener);
		}
		if (numberOfButtons > 2)
		{
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, cancelButtonText, onClickListener);
		}
		return alertDialog;
	}
		
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
      if (url.equals(this.url) && jsCode != null)
      {
      	view.evaluateJavascript(jsCode, null);
      	System.out.println("JUDAX: JSCode  injected!");
			}
		}				
	}
	
	public class ReadFromURLAsyncTask extends AsyncTask<Void, Void, Void>
	{
		private String url;
		private ReadFromURLAsyncTaskListener listener;

		public ReadFromURLAsyncTask(String url, ReadFromURLAsyncTaskListener listener)
		{
			this.url = url;
			this.listener = listener;
		}
		
		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				String s = readFromURL(url);
				listener.success(this, s);
			}
			catch (MalformedURLException e)
			{
				System.err.println("JUDAX: MalformedURLException while reading the '" + url + "' file.");
				e.printStackTrace();
				listener.exception(this, e);
			}
			catch (IOException e)
			{
				System.err.println("JUDAX: IOException while reading the '" + url + "' file.");
				e.printStackTrace();
				listener.exception(this, e);
			}
			return null;
		}

		public String getUrl()
		{
			return url;
		}
	}
	
	private interface ReadFromURLAsyncTaskListener
	{
		public void success(ReadFromURLAsyncTask source, String s);
		public void exception(ReadFromURLAsyncTask source, Exception e);
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
	
//	private static String readFromAssets(Context context, String filename)
//			throws IOException
//	{
//		return readFromInputStream(context.getAssets().open(filename));
//	}	
	
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
		new ReadFromURLAsyncTask("https://raw.githubusercontent.com/judax/OculusMobileSDKHeadTrackingWebVR/master/js/OculusMobileSDKHeadTrackingWebVR.js", new ReadFromURLAsyncTaskListener()
		{
			@Override
			public void success(ReadFromURLAsyncTask source, final String jsCode)
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
						
						if (jsCode != null)
						{
							crosswalkView.setResourceClient(new CrosswalkResourceClientInjectJSCode(crosswalkView, url, jsCode));
						}
						
						crosswalkView.load(url, null);		
					}
				});
			}
			
			@Override
			public void exception(final ReadFromURLAsyncTask source, final Exception e)
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						String message = null;
						if (e instanceof IOException)
						{
				  		message = "Error while loading the JS extension file from the given URL '" + source.getUrl() + "'.";
						}
						else if (e instanceof MalformedURLException)
						{
							message = "The given URL '" + source.getUrl() + "' is not correct.";
						}
						AlertDialog alertDialog = createAlertDialog(OculusMobileSDKHeadTrackingXWalkViewActivity.this, "Error loading JS extension file", message +  " The WebVR API polyfill won't be present. Load the page anyway?", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								if (which == DialogInterface.BUTTON1)
								{
									success(source, null);
								}
								else 
								{
									OculusMobileSDKHeadTrackingXWalkViewActivity.this.finish();
								}
							}
						}, 2, "Yes", "No", null);
			  		alertDialog.show();
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
