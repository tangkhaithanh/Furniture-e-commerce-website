package vn.iotstar.utils;

import jakarta.servlet.http.HttpServletRequest;

public class URLUtil {
    public static String getSiteUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return url.replace(request.getServletPath(), "");

    }
}
