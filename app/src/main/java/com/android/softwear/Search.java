package com.android.softwear;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.softwear.models.Product;
import com.android.softwear.process.ConnectDB;
import com.android.softwear.process.ProductAdapter;
import com.android.softwear.process.ProductsAdapter;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Michael on 10/6/2015.
 */
public class Search extends Activity {

    // placeholder that you will be updating with the database data
    ArrayList<Product> products = MainActivity.getProducts();
    public SearchView mSearchView;
    Menu mainMenu;
    ProductsAdapter adaptProducts;
    static int pos;
    static AdapterView<?> par;
    String URI;
    private String user;
    private Integer SKU;
    private float price;
    static boolean updatedCart = false;
    Menu menu;
    Bitmap bit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);
        if(products.isEmpty()) {
            new returnAllProducts().execute();
        }
        final ListView listview = (ListView) findViewById(R.id.list_view);
        adaptProducts = new ProductsAdapter(Search.this, 0, products);
        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                try {

                    setContentView(R.layout.activity_product_view);
                    //vi = inflater.inflate(R.layout.activity_product_view, parent, false);
                    ProductsAdapter.ViewHolder holder = new ProductsAdapter.ViewHolder();
                    holder.product_name = (TextView) findViewById(R.id.product_name);
                    holder.product_dept = (TextView) findViewById(R.id.product_dept);
                    holder.product_desc = (TextView) findViewById(R.id.product_desc);
                    holder.product_price = (TextView) findViewById(R.id.product_price);
                    holder.product_qty = (TextView) findViewById(R.id.product_qty);
                    holder.product_img = (ImageView) findViewById(R.id.icon);

                    URI = "http://www.michaelscray.com/Softwear/graphics/";
                    String dept = "Dept: ";
                    String money = "$";
                    String qty = "Qty: ";
                    URI += products.get(position).getProduct_img();
                    Uri uris = Uri.parse(URI + products.get(position).getProduct_img());
                    URI uri = java.net.URI.create(URI);
                    holder.product_name.setText(products.get(position).getProduct_name());
                    holder.product_desc.setText(products.get(position).getProduct_desc());
                    holder.product_dept.setText(dept + products.get(position).getProduct_dept());
                    holder.product_price.setText(money + String.valueOf(products.get(position).getPrice()));
                    holder.product_qty.setText(qty + String.valueOf(products.get(position).getProduct_qty()));
                    Picasso.with(getApplicationContext()).load(URI).error(R.mipmap.ic_launcher).into(holder.product_img);

                    Button addToCart = (Button) findViewById(R.id.cart_btn);
                    addToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*  Get User_Name, SKU, Price, and Shipped   */
                            user = MainActivity.currentAccount.getUsername();
                            SKU = products.get(getPos()).getSKU();
                            //String desc = products.get(getPos()).getProduct_desc();
                            //String dept = products.get(getPos()).getProduct_dept();
                            price = products.get(getPos()).getPrice();
                            //int qty = products.get(getPos()).getProduct_qty();
                            //String img = products.get(getPos()).getProduct_img();
                            setUser(user);
                            setSKU(SKU);
                            setPrice(price);
                            updatedCart = true;
                            new addItemToCart().execute();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        adaptProducts.notifyDataSetChanged();
        listview.setAdapter(adaptProducts);
        /*
        EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Search.this.adaptProducts.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }

        });
        handleIntent(getIntent());
        /*
        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            inputSearch = intent.getStringExtra(SearchManager.QUERY);
            try {
                new returnProductView().execute();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        */
    }

    /*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchViewMenuItem.getActionView();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search);

        return super.onPrepareOptionsMenu(menu);
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        new MenuInflater(getApplication()).inflate(R.menu.search_menu, menu);
        getMenuInflater().inflate(R.menu.search_menu, menu);
        this.menu = menu;
        */
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        // When using the support library, the setOnActionExpandListener() method is
        // static and accepts the MenuItem object as an argument
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });
        return true;
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        MenuItemCompat.setOnActionExpandListener(searchItem,new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });
        return true;
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            startActivity(new Intent(getApplicationContext(), Search.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }
    }
    */

    private class returnProductView extends AsyncTask<Void, View, Void> {

        ProductsAdapter.ViewHolder holder;
        View vi = null;
        LayoutInflater inflater = null;

        protected void onPreExecute() {
            try {
                if (vi == null) {
                    vi = inflater.inflate(R.layout.activity_product_view, getPar(), false);
                    ProductsAdapter.ViewHolder holder = new ProductsAdapter.ViewHolder();
                    holder.product_name = (TextView) vi.findViewById(R.id.product_name);
                    holder.product_dept = (TextView) vi.findViewById(R.id.product_dept);
                    holder.product_desc = (TextView) vi.findViewById(R.id.product_desc);
                    holder.product_price = (TextView) vi.findViewById(R.id.product_price);
                    holder.product_qty = (TextView) vi.findViewById(R.id.product_qty);
                    vi.setTag(holder);

                } else {
                    holder = (ProductsAdapter.ViewHolder) vi.getTag();
                }
                String URI = "http://www.michaelscray.com/Softwear/graphics/";
                String dept = "Dept: ";
                String money = "$";
                String qty = "Qty: ";
                int pos = getPos();
                URI += products.get(pos).getProduct_img();
                holder.product_name.setText(products.get(pos).getProduct_name());
                holder.product_desc.setText(products.get(pos).getProduct_desc());
                holder.product_dept.setText(dept + products.get(pos).getProduct_dept());
                holder.product_price.setText(money + String.valueOf(products.get(pos).getPrice()));
                holder.product_qty.setText(qty + String.valueOf(products.get(pos).getProduct_qty()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected Void doInBackground(Void... arg0)  {

            //new ProductsAdapter.getImageView().execute();
            //adaptProduct = new ProductsAdapter(Search.this, position, products);
            //v.getContext().getResources();
            //v.(adaptProduct);
            return null;
        }

        protected View onPostExecute() {
            return vi;
        }

    }

    private class returnAllProducts extends AsyncTask<Void, Void, Void> {

        String queryResult = "";
        Product product = null;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Search.this);
            pDialog.setTitle("Please wait");
            pDialog.setMessage("Loading products...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //protected Void doInBackground(Void... arg0) {

            try {
                queryResult = "Database connection success\n";
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM Inventory");
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

                    products.add(product);
                }
                pDialog.dismiss();
                conn.close();
            }

            catch(Exception e) {
                e.printStackTrace();
                queryResult = "Database connection failure!\n" + e.toString();
            }
            return null;
            //return products;

        }

        protected void onPostExecute(Void result) {
            //super.onPostExecute(result);
            //getData.setText(queryResult);
            //return products;

        }

    }

    public class addItemToCart extends AsyncTask<Void, Void, Void> {
        String tempUser = getUser();
        int tempSKU = getSKU();
        float tempPrice = getPrice();
        String queryResult = "";
        ProgressDialog pDialog;

        protected void onPreExecute() {
            //bench warmer
            if(tempUser == null || tempUser.equals("Guest")) {
                pDialog = new ProgressDialog(Search.this);
                pDialog.setTitle("Login First");
                pDialog.setMessage("Please, sign in to use cart services");
                pDialog.setCancelable(true);
                pDialog.show();
            }
        }

        protected Void doInBackground(Void... arg0)  {

            if(tempUser != null && !(tempUser.equals("Guest"))) {
                try {
                    Connection conn = ConnectDB.getConnection();
                    String queryString = "INSERT INTO Orders (User_Name, SKU, Price, Shipped) VALUES (?, ?, ?, ?)";
                /*  Insert User_Name, SKU, Price, and Shipped   */
                    PreparedStatement st = conn.prepareStatement(queryString);
                    st.setString(1, tempUser);
                    st.setInt(2, tempSKU);
                    st.setFloat(3, tempPrice);
                    st.setString(4, "N");
                    st.executeUpdate();

                    queryResult = ("Item: " + tempSKU + " added to your cart!");
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    queryResult = "Database connection failure!\n" + e.toString();
                }
                //result = ProductQuery.returnProducts(result);
            }
            else {
                pDialog.dismiss();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if(tempUser != null && !(tempUser.equals("Guest"))) {
                Toast.makeText(Search.this, "Added item: " + tempSKU + " to your cart!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Search.this, "You forgot to login, didn't you?", Toast.LENGTH_SHORT).show();
            }
            //bench cooler
            //getData.setText(queryResult);
            //super.onPostExecute(result);
        }

    }

    public int getPos() {
        return pos;
    }

    public AdapterView getPar() {
        return par;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setSKU(Integer SKU) {
        this.SKU = SKU;
    }

    public Integer getSKU() {
        return SKU;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPrice() {
        return price;
    }

    public static boolean cartStatus() {
        return updatedCart;
    }


}
