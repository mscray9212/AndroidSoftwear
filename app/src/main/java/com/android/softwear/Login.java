package com.android.softwear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.softwear.MainActivity;
import com.android.softwear.R;
import com.android.softwear.models.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Michael on 9/23/2015.
 */
public class Login extends Activity {

    static int status = 0;

    public static Account login(Account acct, Connection conn) {

        Statement authenticate = null;

        try {
            /*
            authenticate = conn.prepareStatement("SELECT * `Customers`(`User_Name`, `Password`) VALUES (?,?)");
            authenticate.setString(1, acct.getUsername());
            authenticate.setString(2, acct.getPassword());
            status = authenticate.executeUpdate();
            */
            authenticate = conn.createStatement();
            String sql = "SELECT * FROM `Customers` WHERE `User_Name` = '"+acct.getUsername()+
                    "' AND `Password` = '"+acct.getPassword()+"';";
            ResultSet rs = authenticate.executeQuery(sql);
            while(rs.next()) {
                acct.setFirst_name(rs.getString("First_Name"));
                acct.setLast_name(rs.getString("Last_Name"));
                acct.setEmail(rs.getString("Email"));
                acct.setPassword(rs.getString("Password"));
                acct.setUsername(rs.getString("User_Name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            if (authenticate != null) {
                try {
                    authenticate.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        }

        return acct;
    }

}