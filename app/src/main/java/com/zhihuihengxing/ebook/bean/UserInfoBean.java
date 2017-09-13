package com.zhihuihengxing.ebook.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
public class UserInfoBean implements Serializable{


    private String password;
	private String userID;
	private String userName;
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "UserInfoBean{" +
				"userID='" + userID + '\'' +
				", userName='" + userName + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
