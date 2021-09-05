package org.jsoup.parser;

import java.util.Locale;
import org.jsoup.helper.Validate;

class CharacterReader {
    static final char EOF = '￿';
    private final char[] input;
    private final int length;
    private int mark = 0;
    private int pos = 0;

    CharacterReader(String str) {
        Validate.notNull(str);
        char[] charArray = str.toCharArray();
        this.input = charArray;
        this.length = charArray.length;
    }

    /* access modifiers changed from: package-private */
    public int pos() {
        return this.pos;
    }

    /* access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this.pos >= this.length;
    }

    /* access modifiers changed from: package-private */
    public char current() {
        int i = this.pos;
        return i >= this.length ? EOF : this.input[i];
    }

    /* access modifiers changed from: package-private */
    public char consume() {
        int i = this.pos;
        char c = i >= this.length ? EOF : this.input[i];
        this.pos = i + 1;
        return c;
    }

    /* access modifiers changed from: package-private */
    public void unconsume() {
        this.pos--;
    }

    /* access modifiers changed from: package-private */
    public void advance() {
        this.pos++;
    }

    /* access modifiers changed from: package-private */
    public void mark() {
        this.mark = this.pos;
    }

    /* access modifiers changed from: package-private */
    public void rewindToMark() {
        this.pos = this.mark;
    }

    /* access modifiers changed from: package-private */
    public String consumeAsString() {
        char[] cArr = this.input;
        int i = this.pos;
        this.pos = i + 1;
        return new String(cArr, i, 1);
    }

