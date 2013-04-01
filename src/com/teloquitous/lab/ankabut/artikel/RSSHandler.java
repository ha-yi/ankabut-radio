package com.teloquitous.lab.ankabut.artikel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RSSHandler extends DefaultHandler {
	
	final int state_unknown = 0;
	final int state_title = 1;
	final int state_description = 2;
	final int state_link = 3;
	final int state_pubdate = 4;
	int currentState = state_unknown;
	
	RSSFeed feed;
	RSSItem item;
	
	boolean itemFound = false;
	
	public RSSHandler(){
	}
	
	public RSSFeed getFeed(){
		return feed;
	}

	@Override
	public void startDocument() throws SAXException {
		feed = new RSSFeed();
		item = new RSSItem();
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if (localName.equalsIgnoreCase("item")){
			itemFound = true;
			item = new RSSItem();
			currentState = state_unknown;
		}
		/*
		 * when using localName i have encountered problem when in the RSS include:
		 * <title> and <media:title>
		 */
		else if (qName.equalsIgnoreCase("title")){
			
			currentState = state_title;
		}
//		else if (localName.equalsIgnoreCase("description")){
//			currentState = state_description;
//		}
		else if (localName.equalsIgnoreCase("link")){
			currentState = state_link;
		}
		else if (localName.equalsIgnoreCase("pubdate")){
			currentState = state_pubdate;
		}
		else{
			currentState = state_unknown;
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equalsIgnoreCase("item")){
			feed.addItem(item);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		String strCharacters = new String(ch,start,length);
//		Log.d("CUR STT", strCharacters);
		
		if (itemFound==true){
		// "item" tag found, it's item's parameter
			switch(currentState){
			case state_title:
				item.setTitle(strCharacters);
				break;
//			case state_description:
//				item.setDescription(strCharacters);
//				break;
			case state_link:
				item.setLink(strCharacters);
				break;
			case state_pubdate:
				item.setPubdate(strCharacters);
				break;	
			default:
				break;
			}
		}
		else{
		// not "item" tag found, it's feed's parameter
			switch(currentState){
			case state_title:
				feed.setTitle(strCharacters);
				break;
			case state_description:
				feed.setDescription(strCharacters);
				break;
			case state_link:
				feed.setLink(strCharacters);
				break;
			case state_pubdate:
				feed.setPubdate(strCharacters);
				break;	
			default:
				break;
			}
		}
		
		currentState = state_unknown;
	}
	

}
