package org.jsoup.parser;

import org.jsoup.parser.Token;

enum TokeniserState {
    Data {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char current = characterReader.current();
            if (current == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.emit(characterReader.consume());
            } else if (current == '&') {
                tokeniser.advanceTransition(CharacterReferenceInData);
            } else if (current == '<') {
                tokeniser.advanceTransition(TagOpen);
            } else if (current != 65535) {
                tokeniser.emit(characterReader.consumeToAny('&', '<', TokeniserState.nullChar));
            } else {
                tokeniser.emit((Token) new Token.EOF());
            }
        }
    },
    CharacterReferenceInData {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char[] consumeCharacterReference = tokeniser.consumeCharacterReference((Character) null, false);
            if (consumeCharacterReference == null) {
                tokeniser.emit('&');
            } else {
                tokeniser.emit(consumeCharacterReference);
            }
            tokeniser.transition(Data);
        }
    },
    Rcdata {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char current = characterReader.current();
            if (current == 0) {
                tokeniser.error((TokeniserState) this);
                characterReader.advance();
                tokeniser.emit((char) TokeniserState.replacementChar);
            } else if (current == '&') {
                tokeniser.advanceTransition(CharacterReferenceInRcdata);
            } else if (current == '<') {
                tokeniser.advanceTransition(RcdataLessthanSign);
            } else if (current != 65535) {
                tokeniser.emit(characterReader.consumeToAny('&', '<', TokeniserState.nullChar));
            } else {
                tokeniser.emit((Token) new Token.EOF());
            }
        }
    },
    CharacterReferenceInRcdata {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char[] consumeCharacterReference = tokeniser.consumeCharacterReference((Character) null, false);
            if (consumeCharacterReference == null) {
                tokeniser.emit('&');
            } else {
                tokeniser.emit(consumeCharacterReference);
            }
            tokeniser.transition(Rcdata);
        }
    },
    Rawtext {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char current = characterReader.current();
            if (current == 0) {
                tokeniser.error((TokeniserState) this);
                characterReader.advance();
                tokeniser.emit((char) TokeniserState.replacementChar);
            } else if (current == '<') {
                tokeniser.advanceTransition(RawtextLessthanSign);
            } else if (current != 65535) {
                tokeniser.emit(characterReader.consumeToAny('<', TokeniserState.nullChar));
            } else {
                tokeniser.emit((Token) new Token.EOF());
            }
        }
    },
    ScriptData {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char current = characterReader.current();
            if (current == 0) {
                tokeniser.error((TokeniserState) this);
                characterReader.advance();
                tokeniser.emit((char) TokeniserState.replacementChar);
            } else if (current == '<') {
                tokeniser.advanceTransition(ScriptDataLessthanSign);
            } else if (current != 65535) {
                tokeniser.emit(characterReader.consumeToAny('<', TokeniserState.nullChar));
            } else {
                tokeniser.emit((Token) new Token.EOF());
            }
        }
    },
    PLAINTEXT {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char current = characterReader.current();
            if (current == 0) {
                tokeniser.error((TokeniserState) this);
                characterReader.advance();
                tokeniser.emit((char) TokeniserState.replacementChar);
            } else if (current != 65535) {
                tokeniser.emit(characterReader.consumeTo((char) TokeniserState.nullChar));
            } else {
                tokeniser.emit((Token) new Token.EOF());
            }
        }
    },
    TagOpen {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char current = characterReader.current();
            if (current == '!') {
                tokeniser.advanceTransition(MarkupDeclarationOpen);
            } else if (current == '/') {
                tokeniser.advanceTransition(EndTagOpen);
            } else if (current == '?') {
                tokeniser.advanceTransition(BogusComment);
            } else if (characterReader.matchesLetter()) {
                tokeniser.createTagPending(true);
                tokeniser.transition(TagName);
            } else {
                tokeniser.error((TokeniserState) this);
                tokeniser.emit('<');
                tokeniser.transition(Data);
            }
        }
    },
    EndTagOpen {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.isEmpty()) {
                tokeniser.eofError(this);
                tokeniser.emit("</");
                tokeniser.transition(Data);
            } else if (characterReader.matchesLetter()) {
                tokeniser.createTagPending(false);
                tokeniser.transition(TagName);
            } else if (characterReader.matches('>')) {
                tokeniser.error((TokeniserState) this);
                tokeniser.advanceTransition(Data);
            } else {
                tokeniser.error((TokeniserState) this);
                tokeniser.advanceTransition(BogusComment);
            }
        }
    },
    TagName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            tokeniser.tagPending.appendTagName(characterReader.consumeToAny(9, 10, 13, 12, ' ', '/', '>', TokeniserState.nullChar).toLowerCase());
            char consume = characterReader.consume();
            if (consume != 0) {
                if (consume != ' ') {
                    if (consume == '/') {
                        tokeniser.transition(SelfClosingStartTag);
                        return;
                    } else if (consume == '>') {
                        tokeniser.emitTagPending();
                        tokeniser.transition(Data);
                        return;
                    } else if (consume == 65535) {
                        tokeniser.eofError(this);
                        tokeniser.transition(Data);
                        return;
                    } else if (!(consume == 9 || consume == 10 || consume == 12 || consume == 13)) {
                        return;
                    }
                }
                tokeniser.transition(BeforeAttributeName);
                return;
            }
            tokeniser.tagPending.appendTagName(TokeniserState.replacementStr);
        }
    },
    RcdataLessthanSign {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matches('/')) {
                tokeniser.createTempBuffer();
                tokeniser.advanceTransition(RCDATAEndTagOpen);
            } else if (!characterReader.matchesLetter() || tokeniser.appropriateEndTagName() == null || characterReader.containsIgnoreCase("</" + tokeniser.appropriateEndTagName())) {
                tokeniser.emit("<");
                tokeniser.transition(Rcdata);
            } else {
                tokeniser.tagPending = new Token.EndTag(tokeniser.appropriateEndTagName());
                tokeniser.emitTagPending();
                characterReader.unconsume();
                tokeniser.transition(Data);
            }
        }
    },
    RCDATAEndTagOpen {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matchesLetter()) {
                tokeniser.createTagPending(false);
                tokeniser.tagPending.appendTagName(Character.toLowerCase(characterReader.current()));
                tokeniser.dataBuffer.append(Character.toLowerCase(characterReader.current()));
                tokeniser.advanceTransition(RCDATAEndTagName);
                return;
            }
            tokeniser.emit("</");
            tokeniser.transition(Rcdata);
        }
    },
    RCDATAEndTagName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matchesLetter()) {
                String consumeLetterSequence = characterReader.consumeLetterSequence();
                tokeniser.tagPending.appendTagName(consumeLetterSequence.toLowerCase());
                tokeniser.dataBuffer.append(consumeLetterSequence);
                return;
            }
            char consume = characterReader.consume();
            if (consume == 9 || consume == 10 || consume == 12 || consume == 13 || consume == ' ') {
                if (tokeniser.isAppropriateEndTagToken()) {
                    tokeniser.transition(BeforeAttributeName);
                } else {
                    anythingElse(tokeniser, characterReader);
                }
            } else if (consume != '/') {
                if (consume != '>') {
                    anythingElse(tokeniser, characterReader);
                } else if (tokeniser.isAppropriateEndTagToken()) {
                    tokeniser.emitTagPending();
                    tokeniser.transition(Data);
                } else {
                    anythingElse(tokeniser, characterReader);
                }
            } else if (tokeniser.isAppropriateEndTagToken()) {
                tokeniser.transition(SelfClosingStartTag);
            } else {
                anythingElse(tokeniser, characterReader);
            }
        }

        private void anythingElse(Tokeniser tokeniser, CharacterReader characterReader) {
            tokeniser.emit("</" + tokeniser.dataBuffer.toString());
            characterReader.unconsume();
            tokeniser.transition(Rcdata);
        }
    },
    RawtextLessthanSign {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matches('/')) {
                tokeniser.createTempBuffer();
                tokeniser.advanceTransition(RawtextEndTagOpen);
                return;
            }
            tokeniser.emit('<');
            tokeniser.transition(Rawtext);
        }
    },
    RawtextEndTagOpen {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matchesLetter()) {
                tokeniser.createTagPending(false);
                tokeniser.transition(RawtextEndTagName);
                return;
            }
            tokeniser.emit("</");
            tokeniser.transition(Rawtext);
        }
    },
    RawtextEndTagName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            TokeniserState.handleDataEndTag(tokeniser, characterReader, Rawtext);
        }
    },
    ScriptDataLessthanSign {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == '!') {
                tokeniser.emit("<!");
                tokeniser.transition(ScriptDataEscapeStart);
            } else if (consume != '/') {
                tokeniser.emit("<");
                characterReader.unconsume();
                tokeniser.transition(ScriptData);
            } else {
                tokeniser.createTempBuffer();
                tokeniser.transition(ScriptDataEndTagOpen);
            }
        }
    },
    ScriptDataEndTagOpen {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matchesLetter()) {
                tokeniser.createTagPending(false);
                tokeniser.transition(ScriptDataEndTagName);
                return;
            }
            tokeniser.emit("</");
            tokeniser.transition(ScriptData);
        }
    },
    ScriptDataEndTagName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            TokeniserState.handleDataEndTag(tokeniser, characterReader, ScriptData);
        }
    },
    ScriptDataEscapeStart {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matches('-')) {
                tokeniser.emit('-');
                tokeniser.advanceTransition(ScriptDataEscapeStartDash);
                return;
            }
            tokeniser.transition(ScriptData);
        }
    },
    ScriptDataEscapeStartDash {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matches('-')) {
                tokeniser.emit('-');
                tokeniser.advanceTransition(ScriptDataEscapedDashDash);
                return;
            }
            tokeniser.transition(ScriptData);
        }
    },
    ScriptDataEscaped {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.isEmpty()) {
                tokeniser.eofError(this);
                tokeniser.transition(Data);
                return;
            }
            char current = characterReader.current();
            if (current == 0) {
                tokeniser.error((TokeniserState) this);
                characterReader.advance();
                tokeniser.emit((char) TokeniserState.replacementChar);
            } else if (current == '-') {
                tokeniser.emit('-');
                tokeniser.advanceTransition(ScriptDataEscapedDash);
            } else if (current != '<') {
                tokeniser.emit(characterReader.consumeToAny('-', '<', TokeniserState.nullChar));
            } else {
                tokeniser.advanceTransition(ScriptDataEscapedLessthanSign);
            }
        }
    },
    ScriptDataEscapedDash {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.isEmpty()) {
                tokeniser.eofError(this);
                tokeniser.transition(Data);
                return;
            }
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.emit((char) TokeniserState.replacementChar);
                tokeniser.transition(ScriptDataEscaped);
            } else if (consume == '-') {
                tokeniser.emit(consume);
                tokeniser.transition(ScriptDataEscapedDashDash);
            } else if (consume != '<') {
                tokeniser.emit(consume);
                tokeniser.transition(ScriptDataEscaped);
            } else {
                tokeniser.transition(ScriptDataEscapedLessthanSign);
            }
        }
    },
    ScriptDataEscapedDashDash {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.isEmpty()) {
                tokeniser.eofError(this);
                tokeniser.transition(Data);
                return;
            }
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.emit((char) TokeniserState.replacementChar);
                tokeniser.transition(ScriptDataEscaped);
            } else if (consume == '-') {
                tokeniser.emit(consume);
            } else if (consume == '<') {
                tokeniser.transition(ScriptDataEscapedLessthanSign);
            } else if (consume != '>') {
                tokeniser.emit(consume);
                tokeniser.transition(ScriptDataEscaped);
            } else {
                tokeniser.emit(consume);
                tokeniser.transition(ScriptData);
            }
        }
    },
    ScriptDataEscapedLessthanSign {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matchesLetter()) {
                tokeniser.createTempBuffer();
                tokeniser.dataBuffer.append(Character.toLowerCase(characterReader.current()));
                tokeniser.emit("<" + characterReader.current());
                tokeniser.advanceTransition(ScriptDataDoubleEscapeStart);
            } else if (characterReader.matches('/')) {
                tokeniser.createTempBuffer();
                tokeniser.advanceTransition(ScriptDataEscapedEndTagOpen);
            } else {
                tokeniser.emit('<');
                tokeniser.transition(ScriptDataEscaped);
            }
        }
    },
    ScriptDataEscapedEndTagOpen {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matchesLetter()) {
                tokeniser.createTagPending(false);
                tokeniser.tagPending.appendTagName(Character.toLowerCase(characterReader.current()));
                tokeniser.dataBuffer.append(characterReader.current());
                tokeniser.advanceTransition(ScriptDataEscapedEndTagName);
                return;
            }
            tokeniser.emit("</");
            tokeniser.transition(ScriptDataEscaped);
        }
    },
    ScriptDataEscapedEndTagName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            TokeniserState.handleDataEndTag(tokeniser, characterReader, ScriptDataEscaped);
        }
    },
    ScriptDataDoubleEscapeStart {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            TokeniserState.handleDataDoubleEscapeTag(tokeniser, characterReader, ScriptDataDoubleEscaped, ScriptDataEscaped);
        }
    },
    ScriptDataDoubleEscaped {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char current = characterReader.current();
            if (current == 0) {
                tokeniser.error((TokeniserState) this);
                characterReader.advance();
                tokeniser.emit((char) TokeniserState.replacementChar);
            } else if (current == '-') {
                tokeniser.emit(current);
                tokeniser.advanceTransition(ScriptDataDoubleEscapedDash);
            } else if (current == '<') {
                tokeniser.emit(current);
                tokeniser.advanceTransition(ScriptDataDoubleEscapedLessthanSign);
            } else if (current != 65535) {
                tokeniser.emit(characterReader.consumeToAny('-', '<', TokeniserState.nullChar));
            } else {
                tokeniser.eofError(this);
                tokeniser.transition(Data);
            }
        }
    },
    ScriptDataDoubleEscapedDash {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.emit((char) TokeniserState.replacementChar);
                tokeniser.transition(ScriptDataDoubleEscaped);
            } else if (consume == '-') {
                tokeniser.emit(consume);
                tokeniser.transition(ScriptDataDoubleEscapedDashDash);
            } else if (consume == '<') {
                tokeniser.emit(consume);
                tokeniser.transition(ScriptDataDoubleEscapedLessthanSign);
            } else if (consume != 65535) {
                tokeniser.emit(consume);
                tokeniser.transition(ScriptDataDoubleEscaped);
            } else {
                tokeniser.eofError(this);
                tokeniser.transition(Data);
            }
        }
    },
    ScriptDataDoubleEscapedDashDash {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.emit((char) TokeniserState.replacementChar);
                tokeniser.transition(ScriptDataDoubleEscaped);
            } else if (consume == '-') {
                tokeniser.emit(consume);
            } else if (consume == '<') {
                tokeniser.emit(consume);
                tokeniser.transition(ScriptDataDoubleEscapedLessthanSign);
            } else if (consume == '>') {
                tokeniser.emit(consume);
                tokeniser.transition(ScriptData);
            } else if (consume != 65535) {
                tokeniser.emit(consume);
                tokeniser.transition(ScriptDataDoubleEscaped);
            } else {
                tokeniser.eofError(this);
                tokeniser.transition(Data);
            }
        }
    },
    ScriptDataDoubleEscapedLessthanSign {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matches('/')) {
                tokeniser.emit('/');
                tokeniser.createTempBuffer();
                tokeniser.advanceTransition(ScriptDataDoubleEscapeEnd);
                return;
            }
            tokeniser.transition(ScriptDataDoubleEscaped);
        }
    },
    ScriptDataDoubleEscapeEnd {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            TokeniserState.handleDataDoubleEscapeTag(tokeniser, characterReader, ScriptDataEscaped, ScriptDataDoubleEscaped);
        }
    },
    BeforeAttributeName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.tagPending.newAttribute();
                characterReader.unconsume();
                tokeniser.transition(AttributeName);
            } else if (consume != ' ') {
                if (!(consume == '\"' || consume == '\'')) {
                    if (consume == '/') {
                        tokeniser.transition(SelfClosingStartTag);
                        return;
                    } else if (consume == 65535) {
                        tokeniser.eofError(this);
                        tokeniser.transition(Data);
                        return;
                    } else if (consume != 9 && consume != 10 && consume != 12 && consume != 13) {
                        switch (consume) {
                            case '<':
                            case '=':
                                break;
                            case '>':
                                tokeniser.emitTagPending();
                                tokeniser.transition(Data);
                                return;
                            default:
                                tokeniser.tagPending.newAttribute();
                                characterReader.unconsume();
                                tokeniser.transition(AttributeName);
                                return;
                        }
                    } else {
                        return;
                    }
                }
                tokeniser.error((TokeniserState) this);
                tokeniser.tagPending.newAttribute();
                tokeniser.tagPending.appendAttributeName(consume);
                tokeniser.transition(AttributeName);
            }
        }
    },
    AttributeName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            tokeniser.tagPending.appendAttributeName(characterReader.consumeToAny(9, 10, 13, 12, ' ', '/', '=', '>', TokeniserState.nullChar, '\"', '\'', '<').toLowerCase());
            char consume = characterReader.consume();
            if (consume != 0) {
                if (consume != ' ') {
                    if (!(consume == '\"' || consume == '\'')) {
                        if (consume == '/') {
                            tokeniser.transition(SelfClosingStartTag);
                            return;
                        } else if (consume == 65535) {
                            tokeniser.eofError(this);
                            tokeniser.transition(Data);
                            return;
                        } else if (!(consume == 9 || consume == 10 || consume == 12 || consume == 13)) {
                            switch (consume) {
                                case '<':
                                    break;
                                case '=':
                                    tokeniser.transition(BeforeAttributeValue);
                                    return;
                                case '>':
                                    tokeniser.emitTagPending();
                                    tokeniser.transition(Data);
                                    return;
                                default:
                                    return;
                            }
                        }
                    }
                    tokeniser.error((TokeniserState) this);
                    tokeniser.tagPending.appendAttributeName(consume);
                    return;
                }
                tokeniser.transition(AfterAttributeName);
                return;
            }
            tokeniser.error((TokeniserState) this);
            tokeniser.tagPending.appendAttributeName((char) TokeniserState.replacementChar);
        }
    },
    AfterAttributeName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.tagPending.appendAttributeName((char) TokeniserState.replacementChar);
                tokeniser.transition(AttributeName);
            } else if (consume != ' ') {
                if (!(consume == '\"' || consume == '\'')) {
                    if (consume == '/') {
                        tokeniser.transition(SelfClosingStartTag);
                        return;
                    } else if (consume == 65535) {
                        tokeniser.eofError(this);
                        tokeniser.transition(Data);
                        return;
                    } else if (consume != 9 && consume != 10 && consume != 12 && consume != 13) {
                        switch (consume) {
                            case '<':
                                break;
                            case '=':
                                tokeniser.transition(BeforeAttributeValue);
                                return;
                            case '>':
                                tokeniser.emitTagPending();
                                tokeniser.transition(Data);
                                return;
                            default:
                                tokeniser.tagPending.newAttribute();
                                characterReader.unconsume();
                                tokeniser.transition(AttributeName);
                                return;
                        }
                    } else {
                        return;
                    }
                }
                tokeniser.error((TokeniserState) this);
                tokeniser.tagPending.newAttribute();
                tokeniser.tagPending.appendAttributeName(consume);
                tokeniser.transition(AttributeName);
            }
        }
    },
    BeforeAttributeValue {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
                tokeniser.transition(AttributeValue_unquoted);
            } else if (consume == ' ') {
            } else {
                if (consume != '\"') {
                    if (consume != '`') {
                        if (consume == 65535) {
                            tokeniser.eofError(this);
                            tokeniser.transition(Data);
                            return;
                        } else if (consume != 9 && consume != 10 && consume != 12 && consume != 13) {
                            if (consume == '&') {
                                characterReader.unconsume();
                                tokeniser.transition(AttributeValue_unquoted);
                                return;
                            } else if (consume != '\'') {
                                switch (consume) {
                                    case '<':
                                    case '=':
                                        break;
                                    case '>':
                                        tokeniser.error((TokeniserState) this);
                                        tokeniser.emitTagPending();
                                        tokeniser.transition(Data);
                                        return;
                                    default:
                                        characterReader.unconsume();
                                        tokeniser.transition(AttributeValue_unquoted);
                                        return;
                                }
                            } else {
                                tokeniser.transition(AttributeValue_singleQuoted);
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                    tokeniser.error((TokeniserState) this);
                    tokeniser.tagPending.appendAttributeValue(consume);
                    tokeniser.transition(AttributeValue_unquoted);
                    return;
                }
                tokeniser.transition(AttributeValue_doubleQuoted);
            }
        }
    },
    AttributeValue_doubleQuoted {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            String consumeToAny = characterReader.consumeToAny('\"', '&', TokeniserState.nullChar);
            if (consumeToAny.length() > 0) {
                tokeniser.tagPending.appendAttributeValue(consumeToAny);
            }
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
            } else if (consume == '\"') {
                tokeniser.transition(AfterAttributeValue_quoted);
            } else if (consume == '&') {
                char[] consumeCharacterReference = tokeniser.consumeCharacterReference('\"', true);
                if (consumeCharacterReference != null) {
                    tokeniser.tagPending.appendAttributeValue(consumeCharacterReference);
                } else {
                    tokeniser.tagPending.appendAttributeValue('&');
                }
            } else if (consume == 65535) {
                tokeniser.eofError(this);
                tokeniser.transition(Data);
            }
        }
    },
    AttributeValue_singleQuoted {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            String consumeToAny = characterReader.consumeToAny('\'', '&', TokeniserState.nullChar);
            if (consumeToAny.length() > 0) {
                tokeniser.tagPending.appendAttributeValue(consumeToAny);
            }
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
            } else if (consume == 65535) {
                tokeniser.eofError(this);
                tokeniser.transition(Data);
            } else if (consume == '&') {
                char[] consumeCharacterReference = tokeniser.consumeCharacterReference('\'', true);
                if (consumeCharacterReference != null) {
                    tokeniser.tagPending.appendAttributeValue(consumeCharacterReference);
                } else {
                    tokeniser.tagPending.appendAttributeValue('&');
                }
            } else if (consume == '\'') {
                tokeniser.transition(AfterAttributeValue_quoted);
            }
        }
    },
    AttributeValue_unquoted {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            String consumeToAny = characterReader.consumeToAny(9, 10, 13, 12, ' ', '&', '>', TokeniserState.nullChar, '\"', '\'', '<', '=', '`');
            if (consumeToAny.length() > 0) {
                tokeniser.tagPending.appendAttributeValue(consumeToAny);
            }
            char consume = characterReader.consume();
            if (consume != 0) {
                if (consume != ' ') {
                    if (!(consume == '\"' || consume == '`')) {
                        if (consume == 65535) {
                            tokeniser.eofError(this);
                            tokeniser.transition(Data);
                            return;
                        } else if (!(consume == 9 || consume == 10 || consume == 12 || consume == 13)) {
                            if (consume == '&') {
                                char[] consumeCharacterReference = tokeniser.consumeCharacterReference('>', true);
                                if (consumeCharacterReference != null) {
                                    tokeniser.tagPending.appendAttributeValue(consumeCharacterReference);
                                    return;
                                } else {
                                    tokeniser.tagPending.appendAttributeValue('&');
                                    return;
                                }
                            } else if (consume != '\'') {
                                switch (consume) {
                                    case '<':
                                    case '=':
                                        break;
                                    case '>':
                                        tokeniser.emitTagPending();
                                        tokeniser.transition(Data);
                                        return;
                                    default:
                                        return;
                                }
                            }
                        }
                    }
                    tokeniser.error((TokeniserState) this);
                    tokeniser.tagPending.appendAttributeValue(consume);
                    return;
                }
                tokeniser.transition(BeforeAttributeName);
                return;
            }
            tokeniser.error((TokeniserState) this);
            tokeniser.tagPending.appendAttributeValue((char) TokeniserState.replacementChar);
        }
    },
    AfterAttributeValue_quoted {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 9 || consume == 10 || consume == 12 || consume == 13 || consume == ' ') {
                tokeniser.transition(BeforeAttributeName);
            } else if (consume == '/') {
                tokeniser.transition(SelfClosingStartTag);
            } else if (consume == '>') {
                tokeniser.emitTagPending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.error((TokeniserState) this);
                characterReader.unconsume();
                tokeniser.transition(BeforeAttributeName);
            } else {
                tokeniser.eofError(this);
                tokeniser.transition(Data);
            }
        }
    },
    SelfClosingStartTag {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == '>') {
                tokeniser.tagPending.selfClosing = true;
                tokeniser.emitTagPending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.error((TokeniserState) this);
                tokeniser.transition(BeforeAttributeName);
            } else {
                tokeniser.eofError(this);
                tokeniser.transition(Data);
            }
        }
    },
    BogusComment {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            characterReader.unconsume();
            Token.Comment comment = new Token.Comment();
            comment.bogus = true;
            comment.data.append(characterReader.consumeTo('>'));
            tokeniser.emit((Token) comment);
            tokeniser.advanceTransition(Data);
        }
    },
    MarkupDeclarationOpen {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matchConsume("--")) {
                tokeniser.createCommentPending();
                tokeniser.transition(CommentStart);
            } else if (characterReader.matchConsumeIgnoreCase("DOCTYPE")) {
                tokeniser.transition(Doctype);
            } else if (characterReader.matchConsume("[CDATA[")) {
                tokeniser.transition(CdataSection);
            } else {
                tokeniser.error((TokeniserState) this);
                tokeniser.advanceTransition(BogusComment);
            }
        }
    },
    CommentStart {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.commentPending.data.append(TokeniserState.replacementChar);
                tokeniser.transition(Comment);
            } else if (consume == '-') {
                tokeniser.transition(CommentStartDash);
            } else if (consume == '>') {
                tokeniser.error((TokeniserState) this);
                tokeniser.emitCommentPending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.commentPending.data.append(consume);
                tokeniser.transition(Comment);
            } else {
                tokeniser.eofError(this);
                tokeniser.emitCommentPending();
                tokeniser.transition(Data);
            }
        }
    },
    CommentStartDash {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.commentPending.data.append(TokeniserState.replacementChar);
                tokeniser.transition(Comment);
            } else if (consume == '-') {
                tokeniser.transition(CommentStartDash);
            } else if (consume == '>') {
                tokeniser.error((TokeniserState) this);
                tokeniser.emitCommentPending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.commentPending.data.append(consume);
                tokeniser.transition(Comment);
            } else {
                tokeniser.eofError(this);
                tokeniser.emitCommentPending();
                tokeniser.transition(Data);
            }
        }
    },
    Comment {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char current = characterReader.current();
            if (current == 0) {
                tokeniser.error((TokeniserState) this);
                characterReader.advance();
                tokeniser.commentPending.data.append(TokeniserState.replacementChar);
            } else if (current == '-') {
                tokeniser.advanceTransition(CommentEndDash);
            } else if (current != 65535) {
                tokeniser.commentPending.data.append(characterReader.consumeToAny('-', TokeniserState.nullChar));
            } else {
                tokeniser.eofError(this);
                tokeniser.emitCommentPending();
                tokeniser.transition(Data);
            }
        }
    },
    CommentEndDash {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.commentPending.data.append('-').append(TokeniserState.replacementChar);
                tokeniser.transition(Comment);
            } else if (consume == '-') {
                tokeniser.transition(CommentEnd);
            } else if (consume != 65535) {
                tokeniser.commentPending.data.append('-').append(consume);
                tokeniser.transition(Comment);
            } else {
                tokeniser.eofError(this);
                tokeniser.emitCommentPending();
                tokeniser.transition(Data);
            }
        }
    },
    CommentEnd {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.commentPending.data.append("--").append(TokeniserState.replacementChar);
                tokeniser.transition(Comment);
            } else if (consume == '!') {
                tokeniser.error((TokeniserState) this);
                tokeniser.transition(CommentEndBang);
            } else if (consume == '-') {
                tokeniser.error((TokeniserState) this);
                tokeniser.commentPending.data.append('-');
            } else if (consume == '>') {
                tokeniser.emitCommentPending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.error((TokeniserState) this);
                tokeniser.commentPending.data.append("--").append(consume);
                tokeniser.transition(Comment);
            } else {
                tokeniser.eofError(this);
                tokeniser.emitCommentPending();
                tokeniser.transition(Data);
            }
        }
    },
    CommentEndBang {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.commentPending.data.append("--!").append(TokeniserState.replacementChar);
                tokeniser.transition(Comment);
            } else if (consume == '-') {
                tokeniser.commentPending.data.append("--!");
                tokeniser.transition(CommentEndDash);
            } else if (consume == '>') {
                tokeniser.emitCommentPending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.commentPending.data.append("--!").append(consume);
                tokeniser.transition(Comment);
            } else {
                tokeniser.eofError(this);
                tokeniser.emitCommentPending();
                tokeniser.transition(Data);
            }
        }
    },
    Doctype {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 9 || consume == 10 || consume == 12 || consume == 13 || consume == ' ') {
                tokeniser.transition(BeforeDoctypeName);
                return;
            }
            if (consume != '>') {
                if (consume != 65535) {
                    tokeniser.error((TokeniserState) this);
                    tokeniser.transition(BeforeDoctypeName);
                    return;
                }
                tokeniser.eofError(this);
            }
            tokeniser.error((TokeniserState) this);
            tokeniser.createDoctypePending();
            tokeniser.doctypePending.forceQuirks = true;
            tokeniser.emitDoctypePending();
            tokeniser.transition(Data);
        }
    },
    BeforeDoctypeName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matchesLetter()) {
                tokeniser.createDoctypePending();
                tokeniser.transition(DoctypeName);
                return;
            }
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.createDoctypePending();
                tokeniser.doctypePending.name.append(TokeniserState.replacementChar);
                tokeniser.transition(DoctypeName);
            } else if (consume == ' ') {
            } else {
                if (consume == 65535) {
                    tokeniser.eofError(this);
                    tokeniser.createDoctypePending();
                    tokeniser.doctypePending.forceQuirks = true;
                    tokeniser.emitDoctypePending();
                    tokeniser.transition(Data);
                } else if (consume != 9 && consume != 10 && consume != 12 && consume != 13) {
                    tokeniser.createDoctypePending();
                    tokeniser.doctypePending.name.append(consume);
                    tokeniser.transition(DoctypeName);
                }
            }
        }
    },
    DoctypeName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.matchesLetter()) {
                tokeniser.doctypePending.name.append(characterReader.consumeLetterSequence().toLowerCase());
                return;
            }
            char consume = characterReader.consume();
            if (consume != 0) {
                if (consume != ' ') {
                    if (consume == '>') {
                        tokeniser.emitDoctypePending();
                        tokeniser.transition(Data);
                        return;
                    } else if (consume == 65535) {
                        tokeniser.eofError(this);
                        tokeniser.doctypePending.forceQuirks = true;
                        tokeniser.emitDoctypePending();
                        tokeniser.transition(Data);
                        return;
                    } else if (!(consume == 9 || consume == 10 || consume == 12 || consume == 13)) {
                        tokeniser.doctypePending.name.append(consume);
                        return;
                    }
                }
                tokeniser.transition(AfterDoctypeName);
                return;
            }
            tokeniser.error((TokeniserState) this);
            tokeniser.doctypePending.name.append(TokeniserState.replacementChar);
        }
    },
    AfterDoctypeName {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            if (characterReader.isEmpty()) {
                tokeniser.eofError(this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            } else if (characterReader.matchesAny(9, 10, 13, 12, ' ')) {
                characterReader.advance();
            } else if (characterReader.matches('>')) {
                tokeniser.emitDoctypePending();
                tokeniser.advanceTransition(Data);
            } else if (characterReader.matchConsumeIgnoreCase("PUBLIC")) {
                tokeniser.transition(AfterDoctypePublicKeyword);
            } else if (characterReader.matchConsumeIgnoreCase("SYSTEM")) {
                tokeniser.transition(AfterDoctypeSystemKeyword);
            } else {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.advanceTransition(BogusDoctype);
            }
        }
    },
    AfterDoctypePublicKeyword {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 9 || consume == 10 || consume == 12 || consume == 13 || consume == ' ') {
                tokeniser.transition(BeforeDoctypePublicIdentifier);
            } else if (consume == '\"') {
                tokeniser.error((TokeniserState) this);
                tokeniser.transition(DoctypePublicIdentifier_doubleQuoted);
            } else if (consume == '\'') {
                tokeniser.error((TokeniserState) this);
                tokeniser.transition(DoctypePublicIdentifier_singleQuoted);
            } else if (consume == '>') {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.transition(BogusDoctype);
            } else {
                tokeniser.eofError(this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            }
        }
    },
    BeforeDoctypePublicIdentifier {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume != 9 && consume != 10 && consume != 12 && consume != 13 && consume != ' ') {
                if (consume == '\"') {
                    tokeniser.transition(DoctypePublicIdentifier_doubleQuoted);
                } else if (consume == '\'') {
                    tokeniser.transition(DoctypePublicIdentifier_singleQuoted);
                } else if (consume == '>') {
                    tokeniser.error((TokeniserState) this);
                    tokeniser.doctypePending.forceQuirks = true;
                    tokeniser.emitDoctypePending();
                    tokeniser.transition(Data);
                } else if (consume != 65535) {
                    tokeniser.error((TokeniserState) this);
                    tokeniser.doctypePending.forceQuirks = true;
                    tokeniser.transition(BogusDoctype);
                } else {
                    tokeniser.eofError(this);
                    tokeniser.doctypePending.forceQuirks = true;
                    tokeniser.emitDoctypePending();
                    tokeniser.transition(Data);
                }
            }
        }
    },
    DoctypePublicIdentifier_doubleQuoted {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.publicIdentifier.append(TokeniserState.replacementChar);
            } else if (consume == '\"') {
                tokeniser.transition(AfterDoctypePublicIdentifier);
            } else if (consume == '>') {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.doctypePending.publicIdentifier.append(consume);
            } else {
                tokeniser.eofError(this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            }
        }
    },
    DoctypePublicIdentifier_singleQuoted {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.publicIdentifier.append(TokeniserState.replacementChar);
            } else if (consume == '\'') {
                tokeniser.transition(AfterDoctypePublicIdentifier);
            } else if (consume == '>') {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.doctypePending.publicIdentifier.append(consume);
            } else {
                tokeniser.eofError(this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            }
        }
    },
    AfterDoctypePublicIdentifier {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 9 || consume == 10 || consume == 12 || consume == 13 || consume == ' ') {
                tokeniser.transition(BetweenDoctypePublicAndSystemIdentifiers);
            } else if (consume == '\"') {
                tokeniser.error((TokeniserState) this);
                tokeniser.transition(DoctypeSystemIdentifier_doubleQuoted);
            } else if (consume == '\'') {
                tokeniser.error((TokeniserState) this);
                tokeniser.transition(DoctypeSystemIdentifier_singleQuoted);
            } else if (consume == '>') {
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.transition(BogusDoctype);
            } else {
                tokeniser.eofError(this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            }
        }
    },
    BetweenDoctypePublicAndSystemIdentifiers {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume != 9 && consume != 10 && consume != 12 && consume != 13 && consume != ' ') {
                if (consume == '\"') {
                    tokeniser.error((TokeniserState) this);
                    tokeniser.transition(DoctypeSystemIdentifier_doubleQuoted);
                } else if (consume == '\'') {
                    tokeniser.error((TokeniserState) this);
                    tokeniser.transition(DoctypeSystemIdentifier_singleQuoted);
                } else if (consume == '>') {
                    tokeniser.emitDoctypePending();
                    tokeniser.transition(Data);
                } else if (consume != 65535) {
                    tokeniser.error((TokeniserState) this);
                    tokeniser.doctypePending.forceQuirks = true;
                    tokeniser.transition(BogusDoctype);
                } else {
                    tokeniser.eofError(this);
                    tokeniser.doctypePending.forceQuirks = true;
                    tokeniser.emitDoctypePending();
                    tokeniser.transition(Data);
                }
            }
        }
    },
    AfterDoctypeSystemKeyword {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 9 || consume == 10 || consume == 12 || consume == 13 || consume == ' ') {
                tokeniser.transition(BeforeDoctypeSystemIdentifier);
            } else if (consume == '\"') {
                tokeniser.error((TokeniserState) this);
                tokeniser.transition(DoctypeSystemIdentifier_doubleQuoted);
            } else if (consume == '\'') {
                tokeniser.error((TokeniserState) this);
                tokeniser.transition(DoctypeSystemIdentifier_singleQuoted);
            } else if (consume == '>') {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
            } else {
                tokeniser.eofError(this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            }
        }
    },
    BeforeDoctypeSystemIdentifier {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume != 9 && consume != 10 && consume != 12 && consume != 13 && consume != ' ') {
                if (consume == '\"') {
                    tokeniser.transition(DoctypeSystemIdentifier_doubleQuoted);
                } else if (consume == '\'') {
                    tokeniser.transition(DoctypeSystemIdentifier_singleQuoted);
                } else if (consume == '>') {
                    tokeniser.error((TokeniserState) this);
                    tokeniser.doctypePending.forceQuirks = true;
                    tokeniser.emitDoctypePending();
                    tokeniser.transition(Data);
                } else if (consume != 65535) {
                    tokeniser.error((TokeniserState) this);
                    tokeniser.doctypePending.forceQuirks = true;
                    tokeniser.transition(BogusDoctype);
                } else {
                    tokeniser.eofError(this);
                    tokeniser.doctypePending.forceQuirks = true;
                    tokeniser.emitDoctypePending();
                    tokeniser.transition(Data);
                }
            }
        }
    },
    DoctypeSystemIdentifier_doubleQuoted {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.systemIdentifier.append(TokeniserState.replacementChar);
            } else if (consume == '\"') {
                tokeniser.transition(AfterDoctypeSystemIdentifier);
            } else if (consume == '>') {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.doctypePending.systemIdentifier.append(consume);
            } else {
                tokeniser.eofError(this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            }
        }
    },
    DoctypeSystemIdentifier_singleQuoted {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == 0) {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.systemIdentifier.append(TokeniserState.replacementChar);
            } else if (consume == '\'') {
                tokeniser.transition(AfterDoctypeSystemIdentifier);
            } else if (consume == '>') {
                tokeniser.error((TokeniserState) this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            } else if (consume != 65535) {
                tokeniser.doctypePending.systemIdentifier.append(consume);
            } else {
                tokeniser.eofError(this);
                tokeniser.doctypePending.forceQuirks = true;
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            }
        }
    },
    AfterDoctypeSystemIdentifier {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume != 9 && consume != 10 && consume != 12 && consume != 13 && consume != ' ') {
                if (consume == '>') {
                    tokeniser.emitDoctypePending();
                    tokeniser.transition(Data);
                } else if (consume != 65535) {
                    tokeniser.error((TokeniserState) this);
                    tokeniser.transition(BogusDoctype);
                } else {
                    tokeniser.eofError(this);
                    tokeniser.doctypePending.forceQuirks = true;
                    tokeniser.emitDoctypePending();
                    tokeniser.transition(Data);
                }
            }
        }
    },
    BogusDoctype {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            char consume = characterReader.consume();
            if (consume == '>') {
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            } else if (consume == 65535) {
                tokeniser.emitDoctypePending();
                tokeniser.transition(Data);
            }
        }
    },
    CdataSection {
        /* access modifiers changed from: package-private */
        public void read(Tokeniser tokeniser, CharacterReader characterReader) {
            tokeniser.emit(characterReader.consumeTo("]]>"));
            characterReader.matchConsume("]]>");
            tokeniser.transition(Data);
        }
    };
    
    private static final char eof = '￿';
    private static final char nullChar = '\u0000';
    private static final char replacementChar = '�';
    /* access modifiers changed from: private */
    public static final String replacementStr = null;

    /* access modifiers changed from: package-private */
    public abstract void read(Tokeniser tokeniser, CharacterReader characterReader);

    static {
        replacementStr = String.valueOf(replacementChar);
    }

    /* access modifiers changed from: private */
    public static final void handleDataEndTag(Tokeniser tokeniser, CharacterReader characterReader, TokeniserState tokeniserState) {
        if (characterReader.matchesLetter()) {
            String consumeLetterSequence = characterReader.consumeLetterSequence();
            tokeniser.tagPending.appendTagName(consumeLetterSequence.toLowerCase());
            tokeniser.dataBuffer.append(consumeLetterSequence);
            return;
        }
        boolean z = false;
        boolean z2 = true;
        if (tokeniser.isAppropriateEndTagToken() && !characterReader.isEmpty()) {
            char consume = characterReader.consume();
            if (consume == 9 || consume == 10 || consume == 12 || consume == 13 || consume == ' ') {
                tokeniser.transition(BeforeAttributeName);
            } else if (consume == '/') {
                tokeniser.transition(SelfClosingStartTag);
            } else if (consume != '>') {
                tokeniser.dataBuffer.append(consume);
                z = true;
            } else {
                tokeniser.emitTagPending();
                tokeniser.transition(Data);
            }
            z2 = z;
        }
        if (z2) {
            tokeniser.emit("</" + tokeniser.dataBuffer.toString());
            tokeniser.transition(tokeniserState);
        }
    }

    /* access modifiers changed from: private */
    public static final void handleDataDoubleEscapeTag(Tokeniser tokeniser, CharacterReader characterReader, TokeniserState tokeniserState, TokeniserState tokeniserState2) {
        if (characterReader.matchesLetter()) {
            String consumeLetterSequence = characterReader.consumeLetterSequence();
            tokeniser.dataBuffer.append(consumeLetterSequence.toLowerCase());
            tokeniser.emit(consumeLetterSequence);
            return;
        }
        char consume = characterReader.consume();
        if (consume == 9 || consume == 10 || consume == 12 || consume == 13 || consume == ' ' || consume == '/' || consume == '>') {
            if (tokeniser.dataBuffer.toString().equals("script")) {
                tokeniser.transition(tokeniserState);
            } else {
                tokeniser.transition(tokeniserState2);
            }
            tokeniser.emit(consume);
            return;
        }
        characterReader.unconsume();
        tokeniser.transition(tokeniserState2);
    }
}
