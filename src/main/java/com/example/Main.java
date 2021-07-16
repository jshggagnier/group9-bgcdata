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


import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

//auth0 login imports
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    GetuserAuthenticationData(model,principal);
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
    String Role = GetuserAuthenticationData(model,principal);
    if (Role.equals("user")) 
    {
      model.put("message", "Unauthorized user: Contact your Administrator to grant you permissions to edit the database");
      return "error";
    }
    WorkItem workitem = new WorkItem();
    model.put("WorkItem", workitem);
    return "WorkItemSubmit";
  }

  @GetMapping("/WorkItemEdit/{nid}")
  String LoadFormWorkItemEdit(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal, @PathVariable String nid) {
    String Role = GetuserAuthenticationData(model,principal);
    if (Role.equals("user")) 
    {
      model.put("message", "Unauthorized user: Contact your Administrator to grant you permissions to edit the database");
      return "error";
    }
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM workitems WHERE id = "+nid));
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

  @GetMapping("/PositionSubmit")
  String LoadFormPosition(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    String Role = GetuserAuthenticationData(model,principal);
    if (Role.equals("user")) 
    {
      model.put("message", "Unauthorized user: Contact your Administrator to grant you permissions to edit the database");
      return "error";
    }
    Position position = new Position();
    model.put("Position", position);
    return "PositionSubmit";
  }

  @GetMapping("/viewPositions")
  String viewPositions(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    GetuserAuthenticationData(model,principal);
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(("SELECT * FROM Employees"));
      ArrayList<Position> dataList = new ArrayList<Position>();

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
        System.out.println(obj.Name);
      }

      model.put("Positions", dataList);
      return "PositionView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/viewWorkItems")
  String viewWorkItems(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal) {
    GetuserAuthenticationData(model,principal);
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
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
          "CREATE TABLE IF NOT EXISTS workitems (id serial, itemname varchar(20), startdate DATE, enddate DATE, teams varchar(500), itemtype varchar(3), fundinginformation varchar(100));");
      String sql = "INSERT INTO workitems (itemname, startdate, enddate, teams, itemtype, fundinginformation) VALUES ('"
          + workitem.getItemName() + "', '" + workitem.getStartDate() + "', '" + workitem.getEndDate() + "', '"
          + workitem.getTeamsAssigned()+ "', '" + workitem.getItemType() + "', '" + workitem.getFundingInformation() + "');";
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
      return "WorkItemView";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  // Update
  @PostMapping(path = "/WorkItemEdit", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleBrowsernewWorkItemEdit(Map<String, Object> model, WorkItem workitem) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "UPDATE workitems set itemname = '"
          + workitem.getItemName() + "', startdate = '"+ workitem.getStartDate() + "', enddate = '"+ workitem.getEndDate() 
          + "', teams = '" + workitem.getTeamsAssigned()+ "', itemtype= '"+ workitem.getItemType() 
          + "', fundinginformation ='"+workitem.getFundingInformation()+" WHERE id = '"+workitem.getId()+";";
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
      return "WorkItemView";
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

      String sql = "INSERT INTO Employees (name,team,role,StartDate,EndDate,hasEndDate,isCoop,isFilled) VALUES ('" + pos.getName()
          + "','" + pos.getTeam() + "','" + pos.getRole() + "','" + pos.getStartDate() + "','" + pos.getEndDate()
          + "','" + pos.gethasEndDate() + "','" + pos.getisCoop() + "','" + pos.getisFilled() + "')";

      stmt.executeUpdate(sql);

      ResultSet rs = stmt.executeQuery(("SELECT * FROM Employees"));
      ArrayList<Position> dataList = new ArrayList<Position>();

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
        System.out.println(obj.Name);
      }

      model.put("Positions", dataList);
      return "PositionView";
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


  public String GetuserAuthenticationData(Map<String, Object> model,@AuthenticationPrincipal OidcUser principal)
  {
    String defaultrole = "user";
    if (principal != null) 
    {
      model.put("profile", principal.getClaims());
      String email = (String) principal.getClaims().get("email");
      System.out.println(email);
      try (Connection connection = dataSource.getConnection()) 
      {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (email varchar(50),role varchar(10));");
        ResultSet rs = stmt.executeQuery(("SELECT * FROM users WHERE email='"+email+"'"));
        if(rs.next())
        {
          model.put("userRole",rs.getString("role"));
          return rs.getString("role");
        }
        else
        {
          if(email.equals("testuser@redfoxtech.ca")) {defaultrole = "admin";}
          System.out.println(defaultrole);
          stmt.executeUpdate("INSERT INTO users (email,role) VALUES ('"+email+"','"+defaultrole+"');");
          model.put("userRole",defaultrole);
          return defaultrole;
        }
      } 
      catch (Exception e) 
      {
        model.put("message", e.getMessage());
        System.out.println(e);
        return "error";
      }
    }
    else
    {
      model.put("userRole","Not Logged In");
      return "Not Logged In";
    }
    //Database calls for the role in question
  }
}
