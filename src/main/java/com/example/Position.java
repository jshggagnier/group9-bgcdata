package com.example;
public class Position
{
    public String Name;
    public String StartDate;
    public String Team;
    public String Role;
    public boolean isFilled;
    public boolean isPermanent;
    public String EndDate;

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

    public boolean isIsFilled() {
        return this.isFilled;
    }

    public boolean getIsFilled() {
        return this.isFilled;
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
    }

    public boolean isIsPermanent() {
        return this.isPermanent;
    }

    public boolean getIsPermanent() {
        return this.isPermanent;
    }

    public void setIsPermanent(boolean isPermanent) {
        this.isPermanent = isPermanent;
    }

    public String getEndDate() {
        return this.EndDate;
    }

    public void setEndDate(String EndDate) {
        this.EndDate = EndDate;
    }
}