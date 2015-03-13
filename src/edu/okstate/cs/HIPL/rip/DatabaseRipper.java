package edu.okstate.cs.hipl.rip;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileWriter;

import java.io.IOException;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import java.util.ArrayList;
import java.util.Properties;

public class DatabaseRipper implements Ripper {
    private String hostname;
    private int port;
    private String username;
    private String password;
    private String database;
    private String dbms;
    private String tablename;
    private String columnname;
    private String query;
    public DatabaseRipper(String hostname, int port, String username, String password, String database, String dbms, String tablename, String columnname){
        this.hostname=hostname;
        this.port=port;
        this.username=username;
        this.password=password;
        this.database=database;
        this.dbms=dbms;
        this.tablename=tablename;
        this.columnname=columnname;
    }
    /**
     *Setter method
     * @param tablename set database table name
     */
    
    public void setTableName(String tablename){
        this.tablename=tablename;
    }
    /**
     *Setter Method
     * @param columnname set table column name to extract the urls
     */
    
    public void setColumnName(String columnname){
        this.columnname=columnname;
    }
    /**
     *Getter Method for tablename
     * @return table name as String
     */
    public String getTableName(){
        return this.tablename;
    }
    /**
     *Getter Method for column name
     * @return column name as String
     */
    public String getColumnName(){
        return this.columnname;
    }
    /**
     *Setter method for host name
     * @param hostname sets the hostname for database server
     */
    public void setHostname(String hostname){
        this.hostname=hostname;
    }
    /**
     *Setter method for username
     * @param username sets the username for the host
     */
    
    public void setUsername(String username){
        this.username=username;
    }
    /**
     *Setter method for password
     * @param password sets the password for the host
     */
    public void setPassword(String password){
        this.password=password;
    }
    /**
     *Setter method for database
     * @param database sets the database name.
     */
    public void setDatabase(String database){
        this.database=database;
    }
    /**
     *Setter method for DBMS type(supports mysql and derby)
     * @param dbms sets the dbms type.
     */
    public void setDbms(String dbms){
        this.dbms=dbms;
    }
    
    /**
     *Setter method for database port (defaults to 3306)
     * @param port sets he port number for database server
     */
    public void setPort(int port){
        this.port=port;
    }
    /**
     *Getter method for hostname
     * @return hostname is returned as a string
     */
    public String getHostname(){
        return this.hostname;
    }
    /**
     *Getter method for username
     * @return username is returned as a String
     */
    public String getUsername(){
        return this.username;
    }
    /**
     *Getter method for password
     * @return password is returned as a String
     */
    public String getPassword(){
        return this.password;
    }
    
    /**
     * Getter method for database port
     * @return port number is returned as int
     */
    public int getPort(){
        return this.port;
    }
    /**
     * Getter method for database name
     * @return database name is returned as string
     */
    public String getDatabase(){
        return this.database;
    }
    /**
     * Getter method for database type (supports mysql and derby)
     * @return database type is returned as String
     */
    public String getDbms(){
        return this.dbms;
    }
    /**
     * Gets the URLs of all the images using the ripper
     * @return a array of URLs as String Array
     */
    
    @Override
    public String[] getURLs() {
        // TODO Implement this method
        ArrayList urllist=new ArrayList<String>();
        Connection conn=getConnection();
        Statement stmt=null;
        String query;
        if(this.query.equals("") || this.query==null){
            query="SELECT "+this.columnname+" from "+this.database+"."+this.tablename;
        }
        else{
            query=this.query;
        }
        if(conn!=null){
            try {
                stmt = conn.createStatement();
                ResultSet rs=stmt.executeQuery(query);
                while(rs.next()){
                    String temp=rs.getString(this.columnname);
                    urllist.add(temp);
                }
            } catch (SQLException e) {
                System.out.println("INVALID QUERY");
            }

        }else{
            System.out.println("NULL CONNECTION");
        }
        String[] out = (String[]) urllist.toArray(new String[0]);
        return out;
    }
    /**
     * Writes the obtained URLs to the file
     * @param file - URLs are added to the referenced File.
     */
    
    @Override
    public void writeURLFile(File file) {
        // TODO Implement this method
        String path=file.getAbsolutePath();
        path.replaceAll("\\","/");
        path=path.substring(0, path.lastIndexOf("/"));
        File temp=new File(path);
        if(temp.isDirectory()){
            temp.mkdirs();
        }
        String[] urls=getURLs();
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
            for(int i=0;i<urls.length;i++){
                    bw.write(urls[i]);
                    bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println("INVALID WRITE EXCEPTION");
        }    
    }

    private Connection getConnection() {
        Connection conn=null;
        Properties connProp=new Properties();
        connProp.put("user",this.username);
        connProp.put("password",this.password);
        
        if(this.dbms.equals("mysql")){
            try {
                conn =
                    DriverManager.getConnection("jdbc:" + this.dbms + "://" + this.hostname + ":" + this.port + "/",
                                                connProp);
            } catch (SQLException e) {
                System.out.println("Invalid connection");
            }
        }
        else if(this.dbms.equals("derby")){
            try {
                conn =
                    DriverManager.getConnection("jdbc:" + this.dbms + ":" + this.database + ";create=true", connProp);
            } catch (SQLException e) {
                System.out.println("Invalid connection");
            }
        }
        return conn;
    }
}
