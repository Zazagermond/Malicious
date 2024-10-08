package Parva;

import java.util.*;
import library.*;



import java.io.*;

public class Parser {
	public static final int _EOF = 0;
	public static final int _identifier = 1;
	public static final int _number = 2;
	public static final int _stringLit = 3;
	public static final int _charLit = 4;
	// terminals
	public static final int EOF_SYM = 0;
	public static final int identifier_Sym = 1;
	public static final int number_Sym = 2;
	public static final int stringLit_Sym = 3;
	public static final int charLit_Sym = 4;
	public static final int void_Sym = 5;
	public static final int lparen_Sym = 6;
	public static final int rparen_Sym = 7;
	public static final int lbrace_Sym = 8;
	public static final int rbrace_Sym = 9;
	public static final int semicolon_Sym = 10;
	public static final int const_Sym = 11;
	public static final int comma_Sym = 12;
	public static final int true_Sym = 13;
	public static final int false_Sym = 14;
	public static final int null_Sym = 15;
	public static final int lbrackrbrack_Sym = 16;
	public static final int int_Sym = 17;
	public static final int bool_Sym = 18;
	public static final int char_Sym = 19;
	public static final int lbrack_Sym = 20;
	public static final int rbrack_Sym = 21;
	public static final int if_Sym = 22;
	public static final int else_Sym = 23;
	public static final int while_Sym = 24;
	public static final int repeat_Sym = 25;
	public static final int until_Sym = 26;
	public static final int do_Sym = 27;
	public static final int break_Sym = 28;
	public static final int continue_Sym = 29;
	public static final int halt_Sym = 30;
	public static final int return_Sym = 31;
	public static final int read_Sym = 32;
	public static final int readLine_Sym = 33;
	public static final int write_Sym = 34;
	public static final int writeLine_Sym = 35;
	public static final int barbar_Sym = 36;
	public static final int andand_Sym = 37;
	public static final int plus_Sym = 38;
	public static final int minus_Sym = 39;
	public static final int bang_Sym = 40;
	public static final int new_Sym = 41;
	public static final int sqr_Sym = 42;
	public static final int sqrt_Sym = 43;
	public static final int star_Sym = 44;
	public static final int slash_Sym = 45;
	public static final int percent_Sym = 46;
	public static final int equalequal_Sym = 47;
	public static final int bangequal_Sym = 48;
	public static final int less_Sym = 49;
	public static final int lessequal_Sym = 50;
	public static final int greater_Sym = 51;
	public static final int greaterequal_Sym = 52;
	public static final int equal_Sym = 53;
	public static final int NOT_SYM = 54;
	// pragmas
	public static final int CodeOn_Sym = 55;
	public static final int CodeOff_Sym = 56;
	public static final int DebugOn_Sym = 57;
	public static final int DebugOff_Sym = 58;
	public static final int StackDump_Sym = 59;
	public static final int HeapDump_Sym = 60;
	public static final int TableDump_Sym = 61;

	public static final int maxT = 54;
	public static final int _CodeOn = 55;
	public static final int _CodeOff = 56;
	public static final int _DebugOn = 57;
	public static final int _DebugOff = 58;
	public static final int _StackDump = 59;
	public static final int _HeapDump = 60;
	public static final int _TableDump = 61;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public static Token token;    // last recognized token   /* pdt */
	public static Token la;       // lookahead token
	static int errDist = minErrDist;

	public static boolean
    debug    = false,
    optimize = false,
    listCode = false,
    warnings = true,
    breaking=false,
    continuing=false;

  static final boolean
    known = true;

  // This next method might better be located in the code generator.  Traditionally
  // it has been left in the ATG file, but that might change in future years
  //
  // Not that while sequences like \n \r and \t result in special mappings to lf, cr and tab
  // other sequences like \x \: and \9 simply map to x, ; and 9 .  Most students don't seem
  // to know this!

