package org.jsoup.examples;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class HtmlToPlainText {
    public static void main(String... strArr) throws IOException {
        boolean z = true;
        if (strArr.length != 1) {
            z = false;
        }
        Validate.isTrue(z, "usage: supply url to fetch");
        System.out.println(new HtmlToPlainText().getPlainText(Jsoup.connect(strArr[0]).get()));
    }

    public String getPlainText(Element element) {
        FormattingVisitor formattingVisitor = new FormattingVisitor();
        new NodeTraversor(formattingVisitor).traverse(element);
        return formattingVisitor.toString();
    }

    private class FormattingVisitor implements NodeVisitor {
        private static final int maxWidth = 80;
        private StringBuilder accum;
        private int width;

        private FormattingVisitor() {
            this.width = 0;
            this.accum = new StringBuilder();
        }

        public void head(Node node, int i) {
            String nodeName = node.nodeName();
            if (node instanceof TextNode) {
                append(((TextNode) node).text());
            } else if (nodeName.equals("li")) {
                append("\n * ");
            }
        }

        public void tail(Node node, int i) {
            String nodeName = node.nodeName();
            if (nodeName.equals("br")) {
                append("\n");
            } else if (StringUtil.m10in(nodeName, "p", "h1", "h2", "h3", "h4", "h5")) {
                append("\n\n");
            } else if (nodeName.equals("a")) {
                append(String.format(" <%s>", new Object[]{node.absUrl("href")}));
            }
        }

        private void append(String str) {
            if (str.startsWith("\n")) {
                this.width = 0;
            }
            if (str.equals(" ")) {
                if (this.accum.length() != 0) {
                    StringBuilder sb = this.accum;
                    if (StringUtil.m10in(sb.substring(sb.length() - 1), " ", "\n")) {
                        return;
                    }
                } else {
                    return;
                }
            }
            if (str.length() + this.width > 80) {
                String[] split = str.split("\\s+");
                int i = 0;
                while (i < split.length) {
                    String str2 = split[i];
                    if (!(i == split.length - 1)) {
                        str2 = str2 + " ";
                    }
                    if (str2.length() + this.width > 80) {
                        this.accum.append("\n").append(str2);
                        this.width = str2.length();
                    } else {
                        this.accum.append(str2);
                        this.width += str2.length();
                    }
                    i++;
                }
                return;
            }
            this.accum.append(str);
            this.width += str.length();
        }

        public String toString() {
            return this.accum.toString();
        }
    }
}
