package org.jsoup.nodes;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class Document extends Element {
    private String location;
    private OutputSettings outputSettings = new OutputSettings();
    private QuirksMode quirksMode = QuirksMode.noQuirks;

    public enum QuirksMode {
        noQuirks,
        quirks,
        limitedQuirks
    }

    public String nodeName() {
        return "#document";
    }

    public Document(String str) {
        super(Tag.valueOf("#root"), str);
        this.location = str;
    }

    public static Document createShell(String str) {
        Validate.notNull(str);
        Document document = new Document(str);
        Element appendElement = document.appendElement("html");
        appendElement.appendElement("head");
        appendElement.appendElement("body");
        return document;
    }

    public String location() {
        return this.location;
    }

    public Element head() {
        return findFirstElementByTagName("head", this);
    }

    public Element body() {
        return findFirstElementByTagName("body", this);
    }

    public String title() {
        Element first = getElementsByTag("title").first();
        return first != null ? StringUtil.normaliseWhitespace(first.text()).trim() : "";
    }

    public void title(String str) {
        Validate.notNull(str);
        Element first = getElementsByTag("title").first();
        if (first == null) {
            head().appendElement("title").text(str);
        } else {
            first.text(str);
        }
    }

    public Element createElement(String str) {
        return new Element(Tag.valueOf(str), baseUri());
    }

    public Document normalise() {
        Element findFirstElementByTagName = findFirstElementByTagName("html", this);
        if (findFirstElementByTagName == null) {
            findFirstElementByTagName = appendElement("html");
        }
        if (head() == null) {
            findFirstElementByTagName.prependElement("head");
        }
        if (body() == null) {
            findFirstElementByTagName.appendElement("body");
        }
        normaliseTextNodes(head());
        normaliseTextNodes(findFirstElementByTagName);
        normaliseTextNodes(this);
        normaliseStructure("head", findFirstElementByTagName);
        normaliseStructure("body", findFirstElementByTagName);
        return this;
    }

    private void normaliseTextNodes(Element element) {
        ArrayList arrayList = new ArrayList();
        for (Node node : element.childNodes) {
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                if (!textNode.isBlank()) {
                    arrayList.add(textNode);
                }
            }
        }
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            Node node2 = (Node) arrayList.get(size);
            element.removeChild(node2);
            body().prependChild(new TextNode(" ", ""));
            body().prependChild(node2);
        }
    }

    private void normaliseStructure(String str, Element element) {
        Elements elementsByTag = getElementsByTag(str);
        Element first = elementsByTag.first();
        if (elementsByTag.size() > 1) {
            ArrayList<Node> arrayList = new ArrayList<>();
            for (int i = 1; i < elementsByTag.size(); i++) {
                Element element2 = elementsByTag.get(i);
                for (Node add : element2.childNodes) {
                    arrayList.add(add);
                }
                element2.remove();
            }
            for (Node appendChild : arrayList) {
                first.appendChild(appendChild);
            }
        }
        if (!first.parent().equals(element)) {
            element.appendChild(first);
        }
    }

    private Element findFirstElementByTagName(String str, Node node) {
        if (node.nodeName().equals(str)) {
            return (Element) node;
        }
        for (Node findFirstElementByTagName : node.childNodes) {
            Element findFirstElementByTagName2 = findFirstElementByTagName(str, findFirstElementByTagName);
            if (findFirstElementByTagName2 != null) {
                return findFirstElementByTagName2;
            }
        }
        return null;
    }

    public String outerHtml() {
        return super.html();
    }

    public Element text(String str) {
        body().text(str);
        return this;
    }

    public Document clone() {
        Document document = (Document) super.clone();
        document.outputSettings = this.outputSettings.clone();
        return document;
    }

    public static class OutputSettings implements Cloneable {
        private Charset charset;
        private CharsetEncoder charsetEncoder;
        private Entities.EscapeMode escapeMode = Entities.EscapeMode.base;
        private int indentAmount;
        private boolean outline;
        private boolean prettyPrint;
        private Syntax syntax;

        public enum Syntax {
            html,
            xml
        }

        public OutputSettings() {
            Charset forName = Charset.forName("UTF-8");
            this.charset = forName;
            this.charsetEncoder = forName.newEncoder();
            this.prettyPrint = true;
            this.outline = false;
            this.indentAmount = 1;
            this.syntax = Syntax.html;
        }

        public Entities.EscapeMode escapeMode() {
            return this.escapeMode;
        }

        public OutputSettings escapeMode(Entities.EscapeMode escapeMode2) {
            this.escapeMode = escapeMode2;
            return this;
        }

        public Charset charset() {
            return this.charset;
        }

        public OutputSettings charset(Charset charset2) {
            this.charset = charset2;
            this.charsetEncoder = charset2.newEncoder();
            return this;
        }

        public OutputSettings charset(String str) {
            charset(Charset.forName(str));
            return this;
        }

        /* access modifiers changed from: package-private */
        public CharsetEncoder encoder() {
            return this.charsetEncoder;
        }

        public Syntax syntax() {
            return this.syntax;
        }

        public OutputSettings syntax(Syntax syntax2) {
            this.syntax = syntax2;
            return this;
        }

        public boolean prettyPrint() {
            return this.prettyPrint;
        }

        public OutputSettings prettyPrint(boolean z) {
            this.prettyPrint = z;
            return this;
        }

        public boolean outline() {
            return this.outline;
        }

        public OutputSettings outline(boolean z) {
            this.outline = z;
            return this;
        }

        public int indentAmount() {
            return this.indentAmount;
        }

        public OutputSettings indentAmount(int i) {
            Validate.isTrue(i >= 0);
            this.indentAmount = i;
            return this;
        }

        public OutputSettings clone() {
            try {
                OutputSettings outputSettings = (OutputSettings) super.clone();
                outputSettings.charset(this.charset.name());
                outputSettings.escapeMode = Entities.EscapeMode.valueOf(this.escapeMode.name());
                return outputSettings;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public OutputSettings outputSettings() {
        return this.outputSettings;
    }

    public Document outputSettings(OutputSettings outputSettings2) {
        Validate.notNull(outputSettings2);
        this.outputSettings = outputSettings2;
        return this;
    }

    public QuirksMode quirksMode() {
        return this.quirksMode;
    }

    public Document quirksMode(QuirksMode quirksMode2) {
        this.quirksMode = quirksMode2;
        return this;
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
