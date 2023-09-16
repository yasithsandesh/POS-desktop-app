/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author 94701
 */
public class MySQL {
    private static Connection connection;
    
    static {
    
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_shop", "root", "2002MySaG");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    
    public static ResultSet execute(String query) throws Exception{
    
        Statement statement = connection.createStatement();
        
        if(query.startsWith("SELECT")){
         
            return statement.executeQuery(query);
            
        }else{
            
            statement.executeUpdate(query);
            return null;
        }
        
    }
}
