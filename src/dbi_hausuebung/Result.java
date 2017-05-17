package dbi_hausuebung;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Result {

	private List<String> columnNames = new ArrayList<>();
	private List<List<String>> data = new ArrayList<>();

	public Result(ResultSet resultSet) throws SQLException {

		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
			String columnName = resultSetMetaData.getColumnName(i);
			columnNames.add(columnName);
		}

		while (resultSet.next()) {
			List<String> row = new ArrayList<>();
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				String columnValue = resultSet.getString(resultSetMetaData
						.getColumnLabel(i));
				row.add(columnValue);
			}
			data.add(row);
		}
	}

	public Result(int updateCount) throws SQLException {

		columnNames.add("Information");
		columnNames.add("Value");
		List<String> row = new ArrayList<>();
		row.add("Nr. of updated rows");
		row.add(Integer.toString(updateCount));
		data.add(row);
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}
}
