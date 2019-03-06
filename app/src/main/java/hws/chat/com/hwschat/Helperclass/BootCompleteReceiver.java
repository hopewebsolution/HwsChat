package hws.chat.com.hwschat.Helperclass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import hws.chat.com.hwschat.Services.Socket_Connection;

import static android.content.Context.ALARM_SERVICE;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1=new Intent(context,Socket_Connection.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
            context.startService(intent1);
        } else {
            context.startService(intent1);
        }
    }

}
