package com.apvisa.cordova.vnpay;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import cn.com.apvisa.ben.Bill;
import cn.com.apvisa.ben.PayMethod;
import com.apvisa.pay.SMSFragment;
import cn.com.apvisa.protocol.CPSerialRun;
import cn.com.apvisa.protocol.CPSerialRun.CPSerialParser;
import cn.com.apvisa.protocol.PayMethodRun;
import cn.com.apvisa.protocol.PayMethodRun.PayMethodParser;
import cn.com.apvisa.protocolbase.HttpThread;
import cn.com.apvisa.protocolbase.HttpThread.HttpListener;
import cn.com.apvisa.util.MetaDataUtil;
import cn.com.apvisa.util.MyEPay;
import java.util.List;

import com.apvisa.pay.BankFragment;
import com.apvisa.pay.CardFragment;

public class VnpayActivity extends Activity
{
  private ImageView card;
  private ImageView sms;
  private ImageView bank;
  private ImageView close;
  private PayMethod sms_method;
  private String cp_serial;
  private String packageName;
  private int type;
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(1);
    getWindow().setFlags(1024, 1024);
    //setContentView(R.layout.ap_appay_main);
    this.packageName = getApplication().getPackageName();
    setContentView(getApplication().getResources().getIdentifier("ap_appay_main", "layout", this.packageName));
    Intent intent = getIntent();
    this.cp_serial = intent.getStringExtra("cp_serial");
    if ((this.cp_serial == null) || (this.cp_serial.equals("")))
    {
      Intent broad = new Intent();
      broad.setAction(MyEPay.PAY_RESULT);
      broad.putExtra(MyEPay.CODE_RESULT, MyEPay.CODE_OF_101);
      broad.putExtra(MyEPay.SPSERIAL, this.cp_serial);
      sendBroadcast(broad);
      finish();
    }
    init();
    request();
  }
  
  public void startCordovaActivityWithLayout(View view) {
      Intent intent = new Intent(this, VnpayActivity.class);
      startActivity(intent);
  }

  private void request()
  {
    HttpThread.quickHttpRequest(0, new PayMethodRun(this, 
      MetaDataUtil.getMetaDataValue(this, "APVISA_ACCESSKEY", ""), MetaDataUtil.getImsi(this)), 
      new HttpThread.HttpListener()
      {
        public void onHttpResult(int id, Object obj)
        {
          PayMethodRun.PayMethodParser parser = (PayMethodRun.PayMethodParser)obj;
          if ((parser == null) || (!parser.isParserOk())) {
            return;
          }
          if (parser.getCode() == 0)
          {
            List<PayMethod> infolist = parser.getInfolist();
            for (PayMethod payMethod : infolist)
            {
              if ((payMethod.getPay_code().equals(PayMethod.CODE_OF_SMS)) && 
                (payMethod.getPay_status().equals(PayMethod.BUTTON_HIDDEN))) {
            	  VnpayActivity.this.sms.setVisibility(8);
              } else if ((payMethod.getPay_code().equals(PayMethod.CODE_OF_CARD)) && 
                (payMethod.getPay_status().equals(PayMethod.BUTTON_HIDDEN))) {
            	  VnpayActivity.this.card.setVisibility(8);
              } else if ((payMethod.getPay_code().equals(PayMethod.CODE_OF_BANK)) && 
                (payMethod.getPay_status().equals(PayMethod.BUTTON_HIDDEN))) {
            	  VnpayActivity.this.bank.setVisibility(8);
              }
              if (payMethod.getPay_code().equals(PayMethod.CODE_OF_SMS)) {
            	  VnpayActivity.this.sms_method = payMethod;
              }
            }
            if (VnpayActivity.this.card.getVisibility() == 0) {
            	VnpayActivity.this.onButtonClick(1);
            } else if (VnpayActivity.this.sms.getVisibility() == 0) {
            	VnpayActivity.this.onButtonClick(2);
            } else {
            	VnpayActivity.this.onButtonClick(3);
            }
          }
          else
          {
            Toast.makeText(VnpayActivity.this, "����������", 0).show();
          }
        }
      });
  }
  
  private void init()
  {
    //this.card = ((ImageView)findViewById(R.id.card));
	this.card = ((ImageView)findViewById(getApplication().getResources().getIdentifier("card", "id", this.packageName)));
    this.card.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View view)
      {
    	  VnpayActivity.this.onButtonClick(1);
      }
    });
    //this.sms = ((ImageView)findViewById(R.id.sms));
    this.sms = ((ImageView)findViewById(getApplication().getResources().getIdentifier("sms", "id", this.packageName)));
    this.sms.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View view)
      {
    	  VnpayActivity.this.onButtonClick(2);
      }
    });
    //this.bank = ((ImageView)findViewById(R.id.bank));
    this.bank = ((ImageView)findViewById(getApplication().getResources().getIdentifier("bank", "id", this.packageName)));
    this.bank.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View view)
      {
    	  VnpayActivity.this.onButtonClick(3);
      }
    });
    //this.close = ((ImageView)findViewById(R.id.close));
    this.close = ((ImageView)findViewById(getApplication().getResources().getIdentifier("close", "id", this.packageName)));
    this.close.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View v)
      {
    	  VnpayActivity.this.goBack();
      }
    });
  }
  
  public void onButtonClick(int result)
  {
    Bundle bundle = new Bundle();
    switch (result)
    {
    case 1: 
      this.type = 1;
      Fragment fgcard = CardFragment.newInstance();
      //this.card.setImageResource(R.drawable.ap_cadr_bg);
      //this.bank.setImageResource(R.drawable.ap_selector_bank_image);
      //this.sms.setImageResource(R.drawable.ap_selector_sms_image);
      this.card.setImageResource(getApplication().getResources().getIdentifier("ap_cadr_bg", "drawable", this.packageName));
      this.bank.setImageResource(getApplication().getResources().getIdentifier("ap_selector_bank_image", "drawable", this.packageName));
      this.sms.setImageResource(getApplication().getResources().getIdentifier("ap_selector_sms_image", "drawable", this.packageName));
      bundle.putString("cp_serial", this.cp_serial);
      fgcard.setArguments(bundle);
      replaceFragment(fgcard);
      break;
    case 2: 
      this.type = 2;
      Integer status = Integer.valueOf(this.sms_method == null ? 1 : this.sms_method.getPay_second().intValue());
      Fragment fgsms = SMSFragment.newInstance();
      //this.card.setImageResource(R.drawable.ap_selector_card_image);
      //this.bank.setImageResource(R.drawable.ap_selector_bank_image);
      //this.sms.setImageResource(R.drawable.ap_sms_bg);
      this.card.setImageResource(getApplication().getResources().getIdentifier("ap_selector_card_image", "drawable", this.packageName));
      this.bank.setImageResource(getApplication().getResources().getIdentifier("ap_selector_bank_image", "drawable", this.packageName));
      this.sms.setImageResource(getApplication().getResources().getIdentifier("ap_sms_bg", "drawable", this.packageName));
      bundle.putString("cp_serial", this.cp_serial);
      bundle.putInt("status", status.intValue());
      fgsms.setArguments(bundle);
      replaceFragment(fgsms);
      break;
    case 3: 
      this.type = 3;
      Fragment fgbank = BankFragment.newInstance();
      //this.card.setImageResource(R.drawable.ap_selector_card_image);
      //this.bank.setImageResource(R.drawable.ap_bank_bg);
      //this.sms.setImageResource(R.drawable.ap_selector_sms_image);
      this.card.setImageResource(getApplication().getResources().getIdentifier("ap_selector_card_image", "drawable", this.packageName));
      this.bank.setImageResource(getApplication().getResources().getIdentifier("ap_bank_bg", "drawable", this.packageName));
      this.sms.setImageResource(getApplication().getResources().getIdentifier("ap_selector_sms_image", "drawable", this.packageName));
      bundle.putString("cp_serial", this.cp_serial);
      fgbank.setArguments(bundle);
      replaceFragment(fgbank);
    }
  }
  
  private void replaceFragment(Fragment newFragment)
  {
    FragmentTransaction trasection = getFragmentManager().beginTransaction();
    if (!newFragment.isAdded()) {
      try
      {
        //trasection.replace(R.id.linearLayout2, newFragment);
    	  trasection.replace(getApplication().getResources().getIdentifier("linearLayout2", "id", this.packageName), newFragment);
        trasection.commit();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    } else {
      trasection.show(newFragment);
    }
  }
  
  protected void onResume()
  {
    if (getRequestedOrientation() != 0) {
      setRequestedOrientation(0);
    }
    super.onResume();
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    if (this.type == 3) {
      HttpThread.quickHttpRequest(1, 
        new CPSerialRun(this, 
        MetaDataUtil.getMetaDataValue(this, "APVISA_ACCESSKEY", ""), this.cp_serial), 
        new HttpThread.HttpListener()
        {
          public void onHttpResult(int id, Object obj)
          {
            CPSerialRun.CPSerialParser parser = (CPSerialRun.CPSerialParser)obj;
            if ((parser == null) || (!parser.isParserOk())) {
              return;
            }
            if (parser.getCode() == 0)
            {
              Bill bill = parser.getBill();
              if ((bill != null) && (
                (bill.getResult().equals(Bill.RESULT_OF_SUCCESS)) || (bill.getResult().equals(Bill.RESULT_OF_KOU))))
              {
                Intent broad = new Intent();
                broad.setAction(MyEPay.PAY_RESULT);
                broad.putExtra(MyEPay.CODE_RESULT, String.valueOf(MyEPay.CODE_OF_SUCCESS));
                broad.putExtra(MyEPay.SPSERIAL, VnpayActivity.this.cp_serial);
                broad.putExtra(MyEPay.PAY_TYPE, String.valueOf(MyEPay.BANK_TYPE));
                broad.putExtra(MyEPay.MONEY, String.valueOf(bill.getFee()));
                broad.putExtra(MyEPay.AMOUNT, String.valueOf(bill.getAmount()));
                VnpayActivity.this.sendBroadcast(broad);
              }
            }
          }
        });
    }
  }
  
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    if ((keyCode == 4) && (event.getRepeatCount() == 0))
    {
      goBack();
      return false;
    }
    return super.onKeyDown(keyCode, event);
  }
  
  public void goBack()
  {
    finish();
  }
}