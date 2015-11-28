package com.qa.tests.suite1;

import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.qa.framework.TestStatus;
import com.qa.framework.annotations.*;
import com.qa.framework.exceptions.TestException;
import com.qa.framework.helpers.Test;
import com.qa.framework.helpers.TestResult;
import com.qa.framework.reporting.HtmlReportBuilder;
import com.qa.framework.reporting.TestReport;
import com.qa.framework.reporting.TestStepReport;
import com.reltio.qa.Config;
import com.reltio.qa.services.AccountService;
import com.reltio.qa.services.StorageService;
import com.reltio.qa.services.TestLinkService;
import com.reltio.qa.utils.ExceptionUtils;
import com.reltio.qa.utils.GsonUtils;
import com.reltio.qa.utils.IOUtils;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class TestExecutor {

    private static final Logger logger = Logger.getLogger(TestExecutor.class);

    private static final String TEST_CLASS_PACKAGE = "com.reltio.qa.tests";
    private static final String REPORTS_PATH = "reports//";
    private static final String TEST_LIST_FILE = System.getenv("TEST_LIST_FILE")!=null?System.getenv("TEST_LIST_FILE"):"testList.json";
    private static final String CONFIG_FILE = System.getenv("CONFIG_FILE")!=null?System.getenv("CONFIG_FILE"):"config.json";
    private static final String TEST_PLAN_NAME = System.getenv("TEST_PLAN_NAME")!=null?System.getenv("TEST_PLAN_NAME"):"Reltio API regression test plan " + new SimpleDateFormat("yyyyMMdd").format(new Date());
    private static final String BUILD_NAME = System.getenv("BUILD_NAME")!=null?System.getenv("BUILD_NAME"):"my_build";
    private static final String DRIVE_CONFIG_PATH = "/qa-api-automatization/config/";
    private static final String DRIVE_REPORT_PATH = "/qa-api-automatization/reports/";
    private static final boolean USE_DRIVE = Boolean.parseBoolean(System.getenv("USE_DRIVE")!=null?System.getenv("USE_DRIVE"):"false");
    private static TestPlan testPlan;
    private static Build testBuild;

    private class TestStepWrapper implements Comparable<TestStepWrapper> {

        public Method method;
        public String title;
        public Float order;
        public String name;
        public String requireStep;
        public String id;


        public TestStepWrapper(Method method, Float order, String title, String name, String requireStep, String id) {
            this.method = method;
            this.order = order;
            this.title = title;
            this.name = name;
            this.requireStep = requireStep;
            this.id = id;
        }


        public int compareTo(TestStepWrapper anotherTestStep) {
            return order.compareTo(anotherTestStep.order);
        }
    }

    private static class TestDefinition {

        public String name;
        public Map<String, Object> vars;
        public String dataFolder;
        public List<Integer> stepsToExecute;
        public List<Integer> excludeSteps;
        public List<String> testCasesToExecute;
        public List<String> excludeTestCases;
        public Boolean showReport;
        public Boolean cleanTenant;
        public Boolean updateConfig;
        public Boolean testLinkReport;

        @SuppressWarnings("unused")
        public TestDefinition(String name, Map<String, Object> vars, String dataFolder, List<Integer> stepsToExecute, boolean showReport, boolean testLinkReport) {
            this.name = name;
            this.vars = vars;
            this.dataFolder = dataFolder;
            this.stepsToExecute = stepsToExecute;
            this.showReport = showReport;
            this.testLinkReport = testLinkReport;
        }

        public boolean showReport() {
            return showReport != null && showReport;
        }

        public boolean testLinkReport() {
            return testLinkReport != null && testLinkReport;
        }

    }


    @org.junit.Test
    public void runTest(){
        StorageService.downloadFromDrive("/qa-api-automatization/config/testList.json", System.getProperty("user.dir"));
    }

    private static void initServices(){
        AccountService.init();
        TestLinkService.init();
    }

    private String rangesToDiscrete(String convertJson, String key){
        // convert ranges to discrete values
        JsonElement j1, je = GsonUtils.getJsonParser().parse(convertJson);
        JsonArray stp = null;
        for (byte bb=0; bb<je.getAsJsonArray().size(); bb++) {
            j1 = je.getAsJsonArray().get(bb).getAsJsonObject();
            try {
                stp = GsonUtils.getElementWithKey(key, j1).getAsJsonArray();
            } catch (Exception e) {
                continue;
            }
            String curVal;
            JsonPrimitive jp;
            for (int i = 0; i < stp.size(); i++) {
                curVal = stp.get(i).toString();
                if (curVal.contains("\"")) {
                    curVal = curVal.replace("\"", "").trim();
                    int pos = curVal.indexOf("-");
                    String min = curVal.substring(0, pos).trim();
                    String max = curVal.substring(pos + 1).trim();
                    for (int a = Integer.parseInt(min); a <= Integer.parseInt(max); a++) {
                        jp = new JsonPrimitive(a);
                        stp.add(jp);
                    }
                    stp.remove(i);
                    i--;
                }
            }
        }
        return je.toString();
    }

    private void addGlobalVars(List<TestDefinition> testList) {
        for (TestDefinition td : testList) {
            Map<String, Object> res = new HashMap<String, Object>(Config.getAsMap("vars"));
            if (td.vars != null) {
                res.putAll(td.vars);
            }
            td.vars = res;
        }
    }

    public void execute(List<TestDefinition> tests) {

        ((RollingFileAppender) Logger.getRootLogger().getAppender("CurrentLog")).rollOver();

        logger.info("Starting execution of tests...");
        for (TestDefinition testDefinition : tests) {
            try {
                Class<?> testClass = Class.forName(TEST_CLASS_PACKAGE + "." + testDefinition.name);
                if (testClass.isAnnotationPresent(ReltioTestCase.class)) {
                    Test instance = (Test) testClass.newInstance();
                    instance.setDataFolder(testDefinition.dataFolder == null ? "resources:" + testDefinition.name.replace(".", "/") : testDefinition.dataFolder);
                    instance.setVars(testDefinition.vars);
                    instance.setUseDrive(USE_DRIVE);
                    instance.setCleanTenant(testDefinition.cleanTenant != null ? testDefinition.cleanTenant:false);
                    instance.setUpdateConfig(testDefinition.updateConfig != null ? testDefinition.updateConfig:false);
                    instance.setTenantName(Config.getAsString("vars.tenant_name"));
                    if (Config.has("vars.datatenant_name"))
                        instance.setDatatenantName(Config.getAsString("vars.datatenant_name"));
                    saveReport(runTest(testClass, instance, testDefinition.stepsToExecute, testDefinition.excludeSteps, testDefinition.testCasesToExecute, testDefinition.excludeTestCases), testDefinition.showReport(), testDefinition.testLinkReport());
                }
            } catch (ClassNotFoundException e) {
                String errorText = String.format("Test '%s' not found", testDefinition.name);
                logger.error(errorText, e);
                saveReport(new TestReport(testDefinition.name, errorText), testDefinition.showReport(), testDefinition.testLinkReport());
            } catch ( IllegalAccessException e) {
                String errorText = "Unexpected error during test initialization: " + e.getMessage();
                logger.error(errorText, e);
                saveReport(new TestReport(testDefinition.name, ExceptionUtils.getStackTrace(e)), testDefinition.showReport(), testDefinition.testLinkReport());
            } catch (InstantiationException e){
                String errorText = "Unexpected error during test initialization: " + e.getMessage();
                logger.error(errorText, e);
                saveReport(new TestReport(testDefinition.name, ExceptionUtils.getStackTrace(e)), testDefinition.showReport(), testDefinition.testLinkReport());
            }
        }
        logger.info("All tests were completed");
    }

    private void saveReport(TestReport testReport, boolean showReport, boolean testLinkReport) {
        File dirToSave = new File(REPORTS_PATH + "//" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dirToSave.mkdirs();

        if (testLinkReport)
            for (TestReport.TestLinkReport report:testReport.getTestLinkReports()){
                TestLinkService.reportResult(testPlan, report.getTestLinkId(), testBuild, report.getResult(), report.getLastStepDetail());
            }
        String reportName = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + "_" + testReport.getName() + "_" + testReport.getStatus() + ".html";
        File reportFile = new File(dirToSave.getAbsolutePath() + "//" + reportName);
        try {
            IOUtils.saveToFile(reportFile, new HtmlReportBuilder(testReport).build(testLinkReport));
        } catch (TemplateException e) {
            logger.error("Unable to save report to file: " + e.getMessage(), e);
        }catch (IOException e) {
            logger.error("Unable to save report to file: " + e.getMessage(), e);
        }

        if (USE_DRIVE) {
            String folderName = System.getenv("BUILD_TAG") != null ? System.getenv("BUILD_TAG") : new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
            StorageService.createFolder(DRIVE_REPORT_PATH, folderName);
            StorageService.uploadFile(reportFile.getAbsolutePath(), DRIVE_REPORT_PATH + folderName);
        }

        if (showReport) {
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
                try {
                    desktop.open(reportFile);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    private TestReport runTest(Class<?> testClass, Object instance, List<Integer> stepsToExecute, List<Integer> excludeSteps) {
        return runTest(testClass, instance, stepsToExecute, excludeSteps, null, null);
    }

    private TestReport runTest(Class<?> testClass, Object instance, List<Integer> stepsToExecute, List<Integer> excludeSteps, List<String> testCasesToExecute, List<String> excludeTestCases) {
        ((RollingFileAppender) Logger.getRootLogger().getAppender("CurrentTestLog")).rollOver();

        TestReport report = new TestReport(testClass.getSimpleName());

        Method beforeTest = null;
        Method afterTest = null;
        final List<TestStepWrapper> steps = new ArrayList();
        final List<String> executedSteps = new ArrayList();

        if (instance != null) {
            logger.info(String.format("Initializing test '%s'", testClass.getSimpleName()));
            Method[] methods = testClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(BeforeTest.class)) {
                    if (beforeTest == null) {
                        beforeTest = method;
                    } else {
                        logger.warn(String.format("More than one BeforeTest method found for test '%s', only the first will execute", testClass.getSimpleName()));
                    }
                }
                if (method.isAnnotationPresent(AfterTest.class)) {
                    if (afterTest == null) {
                        afterTest = method;
                    } else {
                        logger.warn(String.format("More than one AfterTest method found for test '%s', only the first will execute", testClass.getSimpleName()));
                    }
                }
                if (method.isAnnotationPresent(TestStep.class)) {
                    TestStep stepAnnotation = method.getAnnotation(TestStep.class);
                    String requireStep = null;
                    if (method.isAnnotationPresent(Require.class)) {
                        requireStep = method.getAnnotation(Require.class).stepName();
                    }
                    steps.add(new TestStepWrapper(method, stepAnnotation.order(), stepAnnotation.title(), method.getName(), requireStep, stepAnnotation.id()));
                }
            }

            Collections.sort(steps);

            logger.info(String.format("Executing test '%s'", testClass.getSimpleName()));
            int stepNumber = 0;
            String stepTitle = "";
            try {

                if (beforeTest != null) {
                    beforeTest.invoke(instance);
                }

//				List<Integer> stp = Arrays.asList(new Integer[] {1, 22, 35, 38, 40, 42, 44, 45, 47, 68, 73, 76, 100, 105});

                for (TestStepWrapper step : steps) {
                    stepNumber++;

                    if ((stepsToExecute != null && !stepsToExecute.contains(stepNumber)) || (excludeSteps != null && excludeSteps.contains(stepNumber))) {
                        continue;
                    }

                    if (((testCasesToExecute != null && step.id.isEmpty()) ||
                            (testCasesToExecute != null && !testCasesToExecute.contains(step.id))) ||
                            ((excludeTestCases != null && !step.id.isEmpty())&&
                                    (excludeTestCases != null && excludeTestCases.contains(step.id)))) {
                        continue;
                    }

                    if (step.requireStep != null && !executedSteps.contains(step.requireStep)) {
                        logger.warn(String.format("Step%d '%s' was not executed, because pre-required step was not executed", stepNumber, step.title));
                        continue;
                    }

                    stepTitle = step.id.isEmpty()?step.title:step.id + " : " + TestLinkService.getTitle(step.id);

                    TestLinkService.addTestCaseToTestPlan(testPlan, step.id);

                    try {
                        TestResult result = (TestResult) step.method.invoke(instance);
                        report.increaseTotalSteps();
                        if (result.isSuccessful()) {
                            logger.info(String.format("Test%02d '%s' passed successfully", stepNumber, stepTitle));
                            report.addStepReport(new TestStepReport(stepNumber, String.format("Test%d '%s'", stepNumber, stepTitle), TestStatus.Successful));
                            report.increaseSuccessfulSteps();
                            executedSteps.add(step.name);
                        } else {
                            logger.info(String.format("Test%02d '%s' failed", stepNumber, stepTitle));
                            logger.info("MESSAGE: " + result.getMessage());
                            report.addStepReport(new TestStepReport(stepNumber, String.format("Test%d '%s'", stepNumber, stepTitle), TestStatus.Failed, result.getMessage()));
                            report.setStatus(TestStatus.Failed);
                            report.increaseFailedSteps();
                            report.addFailedStepTitle(step.title);
                        }
                        if (result.getTestCaseId() != null)
                            report.addTestLinkReport(result.getTestCaseId(), result.getStepNum(), result.getMessage(), stepNumber, result.getStatus());
                        else
                            logger.error("Test with num " + stepNumber + " is not mapped to Test link");
                    } catch (Exception e) {
                        Throwable parent = e.getCause();
                        if (parent != null && parent instanceof TestException) {
                            report.increaseFailedSteps();
                            report.increaseTotalSteps();
                            report.addStepReport(new TestStepReport(stepNumber, String.format("Step%02d '%s'", stepNumber, stepTitle), TestStatus.Exception, ExceptionUtils.getStackTrace(parent)));
                            report.setStatus(TestStatus.Exception);
                            logger.error(String.format("Exception occurred during execution of step%02d of '%s' test: '%s'", stepNumber, testClass.getSimpleName(), parent.getMessage()), e);
                        } else {
                            throw e;
                        }
                    }
                }

                if (afterTest != null) {
                    afterTest.invoke(instance);
                }

            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                Throwable parent = e.getCause();
                report.setStatus(TestStatus.NotExecuted);
                report.setExceptionMessage(String.format("Unexpected error occurred during execution of step%02d '%s':\n%s", stepNumber, stepTitle, ExceptionUtils.getStackTrace((parent != null) ? parent : e)));
                logger.error(String.format("Unexpected error occurred during execution of step%02d of '%s' test: '%s'", stepNumber, testClass.getSimpleName(), (parent != null) ? parent.getMessage() : e.getMessage()), e);
            }
            logger.info(String.format("Test '%s' completed", testClass.getSimpleName()));
        }

        return report;
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[16384];
            for (int len = is.read(buffer); len > 0; len = is.read(buffer)) {
                bos.write(buffer, 0, len);
            }
        } finally {
            is.close();
        }
        return new String(bos.toByteArray(), "UTF-8");
    }
}
