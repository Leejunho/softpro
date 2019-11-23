package com.example.myapplication;

public class MemberInfo {

    private String nickname;
    private String address;
    private String telephone;
    private String photoUrl;
    private int point;

    public MemberInfo(String nickname, String address, String telephone, String photoUrl, int point) {
        this.nickname = nickname;
        this.address = address;
        this.telephone = telephone;
        this.photoUrl = photoUrl;
        this.point = point;
    }

    public MemberInfo(String nickname, String address, String telephone, int point) {
        this.nickname = nickname;
        this.address = address;
        this.telephone = telephone;
        this.point = point;
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
}
