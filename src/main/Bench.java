package main;

import java.sql.PreparedStatement;

public class Bench {
	void InsertBranch(int N){		
		String BRANCHNAME = "Bocholt HochschuleDB";
		String ADDRESS = "Westfälische Hochschule Abteilung Bocholt Münsterstr 265 D-46397 Bocholt";
		String insertTableSQL = 
				"INSERT INTO branches " +
				"(branchid, branchname, balance, address) VALUES " +
				"(?,'"+BRANCHNAME+"',0,'"+ADDRESS+"')";
		try{
			PreparedStatement Pre = Database.getInstance().getConnection().prepareStatement(insertTableSQL);
			for(int index=0; N != index; ++index){
				Pre.setInt(1, index+1);
				Pre.execute();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void InsertAccounts(int N){
		int Ende = N*100; //100000 zu viel zum Ausführen! deutlich über 5min
		int Index = 0;
		String Name = "Datenbank People DBI";
		String Adress = "Westfälische Hochschule Abteilung Bocholt Münsterstr D-46397 Bocholt";
		String insertTableSQL = 
				"INSERT INTO accounts " +
				"(accid, name, balance, branchid, address) VALUES " +
				"(?,'"+Name+"',0,"+"((CAST(RAND() * "+ (N-1) +" AS INT))+1),'"+Adress+"')";
		try{
			PreparedStatement Pre = Database.getInstance().getConnection().prepareStatement(insertTableSQL);
			for (Index=0; Ende != Index; ++Index) {
				Pre.setInt(1, Index+1);	//Können wir die ID auch auf das DBMS schieben?
				Pre.execute();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void InsertTeller(int N){
		int Ende = N*10;
		int Index = 0;
		String Name = "Datenbank People DBI";
		String Adress = "Westfälische Hochschule Abteilung Bocholt Münsterstr D-46397 Bocholt";
		String insertTableSQL = 
				"INSERT INTO tellers " +
				"(tellerid, tellername, balance, branchid, address) VALUES " +
				"(?,'"+Name+"',0,"+"((CAST(RAND() * "+ (N-1) +" AS INT))+1),'"+Adress+"')";
		try{
			PreparedStatement Pre = Database.getInstance().getConnection().prepareStatement(insertTableSQL);
			for (Index=0; Ende != Index; ++Index) {
				Pre.setInt(1, Index+1);	//Können wir die ID auch auf das DBMS schieben?
				Pre.execute();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void InsertHistory(int N){
		//Nichts zutun
	}
	
	void DoBenchmark(int N){
		InitDB db = new InitDB();
		db.Create();
		
		System.out.printf("Benchmark startet");
		long time = System.nanoTime();
		InsertBranch(N);	System.out.printf(" .");
		InsertAccounts(N);	System.out.printf(" .");
		InsertTeller(N);	System.out.printf(" .");
		InsertHistory(N);	System.out.printf(" .");
		time = System.nanoTime()-time;
		System.out.printf("Ende\rZeit(Sec): %d,%03d.%03d.%03d \r", (time/1000000000), (time/1000000)%1000, (time/1000)%1000, time%1000);
		try {
			Database.getInstance().close();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Bench Test = new Bench4();
		Test.DoBenchmark(10);
		System.out.printf("Ich habe fertig!");
	}
}
