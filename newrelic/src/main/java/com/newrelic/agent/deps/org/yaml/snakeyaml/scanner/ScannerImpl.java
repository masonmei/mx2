// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.scanner;

import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.ScalarToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.TagToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.AliasToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.AnchorToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.DirectiveToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.ValueToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.KeyToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.BlockEntryToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.FlowEntryToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.DocumentEndToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.DocumentStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.StreamEndToken;
import java.util.HashMap;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.StreamStartToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.BlockEndToken;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import java.util.Set;
import java.util.HashSet;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import com.newrelic.agent.deps.org.yaml.snakeyaml.tokens.Token;
import java.util.List;
import com.newrelic.agent.deps.org.yaml.snakeyaml.reader.Reader;
import java.util.Map;
import java.util.regex.Pattern;

public final class ScannerImpl implements Scanner
{
    private static final String NULL_BL_LINEBR = "\u0000 \r\n\u0085\u2028\u2029";
    private static final String NULL_BL_T_LINEBR = "\u0000 \t\r\n\u0085\u2028\u2029";
    public static final String NULL_OR_LINEBR = "\u0000\r\n\u0085\u2028\u2029";
    private static final String FULL_LINEBR = "\r\n\u0085\u2028\u2029";
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
    private static final Pattern NOT_HEXA;
    public static final Map<Character, String> ESCAPE_REPLACEMENTS;
    public static final Map<Character, Integer> ESCAPE_CODES;
    private final Reader reader;
    private boolean done;
    private int flowLevel;
    private List<Token> tokens;
    private int tokensTaken;
    private int indent;
    private LinkedList<Integer> indents;
    private boolean allowSimpleKey;
    private Map<Integer, SimpleKey> possibleSimpleKeys;
    private static final String SPACES = "\u0000 \t\r\n\u0085\u2028\u2029";
    
    public ScannerImpl(final Reader reader) {
        this.done = false;
        this.flowLevel = 0;
        this.tokensTaken = 0;
        this.indent = -1;
        this.allowSimpleKey = true;
        this.reader = reader;
        this.tokens = new LinkedList<Token>();
        this.indents = new LinkedList<Integer>();
        this.possibleSimpleKeys = new LinkedHashMap<Integer, SimpleKey>();
        this.fetchStreamStart();
    }
    
