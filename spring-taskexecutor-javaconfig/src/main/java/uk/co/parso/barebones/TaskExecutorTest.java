/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.parso.barebones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @author sam
 */
@Component
public class TaskExecutorTest {
    
    private Logger logger = LoggerFactory.getLogger(TaskExecutorTest.class);
    
    @Autowired
    private TaskExecutor taskExecutor;
    
    @Scheduled( fixedDelay = 1_000 )
    public void test() {
        logger.debug("test()");
        
        taskExecutor.execute(new TestTask());
    }
    
    private class TestTask implements Runnable {

        @Override
        public void run() {
            logger.debug("TestTaks");
        }
    }
}
