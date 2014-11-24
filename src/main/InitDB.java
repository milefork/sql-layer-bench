package main;

public class InitDB {
	void CreateTable () {
		java.sql.Statement statement = null;
		System.out.printf("Create new Tables...");
		String createTableSQL = 
				" create table branches " +
					"( branchid int not null, " +
					"branchname char(20) not null, " +
					"balance int not null, " +
					"address char(72) not null, " +
					"primary key (branchid) ); " +
				"create table accounts " +
					"( accid int not null, " +
					"name char(20) not null, " +
					"balance int not null, " +
					"branchid int not null, " +
					"address char(68) not null, " +
					"primary key (accid), " +
					"foreign key (branchid) references branches ); " +
				"create table tellers " +
					"( tellerid int not null, " +
					"tellername char(20) not null, " +
					"balance int not null, " +
					"branchid int not null, " +
					"address char(68) not null, " +
					"primary key (tellerid), " +
					"foreign key (branchid) references branches ); " +
				"create table history " +
					"( accid int not null, " +
					"tellerid int not null, " +
					"delta int not null, " +
					"branchid int not null, " +
					"accbalance int not null, " +
					"cmmnt char(30) not null, " +
					"foreign key (accid) references accounts, " +
					"foreign key (tellerid) references tellers, " +
					"foreign key (branchid) references branches );  ";
		
		try {
			statement = Database.getInstance().getStatement();
			if (true == statement.execute(createTableSQL)){
				System.out.printf("Result but not displaying them.\r");
			} else {
				System.out.printf("No Results.\r");
			}
			statement.getConnection().commit();
			statement.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	void DropOldTable(){
		java.sql.Statement statement = null;
		System.out.printf("Drop old Tables...");
		String dropTableSQL = 
				"DROP TABLE IF EXISTS history; "+
				"DROP TABLE IF EXISTS accounts; " +
				"DROP TABLE IF EXISTS tellers; " +
				"DROP TABLE IF EXISTS branches; ";
		try {
			statement = Database.getInstance().getStatement();
			if (true == statement.execute(dropTableSQL)){
				System.out.printf("Result but not displaying them.\r");
			} else {
				System.out.printf("No Results.\r");
			}
			statement.getConnection().commit();
			statement.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void Create(){
		DropOldTable();
		CreateTable();	
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.printf("Main INIT\r");
		InitDB Test = new InitDB1();
		Test.Create();
	}
}
