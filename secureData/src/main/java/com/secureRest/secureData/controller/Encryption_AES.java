package com.secureRest.secureData.controller;

import org.springframework.http.ResponseEntity;

import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

class Encryption_AES {
    /* Private variable declaration */
    protected static final String SECRET_KEY = "jab_tak_hain_jaan_janey_jahan";
    protected static final String SALTVALUE = "mein_nachungi";

    /* Encryption Method */
    public static String encrypt(String strToEncrypt)
    {
        try
        {
            /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALTVALUE.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            /* Retruns encrypted value. */
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));

        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e)
        {
            System.out.println("Error occured during encryption: " + e.toString());
        }
        return null;
    }
    public static ResponseEntity<Map<String, String>> Data_splitting(String originalval)

    {
        String encryptedval = encrypt(originalval);
        int min = (encryptedval.length() / 2) - 10;
        int max = (encryptedval.length() / 2) + 10;

        Random rand = new Random();
        int splitIndex = rand.nextInt((max - min) + 1) + min;
        System.out.println("Random number between " + min + " and " + max + ": " + splitIndex);

        String firstHalf = encryptedval.substring(0, splitIndex);
        String secondHalf = encryptedval.substring(splitIndex);

        System.out.println("First half: " + firstHalf);
        System.out.println("Second half: " + secondHalf);

        String firsthalfencryptedval = encrypt(firstHalf);
        System.out.println("First Half Encrypted value: " + firsthalfencryptedval);

        String secondhalfencryptedval = encrypt(secondHalf);
        System.out.println("Second Half Encrypted value: " + secondhalfencryptedval);
        Map<String, String> response = new HashMap<>();
        response.put("output1", firsthalfencryptedval);
        response.put("output2", secondhalfencryptedval);
//        return (firsthalfencryptedval + secondhalfencryptedval);
        return ResponseEntity.ok(response);
    }

}
class Decryption extends Encryption_AES{
    public static String decrypt(String strToDecrypt)
    {
        try
        {
            /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALTVALUE.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            /* Returns decrypted value. */
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e)
        {
            System.out.println("Error occured during decryption: " + e.toString());
        }
        return null;
    }

    public static String data_merger(String firsthalfencryptedval,String secondhalfencryptedval){
        String firsthalfdecryptedval = decrypt(firsthalfencryptedval);
        System.out.println("First Half Decrypted value: " + firsthalfdecryptedval);

        String secondhalfdecryptedval = decrypt(secondhalfencryptedval);
        System.out.println("Second Half Decrypted value: " + secondhalfdecryptedval);

        String fsHalf = firsthalfdecryptedval + secondhalfdecryptedval;
        System.out.println(fsHalf);

        // Call the decrypt() method and store result of decryption.
        String decryptedval = decrypt(fsHalf);
        System.out.println("Decrypted value: " + decryptedval);
        return decryptedval;
    }
}

class Main1 extends Decryption{
    public static void main(String[] args)
    {
        System.out.println("Enter 1 to store Data");
        System.out.println("Enter 2 to get your Data");
        Scanner sc = new Scanner(System.in);
        int n=sc.nextInt();
        switch(n) {
            case 1:
            {
                System.out.println("Enter the text");
                Scanner sc1 = new Scanner(System.in);
                String originalval = sc1.nextLine();
                System.out.println(originalval);
                Decryption obj = new Decryption();
                System.out.println(obj.Data_splitting(originalval));
                break;
            }
            case 2:{
                System.out.println("Database required to fetch");
                break;
            }
            default:{
                System.out.println("INVALID Entry");
            }


        }
    }
}