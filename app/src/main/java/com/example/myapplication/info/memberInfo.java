package com.example.myapplication.info;

import java.util.HashMap;
import java.util.Map;

public class memberInfo {

    private String nickname;
    private String address;
    private String telephone;
    private String photoUrl;
    private int point;
    private String uid;
    private String usermsg;
    private String token;
    private String replacenum;
    private int countpost;
    private int countmsg;
    private int countbox;

    public memberInfo() { }

    public memberInfo(String nickname, String address, String telephone, String photoUrl, int point, String uid, String usermsg, String token, String replacenum, int countpost, int countmsg, int countbox) {
        this.nickname = nickname;
        this.address = address;
        this.telephone = telephone;
        this.photoUrl = photoUrl;
        this.point = point;
        this.uid = uid;
        this.usermsg = usermsg;
        this.token = token;
        this.replacenum = replacenum;
        this.countpost = countpost;
        this.countmsg = countmsg;
        this.countbox = countbox;
    }

    public memberInfo(String nickname, String address, String telephone, int point, String uid, String usermsg, String token, String replacenum, int countpost, int countmsg, int countbox) {
        this.nickname = nickname;
        this.address = address;
        this.telephone = telephone;
        this.point = point;
        this.uid = uid;
        this.usermsg = usermsg;
        this.token = token;
        this.replacenum = replacenum;
        this.countpost = countpost;
        this.countmsg = countmsg;
        this.countbox = countbox;
    }

    public Map<String, Object> getMemberInfo() {
        Map<String, Object> docData = new HashMap<>();
        docData.put("nickname", nickname);
        docData.put("address", address);
        docData.put("telephone", telephone);
        docData.put("photoUrl", photoUrl);
        docData.put("point", point);
        docData.put("uid", uid);
        docData.put("usermsg", usermsg);
        docData.put("token", token);
        docData.put("replacenum", replacenum);
        docData.put("countpost", countpost);
        docData.put("countmsg", countmsg);
        docData.put("countbox", countbox);
        return docData;
    }

    public String getNickname() {
        return this.nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return this.telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getPoint() {
        return this.point;
    }
    public void setPoint(int point) {
        this.point = point;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsermsg() {
        return usermsg;
    }
    public void setUsermsg(String usermsg) {
        this.usermsg = usermsg;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getReplacenum() {
        return replacenum;
    }
    public void setReplacenum(String replacenum) {
        this.replacenum = replacenum;
    }

    public int getCountpost() {
        return this.countpost;
    }
    public void setCountpost(int countpost) {
        this.countpost = countpost;
    }

    public int getCountmsg() {
        return this.countmsg;
    }
    public void setCountmsg(int countmsg) {
        this.countmsg = countmsg;
    }

    public int getCountbox() {
        return this.countbox;
    }
    public void setCountbox(int countbox) {
        this.countbox = countbox;
    }
}
