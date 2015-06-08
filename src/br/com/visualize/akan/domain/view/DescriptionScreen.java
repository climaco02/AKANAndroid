/*
 * File: DescriptionScreen.java Purpose: Brings the implementation of
 * DescriptionScreen, a class that serves to interface with the user.
 */
package br.com.visualize.akan.domain.view;


import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ResponseHandler;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.visualize.akan.R;
import br.com.visualize.akan.api.helper.RoundedImageView;
import br.com.visualize.akan.api.request.HttpConnection;
import br.com.visualize.akan.domain.adapters.DescriptionGridAdapter;
import br.com.visualize.akan.domain.controller.CongressmanController;
import br.com.visualize.akan.domain.controller.QuotaController;
import br.com.visualize.akan.domain.controller.StatisticController;
import br.com.visualize.akan.domain.enumeration.SubQuota;
import br.com.visualize.akan.domain.exception.NullCongressmanException;
import br.com.visualize.akan.domain.model.Congressman;
import br.com.visualize.akan.domain.model.Quota;

import com.squareup.picasso.Picasso;


/**
 * Serves to bring the visibility of the detailed data of a congressman. Through
 * this class you can give the user a description of the expenses of the share
 * congressman.
 */
public class DescriptionScreen extends FragmentActivity implements
        OnDateSetListener {
	private static final int EMPTY_VALUE_QUOTA = 0; // Measured in Real.
	
	private final int WHITE = 0xffF1F1F2;
	private final int GREEN = 0xff00A69A;
	private final int GRAY = 0xff536571;
	private final int YELLOW = 0xffF3D171;
	private final int RED = 0xffF16068;
	
	private static final int BUTTON = 1;
	private static final int TEXT = 2;
	private static final int BAR = 3;
	private static final int BACKGROUND = 4;
	
	private Context context;
	private TextView referenceMonth;
	
	private QuotaController quotaController = null;
	private CongressmanController congressmanController;
	private Calendar calendar = Calendar.getInstance();
	private CustomDialog customDialog;
	
	private int year;
	private int month;
	private DescriptionGridAdapter gridAdapter;
	
	static List<Quota> quotas;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		this.context = getApplicationContext();
		
		setContentView( R.layout.description_screen_activity );

		congressmanController = CongressmanController
		        .getInstance( context );
		int idCongressman = congressmanController.getCongresman().getIdCongressman();
		quotaController = QuotaController.getInstance( context );

		year = calendar.get( Calendar.YEAR );
		month = calendar.get( Calendar.MONTH );
		
		quotas = quotaController.getQuotasByIdCongressman(idCongressman);
		//quotas = quotaController.getQuotaByDate(month, year, idCongressman);
		
		GridView gridview = (GridView) findViewById(R.id.quota_gridview);
	    gridAdapter = new DescriptionGridAdapter(getBaseContext(), R.layout.quota_grid_item, quotas);
		gridview.setAdapter(gridAdapter);

	    gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(DescriptionScreen.this, "" + position,
	                    Toast.LENGTH_SHORT).show();
			}
	    });
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		setDescriptionCongressman();
		
		if( !congressmanController.getCongresman().isStatusCogressman() ) {
			requestQuotas();
			
		} else {
			month = quotaController.initializeDateFromQuotas();
			setValuesQuotas();
		}
		
		referenceMonth = (TextView)findViewById( R.id.reference_month );
		
		setReferenceMonth();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if( !congressmanController.getCongresman().isStatusCogressman() ) {
			quotaController.deleteQuotasFromCongressman( congressmanController
			        .getCongresman().getIdCongressman() );
		} else {
			/* ! Nothing To Do. */
		}
		
		//resetSubQuotaAccordingType();
	}
	
	/**
	 * Get date chosen by user to filtered quotas
	 * 
	 * @param monthOfYear
	 *            month chosen by user
	 * @param year
	 *            year chosen by user
	 * @param datapicker
	 *            current datepicker
	 * 
	 */
	@Override
	public void onDateSet( DatePicker datapicker, int year, int monthOfYear,
	        int dayOfMonth ) {
		this.year = year;
		
		this.month = monthOfYear + 1;
		
		//resetSubQuotaAccordingType();
		
		setReferenceMonth();
		
		setValuesQuotas();
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		MenuInflater inflater = getMenuInflater();
		
		inflater.inflate( R.menu.menu, menu );
		
		return super.onCreateOptionsMenu( menu );
	}
	
	/**
	 * Sets the congressman-related data to be displayed in the description.
	 */
	private void setDescriptionCongressman() {
		Congressman congressman;
		
		congressman = congressmanController.getCongresman();
		String idCongressman = Integer
		        .toString( congressman.getIdCongressman() );
		String photoCongressmanUrl = "http://www.camara.gov.br/internet/deputado/bandep/";
		
		TextView textViewCongressmanName = (TextView)findViewById( R.id.description_txt_congressman_name );
		
		textViewCongressmanName.setText( congressman.getNameCongressman() );
		
		TextView textViewCongressmanParitdo = (TextView)findViewById( R.id.description_txt_congressman_party );
		
		textViewCongressmanParitdo.setText( congressman.getPartyCongressman() );
		
		TextView textViewRankingPosition = (TextView)findViewById( R.id.description_txt_ranking_position );
		
		textViewRankingPosition.setText( Integer.toString( congressman
		        .getRankingCongressman() ) );
		
		TextView textViewTopBarName = (TextView)findViewById( R.id.topbar_congressman );
		
		textViewTopBarName.setText( congressman.getNameCongressman() );
		
		RoundedImageView congressmanImage = (RoundedImageView)findViewById( R.id.description_congressman_photo );
		
		Picasso.with( context )
		        .load( photoCongressmanUrl + idCongressman + ".jpg" )
		        .placeholder(R.drawable.default_photo)
		        .error( R.drawable.default_photo ).into( congressmanImage );
		
		Button followCongressman = (Button)findViewById( R.id.description_btn_follow );
		TextView textViewFollow = (TextView)findViewById( R.id.description_txt_follow );
		
		if( congressmanController.getCongresman().isStatusCogressman() ) {
			followCongressman.setBackgroundResource( R.drawable.following );
			
			textViewFollow.setText( "Seguido" );
			textViewFollow.setTextColor( Color.parseColor( "#008e8e" ) );
			
		} else {
			followCongressman.setBackgroundResource( R.drawable.not_following );
			
			textViewFollow.setText( "Seguir" );
			textViewFollow.setTextColor( Color.parseColor( "#536571" ) );
			
		}
	}
	
	/**
	 * Makes a request to the server requesting the data relating to shares
	 * related to the specified congressman.
	 */
	private void requestQuotas() {
		final ProgressDialog progress = new ProgressDialog( this );
		
		progress.setMessage( "Carregando dados..." );
		progress.show();
		
		new Thread() {
			
			public void run() {
				
				Looper.prepare();
				
				try {
					ResponseHandler<String> responseHandler = HttpConnection
					        .getResponseHandler();
					
					quotaController.getQuotaById( congressmanController
					        .getCongresman().getIdCongressman(),
					        responseHandler );
					
				} catch( Exception e ) {
					/* ! TODO: Handle Exception. */
				}
				
				runOnUiThread( new Runnable() {
					
					@Override
					public void run() {
						progress.setMessage( "Dados carregados" );
						
						month = quotaController.initializeDateFromQuotas();
						
						setReferenceMonth();
						
						setValuesQuotas();
						
						progress.dismiss();
						
						Looper.loop();
						
					}
				} );
			}
			
		}.start();
	}
	
	/**
	 * Sets the value of each parliamentary quota regarding the Congressman.
	 * 
	 * @param idCongressman
	 *            Numeric identifier of congressman that must have deleted the
	 *            quotas.
	 */
	public void setValuesQuotas() {
		
		int idCongressman = congressmanController.getCongresman().getIdCongressman();
		quotas = quotaController.getQuotaByDate( month, year, idCongressman);
		
		gridAdapter.quotas = quotas;
		
		gridAdapter.notifyDataSetChanged();

	}
	
	/**
	 * Assigns the correct sub-quota values as well as the size of the graph bar
	 * indicative of spending, according to the type associated with it.
	 * 
	 * @param typeSubQuota
	 *            The type of sub-quota.
	 * @param quota
	 *            Amount spent associated with sub-quota.
	 */
	private void setSubQuotaAccordingType( SubQuota typeSubQuota, Quota quota ) {
		String type = "";
		
		switch( typeSubQuota ) {
		
			case ACCOMMODATION:
				type = SubQuota.ACCOMMODATION.getRepresentativeNameQuota();
				
				break;
			
			case AIR_FREIGHT:
				type = SubQuota.AIR_FREIGHT.getRepresentativeNameQuota();
				
				break;
			
			case ALIMENTATION:
				type = SubQuota.ALIMENTATION.getRepresentativeNameQuota();
				
				break;
			
			case DISCLOSURE_PARLIAMENTARY_ACTIVITY:
				type = SubQuota.DISCLOSURE_PARLIAMENTARY_ACTIVITY
				        .getRepresentativeNameQuota();
				
				break;
			
			case FUEL:
				type = SubQuota.FUEL.getRepresentativeNameQuota();
				
				break;
			
			case ISSUANCE_OF_AIR_TICKETS:
				type = SubQuota.ISSUANCE_OF_AIR_TICKETS
				        .getRepresentativeNameQuota();
				
				break;
			
			case LEASE_OF_VEHICLES:
				/* ! Write Instructions Here. */
				break;
			
			case OFFICE:
				type = SubQuota.OFFICE.getRepresentativeNameQuota();
				
				break;
			
			case POSTAL_SERVICES:
				type = SubQuota.POSTAL_SERVICES.getRepresentativeNameQuota();
				
				break;
			
			case SAFETY:
				type = SubQuota.SAFETY.getRepresentativeNameQuota();
				
				break;
			
			case SIGNATURE_OF_PUBLICATION:
				/* ! Write Instructions Here. */
				break;
			
			case TECHNICAL_WORK_AND_CONSULTING:
				type = SubQuota.TECHNICAL_WORK_AND_CONSULTING
				        .getRepresentativeNameQuota();
				
				break;
			
			case TELEPHONY:
				type = SubQuota.TELEPHONY.getRepresentativeNameQuota();
				
				break;
			
			default:
				/* ! Nothing To Do. */
		}
		
		//setDetailsQuota( type, quota );
	}
	
	/**
	 * Sets the information of a quota that will be presented, either
	 * graphically or numerically.
	 * 
	 * @param quotaName
	 *            Name of the quota. Should be given only with lowercase letters
	 *            and spaced names with underscore.
	 * @param valueQuota
	 *            Amount spent associated with sub-quota
	 */
	private void setDetailsQuota( String quotaName, Quota quota ) {
		int idBackgroundResource = getResourceID( BACKGROUND, quotaName );
		int idImageResource = getResourceID( BUTTON, quotaName );
		int idTextResource = getResourceID( TEXT, quotaName );
		int idBarResource = getResourceID( BAR, quotaName );
		
		ImageView backgroundQuota = (ImageView)findViewById( idBackgroundResource );
		ImageView imageQuota = (ImageView)findViewById( idImageResource );
		ImageView barQuota = (ImageView)findViewById( idBarResource );
		TextView txtQuota = (TextView)findViewById( idTextResource );
		
		setBackgroundQuota( backgroundQuota, quota );
		setImageQuota( imageQuota, quotaName );
		setBarQuota( barQuota, quota );
		setTextValueQuota( txtQuota, quota );
	}
	
	/**
	 * Sets the picture that represents the background of the quota.
	 * 
	 * @param background
	 *            ImagemView representing the background of the quota.
	 * @param quota
	 *            Name of the quota. Should be given only with lowercase letters
	 *            and spaced names with underscore.
	 */
	private void setBackgroundQuota( ImageView background, Quota quota ) {
		int[ ] colors = selectImageColor( exponentialProbability( quota ) );
		
		//animateBackgroundColor( background, colors );
	}
	
	/**
	 * Sets the picture that represents on the screen a quota.
	 * 
	 * @param image
	 *            ImagemView representing the the quota.
	 * @param quota
	 *            Name of the quota. Should be given only with lowercase letters
	 *            and spaced names with underscore.
	 */
	private void setImageQuota( ImageView image, String quota ) {
		/* ! Write Instructions Here. */
	}
	
	/**
	 * Sets the picture that represents on the screen the spending level of a
	 * quota.
	 * 
	 * @param bar
	 *            ImagemView graphically representing the speinding on a quota.
	 * @param valueQuota
	 *            Amount spent associated with sub-quota.
	 */
	private void setBarQuota( ImageView bar, Quota quota ) {
		double percent = exponentialProbability( quota );
		
		int[ ] colors = selectImageColor( percent );
		
		//animateBarColor( bar, colors );
		//animateBarHeight( bar, percent );
	}
	
	/**
	 * Sets the text that represents on the screen the spending level of a
	 * quota.
	 * 
	 * @param text
	 *            TextView numerically representing the spending on a quota.
	 * @param valueQuota
	 *            Amount spent associated with sub-quota.
	 */
	private void setTextValueQuota( TextView text, Quota quota ) {
		DecimalFormat valueQuotaFormat = new DecimalFormat( "#,###.00" );
		
		if( quota != null ) {
			
			text.setText( valueQuotaFormat.format( quota.getValueQuota() ) );
			
			//animateTextMove( text );
			
		} else {
			text.setText( valueQuotaFormat.format( EMPTY_VALUE_QUOTA ) );
		}
	}
	
	/**
	 * Calculates the level of the corresponding spending bar to the amount
	 * actually spent in relation to the average of congressmen.
	 * 
	 * @return level of bar corresponding to the amount spent.
	 */
	private double exponentialProbability( Quota quota ) {
		double lambda = 1 / quota.getStatisticQuota().getAverage();
		
		double result = 1 - Math.exp( -lambda * quota.getValueQuota() );
		
		return result;
	}
	
	/**
	 * Change the color of a ImageView.
	 * 
	 * @param view
	 *            The interface feature, of the type ImageView, which must have
	 *            changed color.
	 * @param percent
	 *            Value representing the share of spending level.
	 */
	private int[ ] selectImageColor( double percent ) {
		int[ ] colors = new int[ 5 ];
		
		if( 0.0 < percent && percent <= 0.25 ) {
			colors[ 0 ] = WHITE;
			colors[ 1 ] = GREEN;
			colors[ 2 ] = GREEN;
			colors[ 3 ] = GREEN;
			colors[ 4 ] = GREEN;
			
		} else if( 0.25 < percent && percent <= 0.5 ) {
			colors[ 0 ] = WHITE;
			colors[ 1 ] = GREEN;
			colors[ 2 ] = GRAY;
			colors[ 3 ] = GRAY;
			colors[ 4 ] = GRAY;
			
		} else if( 0.5 < percent && percent <= 0.75 ) {
			colors[ 0 ] = WHITE;
			colors[ 1 ] = GREEN;
			colors[ 2 ] = GRAY;
			colors[ 3 ] = YELLOW;
			colors[ 4 ] = YELLOW;
			
		} else if( 0.75 < percent && percent <= 1.0 ) {
			colors[ 0 ] = WHITE;
			colors[ 1 ] = GREEN;
			colors[ 2 ] = GRAY;
			colors[ 3 ] = YELLOW;
			colors[ 4 ] = RED;
			
		} else {
			/* ! Nothing To Do. */
		}
		
		return colors;
	}
	
	private void animateBackgroundColor( ImageView image, int[ ] colors ) {
		
		ValueAnimator colorAnimator = ObjectAnimator.ofInt( image,
		        "backgroundColor", colors );
		
		colorAnimator.setDuration( 1000 );
		colorAnimator.setEvaluator( new ArgbEvaluator() );
		colorAnimator.setInterpolator( new DecelerateInterpolator() );
		
		colorAnimator.start();
	}
	
	private void animateBarColor( ImageView bar, int[ ] colors ) {
		ValueAnimator colorAnimator = ObjectAnimator.ofInt( bar, "colorFilter",
		        colors );
		
		colorAnimator.setDuration( 1000 );
		colorAnimator.setEvaluator( new ArgbEvaluator() );
		colorAnimator.setInterpolator( new DecelerateInterpolator() );
		
		colorAnimator.start();
	}
	
	private void animateBarHeight( ImageView bar, double newHeight ) {
		
		/*
		 * The Mathematical Expression:
		 * 
		 * ( newHeight * 1000000 )/ 100 = newHeight * 10000 
		 * 
		 * Serves to maintain the proportionality between the last decimal value
		 * and the total possible for the size of a ImagemView. A ImageView may
		 * have a level value between 0 and 10000, where 10000 corresponds to
		 * 100%.
		 * 
		 * Becomes the last decimal value for percentage and then does the
		 * proportion between this value and the equivalent value in the range
		 * 0-10000.
		 */
		
		int height = (int)( newHeight * 10000 );
		
		Drawable level = bar.getDrawable();
		
		ValueAnimator heightAnimator = ObjectAnimator.ofInt( level, "level",
		        height );
		
		heightAnimator.setDuration( 1000 );
		heightAnimator.setInterpolator( new DecelerateInterpolator() );
		
		heightAnimator.start();
	}
	
	private void animateTextMove( TextView text ) {
		text.setAnimation( AnimationUtils.loadAnimation(
		        DescriptionScreen.this, android.R.anim.fade_in ) );
	}
	
	/**
	 * Return the ID of a resource by a string, that representing a resource.
	 * 
	 * @param typeResource
	 *            Reference to the resource type. Within the application can be
	 *            a button - BTN - or text - TXT.
	 * @param quota
	 *            Name of the quota. Should be given only with lowercase letters
	 *            and spaced names with underscore.
	 * 
	 * @return Resource identifier, an ID.
	 */
	private int getResourceID( int typeResource, String quota ) {
		final String RESOURCE_BKG = "bkg_quota_" + quota;
		final String RESOURCE_BTN = "btn_quota_" + quota;
		final String RESOURCE_TXT = "txt_quota_" + quota;
		final String RESOURCE_BAR = "bar_quota_" + quota;
		
		String resource = "";
		
		switch( typeResource ) {
			case BUTTON:
				resource = RESOURCE_BTN;
				break;
			
			case TEXT:
				resource = RESOURCE_TXT;
				break;
			
			case BAR:
				resource = RESOURCE_BAR;
				break;
			
			case BACKGROUND:
				resource = RESOURCE_BKG;
				break;
			
			default:
				/* ! Nothing To Do. */
		}
		
		int idResource = getResources().getIdentifier( resource, "id",
		        getPackageName() );
		
		return idResource;
	}
	
	/**
	 * Instantiates DatePickerFragment and show in screen
	 * 
	 * @param view
	 *            receives current view
	 */
	
	public void showDatePickerDialog( View view ) {
		DatePickerFragment newFragment = new DatePickerFragment();
		
		newFragment.show( getSupportFragmentManager(), "datePicker" );
	}
	
	/**
	 * Set up reference month text
	 */
	public void setReferenceMonth() {
		
		String monthText = getApplication().getResources().getStringArray(
		        R.array.month_names )[ month - 1 ];
		
		referenceMonth.setText( monthText + " de " + year );
	}
	
	/**
	 * Back to list of the congressmen
	 * 
	 * @param view
	 *            current View
	 */
	public void backToList( View view ) {
		this.finish();
	}
	
	public void onFollowedCongressman( View view ) {
		customDialog = new CustomDialog( this );
		int timeToDismis = 2000;
		
		customDialog.setMessage( "Parlamentar "
		        + congressmanController.getCongresman().getNameCongressman()
		        + " seguido" );
		
		if( congressmanController.getCongresman().isStatusCogressman() ) {
			
			congressmanController.getCongresman().setStatusCogressman( false );
			
			try {
				congressmanController.updateStatusCongressman();
				
			} catch( NullCongressmanException e ) {
				// TODO: Show a Alert about the exception
			}
			
			setDescriptionCongressman();
			
		} else {
			congressmanController.getCongresman().setStatusCogressman( true );
			
			try {
				congressmanController.updateStatusCongressman();
				
			} catch( NullCongressmanException e ) {
				// TODO: Show a Alert about the exception
			}
			
			setDescriptionCongressman();
			
			customDialog.getWindow().clearFlags(
			        WindowManager.LayoutParams.FLAG_DIM_BEHIND );
			customDialog.show();
			
			final Timer timer = new Timer();
			
			timer.schedule( new TimerTask() {
				
				@Override
				public void run() {
					customDialog.dismiss();
					timer.cancel();
				}
				
			}, timeToDismis );
		}
	}
}
