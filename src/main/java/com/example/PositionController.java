package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

//graph imports
import org.springframework.web.bind.annotation.GetMapping;

//auth0 login imports
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;

//for date formatting
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@SpringBootApplication
public class PositionController {
  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  @GetMapping("/PositionSubmit")
  String LoadFormPosition(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    String Role = GetuserAuthenticationData(model, principal);
    if (Role.equals("unverified") || Role.equals("viewonly")) {
      model.put("message",
          "Unauthorized user: Contact your Administrator to grant you permissions to edit the database");
      return "error";
    }
    Position position = new Position();
    model.put("Position", position);
    return "PositionSubmit";
  }

  // Editing positions
  @GetMapping(path = "/positionedit/{serialID}")
  public String deleteUserData(Map<String, Object> model, @PathVariable int serialID,
      @AuthenticationPrincipal OidcUser principal) {
    String Role = GetuserAuthenticationData(model, principal);
    if (Role.equals("unverified") || Role.equals("viewonly")) {
      model.put("message",
          "Unauthorized user: Contact your Administrator to grant you permissions to edit the database");
      return "error";
    }
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "SELECT * FROM Employees WHERE id = '" + serialID + "' ";
      ResultSet rs = stmt.executeQuery(sql);

      rs.next();
      Position obj = new Position();
      obj.setName(rs.getString("name"));
      obj.setTeam(rs.getString("team"));
      obj.setRole(rs.getString("role"));
      obj.setStartDate(rs.getString("StartDate"));
      obj.setEndDate(rs.getString("EndDate"));
      obj.sethasEndDate(rs.getBoolean("hasEndDate"));
      obj.setisCoop(rs.getBoolean("isCoop"));
      obj.setisFilled(rs.getBoolean("isFilled"));
      obj.setSerialID(serialID);
      model.put("target", obj);

      if (rs.next() != false) {
        model.put("message",
            "Error: Two identical Serial ID's, Database is invalid/has been modified! Contact Administrator with this error!");
        return "error";
      }
      return "PositionEdit";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/positionDelete/{nid}")
  String DeletePosition(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal,
      @PathVariable String nid) {
    String Role = GetuserAuthenticationData(model, principal);
    if (Role.equals("unverified") || Role.equals("viewonly")) {
      model.put("message",
          "Unauthorized user: Contact your Administrator to grant you permissions to edit the database");
      return "error";
    }
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "DELETE FROM employees " + "WHERE (id='" + nid + "')";
      stmt.executeUpdate(sql);

      return "redirect:/viewPositions";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/viewPositions")
  String viewPositions(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    String Role = GetuserAuthenticationData(model, principal);
    if (Role.equals("unverified")) {
      model.put("message",
          "Unauthorized user: Contact your Administrator to grant you permissions to view the database");
      return "error";
    }
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS Employees (id serial,name varchar(20),team varchar(20), role varchar(20),StartDate DATE,EndDate DATE, hasEndDate varchar(10), isCoop varchar(10), isFilled varchar(10))");
      ResultSet rs = stmt.executeQuery(("SELECT * FROM Employees"));
      ArrayList<Position> dataList = new ArrayList<Position>();
      // ArrayList<String> a = new ArrayList<String>();
      ArrayList<String> notCoopNames = new ArrayList<String>();
      ArrayList<String> coopNames = new ArrayList<String>();

      ArrayList<ArrayList<Integer>> m = new ArrayList<ArrayList<Integer>>();
      ArrayList<ArrayList<Long>> coopDatesMilli = new ArrayList<ArrayList<Long>>();
      ArrayList<ArrayList<Long>> permamentDatesMilli = new ArrayList<ArrayList<Long>>();

      // String myDateForTest = "";
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

      while (rs.next()) {
        Position obj = new Position();
        obj.setName(rs.getString("name"));
        obj.setTeam(rs.getString("team"));
        obj.setRole(rs.getString("role"));
        obj.setStartDate(rs.getString("StartDate"));
        obj.setEndDate(rs.getString("EndDate"));
        obj.sethasEndDate(rs.getBoolean("hasEndDate"));
        obj.setisCoop(rs.getBoolean("isCoop"));
        obj.setisFilled(rs.getBoolean("isFilled"));
        obj.setSerialID(rs.getInt("id"));

        dataList.add(obj);
        System.out.println(obj.Name);

        String x = rs.getString("name");
        // a.add(x);
        ArrayList<Integer> t = new ArrayList<Integer>();
        String d = rs.getString("startdate").substring(5, 7);
        String e = rs.getString("EndDate").substring(5, 7);

        int d1 = Integer.parseInt(d);
        int d2 = Integer.parseInt(e);

        t.add(d1);
        t.add(d2);

        // m.add(t);
        ArrayList<Long> millisecDates = new ArrayList<Long>();

        Date dateStart = sdf.parse(rs.getString("StartDate"));
        long millisStart = dateStart.getTime();

        Date dateEnd = sdf.parse(rs.getString("EndDate"));
        long millisEnd = dateEnd.getTime();

        millisecDates.add(millisStart);
        if (rs.getBoolean("hasEndDate")) {
          millisecDates.add(millisEnd);
        }

        if (obj.getisCoop()) {
          coopDatesMilli.add(millisecDates);
          coopNames.add(x);
        } else {
          permamentDatesMilli.add(millisecDates);
          notCoopNames.add(x);
        }
      }

      model.put("Positions", dataList);
      model.put("dates", m);

      ArrayList<ArrayList<Long>> finalDates = new ArrayList<ArrayList<Long>>();
      ArrayList<Long> emptyDates = new ArrayList<Long>();

      for (int i = 0; i < permamentDatesMilli.size(); i++) {
        finalDates.add(emptyDates);
      }
      for (int i = 0; i < coopDatesMilli.size(); i++) {
        finalDates.add(coopDatesMilli.get(i));
      }

      ArrayList<String> allOrderedNames = new ArrayList<String>();
      for (int i = 0; i < notCoopNames.size(); i++) {
        allOrderedNames.add(notCoopNames.get(i));
      }
      for (int i = 0; i < coopNames.size(); i++) {
        allOrderedNames.add(coopNames.get(i));
      }

      model.put("finalDates", finalDates);
      model.put("permamentDates", permamentDatesMilli);
      model.put("Names", allOrderedNames);

      return "PositionView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  // submitting data into database
  @PostMapping(path = "/PositionSubmit", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handlePositionSubmit(Map<String, Object> model, Position pos) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS Employees (id serial,name varchar(20),team varchar(20), role varchar(20),StartDate DATE,EndDate DATE, hasEndDate varchar(10), isCoop varchar(10), isFilled varchar(10))");

      String sql = "INSERT INTO Employees (name,team,role,StartDate,EndDate,hasEndDate,isCoop,isFilled) VALUES ('"
          + pos.getName() + "','" + pos.getTeam() + "','" + pos.getRole() + "','" + pos.getStartDate() + "','"
          + pos.getEndDate() + "','" + pos.gethasEndDate() + "','" + pos.getisCoop() + "','" + pos.getisFilled() + "')";
      stmt.executeUpdate(sql);
      Statement stmt3 = connection.createStatement();
      stmt3.executeUpdate(
          "CREATE TABLE IF NOT EXISTS HIRING (id serial,name varchar(20),team varchar(20), role varchar(20),StartDate DATE,EndDate DATE, hasEndDate varchar(10), isCoop varchar(10), isFilled varchar(10))");

      String sql3 = "INSERT INTO HIRING (name,team,role,StartDate,EndDate,hasEndDate,isCoop,isFilled) VALUES ('"
          + pos.getName() + "','" + pos.getTeam() + "','" + pos.getRole() + "','" + pos.getStartDate() + "','"
          + pos.getEndDate() + "','" + pos.gethasEndDate() + "','" + pos.getisCoop() + "','" + pos.getisFilled() + "')";

      stmt.executeUpdate(sql3);

      Statement stmt2 = connection.createStatement();
      stmt2.executeUpdate(
          "CREATE TABLE IF NOT EXISTS Hiring (id serial,name varchar(20),team varchar(20), role varchar(20),StartDate DATE,EndDate DATE, hasEndDate varchar(10), isCoop varchar(10), isFilled varchar(10))");

      String sql2 = "INSERT INTO Hiring (name,team,role,StartDate,EndDate,hasEndDate,isCoop,isFilled) VALUES ('"
          + pos.getName() + "','" + pos.getTeam() + "','" + pos.getRole() + "','" + pos.getStartDate() + "','"
          + pos.getEndDate() + "','" + pos.gethasEndDate() + "','" + pos.getisCoop() + "','" + pos.getisFilled() + "')";

      stmt2.executeUpdate(sql2);

      ResultSet rs = stmt.executeQuery(("SELECT * FROM Employees"));
      ArrayList<Position> dataList = new ArrayList<Position>();

      // ArrayList<String> a = new ArrayList<String>();
      ArrayList<String> notCoopNames = new ArrayList<String>();
      ArrayList<String> coopNames = new ArrayList<String>();

      ArrayList<ArrayList<Integer>> m = new ArrayList<ArrayList<Integer>>();

      ArrayList<ArrayList<Integer>> coopDates = new ArrayList<ArrayList<Integer>>();
      ArrayList<ArrayList<Integer>> permamentDates = new ArrayList<ArrayList<Integer>>();

      while (rs.next()) {
        Position obj = new Position();
        obj.setName(rs.getString("name"));
        obj.setTeam(rs.getString("team"));
        obj.setRole(rs.getString("role"));
        obj.setStartDate(rs.getString("StartDate"));
        obj.setEndDate(rs.getString("EndDate"));
        obj.sethasEndDate(rs.getBoolean("hasEndDate"));
        obj.setisCoop(rs.getBoolean("isCoop"));
        obj.setisFilled(rs.getBoolean("isFilled"));

        dataList.add(obj);

        // System.out.println(obj.Name);

        String x = rs.getString("name");
        // a.add(x);
        ArrayList<Integer> t = new ArrayList<Integer>();
        String d = rs.getString("startdate").substring(5, 7);
        String e = rs.getString("EndDate").substring(5, 7);

        Boolean color = rs.getBoolean("isCoop");

        int d1 = Integer.parseInt(d);
        int d2 = Integer.parseInt(e);

        t.add(d1);
        t.add(d2);

        if (obj.getisCoop()) {
          coopDates.add(t);
          coopNames.add(x);
        } else {
          permamentDates.add(t);
          notCoopNames.add(x);
        }
        System.out.println(obj.Name);
      }

      model.put("Positions", dataList);
      model.put("Positions", dataList);
      model.put("dates", m);

      ArrayList<ArrayList<Integer>> finalDates = new ArrayList<ArrayList<Integer>>();
      ArrayList<Integer> emptyDates = new ArrayList<Integer>();

      ArrayList<String> allOrderedNames = new ArrayList<String>();

      for (int i = 0; i < permamentDates.size(); i++) {
        finalDates.add(emptyDates);
      }
      for (int i = 0; i < coopDates.size(); i++) {
        finalDates.add(coopDates.get(i));
      }

      for (int i = 0; i < notCoopNames.size(); i++) {
        allOrderedNames.add(notCoopNames.get(i));
      }
      for (int i = 0; i < coopNames.size(); i++) {
        allOrderedNames.add(coopNames.get(i));
      }

      model.put("finalDates", finalDates);
      model.put("permamentDates", permamentDates);
      model.put("Names", allOrderedNames);

      return "redirect:/viewPositions";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  // updating positions data
  @PostMapping(path = "/PositionUpdate", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleUpdate(Map<String, Object> model, @ModelAttribute("Position") Position pos) throws Exception {
    // Establishing connection with database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "UPDATE Employees SET team='" + pos.getTeam() + "', name='" + pos.getName() + "', role='"
          + pos.getRole() + "', hasEndDate='" + pos.gethasEndDate() + "',StartDate='" + pos.getStartDate()
          + "', EndDate='" + pos.getEndDate() + "',isCoop='" + pos.getisCoop() + "',isFilled='" + pos.getisFilled()
          + "' WHERE id ='" + pos.getSerialID() + "';";
      System.out.println(sql);
      stmt.executeUpdate(sql);
      return "redirect:/viewPositions";
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
