package com.wholdus.salesengine.SaveContactsPackage;

import android.Manifest;
import android.content.ContentProviderOperation;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wholdus.salesengine.ContactHandlerPackage.ContactsAdapter;
import com.wholdus.salesengine.ContactHandlerPackage.ContactsModel;
import com.wholdus.salesengine.Miscellenaeous.NetworkConnections;
import com.wholdus.salesengine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaveContacts extends AppCompatActivity {

    String LOG_TAG = "SaveContactsActivity";
    ArrayList<ContactsModel> contactsModelArrayList = new ArrayList<ContactsModel>();
    Context mContext;
    ListView contactsList;
    ContactsAdapter contactsAdapter;

    ArrayList<String> ContactNames = new ArrayList<>();

    TextView buyerCount;
    Button saveContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_contacts);

        mContext = getApplicationContext();

        getCurrentContacts();

        contactsList = (ListView) findViewById(R.id.contacts_list_view);
        getContactsList(contactsModelArrayList);



        saveContacts = (Button) findViewById(R.id.save_contacts_button);
        saveContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContactsList();
            }
        });

    }

    public void getContactsList(final ArrayList<ContactsModel> contactsModelArrayList){

        RequestQueue queue = Volley.newRequestQueue(this);

        String buyerGETURL = NetworkConnections.BUYER_URL + "?access_token=" + NetworkConnections.ACCESS_TOKEN
                + "&whatsapp_sharing_active=1";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, buyerGETURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String statusCode = response.getString("statusCode");
                            String body = response.getString("body");
                            JSONObject bodyJSON = new JSONObject(body);

                            if (statusCode.equals(NetworkConnections.CORRECT_RESPONSE_CODE)) {
                                populateContactList(body, contactsModelArrayList);

                            } else {
                                Log.w(LOG_TAG, "Status code was : " + statusCode);
                                String error = bodyJSON.getString("error");
                                Log.w(LOG_TAG, "Status code was : " + error);
                            }

                        } catch (JSONException e) {
                            Log.w(LOG_TAG, e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(LOG_TAG, error.toString());
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "version=1");
                return params;
            }
        };

        queue.add(getRequest);

    }

    public void populateContactList(String body, ArrayList<ContactsModel> contactsModelArrayList){

        JSONObject bodyJSON;
        JSONArray buyers;

        try {
            bodyJSON = new JSONObject(body);
            buyers = bodyJSON.getJSONArray("buyers");

            JSONObject buyerData;
            String buyerName;
            String buyerNumber;

            for (int i = 0; i < buyers.length(); i++) {
                buyerData = buyers.getJSONObject(i);
                buyerName = buyerData.getString("whatsapp_contact_name");
                buyerNumber = buyerData.getString("whatsapp_number");

                if(!ContactNames.contains(buyerName)){
                    ContactsModel contactsModel = new ContactsModel(i,buyerName,buyerNumber);
                    contactsModelArrayList.add(contactsModel);
                }

            }
        }
        catch (JSONException e){
            Log.w(LOG_TAG, e.toString());
        }

        assignAdapters();

    }

    public void assignAdapters(){

        contactsAdapter = new ContactsAdapter(mContext,contactsModelArrayList);
        contactsList.setAdapter(contactsAdapter);

        buyerCount = (TextView) findViewById(R.id.buyer_count_text_view);
        buyerCount.setText(contactsModelArrayList.size() + " buyers found");
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

                String nameToWrite = contactsModel.getContactName();
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

    public void getCurrentContacts(){
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                ContactNames.add(name);
            }
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
