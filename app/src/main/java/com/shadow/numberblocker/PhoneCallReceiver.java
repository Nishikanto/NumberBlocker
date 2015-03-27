package com.shadow.numberblocker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import java.lang.reflect.Method;

import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

public class PhoneCallReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneCallStateListener customPhoneListener = new PhoneCallStateListener(context);
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public class PhoneCallStateListener extends PhoneStateListener {

        private Context context;
        Boolean block_number=false;


        public PhoneCallStateListener(Context context) {
            this.context = context;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:

                    DatabaseClass DC = new DatabaseClass(context);
                    DC.open();
                    Cursor c = DC.getData();
                    int iNum = c.getColumnIndex(DC.columnName()[0]);
                    int iCall = c.getColumnIndex(DC.columnName()[2]);
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        if(c.getString(iCall).equals("1") && PhoneNumberUtils.compare(incomingNumber, c.getString(iNum))){
                           block_number = true;
                           break;
                        } else
                            block_number = false;
                    }
                    DC.close();

                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                    audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                    audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
                    audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);

                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                    try {
                        //Toast.makeText(context, "in" + block_number, Toast.LENGTH_LONG).show();
                        Class clazz = Class.forName(telephonyManager.getClass().getName());
                        Method method = clazz.getDeclaredMethod("getITelephony");
                        method.setAccessible(true);
                        ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);

                        // Checking incoming call number
                        //System.out.println("Call " + block_number);
                        if (block_number!=false) {
                            // Turn ON the mute
                            audioManager.setStreamMute(AudioManager.STREAM_RING, true);


                            //telephonyService.silenceRinger();//Security exception
                            // problem

                            telephonyService = (ITelephony) method.invoke(telephonyManager);
                            // telephonyService.silenceRinger();
                            //System.out.println(" in  " + block_number);
                            telephonyService.endCall();

                            // Turn OFF the mute
                            audioManager.setStreamMute(AudioManager.STREAM_RING, false);

                        }
                    } catch (Exception e) {
                        //Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    }

                    // Turn OFF the mute
                    audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                    audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
                    audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
                    break;

                case PhoneStateListener.LISTEN_CALL_STATE:
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }
}