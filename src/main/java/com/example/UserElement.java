package com.example;

import java.lang.String;

public class UserElement {
    public String email;
    public String role;
    public String getemail() {
        return this.email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getrole() {
        return this.role;
    }

    public void setrole(String role) {
        this.role = role;
    }
}
