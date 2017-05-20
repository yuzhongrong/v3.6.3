package model;

import java.io.Serializable;

/**
 * Created by： Administrator on 2017/2/9.
 */
public class ProductModel implements Serializable {

    private String assetid;
    private String code;
    private String name;
    private String rate;
    private String days;
    private String rates;
    private String pct;
    private String text_top;
    private String textdown_left;
    private String textdown_middle;
    private String textdown_right;
    private String desc;
    private String investRateFinish;
    private String arrow;
    private String goods_type;
    private String status;
    private int is_open;

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIs_open() {
        return is_open;
    }

    public void setIs_open(int is_open) {
        this.is_open = is_open;
    }

    public String getText_top() {
        return text_top;
    }

    public void setText_top(String text_top) {
        this.text_top = text_top;
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

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getPct() {
        return pct;
    }

    public void setPct(String pct) {
        this.pct = pct;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInvestRateFinish() {
        return investRateFinish;
    }

    public void setInvestRateFinish(String investRateFinish) {
        this.investRateFinish = investRateFinish;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getArrow() {
        return arrow;
    }

    public void setArrow(String arrow) {
        this.arrow = arrow;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }
}
