package com.example.app1;

/**
 * This class displays all the information of one contact in particular
 */

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ContactCard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_card);

        // recover the contact ID
        Intent intent = getIntent();
        String CONTACT_ID = intent.getStringExtra("id");

        // get contact information
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PHONE_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String PHONE_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI,null,ID+ " = ? ",new String[]{CONTACT_ID},null);
        cursor.moveToNext();

        // Recover the name
        String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
        TextView textName = findViewById(R.id.name);
        textName.setText(name);

        // recover the phone number
        int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(HAS_PHONE_NUMBER));
        if (hasPhoneNumber > 0){
            Cursor phoneCursor = contentResolver.query(PHONE_URI, new String[]{NUMBER},PHONE_ID+" = ?",new String[]{CONTACT_ID},null);
            List<String> phoneList = new ArrayList<>();
            phoneCursor.moveToFirst();
            while (!phoneCursor.isAfterLast()){
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).replace(" ","");
                phoneList.add(phoneNumber);
                phoneCursor.moveToNext();
            }
            String phones = TextUtils.join("\n", phoneList);
            TextView textPhone = findViewById(R.id.phoneNumber);
            textPhone.setText(phones);
            phoneCursor.close();
        }

        // launch the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        // action for the favourite button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}