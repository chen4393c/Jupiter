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

import entity.Item;
import external.TicketMasterAPI;

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
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		// term can be empty
		String keyword = request.getParameter("term");
		
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		
		/*
		 * 1. Send HTTP GET request to get all JSONObject events 
		 * and purify the data into Item objects
		 * */
		List<Item> items = tmAPI.search(lat, lon, keyword);
		// 2. Convert the purified data back into JSONArray
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
