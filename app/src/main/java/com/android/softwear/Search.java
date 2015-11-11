package com.android.softwear;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.softwear.interfaces.CartChangeListener;
import com.android.softwear.models.Product;
import com.android.softwear.process.ConnectDB;
import com.android.softwear.process.ProductsAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
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
    ProductsAdapter adaptProducts;
    ListView listview;
    static int pos;
    static AdapterView<?> par;
    String URI, URI_W;
    String searchResult = "";
    private String user;
    private Integer SKU;
    private float price;
    static boolean updatedCart = false;
    static final String TAG = "Search text:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);
        //actionBar = getActionBar();
        // Hide the action bar title
        //actionBar.setDisplayShowTitleEnabled(false);
        if(products.isEmpty()) {
            new returnAllProducts().execute();
        }
        listview = (ListView) findViewById(R.id.list_view);
        adaptProducts = new ProductsAdapter(Search.this, 0, products);
        for(int i=0; i<products.size(); i++) {
            View rowView = listview.getChildAt(i);
            if (rowView != null) {
                try {
                    String w_folder = "http://www.michaelscray.com/Softwear/graphics/wearables/";
                    ImageView wearableImage = (ImageView) rowView.findViewById(R.id.icon);
                    String wearable = w_folder + wearableImage.toString();
                    String png = wearable.substring(0, URI_W.lastIndexOf('.')) + ".png";
                    Log.d(TAG, "URL: " + png);
                    boolean check = new checkFileExists().execute(png).get().booleanValue();
                    Log.d(TAG, "Check value: " + check);
                    if (check) {
                        rowView.setBackgroundColor(getResources().getColor(R.color.tan));
                        adaptProducts.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
                    URI_W = "http://www.michaelscray.com/Softwear/graphics/wearables/";

                    String dept = "Dept: ";
                    String money = "$";
                    String qty = "Qty: ";

                    //final Integer tempSKU = products.get(position).getSKU();
                    //final float tempPrice = products.get(position).getPrice();

                    URI += products.get(position).getProduct_img();
                    URI_W += products.get(position).getProduct_img();
                    Uri uris = Uri.parse(URI + products.get(position).getProduct_img());
                    URI uri = java.net.URI.create(URI);
                    holder.product_name.setText(products.get(position).getProduct_name());
                    holder.product_desc.setText(products.get(position).getProduct_desc());
                    holder.product_dept.setText(dept + products.get(position).getProduct_dept());
                    holder.product_price.setText(money + String.valueOf(products.get(position).getPrice()));
                    holder.product_qty.setText(qty + String.valueOf(products.get(position).getProduct_qty()));
                    Picasso.with(getApplicationContext()).load(URI).error(R.mipmap.ic_launcher).into(holder.product_img);
                    String test = URI_W.substring(0, URI_W.lastIndexOf('.')) + ".png";
                    //new checkFileExists().execute(URI);
                    //file1 = getTruth();
                    Log.d(TAG, "JPG: " + URI);
                    Log.d(TAG, "PNG: " + test);
                    //Example: http://www.michaelscray.com/Softwear/graphics/wearables/sampleTwo.png
                    //new checkFileExists().execute(URI);
                    boolean truth = new checkFileExists().execute(test).get().booleanValue();
                    Log.d(TAG, "Product wearable: " + truth);
                    if (truth) {
                        Button tryMe = (Button) findViewById(R.id.try_btn);
                        tryMe.setVisibility(View.VISIBLE);
                        tryMe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String png = URI_W.substring(0, URI_W.lastIndexOf('.')) + ".png";
                                Intent intent = new Intent(Search.this, CameraActivity.class);
                                intent.putExtra("Image", png);
                                //Log.d(TAG, "wearable: " + URI);
                                startActivity(intent);
                            }
                        });
                    }

                    Button addToCart = (Button) findViewById(R.id.cart_btn);
                    setSKU(products.get(position).getSKU());
                    setPrice(products.get(position).getPrice());
                    addToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*  Get User_Name, SKU, Price, and Shipped   */
                            user = MainActivity.currentAccount.getUsername();
                            SKU = getSKU();
                            //String desc = products.get(getPos()).getProduct_desc();
                            //String dept = products.get(getPos()).getProduct_dept();
                            price = getPrice();
                            //int qty = products.get(getPos()).getProduct_qty();
                            //String img = products.get(getPos()).getProduct_img();
                            setUser(user);
                            setSKU(SKU);
                            setPrice(price);
                            updatedCart = true;
                            //cartChangeListener.onCartChanged();
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
        Log.d(TAG, "ListView length: " + listview.getChildCount());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s != null || s != "") {
                    setSearchResult(s);
                    doStuff(s);
                    Log.d(TAG, "onQueryTextSubmit: " + s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s != null || s != "") {
                    /*
                    int position = 0;
                    while (position < products.size() - 1) {
                        if (products.get(position).getProduct_name().toLowerCase().contains(s.toLowerCase()) ||
                                products.get(position).getProduct_desc().toLowerCase().contains(s.toLowerCase()) ||
                                products.get(position).getProduct_dept().toLowerCase().contains(s.toLowerCase())) {
                            listview.smoothScrollToPositionFromTop(position, 0, 200);
                            break;
                        } else {
                            position++;
                        }
                    } */
                    Log.d(TAG, "onQueryTextChange: " + s);
                    setSearchResult(s);
                    doStuff(s);
                }
                return false;
            }

        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("CLICK", "CLICK");

            }
        });

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                item.setVisible(false);
                return true;
            }
        });


        //searchView.setOnCloseListener();
        return super.onCreateOptionsMenu(menu);
    }

    public void doStuff(String result) {
        ArrayList<Product> currProducts = MainActivity.getProducts();
        final ArrayList<Product> searchProducts = new ArrayList<>();
        listview = (ListView) findViewById(R.id.list_view);
        Product tempProduct;
        for (int i = 0; i < currProducts.size(); i++) {
            if (currProducts.get(i).getProduct_name().toLowerCase().contains(result.toLowerCase()) ||
                    currProducts.get(i).getProduct_desc().toLowerCase().contains(result.toLowerCase()) ||
                    currProducts.get(i).getProduct_dept().toLowerCase().contains(result.toLowerCase())) {

                Log.d(TAG, "Product list contains: " + result + " at position: " + i);
                setTitle("Search Result");
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

        adaptProducts = new ProductsAdapter(Search.this, 0, searchProducts);
        if(listview != null) {
            listview.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    try {
                        //int positions = (Integer) v.getTag();
                        Product productSearch = (Product)parent.getItemAtPosition(position);
                        setContentView(R.layout.activity_product_view);
                        try {
                            getActionBar().setDisplayShowHomeEnabled(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, "Item: " + productSearch.getProduct_name());
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
                        URI += productSearch.getProduct_img();
                        Uri uris = Uri.parse(URI + productSearch.getProduct_img());
                        URI uri = java.net.URI.create(URI);
                        holder.product_name.setText(productSearch.getProduct_name());
                        holder.product_desc.setText(productSearch.getProduct_desc());
                        holder.product_dept.setText(dept + productSearch.getProduct_dept());
                        holder.product_price.setText(money + String.valueOf(productSearch.getPrice()));
                        holder.product_qty.setText(qty + String.valueOf(productSearch.getProduct_qty()));
                        Picasso.with(getApplicationContext()).load(URI).error(R.mipmap.ic_launcher).into(holder.product_img);
                        setSKU(productSearch.getSKU());
                        setPrice(productSearch.getPrice());
                        Button addToCart = (Button) findViewById(R.id.cart_btn);
                        addToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*  Get User_Name, SKU, Price, and Shipped   */
                                user = MainActivity.currentAccount.getUsername();
                                SKU = getSKU();
                                price = getPrice();
                                setUser(user);
                                setSKU(SKU);
                                setPrice(price);
                                updatedCart = true;
                                new addItemToCart().execute();
                                Log.d(TAG, user + ", added item: " + String.valueOf(SKU) + ", for $" + String.valueOf(price));
                                /*
                                View parentRow = (View) v.getParent();
                                ListView listViewItem = (ListView) parentRow.getParent();
                                final int position = listViewItem.getPositionForView(parentRow);

                                int position=(Integer)v.getTag();
                                /*  Get User_Name, SKU, Price, and Shipped
                                user = MainActivity.currentAccount.getUsername();
                                SKU = products.get(position).getSKU();
                                //String desc = products.get(position).getProduct_desc();
                                //String dept = products.get(position).getProduct_dept();
                                price = products.get(position).getPrice();
                                //int qty = products.get(position).getProduct_qty();
                                //String img = products.get(position).getProduct_img();
                                setUser(user);
                                setSKU(SKU);
                                setPrice(price);
                                Log.d(TAG, user + ", added item: " + String.valueOf(SKU) + ", for $" + String.valueOf(price));
                                updatedCart = true;
                                new addItemToCart().execute();
                                */
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        if(listview != null) {
            adaptProducts.notifyDataSetChanged();
            listview.setAdapter(adaptProducts);
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

    private class checkFileExists extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =  (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
                System.out.println(con.getResponseCode());
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            //setTruth(result);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setSearchResult(String searchResult) {
        this.searchResult = searchResult;
    }

    public String getSearchResult() {
        return searchResult;
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
