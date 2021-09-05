package org.jsoup.helper;

import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.TokenQueue;

public class HttpConnection implements Connection {
    private static final int HTTP_TEMP_REDIR = 307;
    private Connection.Request req = new Request();
    private Connection.Response res = new Response();

    public static Connection connect(String str) {
        HttpConnection httpConnection = new HttpConnection();
        httpConnection.url(str);
        return httpConnection;
    }

    public static Connection connect(URL url) {
        HttpConnection httpConnection = new HttpConnection();
        httpConnection.url(url);
        return httpConnection;
    }

    /* access modifiers changed from: private */
    public static String encodeUrl(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll(" ", "%20");
    }

    private HttpConnection() {
    }

    public Connection url(URL url) {
        this.req.url(url);
        return this;
    }

    public Connection url(String str) {
        Validate.notEmpty(str, "Must supply a valid URL");
        try {
            this.req.url(new URL(encodeUrl(str)));
            return this;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + str, e);
        }
    }

    public Connection userAgent(String str) {
        Validate.notNull(str, "User agent must not be null");
        this.req.header("User-Agent", str);
        return this;
    }

    public Connection timeout(int i) {
        this.req.timeout(i);
        return this;
    }

    public Connection maxBodySize(int i) {
        this.req.maxBodySize(i);
        return this;
    }

    public Connection followRedirects(boolean z) {
        this.req.followRedirects(z);
        return this;
    }

    public Connection referrer(String str) {
        Validate.notNull(str, "Referrer must not be null");
        this.req.header("Referer", str);
        return this;
    }

    public Connection method(Connection.Method method) {
        this.req.method(method);
        return this;
    }

    public Connection ignoreHttpErrors(boolean z) {
        this.req.ignoreHttpErrors(z);
        return this;
    }

    public Connection ignoreContentType(boolean z) {
        this.req.ignoreContentType(z);
        return this;
    }

    public Connection data(String str, String str2) {
        this.req.data(KeyVal.create(str, str2));
        return this;
    }

    public Connection data(Map<String, String> map) {
        Validate.notNull(map, "Data map must not be null");
        for (Map.Entry next : map.entrySet()) {
            this.req.data(KeyVal.create((String) next.getKey(), (String) next.getValue()));
        }
        return this;
    }

    public Connection data(String... strArr) {
        Validate.notNull(strArr, "Data key value pairs must not be null");
        Validate.isTrue(strArr.length % 2 == 0, "Must supply an even number of key value pairs");
        for (int i = 0; i < strArr.length; i += 2) {
            String str = strArr[i];
            String str2 = strArr[i + 1];
            Validate.notEmpty(str, "Data key must not be empty");
            Validate.notNull(str2, "Data value must not be null");
            this.req.data(KeyVal.create(str, str2));
        }
        return this;
    }

    public Connection data(Collection<Connection.KeyVal> collection) {
        Validate.notNull(collection, "Data collection must not be null");
        for (Connection.KeyVal data : collection) {
            this.req.data(data);
        }
        return this;
    }

    public Connection header(String str, String str2) {
        this.req.header(str, str2);
        return this;
    }

    public Connection cookie(String str, String str2) {
        this.req.cookie(str, str2);
        return this;
    }

    public Connection cookies(Map<String, String> map) {
        Validate.notNull(map, "Cookie map must not be null");
        for (Map.Entry next : map.entrySet()) {
            this.req.cookie((String) next.getKey(), (String) next.getValue());
        }
        return this;
    }

    public Connection parser(Parser parser) {
        this.req.parser(parser);
        return this;
    }

    public Document get() throws IOException {
        this.req.method(Connection.Method.GET);
        execute();
        return this.res.parse();
    }

    public Document post() throws IOException {
        this.req.method(Connection.Method.POST);
        execute();
        return this.res.parse();
    }

    public Connection.Response execute() throws IOException {
        Response execute = Response.execute(this.req);
        this.res = execute;
        return execute;
    }

    public Connection.Request request() {
        return this.req;
    }

    public Connection request(Connection.Request request) {
        this.req = request;
        return this;
    }

    public Connection.Response response() {
        return this.res;
    }

    public Connection response(Connection.Response response) {
        this.res = response;
        return this;
    }

    private static abstract class Base<T extends Connection.Base> implements Connection.Base<T> {
        Map<String, String> cookies;
        Map<String, String> headers;
        Connection.Method method;
        URL url;

        private Base() {
            this.headers = new LinkedHashMap();
            this.cookies = new LinkedHashMap();
        }

        public URL url() {
            return this.url;
        }

        public T url(URL url2) {
            Validate.notNull(url2, "URL must not be null");
            this.url = url2;
            return this;
        }

        public Connection.Method method() {
            return this.method;
        }

        public T method(Connection.Method method2) {
            Validate.notNull(method2, "Method must not be null");
            this.method = method2;
            return this;
        }

        public String header(String str) {
            Validate.notNull(str, "Header name must not be null");
            return getHeaderCaseInsensitive(str);
        }

        public T header(String str, String str2) {
            Validate.notEmpty(str, "Header name must not be empty");
            Validate.notNull(str2, "Header value must not be null");
            removeHeader(str);
            this.headers.put(str, str2);
            return this;
        }

        public boolean hasHeader(String str) {
            Validate.notEmpty(str, "Header name must not be empty");
            return getHeaderCaseInsensitive(str) != null;
        }

        public T removeHeader(String str) {
            Validate.notEmpty(str, "Header name must not be empty");
            Map.Entry<String, String> scanHeaders = scanHeaders(str);
            if (scanHeaders != null) {
                this.headers.remove(scanHeaders.getKey());
            }
            return this;
        }

        public Map<String, String> headers() {
            return this.headers;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:4:0x001d, code lost:
            r3 = scanHeaders(r3);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.lang.String getHeaderCaseInsensitive(java.lang.String r3) {
            /*
                r2 = this;
                java.lang.String r0 = "Header name must not be null"
                org.jsoup.helper.Validate.notNull(r3, r0)
                java.util.Map<java.lang.String, java.lang.String> r0 = r2.headers
                java.lang.Object r0 = r0.get(r3)
                java.lang.String r0 = (java.lang.String) r0
                if (r0 != 0) goto L_0x001b
                java.util.Map<java.lang.String, java.lang.String> r0 = r2.headers
                java.lang.String r1 = r3.toLowerCase()
                java.lang.Object r0 = r0.get(r1)
                java.lang.String r0 = (java.lang.String) r0
            L_0x001b:
                if (r0 != 0) goto L_0x002a
                java.util.Map$Entry r3 = r2.scanHeaders(r3)
                if (r3 == 0) goto L_0x002a
                java.lang.Object r3 = r3.getValue()
                r0 = r3
                java.lang.String r0 = (java.lang.String) r0
            L_0x002a:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.helper.HttpConnection.Base.getHeaderCaseInsensitive(java.lang.String):java.lang.String");
        }

        private Map.Entry<String, String> scanHeaders(String str) {
            String lowerCase = str.toLowerCase();
            for (Map.Entry<String, String> next : this.headers.entrySet()) {
                if (next.getKey().toLowerCase().equals(lowerCase)) {
                    return next;
                }
            }
            return null;
        }

        public String cookie(String str) {
            Validate.notNull(str, "Cookie name must not be null");
            return this.cookies.get(str);
        }

        public T cookie(String str, String str2) {
            Validate.notEmpty(str, "Cookie name must not be empty");
            Validate.notNull(str2, "Cookie value must not be null");
            this.cookies.put(str, str2);
            return this;
        }

        public boolean hasCookie(String str) {
            Validate.notEmpty("Cookie name must not be empty");
            return this.cookies.containsKey(str);
        }

        public T removeCookie(String str) {
            Validate.notEmpty("Cookie name must not be empty");
            this.cookies.remove(str);
            return this;
        }

        public Map<String, String> cookies() {
            return this.cookies;
        }
    }

    public static class Request extends Base<Connection.Request> implements Connection.Request {
        private Collection<Connection.KeyVal> data;
        private boolean followRedirects;
        private boolean ignoreContentType;
        private boolean ignoreHttpErrors;
        private int maxBodySizeBytes;
        private Parser parser;
        private int timeoutMilliseconds;

        public /* bridge */ /* synthetic */ String cookie(String str) {
            return super.cookie(str);
        }

        public /* bridge */ /* synthetic */ Map cookies() {
            return super.cookies();
        }

        public /* bridge */ /* synthetic */ boolean hasCookie(String str) {
            return super.hasCookie(str);
        }

        public /* bridge */ /* synthetic */ boolean hasHeader(String str) {
            return super.hasHeader(str);
        }

        public /* bridge */ /* synthetic */ String header(String str) {
            return super.header(str);
        }

        public /* bridge */ /* synthetic */ Map headers() {
            return super.headers();
        }

        public /* bridge */ /* synthetic */ Connection.Method method() {
            return super.method();
        }

        public /* bridge */ /* synthetic */ URL url() {
            return super.url();
        }

        private Request() {
            super();
            this.ignoreHttpErrors = false;
            this.ignoreContentType = false;
            this.timeoutMilliseconds = PathInterpolatorCompat.MAX_NUM_POINTS;
            this.maxBodySizeBytes = 1048576;
            this.followRedirects = true;
            this.data = new ArrayList();
            this.method = Connection.Method.GET;
            this.headers.put("Accept-Encoding", "gzip");
            this.parser = Parser.htmlParser();
        }

        public int timeout() {
            return this.timeoutMilliseconds;
        }

        public Request timeout(int i) {
            Validate.isTrue(i >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
            this.timeoutMilliseconds = i;
            return this;
        }

        public int maxBodySize() {
            return this.maxBodySizeBytes;
        }

        public Connection.Request maxBodySize(int i) {
            Validate.isTrue(i >= 0, "maxSize must be 0 (unlimited) or larger");
            this.maxBodySizeBytes = i;
            return this;
        }

        public boolean followRedirects() {
            return this.followRedirects;
        }

        public Connection.Request followRedirects(boolean z) {
            this.followRedirects = z;
            return this;
        }

        public boolean ignoreHttpErrors() {
            return this.ignoreHttpErrors;
        }

        public Connection.Request ignoreHttpErrors(boolean z) {
            this.ignoreHttpErrors = z;
            return this;
        }

        public boolean ignoreContentType() {
            return this.ignoreContentType;
        }

        public Connection.Request ignoreContentType(boolean z) {
            this.ignoreContentType = z;
            return this;
        }

        public Request data(Connection.KeyVal keyVal) {
            Validate.notNull(keyVal, "Key val must not be null");
            this.data.add(keyVal);
            return this;
        }

        public Collection<Connection.KeyVal> data() {
            return this.data;
        }

        public Request parser(Parser parser2) {
            this.parser = parser2;
            return this;
        }

        public Parser parser() {
            return this.parser;
        }
    }

    public static class Response extends Base<Connection.Response> implements Connection.Response {
        private static final int MAX_REDIRECTS = 20;
        private static final Pattern xmlContentTypeRxp = Pattern.compile("application/\\w+\\+xml.*");
        private ByteBuffer byteData;
        private String charset;
        private String contentType;
        private boolean executed = false;
        private int numRedirects = 0;
        private Connection.Request req;
        private int statusCode;
        private String statusMessage;

        public /* bridge */ /* synthetic */ String cookie(String str) {
            return super.cookie(str);
        }

        public /* bridge */ /* synthetic */ Map cookies() {
            return super.cookies();
        }

        public /* bridge */ /* synthetic */ boolean hasCookie(String str) {
            return super.hasCookie(str);
        }

        public /* bridge */ /* synthetic */ boolean hasHeader(String str) {
            return super.hasHeader(str);
        }

        public /* bridge */ /* synthetic */ String header(String str) {
            return super.header(str);
        }

        public /* bridge */ /* synthetic */ Map headers() {
            return super.headers();
        }

        public /* bridge */ /* synthetic */ Connection.Method method() {
            return super.method();
        }

        public /* bridge */ /* synthetic */ URL url() {
            return super.url();
        }

        Response() {
            super();
        }

        private Response(Response response) throws IOException {
            super();
            if (response != null) {
                int i = response.numRedirects + 1;
                this.numRedirects = i;
                if (i >= 20) {
                    throw new IOException(String.format("Too many redirects occurred trying to load URL %s", new Object[]{response.url()}));
                }
            }
        }

        static Response execute(Connection.Request request) throws IOException {
            return execute(request, (Response) null);
        }

        /* JADX WARNING: Removed duplicated region for block: B:89:0x0195 A[SYNTHETIC, Splitter:B:89:0x0195] */
        /* JADX WARNING: Removed duplicated region for block: B:92:0x019a A[Catch:{ all -> 0x019e }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static org.jsoup.helper.HttpConnection.Response execute(org.jsoup.Connection.Request r6, org.jsoup.helper.HttpConnection.Response r7) throws java.io.IOException {
            /*
                java.lang.String r0 = "Content-Encoding"
                java.lang.String r1 = "Request must not be null"
                org.jsoup.helper.Validate.notNull(r6, r1)
                java.net.URL r1 = r6.url()
                java.lang.String r1 = r1.getProtocol()
                java.lang.String r2 = "http"
                boolean r2 = r1.equals(r2)
                if (r2 != 0) goto L_0x0028
                java.lang.String r2 = "https"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0020
                goto L_0x0028
            L_0x0020:
                java.net.MalformedURLException r6 = new java.net.MalformedURLException
                java.lang.String r7 = "Only http & https protocols supported"
                r6.<init>(r7)
                throw r6
            L_0x0028:
                org.jsoup.Connection$Method r1 = r6.method()
                org.jsoup.Connection$Method r2 = org.jsoup.Connection.Method.GET
                if (r1 != r2) goto L_0x003d
                java.util.Collection r1 = r6.data()
                int r1 = r1.size()
                if (r1 <= 0) goto L_0x003d
                serialiseRequestUrl(r6)
            L_0x003d:
                java.net.HttpURLConnection r1 = createConnection(r6)
                r1.connect()     // Catch:{ all -> 0x019e }
                org.jsoup.Connection$Method r2 = r6.method()     // Catch:{ all -> 0x019e }
                org.jsoup.Connection$Method r3 = org.jsoup.Connection.Method.POST     // Catch:{ all -> 0x019e }
                if (r2 != r3) goto L_0x0057
                java.util.Collection r2 = r6.data()     // Catch:{ all -> 0x019e }
                java.io.OutputStream r3 = r1.getOutputStream()     // Catch:{ all -> 0x019e }
                writePost(r2, r3)     // Catch:{ all -> 0x019e }
            L_0x0057:
                int r2 = r1.getResponseCode()     // Catch:{ all -> 0x019e }
                r3 = 0
                r4 = 200(0xc8, float:2.8E-43)
                r5 = 1
                if (r2 == r4) goto L_0x008a
                r4 = 302(0x12e, float:4.23E-43)
                if (r2 == r4) goto L_0x0089
                r4 = 301(0x12d, float:4.22E-43)
                if (r2 == r4) goto L_0x0089
                r4 = 303(0x12f, float:4.25E-43)
                if (r2 == r4) goto L_0x0089
                r4 = 307(0x133, float:4.3E-43)
                if (r2 != r4) goto L_0x0072
                goto L_0x0089
            L_0x0072:
                boolean r4 = r6.ignoreHttpErrors()     // Catch:{ all -> 0x019e }
                if (r4 == 0) goto L_0x0079
                goto L_0x008a
            L_0x0079:
                org.jsoup.HttpStatusException r7 = new org.jsoup.HttpStatusException     // Catch:{ all -> 0x019e }
                java.lang.String r0 = "HTTP error fetching URL"
                java.net.URL r6 = r6.url()     // Catch:{ all -> 0x019e }
                java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x019e }
                r7.<init>(r0, r2, r6)     // Catch:{ all -> 0x019e }
                throw r7     // Catch:{ all -> 0x019e }
            L_0x0089:
                r3 = 1
            L_0x008a:
                org.jsoup.helper.HttpConnection$Response r2 = new org.jsoup.helper.HttpConnection$Response     // Catch:{ all -> 0x019e }
                r2.<init>(r7)     // Catch:{ all -> 0x019e }
                r2.setupFromConnection(r1, r7)     // Catch:{ all -> 0x019e }
                if (r3 == 0) goto L_0x0101
                boolean r7 = r6.followRedirects()     // Catch:{ all -> 0x019e }
                if (r7 == 0) goto L_0x0101
                org.jsoup.Connection$Method r7 = org.jsoup.Connection.Method.GET     // Catch:{ all -> 0x019e }
                r6.method(r7)     // Catch:{ all -> 0x019e }
                java.util.Collection r7 = r6.data()     // Catch:{ all -> 0x019e }
                r7.clear()     // Catch:{ all -> 0x019e }
                java.lang.String r7 = "Location"
                java.lang.String r7 = r2.header(r7)     // Catch:{ all -> 0x019e }
                if (r7 == 0) goto L_0x00c3
                java.lang.String r0 = "http:/"
                boolean r0 = r7.startsWith(r0)     // Catch:{ all -> 0x019e }
                if (r0 == 0) goto L_0x00c3
                r0 = 6
                char r3 = r7.charAt(r0)     // Catch:{ all -> 0x019e }
                r4 = 47
                if (r3 == r4) goto L_0x00c3
                java.lang.String r7 = r7.substring(r0)     // Catch:{ all -> 0x019e }
            L_0x00c3:
                java.net.URL r0 = new java.net.URL     // Catch:{ all -> 0x019e }
                java.net.URL r3 = r6.url()     // Catch:{ all -> 0x019e }
                java.lang.String r7 = org.jsoup.helper.HttpConnection.encodeUrl(r7)     // Catch:{ all -> 0x019e }
                r0.<init>(r3, r7)     // Catch:{ all -> 0x019e }
                r6.url(r0)     // Catch:{ all -> 0x019e }
                java.util.Map r7 = r2.cookies     // Catch:{ all -> 0x019e }
                java.util.Set r7 = r7.entrySet()     // Catch:{ all -> 0x019e }
                java.util.Iterator r7 = r7.iterator()     // Catch:{ all -> 0x019e }
            L_0x00dd:
                boolean r0 = r7.hasNext()     // Catch:{ all -> 0x019e }
                if (r0 == 0) goto L_0x00f9
                java.lang.Object r0 = r7.next()     // Catch:{ all -> 0x019e }
                java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch:{ all -> 0x019e }
                java.lang.Object r3 = r0.getKey()     // Catch:{ all -> 0x019e }
                java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x019e }
                java.lang.Object r0 = r0.getValue()     // Catch:{ all -> 0x019e }
                java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x019e }
                r6.cookie(r3, r0)     // Catch:{ all -> 0x019e }
                goto L_0x00dd
            L_0x00f9:
                org.jsoup.helper.HttpConnection$Response r6 = execute(r6, r2)     // Catch:{ all -> 0x019e }
                r1.disconnect()
                return r6
            L_0x0101:
                r2.req = r6     // Catch:{ all -> 0x019e }
                java.lang.String r7 = r2.contentType()     // Catch:{ all -> 0x019e }
                if (r7 == 0) goto L_0x013c
                boolean r3 = r6.ignoreContentType()     // Catch:{ all -> 0x019e }
                if (r3 != 0) goto L_0x013c
                java.lang.String r3 = "text/"
                boolean r3 = r7.startsWith(r3)     // Catch:{ all -> 0x019e }
                if (r3 != 0) goto L_0x013c
                java.lang.String r3 = "application/xml"
                boolean r3 = r7.startsWith(r3)     // Catch:{ all -> 0x019e }
                if (r3 != 0) goto L_0x013c
                java.util.regex.Pattern r3 = xmlContentTypeRxp     // Catch:{ all -> 0x019e }
                java.util.regex.Matcher r3 = r3.matcher(r7)     // Catch:{ all -> 0x019e }
                boolean r3 = r3.matches()     // Catch:{ all -> 0x019e }
                if (r3 == 0) goto L_0x012c
                goto L_0x013c
            L_0x012c:
                org.jsoup.UnsupportedMimeTypeException r0 = new org.jsoup.UnsupportedMimeTypeException     // Catch:{ all -> 0x019e }
                java.lang.String r2 = "Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml"
                java.net.URL r6 = r6.url()     // Catch:{ all -> 0x019e }
                java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x019e }
                r0.<init>(r2, r7, r6)     // Catch:{ all -> 0x019e }
                throw r0     // Catch:{ all -> 0x019e }
            L_0x013c:
                r7 = 0
                java.io.InputStream r3 = r1.getErrorStream()     // Catch:{ all -> 0x0191 }
                if (r3 == 0) goto L_0x0148
                java.io.InputStream r3 = r1.getErrorStream()     // Catch:{ all -> 0x0191 }
                goto L_0x014c
            L_0x0148:
                java.io.InputStream r3 = r1.getInputStream()     // Catch:{ all -> 0x0191 }
            L_0x014c:
                boolean r4 = r2.hasHeader(r0)     // Catch:{ all -> 0x018f }
                if (r4 == 0) goto L_0x0169
                java.lang.String r0 = r2.header(r0)     // Catch:{ all -> 0x018f }
                java.lang.String r4 = "gzip"
                boolean r0 = r0.equalsIgnoreCase(r4)     // Catch:{ all -> 0x018f }
                if (r0 == 0) goto L_0x0169
                java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ all -> 0x018f }
                java.util.zip.GZIPInputStream r4 = new java.util.zip.GZIPInputStream     // Catch:{ all -> 0x018f }
                r4.<init>(r3)     // Catch:{ all -> 0x018f }
                r0.<init>(r4)     // Catch:{ all -> 0x018f }
                goto L_0x016e
            L_0x0169:
                java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ all -> 0x018f }
                r0.<init>(r3)     // Catch:{ all -> 0x018f }
            L_0x016e:
                r7 = r0
                int r6 = r6.maxBodySize()     // Catch:{ all -> 0x018f }
                java.nio.ByteBuffer r6 = org.jsoup.helper.DataUtil.readToByteBuffer(r7, r6)     // Catch:{ all -> 0x018f }
                r2.byteData = r6     // Catch:{ all -> 0x018f }
                java.lang.String r6 = r2.contentType     // Catch:{ all -> 0x018f }
                java.lang.String r6 = org.jsoup.helper.DataUtil.getCharsetFromContentType(r6)     // Catch:{ all -> 0x018f }
                r2.charset = r6     // Catch:{ all -> 0x018f }
                r7.close()     // Catch:{ all -> 0x019e }
                if (r3 == 0) goto L_0x0189
                r3.close()     // Catch:{ all -> 0x019e }
            L_0x0189:
                r1.disconnect()
                r2.executed = r5
                return r2
            L_0x018f:
                r6 = move-exception
                goto L_0x0193
            L_0x0191:
                r6 = move-exception
                r3 = r7
            L_0x0193:
                if (r7 == 0) goto L_0x0198
                r7.close()     // Catch:{ all -> 0x019e }
            L_0x0198:
                if (r3 == 0) goto L_0x019d
                r3.close()     // Catch:{ all -> 0x019e }
            L_0x019d:
                throw r6     // Catch:{ all -> 0x019e }
            L_0x019e:
                r6 = move-exception
                r1.disconnect()
                goto L_0x01a4
            L_0x01a3:
                throw r6
            L_0x01a4:
                goto L_0x01a3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.helper.HttpConnection.Response.execute(org.jsoup.Connection$Request, org.jsoup.helper.HttpConnection$Response):org.jsoup.helper.HttpConnection$Response");
        }

        public int statusCode() {
            return this.statusCode;
        }

        public String statusMessage() {
            return this.statusMessage;
        }

        public String charset() {
            return this.charset;
        }

        public String contentType() {
            return this.contentType;
        }

        public Document parse() throws IOException {
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before parsing response");
            Document parseByteData = DataUtil.parseByteData(this.byteData, this.charset, this.url.toExternalForm(), this.req.parser());
            this.byteData.rewind();
            this.charset = parseByteData.outputSettings().charset().name();
            return parseByteData;
        }

        public String body() {
            String str;
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
            String str2 = this.charset;
            if (str2 == null) {
                str = Charset.forName("UTF-8").decode(this.byteData).toString();
            } else {
                str = Charset.forName(str2).decode(this.byteData).toString();
            }
            this.byteData.rewind();
            return str;
        }

        public byte[] bodyAsBytes() {
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
            return this.byteData.array();
        }

        private static HttpURLConnection createConnection(Connection.Request request) throws IOException {
            HttpURLConnection httpURLConnection = (HttpURLConnection) request.url().openConnection();
            httpURLConnection.setRequestMethod(request.method().name());
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setConnectTimeout(request.timeout());
            httpURLConnection.setReadTimeout(request.timeout());
            if (request.method() == Connection.Method.POST) {
                httpURLConnection.setDoOutput(true);
            }
            if (request.cookies().size() > 0) {
                httpURLConnection.addRequestProperty("Cookie", getRequestCookieString(request));
            }
            for (Map.Entry next : request.headers().entrySet()) {
                httpURLConnection.addRequestProperty((String) next.getKey(), (String) next.getValue());
            }
            return httpURLConnection;
        }

        private void setupFromConnection(HttpURLConnection httpURLConnection, Connection.Response response) throws IOException {
            this.method = Connection.Method.valueOf(httpURLConnection.getRequestMethod());
            this.url = httpURLConnection.getURL();
            this.statusCode = httpURLConnection.getResponseCode();
            this.statusMessage = httpURLConnection.getResponseMessage();
            this.contentType = httpURLConnection.getContentType();
            processResponseHeaders(httpURLConnection.getHeaderFields());
            if (response != null) {
                for (Map.Entry next : response.cookies().entrySet()) {
                    if (!hasCookie((String) next.getKey())) {
                        cookie((String) next.getKey(), (String) next.getValue());
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void processResponseHeaders(Map<String, List<String>> map) {
            for (Map.Entry next : map.entrySet()) {
                String str = (String) next.getKey();
                if (str != null) {
                    List<String> list = (List) next.getValue();
                    if (str.equalsIgnoreCase("Set-Cookie")) {
                        for (String str2 : list) {
                            if (str2 != null) {
                                TokenQueue tokenQueue = new TokenQueue(str2);
                                String trim = tokenQueue.chompTo("=").trim();
                                String trim2 = tokenQueue.consumeTo(";").trim();
                                if (trim2 == null) {
                                    trim2 = "";
                                }
                                if (trim != null && trim.length() > 0) {
                                    cookie(trim, trim2);
                                }
                            }
                        }
                    } else if (!list.isEmpty()) {
                        header(str, (String) list.get(0));
                    }
                }
            }
        }

        private static void writePost(Collection<Connection.KeyVal> collection, OutputStream outputStream) throws IOException {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            boolean z = true;
            for (Connection.KeyVal next : collection) {
                if (!z) {
                    outputStreamWriter.append('&');
                } else {
                    z = false;
                }
                outputStreamWriter.write(URLEncoder.encode(next.key(), "UTF-8"));
                outputStreamWriter.write(61);
                outputStreamWriter.write(URLEncoder.encode(next.value(), "UTF-8"));
            }
            outputStreamWriter.close();
        }

        private static String getRequestCookieString(Connection.Request request) {
            StringBuilder sb = new StringBuilder();
            boolean z = true;
            for (Map.Entry next : request.cookies().entrySet()) {
                if (!z) {
                    sb.append("; ");
                } else {
                    z = false;
                }
                sb.append((String) next.getKey()).append('=').append((String) next.getValue());
            }
            return sb.toString();
        }

        private static void serialiseRequestUrl(Connection.Request request) throws IOException {
            boolean z;
            URL url = request.url();
            StringBuilder sb = new StringBuilder();
            sb.append(url.getProtocol()).append("://").append(url.getAuthority()).append(url.getPath()).append("?");
            if (url.getQuery() != null) {
                sb.append(url.getQuery());
                z = false;
            } else {
                z = true;
            }
            for (Connection.KeyVal next : request.data()) {
                if (!z) {
                    sb.append('&');
                } else {
                    z = false;
                }
                sb.append(URLEncoder.encode(next.key(), "UTF-8")).append('=').append(URLEncoder.encode(next.value(), "UTF-8"));
            }
            request.url(new URL(sb.toString()));
            request.data().clear();
        }
    }

    public static class KeyVal implements Connection.KeyVal {
        private String key;
        private String value;

        public static KeyVal create(String str, String str2) {
            Validate.notEmpty(str, "Data key must not be empty");
            Validate.notNull(str2, "Data value must not be null");
            return new KeyVal(str, str2);
        }

        private KeyVal(String str, String str2) {
            this.key = str;
            this.value = str2;
        }

        public KeyVal key(String str) {
            Validate.notEmpty(str, "Data key must not be empty");
            this.key = str;
            return this;
        }

        public String key() {
            return this.key;
        }

        public KeyVal value(String str) {
            Validate.notNull(str, "Data value must not be null");
            this.value = str;
            return this;
        }

        public String value() {
            return this.value;
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
