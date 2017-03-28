package com.wholdus.salesengine.MainActivityPackage;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wholdus.salesengine.FetchProductDataPackage.FetchProductData;
import com.wholdus.salesengine.R;
import com.wholdus.salesengine.SaveContactsPackage.SaveContacts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button shareProductsButton = (Button) findViewById(R.id.share_products_button);
        shareProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext = getApplicationContext();
                Intent intent = new Intent(mContext, FetchProductData.class);
                startActivity(intent);
            }
        });

        Button newSharingListButton = (Button) findViewById(R.id.new_sharing_list_button);
        newSharingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext = getApplicationContext();
                Intent intent = new Intent(mContext, SaveContacts.class);
                startActivity(intent);
            }
        });

        Button fetchContactsButton = (Button) findViewById(R.id.fetch_contacts_button);
        fetchContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchContacts();
            }
        });
    }

    private void fetchContacts(){
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)) {
            try {
                String sentContacts = "0";
                ArrayList<Integer> newContacts = new ArrayList<>();
                String[] projection = {ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME_PRIMARY,
                        ContactsContract.CommonDataKinds.Contactables.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Contactables.DATA1};
                Cursor cursor = getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        projection,
                        "(" + ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 OR (" + ContactsContract.Data.MIMETYPE + "=? OR "
                                + ContactsContract.Data.MIMETYPE + "=?)) AND " +
                                ContactsContract.CommonDataKinds.Contactables.CONTACT_ID + " NOT IN (" + sentContacts + ")",
                        null,
                        ContactsContract.CommonDataKinds.Contactables.CONTACT_ID);
                JSONArray contacts = new JSONArray();
                JSONObject contact = new JSONObject();
                contact.put("contactID", 0);
                ArrayList<String> numbers = new ArrayList<>();
                ArrayList<String> mails = new ArrayList<>();
                while (cursor.moveToNext()) {
                    int contactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.CONTACT_ID));
                    if (contact.getInt("contactID") !=contactID){
                        if (contact.getInt("contactID") != 0 && numbers.size() > 0){
                            contact.put("numbersArr", new JSONArray(numbers));
                            contact.put("mailArr", new JSONArray(mails));
                            contacts.put(contact);
                        }
                        contact = new JSONObject();
                        numbers = new ArrayList<>();
                        mails = new ArrayList<>();
                        contact.put("contactID", contactID);
                        newContacts.add(contactID);
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME_PRIMARY));
                        contact.put("name", name);
                    }
                    String data = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA1));
                    if (data != null) {
                        data = data.replaceAll("\\s", "");
                        if (data.contains("@")) {
                            if (!mails.contains(data)) {
                                mails.add(data);
                            }
                        } else {
                            data = data.replaceFirst("\\+91", "");
                            data = data.replaceFirst("^0", "");
                            if (!numbers.contains(data)) {
                                numbers.add(data);
                            }
                        }
                    }

                }
                if (contact.getInt("contactID") != 0 && numbers.size() > 0){
                    contact.put("numbersArr", new JSONArray(numbers));
                    contact.put("mailArr", new JSONArray(mails));
                    contacts.put(contact);
                }
                cursor.close();
                sentContacts += TextUtils.join(",",newContacts);
            } catch (Exception e){
                Log.w("Error", e.toString());
            }
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, 0);
        }
    }
}
