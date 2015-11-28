package com.qa.framework.reporting;


import com.reltio.qa.Config;
import com.qa.framework.exceptions.FatalTestException;
import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestResponseTimeReport {

    private String logFileName;
    private final String perfLogFileName = "response_time.log";

    private class LogLine
    {
        private String date;
        private String time;
        private String message;
        //2015-04-05 22:24:43,145 [INFO ] - Starting execution of settings...
        private final String regex = "(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2},\\d{3}) \\[(.*)\\] (.*)$";

        public LogLine(String line){
            parseLine(line);
        }

        private void parseLine(String line){
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()){
                this.date = matcher.group(1);
                this.time = matcher.group(2);
                this.message = matcher.group(4);
            }else{
                throw new FatalTestException("Can not parse log line '" + line + "'");
            }
        }

        private boolean isNotRequesttoAuth(){
            return !message.contains(Config.getAsString("vars.authServiceUrl"));}

        private boolean isNotRequesttoStatus(){
            return !message.contains(Config.getAsString("vars.statusUrl"));}

        private boolean isRequest(){
            return message.contains("request:");}

        private boolean isRequestHeader(){
            return message.contains("headers:");}

        private boolean isRequestBody(){
            return message.contains("body:");}

        private boolean isResponse(){
            return message.contains("RESPONSE:");}

        private Timestamp getTimeStamp(){
            Timestamp timestamp;
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS");
                java.util.Date dateParsed = dateFormat.parse(date + " " + time);
                long timeLong = dateParsed.getTime();
                timestamp = new Timestamp(timeLong);
            }catch(ParseException e){
                throw new FatalTestException("Can not parse timestamp '" + time + "'");
            }
            return timestamp;
        }

        private String getRequest(){
            String request = message.substring(message.indexOf(":")+1);
            return request;
        }
    }

    public TestResponseTimeReport(String logFileName){
        this.logFileName = logFileName;
    }

    public void buildReport(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(logFileName));
            PrintWriter printer = new PrintWriter(perfLogFileName, "UTF-8");
            long totalResponseTime = 0;
            try{
                String logLineString = reader.readLine();
                while (logLineString != null) {
                    LogLine logLine = new LogLine(logLineString);
                    if (logLine.isRequest() &&
                        logLine.isNotRequesttoAuth() &&
                        logLine.isNotRequesttoStatus()){
                        String request = logLine.getRequest();
                        long responseTime = logLine.getTimeStamp().getTime();
                        logLine.parseLine(reader.readLine());
                        if (logLine.isRequestHeader() || logLine.isRequestBody())
                            logLine.parseLine(reader.readLine());
                        if (logLine.isRequestHeader() || logLine.isRequestBody())
                            logLine.parseLine(reader.readLine());
                        if (logLine.isResponse()){
                            responseTime = logLine.getTimeStamp().getTime() - responseTime;
                        }else{
                            throw new FatalTestException("There is no expected line with response for request : " + request);
                        }
                        totalResponseTime += responseTime;
                        printer.println(request + "," + responseTime);
                    }
                    logLineString = reader.readLine();
                }
            }
        finally{
            printer.println("Total response time," + totalResponseTime);
            reader.close();
            printer.close();}
        }catch (FileNotFoundException e){
            throw new FatalTestException("Cannot read file " + logFileName);
        }catch (IOException e){
            throw new FatalTestException("An error occurred on reading file " + logFileName);
        }
    }



}
