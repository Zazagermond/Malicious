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
    static ArrayList<Entry> Tableentres = new ArrayList<Entry>();
    public static void clearTable() {
    // Clears cross-reference table
    Tableentres = new ArrayList<Entry>();
    } // clearTable

    public static void addRef(String name, boolean declared, int lineRef) {
    // What do you suppose is the purpose of the "declared" parameter?
    Entry test=getEntry(name);
    if (declared) {
       
      (getEntry(name).refs).add(lineRef); //does this change in tableenteis
    }else{
    //not sure how it handes lineref
    Entry check= new Entry(name);
    (check.refs).add(lineRef);
     Tableentres.add(check);
    }
    } // addRef

    public static void printTable() {
    // Prints out all references in the table (eliminate duplicates line numbers)
    ArrayList<Integer> reflist = new ArrayList<Integer>();
    StringBuilder output = new StringBuilder();
    int aref;
    for (Entry entry1:Tableentres){
        output.append(entry1.name+ " ");
         reflist = new ArrayList<Integer>();
        for (int refs1:entry1.refs){
         if (refs1>=0){ 
          if ((reflist).contains(refs1)==false){
              reflist.add(refs1);
              output.append(refs1+" ");
          }
         } 
        }
        
        for (int refs2:entry1.refs){
         if (refs2<0){output.append(refs2) ;
         }
        }
        output.append("\n") ;
    }
    System.out.print(output.toString());
    } // printTable
    public static Entry getEntry( String aname){
      for (Entry aent: Tableentres){
        if ((aent.name).equals(aname))
        { return aent;}
      }
      return null;
    }

    public static boolean ccontains (String aname){
      for (Entry aent: Tableentres){
        if ((aent.name).equals(aname))
        { return true;}
      }
      return false;
    }
    

  } // Table
