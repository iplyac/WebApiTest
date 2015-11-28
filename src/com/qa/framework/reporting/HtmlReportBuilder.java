package com.qa.framework.reporting;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qa.framework.TestStatus;
import com.qa.tests.tasks.TestExecutor;
import com.reltio.qa.services.TestLinkService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class HtmlReportBuilder {				
	private TestReport testReport;
	
	public HtmlReportBuilder(TestReport testReport) {
		this.testReport = testReport;
	}

	public String build(boolean testLinkReport) throws IOException, TemplateException {
		Writer report = new StringWriter();
		Configuration cfg = new Configuration();
		StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
		stringTemplateLoader.putTemplate("report", TestExecutor.inputStreamToString(getClass().getClassLoader().getResourceAsStream(testLinkReport ? "reportTestLink.tpl" : "report.tpl")));
		cfg.setTemplateLoader(stringTemplateLoader);
		cfg.getTemplate("report").process(testLinkReport?getTestLinkReportData():getSimpleReportData(), report);
        report.close();

		return report.toString();				
	}

    private Map<String, Object> getSimpleReportData(){
        Map<String, Object> reportData = new HashMap();
        reportData.put("name", testReport.getName());
        reportData.put("status", testReport.getStatus());
        reportData.put("date", testReport.getDate().toString());
        reportData.put("totalSteps", testReport.getTotalSteps());
        reportData.put("successfulSteps", testReport.getSuccessfulSteps());
        reportData.put("failedSteps", testReport.getFailedSteps());
        if (testReport.getExceptionMessage() != null) {
            reportData.put("exceptionMessage", testReport.getExceptionMessage());
        }

        List<Object> steps = new ArrayList();
        for (TestStepReport step : testReport.getSteps()) {
            Map<String, Object> stepObj = new HashMap();
            stepObj.put("title", step.getTitle());
            stepObj.put("status", step.getStatus());
            stepObj.put("message", step.getMessage());
            steps.add(stepObj);
        }
        reportData.put("steps", steps);

        return reportData;
    }

    private Map<String, Object> getTestLinkReportData(){
        Map<String, Object> reportData = new HashMap();
        reportData.put("name", testReport.getName());
        reportData.put("status", testReport.getStatus());
        reportData.put("date", testReport.getDate().toString());
        reportData.put("totalSteps", testReport.getTotalSteps());
        reportData.put("successfulSteps", testReport.getSuccessfulSteps());
        reportData.put("failedSteps", testReport.getFailedSteps());

        List<Object> testResults = new ArrayList();

        for (TestReport.TestLinkReport testLinkReport:testReport.getTestLinkReports()) {
            Map<String, Object> testResult = new HashMap();
            testResult.put("title", testLinkReport.getTestLinkId() + " " + TestLinkService.getTitle(testLinkReport.getTestLinkId()));
            testResult.put("result", (testReport.getSteps().get(testResults.size()).getStatus() != TestStatus.Successful)?"Failed":"Passed");
            testResult.put("testNum", testResults.size());
            testResults.add(testResult);
        }
        reportData.put("testResults", testResults);

        List<Object> testCases = new ArrayList();
        for (TestReport.TestLinkReport testLinkReport:testReport.getTestLinkReports()) {
            Map<String, Object> testCase = new HashMap();
            testCase.put("testNum", testCases.size());
            testCase.put("title", testLinkReport.getTestLinkId() + " " + TestLinkService.getTitle(testLinkReport.getTestLinkId()));
            testCase.put("summary", TestLinkService.getSummary(testLinkReport.getTestLinkId()));

            List<String> actions = TestLinkService.getActions(testLinkReport.getTestLinkId());
            List<String> expected = TestLinkService.getExpected(testLinkReport.getTestLinkId());
            List<Object> steps = new ArrayList();

            testLinkReport.setLastStepNum((testLinkReport.getLastStepNum() > 0) ? testLinkReport.getLastStepNum() : actions.size());

            for (int stepNum = 0; stepNum < testLinkReport.getLastStepNum(); stepNum++) {
                Map<String, Object> step = new HashMap();
                step.put("number", stepNum + 1);
                step.put("action", actions.get(stepNum));
                step.put("expectedResult", expected.get(stepNum));
                step.put("status", (testReport.getSteps().get(testCases.size()).getStatus() != TestStatus.Successful)&&(stepNum + 1 == testLinkReport.getLastStepNum())?"Failed":"Passed");
                steps.add(step);
            }
            testCase.put("steps", steps);
            testCase.put("details", testReport.getSteps().get(testCases.size()).getStatus() != TestStatus.Successful ? testReport.getSteps().get(testCases.size()).getMessage() : "");
            testCase.put("result", (testReport.getSteps().get(testCases.size()).getStatus() != TestStatus.Successful)?"Failed":"Passed");
            testCases.add(testCase);
        }
        reportData.put("testCases", testCases);

        return reportData;
    }
}
