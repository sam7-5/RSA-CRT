import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        List<Double> decryptNormalMicrosec = new ArrayList<>();
        List<Double> decryptCrtMicrosec = new ArrayList<>();
        int[] powers = {512,1024,2048,3000,3500,4096};


        for ( int i : powers ) {
            //powers.add(i);

            BigInteger p = Utils.randomPrime(i);
            i++;
            BigInteger q = Utils.randomPrime(i);

            BigInteger N = p.multiply(q);
            BigInteger phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

            BigInteger e = minRelativePrime(phiN);
            BigInteger d = privateKey(phiN, e);

            String message = "Hello World!";
          //  System.out.println("Message: " + message);

          //  long start = System.nanoTime();
            BigInteger encrypted = encrypt(message, e, N);
           // System.out.println("Encrypted: " + encrypted);
          //  long finish = System.nanoTime();
           // long timeElapsed = finish - start;
            //double elapsedTimeInMicroSecond = (double) timeElapsed / 1_000_000;
           // System.out.println(elapsedTimeInMicroSecond + " microseconds");

            long start1 = System.nanoTime();
            String decrypted = decrypt(encrypted, d, N);
           // System.out.println("Decrypted: " + decrypted);
            long finish1 = System.nanoTime();
            long timeElapsed1 = finish1 - start1;
            double elapsedTimeInMicroSecond1 = (double) timeElapsed1 / 1_000_000;
           // System.out.println(elapsedTimeInMicroSecond1 + " microseconds");

            decryptNormalMicrosec.add(elapsedTimeInMicroSecond1);

            long start2 = System.nanoTime();
            String decryptedCRT = decrypt(p, q, d, encrypted);
           // System.out.println("Decrypted with CRT: " + decryptedCRT);
            long finish2 = System.nanoTime();
            long timeElapsed2 = finish2 - start2;
            double elapsedTimeInMicroSecond2 = (double) timeElapsed2 / 1_000_000;
           // System.out.println(elapsedTimeInMicroSecond2 + " microseconds");
            decryptCrtMicrosec.add(elapsedTimeInMicroSecond2);
            System.out.println(i);
        }
        System.out.println(Arrays.toString(powers));
        System.out.println(decryptNormalMicrosec);
        System.out.println(decryptCrtMicrosec);

    }

    public static BigInteger encrypt(String message, BigInteger e, BigInteger N) {
        BigInteger msg = new BigInteger(message.getBytes(StandardCharsets.US_ASCII));

        return Utils.quickExponent(msg, e, N);
    }

    public static String decrypt(BigInteger encrypted, BigInteger d, BigInteger N) {
        return new String(Utils.quickExponent(encrypted, d, N).toByteArray());
    }

    public static String decrypt(BigInteger p, BigInteger q, BigInteger d, BigInteger encrypted) {
        return new String(Utils.chineseRemainderTheorem(p, q, d, encrypted).toByteArray());
    }

    public static BigInteger minRelativePrime(BigInteger phiN) {
        for(int i = 3; ; i+=2) {
            if(Utils.euclideanAlgorithm(BigInteger.valueOf(i), phiN).equals(BigInteger.ONE)) {
                return BigInteger.valueOf(i);
            }
        }
    }

    public static BigInteger privateKey(BigInteger phiN, BigInteger e) {
        Entry<BigInteger, BigInteger> xy = Utils.extendedEuclideanAlgorithm(phiN, e);

        return xy.getValue().signum() == -1 ? xy.getValue().add(phiN) : xy.getValue();
    }

}

