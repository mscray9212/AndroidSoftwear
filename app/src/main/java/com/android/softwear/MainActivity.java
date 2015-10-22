package com.android.softwear;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.softwear.models.Account;
import com.android.softwear.models.Product;
import com.android.softwear.process.ProductAdapter;
import com.android.softwear.process.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //ProductQuery products = new ProductQuery();
    static Account currentAccount = new Account();
    static String searchString = "";
    private TextView getData;
    ListAdapter adapter;
    private String user;
    Menu menu;
    //int cartItems =

    //public static Connection getConnect = ConnectDB.getConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //final ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.show();
        getData = (TextView) findViewById(R.id.textView_hello);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        if(currentAccount != null) {
            new getCartIcon().execute();
        }

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
            startActivity(new Intent(getApplicationContext(), Cart.class));
        }

        if (id == R.id.action_search) {
            startActivity(new Intent(getApplicationContext(), Search.class));
        }
        /*
        if (id == R.id.action_search) {
            setContentView(R.layout.activity_test);
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

        if (currentAccount != null && id == R.id.action_logout) {
            currentAccount.setFirst_name("Guest");
            currentAccount.setLast_name("");
            currentAccount.setEmail("");
            currentAccount.setPassword("");
            currentAccount.setUsername("");
            setContentView(R.layout.activity_main);
            setUser(currentAccount);
        }

        return super.onOptionsItemSelected(item);
    }
    /*
    public void userLog(View view) {
        EditText user_Name, user_Pass;
        String password, username;
        user_Name = (EditText) findViewById(R.id.editText_username);
        user_Pass = (EditText) findViewById(R.id.editText_password);
        password = user_Pass.getText().toString();
        username = user_Name.getText().toString();
        account.setPassword(password);
        account.setUsername(username);
        Login.login(account, getConnect);
        setUser(account);

    }
    */
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
            super.onPostExecute(result);
        }
    }

    public class userSearch extends AsyncTask<Void, Void, Void> {
        String queryResult = "";


        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Connection conn = ConnectDB.getConnection();
                if(conn != null) {
                    queryResult = "Successfully connected!";
                    /*
                    List<Product> products;

                    products = SearchQuery.searchProducts(searchString, conn);
                    for (Product product: products) {
                        //displayProducts();
                    }
                    */
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
            super.onPostExecute(result);
        }
    }

    public void userReg(View view) {
        setContentView(R.layout.activity_register);
    }

    //class returnProducts extends AsyncTask<Void, Void, ArrayList<Product>> {
    class returnProducts extends AsyncTask<Void, Void, Void> {
        ArrayList<Product> products = new ArrayList<>();
        String queryResult = "";

        //protected ArrayList<Product> doInBackground(Void... arg0) {
        protected Void doInBackground(Void... arg0) {

            try {
                queryResult = "Database connection success\n";
                Connection conn = ConnectDB.getConnection();
                String queryString = "SELECT * FROM inventory";

                Statement st = conn.createStatement();
                final ResultSet result = st.executeQuery(queryString);

                while(result.next()) {
                    queryResult +=
                            "Product: " + result.getString("Name") + "\n" +
                                    "Description: " + result.getString("Description") + "\n" +
                                    "Price: $" + result.getFloat("Price") + "\n\n";
                }
                conn.close();
            }

            catch(Exception e) {
                e.printStackTrace();
                queryResult = "Database connection failure!\n" + e.toString();
            }
            return null;

            /*
            products = ProductQuery.returnProducts();
            return products;
            */
        }

        protected void onPostExecute(Void result) {
            getData.setText(queryResult);
            /*
            ProductAdapter adbProduct;
            adbProduct = new ProductAdapter(MainActivity.this, 0, products);
            ListView listview = (ListView) findViewById(R.id.lvShopItems);
            listview.setAdapter(adbProduct);
            */
        }

    }

    class returnProductView extends AsyncTask<Void, Void, ArrayList<Product>> {
        ArrayList<Product> products = new ArrayList<>();
        String queryResult = "";
        String search = returnSearchString();

        protected ArrayList<Product> doInBackground(Void... arg0) {
        //protected Void doInBackground(Void... arg0) {

            try {
                queryResult = "Database connection success\n";
                Connection conn = ConnectDB.getConnection();
                String queryString = "SELECT * FROM inventory WHERE Name Like ? OR Department LIKE ? OR Description Like ?";

                PreparedStatement st = conn.prepareStatement(queryString);
                st.setString(1, "%" + search + "%");
                st.setString(2, "%" + search + "%");
                st.setString(3, "%" + search + "%");
                final ResultSet result = st.executeQuery(queryString);

                while(result.next()) {
                    queryResult +=
                            "Product: " + result.getString("Name") + "\n" +
                                    "Description: " + result.getString("Description") + "\n" +
                                    "Price: $" + result.getFloat("Price") + "\n\n";
                }
                conn.close();
            }

            catch(Exception e) {
                e.printStackTrace();
                queryResult = "Database connection failure!\n" + e.toString();
            }
            //result = ProductQuery.returnProducts(result);
            return null;
            //products = ProductQuery.returnProducts();
            //return products;

        }

        protected void onPostExecute(Void result) {
            getData.setText(queryResult);

            ProductAdapter adbProduct;
            //adbProduct = new ProductAdapter(MainActivity.this, 0, products);
            //ListView listview = (ListView) findViewById(R.id.lvShopItems);
            //listview.setAdapter(adbProduct);

        }

    }

    public String returnSearchString() {
        EditText inputSearch;
        String search = "";
        //inputSearch = (EditText) findViewById(R.id.editText_search);
        //search = inputSearch.getText().toString();
        return search;
    }
    /*
            ArrayAdapter<Product> arrayAdapter = new ArrayAdapter<Product>(
                this,
                android.R.layout.simple_list_item_1,
                products);
        ListView lv = (ListView) findViewById(R.id.lvShopItems);
        lv.setAdapter(arrayAdapter);
        /*
        adapter = new ArrayAdapter<Integer, Double>(this, R.layout.activity_search, R.id.)
        product.getSKU();
        product.getProduct_name();
        product.getProduct_dept();
        product.getPrice();
        product.getProduct_desc();
        product.getProduct_img();
        product.getProduct_qty();
        return product;
        */
    /*
    public void userRegister(View view) {
        EditText first_Name, last_Name, user_Email, user_Name, user_Pass;
        String fName, lName, email, username, password;

        first_Name = (EditText)findViewById(R.id.editText_firstName);
        last_Name = (EditText)findViewById(R.id.editText_lastName);
        user_Email = (EditText)findViewById(R.id.editText_email);
        user_Name = (EditText)findViewById(R.id.editText_username);
        user_Pass = (EditText)findViewById(R.id.editText_password);

        fName = first_Name.getText().toString();
        lName = last_Name.getText().toString();
        email = user_Email.getText().toString();
        password = user_Pass.getText().toString();
        username = user_Name.getText().toString();
        Account account = new Account();
        account.setFirst_name(fName);
        account.setLast_name(lName);
        account.setEmail(email);
        account.setPassword(password);
        account.setUsername(username);
        Register.register(account);
        setUser(account);
    }*/

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


}
