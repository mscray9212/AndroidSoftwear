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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
public class Search extends AppCompatActivity {

    // placeholder that you will be updating with the database data
    ArrayList<Product> products = MainActivity.getProducts();
    Menu mainMenu;
    ProductsAdapter adaptProducts;
    ListView listview;
    static int pos;
    static AdapterView<?> par;
    String URI;
    private String user;
    private Integer SKU;
    private float price;
    static boolean updatedCart = false;
    static final String TAG = "Search text:";
    Menu menu;
    Bitmap bit;
    /*
    // action bar
    private ActionBar actionBar;

    // Title navigation Spinner data
    ArrayList<String> navSpinner;

    // Navigation adapter
    TitleNavigationAdapter adapter;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);
        if(products.isEmpty()) {
            new returnAllProducts().execute();
        }
        listview = (ListView) findViewById(R.id.list_view);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menuItem.getActionView();
        //android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        MenuItem menuCategory = menu.findItem(R.id.action_category);
        Spinner spinner = (Spinner) findViewById(R.id.action_category);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //spinner.setAdapter(adapter);
        Log.d(TAG, "searchView: " + searchView.toString());

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                doStuff(s);
                Log.d(TAG, "onQueryTextSubmit: " + s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "onQueryTextChange: " + s);
                //doStuff(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void doStuff(String result) {
        ArrayList<Product> currProducts = MainActivity.getProducts();
        ArrayList<Product> searchProducts = new ArrayList<>();
        listview = (ListView) findViewById(R.id.list_view);
        Product tempProduct;
        if(result != null) {
            for (int i = 0; i < currProducts.size(); i++) {
                if (currProducts.get(i).getProduct_name().toLowerCase().contains(result.toLowerCase())) {
                    Log.d(TAG, "Product list contains: " + result);
                    tempProduct = new Product();
                    tempProduct.setSKU(products.get(i).getSKU());
                    tempProduct.setProduct_name(products.get(i).getProduct_name());
                    tempProduct.setProduct_dept(products.get(i).getProduct_dept());
                    tempProduct.setPrice(products.get(i).getPrice());
                    tempProduct.setProduct_desc(products.get(i).getProduct_desc());
                    tempProduct.setProduct_img(products.get(i).getProduct_img());
                    tempProduct.setProduct_qty(products.get(i).getProduct_qty());
                    try {
                        searchProducts.add(tempProduct);
                        Log.d(TAG, "tempProduct name" + products.get(i).getProduct_name());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        adaptProducts = new ProductsAdapter(Search.this, 0, searchProducts);
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
    }

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
