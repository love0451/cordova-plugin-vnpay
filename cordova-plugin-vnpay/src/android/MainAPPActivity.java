package com.apvisa.pay;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.com.apvisa.util.MyEPay;
import cn.com.apvisa.util.MyEPay.MyEPayPayListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainAPPActivity
  extends Activity
{
  private Button card;
  private EditText money;
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(1);
    getWindow().setFlags(1024, 
      1024);
    setContentView(getResId("layout", "ap_main"));
    init();
  }
  
  private void init()
  {
    this.money = ((EditText)findViewById(getResId("id", "money")));
    this.card = ((Button)findViewById(getResId("id", "card")));
    this.card.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View view)
      {
        MyEPay.getInstance().startPay(MainAPPActivity.this, MainAPPActivity.this.getTradeLongId(), new MyEPay.MyEPayPayListener()
        {
          public void onPayFinished(int result, String cpSerial)
          {
            Toast.makeText(MainAPPActivity.this, String.valueOf(result), 1).show();
          }
        });
      }
    });
  }
  
  protected void onResume()
  {
    if (getRequestedOrientation() != 0) {
      setRequestedOrientation(0);
    }
    super.onResume();
  }
  
  public String getTradeLongId()
  {
    return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + getRandomNumber(5);
  }
  
  public String getRandomNumber(int num)
  {
    int codeCount = num;
    String str = "";
    char[] codeSequence = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    Random random = new Random();
    for (int i = 0; i < codeCount; i++) {
      str = str + codeSequence[random.nextInt(10)];
    }
    return str;
  }
	public int getResId(String group, String key) {
		return this.getResources().getIdentifier(key, group, this.getPackageName());
	}
}

