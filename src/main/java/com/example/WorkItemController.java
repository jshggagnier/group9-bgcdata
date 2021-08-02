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

import java.io.Console;
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
public class WorkItemController {
    @Value("${spring.datasource.url}")
    private String dbUrl;
  
    @Autowired
    private DataSource dataSource;
    

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
          "CREATE TABLE IF NOT EXISTS workitems (id serial, itemname varchar(50), startdate DATE, enddate DATE, teams varchar(500), fundinginformation varchar(100))");
      ResultSet rs = stmt.executeQuery(("SELECT * FROM workitems"));
      ArrayList<WorkItem> dataList = new ArrayList<WorkItem>();
      while (rs.next()) {
        WorkItem obj = new WorkItem();
        obj.setItemName(rs.getString("itemname"));
        obj.setStartDate(rs.getString("startdate"));
        obj.setEndDate(rs.getString("enddate"));
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
  
  @PostMapping(path = "/WorkItemSubmit", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleBrowsernewWorkItemSubmit(Map<String, Object> model, WorkItem workitem) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS workitems (id serial, itemname varchar(50), startdate DATE, enddate DATE, teams varchar(500), fundinginformation varchar(100))");
      String sql = "INSERT INTO workitems (itemname, startdate, enddate, teams, fundinginformation) VALUES ('"
          + workitem.getItemName() + "', '" + workitem.getStartDate() + "', '" + workitem.getEndDate() + "', '"
          + workitem.getTeamsAssigned() + "', '" + workitem.getFundingInformation()
          + "')";
      stmt.executeUpdate(sql);
      ResultSet rs = stmt.executeQuery(("SELECT * FROM workitems"));
      ArrayList<WorkItem> dataList = new ArrayList<WorkItem>();
      while (rs.next()) {
        WorkItem obj = new WorkItem();
        obj.setItemName(rs.getString("itemname"));
        obj.setStartDate(rs.getString("startdate"));
        obj.setEndDate(rs.getString("enddate"));
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
      +"', enddate='"+workitem.getEndDate()+"', teams='"+workitem.getTeamsAssigned()
      +"', fundinginformation='"+workitem.getFundingInformation()+"' WHERE id='"+workitem.getId()+"';";
      stmt.executeUpdate(sql);
      ResultSet rs = stmt.executeQuery("SELECT * FROM workitems");
      ArrayList<WorkItem> dataList = new ArrayList<WorkItem>();
      while (rs.next()) {
        WorkItem obj = new WorkItem();
        obj.setItemName(rs.getString("itemname"));
        obj.setStartDate(rs.getString("startdate"));
        obj.setEndDate(rs.getString("enddate"));
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

  @GetMapping("/WorkItemDelete/{nid}")
  String DeleteWorkItem(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal,
      @PathVariable String nid) {
    String Role = GetuserAuthenticationData(model, principal);
    if (Role.equals("unverified") || Role.equals("viewonly")) {
      model.put("message",
          "Unauthorized user: Contact your Administrator to grant you permissions to edit the database");
      return "error";
    }
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "DELETE FROM workitems " + "WHERE (id='" + nid + "')";
      stmt.executeUpdate(sql);

      return "redirect:/viewWorkItems";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }



  public String GetuserAuthenticationData(Map<String, Object> model,@AuthenticationPrincipal OidcUser principal)
  {
    String defaultrole = "unverified";
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
