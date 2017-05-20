package model;

import java.io.Serializable;

/**
 * Created by： Administrator on 2017/2/9.
 */
public class LiveProductModel implements Serializable {

    private String id;
    private String assetid;
    private String code_id;
    private String name;
    private String shouyulv;
    private String total_trade_volume;
    private String yesterday_total_money;
    private String shouyulvs;
    private String day;
    private String textdown_left;
    private String textdown_middle;
    private String textdown_right;
    private String pct;
    private String text_top;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid;
    }

    public String getCode_id() {
        return code_id;
    }

    public void setCode_id(String code_id) {
        this.code_id = code_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShouyulv() {
        return shouyulv;
    }

    public void setShouyulv(String shouyulv) {
        this.shouyulv = shouyulv;
    }

    public String getTotal_trade_volume() {
        return total_trade_volume;
    }

    public void setTotal_trade_volume(String total_trade_volume) {
        this.total_trade_volume = total_trade_volume;
    }

    public String getYesterday_total_money() {
        return yesterday_total_money;
    }

    public void setYesterday_total_money(String yesterday_total_money) {
        this.yesterday_total_money = yesterday_total_money;
    }

    public String getShouyulvs() {
        return shouyulvs;
    }

    public void setShouyulvs(String shouyulvs) {
        this.shouyulvs = shouyulvs;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTextdown_left() {
        return textdown_left;
    }

    public void setTextdown_left(String textdown_left) {
        this.textdown_left = textdown_left;
    }

    public String getTextdown_middle() {
        return textdown_middle;
    }

    public void setTextdown_middle(String textdown_middle) {
        this.textdown_middle = textdown_middle;
    }

    public String getTextdown_right() {
        return textdown_right;
    }

    public void setTextdown_right(String textdown_right) {
        this.textdown_right = textdown_right;
    }

    public String getPct() {
        return pct;
    }

    public void setPct(String pct) {
        this.pct = pct;
    }

    public String getText_top() {
        return text_top;
    }

    public void setText_top(String text_top) {
        this.text_top = text_top;
    }

    @Override
    public String toString() {
        return "LiveProductModel{" +
                "id='" + id + '\'' +
                ", assetid='" + assetid + '\'' +
                ", code_id='" + code_id + '\'' +
                ", name='" + name + '\'' +
                ", shouyulv='" + shouyulv + '\'' +
                ", total_trade_volume='" + total_trade_volume + '\'' +
                ", yesterday_total_money='" + yesterday_total_money + '\'' +
                ", shouyulvs='" + shouyulvs + '\'' +
                ", day='" + day + '\'' +
                ", textdown_left='" + textdown_left + '\'' +
                ", textdown_middle='" + textdown_middle + '\'' +
                ", textdown_right='" + textdown_right + '\'' +
                ", pct='" + pct + '\'' +
                ", text_top='" + text_top + '\'' +
                '}';
    }
}
