// Do learn to insert your names and a brief description of
// what the program is supposed to do!
// Alisa, Dean, and Zaza: Malicious Hadadas. 
//Lines changed: 76-79; 214-215; 236-368.
// This is a skeleton program for developing a parser for checking PVMlike files
// KL Bradshaw 2021

import java.util.*;
import library.*;

class Token {
  public int kind=0;
  public String val;

  public Token(int kind, String val) {
    this.kind = kind;
    this.val = val;
  }

} // Token

class PVMChecker {

  // +++++++++++++++++++++++++ File Handling and Er ror handlers
  // ++++++++++++++++++++

  static InFile input;
  static OutFile output;

  static String newFileName(String oldFileName, String ext) {
    // Creates new file name by changing extension of oldFileName to ext
    int i = oldFileName.lastIndexOf('.');
    if (i < 0)
      return oldFileName + ext;
    else
      return oldFileName.substring(0, i) + ext;
  } // newFileName

  static void reportError(String errorMessage) {
    // Displays errorMessage on standard output and on reflected output
    System.out.println(errorMessage);
    output.writeLine(errorMessage);
  } // reportError

  static void abort(String errorMessage) {
    // Abandons parsing after issuing error message
    reportError(errorMessage);
    output.close();
    System.exit(1);
  } // abort

  // +++++++++++++++++++++++ token kinds enumeration +++++++++++++++++++++++++

  static final int 
    noSym        = 0,
    eolSym       = 1,
    labelSym     = 2,
    numSym       = 3,
    identSym     = 4,
    beginSym     = 5,
    endSym       = 6,
    addSym       = 7,
    ceqSym       = 8,
    cneSym       = 9,
    brnSym       = 10,
    bzeSym       = 11,
    stoSym       = 12,
    prniSym      = 13,
    inpiSym      = 14,
    ldaSym       = 15,
    ldcSym       = 16,
    ldvSym       = 17,
    dspSym       = 18,
    periodSym    = 19,
    EOFSym       = 20,
    lparenthSym    = 21,
    rparenthSym    = 22,
    starSym       = 23,
    semicolonSym  =24;

  // ++++++++++++++++++++++++ Define Sets for Characters and Keywords
  // ++++++++++++++++++++++++++

