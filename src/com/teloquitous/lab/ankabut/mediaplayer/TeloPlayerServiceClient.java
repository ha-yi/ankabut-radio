package com.teloquitous.lab.ankabut.mediaplayer;

public interface TeloPlayerServiceClient {
	public void onInitializePlayerStart(String message);
	public void onInitializeComplete();
	public void onError();
	public void onStopped(boolean paused);
	public void onCompleted();

}
