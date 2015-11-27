package com.qa;

import com.qa.models.*;
import com.qa.models.responses.WebApiAccessTokenResponseModel;
import com.qa.utils.WebApiHelper;
import org.apache.log4j.Logger;

public class starter {
    private static Logger logger = Logger.getLogger(starter.class);
    private static WebApiHelper api = new WebApiHelper();

    public static void main(String[] args){
        api.getAccessToken().assertSuccess();
        logger.info("done");
    }

}
