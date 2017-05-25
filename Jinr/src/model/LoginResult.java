package model;

import java.io.Serializable;

/**
 * Created by yuzhongrong on 2017/5/12.
 */

public class LoginResult implements Serializable{

    /**
     * code : 0
     * msg : 登录成功
     * data : {"token":"dad3a37aa9d50688b5157698acfd7aee","login_time":"2017-05-04 16:38:33"}
     */

    /**
     * token : dad3a37aa9d50688b5157698acfd7aee
     * login_time : 2017-05-04 16:38:33
     */
    private String token;
    private String login_time;
    private String name;
    private String mobilephone;
    private String uid;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogin_time() {
        return login_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
