package com.android.softwear;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.softwear.models.Account;
import com.android.softwear.process.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Michael on 10/27/2015.
 */
public class AccountActivity extends AppCompatActivity {

    //private TextView getData;
    private Account user;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        user = MainActivity.currentAccount;
        EditText firstName = (EditText) findViewById(R.id.editText_firstName);
        EditText lastName = (EditText) findViewById(R.id.editText_lastName);
        EditText userEmail = (EditText) findViewById(R.id.editText_email);
        EditText userPass = (EditText) findViewById(R.id.editText_password);
        firstName.setText(user.getFirst_name());
        lastName.setText(user.getLast_name());
        userEmail.setText(user.getEmail());
        userPass.setText(user.getPassword());
        Button update = (Button) findViewById(R.id.updateAccount);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText user_First, user_Last, user_Email, user_Name, user_Pass;
                String first, last, email, password, username;
                user_First = (EditText) findViewById(R.id.editText_firstName);
                user_Last = (EditText) findViewById(R.id.editText_lastName);
                user_Email = (EditText) findViewById(R.id.editText_email);
                user_Pass = (EditText) findViewById(R.id.editText_password);
                first = user_First.getText().toString();
                last = user_Last.getText().toString();
                email = user_Email.getText().toString();
                password = user_Pass.getText().toString();
                MainActivity.currentAccount.setFirst_name(first);
                MainActivity.currentAccount.setLast_name(last);
                MainActivity.currentAccount.setEmail(email);
                MainActivity.currentAccount.setPassword(password);
                new userUpdate().execute();
            }
        });

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

    public class getCartIcon extends AsyncTask<Void, Void, Void> {
        String tempUser = MainActivity.currentAccount.getUsername();
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

    public void unlockAccount(View view) {
        EditText first = (EditText) findViewById(R.id.editText_firstName);
        EditText last = (EditText) findViewById(R.id.editText_lastName);
        EditText email = (EditText) findViewById(R.id.editText_email);
        EditText pass = (EditText) findViewById(R.id.editText_password);
        first.setEnabled(true);
        last.setEnabled(true);
        email.setEnabled(true);
        pass.setEnabled(true);
    }

    public class userUpdate extends AsyncTask<Void, Void, Void> {
        String queryResult = "";

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Connection conn = ConnectDB.getConnection();
                if(conn != null) {
                    queryResult = "Successfully connected!";
                    Update.update(MainActivity.currentAccount, conn);
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


}
