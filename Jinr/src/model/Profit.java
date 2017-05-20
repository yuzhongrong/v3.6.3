package model;

import java.io.Serializable;

/**
 * 单个收益属性
 *
 * @author Administrator
 */
public class Profit implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7809007519126382680L;

    private String name = "";
    private String money;
    private String date;
    private String type;
    private String y_shouyu;//
    private String y_date;// 时间
    private String jdt;// 进度条值
    private int tag;// 标志收益条数目

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

    public String getY_date() {
        return y_date;
    }

    public void setY_date(String y_date) {
        this.y_date = y_date;
    }

    public String getJdt() {
        return jdt;
    }

    public void setJdt(String jdt) {
        this.jdt = jdt;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getY_shouyu() {
        return y_shouyu;
    }

    public void setY_shouyu(String y_shouyu) {
        this.y_shouyu = y_shouyu;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Profit [y_shouyu=" + y_shouyu + ", y_date=" + y_date + ", jdt="
                + jdt + ", tag=" + tag + "]";
    }

}