  static String unescape(String s) {
  /* Replaces escape sequences in s by their Unicode values */
    StringBuilder buf = new StringBuilder();
    int i = 0;
    while (i < s.length()) {
      if (s.charAt(i) == '\\') {
        switch (s.charAt(i+1)) {
          case '\\': buf.append('\\'); break;
          case '\'': buf.append('\''); break;
          case '\"': buf.append('\"'); break;
          case  'r': buf.append('\r'); break;
          case  'n': buf.append('\n'); break;
          case  't': buf.append('\t'); break;
          case  'b': buf.append('\b'); break;
          case  'f': buf.append('\f'); break;
          default:   buf.append(s.charAt(i+1)); break;
        }
        i += 2;
      }
      else {
        buf.append(s.charAt(i));
        i++;
      }
    }
    return buf.toString();
  } // unescape

 static boolean isArith(int type) {
    return type == Types.intType || type == Types.charType || type == Types.noType;
  } // isArith

  static boolean isBool(int type) {
    return type == Types.boolType || type == Types.noType;
  } // isBool

  static boolean isArray(int type) {
    return (type % 2) == 1;
  } // isArray

  static boolean compatible(int typeOne, int typeTwo) {
  // Returns true if typeOne is compatible (and comparable for equality) with typeTwo
    return    typeOne == typeTwo
           || isArith(typeOne) && isArith(typeTwo)
           || typeOne == Types.noType || typeTwo == Types.noType
           || isArray(typeOne) && typeTwo == Types.nullType
           || isArray(typeTwo) && typeOne == Types.nullType;
  } // compatible

  static boolean assignable(int typeOne, int typeTwo) {
  // Returns true if a variable of typeOne may be assigned a value of typeTwo
    return    typeOne == typeTwo
           || typeOne == Types.intType && typeTwo == Types.charType
           || typeOne == Types.noType || typeTwo == Types.noType
           || isArray(typeOne) && typeTwo == Types.nullType;
  } // assignable

/* -------------------------------------------------------------------------- */



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
			if (la.kind == CodeOn_Sym) {
				listCode  = true;
			}
			if (la.kind == CodeOff_Sym) {
				listCode  = false;
			}
			if (la.kind == DebugOn_Sym) {
				debug = true;
			}
			if (la.kind == DebugOff_Sym) {
				debug = false;
			}
			if (la.kind == StackDump_Sym) {
				if (debug) CodeGen.stack();
			}
			if (la.kind == HeapDump_Sym) {
				if (debug) CodeGen.heap();
			}
			if (la.kind == TableDump_Sym) {
				if (debug) Table.printTable(OutFile.StdOut);
				if (debug) Types.show(OutFile.StdOut);
			}

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

	static void Parva() {
		Expect(void_Sym);
		Entry program = new Entry();
		program.name = Ident();
		Expect(lparen_Sym);
		Expect(rparen_Sym);
		program.kind = Kinds.Fun;
		program.type = Types.voidType;
		Table.insert(program);
		StackFrame frame = new StackFrame();
		Table.openScope();
		Label DSPLabel = new Label(known);
		CodeGen.openStackFrame(0);
		ExpectWeak(lbrace_Sym, 1);
		while (StartOf(2)) {
			Statement(frame);
		}
		ExpectWeak(rbrace_Sym, 3);
		CodeGen.fixDSP(DSPLabel.address(), frame.size);
		CodeGen.leaveProgram();
		Table.closeScope();
	}

	static String Ident() {
		String name;
		Expect(identifier_Sym);
		name = token.val;
		return name;
	}

