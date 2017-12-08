package com.example.theodhor.speechapplication;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.AlarmClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{


    private EditText editText;
    private Button button;
    private TextToSpeech tts;
    private ArrayList<String> questions;
    private String name, surname, age, asName;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREFS = "prefs";
    private static final String NEW = "new";
    private static final String NAME = "name";
    private static final String AGE = "age";
    private static final String AS_NAME = "as_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(PREFS,0);
        editor = preferences.edit();
        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editText.getText().toString();
                dbCheck(url);
            }
        });
        findViewById(R.id.microphoneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen();
            }
        });
        loadQuestions();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Hello This is Umm!. Just a Rather Very Intelligent System. and me My name is Siri. Can I help you ?");
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

    }

    private void loadQuestions(){
        questions = new ArrayList<>();
        questions.clear();
        questions.add("Hello, what is your name?");
        questions.add("What is your surname?");
        questions.add("How old are you?");
        questions.add("That's all I had, thank you ");
    }
    private List<String> setQuestions(JSONArray questions) throws JSONException {
        this.questions = new ArrayList<>();
        if(0 < questions.length()){
            Toast.makeText(getBaseContext(),"We have "+ questions.length() +" questions", Toast.LENGTH_SHORT).show();
            speak("We have "+ questions.length() +" questions");
            for(int i =0;i<questions.length();i++){
                JSONObject q = questions.getJSONObject(i);
                speak(q.getString("que_title"));
                this.questions.add(q.getString("que_title"));
            }
        }
        return this.questions;
    }
    private List<String> setAnswer(JSONArray answer) throws JSONException {
        this.questions = new ArrayList<>();
        if(0 < answer.length()){
            Toast.makeText(getBaseContext(),"We have "+ answer.length() +" answers", Toast.LENGTH_SHORT).show();
            speak("We have "+ answer.length() +" answers");
            for(int i =0;i<answer.length();i++){
                JSONObject q = answer.getJSONObject(i);
                speak(q.getString("text"));
//                this.questions.add(q.getString("que_title"));
            }
        }
        return null; //this.questions;
    }

    private void listen(){
        speak("Start now ");
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(MainActivity.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String inSpeech = res.get(0);
                recognition(inSpeech);
            }
        }
    }

    private void recognition(String text){
        speak("you say " + text);
        answerOf(editText.getText().toString(),text);
//        Log.e("Speech",""+text);
//        String[] speech = text.split(" ");
//        if(text.contains("hello")||text.equals("hello")){
//            speak("Hi nice to see you");
//        }
//        //
//        if(text.contains("my name is")){
//            name = speech[speech.length-1];
//            Log.e("THIS", "" + name);
//            editor.putString(NAME,name).apply();
//            speak(questions.get(2));
//        }
//        //This must be the age
//        if(text.contains("years") && text.contains("old")){
//            String age = speech[speech.length-3];
//            Log.e("THIS", "" + age);
//            editor.putString(AGE, age).apply();
//        }
//
//        if(text.contains("what time is it")){
//            SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");//dd/MM/yyyy
//            Date now = new Date();
//            String[] strDate = sdfDate.format(now).split(":");
//            if(strDate[1].contains("00"))
//                strDate[1] = "o'clock";
//            speak("The time is " + sdfDate.format(now));
//
//        }
//
//        if(text.contains("wake me up at")){
//            speak(speech[speech.length-1]);
//            String[] time = speech[speech.length-1].split(":");
//            String hour = time[0];
//            String minutes = time[1];
//            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
//            i.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(hour));
//            i.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(minutes));
//            startActivity(i);
//            speak("Setting alarm to ring at " + hour + ":" + minutes);
//        }
//
//        if(text.contains("thank you")){
//            speak("Thank you too " + preferences.getString(NAME, null));
//        }
//
//        if(text.contains("how old am I")){
//            speak("You are "+preferences.getString(AGE,null)+" years old.");
//        }
//
//        if(text.contains("what is your name")){
//            String as_name = preferences.getString(AS_NAME,"");
//            if(as_name.equals(""))
//                speak("How do you want to call me?");
//            else
//                speak("My name is "+as_name);
//        }
//
//        if(text.contains("call you")){
//            String name = speech[speech.length-1];
//            editor.putString(AS_NAME,name).apply();
//            speak("I like it, thank you "+preferences.getString(NAME,null));
//        }
//
//        if(text.contains("what is my name")){
//            speak("Your name is "+preferences.getString(NAME,null));
//        }
    }
    void dbCheck(String url){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("username", "1"));
//        params.add(new BasicNameValuePair("password", "2"));
        GetSetPostURL23 getJsonFromURL23 = new GetSetPostURL23(this,url,params,new GetSetPostURL23Interface() {
            @Override
            public void onDownloadFinished(Context context, JSONArray result) throws JSONException {
                if(result.length() > 0){
                    JSONObject c = result.getJSONObject(0);
                    setQuestions(result);
                }else{
                    Toast.makeText(getBaseContext(),"Have not question!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onDownloadFinished(Context activity, JSONObject result) throws JSONException {
                Toast.makeText(getBaseContext(),"Object", Toast.LENGTH_SHORT).show();
                JSONArray q = result.getJSONArray("question");
                JSONArray a = result.getJSONArray("answer");
                setQuestions(q);
                speak("and");
                setAnswer(a);
            }
            @Override
            public void onDownloadFinished(Context context, String result) {
                Toast.makeText(getBaseContext(),result, Toast.LENGTH_SHORT).show();
            }
        });
        getJsonFromURL23.execute("show/getInitSpeechTest");
    }

    void answerOf(String url,String txt){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("text", txt));
//        params.add(new BasicNameValuePair("password", "2"));
        GetSetPostURL23 getJsonFromURL23 = new GetSetPostURL23(this,url,params,new GetSetPostURL23Interface() {
            @Override
            public void onDownloadFinished(Context context, JSONArray result) throws JSONException {
                if(result.length() > 0){
                    JSONObject c = result.getJSONObject(0);
                }else{
                    Toast.makeText(getBaseContext(),"Have not question!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onDownloadFinished(Context activity, JSONObject result) throws JSONException {
                Toast.makeText(getBaseContext(),"Object", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDownloadFinished(Context context, String result) {
                Toast.makeText(getBaseContext(),result, Toast.LENGTH_SHORT).show();
                speak(result);
            }
        });
        getJsonFromURL23.execute("show/answerOf");
    }
}
