package model;

import java.io.Serializable;

/**
 * 获取系统消息接口的实体类
 * Created by: Administrator on 2017/3/17.
 */
public class SystemNoticeModel implements Serializable {
    private String huodong;
    private String gonggao;
    private int bk;

    public String getHuodong() {
        return huodong;
    }

    public void setHuodong(String huodong) {
        this.huodong = huodong;
    }

    public String getGonggao() {
        return gonggao;
    }

    public void setGonggao(String gonggao) {
        this.gonggao = gonggao;
    }

    public int getBk() {
        return bk;
    }

    public void setBk(int bk) {
        this.bk = bk;
    }
}
