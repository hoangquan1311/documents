import org.example.Main;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class PaymentAPIServiceTest {

    @Test
    public void testPaymentAPIServiceTimeout() throws InterruptedException {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        long timeoutMillis = 5000;
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                Main.main(new String[]{});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        boolean responseReceived = latch.await(timeoutMillis, TimeUnit.MILLISECONDS);

        System.setOut(originalOut);

        assertTrue("The program is not working or there is no output.", !outputStream.toString().isEmpty());
        if (!responseReceived) {
            assertTrue("No timeout error message.", outputStream.toString().contains("Failed to start listening for responses"));
        }
    }
}
