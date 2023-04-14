package com.lak.tunebreaddriver.Util;

public class Validator {
    final static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    final static String USERNAME_PATTERN = "^[a-zA-Z]{3,20}$";
    final static String NAME_PATTERN = "^[a-zA-Z ]{3,50}$";
    final static String PHONE_PATTERN = "^[0-9]{10}$";

    public static boolean validateEmail(String eMail) {
        return eMail.matches(EMAIL_PATTERN);
    }

    public static boolean validateName(String name) {
        return name.matches(USERNAME_PATTERN);
    }

    public static boolean validatePhone(String phone) {
        return phone.matches(PHONE_PATTERN);
    }

    public static boolean validatePersonName(String name) {
        return name.matches(NAME_PATTERN);
    }
}
