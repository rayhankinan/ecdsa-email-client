import java.math.BigInteger;

public class Point {
    private BigInteger x;
    private BigInteger y;
    private BigInteger z;

    public Point(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
        this.z = BigInteger.ONE;
    }

    public Point(Point P) {
        this.x = P.getX();
        this.y = P.getY();
        this.z = P.getZ();
    }

    // Titik O(0, 1, 0)
    public Point() {
        this.x = BigInteger.ZERO;
        this.y = BigInteger.ONE;
        this.z = BigInteger.ZERO;
    }

    public BigInteger getX() {
        return this.x;
    }

    public BigInteger getY() {
        return this.y;
    }

    public BigInteger getZ() {
        return this.z;
    }

    public boolean equals(Point P) {
        return this.x.equals(P.getX()) && this.y.equals(P.getY()) && this.z.equals(P.getZ());
    }

    public boolean isZero() {
        return this.x.equals(BigInteger.ZERO) && this.z.equals(BigInteger.ZERO);
    }
}
