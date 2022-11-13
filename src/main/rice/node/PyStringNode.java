package main.rice.node;
import java.util.*;
import main.rice.obj.PyStringObj;
// TODO: implement the PyStringNode class here
public class PyStringNode extends APyNode<PyStringObj>{
    /**
     * charDomain that comprised of all possible characters that can be used to generate PyStringObjs.
     */
    private String charDomain;

    /**
     * Constructor for PyStringNode that creates a PyStringNode encapsulating characters from a given domain
     * @param domain: the inner type encapsulated in PySetNode
     */
    public PyStringNode(String domain){
        charDomain = domain;
    }
    /**
     * Generates all valid PyStringObjs of PyCharObj type within the exhaustive domain.
     * @return a set of PyStringObjs of type PyCharObj comprising the exhaustive domain
     */
    @Override
    public Set<PyStringObj> genExVals() {
        //Preprocessing
        Set<PyStringObj> result_set = new HashSet<>();
        //Sort domain of lengths to get the maximum length
        List<? extends Number> exDomainCopy = this.getExDomain();
        List<Integer> exDomainInts = new ArrayList<>();
        for(Number len: exDomainCopy){
            int temp = (int) len;
            exDomainInts.add(temp);
        }
        Collections.sort(exDomainInts);

        int lenSize = this.getExDomain().size();
        int maxVal = exDomainInts.get(lenSize - 1);
        //Recursion
        Set<String> helper = genExVals_stringHelper(maxVal);

        //Postprocessing
        for(String str: helper){
            //remove strings of length not in the given domain
            if(this.getExDomain().contains(str.length())){
                PyStringObj stringNode = new PyStringObj(str);
                result_set.add(stringNode);
            }
        }
        return result_set;
    }
    /**
     * Recursion helper method for genExVals that generate all combinations of
     * the charDomain given the current value(length) from the outer type domain
     * @param: maxValue, current length from domain
     * @return: a set of strings where each string is of length maxValue and the appropriate combination of strings.
     */
    public Set<String> genExVals_stringHelper(int maxVal){
        if (maxVal == 0){
            String empty = "";
            Set<String> emptyStringSet = new HashSet<>(Collections.singleton(""));
            return emptyStringSet;
        }
        else{
            //accumulation
            Set<String> subsets = new HashSet<>();
            //recursive accumulation
            Set<String> shortPerms = genExVals_stringHelper(maxVal -1);
            //for each permutation of length - 1
            for (String shortPerm: shortPerms){
                //for each character in charDomain
                for(int i = 0; i <this.charDomain.length(); i++){
                    //get current character in charDomain
                    String current_Char = String.valueOf(this.charDomain.charAt(i));
                    //add current character to the current string in permutation domain
                    //need to copy current string
                    String temp = shortPerm;
                    temp = temp + current_Char;
                    //add the updated permutation to subsets
                    subsets.add(temp);
                }
                subsets.add(shortPerm);
            }
            return subsets;
        }
    }
    /**
     * Helper function for genRandVal(), getting a random character from the given domain
     * @return: a character that represents a random character from charDomain
     */
    public char genRandChar(){
        int charIndex = rand.nextInt(this.charDomain.length());
        return this.charDomain.charAt(charIndex);
    }
    /**
     * Generates a single valid PyStringObj containing random characters of random length within the random domain.
     *
     * @return a single PyStringObj of random length selected from the random domain, containing characters
     * randomly generated from their charDomain respectively
     */
    @Override
    public PyStringObj genRandVal() {
        //create an empty random domain
        String randDomain = "";
        //get a random length from random domain
        int length = (int) this.ranDomainChoice();
        //select an random length
        for(int i = 0; i<length;i++){
            randDomain = randDomain + genRandChar();
        }
        return new PyStringObj(randDomain);
    }
}