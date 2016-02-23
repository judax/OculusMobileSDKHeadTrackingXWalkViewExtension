package com.judax.oculusmobilesdkheadtracking.xwalk;

import org.xwalk.core.XWalkExtension;
import android.app.Activity;
import android.view.View;
import com.judax.oculusmobilesdkheadtracking.OculusMobileSDKHeadTracking;
import com.judax.oculusmobilesdkheadtracking.OculusMobileSDKHeadTrackingData;
import com.judax.oculusmobilesdkheadtracking.OculusMobileSDKHeadTrackingListener;

public class OculusMobileSDKHeadTrackingXWalkViewExtension
{
	private static final String EXTENSION_NAME = "OculusMobileSDKHeadTracking";
  private static final String EXTENSION_JS_CODE = "" +
	
  		
  
//  		"var echoListener = null;" +
//      "extension.setMessageListener(function(msg) {" +
//      "  if (echoListener instanceof Function) {" +
//      "    echoListener(msg);" + "  };" + "});" +
//      "exports.echo = function (msg, callback) {" +
//      "  echoListener = callback;" + "  extension.postMessage(msg);" +
//      "};" + "exports.echoSync = function (msg) {" +
//      "  return extension.internal.sendSyncMessage(msg);" + "};" +

  		
  		
  		"var eventsListeners = {};" +
			"extension.setMessageListener(function(msg) {" +
  		"  var event = JSON.parse(msg);" +
  		"  if (event.name === 'EVAL_JS') {" +
  		"    window.eval(event.data);" +
			"    return;" +
			"  }" +
  		"  var eventListeners = eventsListeners[event.name];" +
			"  if (!eventListeners) return;" +
  		"  for(var i = 0; i < eventListeners.length; i++) {" +
			"    eventListeners[i](event.data);" +
  		"  }" +
  		"});" +
  		"exports.addEventListener = function(eventName, listener) {" +
  		"  if (typeof(eventName) !== 'string') return;" +
  		"  if (typeof(listener) !== 'function') return;" +
  		"  var eventListeners = eventsListeners[eventName];" +
  		"  if (!eventListeners) {" +
  		"    eventListeners = [];" +
  		"    eventsListeners[eventName] = eventListeners;" +
  		"  }" +
  		"  if (eventListeners.indexOf(listener) < 0) {" +
  		"    eventListeners.push(listener);" +
  		"  }" +
  		"};" +
  		"exports.start = function() {" +
  		"  extension.postMessage('start');" +
  		"};" +
  		"exports.removeEventListener = function(eventName, listener) {" +
  		"  if (typeof(eventName) !== 'string') return;" +
  		"  if (typeof(listener) !== 'function') return;" +
  		"  var eventListeners = eventsListeners[eventName];" +
  		"  if (!eventListeners) return;" +
  		"  var i = eventListeners.indexOf(listener);" +
  		"  if (i > 0) {" +
  		"    eventListeners.splice(i, 1);" +
  		"  }" +
  		"};" +
  		"exports.getTimeStamp = function() {" +
  		"  var jsonString = extension.internal.sendSyncMessage('getTimeStamp');" +
  		"  return JSON.parse(jsonString);" +
  		"};" +
  		"exports.getOrientation = function() {" +
  		"  var jsonString = extension.internal.sendSyncMessage('getOrientation');" +
  		"  return JSON.parse(jsonString);" +
  		"};" +
  		"exports.getLinearVelocity = function() {" +
  		"  var jsonString = extension.internal.sendSyncMessage('getLinearVelocity');" +
  		"  return JSON.parse(jsonString);" +
  		"};" +
  		"exports.getLinearAcceleration = function() {" +
  		"  var jsonString = extension.internal.sendSyncMessage('getLinearAcceleration');" +
  		"  return JSON.parse(jsonString);" +
  		"};" +
  		"exports.getAngularVelocity = function() {" +
  		"  var jsonString = extension.internal.sendSyncMessage('getAngularVelocity');" +
  		"  return JSON.parse(jsonString);" +
  		"};" +
  		"exports.getAngularAcceleration = function() {" +
  		"  var jsonString = extension.internal.sendSyncMessage('getAngularAcceleration');" +
  		"  return JSON.parse(jsonString);" +
  		"};" +
  		"exports.getData = function() {" +
  		"  var jsonString = extension.internal.sendSyncMessage('getData');" +
  		"  return JSON.parse(jsonString);" +
  		"};" +

  		"";
  
  /**
   * A helper method to create a event string. Events are always objects composed by a name and some data.
   * @param name the name of the event
   * @param data the data associated to the event
   * @return a string that represents the json structure of a event with the passed name and data
   */
	private static String createEventString(String name, String data)
	{
		return "{\"name\":\"" + name + "\",\"data\":" + data +"}";
	}

