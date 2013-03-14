package com.teloquitous.lab.ankabut.rss;

import android.os.Parcel;
import android.os.Parcelable;

public class Kajian implements Parcelable {
	private String title;
	private String link;
	private String description;
	private String itunesAuthor;
	private String itunesDuration;
	private String statusMessage;
	private boolean isPlayedAtm;
	
	

	public Kajian(String title, String link, String description,
			String itunesAuthor, String itunesDuration, boolean stat) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
		this.itunesAuthor = itunesAuthor;
		this.itunesDuration = itunesDuration;
		this.statusMessage = "";
		this.isPlayedAtm = false;
	}
	
	public Kajian(Parcel p) {
		super();
		this.title = p.readString();
		this.link = p.readString();
		this.description = p.readString();
		this.itunesAuthor = p.readString();
		this.itunesDuration = p.readString();
		this.statusMessage = "";
		this.isPlayedAtm = false;
	}
	

	public Kajian() {
		super();
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the itunesAuthor
	 */
	public String getItunesAuthor() {
		return itunesAuthor;
	}

	/**
	 * @param itunesAuthor the itunesAuthor to set
	 */
	public void setItunesAuthor(String itunesAuthor) {
		this.itunesAuthor = itunesAuthor;
	}

	/**
	 * @return the itunesDuration
	 */
	public String getItunesDuration() {
		return itunesDuration;
	}

	/**
	 * @param itunesDuration the itunesDuration to set
	 */
	public void setItunesDuration(String itunesDuration) {
		this.itunesDuration = itunesDuration;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel d, int flag) {
		d.writeString(title);
		d.writeString(link);
		d.writeString(description);
		d.writeString(itunesAuthor);
		d.writeString(itunesDuration);
		
	}
	
	public static Parcelable.Creator<Kajian> CREATOR = new Parcelable.Creator<Kajian>() {

		public Kajian createFromParcel(Parcel source) {
			return new Kajian(source);
		}

		public Kajian[] newArray(int size) {
			return new Kajian[size];
		}
	};

	@Override
    public String toString() {
        return title;
    }

	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * @param statusMessage the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	/**
	 * @return the isPlayedAtm
	 */
	public boolean isPlayedAtm() {
		return isPlayedAtm;
	}

	/**
	 * @param isPlayedAtm the isPlayedAtm to set
	 */
	public void setPlayedAtm(boolean isPlayedAtm) {
		this.isPlayedAtm = isPlayedAtm;
	}

}
