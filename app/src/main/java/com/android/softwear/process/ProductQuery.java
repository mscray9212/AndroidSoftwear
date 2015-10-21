package com.android.softwear.process;

import android.app.Activity;

import com.android.softwear.models.Product;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 10/4/2015.
 */
public class ProductQuery extends Activity {

    //public static ArrayList<Product> returnProducts() {
    public static String returnProducts(String results) {
        Connection conn = ConnectDB.getConnection();
        ArrayList<Product> list = new ArrayList<Product>();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `inventory`");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Product product = new Product();
                product.setSKU(result.getInt("SKU"));
                product.setProduct_name(result.getString("Name"));
                product.setProduct_dept(result.getString("Department"));
                product.setPrice(result.getFloat("Price"));
                product.setProduct_desc(result.getString("Description"));
                product.setProduct_img(result.getString("Image"));
                product.setProduct_qty(result.getInt("Quantity"));

                results +=
                        "Product: " + result.getString("Name") + "\n" +
                                "Description: " + result.getString("Description") + "\n" +
                                "Price: $" + result.getFloat("Price") + "\n";

                list.add(product);

            }
            //request.setAttribute("products", product_list);
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
        //return list;
    }

    //public static ArrayList<Product> returnProducts() {
    public static String returnProductView(String results) {
        Connection conn = ConnectDB.getConnection();
        ArrayList<Product> list = new ArrayList<Product>();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `inventory`");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Product product = new Product();
                product.setSKU(result.getInt("SKU"));
                product.setProduct_name(result.getString("Name"));
                product.setProduct_dept(result.getString("Department"));
                product.setPrice(result.getFloat("Price"));
                product.setProduct_desc(result.getString("Description"));
                product.setProduct_img(result.getString("Image"));
                product.setProduct_qty(result.getInt("Quantity"));

                results +=
                        "Product: " + result.getString("Name") + "\n" +
                                "Description: " + result.getString("Description") + "\n" +
                                "Price: $" + result.getFloat("Price") + "\n";

                list.add(product);

            }
            //request.setAttribute("products", product_list);
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
        //return list;
    }
}

