package com.judax.oculusmobilesdkheadtracking.xwalk;

import org.xwalk.core.XWalkExtension;
import android.app.Activity;
import android.view.View;
import com.judax.oculusmobilesdkheadtracking.OculusMobileSDKHeadTracking;
import com.judax.oculusmobilesdkheadtracking.OculusMobileSDKHeadTrackingListener;

public class OculusMobileSDKHeadTrackingXWalkViewExtension extends XWalkExtension
{
	private static final String EXTENSION_NAME = "OculusMobileSDKHeadTracking";
	
  private static String EXTENSION_JS_CODE = "" +
  		"(function() {" + 
  		"  exports.getOrientation = function() {" +
  		"    var orientationString = extension.internal.sendSyncMessage('getOrientation');" +
  		"    return JSON.parse(orientationString);" +
  		"  };" +
  		"})();";
  
	private float x = 0, y = 0, z = 0, w = 0;
	private OculusMobileSDKHeadTracking oculusMobileSDKHeadTracking = new OculusMobileSDKHeadTracking();
	
	private OculusMobileSDKHeadTrackingListener oculusMobileSDKHeadTrackingListener = new OculusMobileSDKHeadTrackingListener()
	{
		@Override
		public void orientationUpdated(OculusMobileSDKHeadTracking oculusMobileSDKHeadTracking, float x, float y, float z, float w)
		{
			synchronized(OculusMobileSDKHeadTrackingXWalkViewExtension.this)
			{
				OculusMobileSDKHeadTrackingXWalkViewExtension.this.x = x;
				OculusMobileSDKHeadTrackingXWalkViewExtension.this.y = y;
				OculusMobileSDKHeadTrackingXWalkViewExtension.this.z = z;
				OculusMobileSDKHeadTrackingXWalkViewExtension.this.w = w;
			}
		}
	};
	
	public OculusMobileSDKHeadTrackingXWalkViewExtension()
	{
		super(EXTENSION_NAME, EXTENSION_JS_CODE);
	}
	
	@Override
	public void onMessage(int instanceID, String message)
	{
	}
	
	@Override
	public String onSyncMessage(int instanceID, String message)
	{
		String result = "";
		if (message.equals("getOrientation"))
		{
			synchronized(this)
			{
				result = "{\"x\":" + x + ",\"y\":" + y + ",\"z\":" + z + ",\"w\":" + w + "}";
			}
		}
		return result;
	}
	
	public void start(Activity activity)
	{
		oculusMobileSDKHeadTracking.start(activity);
		oculusMobileSDKHeadTracking.addOculusMobileSDKHeadTrackingListener(oculusMobileSDKHeadTrackingListener);
	}
	
	public View getView()
	{
		return oculusMobileSDKHeadTracking.getView();
	}
	
	public void pause()
	{
		oculusMobileSDKHeadTracking.pause();
	}
	
	public void resume()
	{
		oculusMobileSDKHeadTracking.resume();
	}
	
	public void stop()
	{
		oculusMobileSDKHeadTracking.stop();
		oculusMobileSDKHeadTracking.removeOculusMobileSDKHeadTrackingListener(oculusMobileSDKHeadTrackingListener);
	}
}
