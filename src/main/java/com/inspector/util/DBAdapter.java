/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.inspector.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.h2.tools.DeleteDbFiles;


/**
 *
 * @author dasha
 */
public class DBAdapter {

    public DBAdapter(List<String> pages) {
        try {
            Class.forName("org.h2.Driver");
         //   DeleteDbFiles.execute(".", "test", true);            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        String query = "create table if not exists test(id int NOT NULL auto_increment, name varchar(255), time timestamp, primary key(id))";
        try(Connection connection = DriverManager.getConnection("jdbc:h2:./test");
            Statement statement = connection.createStatement();
                ){
            statement.execute(query);
            ResultSet rs = statement.executeQuery("select name from test group by name");
            List<String> result = new ArrayList<>();
            while(rs.next()){
                result.add(rs.getString("name"));
            }
            for(String page: pages){
                if(!result.contains(page))
                    statement.execute("insert into test(name,time) values('"+page+"','"+getCurrentJavaSqlTimestamp()+"')");
            }
            
        } catch(SQLException e){
            e.printStackTrace();
        }    
    }

    public void insertDate(String name){
        Timestamp date = getCurrentJavaSqlTimestamp();
        String query = "insert into test(name,time) values(?, ?)";
        try(Connection connection = DriverManager.getConnection("jdbc:h2:./test");
            PreparedStatement statement = connection.prepareStatement(query); ){
            statement.setString(1, name);
            statement.setTimestamp(2, date);
            int row = statement.executeUpdate();
            
        } catch(SQLException e){
            e.printStackTrace();
        }
//        try(Connection connection = DriverManager.getConnection("jdbc:h2:./test");
//            PreparedStatement statement = connection.prepareStatement("select * from test");
//                ResultSet rs = statement.executeQuery()){
//                    while (rs.next()) {
//
//                        System.out.println(rs.getString("name"));
//                        System.out.println(rs.getTimestamp("time"));
//                    }
//            
//        } catch(SQLException e ){
//            e.printStackTrace();
//        }
    }
    
    public int getCountAll(String name){
        int result = 0;
        String query = "select count(*) from test where name = '"+name+"'";
        try(Connection connection = DriverManager.getConnection("jdbc:h2:./test");
            PreparedStatement statement = connection.prepareStatement(query);
                ResultSet rs = statement.executeQuery()){
                    while (rs.next()) {
                        result = rs.getInt("count(*)");
                    //    System.out.println(name+" - "+rs.getInt("count(*)"));
                    }
            
        } catch(SQLException e ){
            e.printStackTrace();
        }
        return result;
    }
    
    public int getCountToday(String name){
        int result = 0;
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        Timestamp date = new Timestamp(calendar.getTime().getTime());
 //       System.out.println(date);
        
        String query = "select count(*) from test where name = ? and time>?";
        try(Connection connection = DriverManager.getConnection("jdbc:h2:./test");
            PreparedStatement statement = connection.prepareStatement(query);){
                    statement.setString(1, name);
                    statement.setTimestamp(2, date);
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        result = rs.getInt("count(*)");
               //         System.out.println(name+" - "+rs.getInt("count(*)"));
                    }
            
        } catch(SQLException e ){
            e.printStackTrace();
        }
        return result;
        
    }
    
    public int getCountWeek(String name){
        int result = 0;
        Date today = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(today);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)-7);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    //    System.out.println(calendar.getTime());
        Timestamp date = new Timestamp(calendar.getTime().getTime());
    //    System.out.println(date);
        
        String query = "select count(*) from test where name = ? and time>?";
        try(Connection connection = DriverManager.getConnection("jdbc:h2:./test");
            PreparedStatement statement = connection.prepareStatement(query);){
                    statement.setString(1, name);
                    statement.setTimestamp(2, date);
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        result = rs.getInt("count(*)");
                //        System.out.println(name+" - "+rs.getInt("count(*)"));
                    }
            
        } catch(SQLException e ){
            e.printStackTrace();
        }
        return result;
        
    }
    private Timestamp getCurrentJavaSqlTimestamp() {
      Date date = new Date();
      
      return new Timestamp(date.getTime());
    }   
}
