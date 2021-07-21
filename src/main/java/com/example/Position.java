package com.example;
public class Position
{
    public String Name;
    public String StartDate;
    public String Team;
    public String Role;
    public boolean isCoop;
    public boolean hasEndDate;
    public boolean isFilled;
    public String EndDate;
    public int serialID;

    public int getSerialID() {
        return this.serialID;
    }

    public void setSerialID(int serialID) {
        this.serialID = serialID;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getStartDate() {
        return this.StartDate;
    }

    public void setStartDate(String StartDate) {
        this.StartDate = StartDate;
    }

    public String getTeam() {
        return this.Team;
    }

    public void setTeam(String Team) {
        this.Team = Team;
    }

    public String getRole() {
        return this.Role;
    }

    public void setRole(String Role) {
        this.Role = Role;
    }

    public boolean isisCoop() {
        return this.isCoop;
    }

    public boolean getisCoop() {
        return this.isCoop;
    }

    public void setisCoop(boolean isCoop) {
        this.isCoop = isCoop;
    }

    public boolean isisFilled() {
        return this.isFilled;
    }

    public boolean getisFilled() {
        return this.isFilled;
    }

    public void setisFilled(boolean isFilled) {
        this.isFilled = isFilled;
    }

    public boolean ishasEndDate() {
        return this.hasEndDate;
    }

    public boolean gethasEndDate() {
        return this.hasEndDate;
    }

    public void sethasEndDate(boolean hasEndDate) {
        this.hasEndDate = hasEndDate;
    }

    public String getEndDate() {
        return this.EndDate;
    }

    public void setEndDate(String EndDate) {
        this.EndDate = EndDate;
    }
}