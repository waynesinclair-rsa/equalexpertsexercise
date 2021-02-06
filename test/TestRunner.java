import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.Iterator;

// Runner class

public class TestRunner {
    public TestRunner() {
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(new Class[]{BookingTest.class});
        Iterator i = result.getFailures().iterator();

        while(i.hasNext()) {
            Failure failure = (Failure)i.next();
            System.out.println(failure.toString());
        }

        System.out.println("Result==" + result.wasSuccessful());
    }
}
