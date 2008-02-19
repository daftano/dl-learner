/* Generated By:JavaCC: Do not edit this line. KBParserConstants.java */
package org.dllearner.parser;

public @SuppressWarnings("all") interface KBParserConstants {

  int EOF = 0;
  int SINGLE_LINE_COMMENT = 5;
  int FORMAL_COMMENT = 6;
  int MULTI_LINE_COMMENT = 7;
  int COMMAND_END = 8;
  int ID = 9;
  int NUMBER = 10;
  int DOUBLE = 11;
  int TOP = 12;
  int BOTTOM = 13;
  int AND = 14;
  int OR = 15;
  int EXISTS = 16;
  int ALL = 17;
  int NOT = 18;
  int GE = 19;
  int LE = 20;
  int STRING = 21;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<SINGLE_LINE_COMMENT>",
    "<FORMAL_COMMENT>",
    "<MULTI_LINE_COMMENT>",
    "\".\"",
    "<ID>",
    "<NUMBER>",
    "<DOUBLE>",
    "\"TOP\"",
    "\"BOTTOM\"",
    "\"AND\"",
    "\"OR\"",
    "<EXISTS>",
    "<ALL>",
    "<NOT>",
    "\">=\"",
    "\"<=\"",
    "<STRING>",
    "\"(\"",
    "\")\"",
    "\",\"",
    "\"=\"",
    "\"SUBCLASSOF\"",
    "\"SUB\"",
    "\"Transitive\"",
    "\"Functional\"",
    "\"Symmetric\"",
    "\"Inverse\"",
    "\"Subrole\"",
    "\"SUBCONCEPTOF\"",
    "\"DOMAIN\"",
    "\"OPDOMAIN\"",
    "\"OBJECTPROPERTYDOMAIN\"",
    "\"DPDOMAIN\"",
    "\"DATATYPEPROPERTYDOMAIN\"",
    "\"RANGE\"",
    "\"OPRANGE\"",
    "\"OBJECTPROPERTYRANGE\"",
    "\"DPRANGE\"",
    "\"DATATYPEPROPERTYRANGE\"",
    "\"DOUBLE\"",
    "\"BOOLEAN\"",
    "\"INTEGER\"",
  };

}
