package hws.chat.com.hwschat.Helperclass;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hws.chat.com.hwschat.Activity.Messagner;
import hws.chat.com.hwschat.Applevel.MyApplication;
import hws.chat.com.hwschat.Model.ChatMessage;
import hws.chat.com.hwschat.Model.Messages;
import hws.chat.com.hwschat.Model.Node_Settings;
import hws.chat.com.hwschat.Model.Unread;
import hws.chat.com.hwschat.Model.Users;
import hws.chat.com.hwschat.R;

import static android.content.Context.ACTIVITY_SERVICE;

public class Utility {
    static Context contexts;
    static LocationManager locationManager;
    static Location location;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static SharedPreferences local_db;
    private static SharedPreferences.Editor tbl_user;
    public static Gson gson = new Gson();
    public static MyApplication application;

    // to show  full  screen activity
    public static void full_screen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //to show toast
    public static void show_toast(Activity activity, String message) {
        View view = activity.getCurrentFocus();
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    //to check text is empty or not
    public static boolean is_empty(String value) {
        return TextUtils.isEmpty(value);
    }

    //to check is  network is available or not
    public static boolean is_online(Context activity) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            //we are connected to a network
            connected = true;
        else
            connected = false;
        return connected;
    }

    //to set image in image view from url

    public static void Set_image(String url, ImageView img) {
        if (!TextUtils.isEmpty(url))
            Picasso.get().load(url).error(R.drawable.defult).into(img);
    }

    public static boolean isValidMobile(String phone) {
        String regEx = "^[0-9]{10,10}$";
        return phone.matches(regEx);
    }

    public static boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //to check to value is  equal
    public static boolean is_equal(String value1, String value2) {
        boolean is_valid = false;
        if (value1.equals(value2)) {
            is_valid = true;
        }
        return is_valid;
    }
    // to show toast 1