	static void Statement(StackFrame frame) {
		while (!(StartOf(3))) {SynErr(55); Get();}
		switch (la.kind) {
		case lbrace_Sym: {
			Block(frame);
			break;
		}
		case const_Sym: {
			ConstDeclarations();
			break;
		}
		case int_Sym: case bool_Sym: case char_Sym: {
			VarDeclarations(frame);
			break;
		}
		case identifier_Sym: {
			AssignmentStatement();
			break;
		}
		case if_Sym: {
			IfStatement(frame);
			break;
		}
		case while_Sym: {
			WhileStatement(frame);
			break;
		}
		case halt_Sym: {
			HaltStatement();
			break;
		}
		case return_Sym: {
			ReturnStatement();
			break;
		}
		case read_Sym: case readLine_Sym: {
			ReadStatement();
			break;
		}
		case write_Sym: case writeLine_Sym: {
			WriteStatement();
			break;
		}
		case do_Sym: {
			DoWhileStatement(frame);
			break;
		}
		case repeat_Sym: {
			RepeatStatement(frame);
			break;
		}
		case break_Sym: {
			Breakstatement();
			break;
		}
		case continue_Sym: {
			ContinueStatement();
			break;
		}
		case semicolon_Sym: {
			Get();
			if (warnings) {Warning("empty statement"); if (breaking){Warning("Break outside loop");}if (continuing){Warning("continue outside loop");} }
			break;
		}
		default: SynErr(56); break;
		}
	}

	static void Block(StackFrame frame) {
		Table.openScope();
		boolean empty = true;
		Expect(lbrace_Sym);
		while (StartOf(2)) {
			Statement(frame);
			empty = false;
		}
		if (empty && warnings) Warning("empty {} block");
		ExpectWeak(rbrace_Sym, 4);
		Table.closeScope();
	}

	static void ConstDeclarations() {
		Expect(const_Sym);
		OneConst();
		while (WeakSeparator(comma_Sym, 5, 6)) {
			OneConst();
		}
		ExpectWeak(semicolon_Sym, 4);
	}

	static void VarDeclarations(StackFrame frame) {
		int type;
		type = Type();
		VarList(frame, type);
		ExpectWeak(semicolon_Sym, 4);
	}

	static void AssignmentStatement() {
		int expType;
		DesType des; Label exitBreak = new Label(!known);
		if(breaking){CodeGen.branch(exitBreak);}
		des = Designator();
		if (des.entry.kind != Kinds.Var)
		   SemError("cannot assign to " + Kinds.kindNames[des.entry.kind]);
		AssignOp();
		expType = Expression();
		if (!assignable(des.type, expType))
		  SemError("incompatible types in assignment");
		CodeGen.assign(des.type);
		ExpectWeak(semicolon_Sym, 4);
		exitBreak.here();
	}

	static void IfStatement(StackFrame frame) {
		Label falseLabel = new Label(!known);  Label trueLabel = new Label(!known); Label exitBreak = new Label(!known);
		Expect(if_Sym);
		Expect(lparen_Sym);
		Condition();
		Expect(rparen_Sym);
		CodeGen.branchFalse(falseLabel);
		Statement(frame);
		CodeGen.branchFalse(trueLabel); falseLabel.here(); if (breaking){CodeGen.branch(exitBreak);}
		if (la.kind == else_Sym) {
			Get();
			Statement(frame);
			if (breaking){CodeGen.branch(exitBreak);} if (continuing){CodeGen.branch(exitBreak);}
		} else if (StartOf(7)) {
		} else SynErr(57);
		trueLabel.here(); exitBreak.here();
	}

