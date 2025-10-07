import java.util.Random;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
//NAME:
//ASSIGNMENT:
//LAB SECTION
//LECTURE SECTION:

public class Main{

    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<SentList> sentList = new ArrayList<SentList>();
        ArrayList<SentList> posList = new ArrayList<SentList>();
        ArrayList<SentList> negList = new ArrayList<SentList>();
        ArrayList<Words> wordList = new ArrayList<Words>();

        //load sentiment, positive words and negative words arraylists
        readSentimentList(sentList, posList, negList);

        //read review
        //load ArrayList wordList that will contain original review & pos & neg
        String inFileName;
        String outFileName = "reviews.txt";
        PrintWriter outFile = new PrintWriter(outFileName);

        // open input file adding review + number + ".txt" to review
        // if not able to open, print a message and continue
        // else process the file
        // if the file can be read properly, print the results
        for (int i = 1; i <= 8; i++){
            inFileName = "review" + i + ".txt";
            if (readReview(sentList, posList, negList, wordList, inFileName)){
                printReview(wordList, inFileName, outFile);
            }
            else {
                System.out.println("Unable to open file: " + inFileName);
            }
            wordList.clear();
            Words.setPosFlag(false);
            Words.setNegFlag(false);
        }

        outFile.close();
    }

    //PRE:  accept the empty ArrayLists created in main
    //POST: the arrays are loaded with the proper words and information
    public static void readSentimentList(ArrayList<SentList> sentList,
                                         ArrayList<SentList> posList,
                                         ArrayList<SentList> negList){
        String csvFilePath = "sentiment.txt";
        String line;
        double tempValue;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            while ((line = br.readLine()) != null) {
                 // Split the line by commas into an array of strings
                String[] values = line.split(",");
                tempValue = Double.parseDouble(values[1]);
                 //Create object SentList & add to sentList arraylist
                SentList temp = new SentList(values[0], Double.parseDouble(values[1]));
                sentList.add(temp);
                 //if word values are pos add to posList
                if (tempValue > 1.25){
                    posList.add(temp);
                }
                 //if word values are neg, add to neglist
                else if (tempValue < -1.25){
                    negList.add(temp);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error reading the file.");
            e.printStackTrace();
        }
    }
    //PRE: accept the word lists and file name to open
    //POST: read the file while the line is not null
    //      each word is edited (to lower case without punctuation)
    //      the sentiment value is accessed
    //      if the word is positive - update to a random word in the negative list and update the word value
    //      if the word is negative - update to a random positive word in the positive list & update the word value

    public static boolean readReview(ArrayList<SentList> sentList,
                                  ArrayList<SentList> posList,
                                  ArrayList<SentList> negList,
                                  ArrayList<Words> wordList,
                                  String fileName){

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                 // Split the line by commas into an array of strings
                String[] values = line.split(" ");
                Random rand = new Random();
                 //for each word
                 //  strip off punctuation at the end of word set charWord to null or value of punctuation
                 //  save origWord, posWord, negWord
                 //  change to lower case  (set editWord)
                 //  look up word in sentList (set origValue, posVlue, negValue)
                 //  if positive enough, find new negword in negList & reset negWord & NegValue, reset negFlag
                 //  if negative enough, find new posword in posList & reset posWord & posValue, reset posFlag
                for (int i = 0; i < values.length; i++){
                    String origWord = values[i];
                    String eWord = origWord.toLowerCase();
                    String charWord;
                    if (isPunctuation(eWord.charAt(eWord.length() - 1))){
                        charWord = eWord.substring(eWord.length() - 1);
                        eWord = eWord.substring(0, eWord.length() - 1);
                    }
                    else {
                        charWord = null;
                    }

                    double origValue = getSentiment(sentList, eWord);
                    String posWord = origWord, negWord = origWord;
                    double posValue = origValue, negValue = origValue;
                    boolean pos = false, neg = false;
                    if (origValue > 1){
                        int r = rand.nextInt(negList.size());
                        negWord = negList.get(r).word;
                        if (Character.isUpperCase(origWord.charAt(0))){
                            negWord = negWord.substring(0, 1).toUpperCase() + negWord.substring(1);
                        }
                        if (charWord != null){
                            negWord = negWord + charWord;
                        }
                        negValue = negList.get(r).value;
                        Words.setNegFlag(true);
                        neg = true;
                    }
                    else if (origValue < -1){
                        int r = rand.nextInt(posList.size());
                        posWord = posList.get(r).word;
                        if (Character.isUpperCase(origWord.charAt(0))){
                            posWord = posWord.substring(0, 1).toUpperCase() + posWord.substring(1);
                        }
                        if (charWord != null){
                            posWord = posWord + charWord;
                        }
                        posValue = posList.get(r).value;
                        Words.setPosFlag(true);
                        pos = true;
                    }
                
                    wordList.add(new Words(charWord, origWord, posWord, negWord, origValue, posValue, negValue, pos, neg));
                    

                }

            }
            br.close();
            return true;
        } 
        catch (IOException e) {
            System.out.println("Error reading the file: " + fileName);
            return false;
        }
    }

    //PRE:  accept the updated wordlist
    //POST: loop through word list, create a string that will be the original, positive & negative reviews
    //      print each review
    public static void printReview(ArrayList<Words> wordList, String inFile, PrintWriter outFile){
        //print original review
        double totalSent = 0.0;
        String check = "";
        outFile.println("\n\n");
        outFile.println("Original Review for file: " + inFile);
        outFile.println("");
        for (int i = 0; i < wordList.size(); i++){
            String word = wordList.get(i).origWord;
            if (check.length() + word.length() > 80){
                outFile.println("");
                check = "";
            }
            check += word + " ";
            if (check.length() == 81){
                outFile.print(word);
            }
            else{
                outFile.print(word + " ");
            }
            totalSent += wordList.get(i).sentOrigValue;
        }
        outFile.println("\n");
        outFile.printf("Total Sentiment Value: %.2f\n", totalSent);
        outFile.println("\n");

        //print positive review
        check = "";
        double totalPos = 0.0;
        double totalNeg = 0.0;
        outFile.print("Positive Review for File: " + inFile);
        if (!Words.getPosFlag()){
            outFile.println(". Review cannot be made more positive.\n");
        }
        else{
            outFile.println("\n");
            ArrayList<Words> change = new ArrayList<>();
            for (int i = 0; i < wordList.size(); i++){
                String word = wordList.get(i).posWord;
                if (check.length() + word.length() > 80){
                    outFile.println("");
                    check = "";
                }
                check += word + " ";
                if (check.length() == 81){
                    outFile.print(word);
                }
                else{
                    outFile.print(word + " ");
                }
                if (wordList.get(i).toPos){
                    change.add(wordList.get(i));
                    totalPos += wordList.get(i).sentPosValue;
                    totalNeg += wordList.get(i).sentOrigValue;
                }
            }
            outFile.println("");
            for (Words w: change){
                if (w.charWord != null){
                    w.origWord = w.origWord.substring(0, w.origWord.length() - 1);
                    w.posWord = w.posWord.substring(0, w.posWord.length() - 1);
                }
                int leftPad1 = 15 - w.origWord.length();
                int leftPad2 = 15 - w.posWord.length();
                outFile.printf("%" + leftPad1 + "s %s   %.2f      ->%" + leftPad2 + "s %s   %.2f\n", " ", w.origWord, w.sentOrigValue, " ", w.posWord, w.sentPosValue);
            }
            int pad1 = 6;
            int pad2 = 19;
            if (totalNeg/10 == 0){
                pad1 = 5;
                pad2 = 18;
            }
            outFile.printf("Total:%13s%.2f%" + pad1 + "s->%" + pad2 + "s%.2f\n\n", " ", totalNeg, " ", " ", totalPos);
            outFile.printf("Positive Sentiment: %.2f", totalSent + totalPos - totalNeg);
            outFile.println("\n\n");
        }


        //print negative review
        check = "";
        totalPos = 0.0;
        totalNeg = 0.0;
        outFile.print("Negative Review for File: " + inFile);
        if (!Words.getNegFlag()){
            outFile.println(". Review cannot be made more negative.\n");
        }
        else{
            outFile.println("\n");
            ArrayList<Words> change = new ArrayList<>();
            for (int i = 0; i < wordList.size(); i++){
                String word = wordList.get(i).negWord;
                if (check.length() + word.length() > 80){
                    outFile.println("");
                    check = "";
                }
                check += word + " ";
                if (check.length() == 81){
                    outFile.print(word);
                }
                else{
                    outFile.print(word + " ");
                }
                if (wordList.get(i).toNeg){
                    change.add(wordList.get(i));
                    totalPos += wordList.get(i).sentOrigValue;
                    totalNeg += wordList.get(i).sentNegValue;
                }
            }
            outFile.println("");
            for (Words w: change){
                if (w.charWord != null){
                    w.origWord = w.origWord.substring(0, w.origWord.length() - 1);
                    w.negWord = w.negWord.substring(0, w.negWord.length() - 1);
                }
                int leftPad1 = 15 - w.origWord.length();
                int leftPad2 = 15 - w.negWord.length();
                outFile.printf("%" + leftPad1 + "s %s   %.2f      ->%" + leftPad2 + "s %s   %.2f\n", " ", w.origWord, w.sentOrigValue, " ", w.negWord, w.sentNegValue);
            }
            int pad1 = 6;
            int pad2 = 19;
            if (totalPos/10 >= 1){
                pad1 = 5;
                pad2 = 18;
            }
            outFile.printf("Total:%13s%.2f%" + pad1 + "s->%" + pad2 + "s%.2f\n\n", " ", totalPos, " ", " ", totalNeg);
            outFile.printf("Negative Sentiment Sentiment: %.2f", totalSent - totalPos + totalNeg);
            outFile.print("\n\n------------------------------------------------------------------------------");
        }
 
    }

    //PRE:  accept a character
    //POST: return true if this character is punctuation; false otherwise
    static boolean isPunctuation(char ch) {
        if (ch == '!' || ch == '\"' || ch == '#' || ch == '$' || ch == '%' || ch == '&' || ch == '\'' || ch == '(' || ch == ')' || ch == '*' || ch == '+' || ch == ',' || ch == '-' || ch == '.' || ch == '/' || ch == ':' || ch == ';' || ch == '<' || ch == '=' || ch == '>' || ch == '?' || ch == '@' || ch == '[' || ch == '\\' || ch == ']' || ch == '^' || ch == '`' || ch == '{' || ch == '|' || ch == '}')
          return true;
        return false;
    }

    //PRE:  accept the sentiment words list and a word to find
    //POST: return the value of the sentiment if found, 0 otherwise
    static double getSentiment (ArrayList<SentList> sentList, String eWord) { 
        
        int min = 0;
        int max = sentList.size() - 1;
        int mid = (min + max) / 2;
        while (min <= max){
            if (eWord.compareTo(sentList.get(mid).word) < 0){
                max = mid-1;
            }
            else if (eWord.compareTo(sentList.get(mid).word) > 0){
                min = mid+1;
            }
            else{
                break;
            }
            mid = (min + max) / 2;
        }
        if (eWord.equals(sentList.get(mid).word)){
            return sentList.get(mid).value;
        }
        else{
            return 0.0;
        }
        //for (int w = 0; w < sentList.size(); w++){
        //    if (eWord.equals(sentList.get(w).word)){
        //        return sentList.get(w).value;
        //    }
        //}
        //return 0.0;
    }
}