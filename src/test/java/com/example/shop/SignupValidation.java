package com.example.shop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
public class SignupValidation extends TestCase {
    @Test
    public void userIDValidate() {
        String regex = "^[a-zA-Z]{1}[a-zA-Z0-9_]{4,11}$";
        String userID = "fineID1234";
        Pattern VALID_USERID_ADDRESS_REGEX = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        
        Matcher matcher = VALID_USERID_ADDRESS_REGEX.matcher(userID);
        assertTrue(matcher.find());

    }
}