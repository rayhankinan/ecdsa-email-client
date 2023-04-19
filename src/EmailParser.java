import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class EmailParser {
    public static BigInteger a = new BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", 16);
    public static BigInteger b = new BigInteger("5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B", 16);
    public static BigInteger p = new BigInteger("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", 16);
    public static EllipticalCurve ellipticalCurve = new EllipticalCurve(a, b, p);
    public static Point basePoint = new Point(
            new BigInteger("6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", 16),
            new BigInteger("4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", 16));
    public static BigInteger n = new BigInteger("FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 16);

    public static String exportPrivateKey(PrivateKey privateKey) {
        byte[] encodedPrivateKey = privateKey.getEncoded();

        return Base64.getEncoder().encodeToString(encodedPrivateKey);
    }

    public static PrivateKey importPrivateKey(String rawPrivateKey) {
        byte[] decodedPrivateKey = Base64.getDecoder().decode(rawPrivateKey);

        return new PrivateKey() {
            @Override
            public String getAlgorithm() {
                return "ECDSA";
            }

            @Override
            public String getFormat() {
                return "PKCS#8";
            }

            @Override
            public byte[] getEncoded() {
                return decodedPrivateKey;
            }
        };
    }

    public static String exportPublicKey(PublicKey publicKey) {
        byte[] encodedPublicKey = publicKey.getEncoded();

        return Base64.getEncoder().encodeToString(encodedPublicKey);
    }

    public static PublicKey importPublicKey(String rawPublicKey) {
        byte[] decodedPublicKey = Base64.getDecoder().decode(rawPublicKey);

        return new PublicKey() {
            @Override
            public String getAlgorithm() {
                return "ECDSA";
            }

            @Override
            public String getFormat() {
                return "X.509";
            }

            @Override
            public byte[] getEncoded() {
                return decodedPublicKey;
            }
        };
    }

    public static String exportSignature(byte[] signature) {
        return Base64.getEncoder().encodeToString(signature);
    }

    public static byte[] importSignature(String rawSignature) {
        return Base64.getDecoder().decode(rawSignature);
    }

    public static PrivateKey generatePrivateKey() {
        return EllipticalCurveKey.generatePrivateKey(EmailParser.ellipticalCurve, EmailParser.basePoint, EmailParser.n);
    }

    public static PublicKey generatePublicKey(PrivateKey privateKey) throws IOException {
        return EllipticalCurveKey.generatePublicKey(EmailParser.ellipticalCurve, EmailParser.basePoint, EmailParser.n,
                privateKey);
    }

    public static PrivateKey getOrGeneratePrivateKey() {
        PrivateKey privateKey;

        try {
            EllipticalCurveKeyStore ellipticalCurveKeyStore = new EllipticalCurveKeyStore("key/example.p12",
                    "password");
            ellipticalCurveKeyStore.load();

            privateKey = ellipticalCurveKeyStore.read();

            if (privateKey == null) {
                PrivateKey initialPrivateKey = EmailParser.generatePrivateKey();
                ellipticalCurveKeyStore.save(initialPrivateKey);
                ellipticalCurveKeyStore.store();

                privateKey = initialPrivateKey;
            }

        } catch (Exception e) {
            privateKey = EmailParser.generatePrivateKey();
        }

        return privateKey;
    }

    public static String signMessage(PrivateKey privateKey, String message)
            throws NoSuchAlgorithmException, IOException {
        byte[] rawMessage = message.getBytes();

        Keccak keccak = new Keccak();
        byte[] hash = keccak.hash(rawMessage);

        DigitalSigning digitalSigning = new DigitalSigning(EmailParser.ellipticalCurve, EmailParser.basePoint,
                EmailParser.n, privateKey);
        byte[] rawSign = digitalSigning.getSigning(hash);
        String sign = EmailParser.exportSignature(rawSign);

        PublicKey rawPublicKey = EllipticalCurveKey.generatePublicKey(ellipticalCurve, basePoint, n, privateKey);
        String publicKey = EmailParser.exportPublicKey(rawPublicKey);

        return message + "\n"
                + "---BEGIN SIGNATURE---\n" + sign + "\n---END SIGNATURE---\n"
                + "---BEGIN PUBLIC KEY---\n" + publicKey + "\n---END PUBLIC KEY---\n";
    }

    public static boolean verifyMessage(String message, String sign, String publicKey)
            throws NoSuchAlgorithmException, IOException {
        byte[] rawMessage = message.getBytes();

        Keccak keccak = new Keccak();
        byte[] hash = keccak.hash(rawMessage);

        PublicKey rawPublicKey = EmailParser.importPublicKey(publicKey);
        byte[] rawSign = EmailParser.importSignature(sign);

        DigitalVerifying digitalVerifying = new DigitalVerifying(EmailParser.ellipticalCurve, EmailParser.basePoint,
                EmailParser.n, rawPublicKey);

        return digitalVerifying.verifySigning(hash, rawSign);
    }
}
