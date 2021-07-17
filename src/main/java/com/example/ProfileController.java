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
public class ProfileController {
    @Value("${spring.datasource.url}")
    private String dbUrl;
  
    @Autowired
    private DataSource dataSource;
    
    @RequestMapping("/ManageUsers")
    String ManageUsers(Map<String, Object> model, @AuthenticationPrincipal OidcUser principal)
    {
        try (Connection connection = dataSource.getConnection()) 
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(("SELECT * FROM users"));
            ArrayList<UserElement> dataList = new ArrayList<UserElement>();
            
            while (rs.next()) {
                UserElement obj = new UserElement();
                obj.setEmail(rs.getString("email"));
                obj.setRole(rs.getString("role"));        
                dataList.add(obj);
            }
            model.put("Users", dataList);
            return "ProfileController";
        }
        catch (Exception e) {
            model.put("message", e.getMessage());
            return "error";
        }
    }
    @PostMapping(path = "/UpdateUser", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public String UpdateUser(Map<String, Object> model, UserElement user) throws Exception
    {
        try (Connection connection = dataSource.getConnection()) 
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(("SELECT * FROM users"));
            ArrayList<UserElement> dataList = new ArrayList<UserElement>();
            
            while (rs.next()) {
                UserElement obj = new UserElement();
                obj.setEmail(rs.getString("email"));
                obj.setRole(rs.getString("role"));        
                dataList.add(obj);
            }
            model.put("Users", dataList);
            return "ProfileController";
        }
        catch (Exception e)
        {
            model.put("message", e.getMessage());
            return "error";
        }
    }
}
