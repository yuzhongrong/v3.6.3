package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by: Administrator on 2017/2/9.
 */
public class ProductList implements Serializable {
    private ArrayList<ProductModel> list;

    public ArrayList<ProductModel> getList() {
        return list;
    }

    public void setList(ArrayList<ProductModel> list) {
        this.list = list;
    }
}
