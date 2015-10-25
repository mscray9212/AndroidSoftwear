package com.android.softwear;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.softwear.interfaces.CartChangeListener;
import com.android.softwear.models.Product;
import com.android.softwear.process.ConnectDB;
import com.android.softwear.process.ProductsAdapter;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Michael on 10/19/2015.
 */
public class Cart extends AppCompatActivity {

    private String user;
    static CartChangeListener cartChangeListener;
    ProductsAdapter adaptCart;
    ArrayList<Product> cartItems = new ArrayList<>();
    Menu menu;
    float total = (float)0.00;
    float shipCost = 15;
    float tax = (float)0.07;
    String URI;
    TextView subTotal;
    TextView shipping;
    TextView taxes;
    TextView totals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //ListView.addHeaderView(headerView);
        user = MainActivity.currentAccount.getUsername();
        if(user == null || user.equals("Guest")) {
            shipCost = (float)0.00;
            tax = (float)0.00;
        }
        if(user != null && !(user.equals("Guest"))) {
            new getCartIcon().execute();
            ListView listView = (ListView) findViewById(R.id.list_cart_view);
            new returnCartItems().execute();
            //if (!(cartItems.isEmpty())) {
                adaptCart = new ProductsAdapter(Cart.this, 0, cartItems);
                listView.setOnItemClickListener(new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                        try {
                            ProductsAdapter.ViewHolder holder = new ProductsAdapter.ViewHolder();
                            holder.product_name = (TextView) v.findViewById(R.id.product_name);
                            holder.product_dept = (TextView) v.findViewById(R.id.product_dept);
                            holder.product_desc = (TextView) v.findViewById(R.id.product_desc);
                            holder.product_price = (TextView) v.findViewById(R.id.product_price);
                            holder.product_qty = (TextView) v.findViewById(R.id.product_qty);
                            holder.product_img = (ImageView) findViewById(R.id.icon);

                            URI = "http://www.michaelscray.com/Softwear/graphics/";
                            String dept = "Dept: ";
                            String money = "$";
                            String qty = "Qty: ";
                            URI += cartItems.get(position).getProduct_img();
                            Uri uris = Uri.parse(URI + cartItems.get(position).getProduct_img());
                            URI uri = java.net.URI.create(URI);
                            holder.product_name.setText(cartItems.get(position).getProduct_name());
                            holder.product_desc.setText(cartItems.get(position).getProduct_desc());
                            holder.product_dept.setText(dept + cartItems.get(position).getProduct_dept());
                            holder.product_price.setText(money + String.valueOf(cartItems.get(position).getPrice()));
                            holder.product_qty.setText(qty + String.valueOf(cartItems.get(position).getProduct_qty()));
                            Picasso.with(getApplicationContext()).load(URI).error(R.mipmap.ic_launcher).into(holder.product_img);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                listView.setAdapter(adaptCart);
            //}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        return true;
    }

    public class getCartIcon extends AsyncTask<Void, Void, Void> {
        String tempUser = user;
        int cartNum = 0;
        float tempTotal;

        @Override
        protected void onPreExecute() {
            subTotal = (TextView) findViewById(R.id.textView_subTotal);
            shipping = (TextView) findViewById(R.id.textView_shipping);
            totals = (TextView) findViewById(R.id.textView_total);
            taxes = (TextView) findViewById(R.id.textView_taxes);
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
                        try {
                            tempTotal += result.getFloat("Price");
                            //skus.add(result.getInt("SKU"));
                        } catch(SQLException e) {
                            e.printStackTrace();
                        }
                        cartNum++;
                        //setTotal(result.getFloat(String.valueOf("Price")));
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            getCartItems(cartNum);
            subTotal.setText(String.valueOf(tempTotal));
            shipping.setText(String.valueOf(shipCost));
            tax = tempTotal * tax;
            taxes.setText(String.valueOf(tax));
            totals.setText(String.valueOf(tempTotal + shipCost + tax));
            //setTotal(tempTotal);
            //super.onPostExecute(result);
        }
    }

    public class returnCartItems extends AsyncTask<Void, Void, Void> {
        String tempUser = user;
        Product product = null;
        List<Integer> skus = new ArrayList<>();
        //String descr = "DETAILS: \n\n";

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
                        try {
                            skus.add(result.getInt("SKU"));
                        } catch(SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Connection conn = ConnectDB.getConnection();
                    String queryString = "SELECT * FROM Inventory";

                    PreparedStatement st = conn.prepareStatement(queryString);
                    //st.setString(1, tempUser);

                    final ResultSet result = st.executeQuery(queryString);

                    while (result.next()) {
                        for(int i=0; i < skus.size(); i++) {
                            if(skus.get(i) == result.getInt("SKU")) {
                                product = new Product();
                                product.setProduct_name(result.getString("Name"));
                                product.setProduct_dept(result.getString("Department"));
                                product.setProduct_desc(result.getString("Description"));
                                product.setPrice(result.getFloat("Price"));
                                product.setProduct_qty(result.getInt("Quantity"));
                                product.setProduct_img(result.getString("Image"));
                                cartItems.add(product);
                                /*
                                descr += "Product: " + result.getString("Name") + "\n" +
                                "Department: " + result.getString("Department") + "\n" +
                                "Price: $" + result.getFloat("Price") + "\n\n";
                                */
                            }
                        }
                    }
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public void setTotal(float total) {
        this.total += total;
    }

    public float getTotal() {
        return total;
    }

    public void getCartItems(int cart) {

        MenuItem cartMenuItem = (MenuItem) menu.findItem(R.id.action_cart);
        if (cart == 0) {
            cartMenuItem.setIcon(R.drawable.cart0);
        }
        if (cart == 1) {
            cartMenuItem.setIcon(R.drawable.cart1);
        }
        if (cart == 2) {
            cartMenuItem.setIcon(R.drawable.cart2);
        }
        if (cart == 3) {
            cartMenuItem.setIcon(R.drawable.cart3);
        }
        if (cart == 4) {
            cartMenuItem.setIcon(R.drawable.cart4);
        }
        if (cart == 5) {
            cartMenuItem.setIcon(R.drawable.cart5);
        }
        if (cart > 5) {
            cartMenuItem.setIcon(R.drawable.cart5plus);
        }
        if (cart > 10) {
            cartMenuItem.setIcon(R.drawable.cart10plus);
        }
    }
}
