package org.robotframework.databaserobotlibrary;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

@RobotKeywords
public class DatabaseKeywords {
	private static final int MAX_ROWS = 10000;
	private Connection con = null;
	private List<Map<String, String>> results;

	/* Constructor with possible initializers */
	public DatabaseKeywords() {
	}

	/* Public helper methods */

	@RobotKeyword("Connects to the given database url using the specified driver. " + "The username and password are optional. If not given, default values are used. "
			+ "Every new connection closes the previously opened connection.\n\n" + "Examples:\n"
			+ "| ConnectToDatabase | com.mysql.jdbc.Driver | jdbc:mysql://localhost/feedback?generateSimpleParameterMetadata=true | uname | pword |\n"
			+ "| ConnectToDatabase | com.mysql.jdbc.Driver | jdbc:mysql://localhost/feedback?generateSimpleParameterMetadata=true |\n")
	@ArgumentNames({ "driverClass", "url", "user=", "pass=" })
	public void connectToDatabase(String driverClass, String url, String user, String pass) throws Exception {
		Class.forName(driverClass);
		if (con != null) {
			con.close();
			con = null;
		}
		con = DriverManager.getConnection(url, user, pass);
		con.setAutoCommit(false);
	}

	@RobotKeyword("Closes the connection to the previously connected database.\n\n" + "Example:\n" + "| Disconnect |\n")
	public void disconnect() throws Exception {
		if (con != null) {
			con.close();
			con = null;
		}
	}

	/* Basic methods */

	@RobotKeyword("Executes the given SELECT command. " + "Before making calls to this keyword, the connection should be opened using the ConnectToDatabase keyword. "
			+ "The query results are stored into a class internal variable, " + "from where it can be printed out using the PrintQueryResultsToLog keyword. "
			+ "Verifications against query results can be done using the provided verification keywords.\n\n" + "Examples:\n"
			+ "| ExecuteSelect | SELECT * FROM COMMENTS WHERE comments = 'My fixed comment' AND datum = 2011-01-01 |\n"
			+ "| ExecuteSelect | SELECT day, night FROM COMMENTS WHERE myuser = lars AND datum = '2011-01-01' |\n")
	@ArgumentNames({ "query" })
	public void executeSelect(String query) throws Exception {
		PreparedStatement pstmt = doPrep(query);
		if (query.toUpperCase().startsWith("SELECT")) {
			saveResults(pstmt.executeQuery());
		}
		pstmt.close();
	}

	@RobotKeyword("Executes the given UPDATE command. " + "Before making calls to this keyword, the connection should be opened using the ConnectToDatabase keyword.\n\n" + "Examples:\n"
			+ "| ExecuteUpdate | UPDATE COMMENTS SET comments = 'fixed comment' WHERE myuser = nars AND summary = 'Summary' |\n"
			+ "| ExecuteUpdate | UPDATE COMMENTS SET comments = null WHERE myuser = 'nars' AND summary = Summary |\n")
	@ArgumentNames({ "update" })
	public int executeUpdate(String update) throws Exception {
		int affectedRows = 0;
		PreparedStatement pstmt = doPrep(update);
		if (update.toUpperCase().startsWith("UPDATE")) {
			affectedRows = pstmt.executeUpdate();
			con.commit();
		}
		pstmt.close();
		return affectedRows;
	}

	@RobotKeyword("Executes the given INSERT command. " + "Before making calls to this keyword, the connection should be opened using the ConnectToDatabase keyword.\n\n" + "Example:\n"
			+ "| ExecuteInsert | INSERT INTO COMMENTS values (default, 'lars', 'x@email.com','www.x.com', '2011-09-14 10:33:11', 12,'My comment') |\n")
	@ArgumentNames({ "insert" })
	public int executeInsert(String insert) throws Exception {
		int affectedRows = 0;
		PreparedStatement pstmt = doPrep(insert);
		if (insert.toUpperCase().startsWith("INSERT")) {
			affectedRows = pstmt.executeUpdate();
			con.commit();
		}
		pstmt.close();
		return affectedRows;
	}

	@RobotKeyword("Executes the given DELETE command. " + "Before making calls to this keyword, the connection should be opened using the ConnectToDatabase keyword.\n\n" + "Example:\n"
			+ "| ExecuteDelete | DELETE FROM COMMENTS WHERE myuser = lars AND summary='Sum' AND datum= '2011-09-14' |\n")
	@ArgumentNames({ "del" })
	public int executeDelete(String del) throws Exception {
		int affectedRows = 0;
		PreparedStatement pstmt = doPrep(del);
		if (del.toUpperCase().startsWith("DELETE")) {
			affectedRows = pstmt.executeUpdate();
			con.commit();
		}
		pstmt.close();
		return affectedRows;
	}

