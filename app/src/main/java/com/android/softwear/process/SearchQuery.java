package com.android.softwear.process;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.softwear.R;
import com.android.softwear.models.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 9/22/2015.
 */
public class SearchQuery extends Activity {

    public SearchQuery() {}

    public static ArrayList<Product> returnProductView (String search){

        ArrayList<Product> list = new ArrayList<Product>();
        Product product = null;

        try {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM inventory WHERE Name Like ? OR Department LIKE ? OR Description Like ?");
            ps.setString(1, "%" + search + "%");
            ps.setString(2, "%" + search + "%");
            ps.setString(3, "%" + search + "%");
            ResultSet result = ps.executeQuery();

            while (result.next()) {

                product = new Product();
                product.setSKU(result.getInt("SKU"));
                product.setProduct_name(result.getString("Name"));
                product.setProduct_dept(result.getString("Department"));
                product.setPrice(result.getFloat("Price"));
                product.setProduct_desc(result.getString("Description"));
                product.setProduct_img(result.getString("Image"));
                product.setProduct_qty(result.getInt("Quantity"));

                list.add(product);
            }

            //request.setAttribute("products", product_list);
            conn.close();

        }

        catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList<Product> returnAllProducts (){

        ArrayList<Product> list = new ArrayList<Product>();
        Product product = null;

        try {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM inventory");
            ResultSet result = ps.executeQuery();

            while (result.next()) {

                product = new Product();
                product.setSKU(result.getInt("SKU"));
                product.setProduct_name(result.getString("Name"));
                product.setProduct_dept(result.getString("Department"));
                product.setPrice(result.getFloat("Price"));
                product.setProduct_desc(result.getString("Description"));
                product.setProduct_img(result.getString("Image"));
                product.setProduct_qty(result.getInt("Quantity"));

                list.add(product);
            }

            conn.close();

        }

        catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

}
