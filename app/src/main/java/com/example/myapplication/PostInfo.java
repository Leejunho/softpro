package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {

    private String title;
    private int price;
    private String term;
    private String contents;
    private String publisher;
    private Date createdAt;
    private String id;
    private int viewCount;

    public PostInfo(String title, int price, String term, String contents, String publisher, Date createdAt, String id, int viewCount) {
        this.title = title;
        this.price = price;
        this.term = term;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
        this.viewCount = viewCount;
    }

    public PostInfo(String title, int price, String term, String contents, String publisher, Date createdAt, int viewCount) {
        this.title = title;
        this.price = price;
        this.term = term;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
    }

    public Map<String, Object> getPostInfo() {
        Map<String, Object> docData = new HashMap<>();
        docData.put("title", title);
        docData.put("price", price);
        docData.put("term", term);
        docData.put("contents", contents);
        docData.put("publisher", publisher);
        docData.put("createdAt", createdAt);
        docData.put("viewCount", viewCount);
        return docData;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return this.price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public String getTerm() {
        return this.term;
    }
    public void setTerm(String term) {
        this.term = term;
    }

    public String getContents() {
        return this.contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getPublisher() {
        return this.publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getViewCount() {
        return this.viewCount;
    }
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}
