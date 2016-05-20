package com.wholdus.salesengine.MainActivityPackage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wholdus.salesengine.BuyerFilterPackage.BuyerFilter;
import com.wholdus.salesengine.FetchProductDataPackage.FetchProductData;
import com.wholdus.salesengine.R;

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
                Intent intent = new Intent(mContext, BuyerFilter.class);
                startActivity(intent);
            }
        });
    }
}
