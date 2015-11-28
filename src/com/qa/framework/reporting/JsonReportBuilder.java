package com.qa.framework.reporting;

import java.util.ArrayList;
import java.util.List;

import com.reltio.qa.utils.GsonUtils;

public class JsonReportBuilder {

	private List<TestReport> tests = new ArrayList();
	
	public void addTestReport(TestReport testReport) {
		tests.add(testReport);
	}
		
	public String build() {
		return GsonUtils.getGsonPrettyPrint().toJson(this);
	}

}
