package ml.docilealligator.infinityforreddit.markdown;

import org.commonmark.internal.InlineParserImpl;
import org.commonmark.node.Node;
import org.commonmark.parser.InlineParserContext;

import java.util.regex.Pattern;

public class CustomInlineParser extends InlineParserImpl {
    private static final Pattern SUPERSCRIPT_PATTERN = Pattern.compile("(?:\\^\\((.+?)\\))|(?:\\^(\\S+))");
    private String input;
    private int index;

    public CustomInlineParser(InlineParserContext inlineParserContext) {
        super(inlineParserContext);
    }

    @Override
    public void parse(String content, Node block) {
        reset(content);

        Node previous = null;
        int start = 0;
        while (true) {
            start = index;
            Node node = parseInline(previous);
            previous = node;
            if (node != null) {
                block.appendChild(node);
                int oldLength = input.length();
                input = input.substring(0, start) + content.substring(index, oldLength);
                index += start - index - 1;
            }
            index++;
            if(index >= input.length()) {
                break;
            }
        }
;        super.parse(input, block);
    }

    private Node parseInline(Node previous) {
        char c = peek();
        if (c == '\0') {
            return null;
        }

        Node node;
        if (c == '^') {
            node = parseSuperscript();
            if (node != null) {
                if (previous instanceof Superscript) {
                    previous.appendChild(node);
                    return previous;
                } else {
                    return node;
                }
            }

        }
        return null;
    }

    private Superscript parseSuperscript() {
        int start = index;
        int length = input.length();
        int caret_pos = -1;
        int nCarets = 0;
        int new_lines = 0;
        boolean hasBracket = false;
        for (int i = start; i < length; i++) {
            char currentChar = input.charAt(i);
            if (currentChar == '\n') {
                new_lines++;
                if (new_lines > 0 && nCarets > 0 || hasBracket) {
                    break;
                }
            } else if ((i + 1) < length
                    && nCarets == 0
                    && !hasBracket
                    && !(i > 0 && input.charAt(i - 1) == '\\')
                    && currentChar == '^'
                    && input.charAt(i + 1) != '\n') {
                if (input.charAt(i + 1) == '(') {
                    hasBracket = true;
                }
                caret_pos = i;
                nCarets++;
            } else if (nCarets > 0) {
                if (hasBracket
                        && (i > 0)
                        && currentChar == ')'
                        && input.charAt(i - 1) != '\\') {
                    index = i + 1;
                    var superscriptNode = new Superscript();
                    superscriptNode.setLiteral(input.substring(caret_pos + 2, i));
                    return superscriptNode;
                } else if (!hasBracket && Character.isWhitespace(currentChar)) {
                    index = i;
                    var superscriptNode = new Superscript();
                    superscriptNode.setLiteral(input.substring(caret_pos + 1, i));
                    return superscriptNode;
                } else if (!hasBracket && (i == length - 1)) {
                    index = i + 1;
                    var superscriptNode = new Superscript();
                    superscriptNode.setLiteral(input.substring(caret_pos + 1, i + 1));
                    return superscriptNode;
                } else if(currentChar == '^') {
                    index = i;
                    var superscriptNode = new Superscript();
                    superscriptNode.setLiteral(input.substring(caret_pos + 1, i));
                    return superscriptNode;
                }
            }
        }
        return null;
    }

    void reset(String content) {
        this.input = content;
        this.index = 0;
    }

    private char peek() {
        if (index < input.length()) {
            return input.charAt(index);
        } else {
            return '\0';
        }
    }
}
