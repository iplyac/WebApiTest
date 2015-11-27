import com.qa.utils.AssertHelper;
import com.qa.utils.WebApiHelper;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class test1 {

    private static Logger logger = Logger.getLogger(test1.class);
    private static WebApiHelper api = new WebApiHelper();
    @Test
    public void test(){
        AssertHelper.assertTrue(true);
    }

    @Before
    public void precond(){
        logger.info("start1");
    }

    @After
    public void after(){
        logger.info("done");
    }
}
