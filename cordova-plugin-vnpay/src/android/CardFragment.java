package com.apvisa.pay;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import cn.com.apvisa.ben.Bill;
import cn.com.apvisa.ben.PayInfo;
import cn.com.apvisa.ben.PayMoney;
import cn.com.apvisa.ben.PayType;
import com.apvisa.pay.CardFragment;
import com.apvisa.pay.adapter.PayMoneyAdapter;
import cn.com.apvisa.protocol.CardPayRun;
import cn.com.apvisa.protocol.CardPayRun.CardPayParser;
import cn.com.apvisa.protocol.PayInfoRun;
import cn.com.apvisa.protocol.PayInfoRun.PayInfoParser;
import cn.com.apvisa.protocolbase.HttpThread;
import cn.com.apvisa.protocolbase.HttpThread.HttpListener;
import cn.com.apvisa.util.MetaDataUtil;
import cn.com.apvisa.util.MyEPay;
import java.util.ArrayList;
import java.util.List;

public class CardFragment
  extends Fragment
{
  private ListView money_listview;
  private PayMoneyAdapter payMoneyAdapter;
  private List<PayMoney> moneylist;
  private Button buttonFirst;
  private EditText edit_pin;
  private EditText edit_serial;
  private ImageView viettel;
  private ImageView mobifone;
  private ImageView vinaphone;
  private String type = "";
  private LinearLayout loading;
  private String cp_serial;
  
  public static Fragment newInstance()
  {
    Fragment meFragment = new CardFragment();
    return meFragment;
  }
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
  }
  
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    View view = inflater.inflate(getResId("layout", "ap_pay"), container, false);
    init(view);
    return view;
  }
  
  public void init(View view)
  {
    this.cp_serial = getArguments().getString("cp_serial");
    this.loading = ((LinearLayout)view.findViewById(getResId("id", "loading")));
    this.moneylist = new ArrayList();
    this.money_listview = ((ListView)view.findViewById(getResId("id", "money_listview")));
    this.payMoneyAdapter = new PayMoneyAdapter(getActivity(), this.moneylist);
    this.money_listview.setAdapter(this.payMoneyAdapter);
    this.edit_pin = ((EditText)view.findViewById(getResId("id", "edit_pin")));
    this.edit_serial = ((EditText)view.findViewById(getResId("id", "edit_serial")));
    this.buttonFirst = ((Button)view.findViewById(getResId("id", "button_first")));
    
    this.viettel = ((ImageView)view.findViewById(getResId("id", "viettel")));
    this.viettel.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View v)
      {
        CardFragment.this.onViettel();
      }
    });
    this.mobifone = ((ImageView)view.findViewById(getResId("id", "mobifone")));
    this.mobifone.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View v)
      {
        CardFragment.this.onMobifone();
      }
    });
    this.vinaphone = ((ImageView)view.findViewById(getResId("id", "vinaphone")));
    this.vinaphone.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View v)
      {
        CardFragment.this.onVinaphone();
      }
    });
    this.buttonFirst.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View v)
      {
        String pin = CardFragment.this.edit_pin.getText().toString();
        String serial = CardFragment.this.edit_serial.getText().toString();
        if ((CardFragment.this.type == null) || (CardFragment.this.type.equals("")))
        {
          Toast.makeText(CardFragment.this.getActivity(), "type is null", 1).show();
          return;
        }
        if ((pin == null) || (pin.equals("")))
        {
          Toast.makeText(CardFragment.this.getActivity(), "pin is null", 1).show();
          return;
        }
        if ((serial == null) || (serial.equals("")))
        {
          Toast.makeText(CardFragment.this.getActivity(), "serial is null", 1).show();
          return;
        }
        CardFragment.this.loading.setVisibility(0);
        HttpThread.quickHttpRequest(1, 
          new CardPayRun(CardFragment.this.getActivity(), 
          MetaDataUtil.getMetaDataValue(CardFragment.this.getActivity(), "APVISA_ACCESSKEY", ""), pin, serial, CardFragment.this.type, CardFragment.this.cp_serial), 
          new HttpThread.HttpListener()
          {
            public void onHttpResult(int id, Object obj)
            {
              CardFragment.this.loading.setVisibility(8);
              CardPayRun.CardPayParser parser = (CardPayRun.CardPayParser)obj;
              if ((parser == null) || (!parser.isParserOk())) {
                return;
              }
              if (parser.getCode() == 0)
              {
                Bill bill = parser.getBill();
                if (bill.getResult().equals(Bill.RESULT_OF_SUCCESS))
                {
                  Toast.makeText(CardFragment.this.getActivity(), bill.getDescription(), 0).show();
                  Intent broad = new Intent();
                  broad.setAction(MyEPay.PAY_RESULT);
                  broad.putExtra(MyEPay.CODE_RESULT, String.valueOf(MyEPay.CODE_OF_SUCCESS));
                  broad.putExtra(MyEPay.SPSERIAL, CardFragment.this.cp_serial);
                  broad.putExtra(MyEPay.PAY_TYPE, String.valueOf(MyEPay.CARD_TYPE));
                  broad.putExtra(MyEPay.MONEY, String.valueOf(bill.getFee()));
                  broad.putExtra(MyEPay.AMOUNT, String.valueOf(bill.getAmount()));
                  CardFragment.this.getActivity().sendBroadcast(broad);
                  CardFragment.this.getActivity().finish();
                }
                else
                {
                  Toast.makeText(CardFragment.this.getActivity(), bill.getDescription(), 0).show();
                }
              }
              else
              {
                Toast.makeText(CardFragment.this.getActivity(), parser.getMsg(), 0).show();
              }
            }
          });
      }
    });
  }
  
  public void onViettel()
  {
    this.viettel.setImageResource(getResId("drawable", "ap_viettel"));
    this.mobifone.setImageResource(getResId("drawable", "ap_mobifone_bg"));
    this.vinaphone.setImageResource(getResId("drawable", "ap_vinaphone_bg"));
    this.type = "viettel";
  }
  
  public void onMobifone()
  {
    this.viettel.setImageResource(getResId("drawable", "ap_viettel_bg"));
    this.mobifone.setImageResource(getResId("drawable", "ap_mobifone"));
    this.vinaphone.setImageResource(getResId("drawable", "ap_vinaphone_bg"));
    this.type = "mobifone";
  }
  
  public void onVinaphone()
  {
    this.viettel.setImageResource(getResId("drawable", "ap_viettel_bg"));
    this.mobifone.setImageResource(getResId("drawable", "ap_mobifone_bg"));
    this.vinaphone.setImageResource(getResId("drawable", "ap_vinaphone"));
    this.type = "vinaphone";
  }
  
  private void request()
  {
    this.loading.setVisibility(0);
    HttpThread.quickHttpRequest(0, 
      new PayInfoRun(getActivity(), MetaDataUtil.getMetaDataValue(getActivity(), "APVISA_ACCESSKEY", "")), 
      new HttpThread.HttpListener()
      {
        public void onHttpResult(int id, Object obj)
        {
          PayInfoRun.PayInfoParser parser = (PayInfoRun.PayInfoParser)obj;
          if ((parser == null) || (!parser.isParserOk())) {
            return;
          }
          if (parser.getCode() == 0)
          {
            CardFragment.this.loading.setVisibility(8);
            PayInfo payInfo = parser.getPayInfo();
            for (PayType payType : payInfo.getType()) {
              if ((payType.getCode().equals(PayType.CODE_OF_VIETTEL)) && (payType.getStatus() == PayType.BUTTON_HIDDEN)) {
                CardFragment.this.viettel.setVisibility(8);
              } else if ((payType.getCode().equals(PayType.CODE_OF_MOBIFONE)) && (payType.getStatus() == PayType.BUTTON_HIDDEN)) {
                CardFragment.this.mobifone.setVisibility(8);
              } else if ((payType.getCode().equals(PayType.CODE_OF_VINAPHONE)) && (payType.getStatus() == PayType.BUTTON_HIDDEN)) {
                CardFragment.this.vinaphone.setVisibility(8);
              }
            }
            if (CardFragment.this.viettel.getVisibility() == 0) {
              CardFragment.this.onViettel();
            } else if (CardFragment.this.mobifone.getVisibility() == 0) {
              CardFragment.this.onMobifone();
            } else {
              CardFragment.this.onVinaphone();
            }
            CardFragment.this.handler.obtainMessage(0, payInfo).sendToTarget();
          }
          else
          {
            Toast.makeText(CardFragment.this.getActivity(), "����������", 0).show();
          }
        }
      });
  }
  
  private final int SPINNER_SUCCESS = 0;
  private Handler handler = new Handler()
  {
    public void handleMessage(Message msg)
    {
      switch (msg.what)
      {
      case 0: 
        List<String> list = new ArrayList();
        PayInfo payInfo = (PayInfo)msg.obj;
        List<PayType> type = payInfo.getType();
        for (PayType payType : type) {
          list.add(payType.getName());
        }
        CardFragment.this.moneylist.addAll(payInfo.getMoney());
        CardFragment.this.payMoneyAdapter.notifyDataSetChanged();
        break;
      }
    }
  };
  
  public void onResume()
  {
    super.onResume();
    request();
  }
	public int getResId(String group, String key) {
		return this.getActivity().getResources().getIdentifier(key, group, this.getActivity().getPackageName());
	}
}

