package com.qa.tests;

import com.qa.framework.FrameWorkServiceRunner;
import com.qa.framework.ServiceSetting;
import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.*;

@RunWith(FrameWorkServiceRunner.class)
@Suite.SuiteClasses({
//        com.qa.tests.tasks.test1.class
        com.qa.tests.tasks.TestExecutor.class
})
public class TestTaskExecutor {

    /**
     * qqqqcfdfdssdsadad
     *
     */
    private static Logger logger = Logger.getLogger(TestTaskExecutor.class);
    public static List<ServiceSetting> settings = Arrays.asList(
             new ServiceSetting("config_DTSS.json", "testList_DTSS.json"),
            new ServiceSetting("config.json", "testList_DTSS.json")
    );

}
