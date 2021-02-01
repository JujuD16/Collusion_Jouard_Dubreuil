package com.example.app1;

/**
 * This class displays all the information of one contact in particular
 */

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ContactCard extends AppCompatActivity {

    private static final Uri CONTENT_URI         = ContactsContract.Contacts.CONTENT_URI;
    private static final Uri PHONE_URI           = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private static final Uri EMAIL_URI           = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    private static final Uri ADDR_URI            = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
    private static final String ID               = ContactsContract.Contacts._ID;
    private static final String DISPLAY_NAME     = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String EMAIL            = ContactsContract.CommonDataKinds.Email.DATA;
    private static final String STREET           = ContactsContract.CommonDataKinds.StructuredPostal.STREET;
    private static final String PHONE_ID         = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private static final String EMAIL_ID         = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
    private static final String ADDR_ID          = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;
    private static final String NUMBER           = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String FAVORITE         = ContactsContract.Contacts.STARRED;

    private String CONTACT_ID;
    private String name;
    private int hasPhoneNumber;
    private String phones;
    private String emails;
    private String addr;
    int favoriteContact;
    private List<String> phoneList = new ArrayList<>();
    private List<String> emailList = new ArrayList<>();

    private Intent intentRecovered;
    private ContentResolver contentResolver;
    private Cursor cursor;

    private TextView displayName;
    private TextView displayPhone;
    private TextView displayEmail;
    private TextView displayAddr;

    private Toolbar toolbar;
    private CollapsingToolbarLayout toolBarLayout;
    private FloatingActionButton fab;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_card);

        // recover the contact ID
        intentRecovered = getIntent();
        CONTACT_ID = intentRecovered.getStringExtra("id");
        contentResolver = getContentResolver();

        // Recover the name
        cursor = contentResolver.query(CONTENT_URI,null,ID+ " = ? ",new String[]{CONTACT_ID},null);
        cursor.moveToFirst();
        name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
        displayName = findViewById(R.id.name);
        displayName.setText(name);

        // Recover if it is a favourite contact or not
        favoriteContact = cursor.getInt(cursor.getColumnIndex(FAVORITE));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (favoriteContact == 1) {
            fab.setForeground(getResources().getDrawable(android.R.drawable.btn_star_big_on));
        }
        else {
            fab.setForeground(getResources().getDrawable(android.R.drawable.btn_star_big_off));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // recover one or many phone numbers
        hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(HAS_PHONE_NUMBER));
        if (hasPhoneNumber > 0){
            cursor = contentResolver.query(PHONE_URI, new String[]{NUMBER},PHONE_ID+" = ?",new String[]{CONTACT_ID},null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                phoneList.add(cursor.getString(cursor.getColumnIndex(NUMBER))); // .replace(" ","")
                cursor.moveToNext();
            }
            phones = TextUtils.join("\n", phoneList);
            displayPhone = findViewById(R.id.phoneNumber);
            displayPhone.setText(phones);
            cursor.close();
        }

        // Recover the postal address
        cursor = contentResolver.query(ADDR_URI,null,ADDR_ID+ " = ? ",new String[]{CONTACT_ID},null);
        if (cursor.moveToNext()) {
            addr = cursor.getString(cursor.getColumnIndex(STREET));
            displayAddr = findViewById(R.id.postalAddrRoad);
            displayAddr.setText(addr);
        }

        // Recover one or many mails
        cursor = contentResolver.query(EMAIL_URI, null, EMAIL_ID + " = ?", new String[]{CONTACT_ID}, null);
        while (cursor.moveToNext()) {
            emailList.add(cursor.getString(cursor.getColumnIndex(EMAIL)));
        }
        if (emailList.size() > 0) {
            emails = TextUtils.join("\n", emailList);
            displayEmail = findViewById(R.id.mailAddr);
            displayEmail.setText(emails);
            cursor.close();
        }

        // launch the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
    }

    // This method permits to open Google Maps to have the route to the contact address
    public void onClickMap (View view) {
        if (!addr.isEmpty()) {
            Uri mapsUri = Uri.parse("google.navigation:q="+addr);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    // This method permits to call directly the first number of the contact
    public void onClickPhone (View view) {
        if (phoneList.size() > 0){
                Uri phoneUri = Uri.parse("tel:"+phoneList.get(0));
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, phoneUri);
                startActivity(phoneIntent);
        }
    }

    // This method permits to write a mail directly the first number of the contact
    public void onClickMail (View view) {
        if (emailList.size() > 0){
            Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
            mailIntent.setData(Uri.parse("mailto:"));
            mailIntent.putExtra(Intent.EXTRA_EMAIL, emailList.get(0));
            startActivity(mailIntent);
        }
    }
}