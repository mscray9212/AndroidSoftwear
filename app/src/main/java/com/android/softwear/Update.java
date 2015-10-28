package com.android.softwear;

import android.app.Activity;
import android.util.Log;

import com.android.softwear.models.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by Michael on 10/27/2015.
 */
public class Update extends Activity {

    public static Account update(Account acct, Connection conn) {

        String statement = "";

        try {
            //UPDATE `Customers` SET `First_Name`='Michael',`Last_Name`='Scary',`Email`='mscray9212@gmail.com',`Password`='verify12' WHERE `User_Name`='mscray1'
            PreparedStatement ps = conn.prepareStatement("UPDATE `Customers` SET `First_Name`='" + acct.getFirst_name() + "'," +
                    " `Last_Name`='" + acct.getLast_name()+ "'," +
                    " `Email`='" + acct.getEmail() + "'," +
                    " `Password`='" + acct.getPassword() + "' " +
                    " WHERE `User_Name`='" + acct.getUsername() + "'");
            statement = "UPDATE `Customers` SET `First_Name`='" + acct.getFirst_name() + "'," +
                    " `Last_Name`='" + acct.getLast_name()+ "' " +
                    " `Email`='" + acct.getEmail() + "'," +
                    " `Password`='" + acct.getPassword() + "'," +
                    " WHERE `User_Name`='" + acct.getUsername() + "'";
            ps.executeUpdate();
        }

        catch(Exception e){
            e.printStackTrace();
            Log.i("Update.class", statement);
        }

        return acct;
    }

}
