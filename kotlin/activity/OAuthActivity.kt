package jp.katahirado.android.tsubunomi.activity

import android.app.Activity
import twitter4j.Twitter
import twitter4j.auth.RequestToken
import android.os.Bundle
import android.widget.Button
import jp.katahirado.android.tsubunomi.SharedManager
import jp.katahirado.android.tsubunomi.Const
import android.content.Context
import jp.katahirado.android.tsubunomi.R
import android.view.View.OnClickListener
import android.view.View
import twitter4j.auth.AccessToken
import twitter4j.TwitterException
import android.widget.Toast
import twitter4j.conf.ConfigurationBuilder
import twitter4j.TwitterFactory
import android.content.Intent
import android.net.Uri

public class OAuthActivity(): Activity() {
  private var twitter: Twitter? = null
  private var requestToken: RequestToken? = null

  private val sharedManager = SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE)!!)
  private val button = findViewById(R.layout.oauth) as Button

  protected override fun onCreate(savedInstanceState: Bundle?) {
    super<Activity>.onCreate(savedInstanceState)
    setContentView(R.layout.oauth)
    changeButtonState()

    button.setOnClickListener(object: OnClickListener {
      public override fun onClick(view: View?) {
        if (sharedManager.isConnected()) {
          sharedManager.removeOAuth()
          button.setText("Connect to Twitter")
        } else {
          startOAuth()
        }
      }
    })

    val uri = getIntent()!!.getData()
    if (uri.toString().startsWith(Const.CALLBACK_URL)) {
      val verifier = uri!!.getQueryParameter(Const.OAUTH_VERIFIER)
      try {
      val accessToken = twitter!!.getOAuthAccessToken(requestToken, verifier) as AccessToken
        sharedManager.saveOAuth(accessToken)
      } catch(ex: TwitterException) {
        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG)!!.show()
      }

    }
  }


  protected override fun onRestart() {
    super<Activity>.onRestart()
    changeButtonState()
  }


  fun changeButtonState() {
    button.setText(if (sharedManager.isConnected()) "Disconnect to Twitter" else "Connect to Twitter")
  }

  fun startOAuth() {
    val configurationBuilder = ConfigurationBuilder()
    configurationBuilder.setOAuthConsumerKey(Const.CONSUMER_KEY)
    configurationBuilder.setOAuthConsumerSecret(Const.CONSUMER_SECRET)
    twitter = TwitterFactory(configurationBuilder.build()).getInstance()

    requestToken = twitter!!.getOAuthRequestToken(Const.CALLBACK_URL)
    Toast.makeText(this, "Please authorize this app!", Toast.LENGTH_LONG)!!.show()
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("${requestToken!!.getAuthenticationURL()}&force_login=true")))
  }

}
