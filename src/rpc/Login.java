package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
        try {
            JSONObject object = new JSONObject();
            // Get current HttpSession associated with this request but do not create a new one
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.setStatus(403); // Forbidden error
                object.put("status", "Session Invalid");
            } else {
                String userId = (String) session.getAttribute("user_id");
                String name = connection.getFullName(userId);
                object.put("status", "OK");
                object.put("user_id", userId);
                object.put("name", name);
            }
            RpcHelper.writeJSONObject(response, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
        try {
            JSONObject body = RpcHelper.parseHttpRequestBody(request);
            String userId = body.getString("user_id");
            String password = body.getString("password");

            JSONObject object = new JSONObject();

            if (connection.verifyLogin(userId, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("user_id", userId);
                // Set session to expire in 10 minutes
                session.setMaxInactiveInterval(10 * 60);
                String name = connection.getFullName(userId);
                object.put("status", "OK");
                object.put("user_id", userId);
                object.put("name", name);
            } else {
                response.setStatus(401); // Unauthorized error
            }
            RpcHelper.writeJSONObject(response, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

}
