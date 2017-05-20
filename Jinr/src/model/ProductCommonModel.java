package model;

import java.io.Serializable;

public class ProductCommonModel implements Serializable {
    private String code;
    private String days;
    private String rate;
    private String money;
    private String type;
    private String name;
    private String assetid;
    private String context;
    private String endTime;
    private String prospectiveYieldAmt;
    private String yieldAmt;
    private String status;
    private String describle;
    private String regularCode;
    private String eventKey;
    private String todayEarnRate;
    private String expect;
    private String upRate;
    private String product_code;
    private String product_num;
    private String product_status;

    public String getProduct_status() {
        return product_status;
    }

    public void setProduct_status(String product_status) {
        this.product_status = product_status;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_num() {
        return product_num;
    }

    public void setProduct_num(String product_num) {
        this.product_num = product_num;
    }

    public String getUpRate() {
        return upRate;
    }

    public void setUpRate(String upRate) {
        this.upRate = upRate;
    }

    public String getExpect() {
        return expect;
    }

    public void setExpect(String expect) {
        this.expect = expect;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    private float jdt;

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getProspectiveYieldAmt() {
        return prospectiveYieldAmt;
    }

    public void setProspectiveYieldAmt(String prospectiveYieldAmt) {
        this.prospectiveYieldAmt = prospectiveYieldAmt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }

    public String getYieldAmt() {
        return yieldAmt;
    }

    public void setYieldAmt(String yieldAmt) {
        this.yieldAmt = yieldAmt;
    }

    public String getRegularCode() {
        return regularCode;
    }

    public void setRegularCode(String dqCode) {
        this.regularCode = dqCode;
    }

    public float getJdt() {
        return jdt;
    }

    public void setJdt(float jdt) {
        this.jdt = jdt;
    }

    public String getTodayEarnRate() {
        return todayEarnRate;
    }

    public void setTodayEarnRate(String todayEarnRate) {
        this.todayEarnRate = todayEarnRate;
    }
}
