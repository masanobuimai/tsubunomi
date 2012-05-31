package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.LowerCaseComparator;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.SharedManager;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class UsersActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ArrayAdapter<String> adapter;
    private EditText userText;
    private ArrayList<String> screenNames;
    private ListView listView;
    private SharedManager sharedManager;
    private ArrayList<String> originalScreenNames;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);
        setTitle(getString(R.string.app_name) + " : 表示ユーザー一覧");
        listView = (ListView) findViewById(R.id.users_list);
        Button button = (Button) findViewById(R.id.users_screen_name_search_button);
        userText = (EditText) findViewById(R.id.users_screen_name_text);

        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        screenNames = sharedManager.getScreenNames();
        originalScreenNames = sharedManager.getScreenNames();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, screenNames);
        adapter.sort(new LowerCaseComparator());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        button.setOnClickListener(this);
        listView.requestFocus();
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideIME();
                return false;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(this, UserTimelineActivity.class);
        intent.putExtra(Const.SCREEN_NAME, adapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.users_screen_name_search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) userText.getText();
                String query = builder.toString();
                if (query.length() == 0) {
                    return;
                }
                screenNames = screenNamesFilter(query);
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, screenNames);
                adapter.sort(new LowerCaseComparator());
                listView.setAdapter(adapter);
                hideIME();
                userText.setText("");
                break;
        }
    }

    private ArrayList<String> screenNamesFilter(String query) {
        if (query.equals("*")) {
            return originalScreenNames;
        }
        ArrayList<String> list = new ArrayList<String>();
        for (String s : originalScreenNames) {
            if (s.toLowerCase().startsWith(query.toLowerCase())) {
                list.add(s);
            }
        }
        return list;
    }

    private void hideIME() {
        InputMethodManager manager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(userText.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}