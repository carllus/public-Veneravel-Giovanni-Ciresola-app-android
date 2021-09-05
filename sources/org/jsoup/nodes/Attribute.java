package org.jsoup.nodes;

import java.util.Arrays;
import java.util.Map;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;

public class Attribute implements Map.Entry<String, String>, Cloneable {
    private static final String[] booleanAttributes = {"allowfullscreen", "async", "autofocus", "checked", "compact", "declare", "default", "defer", "disabled", "formnovalidate", "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize", "noshade", "novalidate", "nowrap", "open", "readonly", "required", "reversed", "seamless", "selected", "sortable", "truespeed", "typemustmatch"};
    private String key;
    private String value;

    public Attribute(String str, String str2) {
        Validate.notEmpty(str);
        Validate.notNull(str2);
        this.key = str.trim().toLowerCase();
        this.value = str2;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String str) {
        Validate.notEmpty(str);
        this.key = str.trim().toLowerCase();
    }

    public String getValue() {
        return this.value;
    }

    public String setValue(String str) {
        Validate.notNull(str);
        String str2 = this.value;
        this.value = str;
        return str2;
    }

    public String html() {
        StringBuilder sb = new StringBuilder();
        html(sb, new Document("").outputSettings());
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public void html(StringBuilder sb, Document.OutputSettings outputSettings) {
        sb.append(this.key);
        if (!shouldCollapseAttribute(outputSettings)) {
            sb.append("=\"");
            Entities.escape(sb, this.value, outputSettings, true, false, false);
            sb.append('\"');
        }
    }

    public String toString() {
        return html();
    }

    public static Attribute createFromEncoded(String str, String str2) {
        return new Attribute(str, Entities.unescape(str2, true));
    }

    /* access modifiers changed from: protected */
    public boolean isDataAttribute() {
        return this.key.startsWith("data-") && this.key.length() > 5;
    }

    /* access modifiers changed from: protected */
    public final boolean shouldCollapseAttribute(Document.OutputSettings outputSettings) {
        return ("".equals(this.value) || this.value.equalsIgnoreCase(this.key)) && outputSettings.syntax() == Document.OutputSettings.Syntax.html && Arrays.binarySearch(booleanAttributes, this.key) >= 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Attribute)) {
            return false;
        }
        Attribute attribute = (Attribute) obj;
        String str = this.key;
        if (str == null ? attribute.key != null : !str.equals(attribute.key)) {
            return false;
        }
        String str2 = this.value;
        String str3 = attribute.value;
        return str2 == null ? str3 == null : str2.equals(str3);
    }

    public int hashCode() {
        String str = this.key;
        int i = 0;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.value;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return hashCode + i;
    }

    public Attribute clone() {
        try {
            return (Attribute) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
