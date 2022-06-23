package popdig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SecretKeyUtil {
 /**
  * Generate secret key for given algorithm
  * 
  * @param algorithm
  *            : Generate secret key specific to this algorithm
  * @return SecretKey
  */
 public static SecretKey getSecretKey(String algorithm) {
  KeyGenerator keyGenerator = null;
  try {
   keyGenerator = KeyGenerator.getInstance(algorithm);
  } catch (NoSuchAlgorithmException e) {
   e.printStackTrace();
  }
  return keyGenerator.generateKey();
 }

 /**
  * Convert secret key to string.
  * 
  * @param secretKey
  * 
  * @return String representation of secret key
  */
 public static String keyToString(SecretKey secretKey) {
  /* Get key in encoding format */
  byte encoded[] = secretKey.getEncoded();

  /*
   * Encodes the specified byte array into a String using Base64 encoding
   * scheme
   */
  String encodedKey = Base64.getEncoder().encodeToString(encoded);

  return encodedKey;
 }

 /**
  * Save secret key to a file
  * 
  * @param secretKey
  *            : Secret key to save into file
  * @param fileName
  *            : File name to store
  */
 public static void saveSecretKey(SecretKey secretKey, String fileName) {
  byte[] keyBytes = secretKey.getEncoded();
  File keyFile = new File(fileName);
  FileOutputStream fOutStream = null;
  try {
   fOutStream = new FileOutputStream(keyFile);
   fOutStream.write(keyBytes);
  } catch (Exception e) {
   e.printStackTrace();
  } finally {
   if (fOutStream != null) {
    try {
     fOutStream.close();
    } catch (IOException e) {
     e.printStackTrace();
    }
   }
  }
 }
}
