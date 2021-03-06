/*
 * File: CongressmanListAdapter.java Purpose: Brings the implementation of class
 * CongressmanListAdapter.
 */
package br.com.visualize.akan.domain.adapters;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import br.com.visualize.akan.R;
import br.com.visualize.akan.api.helper.RoundedImageView;
import br.com.visualize.akan.domain.model.Congressman;

import com.squareup.picasso.Picasso;


/**
 * Represents a list of congressman adapting its entirety a particular form
 * described by the class.
 */
@SuppressLint( { "InflateParams", "ViewHolder" } )
public class CongressmenListAdapter extends ArrayAdapter<Congressman> implements
        Filterable {
	
	private final Context context;
	private List<Congressman> congressmens;
	private List<Congressman> filteredList;
	private CongressmanFilter congressmanFilter;
	private int layoutInflated;
	
	public CongressmenListAdapter( Context context, int textViewResourceId,
	        List<Congressman> congressmensList ) {
		
		super( context, textViewResourceId, congressmensList );
		
		this.layoutInflated = textViewResourceId;
		this.context = context;
		this.congressmens = new ArrayList<Congressman>();
		this.filteredList = new ArrayList<Congressman>();
		
		congressmens.addAll( congressmensList );
		filteredList.addAll( congressmens );
		
		getFilter();
	}
	
	/**
	 * Sets a view that will associated the list generated by the Adapter, as
	 * the parameters passed, and returns.
	 */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		String idCongressman = Integer.toString( congressmens.get( position )
		        .getIdCongressman() );
		
		final String URL_PHOTOS = "http://www.camara.gov.br/internet/deputado/bandep/";
		
		LayoutInflater inflater = (LayoutInflater)context
		        .getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		View view = inflater.inflate( layoutInflated, null );
		
		TextView textViewName = (TextView)view
		        .findViewById( R.id.ranking_layout_txt_congressman_name );

		String name = congressmens.get( position ).getNameCongressman()
				.replace("PROFESSOR", "PROF.");
		name = name.replace("PROF.A", "PROF.");
		textViewName.setText( name );
		
		TextView textViewParty = (TextView)view
		        .findViewById( R.id.ranking_layout_txt_congressman_party );
		
		textViewParty.setText( congressmens.get( position )
		        .getPartyCongressman() );
		
		TextView textViewUf = (TextView)view.findViewById( R.id.txt_home_state );
		
		textViewUf.setText( congressmens.get( position ).getUfCongressman() );
		
		RoundedImageView congressmanImage = (RoundedImageView)view
		        .findViewById( R.id.ranking_layout_congressman_photo );
		
		Picasso.with( context ).load( URL_PHOTOS + idCongressman + ".jpg" )
				.placeholder(R.drawable.default_photo)
		        .error( R.drawable.default_photo ).into( congressmanImage );
		
		Button followCongressman = (Button)view
		        .findViewById( R.id.list_btn_follow );
		
		followCongressman.setTag( congressmens.get( position ) );
		
		if( congressmens.get( position ).isStatusCogressman() ) {
			followCongressman.setBackgroundResource( R.drawable.following );
			
		} else {
			followCongressman.setBackgroundResource( R.drawable.not_following );
		}
		
		if( layoutInflated == R.layout.ranking_layout ) {
			TextView textViewValue = (TextView)view
			        .findViewById( R.id.ranking_layout_txt_value );
			
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
			DecimalFormat decimalFormat = (DecimalFormat)nf;
			
			textViewValue.setText( "R$ "
			        + decimalFormat.format( congressmens.get( position )
			                .getTotalSpentCongressman() ) );
			
			TextView textViewPosition = (TextView)view
			        .findViewById( R.id.ranking_layout_position );
			
			textViewPosition.setText( Integer.toString( congressmens.get(
			        position ).getRankingCongressman() ) );
		}
		
		return view;
	}
	
	/**
	 * Returns the list item requested.
	 * 
	 * @param position
	 *            Position of the item to be returned.
	 * 
	 * @return Congressman associated with position.
	 */
	@Override
	public Congressman getItem( int position ) {
		return congressmens.get( position );
	}
	
	/**
	 * Returns the number of elements associated with the list.
	 * 
	 * @return Number of elements.
	 */
	@Override
	public int getCount() {
		return congressmens.size();
	}
	
	@Override
	public boolean isEnabled( int position ) {
		return true;
	}
	
	/**
	 * Sets a new filter based on CongressmanFilter class, for Adapter.
	 * <p>
	 * 
	 * @return new filter, based on CongressmanFilter.
	 */
	@Override
	public Filter getFilter() {
		
		if( congressmanFilter != null ) {
			/* ! Nothing To Do. */
			
		} else {
			congressmanFilter = new CongressmanFilter();
		}
		
		return congressmanFilter;
	}
	
	public void setCongressmanList(List<Congressman> list){
		congressmens = list;
		filteredList = list;
		notifyDataSetChanged();
	}
	
	public void setLayout(int id){
		layoutInflated = id;
		notifyDataSetChanged();
	}
	
	/*
	 * TODO: Review.
	 * 
	 * According to the code convention, a .java file should not have more than
	 * one class public.
	 */
	@SuppressLint( "DefaultLocale" )
	public class CongressmanFilter extends Filter {
		
		@Override
		protected FilterResults performFiltering( CharSequence constraint ) {
			
			constraint = constraint.toString().toLowerCase();
			FilterResults filterResults = new FilterResults();
			
			if( constraint != null && constraint.toString().length() > 0 ) {
				List<Congressman> filteredItems = new ArrayList<Congressman>();
				List<Congressman> currentItems = new ArrayList<Congressman>();
				
				currentItems.addAll( filteredList );
				
				for( int i = 0, l = currentItems.size(); i < l; i++ ) {
					Congressman m = currentItems.get( i );
					
					if( m.getNameCongressman().toLowerCase()
					        .contains( constraint ) ) {
						
						filteredItems.add( m );
						
					} else {
						/* ! Nothing To Do. */
					}
				}
				
				filterResults.count = filteredItems.size();
				filterResults.values = filteredItems;
				
			} else {
				synchronized( this ) {
					filterResults.values = filteredList;
					filterResults.count = filteredList.size();
				}
			}
			
			return filterResults;
		}
		
		@SuppressWarnings( "unchecked" )
		@Override
		protected void publishResults( CharSequence constraint,
		        FilterResults results ) {
			
			congressmens = (List<Congressman>)results.values;
			
			notifyDataSetChanged();
			notifyDataSetInvalidated();
		}
	}
}
