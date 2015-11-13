package com.android.softwear;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.softwear.models.Account;
import com.android.softwear.process.ConnectDB;
import com.android.softwear.process.ProductsAdapter;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Michael on 11/7/2015.
 */
public class ProductActivity extends AppCompatActivity {
    String user = MainActivity.currentAccount.getUsername();
    Integer cartNum = MainActivity.cartNum;
    static int cartSize;
    MenuItem cartMenuItem;
    Menu menu;
    Boolean truth;
    String TAG = "";
    private Integer SKU;
    private float price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        new getCartIcon().execute();

        //vi = inflater.inflate(R.layout.activity_product_view, parent, false);
        ProductsAdapter.ViewHolder holder = new ProductsAdapter.ViewHolder();
        holder.product_name = (TextView) findViewById(R.id.product_name);
        holder.product_dept = (TextView) findViewById(R.id.product_dept);
        holder.product_desc = (TextView) findViewById(R.id.product_desc);
        holder.product_price = (TextView) findViewById(R.id.product_price);
        holder.product_qty = (TextView) findViewById(R.id.product_qty);
        holder.product_img = (ImageView) findViewById(R.id.icon);

        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String dept = "Dept: ";
            String money = "$";
            String qty = "Qty: ";
            /*
            productIntent.putExtra("URI", URI);
            productIntent.putExtra("URI_W", URI_W);
            productIntent.putExtra("Name", products.get(position).getProduct_name());
            productIntent.putExtra("Desc", products.get(position).getProduct_desc());
            productIntent.putExtra("Dept", products.get(position).getProduct_dept());
            productIntent.putExtra("Price", products.get(position).getPrice());
            productIntent.putExtra("Qty", products.get(position).getProduct_qty());
             */
            holder.product_name.setText(extras.getString("Name"));
            holder.product_desc.setText(extras.getString("Desc"));
            holder.product_dept.setText(dept + extras.getString("Dept"));
            holder.product_price.setText(money + String.valueOf(extras.getFloat("Price")));
            holder.product_qty.setText(qty + String.valueOf(extras.getInt("Qty")));
            Picasso.with(getApplicationContext()).load(extras.getString("URI")).error(R.mipmap.ic_launcher).into(holder.product_img);
            truth = extras.getBoolean("truth");
            setSKU(extras.getInt("SKU"));

                if (truth) {
                    Button tryMe = (Button) findViewById(R.id.try_btn);
                    tryMe.setVisibility(View.VISIBLE);
                    tryMe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String png = extras.getString("PNG");
                            Intent intent = new Intent(ProductActivity.this, CameraActivity.class);
                            intent.putExtra("Image", png);
                            //Log.d(TAG, "wearable: " + URI);
                            startActivity(intent);
                        }
                    });
                }

                Button addToCart = (Button) findViewById(R.id.cart_btn);
                addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            /*  Get User_Name, SKU, Price, and Shipped   */
                        user = MainActivity.currentAccount.getUsername();
                        Integer SKU = getSKU();
                        //String desc = products.get(getPos()).getProduct_desc();
                        //String dept = products.get(getPos()).getProduct_dept();
                        Float price = getPrice();
                        //int qty = products.get(getPos()).getProduct_qty();
                        //String img = products.get(getPos()).getProduct_img();
                        setUser(user);
                        setSKU(SKU);
                        setPrice(price);
                        new addItemToCart().execute();
                    }
                });

            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        int items = getCartNumber();
        cartMenuItem = menu.findItem(R.id.action_cart);
        getCartItems(items, cartMenuItem);
        Log.d(TAG, "getCartNumber() = " + items);
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
            if(MainActivity.loggedIn) {
                update();
            }
            startActivity(new Intent(getApplicationContext(), Cart.class));
        }

        if (id == R.id.action_search) {
            if(MainActivity.loggedIn) {
                update();
            }
            startActivity(new Intent(getApplicationContext(), Search.class));
        }

        if (id == R.id.action_account) {
            if(MainActivity.loggedIn) {
                update();
            }
            startActivity(new Intent(getApplicationContext(), AccountActivity.class));
        }

        if (id == R.id.action_about_us) {
            if(MainActivity.loggedIn) {
                update();
            }
            setContentView(R.layout.activity_about_us);
        }

        if (MainActivity.currentAccount != null && id == R.id.action_logout) {
            MainActivity.currentAccount.setFirst_name("Guest");
            MainActivity.currentAccount.setLast_name("");
            MainActivity.currentAccount.setEmail("");
            MainActivity.currentAccount.setPassword("");
            MainActivity.currentAccount.setUsername("Guest");
            setContentView(R.layout.activity_main);
            setUser(MainActivity.currentAccount);
            MainActivity.setLog(false);
            new getCartIcon().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    public void update() {
        Log.d(TAG, "Logged in: " + MainActivity.loggedIn);
        new getCartIcon().execute();
    }

    public void getCartItems(int cart, MenuItem cartMenuItem) {

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
        String tempUser = MainActivity.currentAccount.getUsername();

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
            if(cartNum > 0) {
                getCartItems(cartNum, cartMenuItem);
            }
            super.onPostExecute(result);
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
                pDialog = new ProgressDialog(ProductActivity.this);
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
                Toast.makeText(ProductActivity.this, "Added item: " + tempSKU + " to your cart!", Toast.LENGTH_SHORT).show();
                new getCartIcon().execute();
            }
            else {
                Toast.makeText(ProductActivity.this, "You forgot to login, didn't you?", Toast.LENGTH_SHORT).show();
            }
            //bench cooler
            //getData.setText(queryResult);
            //super.onPostExecute(result);
        }

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
            MainActivity.setLog(true);
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
            MainActivity.setLog(true);
            super.onPostExecute(result);
        }
    }

    public void setUser(Account acct) {
        String aloha;
        if(acct.getFirst_name() != null && acct.getFirst_name() != "Guest") {
            aloha = "Welcome, " + acct.getFirst_name() + "!";
            setContentView(R.layout.activity_user);
            TextView textName = (TextView) findViewById(R.id.textView_hello);
            textName.setTextSize(25);
            textName.setText(aloha);
            setTitle(acct.getFirst_name());

            if(Search.cartStatus()) {
                new getCartIcon().execute();
            }
            Button register = (Button)findViewById(R.id.registerButton);

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            });
            Button login = (Button)findViewById(R.id.loginButton);

            login.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
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

            });
            Button search = (Button)findViewById(R.id.searchButton);

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductActivity.this, Search.class));

                }
            });
            Button account = (Button)findViewById(R.id.accountButton);

            account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductActivity.this, AccountActivity.class));
                }
            });
            Button contact = (Button)findViewById(R.id.contactButton);

            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.activity_about_us);
                }
            });

        }
        else {
       /* If want to have logout go to login screen
       setContentView(R.layout.activity_login);
        setTitle("Hello, Guest");
        */
            aloha = "Welcome, Guest!";
            setContentView(R.layout.activity_user);
            TextView textName = (TextView) findViewById(R.id.textView_hello);
            textName.setTextSize(25);
            textName.setText(aloha);
            setTitle("Hello, Guest");

        }
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

    public static Integer getCartNumber() {
        return cartSize;
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
}
