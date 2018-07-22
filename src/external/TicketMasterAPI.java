package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

/*
 * This class will help us send HTTP request to TicketMaster API and get response.
 * */
public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "3CKFnTIaH7euK22UZjAYNBEAQvHOGDIT";
	
	/*
	 * This function will actually send HTTP request and get response.
	 * */
	public List<Item> search(double lat, double lon, String keyword) {
		// Encode keyword in url since it may contain special characters
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Convert lat/lon to geo hash
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		
		/*
		 * Make your url query part like:
		 * "apikey=12345&geoPoint=abcd&keyword=music&radius=50"
		 * */
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", 
				API_KEY, geoHash, keyword, 50);
		
		try {
			// Open a HTTP connection between your Java application and TicketMaster based on url
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query)
					.openConnection();
			// Set request method to GET
			connection.setRequestMethod("GET");
			/*
			 * Send request to TicketMaster and get response, response code could be
			 * returned directly. Response body is saved in InputStream of connection.
			 * */
			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + URL + "?" + query);
			System.out.println("Response Code : " + responseCode);
			
			/*
			 * Now read response body to get events data. 
			 * BE CAREFUL to use UTF-8 char-set.
			 * Otherwise, you will get strange characters.
			 * */ 
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							connection.getInputStream(),
							"UTF-8"
					)
			);
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			// Handle response data in JSON format
			JSONObject obj = new JSONObject(response.toString());
			if (obj.isNull("_embedded")) {
				return new ArrayList<>();
			}
			JSONObject embedded = obj.getJSONObject("_embedded");
			JSONArray events = embedded.getJSONArray("events");
			return getItemList(events);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	/*
	 * sample response:
	 * {
	 * 		"_embedded": {
	 * 			"events": [
	 * 				{
	 * 					"name": "JAY-Z and BEYONCÉ - OTR II",
                		"type": "event",
                		"id": "vvG1iZ4ARirJkY",
                		"test": false,
                		"url": "https://www.ticketmaster.com/jayz-and-beyonce-otr-ii-pasadena-california-09-22-2018/event/0B00545BEC7A61B0",
                		"locale": "en-us",
                		"images": [..],
                		"distance": 1.46,
                		"units": "MILES",
                		"sales": {..},
                		"dates": {..},
                		"classifications": [..],
                		"promoters": [..],
                		"priceRanges": [..],
                		"seatmap": {..},
                		"ticketLimit": {..},
                		"_links": {..},
                		"_embedded": {
							"venues": [
								...,
								"address": {
									"line1": 1016 Washington Ave,
									"line2": ...,
									"line3": ...
								},
								...,
								"city": {
									"name": "Minneapolis"
								}
							],
							"attractions": [..]
						}
	 * 				},
	 * 				...
	 * 			]
	 * 		},
	 * 		"_links": {...},
	 * 		"page": {...}	
	 * }
	 * 	
	 * */
	
	/*
	 * Helper methods for parsing JSON data
	 * 
	 * "_embedded": {
			"venues": [
				...,
				"address": {
					"line1": 1016 Washington Ave,
					"line2": ...,
					"line3": ...
				},
				...,
				"city": {
					"name": "Minneapolis"
				}
			],
			"attractions": [..]
		}
	 * */
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				StringBuilder result = new StringBuilder();
				for (int i = 0; i < venues.length(); i++) {
					JSONObject venue = venues.getJSONObject(i);
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if (!address.isNull("line1")) {
							result.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							result.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							result.append(address.getString("line3"));
						}
						result.append(",");
					}
					
					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						if (!city.isNull("name")) {
							result.append(city.getString("name"));
						}
					}
					
					if (result.length() > 0) {
						return result.toString();
					}
				}
			}
		}
		return "";
	}
	
	// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray images = event.getJSONArray("images");
			for (int i = 0; i < images.length(); i++) {
				JSONObject image = images.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";
	}
	
	// {"classifications" : [{"segment": {"name": "music"}}, ...]}
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}

	// Convert JSONArray into Item objects
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			
			itemList.add(builder.build());
		}
		return itemList;
	}
	
	/*
	 * A print function to show JSON array returned from TicketMaster for debugging.
	 * */
	private void queryAPI(double lat, double lon) {
		List<Item> itemList = search(lat, lon, null);
		try {
			// Only test a single event
			for (Item item : itemList) {
				JSONObject event = item.toJSONObject();
				System.out.println(event);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Main entry for sample TicketMaster API requests.
	 * */
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		tmApi.queryAPI(37.38, -122.08);
	}
}