  // Set for single-character symbols
  static final Set<Character> singleCharSymbols = new HashSet<>(Set.of(
      '+', '-', '*', '/', '=', '<', '>', '(', ')', ':', ',', ';', '.'));
  static final Set<Character> letterSet = new HashSet<>(Set.of('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
      'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
      'x', 'y', 'z'));
  static final Set<Character> digitSet = new HashSet<>(Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));

  // Set for multi-character keywords and mnemonics
  static final Set<String> keywords = new HashSet<>(Set.of(
      "BEGIN", "END", "ADD", "CEQ", "CNE", "INPI", "LDV", "PRNI", "STO", "DSP", "LDC", "LDA", "BRN", "BZE"));

  // +++++++++++++++++++++++++++++ Character Handler ++++++++++++++++++++++++++

  static final char EOF = '\0';
  static boolean atEndOfFile = false;

  // Declaring ch as a global variable is done for simplicity - global variables
  // are not always a good thing

  static char ch; // look ahead character for scanner

  static void getChar() {
    // Obtains next character ch from input, or CHR(0) if EOF reached
    // Reflect ch to output
    if (atEndOfFile)
      ch = EOF;
    else {
      ch = input.readChar();
      atEndOfFile = ch == EOF;
      if (!atEndOfFile)
        output.write(ch);
    }
  } // getChar

  // +++++++++++++++++++++++++++++++ Scanner ++++++++++++++++++++++++++++++++++
   
  // Declaring sym as a global variable is done for simplicity - global variables
  // are not always a good thing

  static Token sym;

  
  static void getSym() {
    // Scans for next symbol from input
    while (ch > EOF && ch <= ' ' && ch != '\n')
      getChar(); // Need to check EOL, so can't ignore it

    StringBuilder symLex = new StringBuilder();
     int symKind = noSym;

    if (letterSet.contains(ch)) { // Identifier or keyword
      do {
        symLex.append(ch);
        getChar();
      } while (letterSet.contains(ch) || digitSet.contains(ch)); // Continue if it's a letter or digit
//caution double check
      String lexeme = symLex.toString();
      if (keywords.contains(lexeme)) {
        symKind = identifyKeyword(lexeme);
      } else {
        symKind = identSym;
      }

    } else if (digitSet.contains(ch)) { // Number
      do {
        symLex.append(ch);
        getChar();
      } while (digitSet.contains(ch));
      symKind = numSym;

    } 
    else if (ch==';'){
        symKind=semicolonSym;
    }else if(singleCharSymbols.contains(ch)) { // Single-character symbols
      symLex.append(ch);
      sym.kind = identifySingleCharSymbol(ch);
      getChar();

    } else if (ch == EOF) {
      symKind = EOFSym;

    } else if (ch == '\n') {
      symKind = eolSym; // End of line symbol
      getChar();

    } else {
      reportError("Unexpected character: " + ch);
      getChar(); // Skip the character and try to continue
    }

    sym = new Token(symKind, symLex.toString());
  } // getSym

  // Helper method to identify keywords
  static int identifyKeyword(String lexeme) {
    return switch (lexeme) {
      case "BEGIN" -> beginSym;
      case "END" -> endSym;
      case "ADD" -> addSym;
      case "CEQ" -> ceqSym;
      case "CNE" -> cneSym;
      case "INPI" -> inpiSym;
      case "LDV" -> ldvSym;
      case "PRNI" -> prniSym;
      case "STO" -> stoSym;
      case "DSP" -> dspSym;
      case "LDC" -> ldcSym;
      case "LDA" -> ldaSym;
      case "BRN" -> brnSym;
      case "BZE" -> bzeSym;
      default -> identSym; // If no match, return identSym as a default
    };
  }

  // Helper method for single-character symbol identification
  static int identifySingleCharSymbol(char ch) {
    return switch (ch) {
      case '+' -> addSym;
      case '-' -> noSym; // Replace 'noSym' with the correct symbol for subtraction if defined
      case '*' -> starSym; // Replace 'noSym' with the correct symbol for multiplication if defined
      case '/' -> noSym; // Replace 'noSym' with the correct symbol for division if defined
      case '=' -> noSym; // Replace 'noSym' with the correct symbol for equality if defined
      case '<' -> noSym; // Replace 'noSym' with the correct symbol for less than if defined
      case '>' -> noSym; // Replace 'noSym' with the correct symbol for greater than if defined
      case '(' -> lparenthSym; // Replace 'noSym' with the correct symbol for left parenthesis if defined
      case ')' -> rparenthSym; // Replace 'noSym' with the correct symbol for right parenthesis if defined
      case ':' -> noSym; // Replace 'noSym' with the correct symbol for colon if defined
      case ',' -> noSym; // Replace 'noSym' with the correct symbol for comma if defined
      case ';' -> semicolonSym; // Replace 'noSym' with the correct symbol for semicolon if defined
      case '.' -> periodSym;
      default -> noSym; // If no match, return noSym as a default
    };
  }


  
   // ++++ Commented out for the moment
   
    // +++++++++++++++++++++++++++++++ Parser +++++++++++++++++++++++++++++++++++
    
    static void accept(int wantedSym, String errorMessage) {
    // Checks that lookahead token is wantedSym
    if (sym.kind == wantedSym) getSym(); else abort(errorMessage);
    } // accept
    
    static void accept(IntSet allowedSet, String errorMessage) {
    // Checks that lookahead token is in allowedSet
    if (allowedSet.contains(sym.kind)) getSym(); else abort(errorMessage);
    } // accept
   
    static void PVMlike() {
    // { EOL } "BEGIN" { EOL } { Statement } "END" { EOL } "." { EOL }.
    IntSet Statements = new IntSet(labelSym,ldaSym, ldcSym, dspSym,addSym, ceqSym, cneSym,inpiSym ,ldvSym,prniSym, stoSym,brnSym,bzeSym);
      if(sym.kind==eolSym){
      while (sym.kind==eolSym){
        getSym(); 
      }
      }else if (sym.kind!=beginSym){
          Comment();
      }
      if (sym.kind==beginSym){
        accept(beginSym,"BEGIN expected");
      }else{
          Comment();
      }
      if(sym.kind==eolSym){
      while (sym.kind==eolSym){
        getSym();
      }
      }else{
        Comment();
      }
      while (Statements.contains(sym.kind)){//
        Statement();
        if (sym.kind == endSym){
          while (sym.kind==eolSym){
            getSym();
          }
          accept(periodSym,". expected");
          while (sym.kind==eolSym){
            getSym();
          
        }}else{
          Comment();
        }
        if(sym.kind==endSym){
          accept(endSym,"END expected");
        }else{
          Comment();
        }
      }
    
      
    }
  //Helper function for question 4, dealing with comments
    static void Comment(){
          accept(semicolonSym,"Semicolon expected");
          if (sym.kind==eolSym){
            accept(eolSym,"EOL expected");
          }else{
            getSym();
          }
    }
  //helper function for recursive descent
    static void Statement() {
    //Statement = [label] (OneWord | TwoWord | Branch ) EOL . 
    IntSet TwoWords = new IntSet(ldaSym, ldcSym, dspSym);
    IntSet OneWords = new IntSet(addSym, ceqSym, cneSym,inpiSym ,ldvSym,prniSym, stoSym);
    IntSet Branchs = new IntSet(brnSym,bzeSym);
    if (sym.kind==labelSym){
      getSym();
    }
    if (OneWords.contains(sym.kind)){
      OneWord();
    } else if (TwoWords.contains(sym.kind)){
      TwoWord();
    } else if (Branchs.contains(sym.kind)){
      Branch();
    }else 
    {abort("Statement wrong");
     }                   //replace with switch
    accept(eolSym,"EOL expected" );
    }
  //OneWord helper function
    static void OneWord(){
    //OneWord = ( "ADD" | "CEQ" | "CNE" | "INPI" | "LDV" | "PRNI" | "STO" ) . 
    switch (sym.kind){
      case addSym:
        getSym();
      case ceqSym:
        getSym();
      case cneSym:
        getSym();
      case inpiSym:
        getSym();
      case ldvSym:
        getSym();
      case prniSym:
        getSym();
      case stoSym:
        getSym();
      default:
        abort("Invalid vaule for oneword");
      }
    }
