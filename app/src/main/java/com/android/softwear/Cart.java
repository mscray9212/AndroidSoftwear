package com.android.softwear;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.android.softwear.MainActivity;
import com.android.softwear.R;
import com.android.softwear.models.Product;
import com.android.softwear.process.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Michael on 10/19/2015.
 */
public class Cart extends AppCompatActivity {

    private String user;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = MainActivity.currentAccount.getUsername();

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
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;
        // Associate searchable configuration with the SearchView
        /*
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        */
        new getCartIcon().execute();

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

        @Override
        protected Void doInBackground(Void... arg0) {
            if(tempUser != null) {
                try {
                    Connection conn = ConnectDB.getConnection();
                    String queryString = "SELECT * FROM Orders";

                    PreparedStatement st = conn.prepareStatement(queryString);
                    //st.setString(1, tempUser);

                    final ResultSet result = st.executeQuery(queryString);

                    while (result.next()) {
                        if(result.getString("User_Name").equals(tempUser)) {
                            cartNum++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            getCartItems(cartNum);
            super.onPostExecute(result);
        }
    }
}
