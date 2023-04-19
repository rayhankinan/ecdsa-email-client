import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.PrivateKey;

public class DigitalSigning extends EllipticalCurveCryptography {
    private BigInteger rawPrivateKey;

    public DigitalSigning(EllipticalCurve curve, Point basePoint, BigInteger n, PrivateKey privateKey) {
        super(curve, basePoint, n);

        byte[] encodedPrivateKey = privateKey.getEncoded();

        this.rawPrivateKey = new BigInteger(1, encodedPrivateKey);
    }

    public Point getSigning(BigInteger hashedValue) {
        BigInteger r, s;

        do {
            SecureRandom secureRandom = new SecureRandom();
            BigInteger k;
            do {
                k = new BigInteger(n.bitLength(), secureRandom);
            } while (k.compareTo(n) >= 0 || k.compareTo(BigInteger.ZERO) == 0);

            Point P = this.curve.multiplyPoint(basePoint, k);
            r = P.getX().mod(n);
            s = k.modInverse(n).multiply(hashedValue.add(rawPrivateKey.multiply(r))).mod(n);
        } while (r.compareTo(BigInteger.ZERO) == 0 || s.compareTo(BigInteger.ZERO) == 0);

        return new Point(r, s);
    }
}
