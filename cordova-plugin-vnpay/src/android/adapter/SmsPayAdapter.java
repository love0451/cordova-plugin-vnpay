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

public class SmsPayAdapter
  extends BaseAdapter
{
  private Context mContext;
  private LayoutInflater mInflater;
  List<PayMoney> list;
  AsyncImgLoader loader = new AsyncImgLoader();
  
  public SmsPayAdapter(Context context, List<PayMoney> list)
  {
    this.mContext = context;
    this.list = list;
    this.mInflater = LayoutInflater.from(this.mContext);
  }
  
  public View getView(int position, View convertView, ViewGroup parent)
  {
    ViewHolder holder;
    if (convertView == null)
    {
      holder = new ViewHolder(null);
      convertView = this.mInflater.inflate(getResId("layout", "ap_sms_gridview_item"), null);
      holder.money = ((TextView)convertView.findViewById(getResId("id", "money")));
      holder.amount = ((TextView)convertView.findViewById(getResId("id", "amount")));
      holder.imageView = ((ImageView)convertView.findViewById(getResId("id", "pay_img")));
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
    holder.money.setText("$" + ((PayMoney)this.list.get(position)).getMoney());
    holder.imageView.setImageBitmap(this.loader.loadImg(new AsyncImgLoader.OnImageListener()
    {
      public void onImageLoaded(Bitmap bit, String imageUrl) {}
    }, ((PayMoney)this.list.get(position)).getImg()));
    
    holder.amount.setText("x" + ((PayMoney)this.list.get(position)).getAmount());
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
	private TextView money;
    private ImageView imageView;
    private TextView amount;
  }
	public int getResId(String group, String key) {
		return this.mContext.getResources().getIdentifier(key, group, this.mContext.getPackageName());
	}
}

