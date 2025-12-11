package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public Connection connect() {
        String dbName = "quanlydiem";
        String dbUser = "root";
        String dbPass = "QingHui13032006@";
        String url = "jdbc:mysql://localhost:3306/" + dbName + "?useUnicode=true&characterEncoding=UTF-8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, dbUser, dbPass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}