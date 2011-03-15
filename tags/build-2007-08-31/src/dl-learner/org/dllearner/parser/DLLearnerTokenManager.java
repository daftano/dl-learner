/* Generated By:JJTree&JavaCC: Do not edit this line. DLLearnerTokenManager.java */
package org.dllearner.parser;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import org.dllearner.Main;
import org.dllearner.Info;
import org.dllearner.dl.*;
import org.dllearner.ConfigurationOption;
import org.dllearner.utilities.*;

public @SuppressWarnings("all") class DLLearnerTokenManager implements DLLearnerConstants
{
  public static  java.io.PrintStream debugStream = System.out;
  public static  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private static final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x6980000000L) != 0L)
            return 10;
         if ((active0 & 0x400000000L) != 0L)
            return 18;
         if ((active0 & 0x20000L) != 0L)
            return 13;
         return -1;
      default :
         return -1;
   }
}
private static final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
static private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
static private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 40:
         return jjStopAtPos(0, 29);
      case 41:
         return jjStopAtPos(0, 30);
      case 43:
         return jjStopAtPos(0, 10);
      case 44:
         return jjStopAtPos(0, 28);
      case 45:
         return jjStopAtPos(0, 11);
      case 46:
         return jjStopAtPos(0, 8);
      case 59:
         return jjStopAtPos(0, 9);
      case 60:
         return jjMoveStringLiteralDfa1_0(0x800000L);
      case 61:
         return jjStopAtPos(0, 25);
      case 62:
         return jjMoveStringLiteralDfa1_0(0x400000L);
      case 65:
         return jjMoveStringLiteralDfa1_0(0x20000L);
      case 66:
         return jjMoveStringLiteralDfa1_0(0x10000L);
      case 70:
         return jjMoveStringLiteralDfa1_0(0x400000000L);
      case 73:
         return jjMoveStringLiteralDfa1_0(0x1000000000L);
      case 79:
         return jjMoveStringLiteralDfa1_0(0x40000L);
      case 83:
         return jjMoveStringLiteralDfa1_0(0x6980000000L);
      case 84:
         return jjMoveStringLiteralDfa1_0(0x200008000L);
      case 123:
         return jjStopAtPos(0, 26);
      case 125:
         return jjStopAtPos(0, 27);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
static private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 61:
         if ((active0 & 0x400000L) != 0L)
            return jjStopAtPos(1, 22);
         else if ((active0 & 0x800000L) != 0L)
            return jjStopAtPos(1, 23);
         break;
      case 78:
         return jjMoveStringLiteralDfa2_0(active0, 0x20000L);
      case 79:
         return jjMoveStringLiteralDfa2_0(active0, 0x18000L);
      case 82:
         if ((active0 & 0x40000L) != 0L)
            return jjStopAtPos(1, 18);
         break;
      case 85:
         return jjMoveStringLiteralDfa2_0(active0, 0x4180000000L);
      case 110:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000000000L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000L);
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0x2400000000L);
      case 121:
         return jjMoveStringLiteralDfa2_0(active0, 0x800000000L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
static private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
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
      case 66:
         if ((active0 & 0x100000000L) != 0L)
         {
            jjmatchedKind = 32;
            jjmatchedPos = 2;
         }
         return jjMoveStringLiteralDfa3_0(active0, 0x4080000000L);
      case 68:
         if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(2, 17);
         break;
      case 80:
         if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(2, 15);
         break;
      case 84:
         return jjMoveStringLiteralDfa3_0(active0, 0x10000L);
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x200000000L);
      case 98:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000000000L);
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x800000000L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x400000000L);
      case 118:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000000000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
static private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
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
      case 67:
         return jjMoveStringLiteralDfa4_0(active0, 0x4080000000L);
      case 84:
         return jjMoveStringLiteralDfa4_0(active0, 0x10000L);
      case 99:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000000L);
      case 101:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000000L);
      case 109:
         return jjMoveStringLiteralDfa4_0(active0, 0x800000000L);
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x200000000L);
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000000000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
static private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
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
      case 76:
         return jjMoveStringLiteralDfa5_0(active0, 0x80000000L);
      case 79:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000010000L);
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x800000000L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x2000000000L);
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000000L);
      case 115:
         return jjMoveStringLiteralDfa5_0(active0, 0x200000000L);
      case 116:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000000L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
