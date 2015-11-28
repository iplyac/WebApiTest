package com.qa.tests.suite1;

import com.googlecode.junittoolbox.ParallelParameterized;
import com.qa.framework.BasicTest;
import com.qa.framework.TestDefinition;
import com.qa.tests.TestSuiteExecutor;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = ParallelParameterized.class)
public class test1 extends BasicTest{

    private static Logger logger = Logger.getLogger(test1.class);
    TestDefinition test = new TestDefinition(1, "Im Service");

    @Test
    public void test() throws InterruptedException {
        logger.info(test.name);
    }

    public test1(TestDefinition test){
        this.test = test;
    }

    @Before
    public void precond(){
        logger.info(String.format("Start %s", test.id));
    }

    @After
    public void after(){
        logger.info(String.format("Finished %s", test.id));
    }
    @Parameterized.Parameters(name="{index}")
    public static Iterable<TestDefinition> parameters()
    {
        return TestSuiteExecutor.tests;
    }
}