    //to show toast1
    public static void show_toast(String message, View view) {
        /* View view=activity.getCurrentFocus();*/
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    // to save update profile data for update profile

    //to set user is login
    public static void set_chat_user(Context activity, String chats) {
        local_db = activity.getSharedPreferences(activity.getString(R.string.chat_user), Context.MODE_PRIVATE);
        tbl_user = local_db.edit();
        tbl_user.putString("chat_list", chats);
        tbl_user.apply();
    }

    public static ArrayList<Users> get_chat_user(Context activity) {
        local_db = activity.getSharedPreferences(activity.getString(R.string.chat_user), Context.MODE_PRIVATE);
        return Utility.gson.fromJson(local_db.getString("chat_list", ""), new TypeToken<List<Users>>() {
        }.getType());
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String get_hash(Activity activity) {
        PackageInfo info;
        String something = "";
        try {
            info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                something = new String(Base64.encode(md.digest(), 0));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        return something;
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static String current_date() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a dd-MMM-yyyy");
        return df.format(c);
    }

    //to set user
    public static void save_group(Context activity, Users users) {
        ArrayList<Users> group = get_groups(activity);
        if (!is_group(activity, users)) {
            group.add(users);
        }
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.group), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(activity.getString(R.string.group), gson.toJson(group));
        editor.apply();
    }


    //to set user
    public static void save_unread(Context activity, int room_id, int unread_count) {
        ArrayList<Unread> unreads = new ArrayList<>();
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.unread), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String data = sharedPreferences.getString(activity.getString(R.string.unread), "");
        if (!TextUtils.isEmpty(data)) {
            unreads = gson.fromJson(data, new TypeToken<List<Unread>>() {
            }.getType());
            if (unreads != null && unreads.size() != 0) {
                for (int i = 0; i < unreads.size(); i++) {
                    if (unreads.get(i).getRoom() == room_id) {
                        Unread unread = new Unread();
                        unread.setRoom(room_id);
                        unread.setUnread(unread_count);
                        unreads.set(i, unread);
                        break;
                    }
                }
            } else {
                Unread unread = new Unread();
                unread.setRoom(room_id);
                unread.setUnread(unread_count);
                unreads.add(unread);
            }
        } else {
            Unread unread = new Unread();
            unread.setRoom(room_id);
            unread.setUnread(unread_count);
            unreads.add(unread);
        }
        editor.putString(activity.getString(R.string.unread), gson.toJson(unreads));
        editor.apply();
    }

    //to set user
    public static int get_unread(Context activity, int room_id) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.unread), Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(sharedPreferences.getString(activity.getString(R.string.unread), ""))) {
            ArrayList<Unread> unreads = gson.fromJson(sharedPreferences.getString(activity.getString(R.string.unread), ""), new TypeToken<List<Unread>>() {
            }.getType());
            for (Unread unread : unreads) {
                if (unread.getRoom() == room_id) {
                    return unread.getUnread();
                }
            }
        }
        return 0;
    }


    //to make group
    public static void make_group(Activity activity, int extra_key, int Room_id, int Recever_id, String Recever_name, String Recever_profile) {
        Users users = new Users();
        users.setExtra_key(extra_key);
        users.setRoomid(Room_id);
        users.setRecever(Recever_id);
        users.setSender(get_user(activity).getUser_id());
        users.setName(Recever_name);
        users.setProfile(Recever_profile);
        users.setUsername(get_user(activity).getCurrent_user_name());
        users.setUserprofile(get_user(activity).getCurrent_user_name());
        Utility.save_group(activity, users);
        Intent intent = new Intent(activity, Messagner.class);
        intent.putExtra(activity.getString(R.string.required_detail), Utility.gson.toJson(users));
        activity.startActivity(intent);
    }

    //to set user
    public static Users get_group_by_room(Context activity, int room) {
        ArrayList<Users> group = get_groups(activity);
        Users users = new Users();
        for (Users usersf : group) {
            if (usersf.getRoomid() == room) {
                users = usersf;
            }
        }
        return users;
    }

    //to set user
    public static void save_user(Context activity, String node_url, int user_id, String api_url, String api_key, String user_name, String user_profile) {
        Node_Settings settings = new Node_Settings(node_url, user_id, api_url, api_key, user_name, user_profile);
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.user), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(activity.getString(R.string.user), gson.toJson(settings));
        editor.apply();
    }

    //to set clear data
    public static void clear_user(Context activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.user), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    //to get  user  id
    public static Node_Settings get_user(Context activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.user), Context.MODE_PRIVATE);
        if (!Utility.is_empty(sharedPreferences.getString(activity.getString(R.string.user), ""))) {
            return gson.fromJson(sharedPreferences.getString(activity.getString(R.string.user), ""), Node_Settings.class);
        } else
            return new Node_Settings("", 0, "", "", "", "");
    }

    public static void save_chat(Context activity, final ArrayList<ChatMessage> all_message, int room_id) {
        SharedPreferences local_db = activity.getSharedPreferences(activity.getString(R.string.message), Context.MODE_PRIVATE);
        SharedPreferences.Editor tbl_user = local_db.edit();
        ArrayList<Messages> all_chats = get_chats(activity);
        if (all_chats != null) {
            boolean is_chat = false;
            int pos = 0;
            for (Messages messages : all_chats) {
                if (messages != null && messages.getRoom_id() == room_id) {
                    is_chat = true;
                    break;
                }
                pos++;
            }
            if (is_chat) {
                Messages msg = new Messages(room_id, all_message);
                all_chats.set(pos, msg);
            } else {
                Messages msg = new Messages(room_id, all_message);
                all_chats.add(msg);
            }
        } else {
            Messages msg = new Messages(room_id, all_message);
            all_chats.add(msg);
        }
        tbl_user.putString(activity.getString(R.string.message), gson.toJson(all_chats));
        tbl_user.apply();

    }

    public static void save_chat_single(Context activity, final ChatMessage msg, int room_id) {
        SharedPreferences local_db = activity.getSharedPreferences(activity.getString(R.string.message), Context.MODE_PRIVATE);
        SharedPreferences.Editor tbl_user = local_db.edit();
        ArrayList<Messages> all_chats = get_chats(activity);
        if (all_chats != null) {
            boolean is_chat = false;
            int pos = 0;
            for (Messages messages : all_chats) {
                if (messages != null && messages.getRoom_id() == room_id) {
                    is_chat = true;
                    break;
                }
                pos++;
            }
            if (is_chat) {
                ArrayList<ChatMessage> messages = all_chats.get(pos).getMsg();
                messages.add(msg);
                Messages msgss = new Messages(room_id, messages);
                all_chats.set(pos, msgss);
            } else {
                ArrayList<ChatMessage> messages = new ArrayList<>();
                messages.add(msg);
                Messages msgss = new Messages(room_id, messages);
                all_chats.add(msgss);
            }
        } else {
            ArrayList<ChatMessage> messages = new ArrayList<>();
            messages.add(msg);
            Messages msgss = new Messages(room_id, messages);
            all_chats.add(msgss);
        }
        tbl_user.putString(activity.getString(R.string.message), gson.toJson(all_chats));
        tbl_user.apply();

    }

    public static ArrayList<Messages> get_chats(Context activity) {
        SharedPreferences local_db = activity.getSharedPreferences(activity.getString(R.string.message), Context.MODE_PRIVATE);
        String msg = local_db.getString(activity.getString(R.string.message), "");
        if (!TextUtils.isEmpty(msg)) {
            return gson.fromJson(msg, new TypeToken<List<Messages>>() {
            }.getType());

        } else
            return new ArrayList<>();
    }

    public static ArrayList<ChatMessage> get_chat(Activity activity, int room_id) {
        SharedPreferences local_db = activity.getSharedPreferences(activity.getString(R.string.message), Context.MODE_PRIVATE);
        String msg = local_db.getString(activity.getString(R.string.message), "");
        ArrayList<ChatMessage> msg_for = new ArrayList<>();
        if (!TextUtils.isEmpty(msg)) {
            ArrayList<Messages> all_chats = get_chats(activity);
            for (Messages messages : all_chats) {
                if (messages.getRoom_id() == room_id) {
                    msg_for = messages.getMsg();
                }
            }

        }
        return msg_for;
    }


    //to get  user
    public static ArrayList<Users> get_groups(Context activity) {
        ArrayList<Users> group = new ArrayList<>();
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.group), Context.MODE_PRIVATE);
        String groups = sharedPreferences.getString(activity.getString(R.string.group), "");
        if (!TextUtils.isEmpty(groups)) {
            return gson.fromJson(groups, new TypeToken<List<Users>>() {
            }.getType());
        }
        return group;
    }

