package com.nuc.camphome.mailbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.camphome.R;
import com.nuc.camphome.utils.ActivityCollector;

public class LetterDetailedActivity extends AppCompatActivity {
    private TextView BarTitle;
    private ImageView BackImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_detailed);
        ActivityCollector.addActivity(this);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarTitle.setText("首长信箱");
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(LetterDetailedActivity.this);
            }
        });
    }
}
