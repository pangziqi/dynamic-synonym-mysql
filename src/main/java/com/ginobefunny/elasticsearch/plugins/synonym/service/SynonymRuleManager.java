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

import com.ginobefunny.elasticsearch.plugins.synonym.service.utils.JDBCUtils;
import com.ginobefunny.elasticsearch.plugins.synonym.service.utils.Monitor;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by ginozhang on 2017/1/12.
 */
public class SynonymRuleManager {

    private static final Logger LOGGER = ESLoggerFactory.getLogger(Monitor.class.getName());

//    private static final int DB_CHECK_URL = 60;

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "monitor-thread");
        }
    });

    private static SynonymRuleManager singleton;

    private Configuration configuration;

    private SimpleSynonymMap synonymMap;

    public static synchronized SynonymRuleManager initial(Configuration cfg) {
        if (singleton == null) {
            synchronized (SynonymRuleManager.class) {
                if (singleton == null) {
                    singleton = new SynonymRuleManager();
                    singleton.configuration = cfg;
                    singleton.loadSynonymRule();
                    executorService.scheduleWithFixedDelay(new Monitor(), 1,
                    		singleton.configuration.getInterval(), TimeUnit.MINUTES);
                }
            }
        }

        return singleton;
    }

    public static SynonymRuleManager getSingleton() {
        if (singleton == null) {
            throw new IllegalStateException("Please initial first.");
        }
        return singleton;
    }

    public List<String> getSynonymWords(String inputToken) {
        if (this.synonymMap == null) {
            return null;
        }

        return this.synonymMap.getSynonymWords(inputToken);
    }

    private void loadSynonymRule() {
    	// 回头修改一下，不需要返回值，只需要把数据查询出来就可以了
        try {
            List<String> synonymRuleList = JDBCUtils.querySynonymAiLikenessKeywords(configuration.getDBUrl(),configuration.getUser(),configuration.getPassword(),configuration.getSql());
            this.synonymMap = new SimpleSynonymMap(this.configuration);
            for (String rule : synonymRuleList) {
                this.synonymMap.addRule(rule);
            }

            LOGGER.info("Load {} synonym rule succeed!", synonymRuleList.size());
        } catch (Exception e) {
            LOGGER.error("Load synonym rule failed!", e);
            //throw new RuntimeException(e);
        }
    }

    public void reloadSynonymRule() {
    	// 也需要修改，把数据放到单例的manager中
        LOGGER.info("Start to reload synonym rule...");
        try {
            SynonymRuleManager tmpManager = new SynonymRuleManager();
            tmpManager.configuration = getSingleton().configuration;
            List<String> synonymRuleList = JDBCUtils.querySynonymAiLikenessKeywords(configuration.getDBUrl(),configuration.getUser(),configuration.getPassword(),configuration.getSql());
            SimpleSynonymMap tempSynonymMap = new SimpleSynonymMap(tmpManager.configuration);
            for (String rule : synonymRuleList) {
                tempSynonymMap.addRule(rule);
            }

            this.synonymMap = tempSynonymMap;
            LOGGER.info("Succeed to reload {} synonym rule!", synonymRuleList.size());
        } catch (Throwable t) {
            LOGGER.error("Failed to reload synonym rule!", t);
        }

    }
}
