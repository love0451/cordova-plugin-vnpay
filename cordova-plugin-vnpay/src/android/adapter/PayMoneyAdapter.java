package com.apvisa.pay.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.apvisa.ben.PayMoney;
import cn.com.apvisa.util.AsyncImgLoader;
import cn.com.apvisa.util.AsyncImgLoader.OnImageListener;
import java.util.List;

public class PayMoneyAdapter
  extends BaseAdapter
{
  List<PayMoney> list;
  private Context mContext;
  private LayoutInflater mInflater;
  private AsyncImgLoader loader = new AsyncImgLoader();
  
  public PayMoneyAdapter(Context context, List<PayMoney> list)
  {
    this.mContext = context;
    this.list = list;
    this.mInflater = ((LayoutInflater)this.mContext.getSystemService("layout_inflater"));
  }
  
  public View getView(int position, View convertView, ViewGroup parent)
  {
    ViewHolder holder;
    if (convertView == null)
    {
      holder = new ViewHolder(null);
      convertView = this.mInflater.inflate(getResId("layout", "ap_card_money_list_item"), null);
      holder.card_money = ((TextView)convertView.findViewById(getResId("id", "card_money")));
      holder.amount_num = ((TextView)convertView.findViewById(getResId("id", "amount_num")));
      holder.card_img = ((ImageView)convertView.findViewById(getResId("id", "card_img")));
      convertView.setTag(holder);
    }
    else
    {
      holder = (ViewHolder)convertView.getTag();
    }
    setData(holder, position);
    return convertView;
  }
  
  private void setData(ViewHolder holder, int position)
  {
    holder.card_money.setText("$" + ((PayMoney)this.list.get(position)).getMoney());
    holder.amount_num.setText("x" + ((PayMoney)this.list.get(position)).getAmount());
    holder.card_img.setImageBitmap(this.loader.loadImg(new AsyncImgLoader.OnImageListener()
    {
      public void onImageLoaded(Bitmap bit, String imageUrl) {}
    }, ((PayMoney)this.list.get(position)).getImg()));
  }
  
  public int getCount()
  {
    return this.list.size();
  }
  
  public Object getItem(int position)
  {
    return this.list.get(position);
  }
  
  public long getItemId(int position)
  {
    return position;
  }
  
  private static class ViewHolder
  {
    public ViewHolder(Object object) {
		// TODO Auto-generated constructor stub
	}
	private TextView card_money;
    private TextView amount_num;
    private ImageView card_img;
  }
	public int getResId(String group, String key) {
		return this.mContext.getResources().getIdentifier(key, group, this.mContext.getPackageName());
	}
}

