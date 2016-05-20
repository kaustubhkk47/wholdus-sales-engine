package com.wholdus.salesengine.BuyerFilterPackage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.wholdus.salesengine.R;
import com.wholdus.salesengine.SaveContactsPackage.SaveContacts;

import java.util.ArrayList;

public class BuyerFilter extends AppCompatActivity {

    ExpandableListView buyerFilterListView;
    ArrayList<BuyerFilterModel> buyerFilterModelArrayList;
    BuyerFilterAdapter buyerFilterAdapter;
    Button filterConfirmButton;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_filter);

        mContext = getApplicationContext();

        buyerFilterListView = (ExpandableListView) findViewById(R.id.buyer_filter_main_list);
        filterConfirmButton = (Button) findViewById(R.id.buyer_filter_confirm_button);

        getBuyerFilterData();

        buyerFilterAdapter = new BuyerFilterAdapter(mContext, buyerFilterModelArrayList);
        buyerFilterListView.setAdapter(buyerFilterAdapter);

        filterConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext = getApplicationContext();
                Intent intent = new Intent(mContext, SaveContacts.class);
                startActivity(intent);

            }
        });

    }

    public void getBuyerFilterData(){
        //TODO: Use API here

        buyerFilterModelArrayList = new ArrayList<BuyerFilterModel>();

        for (int i=1; i<4;i++) {

            ArrayList<FilterConditionModel> filterConditionModels = new ArrayList<FilterConditionModel>();

            for (int j = 1; j < 5; j++) {
                FilterConditionModel filterConditionModel = new FilterConditionModel(j, "Condition", false);
                filterConditionModels.add(filterConditionModel);
            }

            BuyerFilterModel buyerFilterModel = new BuyerFilterModel(i, "Filter " + i,filterConditionModels);

            buyerFilterModelArrayList.add(buyerFilterModel);
        }
    }
}
