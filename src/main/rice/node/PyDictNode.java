package main.rice.node;
import java.security.Key;
import java.util.*;
import main.rice.obj.*;
// TODO: implement the PyDictNode class here
public class PyDictNode<KeyType extends APyObj, ValType extends APyObj> extends APyNode<PyDictObj<KeyType, ValType>>{
    /**
     * The domain for exhaustive generation.
     */
    protected List<Number> exDomain;

    /**
     * The domain for random generation.
     */
    protected List<Number> ranDomain;

    /**
     * The RNG used for random generation.
     */
    protected Random rand = new Random();

    /**
     * Returns the left child node.
     *
     * @return the left child node
     */
    /**
     * The Key node of InnerType in PyDictNode
     */
    private APyNode<KeyType> innerNodeKey;
    /**
     * The Value node of InnerType in PyDictNode
     */
    private APyNode<ValType> innerNodeVal;

    /**
     * Constructor for concrete subclass PyDictNode
     * @param key: The Key node of InnerType in PyDictNode
     * @param value: The Value node of InnerType in PyDictNode
     */
    public PyDictNode(APyNode<KeyType> key, APyNode<ValType> value){
        innerNodeKey = key;
        innerNodeVal = value;
    }
    /**
     * Returns the left child of this PyDictNode, which are its keys
     *
     * @return the keys of PyDictNode
     */
    public APyNode<KeyType> getLeftChild() {
        return this.innerNodeKey;
    }

    /**
     * Returns the right child of this PyDictNode, which are its values
     * @return the values of PyDictNode
     */
    public APyNode<ValType> getRightChild() {
        return this.innerNodeVal;
    }

    /**
     * Generates all valid PyDictObjs of type <KeyType, ValType> within the exhaustive domain.
     *
     * @return a set of PyDictObjs of type <KeyType, ValType> comprising the exhaustive domain
     */
    @Override
    public Set<PyDictObj<KeyType, ValType>> genExVals() {
        // PRE PROCESS
        Set<PyDictObj<KeyType, ValType>> retDict = new HashSet<>();

        // get maximum length
        List<? extends Number> exDomainCopy = this.getExDomain();
        List<Integer> exDomainInts = new ArrayList<>();

        for (Number len : exDomainCopy) {
            int temp = (int) len;
            exDomainInts.add(temp);
        }
        Collections.sort(exDomainInts);

        int lenSize = this.getExDomain().size();
        int maxVal = exDomainInts.get(lenSize - 1);

        // RECURSION
        Set<KeyType> keyRecurse = this.getLeftChild().genExVals();
        Set<ValType> valueRecurse = this.getRightChild().genExVals();

        Set<Map<KeyType, ValType>> helper = gen_KeyVal_pairs(maxVal, keyRecurse, valueRecurse);


        // POST-PROCESSING
        for (Map<KeyType, ValType> dict : helper) {
            if (this.getExDomain().contains(dict.size())) {
                PyDictObj dictObj = new PyDictObj(dict);
                retDict.add(dictObj);
            }
        }
        return retDict;

    }

    /**
     * a helper method for genExVals that creates all possible key-value pairs of a dictionary of maxValue length
     * @param maxValue: maximum number of key-value pairs of the dictionary,
     * @param keyRec: a set of keys in the PyDictNode
     * @param valRec: a set of values in the PyDictNode
     * @return all possible key-value pairs of a dictionary of maxValue length
     */
    public Set<Map<KeyType, ValType>> gen_KeyVal_pairs(int maxValue, Set<KeyType> keyRec, Set<ValType> valRec){
        //if length of exhaustive domain is 0
        if (maxValue == 0){
            return new HashSet<>(Set.of(new HashMap<>()));
        }
        else{
            //accumulation
            Set<Map<KeyType, ValType>> subsets = new HashSet<>(Set.of(new HashMap<>()));
            //perform combinations recursively
            Set<Map<KeyType, ValType>> pairs = gen_KeyVal_pairs(maxValue - 1, keyRec, valRec);
            for (Map<KeyType, ValType> pair: pairs){
                for (KeyType key: keyRec){
                    for (ValType value: valRec){
                        Map<KeyType, ValType> temp = new HashMap<>(pair);
                        if (! temp.containsKey(key)){
                            temp.put(key, value);
                            subsets.add(temp);

                        }
                    }
                }

            }
            return subsets;
        }
    }
    /**
     * Generates a single valid PyDictObj containing KeyType, ValType of random length within the random domain.
     *
     * @return a single PyDictObj of random length selected from the random domain, containing KeyType and ValType
     * randomly generated from their domain respectively
     */
    @Override
    public PyDictObj<KeyType, ValType> genRandVal() {
        Map<KeyType, ValType> randMap = new HashMap<>();

        int len = (int) this.ranDomainChoice();

        while (randMap.size() != len) {
            randMap.put(this.getLeftChild().genRandVal(), this.getRightChild().genRandVal());
        }

        // post processing
        // create PyListObj
        PyDictObj randDict = new PyDictObj(randMap);

        return randDict;
    }
}
