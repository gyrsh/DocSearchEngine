package com.searchengine.documentservice.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class NormaliserUtils {

    public static String getCleanURL(String url) {

        if (url == null || url.isEmpty()) {
            return "";
        }
        try {
            // Divide the URL
            String trimmedString = url.trim();

            if (!trimmedString.contains("//")) {
                trimmedString = "http://" + trimmedString;
            }

            URI uri = new URI(trimmedString);

            // Lower case the schema and host
            String scheme = uri.getScheme() != null ? uri.getScheme().toLowerCase() : "http";
            String host = uri.getHost() != null ? uri.getHost().toLowerCase() : "";

            // Remove "www." prefix to match www.abc.com and abc.com as the same site
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }

            // 2. Clean Path (remove trailing slash)
            String path = uri.getPath();
            if (path == null) {
                path = "/";
            } else if (path.length() > 1 && path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            // 3. Port Handling (ignore default ports 80 and 443)
            int port = uri.getPort();
            String portString = "";
            if (port != -1 && port != 80 && port != 443) {
                portString = ":" + port;
            }

            // Note: We deliberately ignore query parameters (uri.getQuery())
            // and fragment links (uri.getFragment()) to normalize them.

            return scheme + "://" + host + portString + path;

        } catch (URISyntaxException e) {
            // If the URL is malformed, return the original trimmed URL as a fallback
            return url.trim();
        }

    }

}
