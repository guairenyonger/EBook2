package com.zhihuihengxing.ebook.bean;

import java.io.Serializable;

public class CollectBookBean implements Serializable{
	private String collectBookID;
	private String userID;
	private String bookID;
	public String getCollectBookID() {
		return collectBookID;
	}
	public void setCollectBookID(String collectBookID) {
		this.collectBookID = collectBookID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getBookID() {
		return bookID;
	}
	public void setBookID(String bookID) {
		this.bookID = bookID;
	}
	
}
