package main.rice.node;
import java.util.*;
import main.rice.obj.PyListObj;
import main.rice.obj.PyTupleObj;
import main.rice.obj.APyObj;
import main.rice.obj.*;

// TODO: implement the PyTupleNode class here
public class PyTupleNode<InnerType extends APyObj> extends AIterablePyNode<PyTupleObj<InnerType>, InnerType> {
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
     * The inner node of InnerType in PyTypleNode
     */
    private APyNode<InnerType> innerNodeChild;

    /**
     * Constructor for PyTupleNode that creates a PyTupleNode encapsulating an inner node
     *
     * @param innerNode: the inner type encapsulated in PyTupleNode
     */

    public PyTupleNode(APyNode<InnerType> innerNode) {
        this.leftChild = innerNode;
    }

    /**
     * Generates all valid PyTupleObjs of type InnerType within the exhaustive domain.
     *
     * @return a set of PyTupleObjs of type InnerType comprising the exhaustive domain
     */
    @Override
    public Set<PyTupleObj<InnerType>> genExVals() {
        //pre-processing
        Set<PyTupleObj<InnerType>> result_list = new HashSet<>();
        List<Number> exDomainCopy = (List<Number>) this.getExDomain();
        List<Integer> exDomainInts = new ArrayList<>();
        for (Number len : exDomainCopy) {
            int temp = (int) len;
            exDomainInts.add(temp);
        }
        Collections.sort(exDomainInts);

        int lenSize = this.getExDomain().size();
        int maxVal = exDomainInts.get(lenSize - 1);

        //Recursion
        Set<InnerType> childGenExVals = this.getLeftChild().genExVals();
        Set<List<InnerType>> helper = genExVals_helper(maxVal, childGenExVals);

        //post processing
        for (List<InnerType> list : helper) {
            //remove sets that have length not in the given domain
            if (this.getExDomain().contains(list.size())) {
                PyTupleObj list_node = new PyTupleObj(list);
                result_list.add(list_node);
            }
        }
        return result_list;
    }

    /**
     * Generates a single valid PyTupleObj containing random InnerType of random length within the random domain.
     *
     * @return a single PyTupleObj of random length selected from the random domain, containing InnerType
     * randomly generated from their domain respectively
     */
    @Override
    public PyTupleObj<InnerType> genRandVal() {
        //Create a list of InnerType
        List<InnerType> innerTypeList = new ArrayList<>();
        //Get length randomly from random domain
        int len = (int) this.ranDomainChoice();
        //Select length randomly from domain
        for (int i = 0; i < len; i++) {
            innerTypeList.add(this.getLeftChild().genRandVal());
        }
        //Post processing
        //create PyListObj
        PyTupleObj<InnerType> randList = new PyTupleObj<>(innerTypeList);
        return randList;
    }
}