	static void WhileStatement(StackFrame frame) {
		Label loopExit  = new Label(!known);
		Label loopStart = new Label(known); Label exitBreak = new Label(!known);
		Expect(while_Sym);
		Expect(lparen_Sym);
		Condition();
		Expect(rparen_Sym);
		CodeGen.branchFalse(loopExit);
		Statement(frame);
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){ continuing=false;} CodeGen.branch(loopStart);
		loopExit.here(); exitBreak.here(); breaking=false;
	}

	static void HaltStatement() {
		Expect(halt_Sym);
		if (la.kind == lparen_Sym) {
			Get();
			if (StartOf(8)) {
				WriteList();
			}
			Expect(rparen_Sym);
		}
		ExpectWeak(semicolon_Sym, 4);
		CodeGen.leaveProgram();
	}

	static void ReturnStatement() {
		Label exitBreak = new Label(!known);
		Expect(return_Sym);
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);} CodeGen.leaveProgram();
		ExpectWeak(semicolon_Sym, 4);
		exitBreak.here();
	}

	static void ReadStatement() {
		Label exitBreak = new Label(!known);
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
		if (la.kind == read_Sym) {
			Get();
			Expect(lparen_Sym);
			ReadList();
			Expect(rparen_Sym);
		} else if (la.kind == readLine_Sym) {
			Get();
			Expect(lparen_Sym);
			if (la.kind == identifier_Sym || la.kind == stringLit_Sym) {
				ReadList();
			}
			Expect(rparen_Sym);
			CodeGen.readLine();
		} else SynErr(58);
		ExpectWeak(semicolon_Sym, 4);
		exitBreak.here();
	}

	static void WriteStatement() {
		Label exitBreak = new Label(!known);
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
		if (la.kind == write_Sym) {
			Get();
			Expect(lparen_Sym);
			WriteList();
			Expect(rparen_Sym);
		} else if (la.kind == writeLine_Sym) {
			Get();
			Expect(lparen_Sym);
			if (StartOf(8)) {
				WriteList();
			}
			Expect(rparen_Sym);
			CodeGen.writeLine();
		} else SynErr(59);
		ExpectWeak(semicolon_Sym, 4);
		exitBreak.here();
	}

	static void DoWhileStatement(StackFrame frame) {
		Expect(do_Sym);
		Statement(frame);
		ExpectWeak(while_Sym, 9);
		Expect(lparen_Sym);
		Condition();
		Expect(rparen_Sym);
		ExpectWeak(semicolon_Sym, 4);
	}

	static void RepeatStatement(StackFrame frame) {
		Expect(repeat_Sym);
		while (StartOf(2)) {
			Statement(frame);
		}
		ExpectWeak(until_Sym, 9);
		Expect(lparen_Sym);
		Condition();
		Expect(rparen_Sym);
		ExpectWeak(semicolon_Sym, 4);
	}

	static void Breakstatement() {
		Expect(break_Sym);
		breaking=true;
		ExpectWeak(semicolon_Sym, 4);
	}

	static void ContinueStatement() {
		Expect(continue_Sym);
		continuing=true;
		ExpectWeak(semicolon_Sym, 4);
	}

	static void OneConst() {
		Entry constant = new Entry();
		ConstRec con;
		constant.name = Ident();
		constant.kind = Kinds.Con;
		AssignOp();
		con = Constant();
		constant.value = con.value;
		constant.type = con.type;
		Table.insert(constant);
	}

	static void AssignOp() {
		Expect(equal_Sym);
	}

	static ConstRec Constant() {
		ConstRec con;
		con = new ConstRec();
		if (la.kind == number_Sym) {
			con.value = IntConst();
			con.type = Types.intType;
		} else if (la.kind == charLit_Sym) {
			con.value = CharConst();
			con.type = Types.charType;
		} else if (la.kind == true_Sym) {
			Get();
			con.type = Types.boolType; con.value = 1;
		} else if (la.kind == false_Sym) {
			Get();
			con.type = Types.boolType; con.value = 0;
		} else if (la.kind == null_Sym) {
			Get();
			con.type = Types.nullType; con.value = 0;
		} else SynErr(60);
		return con;
	}

	static int IntConst() {
		int value;
		Expect(number_Sym);
		try {
		  value = Integer.parseInt(token.val);
		} catch (NumberFormatException e) {
		  value = 0; SemError("number out of range");
		}
		return value;
	}

	static int CharConst() {
		int value;
		Expect(charLit_Sym);
		String str = token.val;
		str = unescape(str.substring(1, str.length() - 1));
		value = str.charAt(0);
		return value;
	}

	static int Type() {
		int type;
		type = BasicType();
		if (la.kind == lbrackrbrack_Sym) {
			Get();
			if (type != Types.noType) type++;
		}
		return type;
	}

	static void VarList(StackFrame frame, int type) {
		OneVar(frame, type);
		while (WeakSeparator(comma_Sym, 5, 6)) {
			OneVar(frame, type);
		}
	}

	static int BasicType() {
		int type;
		type = Types.noType;
		if (la.kind == int_Sym) {
			Get();
			type = Types.intType;
		} else if (la.kind == bool_Sym) {
			Get();
			type = Types.boolType;
		} else if (la.kind == char_Sym) {
			Get();
			type = Types.charType;
		} else SynErr(61);
		return type;
	}

	static void OneVar(StackFrame frame, int type) {
		int expType;
		Entry var = new Entry();
		var.name = Ident();
		var.kind = Kinds.Var;
		var.type = type;
		var.offset = frame.size;
		frame.size++;
		if (la.kind == equal_Sym) {
			AssignOp();
			CodeGen.loadAddress(var);
			expType = Expression();
			if (!assignable(var.type, expType))
			  SemError("incompatible types in assignment");
			CodeGen.assign(var.type);
		}
		Table.insert(var);
	}

	static int Expression() {
		int type;
		Label exitBreak = new Label(!known);
		int type2;
		Label shortcircuit = new Label(!known);
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
		type = AndExp();
		while (la.kind == barbar_Sym) {
			Get();
			CodeGen.booleanOp(shortcircuit, CodeGen.or);
			type2 = AndExp();
			if (!isBool(type) || !isBool(type2))
			  SemError("Boolean operands needed");
			type = Types.boolType;
		}
		shortcircuit.here();exitBreak.here();
		return type;
	}

	static DesType Designator() {
		DesType des;
		String name;
		int indexType;
		name = Ident();
		Entry entry = Table.find(name);
		if (!entry.declared)
		  SemError("undeclared identifier");
		des = new DesType(entry);
		if (entry.kind == Kinds.Var)
		  CodeGen.loadAddress(entry);
		if (la.kind == lbrack_Sym) {
			Get();
			if (isArray(des.type)) des.type--;
			else SemError("unexpected subscript");
			if (des.entry.kind != Kinds.Var)
			  SemError("unexpected subscript");
			CodeGen.dereference();
			indexType = Expression();
			if (!isArith(indexType))
			  SemError("invalid subscript type");
			CodeGen.index();
			Expect(rbrack_Sym);
		}
		return des;
	}

	static void Condition() {
		Label exitBreak = new Label(!known);
		int type;
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
		type = Expression();
		if (!isBool(type))
		     SemError("boolean expression needed");exitBreak.here();
	}

	static void WriteList() {
		Label exitBreak = new Label(!known);
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
		WriteElement();
		while (WeakSeparator(comma_Sym, 8, 10)) {
			WriteElement();
		}
		exitBreak.here();
	}

	static void ReadList() {
		Label exitBreak = new Label(!known);
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
		ReadElement();
		while (WeakSeparator(comma_Sym, 11, 10)) {
			ReadElement();
		}
		exitBreak.here();
	}

	static void ReadElement() {
		Label exitBreak = new Label(!known);
		String str;
		DesType des;
		if (la.kind == stringLit_Sym) {
			if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
			str = StringConst();
			CodeGen.writeString(str);
		} else if (la.kind == identifier_Sym) {
			des = Designator();
			if (des.entry.kind != Kinds.Var)
			  SemError("wrong kind of identifier");
			switch (des.type) {
			  case Types.intType:
			  case Types.boolType:
			case Types.charType:
			    CodeGen.read(des.type); break;
			  default:
			    SemError("cannot read this type"); break;
			}
			exitBreak.here();
		} else SynErr(62);
	}

	static String StringConst() {
		String str;
		Expect(stringLit_Sym);
		str = token.val;
		str = unescape(str.substring(1, str.length() - 1));
		return str;
	}

	static void WriteElement() {
		Label exitBreak = new Label(!known);
		int expType;
		String str;
		if (la.kind == stringLit_Sym) {
			if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
			str = StringConst();
			CodeGen.writeString(str);
		} else if (StartOf(12)) {
			expType = Expression();
			if (!(isArith(expType) || expType == Types.boolType))
			  SemError("cannot write this type");
			switch (expType) {
			  case Types.intType:
			  case Types.boolType:
			case Types.charType:
			    CodeGen.write(expType); break;
			  default:
			    break;
			} exitBreak.here();
		} else SynErr(63);
	}

	static int AndExp() {
		int type;
		Label exitBreak = new Label(!known);
		int type2;
		Label shortcircuit = new Label(!known);
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
		type = EqlExp();
		while (la.kind == andand_Sym) {
			Get();
			CodeGen.booleanOp(shortcircuit, CodeGen.and);
			type2 = EqlExp();
			if (!isBool(type) || !isBool(type2))
			  SemError("Boolean operands needed");
			type = Types.boolType;
		}
		shortcircuit.here(); exitBreak.here();
		return type;
	}

	static int EqlExp() {
		int type;
		Label exitBreak = new Label(!known);
		int type2;
		int op;
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
		type = RelExp();
		while (la.kind == equalequal_Sym || la.kind == bangequal_Sym) {
			op = EqualOp();
			type2 = RelExp();
			if (!compatible(type, type2))
			  SemError("incomparable operand types");
			CodeGen.comparison(op, type);
			type = Types.boolType;
		}
		exitBreak.here();
		return type;
	}

	static int RelExp() {
		int type;
		Label exitBreak = new Label(!known);
		int type2;
		int op;
		if(breaking){CodeGen.branch(exitBreak);}if(continuing){CodeGen.branch(exitBreak);}
		type = AddExp();
		if (StartOf(13)) {
			op = RelOp();
			type2 = AddExp();
			if (!isArith(type) || !isArith(type2))
			  SemError("incomparable operand types");
			CodeGen.comparison(op, type);
			type = Types.boolType;
		}
		exitBreak.here();
		return type;
	}

	static int EqualOp() {
		int op;
		op = CodeGen.nop;
		if (la.kind == equalequal_Sym) {
			Get();
			op = CodeGen.ceq;
		} else if (la.kind == bangequal_Sym) {
			Get();
			op = CodeGen.cne;
		} else SynErr(64);
		return op;
	}

	static int AddExp() {
		int type;
		int type2;
		int op;
		type = MultExp();
		while (la.kind == plus_Sym || la.kind == minus_Sym) {
			op = AddOp();
			type2 = MultExp();
			if (!isArith(type) || !isArith(type2)) {
			  SemError("arithmetic operands needed");
			  type = Types.noType;
			}
			else type = Types.intType;
			CodeGen.binaryOp(op);
		}
		return type;
	}

	static int RelOp() {
		int op;
		op = CodeGen.nop;
		if (la.kind == less_Sym) {
			Get();
			op = CodeGen.clt;
		} else if (la.kind == lessequal_Sym) {
			Get();
			op = CodeGen.cle;
		} else if (la.kind == greater_Sym) {
			Get();
			op = CodeGen.cgt;
		} else if (la.kind == greaterequal_Sym) {
			Get();
			op = CodeGen.cge;
		} else SynErr(65);
		return op;
	}

	static int MultExp() {
		int type;
		int type2;
		int op;
		type = Factor();
		while (la.kind == star_Sym || la.kind == slash_Sym || la.kind == percent_Sym) {
			op = MulOp();
			type2 = Factor();
			if (!isArith(type) || !isArith(type2)) {
			  SemError("arithmetic operands needed");
			  type = Types.noType;
			}
			else type = Types.intType;
			CodeGen.binaryOp(op);
		}
		return type;
	}

	static int AddOp() {
		int op;
		op = CodeGen.nop;
		if (la.kind == plus_Sym) {
			Get();
			op = CodeGen.add;
		} else if (la.kind == minus_Sym) {
			Get();
			op = CodeGen.sub;
		} else SynErr(66);
		return op;
	}

	static int Factor() {
		int type;
		type = Types.noType;
		if (StartOf(14)) {
			type = Primary();
		} else if (la.kind == plus_Sym) {
			Get();
			type = Factor();
			if (!isArith(type)) {
			  SemError("arithmetic operand needed");
			  type = Types.noType;
			}
			else type = Types.intType;
		} else if (la.kind == minus_Sym) {
			Get();
			type = Factor();
			if (!isArith(type)) {
			  SemError("arithmetic operand needed");
			  type = Types.noType;
			}
			else type = Types.intType;
			CodeGen.negateInteger();
		} else if (la.kind == bang_Sym) {
			Get();
			type = Factor();
			if (!isBool(type))
			  SemError("Boolean operand needed");
			type = Types.boolType;
			CodeGen.negateBoolean();
		} else SynErr(67);
		return type;
	}

	static int MulOp() {
		int op;
		op = CodeGen.nop;
		if (la.kind == star_Sym) {
			Get();
			op = CodeGen.mul;
		} else if (la.kind == slash_Sym) {
			Get();
			op = CodeGen.div;
		} else if (la.kind == percent_Sym) {
			Get();
			op = CodeGen.rem;
		} else SynErr(68);
		return op;
	}

	static int Primary() {
		int type;
		type = Types.noType;
		int size;
		DesType des;
		ConstRec con;
		switch (la.kind) {
		case identifier_Sym: {
			des = Designator();
			type = des.type;
			switch (des.entry.kind) {
			  case Kinds.Var:
			    CodeGen.dereference();
			    break;
			  case Kinds.Con:
			    CodeGen.loadConstant(des.entry.value);
			    break;
			  default:
			    SemError("wrong kind of identifier");
			    break;
			}
			break;
		}
		case number_Sym: case charLit_Sym: case true_Sym: case false_Sym: case null_Sym: {
			con = Constant();
			type = con.type;
			CodeGen.loadConstant(con.value);
			break;
		}
		case new_Sym: {
			Get();
			type = BasicType();
			Expect(lbrack_Sym);
			size = Expression();
			if (!isArith(size))
			  SemError("array size must be integer");
			type++;
			Expect(rbrack_Sym);
			CodeGen.allocate();
			break;
		}
		case sqr_Sym: {
			Get();
			Expect(lparen_Sym);
			type = Expression();
			if (!isArith(type))
			SemError("Arithmetic argument needed");
			type = Types.intType;
			Expect(rparen_Sym);
			break;
		}
		case sqrt_Sym: {
			Get();
			Expect(lparen_Sym);
			type = Expression();
			if (!isArith(type))
			SemError("Arithmetic argument needed");
			type = Types.intType;
			Expect(rparen_Sym);
			break;
		}
		case lparen_Sym: {
			Get();
			type = Expression();
			Expect(rparen_Sym);
			break;
		}
		default: SynErr(69); break;
		}
		return type;
	}



	public static void Parse() {
		la = new Token();
		la.val = "";
		Get();
		Parva();
		Expect(EOF_SYM);

	}

	private static boolean[][] set = {
		{T,T,x,x, x,x,x,x, T,x,T,T, x,x,x,x, x,T,T,T, x,x,T,x, T,T,x,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{T,T,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,T,T,T, x,x,T,x, T,T,x,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,x,x, T,x,T,T, x,x,x,x, x,T,T,T, x,x,T,x, T,T,x,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{T,T,x,x, x,x,x,x, T,x,T,T, x,x,x,x, x,T,T,T, x,x,T,x, T,T,x,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{T,T,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,T,T,T, x,x,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,T,T,T, x,x,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,T,T, T,x,T,x, x,x,x,x, x,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x},
		{T,T,x,x, x,x,T,x, T,x,T,T, x,x,x,x, x,T,T,T, x,x,T,x, T,T,x,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,T,x, T,x,T,x, x,x,x,x, x,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,x,x,x},
		{x,T,T,x, T,x,T,x, x,x,x,x, x,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x}

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
			case 1: s = "identifier expected"; break;
			case 2: s = "number expected"; break;
			case 3: s = "stringLit expected"; break;
			case 4: s = "charLit expected"; break;
			case 5: s = "\"void\" expected"; break;
			case 6: s = "\"(\" expected"; break;
			case 7: s = "\")\" expected"; break;
			case 8: s = "\"{\" expected"; break;
			case 9: s = "\"}\" expected"; break;
			case 10: s = "\";\" expected"; break;
			case 11: s = "\"const\" expected"; break;
			case 12: s = "\",\" expected"; break;
			case 13: s = "\"true\" expected"; break;
			case 14: s = "\"false\" expected"; break;
			case 15: s = "\"null\" expected"; break;
			case 16: s = "\"[]\" expected"; break;
			case 17: s = "\"int\" expected"; break;
			case 18: s = "\"bool\" expected"; break;
			case 19: s = "\"char\" expected"; break;
			case 20: s = "\"[\" expected"; break;
			case 21: s = "\"]\" expected"; break;
			case 22: s = "\"if\" expected"; break;
			case 23: s = "\"else\" expected"; break;
			case 24: s = "\"while\" expected"; break;
			case 25: s = "\"repeat\" expected"; break;
			case 26: s = "\"until\" expected"; break;
			case 27: s = "\"do\" expected"; break;
			case 28: s = "\"break\" expected"; break;
			case 29: s = "\"continue\" expected"; break;
			case 30: s = "\"halt\" expected"; break;
			case 31: s = "\"return\" expected"; break;
			case 32: s = "\"read\" expected"; break;
			case 33: s = "\"readLine\" expected"; break;
			case 34: s = "\"write\" expected"; break;
			case 35: s = "\"writeLine\" expected"; break;
			case 36: s = "\"||\" expected"; break;
			case 37: s = "\"&&\" expected"; break;
			case 38: s = "\"+\" expected"; break;
			case 39: s = "\"-\" expected"; break;
			case 40: s = "\"!\" expected"; break;
			case 41: s = "\"new\" expected"; break;
			case 42: s = "\"sqr\" expected"; break;
			case 43: s = "\"sqrt\" expected"; break;
			case 44: s = "\"*\" expected"; break;
			case 45: s = "\"/\" expected"; break;
			case 46: s = "\"%\" expected"; break;
			case 47: s = "\"==\" expected"; break;
			case 48: s = "\"!=\" expected"; break;
			case 49: s = "\"<\" expected"; break;
			case 50: s = "\"<=\" expected"; break;
			case 51: s = "\">\" expected"; break;
			case 52: s = "\">=\" expected"; break;
			case 53: s = "\"=\" expected"; break;
			case 54: s = "??? expected"; break;
			case 55: s = "this symbol not expected in Statement"; break;
			case 56: s = "invalid Statement"; break;
			case 57: s = "invalid IfStatement"; break;
			case 58: s = "invalid ReadStatement"; break;
			case 59: s = "invalid WriteStatement"; break;
			case 60: s = "invalid Constant"; break;
			case 61: s = "invalid BasicType"; break;
			case 62: s = "invalid ReadElement"; break;
			case 63: s = "invalid WriteElement"; break;
			case 64: s = "invalid EqualOp"; break;
			case 65: s = "invalid RelOp"; break;
			case 66: s = "invalid AddOp"; break;
			case 67: s = "invalid Factor"; break;
			case 68: s = "invalid MulOp"; break;
			case 69: s = "invalid Primary"; break;
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
