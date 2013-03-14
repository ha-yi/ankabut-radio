package com.teloquitous.lab.ankabut.artikel;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teloquitous.lab.ankabut.AnkabutKeyStrings;
import com.teloquitous.lab.ankabut.R;

public class FeedListAdapter extends BaseAdapter implements AnkabutKeyStrings {
	private Activity _ac;
	private ArrayList<FeedItem> data;
	private static LayoutInflater inflater = null;
	

	public FeedListAdapter(Activity c, ArrayList<FeedItem> data) {
		super();
		this._ac = c;
		this.data = data;
		inflater = (LayoutInflater) _ac
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
	public View getView(int pos, View convertView, ViewGroup vg) {
		View v = convertView;
		if(convertView == null) {
			v = inflater.inflate(R.layout.feed_list_item, null);
		}
		
		ImageView it = (ImageView) v.findViewById(R.id.imageViewType);
		TextView tNama = (TextView) v.findViewById(R.id.tvNamaFeed);
		TextView tSub = (TextView) v.findViewById(R.id.tvPengelolaFeed);
		
		FeedItem f = data.get(pos);
		if(f.getType().equalsIgnoreCase("artikel-a")) {
			it.setImageResource(R.drawable.ic_feed_artikel_a);
		} else {
			it.setImageResource(R.drawable.ic_feed_artikel);
		}
		
		tNama.setText(f.getNama());
		String pengelola = f.getPeneglola();
		if(pengelola.isEmpty() || pengelola == "" || pengelola == null) {
			tSub.setText(f.getUrl());
		} else {
			tSub.setText(pengelola + "\n" + f.getUrl());
		}
		
		return v;
	}
	
}