	@RobotKeyword("Returns if the result is empty or not. Returns 'Empty' or 'NotEmpty' \n\n" + "Example:\n" + "| IsTheResultEmpty |\n")
	public String isTheResultEmpty() throws Exception {
		if (this.results.size() > 0) {
			return "NotEmpty";
		} else {
			return "Empty";
		}
	}

	@RobotKeyword("Returns the text of the entry in the given (attention! index begins with 1 not 0 as e.g. in Java arrays) row and column.\n\n" + "Example:\n"
			+ "| GetResultItemsFromGivenRowAndColumn | 3 | SUMMARY |\n")
	@ArgumentNames({ "row", "columnName" })
	public String getResultItemsFromGivenRowAndColumn(String row, String columnName) throws Exception {
		int roww = Integer.parseInt(row) - 1;
		Assert.assertTrue("Row index out of bounds", this.results.size() > roww);
		return this.results.get(roww).get(columnName.toUpperCase());
	}

	@RobotKeyword("Returns the text of the entry in the given column and the first row.\n\n" + "Example:\n" + "| GetFirstResultItem | SUMMARY |\n")
	@ArgumentNames({ "columnName" })
	public String getFirstResultItem(String columnName) throws Exception {
		return getResultItemsFromGivenRowAndColumn("1", columnName);
	}

	@RobotKeyword("Prints the query results of the SELECT command into the log file.\n\n" + "Example:\n" + "| PrintQueryResultsToLog |\n")
	public void printQueryResultsToLog() {
		for (Map<String, String> mp : this.results) {
			System.out.println(mp);
		}
	}

	/* Extended methods */

	/* Private internal helper methods */

	private PreparedStatement doPrep(String query) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		PreparedStatement pstmt = con.prepareStatement(prepareQueryString(query));
		List<String> params = extractParameters(query);
		ParameterMetaData paramMetaData = pstmt.getParameterMetaData();
		for (int i = 1; i <= params.size(); i++) {
			String pName = params.get(i - 1);
			if (pName.toUpperCase().equals("NULL")) {
				pName = null;
			}
			int type = paramMetaData.getParameterType(i);
			switch (type) {
			case Types.BOOLEAN:
				pstmt.setBoolean(i, Boolean.valueOf(pName));
				break;
			case Types.DECIMAL:
				pstmt.setBigDecimal(i, new BigDecimal(pName));
				break;
			case Types.INTEGER:
			case Types.NUMERIC:
				pstmt.setInt(i, Integer.valueOf(pName));
				break;
			case Types.CHAR:
			case Types.VARCHAR:
				pstmt.setString(i, pName);
				break;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				pstmt.setTimestamp(i, new Timestamp(df.parse(pName).getTime()));
				break;
			default:
				break;
			}
		}
		return pstmt;
	}

	private String prepareQueryString(String query) {
		String q = query.replaceAll("(=\\s{1,})", "=").replaceAll("='.*?'", "= ?").replaceAll("=[^ ]{1,}", "= ?");
		System.out.println(q);
		return q;
	}

	private List<String> extractParameters(String query) {
		Pattern r = Pattern.compile("(='.*?'|=[^ ]{1,})");
		Matcher m = r.matcher(query.replaceAll("(=\\s{1,})", "="));
		List<String> params = new ArrayList<String>();
		while (m.find()) {
			String ss = m.group(1).trim();
			if (ss.startsWith("='")) {
				params.add(ss.substring(2, ss.length() - 1));
			} else {
				params.add(ss.substring(1, ss.length()));
			}
		}
		for (int i = 0; i < params.size(); i++) {
			System.out.println("Parameter " + (i + 1) + " = " + params.get(i));
		}
		return params;
	}

	private void saveResults(final ResultSet rs) throws Exception {
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		List<String> columnNameList = new ArrayList<String>();
		for (int i = 0; i < columnCount; i++) {
			columnNameList.add(md.getColumnName(i + 1).toUpperCase());
		}
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (int i = 0; rs.next() && i < MAX_ROWS; i++) {
			Map<String, String> map = new HashMap<String, String>();
			for (int j = 0; j < columnCount; j++) {
				String s = rs.getString(j + 1);
				if (rs.wasNull()) {
					map.put(columnNameList.get(j), "{NULL}");
				} else {
					map.put(columnNameList.get(j), s.trim());
				}
			}
			list.add(map);
		}
		this.results = list;
		rs.close();
	}

	protected void finalize() throws Throwable {
		try {
			if (con != null) {
				con.rollback();
				con.close();
			}
		} catch (Exception ex) {/* nothing to do */
		}
	}
}