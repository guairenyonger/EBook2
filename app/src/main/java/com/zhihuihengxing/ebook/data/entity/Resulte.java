package com.zhihuihengxing.ebook.data.entity;

import java.io.Serializable;

public class Resulte implements Serializable{
	private int state;
	private String date;
	private String message;
	
	public Resulte(){}
	public Resulte(int state, String date, String message) {
		super();
		this.state = state;
		this.date = date;
		this.message = message;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
