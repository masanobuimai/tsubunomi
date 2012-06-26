package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity
import android.view.View
import android.widget.AdapterView
import android.widget.Adapter
import jp.katahirado.android.tsubunomi.R
import android.widget.AutoCompleteTextView
import android.widget.ListView
import jp.katahirado.android.tsubunomi.SharedManager
import jp.katahirado.android.tsubunomi.Const
import android.content.Context
import jp.katahirado.android.tsubunomi.TweetManager
import android.widget.ArrayAdapter
import java.util.ArrayList
import jp.katahirado.android.tsubunomi.adapter.SearchListAdapter
import jp.katahirado.android.tsubunomi.dao.SearchWordDao
import jp.katahirado.android.tsubunomi.dao.DBOpenHelper
import android.os.Bundle
import android.widget.Button
import android.view.View.OnTouchListener
import android.view.MotionEvent
import jp.katahirado.android.tsubunomi.dialog.TweetDialog

public class SearchTimelineActivity(): Activity(), View.OnClickListener, AdapterView.OnItemClickListener {
  private val searchText = findViewById(R.id.search_text) as AutoCompleteTextView
  private val listView = findViewById(R.id.search_list) as ListView
  private val sharedManager = SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE)!!)
  private val tweetManager = TweetManager(sharedManager)
  private val searchWordDao = SearchWordDao(DBOpenHelper(this).getWritableDatabase())

  private var wordAdapter: ArrayAdapter<String>? = null
  private var doubleWordList: ArrayList<String>? = null
  private var searchListAdapter: SearchListAdapter? = null

  private var query = ""


  protected override fun onCreate(savedInstanceState: Bundle?) {
    super<Activity>.onCreate(savedInstanceState)
    setContentView(R.layout.searchtimeline)
    setTitle("${getString(R.string.app_name)} : Search")

    setWordAdapter()

    val searchButton = findViewById(R.id.search_button) as Button
    searchButton.setOnClickListener(this)

    listView.setOnItemClickListener(this)
    listView.setOnTouchListener(object: OnTouchListener {
      public override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        hideIME()
        return false
      }
    })

    val receiveHash = getIntent()?.getStringExtra(Const.HASH)
    if (receiveHash != null) {
      query = receiveHash
      getSearchTimelineTask()
    }
  }


  protected override fun onResume() {
    super<Activity>.onResume()
    setWordAdapter()
  }


  public override fun onItemClick(adapterView: AdapterView<out Adapter?>?, view: View?, position: Int, id: Long) {
//    val tweet = searchListAdapter!!.getItem(position)
//    TweetDialog(this, sharedManager, tweetManager, tweet).show()
    throw UnsupportedOperationException()
  }



  public override fun onClick(p0: View?) {
    throw UnsupportedOperationException()
  }


  fun setWordAdapter() {}

  fun hideIME() {}

  fun getSearchTimelineTask() {}
}
