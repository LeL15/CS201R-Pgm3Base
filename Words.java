//This class includes both the original, postive & negative review words
//This does not need to be done this way - it is just a suggestion
public class Words {
    String origWord;
    //String editWord;
    String charWord;
    String posWord;
    String negWord;
    double sentOrigValue;
    double sentPosValue;
    double sentNegValue;
    boolean toPos;
    boolean toNeg;
    static boolean posFlag = false;
    static boolean negFlag = false;

    public Words(String c, String o, String p, String n, double s, double sp, double sn, boolean tp, boolean tn){
        origWord = o;
        //editWord = e; 
        posWord = p;
        negWord = n;
        sentOrigValue = s;
        sentPosValue = sp;
        sentNegValue = sn;
        charWord = c;
        toPos = tp;
        toNeg = tn;
    }
    public static void setPosFlag(boolean s){
        posFlag = s;
    }
    public static void setNegFlag(boolean s){
        negFlag = s;
    }

    public static boolean getPosFlag(){
        return posFlag;
    }
    public static boolean getNegFlag(){
        return negFlag;
    }
}
