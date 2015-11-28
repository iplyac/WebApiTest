package com.qa.tests;

import com.qa.framework.FrameWorkServiceRunner;
import com.qa.framework.ServiceSetting;
import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.List;

@RunWith(FrameWorkServiceRunner.class)
@Suite.SuiteClasses({
//        com.qa.tests.tasks.test1.class
        com.qa.tests.tasks.TestExecutor.class
})
public class TestTaskExecutor {
    private static Logger logger = Logger.getLogger(TestTaskExecutor.class);
    public static List<ServiceSetting> settings = Arrays.asList(
            new ServiceSetting(
                    System.getenv("CONFIG_FILE")!=null?System.getenv("CONFIG_FILE"):"config.json",
                    System.getenv("TEST_LIST_FILE")!=null?System.getenv("TEST_LIST_FILE"):"testList.json")
    );
}
