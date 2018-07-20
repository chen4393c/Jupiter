package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

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
	public JSONArray search(double lat, double lon, String keyword) {
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
			
			// Now read response body to get events data
			BufferedReader in = new BufferedReader(new 
					InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			// Handle response data in JSON format
			JSONObject obj = new JSONObject(response.toString());
			if (obj.isNull("_embedded")) {
				return new JSONArray();
			}
			JSONObject embedded = obj.getJSONObject("_embedded");
			JSONArray events = embedded.getJSONArray("events");
			return events;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	
	/*
	 * A print function to show JSON array returned from TicketMaster for debugging.
	 * */
	private void queryAPI(double lat, double lon) {
		JSONArray events = search(lat, lon, null);
		try {
			for (int i = 0; i < events.length(); i++) {
				JSONObject event = events.getJSONObject(i);
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
