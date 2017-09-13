package com.zhihuihengxing.ebook.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/26 0026.
 */
public class BookBean implements Serializable{
    private String bookID;
    private String bookName;
    private String url;
    private String bookKindID;
    private String imageUrl;
    private String author;
    private String describeBook;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBookKindID() {
        return bookKindID;
    }

    public void setBookKindID(String bookKindID) {
        this.bookKindID = bookKindID;
    }

    public String getDescribeBook() {
        return describeBook;
    }

    public void setDescribeBook(String describeBook) {
        this.describeBook = describeBook;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "BookBean{" +
                "bookID='" + bookID + '\'' +
                ", bookName='" + bookName + '\'' +
                ", url='" + url + '\'' +
                ", bookKind='" + bookKindID + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", author='" + author + '\'' +
                ", describe='" + describeBook + '\'' +
                '}';
    }
}
