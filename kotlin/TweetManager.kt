package jp.katahirado.android.tsubunomi

import com.twitter.Extractor
import twitter4j.Twitter
import twitter4j.conf.ConfigurationBuilder
import twitter4j.TwitterFactory
import twitter4j.Status
import twitter4j.UserMentionEntity
import java.util.List

class TweetManager(manager: SharedManager) {
  val extractor: Extractor = Extractor()
  val sharedManager: SharedManager = manager

  fun connectTwitter(): Twitter? {
    val oAuthAccessToken = sharedManager.getPrefString(Const.PREF_KEY_TOKEN, "")
    val oAuthAccessTokenSecret = sharedManager.getPrefString(Const.PREF_KEY_SECRET, "")
    val configurationBuilder = ConfigurationBuilder()
    configurationBuilder.setOAuthConsumerKey(Const.CONSUMER_KEY)
        ?.setOAuthConsumerSecret(Const.CONSUMER_SECRET)
        ?.setOAuthAccessToken(oAuthAccessToken)
        ?.setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
        ?.setMediaProvider("TWITTER")
    return TwitterFactory(configurationBuilder.build()).getInstance()
  }

  fun calculateShortURLsLength(text: String): Int {
    val urls = extractor.extractURLs(text)
    var diffCount = 0
    urls!!.iterator().forEach { url ->
      var shrinkLength
      var checkLength
      var urlLength = url!!.length
      if (url!!.startsWith("http://")) {
        shrinkLength = sharedManager.getPrefInt(Const.SHORT_URL_LENGTH, 0)
        checkLength = shrinkLength
      } else if (url!!.startsWith("https://")) {
        shrinkLength = sharedManager.getPrefInt(Const.SHORT_URL_LENGTH_HTTPS, 0)
        checkLength = shrinkLength
      } else {
        shrinkLength = sharedManager.getPrefInt(Const.SHORT_URL_LENGTH, 0)
        checkLength = shrinkLength - 7
      }

      if (urlLength >= checkLength) {
        diffCount += urlLength - shrinkLength
      } else {
        diffCount -= shrinkLength - urlLength
      }
    }
    return diffCount
  }


  fun buildReplyMention(status: Status): String {
      var result
      var tweetUserName = status.getUser()!!.getScreenName()
      result = tweetUserName
      val currentScreenName = sharedManager.getPrefString(Const.PREF_SCREEN_NAME, "")
      for (userMention in status.getUserMentionEntities()) {
          val mentionName = userMention!!.getScreenName()
          if (tweetUserName != mentionName && mentionName != currentScreenName) {
              result = "${result}@${mentionName}"
          }
      }
      return result
  }

}
