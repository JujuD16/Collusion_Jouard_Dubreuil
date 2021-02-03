package com.example.app2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Database mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new Database(this);
        this.updateAdapter();
    }

    private void updateAdapter(){
        List<Search> searchList = mydb.getAllContacts();
        String[] searches = new String[searchList.size()];
        for (int i = 0; i<searchList.size(); i++){
            searches[i] = searchList.get(i).getUrl();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, searches);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setOnItemClickListener((parent, view, position, id) -> {
            Object item = parent.getItemAtPosition(position);
            String url = (String) item;
            Integer rate = findRate(url);
            Toast toast = Toast.makeText(getApplicationContext(), url + " " + rate + "/5", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK,0,0);
            toast.show();
        });
    }

    private Integer findRate(String url) {
        List<Search> searchList = mydb.getAllContacts();
        for (int i = 0; i<searchList.size(); i++){
            if (searchList.get(i).getUrl().equals(url)){
                return searchList.get(i).getRate();
            }
        }
        return 0;
    }

    public void search(View view){
        EditText text = findViewById(R.id.autocomplete);
        String txt = text.getText().toString();
        SharedPreferences preferences =getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor =preferences.edit();
        editor.putString("lastSearch",txt);
        editor.commit();
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, txt);
        startActivityForResult(intent, 20);
        setResult(RESULT_OK, intent);
    }


    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20){
            SharedPreferences preferences =getPreferences(MODE_PRIVATE);
            String lastSearch = preferences.getString("lastSearch", null);
            if (!(this.searchExists(lastSearch))) {
                this.setVisible();
            }
        }
    }

    private boolean searchExists(String lastSearch) {
        List<Search> searchList = mydb.getAllContacts();
        for (int i = 0; i<searchList.size(); i++){
            if (searchList.get(i).getUrl().equals(lastSearch)){
                return true;
            }
        }
        return false;
    }

    public void storeSearch(View view){
        SharedPreferences preferences =getPreferences(MODE_PRIVATE);
        String lastSearch = preferences.getString("lastSearch", null);
        Button button = (Button) view;
        String txt = button.getText().toString();
        Integer rate = Integer.parseInt(txt);
        mydb.addHist(lastSearch, rate);
        this.setGone();
        this.updateAdapter();
        Toast.makeText(getApplicationContext(), lastSearch + " " + rate +"/5", Toast.LENGTH_LONG).show();
    }

    private void setVisible(){
        TextView viewNote = (TextView) findViewById(R.id.viewNote);
        LinearLayout note1 = (LinearLayout) findViewById(R.id.note1);
        LinearLayout note2 = (LinearLayout) findViewById(R.id.note2);
        viewNote.setVisibility(View.VISIBLE);
        note1.setVisibility(View.VISIBLE);
        note2.setVisibility(View.VISIBLE);
    }

    private void setGone(){
        TextView viewNote = (TextView) findViewById(R.id.viewNote);
        LinearLayout note1 = (LinearLayout) findViewById(R.id.note1);
        LinearLayout note2 = (LinearLayout) findViewById(R.id.note2);
        viewNote.setVisibility(View.GONE);
        note1.setVisibility(View.GONE);
        note2.setVisibility(View.GONE);
    }
}