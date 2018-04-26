package com.apvisa.pay;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.com.apvisa.ben.PayMoney;
import com.apvisa.pay.BankFragment;
import com.apvisa.pay.adapter.SmsPayAdapter;
import cn.com.apvisa.protocol.BankPayInfoRun;
import cn.com.apvisa.protocol.BankPayInfoRun.BankPayInfoParser;
import cn.com.apvisa.protocolbase.HttpThread;
import cn.com.apvisa.protocolbase.HttpThread.HttpListener;
import cn.com.apvisa.util.MetaDataUtil;
import java.util.ArrayList;
import java.util.List;

public class BankFragment
  extends Fragment
{
  WebView mWebView;
  String cp_serial = "";
  private GridView sms_gridview;
  private SmsPayAdapter smsPayAdapter;
  private List<PayMoney> list;
  private LinearLayout loading;
  
  public static Fragment newInstance()
  {
    Fragment meFragment = new BankFragment();
    return meFragment;
  }
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
  }
  
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View view = inflater.inflate(getResId("layout", "ap_bank"), container, false);
    init(view);
    request();
    return view;
  }
  
  @SuppressLint({"SetJavaScriptEnabled"})
  public void init(View view)
  {
    this.cp_serial = getArguments().getString("cp_serial");
    this.sms_gridview = ((GridView)view.findViewById(getResId("id", "sms_gridview")));
    this.list = new ArrayList();
    this.smsPayAdapter = new SmsPayAdapter(getActivity(), this.list);
    this.sms_gridview.setAdapter(this.smsPayAdapter);
    this.sms_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
      {
        BankFragment.this.sms_gridview.setVisibility(4);
        BankFragment.this.mWebView.setVisibility(0);
        BankFragment.this.mWebView.loadUrl("http://www.vnpay.com.cn/api/bank/pay?access_key=" + 
          MetaDataUtil.getMetaDataValue(BankFragment.this.getActivity(), "APVISA_ACCESSKEY", "") + "&order_info=" + 
          BankFragment.this.cp_serial + "&cp_serial=" + BankFragment.this.cp_serial + "&money=" + ((PayMoney)BankFragment.this.list.get(position)).getMoney());
      }
    });
    this.loading = ((LinearLayout)view.findViewById(getResId("id", "loading")));
    this.mWebView = ((WebView)view.findViewById(getResId("id", "webView")));
    this.mWebView.getSettings().setDomStorageEnabled(true);
    this.mWebView.setWebViewClient(new WebViewClient()
    {
      public boolean shouldOverrideUrlLoading(WebView view, String url)
      {
        view.loadUrl(url);
        return true;
      }
      
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
      {
        handler.proceed();
      }
    });
    this.mWebView.getSettings().setJavaScriptEnabled(true);
    this.mWebView.getSettings().setDefaultTextEncodingName("gb2312");
    this.mWebView.getSettings().setSupportZoom(true);
    this.mWebView.getSettings().setBuiltInZoomControls(true);
    this.mWebView.getSettings().setUseWideViewPort(true);
    this.mWebView.getSettings().setCacheMode(-1);
    this.mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    this.mWebView.getSettings().setLoadWithOverviewMode(true);
    this.mWebView.getSettings().setAppCacheEnabled(true);
    this.mWebView.getSettings().setDomStorageEnabled(true);
  }
  
  private void request()
  {
    this.loading.setVisibility(0);
    HttpThread.quickHttpRequest(0, 
      new BankPayInfoRun(getActivity(), MetaDataUtil.getMetaDataValue(getActivity(), "APVISA_ACCESSKEY", "")), 
      new HttpThread.HttpListener()
      {
        public void onHttpResult(int id, Object obj)
        {
          BankFragment.this.loading.setVisibility(8);
          BankPayInfoRun.BankPayInfoParser parser = (BankPayInfoRun.BankPayInfoParser)obj;
          if ((parser == null) || (!parser.isParserOk())) {
            return;
          }
          if (parser.getCode() == 0) {
            BankFragment.this.handler.obtainMessage(0, parser.getList()).sendToTarget();
          } else {
            Toast.makeText(BankFragment.this.getActivity(), parser.getMsg(), 0).show();
          }
        }
      });
  }
  
  private final int SUCCESS = 0;
  private Handler handler = new Handler()
  {
    public void handleMessage(Message msg)
    {
      switch (msg.what)
      {
      case 0: 
        BankFragment.this.list.clear();
        BankFragment.this.list.addAll((List)msg.obj);
        if (BankFragment.this.list.size() < 5) {
          BankFragment.this.sms_gridview.setNumColumns(3);
        } else {
          BankFragment.this.sms_gridview.setNumColumns(5);
        }
        BankFragment.this.smsPayAdapter.notifyDataSetChanged();
        break;
      }
    }
  };
  
	public int getResId(String group, String key) {
		return getActivity().getApplication().getResources().getIdentifier(key, group, getActivity().getApplication().getPackageName());
	}
}
