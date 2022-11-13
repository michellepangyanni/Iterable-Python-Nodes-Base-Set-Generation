package main.rice.node;
import java.util.*;
import main.rice.obj.APyObj;
import main.rice.obj.PyListObj;
// TODO: implement the PyListNode class here
public class PyListNode<InnerType extends APyObj> extends AIterablePyNode<PyListObj<InnerType>, InnerType>{
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
     * Constructor for PyListNode, which creates a PyListNode contains an inner node
     * @param innerNode: an APynode of InnerType in PyListNode
     */
    public PyListNode(APyNode<InnerType> innerNode){
        this.leftChild = innerNode;
    }
    @Override
    /**
     * Generates all valid PyListObjs of type InnerType within the exhaustive domain.
     *
     * @return a set of PyListObjs of type InnerType comprising the exhaustive domain
     */
    public Set<PyListObj<InnerType>> genExVals() {
        //preprocessing
        Set<PyListObj<InnerType>> result_list = new HashSet<>();
        List<? extends Number> exDomainCopy = this.getExDomain();
        List<Integer> exDomainInts = new ArrayList<>();
        for (Number len: exDomainCopy){
            int temp= (int) len;
            exDomainInts.add(temp);
        }
        Collections.sort(exDomainInts);

        int lenSize = this.getExDomain().size();
        int maxVal = exDomainInts.get(lenSize - 1);

        //Recursion
        Set<InnerType> childGenExVals = this.getLeftChild().genExVals();
        Set<List<InnerType>> helper = genExVals_helper( maxVal, childGenExVals);

        //post processing
        for(List<InnerType> list: helper){
            //remove sets that have length not in the given domain
            if(this.getExDomain().contains(list.size())){
                PyListObj list_node = new PyListObj(list);
                result_list.add(list_node);
            }
        }

        return result_list;
    }
    /**
     * Generates a single valid PyListObj containing random InnerType of random length within the random domain.
     *
     * @return a single PyListObj of random length selected from the random domain, containing InnerType
     * randomly generated from their domain respectively
     */
    @Override
    public PyListObj<InnerType> genRandVal() {
        //Create a list of InnerType
        List<InnerType> innerTypeList = new ArrayList<>();
        //Get length randomly from random domain
        int len = (int) this.ranDomainChoice();
        //Select length randomly from domain
        for(int i = 0; i < len; i++){
            innerTypeList.add(this.getLeftChild().genRandVal());
        }
        //Post processing
        //create PyListObj
        PyListObj<InnerType> randList = new PyListObj<>(innerTypeList);
        return randList;
    }
}