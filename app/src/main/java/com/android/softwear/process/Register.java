package com.android.softwear.process;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.softwear.MainActivity;
import com.android.softwear.R;
import com.android.softwear.models.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by Michael on 9/23/2015.
 */
public class Register extends Activity {

    public static Account register(Account acct, Connection conn) {

        int status = 0;

        try {

            PreparedStatement ps = conn.prepareStatement("INSERT INTO `Customers`(`First_Name`, `Last_Name`, `Email`, `Password`, `User_Name`, `Admin`) VALUES (?,?,?,?,?,?)");
            ps.setString(1, acct.getFirst_name());
            ps.setString(2, acct.getLast_name());
            ps.setString(3, acct.getEmail());
            ps.setString(4, acct.getPassword());
            ps.setString(5, acct.getUsername());
            ps.setInt(6, 0);
            /*
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `Customers`" +
                    "(`First_Name`='"+acct.getFirst_name()+"', " +
                    "`Last_Name`='"+acct.getLast_name()+"', " +
                    "`Email`='"+acct.getEmail()+"', " +
                    "`Password`='"+acct.getPassword()+"', " +
                    "`User_Name`='"+acct.getUsername()+"', " +
                    "`Admin`='"+0+"')"); */
            status = ps.executeUpdate();
        }

        catch(Exception e){
            e.printStackTrace();
        }

        return acct;
    }

}