    /* access modifiers changed from: package-private */
    public int nextIndexOf(char c) {
        for (int i = this.pos; i < this.length; i++) {
            if (c == this.input[i]) {
                return i - this.pos;
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public int nextIndexOf(CharSequence charSequence) {
        char charAt = charSequence.charAt(0);
        int i = this.pos;
        while (i < this.length) {
            int i2 = 1;
            if (charAt != this.input[i]) {
                do {
                    i++;
                    if (i >= this.length) {
                        break;
                    }
                } while (charAt == this.input[i]);
            }
            int i3 = i + 1;
            int length2 = (charSequence.length() + i3) - 1;
            int i4 = this.length;
            if (i < i4 && length2 <= i4) {
                int i5 = i3;
                while (i5 < length2 && charSequence.charAt(i2) == this.input[i5]) {
                    i5++;
                    i2++;
                }
                if (i5 == length2) {
                    return i - this.pos;
                }
            }
            i = i3;
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public String consumeTo(char c) {
        int nextIndexOf = nextIndexOf(c);
        if (nextIndexOf == -1) {
            return consumeToEnd();
        }
        String str = new String(this.input, this.pos, nextIndexOf);
        this.pos += nextIndexOf;
        return str;
    }

    /* access modifiers changed from: package-private */
    public String consumeTo(String str) {
        int nextIndexOf = nextIndexOf((CharSequence) str);
        if (nextIndexOf == -1) {
            return consumeToEnd();
        }
        String str2 = new String(this.input, this.pos, nextIndexOf);
        this.pos += nextIndexOf;
        return str2;
    }

    /* access modifiers changed from: package-private */
    public String consumeToAny(char... cArr) {
        int i = this.pos;
        loop0:
        while (this.pos < this.length) {
            for (char c : cArr) {
                if (this.input[this.pos] == c) {
                    break loop0;
                }
            }
            this.pos++;
        }
        return this.pos > i ? new String(this.input, i, this.pos - i) : "";
    }

    /* access modifiers changed from: package-private */
    public String consumeToEnd() {
        char[] cArr = this.input;
        int i = this.pos;
        String str = new String(cArr, i, this.length - i);
        this.pos = this.length;
        return str;
    }

    /* access modifiers changed from: package-private */
    public String consumeLetterSequence() {
        char c;
        int i = this.pos;
        while (true) {
            int i2 = this.pos;
            if (i2 < this.length && (((c = this.input[i2]) >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                this.pos = i2 + 1;
            }
        }
        return new String(this.input, i, this.pos - i);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0027, code lost:
        r1 = r4.input;
        r2 = r4.pos;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String consumeLetterThenDigitSequence() {
        /*
            r4 = this;
            int r0 = r4.pos
        L_0x0002:
            int r1 = r4.pos
            int r2 = r4.length
            if (r1 >= r2) goto L_0x0021
            char[] r2 = r4.input
            char r2 = r2[r1]
            r3 = 65
            if (r2 < r3) goto L_0x0014
            r3 = 90
            if (r2 <= r3) goto L_0x001c
        L_0x0014:
            r3 = 97
            if (r2 < r3) goto L_0x0021
            r3 = 122(0x7a, float:1.71E-43)
            if (r2 > r3) goto L_0x0021
        L_0x001c:
            int r1 = r1 + 1
            r4.pos = r1
            goto L_0x0002
        L_0x0021:
            boolean r1 = r4.isEmpty()
            if (r1 != 0) goto L_0x003a
            char[] r1 = r4.input
            int r2 = r4.pos
            char r1 = r1[r2]
            r3 = 48
            if (r1 < r3) goto L_0x003a
            r3 = 57
            if (r1 > r3) goto L_0x003a
            int r2 = r2 + 1
            r4.pos = r2
            goto L_0x0021
        L_0x003a:
            java.lang.String r1 = new java.lang.String
            char[] r2 = r4.input
            int r3 = r4.pos
            int r3 = r3 - r0
            r1.<init>(r2, r0, r3)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.CharacterReader.consumeLetterThenDigitSequence():java.lang.String");
    }

    /* access modifiers changed from: package-private */
    public String consumeHexSequence() {
        char c;
        int i = this.pos;
        while (true) {
            int i2 = this.pos;
            if (i2 < this.length && (((c = this.input[i2]) >= '0' && c <= '9') || ((c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')))) {
                this.pos = i2 + 1;
            }
        }
        return new String(this.input, i, this.pos - i);
    }

    /* access modifiers changed from: package-private */
    public String consumeDigitSequence() {
        char c;
        int i = this.pos;
        while (true) {
            int i2 = this.pos;
            if (i2 < this.length && (c = this.input[i2]) >= '0' && c <= '9') {
                this.pos = i2 + 1;
            }
        }
        return new String(this.input, i, this.pos - i);
    }

    /* access modifiers changed from: package-private */
    public boolean matches(char c) {
        return !isEmpty() && this.input[this.pos] == c;
    }

    /* access modifiers changed from: package-private */
    public boolean matches(String str) {
        int length2 = str.length();
        if (length2 > this.length - this.pos) {
            return false;
        }
        for (int i = 0; i < length2; i++) {
            if (str.charAt(i) != this.input[this.pos + i]) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean matchesIgnoreCase(String str) {
        int length2 = str.length();
        if (length2 > this.length - this.pos) {
            return false;
        }
        for (int i = 0; i < length2; i++) {
            if (Character.toUpperCase(str.charAt(i)) != Character.toUpperCase(this.input[this.pos + i])) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean matchesAny(char... cArr) {
        if (isEmpty()) {
            return false;
        }
        char c = this.input[this.pos];
        for (char c2 : cArr) {
            if (c2 == c) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean matchesLetter() {
        if (isEmpty()) {
            return false;
        }
        char c = this.input[this.pos];
        if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean matchesDigit() {
        char c;
        if (!isEmpty() && (c = this.input[this.pos]) >= '0' && c <= '9') {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean matchConsume(String str) {
        if (!matches(str)) {
            return false;
        }
        this.pos += str.length();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean matchConsumeIgnoreCase(String str) {
        if (!matchesIgnoreCase(str)) {
            return false;
        }
        this.pos += str.length();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean containsIgnoreCase(String str) {
        return nextIndexOf((CharSequence) str.toLowerCase(Locale.ENGLISH)) > -1 || nextIndexOf((CharSequence) str.toUpperCase(Locale.ENGLISH)) > -1;
    }

    public String toString() {
        char[] cArr = this.input;
        int i = this.pos;
        return new String(cArr, i, this.length - i);
    }
}
