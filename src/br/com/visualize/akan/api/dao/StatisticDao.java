package br.com.visualize.akan.api.dao;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import br.com.visualize.akan.domain.exception.NullStatisticException;
import br.com.visualize.akan.domain.model.Statistic;


public class StatisticDao extends Dao{
	
	private static StatisticDao instanceStatisticDao = null;
	
	private static String tableName = "STATISTIC";
	private static String tableColumns[ ] = { "ID_STATISTIC", "MONTH_STATISTIC",
        "STD_DEVIATION", "AVERAGE_STATISTIC", "MAX_VALUE_STATISTIC", "YEAR_STATISTIC",
        "ID_SUBQUOTA" };
	
	private StatisticDao( Context context ) {
		StatisticDao.context = context;
	}
	
	/**
	 * Return the unique instance of statisticDao active in the project.
	 * 
	 * @return The unique instance of statisticDao.
	 */
	public static StatisticDao getInstance( Context context ) {
		if( instanceStatisticDao != null ) {
			/* !Nothing To Do. */
		} else {
			instanceStatisticDao = new StatisticDao( context );
		}
		return instanceStatisticDao;
	}
	
	public boolean insertStatisticsList( List<Statistic> insertedStatistics )
	    throws NullStatisticException {
		Iterator<Statistic> index = insertedStatistics.iterator();
		boolean result = true;
		while( index.hasNext() ) {
			result = insertStatistic( index.next() );
		}
		return result;
	}
	
	public boolean insertStatistic(Statistic statistic)
	        throws NullStatisticException {
	    if( statistic != null ) {
    		sqliteDatabase = database.getWritableDatabase();
    		ContentValues content = new ContentValues();
    		
    		//content.put( tableColumns[ 0 ], statistic.getIdStatistic());
    		content.put( tableColumns[ 1 ], statistic.getMonth()
    		        .getvalueMonth());
    		content.put( tableColumns[ 2 ], statistic.getStdDeviation());
    		content.put( tableColumns[ 3 ], statistic.getAverage());
    		content.put( tableColumns[ 4 ], statistic.getMaxValue());
    		content.put( tableColumns[ 5 ], statistic.getYear());
    		content.put( tableColumns[ 6 ], statistic.getSubquota()
    		        .getValueSubQuota());
		
		boolean result = ( insertAndClose( sqliteDatabase, tableName, content ) > 0 );
		return result;
	    } else {
	        throw new NullStatisticException();
	    }
	}
	
	public List<Statistic> getStatisticByYearAndType( int year, int subquota ) {
		sqliteDatabase = database.getReadableDatabase();
		Cursor cursor = sqliteDatabase.rawQuery(
		        "SELECT * FROM STATISTIC WHERE ID_SUBQUOTA=" + subquota 
		        + " AND YEAR_STATISTIC=" + year,
		        null );
		
		List<Statistic> listStatistic = new ArrayList<Statistic>();
		
		while( cursor.moveToNext() ) {
			Statistic statistic = new Statistic();
			//statistic.setIdStatistic( cursor.getInt( cursor.getColumnIndex( tableColumns[0] ) ) );
			statistic.setMonthByNumber( cursor.getInt( cursor.getColumnIndex( tableColumns[1] ) ) );
			statistic.setStdDeviation(cursor.getDouble(cursor.getColumnIndex(tableColumns[2])));
			statistic.setAverage(cursor.getDouble(cursor.getColumnIndex(tableColumns[3])));
			statistic.setMaxValue(cursor.getDouble(cursor.getColumnIndex(tableColumns[4])));
			statistic.setYear(cursor.getInt(cursor.getColumnIndex(tableColumns[5])));
			statistic.setSubquotaByNumber(cursor.getInt(cursor.getColumnIndex(tableColumns[6])));
			
			listStatistic.add( statistic );
			}
		return listStatistic;
	}
	
	public Statistic getGeneralStatistic(int subquota){
		List<Statistic> list = getStatisticByYearAndType(0, subquota);
		//return (list.size()>0)? list.get(0) : new Statistic();
		return list.get(0);
	}
	
	@Override
	public boolean checkEmptyLocalDb() {
sqliteDatabase = database.getReadableDatabase();
		
		String query = "SELECT 1 FROM " + tableName;
		
		Cursor cursor = sqliteDatabase.rawQuery( query, null );
		
		boolean isEmpty = NOT_EMPTY;
		
		if( cursor != null ) {
			
			if( cursor.getCount() <= 0 ) {
				cursor.moveToFirst();
				
				isEmpty = EMPTY;
				
			} else {
				/* ! Nothing To Do */
			}
			
		} else {
			isEmpty = EMPTY;
		}
		
		return isEmpty;
	}
}
