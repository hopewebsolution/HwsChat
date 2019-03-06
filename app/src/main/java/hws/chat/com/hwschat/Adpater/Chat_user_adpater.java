package hws.chat.com.hwschat.Adpater;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import hws.chat.com.hwschat.Activity.Messagner;
import hws.chat.com.hwschat.Helperclass.Utility;
import hws.chat.com.hwschat.Model.Users;
import hws.chat.com.hwschat.R;
import hws.chat.com.hwschat.databinding.RowusersBinding;

public class Chat_user_adpater extends RecyclerView.Adapter<Chat_user_adpater.ViewHolder> {
    ArrayList<Users> chat_user;
    Context context;

    public Chat_user_adpater(Context context, ArrayList<Users> chat_user) {
        this.context = context;
        this.chat_user = chat_user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowusersBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.rowusers, parent, false);
        ViewHolder viewHolder = new ViewHolder(binding);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Users users = chat_user.get(position);
        holder.binding.tvname.setText(users.getName());
        holder.binding.tvlastmsg.setText(Utility.get_last_msg(context,users.getRoomid()));
        Utility.Set_image(users.getProfile(), holder.binding.userimg);
        if (Utility.get_unread(context, users.getRoomid()) != 0) {
            holder.binding.counter.setVisibility(View.VISIBLE);
            if (Utility.get_unread(context, users.getRoomid()) >99)
                holder.binding.counter.setText(String.valueOf(99) + "+");
            else
                holder.binding.counter.setText(String.valueOf(Utility.get_unread(context, users.getRoomid())));
        } else {
            holder.binding.counter.setVisibility(View.GONE);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,Messagner.class);
                intent.putExtra(context.getString(R.string.required_detail),Utility.gson.toJson(users));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chat_user.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RowusersBinding binding;

        public ViewHolder(RowusersBinding userlayout) {
            super(userlayout.getRoot());
            binding = userlayout;
        }
    }
}
