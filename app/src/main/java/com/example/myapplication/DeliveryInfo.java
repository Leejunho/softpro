package com.example.myapplication;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DeliveryInfo implements Serializable {
    private String telephone;  // 전화번호
    private String boxnum;     // 택배함 번호
    private Date createdAt;    // 택배 도착 시간
    private String consumertelphone; // 입수자 전화번호

    public DeliveryInfo(String telephone, String boxnum, Date createdAt, String consumertelphone) {
        this.telephone = telephone;
        this.boxnum = boxnum;
        this.createdAt = createdAt;
        this.consumertelphone = consumertelphone;
    }

    public Map<String, Object> getDeliveryInfo() {
        Map<String, Object> docData = new HashMap<>();
        docData.put("telephone", telephone);
        docData.put("boxnum", boxnum);
        docData.put("createdAt", createdAt);
        docData.put("consumertelphone", consumertelphone);
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

    public String getConsumertelphone() {
        return this.consumertelphone;
    }
    public void setConsumertelphone(String consumertelphone) {
        this.consumertelphone = consumertelphone;
    }
}
