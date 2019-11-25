package com.example.myapplication;

public class MemberInfo {

    private String nickname;
    private String address;
    private String telephone;
    private String photoUrl;
    private int point;
    private String uid;
    private String usermsg;
    private String token;

    public MemberInfo() { }

    public MemberInfo(String nickname, String address, String telephone, String photoUrl, int point, String uid, String usermsg, String token) {
        this.nickname = nickname;
        this.address = address;
        this.telephone = telephone;
        this.photoUrl = photoUrl;
        this.point = point;
        this.uid = uid;
        this.usermsg = usermsg;
        this.token = token;
    }

    public MemberInfo(String nickname, String address, String telephone, int point, String uid, String usermsg, String token) {
        this.nickname = nickname;
        this.address = address;
        this.telephone = telephone;
        this.point = point;
        this.uid = uid;
        this.usermsg = usermsg;
        this.token = token;
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
}
