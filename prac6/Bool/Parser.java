package Bool;

import library.*;
import java.util.*;



import java.io.*;

public class Parser {
	public static final int _EOF = 0;
	public static final int _variable = 1;
	// terminals
	public static final int EOF_SYM = 0;
	public static final int variable_Sym = 1;
	public static final int equal_Sym = 2;
	public static final int print_Sym = 3;
	public static final int semicolon_Sym = 4;
	public static final int not_Sym = 5;
	public static final int squote_Sym = 6;
	public static final int true_Sym = 7;
	public static final int d1_Sym = 8;
	public static final int false_Sym = 9;
	public static final int d0_Sym = 10;
	public static final int and_Sym = 11;
	public static final int andand_Sym = 12;
	public static final int point_Sym = 13;
	public static final int or_Sym = 14;
	public static final int barbar_Sym = 15;
	public static final int plus_Sym = 16;
	public static final int NOT_SYM = 17;
	// pragmas

	public static final int maxT = 17;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public static Token token;    // last recognized token   /* pdt */
	public static Token la;       // lookahead token
	static int errDist = minErrDist;

	static boolean[] mem = new boolean[26];



	static void SynErr (int n) {
		if (errDist >= minErrDist) Errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public static void SemErr (String msg) {
		if (errDist >= minErrDist) Errors.Error(token.line, token.col, msg); /* pdt */
		errDist = 0;
	}

	public static void SemError (String msg) {
		if (errDist >= minErrDist) Errors.Error(token.line, token.col, msg); /* pdt */
		errDist = 0;
	}

	public static void Warning (String msg) { /* pdt */
		if (errDist >= minErrDist) Errors.Warn(token.line, token.col, msg);
		errDist = 2; //++ 2009/11/04
	}

	public static boolean Successful() { /* pdt */
		return Errors.count == 0;
	}

	public static String LexString() { /* pdt */
		return token.val;
	}

	public static String LookAheadString() { /* pdt */
		return la.val;
	}

	static void Get () {
		for (;;) {
			token = la; /* pdt */
			la = Scanner.Scan();
			if (la.kind <= maxT) { ++errDist; break; }

			la = token; /* pdt */
		}
	}

	static void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}

	static boolean StartOf (int s) {
		return set[s][la.kind];
	}

	static void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}

	static boolean WeakSeparator (int n, int syFol, int repFol) {
		boolean[] s = new boolean[maxT+1];
		if (la.kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			for (int i=0; i <= maxT; i++) {
				s[i] = set[syFol][i] || set[repFol][i] || set[0][i];
			}
			SynErr(n);
			while (!s[la.kind]) Get();
			return StartOf(syFol);
		}
	}

	static void Bool() {
		int index = 0;
		boolean value = false;
		char var; boolean aval=false;													for (int i = 0; i < 26; i++) mem[i] = false;
		while (la.kind == variable_Sym || la.kind == print_Sym) {
			if (la.kind == variable_Sym) {
				var = Variable();
				index = var - 'A';
				Expect(equal_Sym);
				aval = Expression();
				mem[index] = value;
			} else {
				Get();
				aval = Expression();
				IO.writeLine(value);
			}
			Expect(semicolon_Sym);
		}
		Expect(EOF_SYM);
	}

	static char Variable() {
		char var;
		Expect(variable_Sym);
		var = token.val.charAt(0);
		return var;
	}

	static boolean Expression() {
		boolean bolval;
		boolean aval=false;
		bolval = Term();
		while (la.kind == or_Sym || la.kind == barbar_Sym || la.kind == plus_Sym) {
			Or();
			aval = Term();
			bolval=bolval || aval;
		}
		return bolval;
	}

	static boolean Term() {
		boolean bolval;
		boolean aval=false;
		bolval = Factor();
		while (StartOf(1)) {
			if (la.kind == and_Sym || la.kind == andand_Sym || la.kind == point_Sym) {
				And();
			}
			aval = Factor();
			bolval= bolval && aval;
		}
		return bolval;
	}

	static void Or() {
		if (la.kind == or_Sym) {
			Get();
		} else if (la.kind == barbar_Sym) {
			Get();
		} else if (la.kind == plus_Sym) {
			Get();
		} else SynErr(18);
	}

	static boolean Factor() {
		boolean boval;
		if (la.kind == not_Sym) {
			Get();
			boval = Factor();
			if  (boval==true){boval=false;}else{boval=true;}
		} else if (StartOf(2)) {
			boval = Primary();
			while (la.kind == squote_Sym) {
				Get();
			}
		} else SynErr(19);
		return boval;
	}

	static void And() {
		if (la.kind == and_Sym) {
			Get();
		} else if (la.kind == andand_Sym) {
			Get();
		} else if (la.kind == point_Sym) {
			Get();
		} else SynErr(20);
	}

	static boolean Primary() {
		boolean Boloption;
		boolean aval=false;
		if (la.kind == true_Sym || la.kind == d1_Sym) {
			True();
			Boloption=true;
		} else if (la.kind == false_Sym || la.kind == d0_Sym) {
			False();
			Boloption=false;
		} else SynErr(21);
		return Boloption;
	}

	static void True() {
		if (la.kind == true_Sym) {
			Get();
		} else if (la.kind == d1_Sym) {
			Get();
		} else SynErr(22);
	}

	static void False() {
		if (la.kind == false_Sym) {
			Get();
		} else if (la.kind == d0_Sym) {
			Get();
		} else SynErr(23);
	}



	public static void Parse() {
		la = new Token();
		la.val = "";
		Get();
		Bool();
		Expect(EOF_SYM);

	}

	private static boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,x,x,x, x,T,x,T, T,T,T,T, T,T,x,x, x,x,x},
		{x,x,x,x, x,x,x,T, T,T,T,x, x,x,x,x, x,x,x}

	};

} // end Parser

