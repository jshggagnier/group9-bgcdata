package com.example;

import java.lang.String;

public class WorkItem {
    private String Id;
    private String ItemName;
    private String StartDate;
    private String EndDate;
    private String TeamsAssigned;
    private String FundingInformation;

    public String getId() {
        return this.Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getItemName() {
        return this.ItemName;
    }

    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }

    public String getStartDate() {
        return this.StartDate;
    }

    public void setStartDate(String StartDate) {
        this.StartDate = StartDate;
    }

    public String getEndDate() {
        return this.EndDate;
    }

    public void setEndDate(String EndDate) {
        this.EndDate = EndDate;
    }

    public String getTeamsAssigned() {
        return this.TeamsAssigned;
    }

    public void setTeamsAssigned(String TeamsAssigned) {
        this.TeamsAssigned = TeamsAssigned;
    }

    public String getFundingInformation() {
        return this.FundingInformation;
    }

    public void setFundingInformation(String FundingInformation) {
        this.FundingInformation = FundingInformation;
    }
}
