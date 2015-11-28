package com.qa.framework;

import com.googlecode.junittoolbox.ParallelSuite;
import org.apache.log4j.Logger;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.InitializationError;

public class SuiteRunner extends ParallelSuite{
    private static Logger logger = Logger.getLogger(SuiteRunner.class);


    public SuiteRunner(Class<?> klass, RunnerBuilder runnerBuilder)throws InitializationError{
        super(klass, runnerBuilder);
        logger.info("Run " + klass.getSimpleName());
    }

}
