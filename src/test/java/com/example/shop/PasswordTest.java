package com.example.shop;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import junit.framework.TestCase;
import shop.ShopService;
import shop.models.account.Account;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:shop-test.xml")
public class PasswordTest extends TestCase {
    @Autowired
    ShopService shopService;


    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void Password() {
        try {
            String text = "Hello World";
            String key = "Bar12345Bar12345"; // 128 bit key
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            System.err.println(new String(encrypted));
            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(encrypted));
            System.err.println(decrypted);

            assertEquals(text, decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  

    @Test
    public void EncryptDecrypt() {
        String myPassword = "myPassword123^&*";
        String encrypted = "";
        String decrypted = "";
        try {
            AES256Util util = new AES256Util("myHashKey123456#@#$0098877^^^^");
            encrypted = util.encrypt(myPassword);
            
            decrypted = util.decrypt(encrypted);
            logger.info("encrypted: " + encrypted);
            assertEquals("myPassword123^&*", decrypted);
        } catch(Exception e){
            e.printStackTrace();
        }

    }
}


