import java.util.ArrayList;
import java.util.List;

public class FP_Node {
    String itemID = "-1";
    int sup = 1;
    FP_Node parent = null;
    FP_Node nodeLink = null;
    List<FP_Node> childs = new ArrayList<FP_Node>();

    FP_Node getChild(String item) {
        for(FP_Node child : childs){
            if(child.itemID.equals(item))
                return child;
        }
        return null;
    }
}
