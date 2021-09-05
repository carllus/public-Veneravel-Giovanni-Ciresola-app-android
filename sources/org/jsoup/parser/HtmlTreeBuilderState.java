package org.jsoup.parser;

import java.util.Iterator;
import org.jsoup.helper.DescendableLinkedList;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Token;

enum HtmlTreeBuilderState {
    Initial {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (HtmlTreeBuilderState.isWhitespace(token)) {
                return true;
            }
            if (token.isComment()) {
                htmlTreeBuilder.insert(token.asComment());
            } else if (token.isDoctype()) {
                Token.Doctype asDoctype = token.asDoctype();
                htmlTreeBuilder.getDocument().appendChild(new DocumentType(asDoctype.getName(), asDoctype.getPublicIdentifier(), asDoctype.getSystemIdentifier(), htmlTreeBuilder.getBaseUri()));
                if (asDoctype.isForceQuirks()) {
                    htmlTreeBuilder.getDocument().quirksMode(Document.QuirksMode.quirks);
                }
                htmlTreeBuilder.transition(BeforeHtml);
            } else {
                htmlTreeBuilder.transition(BeforeHtml);
                return htmlTreeBuilder.process(token);
            }
            return true;
        }
    },
    BeforeHtml {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (token.isDoctype()) {
                htmlTreeBuilder.error(this);
                return false;
            }
            if (token.isComment()) {
                htmlTreeBuilder.insert(token.asComment());
            } else if (HtmlTreeBuilderState.isWhitespace(token)) {
                return true;
            } else {
                if (token.isStartTag() && token.asStartTag().name().equals("html")) {
                    htmlTreeBuilder.insert(token.asStartTag());
                    htmlTreeBuilder.transition(BeforeHead);
                } else if (token.isEndTag() && StringUtil.m10in(token.asEndTag().name(), "head", "body", "html", "br")) {
                    return anythingElse(token, htmlTreeBuilder);
                } else {
                    if (!token.isEndTag()) {
                        return anythingElse(token, htmlTreeBuilder);
                    }
                    htmlTreeBuilder.error(this);
                    return false;
                }
            }
            return true;
        }

        private boolean anythingElse(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            htmlTreeBuilder.insert("html");
            htmlTreeBuilder.transition(BeforeHead);
            return htmlTreeBuilder.process(token);
        }
    },
    BeforeHead {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (HtmlTreeBuilderState.isWhitespace(token)) {
                return true;
            }
            if (token.isComment()) {
                htmlTreeBuilder.insert(token.asComment());
            } else if (token.isDoctype()) {
                htmlTreeBuilder.error(this);
                return false;
            } else if (token.isStartTag() && token.asStartTag().name().equals("html")) {
                return InBody.process(token, htmlTreeBuilder);
            } else {
                if (token.isStartTag() && token.asStartTag().name().equals("head")) {
                    htmlTreeBuilder.setHeadElement(htmlTreeBuilder.insert(token.asStartTag()));
                    htmlTreeBuilder.transition(InHead);
                } else if (token.isEndTag() && StringUtil.m10in(token.asEndTag().name(), "head", "body", "html", "br")) {
                    htmlTreeBuilder.process(new Token.StartTag("head"));
                    return htmlTreeBuilder.process(token);
                } else if (token.isEndTag()) {
                    htmlTreeBuilder.error(this);
                    return false;
                } else {
                    htmlTreeBuilder.process(new Token.StartTag("head"));
                    return htmlTreeBuilder.process(token);
                }
            }
            return true;
        }
    },
    InHead {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (HtmlTreeBuilderState.isWhitespace(token)) {
                htmlTreeBuilder.insert(token.asCharacter());
                return true;
            }
            int i = C057124.$SwitchMap$org$jsoup$parser$Token$TokenType[token.type.ordinal()];
            if (i == 1) {
                htmlTreeBuilder.insert(token.asComment());
            } else if (i == 2) {
                htmlTreeBuilder.error(this);
                return false;
            } else if (i == 3) {
                Token.StartTag asStartTag = token.asStartTag();
                String name = asStartTag.name();
                if (name.equals("html")) {
                    return InBody.process(token, htmlTreeBuilder);
                }
                if (StringUtil.m10in(name, "base", "basefont", "bgsound", "command", "link")) {
                    Element insertEmpty = htmlTreeBuilder.insertEmpty(asStartTag);
                    if (name.equals("base") && insertEmpty.hasAttr("href")) {
                        htmlTreeBuilder.maybeSetBaseUri(insertEmpty);
                    }
                } else if (name.equals("meta")) {
                    htmlTreeBuilder.insertEmpty(asStartTag);
                } else if (name.equals("title")) {
                    HtmlTreeBuilderState.handleRcData(asStartTag, htmlTreeBuilder);
                } else if (StringUtil.m10in(name, "noframes", "style")) {
                    HtmlTreeBuilderState.handleRawtext(asStartTag, htmlTreeBuilder);
                } else if (name.equals("noscript")) {
                    htmlTreeBuilder.insert(asStartTag);
                    htmlTreeBuilder.transition(InHeadNoscript);
                } else if (name.equals("script")) {
                    htmlTreeBuilder.tokeniser.transition(TokeniserState.ScriptData);
                    htmlTreeBuilder.markInsertionMode();
                    htmlTreeBuilder.transition(Text);
                    htmlTreeBuilder.insert(asStartTag);
                } else if (!name.equals("head")) {
                    return anythingElse(token, htmlTreeBuilder);
                } else {
                    htmlTreeBuilder.error(this);
                    return false;
                }
            } else if (i != 4) {
                return anythingElse(token, htmlTreeBuilder);
            } else {
                String name2 = token.asEndTag().name();
                if (name2.equals("head")) {
                    htmlTreeBuilder.pop();
                    htmlTreeBuilder.transition(AfterHead);
                } else if (StringUtil.m10in(name2, "body", "html", "br")) {
                    return anythingElse(token, htmlTreeBuilder);
                } else {
                    htmlTreeBuilder.error(this);
                    return false;
                }
            }
            return true;
        }

        private boolean anythingElse(Token token, TreeBuilder treeBuilder) {
            treeBuilder.process(new Token.EndTag("head"));
            return treeBuilder.process(token);
        }
    },
    InHeadNoscript {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (token.isDoctype()) {
                htmlTreeBuilder.error(this);
                return true;
            } else if (token.isStartTag() && token.asStartTag().name().equals("html")) {
                return htmlTreeBuilder.process(token, InBody);
            } else {
                if (token.isEndTag() && token.asEndTag().name().equals("noscript")) {
                    htmlTreeBuilder.pop();
                    htmlTreeBuilder.transition(InHead);
                    return true;
                } else if (HtmlTreeBuilderState.isWhitespace(token) || token.isComment() || (token.isStartTag() && StringUtil.m10in(token.asStartTag().name(), "basefont", "bgsound", "link", "meta", "noframes", "style"))) {
                    return htmlTreeBuilder.process(token, InHead);
                } else {
                    if (token.isEndTag() && token.asEndTag().name().equals("br")) {
                        return anythingElse(token, htmlTreeBuilder);
                    }
                    if ((!token.isStartTag() || !StringUtil.m10in(token.asStartTag().name(), "head", "noscript")) && !token.isEndTag()) {
                        return anythingElse(token, htmlTreeBuilder);
                    }
                    htmlTreeBuilder.error(this);
                    return false;
                }
            }
        }

        private boolean anythingElse(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            htmlTreeBuilder.error(this);
            htmlTreeBuilder.process(new Token.EndTag("noscript"));
            return htmlTreeBuilder.process(token);
        }
    },
    AfterHead {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            Token token2 = token;
            HtmlTreeBuilder htmlTreeBuilder2 = htmlTreeBuilder;
            if (HtmlTreeBuilderState.isWhitespace(token)) {
                htmlTreeBuilder2.insert(token.asCharacter());
                return true;
            } else if (token.isComment()) {
                htmlTreeBuilder2.insert(token.asComment());
                return true;
            } else if (token.isDoctype()) {
                htmlTreeBuilder2.error(this);
                return true;
            } else if (token.isStartTag()) {
                Token.StartTag asStartTag = token.asStartTag();
                String name = asStartTag.name();
                if (name.equals("html")) {
                    return htmlTreeBuilder2.process(token2, InBody);
                }
                if (name.equals("body")) {
                    htmlTreeBuilder2.insert(asStartTag);
                    htmlTreeBuilder2.framesetOk(false);
                    htmlTreeBuilder2.transition(InBody);
                    return true;
                } else if (name.equals("frameset")) {
                    htmlTreeBuilder2.insert(asStartTag);
                    htmlTreeBuilder2.transition(InFrameset);
                    return true;
                } else if (StringUtil.m10in(name, "base", "basefont", "bgsound", "link", "meta", "noframes", "script", "style", "title")) {
                    htmlTreeBuilder2.error(this);
                    Element headElement = htmlTreeBuilder.getHeadElement();
                    htmlTreeBuilder2.push(headElement);
                    htmlTreeBuilder2.process(token2, InHead);
                    htmlTreeBuilder2.removeFromStack(headElement);
                    return true;
                } else if (name.equals("head")) {
                    htmlTreeBuilder2.error(this);
                    return false;
                } else {
                    anythingElse(token, htmlTreeBuilder);
                    return true;
                }
            } else if (!token.isEndTag()) {
                anythingElse(token, htmlTreeBuilder);
                return true;
            } else if (StringUtil.m10in(token.asEndTag().name(), "body", "html")) {
                anythingElse(token, htmlTreeBuilder);
                return true;
            } else {
                htmlTreeBuilder2.error(this);
                return false;
            }
        }

        private boolean anythingElse(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            htmlTreeBuilder.process(new Token.StartTag("body"));
            htmlTreeBuilder.framesetOk(true);
            return htmlTreeBuilder.process(token);
        }
    },
    InBody {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            Element element;
            Token token2 = token;
            HtmlTreeBuilder htmlTreeBuilder2 = htmlTreeBuilder;
            int i = C057124.$SwitchMap$org$jsoup$parser$Token$TokenType[token2.type.ordinal()];
            if (i == 1) {
                htmlTreeBuilder2.insert(token.asComment());
            } else if (i != 2) {
                int i2 = 3;
                if (i == 3) {
                    Token.StartTag asStartTag = token.asStartTag();
                    String name = asStartTag.name();
                    if (name.equals("html")) {
                        htmlTreeBuilder2.error(this);
                        Element element2 = (Element) htmlTreeBuilder.getStack().getFirst();
                        Iterator<Attribute> it = asStartTag.getAttributes().iterator();
                        while (it.hasNext()) {
                            Attribute next = it.next();
                            if (!element2.hasAttr(next.getKey())) {
                                element2.attributes().put(next);
                            }
                        }
                    } else if (StringUtil.m10in(name, Constants.InBodyStartToHead)) {
                        return htmlTreeBuilder2.process(token2, InHead);
                    } else {
                        if (name.equals("body")) {
                            htmlTreeBuilder2.error(this);
                            DescendableLinkedList<Element> stack = htmlTreeBuilder.getStack();
                            if (stack.size() == 1 || (stack.size() > 2 && !stack.get(1).nodeName().equals("body"))) {
                                return false;
                            }
                            htmlTreeBuilder2.framesetOk(false);
                            Element element3 = stack.get(1);
                            Iterator<Attribute> it2 = asStartTag.getAttributes().iterator();
                            while (it2.hasNext()) {
                                Attribute next2 = it2.next();
                                if (!element3.hasAttr(next2.getKey())) {
                                    element3.attributes().put(next2);
                                }
                            }
                        } else if (name.equals("frameset")) {
                            htmlTreeBuilder2.error(this);
                            DescendableLinkedList<Element> stack2 = htmlTreeBuilder.getStack();
                            if (stack2.size() == 1 || ((stack2.size() > 2 && !stack2.get(1).nodeName().equals("body")) || !htmlTreeBuilder.framesetOk())) {
                                return false;
                            }
                            Element element4 = stack2.get(1);
                            if (element4.parent() != null) {
                                element4.remove();
                            }
                            while (stack2.size() > 1) {
                                stack2.removeLast();
                            }
                            htmlTreeBuilder2.insert(asStartTag);
                            htmlTreeBuilder2.transition(InFrameset);
                        } else if (StringUtil.m10in(name, Constants.InBodyStartPClosers)) {
                            if (htmlTreeBuilder2.inButtonScope("p")) {
                                htmlTreeBuilder2.process(new Token.EndTag("p"));
                            }
                            htmlTreeBuilder2.insert(asStartTag);
                        } else if (StringUtil.m10in(name, Constants.Headings)) {
                            if (htmlTreeBuilder2.inButtonScope("p")) {
                                htmlTreeBuilder2.process(new Token.EndTag("p"));
                            }
                            if (StringUtil.m10in(htmlTreeBuilder.currentElement().nodeName(), Constants.Headings)) {
                                htmlTreeBuilder2.error(this);
                                htmlTreeBuilder.pop();
                            }
                            htmlTreeBuilder2.insert(asStartTag);
                        } else if (StringUtil.m10in(name, Constants.InBodyStartPreListing)) {
                            if (htmlTreeBuilder2.inButtonScope("p")) {
                                htmlTreeBuilder2.process(new Token.EndTag("p"));
                            }
                            htmlTreeBuilder2.insert(asStartTag);
                            htmlTreeBuilder2.framesetOk(false);
                        } else if (name.equals("form")) {
                            if (htmlTreeBuilder.getFormElement() != null) {
                                htmlTreeBuilder2.error(this);
                                return false;
                            }
                            if (htmlTreeBuilder2.inButtonScope("p")) {
                                htmlTreeBuilder2.process(new Token.EndTag("p"));
                            }
                            htmlTreeBuilder2.insertForm(asStartTag, true);
                        } else if (name.equals("li")) {
                            htmlTreeBuilder2.framesetOk(false);
                            DescendableLinkedList<Element> stack3 = htmlTreeBuilder.getStack();
                            int size = stack3.size() - 1;
                            while (true) {
                                if (size <= 0) {
                                    break;
                                }
                                Element element5 = stack3.get(size);
                                if (!element5.nodeName().equals("li")) {
                                    if (htmlTreeBuilder2.isSpecial(element5) && !StringUtil.m10in(element5.nodeName(), Constants.InBodyStartLiBreakers)) {
                                        break;
                                    }
                                    size--;
                                } else {
                                    htmlTreeBuilder2.process(new Token.EndTag("li"));
                                    break;
                                }
                            }
                            if (htmlTreeBuilder2.inButtonScope("p")) {
                                htmlTreeBuilder2.process(new Token.EndTag("p"));
                            }
                            htmlTreeBuilder2.insert(asStartTag);
                        } else if (StringUtil.m10in(name, Constants.DdDt)) {
                            htmlTreeBuilder2.framesetOk(false);
                            DescendableLinkedList<Element> stack4 = htmlTreeBuilder.getStack();
                            int size2 = stack4.size() - 1;
                            while (true) {
                                if (size2 <= 0) {
                                    break;
                                }
                                Element element6 = stack4.get(size2);
                                if (!StringUtil.m10in(element6.nodeName(), Constants.DdDt)) {
                                    if (htmlTreeBuilder2.isSpecial(element6) && !StringUtil.m10in(element6.nodeName(), Constants.InBodyStartLiBreakers)) {
                                        break;
                                    }
                                    size2--;
                                } else {
                                    htmlTreeBuilder2.process(new Token.EndTag(element6.nodeName()));
                                    break;
                                }
                            }
                            if (htmlTreeBuilder2.inButtonScope("p")) {
                                htmlTreeBuilder2.process(new Token.EndTag("p"));
                            }
                            htmlTreeBuilder2.insert(asStartTag);
                        } else if (name.equals("plaintext")) {
                            if (htmlTreeBuilder2.inButtonScope("p")) {
                                htmlTreeBuilder2.process(new Token.EndTag("p"));
                            }
                            htmlTreeBuilder2.insert(asStartTag);
                            htmlTreeBuilder2.tokeniser.transition(TokeniserState.PLAINTEXT);
                        } else if (name.equals("button")) {
                            if (htmlTreeBuilder2.inButtonScope("button")) {
                                htmlTreeBuilder2.error(this);
                                htmlTreeBuilder2.process(new Token.EndTag("button"));
                                htmlTreeBuilder2.process(asStartTag);
                            } else {
                                htmlTreeBuilder.reconstructFormattingElements();
                                htmlTreeBuilder2.insert(asStartTag);
                                htmlTreeBuilder2.framesetOk(false);
                            }
                        } else if (name.equals("a")) {
                            if (htmlTreeBuilder2.getActiveFormattingElement("a") != null) {
                                htmlTreeBuilder2.error(this);
                                htmlTreeBuilder2.process(new Token.EndTag("a"));
                                Element fromStack = htmlTreeBuilder2.getFromStack("a");
                                if (fromStack != null) {
                                    htmlTreeBuilder2.removeFromActiveFormattingElements(fromStack);
                                    htmlTreeBuilder2.removeFromStack(fromStack);
                                }
                            }
                            htmlTreeBuilder.reconstructFormattingElements();
                            htmlTreeBuilder2.pushActiveFormattingElements(htmlTreeBuilder2.insert(asStartTag));
                        } else if (StringUtil.m10in(name, Constants.Formatters)) {
                            htmlTreeBuilder.reconstructFormattingElements();
                            htmlTreeBuilder2.pushActiveFormattingElements(htmlTreeBuilder2.insert(asStartTag));
                        } else if (name.equals("nobr")) {
                            htmlTreeBuilder.reconstructFormattingElements();
                            if (htmlTreeBuilder2.inScope("nobr")) {
                                htmlTreeBuilder2.error(this);
                                htmlTreeBuilder2.process(new Token.EndTag("nobr"));
                                htmlTreeBuilder.reconstructFormattingElements();
                            }
                            htmlTreeBuilder2.pushActiveFormattingElements(htmlTreeBuilder2.insert(asStartTag));
                        } else if (StringUtil.m10in(name, Constants.InBodyStartApplets)) {
                            htmlTreeBuilder.reconstructFormattingElements();
                            htmlTreeBuilder2.insert(asStartTag);
                            htmlTreeBuilder.insertMarkerToFormattingElements();
                            htmlTreeBuilder2.framesetOk(false);
                        } else if (name.equals("table")) {
                            if (htmlTreeBuilder.getDocument().quirksMode() != Document.QuirksMode.quirks && htmlTreeBuilder2.inButtonScope("p")) {
                                htmlTreeBuilder2.process(new Token.EndTag("p"));
                            }
                            htmlTreeBuilder2.insert(asStartTag);
                            htmlTreeBuilder2.framesetOk(false);
                            htmlTreeBuilder2.transition(InTable);
                        } else if (StringUtil.m10in(name, Constants.InBodyStartEmptyFormatters)) {
                            htmlTreeBuilder.reconstructFormattingElements();
                            htmlTreeBuilder2.insertEmpty(asStartTag);
                            htmlTreeBuilder2.framesetOk(false);
                        } else if (name.equals("input")) {
                            htmlTreeBuilder.reconstructFormattingElements();
                            if (!htmlTreeBuilder2.insertEmpty(asStartTag).attr("type").equalsIgnoreCase("hidden")) {
                                htmlTreeBuilder2.framesetOk(false);
                            }
                        } else if (StringUtil.m10in(name, Constants.InBodyStartMedia)) {
                            htmlTreeBuilder2.insertEmpty(asStartTag);
                        } else if (name.equals("hr")) {
                            if (htmlTreeBuilder2.inButtonScope("p")) {
                                htmlTreeBuilder2.process(new Token.EndTag("p"));
                            }
                            htmlTreeBuilder2.insertEmpty(asStartTag);
                            htmlTreeBuilder2.framesetOk(false);
                        } else if (name.equals("image")) {
                            if (htmlTreeBuilder2.getFromStack("svg") == null) {
                                return htmlTreeBuilder2.process(asStartTag.name("img"));
                            }
                            htmlTreeBuilder2.insert(asStartTag);
                        } else if (name.equals("isindex")) {
                            htmlTreeBuilder2.error(this);
                            if (htmlTreeBuilder.getFormElement() != null) {
                                return false;
                            }
                            htmlTreeBuilder2.tokeniser.acknowledgeSelfClosingFlag();
                            htmlTreeBuilder2.process(new Token.StartTag("form"));
                            if (asStartTag.attributes.hasKey("action")) {
                                htmlTreeBuilder.getFormElement().attr("action", asStartTag.attributes.get("action"));
                            }
                            htmlTreeBuilder2.process(new Token.StartTag("hr"));
                            htmlTreeBuilder2.process(new Token.StartTag("label"));
                            htmlTreeBuilder2.process(new Token.Character(asStartTag.attributes.hasKey("prompt") ? asStartTag.attributes.get("prompt") : "This is a searchable index. Enter search keywords: "));
                            Attributes attributes = new Attributes();
                            Iterator<Attribute> it3 = asStartTag.attributes.iterator();
                            while (it3.hasNext()) {
                                Attribute next3 = it3.next();
                                if (!StringUtil.m10in(next3.getKey(), Constants.InBodyStartInputAttribs)) {
                                    attributes.put(next3);
                                }
                            }
                            attributes.put("name", "isindex");
                            htmlTreeBuilder2.process(new Token.StartTag("input", attributes));
                            htmlTreeBuilder2.process(new Token.EndTag("label"));
                            htmlTreeBuilder2.process(new Token.StartTag("hr"));
                            htmlTreeBuilder2.process(new Token.EndTag("form"));
                        } else if (name.equals("textarea")) {
                            htmlTreeBuilder2.insert(asStartTag);
                            htmlTreeBuilder2.tokeniser.transition(TokeniserState.Rcdata);
                            htmlTreeBuilder.markInsertionMode();
                            htmlTreeBuilder2.framesetOk(false);
                            htmlTreeBuilder2.transition(Text);
                        } else if (name.equals("xmp")) {
                            if (htmlTreeBuilder2.inButtonScope("p")) {
                                htmlTreeBuilder2.process(new Token.EndTag("p"));
                            }
                            htmlTreeBuilder.reconstructFormattingElements();
                            htmlTreeBuilder2.framesetOk(false);
                            HtmlTreeBuilderState.handleRawtext(asStartTag, htmlTreeBuilder2);
                        } else if (name.equals("iframe")) {
                            htmlTreeBuilder2.framesetOk(false);
                            HtmlTreeBuilderState.handleRawtext(asStartTag, htmlTreeBuilder2);
                        } else if (name.equals("noembed")) {
                            HtmlTreeBuilderState.handleRawtext(asStartTag, htmlTreeBuilder2);
                        } else if (name.equals("select")) {
                            htmlTreeBuilder.reconstructFormattingElements();
                            htmlTreeBuilder2.insert(asStartTag);
                            htmlTreeBuilder2.framesetOk(false);
                            HtmlTreeBuilderState state = htmlTreeBuilder.state();
                            if (state.equals(InTable) || state.equals(InCaption) || state.equals(InTableBody) || state.equals(InRow) || state.equals(InCell)) {
                                htmlTreeBuilder2.transition(InSelectInTable);
                            } else {
                                htmlTreeBuilder2.transition(InSelect);
                            }
                        } else if (StringUtil.m10in(name, Constants.InBodyStartOptions)) {
                            if (htmlTreeBuilder.currentElement().nodeName().equals("option")) {
                                htmlTreeBuilder2.process(new Token.EndTag("option"));
                            }
                            htmlTreeBuilder.reconstructFormattingElements();
                            htmlTreeBuilder2.insert(asStartTag);
                        } else if (StringUtil.m10in(name, Constants.InBodyStartRuby)) {
                            if (htmlTreeBuilder2.inScope("ruby")) {
                                htmlTreeBuilder.generateImpliedEndTags();
                                if (!htmlTreeBuilder.currentElement().nodeName().equals("ruby")) {
                                    htmlTreeBuilder2.error(this);
                                    htmlTreeBuilder2.popStackToBefore("ruby");
                                }
                                htmlTreeBuilder2.insert(asStartTag);
                            }
                        } else if (name.equals("math")) {
                            htmlTreeBuilder.reconstructFormattingElements();
                            htmlTreeBuilder2.insert(asStartTag);
                            htmlTreeBuilder2.tokeniser.acknowledgeSelfClosingFlag();
                        } else if (name.equals("svg")) {
                            htmlTreeBuilder.reconstructFormattingElements();
                            htmlTreeBuilder2.insert(asStartTag);
                            htmlTreeBuilder2.tokeniser.acknowledgeSelfClosingFlag();
                        } else if (StringUtil.m10in(name, Constants.InBodyStartDrop)) {
                            htmlTreeBuilder2.error(this);
                            return false;
                        } else {
                            htmlTreeBuilder.reconstructFormattingElements();
                            htmlTreeBuilder2.insert(asStartTag);
                        }
                    }
                } else if (i == 4) {
                    Token.EndTag asEndTag = token.asEndTag();
                    String name2 = asEndTag.name();
                    if (name2.equals("body")) {
                        if (!htmlTreeBuilder2.inScope("body")) {
                            htmlTreeBuilder2.error(this);
                            return false;
                        }
                        htmlTreeBuilder2.transition(AfterBody);
                    } else if (name2.equals("html")) {
                        if (htmlTreeBuilder2.process(new Token.EndTag("body"))) {
                            return htmlTreeBuilder2.process(asEndTag);
                        }
                    } else if (!StringUtil.m10in(name2, Constants.InBodyEndClosers)) {
                        Element element7 = null;
                        if (name2.equals("form")) {
                            FormElement formElement = htmlTreeBuilder.getFormElement();
                            htmlTreeBuilder2.setFormElement((FormElement) null);
                            if (formElement == null || !htmlTreeBuilder2.inScope(name2)) {
                                htmlTreeBuilder2.error(this);
                                return false;
                            }
                            htmlTreeBuilder.generateImpliedEndTags();
                            if (!htmlTreeBuilder.currentElement().nodeName().equals(name2)) {
                                htmlTreeBuilder2.error(this);
                            }
                            htmlTreeBuilder2.removeFromStack(formElement);
                        } else if (name2.equals("p")) {
                            if (!htmlTreeBuilder2.inButtonScope(name2)) {
                                htmlTreeBuilder2.error(this);
                                htmlTreeBuilder2.process(new Token.StartTag(name2));
                                return htmlTreeBuilder2.process(asEndTag);
                            }
                            htmlTreeBuilder2.generateImpliedEndTags(name2);
                            if (!htmlTreeBuilder.currentElement().nodeName().equals(name2)) {
                                htmlTreeBuilder2.error(this);
                            }
                            htmlTreeBuilder2.popStackToClose(name2);
                        } else if (name2.equals("li")) {
                            if (!htmlTreeBuilder2.inListItemScope(name2)) {
                                htmlTreeBuilder2.error(this);
                                return false;
                            }
                            htmlTreeBuilder2.generateImpliedEndTags(name2);
                            if (!htmlTreeBuilder.currentElement().nodeName().equals(name2)) {
                                htmlTreeBuilder2.error(this);
                            }
                            htmlTreeBuilder2.popStackToClose(name2);
                        } else if (StringUtil.m10in(name2, Constants.DdDt)) {
                            if (!htmlTreeBuilder2.inScope(name2)) {
                                htmlTreeBuilder2.error(this);
                                return false;
                            }
                            htmlTreeBuilder2.generateImpliedEndTags(name2);
                            if (!htmlTreeBuilder.currentElement().nodeName().equals(name2)) {
                                htmlTreeBuilder2.error(this);
                            }
                            htmlTreeBuilder2.popStackToClose(name2);
                        } else if (StringUtil.m10in(name2, Constants.Headings)) {
                            if (!htmlTreeBuilder2.inScope(Constants.Headings)) {
                                htmlTreeBuilder2.error(this);
                                return false;
                            }
                            htmlTreeBuilder2.generateImpliedEndTags(name2);
                            if (!htmlTreeBuilder.currentElement().nodeName().equals(name2)) {
                                htmlTreeBuilder2.error(this);
                            }
                            htmlTreeBuilder2.popStackToClose(Constants.Headings);
                        } else if (name2.equals("sarcasm")) {
                            return anyOtherEndTag(token, htmlTreeBuilder);
                        } else {
                            if (StringUtil.m10in(name2, Constants.InBodyEndAdoptionFormatters)) {
                                int i3 = 0;
                                while (i3 < 8) {
                                    Element activeFormattingElement = htmlTreeBuilder2.getActiveFormattingElement(name2);
                                    if (activeFormattingElement == null) {
                                        return anyOtherEndTag(token, htmlTreeBuilder);
                                    }
                                    if (!htmlTreeBuilder2.onStack(activeFormattingElement)) {
                                        htmlTreeBuilder2.error(this);
                                        htmlTreeBuilder2.removeFromActiveFormattingElements(activeFormattingElement);
                                        return true;
                                    } else if (!htmlTreeBuilder2.inScope(activeFormattingElement.nodeName())) {
                                        htmlTreeBuilder2.error(this);
                                        return false;
                                    } else {
                                        if (htmlTreeBuilder.currentElement() != activeFormattingElement) {
                                            htmlTreeBuilder2.error(this);
                                        }
                                        DescendableLinkedList<Element> stack5 = htmlTreeBuilder.getStack();
                                        int size3 = stack5.size();
                                        Element element8 = element7;
                                        int i4 = 0;
                                        boolean z = false;
                                        while (true) {
                                            if (i4 >= size3 || i4 >= 64) {
                                                element = element7;
                                            } else {
                                                element = stack5.get(i4);
                                                if (element != activeFormattingElement) {
                                                    if (z && htmlTreeBuilder2.isSpecial(element)) {
                                                        break;
                                                    }
                                                } else {
                                                    element8 = stack5.get(i4 - 1);
                                                    z = true;
                                                }
                                                i4++;
                                            }
                                        }
                                        element = element7;
                                        if (element == null) {
                                            htmlTreeBuilder2.popStackToClose(activeFormattingElement.nodeName());
                                            htmlTreeBuilder2.removeFromActiveFormattingElements(activeFormattingElement);
                                            return true;
                                        }
                                        Element element9 = element;
                                        Element element10 = element9;
                                        int i5 = 0;
                                        while (i5 < i2) {
                                            if (htmlTreeBuilder2.onStack(element9)) {
                                                element9 = htmlTreeBuilder2.aboveOnStack(element9);
                                            }
                                            if (!htmlTreeBuilder2.isInActiveFormattingElements(element9)) {
                                                htmlTreeBuilder2.removeFromStack(element9);
                                            } else if (element9 == activeFormattingElement) {
                                                break;
                                            } else {
                                                Element element11 = new Element(Tag.valueOf(element9.nodeName()), htmlTreeBuilder.getBaseUri());
                                                htmlTreeBuilder2.replaceActiveFormattingElement(element9, element11);
                                                htmlTreeBuilder2.replaceOnStack(element9, element11);
                                                if (element10.parent() != null) {
                                                    element10.remove();
                                                }
                                                element11.appendChild(element10);
                                                element9 = element11;
                                                element10 = element9;
                                            }
                                            i5++;
                                            i2 = 3;
                                        }
                                        if (StringUtil.m10in(element8.nodeName(), Constants.InBodyEndTableFosters)) {
                                            if (element10.parent() != null) {
                                                element10.remove();
                                            }
                                            htmlTreeBuilder2.insertInFosterParent(element10);
                                        } else {
                                            if (element10.parent() != null) {
                                                element10.remove();
                                            }
                                            element8.appendChild(element10);
                                        }
                                        Element element12 = new Element(activeFormattingElement.tag(), htmlTreeBuilder.getBaseUri());
                                        element12.attributes().addAll(activeFormattingElement.attributes());
                                        for (Node appendChild : (Node[]) element.childNodes().toArray(new Node[element.childNodeSize()])) {
                                            element12.appendChild(appendChild);
                                        }
                                        element.appendChild(element12);
                                        htmlTreeBuilder2.removeFromActiveFormattingElements(activeFormattingElement);
                                        htmlTreeBuilder2.removeFromStack(activeFormattingElement);
                                        htmlTreeBuilder2.insertOnStackAfter(element, element12);
                                        i3++;
                                        i2 = 3;
                                        element7 = null;
                                    }
                                }
                            } else if (StringUtil.m10in(name2, Constants.InBodyStartApplets)) {
                                if (!htmlTreeBuilder2.inScope("name")) {
                                    if (!htmlTreeBuilder2.inScope(name2)) {
                                        htmlTreeBuilder2.error(this);
                                        return false;
                                    }
                                    htmlTreeBuilder.generateImpliedEndTags();
                                    if (!htmlTreeBuilder.currentElement().nodeName().equals(name2)) {
                                        htmlTreeBuilder2.error(this);
                                    }
                                    htmlTreeBuilder2.popStackToClose(name2);
                                    htmlTreeBuilder.clearFormattingElementsToLastMarker();
                                }
                            } else if (!name2.equals("br")) {
                                return anyOtherEndTag(token, htmlTreeBuilder);
                            } else {
                                htmlTreeBuilder2.error(this);
                                htmlTreeBuilder2.process(new Token.StartTag("br"));
                                return false;
                            }
                        }
                    } else if (!htmlTreeBuilder2.inScope(name2)) {
                        htmlTreeBuilder2.error(this);
                        return false;
                    } else {
                        htmlTreeBuilder.generateImpliedEndTags();
                        if (!htmlTreeBuilder.currentElement().nodeName().equals(name2)) {
                            htmlTreeBuilder2.error(this);
                        }
                        htmlTreeBuilder2.popStackToClose(name2);
                    }
                } else if (i == 5) {
                    Token.Character asCharacter = token.asCharacter();
                    if (asCharacter.getData().equals(HtmlTreeBuilderState.nullString)) {
                        htmlTreeBuilder2.error(this);
                        return false;
                    } else if (!htmlTreeBuilder.framesetOk() || !HtmlTreeBuilderState.isWhitespace(asCharacter)) {
                        htmlTreeBuilder.reconstructFormattingElements();
                        htmlTreeBuilder2.insert(asCharacter);
                        htmlTreeBuilder2.framesetOk(false);
                    } else {
                        htmlTreeBuilder.reconstructFormattingElements();
                        htmlTreeBuilder2.insert(asCharacter);
                    }
                }
            } else {
                htmlTreeBuilder2.error(this);
                return false;
            }
            return true;
        }

        /* access modifiers changed from: package-private */
        public boolean anyOtherEndTag(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            String name = token.asEndTag().name();
            Iterator<Element> descendingIterator = htmlTreeBuilder.getStack().descendingIterator();
            while (descendingIterator.hasNext()) {
                Element next = descendingIterator.next();
                if (next.nodeName().equals(name)) {
                    htmlTreeBuilder.generateImpliedEndTags(name);
                    if (!name.equals(htmlTreeBuilder.currentElement().nodeName())) {
                        htmlTreeBuilder.error(this);
                    }
                    htmlTreeBuilder.popStackToClose(name);
                    return true;
                } else if (htmlTreeBuilder.isSpecial(next)) {
                    htmlTreeBuilder.error(this);
                    return false;
                }
            }
            return true;
        }
    },
    Text {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (token.isCharacter()) {
                htmlTreeBuilder.insert(token.asCharacter());
                return true;
            } else if (token.isEOF()) {
                htmlTreeBuilder.error(this);
                htmlTreeBuilder.pop();
                htmlTreeBuilder.transition(htmlTreeBuilder.originalState());
                return htmlTreeBuilder.process(token);
            } else if (!token.isEndTag()) {
                return true;
            } else {
                htmlTreeBuilder.pop();
                htmlTreeBuilder.transition(htmlTreeBuilder.originalState());
                return true;
            }
        }
    },
    InTable {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            Token token2 = token;
            HtmlTreeBuilder htmlTreeBuilder2 = htmlTreeBuilder;
            if (token.isCharacter()) {
                htmlTreeBuilder.newPendingTableCharacters();
                htmlTreeBuilder.markInsertionMode();
                htmlTreeBuilder2.transition(InTableText);
                return htmlTreeBuilder2.process(token2);
            } else if (token.isComment()) {
                htmlTreeBuilder2.insert(token.asComment());
                return true;
            } else if (token.isDoctype()) {
                htmlTreeBuilder2.error(this);
                return false;
            } else if (token.isStartTag()) {
                Token.StartTag asStartTag = token.asStartTag();
                String name = asStartTag.name();
                if (name.equals("caption")) {
                    htmlTreeBuilder.clearStackToTableContext();
                    htmlTreeBuilder.insertMarkerToFormattingElements();
                    htmlTreeBuilder2.insert(asStartTag);
                    htmlTreeBuilder2.transition(InCaption);
                } else if (name.equals("colgroup")) {
                    htmlTreeBuilder.clearStackToTableContext();
                    htmlTreeBuilder2.insert(asStartTag);
                    htmlTreeBuilder2.transition(InColumnGroup);
                } else if (name.equals("col")) {
                    htmlTreeBuilder2.process(new Token.StartTag("colgroup"));
                    return htmlTreeBuilder2.process(token2);
                } else if (StringUtil.m10in(name, "tbody", "tfoot", "thead")) {
                    htmlTreeBuilder.clearStackToTableContext();
                    htmlTreeBuilder2.insert(asStartTag);
                    htmlTreeBuilder2.transition(InTableBody);
                } else if (StringUtil.m10in(name, "td", "th", "tr")) {
                    htmlTreeBuilder2.process(new Token.StartTag("tbody"));
                    return htmlTreeBuilder2.process(token2);
                } else if (name.equals("table")) {
                    htmlTreeBuilder2.error(this);
                    if (htmlTreeBuilder2.process(new Token.EndTag("table"))) {
                        return htmlTreeBuilder2.process(token2);
                    }
                } else if (StringUtil.m10in(name, "style", "script")) {
                    return htmlTreeBuilder2.process(token2, InHead);
                } else {
                    if (name.equals("input")) {
                        if (!asStartTag.attributes.get("type").equalsIgnoreCase("hidden")) {
                            return anythingElse(token, htmlTreeBuilder);
                        }
                        htmlTreeBuilder2.insertEmpty(asStartTag);
                    } else if (!name.equals("form")) {
                        return anythingElse(token, htmlTreeBuilder);
                    } else {
                        htmlTreeBuilder2.error(this);
                        if (htmlTreeBuilder.getFormElement() != null) {
                            return false;
                        }
                        htmlTreeBuilder2.insertForm(asStartTag, false);
                    }
                }
                return true;
            } else if (token.isEndTag()) {
                String name2 = token.asEndTag().name();
                if (name2.equals("table")) {
                    if (!htmlTreeBuilder2.inTableScope(name2)) {
                        htmlTreeBuilder2.error(this);
                        return false;
                    }
                    htmlTreeBuilder2.popStackToClose("table");
                    htmlTreeBuilder.resetInsertionMode();
                    return true;
                } else if (!StringUtil.m10in(name2, "body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                    return anythingElse(token, htmlTreeBuilder);
                } else {
                    htmlTreeBuilder2.error(this);
                    return false;
                }
            } else if (!token.isEOF()) {
                return anythingElse(token, htmlTreeBuilder);
            } else {
                if (htmlTreeBuilder.currentElement().nodeName().equals("html")) {
                    htmlTreeBuilder2.error(this);
                }
                return true;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean anythingElse(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            htmlTreeBuilder.error(this);
            if (!StringUtil.m10in(htmlTreeBuilder.currentElement().nodeName(), "table", "tbody", "tfoot", "thead", "tr")) {
                return htmlTreeBuilder.process(token, InBody);
            }
            htmlTreeBuilder.setFosterInserts(true);
            boolean process = htmlTreeBuilder.process(token, InBody);
            htmlTreeBuilder.setFosterInserts(false);
            return process;
        }
    },
    InTableText {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (C057124.$SwitchMap$org$jsoup$parser$Token$TokenType[token.type.ordinal()] != 5) {
                if (htmlTreeBuilder.getPendingTableCharacters().size() > 0) {
                    for (Token.Character next : htmlTreeBuilder.getPendingTableCharacters()) {
                        if (!HtmlTreeBuilderState.isWhitespace(next)) {
                            htmlTreeBuilder.error(this);
                            if (StringUtil.m10in(htmlTreeBuilder.currentElement().nodeName(), "table", "tbody", "tfoot", "thead", "tr")) {
                                htmlTreeBuilder.setFosterInserts(true);
                                htmlTreeBuilder.process(next, InBody);
                                htmlTreeBuilder.setFosterInserts(false);
                            } else {
                                htmlTreeBuilder.process(next, InBody);
                            }
                        } else {
                            htmlTreeBuilder.insert(next);
                        }
                    }
                    htmlTreeBuilder.newPendingTableCharacters();
                }
                htmlTreeBuilder.transition(htmlTreeBuilder.originalState());
                return htmlTreeBuilder.process(token);
            }
            Token.Character asCharacter = token.asCharacter();
            if (asCharacter.getData().equals(HtmlTreeBuilderState.nullString)) {
                htmlTreeBuilder.error(this);
                return false;
            }
            htmlTreeBuilder.getPendingTableCharacters().add(asCharacter);
            return true;
        }
    },
    InCaption {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (!token.isEndTag() || !token.asEndTag().name().equals("caption")) {
                if ((token.isStartTag() && StringUtil.m10in(token.asStartTag().name(), "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr")) || (token.isEndTag() && token.asEndTag().name().equals("table"))) {
                    htmlTreeBuilder.error(this);
                    if (htmlTreeBuilder.process(new Token.EndTag("caption"))) {
                        return htmlTreeBuilder.process(token);
                    }
                    return true;
                } else if (!token.isEndTag() || !StringUtil.m10in(token.asEndTag().name(), "body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                    return htmlTreeBuilder.process(token, InBody);
                } else {
                    htmlTreeBuilder.error(this);
                    return false;
                }
            } else if (!htmlTreeBuilder.inTableScope(token.asEndTag().name())) {
                htmlTreeBuilder.error(this);
                return false;
            } else {
                htmlTreeBuilder.generateImpliedEndTags();
                if (!htmlTreeBuilder.currentElement().nodeName().equals("caption")) {
                    htmlTreeBuilder.error(this);
                }
                htmlTreeBuilder.popStackToClose("caption");
                htmlTreeBuilder.clearFormattingElementsToLastMarker();
                htmlTreeBuilder.transition(InTable);
                return true;
            }
        }
    },
    InColumnGroup {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (HtmlTreeBuilderState.isWhitespace(token)) {
                htmlTreeBuilder.insert(token.asCharacter());
                return true;
            }
            int i = C057124.$SwitchMap$org$jsoup$parser$Token$TokenType[token.type.ordinal()];
            if (i == 1) {
                htmlTreeBuilder.insert(token.asComment());
            } else if (i == 2) {
                htmlTreeBuilder.error(this);
            } else if (i == 3) {
                Token.StartTag asStartTag = token.asStartTag();
                String name = asStartTag.name();
                if (name.equals("html")) {
                    return htmlTreeBuilder.process(token, InBody);
                }
                if (!name.equals("col")) {
                    return anythingElse(token, htmlTreeBuilder);
                }
                htmlTreeBuilder.insertEmpty(asStartTag);
            } else if (i != 4) {
                if (i != 6) {
                    return anythingElse(token, htmlTreeBuilder);
                }
                if (htmlTreeBuilder.currentElement().nodeName().equals("html")) {
                    return true;
                }
                return anythingElse(token, htmlTreeBuilder);
            } else if (!token.asEndTag().name().equals("colgroup")) {
                return anythingElse(token, htmlTreeBuilder);
            } else {
                if (htmlTreeBuilder.currentElement().nodeName().equals("html")) {
                    htmlTreeBuilder.error(this);
                    return false;
                }
                htmlTreeBuilder.pop();
                htmlTreeBuilder.transition(InTable);
            }
            return true;
        }

        private boolean anythingElse(Token token, TreeBuilder treeBuilder) {
            if (treeBuilder.process(new Token.EndTag("colgroup"))) {
                return treeBuilder.process(token);
            }
            return true;
        }
    },
    InTableBody {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            int i = C057124.$SwitchMap$org$jsoup$parser$Token$TokenType[token.type.ordinal()];
            if (i == 3) {
                Token.StartTag asStartTag = token.asStartTag();
                String name = asStartTag.name();
                if (name.equals("tr")) {
                    htmlTreeBuilder.clearStackToTableBodyContext();
                    htmlTreeBuilder.insert(asStartTag);
                    htmlTreeBuilder.transition(InRow);
                    return true;
                } else if (StringUtil.m10in(name, "th", "td")) {
                    htmlTreeBuilder.error(this);
                    htmlTreeBuilder.process(new Token.StartTag("tr"));
                    return htmlTreeBuilder.process(asStartTag);
                } else if (StringUtil.m10in(name, "caption", "col", "colgroup", "tbody", "tfoot", "thead")) {
                    return exitTableBody(token, htmlTreeBuilder);
                } else {
                    return anythingElse(token, htmlTreeBuilder);
                }
            } else if (i != 4) {
                return anythingElse(token, htmlTreeBuilder);
            } else {
                String name2 = token.asEndTag().name();
                if (StringUtil.m10in(name2, "tbody", "tfoot", "thead")) {
                    if (!htmlTreeBuilder.inTableScope(name2)) {
                        htmlTreeBuilder.error(this);
                        return false;
                    }
                    htmlTreeBuilder.clearStackToTableBodyContext();
                    htmlTreeBuilder.pop();
                    htmlTreeBuilder.transition(InTable);
                    return true;
                } else if (name2.equals("table")) {
                    return exitTableBody(token, htmlTreeBuilder);
                } else {
                    if (!StringUtil.m10in(name2, "body", "caption", "col", "colgroup", "html", "td", "th", "tr")) {
                        return anythingElse(token, htmlTreeBuilder);
                    }
                    htmlTreeBuilder.error(this);
                    return false;
                }
            }
        }

        private boolean exitTableBody(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (htmlTreeBuilder.inTableScope("tbody") || htmlTreeBuilder.inTableScope("thead") || htmlTreeBuilder.inScope("tfoot")) {
                htmlTreeBuilder.clearStackToTableBodyContext();
                htmlTreeBuilder.process(new Token.EndTag(htmlTreeBuilder.currentElement().nodeName()));
                return htmlTreeBuilder.process(token);
            }
            htmlTreeBuilder.error(this);
            return false;
        }

        private boolean anythingElse(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            return htmlTreeBuilder.process(token, InTable);
        }
    },
    InRow {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (token.isStartTag()) {
                Token.StartTag asStartTag = token.asStartTag();
                String name = asStartTag.name();
                if (StringUtil.m10in(name, "th", "td")) {
                    htmlTreeBuilder.clearStackToTableRowContext();
                    htmlTreeBuilder.insert(asStartTag);
                    htmlTreeBuilder.transition(InCell);
                    htmlTreeBuilder.insertMarkerToFormattingElements();
                    return true;
                } else if (StringUtil.m10in(name, "caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr")) {
                    return handleMissingTr(token, htmlTreeBuilder);
                } else {
                    return anythingElse(token, htmlTreeBuilder);
                }
            } else if (!token.isEndTag()) {
                return anythingElse(token, htmlTreeBuilder);
            } else {
                String name2 = token.asEndTag().name();
                if (name2.equals("tr")) {
                    if (!htmlTreeBuilder.inTableScope(name2)) {
                        htmlTreeBuilder.error(this);
                        return false;
                    }
                    htmlTreeBuilder.clearStackToTableRowContext();
                    htmlTreeBuilder.pop();
                    htmlTreeBuilder.transition(InTableBody);
                    return true;
                } else if (name2.equals("table")) {
                    return handleMissingTr(token, htmlTreeBuilder);
                } else {
                    if (StringUtil.m10in(name2, "tbody", "tfoot", "thead")) {
                        if (!htmlTreeBuilder.inTableScope(name2)) {
                            htmlTreeBuilder.error(this);
                            return false;
                        }
                        htmlTreeBuilder.process(new Token.EndTag("tr"));
                        return htmlTreeBuilder.process(token);
                    } else if (!StringUtil.m10in(name2, "body", "caption", "col", "colgroup", "html", "td", "th")) {
                        return anythingElse(token, htmlTreeBuilder);
                    } else {
                        htmlTreeBuilder.error(this);
                        return false;
                    }
                }
            }
        }

        private boolean anythingElse(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            return htmlTreeBuilder.process(token, InTable);
        }

        private boolean handleMissingTr(Token token, TreeBuilder treeBuilder) {
            if (treeBuilder.process(new Token.EndTag("tr"))) {
                return treeBuilder.process(token);
            }
            return false;
        }
    },
    InCell {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (token.isEndTag()) {
                String name = token.asEndTag().name();
                if (StringUtil.m10in(name, "td", "th")) {
                    if (!htmlTreeBuilder.inTableScope(name)) {
                        htmlTreeBuilder.error(this);
                        htmlTreeBuilder.transition(InRow);
                        return false;
                    }
                    htmlTreeBuilder.generateImpliedEndTags();
                    if (!htmlTreeBuilder.currentElement().nodeName().equals(name)) {
                        htmlTreeBuilder.error(this);
                    }
                    htmlTreeBuilder.popStackToClose(name);
                    htmlTreeBuilder.clearFormattingElementsToLastMarker();
                    htmlTreeBuilder.transition(InRow);
                    return true;
                } else if (StringUtil.m10in(name, "body", "caption", "col", "colgroup", "html")) {
                    htmlTreeBuilder.error(this);
                    return false;
                } else if (!StringUtil.m10in(name, "table", "tbody", "tfoot", "thead", "tr")) {
                    return anythingElse(token, htmlTreeBuilder);
                } else {
                    if (!htmlTreeBuilder.inTableScope(name)) {
                        htmlTreeBuilder.error(this);
                        return false;
                    }
                    closeCell(htmlTreeBuilder);
                    return htmlTreeBuilder.process(token);
                }
            } else if (!token.isStartTag() || !StringUtil.m10in(token.asStartTag().name(), "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                return anythingElse(token, htmlTreeBuilder);
            } else {
                if (htmlTreeBuilder.inTableScope("td") || htmlTreeBuilder.inTableScope("th")) {
                    closeCell(htmlTreeBuilder);
                    return htmlTreeBuilder.process(token);
                }
                htmlTreeBuilder.error(this);
                return false;
            }
        }

        private boolean anythingElse(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            return htmlTreeBuilder.process(token, InBody);
        }

        private void closeCell(HtmlTreeBuilder htmlTreeBuilder) {
            if (htmlTreeBuilder.inTableScope("td")) {
                htmlTreeBuilder.process(new Token.EndTag("td"));
            } else {
                htmlTreeBuilder.process(new Token.EndTag("th"));
            }
        }
    },
    InSelect {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            switch (C057124.$SwitchMap$org$jsoup$parser$Token$TokenType[token.type.ordinal()]) {
                case 1:
                    htmlTreeBuilder.insert(token.asComment());
                    return true;
                case 2:
                    htmlTreeBuilder.error(this);
                    return false;
                case 3:
                    Token.StartTag asStartTag = token.asStartTag();
                    String name = asStartTag.name();
                    if (name.equals("html")) {
                        return htmlTreeBuilder.process(asStartTag, InBody);
                    }
                    if (name.equals("option")) {
                        htmlTreeBuilder.process(new Token.EndTag("option"));
                        htmlTreeBuilder.insert(asStartTag);
                        return true;
                    } else if (name.equals("optgroup")) {
                        if (htmlTreeBuilder.currentElement().nodeName().equals("option")) {
                            htmlTreeBuilder.process(new Token.EndTag("option"));
                        } else if (htmlTreeBuilder.currentElement().nodeName().equals("optgroup")) {
                            htmlTreeBuilder.process(new Token.EndTag("optgroup"));
                        }
                        htmlTreeBuilder.insert(asStartTag);
                        return true;
                    } else if (name.equals("select")) {
                        htmlTreeBuilder.error(this);
                        return htmlTreeBuilder.process(new Token.EndTag("select"));
                    } else if (StringUtil.m10in(name, "input", "keygen", "textarea")) {
                        htmlTreeBuilder.error(this);
                        if (!htmlTreeBuilder.inSelectScope("select")) {
                            return false;
                        }
                        htmlTreeBuilder.process(new Token.EndTag("select"));
                        return htmlTreeBuilder.process(asStartTag);
                    } else if (name.equals("script")) {
                        return htmlTreeBuilder.process(token, InHead);
                    } else {
                        return anythingElse(token, htmlTreeBuilder);
                    }
                case 4:
                    String name2 = token.asEndTag().name();
                    if (name2.equals("optgroup")) {
                        if (htmlTreeBuilder.currentElement().nodeName().equals("option") && htmlTreeBuilder.aboveOnStack(htmlTreeBuilder.currentElement()) != null && htmlTreeBuilder.aboveOnStack(htmlTreeBuilder.currentElement()).nodeName().equals("optgroup")) {
                            htmlTreeBuilder.process(new Token.EndTag("option"));
                        }
                        if (htmlTreeBuilder.currentElement().nodeName().equals("optgroup")) {
                            htmlTreeBuilder.pop();
                            return true;
                        }
                        htmlTreeBuilder.error(this);
                        return true;
                    } else if (name2.equals("option")) {
                        if (htmlTreeBuilder.currentElement().nodeName().equals("option")) {
                            htmlTreeBuilder.pop();
                            return true;
                        }
                        htmlTreeBuilder.error(this);
                        return true;
                    } else if (!name2.equals("select")) {
                        return anythingElse(token, htmlTreeBuilder);
                    } else {
                        if (!htmlTreeBuilder.inSelectScope(name2)) {
                            htmlTreeBuilder.error(this);
                            return false;
                        }
                        htmlTreeBuilder.popStackToClose(name2);
                        htmlTreeBuilder.resetInsertionMode();
                        return true;
                    }
                case 5:
                    Token.Character asCharacter = token.asCharacter();
                    if (asCharacter.getData().equals(HtmlTreeBuilderState.nullString)) {
                        htmlTreeBuilder.error(this);
                        return false;
                    }
                    htmlTreeBuilder.insert(asCharacter);
                    return true;
                case 6:
                    if (htmlTreeBuilder.currentElement().nodeName().equals("html")) {
                        return true;
                    }
                    htmlTreeBuilder.error(this);
                    return true;
                default:
                    return anythingElse(token, htmlTreeBuilder);
            }
        }

        private boolean anythingElse(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            htmlTreeBuilder.error(this);
            return false;
        }
    },
    InSelectInTable {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (token.isStartTag() && StringUtil.m10in(token.asStartTag().name(), "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th")) {
                htmlTreeBuilder.error(this);
                htmlTreeBuilder.process(new Token.EndTag("select"));
                return htmlTreeBuilder.process(token);
            } else if (!token.isEndTag() || !StringUtil.m10in(token.asEndTag().name(), "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th")) {
                return htmlTreeBuilder.process(token, InSelect);
            } else {
                htmlTreeBuilder.error(this);
                if (!htmlTreeBuilder.inTableScope(token.asEndTag().name())) {
                    return false;
                }
                htmlTreeBuilder.process(new Token.EndTag("select"));
                return htmlTreeBuilder.process(token);
            }
        }
    },
    AfterBody {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (HtmlTreeBuilderState.isWhitespace(token)) {
                return htmlTreeBuilder.process(token, InBody);
            }
            if (token.isComment()) {
                htmlTreeBuilder.insert(token.asComment());
                return true;
            } else if (token.isDoctype()) {
                htmlTreeBuilder.error(this);
                return false;
            } else if (token.isStartTag() && token.asStartTag().name().equals("html")) {
                return htmlTreeBuilder.process(token, InBody);
            } else {
                if (!token.isEndTag() || !token.asEndTag().name().equals("html")) {
                    if (token.isEOF()) {
                        return true;
                    }
                    htmlTreeBuilder.error(this);
                    htmlTreeBuilder.transition(InBody);
                    return htmlTreeBuilder.process(token);
                } else if (htmlTreeBuilder.isFragmentParsing()) {
                    htmlTreeBuilder.error(this);
                    return false;
                } else {
                    htmlTreeBuilder.transition(AfterAfterBody);
                    return true;
                }
            }
        }
    },
    InFrameset {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (HtmlTreeBuilderState.isWhitespace(token)) {
                htmlTreeBuilder.insert(token.asCharacter());
            } else if (token.isComment()) {
                htmlTreeBuilder.insert(token.asComment());
            } else if (token.isDoctype()) {
                htmlTreeBuilder.error(this);
                return false;
            } else if (token.isStartTag()) {
                Token.StartTag asStartTag = token.asStartTag();
                String name = asStartTag.name();
                if (name.equals("html")) {
                    return htmlTreeBuilder.process(asStartTag, InBody);
                }
                if (name.equals("frameset")) {
                    htmlTreeBuilder.insert(asStartTag);
                } else if (name.equals("frame")) {
                    htmlTreeBuilder.insertEmpty(asStartTag);
                } else if (name.equals("noframes")) {
                    return htmlTreeBuilder.process(asStartTag, InHead);
                } else {
                    htmlTreeBuilder.error(this);
                    return false;
                }
            } else if (!token.isEndTag() || !token.asEndTag().name().equals("frameset")) {
                if (!token.isEOF()) {
                    htmlTreeBuilder.error(this);
                    return false;
                } else if (!htmlTreeBuilder.currentElement().nodeName().equals("html")) {
                    htmlTreeBuilder.error(this);
                }
            } else if (htmlTreeBuilder.currentElement().nodeName().equals("html")) {
                htmlTreeBuilder.error(this);
                return false;
            } else {
                htmlTreeBuilder.pop();
                if (!htmlTreeBuilder.isFragmentParsing() && !htmlTreeBuilder.currentElement().nodeName().equals("frameset")) {
                    htmlTreeBuilder.transition(AfterFrameset);
                }
            }
            return true;
        }
    },
    AfterFrameset {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (HtmlTreeBuilderState.isWhitespace(token)) {
                htmlTreeBuilder.insert(token.asCharacter());
                return true;
            } else if (token.isComment()) {
                htmlTreeBuilder.insert(token.asComment());
                return true;
            } else if (token.isDoctype()) {
                htmlTreeBuilder.error(this);
                return false;
            } else if (token.isStartTag() && token.asStartTag().name().equals("html")) {
                return htmlTreeBuilder.process(token, InBody);
            } else {
                if (token.isEndTag() && token.asEndTag().name().equals("html")) {
                    htmlTreeBuilder.transition(AfterAfterFrameset);
                    return true;
                } else if (token.isStartTag() && token.asStartTag().name().equals("noframes")) {
                    return htmlTreeBuilder.process(token, InHead);
                } else {
                    if (token.isEOF()) {
                        return true;
                    }
                    htmlTreeBuilder.error(this);
                    return false;
                }
            }
        }
    },
    AfterAfterBody {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (token.isComment()) {
                htmlTreeBuilder.insert(token.asComment());
                return true;
            } else if (token.isDoctype() || HtmlTreeBuilderState.isWhitespace(token) || (token.isStartTag() && token.asStartTag().name().equals("html"))) {
                return htmlTreeBuilder.process(token, InBody);
            } else {
                if (token.isEOF()) {
                    return true;
                }
                htmlTreeBuilder.error(this);
                htmlTreeBuilder.transition(InBody);
                return htmlTreeBuilder.process(token);
            }
        }
    },
    AfterAfterFrameset {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            if (token.isComment()) {
                htmlTreeBuilder.insert(token.asComment());
                return true;
            } else if (token.isDoctype() || HtmlTreeBuilderState.isWhitespace(token) || (token.isStartTag() && token.asStartTag().name().equals("html"))) {
                return htmlTreeBuilder.process(token, InBody);
            } else {
                if (token.isEOF()) {
                    return true;
                }
                if (token.isStartTag() && token.asStartTag().name().equals("noframes")) {
                    return htmlTreeBuilder.process(token, InHead);
                }
                htmlTreeBuilder.error(this);
                return false;
            }
        }
    },
    ForeignContent {
        /* access modifiers changed from: package-private */
        public boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder) {
            return true;
        }
    };
    
    /* access modifiers changed from: private */
    public static String nullString;

    /* access modifiers changed from: package-private */
    public abstract boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder);

    static {
        nullString = String.valueOf(0);
    }

    /* renamed from: org.jsoup.parser.HtmlTreeBuilderState$24 */
    static /* synthetic */ class C057124 {
        static final /* synthetic */ int[] $SwitchMap$org$jsoup$parser$Token$TokenType = null;

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
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.Comment     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$org$jsoup$parser$Token$TokenType     // Catch:{ NoSuchFieldError -> 0x001d }
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.Doctype     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$org$jsoup$parser$Token$TokenType     // Catch:{ NoSuchFieldError -> 0x0028 }
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.StartTag     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$org$jsoup$parser$Token$TokenType     // Catch:{ NoSuchFieldError -> 0x0033 }
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.EndTag     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$org$jsoup$parser$Token$TokenType     // Catch:{ NoSuchFieldError -> 0x003e }
                org.jsoup.parser.Token$TokenType r1 = org.jsoup.parser.Token.TokenType.Character     // Catch:{ NoSuchFieldError -> 0x003e }
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
            throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.HtmlTreeBuilderState.C057124.<clinit>():void");
        }
    }

    /* access modifiers changed from: private */
    public static boolean isWhitespace(Token token) {
        if (!token.isCharacter()) {
            return false;
        }
        String data = token.asCharacter().getData();
        for (int i = 0; i < data.length(); i++) {
            if (!StringUtil.isWhitespace(data.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static void handleRcData(Token.StartTag startTag, HtmlTreeBuilder htmlTreeBuilder) {
        htmlTreeBuilder.insert(startTag);
        htmlTreeBuilder.tokeniser.transition(TokeniserState.Rcdata);
        htmlTreeBuilder.markInsertionMode();
        htmlTreeBuilder.transition(Text);
    }

    /* access modifiers changed from: private */
    public static void handleRawtext(Token.StartTag startTag, HtmlTreeBuilder htmlTreeBuilder) {
        htmlTreeBuilder.insert(startTag);
        htmlTreeBuilder.tokeniser.transition(TokeniserState.Rawtext);
        htmlTreeBuilder.markInsertionMode();
        htmlTreeBuilder.transition(Text);
    }

    private static final class Constants {
        /* access modifiers changed from: private */
        public static final String[] DdDt = null;
        /* access modifiers changed from: private */
        public static final String[] Formatters = null;
        /* access modifiers changed from: private */
        public static final String[] Headings = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyEndAdoptionFormatters = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyEndClosers = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyEndTableFosters = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartApplets = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartDrop = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartEmptyFormatters = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartInputAttribs = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartLiBreakers = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartMedia = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartOptions = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartPClosers = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartPreListing = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartRuby = null;
        /* access modifiers changed from: private */
        public static final String[] InBodyStartToHead = null;

        private Constants() {
        }

        static {
            InBodyStartToHead = new String[]{"base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", "title"};
            InBodyStartPClosers = new String[]{"address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", "p", "section", "summary", "ul"};
            Headings = new String[]{"h1", "h2", "h3", "h4", "h5", "h6"};
            InBodyStartPreListing = new String[]{"pre", "listing"};
            InBodyStartLiBreakers = new String[]{"address", "div", "p"};
            DdDt = new String[]{"dd", "dt"};
            Formatters = new String[]{"b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", "tt", "u"};
            InBodyStartApplets = new String[]{"applet", "marquee", "object"};
            InBodyStartEmptyFormatters = new String[]{"area", "br", "embed", "img", "keygen", "wbr"};
            InBodyStartMedia = new String[]{"param", "source", "track"};
            InBodyStartInputAttribs = new String[]{"name", "action", "prompt"};
            InBodyStartOptions = new String[]{"optgroup", "option"};
            InBodyStartRuby = new String[]{"rp", "rt"};
            InBodyStartDrop = new String[]{"caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", "tr"};
            InBodyEndClosers = new String[]{"address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", "pre", "section", "summary", "ul"};
            InBodyEndAdoptionFormatters = new String[]{"a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", "strike", "strong", "tt", "u"};
            InBodyEndTableFosters = new String[]{"table", "tbody", "tfoot", "thead", "tr"};
        }
    }
}
