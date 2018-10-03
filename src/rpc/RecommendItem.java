package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import algorithm.GeoRecommendation;
import entity.Item;

/**
 * Servlet implementation class RecommendItem
 */
@WebServlet("/recommendation")
public class RecommendItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendItem() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
        double lat, lon;
        try {
            lat = Double.parseDouble(request.getParameter("lat"));
            lon = Double.parseDouble(request.getParameter("lon"));
        } catch (NumberFormatException e) {
            // Use default location if parse failed
            lat = 37.38;
            lon = -122.08;
        }

        GeoRecommendation recommendation = new GeoRecommendation();
        List<Item> recommendedItems = recommendation.recommendItemIds(userId, lat, lon);

        JSONArray recommendedItemsJSONarray = new JSONArray();
        for (Item item : recommendedItems) {
            recommendedItemsJSONarray.put(item.toJSONObject());
        }
        RpcHelper.writeJSONArray(response, recommendedItemsJSONarray);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
