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
    private String tel;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getToken() {
        return token;
    }


    public String getLogin_time() {
        return login_time;
    }





}
