package com.apvisa.pay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class BankTypeAdapter extends AbsImgAdapterBase<String>
{
  private Context mContext;
  List<String> list;
  
  public BankTypeAdapter(Context context, List<String> mList)
  {
    super(context, mList);
    this.mContext = context;
    this.list = mList;
  }
  
  public View getView(int position, View convertView, ViewGroup parent)
  {
    ViewHolder viewHolder = null;
    if (convertView == null)
    {
      viewHolder = new ViewHolder(null);
      LayoutInflater mInflater = LayoutInflater.from(this.mContext);
      convertView = mInflater.inflate(getResId("layout", "ap_card_money_list_item"), null);
      viewHolder.card_money = ((TextView)convertView.findViewById(getResId("id","card_money")));
      convertView.setTag(viewHolder);
    }
    else
    {
      viewHolder = (ViewHolder)convertView.getTag();
    }
    setData(viewHolder, position);
    return convertView;
  }
  
  private void setData(ViewHolder holder, int position)
  {
    holder.card_money.setText((CharSequence)this.list.get(position));
  }
  
  public int getCount()
  {
    return this.list.size();
  }
  
  public Object getItem(int position)
  {
    return this.list.get(position);
  }
  
  private class ViewHolder
  {
    public TextView card_money;

	public ViewHolder(Object object) {
		// TODO Auto-generated constructor stub
	}
  }
	public int getResId(String group, String key) {
		return this.mContext.getResources().getIdentifier(key, group, this.mContext.getPackageName());
	}
}
