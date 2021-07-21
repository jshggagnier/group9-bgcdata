/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

//graph imports
import org.springframework.web.bind.annotation.GetMapping;

//auth0 login imports
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

//for date formatting
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@SpringBootApplication
public class Main implements WebMvcConfigurer {

  public void addViewController(ViewControllerRegistry registry) {
    registry.addViewController("/index").setViewName("index");
  }

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    GetuserAuthenticationData(model, principal);
    try (Connection connection = dataSource.getConnection()) {
      // Statement stmt = connection.createStatement();
      // stmt.executeUpdate("CREATE TABLE IF NOT EXISTS squares (id serial, boxname
      // varchar(20), height int, width int, boxcolor char(7), outlined boolean)");
      return "index";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/WorkItemSubmit")
  String LoadFormWorkItem(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    String Role = GetuserAuthenticationData(model, principal);
    if (Role.equals("unverified") || Role.equals("viewonly")) {
      model.put("message",
          "Unauthorized user: Contact your Administrator to grant you permissions to edit the database");
      return "error";
    }
    WorkItem workitem = new WorkItem();
    model.put("WorkItem", workitem);
    return "WorkItemSubmit";
  }

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
  public String deleteUserData(Map<String, Object> model, @PathVariable int serialID, @AuthenticationPrincipal OidcUser principal) {
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
      model.put("target",obj);

      if(rs.next() != false) 
      {
        model.put("message","Error: Two identical Serial ID's, Database is invalid/has been modified! Contact Administrator with this error!");
        return "error";
      }
      return "PositionEdit";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/viewPositions")
  String viewPositions(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    String Role = GetuserAuthenticationData(model, principal);
    if (Role.equals("unverified")){
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
      ArrayList<String> a = new ArrayList<String>();
      ArrayList<ArrayList<Integer>> m = new ArrayList<ArrayList<Integer>>();

      ArrayList<ArrayList<Integer>> coopDates = new ArrayList<ArrayList<Integer>>();
      ArrayList<ArrayList<Integer>> permamentDates = new ArrayList<ArrayList<Integer>>();

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
        a.add(x);
        ArrayList<Integer> t = new ArrayList<Integer>();
        String d = rs.getString("startdate").substring(5, 7);
        String e = rs.getString("EndDate").substring(5, 7);

        Boolean color = rs.getBoolean("isCoop");

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
        millisecDates.add(millisEnd);

        // if (obj.getisCoop()) {
        //   coopDates.add(t);
        // } else {
        //   permamentDates.add(t);
        // }

        if (obj.getisCoop()) {
          coopDatesMilli.add(millisecDates);
        } else {
          permamentDatesMilli.add(millisecDates);
        }
      }

      model.put("Positions", dataList);
      model.put("Names", a);
      model.put("dates", m);

      
      // Date date = sdf.parse(myDateForTest);
      // long millis = date.getTime();
      // model.put("myTest", millis);



      
      // ArrayList<ArrayList<Integer>> finalDates = new ArrayList<ArrayList<Integer>>();
      // ArrayList<Integer> emptyDates = new ArrayList<Integer>();

      // for (int i = 0; i < permamentDates.size(); i++) {
      //   finalDates.add(emptyDates);
      // }
      // for (int i = 0; i < coopDates.size(); i++) {
      //   finalDates.add(coopDates.get(i));
      // }

      // model.put("finalDates", finalDates);
      // model.put("permamentDates", permamentDates);

      ArrayList<ArrayList<Long>> finalDates = new ArrayList<ArrayList<Long>>();
      ArrayList<Long> emptyDates = new ArrayList<Long>();

      for (int i = 0; i < permamentDatesMilli.size(); i++) {
        finalDates.add(emptyDates);
      }
      for (int i = 0; i < coopDatesMilli.size(); i++) {
        finalDates.add(coopDatesMilli.get(i));
      }

      model.put("finalDates", finalDates);
      model.put("permamentDates", permamentDatesMilli);

      return "PositionView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/WorkItemEdit/{nid}")
  String LoadFormWorkItemEdit(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal,
      @PathVariable String nid) {
    String Role = GetuserAuthenticationData(model, principal);
    if (Role.equals("unverified") || Role.equals("viewonly")) {
      model.put("message",
          "Unauthorized user: Contact your Administrator to grant you permissions to edit the database");
      return "error";
    }
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM workitems WHERE id = " + nid));
      WorkItem workitem = new WorkItem();
      while (rs.next()) {
        workitem.setItemName(rs.getString("itemname"));
        workitem.setStartDate(rs.getString("startdate"));
        workitem.setEndDate(rs.getString("enddate"));
        workitem.setItemType(rs.getString("itemtype"));
        workitem.setTeamsAssigned(rs.getString("teams"));
        workitem.setFundingInformation(rs.getString("fundinginformation"));
        workitem.setId(rs.getString("id"));
      }
      model.put("WorkItem", workitem);
      return "WorkItemEdit";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @PostMapping(path = "/WorkItemEdit", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleBrowsernewWorkItemEditSubmit(Map<String, Object> model, WorkItem workitem, @AuthenticationPrincipal OidcUser principal) throws Exception {
    String Role = GetuserAuthenticationData(model, principal);
    if (Role.equals("unverified")) {
      model.put("message",
          "Unauthorized user: Contact your Administrator to grant you permissions to view the database");
      return "error";
    }
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "UPDATE workitems SET itemname='"+workitem.getItemName()+"', startdate='"+workitem.getStartDate()
      +"', enddate='"+workitem.getEndDate()+"', itemtype='"+workitem.getItemType()+"', teams='"+workitem.getTeamsAssigned()
      +"', fundinginformation='"+workitem.getFundingInformation()+"' WHERE id='"+workitem.getId()+"';";
      stmt.executeUpdate(sql);
      ResultSet rs = stmt.executeQuery("SELECT * FROM workitems");
      ArrayList<WorkItem> dataList = new ArrayList<WorkItem>();
      while (rs.next()) {
        WorkItem obj = new WorkItem();
        obj.setItemName(rs.getString("itemname"));
        obj.setStartDate(rs.getString("startdate"));
        obj.setEndDate(rs.getString("enddate"));
        obj.setItemType(rs.getString("itemtype"));
        obj.setTeamsAssigned(rs.getString("teams"));
        obj.setFundingInformation(rs.getString("fundinginformation"));
        obj.setId(rs.getString("id"));

        dataList.add(obj);
      }
      model.put("WorkItems", dataList);
      return "redirect:/viewWorkItems";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/viewWorkItems")
  String viewWorkItems(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    String Role = GetuserAuthenticationData(model, principal);
    if (Role.equals("unverified")) {
      model.put("message",
          "Unauthorized user: Contact your Administrator to grant you permissions to view the database");
      return "error";
    }
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS workitems (id serial, itemname varchar(50), startdate DATE, enddate DATE, teams varchar(500), itemtype varchar(3), fundinginformation varchar(100))");
      ResultSet rs = stmt.executeQuery(("SELECT * FROM workitems"));
      ArrayList<WorkItem> dataList = new ArrayList<WorkItem>();
      while (rs.next()) {
        WorkItem obj = new WorkItem();
        obj.setItemName(rs.getString("itemname"));
        obj.setStartDate(rs.getString("startdate"));
        obj.setEndDate(rs.getString("enddate"));
        obj.setItemType(rs.getString("itemtype"));
        obj.setTeamsAssigned(rs.getString("teams"));
        obj.setFundingInformation(rs.getString("fundinginformation"));
        obj.setId(rs.getString("id"));

        dataList.add(obj);
      }
      model.put("WorkItems", dataList);
      return "WorkItemView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  // Submit Catch
  @PostMapping(path = "/WorkItemSubmit", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleBrowsernewWorkItemSubmit(Map<String, Object> model, WorkItem workitem) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS workitems (id serial, itemname varchar(50), startdate DATE, enddate DATE, teams varchar(500), itemtype varchar(3), fundinginformation varchar(100))");
      String sql = "INSERT INTO workitems (itemname, startdate, enddate, teams, itemtype, fundinginformation) VALUES ('"
          + workitem.getItemName() + "', '" + workitem.getStartDate() + "', '" + workitem.getEndDate() + "', '"
          + workitem.getTeamsAssigned() + "', '" + workitem.getItemType() + "', '" + workitem.getFundingInformation()
          + "')";
      stmt.executeUpdate(sql);
      ResultSet rs = stmt.executeQuery(("SELECT * FROM workitems"));
      ArrayList<WorkItem> dataList = new ArrayList<WorkItem>();
      while (rs.next()) {
        WorkItem obj = new WorkItem();
        obj.setItemName(rs.getString("itemname"));
        obj.setStartDate(rs.getString("startdate"));
        obj.setEndDate(rs.getString("enddate"));
        obj.setItemType(rs.getString("itemtype"));
        obj.setTeamsAssigned(rs.getString("teams"));
        obj.setFundingInformation(rs.getString("fundinginformation"));
        obj.setId(rs.getString("id"));

        dataList.add(obj);
      }
      model.put("WorkItems", dataList);
      return "redirect:/viewWorkItems";
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

      ResultSet rs = stmt.executeQuery(("SELECT * FROM Employees"));
      ArrayList<Position> dataList = new ArrayList<Position>();

      ArrayList<String> a = new ArrayList<String>();
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
        a.add(x);
        ArrayList<Integer> t = new ArrayList<Integer>();
        String d = rs.getString("startdate").substring(5, 7);
        String e = rs.getString("EndDate").substring(5, 7);

        Boolean color = rs.getBoolean("isCoop");

        int d1 = Integer.parseInt(d);
        int d2 = Integer.parseInt(e);

        t.add(d1);
        t.add(d2);

        // m.add(t);

        if (obj.getisCoop()) {
          coopDates.add(t);
        } else {
          permamentDates.add(t);
        }
        System.out.println(obj.Name);
      }

      model.put("Positions", dataList);
      model.put("Positions", dataList);
      model.put("Names", a);
      model.put("dates", m);

      ArrayList<ArrayList<Integer>> finalDates = new ArrayList<ArrayList<Integer>>();
      ArrayList<Integer> emptyDates = new ArrayList<Integer>();

      for (int i = 0; i < permamentDates.size(); i++) {
        finalDates.add(emptyDates);
      }
      for (int i = 0; i < coopDates.size(); i++) {
        finalDates.add(coopDates.get(i));
      }

      model.put("finalDates", finalDates);
      model.put("permamentDates", permamentDates);

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
      String sql = "UPDATE Employees SET team='" + pos.getTeam() + "', name='" + pos.getName() + "', role='" + pos.getRole() + "', hasEndDate='"
      + pos.gethasEndDate()+ "',StartDate='" + pos.getStartDate() + "', EndDate='" + pos.getEndDate() + "',isCoop='" + pos.getisCoop() + "',isFilled='"
      + pos.getisFilled() + "' WHERE id ='" + pos.getSerialID() + "';";
      System.out.println(sql);
      stmt.executeUpdate(sql);
      return "redirect:/viewPositions";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
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
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (email varchar(50),role varchar(10))");
        ResultSet rs = stmt.executeQuery(("SELECT * FROM users WHERE email='" + email + "'"));
        if (rs.next()) {
          model.put("userRole", rs.getString("role"));
          return rs.getString("role");
        } else {
          if (email.equals("testuser@redfoxtech.ca")) {
            defaultrole = "admin";
          }
          System.out.println(defaultrole);
          stmt.executeUpdate("INSERT INTO users (email,role) VALUES ('" + email + "','" + defaultrole + "')");
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
