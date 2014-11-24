package main;

import java.sql.PreparedStatement;

public class Bench4 extends Bench {

	private static String bulkInsert(String header, int bulkSize, int n) {
		final String name = "Datenbank People DBI";
		final String address = "Westfälische Hochschule Abteilung Bocholt Münsterstr D-46397 Bocholt";
		StringBuilder sb = new StringBuilder();
		sb.append(header);
		
		for (int i = 0; i < bulkSize - 1; ++i) {
			sb.append("('").append(name).append("',0,((CAST(RAND() * ").append(n-1).append(" AS INT))+1),'").append(address).append("'),\n");
		}
		sb.append("('").append(name).append("',0,((CAST(RAND() * ").append(n-1).append(" AS INT))+1),'").append(address).append("');");
		return sb.toString();
	}
	
	
	void InsertBranch(int N){		
		String BRANCHNAME = "Bocholt HochschuleDB";
		String ADDRESS = "Westfälische Hochschule Abteilung Bocholt Münsterstr 265 D-46397 Bocholt";
		String insertTableSQL = 
				"INSERT INTO branches (branchname, balance, address) VALUES  ('"+BRANCHNAME+"',0,'"+ADDRESS+"')";
		try{
			PreparedStatement Pre = Database.getInstance().getConnection().prepareStatement(insertTableSQL);
			for(int index=0; N != index; ++index) {
				Pre.addBatch();
			}
			Pre.executeBatch();
			Pre.getConnection().commit();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void InsertAccounts(int N){
		int ende = N*1000; //100000
				
		try{
			PreparedStatement Pre = Database.getInstance().getConnection().prepareStatement(bulkInsert("INSERT INTO accounts (name, balance, branchid, address) VALUES ", 1000, N));
			for (int index = 0; ende > index ; index += 1000){
				Pre.executeUpdate();
				Pre.getConnection().commit();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	void InsertTeller(int N){
		int ende = N*10;
				
		try{
			PreparedStatement Pre = Database.getInstance().getConnection().prepareStatement(bulkInsert("INSERT INTO tellers (tellername, balance, branchid, address) VALUES ", ende, N));
			Pre.executeUpdate();
			Pre.getConnection().commit();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void DoBenchmark(int N){
		InitDB db = new InitDB1();
		db.Create();
		try {
			System.out.printf("Benchmark startet");
			long time = System.nanoTime();
			InsertBranch(N);	System.out.printf(" .");
			InsertAccounts(N);	System.out.printf(" .");
			InsertTeller(N);	System.out.printf(" .");
			InsertHistory(N);	System.out.printf(" .");
			time = System.nanoTime()-time;
			System.out.printf("Ende\rZeit(Sec): %d,%03d.%03d.%03d \r", (time/1000000000), (time/1000000)%1000, (time/1000)%1000, time%1000);
			
			Database.getInstance().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
