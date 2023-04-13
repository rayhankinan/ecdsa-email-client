import java.math.BigInteger;

public class EllipticalCurveCryptography {
    protected EllipticalCurve curve;
    protected Point basePoint;
    protected BigInteger n;

    public EllipticalCurveCryptography(EllipticalCurve curve, Point basePoint, BigInteger n) {
        if (!curve.checkPoint(basePoint)) {
            throw new IllegalArgumentException("The base point is not on the curve");
        }

        this.curve = curve;
        this.basePoint = basePoint;
        this.n = n;
    }
}
