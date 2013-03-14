package com.teloquitous.lab.ankabut.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.teloquitous.lab.ankabut.AnkabutKeyStrings;
import com.teloquitous.lab.ankabut.R;
import com.teloquitous.lab.ankabut.mediaplayer.TeloPlayerServiceClient;
import com.teloquitous.lab.ankabut.mediaplayer.TeloRadioService;
import com.teloquitous.lab.ankabut.mediaplayer.TeloRadioService.MediaPlayerBinder;
import com.teloquitous.lab.ankabut.rss.Radio;
import com.teloquitous.lab.ankabut.rss.RadioListAdaptr;

public class RadioListFragment extends Fragment implements AnkabutKeyStrings,
		TeloPlayerServiceClient {
	// private static Context _c;
	private ListView listView;
	private View root;
	private boolean dataInitialized = false;
	private ArrayList<Radio> data = new ArrayList<Radio>();
	private RadioListAdaptr adap;
	private boolean serviceBerjalan = false;
	private boolean mBound;
	private TeloRadioService tPlayer;

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder serviceBinder) {
			MediaPlayerBinder binder = (MediaPlayerBinder) serviceBinder;
			tPlayer = binder.getService();
			tPlayer.setClient(RadioListFragment.this);
			mBound = true;

		}
	};
	private boolean playedOnLastRun;
	private int selectedPos = -1;
	private TextView textEmpty;
	private Radio curRadio;
	private MediaPlayer mPlayer;
	private int lastSelectPos;
	private SharedPreferences preferenceManager;
	private Animation anim;

	public static Fragment newInstance(Context context) {
		RadioListFragment f = new RadioListFragment();
		// _c = context;
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.activity_radio_list, container, false);

		bindToService();
		initPreferences();

		initView();
		initData();

		return root;
	}

	private void initPreferences() {
		try {
			preferenceManager = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			playedOnLastRun = preferenceManager.getBoolean(_KEY_PREF_ON_PLAY,
					false);
			if (teloPlayerRunning() && playedOnLastRun) {
				selectedPos = preferenceManager.getInt(_KEY_PREF_PLAY_LIST_POS,
						-1);
			}
		} catch (Exception e) {
		}
	}

	private void bindToService() {
		Intent intent = new Intent(getActivity(), TeloRadioService.class);
		serviceBerjalan = teloPlayerRunning();
		if (serviceBerjalan) {
			getActivity().bindService(intent, mConnection,
					Context.BIND_AUTO_CREATE);
		} else {
			// Log.d("AudioRSS", "start Service");
			getActivity().startService(intent);
			getActivity().bindService(intent, mConnection,
					Context.BIND_AUTO_CREATE);
		}
	}

	private boolean teloPlayerRunning() {
		ActivityManager manager = (ActivityManager) getActivity()
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (TeloRadioService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private void initView() {
		initListViewHeader();
		listView = (ListView) root.findViewById(R.id.listViewRadio);
		textEmpty = (TextView) root.findViewById(R.id.textViewEmpty);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				playRadio(pos);
			}
		});
		/*
		 * listView.setOnItemLongClickListener(new OnItemLongClickListener() {
		 * 
		 * @Override public boolean onItemLongClick(AdapterView<?> arg0, View
		 * arg1, int arg2, long arg3) { if(selectedPos == arg2) {
		 * showPopupMenu(arg1); } return false; } });
		 */

		anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(300);
		anim.setStartOffset(100);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		textEmpty.startAnimation(anim);
		textEmpty.setText("Loading...");
	}

	/*
	 * protected void showPopupMenu(View v) { PopupMenu mp = new
	 * PopupMenu(getActivity(), v); // MenuInflater inf = mp.getMenuInflater();
	 * String menuText; if(!onRecord) { menuText = "Rekam"; } else { menuText =
	 * "Stop Rekaman"; } mp.getMenu().add(menuText);
	 * mp.setOnMenuItemClickListener(new OnMenuItemClickListener() {
	 * 
	 * @Override public boolean onMenuItemClick(MenuItem arg0) { // TODO
	 * Auto-generated method stub return false; } }); mp.show();
	 * 
	 * }
	 */

	protected void playRadio(int pos) {
		curRadio = data.get(pos);
		lastSelectPos = selectedPos;
		if (mBound) {
			mPlayer = tPlayer.getMediaPlayer();
			if (mPlayer == null) {
				selectedPos = pos;
				tPlayer.initMediaPlayer(curRadio);
			} else {
				if (selectedPos == pos) {
					try {
						tPlayer.stopMediaPlayer();
					} catch (Exception e) {
						this.onCompleted();
					}
					lastSelectPos = pos;
				} else {
					if (selectedPos == -1)
						selectedPos = pos; // prevent on rerun error
											// caused by array index out
											// of bound.
					try {
						tPlayer.stopMediaPlayer();
					} catch (Exception e) {
						this.onCompleted();
					}
					selectedPos = pos;
					tPlayer.initMediaPlayer(curRadio);
				}
			}
		}
		listView.invalidateViews();

	}

	private void initListViewHeader() {
		TextView tvTitle = (TextView) root
				.findViewById(R.id.textViewRadioAnkabut);
		TextView tvSubTitle = (TextView) root
				.findViewById(R.id.textViewRadioOnline);
		// set gradient
		int color[] = { Color.parseColor("#55FF55"),
				Color.parseColor("#55FF55"), Color.parseColor("#008000"),
				Color.parseColor("#008000") };
		float position[] = { 0, 0.5f, 0.55f, 1 };
		TileMode md = TileMode.REPEAT;
		LinearGradient lg = new LinearGradient(0, 0, 0, 45, color, position, md);
		Shader grad = lg;
		tvTitle.getPaint().setShader(grad);
		tvSubTitle.getPaint().setShader(grad);
		// tvSubTitle.setShadowLayer(0.2f, 0, 0, Color.WHITE);

		final LinearLayout l = (LinearLayout) root
				.findViewById(R.id.head_layout);
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
			if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				l.setVisibility(View.GONE);
			} else {
				l.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {

		}
	}

	private void initData() {
		if (!dataInitialized) {
			new MyTask().execute();
		} else {
			fillListView();
		}
	}

	private void fillListView() {
		if (data != null && data.size() > 0) {
			try {
				adap = new RadioListAdaptr(getActivity(), data, serviceBerjalan);
				listView.setAdapter(adap);
				dataInitialized = true;

				int i = 0;
				if (playedOnLastRun) {
					for (Radio k : data) {
						if (k.isPlayedAtm()) {
							selectedPos = i;
							playedOnLastRun = false;
							break;
						}
						i++;
					}
				}
			} catch (Exception e) {
				terjadiKesalahanFatal();
			}
		}
	}

	private void terjadiKesalahanFatal() {
		dataInitialized = false;
		textEmpty
				.setText("Sebuah kesalahan telah terjadi saat pasukan pengantar "
						+ "data membawa data dari markas besar Internet.\n "
						+ "Silahkan tutup dan jalankan kembali aplikasi.");
		textEmpty.setTextColor(Color.parseColor("#FF9999"));
		textEmpty.clearAnimation();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	private class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(
						getActivity().getAssets().open("playlist.json")));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = r.readLine()) != null) {
					sb.append(line + "\n");
				}

				ArrayList<Radio> ar = new ArrayList<Radio>();
				String jsonText = sb.toString();
				JSONArray jA = new JSONArray(jsonText);
				for (int i = 0; i < jA.length(); i++) {
					JSONObject jo = jA.getJSONObject(i);
					Radio ra = new Radio();
					ra.setNamaRadio(jo.getString(_KEY_JSON_NAMA));
					ra.setKota(jo.getString(_KEY_JSON_LOKASI));
					ra.setUrl(jo.getString(_KEY_JSON_URL));
					ra.setPlayedAtm(false);
					ar.add(ra);
				}
				data = ar;

			} catch (IOException e) {
				// e.printStackTrace();
				// Log.e(_c.getApplicationInfo().className, "IOException");
				terjadiKesalahanFatal();
			} catch (JSONException e) {
				e.printStackTrace();
				// Log.e(_c.getApplicationInfo().className, "JSONException");
				terjadiKesalahanFatal();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			fillListView();
			super.onPostExecute(result);
		}

	}

	@Override
	public void onInitializePlayerStart(String message) {
		data.get(selectedPos).setStatusMessage(message);
		data.get(selectedPos).setPlayedAtm(true);
		listView.invalidateViews();

		if (preferenceManager != null) {
			Editor e = preferenceManager.edit();
			e.putBoolean(_KEY_PREF_ON_RADIO, true);
			e.putBoolean(_KEY_PREF_ON_PLAY, true);
			e.putString(_KEY_PREF_PLAY_URL, curRadio.getUrl());
			e.putInt(_KEY_PREF_PLAY_LIST_POS, selectedPos);
			e.commit();
		}

	}

	@Override
	public void onInitializeComplete() {
		data.get(selectedPos).setStatusMessage("Menjalankan");
		data.get(selectedPos).setPlayedAtm(true);
		listView.invalidateViews();

	}

	@Override
	public void onError() {
		data.get(selectedPos).setStatusMessage("Error");
		data.get(selectedPos).setPlayedAtm(false);
		listView.invalidateViews();
		selectedPos = -1;
		savePreferences();
	}

	private void savePreferences() {
		if (preferenceManager != null) {
			Editor e = preferenceManager.edit();
			e.putBoolean(_KEY_PREF_ON_PLAY, false);
			e.putString(_KEY_PREF_PLAY_URL, "");
			e.commit();
		}
	}

	@Override
	public void onStopped(boolean stat) {
		if (lastSelectPos != selectedPos && lastSelectPos != -1) {
			data.get(lastSelectPos).setStatusMessage("Done.");
			data.get(lastSelectPos).setPlayedAtm(false);
		} else {
			data.get(selectedPos).setStatusMessage("Done.");
			data.get(selectedPos).setPlayedAtm(false);
		}

		listView.invalidateViews();
		selectedPos = -1;
		savePreferences();
	}

	@Override
	public void onCompleted() {
		Log.d("complete", "");
		data.get(selectedPos).setStatusMessage("Done.");
		data.get(selectedPos).setPlayedAtm(false);
		listView.invalidateViews();
		selectedPos = -1;
		savePreferences();
	}

	@Override
	public void onDestroy() {
		if (mBound)
			getActivity().unbindService(mConnection);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		Log.d("On Pause", "on pause activity");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.d("On RESUME", "on resume activity");
		if (data != null && data.size() > 0) {
			dataInitialized = true;
		}
		super.onResume();
	}

}
