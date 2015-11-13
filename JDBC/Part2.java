/*
 * Part 2: JDBC program as a client program to do Bank Transactions 
 * */

import java.sql.*;
import java.io.*;
import oracle.jdbc.*;
import oracle.sql.*;
import java.util.*;

public class Part2 
{
  private static Connection conn = null;
  public static void main(String[] args) 
  {
    try
    {
      DriverManager.registerDriver
        (new oracle.jdbc.driver.OracleDriver());

      System.out.println("Connecting to JDBC...");

      conn = DriverManager.getConnection
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
      conn.setAutoCommit(false);
      System.out.println("JDBC connected.\n");

      // Create a statement
      //Statement stmt = conn.createStatement();
      Boolean loop = true;     
      while(loop){
        String option = null; 

        System.out.println("---------------------------------------------------------"); 
        System.out.println("1. Open Branch");
        System.out.println("2. Close Branch");
        System.out.println("3. Setup Account");
        System.out.println("4. Setup Customer");
        System.out.println("5. Close Account");
        System.out.println("6. Withdraw");
        System.out.println("7. Deposit");
        System.out.println("8. Transfer");
        System.out.println("9. Show Branch");
        System.out.println("10. Show All Branches");
        System.out.println("11. Show Customer");
        System.out.println("12. Exit");

        Console console = System.console();
        option = console.readLine("Please select an option (input number or type option): ");

        if (option.equals("1") || option.toUpperCase().equals("OPEN BRANCH")){
          System.out.println("---------------------------------------------------------"); 
          String address = console.readLine("Enter an address: ");
          openBranch(address);
        }
        else if (option.equals("2") || option.toUpperCase().equals("CLOSE BRANCH")){
          System.out.println("---------------------------------------------------------"); 
          String branch = console.readLine("Enter a branch number or an address to close: ");
          closeBranch(branch);
        }
        else if (option.equals("3") || option.toUpperCase().equals("SETUP ACCOUNT")){
          System.out.println("---------------------------------------------------------"); 
          String customerName = console.readLine("Enter customer name this account is for: ");
          String branch = console.readLine("Enter branch number or branch address this account is for: ");
          String initialAmount = console.readLine("Enter the initial amount for this account: ");
          setupAccount(customerName, branch, initialAmount);
        }        
        else if (option.equals("4") || option.toUpperCase().equals("SETUP CUSTOMER")){
          System.out.println("---------------------------------------------------------"); 
          String customerName = console.readLine("Enter customer name: ");
          String branch = console.readLine("Enter Branch number or branch address: ");
          setupCustomer(customerName, branch);
        }
        else if (option.equals("5") || option.toUpperCase().equals("CLOSE ACCOUNT")){
          System.out.println("---------------------------------------------------------"); 
          String customerName = console.readLine("Enter customer name: ");
          String branch = console.readLine("Enter Branch number or branch address: ");
          closeAccount(customerName, branch);
        }
        else if (option.equals("6") || option.toUpperCase().equals("WITHDRAW")){
          System.out.println("---------------------------------------------------------"); 
          String customerName = console.readLine("Enter customer name: ");
          String accountNo = console.readLine("Enter account number (XXX XXXX): " );
          String withdrawAmt = console.readLine("Enter withdraw amount: " );
          withdraw(customerName, accountNo, withdrawAmt);
        }	
        else if (option.equals("7") || option.toUpperCase().equals("DEPOSIT")){
          System.out.println("---------------------------------------------------------"); 
          String customerName = console.readLine("Enter customer name: ");
          String accountNo = console.readLine("Enter account number (XXX XXXX): " );
          String depositAmt = console.readLine("Enter deposit amount: " );
          deposit(customerName, accountNo, depositAmt);
        }
        else if (option.equals("8") || option.toUpperCase().equals("TRANSFER")){
          transfer();
        }
        else if (option.equals("9") || option.toUpperCase().equals("SHOW BRANCH")){
          showBranch();
        }
        else if (option.equals("10") || option.toUpperCase().equals("SHOW ALL BRANCHES")){
          showAllBranches();
        }
        else if (option.equals("11") || option.toUpperCase().equals("SHOW CUSTOMER")){
          showCustomer();
        }
        else if (option.equals("12") || option.toUpperCase().equals("EXIT")){
          System.out.println("Exiting... goodbye!");
          loop = false;
        }
        else{
          System.out.println("Invalid Input. Try again");
        }

      }


      //			stmt.close();
      conn.close();
    } 
    catch(Exception e)
    {
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
  }

  //Method to open a branch. Prompts the user to enter an address. 
  public static void openBranch(String address){
    Statement stmt = null;
    try{

      stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      ResultSet rs = stmt.executeQuery("Select branchNo,address from branch order by branchNo asc");
      int counter = 0;
      //Check for the first unused instance of a branchNo and insert into the branch table with that  
      while (rs.next()){
        if (Integer.parseInt(rs.getString("branchNo")) != counter){
          break;
        } 
        counter++;
      }


      rs.moveToInsertRow();
      rs.updateString("branchNo",String.format("%03d",counter)); 
      rs.updateString("address", address);
      rs.insertRow(); 

      System.out.println("Branch was opened at " + address + " with branch number " + String.format("%03d",counter));  

      conn.commit();

      rs.close();
    }
    catch(SQLIntegrityConstraintViolationException e){
      System.out.println("Entered branch address already exists, cannot add a new branch");
    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (stmt != null){
          stmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }
  public static void closeBranch(String branch){
    Statement stmt = null; 
    try{
      stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      ResultSet rs = stmt.executeQuery("Select branchNo, address from branch");
      //Case where branch number is given
      try{
        int branchNo = Integer.parseInt(branch);

        //boolean to keep track if there has been any match
        boolean isFound = false;
        while(rs.next()){
          if(String.format("%03d",branchNo).equals(rs.getString("branchNo"))){
            isFound = true;
            System.out.println("Deleting Branch Number " + rs.getString("branchNo") + " at address " + rs.getString("address"));
            rs.deleteRow();
          }
        }
        if (!isFound){
          System.out.println("No matching branch with branch number found, please enter the correct branch number");
        }

      }
      //Case where address is given
      catch(NumberFormatException e){
        boolean isFound = false;
        while(rs.next()){
          if(branch.equals(rs.getString("address"))){
            isFound = true;
            System.out.println("Deleting Branch Number " + rs.getString("branchNo") + " at address " + rs.getString("address"));
            rs.deleteRow();
          }
        }
        if (!isFound){
          System.out.println("No matching branch with the specified address was found, please enter a valid address");
        }
      }


      conn.commit();
      rs.close(); 


    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (stmt != null){
          stmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }
  public static void setupAccount(String customerName, String branch, String initialAmount){
    PreparedStatement pStmt = null; //This is used to check if branch and customer exists in the database before inserting an account
    ResultSet rs = null;
    String branchNum = null;
    String customerNum = null;
    float initAmt = 0.0f; 
    try{
      /*---------- Check if branch is open (in the database) ----------- */ 
      //Case where branch number is given
      try{
        int branchNo = Integer.parseInt(branch);
        pStmt = conn.prepareStatement("Select branchNo from branch where branchNo = ?",
            ResultSet.TYPE_SCROLL_INSENSITIVE, 
            ResultSet.CONCUR_READ_ONLY);
        pStmt.setString(1, String.format("%03d",branchNo));
        rs = pStmt.executeQuery();
        //Check if 
        if(!rs.first()){
          System.out.println("Specified branch by branchNumber does not exist");  
          return;
        }
        branchNum = String.format("%03d",branchNo);
      }
      //Case where address is given
      catch(NumberFormatException e){
        pStmt = conn.prepareStatement("Select branchNo, address from branch where address= ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        pStmt.setString(1, branch);
        rs = pStmt.executeQuery();
        if(!rs.first()){
          System.out.println("Specified branch by address does not exist");  
          return;
        }
        rs.first();
        branchNum = rs.getString("branchNo");
      }
      finally{
        try{
          if (rs != null){
            rs.close();
          }
          if (pStmt != null){
            pStmt.close();
          }
        }
        catch(Exception e){
          e.printStackTrace();
        }
      }


      // Check passed in customer name if she exists
      pStmt = conn.prepareStatement("Select customerNo, name from customer where name = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); 
      pStmt.setString(1,customerName);

      rs = pStmt.executeQuery();
      if(!rs.first()){
        System.out.println("The customer name " + customerName + " is not found in the customer table. Please enter an existing customer");
        return;
      }

      rs.first();
      customerNum = rs.getString("customerNo");

      rs.close();
      pStmt.close();

      // Check if initial amount is valid (greater than 0)
      try{
        if (Float.parseFloat(initialAmount) < 0){
          System.out.println("Initial amount given is less than 0, cannot make account");
          return;
        }
        initAmt = Float.parseFloat(initialAmount);
      }
      catch(NumberFormatException e){
        initAmt = 0;        
      }

      pStmt = conn.prepareStatement("Select branchNo, localAccNo, customerNo, balance from account where branchNo = ? order by localAccNo asc",
          ResultSet.TYPE_SCROLL_INSENSITIVE, 
          ResultSet.CONCUR_UPDATABLE);
      pStmt.setString(1,branchNum);
      rs = pStmt.executeQuery();
      int counter = 0;

      while (rs.next()){
        if(Integer.parseInt(rs.getString("localAccNo")) != counter){
          break;
        }
        counter++;
      }


      rs.moveToInsertRow();
      rs.updateString("branchNo", branchNum);
      rs.updateString("localAccNo",String.format("%04d",counter)); 
      rs.updateString("customerNo", customerNum);
      rs.updateFloat("balance", initAmt);
      rs.insertRow(); 

      System.out.println("Account was opened at branch number " + 
          branchNum + 
          " on customer named " + 
          customerName + 
          ". Local account number is " 
          + String.format("%04d",counter) 
          + " with initial amount of " 
          + initialAmount);  

      //Update the customer status after inserting the new account row
      updateCustomerStatus(customerNum);

      conn.commit();

      rs.close();



    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }
  public static void setupCustomer(String customerName, String branch){
    PreparedStatement pStmt = null; 
    ResultSet rs = null;
    try{
      //Check if customer already exists in database
      pStmt = conn.prepareStatement("select name from customer where name = ?", 
          ResultSet.TYPE_SCROLL_INSENSITIVE, 
          ResultSet.CONCUR_UPDATABLE);

      pStmt.setString(1,customerName);
      rs = pStmt.executeQuery();

      if(rs.first()){
        System.out.println("Customer has already been set up, not setting up customer again");
        return;
      }

      rs.close();
      pStmt.close();

      //Insert the customer at with the highest customer number, name, and status
      int maxCustomerNo = 0;
      pStmt = conn.prepareStatement("select max(customerNo) as max_customer_no from customer",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);

      rs = pStmt.executeQuery();
      if (rs.first()){
        maxCustomerNo = rs.getInt("max_customer_no") + 1;
      }

      rs.close();
      pStmt.close();

      pStmt = conn.prepareStatement("select customerNo, name, status from customer",
          ResultSet.TYPE_SCROLL_INSENSITIVE, 
          ResultSet.CONCUR_UPDATABLE);
      rs = pStmt.executeQuery();
      rs.moveToInsertRow();
      rs.updateString("customerNo", String.format("%05d",maxCustomerNo));
      rs.updateString("name", customerName); 
      rs.updateInt("status", 0);
      rs.insertRow(); 

      rs.close();
      pStmt.close();
      //Set up an account for the customer  
      setupAccount(customerName,branch,String.valueOf(0));

      conn.commit();


      System.out.println("Customer " + customerName + " was set up with a new customer account (#" + String.format("%05d",maxCustomerNo) + ") with status 0");     


    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }

  //Close an account given a customer name and a branch (number or address)
  //@param customerName: the customer name (String)
  //@param branch: the branch address or number (String)
  //return void
  public static void closeAccount(String customerName, String branch){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    try{
      int branchNum = 0;
      int customerNum = 0;
      //Assign branch number if branch string given can be parsed into an integer
      try{
        branchNum = Integer.parseInt(branch);
      }
      //If branch string given is an address, need to query database for the branchNo 
      catch(NumberFormatException e){
        pStmt = conn.prepareStatement("select branchNo from branch where address = ?", 
            ResultSet.TYPE_SCROLL_INSENSITIVE, 
            ResultSet.CONCUR_READ_ONLY);

        pStmt.setString(1,branch);
        rs = pStmt.executeQuery();
        //Check if given branch address gives back no results, if so return with an error
        if(!rs.first()){
          System.out.println("Error - Address given does not match with any branch address");
          return;
        }
        rs.first();
        branchNum = Integer.parseInt(rs.getString("branchNo"));

      }
      finally{
        try{
          if(rs !=null){
            rs.close();
          }
          if (pStmt != null){
            pStmt.close();
          }
        }
        catch(Exception e){
          e.printStackTrace();
        }
      }

      // Check passed in customer name if she exists
      pStmt = conn.prepareStatement("Select customerNo, name from customer where name = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY); 

      pStmt.setString(1,customerName);

      rs = pStmt.executeQuery();
      if(!rs.first()){
        System.out.println("The customer name " + customerName + " is not found in the customer table. Please enter an existing customer");
        return;
      }

      rs.first();
      customerNum = Integer.parseInt(rs.getString("customerNo"));

      rs.close();
      pStmt.close();

      pStmt = conn.prepareStatement("select branchNo, localAccNo, customerNo, balance from account where branchNo = ? and customerNo = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE, 
          ResultSet.CONCUR_UPDATABLE);

      pStmt.setString(1,String.format("%03d",branchNum));
      pStmt.setString(2,String.format("%05d",customerNum));

      rs = pStmt.executeQuery();

      //Check if result set is empty
      if(!rs.isBeforeFirst()){
        System.out.println("There are no accounts associated with this customer at this branch, did not close any accounts");
        return; 
      }
      Boolean isDelete = false; 
      //For each result row, if the balance is 0, then delete it
      while (rs.next()){
        if(rs.getFloat("balance") == 0){
          System.out.println("Closing account at branch#"   + 
              rs.getString("branchNo")      + 
              " with local account number#" + 
              rs.getString("localAccNo")    + 
              " under customer "            + 
              customerName);  
          rs.deleteRow();
          isDelete = true;
        }
      }

      rs.close();
      pStmt.close();

      if(!isDelete){
        System.out.println("No accounts associated with this customer " + customerName + " at this branch# " + branchNum + " had a balance of 0. None deleted");
        return;
      }

      //Updating customer status after removing a branch
      updateCustomerStatus(String.format("%05d",customerNum));

      //Closing the associated branch if it has no more accounts
      pStmt = conn.prepareStatement("select count(*) as total from account where branchNo = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY); 
      pStmt.setString(1,String.format("%03d",branchNum));
      rs = pStmt.executeQuery();
      rs.first();
      int numAssocAcc = rs.getInt("total");

      //If total of associated accounts is 0, close the branch 
      if(numAssocAcc == 0){
        closeBranch(branch);
      }

      rs.close();
      pStmt.close(); 

      //deleting the associated customer if she has no more accounts
      pStmt = conn.prepareStatement("select count(*) as total from account where customerNo = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      pStmt.setString(1,String.format("%05d",customerNum));
      rs = pStmt.executeQuery();
      rs.first();
      numAssocAcc = rs.getInt("total");

      rs.close();
      pStmt.close();
      if (numAssocAcc == 0){
        pStmt = conn.prepareStatement("delete from customer where customerNo = ?",
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_UPDATABLE);

        pStmt.setString(1,String.format("%05d",customerNum));
        pStmt.executeUpdate();
        System.out.println("Deleted customer " + customerName + " as there are no more accounts associated with this customer");
        pStmt.close();
      }
      conn.commit();
    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }
  public static void withdraw(String custName, String accNo, String withdrawAmt){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    try{
      int customerNum = 0;
      //Parsing the accNo in format XXX XXXX
      String[] account = accNo.split("\\s+");
      int branchNum = 0;
      int localAccNum = 0;
      float amtToWithdraw = Float.parseFloat(withdrawAmt);
      //Checking correct acount number format
      if (account.length != 2){
        System.out.println("Error - please enter a valid account number (XXX XXXX)");
        return;
      }
      if (account[0].length() != 3 || account[1].length() != 4){
        System.out.println("Error - please enter a valid account number (XXX XXXX)");
        return;
      }
      branchNum = Integer.parseInt(account[0]);
      localAccNum  = Integer.parseInt(account[1]);


      //Get the customer number (assuming customer name is unique);
      pStmt = conn.prepareStatement("select customerNo from customer where name = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      pStmt.setString(1,custName);
      rs = pStmt.executeQuery();
      if (!rs.first()){
        System.out.println("Error - Customer with the given name was not found, cancelling withdraw");
        return;
      }

      rs.first();
      customerNum = Integer.parseInt(rs.getString("customerNo"));

      rs.close();
      pStmt.close();

      pStmt = conn.prepareStatement("select branchNo, localAccNo, customerNo, balance" +  
          " from account " + 
          " where customerNo = ? and branchNo = ? and localAccNo = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_UPDATABLE);

      pStmt.setString(1,String.format("%05d",customerNum));
      pStmt.setString(2,String.format("%03d",branchNum));
      pStmt.setString(3,String.format("%04d",localAccNum));

      rs = pStmt.executeQuery();
      if(rs.first()){
        if(rs.getFloat("balance") >= amtToWithdraw){
          rs.updateFloat("balance", rs.getFloat("balance") - amtToWithdraw);
          rs.updateRow();  
          System.out.println("Account number " +
              String.format("%03d",branchNum) +
              " " +
              String.format("%04d",localAccNum) +
              " was withdrawn the amount of " +
              String.format("%.2f",amtToWithdraw)+
              ". Balance left is "
              + rs.getFloat("balance"));

        }
        else{
          System.out.println("Error - balance is less than the withdraw amount specified, cannot withdraw");
          return; 
        }
      }
      else{
        System.out.println("Error - could not find an account at this branch with this customer, cancelling withdraw");
        return;
      }

      updateCustomerStatus(String.format("%05d",customerNum));
      conn.commit();

    }
    catch(NumberFormatException e){
      System.out.println("Error - Entered account number is not valid, please enter a valid account number(XXX XXXX)");
      return;
    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }
  public static void deposit(String custName, String accNo, String depositAmount){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    try{
      int customerNum = 0;
      //Parsing the accNo in format XXX XXXX
      String[] account = accNo.split("\\s+");
      int branchNum = 0;
      int localAccNum = 0;
      float amtToDeposit = Float.parseFloat(depositAmount);
      //Checking correct acount number format
      if (account.length != 2){
        System.out.println("Error - please enter a valid account number (XXX XXXX)");
        return;
      }
      if (account[0].length() != 3 || account[1].length() != 4){
        System.out.println("Error - please enter a valid account number (XXX XXXX)");
        return;
      }
      branchNum = Integer.parseInt(account[0]);
      localAccNum  = Integer.parseInt(account[1]);

      //Get the customer number (assuming customer name is unique);
      pStmt = conn.prepareStatement("select customerNo from customer where name = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      pStmt.setString(1,custName);
      rs = pStmt.executeQuery();
      if (!rs.first()){
        System.out.println("Error - Customer with the given name was not found, cancelling withdraw");
        return;
      }

      rs.first();
      customerNum = Integer.parseInt(rs.getString("customerNo"));

      rs.close();
      pStmt.close();

      pStmt = conn.prepareStatement("select branchNo, localAccNo, customerNo, balance" +  
          " from account " + 
          " where customerNo = ? and branchNo = ? and localAccNo = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_UPDATABLE);

      pStmt.setString(1,String.format("%05d",customerNum));
      pStmt.setString(2,String.format("%03d",branchNum));
      pStmt.setString(3,String.format("%04d",localAccNum));

      rs = pStmt.executeQuery();
      if(rs.first()){
        rs.updateFloat("balance", rs.getFloat("balance") + amtToDeposit);
        rs.updateRow();  
        System.out.println("Account number " +
            String.format("%03d",branchNum) +
            " " +
            String.format("%04d",localAccNum) +
            " was deposited the amount of " +
            String.format("%.2f", amtToDeposit)+
            ". Balance left is "
            + rs.getFloat("balance"));

      }
      else{
        System.out.println("Error - could not find an account at this branch with this customer, cancelling withdraw");
        return;
      }

      updateCustomerStatus(String.format("%05d",customerNum));
      conn.commit();
    }
    catch(NumberFormatException e){
      System.out.println("Error - Entered account number is not valid, please enter a valid account number(XXX XXXX)");
      return;
    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }
  public static void transfer(){
    System.out.println("I'm in transfer");
    try{
      Statement stmt = conn.createStatement();

      stmt.close();
    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
  }
  public static void showBranch(){
    System.out.println("I'm in show branch");
    try{
      Statement stmt = conn.createStatement();

      stmt.close();
    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
  }
  public static void showAllBranches(){
    System.out.println("I'm in show all branches");
    try{
      Statement stmt = conn.createStatement();

      stmt.close();
    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
  }
  public static void showCustomer(){
    System.out.println("I'm in show customer");
    try{
      Statement stmt = conn.createStatement();

      stmt.close();
    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
  }

  //Helper method to update the customer's status after updating customer's total balance
  //@param customerNo : string, assuming this is in format "XXXXX"
  //@return void
  private static void updateCustomerStatus(String customerNo){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    try{
      float totalBalance = 0.0f;

      //Get all accounts given the customer number
      pStmt = conn.prepareStatement("select balance from account where customerNo = ?",  
          ResultSet.TYPE_SCROLL_INSENSITIVE, 
          ResultSet.CONCUR_READ_ONLY);
      pStmt.setString(1,customerNo);
      rs = pStmt.executeQuery();

      //add up the total balance of the accounts
      while (rs.next()){
        totalBalance += rs.getFloat("balance");
      }      

      rs.close();
      pStmt.close();

      pStmt = conn.prepareStatement("update customer set status = ? where customerNo = ?", 
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_UPDATABLE);
      pStmt.setString(2,customerNo);

      int status = 0;
      //If balance is 0 then status is 0
      if (totalBalance == 0){
        status = 0; 
      }
      //If total balance is < 1000 then status is 1
      else if (totalBalance < 1000){
        status = 1;
      }
      //If total balance is < 2000 then status is 2
      else if (totalBalance < 2000){
        status = 2;
      }
      //Otherwise total balance is 3
      else{
        status = 3;
      }

      pStmt.setInt(1,status);
      pStmt.executeUpdate();

      conn.commit();   


    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if(rs != null){
          rs.close();
        }
        if(pStmt != null){
          pStmt.close();
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }
}
