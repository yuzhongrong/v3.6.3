package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CardReelMode implements Serializable {
    private String coupon_type;//3免手续费卷
    private String id;
    private String money;
    private String name;
    private String selected_name;
    private String use_mode;//1比例 2固定金额

    public String getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(String coupon_type) {
        this.coupon_type = coupon_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelected_name() {
        return selected_name;
    }

    public void setSelected_name(String selected_name) {
        this.selected_name = selected_name;
    }

    public String getUse_mode() {
        return use_mode;
    }

    public void setUse_mode(String use_mode) {
        this.use_mode = use_mode;
    }
}
