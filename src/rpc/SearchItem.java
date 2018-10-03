package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false); if (session == null) { response.setStatus(403); return; }
        String userId = session.getAttribute("user_id").toString(); // for displaying favorite in search
        
        double lat, lon;
        try {
            lat = Double.parseDouble(request.getParameter("lat"));
            lon = Double.parseDouble(request.getParameter("lon"));
        } catch (Exception e) {
            // Use default location if parse failed
            lat = 37.38;
            lon = -122.08;
        }

        // term can be empty
        String keyword = request.getParameter("term");
        // 1. Set up the db connection to save data
        DBConnection connection = DBConnectionFactory.getConnection();
        // 2. Send HTTP request to search with TicketMasterAPI, purify the data and save to db
        List<Item> items = connection.searchItems(lat, lon, keyword);

        // 3. Check user's favorite event list
        Set<String> favoriteItemIds = connection.getFavoriteItemIds(userId);

        // 4. Close the connection
        connection.close();
        // 5. Convert the purified data back into JSONArray
        JSONArray eventJSONArray = new JSONArray();

        try {
            for (Item item : items) {
                JSONObject eventJSONObject = item.toJSONObject();
                eventJSONObject.put("favorite", favoriteItemIds.contains(eventJSONObject.getString("item_id")));
                eventJSONArray.put(eventJSONObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RpcHelper.writeJSONArray(response, eventJSONArray);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
