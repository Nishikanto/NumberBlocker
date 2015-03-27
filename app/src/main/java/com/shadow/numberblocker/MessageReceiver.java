package com.shadow.numberblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by nishi_000 on 2/21/2015.
 */
public class MessageReceiver extends BroadcastReceiver {

    Boolean block_number=false;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    DatabaseClass DC = new DatabaseClass(context);
                    DC.open();
                    Cursor c = DC.getData();
                    int iNum = c.getColumnIndex(DC.columnName()[0]);
                    int iMSG = c.getColumnIndex(DC.columnName()[1]);
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        if(c.getString(iMSG).equals("1") && senderNum.equalsIgnoreCase("+" + c.getString(iNum))){
                            block_number = true;
                            break;
                        } else
                            block_number = false;
                    }
                    DC.close();
                    //System.out.println("Call " + block_number);
                    if(block_number!=false){
                        //String message = currentMessage.getDisplayMessageBody();
                        abortBroadcast();
                        //System.out.println(senderNum);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }
}
