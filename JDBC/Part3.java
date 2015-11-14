/* Part 3: JDBC program to test Part 2's functionality
*/

import java.sql.*;
import java.io.*;
import oracle.jdbc.*;
import oracle.sql.*;
import java.util.*;

public class Part3{
  public static void main(String[] args){
    try{
      Part2 p2 = new Part2();

      //Connecting to JDBC
      p2.conn = DriverManager.getConnection
        //         ("jdbc:oracle:thin:@moon.scs.carleton.ca:1522:moon",
        ("jdbc:oracle:thin:@localhost:1521:xe",
         //          "oracleusername",
         //          "system",
         "oracle",
         //          "oracleuserpassword");
         //          "oracle11g");
        "oracle11g");
      //param1: the JDBC URL
      //param2: the name you used to log in to the DBMS
      //param3: the password
      p2.conn.setAutoCommit(false);
      System.out.println("JDBC connected.\n");

      System.out.println("Resetting all entries from last test execution");

      //Delete all entries in account table
      PreparedStatement pStmt = p2.conn.prepareStatement("delete from account");
      pStmt.execute();
      p2.conn.commit();
      pStmt.close();
      //Delete all entries in customer table
      pStmt = p2.conn.prepareStatement("delete from customer");
      pStmt.execute();
      p2.conn.commit();
      pStmt.close();

      //Delete all entries in branch table
      pStmt = p2.conn.prepareStatement("delete from branch");
      pStmt.execute();
      p2.conn.commit();
      pStmt.close();



      System.out.println("Tests 1-4: Adding branches");
      System.out.println("--------------------------");
      p2.openBranch("London");
      p2.openBranch("Paris");
      p2.openBranch("Toronto");
      p2.openBranch("New York");

      System.out.println(" ");
      System.out.println("Test 5: Show all branches");
      System.out.println("--------------------------");
      p2.showAllBranches();

      System.out.println(" ");
      System.out.println("Test 6: setup a customer called John in London branch");
      System.out.println("--------------------------");
      p2.setupCustomer("John","London",null);

      System.out.println(" ");
      System.out.println("Test 7: setup an account for John in Toronto branch with initial deposit of $500"); 
      System.out.println("--------------------------");
      p2.setupAccount("John","Toronto","500.00");

      System.out.println(" ");
      System.out.println("Test 8: setup an account for John in Paris branch with initial deposit of $1,000"); 
      System.out.println("--------------------------");
      p2.setupAccount("John","Paris", "1000.00");

      System.out.println(" ");
      System.out.println("Test 9: setup an account for John in New York branch with initial deposit of $1,000"); 
      System.out.println("--------------------------");
      p2.setupAccount("John","New York", "1000.00");

      System.out.println(" ");
      System.out.println("Test 10: show customer John"); 
      System.out.println("--------------------------");
      p2.showCustomer("John");

      System.out.println(" ");
      System.out.println("Test 11: setup a customer called Joan in Toronto branch with initial deposit of $1,000");
      System.out.println("--------------------------");
      p2.setupCustomer("Joan", "Toronto", "1000.00");


      System.out.println(" ");
      System.out.println("Test 12: setup a customer called Mary in Paris branch");
      System.out.println("--------------------------");
      p2.setupCustomer("Mary","Paris", null);
      
       
      System.out.println(" ");
      System.out.println("Test 13: deposit $1,000 to Mary's Paris branch");
      System.out.println("--------------------------");
      p2.deposit("Mary","001 0001", "1000.00");

      System.out.println(" ");
      System.out.println("Test 14: setup a customer called Mary in New York branch with initial deposit of $1,000. This should fail as customer already exists"); 
      System.out.println("--------------------------");
      p2.setupCustomer("Mary","New York", "1000.00");

      System.out.println(" ");
      System.out.println("Test 15: setup an account for Mary in New York branch with initial deposit of $1,000"); 
      System.out.println("--------------------------");
      p2.setupAccount("Mary","New York", "1000.00");


      System.out.println(" ");
      System.out.println("Test 16: show customer Mary"); 
      System.out.println("--------------------------");
      p2.showCustomer("Mary");

      System.out.println(" ");
      System.out.println("Test 17: setup a customer called Sean in Toronto branch with initial deposit of $1,000"); 
      System.out.println("--------------------------");
      p2.setupCustomer("Sean","Toronto", "1000.00");

      System.out.println(" ");
      System.out.println("Test 18: setup a customer called Tony in Ottawa branch. Should fail as there is no Ottawa branch"); 
      System.out.println("--------------------------");
      p2.setupCustomer("Tony","Ottawa",null);

      System.out.println(" ");
      System.out.println("Test 19: setup a customer called Tony in Toronto branch"); 
      System.out.println("--------------------------");
      p2.setupCustomer("Tony","Toronto",null);

      System.out.println(" ");
      System.out.println("Test 20: transfer $1000 from John's Toronto account to his Paris account. Should fail as John's toronto account has insufficient funds.");
      System.out.println("--------------------------");
      p2.transfer("John", "002 0000", "001 0000", "1000.00");

      System.out.println(" ");
      System.out.println("Test 21: transfer $1000 from John's New York account to his Paris account."); 
      System.out.println("--------------------------");
      p2.transfer("John", "003 0000", "001 0000", "1000.00");

      System.out.println(" ");
      System.out.println("Test 22: show the balances of all John’s account. "); 
      System.out.println("--------------------------");
      p2.showCustomer("John");

      System.out.println(" ");
      System.out.println("Test 23: Close all John's account that has 0 balance (accounts in New york and London");
      System.out.println("--------------------------");
      p2.closeAccount("John","London");
      p2.closeAccount("John","New York");

      System.out.println(" ");
      System.out.println("Test 24: Withdraw $1000.00 from Sean's account in toronto branch");
      System.out.println("--------------------------");
      p2.withdraw("Sean","002 0002","1000.00");

      System.out.println(" ");
      System.out.println("Test 25: show all accounts in Toronto branch");
      System.out.println("--------------------------");
      p2.showBranch("Toronto");

      System.out.println(" ");
      System.out.println("Test 26: Show all branches of the bank");
      System.out.println("--------------------------");
      p2.showAllBranches();

      p2.conn.close();


    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
  } 

}
