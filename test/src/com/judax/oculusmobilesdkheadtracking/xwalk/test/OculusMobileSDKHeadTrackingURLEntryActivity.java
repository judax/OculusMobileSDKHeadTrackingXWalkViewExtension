package com.judax.oculusmobilesdkheadtracking.xwalk.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class OculusMobileSDKHeadTrackingURLEntryActivity extends Activity
{
	private static final String LAST_USED_URL_KEY = "url";
	private static final String URL_HISTORY_URLS_KEY = "urlHistoryURLs";
	
	private EditText urlEditText = null;
	private Button removeSelectedURLsButton = null;
	private Button clearURLHistoryButton = null;
	private URLHistoryListViewAdapter urlHistoryListViewAdapter = null;
	private JSONArray urlHistoryURLs = new JSONArray();
	private HashSet<Integer> urlIndicesToRemove = new HashSet<Integer>();
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			int position = (Integer)buttonView.getTag();
			if (isChecked)
			{
				urlIndicesToRemove.add(position);
			}
			else {
				urlIndicesToRemove.remove(position);
			}
			boolean enabled = !urlIndicesToRemove.isEmpty();
			removeSelectedURLsButton.setEnabled(enabled);
		}
	};
	private OnClickListener urlHistoryListViewEntryTextViewClicked = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			TextView textView = (TextView)view;
			urlEditText.setText(textView.getText());
		}
	}; 
	
	private void savePreferences()
	{
		savePreferences(null);
	}
	
	private void savePreferences(String url)
	{
		Editor editor = getPreferences(Activity.MODE_PRIVATE).edit();
		if (url != null) 
		{
			editor.putString(LAST_USED_URL_KEY, url);
		}
		editor.putString(URL_HISTORY_URLS_KEY, urlHistoryURLs.toString());
		if (!editor.commit())
		{
  		AlertDialog alertDialog = createAlertDialog(OculusMobileSDKHeadTrackingURLEntryActivity.this, "Error saving URL", "For an unknown reason, the URL or URL history could not be saved to the app preferences. The information might not be available from one execution to another if you close the app.", null, 1, "Ok", null, null);
  		alertDialog.show();
		}
	}
	
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
	
	private void addURLToHistoryIfItDoesNotExist(String url) throws JSONException
	{
		boolean exists = false;
		for (int i = 0; !exists && i < urlHistoryURLs.length(); i++)
		{
			exists = urlHistoryURLs.getString(i).equals(url);
		}
		if (!exists)
		{
			urlHistoryURLs.put(url);
			urlHistoryListViewAdapter.add(url);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.url_entry_layout);
		
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
		  		try
		  		{
		  			addURLToHistoryIfItDoesNotExist(url);
		  		}
		  		catch(JSONException e)
		  		{
			  		createAlertDialog(OculusMobileSDKHeadTrackingURLEntryActivity.this, "JSON Exception", "Could not add the url to the url history. " + e.getMessage(), null, 1, "Ok", null, null).show();
		  		}
		  		savePreferences(url);
		  		clearURLHistoryButton.setEnabled(urlHistoryURLs.length() > 0);
					Intent intent = new Intent(getApplicationContext(), OculusMobileSDKHeadTrackingXWalkViewActivity.class);
					intent.putExtra("url", url);
					startActivity(intent);
		  	}
		  	catch(MalformedURLException e)
		  	{
		  		createAlertDialog(OculusMobileSDKHeadTrackingURLEntryActivity.this, "Not an URL", "The text does not represent a valid URL.", null, 1, "Ok", null, null).show();
		  	}
			}
		});
		
		removeSelectedURLsButton = (Button)this.findViewById(R.id.removeSelectedURLsButton);
		removeSelectedURLsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (urlIndicesToRemove.isEmpty()) return;
				// As there is no remove function in JSONArrays until API 19:
				// 1.- make a copy of the JSONArray without the elements to be removed and storing the removed urls
				// 2.- recreate the JSONArray from the copy without the removed urls
				// 3.- remove the removed urls from the listview adapter
				try
				{
					int length = urlHistoryURLs.length();
					ArrayList<String> urlHistoryURLsCopyWithoutRemovedURLs = new ArrayList<String>(length - urlIndicesToRemove.size());
					ArrayList<String> removedURLs = new ArrayList<String>(urlIndicesToRemove.size());
					for (int i = 0; i < length; i++)
					{
						String url = urlHistoryURLs.getString(i); 
						if (urlIndicesToRemove.contains(i))
						{
							removedURLs.add(url);
						}
						else 
						{
							urlHistoryURLsCopyWithoutRemovedURLs.add(url);
						}
					}
					urlHistoryURLs = new JSONArray(urlHistoryURLsCopyWithoutRemovedURLs);
					for (String url: removedURLs)
					{
						urlHistoryListViewAdapter.remove(url);
					}
					savePreferences();
					clearURLHistoryButton.setEnabled(urlHistoryURLs.length() > 0);
				}
				catch(JSONException e)
				{
		  		createAlertDialog(OculusMobileSDKHeadTrackingURLEntryActivity.this, "JSON Exception", "Could not correctly get a URL string from the JSONArray in order to reove it: " + e.getMessage(), null, 1, "Ok", null, null).show();
				}
			}
		});
		
		clearURLHistoryButton = (Button)this.findViewById(R.id.clearURLHistoryButton);
		clearURLHistoryButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
	  		createAlertDialog(OculusMobileSDKHeadTrackingURLEntryActivity.this, "Clear URL History", "Are you sure you want to clear the URL history with '" + urlHistoryURLs.length() + "' entries?", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						switch(which)
						{
						case AlertDialog.BUTTON_POSITIVE:
							urlHistoryURLs = new JSONArray();
							urlHistoryListViewAdapter.clear();
				  		savePreferences();
				  		clearURLHistoryButton.setEnabled(urlHistoryURLs.length() > 0);
							break;
						}
					}
				}, 2, "Yes", "No", null).show();
			}
		});
		
		urlEditText = (EditText)this.findViewById(R.id.urlEditText);
		urlEditText.setText(getPreferences(Activity.MODE_PRIVATE).getString(LAST_USED_URL_KEY, ""));
		
		urlHistoryListViewAdapter = new URLHistoryListViewAdapter();
		ListView urlHistoryListView = (ListView)this.findViewById(R.id.urlHistoryListView); 
		urlHistoryListView.setAdapter(urlHistoryListViewAdapter);
		try
		{
			String urlHistoryURlsJSONString = getPreferences(Activity.MODE_PRIVATE).getString(URL_HISTORY_URLS_KEY, "[]");
			urlHistoryURLs = new JSONArray(urlHistoryURlsJSONString);
			int length = urlHistoryURLs.length();
			for (int i = 0; i < length; i++)
			{
				urlHistoryListViewAdapter.add(urlHistoryURLs.getString(i));
			}
			clearURLHistoryButton.setEnabled(length > 0);
		}
		catch (JSONException e)
		{
  		createAlertDialog(this, "JSON Exception", "Could not correctly parse and load previous URL history in JSON format: " + e.getMessage(), null, 1, "Ok", null, null).show();
		}

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
	
	private class URLHistoryListViewAdapter extends ArrayAdapter<String> 
	{
		public URLHistoryListViewAdapter()
		{
			super(OculusMobileSDKHeadTrackingURLEntryActivity.this, R.layout.url_history_list_item_layout);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) 
		{
			if (view == null)
			{
				LayoutInflater inflater = getLayoutInflater();
				view = inflater.inflate(R.layout.url_history_list_item_layout, parent, false); 
			}
			try
			{
				TextView urlHistoryListViewEntryTextView = (TextView) view.findViewById(R.id.urlHistoryEntryTextView);
				urlHistoryListViewEntryTextView.setOnClickListener(urlHistoryListViewEntryTextViewClicked);
				urlHistoryListViewEntryTextView.setText(urlHistoryURLs.getString(position));
				CheckBox urlHistoryListViewEntryCheckBox = (CheckBox)view.findViewById(R.id.urlHistoryEntryCheckbox);
				urlHistoryListViewEntryCheckBox.setTag(position);
				urlHistoryListViewEntryCheckBox.setOnCheckedChangeListener(checkedChangeListener);
			}
			catch(JSONException e)
			{
	  		createAlertDialog(this.getContext(), "JSON Exception", "JSONException accesing element at index '" + position + "' of the URL History JSON array. " + e.getMessage(), null, 1, "Ok", null, null).show();
			}
			return view;
		}			
	}
}
