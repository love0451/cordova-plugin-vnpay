package com.apvisa.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.com.apvisa.ben.PayMoney;
import com.apvisa.pay.adapter.SmsPayAdapter;
import cn.com.apvisa.protocol.SMSPayInfoRun;
import cn.com.apvisa.protocol.SMSPayInfoRun.SMSPayInfoParser;
import cn.com.apvisa.protocolbase.HttpThread;
import cn.com.apvisa.protocolbase.HttpThread.HttpListener;
import cn.com.apvisa.util.MetaDataUtil;
import cn.com.apvisa.util.MyEPay;
import java.util.ArrayList;
import java.util.List;

public class SMSFragment
  extends Fragment
{
  private GridView sms_gridview;
  private SmsPayAdapter smsPayAdapter;
  private List<PayMoney> list;
  private LinearLayout loading;
  private PayMoney payMoney;
  private String cp_serial;
  
  public static Fragment newInstance()
  {
    Fragment meFragment = new SMSFragment();
    return meFragment;
  }
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
  }
  
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View view = inflater.inflate(getResId("layout", "ap_smspay"), container, false);
    init(view);
    request();
    return view;
  }
  
  public void init(View view)
  {
    this.cp_serial = getArguments().getString("cp_serial");
    this.sms_gridview = ((GridView)view.findViewById(getResId("id", "sms_gridview")));
    this.list = new ArrayList();
    this.smsPayAdapter = new SmsPayAdapter(getActivity(), this.list);
    this.sms_gridview.setAdapter(this.smsPayAdapter);
    this.loading = ((LinearLayout)view.findViewById(getResId("id", "loading")));
    this.sms_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
      {
        Integer status = Integer.valueOf(SMSFragment.this.getArguments().getInt("status"));
        SMSFragment.this.payMoney = ((PayMoney)SMSFragment.this.list.get(position));
        if ((status != null) && (status.intValue() == 0)) {
          SMSFragment.this.dialog();
        } else {
          SMSFragment.this.sendmsm(SMSFragment.this.payMoney.getPort(), SMSFragment.this.payMoney.getSms_content());
        }
      }
    });
  }
  
  private void request()
  {
    this.loading.setVisibility(0);
    HttpThread.quickHttpRequest(0, 
      new SMSPayInfoRun(getActivity(), MetaDataUtil.getMetaDataValue(getActivity(), "APVISA_ACCESSKEY", ""), 
      MetaDataUtil.getImsi(getActivity()), this.cp_serial), 
      new HttpThread.HttpListener()
      {
        public void onHttpResult(int id, Object obj)
        {
          SMSFragment.this.loading.setVisibility(8);
          SMSPayInfoRun.SMSPayInfoParser parser = (SMSPayInfoRun.SMSPayInfoParser)obj;
          if ((parser == null) || (!parser.isParserOk())) {
            return;
          }
          if (parser.getCode() == 0) {
            SMSFragment.this.handler.obtainMessage(0, parser.getList()).sendToTarget();
          } else {
            Toast.makeText(SMSFragment.this.getActivity(), parser.getMsg(), 0).show();
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
        SMSFragment.this.list.clear();
        SMSFragment.this.list.addAll((List)msg.obj);
        if (SMSFragment.this.list.size() < 5) {
          SMSFragment.this.sms_gridview.setNumColumns(3);
        } else {
          SMSFragment.this.sms_gridview.setNumColumns(5);
        }
        SMSFragment.this.smsPayAdapter.notifyDataSetChanged();
        break;
      }
    }
  };
  
  private void sendmsm(String phone, String message)
  {
    SmsManager sms = SmsManager.getDefault();
    sms.sendTextMessage(phone, null, message, null, null);
    this.loading.setVisibility(0);
    
    Intent broad = new Intent();
    broad.setAction(MyEPay.PAY_RESULT);
    broad.putExtra(MyEPay.CODE_RESULT, String.valueOf(MyEPay.CODE_OF_SUCCESS));
    broad.putExtra(MyEPay.SPSERIAL, this.cp_serial);
    broad.putExtra(MyEPay.PAY_TYPE, String.valueOf(MyEPay.SMS_TYPE));
    broad.putExtra(MyEPay.MONEY, String.valueOf(this.payMoney.getMoney()));
    broad.putExtra(MyEPay.AMOUNT, String.valueOf(this.payMoney.getAmount()));
    getActivity().sendBroadcast(broad);
    getActivity().finish();
  }
  
  protected void dialog()
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage("����������?");
    builder.setTitle("����");
    builder.setPositiveButton("����", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface dialog, int which)
      {
        SMSFragment.this.sendmsm(SMSFragment.this.payMoney.getPort(), SMSFragment.this.payMoney.getSms_content());
        dialog.dismiss();
      }
    });
    builder.setNegativeButton("����", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface dialog, int which)
      {
        dialog.dismiss();
      }
    });
    builder.create().show();
  }
  
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    if ((keyCode == 4) && (event.getRepeatCount() == 0)) {
      dialog();
    }
    return false;
  }
  
  public void onDestroy()
  {
    super.onDestroy();
  }
  
	public int getResId(String group, String key) {
		return this.getActivity().getResources().getIdentifier(key, group, this.getActivity().getPackageName());
	}
}

