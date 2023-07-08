import java.util.ArrayList;
import java.util.List;

public class FP_Node2 {
    int itemID = -1;  // item id
    int counter = 1;  // frequency counter  (a.k.a. support)

    // the parent node of that node or null if it is the root
    FP_Node2 parent = null;
    // the child nodes of that node
    List<FP_Node2> childs = new ArrayList<FP_Node2>();

    FP_Node2 nodeLink = null; // link to next node with the same item id (for the header table).


    /**
     * Return the immediate child of this node having a given ID.
     * If there is no such child, return null;
     */
    FP_Node2 getChildWithID(int id) {
        // for each child node
        for(FP_Node2 child : childs){
            // if the id is the one that we are looking for
            if(child.itemID == id){
                // return that node
                return child;
            }
        }
        // if not found, return null
        return null;
    }

    public String toString(String indent) {
        StringBuilder output = new StringBuilder();
        output.append(""+ itemID);
        output.append(" (count="+ counter);
        output.append(")\n");
        String newIndent = indent + "   ";
        for (FP_Node2 child : childs) {
            output.append(newIndent+ child.toString(newIndent));
        }
        return output.toString();
    }

    public String toString() {
        return ""+itemID;
    }
}