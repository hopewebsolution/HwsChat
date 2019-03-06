package hws.chat.com.hwschat.Applevel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.support.multidex.MultiDex;

import java.net.URISyntaxException;

import hws.chat.com.hwschat.Helperclass.Utility;
import io.socket.client.IO;
import io.socket.client.Socket;


public class MyApplication extends Application {

    public static String Time = "";


    public static String getTime() {
        return Time;
    }

    public static void setTime(String time) {
        Time = time;
    }

    protected PowerManager.WakeLock mWakeLock;
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}

