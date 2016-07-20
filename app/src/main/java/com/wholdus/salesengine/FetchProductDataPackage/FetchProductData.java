package com.wholdus.salesengine.FetchProductDataPackage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wholdus.salesengine.Miscellenaeous.NetworkConnections;
import com.wholdus.salesengine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FetchProductData extends AppCompatActivity {

    String LOG_TAG = "FetchProductData";

    EditText mProductID;
    Button fetchProductData;
    TextView mProductName;
    View mProductDisplay;
    ImageView mProductImageView;
    Button mShareProductData;

    Bitmap bitmap;

    int ProductID;
    String productName;
    String productURL;
    String minPricePerUnit;
    String imageURL;

    PdfDocument document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_product_data);

        mProductID = (EditText) findViewById(R.id.product_id_edit_text);
        fetchProductData = (Button) findViewById(R.id.fetch_product_data_button);

        mProductName = (TextView) findViewById(R.id.product_name_text_view);
        mProductDisplay = findViewById(R.id.product_display_layout);
        mProductImageView = (ImageView) findViewById(R.id.product_image_view);
        mShareProductData = (Button) findViewById(R.id.share_product_data_button);

        fetchProductData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptFetchData();
            }
        });

        mShareProductData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareProductData();
            }
        });
    }

    public void attemptFetchData(){

        mProductID.setError(null);

        String prodcutID = mProductID.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(prodcutID)){
            mProductID.setError("error_no_product_id");
            focusView = mProductID;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        }
        else {
            ProductID = Integer.valueOf(prodcutID);
            makeAPIcall();
        }
    }

    public void makeAPIcall(){
        RequestQueue queue = Volley.newRequestQueue(this);

        String productGETURL = NetworkConnections.PRODUCT_URL + "?productID=" + String.valueOf(ProductID);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, productGETURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String statusCode = response.getString("statusCode");
                            String body = response.getString("body");
                            JSONObject bodyJSON = new JSONObject(body);

                            if (statusCode.equals(NetworkConnections.CORRECT_RESPONSE_CODE)) {

                                displayProductData(body);

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
        );

        queue.add(getRequest);
    }

    public void displayProductData(String body){

        JSONObject bodyJSON;
        JSONArray products;

        try {
            bodyJSON = new JSONObject(body);
            products = bodyJSON.getJSONArray("products");

            JSONObject productData = products.getJSONObject(0);

            productName = productData.getString("display_name");
            productURL = "http://www.wholdus.com/" + productData.getString("url");
            minPricePerUnit = productData.getString("min_price_per_unit");

            String imageData = productData.getString("image");
            JSONObject imageJSON = new JSONObject(imageData);

            String imagePath = imageJSON.getString("image_path");
            String imageName = imageJSON.getString("image_name");
            int imageCount = imageJSON.getInt("image_count");
            JSONArray imageNumbers = imageJSON.getJSONArray("image_numbers");
            String imageNumber = imageNumbers.getString(0);

            String imagePathStart = imagePath.substring(0,5);

            if (imagePathStart.equals("static")){
                imagePath = imagePath.substring(7);
            }

            if(imageCount>0){
                imageURL = NetworkConnections.DEFAULT_URL + imagePath + "400x400/" +
                        imageName + "-" + imageNumber + ".jpg";
            }

            String imageString = "*" + productName + "*" + "\r\n" + "Only " +"*" +minPricePerUnit+"*" + " Rs. per piece!"
                    + "\r\n" + productURL ;

            mProductDisplay.setVisibility(View.VISIBLE);
            mProductName.setText(imageString);
            mProductImageView.setVisibility(View.VISIBLE);
            mShareProductData.setVisibility(View.VISIBLE);
            new DownloadImageTask(mProductImageView)
                    .execute(imageURL);

        }
        catch (JSONException e){
            return;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bitmap = result;
        }
    }

    public void shareProductData(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        } else {

            //String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, productName, null);
            //Uri bmpUri = Uri.parse(pathofBmp);

            //String pdfPath = "storage/6537-6363/Getting Started.pdf";
            File pdfFile = new File(createPDF());
            Uri bmpUri = Uri.fromFile(pdfFile);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            //shareIntent.setType("image/jpeg");

            ArrayList<Uri> imageUriArray = new ArrayList<Uri>();
            imageUriArray.add(bmpUri);
            imageUriArray.add(bmpUri);
            //shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);

            String imageString = "*" + productName + "*" + "\r\n" + "Only " +"*" +minPricePerUnit+"*" + " Rs. per piece!"
                    + "\r\n" + productURL ;
            //shareIntent.putExtra(Intent.EXTRA_TEXT, imageString);

            ArrayList<CharSequence> stringArray = new ArrayList<CharSequence>();
            String arr[] = {"Zero", "One"};
            Collection l = Arrays.asList(arr);
            stringArray.addAll(l);
            //shareIntent.putCharSequenceArrayListExtra(Intent.EXTRA_TEXT, stringArray);

            //shareIntent.putExtra(Intent.EXTRA_TEXT, productURL);
            //shareIntent.setType("text/plain");
            shareIntent.setPackage("com.whatsapp");

            shareIntent.setType("application/pdf");

            startActivity(shareIntent);
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

                    shareProductData();

                } else {

                    Toast.makeText(getApplicationContext(), R.string.grant_write_access, Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    public String createPDF(){
        String pdfName = "test.pdf";
        document = new PdfDocument();
        View content = findViewById(R.id.product_display_layout);
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(content.getWidth(), content.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        content.draw(page.getCanvas());
        document.finishPage(page);
        String externalStorage = Environment.getExternalStorageDirectory().getPath();
        File outputFile = new File(externalStorage, pdfName);
        try {
            outputFile.createNewFile();
            OutputStream out = new FileOutputStream(outputFile);
            document.writeTo(out);
            document.close();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile.getPath();
    }

}
