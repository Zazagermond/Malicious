// Compute medal tallies for countries in Olympics productions
// K. Bradshaw, Rhodes University, 2024

// Complete this skeleton for Task 3 Prac 6, or develop your own version

package Olympics;

import java.util.*;
import library.*;

class Entry { // Country table entries
  public String name; // The country name itself
  public int total; // Total medals

  public Entry(String name) {
    this.name = name;
    this.total = 0; // no medals yet
  }
} // Entry

class Table {

  static Map<String, Entry> countryTable = new HashMap<>();

  public static void clearTable() {
    // Clears cross-reference table
    countryTable.clear();
  } // clearTable

  public static void incMedal(String name) {
    // // Increment the medal total for country "name" -- if country doesn't exist create it.
    Entry entry = countryTable.get(name); // Check if the country already exists in the table
    if (entry == null) { // If not, create a new entry
       entry = new Entry(name);
       countryTable.put(name, entry);
     }
    entry.total++; // Increment the medal count
  } // incMedal

  public static void printTable() {
    // // Prints out all countries with their medal totals
    IO.writeLine("Country         Total medals");
    
    for (Entry entry : countryTable.values()) {
      String formation = String.format("%-15s %d%n", entry.name, entry.total);
      IO.writeLine(formation);
     }
  } // printTable

} // Table
