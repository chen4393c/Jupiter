package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * TicketMaster response is dirty. Sometimes we may need to compute
 * and generate fields by programming.
 * */

public class Item {
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private double distance;
	
	/*
	 * This is a builder pattern in Java
	 * */
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
	}
	
	/*
	 * This function is to convert an Item object into a
	 * JSONObject instance because in our application, front-end
	 * code cannot understand Java class, it can only understand
	 * JSON.
	 * */
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories));
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	// getters
	public String getItemId() {
		return itemId;
	}
	
	public String getName() {
		return name;
	}
	
	public double getRating() {
		return rating;
	}
	
	public String getAddress() {
		return address;
	}
	
	public Set<String> getCategories() {
		return categories;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public String getUrl() {
		return url;
	}
	
	public double getDistance() {
		return distance;
	}
	/*
	 * Question: Could you guarantee that TicketMaster can 
	 * return all data fields to us every time? If it returns 
	 * null for some data field, how could your constructor 
	 * deal with that?
	 * */
	
	/*
	 * Builder pattern builds a complex object using simple 
	 * objects and using a step by step approach. It separates 
	 * the construction of a complex object from its 
	 * representation so that the same construction process 
	 * can create different representations. We can also make 
	 * the object to build immutable.
	 * */
	public static class ItemBuilder {
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
		
		// setters
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}


		public void setName(String name) {
			this.name = name;
		}


		public void setRating(double rating) {
			this.rating = rating;
		}


		public void setAddress(String address) {
			this.address = address;
		}


		public void setCategories(Set<String> categories) {
			this.categories = categories;
		}


		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}


		public void setUrl(String url) {
			this.url = url;
		}


		public void setDistance(double distance) {
			this.distance = distance;
		}
		
		public Item build() {
			return new Item(this);
		}
	}
}
