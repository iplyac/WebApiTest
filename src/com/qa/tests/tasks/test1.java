package com.qa.tests.tasks;

import com.googlecode.junittoolbox.ParallelParameterized;
import com.qa.framework.BasicTest;
import com.qa.framework.ServiceSetting;
import com.qa.tests.TestTaskExecutor;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = ParallelParameterized.class)
public class test1 extends BasicTest{

    private static Logger logger = Logger.getLogger(test1.class);
    ServiceSetting test = new ServiceSetting("dasdad", "Im Service");

    @Test
    public void test() throws InterruptedException {
        logger.info(test.getConfig());
    }

    public test1(ServiceSetting test){
        this.test = test;
    }

    @Before
    public void precond(){
        logger.info(String.format("Start %s", test.getConfig()));
    }

    @After
    public void after(){
        logger.info(String.format("Finished %s", test.getConfig()));
    }
    @Parameterized.Parameters(name="{index}")
    public static Iterable<ServiceSetting> parameters()
    {
        return TestTaskExecutor.settings;
    }
}
