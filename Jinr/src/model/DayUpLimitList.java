package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by: Administrator on 2017/2/13.
 */
public class DayUpLimitList implements Serializable {

    private ArrayList<ProductLimit> list;
    private long current_time;

    public long getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(long current_time) {
        this.current_time = current_time;
    }

    public ArrayList<ProductLimit> getList() {
        return list;
    }

    public void setList(ArrayList<ProductLimit> list) {
        this.list = list;
    }
}
