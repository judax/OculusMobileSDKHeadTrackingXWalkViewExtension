package com.judax.oculusmobilesdkheadtracking.xwalk.test;

import java.net.MalformedURLException;
import java.net.URL;

import com.example.oculusmobilesdkheadtracking.xwalk.test.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class OculusMobileSDKHeadTrackingURLEntryActivity extends Activity
{
	private EditText urlEditText = null;
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_layout);
		
		ImageButton qrcodeImageButton = (ImageButton)this.findViewById(R.id.qrcodeImageButton);
		qrcodeImageButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				IntentIntegrator intentIntegrator = new IntentIntegrator(OculusMobileSDKHeadTrackingURLEntryActivity.this);
				intentIntegrator.initiateScan();
			}
		});

		Button goButton = (Button)this.findViewById(R.id.goButton);
		goButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
		  	String url = urlEditText.getText().toString();
		  	try
		  	{
		  		new URL(url);
		  		Editor editor = getPreferences(Activity.MODE_PRIVATE).edit();
		  		editor.putString("url", url);
		  		if (!editor.commit())
		  		{
			  		AlertDialog alertDialog = createAlertDialog(OculusMobileSDKHeadTrackingURLEntryActivity.this, "Error saving URL", "For an unknown reason, the URL could not be saved to the app preferences. The URL will be loaded but won't be available for a future executions.", null, 1, "Ok", null, null);
			  		alertDialog.show();
		  		}
					Intent intent = new Intent(getApplicationContext(), OculusMobileSDKHeadTrackingXWalkViewActivity.class);
					intent.putExtra("url", url);
					startActivity(intent);
					
		  	}
		  	catch(MalformedURLException e)
		  	{
		  		AlertDialog alertDialog = createAlertDialog(OculusMobileSDKHeadTrackingURLEntryActivity.this, "Not an URL", "The text does not represent a valid URL.", null, 1, "Ok", null, null);
		  		alertDialog.show();
		  	}
			}
		});
		
		urlEditText = (EditText)this.findViewById(R.id.urlEditText);
		urlEditText.setText(getPreferences(Activity.MODE_PRIVATE).getString("url", "http://10.0.0.6/webvrtests/vrdevice/"));
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
	  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	  if (scanResult != null && scanResult.getContents() != null) 
	  {
	  	String url = scanResult.getContents();
	  	try
	  	{
	  		new URL(url);
	  		urlEditText.setText(url);
	  	}
	  	catch(MalformedURLException e)
	  	{
	  		AlertDialog alertDialog = createAlertDialog(this, "Not an URL", "The read QRCode does not represent a valid URL.", null, 1, "Ok", null, null);
	  		alertDialog.show();
	  	}
	  }
	}
}
