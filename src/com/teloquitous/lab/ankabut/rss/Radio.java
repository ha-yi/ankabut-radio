package com.teloquitous.lab.ankabut.rss;

import android.os.Parcel;
import android.os.Parcelable;

public class Radio implements Parcelable {
	private String namaRadio;
	private String kota;
	private String url;
	private boolean playedAtm;
	private String statusMessage;

	public Radio() {
		this("","","","");
	}

	public Radio(String namaRadio, String kota, String url, String s) {
		super();
		this.namaRadio = namaRadio;
		this.kota = kota;
		this.url = url;
		this.playedAtm = false;
		this.statusMessage = s;
	}

	public Radio(Parcel p) {
		this.namaRadio = p.readString();
		this.kota = p.readString();
		this.url = p.readString();
		this.statusMessage = p.readString();
		this.playedAtm = false;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flag) {
		p.writeString(namaRadio);
		p.writeString(kota);
		p.writeString(url);
		p.writeString(statusMessage);
		// this.playedAtm = false;
	}

	/**
	 * @return the namaRadio
	 */
	public String getNamaRadio() {
		return namaRadio;
	}

	/**
	 * @param namaRadio
	 *            the namaRadio to set
	 */
	public void setNamaRadio(String namaRadio) {
		this.namaRadio = namaRadio;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the kota
	 */
	public String getKota() {
		return kota;
	}

	/**
	 * @param kota
	 *            the kota to set
	 */
	public void setKota(String kota) {
		this.kota = kota;
	}

	/**
	 * @return the playedAtm
	 */
	public boolean isPlayedAtm() {
		return playedAtm;
	}

	/**
	 * @param playedAtm
	 *            the playedAtm to set
	 */
	public void setPlayedAtm(boolean playedAtm) {
		this.playedAtm = playedAtm;
	}

	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * @param statusMessage
	 *            the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public static Parcelable.Creator<Radio> CREATOR = new Parcelable.Creator<Radio>() {

		public Radio createFromParcel(Parcel source) {
			return new Radio(source);
		}

		public Radio[] newArray(int size) {
			return new Radio[size];
		}
	};

}
