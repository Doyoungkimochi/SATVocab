package com.pamkim.sandwiches;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class MainActivity extends Activity {

    EditText mEditText;
    Button mButton;
    TextView mTextView;



    int REQUEST_CODE = 123;

    int currentTime;
    boolean timeStillRunning;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mEditText = (EditText) findViewById(R.id.editText);
        mButton = (Button) findViewById(R.id.button);
        mTextView = (TextView) findViewById(R.id.textView);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString();
                if (!text.equalsIgnoreCase("")) {
                    startService(new Intent(getBaseContext(), MyService.class));
                    launchVocab(mButton, text);
                }
            }
        });

    }




    public void launchVocab(View view, String text){
        Intent i = new Intent(this, VocabQuiz.class);

        i.putExtra("timer", text);

        startActivity(i);
    }







}
