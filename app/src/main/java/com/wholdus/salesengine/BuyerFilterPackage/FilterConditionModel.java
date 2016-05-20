package com.wholdus.salesengine.BuyerFilterPackage;

/**
 * Created by kaustubh on 19/5/16.
 */
public class FilterConditionModel {

    public int id;
    public String conditionName;
    public boolean conditionApplied;

    public FilterConditionModel(int id, String conditionName, boolean conditionApplied){
        this.id = id;
        this.conditionName = conditionName;
        this.conditionApplied = conditionApplied;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getConditionName(){
        return this.conditionName;
    }

    public void setConditionName(String condtionName){
        this.conditionName = condtionName;
    }

    public boolean getConditionApplied(){
        return this.conditionApplied;
    }

    public void setConditionApplied(boolean conditionApplied){
        this.conditionApplied = conditionApplied;
    }
}
