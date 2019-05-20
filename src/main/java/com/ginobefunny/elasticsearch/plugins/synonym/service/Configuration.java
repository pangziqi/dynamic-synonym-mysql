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
package com.ginobefunny.elasticsearch.plugins.synonym.service;

import org.apache.lucene.analysis.Analyzer;

/**
 * Created by ginozhang on 2017/1/12.
 */
public class Configuration {

    private final boolean ignoreCase;

    private final boolean expand;
    
    private final int interval;

    private final String dbUrl;
    
    private final String user;
    
    private final String password;
    
    private final String sql;

    private final Analyzer analyzer;
    
    public final static String DB_PROPERTIES = "db.properties";

    public Configuration(boolean ignoreCase, boolean expand,int interval, Analyzer analyzer, String dbUrl,String user,String password,String sql) {
        this.ignoreCase = ignoreCase;
        this.expand = expand;
        this.interval = interval;
        this.analyzer = analyzer;
        this.dbUrl = dbUrl;
        this.user = user;
        this.password = password;
        this.sql = sql;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public boolean isExpand() {
        return expand;
    }

    public String getDBUrl() {
        return dbUrl;
    }
    
    public int getInterval(){
    	return interval;
    }
    
    public String getUser(){
    	return user;
    }
    
    public String getPassword(){
    	return password;
    }

	public String getSql() {
		return sql;
	}
}
