package br.com.visualize.akan.domain.controller;

import java.util.Iterator;
import java.util.List;

import org.apache.http.client.ResponseHandler;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;
import br.com.visualize.akan.api.dao.StatisticDao;
import br.com.visualize.akan.api.helper.JsonHelper;
import br.com.visualize.akan.api.request.HttpConnection;
import br.com.visualize.akan.domain.exception.ConnectionFailedException;
import br.com.visualize.akan.domain.exception.NullCongressmanException;
import br.com.visualize.akan.domain.exception.NullStatisticException;
import br.com.visualize.akan.domain.model.Statistic;

public class StatisticController {
	private UrlController urlController;
	private static List<Statistic> statisticList;
	private static StatisticController instanceStatisticController = null;
	private StatisticDao statisticDao = null;
	
	private StatisticController( Context context ) {
		
		statisticDao = StatisticDao.getInstance( context );
		urlController = UrlController.getInstance( context );
	}
	
	/**
	 * Return the unique instance of QuotaDao active in the project.
	 * <p>
	 * @return The unique instance of QuotaDao.
	 */
	public static StatisticController getInstance( Context context ) {
		if( instanceStatisticController != null ) {
			/* !Nothing To Do. */
			
		} else {
			instanceStatisticController = new StatisticController( context );
		}
		
		return instanceStatisticController;
	}
	
	public double getMaxStatisticValue(List<Statistic> statistics){
		double maxValue = 0.0;
		Iterator<Statistic> i = statistics.iterator();
		while(i.hasNext()){
			Statistic statistic = i.next();
			maxValue= (statistic.getAverage()>maxValue)? statistic.getAverage() : maxValue;
		}
		return maxValue;
	}
	
	/* TODO: Write JAVADOC. */
	public void requestStatistics(ResponseHandler<String> responseHandler ) 
			throws NullCongressmanException, JSONException, 
			ConnectionFailedException, NullStatisticException {
			requestStatisticPerMonth(responseHandler);
			requestStatisticStdDeviation(responseHandler);
	}
		
	/* TODO: Write JAVADOC. */
	private void requestStatisticPerMonth(ResponseHandler<String> responseHandler ) 
			throws NullCongressmanException, JSONException, 
            ConnectionFailedException, NullStatisticException {
		
		String url = urlController.statisticsPerMonthUrl();
		
		String jsonStatisticList;
		if(statisticDao.checkEmptyLocalDb()){
			jsonStatisticList = HttpConnection.request( responseHandler, url );
			insertStatistics( JsonHelper.listStatisticsFromJSON(jsonStatisticList));
		}
	}
	
	/* TODO: Write JAVADOC. */
	private void requestStatisticStdDeviation(ResponseHandler<String> responseHandler ) 
			throws NullCongressmanException, JSONException, 
            ConnectionFailedException, NullStatisticException {
		
		String url = urlController.statisticsStdDeviationUrl();
		
		String jsonStatisticList;
		
		jsonStatisticList = HttpConnection.request( responseHandler, url );
		insertStatistics( JsonHelper.listStatisticsFromJSON(jsonStatisticList));
	}
	
	public Statistic getGeneralStatistic(int subquota){
		return statisticDao.getGeneralStatistic(subquota);
	}
	
	public List<Statistic> getStatisticByTypeAndYear(int subquota, int year){
		return statisticDao.getStatisticByYearAndType(year, subquota);
	}
	
	public static List<Statistic> getStatisticList() {
		return statisticList;
	}

	public static void setStatisticList(List<Statistic> statisticList) {
		StatisticController.statisticList = statisticList;
	}
	
	public void insertStatistics(List<Statistic> statisticList)
	        throws NullStatisticException {
		boolean result = statisticDao.insertStatisticsList(statisticList);
		Log.e("INSERT STATUS", "" + result);
	}

}
