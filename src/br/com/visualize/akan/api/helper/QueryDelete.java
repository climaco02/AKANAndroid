package br.com.visualize.akan.api.helper;

public class QueryDelete extends Query implements QueryStrategy {

	@Override
	public String doOperation(String tableName, String[] columnNames,
			String[] values, String columnReference, String valueReference,
			String valueComparison, String columnOrdered, String orderType) {

		StringBuilder query = new StringBuilder( "DELETE FROM" );
		
		query.append(BLANK);
		query.append(tableName);
		query.append(BLANK);
		
		query.append(WHERE);
		query.append(BLANK);
		query.append(columnReference);
		query.append(BLANK);
		query.append(EQUAL);
		query.append(BLANK);
		query.append(valueReference);
		
		return query.toString();
	}
}
