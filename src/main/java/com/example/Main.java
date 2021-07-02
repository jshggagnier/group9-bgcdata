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

import jdk.jfr.Registered;

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
import org.springframework.ui.Model;
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
    if (principal != null) {
      model.put("profile", principal.getClaims());
    }
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      // stmt.executeUpdate("CREATE TABLE IF NOT EXISTS squares (id serial, boxname
      // varchar(20), height int, width int, boxcolor char(7), outlined boolean)");
      return "index";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/WorkItemSubmit")
  String LoadFormWorkItem(Map<String, Object> model) {
    WorkItem workitem = new WorkItem();
    model.put("WorkItem", workitem);
    return "WorkItemSubmit";
  }

  @GetMapping("/PositionSubmit")
  String LoadFormPosition(Map<String, Object> model) {
    Position position = new Position();
    model.put("Position", position);
    return "PositionSubmit";
  }

  // Submit Catch
  @PostMapping(path = "/WorkItemSubmit", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleBrowsernewWorkItemSubmit(Map<String, Object> model, WorkItem workitem) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS workitems (id serial, itemname varchar(20), startdate DATE, enddate DATE, teams varchar(300), itemtype varchar(3), fundinginformation varchar(100));");
      String sql = "INSERT INTO workitems (name, startdate, enddate, itemtype, fundinginformation) VALUES ('"
          + workitem.getItemName() + "', '" + workitem.getStartDate() + "', '" + "'" + workitem.getEndDate() + "', '"
          + workitem.getItemType() + "', '" + workitem.getFundingInformation() + "');";
      stmt.executeUpdate(sql);
      ResultSet rs = stmt.executeQuery(("SELECT * FROM workitems"));
      ArrayList<WorkItem> dataList = new ArrayList<WorkItem>();

      while (rs.next()) {
        WorkItem obj = new WorkItem();
        obj.setItemName(rs.getString("itemname"));
        obj.setStartDate(rs.getString("startdate"));
        obj.setEndDate(rs.getString("enddate"));
        obj.setItemType(rs.getString("itemtype"));
        obj.setFundingInformation(rs.getString("fundinginformation"));

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
          "CREATE TABLE IF NOT EXISTS Employees (id serial,name varchar(20),team varchar(20), role varchar(20),StartDate DATE,EndDate DATE, isPermanent varchar(10))");

      String sql = "INSERT INTO Employees (name,team,role,StartDate,EndDate,isPermanent) VALUES ('" + pos.getName()
          + "','" + pos.getTeam() + "','" + pos.getRole() + "','" + pos.getStartDate() + "','" + pos.getEndDate()
          + "','" + pos.getIsPermanent() + "')";

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
        obj.setIsPermanent(rs.getBoolean("isPermanent"));

        dataList.add(obj);
        System.out.println(obj.Name);
      }

      model.put("E", dataList);
      return "db";
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

}
