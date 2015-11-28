package com.qa.framework.reporting;

import com.qa.framework.TestStatus;

public class TestStepReport {

	private int order;
	private String title;
	private TestStatus status;
	private String message;
	
	public TestStepReport(int order, String title, TestStatus isSuccessful) {	
		this.order = order;
		this.title = title;
		this.status = isSuccessful;		
	}
	
	public TestStepReport(int order, String title, TestStatus isSuccessful, String message) {	
		this(order, title, isSuccessful);
		this.message = message;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

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
	
	
}
