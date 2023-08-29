package ninja.egg82.analytics.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentLinkedDeque;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GAHMAC {
    private ConcurrentLinkedDeque<Mac> pool = new ConcurrentLinkedDeque<>(); // Mac is not stateless and thus requires a pool in multi-threaded environments

    private final byte[] key;

    public GAHMAC(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null.");
        }
        if (key.length == 0) {
            throw new IllegalArgumentException("key cannot be empty.");
        }

        this.key = key;
    }

    public byte[] hmac256(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null.");
        }

        Mac hmac = getMac();
        byte[] retVal = hmac.doFinal(data);
        pool.add(hmac);
        return retVal;
    }

    private Mac getMac() throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = pool.pollFirst();
        if (mac == null) {
            mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(key, "HmacSHA256");
            mac.init(secret);
        }
        return mac;
    }
}
