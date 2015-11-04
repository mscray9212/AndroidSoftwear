package com.android.softwear.process;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.softwear.MainActivity;
import com.android.softwear.R;
import com.android.softwear.models.Product;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Michael on 10/5/2015.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    private Activity activity;
    private ArrayList<Product> products;
    ArrayList<Product> cartItems;
    Product product;
    static Integer size;
    private static LayoutInflater inflater = null;
    static View vi;
    String money = "$";
    String dept = "Dept: ";
    String qty ="Qty: ";
    static String URI;


    public ProductAdapter(Activity activity, int textViewResourceId, ArrayList<Product> product) {
        super(activity, textViewResourceId, product);
        try {
            this.activity = activity;
            this.products = product;
            setCartItems(products);
            new getCartCount().execute();
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //imageLoader = new ImageLoader(activity.getApplicationActivity());
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public int getCount() {
        return products.size();
    }

    public Product getItem(Product position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView product_name;
        public TextView product_desc;
        public TextView product_dept;
        public TextView product_price;
        public TextView product_qty;
        public ImageView product_img;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        vi = convertView;
        final ViewHolder holder;

        if(cartItems.size() == getCount()) {
            try {
                if (convertView == null) {
                    vi = inflater.inflate(R.layout.activity_cart_items, parent, false);
                    holder = new ViewHolder();
                    holder.product_name = (TextView) vi.findViewById(R.id.product_name);
                    holder.product_dept = (TextView) vi.findViewById(R.id.product_dept);
                    holder.product_desc = (TextView) vi.findViewById(R.id.product_desc);
                    holder.product_price = (TextView) vi.findViewById(R.id.product_price);
                    holder.product_qty = (TextView) vi.findViewById(R.id.product_qty);
                    holder.product_img = (ImageView) vi.findViewById(R.id.icon);
                    vi.setTag(holder);
                } else {
                    holder = (ViewHolder) vi.getTag();
                }
                Button remove = (Button) vi.findViewById(R.id.remove_btn);
                URI = "http://www.michaelscray.com/Softwear/graphics/";
                URI += products.get(position).getProduct_img();
                Uri uri = Uri.parse(URI + products.get(position).getProduct_img());
                holder.product_name.setText(products.get(position).getProduct_name());
                holder.product_desc.setText(products.get(position).getProduct_desc());
                holder.product_dept.setText(dept + products.get(position).getProduct_dept());
                holder.product_price.setText(money + String.valueOf(products.get(position).getPrice()));
                holder.product_qty.setText(qty + String.valueOf(products.get(position).getProduct_qty()));
                Picasso.with(getContext()).load(URI).error(R.mipmap.ic_launcher).into(holder.product_img);
                setProduct(getItem(position));

                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do something
                        new removeCartItem().execute(); //or some other task
                        notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return vi;
    }

    public class removeCartItem extends AsyncTask<Void, Void, Void> {
        String tempUser = MainActivity.currentAccount.getUsername();
        Product updateCart = getProduct();
        ArrayList<Product> cartItems = getCartItems();
        Integer tempSKU = updateCart.getSKU();
        String queryResult = "";

        protected void onPreExecute() {

        }

        protected Void doInBackground(Void... arg0)  {

            if(tempUser != null && !(tempUser.equals("Guest"))) {
                try {
                    Connection conn = ConnectDB.getConnection();
                    String queryString = "DELETE FROM `Orders` WHERE `User_Name` = '"+tempUser+"' AND `SKU` = '"+tempSKU+"'";
                /*  Insert User_Name, SKU, Price, and Shipped   */
                    PreparedStatement st = conn.prepareStatement(queryString);
                    st.executeUpdate();
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    queryResult = "Database connection failure!\n" + e.toString();
                }
                cartItems.remove(updateCart);
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(getContext(), "Removed item: " + tempSKU + " from your cart!", Toast.LENGTH_SHORT).show();
        }

    }

    public class getCartCount extends AsyncTask<Void, Void, Void> {
        String tempUser = MainActivity.currentAccount.getUsername();
        int cartNum = 0;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if(tempUser != null) {
                try {
                    Connection conn = ConnectDB.getConnection();
                    String queryString = "SELECT * FROM Orders WHERE `User_Name` = '" + tempUser + "'";

                    PreparedStatement st = conn.prepareStatement(queryString);
                    //st.setString(1, tempUser);

                    final ResultSet result = st.executeQuery(queryString);

                    while (result.next()) {
                        cartNum++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            setCartSize(cartNum);
            return null;
        }

        protected void onPostExecute(Void result) {

        }
    }

    private void setProduct(Product product) {
        this.product = product;
    }

    private Product getProduct() {
        return product;
    }

    private void setCartItems(ArrayList<Product> cartItems) {
        this.cartItems = cartItems;
    }

    private ArrayList<Product> getCartItems() {
        return cartItems;
    }

    private void setCartSize(Integer size) {
        this.size = size;
    }

    private Integer getCartSize() {
        return size;
    }

}