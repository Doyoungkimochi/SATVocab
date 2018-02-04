package com.pamkim.sandwiches;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by toripham on 2/3/18.
 */

public class MyService extends Service {

        BroadcastReceiver mReceiver;
        Handler handler;
        private IntentFilter it;






        public MyService(){
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Toast.makeText(getApplicationContext(), "onCreate() has been executed", Toast.LENGTH_SHORT).show();
            handler = new Handler(getApplication().getMainLooper());
            it = new IntentFilter();
            it.addAction("com.pamkim.sandwiches");
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(context, "Please continue", Toast.LENGTH_LONG).show();
                }
            };
            registerReceiver(mReceiver, it);




        }



        @Override
        public IBinder onBind(Intent intent) {
            // TODO: Return the communication channel to the service.
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();



            Log.d("Running app", "hi");



            String appCurrentRunning = TaskChecker.getCurrentTopActivity(getApplicationContext());



            return START_STICKY;


            }



        @Override
        public void onDestroy() {
           Toast.makeText(this,"service destroyed, Current Thread is " + Thread.currentThread().getId(),Toast.LENGTH_SHORT).show();
            String appCurrentRunning = TaskChecker.getCurrentTopActivity(getApplicationContext());
            Toast.makeText(getApplicationContext(), "App running: " + appCurrentRunning, Toast.LENGTH_LONG).show();
            super.onDestroy();
           unregisterReceiver(mReceiver);
            Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();



        }




}
