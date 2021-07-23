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

import ch.qos.logback.core.joran.conditional.IfAction;

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
