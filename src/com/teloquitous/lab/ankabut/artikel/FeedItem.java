package com.teloquitous.lab.ankabut.artikel;

public class FeedItem {
	/*
	 *  "nama": "Muslimah.or.id",
        "pengelola": "",
        "url": "http://muslimah.or.id/",
        "feed": "http://feeds.feedburner.com/muslimah-or-id?format=xml",
        "type": "artikel-a"
	 */
	
	private String nama;
	private String peneglola;
	private String url;
	private String feed;
	private String type;
	
	
	public FeedItem() {
		this("","","","","");
	}

	public FeedItem(String nama, String peneglola, String url, String feed,
			String type) {
		super();
		this.nama = nama;
		this.peneglola = peneglola;
		this.url = url;
		this.feed = feed;
		this.type = type;
	}
	
	/**
	 * @return the nama
	 */
	public String getNama() {
		return nama;
	}
	/**
	 * @param nama the nama to set
	 */
	public void setNama(String nama) {
		this.nama = nama;
	}
	/**
	 * @return the peneglola
	 */
	public String getPeneglola() {
		return peneglola;
	}
	/**
	 * @param peneglola the peneglola to set
	 */
	public void setPeneglola(String peneglola) {
		this.peneglola = peneglola;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the feed
	 */
	public String getFeed() {
		return feed;
	}
	/**
	 * @param feed the feed to set
	 */
	public void setFeed(String feed) {
		this.feed = feed;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
}
