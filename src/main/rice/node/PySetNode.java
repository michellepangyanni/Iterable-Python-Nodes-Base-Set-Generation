package main.rice.node;
import java.util.*;
import main.rice.obj.APyObj;
import main.rice.obj.PySetObj;

// TODO: implement the PySetNode class here
public class PySetNode<InnerType extends APyObj> extends AIterablePyNode<PySetObj<InnerType>, InnerType>{
    /**
     * The domain for exhaustive generation.
     */
    protected List<? extends Number> exDomain;

    /**
     * The domain for random generation.
     */
    protected List<? extends Number> ranDomain;

    /**
     * The RNG used for random generation.
     */
    protected Random rand = new Random();

    /**
     * Constructor of PySetNode that creates a PySetNode encapsulating an inner node
     * @param innerNode: the inner node encapsulated in PySetNode
     */
    public PySetNode(APyNode<InnerType> innerNode){
        this.leftChild = innerNode;
    }
    @Override
    /**
     * Generates all valid PySetObjs of type InnerType within the exhaustive domain.
     *
     * @return a set of PySetObjs of type InnerType comprising the exhaustive domain
     */
    public Set<PySetObj<InnerType>> genExVals() {
        //Preprocessing
        Set<PySetObj<InnerType>> result_set = new HashSet<>();
        //Sort domain of lengths to get the maximum length
        List<Number> exDomainCopy = (List<Number>) this.getExDomain();
        List<Integer> exDomainInts = new ArrayList<>();
        for(Number len: exDomainCopy){
            int temp = (int) len;
            exDomainInts.add(temp);
        }
        Collections.sort(exDomainInts);

        int lenSize = this.getExDomain().size();
        int maxVal = exDomainInts.get(lenSize - 1);
        //Recursion
        Set<InnerType> childGenExVals = this.getLeftChild().genExVals();
        Set<Set<InnerType>> helper = genExVals_sethelper(maxVal, childGenExVals);
        //Postprocessing
        for(Set<InnerType> set: helper){
            //remove sets of length not in the given domain
            if(this.getExDomain().contains(set.size())){
                PySetObj setNode = new PySetObj(set);
                result_set.add(setNode);
            }
        }
        return result_set;
    }

    /**
     * Recursion helper method for genExVals that generate all combinations of
     * the inner type domain given the current value(length) from the outer type domain
     * @param: maxValue, current length from domain
     * @return: a set of sets where each set is of length maxValue and the appropriate combination of elements of
     * type InnerType.
     */
    public Set<Set<InnerType>> genExVals_sethelper(int maxValue, Set<InnerType> childGenExVals){
        //when length is 0, return a empty set of lists
        if (maxValue == 0){
            return new HashSet<>(Set.of(new HashSet<>()));
        }
        else{
            //Adding
            Set<Set<InnerType>> subsets = new HashSet<>(Set.of(new HashSet<>()));
            //permutation
            Set<Set<InnerType>> shortPerms = genExVals_sethelper(maxValue -1, childGenExVals);
            //for each permutation of length - 1
            for (Set<InnerType> shortPerm: shortPerms){
                //for each value to be added
                for(InnerType val: childGenExVals){
                    //copy the previous permutation of lower length
                    Set<InnerType> temp = new HashSet<>(shortPerm);
                    //add that value val to the permutation
                    temp.add(val);
                    //add the updated permutation to subsets
                    subsets.add(temp);
                }
            }
            return subsets;
        }
    }
    /**
     * Generates a single valid PySetObj containing random InnerType of random length within the random domain.
     *
     * @return a single PySetObj of random length selected from the random domain, containing InnerType
     * randomly generated from their domain respectively
     */
    @Override
    public PySetObj<InnerType> genRandVal() {
        //create a list of InnerType
        Set<InnerType> innerList = new HashSet<>();
        //get length randomly from random domain
        int len = (int) this.ranDomainChoice();

        while(innerList.size() != len){
            //sets do not allow duplicates
            if (! innerList.contains(this.getLeftChild().genRandVal())){
                innerList.add(this.getLeftChild().genRandVal());
            }
        }
        //post processing
        //create PySetObj
        PySetObj<InnerType> randList = new PySetObj<>(innerList);
        return randList;
    }
}