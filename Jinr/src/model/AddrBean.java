package model;

import java.io.Serializable;

/**
 * Created by: 966 on 2016/12/20.
 */
public class AddrBean implements Serializable {

    public String address_code ;//地址+编码
    public String region_name ; //省市区镇县
    public String province ;    //省id
    public String city ;        //市id
    public String area ;        //区镇id
    public String county ;      //县id

    public AddrBean(String address_code,String region_name,String province,
                      String city,String area,String county){
        this.address_code = address_code;
        this.region_name = region_name;
        this.province = province;
        this.city = city;
        this.county = county;
        this.area = area;
    }

}
