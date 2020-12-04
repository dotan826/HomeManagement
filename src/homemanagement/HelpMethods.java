package homemanagement;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * This class contains all the methods for this program (Without GUI!).
 * 
 * 
 */
public class HelpMethods {
    private int returnValueChooser;                       // saving file chooser answer
    private JFileChooser chooser;                         // file chooser
    private FileNameExtensionFilter filter;               // filter for file chooser
    private File dbFile;                                  // database file
    private int returnValueJOptionPane;                   // saving joption pane answer
    private Connection databaseConnection;                // database file active connection
    private Statement sqlStatement;                       // SQL Statement (WITHOUT parameters in the query)
    private String sqlQuery;                              // SQL Query
    private PreparedStatement sqlPreparedStatement;       // SQL Statement (WITH parameters in the query)
    private ResultSet sqlResultSet;                       // SQL Result Set (sql query answers)
    
    /**
     * Set Windows 10 GUI (Look and Feel)
     */
    public void changeLookAndFeelWindow10() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            
        } catch (ClassNotFoundException e) {
            
        } catch (InstantiationException e) {
            
        } catch (IllegalAccessException e) {
            
        }
    }
    
    /**
     * Creating File Chooser for use in create/load files.
     */
    public void createJFileChooser(){
        String userDir = System.getProperty("user.home");  // gets the current pc user directory path
        chooser = new JFileChooser(userDir + "/Desktop"); // initiate file chooser with default folder as Desktop
        filter = new FileNameExtensionFilter("SQLite Database File", "db"); // initiate filter for SQLite Files
        chooser.setFileFilter(filter);
    } // end of method
    
    /**
     * Creating Database File.
     * @param windowfirst The window in which the action initiate.
     */
    public void createDatabase(WindowFirst windowfirst){
        this.createJFileChooser();
        returnValueChooser = chooser.showOpenDialog(windowfirst);
        if (returnValueChooser == JFileChooser.APPROVE_OPTION){
            String fileName = chooser.getSelectedFile().getName(); // get the name of the file
            if(fileName.length()>=4){ // if the file name is longer than 4 characters
                if(chooser.getSelectedFile().getName().substring(fileName.length()-3, fileName.length()).equals(".db")){  // if the file ends with ".db"
                    dbFile = new File(chooser.getSelectedFile().getAbsolutePath());
                }
                else{
                    dbFile = new File(chooser.getSelectedFile().getAbsolutePath() + ".db");
                }
            }
            else{
                dbFile = new File(chooser.getSelectedFile().getAbsolutePath() + ".db");
            }
            if (!(dbFile.exists())){  // if file not exists then create a new database file
                dbFile.getParentFile().mkdirs(); // update directories before file creation
                try {
                    dbFile.createNewFile(); // creating the new file
                } catch (IOException ex) {
                    Logger.getLogger(WindowFirst.class.getName()).log(Level.SEVERE, null, ex);
                }
                windowfirst.getTextFieldPathOfDatabaseFile().setText(dbFile.getAbsolutePath());
                windowfirst.getButtonCreateDatabase().setEnabled(false);
                windowfirst.getButtonLoadDatabase().setEnabled(false);
                windowfirst.getButtonDisconnectDatabase().setEnabled(true);
                windowfirst.getButtonDeleteDatabase().setEnabled(true);
                windowfirst.getButtonDatabaseInformation().setEnabled(true);
            }
            else{ // if file exists show error
                JOptionPane.showMessageDialog(windowfirst, "File Exists Already! Can't Overide.");
            }
        }
    } // end of method
    
    /**
     * Deleting Databse File.
     * @param windowfirst The window in which the action initiate.
     */
    public void deleteDatabase(WindowFirst windowfirst){
        returnValueJOptionPane = JOptionPane.showConfirmDialog(windowfirst, "Are you sure?"); // saing client answer
        if (returnValueJOptionPane == JOptionPane.YES_OPTION){ // if the client confirm the delete then...
            if (new File(windowfirst.getTextFieldPathOfDatabaseFile().getText()).delete()){ // if the delete is successfull
                JOptionPane.showMessageDialog(windowfirst, "File deleted successfully");
                windowfirst.getTextFieldPathOfDatabaseFile().setText(""); // erase file path
                windowfirst.getButtonCreateDatabase().setEnabled(true);
                windowfirst.getButtonLoadDatabase().setEnabled(true);
                windowfirst.getButtonDisconnectDatabase().setEnabled(false);
                windowfirst.getButtonDeleteDatabase().setEnabled(false);
                windowfirst.getButtonDatabaseInformation().setEnabled(false);
                dbFile = null; // erase file object
            }
            else{
                JOptionPane.showMessageDialog(windowfirst, "Failed to delete the file");
            }
        }
    } // end of method
    
    /**
     * Disconnect Database File.
     * @param windowfirst The window in which the action initiate.
     */
    public void disconnectDatabase(WindowFirst windowfirst) {
        windowfirst.getTextFieldPathOfDatabaseFile().setText(""); // erase file path
        windowfirst.getButtonCreateDatabase().setEnabled(true);
        windowfirst.getButtonLoadDatabase().setEnabled(true);
        windowfirst.getButtonDisconnectDatabase().setEnabled(false);
        windowfirst.getButtonDeleteDatabase().setEnabled(false);
        windowfirst.getButtonDatabaseInformation().setEnabled(false);
        dbFile = null; // erase file object
    } // end of mathod
    
    /**
     * Load an existing Database.
     * @param windowfirst The window in which the action initiate.
     */
    public void loadDatabase(WindowFirst windowfirst){
        this.createJFileChooser();
        returnValueChooser = chooser.showOpenDialog(windowfirst);
        if (returnValueChooser == JFileChooser.APPROVE_OPTION){
            String fileName = chooser.getSelectedFile().getName(); // get the name of the file
            if(fileName.length()>=4){ // if the file name is longer than 4 characters{
                if(chooser.getSelectedFile().getName().substring(fileName.length()-3, fileName.length()).equals(".db")){  // if the file ends with ".db"
                    dbFile = new File(chooser.getSelectedFile().getAbsolutePath());
                    if (dbFile.exists()) {  // if file exists then load this file
                        windowfirst.getTextFieldPathOfDatabaseFile().setText(dbFile.getAbsolutePath());
                        windowfirst.getButtonCreateDatabase().setEnabled(false);
                        windowfirst.getButtonLoadDatabase().setEnabled(false);
                        windowfirst.getButtonDisconnectDatabase().setEnabled(true);
                        windowfirst.getButtonDeleteDatabase().setEnabled(true);
                        windowfirst.getButtonDatabaseInformation().setEnabled(true);
                    }
                    else{
                        JOptionPane.showMessageDialog(windowfirst, "File doesn't exists! Can't load the file.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(windowfirst, "Not a valid database type file! Can't load the file.");
                }
            }
            else{
                JOptionPane.showMessageDialog(windowfirst, "File without an extension file type! Can't load the file.");
            }
        }
    } // end of method
    
    /**
     * Connect to Database file.
     * @param windowfirst
     */
    public void SQLiteJDBCDriverConnect(WindowFirst windowfirst){
        databaseConnection = null;
        try{
            String urlDatabaseFile = ("jdbc:sqlite:" + dbFile.getAbsolutePath()); // create SQLite connection string
            databaseConnection = DriverManager.getConnection(urlDatabaseFile);    // try to make a connection
            JOptionPane.showMessageDialog(windowfirst, "Connection to SQLite has been established."); // if connection is successful
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(windowfirst, "Can't make a Connection to SQLite file. --> " + e.getMessage()); // if connection is NOT successful
        }
    } // end of method
    
    /**
     * Disconnect from Databse file.
     * @param windowfirst 
     */
    public void SQLiteJDBCDriverDisconnect(WindowFirst windowfirst) {
        try {
            if (databaseConnection != null){ // if there is an active connection then close it
                databaseConnection.close();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(windowfirst, "Can't close Connection to SQLite file. --> " + ex.getMessage()); // if close failed
        }
    } // end of method
    
    /**
     * Create a new Table in the new database file.
     * @param windowfirst
     */
    public void SQLiteJDBCDriverCreateNewTable(WindowFirst windowfirst){
        sqlQuery = "CREATE TABLE IF NOT EXISTS Household (\n" +
                    " ExpenseType text PRIMARY KEY, \n" +
                    " January integer, \n" +
                    " February integer, \n" +
                    " March integer, \n" +
                    " April integer, \n" +
                    " May integer, \n" +
                    " June integer, \n" +
                    " July integer, \n" +
                    " August integer, \n" +
                    " September integer, \n" +
                    " October integer, \n" +
                    " November integer, \n" +
                    " December integer, \n" +
                    " AnnualCumulative integer, \n" +
                    " Notes text \n" +
                    ");";
        try{
            sqlStatement = databaseConnection.createStatement();
            sqlStatement.execute(sqlQuery);
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(windowfirst, "Can't create a new SQLite Statement. --> " + e.getMessage()); // if cant make a new statement object
        }
        
    } // end of method
    
    /**
     * Add a new row for Expense Type.
     * @param expenseType
     * @param windowfirst
     */
    public void SQLiteJDBCDriverInsertNewExpenseType(String expenseType, WindowFirst windowfirst){
        sqlQuery = "INSERT INTO Household(ExpenseType) VALUES(?)"; // sql query for insert
        try{
            sqlPreparedStatement = databaseConnection.prepareStatement(sqlQuery); // creating prepared statement
            sqlPreparedStatement.setString(1, expenseType);  // sets expense type
            sqlPreparedStatement.executeUpdate();  // execute Insert action
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(windowfirst, "Can't create a new SQLite Prepared Statement (Insert new row). --> " + e.getMessage()); // if cant make a new prepared statement object
        }
    } // end of method
    
    /**
     * Update a row with Money Expense for specific month.
     * @param expenseType
     * @param month
     * @param expenseMoneySum
     * @param windowfirst 
     */
    public void SQLiteJDBCDriverUpdateExpenseMoneySum(String expenseType, String month, int expenseMoneySum, WindowFirst windowfirst){
        sqlQuery = "UPDATE Household SET " + month + " = ? WHERE ExpenseType LIKE '" + expenseType + "'"; // sql query for update
        try{
            sqlPreparedStatement = databaseConnection.prepareStatement(sqlQuery); // creating prepared statement
            sqlPreparedStatement.setInt(1, expenseMoneySum); // sets expense money
            sqlPreparedStatement.executeUpdate(); // execute Update action
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(windowfirst, "Can't create a new SQLite Prepared Statement (Insert new row). --> " + e.getMessage()); // if cant make a new prepared statement object
        }
    } // end of method
    
    /**
     * Gets/Refresh all the information from SQL into the Table.
     * @param jTable1
     * @param windowfirst 
     */
    public void SQLiteJDBCDriverGetAllInformationFromTable(JTable jTable1, WindowFirst windowfirst){
        ((DefaultTableModel)jTable1.getModel()).setRowCount(0); // clears all existing rows
        int countSumAllYear = 0; // calculating all year
        sqlQuery = "SELECT ExpenseType, January, February, March, April, May,"
                + " June, July, August, September, October, November,"
                + " December, AnnualCumulative, Notes FROM Household"; // sql query for reading
        try{
            sqlStatement = databaseConnection.createStatement(); // creating a statement for reading data
            sqlResultSet = sqlStatement.executeQuery(sqlQuery);  // execute Select statement
            int i=0; // counters
            while (sqlResultSet.next()){
                String expenseType = sqlResultSet.getString("ExpenseType");
                int january = sqlResultSet.getInt("January");
                int february = sqlResultSet.getInt("February");
                int march = sqlResultSet.getInt("March");
                int april = sqlResultSet.getInt("April");
                int may = sqlResultSet.getInt("May");
                int june = sqlResultSet.getInt("June");
                int july = sqlResultSet.getInt("July");
                int august = sqlResultSet.getInt("August");
                int september = sqlResultSet.getInt("September");
                int october = sqlResultSet.getInt("October");
                int november = sqlResultSet.getInt("November");
                int december = sqlResultSet.getInt("December");
                int annualCumulative = sqlResultSet.getInt("AnnualCumulative");
                String notes = sqlResultSet.getString("Notes");
                
                countSumAllYear = january + february + march + april + may + june + july + august + september + october + november + december;
                
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{expenseType, january, february,
                                                                    march, april, may, june, july, august, september,
                                                                    october, november, december, countSumAllYear, notes});
                i++; // move to next row
            }
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(windowfirst, "Can't Read rows from the Table. --> " + e.getMessage()); // if cant read rows from table
        }
    } // end of method
    
    /**
     * Gets all the data from a Specific Column.
     * @param columnName
     * @param windowfirst
     * @return 
     */
    public ArrayList<String> SQLiteJDBCDriverGetSpecificColumn(String columnName, WindowFirst windowfirst){
        sqlQuery = "SELECT " + columnName + " FROM Household"; // sql query for reading
        ArrayList<String> columnData = new ArrayList<String>();
        try{
            sqlStatement = databaseConnection.createStatement(); // creating a statement for reading data
            sqlResultSet = sqlStatement.executeQuery(sqlQuery);  // execute Select statement
            while (sqlResultSet.next()){
                columnData.add(sqlResultSet.getString(columnName)); // save all data from column
            }
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(windowfirst, "Can't Read rows from the Table. --> " + e.getMessage()); // if cant read rows from table
        }
        return columnData;
    } // end of method
    
    /**
     * Delete Expense Type entire row.
     * @param expenseType
     * @param windowfirst 
     */
    public void SQLiteJDBCDriverDeleteExpenseType(String expenseType, WindowFirst windowfirst){
        sqlQuery = "DELETE FROM Household WHERE ExpenseType LIKE '" + expenseType + "'"; // sql query for reading
        try{
            sqlStatement = databaseConnection.createStatement(); // creating a statement for reading data
            sqlStatement.execute(sqlQuery);
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(windowfirst, "Can't Delete row from the Table. --> " + e.getMessage()); // if cant delete row from table
        }
    } // end of method
    
    /**
     * Gets data from a Specific Row.
     * @param expenseType
     * @param windowfirst
     * @return 
     */
    public String SQLiteJDBCDriverGetSpecificNotesRow(String expenseType, WindowFirst windowfirst){
        sqlQuery = "SELECT Notes FROM Household WHERE ExpenseType LIKE '" + expenseType + "'"; // sql query for reading
        String rowData = null;
        try{
            sqlStatement = databaseConnection.createStatement(); // creating a statement for reading data
            sqlResultSet = sqlStatement.executeQuery(sqlQuery);  // execute Select statement
            while (sqlResultSet.next()){
                rowData = sqlResultSet.getString("Notes"); // save row data from Notes column
            }
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(windowfirst, "Can't Read rows from the Table. --> " + e.getMessage()); // if cant read rows from table
        }
        return rowData;
    } // end of method
    
    /**
     * Update a specific Note.
     * @param expenseType
     * @param expenseMoneyNotes
     * @param windowfirst 
     */
    public void SQLiteJDBCDriverUpdateSpecificNote(String expenseType, String expenseMoneyNotes, WindowFirst windowfirst){
        sqlQuery = "UPDATE Household SET Notes = ? WHERE ExpenseType LIKE '" + expenseType + "'"; // sql query for update
        try{
            sqlPreparedStatement = databaseConnection.prepareStatement(sqlQuery); // creating prepared statement
            sqlPreparedStatement.setString(1, expenseMoneyNotes); // sets expense Note
            sqlPreparedStatement.executeUpdate(); // execute Update action
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(windowfirst, "Can't Insert the new Note. --> " + e.getMessage()); // if cant make a new prepared statement object
        }
    } // end of method
    
    
} // end of class
