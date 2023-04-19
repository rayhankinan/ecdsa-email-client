import java.security.PrivateKey;
import java.util.Base64;

public class App {
    public static void main(String[] args) throws Exception {
        String message = "Hello World!";

        EllipticalCurveKeyStore ellipticalCurveKeyStore = new EllipticalCurveKeyStore("key/example.p12",
                "password");
        ellipticalCurveKeyStore.load();

        PrivateKey readPrivateKey = ellipticalCurveKeyStore.read();

        // PrivateKey initialPrivateKey = EmailParser.generatePrivateKey();
        // ellipticalCurveKeyStore.save(initialPrivateKey);
        // ellipticalCurveKeyStore.store();

        System.out.println(Base64.getEncoder().encodeToString(readPrivateKey.getEncoded()));

        String wholeMessage = EmailParser.signMessage(readPrivateKey, message);

        String[] firstPartition = wholeMessage.split("\n---BEGIN SIGNATURE---\n");
        String receivedMessage = firstPartition[0];

        String[] secondPartition = firstPartition[1].split("\n---END SIGNATURE---\n---BEGIN PUBLIC KEY---\n");
        String sign = secondPartition[0];
        String publicKey = secondPartition[1].replaceAll("\n---END PUBLIC KEY---\n", "");

        boolean result = EmailParser.verifyMessage(receivedMessage, sign, publicKey);

        System.out.println(result);
    }
}
