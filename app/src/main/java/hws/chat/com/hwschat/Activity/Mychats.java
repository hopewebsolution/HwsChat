package hws.chat.com.hwschat.Activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Random;

import hws.chat.com.hwschat.Adpater.Chat_user_adpater;
import hws.chat.com.hwschat.Helperclass.Toollbar;
import hws.chat.com.hwschat.Helperclass.Utility;
import hws.chat.com.hwschat.Model.Users;
import hws.chat.com.hwschat.R;
import hws.chat.com.hwschat.Services.Socket_Connection;
import hws.chat.com.hwschat.databinding.MainactivityBinding;

public class Mychats extends Toollbar {
    MainactivityBinding mainactivityBinding;
    Chat_user_adpater chat_user_adpater;
    ArrayList<Users> groups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainactivityBinding = DataBindingUtil.setContentView(Mychats.this, R.layout.mainactivity);
        if (Utility.is_empty(String.valueOf(Utility.get_user(this).getUser_id()))||Utility.get_user(this).getUser_id()==0) {
            Utility.save_user(this, "http://hopewebsolution.com:2000", get_random(), "http://liwa.biz/api/uploadfile", "chat_file", "Kamal Ashiwal", "https://previews.123rf.com/images/vgstudio/vgstudio1506/vgstudio150600107/41221598-portrait-of-happy-smiling-businessman-in-crossed-arms-pose-in-black-confident-suit-against-grey-back.jpg");
        }
        inti();
    }

    private int get_random() {
        Random rand = new Random();
        return rand.nextInt(1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId() == R.id.action_group) {
           Utility.make_group(this,0,1236,123,"Kamlesh","");
        }
        return super.onOptionsItemSelected(item);
    }

    private void inti() {
        startService(new Intent(this, Socket_Connection.class));
        Socket_Connection socket_connection=new Socket_Connection();
        socket_connection.user_join(String.valueOf(Utility.get_user(getApplication()).getUser_id()),this);
        creates_groups();

    }

    @Override
    protected void onResume() {
        super.onResume();
        chat_user_adpater.notifyDataSetChanged();
    }

    private void creates_groups() {
        groups = Utility.get_groups(this);
        mainactivityBinding.rvuser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        chat_user_adpater = new Chat_user_adpater(this, groups);
        mainactivityBinding.rvuser.setAdapter(chat_user_adpater);
    }


}
