/* Generated By:JavaCC: Do not edit this line. ManchesterSyntaxParserTokenManager.java */
package org.dllearner.parser;
import org.dllearner.core.owl.*;
import java.io.*;
import java.net.URL;

/** Token Manager. */
public class ManchesterSyntaxParserTokenManager implements ManchesterSyntaxParserConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x40000L) != 0L)
            return 39;
         if ((active0 & 0x3c1fc06L) != 0L)
            return 40;
         return -1;
      case 1:
         if ((active0 & 0x40000L) != 0L)
            return 39;
         if ((active0 & 0x201fc00L) != 0L)
            return 40;
         return -1;
      case 2:
         if ((active0 & 0x201dc00L) != 0L)
            return 40;
         return -1;
      case 3:
         if ((active0 & 0x200c800L) != 0L)
            return 40;
         return -1;
      case 4:
         if ((active0 & 0x2000800L) != 0L)
            return 40;
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 9:
         return jjStartNfaWithStates_0(0, 2, 40);
      case 32:
         return jjStartNfaWithStates_0(0, 1, 40);
      case 40:
         return jjStartNfaWithStates_0(0, 22, 40);
      case 41:
         return jjStartNfaWithStates_0(0, 23, 40);
      case 46:
         return jjStartNfaWithStates_0(0, 24, 40);
      case 60:
         return jjMoveStringLiteralDfa1_0(0x40000L);
      case 62:
         return jjMoveStringLiteralDfa1_0(0x20000L);
      case 66:
         return jjMoveStringLiteralDfa1_0(0x800L);
      case 84:
         return jjMoveStringLiteralDfa1_0(0x400L);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x1000L);
      case 110:
         return jjMoveStringLiteralDfa1_0(0x10000L);
      case 111:
         return jjMoveStringLiteralDfa1_0(0xa000L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x4000L);
      case 118:
         return jjMoveStringLiteralDfa1_0(0x2000000L);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 61:
         if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(1, 17);
         else if ((active0 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(1, 18, 39);
         break;
      case 79:
         return jjMoveStringLiteralDfa2_0(active0, 0xc00L);
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x2000000L);
      case 110:
         return jjMoveStringLiteralDfa2_0(active0, 0x9000L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x14000L);
      case 114:
         if ((active0 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(1, 13, 40);
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 80:
         if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(2, 10, 40);
         break;
      case 84:
         return jjMoveStringLiteralDfa3_0(active0, 0x800L);
      case 100:
         if ((active0 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(2, 12, 40);
         break;
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x2008000L);
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000L);
      case 116:
         if ((active0 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(2, 16, 40);
         break;
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 84:
         return jjMoveStringLiteralDfa4_0(active0, 0x800L);
      case 101:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(3, 14, 40);
         break;
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000000L);
      case 121:
         if ((active0 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(3, 15, 40);
         break;
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 79:
         return jjMoveStringLiteralDfa5_0(active0, 0x800L);
      case 101:
         if ((active0 & 0x2000000L) != 0L)
            return jjStartNfaWithStates_0(4, 25, 40);
         break;
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 77:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(5, 11, 40);
         break;
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 39;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 39:
                  if ((0xbfffffffffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(10, 11);
                  if (curChar == 58)
                     jjCheckNAddTwoStates(12, 13);
                  break;
               case 40:
                  if ((0xafffffffffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  if (curChar == 58)
                  {
                     if (kind > 20)
                        kind = 20;
                     jjCheckNAdd(8);
                  }
                  break;
               case 0:
                  if ((0xafffffffffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  else if (curChar == 60)
                     jjCheckNAddTwoStates(10, 11);
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 8)
                        kind = 8;
                     jjCheckNAddStates(0, 2);
                  }
                  else if (curChar == 48)
                  {
                     if (kind > 8)
                        kind = 8;
                     jjCheckNAdd(36);
                  }
                  else if (curChar == 47)
                     jjAddStates(3, 5);
                  else if (curChar == 58)
                  {
                     if (kind > 20)
                        kind = 20;
                     jjCheckNAdd(8);
                  }
                  else if (curChar == 39)
                     jjCheckNAddTwoStates(4, 5);
                  else if (curChar == 34)
                     jjCheckNAddTwoStates(1, 2);
                  break;
               case 1:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(1, 2);
                  break;
               case 2:
                  if (curChar == 34 && kind > 19)
                     kind = 19;
                  break;
               case 3:
                  if (curChar == 39)
                     jjCheckNAddTwoStates(4, 5);
                  break;
               case 4:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(4, 5);
                  break;
               case 5:
                  if (curChar == 39 && kind > 19)
                     kind = 19;
                  break;
               case 6:
                  if ((0xafffffffffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 7:
                  if (curChar != 58)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAdd(8);
                  break;
               case 8:
                  if ((0xafffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAdd(8);
                  break;
               case 9:
                  if (curChar == 60)
                     jjCheckNAddTwoStates(10, 11);
                  break;
               case 10:
                  if ((0xbfffffffffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(10, 11);
                  break;
               case 11:
                  if (curChar == 58)
                     jjCheckNAddTwoStates(12, 13);
                  break;
               case 12:
                  if ((0xbfffffffffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(12, 13);
                  break;
               case 13:
                  if (curChar == 62 && kind > 21)
                     kind = 21;
                  break;
               case 14:
                  if (curChar == 47)
                     jjAddStates(3, 5);
                  break;
               case 15:
                  if (curChar == 47)
                     jjCheckNAddStates(6, 8);
                  break;
               case 16:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 17:
                  if ((0x2400L & l) != 0L && kind > 5)
                     kind = 5;
                  break;
               case 18:
                  if (curChar == 10 && kind > 5)
                     kind = 5;
                  break;
               case 19:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 20:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 21:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 22:
                  if (curChar == 42)
                     jjCheckNAddStates(9, 11);
                  break;
               case 23:
                  if ((0xffff7bffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(24, 22);
                  break;
               case 24:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(24, 22);
                  break;
               case 25:
                  if (curChar == 47 && kind > 6)
                     kind = 6;
                  break;
               case 26:
                  if (curChar == 42)
                     jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 27:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(28, 29);
                  break;
               case 28:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(28, 29);
                  break;
               case 29:
                  if (curChar == 42)
                     jjCheckNAddStates(12, 14);
                  break;
               case 30:
                  if ((0xffff7bffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(31, 29);
                  break;
               case 31:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(31, 29);
                  break;
               case 32:
                  if (curChar == 47 && kind > 7)
                     kind = 7;
                  break;
               case 33:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAddStates(0, 2);
                  break;
               case 34:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(34);
                  break;
               case 35:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(35, 36);
                  break;
               case 36:
                  if (curChar != 46)
                     break;
                  if (kind > 9)
                     kind = 9;
                  jjCheckNAdd(37);
                  break;
               case 37:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 9)
                     kind = 9;
                  jjCheckNAdd(37);
                  break;
               case 38:
                  if (curChar != 48)
                     break;
                  if (kind > 8)
                     kind = 8;
                  jjCheckNAdd(36);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 39:
               case 10:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddTwoStates(10, 11);
                  break;
               case 40:
               case 6:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 0:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 1:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjAddStates(15, 16);
                  break;
               case 4:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjAddStates(17, 18);
                  break;
               case 8:
                  if ((0xffffffffefffffffL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 12:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjAddStates(19, 20);
                  break;
               case 16:
                  jjAddStates(6, 8);
                  break;
               case 21:
                  jjCheckNAddTwoStates(21, 22);
                  break;
               case 23:
               case 24:
                  jjCheckNAddTwoStates(24, 22);
                  break;
               case 28:
                  jjCheckNAddTwoStates(28, 29);
                  break;
               case 30:
               case 31:
                  jjCheckNAddTwoStates(31, 29);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 39:
               case 10:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(10, 11);
                  break;
               case 40:
               case 6:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 0:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 1:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(15, 16);
                  break;
               case 4:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(17, 18);
                  break;
               case 8:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 12:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(19, 20);
                  break;
               case 16:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(6, 8);
                  break;
               case 21:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 23:
               case 24:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(24, 22);
                  break;
               case 28:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(28, 29);
                  break;
               case 30:
               case 31:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(31, 29);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 39 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   34, 35, 36, 15, 26, 27, 16, 17, 19, 22, 23, 25, 29, 30, 32, 1, 
   2, 4, 5, 12, 13, 
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, "\124\117\120", 
"\102\117\124\124\117\115", "\141\156\144", "\157\162", "\163\157\155\145", "\157\156\154\171", 
"\156\157\164", "\76\75", "\74\75", null, null, null, "\50", "\51", "\56", 
"\166\141\154\165\145", };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};
static final long[] jjtoToken = {
   0x3ffff01L, 
};
static final long[] jjtoSkip = {
   0xfeL, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[39];
private final int[] jjstateSet = new int[78];
protected char curChar;
/** Constructor. */
public ManchesterSyntaxParserTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}

/** Constructor. */
public ManchesterSyntaxParserTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 39; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 13 && (0x2400L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
