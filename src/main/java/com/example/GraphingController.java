package com.example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SpringBootApplication
public class GraphingController {
    @Value("${spring.datasource.url}")
    public String dbUrl;
  
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/WorkitemGraph")
    public String GraphingWorkitems(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal)
    {
        GetuserAuthenticationData(model,principal);
        try (Connection connection = dataSource.getConnection()) 
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(("SELECT * FROM workitems"));
            ArrayList<WorkItem> dataList = new ArrayList<WorkItem>();
            
            while (rs.next()) {
                WorkItem obj = new WorkItem();
                obj.setId(rs.getString("id"));
                obj.setItemName(rs.getString("itemname"));
                obj.setStartDate(rs.getString("startdate"));
                obj.setEndDate(rs.getString("enddate"));
                obj.setTeamsAssigned(rs.getString("teams"));
                dataList.add(obj);
            }
            model.put("WorkItems", dataList);

            rs = stmt.executeQuery(("SELECT * FROM employees"));
            ArrayList<Position>dataList2 = new ArrayList<Position>();
            while (rs.next()) {
                Position obj2 = new Position();
                obj2.setSerialID(rs.getInt("id"));
                obj2.setName(rs.getString("name"));
                obj2.setStartDate(rs.getString("startdate"));
                obj2.setEndDate(rs.getString("enddate"));
                obj2.setisCoop(rs.getBoolean("isCoop"));
                obj2.setisFilled(rs.getBoolean("isFilled"));
                obj2.sethasEndDate(rs.getBoolean("hasEndDate"));
                dataList2.add(obj2);
            }
            model.put("Positions", dataList2);
            return "WorkitemGraph";
        }
        catch (Exception e) {
            model.put("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/HiringGraph")
    public String GraphingPositions(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal)
    {
        GetuserAuthenticationData(model,principal);
        try (Connection connection = dataSource.getConnection()) 
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(("SELECT * FROM employees"));
            ArrayList<Position>dataList2 = new ArrayList<Position>();
            while (rs.next()) {
                Position obj2 = new Position();
                obj2.setSerialID(rs.getInt("id"));
                obj2.setName(rs.getString("name"));
                obj2.setStartDate(rs.getString("startdate"));
                obj2.setEndDate(rs.getString("enddate"));
                obj2.setisCoop(rs.getBoolean("isCoop"));
                obj2.setisFilled(rs.getBoolean("isFilled"));
                obj2.sethasEndDate(rs.getBoolean("hasEndDate"));
                dataList2.add(obj2);
            }
            model.put("Positions", dataList2);
            return "PositionGraph";
        }
        catch (Exception e) {
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