//    //to get  user
//    public static ArrayList<Users> refresh(Context activity, ArrayList<Users> group) {
//        int index = 0;
//        for (int i = 0; i < group.size(); i++) {
//            Users users = group.get(i);
//            group.set(i, users);
//        }
//        return group;
//    }

    //to check  group is already
    public static boolean is_group(Context activity, Users new_user) {
        ArrayList<Users> group = get_groups(activity);
        for (Users users1 : group) {
            if (users1.getRoomid() == new_user.getRoomid()) {
                return true;
            }
        }
        return false;
    }

    //get last msg
    public static String get_last_msg(Context activity, int room_id) {
        ArrayList<Messages> all_chats = get_chats(activity);
        String msg = "";
        if (all_chats != null) {
            for (int i = 0; i < all_chats.size(); i++) {
                if (all_chats.get(i).getRoom_id() == room_id) {
                    if (all_chats.get(i).getMsg().size() > 0) {
                        for (ChatMessage chatMessage : all_chats.get(i).getMsg()) {
                            if (chatMessage.isMine()) {
                                msg = chatMessage.getBody();
                            }
                        }
                    }
                    break;
                }
            }
        }
        return msg;
    }

    public static void openFile(Context context, File url) throws IOException {
        // Create URI
        File file = url;
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
