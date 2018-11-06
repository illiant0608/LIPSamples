package BackTrackingParser;

public class BackTrackParser extends Parser {
    public BackTrackParser(Lexer input) { super(input); }

    /** stat : list EOF | assign EOF ; */
    public void stat() throws RecognitionException {
        // attempt alternative 1: list EOF
        if ( speculate_stat_alt1() ) {
            list(); match(Lexer.EOF_TYPE);
        }
        // attempt alternative 2: assign EOF
        else if ( speculate_stat_alt2() ) {
            assign(); match(Lexer.EOF_TYPE);
        }

        // must be an error; neither matched; LT(1) is lookahead token 1
        else
            throw new NoViableAltException("No matching alts, parse failed.");
    }

    public boolean speculate_stat_alt1() {
        boolean success = true;
        mark(); // mark this spot in input so we can rewind
        try { list(); match(Lexer.EOF_TYPE); }
        catch (RecognitionException e) {
            System.out.println("alt1 not match, rewind.");
            success = false;
        }
        release(); // either way, rewind to where we were
        return success;
    }

    public boolean speculate_stat_alt2() {
        boolean success = true;
        mark(); // mark this spot in input so we can rewind
        try { assign(); match(Lexer.EOF_TYPE); }
        catch (RecognitionException e) {
            System.out.println("alt2 not match, fail.");
            success = false;
        }
        release(); // either way, rewind to where we were
        return success;
    }

    /** assign : list '=' list ; // parallel assignment */
    public void assign() throws RecognitionException {
        list(); match(BacktrackLexer.EQUALS); list();
    }

    /** list : '[' elements ']' ; // match bracketed list */
    public void list() throws RecognitionException {
        match(BacktrackLexer.LBRACK); elements(); match(BacktrackLexer.RBRACK);
    }

    /** elements : element (',' element)* ; // match comma-separated list */
    void elements() throws RecognitionException {
        element(); while ( LA(1)== BacktrackLexer.COMMA ) { match(BacktrackLexer.COMMA); element(); }
    }

    /** element : name '=' NAME | NAME | list ; // assignment, name or list */
    void element() throws RecognitionException {
        if ( LA(1)==BacktrackLexer.NAME && LA(2)==BacktrackLexer.EQUALS) {
            match(BacktrackLexer.NAME);
            match(BacktrackLexer.EQUALS);
            match(BacktrackLexer.NAME);
        }
        else if ( LA(1)==BacktrackLexer.NAME ) match(BacktrackLexer.NAME);
        else if ( LA(1)==BacktrackLexer.LBRACK ) list();
        else throw new NoViableAltException("expecting element found "+LT(1));
    }
}
