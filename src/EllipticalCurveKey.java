import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.IOException;
import javax.crypto.SecretKey;

public class EllipticalCurveKey {
    public static PrivateKey generatePrivateKey(EllipticalCurve curve, Point basePoint, BigInteger n) {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger rawPrivateKey;

        do {
            rawPrivateKey = new BigInteger(n.bitLength(), secureRandom);
        } while (rawPrivateKey.compareTo(n) >= 0 || rawPrivateKey.compareTo(BigInteger.ZERO) == 0);

        byte[] encodedPrivateKey = rawPrivateKey.toByteArray();
        byte[] encodedPrivateKeyCopy = new byte[encodedPrivateKey.length];

        System.arraycopy(encodedPrivateKey, 0, encodedPrivateKeyCopy, 0, encodedPrivateKey.length);

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

    public static PublicKey generatePublicKey(EllipticalCurve curve, Point basePoint, BigInteger n,
            PrivateKey privateKey)
            throws IOException {
        byte[] encodedPrivateKey = privateKey.getEncoded();
        BigInteger rawPrivateKey = new BigInteger(1, encodedPrivateKey);

        byte[] encodedPublicKey = curve.multiplyPoint(basePoint, rawPrivateKey).toByteArray();
        byte[] encodedPublicKeyCopy = new byte[encodedPublicKey.length];

        System.arraycopy(encodedPublicKey, 0, encodedPublicKeyCopy, 0, encodedPublicKey.length);

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
                return encodedPublicKeyCopy;
            }
        };
    }

    public static SecretKey convertToSecretKey(PrivateKey privateKey) {
        byte[] encodedPrivateKey = privateKey.getEncoded();
        byte[] encodedSecretKey = new byte[encodedPrivateKey.length];

        System.arraycopy(encodedPrivateKey, 0, encodedSecretKey, 0, encodedPrivateKey.length);

        return new SecretKey() {
            @Override
            public String getAlgorithm() {
                return "AES";
            }

            @Override
            public String getFormat() {
                return "RAW";
            }

            @Override
            public byte[] getEncoded() {
                return encodedSecretKey;
            }
        };
    }

    public static PrivateKey convertToPrivateKey(SecretKey secretKey) {
        byte[] encodedSecretKey = secretKey.getEncoded();
        byte[] encodedPrivateKey = new byte[encodedSecretKey.length];

        System.arraycopy(encodedSecretKey, 0, encodedPrivateKey, 0, encodedSecretKey.length);

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
}
