package com.teloquitous.lab.ankabut.rss;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.teloquitous.lab.ankabut.AnkabutKeyStrings;
import com.teloquitous.lab.ankabut.R;

public class KajianRSSListAdapter extends BaseAdapter implements
		AnkabutKeyStrings {
	private Activity activity;
	// Context ctx;
	private List<Kajian> data;
	private static LayoutInflater inflater = null;
	private SharedPreferences p;
	private boolean isPlayed;
	private boolean serviceRunning;
	private boolean onRadio;
	private String url = "";

	public KajianRSSListAdapter(Activity c, List<Kajian> data) {
		super();
		this.activity = c;
		// ctx = c;
		this.data = data;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		getPref();
		
	}

	private void getPref() {
		p = PreferenceManager.getDefaultSharedPreferences(activity);
		isPlayed = p.getBoolean(_KEY_PREF_ON_PLAY, false);
		onRadio = p.getBoolean(_KEY_PREF_ON_RADIO, false);

		if (isPlayed) {
			url = p.getString(_KEY_PREF_PLAY_URL, "");
		}
	}

	public KajianRSSListAdapter(Fragment f, List<Kajian> data, boolean b) throws Exception {
		this.data = data;
		this.activity = f.getActivity();
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		serviceRunning = b;
		getPref();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int pos, View cv, ViewGroup parent) {
		View v = cv;
		if (cv == null) {
			v = inflater.inflate(R.layout.list_kajian_item, null);
		}

		TextView author = (TextView) v.findViewById(R.id.textViewAuthor);
		TextView title = (TextView) v.findViewById(R.id.textViewTitle);
		TextView dura = (TextView) v.findViewById(R.id.textViewDuration);
		TextView date = (TextView) v.findViewById(R.id.textViewDate);
		ImageButton btnDownload = (ImageButton) v.findViewById(R.id.imageButton1);
		btnDownload.setFocusable(false);
		btnDownload.setFocusableInTouchMode(false);

		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(500);
		anim.setStartOffset(200);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);

		Kajian k = data.get(pos);
		if(k.getLink().equals(url) && isPlayed && serviceRunning && !onRadio) {
			k.setPlayedAtm(true);
			k.setStatusMessage("Playing");
			isPlayed = false;
		}
		
		author.setText(k.getItunesAuthor());
		title.setText(k.getTitle());

		dura.setText(k.getItunesDuration());
		if (k.isPlayedAtm()) {
			date.setText(k.getStatusMessage());
			date.startAnimation(anim);
			author.setTextColor(Color.parseColor("#22AA22"));
		} else {
			date.setText(k.getDescription());
			date.clearAnimation();
			author.setTextColor(Color.WHITE);
		}
		final String url = k.getLink();
		final String judul = k.getTitle();
		
		btnDownload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				File f = new File(Environment.getExternalStorageDirectory()
						+ "/Ankabut");
				if (!f.exists()) {
					f.mkdir();
				}
				DownloadManager downloadManager = (DownloadManager) activity.getSystemService(
						Context.DOWNLOAD_SERVICE);
				Request r = new Request(Uri.parse(url));
				r.setDestinationInExternalPublicDir("Ankabut",
						judul + ".mp3");
				r.setAllowedNetworkTypes(Request.NETWORK_WIFI | Request.NETWORK_MOBILE)
						.setTitle("Ankabut")
						.setDescription(judul);
				Toast.makeText(activity, "Mendownload: " + judul, Toast.LENGTH_LONG).show();

				downloadManager.enqueue(r);
				
			}
		});

		return v;

	}

}
