package org.jsoup.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class DataUtil {
    private static final int bufferSize = 131072;
    private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");
    static final String defaultCharset = "UTF-8";

    private DataUtil() {
    }

    public static Document load(File file, String str, String str2) throws IOException {
        return parseByteData(readFileToByteBuffer(file), str, str2, Parser.htmlParser());
    }

    public static Document load(InputStream inputStream, String str, String str2) throws IOException {
        return parseByteData(readToByteBuffer(inputStream), str, str2, Parser.htmlParser());
    }

    public static Document load(InputStream inputStream, String str, String str2, Parser parser) throws IOException {
        return parseByteData(readToByteBuffer(inputStream), str, str2, parser);
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static org.jsoup.nodes.Document parseByteData(java.nio.ByteBuffer r8, java.lang.String r9, java.lang.String r10, org.jsoup.parser.Parser r11) {
        /*
            java.lang.String r0 = "UTF-8"
            r1 = 0
            if (r9 != 0) goto L_0x007d
            java.nio.charset.Charset r2 = java.nio.charset.Charset.forName(r0)
            java.nio.CharBuffer r2 = r2.decode(r8)
            java.lang.String r2 = r2.toString()
            org.jsoup.nodes.Document r3 = r11.parseInput(r2, r10)
            java.lang.String r4 = "meta[http-equiv=content-type], meta[charset]"
            org.jsoup.select.Elements r4 = r3.select(r4)
            org.jsoup.nodes.Element r4 = r4.first()
            if (r4 == 0) goto L_0x008f
            java.lang.String r5 = "http-equiv"
            boolean r5 = r4.hasAttr(r5)
            java.lang.String r6 = "charset"
            if (r5 == 0) goto L_0x004f
            java.lang.String r5 = "content"
            java.lang.String r5 = r4.attr(r5)
            java.lang.String r5 = getCharsetFromContentType(r5)
            if (r5 != 0) goto L_0x0053
            boolean r7 = r4.hasAttr(r6)
            if (r7 == 0) goto L_0x0053
            java.lang.String r7 = r4.attr(r6)     // Catch:{ IllegalCharsetNameException -> 0x004d }
            boolean r7 = java.nio.charset.Charset.isSupported(r7)     // Catch:{ IllegalCharsetNameException -> 0x004d }
            if (r7 == 0) goto L_0x0053
            java.lang.String r4 = r4.attr(r6)     // Catch:{ IllegalCharsetNameException -> 0x004d }
            r5 = r4
            goto L_0x0053
        L_0x004d:
            r5 = r1
            goto L_0x0053
        L_0x004f:
            java.lang.String r5 = r4.attr(r6)
        L_0x0053:
            if (r5 == 0) goto L_0x008f
            int r4 = r5.length()
            if (r4 == 0) goto L_0x008f
            boolean r4 = r5.equals(r0)
            if (r4 != 0) goto L_0x008f
            java.lang.String r9 = r5.trim()
            java.lang.String r2 = "[\"']"
            java.lang.String r3 = ""
            java.lang.String r9 = r9.replaceAll(r2, r3)
            r8.rewind()
            java.nio.charset.Charset r2 = java.nio.charset.Charset.forName(r9)
            java.nio.CharBuffer r2 = r2.decode(r8)
            java.lang.String r2 = r2.toString()
            goto L_0x008e
        L_0x007d:
            java.lang.String r2 = "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML"
            org.jsoup.helper.Validate.notEmpty(r9, r2)
            java.nio.charset.Charset r2 = java.nio.charset.Charset.forName(r9)
            java.nio.CharBuffer r2 = r2.decode(r8)
            java.lang.String r2 = r2.toString()
        L_0x008e:
            r3 = r1
        L_0x008f:
            int r4 = r2.length()
            if (r4 <= 0) goto L_0x00b4
            r4 = 0
            char r4 = r2.charAt(r4)
            r5 = 65279(0xfeff, float:9.1475E-41)
            if (r4 != r5) goto L_0x00b4
            r8.rewind()
            java.nio.charset.Charset r9 = java.nio.charset.Charset.forName(r0)
            java.nio.CharBuffer r8 = r9.decode(r8)
            java.lang.String r8 = r8.toString()
            r9 = 1
            java.lang.String r2 = r8.substring(r9)
            goto L_0x00b6
        L_0x00b4:
            r0 = r9
            r1 = r3
        L_0x00b6:
            if (r1 != 0) goto L_0x00c3
            org.jsoup.nodes.Document r1 = r11.parseInput(r2, r10)
            org.jsoup.nodes.Document$OutputSettings r8 = r1.outputSettings()
            r8.charset((java.lang.String) r0)
        L_0x00c3:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.helper.DataUtil.parseByteData(java.nio.ByteBuffer, java.lang.String, java.lang.String, org.jsoup.parser.Parser):org.jsoup.nodes.Document");
    }

    static ByteBuffer readToByteBuffer(InputStream inputStream, int i) throws IOException {
        boolean z = true;
        Validate.isTrue(i >= 0, "maxSize must be 0 (unlimited) or larger");
        if (i <= 0) {
            z = false;
        }
        byte[] bArr = new byte[131072];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(131072);
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                break;
            }
            if (z) {
                if (read > i) {
                    byteArrayOutputStream.write(bArr, 0, i);
                    break;
                }
                i -= read;
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
        return ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
    }

    static ByteBuffer readToByteBuffer(InputStream inputStream) throws IOException {
        return readToByteBuffer(inputStream, 0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0020  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.nio.ByteBuffer readFileToByteBuffer(java.io.File r4) throws java.io.IOException {
        /*
            r0 = 0
            java.io.RandomAccessFile r1 = new java.io.RandomAccessFile     // Catch:{ all -> 0x001d }
            java.lang.String r2 = "r"
            r1.<init>(r4, r2)     // Catch:{ all -> 0x001d }
            long r2 = r1.length()     // Catch:{ all -> 0x001a }
            int r4 = (int) r2     // Catch:{ all -> 0x001a }
            byte[] r4 = new byte[r4]     // Catch:{ all -> 0x001a }
            r1.readFully(r4)     // Catch:{ all -> 0x001a }
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.wrap(r4)     // Catch:{ all -> 0x001a }
            r1.close()
            return r4
        L_0x001a:
            r4 = move-exception
            r0 = r1
            goto L_0x001e
        L_0x001d:
            r4 = move-exception
        L_0x001e:
            if (r0 == 0) goto L_0x0023
            r0.close()
        L_0x0023:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.helper.DataUtil.readFileToByteBuffer(java.io.File):java.nio.ByteBuffer");
    }

    static String getCharsetFromContentType(String str) {
        if (str == null) {
            return null;
        }
        Matcher matcher = charsetPattern.matcher(str);
        if (matcher.find()) {
            String replace = matcher.group(1).trim().replace("charset=", "");
            if (replace.length() == 0) {
                return null;
            }
            try {
                if (Charset.isSupported(replace)) {
                    return replace;
                }
                String upperCase = replace.toUpperCase(Locale.ENGLISH);
                if (Charset.isSupported(upperCase)) {
                    return upperCase;
                }
            } catch (IllegalCharsetNameException unused) {
            }
        }
        return null;
    }
}
