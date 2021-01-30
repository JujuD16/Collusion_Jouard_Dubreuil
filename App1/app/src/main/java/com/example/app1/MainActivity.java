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
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    private List<ContactInfo> contactInfoList = new ArrayList<>();
    private static String[] PERMISSION_CONTACT = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    private static final int REQUEST_CONTACT = 1;

    // This method initialise the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewInitialisation();

        requestContactsPermissions();
    }

    // This method initialises the recyclerView that permits to print the dynamic
    // list of all the contacts
    private void recyclerViewInitialisation(){
        contactsAdapter = new ContactsAdapter(this, contactInfoList);
        recyclerView.setAdapter(contactsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // This method request the permission to download contacts from the device
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
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI,null,null,null,DISPLAY_NAME);

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
            }
            contactsAdapter.notifyDataSetChanged();
        }
    }

    // This method rewrite the method invoked for every call on
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
}