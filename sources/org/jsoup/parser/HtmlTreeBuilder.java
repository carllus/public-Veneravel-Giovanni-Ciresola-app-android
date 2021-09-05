package org.jsoup.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.helper.DescendableLinkedList;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Token;
import org.jsoup.select.Elements;

class HtmlTreeBuilder extends TreeBuilder {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String[] TagSearchButton = {"button"};
    private static final String[] TagSearchEndTags = {"dd", "dt", "li", "option", "optgroup", "p", "rp", "rt"};
    private static final String[] TagSearchList = {"ol", "ul"};
    private static final String[] TagSearchSelectScope = {"optgroup", "option"};
    private static final String[] TagSearchSpecial = {"address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", "blockquote", "body", "br", "button", "caption", "center", "col", "colgroup", "command", "dd", "details", "dir", "div", "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", "form", "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "iframe", "img", "input", "isindex", "li", "link", "listing", "marquee", "menu", "meta", "nav", "noembed", "noframes", "noscript", "object", "ol", "p", "param", "plaintext", "pre", "script", "section", "select", "style", "summary", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "ul", "wbr", "xmp"};
    private static final String[] TagSearchTableScope = {"html", "table"};
    private static final String[] TagsScriptStyle = {"script", "style"};
    public static final String[] TagsSearchInScope = {"applet", "caption", "html", "table", "td", "th", "marquee", "object"};
    private boolean baseUriSetFromDoc = false;
    private Element contextElement;
    private FormElement formElement;
    private DescendableLinkedList<Element> formattingElements = new DescendableLinkedList<>();
    private boolean fosterInserts = false;
    private boolean fragmentParsing = false;
    private boolean framesetOk = true;
    private Element headElement;
    private HtmlTreeBuilderState originalState;
    private List<Token.Character> pendingTableCharacters = new ArrayList();
    private HtmlTreeBuilderState state;

    HtmlTreeBuilder() {
    }

    /* access modifiers changed from: package-private */
    public Document parse(String str, String str2, ParseErrorList parseErrorList) {
        this.state = HtmlTreeBuilderState.Initial;
        this.baseUriSetFromDoc = false;
        return super.parse(str, str2, parseErrorList);
    }

    /* access modifiers changed from: package-private */
    public List<Node> parseFragment(String str, Element element, String str2, ParseErrorList parseErrorList) {
        Element element2;
        this.state = HtmlTreeBuilderState.Initial;
        initialiseParse(str, str2, parseErrorList);
        this.contextElement = element;
        this.fragmentParsing = true;
        if (element != null) {
            if (element.ownerDocument() != null) {
                this.doc.quirksMode(element.ownerDocument().quirksMode());
            }
            String tagName = element.tagName();
            if (StringUtil.m10in(tagName, "title", "textarea")) {
                this.tokeniser.transition(TokeniserState.Rcdata);
            } else if (StringUtil.m10in(tagName, "iframe", "noembed", "noframes", "style", "xmp")) {
                this.tokeniser.transition(TokeniserState.Rawtext);
            } else if (tagName.equals("script")) {
                this.tokeniser.transition(TokeniserState.ScriptData);
            } else if (tagName.equals("noscript")) {
                this.tokeniser.transition(TokeniserState.Data);
            } else if (tagName.equals("plaintext")) {
                this.tokeniser.transition(TokeniserState.Data);
            } else {
                this.tokeniser.transition(TokeniserState.Data);
            }
            element2 = new Element(Tag.valueOf("html"), str2);
            this.doc.appendChild(element2);
            this.stack.push(element2);
            resetInsertionMode();
            Elements parents = element.parents();
            parents.add(0, element);
            Iterator<Element> it = parents.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Element next = it.next();
                if (next instanceof FormElement) {
                    this.formElement = (FormElement) next;
                    break;
                }
            }
        } else {
            element2 = null;
        }
        runParser();
        if (element != null) {
            return element2.childNodes();
        }
        return this.doc.childNodes();
    }

    /* access modifiers changed from: protected */
    public boolean process(Token token) {
        this.currentToken = token;
        return this.state.process(token, this);
    }

