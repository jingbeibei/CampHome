package com.nuc.camphome.mailbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.camphome.R;
import com.nuc.camphome.beans.Letter;
import com.nuc.camphome.utils.ActivityCollector;

public class LetterDetailedActivity extends AppCompatActivity {
    private TextView BarTitle;
    private ImageView BackImage;
    private Letter letter;
    private TextView letterTitleTV, letterTimeTV, letterContentTV, answerTV, answerTimeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_detailed);
        ActivityCollector.addActivity(this);
        letter = (Letter) getIntent().getSerializableExtra("letter");
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarTitle.setText("首长信箱");
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(LetterDetailedActivity.this);
            }
        });
        initView();

    }

    private void initView() {
        letterTitleTV = (TextView) findViewById(R.id.letter_title_tv);
        letterContentTV = (TextView) findViewById(R.id.letter_contents_tv);
        letterTimeTV = (TextView) findViewById(R.id.letter_entrytime_tv);
        answerTV = (TextView) findViewById(R.id.letter_answer_tv);
        answerTimeTV = (TextView) findViewById(R.id.letter_answertime_tv);
        letterTitleTV.setText(letter.getTitle());
        letterTimeTV.setText(letter.getEntryTime().substring(0, 10));
        letterContentTV.setText(letter.getContents());
        String answer = letter.getAnswer();
        String answertime = letter.getAnswerTime().substring(0, 10);
        if (answertime != null) {
            answerTV.setText(answer);
            answerTimeTV.setText(answertime);
        }

    }
}
