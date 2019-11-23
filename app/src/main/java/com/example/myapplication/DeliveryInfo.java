package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DeliveryInfo implements Serializable {
    private String telephone;  // 전화번호
    private String boxnum;     // 택배함 번호
    private Date createdAt;    // 택배 도착 시간

    public DeliveryInfo(String telephone, String boxnum, Date createdAt) {
        this.telephone = telephone;
        this.boxnum = boxnum;
        this.createdAt = createdAt;
    }

    public Map<String, Object> getDeliveryInfo() {
        Map<String, Object> docData = new HashMap<>();
        docData.put("telephone", telephone);
        docData.put("boxnum", boxnum);
        docData.put("createdAt", createdAt);
        return docData;
    }

    public String getTelephone() {
        return this.telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getBoxnum() {
        return this.boxnum;
    }
    public void setBoxnum(String boxnum) {
        this.boxnum = boxnum;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
