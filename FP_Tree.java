import java.util.List;

public class FP_Tree {

    List<Integer> headerList = null;
    FP_Node root = new FP_Node();


    public void addTransaction(List<String> transaction) {
    FP_Node cur_node =root;
    for(String items :transaction) {
        if (cur_node.getChild(items) == null) {
            FP_Node newNode = new FP_Node();
            newNode.itemID = items;
            newNode.parent = cur_node;
            cur_node.childs.add(newNode);
            cur_node = newNode;
            FP_Node2 lastNode = mapItemLastNode.get(item);
            if(lastNode != null)
                lastNode.nodeLink = newNode;
            mapItemLastNode.put(item, newNode);
            FP_Node2 headernode = mapItemNodes.get(item);
            if(headernode == null)
                mapItemNodes.put(item, newNode);
        }
        else
        {
            cur_node.sup ++;
            cur_node=cur_node.getChild(items);
        }
        }//for
    }// addtransaction



}//class
