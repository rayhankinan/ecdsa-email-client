import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

public class EmailParser {
    public static String exportPrivateKey(PrivateKey privateKey) {
        byte[] encodedPrivateKey = privateKey.getEncoded();

        return new String(encodedPrivateKey);
    }

    public static PrivateKey importPrivateKey(String rawPrivateKey) {
        byte[] encodedPrivateKey = rawPrivateKey.getBytes();

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
                return encodedPrivateKey;
            }
        };
    }

    public static String exportPublicKey(PublicKey publicKey) {
        byte[] encodedPublicKey = publicKey.getEncoded();

        return new String(encodedPublicKey);
    }

    public static PublicKey importPublicKey(String rawPublicKey) {
        byte[] encodedPublicKey = rawPublicKey.getBytes();

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
                return encodedPublicKey;
            }
        };
    }

    public static String exportSignature(byte[] signature) {
        BigInteger signatureValue = new BigInteger(1, signature);

        return signatureValue.toString(16);
    }

    public static byte[] importSignature(String rawSignature) {
        BigInteger signatureValue = new BigInteger(rawSignature, 16);

        return signatureValue.toByteArray();
    }
}
