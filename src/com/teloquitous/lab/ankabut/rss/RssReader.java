package com.teloquitous.lab.ankabut.rss;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.util.Log;

public class RssReader {
	private String rssUrl;

	public RssReader(String rssUrl) {
		this.rssUrl = rssUrl;
	}
	
	public List <Kajian> getItems() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		RssParserHandler handler = new RssParserHandler();
		Log.d("Parsing", rssUrl);
		parser.parse(rssUrl, handler);
		return handler.getItems();
	}

}
