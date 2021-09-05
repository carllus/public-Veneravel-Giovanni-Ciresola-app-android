package org.jsoup.parser;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Token;

class Tokeniser {
    static final char replacementChar = '�';
    private StringBuilder charBuffer = new StringBuilder();
    Token.Comment commentPending;
    StringBuilder dataBuffer;
    Token.Doctype doctypePending;
    private Token emitPending;
    private ParseErrorList errors;
    private boolean isEmitPending = false;
    private Token.StartTag lastStartTag;
    private CharacterReader reader;
    private boolean selfClosingFlagAcknowledged = true;
    private TokeniserState state = TokeniserState.Data;
    Token.Tag tagPending;

    /* access modifiers changed from: package-private */
    public boolean currentNodeInHtmlNS() {
        return true;
    }

    Tokeniser(CharacterReader characterReader, ParseErrorList parseErrorList) {
        this.reader = characterReader;
        this.errors = parseErrorList;
    }

    /* access modifiers changed from: package-private */
    public Token read() {
        if (!this.selfClosingFlagAcknowledged) {
            error("Self closing flag not acknowledged");
            this.selfClosingFlagAcknowledged = true;
        }
        while (!this.isEmitPending) {
            this.state.read(this, this.reader);
        }
        if (this.charBuffer.length() > 0) {
            String sb = this.charBuffer.toString();
            StringBuilder sb2 = this.charBuffer;
            sb2.delete(0, sb2.length());
            return new Token.Character(sb);
        }
        this.isEmitPending = false;
        return this.emitPending;
    }

    /* access modifiers changed from: package-private */
    public void emit(Token token) {
        Validate.isFalse(this.isEmitPending, "There is an unread token pending!");
        this.emitPending = token;
        this.isEmitPending = true;
        if (token.type == Token.TokenType.StartTag) {
            Token.StartTag startTag = (Token.StartTag) token;
            this.lastStartTag = startTag;
            if (startTag.selfClosing) {
                this.selfClosingFlagAcknowledged = false;
            }
        } else if (token.type == Token.TokenType.EndTag && ((Token.EndTag) token).attributes != null) {
            error("Attributes incorrectly present on end tag");
        }
    }

    /* access modifiers changed from: package-private */
    public void emit(String str) {
        this.charBuffer.append(str);
    }

    /* access modifiers changed from: package-private */
    public void emit(char[] cArr) {
        this.charBuffer.append(cArr);
    }

    /* access modifiers changed from: package-private */
    public void emit(char c) {
        this.charBuffer.append(c);
    }

    /* access modifiers changed from: package-private */
    public TokeniserState getState() {
        return this.state;
    }

    /* access modifiers changed from: package-private */
    public void transition(TokeniserState tokeniserState) {
        this.state = tokeniserState;
    }

    /* access modifiers changed from: package-private */
    public void advanceTransition(TokeniserState tokeniserState) {
        this.reader.advance();
        this.state = tokeniserState;
    }

    /* access modifiers changed from: package-private */
    public void acknowledgeSelfClosingFlag() {
        this.selfClosingFlagAcknowledged = true;
    }

