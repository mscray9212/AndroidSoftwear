package com.android.softwear.process;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.android.softwear.R;
import com.android.softwear.models.Product;

import java.util.ArrayList;

/**
 * Created by Michael on 10/19/2015.
 */
public class Cart extends AppCompatActivity {

    ArrayList<Product> cartItems;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



        public void getCartItems() {

            cartItems = new ArrayList<Product>();

            MenuItem cartMenuItem = (MenuItem) menu.findItem(R.id.action_cart);
            if (cartItems.size() == 0) {
                cartMenuItem.setIcon(R.drawable.cart0);
            }
            if (cartItems.size() == 1) {
                cartMenuItem.setIcon(R.drawable.cart1);
            }
            if (cartItems.size() == 2) {
                cartMenuItem.setIcon(R.drawable.cart2);
            }
            if (cartItems.size() == 3) {
                cartMenuItem.setIcon(R.drawable.cart3);
            }
            if (cartItems.size() == 4) {
                cartMenuItem.setIcon(R.drawable.cart4);
            }
            if (cartItems.size() == 5) {
                cartMenuItem.setIcon(R.drawable.cart5);
            }
            if (cartItems.size() > 5) {
                cartMenuItem.setIcon(R.drawable.cart5plus);
            }
            if (cartItems.size() > 10) {
                cartMenuItem.setIcon(R.drawable.cart10plus);
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        getCartItems();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        return true;
    }
}
