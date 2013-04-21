package com.teloquitous.lab.ankabut.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.teloquitous.lab.ankabut.AnkabutKeyStrings;
import com.teloquitous.lab.ankabut.R;
import com.teloquitous.lab.ankabut.RSSFeedActivity;
import com.teloquitous.lab.ankabut.artikel.FeedItem;
import com.teloquitous.lab.ankabut.artikel.FeedListAdapter;

public class ArtikelRssFragment extends Fragment implements AnkabutKeyStrings {
	private ViewGroup root;
	private ListView listView;
	private TextView tvError;
	private boolean dataInitiated = false;
	private ArrayList<FeedItem> data = new ArrayList<FeedItem>();
	private FeedListAdapter adap;
	private Animation anim;
	private GoogleAnalytics analytics;
	private Tracker tracker;

	public static Fragment newInstance(Context context) {
		ArtikelRssFragment f = new ArtikelRssFragment();
		return f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = (ViewGroup) inflater.inflate(R.layout.feed_list, null);
		listView = (ListView) root.findViewById(R.id.listViewFeed);
		tvError = (TextView) root.findViewById(R.id.tvErr);
		tvError.setText("Loading...");
		tvError.setTextColor(Color.BLUE);
		anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(300);
		anim.setStartOffset(200);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		tvError.startAnimation(anim);

		if (!dataInitiated) {
			new MyTask().execute();
		} else {
			fillListView();
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				getFeed(pos);

			}
		});

		return root;
	}

	protected void getFeed(int pos) {
		try {
			FeedItem i = data.get(pos);
			if (i != null) {
				tracker.sendEvent("Pilih Feed",i.getNama() ,null , null);
				GAServiceManager.getInstance().dispatch();
				Intent it = new Intent(getActivity(), RSSFeedActivity.class);
				it.putExtra("url", i.getFeed());
				startActivity(it);
				
			}
		} catch (Exception e) {
		}

	}

	private void fillListView() {
		tvError.clearAnimation();
		if (data != null && data.size() > 0) {
			try {
				adap = new FeedListAdapter(getActivity(), data);
				dataInitiated = true;
				listView.setAdapter(adap);
			} catch (Exception e) {
				terjadiKesalahanFatal();
			}
		} else {
			terjadiKesalahanFatal();
		}

	}

	private class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			InputStream stream = null;
			try {
				
				stream = getActivity().getAssets().open("feed.json");

				BufferedReader r = new BufferedReader(new InputStreamReader(
						stream));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = r.readLine()) != null) {
					sb.append(line + "\n");
				}

				ArrayList<FeedItem> fi = new ArrayList<FeedItem>();
				String jsonText = sb.toString();
				JSONArray jA = new JSONArray(jsonText);
				for (int i = 0; i < jA.length(); i++) {
					JSONObject jo = jA.getJSONObject(i);
					FeedItem f = new FeedItem();
					f.setNama(jo.getString(_KEY_JSON_NAMA));
					f.setPeneglola(jo.getString(_KEY_JSON_PENGELOLA));
					f.setUrl(jo.getString(_KEY_JSON_URL));
					f.setFeed(jo.getString(_KEY_JSON_FEED));
					f.setType(jo.getString(_KEY_JSON_TYPE));
					fi.add(f);
				}
				data = fi;

			} catch (IOException e) {
				terjadiKesalahanFatal();
			} catch (Exception e) {
				e.printStackTrace();
				terjadiKesalahanFatal();
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (Exception e2) {

					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			fillListView();
			super.onPostExecute(result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void terjadiKesalahanFatal() {
		tvError.setText("Terjadi kesalahan... silahkan muat ulang aplikasi.");
		tvError.setTextColor(Color.RED);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		analytics = GoogleAnalytics.getInstance(getActivity());
		tracker = analytics.getDefaultTracker();
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		GAServiceManager.getInstance().dispatch();
	}
}
