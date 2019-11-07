package com.ricky.player.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


/**
 * @Author: ricky
 * @Date: 2019/7/8 14:37
 */
public class DataBaseUtils {

  private static Connection connectionA;
  private static Connection connectionB;
  public static final Logger logger = LoggerFactory.getLogger(DataBaseUtils.class);

  static {
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static Connection getConnection(String url, String username, String password) {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(url, username, password);
      conn.setAutoCommit(true);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return conn;
  }

  public static void close(Object o) {
    if (o == null) {
      return;
    }
    if (o instanceof ResultSet) {
      try {
        ((ResultSet) o).close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } else if (o instanceof Statement) {
      try {
        ((Statement) o).close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } else if (o instanceof Connection) {
      Connection c = (Connection) o;
      try {
        if (!c.isClosed()) {
          c.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 获取排名最高的电影
   */
  public static HashMap<String,Object> getHighFilm(Connection conn) {
    String sql = "select * from film where status = 0 order by score desc,update_time limit 1";
    ResultSet rs = null;
    HashMap<String,Object> result = new HashMap<>(16);
    try {
      PreparedStatement ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        result.put("id",rs.getLong("id"));
        result.put("name",rs.getString("name"));
        result.put("url",rs.getString("url"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      close(rs);
    }
    return result;
  }

  /**
   * 更新正在播放的电影的信息
   */
  public static void updatePlayingFilm(Connection conn) {
    String sql = "update film set score = 0,play_num = play_num + 1,status = 0 where status = 1";
    try {
      conn.prepareStatement(sql).executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * 更新最高分的电影为status = 1
   */
  public static void updateFilmStatus(Connection conn,String id) {
    String sql = "update film set status = 1 where id = " + id;
    try {
      conn.prepareStatement(sql).executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }



  public static void main(String[] args) {

    String urlA = "jdbc:mysql://localhost:3306/user?useSSL=true";
    String usernameA = "root";
    String passwordA = "123456";

    Connection connection = getConnection(urlA, usernameA, passwordA);
    HashMap<String, Object> highFilm = getHighFilm(connection);


    close(connection);


  }
}
