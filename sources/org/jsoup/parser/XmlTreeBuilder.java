package org.jsoup.parser;

import java.util.Iterator;
import java.util.List;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Token;

public class XmlTreeBuilder extends TreeBuilder {
    /* access modifiers changed from: protected */
    public void initialiseParse(String str, String str2, ParseErrorList parseErrorList) {
        super.initialiseParse(str, str2, parseErrorList);
        this.stack.add(this.doc);
        this.doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
    }

    /* renamed from: org.jsoup.parser.XmlTreeBuilder$1 */
    static /* synthetic */ class C06471 {
        static final /* synthetic */ int[] $SwitchMap$org$jsoup$parser$Token$TokenType;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|14) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                org.jsoup.parser.Token$TokenType[] r0 = org.jsoup.parser.Token.TokenType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$org$jsoup$parser$Token$TokenType = r0
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.StartTag     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$org$jsoup$parser$Token$TokenType     // Catch:{ NoSuchFieldError -> 0x001d }
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.EndTag     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$org$jsoup$parser$Token$TokenType     // Catch:{ NoSuchFieldError -> 0x0028 }
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.Comment     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$org$jsoup$parser$Token$TokenType     // Catch:{ NoSuchFieldError -> 0x0033 }
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.Character     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$org$jsoup$parser$Token$TokenType     // Catch:{ NoSuchFieldError -> 0x003e }
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.Doctype     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = $SwitchMap$org$jsoup$parser$Token$TokenType     // Catch:{ NoSuchFieldError -> 0x0049 }
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.EOF     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.XmlTreeBuilder.C06471.<clinit>():void");
        }
    }

    /* access modifiers changed from: protected */
    public boolean process(Token token) {
        switch (C06471.$SwitchMap$org$jsoup$parser$Token$TokenType[token.type.ordinal()]) {
            case 1:
                insert(token.asStartTag());
                return true;
            case 2:
                popStackToClose(token.asEndTag());
                return true;
            case 3:
                insert(token.asComment());
                return true;
            case 4:
                insert(token.asCharacter());
                return true;
            case 5:
                insert(token.asDoctype());
                return true;
            case 6:
                return true;
            default:
                Validate.fail("Unexpected token type: " + token.type);
                return true;
        }
    }

    private void insertNode(Node node) {
        currentElement().appendChild(node);
    }

    /* access modifiers changed from: package-private */
    public Element insert(Token.StartTag startTag) {
        Tag valueOf = Tag.valueOf(startTag.name());
        Element element = new Element(valueOf, this.baseUri, startTag.attributes);
        insertNode(element);
        if (startTag.isSelfClosing()) {
            this.tokeniser.acknowledgeSelfClosingFlag();
            if (!valueOf.isKnownTag()) {
                valueOf.setSelfClosing();
            }
        } else {
            this.stack.add(element);
        }
        return element;
    }

    /* JADX WARNING: type inference failed for: r3v1, types: [org.jsoup.nodes.XmlDeclaration] */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void insert(org.jsoup.parser.Token.Comment r5) {
        /*
            r4 = this;
            org.jsoup.nodes.Comment r0 = new org.jsoup.nodes.Comment
            java.lang.String r1 = r5.getData()
            java.lang.String r2 = r4.baseUri
            r0.<init>(r1, r2)
            boolean r5 = r5.bogus
            if (r5 == 0) goto L_0x003c
            java.lang.String r5 = r0.getData()
            int r1 = r5.length()
            r2 = 1
            if (r1 <= r2) goto L_0x003c
            java.lang.String r1 = "!"
            boolean r3 = r5.startsWith(r1)
            if (r3 != 0) goto L_0x002a
            java.lang.String r3 = "?"
            boolean r3 = r5.startsWith(r3)
            if (r3 == 0) goto L_0x003c
        L_0x002a:
            java.lang.String r2 = r5.substring(r2)
            org.jsoup.nodes.XmlDeclaration r3 = new org.jsoup.nodes.XmlDeclaration
            java.lang.String r0 = r0.baseUri()
            boolean r5 = r5.startsWith(r1)
            r3.<init>(r2, r0, r5)
            r0 = r3
        L_0x003c:
            r4.insertNode(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.XmlTreeBuilder.insert(org.jsoup.parser.Token$Comment):void");
    }

    /* access modifiers changed from: package-private */
    public void insert(Token.Character character) {
        insertNode(new TextNode(character.getData(), this.baseUri));
    }

    /* access modifiers changed from: package-private */
    public void insert(Token.Doctype doctype) {
        insertNode(new DocumentType(doctype.getName(), doctype.getPublicIdentifier(), doctype.getSystemIdentifier(), this.baseUri));
    }

    private void popStackToClose(Token.EndTag endTag) {
        Element element;
        String name = endTag.name();
        Iterator descendingIterator = this.stack.descendingIterator();
        while (true) {
            if (!descendingIterator.hasNext()) {
                element = null;
                break;
            }
            element = (Element) descendingIterator.next();
            if (element.nodeName().equals(name)) {
                break;
            }
        }
        if (element != null) {
            Iterator descendingIterator2 = this.stack.descendingIterator();
            while (descendingIterator2.hasNext()) {
                if (((Element) descendingIterator2.next()) == element) {
                    descendingIterator2.remove();
                    return;
                }
                descendingIterator2.remove();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public List<Node> parseFragment(String str, String str2, ParseErrorList parseErrorList) {
        initialiseParse(str, str2, parseErrorList);
        runParser();
        return this.doc.childNodes();
    }
}
