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
          System.out.println("---------------------------------------------------------"); 
          String customerName = console.readLine("Enter customer name: ");
          String sourceAccountNo = console.readLine("Enter account number to withdraw money from (XXX XXXX): " );
          String sinkAccountNo = console.readLine("Enter account number to transfer the money to (XXX XXXX): " );
          String amount = console.readLine("Enter amount of money to transfer (or press enter if no initial amount): " );
          transfer(customerName, sourceAccountNo, sinkAccountNo, amount);
        }
        else if (option.equals("9") || option.toUpperCase().equals("SHOW BRANCH")){
          System.out.println("---------------------------------------------------------"); 
          String branch = console.readLine("Enter branch number or address: ");
          showBranch(branch);
        }
        else if (option.equals("10") || option.toUpperCase().equals("SHOW ALL BRANCHES")){
          showAllBranches();
        }
        else if (option.equals("11") || option.toUpperCase().equals("SHOW CUSTOMER")){
          System.out.println("---------------------------------------------------------"); 
          String customerName = console.readLine("Enter customer name : ");
          showCustomer(customerName);
        }
        else if (option.equals("12") || option.toUpperCase().equals("EXIT")){
          System.out.println("Exiting... goodbye!");
          loop = false;
        }
        else{
          System.out.println("Invalid Input. Try again");
        }

      }
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
      System.out.println("Entered branch address is invalid or already exists. cannot add a new branch");
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
          return;
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
          return;
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

  /* Function setupAccount
   * Description: sets up an account, given the customer, the branch (address or branch number), and an initial amount
   * @param customerName: the customer name (String)
   * @param branch : the branch number or the branch address (String)
   * @param initialAmount : the initial amount to set up an account with (Float), must be greater than or equal to 0
   * return: Boolean -> True if account has been successfully set up, false if there was any case of inconsistency
   */
  public static Boolean setupAccount(String customerName, String branch, String initialAmount){
    PreparedStatement pStmt = null; //This is used to check if branch and customer exists in the database before inserting an account
    ResultSet rs = null;
    String branchNum = null;
    String customerNum = null;
    Boolean isValid = false;
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
          return false;
        }
        branchNum = String.format("%03d",branchNo);
      }
      //Case where address is given
      catch(NumberFormatException e){
        branchNum = String.format("%03d", getBranchNoFromAddress(branch)); 
        if (Integer.parseInt(branchNum) == -1){
          System.out.println("Specified branch by address does not exist");
          return false;
        }
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
        return false;
      }

      rs.first();
      customerNum = rs.getString("customerNo");

      rs.close();
      pStmt.close();

      // Check if initial amount is valid (greater than 0)
      try{
        if (Float.parseFloat(initialAmount) < 0){
          System.out.println("Initial amount given is less than 0, cannot make account");
          return false;
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
          + String.format("%.2f",initAmt));  

      //Update the customer status after inserting the new account row
      updateCustomerStatus(customerNum);

      conn.commit();

      rs.close();

      isValid = true;
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
      return isValid;
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
      Boolean isValid = setupAccount(customerName,branch,String.valueOf(0));

      //If we can't set up an account, we know for sure that the branch is not valid, rollback changes
      if (!isValid){
        System.out.println("Specified branch was not valid. Cannot set up customer's account. No customer was set up");
        conn.rollback();
        return;
      }
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
        branchNum = getBranchNoFromAddress(branch); 
        if (branchNum == -1){
          System.out.println("Specified branch by address does not exist");
          return;
        }
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

      //Query customer table for the customer number
      try{
        customerNum = Integer.parseInt(getCustomerNoFromName(customerName));
      }
      catch(NumberFormatException e){
        System.out.println("The customer name " + customerName + " is not found. Please enter an existing customer");
        return;
      }

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

      //Query customer table for the customer number
      try{
        customerNum = Integer.parseInt(getCustomerNoFromName(custName));
      }
      catch(NumberFormatException e){
        System.out.println("The customer name " + custName + " is not found. Please enter an existing customer");
        return;
      }


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

      //Query customer table for the customer number
      try{
        customerNum = Integer.parseInt(getCustomerNoFromName(custName));
      }
      catch(NumberFormatException e){
        System.out.println("The customer name " + custName + " is not found. Please enter an existing customer");
        return;
      }

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

  /* Function name: transfer
   * Transfers money from one account (source) to another account (sink). Both accounts must be owned by the given customer
   * and the Source account must have at least the amount to be transferred to the Sink account
   * @param customerName : the customer name (String)
   * @param accNoSource : the source account number to take money out of (format XXX XXXX) (String)
   * @param accNoSink : the sink account number to transfer money to (format XXX XXXX) (String)
   * return void 
   */
  public static void transfer(String customerName, String accNoSource, String accNoSink, String amount){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    try{
      int customerNum               = 0;
      //Parsing the accNo in format XXX XXXX
      String[] sourceAccount        = accNoSource.split("\\s+");
      String[] sinkAccount          = accNoSink.split("\\s+");
      int sourceBranchNum           = 0;
      int sourceLocalAccNum         = 0;
      int sinkBranchNum             = 0;
      int sinkLocalAccNum           = 0;
      float amtToTransfer           = Float.parseFloat(amount);

      //Checking correct sink account number format
      if (sourceAccount.length != 2){
        System.out.println("Error - please enter a valid account number (XXX XXXX) for the account source");
        return;
      }
      if (sourceAccount[0].length() != 3 || sourceAccount[1].length() != 4){
        System.out.println("Error - please enter a valid account number (XXX XXXX) for the account source");
        return;
      }

      //Checking correct source account number format
      if (sinkAccount.length != 2){
        System.out.println("Error - please enter a valid account number (XXX XXXX) for the account sink");
        return;
      }
      if (sinkAccount[0].length() != 3 || sinkAccount[1].length() != 4){
        System.out.println("Error - please enter a valid account number (XXX XXXX) for the account sink");
        return;
      }

      sourceBranchNum = Integer.parseInt(sourceAccount[0]);
      sourceLocalAccNum = Integer.parseInt(sourceAccount[1]);

      sinkBranchNum = Integer.parseInt(sinkAccount[0]);
      sinkLocalAccNum = Integer.parseInt(sinkAccount[1]);

      //Query customer table for the customer number
      try{
        customerNum = Integer.parseInt(getCustomerNoFromName(customerName));
      }
      catch(NumberFormatException e){
        System.out.println("The customer name " + customerName + " is not found. Please enter an existing customer");
        return;
      }

      //Query account table using customer number, branch number, and matches the source or sink local account numbers
      pStmt = conn.prepareStatement("select branchNo, localAccNo, customerNo, balance " + 
          "from account where customerNo = ? AND ((branchNo = ? and localAccNo = ?) OR (branchNo = ? and localAccNo = ?))",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_UPDATABLE);
      pStmt.setString(1,String.format("%05d",customerNum));
      pStmt.setString(2,String.format("%03d",sourceBranchNum));
      pStmt.setString(3,String.format("%04d",sourceLocalAccNum));
      pStmt.setString(4,String.format("%03d",sinkBranchNum));
      pStmt.setString(5,String.format("%04d",sinkLocalAccNum));

      rs = pStmt.executeQuery();

      Boolean isSourceExist = false;
      float sourceBalanceRemaining = 0.0f;
      float sinkBalanceRemaining = 0.0f;
      //First check if the specified amount is less than the balance of the account source's balance, if it is then subtract
      while(rs.next()){
        //Find matching source account number
        if(Integer.parseInt(rs.getString("branchNo")) == sourceBranchNum 
            && Integer.parseInt(rs.getString("localAccNo")) == sourceLocalAccNum){

          isSourceExist = true;
          if(rs.getFloat("balance") < amtToTransfer){
            System.out.println("Error - amount to transfer is greater than the balance of the first account. Cannot transfer");
            return;
          } 

          rs.updateFloat("balance", rs.getFloat("balance") - amtToTransfer);
          sourceBalanceRemaining = rs.getFloat("balance");
          rs.updateRow(); 
            } 
      }

      if (!isSourceExist){
        System.out.println("Error - First account number given does not exist with this customer. Cannot transfer");
        return; 
      }
      //Next, check if the account sink exists, if not, then rollback changes (specify error)
      rs.beforeFirst();
      Boolean isSinkExist = false;
      while(rs.next()){
        //Find matching sink account number
        if(Integer.parseInt(rs.getString("branchNo")) == sinkBranchNum 
            && Integer.parseInt(rs.getString("localAccNo")) == sinkLocalAccNum){

          isSinkExist = true;
          rs.updateFloat("balance",rs.getFloat("balance") + amtToTransfer);
          sinkBalanceRemaining = rs.getFloat("balance");
          rs.updateRow();
            } 
      }

      //If the sink account number does not match with any in the result set, we need to roll back our changes 
      if(!isSinkExist){
        System.out.println("Error - Second account number given does not exist with this customer. Cannot transfer");
        conn.rollback();
        return;
      }

      conn.commit();
      System.out.println("Successfully transferred $" +
          String.format("%.2f",amtToTransfer) + " from Account " +
          String.format("%03d",sourceBranchNum) + " " + String.format("%04d",sourceLocalAccNum) +
          " (Updated Balance: $" + String.format("%.2f",sourceBalanceRemaining) + ")" +  
          " to Account " +
          String.format("%03d",sinkBranchNum) + " " + String.format("%04d",sinkLocalAccNum) +
          " (Updated Balance: $" + String.format("%.2f",sinkBalanceRemaining) + ")" +
          " For customer " + customerName); 

    }
    catch(NumberFormatException e){
      System.out.println("Error - One of the accounts has an invalid account number or the amount specified is invalid. Account number must be in format XXX XXXX. No transfer occured");
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

  /* Function showBranch
   * Description: Show the branch information, associated accounts, and total balance of the branch, for a given branch address or number
   * @param branch: the branch address or the branch number
   */
  public static void showBranch(String branch){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    try{

      float totalBalance = 0.0f;
      int   branchNum    = 0;
      String branchAddress = null;

      //Attempt to convert branch into a number. (if it worked, then we know it's a branch number passed in)
      try{
        branchNum = Integer.parseInt(branch);

      }
      //Otherwise, get the branch number given the branch address 
      catch (NumberFormatException e){
        branchNum = getBranchNoFromAddress(branch);
        if (branchNum == -1 ){
          System.out.println("Error - given branch address does not specify a branch.");
          return;
        }
      }

      //Check if branch exists in database 
      pStmt = conn.prepareStatement("select branchNo, address from branch where branchNo = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);

      pStmt.setString(1, String.format("%03d", branchNum));
      rs = pStmt.executeQuery();
      if (!rs.first()){
        System.out.println("Error - Specified branch number does not exist.");
        return;
      } 

      branchAddress = rs.getString("address");

      rs.close();
      pStmt.close();

      //Print out the branch number and address, followed by the accounts (and their balances)  associated with this branch 
      System.out.println(" ");
      System.out.println("|------------------------------------------------- ");
      System.out.println("|Branch Number : " + String.format("%03d",branchNum));
      System.out.println("|Branch Address : " + branchAddress);
      System.out.println("|ACCOUNTS");
      System.out.println("|--------");

      pStmt = conn.prepareStatement("select localAccNo, balance from account where branchNo = ? order by localAccNo asc",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY); 
      pStmt.setString(1,String.format("%03d",branchNum));
      rs = pStmt.executeQuery();

      Boolean isEmpty = true;
      while (rs.next()){
        System.out.println("|Local Account number: " + rs.getString("localAccNo") + "     balance: " + String.format("%.2f",rs.getFloat("balance")));

        totalBalance += rs.getFloat("balance");
        isEmpty = false;
      }
      
      if(isEmpty){
        System.out.println("|No accounts associated with this branch");
        
      }
      System.out.println("|Total balance: " + String.format("%.2f", totalBalance));
      System.out.println("|------------------------------------------------- ");

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

  /* Function showAllBranches
   * Description: Show all the branches in the DB using the showBranch method
   */
  public static void showAllBranches(){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    try{
      pStmt = conn.prepareStatement("Select branchNo from branch",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      rs = pStmt.executeQuery();

      while(rs.next()){
        showBranch(rs.getString("branchNo"));    
      }


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

  /* Function showCustomer
   * Description: show's customer information (customer number, status) as well as information on all accounts associated
   * Also displays the total balance for customer
   * @param customerName : the customer name (string)
   * return void
   */
  public static void showCustomer(String customerName){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    float totalBalance = 0.0f;
    try{

      String customerNum = getCustomerNoFromName(customerName);
      if (customerNum == null){
        return;
      }

      pStmt = conn.prepareStatement("select customerNo, name, status from customer where customerNo = ?",
                                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY);

      pStmt.setString(1,customerNum);

      rs = pStmt.executeQuery();

      rs.first();

      System.out.println(" ");
      System.out.println("Customer Number: " +  rs.getString("customerNo"));
      System.out.println("Customer Name: " + rs.getString("name"));
      System.out.println("Customer Status: " + rs.getInt("status"));
      System.out.println(" ");
      System.out.println("ACCOUNTS");
      System.out.println("--------");

      rs.close();
      pStmt.close();

      //Print out the associated accounts if any.
      pStmt = conn.prepareStatement("select branchNo, localAccNo, balance from account where customerNo = ?",
                                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY);

      pStmt.setString(1,customerNum);
      rs = pStmt.executeQuery();

      if(!rs.first()){
        System.out.println("No accounts associated with this customer");
      }
      rs.beforeFirst();

      while(rs.next()){
        System.out.println(" ");
        System.out.println("Account Number: " + rs.getString("branchNo") + " " + rs.getString("localAccNo") + "  Balance: " + String.format("%.2f",rs.getFloat("balance")));
        totalBalance += rs.getFloat("balance");
      }

      System.out.println(" ");

      System.out.println("Total Balance: " + String.format("%.2f",totalBalance));

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

  /* Function: getCustomerNoFromName
   * Helper method to Getting customer number from a given customer name
   * @param customerName : the given customer's name
   * 
   * return String for the customer number in string form, or null 
   * if the given customer name doesn't exist in database
   */
  private static String getCustomerNoFromName(String customerName){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    String customerNum = null;
    try{
      //Get the customer number (assuming customer name is unique);
      pStmt = conn.prepareStatement("select customerNo from customer where name = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      pStmt.setString(1,customerName);
      rs = pStmt.executeQuery();
      if (!rs.first()){
        System.out.println("Error - Customer with the given name was not found ");
        return null;
      }

      customerNum = rs.getString("customerNo"); 
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
      return customerNum;

    }
  }

  /* Function getBranchNoFromAddress 
   * Description: Helper method to getting the branch number from a given branch address
   * @param branchAddress
   * return an int for the branch number , or -1 if the given address does not specify a branch number
   */
  private static int getBranchNoFromAddress (String branchAddress){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    int branchNum = 0;
    try{
      //Get the branch number (assuming the address is unique);
      pStmt = conn.prepareStatement("select branchNo from branch where address = ?",
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      pStmt.setString(1,branchAddress);
      rs = pStmt.executeQuery();
      if (!rs.first()){
        branchNum = -1;
        return branchNum; 
      }

      branchNum = Integer.parseInt(rs.getString("branchNo"));  
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
      return branchNum;

    }
  }
}
