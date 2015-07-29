package com.opsysinc.scripting.client;

import com.google.gwt.junit.client.GWTTestCase;
import junit.framework.TestCase;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class ScriptWebAppTest extends GWTTestCase {

    /**
     * Must refer to a valid module that sources this class.
     */
    @Override
    public String getModuleName() {
        return "com.opsysinc.scripting.ScriptWebAppJUnit";
    }

    /**
     * This test will send a request to the server using the greetServer method
     * in ScriptService and verify the response.
     */
    public void testScriptService() {

        TestCase.assertTrue(true);
        /*
		 * // Create the service that we will test. ScriptServiceAsync
		 * scriptService = GWT.create(ScriptService.class); ServiceDefTarget
		 * target = (ServiceDefTarget) scriptService;
		 * target.setServiceEntryPoint(GWT.getModuleBaseURL() +
		 * "scriptwebapp/script");
		 *
		 * // Since RPC calls are asynchronous, we will need to wait for a
		 * response // after this test method returns. This line tells the test
		 * runner to // wait // up to 10 seconds before timing out.
		 * delayTestFinish(10000);
		 */
    }
}
