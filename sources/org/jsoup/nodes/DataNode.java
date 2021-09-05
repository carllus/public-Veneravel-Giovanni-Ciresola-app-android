package org.jsoup.nodes;

import org.jsoup.nodes.Document;

public class DataNode extends Node {
    private static final String DATA_KEY = "data";

    public String nodeName() {
        return "#data";
    }

    /* access modifiers changed from: package-private */
    public void outerHtmlTail(StringBuilder sb, int i, Document.OutputSettings outputSettings) {
    }

    public DataNode(String str, String str2) {
        super(str2);
        this.attributes.put(DATA_KEY, str);
    }

    public String getWholeData() {
        return this.attributes.get(DATA_KEY);
    }

    public DataNode setWholeData(String str) {
        this.attributes.put(DATA_KEY, str);
        return this;
    }

    /* access modifiers changed from: package-private */
    public void outerHtmlHead(StringBuilder sb, int i, Document.OutputSettings outputSettings) {
        sb.append(getWholeData());
    }

    public String toString() {
        return outerHtml();
    }

    public static DataNode createFromEncoded(String str, String str2) {
        return new DataNode(Entities.unescape(str), str2);
    }
}
