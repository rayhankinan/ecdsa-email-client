import java.math.BigInteger;

public class EllipticalCurve {
    private BigInteger a;
    private BigInteger b;
    private BigInteger p;

    public EllipticalCurve(BigInteger a, BigInteger b, BigInteger p) {
        if (BigInteger.valueOf(4).multiply(a.pow(3)).add(BigInteger.valueOf(27).multiply(b.pow(2)))
                .equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("The curve is not elliptical");
        }

        this.a = a;
        this.b = b;
        this.p = p;
    }

    public boolean checkPoint(Point P) {
        return P.getX().pow(3)
                .add(this.a.multiply(P.getX()).multiply(P.getZ().pow(2)))
                .add(this.b.multiply(P.getZ().pow(3))).mod(this.p)
                .equals(P.getY().pow(2).multiply(P.getZ()).mod(this.p));
    }

    public Point addPoint(Point P, Point Q) {
        if (!this.checkPoint(P) || !this.checkPoint(Q))
            throw new IllegalArgumentException("The points are not on the curve");

        if (P.isZero()) // P + O = P
            return new Point(Q);

        if (Q.isZero()) // O + Q = Q
            return new Point(P);

        if (this.locatedOpposite(P, Q)) // P + (-P) = O
            return new Point();

        if (P.equals(Q)) { // P + P = 2P
            BigInteger gradient = BigInteger.valueOf(3).multiply(P.getX().pow(2)).add(this.a)
                    .multiply(BigInteger.valueOf(2).multiply(P.getY()).modInverse(this.p)).mod(this.p);
            BigInteger x = gradient.pow(2).subtract(BigInteger.valueOf(2).multiply(P.getX())).mod(this.p);
            BigInteger y = gradient.multiply(P.getX().subtract(x)).subtract(P.getY()).mod(this.p);

            return new Point(x, y);
        }

        BigInteger gradient = Q.getY().subtract(P.getY()).multiply(Q.getX().subtract(P.getX()).modInverse(this.p))
                .mod(this.p);
        BigInteger x = gradient.pow(2).subtract(P.getX()).subtract(Q.getX()).mod(this.p);
        BigInteger y = gradient.multiply(P.getX().subtract(x)).subtract(P.getY()).mod(this.p);

        return new Point(x, y);
    }

    public Point subtractPoint(Point P, Point Q) {
        if (!this.checkPoint(P) || !this.checkPoint(Q)) {
            throw new IllegalArgumentException("The points are not on the curve");
        }

        return this.addPoint(P, this.getNegation(Q));
    }

    public Point multiplyPoint(Point P, BigInteger k) {
        if (!this.checkPoint(P)) {
            throw new IllegalArgumentException("The points are not on the curve");
        }

        Point sum = new Point(P);
        Point tempP = new Point(P);
        BigInteger tempK = k.subtract(BigInteger.ONE);

        while (tempK.compareTo(BigInteger.ZERO) == 1) {
            if (tempK.mod(BigInteger.valueOf(2)).equals(BigInteger.ONE)) {
                sum = this.addPoint(sum, tempP);
            }

            tempK = tempK.shiftRight(1);
            tempP = this.addPoint(tempP, tempP);
        }

        return sum;
    }

    private Point getNegation(Point P) {
        return new Point(P.getX(), this.p.subtract(P.getY()).mod(this.p));
    }

    private boolean locatedOpposite(Point P, Point Q) {
        return P.equals(this.getNegation(Q));
    }
}
