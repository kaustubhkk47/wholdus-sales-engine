package com.wholdus.salesengine.SaveContactsPackage;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wholdus.salesengine.ContactHandlerPackage.ContactsAdapter;
import com.wholdus.salesengine.ContactHandlerPackage.ContactsModel;
import com.wholdus.salesengine.R;

import java.util.ArrayList;

public class SaveContacts extends AppCompatActivity {

    String LOG_TAG = "SaveContactsActivity";

    Context mContext;
    ListView contactsList;
    ArrayList<ContactsModel> contactsModelArrayList;
    ContactsAdapter contactsAdapter;

    TextView buyerCount;
    EditText contactsListName;
    Button saveContacts;

    String ListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_contacts);

        mContext = getApplicationContext();

        contactsList = (ListView) findViewById(R.id.contacts_list_view);
        getContactsList();

        contactsAdapter = new ContactsAdapter(mContext,contactsModelArrayList);
        contactsList.setAdapter(contactsAdapter);

        buyerCount = (TextView) findViewById(R.id.buyer_count_text_view);
        buyerCount.setText(contactsModelArrayList.size() + " buyers found");

        contactsListName = (EditText) findViewById(R.id.contacts_list_edit_text);

        saveContacts = (Button) findViewById(R.id.save_contacts_button);
        saveContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSaveContacts();
            }
        });

    }

    public void getContactsList(){

        contactsModelArrayList = new ArrayList<ContactsModel>();

        ContactsModel contactsModel = new ContactsModel(0,"Iesh Dixit","7895161705");
        contactsModelArrayList.add(contactsModel);

        contactsModel = new ContactsModel(0,"Kaustubh Kulkarni","7417425646");
        contactsModelArrayList.add(contactsModel);

        contactsModel = new ContactsModel(0,"Aditya Jha","7417485915");
        contactsModelArrayList.add(contactsModel);

        contactsModel = new ContactsModel(0,"Kushagra Goyal","8791947227");
        contactsModelArrayList.add(contactsModel);

        contactsModel = new ContactsModel(0,"Manish Yadav","8586822777");
        contactsModelArrayList.add(contactsModel);

        contactsModel = new ContactsModel(0,"Aditya Rana","9873856409");
        contactsModelArrayList.add(contactsModel);
    }

    public void attemptSaveContacts(){
        contactsListName.setError(null);

        String listName = contactsListName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(listName)){
            contactsListName.setError("Enter list name");
            focusView = contactsListName;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        }
        else {
            ListName = listName;
            saveContactsList();
        }
    }

    public void saveContactsList(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CONTACTS}, 1);

        } else {

            for (int i = 0; i<contactsModelArrayList.size();i++) {

                ContactsModel contactsModel = contactsModelArrayList.get(i);

                String nameToWrite = contactsModel.getContactName() + " WholdusList " + ListName;
                String mobileNumber = contactsModel.getContactNumber();

                ArrayList<ContentProviderOperation> ops =
                        new ArrayList<ContentProviderOperation>();

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

                if (nameToWrite != null) {
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    nameToWrite).build());
                }

                if (mobileNumber != null) {
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
                }

                try {
                    mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            Toast.makeText(mContext, "Contacts added.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    saveContactsList();

                } else {

                    Toast.makeText(getApplicationContext(), R.string.grant_write_access, Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}
