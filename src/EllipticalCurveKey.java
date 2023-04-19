import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.IOException;

public class EllipticalCurveKey {
    public static PrivateKey generatePrivateKey(EllipticalCurve curve, Point basePoint, BigInteger n) {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger rawPrivateKey;

        do {
            rawPrivateKey = new BigInteger(n.bitLength(), secureRandom);
        } while (rawPrivateKey.compareTo(n) >= 0 || rawPrivateKey.compareTo(BigInteger.ZERO) == 0);

        final byte[] encodedPrivateKey = rawPrivateKey.toByteArray();

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
    };

    public static PublicKey generatePublicKey(EllipticalCurve curve, Point basePoint, BigInteger n,
            PrivateKey privateKey)
            throws IOException {
        byte[] encodedPrivateKey = privateKey.getEncoded();
        BigInteger rawPrivateKey = new BigInteger(1, encodedPrivateKey);

        final byte[] encodedPublicKey = curve.multiplyPoint(basePoint, rawPrivateKey).toByteArray();

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
    };
}
