package com.android.softwear.process;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Michael on 9/22/2015.
 */
public class ConnectDB {

    public static Connection conn = null;

    public static Connection getConnection() {

        try {

            Class.forName("com.mysql.jdbc.Driver");

            String db_user = "mscray1";
            String db_pass = "qazZAQ123321";
            String url = "jdbc:mysql://198.71.227.92:3306/softwear";
            //String url = "jdbc:mysql://10.0.2.2:3306/softwear";
            //String db_user = "mscray1";
            //String db_pass = "";
            Properties connectionProps = new Properties();
            connectionProps.put("user", db_user);
            connectionProps.put("password", db_pass);

            conn = DriverManager.getConnection(url, db_user, db_pass);
        }

        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }

        catch(SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }
}
