package com.company.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncodeUtil {

    private final static char[] encodedTT = {'/', '@', ':', '?', '=', '&'};

    private final static String[] originalTT = {"%2F", "%40", "%3A", "%3F", "%3D", "%26"};


    public static String encodeTTLink(String url) {

        StringBuilder link = new StringBuilder();

        for (char c : url.toCharArray()) {

            int index = get(c);

            if (index != -1) {
                link.append(originalTT[index]);
            } else {
                link.append(c);
            }

        }

        return link.toString();
    }

    /**
     * Get Index encodedTT
     */

    private static int get(char c) {

        for (int i = 0; i < encodedTT.length; i++) {
            char a = encodedTT[i];
            if (a == c) {
                return i;
            }
        }

        return -1;
    }
}

