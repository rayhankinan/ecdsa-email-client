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

    public byte[] toByteArray() throws IOException {
        /* Handle X */
        byte[] rawX = this.x.toByteArray();
        byte[] reversedX = new byte[Math.max(rawX.length, 32)];
        Arrays.fill(reversedX, (byte) 0);

        for (int i = 0; i < rawX.length; i++) {
            reversedX[i] = rawX[rawX.length - 1 - i];
        }

        byte[] tmpX = new byte[32];
        System.arraycopy(reversedX, 0, tmpX, 0, 32);
        rawX = tmpX;

        /* Handle Y */
        byte[] rawY = this.y.toByteArray();
        byte[] reversedY = new byte[Math.max(rawY.length, 32)];
        Arrays.fill(reversedY, (byte) 0);

        for (int i = 0; i < rawY.length; i++) {
            reversedY[i] = rawY[rawY.length - 1 - i];
        }

        byte[] tmpY = new byte[32];
        System.arraycopy(reversedY, 0, tmpY, 0, 32);
        rawY = tmpY;

        /* Write to Output Stream */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(rawX);
        outputStream.write(rawY);

        return outputStream.toByteArray();
    }
}
