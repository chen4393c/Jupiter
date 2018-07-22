package db.mysql;

public class MySQLDBUtil {
	private static final String HOSTNAME = "localhost";
	private static final String PORT_NUM = "3306";
	private static final String DB_NAME = "laiproject";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	public static final String URL = String.format(
		"jdbc:mysql://%s:%s/%s?user=%s&password=%s&autoReconnect=true", 
		HOSTNAME, PORT_NUM, DB_NAME, USERNAME, PASSWORD
	);
}
