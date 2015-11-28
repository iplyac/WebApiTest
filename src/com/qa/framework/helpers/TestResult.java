package com.qa.framework.helpers;

import com.qa.framework.TestStatus;
import com.qa.framework.exceptions.StepException;
import com.reltio.qa.services.TestLinkService;

public class TestResult {

    private TestStatus status = TestStatus.Successful;
    private String message = "";
    private String testCaseId;
    private int stepNum = 0;
    private int stepLimit = 0;

    public void fail(int stepNum, String details)throws StepException{
        setStatus(TestStatus.Failed);
        step(stepNum);
        setMessage(details);
    }

    public void fail(String msg)throws StepException{
        setStatus(TestStatus.Failed);
        setMessage(msg);
    }

    public void fail(Exception exception)throws StepException {
        setStatus(TestStatus.Failed);
        setMessage("\nException:\n" + exception.getMessage());
    }

    public void nextStep()throws StepException{
        if (stepNum < stepLimit)stepNum++;else fail("Step limit is reached");
    }

    public void step(int stepNum){this.stepNum = stepNum;}
    public int getStepNum(){return stepNum;}

    public TestStatus getStatus() {
        return status;
    }
    public void setStatus(TestStatus status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccessful() {
        return status == TestStatus.Successful;
    }

    public TestResult(String testCaseId){
        this.testCaseId = testCaseId;
        this.stepLimit = TestLinkService.getActions(testCaseId).size();
    }

    @Deprecated
    public TestResult(){}

    public String getTestCaseId() {
        return testCaseId;
    }
}