    public boolean checkToken(final List<Class<? extends Token>> choices) {
        while (this.needMoreTokens()) {
            this.fetchMoreTokens();
        }
        if (!this.tokens.isEmpty()) {
            if (choices.size() == 0) {
                return true;
            }
            final Token first = this.tokens.get(0);
            for (final Class<? extends Token> class1 : choices) {
                if (class1.isInstance(first)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean checkToken(final Class<? extends Token> choice) {
        final List<Class<? extends Token>> list = new ArrayList<Class<? extends Token>>();
        list.add(choice);
        return this.checkToken(list);
    }
    
    public Token peekToken() {
        while (this.needMoreTokens()) {
            this.fetchMoreTokens();
        }
        return this.tokens.get(0);
    }
    
    public Token getToken() {
        if (!this.tokens.isEmpty()) {
            ++this.tokensTaken;
            return this.tokens.remove(0);
        }
        return null;
    }
    
    private boolean needMoreTokens() {
        if (this.done) {
            return false;
        }
        if (this.tokens.isEmpty()) {
            return true;
        }
        this.stalePossibleSimpleKeys();
        return this.nextPossibleSimpleKey() == this.tokensTaken;
    }
    
    private void fetchMoreTokens() {
        this.scanToNextToken();
        this.stalePossibleSimpleKeys();
        this.unwindIndent(this.reader.getColumn());
        final char ch = this.reader.peek();
        switch (ch) {
            case '\0': {
                this.fetchStreamEnd();
                return;
            }
            case '%': {
                if (this.checkDirective()) {
                    this.fetchDirective();
                    return;
                }
                break;
            }
            case '-': {
                if (this.checkDocumentStart()) {
                    this.fetchDocumentStart();
                    return;
                }
                if (this.checkBlockEntry()) {
                    this.fetchBlockEntry();
                    return;
                }
                break;
            }
            case '.': {
                if (this.checkDocumentEnd()) {
                    this.fetchDocumentEnd();
                    return;
                }
                break;
            }
            case '[': {
                this.fetchFlowSequenceStart();
                return;
            }
            case '{': {
                this.fetchFlowMappingStart();
                return;
            }
            case ']': {
                this.fetchFlowSequenceEnd();
                return;
            }
            case '}': {
                this.fetchFlowMappingEnd();
                return;
            }
            case ',': {
                this.fetchFlowEntry();
                return;
            }
            case '?': {
                if (this.checkKey()) {
                    this.fetchKey();
                    return;
                }
                break;
            }
            case ':': {
                if (this.checkValue()) {
                    this.fetchValue();
                    return;
                }
                break;
            }
            case '*': {
                this.fetchAlias();
                return;
            }
            case '&': {
                this.fetchAnchor();
                return;
            }
            case '!': {
                this.fetchTag();
                return;
            }
            case '|': {
                if (this.flowLevel == 0) {
                    this.fetchLiteral();
                    return;
                }
                break;
            }
            case '>': {
                if (this.flowLevel == 0) {
                    this.fetchFolded();
                    return;
                }
                break;
            }
            case '\'': {
                this.fetchSingle();
                return;
            }
            case '\"': {
                this.fetchDouble();
                return;
            }
        }
        if (this.checkPlain()) {
            this.fetchPlain();
            return;
        }
        throw new ScannerException("while scanning for the next token", null, "found character " + ch + "(" + (int)ch + " that cannot start any token", this.reader.getMark());
    }
    
    private int nextPossibleSimpleKey() {
        final Iterator<SimpleKey> iter = this.possibleSimpleKeys.values().iterator();
        if (iter.hasNext()) {
            final SimpleKey key = iter.next();
            return key.getTokenNumber();
        }
        return -1;
    }
    
    private void stalePossibleSimpleKeys() {
        Set<Integer> toRemove = null;
        for (final Integer level : this.possibleSimpleKeys.keySet()) {
            final SimpleKey key = this.possibleSimpleKeys.get(level);
            if (key.getLine() != this.reader.getLine() || this.reader.getIndex() - key.getIndex() > 1024) {
                if (key.isRequired()) {
                    throw new ScannerException("while scanning a simple key", key.getMark(), "could not found expected ':'", this.reader.getMark());
                }
                if (toRemove == null) {
                    toRemove = new HashSet<Integer>();
                }
                toRemove.add(level);
            }
        }
        if (toRemove != null) {
            for (final Integer level : toRemove) {
                this.possibleSimpleKeys.remove(level);
            }
        }
    }
    
    private void savePossibleSimpleKey() {
        final boolean required = this.flowLevel == 0 && this.indent == this.reader.getColumn();
        if (!this.allowSimpleKey && required) {
            throw new YAMLException("A simple key is required only if it is the first token in the current line");
        }
        if (this.allowSimpleKey) {
            this.removePossibleSimpleKey();
            final int tokenNumber = this.tokensTaken + this.tokens.size();
            final SimpleKey key = new SimpleKey(tokenNumber, required, this.reader.getIndex(), this.reader.getLine(), this.reader.getColumn(), this.reader.getMark());
            this.possibleSimpleKeys.put(new Integer(this.flowLevel), key);
        }
    }
    
    private void removePossibleSimpleKey() {
        if (this.possibleSimpleKeys.keySet().contains(new Integer(this.flowLevel))) {
            final SimpleKey key = this.possibleSimpleKeys.get(new Integer(this.flowLevel));
            if (key.isRequired()) {
                throw new ScannerException("while scanning a simple key", key.getMark(), "could not found expected ':'", this.reader.getMark());
            }
            this.possibleSimpleKeys.remove(this.flowLevel);
        }
    }
    
    private void unwindIndent(final int col) {
        if (this.flowLevel != 0) {
            return;
        }
        while (this.indent > col) {
            final Mark mark = this.reader.getMark();
            this.indent = this.indents.removeFirst();
            this.tokens.add(new BlockEndToken(mark, mark));
        }
    }
    
    private boolean addIndent(final int column) {
        if (this.indent < column) {
            this.indents.addFirst(this.indent);
            this.indent = column;
            return true;
        }
        return false;
    }
    
    private void fetchStreamStart() {
        final Mark mark = this.reader.getMark();
        final Token token = new StreamStartToken(mark, mark);
        this.tokens.add(token);
    }
    
    private void fetchStreamEnd() {
        this.unwindIndent(-1);
        this.allowSimpleKey = false;
        this.possibleSimpleKeys = new HashMap<Integer, SimpleKey>();
        final Mark mark = this.reader.getMark();
        final Token token = new StreamEndToken(mark, mark);
        this.tokens.add(token);
        this.done = true;
    }
    
    private void fetchDirective() {
        this.unwindIndent(-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanDirective();
        this.tokens.add(tok);
    }
    
    private void fetchDocumentStart() {
        this.fetchDocumentIndicator(true);
    }
    
    private void fetchDocumentEnd() {
        this.fetchDocumentIndicator(false);
    }
    
    private void fetchDocumentIndicator(final boolean isDocumentStart) {
        this.unwindIndent(-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Mark startMark = this.reader.getMark();
        this.reader.forward(3);
        final Mark endMark = this.reader.getMark();
        Token token;
        if (isDocumentStart) {
            token = new DocumentStartToken(startMark, endMark);
        }
        else {
            token = new DocumentEndToken(startMark, endMark);
        }
        this.tokens.add(token);
    }
    
    private void fetchFlowSequenceStart() {
        this.fetchFlowCollectionStart(false);
    }
    
    private void fetchFlowMappingStart() {
        this.fetchFlowCollectionStart(true);
    }
    
    private void fetchFlowCollectionStart(final boolean isMappingStart) {
        this.savePossibleSimpleKey();
        ++this.flowLevel;
        this.allowSimpleKey = true;
        final Mark startMark = this.reader.getMark();
        this.reader.forward(1);
        final Mark endMark = this.reader.getMark();
        Token token;
        if (isMappingStart) {
            token = new FlowMappingStartToken(startMark, endMark);
        }
        else {
            token = new FlowSequenceStartToken(startMark, endMark);
        }
        this.tokens.add(token);
    }
    
    private void fetchFlowSequenceEnd() {
        this.fetchFlowCollectionEnd(false);
    }
    
    private void fetchFlowMappingEnd() {
        this.fetchFlowCollectionEnd(true);
    }
    
    private void fetchFlowCollectionEnd(final boolean isMappingEnd) {
        this.removePossibleSimpleKey();
        --this.flowLevel;
        this.allowSimpleKey = false;
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        Token token;
        if (isMappingEnd) {
            token = new FlowMappingEndToken(startMark, endMark);
        }
        else {
            token = new FlowSequenceEndToken(startMark, endMark);
        }
        this.tokens.add(token);
    }
    
    private void fetchFlowEntry() {
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        final Token token = new FlowEntryToken(startMark, endMark);
        this.tokens.add(token);
    }
    
    private void fetchBlockEntry() {
        if (this.flowLevel == 0) {
            if (!this.allowSimpleKey) {
                throw new ScannerException(null, null, "sequence entries are not allowed here", this.reader.getMark());
            }
            if (this.addIndent(this.reader.getColumn())) {
                final Mark mark = this.reader.getMark();
                this.tokens.add(new BlockSequenceStartToken(mark, mark));
            }
        }
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        final Token token = new BlockEntryToken(startMark, endMark);
        this.tokens.add(token);
    }
    
    private void fetchKey() {
        if (this.flowLevel == 0) {
            if (!this.allowSimpleKey) {
                throw new ScannerException(null, null, "mapping keys are not allowed here", this.reader.getMark());
            }
            if (this.addIndent(this.reader.getColumn())) {
                final Mark mark = this.reader.getMark();
                this.tokens.add(new BlockMappingStartToken(mark, mark));
            }
        }
        this.allowSimpleKey = (this.flowLevel == 0);
        this.removePossibleSimpleKey();
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        final Token token = new KeyToken(startMark, endMark);
        this.tokens.add(token);
    }
    
    private void fetchValue() {
        if (this.possibleSimpleKeys.keySet().contains(this.flowLevel)) {
            final SimpleKey key = this.possibleSimpleKeys.get(this.flowLevel);
            this.possibleSimpleKeys.remove(this.flowLevel);
            this.tokens.add(key.getTokenNumber() - this.tokensTaken, new KeyToken(key.getMark(), key.getMark()));
            if (this.flowLevel == 0 && this.addIndent(key.getColumn())) {
                this.tokens.add(key.getTokenNumber() - this.tokensTaken, new BlockMappingStartToken(key.getMark(), key.getMark()));
            }
            this.allowSimpleKey = false;
        }
        else {
            if (this.flowLevel == 0 && !this.allowSimpleKey) {
                throw new ScannerException(null, null, "mapping values are not allowed here", this.reader.getMark());
            }
            if (this.flowLevel == 0 && this.addIndent(this.reader.getColumn())) {
                final Mark mark = this.reader.getMark();
                this.tokens.add(new BlockMappingStartToken(mark, mark));
            }
            this.allowSimpleKey = (this.flowLevel == 0);
            this.removePossibleSimpleKey();
        }
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        final Token token = new ValueToken(startMark, endMark);
        this.tokens.add(token);
    }
    
    private void fetchAlias() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanAnchor(false);
        this.tokens.add(tok);
    }
    
    private void fetchAnchor() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanAnchor(true);
        this.tokens.add(tok);
    }
    
    private void fetchTag() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanTag();
        this.tokens.add(tok);
    }
    
    private void fetchLiteral() {
        this.fetchBlockScalar('|');
    }
    
    private void fetchFolded() {
        this.fetchBlockScalar('>');
    }
    
    private void fetchBlockScalar(final char style) {
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        final Token tok = this.scanBlockScalar(style);
        this.tokens.add(tok);
    }
    
    private void fetchSingle() {
        this.fetchFlowScalar('\'');
    }
    
    private void fetchDouble() {
        this.fetchFlowScalar('\"');
    }
    
    private void fetchFlowScalar(final char style) {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanFlowScalar(style);
        this.tokens.add(tok);
    }
    
    private void fetchPlain() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanPlain();
        this.tokens.add(tok);
    }
    
    private boolean checkDirective() {
        return this.reader.getColumn() == 0;
    }
    
    private boolean checkDocumentStart() {
        return this.reader.getColumn() == 0 && "---".equals(this.reader.prefix(3)) && "\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(3)) != -1;
    }
    
    private boolean checkDocumentEnd() {
        return this.reader.getColumn() == 0 && "...".equals(this.reader.prefix(3)) && "\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(3)) != -1;
    }
    
    private boolean checkBlockEntry() {
        return "\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(1)) != -1;
    }
    
    private boolean checkKey() {
        return this.flowLevel != 0 || "\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(1)) != -1;
    }
    
    private boolean checkValue() {
        return this.flowLevel != 0 || "\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(1)) != -1;
    }
    
    private boolean checkPlain() {
        final char ch = this.reader.peek();
        return "\u0000 \t\r\n\u0085\u2028\u2029-?:,[]{}#&*!|>'\"%@`".indexOf(ch) == -1 || ("\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(1)) == -1 && (ch == '-' || (this.flowLevel == 0 && "?:".indexOf(ch) != -1)));
    }
    
    private void scanToNextToken() {
        if (this.reader.getIndex() == 0 && this.reader.peek() == '\ufeff') {
            this.reader.forward();
        }
        boolean found = false;
        while (!found) {
            while (this.reader.peek() == ' ') {
                this.reader.forward();
            }
            if (this.reader.peek() == '#') {
                while ("\u0000\r\n\u0085\u2028\u2029".indexOf(this.reader.peek()) == -1) {
                    this.reader.forward();
                }
            }
            if (this.scanLineBreak().length() != 0) {
                if (this.flowLevel != 0) {
                    continue;
                }
                this.allowSimpleKey = true;
            }
            else {
                found = true;
            }
        }
    }
    
    private Token scanDirective() {
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final String name = this.scanDirectiveName(startMark);
        List<?> value = null;
        Mark endMark;
        if (name.equals("YAML")) {
            value = this.scanYamlDirectiveValue(startMark);
            endMark = this.reader.getMark();
        }
        else if (name.equals("TAG")) {
            value = this.scanTagDirectiveValue(startMark);
            endMark = this.reader.getMark();
        }
        else {
            endMark = this.reader.getMark();
            while ("\u0000\r\n\u0085\u2028\u2029".indexOf(this.reader.peek()) == -1) {
                this.reader.forward();
            }
        }
        this.scanDirectiveIgnoredLine(startMark);
        return new DirectiveToken(name, value, startMark, endMark);
    }
    
    private String scanDirectiveName(final Mark startMark) {
        int length;
        char ch;
        for (length = 0, ch = this.reader.peek(length); "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_".indexOf(ch) != -1; ch = this.reader.peek(length)) {
            ++length;
        }
        if (length == 0) {
            throw new ScannerException("while scanning a directive", startMark, "expected alphabetic or numeric character, but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        final String value = this.reader.prefix(length);
        this.reader.forward(length);
        ch = this.reader.peek();
        if ("\u0000 \r\n\u0085\u2028\u2029".indexOf(ch) == -1) {
            throw new ScannerException("while scanning a directive", startMark, "expected alphabetic or numeric character, but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        return value;
    }
    
    private List<Integer> scanYamlDirectiveValue(final Mark startMark) {
        while (this.reader.peek() == ' ') {
            this.reader.forward();
        }
        final Integer major = this.scanYamlDirectiveNumber(startMark);
        if (this.reader.peek() != '.') {
            throw new ScannerException("while scanning a directive", startMark, "expected a digit or '.', but found " + this.reader.peek() + "(" + (int)this.reader.peek() + ")", this.reader.getMark());
        }
        this.reader.forward();
        final Integer minor = this.scanYamlDirectiveNumber(startMark);
        if ("\u0000 \r\n\u0085\u2028\u2029".indexOf(this.reader.peek()) == -1) {
            throw new ScannerException("while scanning a directive", startMark, "expected a digit or ' ', but found " + this.reader.peek() + "(" + (int)this.reader.peek() + ")", this.reader.getMark());
        }
        final List<Integer> result = new ArrayList<Integer>(2);
        result.add(major);
        result.add(minor);
        return result;
    }
    
    private Integer scanYamlDirectiveNumber(final Mark startMark) {
        final char ch = this.reader.peek();
        if (!Character.isDigit(ch)) {
            throw new ScannerException("while scanning a directive", startMark, "expected a digit, but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        int length;
        for (length = 0; Character.isDigit(this.reader.peek(length)); ++length) {}
        final Integer value = new Integer(this.reader.prefix(length));
        this.reader.forward(length);
        return value;
    }
    
    private List<String> scanTagDirectiveValue(final Mark startMark) {
        while (this.reader.peek() == ' ') {
            this.reader.forward();
        }
        final String handle = this.scanTagDirectiveHandle(startMark);
        while (this.reader.peek() == ' ') {
            this.reader.forward();
        }
        final String prefix = this.scanTagDirectivePrefix(startMark);
        final List<String> result = new ArrayList<String>(2);
        result.add(handle);
        result.add(prefix);
        return result;
    }
    
    private String scanTagDirectiveHandle(final Mark startMark) {
        final String value = this.scanTagHandle("directive", startMark);
        final char ch = this.reader.peek();
        if (ch != ' ') {
            throw new ScannerException("while scanning a directive", startMark, "expected ' ', but found " + this.reader.peek() + "(" + ch + ")", this.reader.getMark());
        }
        return value;
    }
    
    private String scanTagDirectivePrefix(final Mark startMark) {
        final String value = this.scanTagUri("directive", startMark);
        if ("\u0000 \r\n\u0085\u2028\u2029".indexOf(this.reader.peek()) == -1) {
            throw new ScannerException("while scanning a directive", startMark, "expected ' ', but found " + this.reader.peek() + "(" + (int)this.reader.peek() + ")", this.reader.getMark());
        }
        return value;
    }
    
    private String scanDirectiveIgnoredLine(final Mark startMark) {
        while (this.reader.peek() == ' ') {
            this.reader.forward();
        }
        if (this.reader.peek() == '#') {
            while ("\u0000\r\n\u0085\u2028\u2029".indexOf(this.reader.peek()) == -1) {
                this.reader.forward();
            }
        }
        final char ch = this.reader.peek();
        if ("\u0000\r\n\u0085\u2028\u2029".indexOf(ch) == -1) {
            throw new ScannerException("while scanning a directive", startMark, "expected a comment or a line break, but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        return this.scanLineBreak();
    }
    
    private Token scanAnchor(final boolean isAnchor) {
        final Mark startMark = this.reader.getMark();
        final char indicator = this.reader.peek();
        final String name = (indicator == '*') ? "alias" : "anchor";
        this.reader.forward();
        int length;
        char ch;
        for (length = 0, ch = this.reader.peek(length); "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_".indexOf(ch) != -1; ch = this.reader.peek(length)) {
            ++length;
        }
        if (length == 0) {
            throw new ScannerException("while scanning an " + name, startMark, "expected alphabetic or numeric character, but found but found " + ch, this.reader.getMark());
        }
        final String value = this.reader.prefix(length);
        this.reader.forward(length);
        ch = this.reader.peek();
        if ("\u0000 \t\r\n\u0085\u2028\u2029?:,]}%@`".indexOf(ch) == -1) {
            throw new ScannerException("while scanning an " + name, startMark, "expected alphabetic or numeric character, but found " + ch + "(" + (int)this.reader.peek() + ")", this.reader.getMark());
        }
        final Mark endMark = this.reader.getMark();
        Token tok;
        if (isAnchor) {
            tok = new AnchorToken(value, startMark, endMark);
        }
        else {
            tok = new AliasToken(value, startMark, endMark);
        }
        return tok;
    }
    
    private Token scanTag() {
        final Mark startMark = this.reader.getMark();
        char ch = this.reader.peek(1);
        String handle = null;
        String suffix = null;
        if (ch == '<') {
            this.reader.forward(2);
            suffix = this.scanTagUri("tag", startMark);
            if (this.reader.peek() != '>') {
                throw new ScannerException("while scanning a tag", startMark, "expected '>', but found " + this.reader.peek() + "(" + (int)this.reader.peek() + ")", this.reader.getMark());
            }
            this.reader.forward();
        }
        else if ("\u0000 \t\r\n\u0085\u2028\u2029".indexOf(ch) != -1) {
            suffix = "!";
            this.reader.forward();
        }
        else {
            int length = 1;
            boolean useHandle = false;
            while ("\u0000 \r\n\u0085\u2028\u2029".indexOf(ch) == -1) {
                if (ch == '!') {
                    useHandle = true;
                    break;
                }
                ++length;
                ch = this.reader.peek(length);
            }
            handle = "!";
            if (useHandle) {
                handle = this.scanTagHandle("tag", startMark);
            }
            else {
                handle = "!";
                this.reader.forward();
            }
            suffix = this.scanTagUri("tag", startMark);
        }
        ch = this.reader.peek();
        if ("\u0000 \r\n\u0085\u2028\u2029".indexOf(ch) == -1) {
            throw new ScannerException("while scanning a tag", startMark, "expected ' ', but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        final String[] value = { handle, suffix };
        final Mark endMark = this.reader.getMark();
        return new TagToken(value, startMark, endMark);
    }
    
    private Token scanBlockScalar(final char style) {
        final boolean folded = style == '>';
        final StringBuffer chunks = new StringBuffer();
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Object[] chompi = this.scanBlockScalarIndicators(startMark);
        final Boolean chomping = (Boolean)chompi[0];
        final int increment = (int)chompi[1];
        this.scanBlockScalarIgnoredLine(startMark);
        int minIndent = this.indent + 1;
        if (minIndent < 1) {
            minIndent = 1;
        }
        String breaks = null;
        int maxIndent = 0;
        int indent = 0;
        Mark endMark;
        if (increment == -1) {
            final Object[] brme = this.scanBlockScalarIndentation();
            breaks = (String)brme[0];
            maxIndent = (int)brme[1];
            endMark = (Mark)brme[2];
            indent = Math.max(minIndent, maxIndent);
        }
        else {
            indent = minIndent + increment - 1;
            final Object[] brme = this.scanBlockScalarBreaks(indent);
            breaks = (String)brme[0];
            endMark = (Mark)brme[1];
        }
        String lineBreak = "";
        while (this.reader.getColumn() == indent && this.reader.peek() != '\0') {
            chunks.append(breaks);
            final boolean leadingNonSpace = " \t".indexOf(this.reader.peek()) == -1;
            int length;
            for (length = 0; "\u0000\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(length)) == -1; ++length) {}
            chunks.append(this.reader.prefix(length));
            this.reader.forward(length);
            lineBreak = this.scanLineBreak();
            final Object[] brme2 = this.scanBlockScalarBreaks(indent);
            breaks = (String)brme2[0];
            endMark = (Mark)brme2[1];
            if (this.reader.getColumn() != indent || this.reader.peek() == '\0') {
                break;
            }
            if (folded && lineBreak.equals("\n") && leadingNonSpace && " \t".indexOf(this.reader.peek()) == -1) {
                if (breaks.length() != 0) {
                    continue;
                }
                chunks.append(" ");
            }
            else {
                chunks.append(lineBreak);
            }
        }
        if (chomping == null || chomping) {
            chunks.append(lineBreak);
        }
        if (chomping != null && chomping) {
            chunks.append(breaks);
        }
        return new ScalarToken(chunks.toString(), false, startMark, endMark, style);
    }
    
    private Object[] scanBlockScalarIndicators(final Mark startMark) {
        Boolean chomping = null;
        int increment = -1;
        char ch = this.reader.peek();
        if (ch == '-' || ch == '+') {
            if (ch == '+') {
                chomping = Boolean.TRUE;
            }
            else {
                chomping = Boolean.FALSE;
            }
            this.reader.forward();
            ch = this.reader.peek();
            if (Character.isDigit(ch)) {
                increment = Integer.parseInt(String.valueOf(ch));
                if (increment == 0) {
                    throw new ScannerException("while scanning a block scalar", startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
                }
                this.reader.forward();
            }
        }
        else if (Character.isDigit(ch)) {
            increment = Integer.parseInt(String.valueOf(ch));
            if (increment == 0) {
                throw new ScannerException("while scanning a block scalar", startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
            }
            this.reader.forward();
            ch = this.reader.peek();
            if (ch == '-' || ch == '+') {
                if (ch == '+') {
                    chomping = Boolean.TRUE;
                }
                else {
                    chomping = Boolean.FALSE;
                }
                this.reader.forward();
            }
        }
        ch = this.reader.peek();
        if ("\u0000 \r\n\u0085\u2028\u2029".indexOf(ch) == -1) {
            throw new ScannerException("while scanning a block scalar", startMark, "expected chomping or indentation indicators, but found " + ch, this.reader.getMark());
        }
        return new Object[] { chomping, new Integer(increment) };
    }
    
    private String scanBlockScalarIgnoredLine(final Mark startMark) {
        while (this.reader.peek() == ' ') {
            this.reader.forward();
        }
        if (this.reader.peek() == '#') {
            while ("\u0000\r\n\u0085\u2028\u2029".indexOf(this.reader.peek()) == -1) {
                this.reader.forward();
            }
        }
        final char ch = this.reader.peek();
        if ("\u0000\r\n\u0085\u2028\u2029".indexOf(ch) == -1) {
            throw new ScannerException("while scanning a block scalar", startMark, "expected a comment or a line break, but found " + ch, this.reader.getMark());
        }
        return this.scanLineBreak();
    }
    
    private Object[] scanBlockScalarIndentation() {
        final StringBuffer chunks = new StringBuffer();
        int maxIndent = 0;
        Mark endMark = this.reader.getMark();
        while (" \r\n\u0085\u2028\u2029".indexOf(this.reader.peek()) != -1) {
            if (this.reader.peek() != ' ') {
                chunks.append(this.scanLineBreak());
                endMark = this.reader.getMark();
            }
            else {
                this.reader.forward();
                if (this.reader.getColumn() <= maxIndent) {
                    continue;
                }
                maxIndent = this.reader.getColumn();
            }
        }
        return new Object[] { chunks.toString(), new Integer(maxIndent), endMark };
    }
    
    private Object[] scanBlockScalarBreaks(final int indent) {
        final StringBuffer chunks = new StringBuffer();
        Mark endMark = this.reader.getMark();
        while (this.reader.getColumn() < indent && this.reader.peek() == ' ') {
            this.reader.forward();
        }
        while ("\r\n\u0085\u2028\u2029".indexOf(this.reader.peek()) != -1) {
            chunks.append(this.scanLineBreak());
            endMark = this.reader.getMark();
            while (this.reader.getColumn() < indent && this.reader.peek() == ' ') {
                this.reader.forward();
            }
        }
        return new Object[] { chunks.toString(), endMark };
    }
    
    private Token scanFlowScalar(final char style) {
        final boolean _double = style == '\"';
        final StringBuffer chunks = new StringBuffer();
        final Mark startMark = this.reader.getMark();
        final char quote = this.reader.peek();
        this.reader.forward();
        chunks.append(this.scanFlowScalarNonSpaces(_double, startMark));
        while (this.reader.peek() != quote) {
            chunks.append(this.scanFlowScalarSpaces(startMark));
            chunks.append(this.scanFlowScalarNonSpaces(_double, startMark));
        }
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        return new ScalarToken(chunks.toString(), false, startMark, endMark, style);
    }
    
    private String scanFlowScalarNonSpaces(final boolean _double, final Mark startMark) {
        final StringBuffer chunks = new StringBuffer();
        while (true) {
            int length;
            for (length = 0; "'\"\\\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(length)) == -1; ++length) {}
            if (length != 0) {
                chunks.append(this.reader.prefix(length));
                this.reader.forward(length);
            }
            char ch = this.reader.peek();
            if (!_double && ch == '\'' && this.reader.peek(1) == '\'') {
                chunks.append("'");
                this.reader.forward(2);
            }
            else if ((_double && ch == '\'') || (!_double && "\"\\".indexOf(ch) != -1)) {
                chunks.append(ch);
                this.reader.forward();
            }
            else {
                if (!_double || ch != '\\') {
                    return chunks.toString();
                }
                this.reader.forward();
                ch = this.reader.peek();
                if (ScannerImpl.ESCAPE_REPLACEMENTS.containsKey(new Character(ch))) {
                    chunks.append(ScannerImpl.ESCAPE_REPLACEMENTS.get(new Character(ch)));
                    this.reader.forward();
                }
                else if (ScannerImpl.ESCAPE_CODES.containsKey(new Character(ch))) {
                    length = ScannerImpl.ESCAPE_CODES.get(new Character(ch));
                    this.reader.forward();
                    final String val = this.reader.prefix(length);
                    if (ScannerImpl.NOT_HEXA.matcher(val).find()) {
                        throw new ScannerException("while scanning a double-quoted scalar", startMark, "expected escape sequence of " + length + " hexadecimal numbers, but found: " + val, this.reader.getMark());
                    }
                    final char unicode = (char)Integer.parseInt(val, 16);
                    chunks.append(unicode);
                    this.reader.forward(length);
                }
                else {
                    if ("\r\n\u0085\u2028\u2029".indexOf(ch) == -1) {
                        throw new ScannerException("while scanning a double-quoted scalar", startMark, "found unknown escape character " + ch + "(" + (int)ch + ")", this.reader.getMark());
                    }
                    this.scanLineBreak();
                    chunks.append(this.scanFlowScalarBreaks(startMark));
                }
            }
        }
    }
    
    private String scanFlowScalarSpaces(final Mark startMark) {
        final StringBuffer chunks = new StringBuffer();
        int length;
        for (length = 0; " \t".indexOf(this.reader.peek(length)) != -1; ++length) {}
        final String whitespaces = this.reader.prefix(length);
        this.reader.forward(length);
        final char ch = this.reader.peek();
        if (ch == '\0') {
            throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected end of stream", this.reader.getMark());
        }
        if ("\r\n\u0085\u2028\u2029".indexOf(ch) != -1) {
            final String lineBreak = this.scanLineBreak();
            final String breaks = this.scanFlowScalarBreaks(startMark);
            if (!lineBreak.equals("\n")) {
                chunks.append(lineBreak);
            }
            else if (breaks.length() == 0) {
                chunks.append(" ");
            }
            chunks.append(breaks);
        }
        else {
            chunks.append(whitespaces);
        }
        return chunks.toString();
    }
    
    private String scanFlowScalarBreaks(final Mark startMark) {
        final StringBuffer chunks = new StringBuffer();
        while (true) {
            final String prefix = this.reader.prefix(3);
            if ((prefix.equals("---") || prefix.equals("...")) && "\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(3)) != -1) {
                throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected document separator", this.reader.getMark());
            }
            while (" \t".indexOf(this.reader.peek()) != -1) {
                this.reader.forward();
            }
            if ("\r\n\u0085\u2028\u2029".indexOf(this.reader.peek()) == -1) {
                return chunks.toString();
            }
            chunks.append(this.scanLineBreak());
        }
    }
    
    private Token scanPlain() {
        final StringBuffer chunks = new StringBuffer();
        Mark endMark;
        final Mark startMark = endMark = this.reader.getMark();
        final int indent = this.indent + 1;
        String spaces = "";
        do {
            int length = 0;
            if (this.reader.peek() == '#') {
                break;
            }
            char ch;
            while (true) {
                ch = this.reader.peek(length);
                if ("\u0000 \t\r\n\u0085\u2028\u2029".indexOf(ch) != -1 || (this.flowLevel == 0 && ch == ':' && "\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(length + 1)) != -1) || (this.flowLevel != 0 && ",:?[]{}".indexOf(ch) != -1)) {
                    break;
                }
                ++length;
            }
            if (this.flowLevel != 0 && ch == ':' && "\u0000 \t\r\n\u0085\u2028\u2029,[]{}".indexOf(this.reader.peek(length + 1)) == -1) {
                this.reader.forward(length);
                throw new ScannerException("while scanning a plain scalar", startMark, "found unexpected ':'", this.reader.getMark(), "Please check http://pyyaml.org/wiki/YAMLColonInFlowContext for details.");
            }
            if (length == 0) {
                break;
            }
            this.allowSimpleKey = false;
            chunks.append(spaces);
            chunks.append(this.reader.prefix(length));
            this.reader.forward(length);
            endMark = this.reader.getMark();
            spaces = this.scanPlainSpaces();
            if ("".equals(spaces) || this.reader.peek() == '#') {
                break;
            }
        } while (this.flowLevel != 0 || this.reader.getColumn() >= indent);
        return new ScalarToken(chunks.toString(), startMark, endMark, true);
    }
    
    private String scanPlainSpaces() {
        final StringBuffer chunks = new StringBuffer();
        int length;
        for (length = 0; this.reader.peek(length) == ' '; ++length) {}
        final String whitespaces = this.reader.prefix(length);
        this.reader.forward(length);
        final char ch = this.reader.peek();
        if ("\r\n\u0085\u2028\u2029".indexOf(ch) != -1) {
            final String lineBreak = this.scanLineBreak();
            this.allowSimpleKey = true;
            String prefix = this.reader.prefix(3);
            if ("---".equals(prefix) || ("...".equals(prefix) && "\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(3)) != -1)) {
                return "";
            }
            final StringBuffer breaks = new StringBuffer();
            while (" \r\n\u0085\u2028\u2029".indexOf(this.reader.peek()) != -1) {
                if (this.reader.peek() == ' ') {
                    this.reader.forward();
                }
                else {
                    breaks.append(this.scanLineBreak());
                    prefix = this.reader.prefix(3);
                    if ("---".equals(prefix) || ("...".equals(prefix) && "\u0000 \t\r\n\u0085\u2028\u2029".indexOf(this.reader.peek(3)) != -1)) {
                        return "";
                    }
                    continue;
                }
            }
            if (!lineBreak.equals("\n")) {
                chunks.append(lineBreak);
            }
            else if (breaks == null || breaks.toString().equals("")) {
                chunks.append(" ");
            }
            chunks.append(breaks);
        }
        else {
            chunks.append(whitespaces);
        }
        return chunks.toString();
    }
    
    private String scanTagHandle(final String name, final Mark startMark) {
        char ch = this.reader.peek();
        if (ch != '!') {
            throw new ScannerException("while scanning a " + name, startMark, "expected '!', but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        int length = 1;
        ch = this.reader.peek(length);
        if (ch != ' ') {
            while ("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_".indexOf(ch) != -1) {
                ++length;
                ch = this.reader.peek(length);
            }
            if (ch != '!') {
                this.reader.forward(length);
                throw new ScannerException("while scanning a " + name, startMark, "expected '!', but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
            }
            ++length;
        }
        final String value = this.reader.prefix(length);
        this.reader.forward(length);
        return value;
    }
    
    private String scanTagUri(final String name, final Mark startMark) {
        final StringBuffer chunks = new StringBuffer();
        int length;
        char ch;
        for (length = 0, ch = this.reader.peek(length); "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_".indexOf(ch) != -1 || "-;/?:@&=+$,_.!~*'()[]%".indexOf(ch) != -1; ch = this.reader.peek(length)) {
            if (ch == '%') {
                chunks.append(this.reader.prefix(length));
                this.reader.forward(length);
                length = 0;
                chunks.append(this.scanUriEscapes(name, startMark));
            }
            else {
                ++length;
            }
        }
        if (length != 0) {
            chunks.append(this.reader.prefix(length));
            this.reader.forward(length);
            length = 0;
        }
        if (chunks.length() == 0) {
            throw new ScannerException("while scanning a " + name, startMark, "expected URI, but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        return chunks.toString();
    }
    
    private String scanUriEscapes(final String name, final Mark startMark) {
        final StringBuffer bytes = new StringBuffer();
        while (this.reader.peek() == '%') {
            this.reader.forward();
            try {
                bytes.append(Integer.parseInt(this.reader.prefix(2), 16));
            }
            catch (NumberFormatException nfe) {
                throw new ScannerException("while scanning a " + name, startMark, "expected URI escape sequence of 2 hexadecimal numbers, but found " + this.reader.peek(1) + "(" + (int)this.reader.peek(1) + ") and " + this.reader.peek(2) + "(" + (int)this.reader.peek(2) + ")", this.reader.getMark());
            }
            this.reader.forward(2);
        }
        return bytes.toString();
    }
    
    private String scanLineBreak() {
        final char ch = this.reader.peek();
        if ("\r\n\u0085".indexOf(ch) != -1) {
            if ("\r\n".equals(this.reader.prefix(2))) {
                this.reader.forward(2);
            }
            else {
                this.reader.forward();
            }
            return "\n";
        }
        if ("\u2028\u2029".indexOf(ch) != -1) {
            this.reader.forward();
            return String.valueOf(ch);
        }
        return "";
    }
    
    static {
        NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
        ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
        ESCAPE_CODES = new HashMap<Character, Integer>();
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('0'), "\u0000");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('a'), "\u0007");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('b'), "\b");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('t'), "\t");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('n'), "\n");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('v'), "\u000b");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('f'), "\f");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('r'), "\r");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('e'), "\u001b");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character(' '), " ");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('\"'), "\"");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('\\'), "\\");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('N'), "\u0085");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('_'), " ");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('L'), "\u2028");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('P'), "\u2029");
        ScannerImpl.ESCAPE_CODES.put(new Character('x'), new Integer(2));
        ScannerImpl.ESCAPE_CODES.put(new Character('u'), new Integer(4));
        ScannerImpl.ESCAPE_CODES.put(new Character('U'), new Integer(8));
    }
}
