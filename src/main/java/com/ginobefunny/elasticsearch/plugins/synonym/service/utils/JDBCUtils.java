/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ginobefunny.elasticsearch.plugins.synonym.service.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.StringUtils;

/**
 * Created by ginozhang on 2017/1/12.
 */
public final class JDBCUtils {

    public static long queryMaxSynonymRuleVersion(String dbUrl) throws Exception {
        long maxVersion = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl);
            stmt = conn.createStatement();
            String sql = "SELECT max(version) VERSION FROM dynamic_synonym_rule";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                maxVersion = rs.getLong("VERSION");
            }
        } finally {
            closeQuietly(conn, stmt, rs);
        }

        return maxVersion;
    }

    public static List<String> querySynonymRules(String dbUrl, long lastestVersion) throws Exception {
        List<String> list = new ArrayList<String>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl);
            stmt = conn.createStatement();
            String sql;
            if (lastestVersion > 0) {
                sql = "SELECT rule FROM dynamic_synonym_rule WHERE version <= " + lastestVersion + " and status = 1";
            } else {
                sql = "SELECT rule FROM dynamic_synonym_rule WHERE status = 1";
            }

            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString("rule"));
            }
        } finally {
            closeQuietly(conn, stmt, rs);
        }

        return list;

    }
    
    public static List<String> querySynonymAiLikenessKeywords(String dbUrl,String user,String password,String sql) throws Exception {
        List<String> list = new ArrayList<String>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl,user,password);
            stmt = conn.createStatement();
//            String sql = "SELECT main_keyword,likeness_keywords FROM ai_keyword WHERE keyword_type = 1 AND is_delete = 1";
            if(!StringUtils.isNullOrEmpty(sql)){
            	rs = stmt.executeQuery(sql);
            	while (rs.next()) {
            		String likenessKeywords = rs.getString("likeness_keywords");
            		if(!StringUtils.isNullOrEmpty(likenessKeywords)){
            			String keywordAndlikenessKeywords = likenessKeywords + rs.getString("main_keyword");
            			list.add(keywordAndlikenessKeywords);
            		}
            	}
            }
        } finally {
            closeQuietly(conn, stmt, rs);
        }
        return list;
    }

    private static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

}