//TwoWord helper function
    static void TwoWord(){
    //TwoWord = ( "DSP" | "LDC" | "LDA" ) number .
    switch (sym.kind){
      case dspSym:
        getSym();
      case ldcSym:
        getSym();
      case ldaSym:
        getSym();
      
      default:
        abort("Invalid start to twowords");
      }
    accept(numSym,"number expected");
    }
    
    
//Branch helper function
    static void Branch(){
    //Branch = ( "BRN" | "BZE" ) ( number | identifier ) . 
    switch (sym.kind){
      case brnSym:
        getSym();
      case bzeSym:
        getSym();
      default:
        abort("Invalid start to branch");
      }
    
    
   switch (sym.kind){
      case numSym:
        getSym();
      case identSym:
        getSym();
      default:
        abort("Invalid start to branch");
      }
    }
  // +++++++++++++++++++++ Main driver function +++++++++++++++++++++++++++++++

  public static void main(String[] args) {
    // Open input and output files from command line arguments
    if (args.length == 0) {
      System.out.println("Usage: PVMChecker FileName");
      System.exit(1);
    }
    input = new InFile(args[0]);
    output = new OutFile(newFileName(args[0], ".out"));

    getChar(); // Lookahead character

    // To test the scanner we can use a loop like the following:

    /*do {
      getSym(); // Lookahead symbol
      OutFile.StdOut.write(sym.kind, 3);
      OutFile.StdOut.writeLine(" " + sym.val);
    } while (sym.kind != EOFSym);
    */
    getSym(); // Lookahead symbol
    PVMlike(); // Start to parse from the goal symbol
    System.out.println("Parsed correctly");
    /*
     * After the scanner is debugged, comment out lines 127 to 131 and uncomment
     * lines 135 to 138.
     * In other words, replace the code immediately above with this code:
     * 
     * getSym(); // Lookahead symbol
     * PVMlike(); // Start to parse from the goal symbol
     * // if we get back here everything must have been satisfactory
     * System.out.println("Parsed correctly");
     * 
     */
    output.close();
  } // main

} // PVMChecker
