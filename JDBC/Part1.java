/*This is a sample JDBC program. In order to run it,
 *    You need to do the following. 
 *
 *    1. edit this file with correct oracle username and password
 *    2. javac JDBCselect.java
 *    3. java JDBCselect
 *
 *    JDBC connected.
 *
 *    If you don't have this table in your database, you will
 *    get error message displayed. 
 *    */

/*
 * Part 1: JDBC program to create table Branch, Customer, and Account  
 * */

import java.sql.*;
import java.io.*;
import oracle.jdbc.*;
import oracle.sql.*;
import java.util.*;

public class Part1 
{
	public static void main(String[] args) 
	{
		try
		{
			DriverManager.registerDriver
				(new oracle.jdbc.driver.OracleDriver());

			System.out.println("Connecting to JDBC...");

			Connection conn = DriverManager.getConnection
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

			System.out.println("JDBC connected.\n");

			// Create a statement
			Statement stmt = conn.createStatement();

			//Creating branch table	  
			boolean result = stmt.execute("create table branch( branchNo varchar2(3) NOT NULL, address varchar2(50) NOT NULL UNIQUE, PRIMARY KEY(branchNo))");
			System.out.println("Table BRANCH created");

			//Creating  customer table	
			result = stmt.execute
			("create table customer(customerNo varchar2(5) NOT NULL, name varchar2(25) NOT NULL UNIQUE, status INTEGER CHECK(status >= 0 AND status <= 3), PRIMARY KEY(customerNo))");
			System.out.println("Table CUSTOMER created");

			
			result = stmt.execute("create table account(branchNo varchar2(3) NOT NULL, localAccNo varchar2(4) NOT NULL, customerNo varchar2(5) NOT NULL, balance NUMBER CHECK(balance >= 0), PRIMARY KEY(branchNo,localAccNo), FOREIGN KEY (branchNo) references branch(branchNo) ON DELETE CASCADE, FOREIGN KEY (customerNo) references customer(customerNo) ON DELETE CASCADE)");
			System.out.println("Table ACCOUNT created");


			stmt.close();
			conn.close();
		} 
		catch(Exception e)
		{
			System.out.println("SQL exception: ");
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
