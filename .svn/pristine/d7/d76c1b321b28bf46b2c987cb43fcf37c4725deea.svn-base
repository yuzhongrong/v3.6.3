package model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class UmMessageItem implements Serializable {
    private String msmType;
    private Long msmDate;
    private String msmcontent;
    private boolean msmstatus;
    private int unreadmsg;
    private Long id;
    private String displayType;
    private String otherParams;

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getOtherParams() {
        return otherParams;
    }

    public void setOtherParams(String otherParams) {//将参数中的\全部换成""
        String string=otherParams.replaceAll("\\\\","");//是4杠，不是2杠
        try {
            JSONObject jsonObject=new JSONObject(string);
            this.otherParams = jsonObject.optString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public int getUnreadmsg() {
        return unreadmsg;
    }

    public void setUnreadmsg(int unreadmsg) {
        this.unreadmsg = unreadmsg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsmType() {
        return msmType;
    }

    public void setMsmType(String msmType) {
        this.msmType = msmType;
    }

    public Long getMsmDate() {
        return msmDate;
    }

    public void setMsmDate(Long msmDate) {
        this.msmDate = msmDate;
    }

    public String getMsmcontent() {
        return msmcontent;
    }

    public void setMsmcontent(String msmcontent) {
        this.msmcontent = msmcontent;
    }

    public boolean getMsmstatus() {
        return msmstatus;
    }

    public void setMsmstatus(boolean msmstatus) {
        this.msmstatus = msmstatus;
    }
}