/* pdt - considerable extension from here on */

class ErrorRec {
	public int line, col, num;
	public String str;
	public ErrorRec next;

	public ErrorRec(int l, int c, String s) {
		line = l; col = c; str = s; next = null;
	}

} // end ErrorRec

class Errors {

	public static int count = 0;                                     // number of errors detected
	public static int warns = 0;                                     // number of warnings detected
	public static String errMsgFormat = "file {0} : ({1}, {2}) {3}"; // 0=file 1=line, 2=column, 3=text
	static String fileName = "";
	static String listName = "";
	static boolean mergeErrors = false;
	static PrintWriter mergedList;

	static ErrorRec first = null, last;
	static boolean eof = false;

	static String getLine() {
		char ch, CR = '\r', LF = '\n';
		int l = 0;
		StringBuffer s = new StringBuffer();
		ch = (char) Buffer.Read();
		while (ch != Buffer.EOF && ch != CR && ch != LF) {
			s.append(ch); l++; ch = (char) Buffer.Read();
		}
		eof = (l == 0 && ch == Buffer.EOF);
		if (ch == CR) {  // check for MS-DOS
			ch = (char) Buffer.Read();
			if (ch != LF && ch != Buffer.EOF) Buffer.pos--;
		}
		return s.toString();
	}

	static private String Int(int n, int len) {
		String s = String.valueOf(n);
		int i = s.length(); if (len < i) len = i;
		int j = 0, d = len - s.length();
		char[] a = new char[len];
		for (i = 0; i < d; i++) a[i] = ' ';
		for (j = 0; i < len; i++) {a[i] = s.charAt(j); j++;}
		return new String(a, 0, len);
	}

	static void display(String s, ErrorRec e) {
		mergedList.print("**** ");
		for (int c = 1; c < e.col; c++)
			if (s.charAt(c-1) == '\t') mergedList.print("\t"); else mergedList.print(" ");
		mergedList.println("^ " + e.str);
	}

	public static void Init (String fn, String dir, boolean merge) {
		fileName = fn;
		listName = dir + "listing.txt";
		mergeErrors = merge;
		if (mergeErrors)
			try {
				mergedList = new PrintWriter(new BufferedWriter(new FileWriter(listName, false)));
			} catch (IOException e) {
				Errors.Exception("-- could not open " + listName);
			}
	}

	public static void Summarize () {
		if (mergeErrors) {
			mergedList.println();
			ErrorRec cur = first;
			Buffer.setPos(0);
			int lnr = 1;
			String s = getLine();
			while (!eof) {
				mergedList.println(Int(lnr, 4) + " " + s);
				while (cur != null && cur.line == lnr) {
					display(s, cur); cur = cur.next;
				}
				lnr++; s = getLine();
			}
			if (cur != null) {
				mergedList.println(Int(lnr, 4));
				while (cur != null) {
					display(s, cur); cur = cur.next;
				}
			}
			mergedList.println();
			mergedList.println(count + " errors detected");
			if (warns > 0) mergedList.println(warns + " warnings detected");
			mergedList.close();
		}
		switch (count) {
			case 0 : System.out.println("Parsed correctly"); break;
			case 1 : System.out.println("1 error detected"); break;
			default: System.out.println(count + " errors detected"); break;
		}
		if (warns > 0) System.out.println(warns + " warnings detected");
		if ((count > 0 || warns > 0) && mergeErrors) System.out.println("see " + listName);
	}

	public static void storeError (int line, int col, String s) {
		if (mergeErrors) {
			ErrorRec latest = new ErrorRec(line, col, s);
			if (first == null) first = latest; else last.next = latest;
			last = latest;
		} else printMsg(fileName, line, col, s);
	}

	public static void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "variable expected"; break;
			case 2: s = "\"=\" expected"; break;
			case 3: s = "\"print\" expected"; break;
			case 4: s = "\";\" expected"; break;
			case 5: s = "\"not\" expected"; break;
			case 6: s = "\"\'\" expected"; break;
			case 7: s = "\"true\" expected"; break;
			case 8: s = "\"1\" expected"; break;
			case 9: s = "\"false\" expected"; break;
			case 10: s = "\"0\" expected"; break;
			case 11: s = "\"and\" expected"; break;
			case 12: s = "\"&&\" expected"; break;
			case 13: s = "\".\" expected"; break;
			case 14: s = "\"or\" expected"; break;
			case 15: s = "\"||\" expected"; break;
			case 16: s = "\"+\" expected"; break;
			case 17: s = "??? expected"; break;
			case 18: s = "invalid Or"; break;
			case 19: s = "invalid Factor"; break;
			case 20: s = "invalid And"; break;
			case 21: s = "invalid Primary"; break;
			case 22: s = "invalid True"; break;
			case 23: s = "invalid False"; break;
			default: s = "error " + n; break;
		}
		storeError(line, col, s);
		count++;
	}

	public static void SemErr (int line, int col, int n) {
		storeError(line, col, ("error " + n));
		count++;
	}

	public static void Error (int line, int col, String s) {
		storeError(line, col, s);
		count++;
	}

	public static void Error (String s) {
		if (mergeErrors) mergedList.println(s); else System.out.println(s);
		count++;
	}

	public static void Warn (int line, int col, String s) {
		storeError(line, col, s);
		warns++;
	}

	public static void Warn (String s) {
		if (mergeErrors) mergedList.println(s); else System.out.println(s);
		warns++;
	}

	public static void Exception (String s) {
		System.out.println(s);
		System.exit(1);
	}

	private static void printMsg(String fileName, int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.replace(pos, pos+3, fileName); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{2}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{3}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		System.out.println(b.toString());
	}

} // end Errors
