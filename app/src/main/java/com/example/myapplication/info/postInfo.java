package com.example.myapplication.info;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class postInfo implements Serializable {

    private String title;
    private int price;
    private String term;
    private String contents;
    private String publisher;
    private Date createdAt;
    private String id;
    private int viewCount;
    private String consumer;
    private String roomID;
    private String completepublisher;
    private String completeconsumer;
    private String complete;
    private int boxnum;
    private String checkpublisher;
    private String checkconsumer;




    public postInfo(String title, int price, String term, String contents, String publisher, Date createdAt, String id, int viewCount, String consumer, String roomID, String completepublisher, String completeconsumer, String complete, int boxnum, String checkpublisher, String checkconsumer) {
        this.title = title;
        this.price = price;
        this.term = term;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
        this.viewCount = viewCount;
        this.consumer = consumer;
        this.roomID = roomID;
        this.completepublisher = completepublisher;
        this.completeconsumer = completeconsumer;
        this.complete = complete;
        this.boxnum = boxnum;
        this.checkpublisher = checkpublisher;
        this.checkconsumer = checkconsumer;
    }

    public postInfo(String title, int price, String term, String contents, String publisher, Date createdAt, int viewCount, String consumer, String roomID, String completepublisher, String completeconsumer, String complete, int boxnum, String checkpublisher, String checkconsumer) {
        this.title = title;
        this.price = price;
        this.term = term;
        this.contents = contents;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.consumer = consumer;
        this.roomID = roomID;
        this.completepublisher = completepublisher;
        this.completeconsumer = completeconsumer;
        this.complete = complete;
        this.boxnum = boxnum;
        this.checkpublisher = checkpublisher;
        this.checkconsumer = checkconsumer;
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
        docData.put("consumer", consumer);
        docData.put("roomID", roomID);
        docData.put("completepublisher", completepublisher);
        docData.put("completeconsumer", completeconsumer);
        docData.put("complete", complete);
        docData.put("id", id);
        docData.put("boxnum", boxnum);
        docData.put("checkpublisher", checkpublisher);
        docData.put("checkconsumer", checkconsumer);
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

    public String getConsumer() {
        return this.consumer;
    }
    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getRoomID() {
        return this.roomID;
    }
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getCompletepublisher() {
        return this.completepublisher;
    }
    public void setCompletepublisher(String completepublisher) {
        this.completepublisher = completepublisher;
    }

    public String getCompleteconsumer() {
        return this.completeconsumer;
    }
    public void setCompleteconsumer(String completeconsumer) {
        this.completeconsumer = completeconsumer;
    }

    public String getComplete() {
        return this.complete;
    }
    public void setComplete(String complete) {
        this.complete = complete;
    }

    public int getBoxnum() {
        return this.boxnum;
    }
    public void setBoxnum(int boxnum) {
        this.boxnum = boxnum;
    }

    public String getCheckpublisher() {
        return this.checkpublisher;
    }
    public void setCheckpublisher(String checkpublisher) {
        this.checkpublisher = checkpublisher;
    }

    public String getCheckconsumer() {
        return this.checkconsumer;
    }
    public void setCheckconsumer(String checkconsumer) {
        this.checkconsumer = checkconsumer;
    }
}
