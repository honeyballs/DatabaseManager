package dbi_hausuebung;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

	private String db, username, pw;

	private Connection con = null;
	private Statement stmnt = null;
	private ResultSet rs = null;
	private boolean isConnected = false;

	public DBManager(String[] credentials) {

		db = credentials[0];
		username = credentials[1];
		pw = credentials[2];

	}

	public boolean startConnection() {

		isConnected = true;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(Controller.dbPath + db, username,
					pw);

		} catch (ClassNotFoundException e) {
			isConnected = false;
			e.printStackTrace();
		} catch (SQLException e) {
			isConnected = false;
			e.printStackTrace();
		}

		return isConnected;

	}

	public boolean isConnected() {
		return isConnected;
	}

	public Result executeQuery(String query) {

		Result result = null;

		if (con != null) {
			try {

				stmnt = con.createStatement();

				boolean select = stmnt.execute(query);

				if (select) {
					result = new Result(stmnt.getResultSet());
				} else {
					result = new Result(stmnt.getUpdateCount());
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public void disconnect() {

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}
}