static private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
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
      case 65:
         return jjMoveStringLiteralDfa6_0(active0, 0x80000000L);
      case 77:
         if ((active0 & 0x10000L) != 0L)
            return jjStopAtPos(5, 16);
         break;
      case 78:
         return jjMoveStringLiteralDfa6_0(active0, 0x4000000000L);
      case 105:
         return jjMoveStringLiteralDfa6_0(active0, 0x600000000L);
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0x2000000000L);
      case 115:
         return jjMoveStringLiteralDfa6_0(active0, 0x1000000000L);
      case 116:
         return jjMoveStringLiteralDfa6_0(active0, 0x800000000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
static private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 67:
         return jjMoveStringLiteralDfa7_0(active0, 0x4000000000L);
      case 83:
         return jjMoveStringLiteralDfa7_0(active0, 0x80000000L);
      case 101:
         if ((active0 & 0x1000000000L) != 0L)
            return jjStopAtPos(6, 36);
         else if ((active0 & 0x2000000000L) != 0L)
            return jjStopAtPos(6, 37);
         break;
      case 111:
         return jjMoveStringLiteralDfa7_0(active0, 0x400000000L);
      case 114:
         return jjMoveStringLiteralDfa7_0(active0, 0x800000000L);
      case 116:
         return jjMoveStringLiteralDfa7_0(active0, 0x200000000L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
static private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 69:
         return jjMoveStringLiteralDfa8_0(active0, 0x4000000000L);
      case 83:
         return jjMoveStringLiteralDfa8_0(active0, 0x80000000L);
      case 105:
         return jjMoveStringLiteralDfa8_0(active0, 0xa00000000L);
      case 110:
         return jjMoveStringLiteralDfa8_0(active0, 0x400000000L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
static private final int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 79:
         return jjMoveStringLiteralDfa9_0(active0, 0x80000000L);
      case 80:
         return jjMoveStringLiteralDfa9_0(active0, 0x4000000000L);
      case 97:
         return jjMoveStringLiteralDfa9_0(active0, 0x400000000L);
      case 99:
         if ((active0 & 0x800000000L) != 0L)
            return jjStopAtPos(8, 35);
         break;
      case 118:
         return jjMoveStringLiteralDfa9_0(active0, 0x200000000L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
static private final int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 70:
         if ((active0 & 0x80000000L) != 0L)
            return jjStopAtPos(9, 31);
         break;
      case 84:
         return jjMoveStringLiteralDfa10_0(active0, 0x4000000000L);
      case 101:
         if ((active0 & 0x200000000L) != 0L)
            return jjStopAtPos(9, 33);
         break;
      case 108:
         if ((active0 & 0x400000000L) != 0L)
            return jjStopAtPos(9, 34);
         break;
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
static private final int jjMoveStringLiteralDfa10_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 79:
         return jjMoveStringLiteralDfa11_0(active0, 0x4000000000L);
      default :
         break;
   }
   return jjStartNfa_0(9, active0);
}
static private final int jjMoveStringLiteralDfa11_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0);
      return 11;
   }
   switch(curChar)
   {
      case 70:
         if ((active0 & 0x4000000000L) != 0L)
            return jjStopAtPos(11, 38);
         break;
      default :
         break;
   }
   return jjStartNfa_0(10, active0);
}
static private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
static private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
static private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
static private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
static private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 53;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 13)
                        kind = 13;
                     jjCheckNAddStates(0, 2);
                  }
                  else if (curChar == 48)
                  {
                     if (kind > 13)
                        kind = 13;
                     jjCheckNAdd(50);
                  }
                  else if (curChar == 47)
                     jjAddStates(3, 5);
                  else if (curChar == 34)
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 1:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 20:
                  if (curChar == 34)
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 21:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 22:
                  if (curChar == 34 && kind > 24)
                     kind = 24;
                  break;
               case 28:
                  if (curChar == 47)
                     jjAddStates(3, 5);
                  break;
               case 29:
                  if (curChar == 47)
                     jjCheckNAddStates(6, 8);
                  break;
               case 30:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 31:
                  if ((0x2400L & l) != 0L && kind > 5)
                     kind = 5;
                  break;
               case 32:
                  if (curChar == 10 && kind > 5)
                     kind = 5;
                  break;
               case 33:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 32;
                  break;
               case 34:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(35, 36);
                  break;
               case 35:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(35, 36);
                  break;
               case 36:
                  if (curChar == 42)
                     jjCheckNAddStates(9, 11);
                  break;
               case 37:
                  if ((0xffff7bffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(38, 36);
                  break;
               case 38:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(38, 36);
                  break;
               case 39:
                  if (curChar == 47 && kind > 6)
                     kind = 6;
                  break;
               case 40:
                  if (curChar == 42)
                     jjstateSet[jjnewStateCnt++] = 34;
                  break;
               case 41:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(42, 43);
                  break;
               case 42:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(42, 43);
                  break;
               case 43:
                  if (curChar == 42)
                     jjCheckNAddStates(12, 14);
                  break;
               case 44:
                  if ((0xffff7bffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(45, 43);
                  break;
               case 45:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(45, 43);
                  break;
               case 46:
                  if (curChar == 47 && kind > 7)
                     kind = 7;
                  break;
               case 47:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjCheckNAddStates(0, 2);
                  break;
               case 48:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjCheckNAdd(48);
                  break;
               case 49:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(49, 50);
                  break;
               case 50:
                  if (curChar != 46)
                     break;
                  if (kind > 14)
                     kind = 14;
                  jjCheckNAdd(51);
                  break;
               case 51:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 14)
                     kind = 14;
                  jjCheckNAdd(51);
                  break;
               case 52:
                  if (curChar != 48)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjCheckNAdd(50);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe00000000L & l) != 0L)
                  {
                     if (kind > 12)
                        kind = 12;
                     jjCheckNAdd(1);
                  }
                  else if (curChar == 78)
                     jjAddStates(15, 16);
                  else if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 18;
                  else if (curChar == 65)
                     jjstateSet[jjnewStateCnt++] = 13;
                  else if (curChar == 83)
                     jjstateSet[jjnewStateCnt++] = 10;
                  else if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 1:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAdd(1);
                  break;
               case 2:
                  if (curChar == 83 && kind > 19)
                     kind = 19;
                  break;
               case 3:
                  if (curChar == 84)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 4:
                  if (curChar == 83)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 5:
                  if (curChar == 73)
                     jjstateSet[jjnewStateCnt++] = 4;
                  break;
               case 6:
                  if (curChar == 88)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 7:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 8:
                  if (curChar == 69 && kind > 19)
                     kind = 19;
                  break;
               case 9:
                  if (curChar == 77)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 10:
                  if (curChar == 79)
                     jjstateSet[jjnewStateCnt++] = 9;
                  break;
               case 11:
                  if (curChar == 83)
                     jjstateSet[jjnewStateCnt++] = 10;
                  break;
               case 12:
                  if (curChar == 76 && kind > 20)
                     kind = 20;
                  break;
               case 13:
               case 15:
                  if (curChar == 76)
                     jjCheckNAdd(12);
                  break;
               case 14:
                  if (curChar == 65)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 16:
                  if (curChar == 65)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 17:
                  if (curChar == 82)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 18:
                  if (curChar == 79)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 19:
                  if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 21:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjAddStates(17, 18);
                  break;
               case 23:
                  if (curChar == 78)
                     jjAddStates(15, 16);
                  break;
               case 24:
                  if (curChar == 71 && kind > 21)
                     kind = 21;
                  break;
               case 25:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 24;
                  break;
               case 26:
                  if (curChar == 84 && kind > 21)
                     kind = 21;
                  break;
               case 27:
                  if (curChar == 79)
                     jjstateSet[jjnewStateCnt++] = 26;
                  break;
               case 30:
                  jjAddStates(6, 8);
                  break;
               case 35:
                  jjCheckNAddTwoStates(35, 36);
                  break;
               case 37:
               case 38:
                  jjCheckNAddTwoStates(38, 36);
                  break;
               case 42:
                  jjCheckNAddTwoStates(42, 43);
                  break;
               case 44:
               case 45:
                  jjCheckNAddTwoStates(45, 43);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 21:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(17, 18);
                  break;
               case 30:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(6, 8);
                  break;
               case 35:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(35, 36);
                  break;
               case 37:
               case 38:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(38, 36);
                  break;
               case 42:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(42, 43);
                  break;
               case 44:
               case 45:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(45, 43);
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
      if ((i = jjnewStateCnt) == (startsAt = 53 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   48, 49, 50, 29, 40, 41, 30, 31, 33, 36, 37, 39, 43, 44, 46, 25, 
   27, 21, 22, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, "\56", "\73", "\53", "\55", null, 
null, null, "\124\117\120", "\102\117\124\124\117\115", "\101\116\104", "\117\122", 
null, null, null, "\76\75", "\74\75", null, "\75", "\173", "\175", "\54", "\50", 
"\51", "\123\125\102\103\114\101\123\123\117\106", "\123\125\102", 
"\124\162\141\156\163\151\164\151\166\145", "\106\165\156\143\164\151\157\156\141\154", 
"\123\171\155\155\145\164\162\151\143", "\111\156\166\145\162\163\145", "\123\165\142\162\157\154\145", 
"\123\125\102\103\117\116\103\105\120\124\117\106", };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x7fffffff01L, 
};
static final long[] jjtoSkip = {
   0xfeL, 
};
static protected SimpleCharStream input_stream;
static private final int[] jjrounds = new int[53];
static private final int[] jjstateSet = new int[106];
static protected char curChar;
public DLLearnerTokenManager(SimpleCharStream stream){
   if (input_stream != null)
      throw new TokenMgrError("ERROR: Second call to constructor of static lexer. You must use ReInit() to initialize the static variables.", TokenMgrError.STATIC_LEXER_ERROR);
   input_stream = stream;
}
public DLLearnerTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
static public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
static private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 53; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
static public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
static public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

static protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

static int curLexState = 0;
static int defaultLexState = 0;
static int jjnewStateCnt;
static int jjround;
static int jjmatchedPos;
static int jjmatchedKind;

public static Token getNextToken() 
{
  int kind;
  Token specialToken = null;
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
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
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

}