/*
CLEAN: drops all tables 
 * */

import java.sql.*;
import java.io.*;
import oracle.jdbc.*;
import oracle.sql.*;
import java.util.*;

public class Clean 
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

			//dropping account table
			boolean result = stmt.execute("drop table account");
			System.out.println("Table ACCOUNT dropped");

			//dropping branch table	  
			result = stmt.execute(" Drop table branch");
			System.out.println("Table BRANCH dropped");

			//Creating  customer table	
			result = stmt.execute("drop table customer");
			System.out.println("Table CUSTOMER dropped");

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
