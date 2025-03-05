package org.project.common;

public class Validator {

    public static boolean isValidPhoneNumber(int phoneNumber) {
        String phoneNumberStr = String.valueOf(phoneNumber);
        return phoneNumberStr.length() == 9;
    }

    public static boolean isValidZipCode(String zipCode) {
        return zipCode.matches("\\d{4}-\\d{3}");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.split("@").length == 2;
    }
}
