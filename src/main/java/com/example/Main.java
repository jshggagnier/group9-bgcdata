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
  String index(Map<String, Object> model) {
  try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS squares (id serial, boxname varchar(20), height int, width int, boxcolor char(7), outlined boolean)");
      //String sql = "INSERT INTO squares (boxname,height,width,color,outlined) VALUES ('bob',10,10,'black',true)";
      //stmt.executeUpdate(sql);
      ResultSet rs = stmt.executeQuery("SELECT * FROM squares");
      ArrayList<String> output = new ArrayList<String>();
      ArrayList<Integer> idlist = new ArrayList<Integer>();
      ArrayList<String> colorlist = new ArrayList<String>();
      ColorUtils colortester = new ColorUtils();

      while (rs.next()) {
        output.add(rs.getString("boxname"));
        idlist.add(rs.getInt("id"));
        colorlist.add(rs.getString("boxcolor"));
      }

      for (int x = 0;x < colorlist.size();x++) {
        colorlist.set(x,colortester.getColorNameFromHex((int) Long.parseLong(colorlist.get(x).substring(1), 16))) ;
      }
      
      model.put("colors",colorlist);
      model.put("boxes", output);
      model.put("idlist", idlist);
      return "index";
    } 
  catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(path = "/AddSquare")
  public String getAddSquareForm(Map<String, Object> model){
    Square square = new Square();  // creates new square
    model.put("square", square);
    return "AddSquare";
  }

  @PostMapping(
    path = "/AddSquare",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleBrowserSquareSubmit(Map<String, Object> model, Square square) throws Exception {
    // Save the person data into the database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS squares (id serial, boxname varchar(20), height int, width int, boxcolor char(7), outlined boolean)");
      String sql = "INSERT INTO squares (boxname,height,width,boxcolor,outlined) VALUES ('"+ square.getboxname() +"','"+square.getheight()+"','"+ square.getwidth() +"','"+square.getboxcolor()+"',"+square.getoutlined()+")";
      stmt.executeUpdate(sql);
      //System.out.println(person.getFname() + " " + person.getLname());
      return "redirect:/";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }

@GetMapping("/openbox")
public String getID(Map<String, Object> model,@RequestParam String id) {
  try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM squares WHERE id="+(Integer.parseInt(id)));
    rs.next();
    int identifier;
    String boxname = new String();
    int height;
    int width;
    String boxcolor = new String();
    boolean outlined;

    identifier = rs.getInt("id");
    boxname = rs.getString("boxname");
    height = rs.getInt("height");
    width = rs.getInt("width");
    boxcolor = rs.getString("boxcolor");
    outlined = rs.getBoolean("outlined");

    model.put("id", identifier);
    model.put("boxname", boxname);
    model.put("height", height);
    model.put("width", width);
    model.put("boxcolor", boxcolor);
    model.put("outlined", outlined);

    return "openbox";
  } catch (Exception e) {
    model.put("message", e.getMessage());
    return "error";
  }
}

@GetMapping(
  path = "/deletebox"
)
public String handleBrowserSquareDelete(Map<String, Object> model,@RequestParam String id)
 throws Exception {
  // Delete the Square from the Database
  try (Connection connection = dataSource.getConnection()) {
    int identifier = Integer.parseInt(id);
    Statement stmt = connection.createStatement();
    String sql = "DELETE FROM squares WHERE ID="+identifier;
    System.out.println(sql);
    stmt.executeUpdate(sql);
    return "redirect:/";
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
