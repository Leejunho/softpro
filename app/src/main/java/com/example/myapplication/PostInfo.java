package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {

    private String title;
    private String item_name;
    private String price;
    private String term;
    private String contents;
    private String publisher;
    private Date createdAt;
    private String id;

    public PostInfo(String title, String item_name, String price, String term, String contents, String publisher, Date createdAt, String id) {
        this.title = title;
        this.item_name = item_name;
        this.price = price;
        this.term = term;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
    }

    public PostInfo(String title, String item_name, String price, String term, String contents, String publisher, Date createdAt) {
        this.title = title;
        this.item_name = item_name;
        this.price = price;
        this.term = term;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
    }

    public Map<String, Object> getPostInfo() {
        Map<String, Object> docData = new HashMap<>();
        docData.put("title", title);
        docData.put("item_name", item_name);
        docData.put("price", price);
        docData.put("term", term);
        docData.put("contents", contents);
        docData.put("publisher", publisher);
        docData.put("createdAt", createdAt);
        return docData;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getItem_name() {
        return this.item_name;
    }
    public void setItem_name(String itemname) {
        this.item_name = item_name;
    }

    public String getPrice() {
        return this.price;
    }
    public void setPrice(String price) {
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
}
