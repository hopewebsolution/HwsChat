package hws.chat.com.hwschat.Services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import hws.chat.com.hwschat.Activity.Messagner;
import hws.chat.com.hwschat.Activity.Mychats;
import hws.chat.com.hwschat.Helperclass.Utility;
import hws.chat.com.hwschat.Model.ChatMessage;
import hws.chat.com.hwschat.Model.Users;
import hws.chat.com.hwschat.R;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static hws.chat.com.hwschat.Activity.Messagner.is_room;
import static hws.chat.com.hwschat.Activity.Messagner.room_id;
import static hws.chat.com.hwschat.Activity.Messagner.user_online;
import static hws.chat.com.hwschat.Activity.Messagner.user_stop;
import static hws.chat.com.hwschat.Activity.Messagner.user_typing;
import static hws.chat.com.hwschat.Helperclass.Utility.isAppOnForeground;

public class Socket_Connection extends Service {
    public static Socket socket;
    Messagner messagner;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Utility.is_online(getApplicationContext())) {
            try {
                socket = IO.socket(Utility.get_user(getApplicationContext()).getNode_url());
                if (!socket.connected()) {
                    socket.connect();
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                JSONObject data = (JSONObject) args[0];
                ChatMessage chatMessage = null;

                try {
                    String msg = data.getString("message");
                    chatMessage = new Gson().fromJson(msg, ChatMessage.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (Utility.is_group(getApplicationContext(), chatMessage.getUsers())) {
                    if (isAppOnForeground(getApplicationContext())) {
                        if (room_id == chatMessage.getRoom_id()) {
                            recevied_message(chatMessage);
                        } else {
                            push_notification(chatMessage, getApplicationContext());
                        }
                    } else {
                        push_notification(chatMessage, getApplicationContext());
                    }
                } else if (chatMessage.getReceiver() == Utility.get_user(getApplicationContext()).getUser_id()) {
                    {
                        Users users = new Users();
                        users.setExtra_key(0);
                        users.setRoomid(chatMessage.getRoom_id());
                        users.setRecever(chatMessage.getSender());
                        users.setSender(Utility.get_user(getApplicationContext()).getUser_id());
                        users.setName(chatMessage.getUsers().getUsername());
                        users.setProfile(chatMessage.getUsers().getUserprofile());
                        users.setUsername(chatMessage.getUsers().getName());
                        users.setUserprofile(chatMessage.getUsers().getUserprofile());
                        Utility.save_group(getApplicationContext(), users);
                        push_notification(chatMessage, getApplicationContext());
                    }
                }

            }
        });
        socket.on("online", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                JSONObject data = (JSONObject) args[0];
                if (is_room) {
                    user_online(data.toString());
                }
            }
        });

        socket.on("typing", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                JSONObject data = (JSONObject) args[0];
                if (is_room) {
                    user_typing(data.toString());
                }
            }
        });
        socket.on("stop_typing", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                JSONObject data = (JSONObject) args[0];
                if (is_room) {
                    user_stop(data.toString());
                }
            }
        });

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful\

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    public void send_message(ChatMessage message) {
        if (socket != null) {
            socket.emit("messagedetection", Utility.gson.toJson(message));
        }

    }

    private void recevied_message(ChatMessage chatMessage) {
        if (is_room) {
            messagner.notifiy_message(chatMessage);
        } else {
            push_notification(chatMessage, getApplicationContext());
        }
    }

    public void push_notification(ChatMessage chatMessage, Context context) {
        Utility.save_unread(context,chatMessage.getRoom_id(),Utility.get_unread(context,chatMessage.getRoom_id())+1);
        Utility.save_chat_single(getApplicationContext(), chatMessage, chatMessage.getRoom_id());
        Users users = chatMessage.getUsers();
        Intent intent = new Intent(context, Messagner.class);
        intent.putExtra(context.getString(R.string.required_detail), Utility.gson.toJson(users));
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        @SuppressLint("InlinedApi")
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(chatMessage.getUsers().getUsername())
                .setSound(notification)
                .setGroup(getPackageName())
                .setContentText(chatMessage.getBody());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_ONE_SHOT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());

    }


    //use  when  user is typing
    public void user_for_typing(Users users) {
        if (socket != null && socket.connected())
            socket.emit("typing", users.getRoomid(), users.getRecever());
    }

    //use  when  user is typing
    public void user_for_stop(Users users) {
        if (socket != null && socket.connected())
            socket.emit("stop_typing", users.getRoomid(), users.getRecever());
    }


    //use  when  user is typing
    public void user_for_online(String recever) {
        if (socket != null)
            socket.emit("online", recever);
    }

    //use  when  user is typing
    public void user_join(String user_id,Context context) {
        if (socket != null) {
            socket.emit("join", user_id);
        } else {
            socket_connection(user_id,context);
        }
    }

    //use  when  user is typing
    public void user_for_offilne(Users users) {
        if (socket != null && socket.connected())
            socket.emit("userdisconnect", users.getRoomid(), users.getSender());
    }


    public void socket_connection( String user_id,Context context) {
        try {
            socket = IO.socket(Utility.get_user(context).getNode_url());
            if (!socket.connected()) {
                socket.connect();
            }

           user_join(user_id,context);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private ComponentName get_current_screen() {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis()+(1+1000), pendingIntent);
    }
}
