package external;

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
		return null;
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
}
