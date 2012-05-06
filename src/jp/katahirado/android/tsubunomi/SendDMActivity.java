package jp.katahirado.android.tsubunomi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: yuichi_katahira
 */
public class SendDMActivity extends Activity {
    private EditText dmText;
    private Button dmButton;
    private String screenName;
    private Twitter twitter;
    private SharedManager sharedManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.senddm);

        sharedManager = SharedManager.getInstance();
        sharedManager.sharedPreferencesInit(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));

        dmText = (EditText) findViewById(R.id.dmText);
        dmButton = (Button) findViewById(R.id.dmButton);
        dmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDM();
            }
        });
        Intent intent;
        if(sharedManager.isConnected()){
            connectTwitter();
            dmButton.setEnabled(true);
        }else{
            dmButton.setEnabled(false);
            //Twitter連携設定が保持されていなかったら連携設定に飛ばす
            intent = new Intent(this,OAuthActivity.class);
            startActivity(intent);
        }
        intent = getIntent();
        String action = intent.getAction();
        if(Intent.ACTION_VIEW.equals(action)){
            Uri uri = intent.getData();
            if(uri !=null){
                //タイトルに◯◯にダイレクトメッセージと表示する
                screenName = uri.getLastPathSegment();
                setTitle(getString(R.string.app_name) + " : " + screenName + "にダイレクトメッセージ");
            }
        }
    }

    private void connectTwitter() {
        String oAuthAccessToken = sharedManager.getPrefString(Const.PREF_KEY_TOKEN, "");
        String oAuthAccessTokenSecret = sharedManager.getPrefString(Const.PREF_KEY_SECRET, "");
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        Configuration configuration=configurationBuilder
                .setOAuthConsumerKey(Const.CONSUMER_KEY)
                .setOAuthConsumerSecret(Const.CONSUMER_SECRET)
                .setOAuthAccessToken(oAuthAccessToken)
                .setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
                .build();
        twitter = new TwitterFactory(configuration).getInstance();
    }

    private void sendDM() {
        SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) dmText.getText();
        String dmString = spannableStringBuilder.toString();
        try {
            twitter.sendDirectMessage(screenName,dmString);
            Toast.makeText(this, "送信しました", Toast.LENGTH_SHORT).show();
        }catch (TwitterException e){
            e.printStackTrace();
        }finally {
            screenName=null;
            dmText.setText("");
            setTitle(R.string.app_name);
            InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(dmButton.getWindowToken(), 0);
        }
    }
}