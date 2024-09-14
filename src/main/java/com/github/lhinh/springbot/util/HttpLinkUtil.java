package com.github.lhinh.springbot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class HttpLinkUtil {
    private static final String HTTP_LINK_REGEX = "^(http:\\/\\/|https:\\/\\/\\b)?(www.|music.\\b)?([a-zA-Z0-9]*\\b)+.([a-zA-Z]{2,3}\\b)(\\/\\S*\\b)?$";
    private static final String IMAGE_EXT_REGEX = ".*\\.(jpg|jpeg|gif|png|bmp|tiff|webp)$";

    private final Pattern pattern;
    private Matcher matcher;

    public HttpLinkUtil() {
        this.pattern = Pattern.compile(HTTP_LINK_REGEX);
    }

    public static boolean isValidHttpLink(String link) {
        return Pattern.matches(HTTP_LINK_REGEX, link);
    }

    public static boolean isImage(String link) {
        return Pattern.matches(IMAGE_EXT_REGEX, link);
    }

    public String getDomain(String link) {
        matcher = pattern.matcher(link);
        return matcher.group(3) + matcher.group(4);
    }
}
