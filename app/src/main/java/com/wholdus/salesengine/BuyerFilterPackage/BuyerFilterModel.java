package com.wholdus.salesengine.BuyerFilterPackage;

import java.util.ArrayList;

/**
 * Created by kaustubh on 19/5/16.
 */
public class BuyerFilterModel {

    public int id;
    public String filterName;
    public ArrayList<FilterConditionModel> filterConditions;

    BuyerFilterModel(int id,String filterName, ArrayList<FilterConditionModel> filterConditions){
        this.id = id;
        this.filterName = filterName;
        this.filterConditions = filterConditions;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getFilterName(){
        return this.filterName;
    }

    public void setFilterName(String filterName){
        this.filterName = filterName;
    }

    public ArrayList<FilterConditionModel> getFilterConditions(){
        return this.filterConditions;
    }

    public void setFilterConditions(ArrayList<FilterConditionModel> filterConditions){
        this.filterConditions = filterConditions;
    }
}
