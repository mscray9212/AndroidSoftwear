package com.android.softwear;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.softwear.interfaces.CartChangeListener;
import com.android.softwear.models.Account;
import com.android.softwear.models.Product;
import com.android.softwear.process.ConnectDB;
import com.android.softwear.process.ProductAdapter;
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
    static final String TAG = "";
    ProductAdapter adaptCart;
    static ArrayList<Product> cartItems = new ArrayList<>();
    Button remove_btn;
    ListView listView;
    Menu menu;
    float total = (float)0.00;
    float shipCost = 15;
    float tax = (float)0.07;
    float taxRate = (float) 0.07;
    String URI;
    TextView subTotal;
    TextView shipping;
    TextView taxes;
    TextView cTaxes;
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
            new returnCartItems().execute();
            ArrayList<Product> tempItems = new ArrayList<>();
            tempItems = getCartItems();
            Log.d(TAG, "cartItems size: " + String.valueOf(tempItems.size()));
            listView = (ListView) findViewById(R.id.list_cart_view);
            adaptCart = new ProductAdapter(Cart.this, 0, tempItems);

            /*
            //if (!(cartItems.isEmpty())) {
                adaptCart = new ProductAdapter(Cart.this, 0, cartItems);

                        for(int i=0; i<cartItems.size(); i++) {
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
                            URI += cartItems.get(i).getProduct_img();
                            Uri uris = Uri.parse(URI + cartItems.get(i).getProduct_img());
                            URI uri = java.net.URI.create(URI);
                            holder.product_name.setText(cartItems.get(i).getProduct_name());
                            holder.product_desc.setText(cartItems.get(i).getProduct_desc());
                            holder.product_dept.setText(dept + cartItems.get(i).getProduct_dept());
                            holder.product_price.setText(money + String.valueOf(cartItems.get(i).getPrice()));
                            holder.product_qty.setText(qty + String.valueOf(cartItems.get(i).getProduct_qty()));
                            Picasso.with(getApplicationContext()).load(URI).error(R.mipmap.ic_launcher).into(holder.product_img);

                            Button removeItem = (Button) findViewById(R.id.remove_btn);
                            removeItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                            /*  Get User_Name, SKU, Price, and Shipped   */
                                    /*
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
                                    new removeFromCart().execute();

                                }
                            });

                        }
                */
                listView.setAdapter(adaptCart);
            }
        //}
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login){
            setContentView(R.layout.activity_login);
            Button logB = (Button) findViewById(R.id.logB);
            logB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText user_Name, user_Pass;
                    String password, username;
                    user_Name = (EditText) findViewById(R.id.editText_username);
                    user_Pass = (EditText) findViewById(R.id.editText_password);
                    password = user_Pass.getText().toString();
                    username = user_Name.getText().toString();
                    MainActivity.currentAccount.setPassword(password);
                    MainActivity.currentAccount.setUsername(username);
                    new userLog().execute();
                }
            });
        }
        if (id == R.id.action_register) {
            setContentView(R.layout.activity_register);
            Button logB = (Button) findViewById(R.id.logR);
            logB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText user_First, user_Last, user_Email, user_Name, user_Pass;
                    String first, last, email, password, username;
                    user_First = (EditText) findViewById(R.id.editText_firstName);
                    user_Last = (EditText) findViewById(R.id.editText_lastName);
                    user_Email = (EditText) findViewById(R.id.editText_email);
                    user_Name = (EditText) findViewById(R.id.editText_username);
                    user_Pass = (EditText) findViewById(R.id.editText_password);
                    first = user_First.getText().toString();
                    last = user_Last.getText().toString();
                    email = user_Email.getText().toString();
                    password = user_Pass.getText().toString();
                    username = user_Name.getText().toString();
                    MainActivity.currentAccount.setFirst_name(first);
                    MainActivity.currentAccount.setLast_name(last);
                    MainActivity.currentAccount.setEmail(email);
                    MainActivity.currentAccount.setPassword(password);
                    MainActivity.currentAccount.setUsername(username);
                    new userReg().execute();
                }
            });
        }

        if (id == R.id.action_cart) {
            new getCartIcon().execute();
            startActivity(new Intent(getApplicationContext(), Cart.class));
        }

        if (id == R.id.action_search) {
            new getCartIcon().execute();
            startActivity(new Intent(getApplicationContext(), Search.class));
        }

        if (id == R.id.action_account) {
            new getCartIcon().execute();
            startActivity(new Intent(getApplicationContext(), AccountActivity.class));
        }

        if (id == R.id.action_about_us) {
            setContentView(R.layout.activity_about_us);
        }
        /*
        if (id == R.id.action_search) {
            setContentView(R.layout.activity_search;
            getData = (TextView) findViewById(R.id.textView_test);
            //Button search_btn = (Button) findViewById(R.id.search_btn);
            new returnProducts().execute();
            /*
            search_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new returnSearchProduct().execute();
                }
            });
        }*/

        if (MainActivity.currentAccount != null && id == R.id.action_logout) {
            MainActivity.currentAccount.setFirst_name("Guest");
            MainActivity.currentAccount.setLast_name("");
            MainActivity.currentAccount.setEmail("");
            MainActivity.currentAccount.setPassword("");
            MainActivity.currentAccount.setUsername("Guest");
            setContentView(R.layout.activity_main);
            setUser(MainActivity.currentAccount);
            new getCartIcon().execute();
        }

        return super.onOptionsItemSelected(item);
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
            cTaxes = (TextView) findViewById(R.id.current_taxes);

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
            String taxation = "Taxes @ %" + String.valueOf(taxRate);
            getCartItems(cartNum);
            subTotal.setText(String.format("%.2f", Float.parseFloat(String.valueOf(tempTotal))));
            if(tempTotal == 0) {
                shipCost = 0;
            }
            shipping.setText(String.format("%.2f", Float.parseFloat(String.valueOf(shipCost))));
            tax = tempTotal * tax;
            cTaxes.setText(taxation);
            taxes.setText(String.format("%.2f", Float.parseFloat(String.valueOf(tax))));
            totals.setText(String.format("%.2f", Float.parseFloat(String.valueOf(tempTotal + shipCost + tax))));
            //new returnCartItems().execute();
            //setTotal(tempTotal);
            //super.onPostExecute(result);
        }
    }

    public class returnCartItems extends AsyncTask<Void, Void, Void> {
        String tempUser = user;
        Product product = null;
        List<Integer> skus = new ArrayList<>();

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
                                product.setSKU(result.getInt("SKU"));
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
            setCartItems(cartItems);
            super.onPostExecute(result);
        }
    }

    public void setCartItems(ArrayList<Product> cartItems) {
        this.cartItems = cartItems;
    }

    public ArrayList<Product> getCartItems() {
        return cartItems;
    }

    public class userLog extends AsyncTask<Void, Void, Void> {
        String queryResult = "";

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Connection conn = ConnectDB.getConnection();
                if(conn != null) {
                    queryResult = "Successfully connected!";
                    Login.login(MainActivity.currentAccount, conn);
                }
                else {
                    queryResult = "Error in connection";
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            setUser(MainActivity.currentAccount);
            //getData.setText(queryResult);
            new getCartIcon().execute();
            super.onPostExecute(result);
        }
    }

    public class userReg extends AsyncTask<Void, Void, Void> {
        String queryResult = "";

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Connection conn = ConnectDB.getConnection();
                if(conn != null) {
                    queryResult = "Successfully connected!";
                    Register.register(MainActivity.currentAccount, conn);
                    conn.close();
                }
                else {
                    queryResult = "Error in connection";
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            setUser(MainActivity.currentAccount);
            //getData.setText(queryResult);
            super.onPostExecute(result);
        }
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

    public void setUser(Account acct) {
        String aloha;
        if(acct.getFirst_name() != null) {
            aloha = "Welcome, " + acct.getFirst_name() + "!";
        }
        else {
            aloha = "Welcome, Guest!";
        }
        setContentView(R.layout.activity_main);
        TextView textName = (TextView) findViewById(R.id.textView_hello);
        textName.setTextSize(22);
        textName.setText(aloha);
    }
}
