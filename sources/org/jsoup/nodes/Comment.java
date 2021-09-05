package org.jsoup.nodes;

import org.jsoup.nodes.Document;

public class Comment extends Node {
    private static final String COMMENT_KEY = "comment";

    public String nodeName() {
        return "#comment";
    }

    /* access modifiers changed from: package-private */
    public void outerHtmlTail(StringBuilder sb, int i, Document.OutputSettings outputSettings) {
    }

    public Comment(String str, String str2) {
        super(str2);
        this.attributes.put(COMMENT_KEY, str);
    }

    public String getData() {
        return this.attributes.get(COMMENT_KEY);
    }

    /* access modifiers changed from: package-private */
    public void outerHtmlHead(StringBuilder sb, int i, Document.OutputSettings outputSettings) {
        if (outputSettings.prettyPrint()) {
            indent(sb, i, outputSettings);
        }
        sb.append("<!--").append(getData()).append("-->");
    }

    public String toString() {
        return outerHtml();
    }
}
