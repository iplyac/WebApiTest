package com.qa.tests;

import com.qa.framework.FrameWorkServiceRunner;
import com.qa.framework.TestDefinition;
import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.List;

@RunWith(FrameWorkServiceRunner.class)
@Suite.SuiteClasses({
        com.qa.tests.suite1.test2.class
})
public class TestSuiteExecutor {
    private static Logger logger = Logger.getLogger(TestSuiteExecutor.class);
    public static List<TestDefinition> tests = Arrays.asList(new TestDefinition(1, "Details test 1"), new TestDefinition(2, "Details test 2"));

}
