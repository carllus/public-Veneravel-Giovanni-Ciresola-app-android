package org.jsoup.nodes;

import org.jsoup.nodes.Document;

public class XmlDeclaration extends Node {
    private static final String DECL_KEY = "declaration";
    private final boolean isProcessingInstruction;

    public String nodeName() {
        return "#declaration";
    }

    /* access modifiers changed from: package-private */
    public void outerHtmlTail(StringBuilder sb, int i, Document.OutputSettings outputSettings) {
    }

    public XmlDeclaration(String str, String str2, boolean z) {
        super(str2);
        this.attributes.put(DECL_KEY, str);
        this.isProcessingInstruction = z;
    }

    public String getWholeDeclaration() {
        return this.attributes.get(DECL_KEY);
    }

    /* access modifiers changed from: package-private */
    public void outerHtmlHead(StringBuilder sb, int i, Document.OutputSettings outputSettings) {
        sb.append("<").append(this.isProcessingInstruction ? "!" : "?").append(getWholeDeclaration()).append(">");
    }

    public String toString() {
        return outerHtml();
    }
}
