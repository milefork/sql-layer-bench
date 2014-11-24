package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.Semaphore;


public class Bench5 extends Bench implements Runnable{

	int connections = 10;
	int bulkSize = 10;
 	int n = 0;
 	String accInsert = "";
	
 	
	Semaphore connLock = new Semaphore(connections, true);
	Stack<Connection> stack = new Stack<Connection>();
	void initConnection(){
		int index = 0;
		try{
			for (index=0; index!=connections; ++index){
				stack.push(Database.getInstance().getConnection());		
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void run(){
		System.out.printf(".");
		
		try{
			//TODO Even this won't work ... :(
			Connection conn=DriverManager.getConnection("jdbc:fdbsql://serv-pc:15433/benchmark");
			conn.setAutoCommit(false);
			PreparedStatement Pre = conn.prepareStatement(accInsert);
			Pre.executeUpdate();
			conn.commit();
			Pre.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	private String bulkInsert(String header, int bulkSize) {
		final String name = "Datenbank People DBI";
		final String address = "Westfälische Hochschule Abteilung Bocholt Münsterstr D-46397 Bocholt";
		StringBuilder sb = new StringBuilder();
		sb.append(header);
		bulkSize = bulkSize - 1;
		for (int i = 0; i != bulkSize; ++i) {
			sb.append("('").append(name).append("',0,((CAST(RAND() * ").append(n-1).append(" AS INT))+1),'").append(address).append("'),\n");
		}
		sb.append("('").append(name).append("',0,((CAST(RAND() * ").append(n-1).append(" AS INT))+1),'").append(address).append("');");
		return sb.toString();
	}
	
	
	void InsertBranch(){		
		String BRANCHNAME = "Bocholt HochschuleDB";
		String ADDRESS = "Westfälische Hochschule Abteilung Bocholt Münsterstr 265 D-46397 Bocholt";
		String insertTableSQL = 
				"INSERT INTO branches (branchname, balance, address) VALUES  ('"+BRANCHNAME+"',0,'"+ADDRESS+"')";
		try{
			Connection conn=Database.getInstance().getConnection();
			PreparedStatement Pre = conn.prepareStatement(insertTableSQL);
			for(int index=0; n != index; ++index) {
				Pre.addBatch();
			}
			Pre.executeBatch();
			conn.commit();
			Pre.close();
			Database.getInstance().releaseConnection(conn);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void InsertAccounts(){
		int ende = n*1000; //100000
		int index = 0;
		Queue<Thread> queue = new LinkedList<Thread>();
		Thread tHelper = null;
		accInsert = bulkInsert("INSERT INTO accounts (name, balance, branchid, address) VALUES ", bulkSize);
		ende /= bulkSize;
		ende=10;
		
		for (index = 0; ende != index; ++index) {	
			tHelper = new Thread(this);
			tHelper.start();
			queue.add(tHelper);
		}
		for (index = 0; ende != index; ++index) {	
			tHelper = queue.poll();
			try{
				for (;;tHelper.join(1)) {
					if (false==tHelper.isAlive()) {
						break;
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	void InsertTeller(){
		int ende = n*10;
		try{
			Connection conn=Database.getInstance().getConnection();
			PreparedStatement Pre = conn.prepareStatement(bulkInsert("INSERT INTO tellers (tellername, balance, branchid, address) VALUES ", ende));
			Pre.executeUpdate();
			conn.commit();
			Pre.close();
			Database.getInstance().releaseConnection(conn);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void DoBenchmark(int N){
		try {
			Database.getInstance(connections);
			initConnection();
			
			InitDB db = new InitDB1();
			db.Create();
			n = N;
			
			System.out.printf("Benchmark startet");
			long time = System.nanoTime();
			InsertBranch();	System.out.printf(" .");
			InsertAccounts();	System.out.printf(" .");
			InsertTeller();	System.out.printf(" .");
			InsertHistory(N);	System.out.printf(" .");
			time = System.nanoTime()-time;
			System.out.printf("Ende\rZeit(Sec): %d,%03d.%03d.%03d \r", (time/1000000000), (time/1000000)%1000, (time/1000)%1000, time%1000);
			
			Database.getInstance().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
