package Bool;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.BitSet;

class Token {
	public int kind;    // token kind
	public int pos;     // token position in the source text (starting at 0)
	public int col;     // token column (starting at 0)
	public int line;    // token line (starting at 1)
	public String val;  // token value
	public Token next;  // AW 2003-03-07 Tokens are kept in linked list
}

class Buffer {
	public static final char EOF = (char)256;
	static byte[] buf;
	static int bufLen;
	static int pos;

	public static void Fill (FileInputStream s) {
		try {
			bufLen = s.available();
			buf = new byte[bufLen];
			s.read(buf, 0, bufLen);
			pos = 0;
		} catch (IOException e){
			System.out.println("--- error on filling the buffer ");
			System.exit(1);
		}
	}

	public static int Read () {
		if (pos < bufLen) return buf[pos++] & 0xff;  // mask out sign bits
		else return EOF;                             /* pdt */
	}

	public static int Peek () {
		if (pos < bufLen) return buf[pos] & 0xff;    // mask out sign bits
		else return EOF;                             /* pdt */
	}

	/* AW 2003-03-10 moved this from ParserGen.cs */
	public static String GetString (int beg, int end) {
		StringBuffer s = new StringBuffer(64);
		int oldPos = Buffer.getPos();
		Buffer.setPos(beg);
		while (beg < end) { s.append((char)Buffer.Read()); beg++; }
		Buffer.setPos(oldPos);
		return s.toString();
	}

	public static int getPos() {
		return pos;
	}

	public static void setPos (int value) {
		if (value < 0) pos = 0;
		else if (value >= bufLen) pos = bufLen;
		else pos = value;
	}

} // end Buffer

public class Scanner {
	static final char EOL = '\n';
	static final int  eofSym = 0;
	static final int charSetSize = 256;
	static final int maxT = 17;
	static final int noSym = 17;
	// terminals
	static final int EOF_SYM = 0;
	static final int variable_Sym = 1;
	static final int equal_Sym = 2;
	static final int print_Sym = 3;
	static final int semicolon_Sym = 4;
	static final int not_Sym = 5;
	static final int squote_Sym = 6;
	static final int true_Sym = 7;
	static final int d1_Sym = 8;
	static final int false_Sym = 9;
	static final int d0_Sym = 10;
	static final int and_Sym = 11;
	static final int andand_Sym = 12;
	static final int point_Sym = 13;
	static final int or_Sym = 14;
	static final int barbar_Sym = 15;
	static final int plus_Sym = 16;
	static final int NOT_SYM = 17;
	// pragmas

	static short[] start = {
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0, 22, 10,  0,  0,  0, 28,  0,  0, 24,  0,
	 19, 14,  0,  0,  0,  0,  0,  0,  0,  0,  0,  7,  0,  2,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0, 33,  1,  1,  1,  1, 32,  1,  1,  1,  1,  1,  1,  1, 30, 34,
	 29,  1,  1,  1, 31,  1,  1,  1,  1,  1,  1,  0, 26,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	  -1};
	static char valCh;       // current input character (for token.val)

	static Token t;          // current token
	static char ch;          // current input character
	static int pos;          // column number of current character
	static int line;         // line number of current character
	static int lineStart;    // start position of current line
	static int oldEols;      // EOLs that appeared in a comment;
	static BitSet ignore;    // set of characters to be ignored by the scanner

	static Token tokens;     // the complete input token stream
	static Token pt;         // current peek token

