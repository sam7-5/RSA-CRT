import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Random;

public class Utils {

    private static Random r = new Random();

    public static BigInteger euclideanAlgorithm(BigInteger a, BigInteger b) {
        return a.equals(BigInteger.ZERO) ? b : euclideanAlgorithm(b.mod(a), a);
    }

    public static Entry<BigInteger, BigInteger> extendedEuclideanAlgorithm(BigInteger a, BigInteger b) {
        BigInteger x = BigInteger.ZERO, y = BigInteger.ONE, lastX = BigInteger.ONE, lastY = BigInteger.ZERO, temp;

        while (!b.equals(BigInteger.ZERO)) {
            BigInteger q = a.divide(b);
            BigInteger r = a.mod(b);

            a = b;
            b = r;

            temp = x;
            x = lastX.subtract(q.multiply(x));
            lastX = temp;

            temp = y;
            y = lastY.subtract(q.multiply(y));
            lastY = temp;
        }

        return new AbstractMap.SimpleEntry<>(lastX, lastY);
    }

    public static BigInteger quickExponent(BigInteger base, BigInteger exponent, BigInteger mod) {
        BigInteger result = BigInteger.ONE;
        base = base.mod(mod);

        if (base.equals(BigInteger.ZERO)) {
            return BigInteger.ZERO;
        }

        while (!exponent.equals(BigInteger.ZERO)) {

            if (exponent.and(BigInteger.ONE).equals(BigInteger.ONE)) {
                result = result.multiply(base).mod(mod);
            }

            exponent = exponent.shiftRight(1);
            base = base.multiply(base).mod(mod);
        }

        return result;
    }

    public static BigInteger randomPrime(int bitLength) {
        BigInteger prime = new BigInteger(bitLength, 0, r);

        if (millerRabin(prime)) {
            return prime;
        } else {
            return randomPrime(bitLength);
        }
    }

    public static BigInteger chineseRemainderTheorem(BigInteger p, BigInteger q, BigInteger d, BigInteger encrypted) {
        Entry<BigInteger, BigInteger> xy = extendedEuclideanAlgorithm(p, q);
        BigInteger M = p.multiply(q);
        BigInteger c1 = quickExponent(encrypted, d.mod(p.subtract(BigInteger.ONE)), p);
        BigInteger c2 = quickExponent(encrypted, d.mod(q.subtract(BigInteger.ONE)), q);

        return c1.multiply(xy.getValue().multiply(q)).add(c2.multiply(xy.getKey()).multiply(p)).mod(M);
    }

    private static boolean millerRabin(BigInteger n) {
        for (int i = 0; i < 20; i++) {

            BigInteger a;
            do {
                a = new BigInteger(n.bitLength(), r);
            } while (a.equals(BigInteger.ZERO));

            if (!millerRabinPass(a, n)) {
                return false;
            }
        }

        return true;
    }

    private static boolean millerRabinPass(BigInteger a, BigInteger n) {
        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        BigInteger d = nMinusOne;
        int s = d.getLowestSetBit();
        d = d.shiftRight(s);
        BigInteger aToPower = quickExponent(a, d, n);

        if (aToPower.equals(BigInteger.ONE)) {
            return true;
        }

        for (int i = 0; i < s - 1; i++) {

            if (aToPower.equals(nMinusOne))  {
                return true;
            }

            aToPower = quickExponent(aToPower, BigInteger.TWO, n);
        }

        return aToPower.equals(nMinusOne);
    }

}
