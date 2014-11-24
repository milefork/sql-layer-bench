package main;

/**
 * Exception Klasse für die Singelton Verbindung
 * @author 201327170
 * @see Database
 *
 */
public class ConnectionException extends Exception {
	private static final long serialVersionUID = 6417016287094370455L;

	public ConnectionException(){};
	
	public ConnectionException(String msg){
		super(msg);
	}
}
