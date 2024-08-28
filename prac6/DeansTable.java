// Handle cross reference table for EBNF productions
// P.D. Terry, modified by KL Bradshaw 2024 

// Complete this skeleton for Prac 6, or develop your own version

package EBNF;

import java.util.*;
import library.*;

  class Entry {                      // Cross reference table entries
    public String name;              // The identifier itself
    public ArrayList<Integer> refs;  // Line numbers where it appears
    public Entry(String name) {
      this.name = name;
      this.refs = new ArrayList<Integer>();
    }
  } // Entry

  class Table {
    static ArrayList<Entry> SymTable=new ArrayList<>();
    public static void clearTable() {
    // Clears cross-reference table
      SymTable.clear();
    } // clearTable

    public static void addRef(String name, boolean declared, int lineRef) {
    // What do you suppose is the purpose of the "declared" parameter?
      if (declared==true){
        for (Entry i:SymTable){
          if (i.name.equals(name))
              i.refs.add(lineRef);
      }}else{
        Entry clone=new Entry(name);
        clone.refs.add(0-lineRef);
        SymTable.add(clone);
      }
    } // addRef

    public static void printTable() {
    // Prints out all references in the table (eliminate duplicates line numbers)
    String outer;
    String roller="";
      for (Entry i:SymTable){
        for (int refer:i.refs){
          roller=roller+"   "+refer;
        }
        outer=String.format("%s %6s %s", i.name,roller);
        System.out.println(outer);
      }
    } // printTable

  } // Table
