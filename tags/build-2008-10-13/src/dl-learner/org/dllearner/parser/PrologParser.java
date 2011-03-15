/* Generated By:JavaCC: Do not edit this line. PrologParser.java */
package org.dllearner.parser;

public @SuppressWarnings("all") class PrologParser implements PrologParserConstants {
    public PrologParser() {
        this(new java.io.StringReader(""));
    }

    public org.dllearner.prolog.Term parseTerm(String src) throws ParseException {
        reinitToString(src);
        return term();
    }

    public java.util.ArrayList parseTermList(String src) throws ParseException {
        reinitToString(src);
        return termList();
    }

    public org.dllearner.prolog.Atom parseAtom(String src) throws ParseException {
        reinitToString(src);
        return atom();
    }

    public org.dllearner.prolog.Clause parseClause(String src) throws ParseException {
        reinitToString(src);
        return clause();
    }

    public org.dllearner.prolog.Program parseProgram(String src) throws ParseException {
        reinitToString(src);
        return program();
    }

    private void reinitToString(String src) {
        ReInit(new java.io.StringReader(src));
    }

//////////////////////////////////////////////////////////////////////////////////////////
  final public org.dllearner.prolog.Program program() throws ParseException {
    org.dllearner.prolog.Program p = new org.dllearner.prolog.Program();
    org.dllearner.prolog.Clause c;
    label_1:
    while (true) {
      c = clause();
                     p.addClause(c);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IDENTIFIER:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
    }
    jj_consume_token(0);
                                                  {if (true) return p;}
    throw new Error("Missing return statement in function");
  }

  final public org.dllearner.prolog.Clause clause() throws ParseException {
    org.dllearner.prolog.Atom head;
    org.dllearner.prolog.Body body = null;
    head = atom();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 19:
      jj_consume_token(19);
      body = body();
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    jj_consume_token(20);
                                                   {if (true) return new org.dllearner.prolog.Clause(head, body);}
    throw new Error("Missing return statement in function");
  }

  final public org.dllearner.prolog.Body body() throws ParseException {
    org.dllearner.prolog.Literal l;
    org.dllearner.prolog.Body b = new org.dllearner.prolog.Body();
    l = literal();
                    b.addLiteral(l);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 21:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
      jj_consume_token(21);
      l = literal();
                          b.addLiteral(l);
    }
      {if (true) return b;}
    throw new Error("Missing return statement in function");
  }

  final public org.dllearner.prolog.Atom atom() throws ParseException {
    Token atom;
    java.util.ArrayList arguments = new java.util.ArrayList() ;
    atom = jj_consume_token(IDENTIFIER);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 22:
      jj_consume_token(22);
      arguments = termList();
      jj_consume_token(23);
      break;
    default:
      jj_la1[3] = jj_gen;
      ;
    }
                                                             {if (true) return new org.dllearner.prolog.Atom(atom.image, arguments);}
    throw new Error("Missing return statement in function");
  }

  final public org.dllearner.prolog.Literal literal() throws ParseException {
    org.dllearner.prolog.Atom a;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENTIFIER:
      a = atom();
                 {if (true) return new org.dllearner.prolog.Literal(a, true);}
      break;
    case NOT:
      jj_consume_token(NOT);
      a = atom();
                         {if (true) return new org.dllearner.prolog.Literal(a, false);}
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public org.dllearner.prolog.Term term() throws ParseException {
    Token v;
    String o;
    Token f;
    java.util.ArrayList arguments = null;
    org.dllearner.prolog.Term t1, t2;
    if (jj_2_1(2147483647)) {
      o = prefixOp();
      t2 = simpleTerm();
                                                             {if (true) return new org.dllearner.prolog.Function(o, t2);}
    } else if (jj_2_2(2147483647)) {
      t1 = simpleTerm();
      o = infixOp();
      t2 = simpleTerm();
                                                                                {if (true) return new org.dllearner.prolog.Function(t1, o, t2);}
    } else if (jj_2_3(2147483647)) {
      t1 = simpleTerm();
      o = postfixOp();
                                                                {if (true) return new org.dllearner.prolog.Function(t1, o);}
    } else if (jj_2_4(2147483647)) {
      t1 = simpleTerm();
                                                {if (true) return t1;}
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public org.dllearner.prolog.Term simpleTerm() throws ParseException {
    Token v;
    String o;
    Token f;
    java.util.ArrayList arguments = null;
    org.dllearner.prolog.Term l;
    org.dllearner.prolog.Number n;
    if (jj_2_5(2)) {
      f = jj_consume_token(IDENTIFIER);
      jj_consume_token(22);
      arguments = termList();
      jj_consume_token(23);
                                                                   {if (true) return new org.dllearner.prolog.Function(f.image, arguments);}
    } else if (jj_2_6(2)) {
      f = jj_consume_token(IDENTIFIER);
                                      {if (true) return new org.dllearner.prolog.PrologConstant(f.image);}
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case VAR:
        v = jj_consume_token(VAR);
                  {if (true) return new org.dllearner.prolog.Variable(v.image);}
        break;
      case DOUBLE:
        v = jj_consume_token(DOUBLE);
                     {if (true) return new org.dllearner.prolog.Number(v.image);}
        break;
      case STRINGCONSTANT:
        v = jj_consume_token(STRINGCONSTANT);
                             {if (true) return new org.dllearner.prolog.StringConstant(v.image);}
        break;
      case 24:
      case 25:
        l = list();
                   {if (true) return l;}
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public String prefixOp() throws ParseException {
    Token f;
    f = jj_consume_token(OPERATOR);
                     {if (true) return f.image;}
    throw new Error("Missing return statement in function");
  }

  final public String infixOp() throws ParseException {
    Token f;
    f = jj_consume_token(OPERATOR);
                     {if (true) return f.image;}
    throw new Error("Missing return statement in function");
  }

  final public String postfixOp() throws ParseException {
    Token f;
    f = jj_consume_token(OPERATOR);
                     {if (true) return f.image;}
    throw new Error("Missing return statement in function");
  }

  final public java.util.ArrayList termList() throws ParseException {
    org.dllearner.prolog.Term t;
    java.util.ArrayList l = new java.util.ArrayList();
    t = term();
                       l.add(t);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 21:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_3;
      }
      jj_consume_token(21);
      t = term();
                       l.add(t);
    }
      {if (true) return l;}
    throw new Error("Missing return statement in function");
  }

  final public org.dllearner.prolog.List list() throws ParseException {
    java.util.ArrayList content = null;
    org.dllearner.prolog.Term head, tmp;
    org.dllearner.prolog.List l;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 24:
      jj_consume_token(24);
           {if (true) return new org.dllearner.prolog.List();}
      break;
    default:
      jj_la1[7] = jj_gen;
      if (jj_2_8(3)) {
        jj_consume_token(25);
        head = term();
        jj_consume_token(26);
                                           {if (true) return new org.dllearner.prolog.List(head, null);}
      } else if (jj_2_9(3)) {
        jj_consume_token(25);
        head = term();
        jj_consume_token(27);
        l = list();
        jj_consume_token(26);
                                                          {if (true) return new org.dllearner.prolog.List(head, l);}
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 25:
       content = new java.util.ArrayList();
          jj_consume_token(25);
          label_4:
          while (true) {
            if (jj_2_7(2)) {
              ;
            } else {
              break label_4;
            }
            tmp = term();
                                     content.add(tmp);
            jj_consume_token(21);
          }
          tmp = term();
                                                                          content.add(tmp);
          jj_consume_token(26);
        {if (true) return org.dllearner.prolog.List.compose(content);}
          break;
        default:
          jj_la1[8] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    }
    throw new Error("Missing return statement in function");
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  final private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  final private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  final private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  final private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  final private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  final private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  final private boolean jj_3R_5() {
    if (jj_scan_token(OPERATOR)) return true;
    return false;
  }

  final private boolean jj_3_7() {
    if (jj_3R_9()) return true;
    if (jj_scan_token(21)) return true;
    return false;
  }

  final private boolean jj_3R_13() {
    if (jj_3R_15()) return true;
    return false;
  }

  final private boolean jj_3R_12() {
    if (jj_scan_token(STRINGCONSTANT)) return true;
    return false;
  }

  final private boolean jj_3R_11() {
    if (jj_scan_token(DOUBLE)) return true;
    return false;
  }

  final private boolean jj_3R_10() {
    if (jj_scan_token(VAR)) return true;
    return false;
  }

  final private boolean jj_3_6() {
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  final private boolean jj_3R_18() {
    if (jj_scan_token(25)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_7()) { jj_scanpos = xsp; break; }
    }
    if (jj_3R_9()) return true;
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3_5() {
    if (jj_scan_token(IDENTIFIER)) return true;
    if (jj_scan_token(22)) return true;
    if (jj_3R_14()) return true;
    if (jj_scan_token(23)) return true;
    return false;
  }

  final private boolean jj_3R_6() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_5()) {
    jj_scanpos = xsp;
    if (jj_3_6()) {
    jj_scanpos = xsp;
    if (jj_3R_10()) {
    jj_scanpos = xsp;
    if (jj_3R_11()) {
    jj_scanpos = xsp;
    if (jj_3R_12()) {
    jj_scanpos = xsp;
    if (jj_3R_13()) return true;
    }
    }
    }
    }
    }
    return false;
  }

  final private boolean jj_3_9() {
    if (jj_scan_token(25)) return true;
    if (jj_3R_9()) return true;
    if (jj_scan_token(27)) return true;
    if (jj_3R_15()) return true;
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3_8() {
    if (jj_scan_token(25)) return true;
    if (jj_3R_9()) return true;
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3R_17() {
    if (jj_scan_token(24)) return true;
    return false;
  }

  final private boolean jj_3R_15() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_17()) {
    jj_scanpos = xsp;
    if (jj_3_8()) {
    jj_scanpos = xsp;
    if (jj_3_9()) {
    jj_scanpos = xsp;
    if (jj_3R_18()) return true;
    }
    }
    }
    return false;
  }

  final private boolean jj_3_4() {
    if (jj_3R_6()) return true;
    return false;
  }

  final private boolean jj_3R_16() {
    if (jj_scan_token(21)) return true;
    if (jj_3R_9()) return true;
    return false;
  }

  final private boolean jj_3_3() {
    if (jj_3R_6()) return true;
    if (jj_3R_8()) return true;
    return false;
  }

  final private boolean jj_3_2() {
    if (jj_3R_6()) return true;
    if (jj_3R_7()) return true;
    if (jj_3R_6()) return true;
    return false;
  }

  final private boolean jj_3R_14() {
    if (jj_3R_9()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_16()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_3R_5()) return true;
    if (jj_3R_6()) return true;
    return false;
  }

  final private boolean jj_3R_9() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_1()) {
    jj_scanpos = xsp;
    if (jj_3_2()) {
    jj_scanpos = xsp;
    if (jj_3_3()) {
    jj_scanpos = xsp;
    if (jj_3_4()) return true;
    }
    }
    }
    return false;
  }

  final private boolean jj_3R_8() {
    if (jj_scan_token(OPERATOR)) return true;
    return false;
  }

  final private boolean jj_3R_7() {
    if (jj_scan_token(OPERATOR)) return true;
    return false;
  }

  public PrologParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[9];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x2000,0x80000,0x200000,0x400000,0x2080,0x3001900,0x200000,0x1000000,0x2000000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[9];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public PrologParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public PrologParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new PrologParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public PrologParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new PrologParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public PrologParser(PrologParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(PrologParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector<int[]> jj_expentries = new java.util.Vector<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[28];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 9; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 28; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 9; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}