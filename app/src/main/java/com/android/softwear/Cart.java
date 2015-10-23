package com.android.softwear;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.softwear.MainActivity;
import com.android.softwear.R;
import com.android.softwear.interfaces.CartChangeListener;
import com.android.softwear.models.Product;
import com.android.softwear.process.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Michael on 10/19/2015.
 */
public class Cart extends AppCompatActivity {

    private String user;
    static CartChangeListener cartChangeListener;
    Menu menu;
    float total = (float)0.00;
    float shipCost = 15;
    float tax = (float)0.07;
    TextView subTotal;
    TextView shipping;
    TextView taxes;
    TextView totals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        user = MainActivity.currentAccount.getUsername();
        if(user == null || user.equals("Guest")) {
            shipCost = (float)0.00;
            tax = (float)0.00;
        }
        if(user != null && !(user.equals("Guest"))) {
            new getCartIcon().execute();
        }
        // Associate searchable configuration with the SearchView
        /*
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        //new getCartIcon().execute();
        subTotal = (TextView) findViewById(R.id.textView_subTotal);
        shipping = (TextView) findViewById(R.id.textView_shipping);
        totals = (TextView) findViewById(R.id.textView_total);
        taxes = (TextView) findViewById(R.id.textView_taxes);
        total = getTotal();
        subTotal.setText(String.valueOf(total));
        shipping.setText(String.valueOf(shipCost));
        tax = total * tax;
        totals.setText(String.valueOf(total + shipCost + tax));
        */

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
            totals.setText(String.valueOf(tempTotal + shipCost + tax));
            //setTotal(tempTotal);
            //super.onPostExecute(result);
        }
    }

    public class getCartTotal extends AsyncTask<Void, Void, Void> {
        String tempUser = user;
        float tempTotal;
        int cartNum = 0;

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
                        tempTotal += result.getFloat("Price");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            setTotal(tempTotal);
            super.onPostExecute(result);
        }
    }

    public class getCartItems extends AsyncTask<Void, Void, Void> {
        String tempUser = user;
        float tempTotal;
        int cartNum = 0;

        @Override
        protected Void doInBackground(Void... arg0) {
            if(tempUser != null) {
                try {
                    Connection conn = ConnectDB.getConnection();
                    String queryString = "SELECT * FROM Inventory, Orders WHERE `User_Name` = '" + tempUser + "'";

                    PreparedStatement st = conn.prepareStatement(queryString);
                    //st.setString(1, tempUser);

                    final ResultSet result = st.executeQuery(queryString);

                    while (result.next()) {
                        tempTotal += result.getFloat("Price");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            setTotal(tempTotal);
            super.onPostExecute(result);
        }
    }

    public void setTotal(float total) {
        this.total += total;
    }

    public float getTotal() {
        return total;
    }
}