	// A private class so nothing that is not needed is exposed to the users of the OculusMobileSDKHeadTrackingXWalkExtension instances
	private class XWalkExtensionImplementation extends XWalkExtension
	{
		public Integer instanceID = null;
		
		@Override
		public void onInstanceCreated(int instanceID)
		{
			// TODO Auto-generated method stub
			super.onInstanceCreated(instanceID);
		}
		
		public XWalkExtensionImplementation()
		{
			super(EXTENSION_NAME, EXTENSION_JS_CODE);
		}
		
		@Override
		public String onSyncMessage(int instanceID, String message)
		{
			String result = "";
			OculusMobileSDKHeadTrackingData data = oculusMobileSDKHeadTracking.getData();
			if (message.equals("timeStamp"))
			{
				return "" + data.timeStamp;
			}
			else if (message.equals("getOrientation"))
			{
				result = "{\"x\":" + data.orientationX + ",\"y\":" + data.orientationY + ",\"z\":" + data.orientationZ + ",\"w\":" + data.orientationW + "}";
			}
			else if (message.equals("getLinearVelocity"))
			{
				result = "{\"x\":" + data.linearVelocityX + ",\"y\":" + data.linearVelocityY + ",\"z\":" + data.linearVelocityZ + "}";
			}
			else if (message.equals("getLinearAcceleration"))
			{
				result = "{\"x\":" + data.linearAccelerationX + ",\"y\":" + data.linearAccelerationY + ",\"z\":" + data.linearAccelerationZ + "}";
			}
			else if (message.equals("getAngularVelocity"))
			{
				result = "{\"x\":" + data.angularVelocityX + ",\"y\":" + data.angularVelocityY + ",\"z\":" + data.angularVelocityZ + "}";
			}
			else if (message.equals("getAngularAcceleration"))
			{
				result = "{\"x\":" + data.angularAccelerationX + ",\"y\":" + data.angularAccelerationY + ",\"z\":" + data.angularAccelerationZ + "}";
			}
			else if (message.equals("getData"))
			{
				result = "" +
						"{" +
						"\"timeStamp\":" + data.timeStamp + "," +
						"\"orientation\":" + "{\"x\":" + data.orientationX + ",\"y\":" + data.orientationY + ",\"z\":" + data.orientationZ + ",\"w\":" + data.orientationW + "}" +
						"\"linearVelocity\":" + "{\"x\":" + data.linearVelocityX + ",\"y\":" + data.linearVelocityY + ",\"z\":" + data.linearVelocityZ + "}" +
						"\"linearAcceleration\":" + "{\"x\":" + data.linearAccelerationX + ",\"y\":" + data.linearAccelerationY + ",\"z\":" + data.linearAccelerationZ + "}" +
						"\"angularVelocity\":" + "{\"x\":" + data.angularVelocityX + ",\"y\":" + data.angularVelocityY + ",\"z\":" + data.angularVelocityZ + "}" +
						"\"angularAcceleration\":" + "{\"x\":" + data.angularAccelerationX + ",\"y\":" + data.angularAccelerationY + ",\"z\":" + data.angularAccelerationZ + "}" +
						"}";
			}
			return result;
		}
		
		@Override
		public void onMessage(int instanceID, String message)
		{
			if (message.equals("start"))
			{
				this.instanceID = instanceID;
				// If the oculus head tracking has already started, notify the start event
				if (oculusMobileSDKHeadTracking.hasStarted())
				{
					System.out.println("start: " + createStartEventString());
					postMessage(instanceID, createStartEventString());
				}
			}
		}
	};
	
	private String createStartEventString()
	{
		OculusMobileSDKHeadTrackingData data = oculusMobileSDKHeadTracking.getData();
		return createEventString("start", "{\"xFOV\":" + data.xFOV + ",\"yFOV\":" + data.yFOV + ",\"interpupillaryDistance\":" + data.interpupillaryDistance + "}");
	}
	
	// The real crosswalk extension instance.
	private XWalkExtensionImplementation xwalkExtension = new XWalkExtensionImplementation();
	
	// The oculus head tracking instance
	private OculusMobileSDKHeadTracking oculusMobileSDKHeadTracking = new OculusMobileSDKHeadTracking();
	
	// The listeners for head tracking events
	private OculusMobileSDKHeadTrackingListener oculusMobileSDKHeadTrackingListener = new OculusMobileSDKHeadTrackingListener()
	{
		@Override
		public void headTrackingError(OculusMobileSDKHeadTracking oculusMobileSDKHeadTracking, String errorMessage)
		{
		}

		@Override
		public void headTrackingStarted(OculusMobileSDKHeadTracking oculusMobileSDKHeadTracking, OculusMobileSDKHeadTrackingData data)
		{
			// If the extension has been already instantiated, then notify about the creation of the 
			if (xwalkExtension.instanceID != null)
			{
				xwalkExtension.postMessage(xwalkExtension.instanceID, createStartEventString());
			}
		}
	};
	
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
