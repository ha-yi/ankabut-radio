package com.teloquitous.lab.ankabut.rss;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class RssParserHandler extends DefaultHandler {
	private ArrayList<Kajian> kajianItem;
	private Kajian curKajian;
	
	private boolean pTitl;
	private boolean pLink;
	private boolean pDesc;
	private boolean pAuth;
	private boolean pDura;
	
	private static final String S_ITEM = "item";
	private static final String S_TITLE = "title";
	private static final String S_LINK = "link";
	private static final String S_DESCRIPTION = "pubDate";
	private static final String S_AUTHOR = "itunes:author";
	private static final String S_DURATION = "itunes:duration";
//	private int itemNum;
	
	public RssParserHandler() {
		kajianItem = new ArrayList<Kajian>();
//		itemNum = 0;
	}
	
	public List<Kajian> getItems() {
		Log.d("Item dalam RSSPARSER", "=" + kajianItem.size());
		return kajianItem;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		//Log.d("SAX", new String(ch, start, length));
//		if(itemNum >= 50) return;
		
		if(pTitl) {
			if(curKajian != null) {
				curKajian.setTitle(new String(ch, start, length));
//				itemNum++;
			}
		} else if(pLink) {
			if(curKajian != null) {
				curKajian.setLink(new String(ch, start, length));
			}
		} else if(pDesc) {
			if(curKajian != null) {
				curKajian.setDescription(new String(ch, start, length));
			}
		} else if(pAuth) {
			if(curKajian != null) {
				curKajian.setItunesAuthor(new String(ch, start, length));
			}
		} else if(pDura) {
			if(curKajian != null) {
				curKajian.setItunesDuration(new String(ch, start, length));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (S_ITEM.equals(qName)) {
			kajianItem.add(curKajian);
			curKajian = null;
		} else if (S_TITLE.equals(qName)) {
			pTitl = false;
		} else if (S_LINK.equals(qName)) {
			pLink = false;
		} else if (S_DESCRIPTION.equals(qName)) {
			pDesc = false;
		} else if (S_AUTHOR.equals(qName)) {
			pAuth = false;
		} else if (S_DURATION.equals(qName)) {
			pDura = false;
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (S_ITEM.equals(qName)) {
			curKajian = new Kajian();
		} else if (S_TITLE.equals(qName)) {
			pTitl = true;
		} else if (S_LINK.equals(qName)) {
			pLink = true;
		} else if (S_DESCRIPTION.equals(qName)) {
			pDesc = true;
		} else if (S_AUTHOR.equals(qName)) {
			pAuth = true;
		} else if (S_DURATION.equals(qName)) {
			pDura = true;
		}
	}
	

}
