package com.teloquitous.lab.ankabut.rss;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teloquitous.lab.ankabut.AnkabutKeyStrings;
import com.teloquitous.lab.ankabut.R;

public class RadioListAdaptr extends BaseAdapter implements AnkabutKeyStrings {
	private Activity _ac;
	private ArrayList<Radio> data;
	private static LayoutInflater inflater = null;
	private SharedPreferences p;
	private boolean isPlayed;
	private boolean serviceRunning;
	private boolean onRadio;
	private String url = "";

	public RadioListAdaptr(Activity _ac, ArrayList<Radio> data,
			boolean serviceRunning) throws Exception {
		super();
		this._ac = _ac;
		this.data = data;
		this.serviceRunning = serviceRunning;
		inflater = (LayoutInflater) _ac
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		getPref();
	}

	private void getPref() {
		p = PreferenceManager.getDefaultSharedPreferences(_ac);
		isPlayed = p.getBoolean(_KEY_PREF_ON_PLAY, false);
		onRadio = p.getBoolean(_KEY_PREF_ON_RADIO, false);

		if (isPlayed) {
			url = p.getString(_KEY_PREF_PLAY_URL, "");
		}

	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (convertView == null) {
			v = inflater.inflate(R.layout.list_radio_item, null);
		}

		ImageView stt = (ImageView) v.findViewById(R.id.imageViewRadioStat);
		TextView tvNama = (TextView) v.findViewById(R.id.textViewNamaRadio);
		TextView tvKota = (TextView) v.findViewById(R.id.textViewKota);
		ImageView blink = (ImageView) v.findViewById(R.id.imageViewRadioBlink);
		TextView tvStat = (TextView) v.findViewById(R.id.textViewStatus);

		Radio r = data.get(position);
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(500);
		anim.setStartOffset(200);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);

		if (url.equals(r.getUrl()) && isPlayed && serviceRunning && onRadio) {
			r.setPlayedAtm(true);
			r.setStatusMessage("Menjalankan");
			isPlayed = false;
		}

		tvNama.setText(r.getNamaRadio());
//		int c = tvNama.getTextColors().getDefaultColor();
		tvKota.setText(r.getKota());
		if (r.isPlayedAtm()) {
			tvNama.setTextColor(_ac.getResources().getColor(R.color.hijau_1));
			stt.setImageResource(R.drawable.ic_radio_on);
			blink.setImageResource(R.drawable.lingkaran_kecil);
			blink.startAnimation(anim);
			tvStat.setText(r.getStatusMessage());
			tvStat.startAnimation(anim);
		} else {
			tvNama.setTextColor(Color.WHITE);
			stt.setImageResource(R.drawable.ic_radio_off);
			blink.setImageResource(android.R.color.transparent);
			blink.clearAnimation();
			if (r.getStatusMessage().equalsIgnoreCase("Error")) {
				tvStat.setText("Tidak ada koneksi.");
			} else {
				tvStat.setText("");
			}
			tvStat.clearAnimation();
		}

		return v;
	}

}
