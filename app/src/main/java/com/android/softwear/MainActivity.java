package com.android.softwear;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.softwear.models.Account;
import com.android.softwear.models.Product;
import com.android.softwear.process.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //ProductQuery products = new ProductQuery();
    static ArrayList<Product> products = new ArrayList<>();

    public static Account currentAccount = new Account();
    static Integer cartSize = 0;
    static Integer size = 0;
    private TextView getData;
    private String user;
    static int cartNum;
    static boolean loggedIn = false;
    String TAG = "";
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new retrieveAllProducts().execute();
        new getCartCount().execute();
        Log.d(TAG, "Logged in: " + loggedIn);
        setContentView(R.layout.activity_main);
        //final ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.show();
        getData = (TextView) findViewById(R.id.textView_hello);
        if(Search.cartStatus()) {
            new getCartIcon().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public  void onResume() {
        super.onResume();
        if(loggedIn) {
            update();
        }
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
                    currentAccount.setPassword(password);
                    currentAccount.setUsername(username);
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
                    currentAccount.setFirst_name(first);
                    currentAccount.setLast_name(last);
                    currentAccount.setEmail(email);
                    currentAccount.setPassword(password);
                    currentAccount.setUsername(username);
                    new userReg().execute();
                }
            });
        }

        if (id == R.id.action_cart) {
            if(loggedIn) {
                update();
            }
            startActivity(new Intent(getApplicationContext(), Cart.class));
        }

        if (id == R.id.action_search) {
            if(loggedIn) {
                update();
            }
            startActivity(new Intent(getApplicationContext(), Search.class));
        }

        if (id == R.id.action_account) {
            if(loggedIn) {
                update();
            }
            startActivity(new Intent(getApplicationContext(), AccountActivity.class));
        }

        if (id == R.id.action_about_us) {
            if(loggedIn) {
                update();
            }
            setContentView(R.layout.activity_about_us);
        }

        if (currentAccount != null && id == R.id.action_logout) {
            currentAccount.setFirst_name("Guest");
            currentAccount.setLast_name("");
            currentAccount.setEmail("");
            currentAccount.setPassword("");
            currentAccount.setUsername("Guest");
            setContentView(R.layout.activity_main);
            setUser(currentAccount);
            setLog(false);
            new getCartIcon().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    public void update() {
        Log.d(TAG, "Logged in: " + loggedIn);
        new getCartIcon().execute();
    }

    public class userLog extends AsyncTask<Void, Void, Void> {
        String queryResult = "";

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Connection conn = ConnectDB.getConnection();
                if(conn != null) {
                    queryResult = "Successfully connected!";
                    Login.login(currentAccount, conn);
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
            setUser(currentAccount);
            getData.setText(queryResult);
            setLog(true);
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
                        Register.register(currentAccount, conn);
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
            setUser(currentAccount);
            getData.setText(queryResult);
            setLog(true);
            super.onPostExecute(result);
        }
    }

    public void userReg(View view) {
        setContentView(R.layout.activity_register);
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

    public class getCartIcon extends AsyncTask<Void, Void, Void> {
        String tempUser = currentAccount.getUsername();

        @Override
        protected Void doInBackground(Void... arg0) {
            cartNum = 0;
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



    public Account getAccount() {
        return currentAccount;
    }

    private class retrieveAllProducts extends AsyncTask<Void, Void, Void> {

        String queryResult = "";
        Product product = null;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
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

    public class getCartCount extends AsyncTask<Void, Void, Void> {
        String tempUser = currentAccount.getUsername();
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

    public static void setCartSize(Integer size) {
        MainActivity.size = size;
    }

    public static ArrayList<Product> getProducts() {
        return products;
    }

    public static Integer getCartNumber() {
        return cartSize;
    }

    public static Integer getCartSize() {
        return size;
    }

    public static void setLog(boolean loggedIn) {
        MainActivity.loggedIn = loggedIn;
    }

    public static Boolean getLog() {
        return loggedIn;
    }


}
