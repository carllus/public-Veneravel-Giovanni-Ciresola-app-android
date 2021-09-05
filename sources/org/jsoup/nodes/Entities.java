package org.jsoup.nodes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class Entities {
    private static final Map<String, Character> base;
    /* access modifiers changed from: private */
    public static final Map<Character, String> baseByVal;
    private static final Map<String, Character> full;
    /* access modifiers changed from: private */
    public static final Map<Character, String> fullByVal;
    private static final Object[][] xhtmlArray;
    /* access modifiers changed from: private */
    public static final Map<Character, String> xhtmlByVal = new HashMap();

    public enum EscapeMode {
        xhtml(Entities.xhtmlByVal),
        base(Entities.baseByVal),
        extended(Entities.fullByVal);
        
        private Map<Character, String> map;

        private EscapeMode(Map<Character, String> map2) {
            this.map = map2;
        }

        public Map<Character, String> getMap() {
            return this.map;
        }
    }

    private Entities() {
    }

    public static boolean isNamedEntity(String str) {
        return full.containsKey(str);
    }

    public static boolean isBaseNamedEntity(String str) {
        return base.containsKey(str);
    }

    public static Character getCharacterByName(String str) {
        return full.get(str);
    }

    static String escape(String str, Document.OutputSettings outputSettings) {
        StringBuilder sb = new StringBuilder(str.length() * 2);
        escape(sb, str, outputSettings, false, false, false);
        return sb.toString();
    }

    static void escape(StringBuilder sb, String str, Document.OutputSettings outputSettings, boolean z, boolean z2, boolean z3) {
        StringBuilder sb2 = sb;
        EscapeMode escapeMode = outputSettings.escapeMode();
        CharsetEncoder encoder = outputSettings.encoder();
        Map<Character, String> map = escapeMode.getMap();
        int length = str.length();
        int i = 0;
        boolean z4 = false;
        boolean z5 = false;
        while (i < length) {
            int codePointAt = str.codePointAt(i);
            if (z2) {
                if (StringUtil.isWhitespace(codePointAt)) {
                    if ((!z3 || z4) && !z5) {
                        sb2.append(' ');
                        z5 = true;
                    }
                    i += Character.charCount(codePointAt);
                } else {
                    z4 = true;
                    z5 = false;
                }
            }
            if (codePointAt < 65536) {
                char c = (char) codePointAt;
                if (c != '\"') {
                    if (c == '&') {
                        sb2.append("&amp;");
                    } else if (c != '<') {
                        if (c != '>') {
                            if (c != 160) {
                                if (encoder.canEncode(c)) {
                                    sb2.append(c);
                                } else if (map.containsKey(Character.valueOf(c))) {
                                    sb2.append('&').append(map.get(Character.valueOf(c))).append(';');
                                } else {
                                    sb2.append("&#x").append(Integer.toHexString(codePointAt)).append(';');
                                }
                            } else if (escapeMode != EscapeMode.xhtml) {
                                sb2.append("&nbsp;");
                            } else {
                                sb2.append(c);
                            }
                        } else if (!z) {
                            sb2.append("&gt;");
                        } else {
                            sb2.append(c);
                        }
                    } else if (!z) {
                        sb2.append("&lt;");
                    } else {
                        sb2.append(c);
                    }
                } else if (z) {
                    sb2.append("&quot;");
                } else {
                    sb2.append(c);
                }
            } else {
                String str2 = new String(Character.toChars(codePointAt));
                if (encoder.canEncode(str2)) {
                    sb2.append(str2);
                } else {
                    sb2.append("&#x").append(Integer.toHexString(codePointAt)).append(';');
                }
            }
            i += Character.charCount(codePointAt);
        }
    }

    static String unescape(String str) {
        return unescape(str, false);
    }

    static String unescape(String str, boolean z) {
        return Parser.unescapeEntities(str, z);
    }

    static {
        Object[][] objArr = {new Object[]{"quot", 34}, new Object[]{"amp", 38}, new Object[]{"lt", 60}, new Object[]{"gt", 62}};
        xhtmlArray = objArr;
        Map<String, Character> loadEntities = loadEntities("entities-base.properties");
        base = loadEntities;
        baseByVal = toCharacterKey(loadEntities);
        Map<String, Character> loadEntities2 = loadEntities("entities-full.properties");
        full = loadEntities2;
        fullByVal = toCharacterKey(loadEntities2);
        for (Object[] objArr2 : objArr) {
            xhtmlByVal.put(Character.valueOf((char) ((Integer) objArr2[1]).intValue()), (String) objArr2[0]);
        }
    }

    private static Map<String, Character> loadEntities(String str) {
        Properties properties = new Properties();
        HashMap hashMap = new HashMap();
        try {
            InputStream resourceAsStream = Entities.class.getResourceAsStream(str);
            properties.load(resourceAsStream);
            resourceAsStream.close();
            for (Map.Entry entry : properties.entrySet()) {
                hashMap.put((String) entry.getKey(), Character.valueOf((char) Integer.parseInt((String) entry.getValue(), 16)));
            }
            return hashMap;
        } catch (IOException e) {
            throw new MissingResourceException("Error loading entities resource: " + e.getMessage(), "Entities", str);
        }
    }

    private static Map<Character, String> toCharacterKey(Map<String, Character> map) {
        HashMap hashMap = new HashMap();
        for (Map.Entry next : map.entrySet()) {
            Character ch = (Character) next.getValue();
            String str = (String) next.getKey();
            if (!hashMap.containsKey(ch)) {
                hashMap.put(ch, str);
            } else if (str.toLowerCase().equals(str)) {
                hashMap.put(ch, str);
            }
        }
        return hashMap;
    }
}
