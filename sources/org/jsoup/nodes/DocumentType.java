package org.jsoup.nodes;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;

public class DocumentType extends Node {
    public String nodeName() {
        return "#doctype";
    }

    /* access modifiers changed from: package-private */
    public void outerHtmlTail(StringBuilder sb, int i, Document.OutputSettings outputSettings) {
    }

    public DocumentType(String str, String str2, String str3, String str4) {
        super(str4);
        attr("name", str);
        attr("publicId", str2);
        attr("systemId", str3);
    }

    /* access modifiers changed from: package-private */
    public void outerHtmlHead(StringBuilder sb, int i, Document.OutputSettings outputSettings) {
        sb.append("<!DOCTYPE");
        if (!StringUtil.isBlank(attr("name"))) {
            sb.append(" ").append(attr("name"));
        }
        if (!StringUtil.isBlank(attr("publicId"))) {
            sb.append(" PUBLIC \"").append(attr("publicId")).append('\"');
        }
        if (!StringUtil.isBlank(attr("systemId"))) {
            sb.append(" \"").append(attr("systemId")).append('\"');
        }
        sb.append('>');
    }
}
