import library.*;
//Dean, Alisha, Zaza - The malicious Hadada

public class IOprime {
    public static void main(String[] args) {
        // check that arguments have been given
        if (args.length != 3) {
            IO.writeLine("Argument is missing! Usage prog.java [in-file], [out-file] [primes.txt]");
            System.exit(1);
        }

        // attempt to open data file for int input
        InFile data = new InFile(args[0]);

        if (data.openError()) {
            IO.writeLine("Cant open " + args[0]);
            System.exit(1);
        }
        
        // attempt to open results file to write prime numbers to
        OutFile results = new OutFile(args[1]);

        if (results.openError()) {
            IO.writeLine("Cant open " + args[1]);
            System.exit(1);
        }

        // Read the prime numbers from primesFile
        String primesFileName = args[2];
        //Read given file of prime numbers from the library
        InFile primesFile = new InFile(primesFileName);
        if (primesFile.openError()) {
            IO.writeLine("Cant open " + primesFileName);
            System.exit(1);
        }
        
        int maxNumber=0;
        try {
            // Read the maximum number from the input file
            maxNumber = data.readInt();
        } catch (NumberFormatException e) {
            IO.writeLine("Must be an integer.");
            System.exit(1);
        }
        //make a unique set of the prime numbers from the prime numbers file supplied.
        IntSet primeSet = new IntSet();
        int prime = primesFile.readInt();
        while (!primesFile.noMoreData()) {
            primeSet.incl(prime);
            prime = primesFile.readInt();
        }

        results.writeLine("Prime numbers up to " + maxNumber + ":");
        //iterate over the numbers between 2 and maxNumber, if the number is in the prime file, then add to our output.
        for (int i = 2; i < maxNumber; i++) {
            if (primeSet.contains(i)) {
                results.write(i + " ");
            }
        }
        
        results.writeLine("");
        results.close();

        // Print a confirmation message 
        System.out.println("Prime numbers have been written to primes.txt");
    }}