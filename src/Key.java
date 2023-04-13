import java.math.BigInteger;
import java.security.SecureRandom;

public class Key extends EllipticalCurveCryptography {
    private BigInteger privateKey;
    private Point publicKey;

    public Key(EllipticalCurve curve, Point basePoint, BigInteger n) {
        super(curve, basePoint, n);

        SecureRandom secureRandom = new SecureRandom();
        BigInteger privateKey;
        do {
            privateKey = new BigInteger(n.bitLength(), secureRandom);
        } while (privateKey.compareTo(n) >= 0 || privateKey.compareTo(BigInteger.ZERO) == 0);

        this.privateKey = privateKey;
        this.publicKey = this.curve.multiplyPoint(basePoint, privateKey);
    }

    public BigInteger getPrivateKey() {
        return this.privateKey;
    }

    public Point getPublicKey() {
        return this.publicKey;
    }
}
