package com.teloquitous.lab.ankabut;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.teloquitous.lab.ankabut.artikel.RSSFeed;
import com.teloquitous.lab.ankabut.artikel.RSSHandler;
import com.teloquitous.lab.ankabut.artikel.RSSItem;

public class RSSFeedActivity extends Activity {
	private String RSSUrl;
	private RSSFeed myRssFeed = null;
	private ListView listView;
	private Animation anim;
	private TextView tvEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rssfeed);
		RSSUrl = getIntent().getStringExtra("url");
		Log.d("URL", RSSUrl);

		listView = (ListView) findViewById(R.id.listitem);
		tvEmpty = (TextView) findViewById(R.id.empty);
		tvEmpty.setText("Mengambil data...");

		anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(300);
		anim.setStartOffset(100);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		tvEmpty.startAnimation(anim);
		initHeader();

		if (RSSUrl != null && !RSSUrl.isEmpty()) {
			new MyTask().execute();
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long arg3) {
				Uri feedUri = Uri.parse(myRssFeed.getItem(pos).getLink());
				Intent myIntent = new Intent(Intent.ACTION_VIEW, feedUri);
				startActivity(myIntent);
			}
		});
	}

	private void initHeader() {
		final LinearLayout l = (LinearLayout) findViewById(R.id.head_layout);
		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
			@Override
			public Shader resize(int width, int height) {
				LinearGradient lg = new LinearGradient(0, 0, 0,
						l.getHeight(),
						new int[] {
								Color.parseColor("#00000000"), // atas
								Color.parseColor("#00D4FFD4"), // atas
								Color.parseColor("#22D4FFD4"),
								Color.parseColor("#FF33B5E5"),
								Color.parseColor("#FF33B5E5") }, // bawah
						new float[] { 0, 0.65f, 0.99f, 0.99f, 1 },
						Shader.TileMode.REPEAT);
				return lg;
			}
		};
		PaintDrawable p = new PaintDrawable();
		p.setShape(new RectShape());
		p.setShaderFactory(sf);
		l.setBackgroundDrawable(p.getCurrent());

		try {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				l.setVisibility(View.GONE);
			} else {
				l.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {

		}

	}

	private class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			try {

				URL rssUrl = new URL(RSSUrl);

				SAXParserFactory mySAXParserFactory = SAXParserFactory
						.newInstance();
				SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
				XMLReader myXMLReader = mySAXParser.getXMLReader();
				RSSHandler myRSSHandler = new RSSHandler();
				myXMLReader.setContentHandler(myRSSHandler);
				InputSource myInputSource = new InputSource(rssUrl.openStream());
				myInputSource.setEncoding("utf-8");
				myXMLReader.parse(myInputSource);

				myRssFeed = myRSSHandler.getFeed();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			stopAnim();
			if (myRssFeed != null) {
				TextView feedTitle = (TextView) findViewById(R.id.feedtitle);
				TextView feedDescribtion = (TextView) findViewById(R.id.feeddescribtion);
//				TextView feedPubdate = (TextView) findViewById(R.id.feedpubdate);
				TextView feedLink = (TextView) findViewById(R.id.feedlink);
				feedTitle.setText(myRssFeed.getTitle());
				feedDescribtion.setText(myRssFeed.getDescription());
//				feedPubdate.setText(myRssFeed.getPubdate());
				feedLink.setText(myRssFeed.getLink());

				fillListView();

			} else {
				onEmptyFeed();
			}

			super.onPostExecute(result);
		}

	}

	private void stopAnim() {
		tvEmpty.clearAnimation();
	}

	private void onEmptyFeed() {
		tvEmpty.setText("Feed kosong");
	}

	private void fillListView() {
		setTitle(myRssFeed.getTitle());
		FeedAdapter adap = new FeedAdapter(getApplicationContext(), myRssFeed);
		listView.setAdapter(adap);
	}

	private class FeedAdapter extends BaseAdapter {
		private Context _c;
		// private RSSFeed data;
		private List<RSSItem> data;
		private LayoutInflater inflater = null;

		public FeedAdapter(Context _c, RSSFeed myRssFeed) {
			super();
			this._c = _c;
			this.data = myRssFeed.getList();
			inflater = (LayoutInflater) this._c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int pos, View cv, ViewGroup vg) {
			View v = cv;
			if (cv == null) {
				v = inflater.inflate(R.layout.feed_list_item_2, null);
			}

			TextView tvTitle = (TextView) v.findViewById(R.id.textVTitle);
			TextView tvPubDate = (TextView) v.findViewById(R.id.textVPubdate);
			RSSItem i = data.get(pos);
			tvTitle.setText(i.getTitle());
			tvPubDate.setText(i.getPubdate());

			return v;
		}

	}

}
