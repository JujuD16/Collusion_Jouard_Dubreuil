package com.example.app1;

/**
 * This class displays all the information of one contact in particular
 */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ContactCard extends AppCompatActivity {

    private static final String ID               = ContactsContract.Contacts._ID;
    private static final Uri CONTENT_URI         = ContactsContract.Contacts.CONTENT_URI;
    private static final String DISPLAY_NAME     = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String FAVORITE         = ContactsContract.Contacts.STARRED;
    private static final String NAME_TYPE        = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;
    private static final String NAME             = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME;

    private static final String EMAIL_ID         = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
    private static final Uri EMAIL_URI           = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    private static final String EMAIL_ADDRESS    = ContactsContract.CommonDataKinds.Email.ADDRESS;
    private static final String EMAIL            = ContactsContract.CommonDataKinds.Email.DATA;
    private static final String EMAIL_TYPE       = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;

    private static final String ADDR_ID          = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;
    private static final Uri ADDR_URI            = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
    private static final String POSTAL_ADDRESS    = ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;
    private static final String STREET           = ContactsContract.CommonDataKinds.StructuredPostal.STREET;
    private static final String ADDR_TYPE       = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;

    private static final String PHONE_ID         = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private static final Uri PHONE_URI           = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private static final String NUMBER           = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String PHONE_TYPE       = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;

    private static final Uri PROVIDER = android.provider.ContactsContract.Data.CONTENT_URI;
    private ArrayList<android.content.ContentProviderOperation> ops = new ArrayList<android.content.ContentProviderOperation>();

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
    private ContentValues contentValues = new ContentValues();

    private Toolbar toolbar;
    private CollapsingToolbarLayout toolBarLayout;
    private FloatingActionButton fab;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_card);

        displayName   = findViewById(R.id.name);
        displayPhone  = findViewById(R.id.phoneNumber);
        displayEmail  = findViewById(R.id.mailAddr);
        displayAddr   = findViewById(R.id.postalAddr);

        // recover the contact ID
        intentRecovered = getIntent();
        CONTACT_ID = intentRecovered.getStringExtra("id");
        contentResolver = getContentResolver();

        // Recover the name
        cursor = contentResolver.query(CONTENT_URI,null,ID+ " = ? ",new String[]{CONTACT_ID},null);
        cursor.moveToFirst();
        name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
        displayName.setText(name);

        // Recover if it is a favourite contact or not
        fab = (FloatingActionButton) findViewById(R.id.fab);
        favoriteContact = cursor.getInt(cursor.getColumnIndex(FAVORITE));
        if (favoriteContact == 1) {
            fab.setForeground(getResources().getDrawable(android.R.drawable.btn_star_big_on));
        }
        else {
            fab.setForeground(getResources().getDrawable(android.R.drawable.btn_star_big_off));
        }
        // Modify if it is a favourite contact or not
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favoriteContact == 1) {
                    favoriteContact = 0;
                    contentValues.put(FAVORITE, favoriteContact);
                    contentResolver.update(CONTENT_URI, contentValues, ID + "= ?", new String[]{CONTACT_ID});
                    fab.setForeground(getResources().getDrawable(android.R.drawable.btn_star_big_off));
                    contentValues.clear();
                }
                else {
                    favoriteContact = 1;
                    contentValues.put(FAVORITE, favoriteContact);
                    contentResolver.update(CONTENT_URI, contentValues, ID + "= ?", new String[]{CONTACT_ID});
                    fab.setForeground(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                    contentValues.clear();
                }
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
            displayPhone.setText(phones);
            cursor.close();
        }

        // Recover the postal address
        cursor = contentResolver.query(ADDR_URI,null,ADDR_ID+ " = ? ",new String[]{CONTACT_ID},null);
        if (cursor.moveToNext()) {
            addr = cursor.getString(cursor.getColumnIndex(STREET));
            displayAddr.setText(addr);
        }

        // Recover one or many mails
        cursor = contentResolver.query(EMAIL_URI, new String[]{EMAIL_ADDRESS}, EMAIL_ID + " = ?", new String[]{CONTACT_ID}, null);
        while (cursor.moveToNext()) {
            emailList.add(cursor.getString(cursor.getColumnIndex(EMAIL)));
        }
        if (emailList.size() > 0) {
            emails = TextUtils.join("\n", emailList);
            displayEmail.setText(emails);
            cursor.close();
        }

        // launch the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        toolBarLayout.setTitle(getTitle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        displayName   = findViewById(R.id.name);
        displayPhone  = findViewById(R.id.phoneNumber);
        displayEmail  = findViewById(R.id.mailAddr);
        displayAddr   = findViewById(R.id.postalAddr);

        name    = displayName.getText().toString();
        emails  = displayEmail.getText().toString();
        phones  = displayPhone.getText().toString();
        addr    = displayAddr.getText().toString();

        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

        if(!name.equals("")) {
            ops.add(android.content.ContentProviderOperation.newUpdate(PROVIDER)
                    .withSelection(where,new String[]{CONTACT_ID, NAME_TYPE})
                    .withValue(NAME, name)
                    .build());
        }
        if (!emails.equals(R.id.mailAddr)) {
                ops.add(android.content.ContentProviderOperation.newUpdate(PROVIDER)
                        .withSelection(where,new String[]{CONTACT_ID, EMAIL_TYPE})
                        .withValue(EMAIL, emails)
                        .build());
        }
        if(!phones.equals(R.id.phoneNumber)) {
            ops.add(android.content.ContentProviderOperation.newUpdate(PROVIDER)
                    .withSelection(where,new String[]{CONTACT_ID, PHONE_TYPE})
                    .withValue(NUMBER, phones)
                    .build());
        }
        if(!addr.equals(R.id.postalAddr)) {
            ops.add(android.content.ContentProviderOperation.newUpdate(PROVIDER)
                    .withSelection(where,new String[]{CONTACT_ID, ADDR_TYPE})
                    .withValue(POSTAL_ADDRESS, addr)
                    .build());
        }

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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