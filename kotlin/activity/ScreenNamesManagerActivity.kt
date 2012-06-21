package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity
import android.widget.AdapterView
import android.view.View
import android.widget.Adapter
import android.widget.ArrayAdapter
import jp.katahirado.android.tsubunomi.Const
import jp.katahirado.android.tsubunomi.SharedManager
import android.content.Context
import jp.katahirado.android.tsubunomi.R
import android.os.Bundle
import jp.katahirado.android.tsubunomi.LowerCaseComparator
import android.widget.ListView
import android.widget.EditText
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.text.SpannableStringBuilder
import java.util.List
import java.util.ArrayList
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener

public class ScreenNamesManagerActivity() : Activity(), View.OnClickListener, AdapterView.OnItemClickListener {
  private val sharedManager = SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE)!!)
  private val screenNames = sharedManager.getScreenNames()
  private var adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, screenNames)
  private val screenNameText = findViewById(R.id.manage_screen_name_text) as EditText
  private val manageList = findViewById(R.id.screen_name_manage_list) as ListView

  override fun onCreate(savedInstanceState: Bundle?) {
    super<Activity>.onCreate(savedInstanceState)
    setContentView(R.layout.screen_names_manage)
    setTitle("${getString(R.string.app_name)} : Delete autocomplete word")

    val searchButton = findViewById(R.id.screen_name_search_button)
    searchButton!!.setOnClickListener(this)
    adapter.sort(LowerCaseComparator())

    manageList.setAdapter(adapter)
    manageList.setOnClickListener(this)
    manageList.requestFocus()
    manageList.setOnTouchListener(object: OnTouchListener {
      public override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        hideIME()
        return false
      }
    })
  }

  public override fun onClick(p0: View?) {
    if (p0!!.getId() == R.id.screen_name_search_button) {
      val builder = screenNameText.getText() as SpannableStringBuilder
      val query = builder.toString()
      adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, screenNamesFilter(query))
      adapter.sort(LowerCaseComparator())
      manageList.setAdapter(adapter)
      hideIME()
      screenNameText.setText("")
    }
  }


  public override fun onItemClick(adapterView: AdapterView<out Adapter?>?, view: View?, position: Int, id: Long) {
    val screenName = adapter.getItem(position)
    val builder = AlertDialog.Builder(this)
    builder.setMessage("${screenName} を削除してもよろしいですか？")!!.setCancelable(false)
    builder.setPositiveButton("Delete", object: OnClickListener {
      public override fun onClick(dialog: DialogInterface?, i: Int) {
        adapter.remove(screenName)
        screenNames.remove(screenName)
        val names = sharedManager.getScreenNames()
        names.remove(screenName)
        sharedManager.setScreenNames(names)
      }
    })
  }



  fun hideIME() {
    val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(screenNameText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
  }

  fun screenNamesFilter(query: String?): List<String> {
    if (query!!.isEmpty()) return sharedManager.getScreenNames()

    val list = ArrayList<String>()
    screenNames filter { it.toLowerCase().startsWith(query!!.toLowerCase()) } forEach { list.add(it) }
    return list
  }

}
