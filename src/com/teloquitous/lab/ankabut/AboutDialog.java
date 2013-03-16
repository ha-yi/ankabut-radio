package com.teloquitous.lab.ankabut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Dialog;
import android.content.Context;
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
//		TextView tvTitle = (TextView) findViewById(R.id.textAboutTitle);
//		int color[] = { Color.parseColor("#55FF55"),
//				Color.parseColor("#55FF55"), Color.parseColor("#008000"),
//				Color.parseColor("#008000") };
//		float position[] = { 0, 0.5f, 0.55f, 1 };
//		TileMode md = TileMode.REPEAT;
//		LinearGradient lg = new LinearGradient(0, 0, 0, 45, color, position, md);
//		Shader grad = lg;
//		tvTitle.getPaint().setShader(grad);

		TextView tvSub = (TextView) findViewById(R.id.textAboutSubtitle);
		TextView tvDet = (TextView) findViewById(R.id.textAboutDetail);
		tvSub.setText(Html.fromHtml(readRawTextFile(R.raw.info)));
//		tvSub.setMovementMethod(LinkMovementMethod.getInstance()); // enable click on link in TextView
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
