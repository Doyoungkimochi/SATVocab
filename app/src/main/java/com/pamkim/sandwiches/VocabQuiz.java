package com.pamkim.sandwiches;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

//add dependencies to your class
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.os.Handler;
import android.os.Message;


public class VocabQuiz extends AppCompatActivity {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d("thing","hi");
        }
    };


    TextView mDefnText;
    TextView mTimeView;
    RadioButton mWordCorrect;
    RadioButton mWordWrong1;
    RadioButton mWordWrong2;
    RadioGroup mAnswersGroup;
    Button mButtonSubmit;
    Button mButtonShowLinks;
    String cWord;
    String wWord1;
    String wWord2;
    boolean errorReceived;
    boolean linksNotShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        mTimeView = (TextView) findViewById(R.id.time_view);

        mButtonSubmit = (Button) findViewById(R.id.button_submit);
        mButtonShowLinks = (Button) findViewById(R.id.button_show_links);
        mButtonSubmit.setVisibility(View.INVISIBLE);
        mButtonShowLinks.setVisibility(View.INVISIBLE);

        mWordCorrect = (RadioButton) findViewById(R.id.word_correct_text);
        mWordWrong1 = (RadioButton) findViewById(R.id.word_wrong_text1);
        mWordWrong2 = (RadioButton) findViewById(R.id.word_wrong_text2);

        mAnswersGroup = (RadioGroup) findViewById(R.id.buttons_radiogroup);

        Bundle timerData = getIntent().getExtras();

        if(timerData==null){
            return;
        }
        String timerMessage = timerData.getString("timer");

        int seconds = Integer.valueOf(timerMessage);
        CountDownTimer countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) millisUntilFinished / 60000;
                int seconds = (int) millisUntilFinished % 60000 / 1000;
                String timeLeftText;
                timeLeftText = "";
                timeLeftText += minutes;
                timeLeftText += ":";
                if (seconds < 10) {
                    timeLeftText += "0";
                }
                timeLeftText += seconds;
                mTimeView.setText(timeLeftText);

            }

            @Override
            public void onFinish() {
                mTimeView.setText("Finished");
                stopService(new Intent(getBaseContext(), MyService.class));


            }
        }.start();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };

        Thread withLinks = new Thread(r);
        withLinks.start();

        startQuiz();

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = mAnswersGroup.getCheckedRadioButtonId();
                checkAnswers(selectedId);



            }
        });
        mButtonShowLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linksNotShowing==true){
                    mWordCorrect.setText("https://en.oxforddictionaries.com/definition/" + cWord);
                    mWordWrong1.setText("https://en.oxforddictionaries.com/definition/" + wWord1);
                    mWordWrong2.setText("https://en.oxforddictionaries.com/definition/" + wWord2);



                    mButtonShowLinks.setText("Show Words");
                    linksNotShowing=false;

                } else {
                    mButtonShowLinks.setText("Show Links");
                    setTexts();
                }
            }
        });
    }
    public void startQuiz(){
        Integer[] a = {0x7f070087, 0x7f070088, 0x7f070089};
        Collections.shuffle(Arrays.asList(a));

        mWordCorrect = (RadioButton) findViewById(a[0]);
        mWordWrong1 = (RadioButton) findViewById(a[1]);
        mWordWrong2 = (RadioButton) findViewById(a[2]);


        ensureDefnAvailable();
        if(errorReceived == true){
            while(errorReceived == true){
                ensureDefnAvailable();
            }
        }
        setTexts();
        mButtonSubmit.setVisibility(View.VISIBLE);
        mButtonShowLinks.setVisibility(View.VISIBLE);


    }


    private void ensureDefnAvailable(){
        getNewWords();
        new CallbackTask().execute(actualWord(cWord));
    }

    private void getNewWords(){
        RandomWordsFetcher correctWord = new RandomWordsFetcher();
        RandomWordsFetcher wrongWord1 = new RandomWordsFetcher();
        RandomWordsFetcher wrongWord2 = new RandomWordsFetcher();

        ensureGetDiffWords(correctWord.getString(), wrongWord1.getString(), wrongWord2.getString());
    }

    private void ensureGetDiffWords(String correctWord, String wrongWord1, String wrongWord2){

        Boolean sameWordsSelected = (correctWord.equals(wrongWord1) || correctWord.equals(wrongWord2) || wrongWord1.equals(wrongWord2));

        while(sameWordsSelected){
            correctWord = (new RandomWordsFetcher()).getString();
            wrongWord1 = (new RandomWordsFetcher()).getString();
            wrongWord2 = (new RandomWordsFetcher()).getString();
            sameWordsSelected = (correctWord.equals(wrongWord1) || correctWord.equals(wrongWord2) || wrongWord1.equals(wrongWord2));
        }

        cWord = correctWord;
        wWord1 = wrongWord1;
        wWord2 = wrongWord2;
    }

    private void setTexts(){
        mWordCorrect.setText(cWord);
        mWordWrong1.setText(wWord1);
        mWordWrong2.setText(wWord2);
        linksNotShowing = true;
    }



    private void checkAnswers(int selectedId) {
        if (selectedId == mWordCorrect.getId()) {
            Toast.makeText(this, "correct!", Toast.LENGTH_SHORT).show();
            startQuiz();
        } else {
            Toast.makeText(this, "wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private String actualWord(String rightWord) {
        final String language = "en";
        final String word = rightWord;
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }
    public void sendBroadcast(){
        Intent intent = new Intent();
        intent.setAction("com.pamkim.sandwiches");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    @Override
    protected void onPause(){

        sendBroadcast();

        super.onPause();

    }

    //in android calling network requests on the main thread forbidden by default
    //create class to do async job
    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //TODO: replace with your own app id and app key
            final String app_id = "8f2b6f3c";
            final String app_key = "55037759466e95e1f2349a6fbc9f22f8";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("app_id", app_id);
                urlConnection.setRequestProperty("app_key", app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                String jsonData = stringBuilder.toString();
                JSONObject obj = new JSONObject(jsonData);

                JSONArray resultsArr = obj.getJSONArray("results");
                String test = resultsArr.getJSONObject(0).toString();

                JSONArray lexicalEntriesArr = resultsArr.getJSONObject(0).getJSONArray("lexicalEntries");
                JSONArray entriesArr = lexicalEntriesArr.getJSONObject(0).getJSONArray("entries");
                JSONArray sensesArr = entriesArr.getJSONObject(0).getJSONArray("senses");
                JSONArray definitionsArr = sensesArr.getJSONObject(0).getJSONArray("definitions");

                errorReceived = false;
                String definition = definitionsArr.toString();

                return definition;

            } catch (Exception e) {
                e.printStackTrace();
                errorReceived = true;
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mDefnText = (TextView) findViewById(R.id.defn_text);
            if (result.startsWith("[\"")) {
                result = result.substring(2, result.length() - 2);
                mDefnText.setText(result);
            }
            System.out.println(result);
        }
    }
}



