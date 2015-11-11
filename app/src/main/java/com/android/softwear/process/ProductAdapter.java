package com.android.softwear.process;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.softwear.Cart;
import com.android.softwear.MainActivity;
import com.android.softwear.R;
import com.android.softwear.models.Product;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Michael on 10/5/2015.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    private Activity activity;
    private ArrayList<Product> products;
    Product product;
    private Integer SKU;
    Menu menu;
    static Product removeProduct;
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

    public Product getItem(int position) {
        return products.get(position);
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
        int pos = position;
        vi = convertView;
        final ViewHolder holder;

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

                setSKU(products.get(position).getSKU());
                remove.setOnClickListener(new View.OnClickListener() {
                    Boolean update = false;
                    @Override
                    public void onClick(View v) {
                        update = true;
                        Intent intent = new Intent(getContext(), Cart.class);
                        intent.putExtra("update", update);
                        SKU = getSKU();
                        new removeCartItem().execute();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(intent);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }


        return vi;
    }

    public class removeCartItem extends AsyncTask<Void, Integer, Void> {
        String tempUser = MainActivity.currentAccount.getUsername();
        Product updateCart = getProduct();
        ArrayList<Product> cartUpdate = Cart.getCartList();
        Integer cartNumber = Cart.getCartNumber();
        Integer tempSKU = getSKU();
        String queryResult = "";

        protected void onPreExecute() {

        }

        protected Void doInBackground(Void... arg0)  {

            if(tempUser != null && !(tempUser.equals("Guest"))) {
                try {
                    Connection conn = ConnectDB.getConnection();
                    String queryString = "DELETE FROM `Orders` WHERE `User_Name` = '"+tempUser+"' AND `SKU` = '"+tempSKU+"'";
                    PreparedStatement st = conn.prepareStatement(queryString);
                    st.executeUpdate();
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    queryResult = "Database connection failure!\n" + e.toString();
                }
                cartUpdate.remove(updateCart);
                //Cart.setCartList(updateCart);
                MainActivity.setCartSize(cartNumber - 1);
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
            super.onPostExecute(result);
        }
    }

    private Product getProduct() {
        return product;
    }

    private void setCartSize(Integer size) {
        this.size = size;
    }

    public void setSKU(Integer SKU) {
        this.SKU = SKU;
    }

    public Integer getSKU() {
        return SKU;
    }



}