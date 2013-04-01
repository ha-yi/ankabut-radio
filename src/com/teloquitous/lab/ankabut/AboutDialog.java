package com.teloquitous.lab.ankabut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class AboutDialog extends Dialog {
	private static Context _c;

	public AboutDialog(Context context) {
		super(context);
		_c = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.about);
		StringBuilder sb = new StringBuilder();
		String version = null;
		try {
			version = _c.getPackageManager().getPackageInfo(
					_c.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		sb.append("Version: ");
		sb.append(version);
		sb.append(readRawTextFile(R.raw.info));

		TextView tvSub = (TextView) findViewById(R.id.textAboutSubtitle);
		TextView tvDet = (TextView) findViewById(R.id.textAboutDetail);
		tvSub.setText(Html.fromHtml(sb.toString()));
		tvDet.setText(Html.fromHtml(readRawTextFile(R.raw.detail)));
	}

	public static String readRawTextFile(int id) {
		InputStream inputStream = _c.getResources().openRawResource(id);
		InputStreamReader in = new InputStreamReader(inputStream);
		BufferedReader buf = new BufferedReader(in);
		String line;
		StringBuilder text = new StringBuilder();
		try {

			while ((line = buf.readLine()) != null)
				text.append(line);
		} catch (IOException e) {
			return null;
		}
		return text.toString();
	}

}
