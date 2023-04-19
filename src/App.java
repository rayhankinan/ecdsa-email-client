import java.security.PrivateKey;

public class App {
    public static void main(String[] args) throws Exception {
        EllipticalCurveKeyStore ellipticalCurveKeyStore = new EllipticalCurveKeyStore("key/example.p12",
                "password");
        ellipticalCurveKeyStore.load();

        PrivateKey readPrivateKey = ellipticalCurveKeyStore.read();

        System.out.println("Private Key: " + EmailParser.exportPrivateKey(readPrivateKey));

        String wholeMessage = EmailParser.signMessage(readPrivateKey, "Test dulu");

        String[] firstPartition = wholeMessage.split("\n---BEGIN SIGNATURE---\n");
        String receivedMessage = firstPartition[0];

        String[] secondPartition = firstPartition[1].split("\n---END SIGNATURE---\n---BEGIN PUBLIC KEY---\n");
        String sign = secondPartition[0];
        String publicKey = secondPartition[1].replaceAll("\n---END PUBLIC KEY---\n", "");

        System.out.println("Public Key: " + publicKey);

        boolean result = EmailParser.verifyMessage(receivedMessage, sign, publicKey);

        System.out.println(result);
    }
}
