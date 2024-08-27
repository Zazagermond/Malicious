// Handle cross reference table for EBNF productions
// P.D. Terry, modified by KL Bradshaw 2024 

// Complete this skeleton for Prac 6, or develop your own version

package EBNF;

import java.util.*;
import library.*;
  
  class Entry {                      // Cross reference table entries
    public String name;              // The identifier itself
    public ArrayList<Integer> refs;  // Line numbers where it appears
    public Entry next;
    public Entry(){
      this.next=null;
    }
    public Entry(String name, Entry rem) {
      this.name = name;
      this.refs = new ArrayList<Integer>();
      if (rem==null){
      this.next=null;}else {
        this.next=rem;
      }
    }
  } // Entry

  class Table {
    //ArrayList<Entry> Tableentres = new ArrayList<Entry>();//
    public static Entry Tableentres;
    public static void clearTable() {
    // Clears cross-reference table
    Tableentres=new Tableentres();
    } // clearTable

    public static void addRef(String name, boolean declared, int lineRef) {
    // What do you suppose is the purpose of the "declared" parameter?
    Entry test=getEntry(name);
    
    if (test!=null) {
      Entry test=getEntry(name);
      (test.refs).add(lineRef); //does this change in tableenteis
    }else{

    Tableentres.add(Entry(name), null); //not sure how it handes lineref
    }
    } // addRef

    public static void printTable() {
    // Prints out all references in the table (eliminate duplicates line numbers)

    } // printTable
    public static void getEntry( String aname){
      for (Entry aent: Tableentres){
        if ((aent.name).equals(aname))
        { return aent;}
      }
      return null;
    }

    public static boolean contains (String name){
      for (Entry aent: Tableentres){
        if ((aent.name).equals(aname))
        { return true;}
      }
      return false;
    }
    

  } // Table