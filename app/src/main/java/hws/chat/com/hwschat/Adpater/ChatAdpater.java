package hws.chat.com.hwschat.Adpater;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import hws.chat.com.hwschat.Activity.FullscreenActivity;
import hws.chat.com.hwschat.Activity.Messagner;
import hws.chat.com.hwschat.Helperclass.DownloadTask;
import hws.chat.com.hwschat.Helperclass.Utility;
import hws.chat.com.hwschat.Model.ChatMessage;
import hws.chat.com.hwschat.Model.Users;
import hws.chat.com.hwschat.R;
import hws.chat.com.hwschat.databinding.RowusersBinding;

public class ChatAdpater extends RecyclerView.Adapter<ChatAdpater.ViewHolder> {
    private LayoutInflater inflater = null;
    private ArrayList<ChatMessage> chatMessageList;
    Context context;
    int sender;
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    public ChatAdpater(Context context, ArrayList<ChatMessage> chatMessageList,int sender) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.sender=sender;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_layout, parent, false);
        } else if (viewType == TYPE_TWO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received_layout, parent, false);
        } else {
            throw new RuntimeException("The type has to be ONE or TWO");
        }
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChatMessage message = chatMessageList.get(position);
        holder.tv_message.setText(message.getBody());
        holder.tv_date.setText(message.getDate());
        if (!message.isMine()) {
            holder.files.setVisibility(View.VISIBLE);
            holder.tv_message.setVisibility(View.GONE);
            holder.tv_date.setVisibility(View.GONE);
            if (message.getFileModel().getFormate().equalsIgnoreCase("img")) {
                holder.img.setVisibility(View.VISIBLE);
                holder.audioayout.setVisibility(View.GONE);
                holder.document.setVisibility(View.GONE);
                Utility.Set_image(message.getFileModel().getFile(), holder.img);
            } else if (message.getFileModel().getFormate().equalsIgnoreCase("doc")) {
               holder.img.setVisibility(View.GONE);
               holder.audioayout.setVisibility(View.GONE);
               holder.document.setVisibility(View.VISIBLE);
                if (message.getFileModel().getFile().contains(".pdf")) {
                    holder.document.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pdf));
                } else if (message.getFileModel().getFile().contains(".doc") || message.getFileModel().getFile().contains(".docx")) {
                    holder.document.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_word));
                } else if (message.getFileModel().getFile().contains(".xls") || message.getFileModel().getFile().contains(".xlsx")) {
                    holder.document.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_xls));
                } else if (message.getFileModel().getFile().contains(".txt")) {
                    holder.document.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_txt));
                }
            } else if (message.getFileModel().getFormate().equalsIgnoreCase("audio")) {
               holder.img.setVisibility(View.GONE);
               holder.audioayout.setVisibility(View.VISIBLE);
               holder.document.setVisibility(View.GONE);
            } else {
               holder.img.setVisibility(View.GONE);
               holder.audio.setVisibility(View.GONE);
               holder.document.setVisibility(View.GONE);
            }
        } else {
           holder.tv_message.setVisibility(View.VISIBLE);
           holder.files.setVisibility(View.GONE);
           holder.tv_date.setVisibility(View.VISIBLE);

        }
        final MediaPlayer mp = new MediaPlayer();
        holder.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                    holder.pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                } else {
                    mp.start();
                    holder.pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause_black_24dp));

                }
            }
        });
        holder.audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.audio.setVisibility(View.GONE);
                holder.pause.setVisibility(View.VISIBLE);
                try {
                    mp.setDataSource(message.getFileModel().getFile());
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                       holder.audio.setVisibility(View.VISIBLE);
                       holder.pause.setVisibility(View.GONE);
                    }
                });
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // mp is your MediaPlayer
                        // progress is your ProgressBar
                        int currentPosition = 0;
                        int total = mp.getDuration();
                        holder.seekBar.setMax(total);
                        while (mp != null && currentPosition < total) {
                            try {
                                Thread.sleep(1000);
                                currentPosition = mp.getCurrentPosition();
                                holder.seekBar.setProgress(currentPosition);
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                    }
                }).start();

                holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (b) {
                            mp.seekTo(i);
                        }

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });


            }
        });
        holder.document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.is_online(context)) {
                    new DownloadTask(context, message.getFileModel().getFile());
                }
            }
        });
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, FullscreenActivity.class).putExtra("img", message.getFileModel().getFile()));
            }
        });

    }
    @Override
    public int getItemViewType(int position) {
        ChatMessage item = chatMessageList.get(position);
        if(item.getSender() == sender) {
            return TYPE_ONE;
        }
         else  {
            return TYPE_TWO;
        }
    }
    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_message, tv_date;
        public ImageView img,audio,document,pause,userimg;
        public SeekBar seekBar;
        LinearLayout audioayout;
        FrameLayout files;

        public ViewHolder(View view) {
            super(view);
            tv_message = (TextView) view.findViewById(R.id.message);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            img = (ImageView) view.findViewById(R.id.img);
            audio = (ImageView) view.findViewById(R.id.audio);
            document = (ImageView) view.findViewById(R.id.document);
            pause = (ImageView) view.findViewById(R.id.pause);
            userimg = (ImageView) view.findViewById(R.id.userimg);
            seekBar = (SeekBar) view.findViewById(R.id.seekbar);
            audioayout = (LinearLayout) view.findViewById(R.id.audioayout);
            files = (FrameLayout) view.findViewById(R.id.filess);

        }
    }
}
