package com.judax.oculusmobilesdkheadtracking.xwalk.test;

import org.xwalk.core.XWalkView;

import com.judax.oculusmobilesdkheadtracking.xwalk.OculusMobileSDKHeadTrackingXWalkViewExtension;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class OculusMobileSDKHeadTrackingXWalkViewActivity extends Activity
{
  private XWalkView crosswalkView = null;
	private OculusMobileSDKHeadTrackingXWalkViewExtension oculusMobileSDKHeadTrackingXWalkViewExtension = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

    FrameLayout layout = new FrameLayout(this);

    // Create the crosswalk webview
  	crosswalkView = new XWalkView(this);

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
		crosswalkView.load(url, null);		
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
