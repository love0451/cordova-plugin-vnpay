package com.apvisa.pay.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import cn.com.apvisa.base.ParamsCheck;
import cn.com.apvisa.util.AsyncImgLoader;
import cn.com.apvisa.util.AsyncImgLoader.OnImageListener;
import java.util.List;

public abstract class AbsImgAdapterBase<E>
  extends AbsAdapterBase<E>
  implements AsyncImgLoader.OnImageListener
{
  protected AsyncImgLoader mAsyncLoader = null;
  protected Context mContext;
  protected LayoutInflater mInflater;
  protected Resources mResources;
  private static final int REFRESH_ID = 1;
  
  public AbsImgAdapterBase(Context context)
  {
    this.mContext = context;
    init(context);
  }
  
  private void init(Context context)
  {
    this.mInflater = LayoutInflater.from(context);
    this.mResources = context.getResources();
  }
  
  public AbsImgAdapterBase(Context context, List<E> mList)
  {
    super(mList);
    this.mContext = context;
    init(context);
  }
  
  private Handler handler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message msg)
    {
      if (msg.what == 1) {
        AbsImgAdapterBase.this.notifyDataSetChanged();
      }
    }
  };
  
  protected void refreshNotify()
  {
    if (!this.handler.hasMessages(1)) {
      this.handler.sendEmptyMessageDelayed(1, 100L);
    }
  }
  
  Bitmap bit = null;
  
  protected Bitmap getAsyncBitMap(String picUrl)
  {
    if (ParamsCheck.isNull(picUrl)) {
      return null;
    }
    Bitmap rtBit = this.mAsyncLoader.loadImg(picUrl);
    return rtBit;
  }
  
  public void onImageLoaded(Bitmap bit, String imageUrl)
  {
    if (bit != null) {
      refreshNotify();
    }
  }
  
  public void resumeImgThread()
  {
    refreshNotify();
  }
  
  public void pauseImgThread()
  {
    shutdownNow();
  }
  
  public void shutdownNow()
  {
    if (this.mAsyncLoader != null) {
      this.mAsyncLoader.shutdownNow();
    }
  }
}

