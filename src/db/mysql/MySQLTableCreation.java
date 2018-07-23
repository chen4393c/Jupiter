package db.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

/**
 * Run this as Java application to reset db schema
 * */

public class MySQLTableCreation {
	public static void main(String[] args) {
		// 0. Ensure the driver is registered.
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
		} catch (Exception e) {
			System.out.println("Driver class was not found. " + e.getMessage());
		}

		/*
		 * This is java.sql.Connection instead of 
		 * com.mysql.jdbc.Connection
		 * */
		Connection conn = null;
		
		// 1. Connect to MySQL.
		System.out.println("Connecting to " + MySQLDBUtil.URL);
		try {
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (SQLException e) {
			System.out.println("Connecting to MySQL failed. " + e.getMessage());
		}
		
		if (conn == null) {
			return;
		}
		
		try {
			// 2. Drop tables in case they exist.
			Statement stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS history";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS categories";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS items";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);

			// 3. Create tables
			sql = "CREATE TABLE items ("
					+ "item_id VARCHAR(255) NOT NULL, "
					+ "name VARCHAR(255), "
					+ "address VARCHAR(255), "
					+ "image_url VARCHAR(255), "
					+ "url VARCHAR(255), "
					+ "distance FLOAT, "
					+ "PRIMARY KEY (item_id)"
					+ ")";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE categories ("
					+ "item_id VARCHAR(255) NOT NULL, "
					+ "category VARCHAR(255) NOT NULL, "
					+ "PRIMARY KEY (item_id, category), "
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL, "
					+ "password VARCHAR(255) NOT NULL, "
					+ "first_name VARCHAR(255), "
					+ "last_name VARCHAR(255), "
					+ "PRIMARY KEY (user_id)"
					+ ")";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE history ("
					+ "user_id VARCHAR(255) NOT NULL, "
					+ "item_id VARCHAR(255) NOT NULL, "
					+ "last_favor_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, "
					+ "PRIMARY KEY (user_id, item_id), "
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id), "
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id) "
					+ ")";
			stmt.executeUpdate(sql);

			// 4. Insert a fake user
			sql = "INSERT INTO users VALUES"
					+ "(\"1111\", \"2222\", \"Chaoran\", \"Chen\")";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Import is done successfully");
	}
}