    /* access modifiers changed from: package-private */
    public char[] consumeCharacterReference(Character ch, boolean z) {
        int i;
        if (this.reader.isEmpty()) {
            return null;
        }
        if ((ch != null && ch.charValue() == this.reader.current()) || this.reader.matchesAny(9, 10, 13, 12, ' ', '<', '&')) {
            return null;
        }
        this.reader.mark();
        if (this.reader.matchConsume("#")) {
            boolean matchConsumeIgnoreCase = this.reader.matchConsumeIgnoreCase("X");
            CharacterReader characterReader = this.reader;
            String consumeHexSequence = matchConsumeIgnoreCase ? characterReader.consumeHexSequence() : characterReader.consumeDigitSequence();
            if (consumeHexSequence.length() == 0) {
                characterReferenceError("numeric reference with no numerals");
                this.reader.rewindToMark();
                return null;
            }
            if (!this.reader.matchConsume(";")) {
                characterReferenceError("missing semicolon");
            }
            try {
                i = Integer.valueOf(consumeHexSequence, matchConsumeIgnoreCase ? 16 : 10).intValue();
            } catch (NumberFormatException unused) {
                i = -1;
            }
            if (i != -1 && ((i < 55296 || i > 57343) && i <= 1114111)) {
                return Character.toChars(i);
            }
            characterReferenceError("character outside of valid range");
            return new char[]{replacementChar};
        }
        String consumeLetterThenDigitSequence = this.reader.consumeLetterThenDigitSequence();
        boolean matches = this.reader.matches(';');
        if (!(Entities.isBaseNamedEntity(consumeLetterThenDigitSequence) || (Entities.isNamedEntity(consumeLetterThenDigitSequence) && matches))) {
            this.reader.rewindToMark();
            if (matches) {
                characterReferenceError(String.format("invalid named referenece '%s'", new Object[]{consumeLetterThenDigitSequence}));
            }
            return null;
        } else if (!z || (!this.reader.matchesLetter() && !this.reader.matchesDigit() && !this.reader.matchesAny('=', '-', '_'))) {
            if (!this.reader.matchConsume(";")) {
                characterReferenceError("missing semicolon");
            }
            return new char[]{Entities.getCharacterByName(consumeLetterThenDigitSequence).charValue()};
        } else {
            this.reader.rewindToMark();
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public Token.Tag createTagPending(boolean z) {
        Token.Tag startTag = z ? new Token.StartTag() : new Token.EndTag();
        this.tagPending = startTag;
        return startTag;
    }

    /* access modifiers changed from: package-private */
    public void emitTagPending() {
        this.tagPending.finaliseTag();
        emit((Token) this.tagPending);
    }

    /* access modifiers changed from: package-private */
    public void createCommentPending() {
        this.commentPending = new Token.Comment();
    }

    /* access modifiers changed from: package-private */
    public void emitCommentPending() {
        emit((Token) this.commentPending);
    }

    /* access modifiers changed from: package-private */
    public void createDoctypePending() {
        this.doctypePending = new Token.Doctype();
    }

    /* access modifiers changed from: package-private */
    public void emitDoctypePending() {
        emit((Token) this.doctypePending);
    }

    /* access modifiers changed from: package-private */
    public void createTempBuffer() {
        this.dataBuffer = new StringBuilder();
    }

    /* access modifiers changed from: package-private */
    public boolean isAppropriateEndTagToken() {
        if (this.lastStartTag == null) {
            return false;
        }
        return this.tagPending.tagName.equals(this.lastStartTag.tagName);
    }

    /* access modifiers changed from: package-private */
    public String appropriateEndTagName() {
        Token.StartTag startTag = this.lastStartTag;
        if (startTag == null) {
            return null;
        }
        return startTag.tagName;
    }

    /* access modifiers changed from: package-private */
    public void error(TokeniserState tokeniserState) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpected character '%s' in input state [%s]", Character.valueOf(this.reader.current()), tokeniserState));
        }
    }

    /* access modifiers changed from: package-private */
    public void eofError(TokeniserState tokeniserState) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpectedly reached end of file (EOF) in input state [%s]", tokeniserState));
        }
    }

    private void characterReferenceError(String str) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Invalid character reference: %s", str));
        }
    }

    private void error(String str) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), str));
        }
    }

    /* access modifiers changed from: package-private */
    public String unescapeEntities(boolean z) {
        StringBuilder sb = new StringBuilder();
        while (!this.reader.isEmpty()) {
            sb.append(this.reader.consumeTo('&'));
            if (this.reader.matches('&')) {
                this.reader.consume();
                char[] consumeCharacterReference = consumeCharacterReference((Character) null, z);
                if (consumeCharacterReference == null || consumeCharacterReference.length == 0) {
                    sb.append('&');
                } else {
                    sb.append(consumeCharacterReference);
                }
            }
        }
        return sb.toString();
    }
}
