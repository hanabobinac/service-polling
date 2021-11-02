package se.company.services.utils;

import java.net.URL;

public class UrlValidator {

    private static String[] schemes = {"http", "https"};

    public static boolean isValid(String url) {
        var urlValidator = new org.apache.commons.validator.routines.UrlValidator(schemes);
        if (urlValidator.isValid("http://" + url)) {
            System.out.println("url is valid");
            return true;
        } else {
            System.out.println("url is invalid");
            return false;
        }
    }


    public static boolean isValid1(String url) {
        try {
            var url1 = new URL("http://" + url).toURI();
            //var url1 = new URL(url).toURI();
            Logger.debug("URL: " + url1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}