package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection {
	private Connection conn;
	
	public MySQLConnection() {
        try {
            // Register driver
            Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
            // Connect to MySQL
            conn = DriverManager.getConnection(MySQLDBUtil.URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void close() {
		if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if (conn == null) {
            return;
        }
        String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES(?, ?)";
        try {
            for (String itemId : itemIds) {
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, userId);
                statement.setString(2, itemId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (conn == null) {
            return;
        }
        String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
        try {
            for (String itemId : itemIds) {
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, userId);
                statement.setString(2, itemId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		Set<String> itemIds = new HashSet<>();
        if (conn == null) {
            return itemIds;
        }
        String sql = "SELECT item_id FROM history WHERE user_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                itemIds.add(resultSet.getString("item_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemIds;
	}
	
	@Override
    public Set<Item> getFavoriteItems(String userId) {
        Set<Item> items = new HashSet<>();
        if (conn == null) {
            return items;
        }
        Set<String> itemIds = getFavoriteItemIds(userId);
        String sql = "SELECT * FROM items WHERE item_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            for (String itemId : itemIds) {
                statement.setString(1, itemId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Item.ItemBuilder builder = new Item.ItemBuilder();
                    builder.setItemId(itemId);
                    builder.setName(resultSet.getString("name"));
                    builder.setAddress(resultSet.getString("address"));
                    builder.setCategories(getCategories(itemId));
                    builder.setImageUrl(resultSet.getString("image_url"));
                    builder.setUrl(resultSet.getString("url"));
                    builder.setDistance(resultSet.getDouble("distance"));

                    items.add(builder.build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

	@Override
	public Set<String> getCategories(String itemId) {
		Set<String> categories = new HashSet<>();
        if (conn == null) {
            return categories;
        }
        String sql = "SELECT category FROM categories WHERE item_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, itemId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                categories.add(resultSet.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		/*
         * Similar code with SearchItem.java: call TickMasterAPI.search
         * */
        TicketMasterAPI tmAPI = new TicketMasterAPI();
        /*
         * 1. Send HTTP GET request to get all JSONObject events
         * and purify the data into Item objects
         * */
        List<Item> items = tmAPI.search(lat, lon, term);
        // 2. Save item data into db
        for (Item item : items) {
            saveItem(item);
        }
        return items;
	}

	@Override
	public void saveItem(Item item) {
		if (conn == null) {
            return;
        }
        try {
            // 1. Insert data from item object into items table
            // IGNORE: handle duplicate records
            String sql = "INSERT IGNORE INTO items VALUES(?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, item.getItemId());
            statement.setString(2, item.getName());
            statement.setString(3, item.getAddress());
            statement.setString(4, item.getImageUrl());
            statement.setString(5, item.getUrl());
            statement.setDouble(6, item.getDistance());
            statement.executeUpdate();

            // 2. Update categories table for each category
            sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
            for (String category : item.getCategories()) {
                statement = conn.prepareStatement(sql);
                statement.setString(1, item.getItemId());
                statement.setString(2, category);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	@Override
	public String getFullName(String userId) {
		if (conn == null) {
            return null;
        }
        String name = "";
        try {
            String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                name = String.join(" ",
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
            return false;
        }
        try {
            String sql = "SELECT user_id FROM users WHERE user_id = ? and password = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
	}

}
