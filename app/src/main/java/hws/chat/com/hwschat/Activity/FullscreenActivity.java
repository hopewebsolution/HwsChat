package hws.chat.com.hwschat.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import hws.chat.com.hwschat.Helperclass.Utility;
import hws.chat.com.hwschat.R;

public class FullscreenActivity extends Activity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen);
        imageView = (ImageView) findViewById(R.id.img);
        if (getIntent() != null) {
            Utility.Set_image(getIntent().getStringExtra("img"), imageView);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
