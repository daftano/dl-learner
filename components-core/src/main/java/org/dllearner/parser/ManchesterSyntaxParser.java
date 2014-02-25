/* Generated By:JavaCC: Do not edit this line. ManchesterSyntaxParser.java */
package org.dllearner.parser;

import java.io.StringReader;

import org.dllearner.core.owl.DatatypeProperty;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Intersection;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Negation;
import org.dllearner.core.owl.Nothing;
import org.dllearner.core.owl.ObjectAllRestriction;
import org.dllearner.core.owl.ObjectMaxCardinalityRestriction;
import org.dllearner.core.owl.ObjectMinCardinalityRestriction;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.core.owl.ObjectSomeRestriction;
import org.dllearner.core.owl.ObjectValueRestriction;
import org.dllearner.core.owl.StringValueRestriction;
import org.dllearner.core.owl.Thing;
import org.dllearner.core.owl.Union;

public class ManchesterSyntaxParser implements ManchesterSyntaxParserConstants {

        public static Description parseClassExpression(String classExpression) throws ParseException {
                ManchesterSyntaxParser parser = new ManchesterSyntaxParser(new StringReader(classExpression));
                return parser.ClassExpression();
        }

  final public Description ClassExpression() throws ParseException {
        Description c,c1,c2;
        String s,s1,s2;
        int i;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TOP:
      jj_consume_token(TOP);
           {if (true) return new Thing();}
      break;
    case BOTTOM:
      jj_consume_token(BOTTOM);
              {if (true) return new Nothing();}
      break;
    default:
      jj_la1[0] = jj_gen;
      if (jj_2_1(2147483647)) {
        jj_consume_token(22);
        c1 = ClassExpression();
        jj_consume_token(AND);
        c2 = ClassExpression();
        jj_consume_token(23);
         {if (true) return new Intersection(c1,c2);}
      } else if (jj_2_2(2147483647)) {
        jj_consume_token(22);
        c1 = ClassExpression();
        jj_consume_token(OR);
        c2 = ClassExpression();
        jj_consume_token(23);
         {if (true) return new Union(c1,c2);}
      } else if (jj_2_3(2147483647)) {
        jj_consume_token(22);
        s = URI();
        jj_consume_token(SOME);
        c = ClassExpression();
        jj_consume_token(23);
         {if (true) return new ObjectSomeRestriction(new ObjectProperty(s),c);}
      } else if (jj_2_4(2147483647)) {
        jj_consume_token(22);
        s = URI();
        jj_consume_token(ONLY);
        c = ClassExpression();
        jj_consume_token(23);
         {if (true) return new ObjectAllRestriction(new ObjectProperty(s),c);}
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NOT:
          jj_consume_token(NOT);
          c = ClassExpression();
         {if (true) return new Negation(c);}
          break;
        case GE:
          jj_consume_token(GE);
          i = Integer();
          s = URI();
          jj_consume_token(24);
          c = ClassExpression();
         {if (true) return new ObjectMinCardinalityRestriction(i,new ObjectProperty(s),c);}
          break;
        case LE:
          jj_consume_token(LE);
          i = Integer();
          s = URI();
          jj_consume_token(24);
          c = ClassExpression();
         {if (true) return new ObjectMaxCardinalityRestriction(i,new ObjectProperty(s),c);}
          break;
        default:
          jj_la1[1] = jj_gen;
          if (jj_2_5(4)) {
            jj_consume_token(22);
            s1 = URI();
            jj_consume_token(25);
            s2 = URI();
            jj_consume_token(23);
      {if (true) return new ObjectValueRestriction(new ObjectProperty(s1), new Individual(s2));}
          } else if (jj_2_6(4)) {
            jj_consume_token(22);
            s1 = URI();
            jj_consume_token(25);
            s2 = String();
            jj_consume_token(23);
      {if (true) return new StringValueRestriction(new DatatypeProperty(s1), s2);}
          } else {
            switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
            case URI:
            case URI_PREFIX:
              s = URI();
               {if (true) return new NamedClass(s);}
              break;
            default:
              jj_la1[2] = jj_gen;
              jj_consume_token(-1);
              throw new ParseException();
            }
          }
        }
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public double Double() throws ParseException {
  Token t;
    t = jj_consume_token(DOUBLE);
    {if (true) return new Double(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public int Integer() throws ParseException {
  Token t;
    t = jj_consume_token(NUMBER);
    {if (true) return new Integer(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public String String() throws ParseException {
  Token t;
  String s;
    t = jj_consume_token(STRING);
    // cut quotes
    s = t.image;
    s = s.substring(1, s.length() - 1);
    {if (true) return s;}
    throw new Error("Missing return statement in function");
  }

  final public String URI() throws ParseException {
  Token t;
  String s;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case URI_PREFIX:
      // LOOKAHEAD("<")
        t = jj_consume_token(URI_PREFIX);
    // cut "<" and ">"
    s = t.image;
    s = s.substring(1, s.length() - 1);
    {if (true) return s;}
      break;
    case URI:
      t = jj_consume_token(URI);
               {if (true) return t.image;}
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_3R_7() {
    if (jj_scan_token(22)) return true;
    if (jj_3R_1()) return true;
    if (jj_scan_token(OR)) return true;
    if (jj_3R_1()) return true;
    if (jj_scan_token(23)) return true;
    return false;
  }

  private boolean jj_3R_6() {
    if (jj_scan_token(22)) return true;
    if (jj_3R_1()) return true;
    if (jj_scan_token(AND)) return true;
    if (jj_3R_1()) return true;
    if (jj_scan_token(23)) return true;
    return false;
  }

  private boolean jj_3R_5() {
    if (jj_scan_token(BOTTOM)) return true;
    return false;
  }

  private boolean jj_3R_1() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_4()) {
    jj_scanpos = xsp;
    if (jj_3R_5()) {
    jj_scanpos = xsp;
    if (jj_3R_6()) {
    jj_scanpos = xsp;
    if (jj_3R_7()) {
    jj_scanpos = xsp;
    if (jj_3R_8()) {
    jj_scanpos = xsp;
    if (jj_3R_9()) {
    jj_scanpos = xsp;
    if (jj_3R_10()) {
    jj_scanpos = xsp;
    if (jj_3R_11()) {
    jj_scanpos = xsp;
    if (jj_3R_12()) {
    jj_scanpos = xsp;
    if (jj_3_5()) {
    jj_scanpos = xsp;
    if (jj_3_6()) {
    jj_scanpos = xsp;
    if (jj_3R_13()) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3R_4() {
    if (jj_scan_token(TOP)) return true;
    return false;
  }

  private boolean jj_3R_16() {
    if (jj_scan_token(NUMBER)) return true;
    return false;
  }

  private boolean jj_3R_15() {
    if (jj_scan_token(URI)) return true;
    return false;
  }

  private boolean jj_3R_13() {
    if (jj_3R_2()) return true;
    return false;
  }

  private boolean jj_3R_14() {
    if (jj_scan_token(URI_PREFIX)) return true;
    return false;
  }

  private boolean jj_3R_2() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_14()) {
    jj_scanpos = xsp;
    if (jj_3R_15()) return true;
    }
    return false;
  }

  private boolean jj_3_6() {
    if (jj_scan_token(22)) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(25)) return true;
    if (jj_3R_3()) return true;
    if (jj_scan_token(23)) return true;
    return false;
  }

  private boolean jj_3_4() {
    if (jj_scan_token(22)) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(ONLY)) return true;
    return false;
  }

  private boolean jj_3_5() {
    if (jj_scan_token(22)) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(25)) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(23)) return true;
    return false;
  }

  private boolean jj_3_3() {
    if (jj_scan_token(22)) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(SOME)) return true;
    return false;
  }

  private boolean jj_3R_12() {
    if (jj_scan_token(LE)) return true;
    if (jj_3R_16()) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(24)) return true;
    if (jj_3R_1()) return true;
    return false;
  }

  private boolean jj_3_2() {
    if (jj_scan_token(22)) return true;
    if (jj_3R_1()) return true;
    if (jj_scan_token(OR)) return true;
    return false;
  }

  private boolean jj_3R_11() {
    if (jj_scan_token(GE)) return true;
    if (jj_3R_16()) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(24)) return true;
    if (jj_3R_1()) return true;
    return false;
  }

  private boolean jj_3R_10() {
    if (jj_scan_token(NOT)) return true;
    if (jj_3R_1()) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_scan_token(22)) return true;
    if (jj_3R_1()) return true;
    if (jj_scan_token(AND)) return true;
    return false;
  }

  private boolean jj_3R_9() {
    if (jj_scan_token(22)) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(ONLY)) return true;
    if (jj_3R_1()) return true;
    if (jj_scan_token(23)) return true;
    return false;
  }

  private boolean jj_3R_3() {
    if (jj_scan_token(STRING)) return true;
    return false;
  }

  private boolean jj_3R_8() {
    if (jj_scan_token(22)) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(SOME)) return true;
    if (jj_3R_1()) return true;
    if (jj_scan_token(23)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ManchesterSyntaxParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[4];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0xc00,0x70000,0x300000,0x300000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[6];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public ManchesterSyntaxParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ManchesterSyntaxParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ManchesterSyntaxParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public ManchesterSyntaxParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ManchesterSyntaxParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public ManchesterSyntaxParser(ManchesterSyntaxParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ManchesterSyntaxParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
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
  private boolean jj_scan_token(int kind) {
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


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
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
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[26];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 4; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 26; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 6; i++) {
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
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
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
