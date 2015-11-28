package com.qa.framework.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.qa.framework.TestStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;

public class TestReport {
	public class TestLinkReport{
		private String testLinkId;
		private String lastStepDetail;
		private int lastStepNum;
		private int testCaseNum;
        private TestStatus result;

		public String getTestLinkId() {
			return testLinkId;
		}

		public void setTestLinkId(String testLinkId) {
			this.testLinkId = testLinkId;
		}

		public int getLastStepNum() {
			return lastStepNum;
		}

		public void setLastStepNum(int lastStepNum) {
			this.lastStepNum = lastStepNum;
		}

		public String getLastStepDetail() {
			return lastStepDetail;
		}

		public void setLastStepDetail(String lastStepDetail) {
			this.lastStepDetail = lastStepDetail;
		}

		public int getTestCaseNum() {
			return testCaseNum;
		}

		public void setTestCaseNum(int testCaseNum) {
			this.testCaseNum = testCaseNum;
		}

        public ExecutionStatus getResult(){
            switch(result){
                case Successful:return ExecutionStatus.PASSED;
                case Failed:return ExecutionStatus.FAILED;
                case Exception:return ExecutionStatus.BLOCKED;
            }
            return null;
        }

        public void setResult(TestStatus result) {
            this.result = result;
        }

        public TestLinkReport(String testLinkId, int lastStepNum, String lastStepDetail, int testCaseNum, TestStatus result){
			setTestLinkId(testLinkId);
			setLastStepNum(lastStepNum);
			setLastStepDetail(lastStepDetail);
			setTestCaseNum(testCaseNum);
            setResult(result);
		}
	}
	private String name;
	private TestStatus status = TestStatus.Successful;
	private List<TestStepReport> steps = new ArrayList();
	private String exceptionMessage;
		
	private Date date = new Date();
	
	private int totalSteps;
	private int successfulSteps;
	private int failedSteps;
    private List<TestLinkReport> testLinkReports = new ArrayList();
	private List<String> failedStepTitles = new ArrayList();
	
	public void addFailedStepTitle(String title) {
		failedStepTitles.add(title);
	}
	
	public int getTotalSteps() {
		return totalSteps;
	}

	public void increaseTotalSteps() {
		this.totalSteps++;
	}

	public int getSuccessfulSteps() {
		return successfulSteps;
	}

	public void increaseSuccessfulSteps() {
		this.successfulSteps++;
	}

	public int getFailedSteps() {
		return failedSteps;
	}

	public void increaseFailedSteps() {
		this.failedSteps++;
	}

	public Date getDate() {
		return date;
	}

	public List<String> getFailedStepTitles() {
		return failedStepTitles;
	}
	
	public TestReport(String name) {
		this.name = name;
	}
	
	public TestReport(String name, String exceptionMessage) {
		this.name = name;
		this.exceptionMessage = exceptionMessage;
		status = TestStatus.NotExecuted;
	}
	
	public void addStepReport(TestStepReport step) {
		steps.add(step);
	}		
	
	public List<TestStepReport> getSteps() {
		return steps;
	}
		
	public String getName() {
		return name;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}
	
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public TestStatus getStatus() {
		return status;
	}

	public void setStatus(TestStatus status) {
		this.status = status;
	}

    public List<TestLinkReport> getTestLinkReports(){
        return testLinkReports;
    }

    public void addTestLinkReport(String testLinkId, int lastStepNum, String lastStepDetail, int testCaseNum, TestStatus result){
        getTestLinkReports().add(new TestLinkReport(testLinkId, lastStepNum, lastStepDetail, testCaseNum, result));
    }
}
