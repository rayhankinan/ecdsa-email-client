import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

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

    /* Little Endian */
    public Point(byte[] rawPoint) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(rawPoint);

        byte[] rawX = new byte[32];
        byte[] rawY = new byte[32];

        inputStream.read(rawX);
        inputStream.read(rawY);

        byte[] reversedX = new byte[32];
        for (int i = 0; i < rawX.length; i++) {
            reversedX[i] = rawX[rawX.length - 1 - i];
        }

        byte[] reversedY = new byte[32];
        for (int i = 0; i < rawY.length; i++) {
            reversedY[i] = rawY[rawY.length - 1 - i];
        }

        this.x = new BigInteger(1, reversedX);
        this.y = new BigInteger(1, reversedY);
        this.z = BigInteger.ONE;
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

    /* Little Endian */
    public byte[] toByteArray() throws IOException {
        /* Handle X */
        byte[] rawX = this.x.toByteArray();
        byte[] reversedX = new byte[32];

        Arrays.fill(reversedX, (byte) 0);
        for (int i = 0; i < Math.min(rawX.length, 32); i++) {
            reversedX[i] = rawX[rawX.length - 1 - i];
        }

        /* Handle Y */
        byte[] rawY = this.y.toByteArray();
        byte[] reversedY = new byte[32];

        Arrays.fill(reversedY, (byte) 0);
        for (int i = 0; i < Math.min(rawY.length, 32); i++) {
            reversedY[i] = rawY[rawY.length - 1 - i];
        }

        /* Write to Output Stream */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(reversedX);
        outputStream.write(reversedY);

        return outputStream.toByteArray();
    }
}
