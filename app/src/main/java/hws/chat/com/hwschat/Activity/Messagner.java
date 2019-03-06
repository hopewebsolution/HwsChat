package hws.chat.com.hwschat.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import hws.chat.com.hwschat.Adpater.ChatAdpater;
import hws.chat.com.hwschat.Helperclass.Utility;
import hws.chat.com.hwschat.Model.ChatMessage;
import hws.chat.com.hwschat.Model.File_model;
import hws.chat.com.hwschat.Model.Users;
import hws.chat.com.hwschat.R;
import hws.chat.com.hwschat.Services.Socket_Connection;
import hws.chat.com.hwschat.databinding.ActivitymessangerBinding;
import id.zelory.compressor.Compressor;
import io.reactivex.annotations.NonNull;
import io.socket.client.Socket;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Messagner extends AppCompatActivity implements View.OnClickListener {

    static ActivitymessangerBinding activitymessangerBinding;
    static ArrayList<ChatMessage> messagelist;
    public static int room_id = 0;
    static ChatAdpater chatAdapter;
    Thread t;
    Timer timer;
    static boolean is_online = false;
    Socket socket;
    static Users users, front_user;
    private String msg_body = "";
    Socket_Connection socket_connection;
    public static boolean is_room = false;
    static Activity activity;
    public static String user_id = "";
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private static final int TYPING_TIMER_LENGTH = 2500;
    private static final int TYPING_TIMER_LENGTH1 = 1000;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    boolean isSpeakButtonLongPressed = false;
    CountDownTimer countDownTimer;
    int RequestCode = 2;
    int PICKFILE_REQUEST_CODE = 3;
    int PICKCAMERA_REQUEST_CODE = 4;
    int PICKDOC_REQUEST_CODE = 5;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitymessangerBinding = DataBindingUtil.setContentView(Messagner.this, R.layout.activitymessanger);
        //intililzation
        inti();
        //clciks
        clicks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        is_room = true;
        activity = this;

    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.save_chat(getApplicationContext(), messagelist, users.getRoomid());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_room = false;
        room_id = 0;

    }

    private void clicks() {
        activitymessangerBinding.back.setOnClickListener(this);
        activitymessangerBinding.sendmsg.setOnClickListener(this);
        activitymessangerBinding.back.setOnClickListener(this);
        activitymessangerBinding.other.setOnClickListener(this);
        activitymessangerBinding.emoji.setOnClickListener(this);
        activitymessangerBinding.record.setOnLongClickListener(speakHoldListener);
        activitymessangerBinding.record.setOnTouchListener(speakTouchListener);


    }

    private void inti() {
        random = new Random();
        if (getIntent() != null) {
            users = Utility.gson.fromJson(getIntent().getStringExtra(getString(R.string.required_detail)), Users.class);
            room_id = users.getRoomid();
            users = Utility.get_group_by_room(getApplicationContext(), room_id);
            user_id = String.valueOf(users.getSender());
            activitymessangerBinding.uname.setText(users.getName());
            Utility.save_unread(this, room_id, 0);
            Utility.Set_image(users.getProfile(), activitymessangerBinding.userprofile);
        }
        socket_connection = new Socket_Connection();
        socket_connection.user_join(String.valueOf(Utility.get_user(getApplication()).getUser_id()), this);
        socket_connection.user_for_online(String.valueOf(users.getRecever()));

        messagelist = Utility.get_chat(this, users.getRoomid());
        chatAdapter = new ChatAdpater(this, messagelist, users.getSender());
        activitymessangerBinding.Rvacc.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        activitymessangerBinding.Rvacc.setAdapter(chatAdapter);
        activitymessangerBinding.Rvacc.smoothScrollToPosition(messagelist.size() - 1);

        activitymessangerBinding.ustatus.setText(Utility.current_date());
        activitymessangerBinding.edtmsg1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    activitymessangerBinding.record.setVisibility(View.GONE);
                    activitymessangerBinding.sendmsg.setVisibility(View.VISIBLE);
                } else {
                    activitymessangerBinding.record.setVisibility(View.VISIBLE);
                    activitymessangerBinding.sendmsg.setVisibility(View.GONE);
                }

                if (!mTyping) {
                    mTyping = true;
                    socket_connection.user_for_typing(users);
                }
                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendmsg:
                String msg = activitymessangerBinding.edtmsg1.getText().toString();
                if (!Utility.is_empty(msg)) {
                    ChatMessage chatMessage = new ChatMessage(msg, 0, true, Utility.current_date(), users.getRoomid(), Utility.get_user(Messagner.this).getUser_id(), users.getRecever(), users, new File_model("", ""));
                    socket_connection.send_message(chatMessage);
                    activitymessangerBinding.edtmsg1.setText("");
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.emoji:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;
            case R.id.other:
                showCustomDialog();
                break;
        }
    }

    public static void notifiy_message(final ChatMessage message) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                messagelist.add(message);
                chatAdapter.notifyDataSetChanged();
                activitymessangerBinding.Rvacc.smoothScrollToPosition(messagelist.size() - 1);

            }
        });
    }

    public static void user_online(final String object_data) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject(object_data);
                    if (object.getString("userid").equalsIgnoreCase(String.valueOf(users.getRecever()))) {
                        if (object.getBoolean("status")) {
                            is_online = true;
                            activitymessangerBinding.ustatus.setText(activity.getString(R.string.online));
                            activitymessangerBinding.onlinestatus.setImageDrawable(activity.getResources().getDrawable(R.drawable.online));
                        } else {
                            is_online = false;
                            activitymessangerBinding.ustatus.setText(Utility.current_date());
                            activitymessangerBinding.onlinestatus.setImageDrawable(activity.getResources().getDrawable(R.drawable.offline));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static void user_typing(final String object_data) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject(object_data);
                    try {
                        String recever = object.getString("recever");
                        String roomid = object.getString("room_id");
                        if (String.valueOf(users.getRoomid()).equalsIgnoreCase(roomid) && recever.equalsIgnoreCase(String.valueOf(users.getSender()))) {
                            activitymessangerBinding.ustatus.setText("typing...");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public static void user_stop(final String object_data) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject(object_data);
                    try {
                        String recever = object.getString("recever");
                        String roomid = object.getString("room_id");
                        if (String.valueOf(users.getRoomid()).equalsIgnoreCase(roomid) && recever.equalsIgnoreCase(String.valueOf(users.getSender()))) {
                            activitymessangerBinding.ustatus.setText(activity.getString(R.string.online));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;
            mTyping = false;
            socket_connection.user_for_stop(users);
        }
    };

    private void requestPermission() {
        ActivityCompat.requestPermissions(Messagner.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    private View.OnLongClickListener speakHoldListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View pView) {
            // Do something when your hold starts here.
            if (checkPermission() && !isSpeakButtonLongPressed) {
                recoding();
            } else {
                requestPermission();
            }
            return true;
        }
    };

    private void recoding() {
        isSpeakButtonLongPressed = true;
        activitymessangerBinding.progress.setVisibility(View.VISIBLE);
        activitymessangerBinding.edtmsg1.setVisibility(View.GONE);
        activitymessangerBinding.other.setVisibility(View.GONE);
        activitymessangerBinding.emoji.setVisibility(View.GONE);
        AudioSavePathInDevice = createFolder() + "/" + CreateRandomAudioFileName(5) + ".mp3";
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(AudioSavePathInDevice);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private View.OnTouchListener speakTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View pView, MotionEvent pEvent) {
            pView.onTouchEvent(pEvent);
            // We're only interested in when the button is released.

            if (pEvent.getAction() == MotionEvent.ACTION_UP) {
                // We're only interested in anything if our speak button is currently pressed.
                if (isSpeakButtonLongPressed) {
                    stoprecoding();
                }
            }
            return false;
        }
    };

    private void stoprecoding() {
        isSpeakButtonLongPressed = false;
        activitymessangerBinding.progress.setVisibility(View.GONE);
        activitymessangerBinding.edtmsg1.setVisibility(View.VISIBLE);
        activitymessangerBinding.other.setVisibility(View.VISIBLE);
        activitymessangerBinding.emoji.setVisibility(View.VISIBLE);
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                File file = new File(AudioSavePathInDevice);
                file_upload("audio", file);
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public File createFolder() {
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "/HWS/Media/audio");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File file;

        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && null != data) {
                // Get the Uri of the selected file
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.ORIENTATION};
                Cursor cursor = this.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String picturePath = cursor.getString(columnIndex);

                cursor.close();
                file = new File(picturePath);
                file_upload("img", file);
            }

        }
        if (requestCode == PICKCAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                File file1 = new File(getRealPathFromURI(imageUri));
                file_upload("img", file1);
            }
        }

        if (requestCode == PICKDOC_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                file = new File(uri.getPath());
                file_upload("doc", file);
            }
        }

    }

    public void file_upload(final String type, File myfile) {
        activitymessangerBinding.loading.setVisibility(View.VISIBLE);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (type.equalsIgnoreCase("img")) {
            try {
                File now_file = new Compressor(this).compressToFile(myfile);
                builder.addFormDataPart(Utility.get_user(this).getPost_file_key(), now_file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), now_file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            builder.addFormDataPart(Utility.get_user(this).getPost_file_key(), myfile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), myfile));
        }
        builder.setType(MultipartBody.FORM);
        MultipartBody body = builder.build();
        final Request request = new Request.Builder()
                .url(Utility.get_user(this).getApi_for_file())
                .post(body)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.retryOnConnectionFailure();
        okHttpClient.connectTimeoutMillis();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        activitymessangerBinding.loading.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, final @NonNull Response response) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        activitymessangerBinding.loading.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            try {
                                JSONObject object = new JSONObject(response.body().string());
                                File_model model = new File_model(object.getString("data"), type);
                                ChatMessage chatMessage = new ChatMessage("", 0, false, Utility.current_date(), users.getRoomid(), Utility.get_user(Messagner.this).getUser_id(), users.getRecever(), users, model);
                                socket_connection.send_message(chatMessage);
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            }

        });
    }

    public void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.activity_sort, null);
        dialog.setContentView(view);

        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.drawable.button_border_tranfranet);
        window.setGravity(Gravity.CENTER);
        dialog.setCancelable(false);

        TextView doc = (TextView) view.findViewById(R.id.document);
        doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (checkPermission()) {
                    String deviceMan = android.os.Build.MANUFACTURER;
                    if (deviceMan.equalsIgnoreCase("samsung")) {
                        Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                        intent.putExtra("CONTENT_TYPE", "*/*");
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(intent, PICKDOC_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(intent, PICKDOC_REQUEST_CODE);
                    }
                } else {
                    requestPermission();
                }

            }
        });

        TextView gallery = (TextView) view.findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (checkPermission()) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    startActivityForResult(i, PICKFILE_REQUEST_CODE);
                } else {
                    requestPermission();
                }

            }
        });
        TextView Camera = (TextView) view.findViewById(R.id.Camera);
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (checkPermission()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, PICKCAMERA_REQUEST_CODE);
                } else {
                    requestPermission();
                }
            }
        });

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
