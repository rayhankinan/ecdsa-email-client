import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;

public class App {
    public static void main(String[] args) throws Exception {
        BigInteger a = new BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", 16);
        BigInteger b = new BigInteger("5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B", 16);
        BigInteger p = new BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", 16);

        EllipticalCurve ellipticalCurve = new EllipticalCurve(a, b, p);
        Point basePoint = new Point(
                new BigInteger("6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", 16),
                new BigInteger("4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", 16));
        BigInteger n = new BigInteger("FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 16);

        byte[] message = "Hello World!".getBytes();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256"); // INI DIGANTI DENGAN ALGORITMA GAGAS
        byte[] hash = messageDigest.digest(message);

        EllipticalCurveKeyStore ellipticalCurveKeyStore = new EllipticalCurveKeyStore("key/example.p12",
                "password");
        ellipticalCurveKeyStore.load();

        PrivateKey readPrivateKey = ellipticalCurveKeyStore.read();

        // PrivateKey initialPrivateKey =
        // EllipticalCurveKey.generatePrivateKey(ellipticalCurve, basePoint, n);
        // ellipticalCurveKeyStore.save(initialPrivateKey);
        // ellipticalCurveKeyStore.store();

        PublicKey publicKey = EllipticalCurveKey.generatePublicKey(ellipticalCurve, basePoint, n, readPrivateKey);

        DigitalSigning digitalSigning = new DigitalSigning(ellipticalCurve, basePoint, n, readPrivateKey);
        byte[] sign = digitalSigning.getSigning(hash);

        DigitalVerifying digitalVerifying = new DigitalVerifying(ellipticalCurve, basePoint, n, publicKey);
        boolean result = digitalVerifying.verifySigning(hash, sign);
        System.out.println(result);
    }
}
