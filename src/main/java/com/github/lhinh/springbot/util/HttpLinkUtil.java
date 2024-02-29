package com.github.lhinh.springbot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class HttpLinkUtil {
    private final String HTTP_LINK_REGEX = "^(http:\\/\\/|https:\\/\\/\\b)?(www.\\b)?([a-zA-Z0-9]*\\b)+.([a-zA-Z]{2,3}\\b)(\\/\\S*\\b)?$";

    private final Pattern pattern;
    private Matcher matcher;

    public HttpLinkUtil() {
        this.pattern = Pattern.compile(HTTP_LINK_REGEX);
    }
    public boolean isValidHttpLink(String link) {
        return Pattern.matches(HTTP_LINK_REGEX, link);
    }

    public String getDomain(String link) {
        matcher = pattern.matcher(link);
        return matcher.group(3) + matcher.group(4);
    }
}
