package unq.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mrivero on 25/3/17.
 */
public class HMACEncrypter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HMACEncrypter.class);

    private static String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    public static String encrypt(String dataToEncrypt, String key){
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), HMAC_SHA256_ALGORITHM);
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(secret);
            byte[] resultBytes = mac.doFinal(dataToEncrypt.getBytes());
            return Base64.encodeBase64String(resultBytes);
        } catch (InvalidKeyException e) {
            LOGGER.error("Invalid key " + secret);
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("No algorithm found " + HMAC_SHA256_ALGORITHM);
            throw new RuntimeException(e);
        }
    }


    public static boolean equals(String encryptedReference, String data, String key){
        return encryptedReference.equals(encrypt(data,key));
    }
}
