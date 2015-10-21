package com.android.softwear.models;

/**
 * Created by Michael on 9/22/2015.
 */
public class Product {

    private int SKU;
    private String product_name;
    private String product_dept;
    private float price;
    private String product_desc;
    private String product_img;
    private int product_qty;

    public int getSKU() {
        return SKU;
    }

    public void setSKU(int SKU) {
        this.SKU = SKU;
    }

    public String getProduct_dept() {
        return product_dept;
    }

    public void setProduct_dept(String product_dept) {
        this.product_dept = product_dept;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    public int getProduct_qty() {
        return product_qty;
    }

    public void setProduct_qty(int product_qty) {
        this.product_qty = product_qty;
    }
}