    /* access modifiers changed from: package-private */
    public boolean process(Token token, HtmlTreeBuilderState htmlTreeBuilderState) {
        this.currentToken = token;
        return htmlTreeBuilderState.process(token, this);
    }

    /* access modifiers changed from: package-private */
    public void transition(HtmlTreeBuilderState htmlTreeBuilderState) {
        this.state = htmlTreeBuilderState;
    }

    /* access modifiers changed from: package-private */
    public HtmlTreeBuilderState state() {
        return this.state;
    }

    /* access modifiers changed from: package-private */
    public void markInsertionMode() {
        this.originalState = this.state;
    }

    /* access modifiers changed from: package-private */
    public HtmlTreeBuilderState originalState() {
        return this.originalState;
    }

    /* access modifiers changed from: package-private */
    public void framesetOk(boolean z) {
        this.framesetOk = z;
    }

    /* access modifiers changed from: package-private */
    public boolean framesetOk() {
        return this.framesetOk;
    }

    /* access modifiers changed from: package-private */
    public Document getDocument() {
        return this.doc;
    }

    /* access modifiers changed from: package-private */
    public String getBaseUri() {
        return this.baseUri;
    }

    /* access modifiers changed from: package-private */
    public void maybeSetBaseUri(Element element) {
        if (!this.baseUriSetFromDoc) {
            String absUrl = element.absUrl("href");
            if (absUrl.length() != 0) {
                this.baseUri = absUrl;
                this.baseUriSetFromDoc = true;
                this.doc.setBaseUri(absUrl);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isFragmentParsing() {
        return this.fragmentParsing;
    }

    /* access modifiers changed from: package-private */
    public void error(HtmlTreeBuilderState htmlTreeBuilderState) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpected token [%s] when in state [%s]", this.currentToken.tokenType(), htmlTreeBuilderState));
        }
    }

    /* access modifiers changed from: package-private */
    public Element insert(Token.StartTag startTag) {
        if (startTag.isSelfClosing()) {
            Element insertEmpty = insertEmpty(startTag);
            this.stack.add(insertEmpty);
            this.tokeniser.transition(TokeniserState.Data);
            this.tokeniser.emit((Token) new Token.EndTag(insertEmpty.tagName()));
            return insertEmpty;
        }
        Element element = new Element(Tag.valueOf(startTag.name()), this.baseUri, startTag.attributes);
        insert(element);
        return element;
    }

    /* access modifiers changed from: package-private */
    public Element insert(String str) {
        Element element = new Element(Tag.valueOf(str), this.baseUri);
        insert(element);
        return element;
    }

    /* access modifiers changed from: package-private */
    public void insert(Element element) {
        insertNode(element);
        this.stack.add(element);
    }

    /* access modifiers changed from: package-private */
    public Element insertEmpty(Token.StartTag startTag) {
        Tag valueOf = Tag.valueOf(startTag.name());
        Element element = new Element(valueOf, this.baseUri, startTag.attributes);
        insertNode(element);
        if (startTag.isSelfClosing()) {
            if (!valueOf.isKnownTag()) {
                valueOf.setSelfClosing();
                this.tokeniser.acknowledgeSelfClosingFlag();
            } else if (valueOf.isSelfClosing()) {
                this.tokeniser.acknowledgeSelfClosingFlag();
            }
        }
        return element;
    }

    /* access modifiers changed from: package-private */
    public FormElement insertForm(Token.StartTag startTag, boolean z) {
        FormElement formElement2 = new FormElement(Tag.valueOf(startTag.name()), this.baseUri, startTag.attributes);
        setFormElement(formElement2);
        insertNode(formElement2);
        if (z) {
            this.stack.add(formElement2);
        }
        return formElement2;
    }

    /* access modifiers changed from: package-private */
    public void insert(Token.Comment comment) {
        insertNode(new Comment(comment.getData(), this.baseUri));
    }

    /* access modifiers changed from: package-private */
    public void insert(Token.Character character) {
        Node node;
        String tagName = currentElement().tagName();
        if (tagName.equals("script") || tagName.equals("style")) {
            node = new DataNode(character.getData(), this.baseUri);
        } else {
            node = new TextNode(character.getData(), this.baseUri);
        }
        currentElement().appendChild(node);
    }

    private void insertNode(Node node) {
        FormElement formElement2;
        if (this.stack.size() == 0) {
            this.doc.appendChild(node);
        } else if (isFosterInserts()) {
            insertInFosterParent(node);
        } else {
            currentElement().appendChild(node);
        }
        if (node instanceof Element) {
            Element element = (Element) node;
            if (element.tag().isFormListed() && (formElement2 = this.formElement) != null) {
                formElement2.addElement(element);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Element pop() {
        if (((Element) this.stack.peekLast()).nodeName().equals("td") && !this.state.name().equals("InCell")) {
            Validate.isFalse(true, "pop td not in cell");
        }
        if (((Element) this.stack.peekLast()).nodeName().equals("html")) {
            Validate.isFalse(true, "popping html!");
        }
        return (Element) this.stack.pollLast();
    }

    /* access modifiers changed from: package-private */
    public void push(Element element) {
        this.stack.add(element);
    }

    /* access modifiers changed from: package-private */
    public DescendableLinkedList<Element> getStack() {
        return this.stack;
    }

    /* access modifiers changed from: package-private */
    public boolean onStack(Element element) {
        return isElementInQueue(this.stack, element);
    }

    private boolean isElementInQueue(DescendableLinkedList<Element> descendableLinkedList, Element element) {
        Iterator<Element> descendingIterator = descendableLinkedList.descendingIterator();
        while (descendingIterator.hasNext()) {
            if (descendingIterator.next() == element) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public Element getFromStack(String str) {
        Iterator descendingIterator = this.stack.descendingIterator();
        while (descendingIterator.hasNext()) {
            Element element = (Element) descendingIterator.next();
            if (element.nodeName().equals(str)) {
                return element;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean removeFromStack(Element element) {
        Iterator descendingIterator = this.stack.descendingIterator();
        while (descendingIterator.hasNext()) {
            if (((Element) descendingIterator.next()) == element) {
                descendingIterator.remove();
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void popStackToClose(String str) {
        Iterator descendingIterator = this.stack.descendingIterator();
        while (descendingIterator.hasNext()) {
            if (((Element) descendingIterator.next()).nodeName().equals(str)) {
                descendingIterator.remove();
                return;
            }
            descendingIterator.remove();
        }
    }

    /* access modifiers changed from: package-private */
    public void popStackToClose(String... strArr) {
        Iterator descendingIterator = this.stack.descendingIterator();
        while (descendingIterator.hasNext()) {
            if (StringUtil.m10in(((Element) descendingIterator.next()).nodeName(), strArr)) {
                descendingIterator.remove();
                return;
            }
            descendingIterator.remove();
        }
    }

    /* access modifiers changed from: package-private */
    public void popStackToBefore(String str) {
        Iterator descendingIterator = this.stack.descendingIterator();
        while (descendingIterator.hasNext() && !((Element) descendingIterator.next()).nodeName().equals(str)) {
            descendingIterator.remove();
        }
    }

    /* access modifiers changed from: package-private */
    public void clearStackToTableContext() {
        clearStackToContext("table");
    }

    /* access modifiers changed from: package-private */
    public void clearStackToTableBodyContext() {
        clearStackToContext("tbody", "tfoot", "thead");
    }

    /* access modifiers changed from: package-private */
    public void clearStackToTableRowContext() {
        clearStackToContext("tr");
    }

    private void clearStackToContext(String... strArr) {
        Iterator descendingIterator = this.stack.descendingIterator();
        while (descendingIterator.hasNext()) {
            Element element = (Element) descendingIterator.next();
            if (!StringUtil.m10in(element.nodeName(), strArr) && !element.nodeName().equals("html")) {
                descendingIterator.remove();
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Element aboveOnStack(Element element) {
        Iterator descendingIterator = this.stack.descendingIterator();
        while (descendingIterator.hasNext()) {
            if (((Element) descendingIterator.next()) == element) {
                return (Element) descendingIterator.next();
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void insertOnStackAfter(Element element, Element element2) {
        int lastIndexOf = this.stack.lastIndexOf(element);
        Validate.isTrue(lastIndexOf != -1);
        this.stack.add(lastIndexOf + 1, element2);
    }

    /* access modifiers changed from: package-private */
    public void replaceOnStack(Element element, Element element2) {
        replaceInQueue(this.stack, element, element2);
    }

    private void replaceInQueue(LinkedList<Element> linkedList, Element element, Element element2) {
        int lastIndexOf = linkedList.lastIndexOf(element);
        Validate.isTrue(lastIndexOf != -1);
        linkedList.remove(lastIndexOf);
        linkedList.add(lastIndexOf, element2);
    }

    /* access modifiers changed from: package-private */
    public void resetInsertionMode() {
        Iterator descendingIterator = this.stack.descendingIterator();
        boolean z = false;
        while (descendingIterator.hasNext()) {
            Element element = (Element) descendingIterator.next();
            if (!descendingIterator.hasNext()) {
                z = true;
                element = this.contextElement;
            }
            String nodeName = element.nodeName();
            if ("select".equals(nodeName)) {
                transition(HtmlTreeBuilderState.InSelect);
                return;
            } else if ("td".equals(nodeName) || ("td".equals(nodeName) && !z)) {
                transition(HtmlTreeBuilderState.InCell);
                return;
            } else if ("tr".equals(nodeName)) {
                transition(HtmlTreeBuilderState.InRow);
                return;
            } else if ("tbody".equals(nodeName) || "thead".equals(nodeName) || "tfoot".equals(nodeName)) {
                transition(HtmlTreeBuilderState.InTableBody);
                return;
            } else if ("caption".equals(nodeName)) {
                transition(HtmlTreeBuilderState.InCaption);
                return;
            } else if ("colgroup".equals(nodeName)) {
                transition(HtmlTreeBuilderState.InColumnGroup);
                return;
            } else if ("table".equals(nodeName)) {
                transition(HtmlTreeBuilderState.InTable);
                return;
            } else if ("head".equals(nodeName)) {
                transition(HtmlTreeBuilderState.InBody);
                return;
            } else if ("body".equals(nodeName)) {
                transition(HtmlTreeBuilderState.InBody);
                return;
            } else if ("frameset".equals(nodeName)) {
                transition(HtmlTreeBuilderState.InFrameset);
                return;
            } else if ("html".equals(nodeName)) {
                transition(HtmlTreeBuilderState.BeforeHead);
                return;
            } else if (z) {
                transition(HtmlTreeBuilderState.InBody);
                return;
            }
        }
    }

    private boolean inSpecificScope(String str, String[] strArr, String[] strArr2) {
        return inSpecificScope(new String[]{str}, strArr, strArr2);
    }

    private boolean inSpecificScope(String[] strArr, String[] strArr2, String[] strArr3) {
        Iterator descendingIterator = this.stack.descendingIterator();
        while (descendingIterator.hasNext()) {
            String nodeName = ((Element) descendingIterator.next()).nodeName();
            if (StringUtil.m10in(nodeName, strArr)) {
                return true;
            }
            if (StringUtil.m10in(nodeName, strArr2)) {
                return false;
            }
            if (strArr3 != null && StringUtil.m10in(nodeName, strArr3)) {
                return false;
            }
        }
        Validate.fail("Should not be reachable");
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean inScope(String[] strArr) {
        return inSpecificScope(strArr, TagsSearchInScope, (String[]) null);
    }

    /* access modifiers changed from: package-private */
    public boolean inScope(String str) {
        return inScope(str, (String[]) null);
    }

    /* access modifiers changed from: package-private */
    public boolean inScope(String str, String[] strArr) {
        return inSpecificScope(str, TagsSearchInScope, strArr);
    }

    /* access modifiers changed from: package-private */
    public boolean inListItemScope(String str) {
        return inScope(str, TagSearchList);
    }

    /* access modifiers changed from: package-private */
    public boolean inButtonScope(String str) {
        return inScope(str, TagSearchButton);
    }

    /* access modifiers changed from: package-private */
    public boolean inTableScope(String str) {
        return inSpecificScope(str, TagSearchTableScope, (String[]) null);
    }

    /* access modifiers changed from: package-private */
    public boolean inSelectScope(String str) {
        Iterator descendingIterator = this.stack.descendingIterator();
        while (descendingIterator.hasNext()) {
            String nodeName = ((Element) descendingIterator.next()).nodeName();
            if (nodeName.equals(str)) {
                return true;
            }
            if (!StringUtil.m10in(nodeName, TagSearchSelectScope)) {
                return false;
            }
        }
        Validate.fail("Should not be reachable");
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setHeadElement(Element element) {
        this.headElement = element;
    }

    /* access modifiers changed from: package-private */
    public Element getHeadElement() {
        return this.headElement;
    }

    /* access modifiers changed from: package-private */
    public boolean isFosterInserts() {
        return this.fosterInserts;
    }

    /* access modifiers changed from: package-private */
    public void setFosterInserts(boolean z) {
        this.fosterInserts = z;
    }

    /* access modifiers changed from: package-private */
    public FormElement getFormElement() {
        return this.formElement;
    }

    /* access modifiers changed from: package-private */
    public void setFormElement(FormElement formElement2) {
        this.formElement = formElement2;
    }

    /* access modifiers changed from: package-private */
    public void newPendingTableCharacters() {
        this.pendingTableCharacters = new ArrayList();
    }

    /* access modifiers changed from: package-private */
    public List<Token.Character> getPendingTableCharacters() {
        return this.pendingTableCharacters;
    }

    /* access modifiers changed from: package-private */
    public void setPendingTableCharacters(List<Token.Character> list) {
        this.pendingTableCharacters = list;
    }

    /* access modifiers changed from: package-private */
    public void generateImpliedEndTags(String str) {
        while (str != null && !currentElement().nodeName().equals(str) && StringUtil.m10in(currentElement().nodeName(), TagSearchEndTags)) {
            pop();
        }
    }

    /* access modifiers changed from: package-private */
    public void generateImpliedEndTags() {
        generateImpliedEndTags((String) null);
    }

    /* access modifiers changed from: package-private */
    public boolean isSpecial(Element element) {
        return StringUtil.m10in(element.nodeName(), TagSearchSpecial);
    }

    /* access modifiers changed from: package-private */
    public void pushActiveFormattingElements(Element element) {
        Element next;
        Iterator<Element> descendingIterator = this.formattingElements.descendingIterator();
        int i = 0;
        while (true) {
            if (!descendingIterator.hasNext() || (next = descendingIterator.next()) == null) {
                break;
            }
            if (isSameFormattingElement(element, next)) {
                i++;
            }
            if (i == 3) {
                descendingIterator.remove();
                break;
            }
        }
        this.formattingElements.add(element);
    }

    private boolean isSameFormattingElement(Element element, Element element2) {
        return element.nodeName().equals(element2.nodeName()) && element.attributes().equals(element2.attributes());
    }

    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:23:? A[RETURN, SYNTHETIC] */
    void reconstructFormattingElements() {
        /*
            r7 = this;
            org.jsoup.helper.DescendableLinkedList<org.jsoup.nodes.Element> r0 = r7.formattingElements
            int r0 = r0.size()
            if (r0 == 0) goto L_0x0071
            org.jsoup.helper.DescendableLinkedList<org.jsoup.nodes.Element> r1 = r7.formattingElements
            java.lang.Object r1 = r1.getLast()
            if (r1 == 0) goto L_0x0071
            org.jsoup.helper.DescendableLinkedList<org.jsoup.nodes.Element> r1 = r7.formattingElements
            java.lang.Object r1 = r1.getLast()
            org.jsoup.nodes.Element r1 = (org.jsoup.nodes.Element) r1
            boolean r1 = r7.onStack(r1)
            if (r1 == 0) goto L_0x001f
            goto L_0x0071
        L_0x001f:
            org.jsoup.helper.DescendableLinkedList<org.jsoup.nodes.Element> r1 = r7.formattingElements
            java.lang.Object r1 = r1.getLast()
            org.jsoup.nodes.Element r1 = (org.jsoup.nodes.Element) r1
            r2 = 1
            int r0 = r0 - r2
            r3 = r0
        L_0x002a:
            r4 = 0
            if (r3 != 0) goto L_0x002e
            goto L_0x0041
        L_0x002e:
            org.jsoup.helper.DescendableLinkedList<org.jsoup.nodes.Element> r1 = r7.formattingElements
            int r3 = r3 + -1
            java.lang.Object r1 = r1.get(r3)
            org.jsoup.nodes.Element r1 = (org.jsoup.nodes.Element) r1
            if (r1 == 0) goto L_0x0040
            boolean r5 = r7.onStack(r1)
            if (r5 == 0) goto L_0x002a
        L_0x0040:
            r2 = 0
        L_0x0041:
            if (r2 != 0) goto L_0x004d
            org.jsoup.helper.DescendableLinkedList<org.jsoup.nodes.Element> r1 = r7.formattingElements
            int r3 = r3 + 1
            java.lang.Object r1 = r1.get(r3)
            org.jsoup.nodes.Element r1 = (org.jsoup.nodes.Element) r1
        L_0x004d:
            org.jsoup.helper.Validate.notNull(r1)
            java.lang.String r2 = r1.nodeName()
            org.jsoup.nodes.Element r2 = r7.insert((java.lang.String) r2)
            org.jsoup.nodes.Attributes r5 = r2.attributes()
            org.jsoup.nodes.Attributes r6 = r1.attributes()
            r5.addAll(r6)
            org.jsoup.helper.DescendableLinkedList<org.jsoup.nodes.Element> r5 = r7.formattingElements
            r5.add(r3, r2)
            org.jsoup.helper.DescendableLinkedList<org.jsoup.nodes.Element> r2 = r7.formattingElements
            int r5 = r3 + 1
            r2.remove(r5)
            if (r3 != r0) goto L_0x0040
        L_0x0071:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.HtmlTreeBuilder.reconstructFormattingElements():void");
    }

    /* access modifiers changed from: package-private */
    public void clearFormattingElementsToLastMarker() {
        while (!this.formattingElements.isEmpty()) {
            Element peekLast = this.formattingElements.peekLast();
            this.formattingElements.removeLast();
            if (peekLast == null) {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeFromActiveFormattingElements(Element element) {
        Iterator<Element> descendingIterator = this.formattingElements.descendingIterator();
        while (descendingIterator.hasNext()) {
            if (descendingIterator.next() == element) {
                descendingIterator.remove();
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isInActiveFormattingElements(Element element) {
        return isElementInQueue(this.formattingElements, element);
    }

    /* access modifiers changed from: package-private */
    public Element getActiveFormattingElement(String str) {
        Element next;
        Iterator<Element> descendingIterator = this.formattingElements.descendingIterator();
        while (descendingIterator.hasNext() && (next = descendingIterator.next()) != null) {
            if (next.nodeName().equals(str)) {
                return next;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void replaceActiveFormattingElement(Element element, Element element2) {
        replaceInQueue(this.formattingElements, element, element2);
    }

    /* access modifiers changed from: package-private */
    public void insertMarkerToFormattingElements() {
        this.formattingElements.add((Object) null);
    }

    /* access modifiers changed from: package-private */
    public void insertInFosterParent(Node node) {
        Element element;
        Element fromStack = getFromStack("table");
        boolean z = false;
        if (fromStack == null) {
            element = (Element) this.stack.get(0);
        } else if (fromStack.parent() != null) {
            element = fromStack.parent();
            z = true;
        } else {
            element = aboveOnStack(fromStack);
        }
        if (z) {
            Validate.notNull(fromStack);
            fromStack.before(node);
            return;
        }
        element.appendChild(node);
    }

    public String toString() {
        return "TreeBuilder{currentToken=" + this.currentToken + ", state=" + this.state + ", currentElement=" + currentElement() + '}';
    }
}
