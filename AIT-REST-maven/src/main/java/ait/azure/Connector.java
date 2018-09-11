/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ait.azure;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Dwight
 */

public class Connector {

    private static String HOSTNAME = "internsserver.database.windows.net";
    private static String DB_NAME = "DB_Interns";
    private static String LOGIN = "cps01";
    private static String PASSWORD = "i&xAVaJ6.";

    public Connector() {

    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", HOSTNAME, DB_NAME, LOGIN, PASSWORD);
            connection = (Connection) DriverManager.getConnection(url);  
           
        } catch( Exception e){
            e.printStackTrace();
        }
        return connection;
    }
}
