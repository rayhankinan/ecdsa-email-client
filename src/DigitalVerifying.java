import java.math.BigInteger;

public class DigitalVerifying extends EllipticalCurveCryptography {
    private Point publicKey;

    public DigitalVerifying(EllipticalCurve curve, Point basePoint, BigInteger n, Point publicKey) {
        super(curve, basePoint, n);

        this.publicKey = publicKey;
    }

    public boolean verifySigning(BigInteger hashedValue, Point sign) {
        BigInteger r = sign.getX();
        BigInteger s = sign.getY();

        BigInteger w = s.modInverse(n);
        BigInteger u1 = hashedValue.multiply(w).mod(n);
        BigInteger u2 = r.multiply(w).mod(n);

        Point P = this.curve.addPoint(
                this.curve.multiplyPoint(this.basePoint, u1),
                this.curve.multiplyPoint(this.publicKey, u2));

        if (P.isZero()) {
            return false;
        }

        BigInteger v = P.getX().mod(n);

        return v.equals(r);
    }
}
