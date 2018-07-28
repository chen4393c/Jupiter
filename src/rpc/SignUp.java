package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class SignUp
 */
@WebServlet("/sign-up")
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUp() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
        try {
            JSONObject body = RpcHelper.parseHttpRequestBody(request);
            String userId = body.getString("user_id");

            JSONObject object = new JSONObject();

            if (connection.checkDuplicateUserId(userId)) {
                object.put("status", "Duplicate User Id");
            } else {
                String password = body.getString("password");
                String firstName = body.getString("first_name");
                String lastName = body.getString("last_name");

                connection.createNewUser(userId, password, firstName, lastName);

                object.put("status", "OK");
            }
            RpcHelper.writeJSONObject(response, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

}
