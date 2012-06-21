package jp.katahirado.android.tsubunomi;

import android.content.SharedPreferences
import twitter4j.User
import twitter4j.auth.AccessToken
import twitter4j.TwitterAPIConfiguration
import java.util.Date
import java.util.Calendar
import java.util.ArrayList
import java.util.Arrays
import java.util.List

public class SharedManager(preferences: SharedPreferences) {
  private val sharedPreferences = preferences

  fun isConnected(): Boolean {
    return getPrefString(Const.PREF_KEY_TOKEN, null) != null
  }


  fun saveOAuth(accessToken: AccessToken) {
    val editor = sharedPreferences.edit()
    editor!!.putString(Const.PREF_KEY_TOKEN, accessToken.getToken())
    editor!!.putString(Const.PREF_KEY_SECRET, accessToken.getTokenSecret())
    editor!!.putString(Const.PREF_SCREEN_NAME, accessToken.getScreenName())
    editor!!.commit()
  }

  fun removeOAuth() {
      val editor = sharedPreferences.edit();
      editor!!.remove(Const.PREF_KEY_TOKEN);
      editor!!.remove(Const.PREF_KEY_SECRET);
      editor!!.remove(Const.PREF_SCREEN_NAME);
      editor!!.commit();
  }

  fun setTwitterAPIConfiguration(configuration: TwitterAPIConfiguration) {
      val editor = sharedPreferences.edit();
      editor!!.putLong(Const.CHECK_CONFIG_MILLI_SECOND, System.currentTimeMillis());
      editor!!.putInt(Const.CHARACTERS_RESERVED_PER_MEDIA, configuration.getCharactersReservedPerMedia());
      editor!!.putInt(Const.SHORT_URL_LENGTH, configuration.getShortURLLength());
      editor!!.putInt(Const.SHORT_URL_LENGTH_HTTPS, configuration.getShortURLLengthHttps());
      editor!!.putInt(Const.MAX_MEDIA_PER_UPLOAD, configuration.getMaxMediaPerUpload());
      editor!!.putInt(Const.PHOTO_SIZE_LIMIT, configuration.getPhotoSizeLimit());
      editor!!.commit();
  }


  fun isCheckConfigTime(): Boolean {
    val lastCheckTime = Date(sharedPreferences.getLong(Const.CHECK_CONFIG_MILLI_SECOND, 0))
    val calendar = Calendar.getInstance()
    calendar!!.setTime(lastCheckTime)
    calendar!!.add(Calendar.DATE, 1)
    val targetTime = calendar!!.getTime()
    val currentTime = Date(System.currentTimeMillis())
    return currentTime > targetTime
  }

  fun getScreenNames(): List<String> {
    val result = ArrayList<String>()
    for (item in getPrefString(Const.SCREEN_NAMES, null).split(",")) {
      result.add(item)
    }
    return result
  }

  fun setScreenNames(list: List<String>) {
    val listString = list.toString()
    val saveString = listString.substring(1, listString.length - 1)
    val editor = sharedPreferences.edit()!!
    editor.putString(Const.SCREEN_NAMES, saveString)
    editor.commit()
  }


  fun getPrefString(prefKey: String, s: String?): String {
    return sharedPreferences.getString(prefKey, s)!!
  }

  fun getPrefInt(prefKey: String, i: Int): Int {
    return sharedPreferences.getInt(prefKey, i)
  }

  fun setCurrentUser(user: User) {
    val editor = sharedPreferences.edit()
    editor!!.putString(Const.PREF_SCREEN_NAME, user.getScreenName())
    editor!!.commit()
  }

}
