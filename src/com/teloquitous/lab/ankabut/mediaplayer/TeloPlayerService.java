package com.teloquitous.lab.ankabut.mediaplayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.teloquitous.lab.ankabut.AnkabutKeyStrings;
import com.teloquitous.lab.ankabut.MainTabActivity;
import com.teloquitous.lab.ankabut.R;
import com.teloquitous.lab.ankabut.rss.Kajian;
import com.teloquitous.lab.ankabut.rss.Radio;

public class TeloPlayerService extends Service implements
		MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
		MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener,
		MediaPlayer.OnCompletionListener, AnkabutKeyStrings {

	private MediaPlayer mMediaPlayer = null;
	private final Binder mBinder = new MediaPlayerBinder();
	private Kajian kajian;
	private Radio radio;
	private boolean onRadio;
	TeloPlayerServiceClient client;
	ScheduledExecutorService ss;

	public class MediaPlayerBinder extends Binder {
		public TeloPlayerService getService() {
			return TeloPlayerService.this;
		}

	}

	public MediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}

	public void initMediaPlayer(Kajian k) {
		client.onInitializePlayerStart("Menyambung...");
		this.kajian = k;
		onRadio = false;
		ss = Executors.newScheduledThreadPool(1);

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mMediaPlayer.setDataSource(kajian.getLink());
		} catch (Exception e) {
			e.printStackTrace();
		}

		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.prepareAsync();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Log.d("SERVICE", "Service started");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		mMediaPlayer.reset();
		client.onError();
		return true;
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		client.onInitializeComplete();
		startMediaPlayer();
	}

	public void setClient(TeloPlayerServiceClient c) {
		client = c;
	}

	public void pauseMediaPlayer() {
		mMediaPlayer.pause();
		// stopForeground(true);
		client.onStopped(true);
	}

	@SuppressLint("HandlerLeak")
	private void startMediaPlayer() {
		Context context = getApplicationContext();
		// Notification notification = new Notification(R.drawable.ic_launcher,
		// "Ankabut", System.currentTimeMillis());
		Intent notifIntent = new Intent(this, MainTabActivity.class);
		notifIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notifIntent, 0);
		final NotificationManager nmgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);

		String contentTitle;
		String contentText;
		if (onRadio) {
			contentTitle = radio.getNamaRadio();
			contentText = radio.getUrl();
			notifIntent.putExtra("tab", 0);
			builder.setSmallIcon(R.drawable.ic_stat_play_radio);
		} else {
			contentTitle = kajian.getItunesAuthor();
			contentText = kajian.getTitle();
			notifIntent.putExtra("tab", 1);
			builder.setSmallIcon(R.drawable.ic_stat_play_url);
		}

		// notification.setLatestEventInfo(context, contentTitle, contentText,
		// pendingIntent);

		builder.setContentTitle(contentTitle).setContentText(contentText)
				.setContentIntent(pendingIntent);

		startForeground(1, builder.build());

		final Handler mHandler = new Handler() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void handleMessage(Message msg) {
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					int pos = mMediaPlayer.getCurrentPosition();
					TimeZone tz = TimeZone.getTimeZone("UTC");
					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					df.setTimeZone(tz);
					String wkt = df.format(new Date(pos));
					builder.setContentInfo(wkt);
					nmgr.notify(1, builder.build());
				}
			}

		};

		ss.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				mHandler.sendMessage(mHandler.obtainMessage());
			}
		}, 1, 1, TimeUnit.SECONDS);

		mMediaPlayer.start();
	}

	public void stopMediaPlayer() {
		stopForeground(true);
		if (mMediaPlayer != null) {
			// if (mMediaPlayer.isPlaying())
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mMediaPlayer.release();
			client.onStopped(false);
			mMediaPlayer = null;
		}
		if (ss != null)
			ss.shutdown();
	}

	public void resetMediaPlayer() {
		stopForeground(true);
		mMediaPlayer.reset();
		if (ss != null)
			ss.shutdown();
	}

	public void resumeMediaPlayer() {
		try {
			mMediaPlayer.start();
			client.onInitializeComplete();
		} catch (Exception e) {
			client.onError();
		}

	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		stopForeground(true);
		client.onCompleted();
		mMediaPlayer.release();
		if (ss != null)
			ss.shutdown();
	}

	@Override
	public void onDestroy() {
		try {
			SharedPreferences p = PreferenceManager
					.getDefaultSharedPreferences(this);
			Editor e = p.edit();
			e.putBoolean(_KEY_PREF_ON_PLAY, false);
			e.putString(_KEY_PREF_PLAY_URL, "");
			e.putInt(_KEY_PREF_PLAY_LIST_POS, -1);
			e.commit();
		} catch (Exception e) {
		}

		mMediaPlayer.reset();
		mMediaPlayer.release();
		if (ss != null)
			ss.shutdown();
		super.onDestroy();
	}
}