	public static void Init (String fileName) {
		FileInputStream s = null;
		try {
			s = new FileInputStream(fileName);
			Init(s);
		} catch (IOException e) {
			System.out.println("--- Cannot open file " + fileName);
			System.exit(1);
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					System.out.println("--- Cannot close file " + fileName);
					System.exit(1);
				}
			}
		}
	}

	public static void Init (FileInputStream s) {
		Buffer.Fill(s);
		pos = -1; line = 1; lineStart = 0;
		oldEols = 0;
		NextCh();
		ignore = new BitSet(charSetSize+1);
		ignore.set(' '); // blanks are always white space
		ignore.set(0); ignore.set(1); ignore.set(2); ignore.set(3); 
		ignore.set(4); ignore.set(5); ignore.set(6); ignore.set(7); 
		ignore.set(8); ignore.set(9); ignore.set(10); ignore.set(11); 
		ignore.set(12); ignore.set(13); ignore.set(14); ignore.set(15); 
		ignore.set(16); ignore.set(17); ignore.set(18); ignore.set(19); 
		ignore.set(20); ignore.set(21); ignore.set(22); ignore.set(23); 
		ignore.set(24); ignore.set(25); ignore.set(26); ignore.set(27); 
		ignore.set(28); ignore.set(29); ignore.set(30); ignore.set(31); 
		
		//--- AW: fill token list
		tokens = new Token();  // first token is a dummy
		Token node = tokens;
		do {
			node.next = NextToken();
			node = node.next;
		} while (node.kind != eofSym);
		node.next = node;
		node.val = "EOF";
		t = pt = tokens;
	}

	static void NextCh() {
		if (oldEols > 0) { ch = EOL; oldEols--; }
		else {
			ch = (char)Buffer.Read(); pos++;
			// replace isolated '\r' by '\n' in order to make
			// eol handling uniform across Windows, Unix and Mac
			if (ch == '\r' && Buffer.Peek() != '\n') ch = EOL;
			if (ch == EOL) { line++; lineStart = pos + 1; }
		}
		valCh = ch;
		if (ch != Buffer.EOF) ch = Character.toLowerCase(ch);
	}


	static boolean Comment0() {
		int level = 1, line0 = line, lineStart0 = lineStart;
		NextCh();
		if (ch == '*') {
			NextCh();
			for(;;) {
				if (ch == '*') {
					NextCh();
					if (ch == '/') {
						level--;
						if (level == 0) { oldEols = line - line0; NextCh(); return true; }
						NextCh();
					}
				} else if (ch == '/') {
					NextCh();
					if (ch == '*') {
						level++; NextCh();
					}
				} else if (ch == Buffer.EOF) return false;
				else NextCh();
			}
		} else {
			if (ch == EOL) { line--; lineStart = lineStart0; }
			pos = pos - 2; Buffer.setPos(pos+1); NextCh();
		}
		return false;
	}

	static boolean Comment1() {
		int level = 1, line0 = line, lineStart0 = lineStart;
		NextCh();
		if (ch == '*') {
			NextCh();
			for(;;) {
				if (ch == '*') {
					NextCh();
					if (ch == ')') {
						level--;
						if (level == 0) { oldEols = line - line0; NextCh(); return true; }
						NextCh();
					}
				} else if (ch == '(') {
					NextCh();
					if (ch == '*') {
						level++; NextCh();
					}
				} else if (ch == Buffer.EOF) return false;
				else NextCh();
			}
		} else {
			if (ch == EOL) { line--; lineStart = lineStart0; }
			pos = pos - 2; Buffer.setPos(pos+1); NextCh();
		}
		return false;
	}


	static void CheckLiteral() {
		String lit = t.val.toLowerCase();
		
	}

	/* AW Scan() renamed to NextToken() */
	static Token NextToken() {
		while (ignore.get(ch)) NextCh();
		if (ch == '/' && Comment0() ||ch == '(' && Comment1()) return NextToken();
		t = new Token();
		t.pos = pos; t.col = pos - lineStart + 1; t.line = line;
		int state = start[ch];
		StringBuffer buf = new StringBuffer(16);
		buf.append(valCh); NextCh();
		boolean done = false;
		while (!done) {
			switch (state) {
				case -1: { t.kind = eofSym; done = true; break; }  // NextCh already done /* pdt */
				case 0: { t.kind = noSym; done = true; break; }    // NextCh already done
				case 1:
					{ t.kind = variable_Sym; done = true; break; }
				case 2:
					{ t.kind = equal_Sym; done = true; break; }
				case 3:
					if (ch == 'i') { buf.append(valCh); NextCh(); state = 4; break;}
					else { t.kind = noSym; done = true; break; }
				case 4:
					if (ch == 'n') { buf.append(valCh); NextCh(); state = 5; break;}
					else { t.kind = noSym; done = true; break; }
				case 5:
					if (ch == 't') { buf.append(valCh); NextCh(); state = 6; break;}
					else { t.kind = noSym; done = true; break; }
				case 6:
					{ t.kind = print_Sym; done = true; break; }
				case 7:
					{ t.kind = semicolon_Sym; done = true; break; }
				case 8:
					if (ch == 't') { buf.append(valCh); NextCh(); state = 9; break;}
					else { t.kind = noSym; done = true; break; }
				case 9:
					{ t.kind = not_Sym; done = true; break; }
				case 10:
					{ t.kind = squote_Sym; done = true; break; }
				case 11:
					if (ch == 'u') { buf.append(valCh); NextCh(); state = 12; break;}
					else { t.kind = noSym; done = true; break; }
				case 12:
					if (ch == 'e') { buf.append(valCh); NextCh(); state = 13; break;}
					else { t.kind = noSym; done = true; break; }
				case 13:
					{ t.kind = true_Sym; done = true; break; }
				case 14:
					{ t.kind = d1_Sym; done = true; break; }
				case 15:
					if (ch == 'l') { buf.append(valCh); NextCh(); state = 16; break;}
					else { t.kind = noSym; done = true; break; }
				case 16:
					if (ch == 's') { buf.append(valCh); NextCh(); state = 17; break;}
					else { t.kind = noSym; done = true; break; }
				case 17:
					if (ch == 'e') { buf.append(valCh); NextCh(); state = 18; break;}
					else { t.kind = noSym; done = true; break; }
				case 18:
					{ t.kind = false_Sym; done = true; break; }
				case 19:
					{ t.kind = d0_Sym; done = true; break; }
				case 20:
					if (ch == 'd') { buf.append(valCh); NextCh(); state = 21; break;}
					else { t.kind = noSym; done = true; break; }
				case 21:
					{ t.kind = and_Sym; done = true; break; }
				case 22:
					if (ch == '&') { buf.append(valCh); NextCh(); state = 23; break;}
					else { t.kind = noSym; done = true; break; }
				case 23:
					{ t.kind = andand_Sym; done = true; break; }
				case 24:
					{ t.kind = point_Sym; done = true; break; }
				case 25:
					{ t.kind = or_Sym; done = true; break; }
				case 26:
					if (ch == '|') { buf.append(valCh); NextCh(); state = 27; break;}
					else { t.kind = noSym; done = true; break; }
				case 27:
					{ t.kind = barbar_Sym; done = true; break; }
				case 28:
					{ t.kind = plus_Sym; done = true; break; }
				case 29:
					if (ch == 'r') { buf.append(valCh); NextCh(); state = 3; break;}
					else { t.kind = variable_Sym; done = true; break; }
				case 30:
					if (ch == 'o') { buf.append(valCh); NextCh(); state = 8; break;}
					else { t.kind = variable_Sym; done = true; break; }
				case 31:
					if (ch == 'r') { buf.append(valCh); NextCh(); state = 11; break;}
					else { t.kind = variable_Sym; done = true; break; }
				case 32:
					if (ch == 'a') { buf.append(valCh); NextCh(); state = 15; break;}
					else { t.kind = variable_Sym; done = true; break; }
				case 33:
					if (ch == 'n') { buf.append(valCh); NextCh(); state = 20; break;}
					else { t.kind = variable_Sym; done = true; break; }
				case 34:
					if (ch == 'r') { buf.append(valCh); NextCh(); state = 25; break;}
					else { t.kind = variable_Sym; done = true; break; }

			}
		}
		t.val = buf.toString();
		return t;
	}

	/* AW 2003-03-07 get the next token, move on and synch peek token with current */
	public static Token Scan () {
		t = pt = t.next;
		return t;
	}

	/* AW 2003-03-07 get the next token, ignore pragmas */
	public static Token Peek () {
		do {                      // skip pragmas while peeking
			pt = pt.next;
		} while (pt.kind > maxT);
		return pt;
	}

	/* AW 2003-03-11 to make sure peek start at current scan position */
	public static void ResetPeek () { pt = t; }

} // end Scanner
