package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		A2();
	}
	
	public static void A1() {
		System.out.println("Geben sie Produktids ein! Abbrechen mit !");
		String s = "";
		ArrayList<String> pid = new ArrayList<String>();
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{
				s = in.readLine();
				if(!s.contains("!"))
					pid.add(s);
				else
					break;
			}
			String st = "Select pid, aid, SUM(dollars) from dbi.orders where pid IN (";
			
			st += "'" + pid.get(0) + "'";
			for (int i = 1; i < pid.size(); ++i)
			{
				st += ", '" + pid.get(i) + "'";
			}
			st += ") group by aid,pid";
			System.out.println(st);
			
			Connection conn = DriverManager.getConnection("jdbc:fdbsql://serv-pc/dbi");
			java.sql.Statement stmt = conn.createStatement();
			ResultSet res = stmt.executeQuery(st);
			
			while (res.next()) {
				System.out.printf("%s %s %f \n", res.getString(1), res.getString(2), res.getFloat(3));
			}
			stmt.close();
			conn.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void A2() {
		
	}
}
