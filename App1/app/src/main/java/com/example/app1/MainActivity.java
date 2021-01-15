package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToContactCard (View view){
        Button button = (Button) view;
        String txt = button.getText().toString();
        Intent intent = new Intent(this,ContactCard.class);
        startActivity(intent);
    }
}