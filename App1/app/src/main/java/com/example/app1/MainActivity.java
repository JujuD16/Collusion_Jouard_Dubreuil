package com.example.app1;
/**
 * This class is the welcome page of the app
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Uri CONTENT_VCARD_URI = ContactsContract.Contacts.CONTENT_VCARD_URI;
    private Uri CONTENT_URI       = ContactsContract.Contacts.CONTENT_URI;
    private String ID             = ContactsContract.Contacts._ID;
    private String DISPLAY_NAME   = ContactsContract.Contacts.DISPLAY_NAME;
    private String LOOKUP_KEY     = ContactsContract.Contacts.LOOKUP_KEY;

    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    private List<ContactInfo> contactInfoList = new ArrayList<>();
    private static String[] PERMISSION_CONTACT = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    private static final int REQUEST_CONTACT = 1;

    private ContentResolver contentResolver;
    private Cursor cursor;
    private ArrayList<String> vCard = new ArrayList<String>();

    private int CHILD_ACTIVITY_CODE = 14;

    // This method initialises the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewInitialisation();

        requestContactsPermissions();

        // share the contacts with the App 2
        sendContact_vCard();
    }

    // This method initialises the recyclerView that permits to print the dynamic
    // list of all the contacts
    private void recyclerViewInitialisation(){
        contactsAdapter = new ContactsAdapter(this, contactInfoList);
        recyclerView.setAdapter(contactsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // This method requests the permission to download contacts from the device
    public void requestContactsPermissions(){
        boolean userDidNotGrantedTheApp =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED;

        if (userDidNotGrantedTheApp){
            boolean UIWithRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS);

            if (UIWithRationale){
                Snackbar.make(recyclerView, "Allow permissions to contacts", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,PERMISSION_CONTACT,REQUEST_CONTACT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,PERMISSION_CONTACT,REQUEST_CONTACT);
            }
        } else {
            getContactsInfo();
        }
    }

    // This method downloads contacts from the device
    private void getContactsInfo(){
        contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI,null,null,null,DISPLAY_NAME);

        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                ContactInfo contactInfo = new ContactInfo();

                // recover the ID
                String CONTACT_ID = cursor.getString(cursor.getColumnIndex(ID));
                contactInfo.setID(CONTACT_ID);

                // Recover the name
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                contactInfo.setName(name);

                // add the contact to the list
                contactInfoList.add(contactInfo);

                // recover the contact vCard for the App 2
                getContact_vCard(cursor);

            }
            contactsAdapter.notifyDataSetChanged();
        }
    }

    // This method rewrites the method invoked for every call on
    // ActivityCompat.requestPermissions(android.app.Activity, String[], int).
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults){
            if (result == PackageManager.PERMISSION_GRANTED){
                getContactsInfo();
            }
        }
    }

    // This method launches the page to create a new contact
    public void onClickPlus (View view) {
        Intent intent = new Intent(this,ContactCard.class);
        intent.putExtra("id", "");
        this.startActivity(intent);
    }

    // This method recovers all the contacts in vCard format
    private void getContact_vCard(Cursor cursor){

        String lookupKey = cursor.getString(cursor.getColumnIndex(LOOKUP_KEY));
        Uri vCardUri = Uri.withAppendedPath(CONTENT_VCARD_URI, lookupKey);
        AssetFileDescriptor assetFileDescriptor;
        String vCardString ="";

        if (Build.VERSION.SDK_INT >= 24) {

            FileInputStream inputStream = null;
            byte[] buffer = new byte[4096];
            ByteArrayOutputStream outputStream = null;
            try {
                assetFileDescriptor = contentResolver.openAssetFileDescriptor(vCardUri, "r");

                if (assetFileDescriptor != null) {
                    outputStream = new ByteArrayOutputStream();
                    int read = 0;
                    inputStream = assetFileDescriptor.createInputStream();
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                }
            } catch (FileNotFoundException e) {
                Log.v("TAG", "vCard for the contact " + lookupKey + " not found", e);
            } catch (IOException e) {
                Log.v("TAG", "Problem creating stream from the assetFileDescriptor.", e);
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException e) {
                }

                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                }
            }
            vCardString = new String(outputStream.toByteArray());
        }
        else {

            try {
                assetFileDescriptor = contentResolver.openAssetFileDescriptor(vCardUri, "r");
                FileInputStream fileInputStream = assetFileDescriptor.createInputStream();
                byte[] buf = new byte[(int) assetFileDescriptor.getDeclaredLength()];
                fileInputStream.read(buf);
                vCardString = new String(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.v("TAG", "vCard for the contact " + cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
        vCard.add(vCardString);
    }

    // This method sends all the contacts in vCard format to the App 2
    private void sendContact_vCard(){

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setClassName("com.example.app2", "com.example.app2.MainActivity");
        shareIntent.putStringArrayListExtra("vCards", vCard);
        shareIntent.setType("text/plain");

        startActivityForResult(shareIntent, CHILD_ACTIVITY_CODE );
    }

    // This method registers a callback for an Activity Result
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // recover response from App 2
        if (requestCode == CHILD_ACTIVITY_CODE && resultCode == RESULT_OK) {
            Log.v("TAG", "Contacts sent to App 2");
        }
    }
}