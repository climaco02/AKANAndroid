/*
 * File: QuotaListAdapter.java Purpose: Brings the implementation of class
 * QuotaListAdapter.
 */
package br.com.visualize.akan.domain.adapters;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.visualize.akan.R;
import br.com.visualize.akan.domain.model.Quota;


/**
 * Represents a list of congressman adapting its entirety a particular form
 * described by the class.
 */
@SuppressLint( { "InflateParams", "ViewHolder" } )
public class DescriptionGridAdapter extends ArrayAdapter<Quota> {
	
	private final Context context;
	public List<Quota> quotas;
	private List<Quota> filteredList;
	private int layoutInflated;
	
	public DescriptionGridAdapter( Context context, int textViewResourceId,
	        List<Quota> quotasList ) {
		
		super( context, textViewResourceId, quotasList );
		
		this.layoutInflated = textViewResourceId;
		this.context = context;
		this.quotas = new ArrayList<Quota>();
		this.filteredList = new ArrayList<Quota>();
		
		quotas.addAll( quotasList );
		filteredList.addAll( quotas );
		
		getFilter();
	}
	
	/**
	 * Sets a view that will associated the list generated by the Adapter, as
	 * the parameters passed, and returns.
	 */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		Quota quota = quotas.get( position );
		String nameQuota = quota.getTypeQuota().getRepresentativeNameQuota();
		
		int bkgId = context.getResources().getIdentifier( "background_"+nameQuota, "drawable",
		        context.getPackageName() );
		int btnId = context.getResources().getIdentifier( "quota_"+nameQuota, "drawable",
		        context.getPackageName() );
		int barId = context.getResources().getIdentifier( "level_"+nameQuota, "drawable",
		        context.getPackageName() );
		
		LayoutInflater inflater = (LayoutInflater)context
		        .getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		View view = inflater.inflate( layoutInflated, null );
		
		ImageView bkgQuota = (ImageView) view.findViewById(R.id.bkg_quota_imageview);
		bkgQuota.setBackgroundResource(bkgId);
		ImageView btnQuota = (ImageView) view.findViewById(R.id.btn_quota_imageview);
		btnQuota.setBackgroundResource(btnId);
		ImageView barQuota = (ImageView) view.findViewById(R.id.bar_quota_imageview);
		barQuota.setBackgroundResource(barId);
		TextView valueQuota = (TextView) view.findViewById(R.id.quota_text);
		valueQuota.setText(quota.getValueQuota()+"");
		
		return view;
	}
	
	/**
	 * Returns the list item requested.
	 * 
	 * @param position
	 *            Position of the item to be returned.
	 * 
	 * @return Quota associated with position.
	 */
	@Override
	public Quota getItem( int position ) {
		return quotas.get( position );
	}
	
	/**
	 * Returns the number of elements associated with the list.
	 * 
	 * @return Number of elements.
	 */
	@Override
	public int getCount() {
		return quotas.size();
	}
	
	@Override
	public boolean isEnabled( int position ) {
		return true;
	}
}
