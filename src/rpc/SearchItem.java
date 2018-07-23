package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
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
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        // 3. Close the connection
        connection.close();
        // 4. Convert the purified data back into JSONArray
		JSONArray eventJSONArray = new JSONArray();
		for (Item item : items) {
			JSONObject eventJSONObject = item.toJSONObject();
			eventJSONArray.put(eventJSONObject);
		}
		
		RpcHelper.writeJSONArray(response, eventJSONArray);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
