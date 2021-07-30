package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import javax.sql.StatementEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

//graph imports
import org.springframework.web.bind.annotation.GetMapping;

//auth0 login imports
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;

//for date formatting
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Controller
@SpringBootApplication
public class HiringGraphController {
  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  @GetMapping("/hiringGraph")
  String LoadHiringGraph(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {

    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM Hiring"));

      HashMap<String, Integer> hmap = new HashMap<String, Integer>();

      HashMap<String, Integer> hmap2 = new HashMap<String, Integer>();
      int var = 0;
      while (rs.next()) {

        String date = rs.getString("StartDate");
        Boolean x = rs.getBoolean("isFilled");

        if (!x) {
          hmap.put(date, (hmap.get(date) == null ? 0 : hmap.get(date)) + 1);
          // System.out.println(var + " " + temp);
        }

      }
      Map<String, Integer> m = new TreeMap<String, Integer>(hmap);
      ArrayList<Integer> w1 = new ArrayList<Integer>();
      ArrayList<String> w2 = new ArrayList<String>();
      for (Map.Entry mapElement : m.entrySet()) {
        String key = (String) mapElement.getKey();

        // Add some bonus marks
        // to all the students and print it
        int value = ((int) mapElement.getValue());

        w1.add(value);
        w2.add(key);
      }

      for (int i = 0; i < w1.size(); i++) {
        int val = 0;
        for (int j = i; j >= 0; j--) {
          val += w1.get(j);
        }
        m.put(w2.get(i), val);
      }

      ResultSet rs2 = stmt.executeQuery(("SELECT * FROM Employees"));
      // ArrayList<Position> dataList = new ArrayList<Position>();

      while (rs2.next()) {

        String date = rs2.getString("StartDate");
        Boolean x = rs2.getBoolean("isFilled");

        if (x) {
          hmap2.put(date, (hmap2.get(date) == null ? 0 : hmap2.get(date)) + 1);
          // System.out.println(var + " " + temp);
        } else
          hmap2.put(date, (hmap2.get(date) == null ? 0 : 0));

      }

      Map<String, Integer> m1 = new TreeMap<String, Integer>(hmap2);
      ArrayList<Integer> w11 = new ArrayList<Integer>();
      ArrayList<String> w22 = new ArrayList<String>();
      for (Map.Entry mapElement : m1.entrySet()) {
        String key = (String) mapElement.getKey();

        // Add some bonus marks
        // to all the students and print it
        int value = ((int) mapElement.getValue());

        w11.add(value);
        w22.add(key);
      }

      for (int i = 0; i < w11.size(); i++) {
        int val = 0;
        for (int j = i; j >= 0; j--) {
          val += w11.get(j);
        }
        m1.put(w22.get(i), val);
      }

      // model.put("Dates",m1.keySet());

      model.put("Values2", m1.values());

      model.put("Dates", m.keySet());
      model.put("Values", m.values());
      // model.put("Positions", dataList);
      // model.put("Names", a);
      // model.put("dates", m);

      // ArrayList<ArrayList<Integer>> finalDates = new
      // ArrayList<ArrayList<Integer>>();
      // ArrayList<Integer> emptyDates = new ArrayList<Integer>();

      // for (int i = 0; i < permamentDates.size(); i++) {
      // finalDates.add(emptyDates);
      // }
      // for (int i = 0; i < coopDates.size(); i++) {
      // finalDates.add(coopDates.get(i));
      // }

      // model.put("finalDates", finalDates);
      // model.put("permamentDates", permamentDates);

      return "HiringGraph";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  

  public String GetuserAuthenticationData(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    String defaultrole = "unverified";
    if (principal != null) {
      model.put("profile", principal.getClaims());
      String email = (String) principal.getClaims().get("email");
      System.out.println(email);
      try (Connection connection = dataSource.getConnection()) {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (email varchar(50),role varchar(10));");
        ResultSet rs = stmt.executeQuery(("SELECT * FROM users WHERE email='" + email + "'"));
        if (rs.next()) {
          model.put("userRole", rs.getString("role"));
          return rs.getString("role");
        } else {
          stmt.executeUpdate("INSERT INTO users (email,role) VALUES ('" + email + "','" + defaultrole + "');");
          model.put("userRole", defaultrole);
          return defaultrole;
        }
      } catch (Exception e) {
        model.put("message", e.getMessage());
        System.out.println(e);
        return "error";
      }
    } else {
      model.put("userRole", "Not Logged In");
      return "Not Logged In";
    }
    // Database calls for the role in question
  }

}
