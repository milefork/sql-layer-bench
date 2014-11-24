package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Object Cache with Initializes Objects
 * Supporting several Connections, if the sql Layer is properly configured
 * @author 201327170
 *
 */
public class Database {
	
	private static Database instance;
	private int connections=10;
	private ArrayList<Connection> conn;
	private java.sql.Statement stmt1;
	private java.sql.Statement stmt2;
	private int connection_counter;
	private int round_connection=0;
	private int statement_counter;
	
	private void addConnection() throws SQLException {
		if(connection_counter<connections) {
			Connection tmp;
			if((connection_counter%2)==0){
				tmp = DriverManager.getConnection("jdbc:fdbsql://serv-pc:15433/benchmark");
			}
			else{
				tmp = DriverManager.getConnection("jdbc:fdbsql://serv-pc:15432/benchmark");
			}
			tmp.setAutoCommit(false);
			conn.add(tmp);
			connection_counter++;
		}
	}
	private void addConnection(int id) throws SQLException {
		if(connection_counter<connections) {
			Connection tmp;
			if((connection_counter%2)==0){
				tmp = DriverManager.getConnection("jdbc:fdbsql://serv-pc:15433/benchmark");
			}
			else{
				tmp = DriverManager.getConnection("jdbc:fdbsql://serv-pc:15432/benchmark");
			}
			tmp.setAutoCommit(false);
			conn.set(id, tmp);
		}
	}
	
	private void connect() throws SQLException {
		for(;connection_counter<connections;){
			addConnection();
		}
	}
	/**
	 * Must call before use!
	 * @param connCount the Count of Maximal connections
	 */
	private final void setConnections(int connCount){
		connections=connCount;
		try {
			connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Database(int connCount) throws ConnectionException{
		conn = new ArrayList<Connection>();
		setConnections(connCount);
	}
	
	public static Database getInstance(int connCount) throws ConnectionException {
		if(Database.instance==null){
			Database.instance = new Database(connCount);
		}
		return Database.instance;
	}
	
	public static Database getInstance() throws ConnectionException {
		if(Database.instance==null){
			Database.instance = new Database(10);
		}
		return Database.instance;
	}
	
	public final void releaseConnection(Connection c) {
		try {
			c.commit();
			c.close();
			conn.remove(c);
			connection_counter--;
		}
		catch (Exception ex) { ex.printStackTrace();}
	}
	
	public final Connection getConnection() throws SQLException{
		round_connection++;
		if(round_connection==connections) {
			round_connection=0;
		}
		if(connection_counter<connections) {
			addConnection();
			return getConnection();
		}
		
		if(conn.get(round_connection).isClosed()){
			addConnection(round_connection);
			return conn.get(round_connection);
		}
		return conn.get(round_connection);
	}
	
	public final Statement getStatement() throws SQLException{
		if(stmt1!=null){
			if (stmt1.isClosed()){
				stmt1 = getConnection().createStatement();
			}
		} else {
			stmt1 = getConnection().createStatement();
		}
		if(stmt2!=null){
			if (stmt2.isClosed()){
				stmt2 = getConnection().createStatement();
			}
		} else {
			stmt2 = getConnection().createStatement();
		}
		if(statement_counter%2==0) {
			return stmt1;
		}
		else {
			statement_counter++;
			return stmt2;
		}
	}
	
	/**
	 * Commit & Close
	 * @param id ConnectionID
	 */
	
	public final void closeConnection(int id){
		try{
			connection_counter--;
			conn.get(id).commit();
			conn.get(id).close();
		}
		catch (SQLException e){
			connection_counter++;
			e.printStackTrace();
		}
	}
	
	/**
	 * closes all objects
	 */
	public final void close(){
		try {
			try{
				stmt1.close();
				stmt1=null;
				stmt2.close();
				stmt2=null;
			}
			catch(SQLException ex){
				
			}
			for (Connection c : conn) {
				c.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
