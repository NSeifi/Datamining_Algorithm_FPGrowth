import java.io.File;
import java.io.FileNotFoundException;
import java.io.*;
import java.io.IOException;
import java.util.*;

public class FPgrowth {

    public static Integer minSupport;
    public static String address;
    public static Double minis;
    public static LinkedHashMap<String, Integer> itemList1;
    public static Map<List<String>, Integer> frqntPattern = new LinkedHashMap<List<String>, Integer>();

    public static void main(String [] args) throws IOException {
        Scanner sc =new Scanner(System.in);
        address=sc.nextLine();
        minis=sc.nextDouble();
        /*address=args[0];
        minis=Double.parseDouble(args[1]);*/
        firstScan();
        FPGrowth(null,null,0);
        writeFile();
    }//main

    private static void writeFile() throws IOException {
        //Change this address
        PrintWriter pw= new PrintWriter(new FileWriter("/home/nasim/MiningResult.txt"));
        System.out.println("|FPs| : "+frqntPattern.size());
        pw.println("|FPs| : "+frqntPattern.size());
        for(Map.Entry<List<String>, Integer> entry : frqntPattern.entrySet()){
            String data=   entry.getKey()+" : "+entry.getValue();
            pw.println(data);
        }
        pw.close();
    }

    private static  void firstScan()
            throws FileNotFoundException {
        itemList1=new  LinkedHashMap<String, Integer>();
        File file = new File(address);
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            String[] arr = str.split("\\s+");
            if (itemList1.isEmpty()) {
                itemList1.put(arr[0],0);
                minSupport= Math.toIntExact(Math.round(Integer.parseInt(arr[0]) * minis / 100));
                //System.out.println(minSupport+"min sup "+minis);
            }//if
            else{
                for (int j = 2; j < arr.length; j++)
                {
                    if (itemList1.containsKey(arr[j]))
                        itemList1.replace(arr[j],itemList1.get(arr[j]),itemList1.get(arr[j])+1);
                    else
                        itemList1.put(arr[j],1);
                }
            }//else
        }//while

        itemList1=minSup(itemList1);
        System.out.println(itemList1+" ItemLists after minsup and sort");
    }//readFile

    private static LinkedHashMap<String, Integer> minSup(LinkedHashMap<String, Integer> itemSet)
    {
        LinkedHashMap<String, Integer> itemR=new  LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> pair: itemSet.entrySet()) {
            if (pair.getValue()>=minSupport)
                itemR.put(pair.getKey(),pair.getValue());

        }

        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(itemR.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                if (o1.getValue() < o2.getValue()) return 1;
                else if (o1.getValue() > o2.getValue()) return -1;
                else if (o1.getValue() == o2.getValue())
                    if (Integer.parseInt(o1.getKey())> Integer.parseInt(o2.getKey()))
                        return 1;
                    else return -1;
                else return 0;
            }
        });
        itemR.clear();
        for (Map.Entry<String, Integer> aa : list) {
            itemR.put(aa.getKey(), aa.getValue());
        }

        return itemR;
    }//minSup

    private static  FPNode secondScan(Map<String, FPNode> headerTable)
            throws FileNotFoundException {
           File file = new File(address);
           Scanner sc = new Scanner(file);
           FPNode root = new FPNode("ROOT");
           root.parent = null;
           while (sc.hasNextLine())
           {
               FPNode prev = root;
               HashMap<String, FPNode> children = prev.children;
               String str = sc.nextLine();
               String[] arr = str.split("\\s+");
               List<String> transaction = new ArrayList<String>();

               for (int j = 2; j < arr.length; j++)
                   if (itemList1.containsKey(arr[j]))
                       transaction.add(arr[j]);

                   Collections.sort(transaction, new Comparator<String>() {
                       public int compare(String item1, String item2) {
                        if (itemList1.get(item1) > itemList1.get(item2)) return -1;
                        else if (itemList1.get(item1) < itemList1.get(item2)) return 1;
                        return 0;
                    }
                });

                for (String itemName : transaction) {
                    FPNode t;
                    if (children.containsKey(itemName)) {
                    children.get(itemName).support++;
                    t = children.get(itemName);
                }
                    else
                    {
                        t = new FPNode(itemName);
                        t.parent = prev;
                        children.put(itemName, t);

                    //add to header
                        FPNode header = headerTable.get(itemName);
                        if (header != null)
                            header.attach(t);
                }
                    prev = t;
                    children = t.children;
                }
        }//while
        return root;
    }//readFile

    private static FPNode buildTree(List<List<String>> transactions, final Map<String, FPNode> headerTable)
    {
        FPNode root = new FPNode("ROOT");
        root.parent = null;

        for(List<String> transaction : transactions){
            FPNode prev = root;
            HashMap<String, FPNode> children = prev.children;

            for(String itemName:transaction){
                if(!headerTable.containsKey(itemName))continue;

                FPNode t;
                if(children.containsKey(itemName)){
                    children.get(itemName).support++;
                    t = children.get(itemName);
                }
                else{
                    t = new FPNode(itemName);
                    t.parent = prev;
                    children.put(itemName, t);

                    FPNode header = headerTable.get(itemName);
                    if(header!=null){
                        header.attach(t);
                    }
                }
                prev = t;
                children = t.children;
            }
        }
        return root;
    }//second tree

    public static void FPGrowth(List<List<String>> transactions, List<String> postModel, int i) throws FileNotFoundException {
        i++;

        Map<String, FPNode> headerTable= new HashMap<>();;
        Map<String, Integer> itemCount;
        FPNode root;

        if (i==1)
        {
            for (Map.Entry<String, Integer> entry : itemList1.entrySet()) {
                String itemName = entry.getKey();
                Integer count = entry.getValue();
                FPNode node = new FPNode(itemName);
                node.support = count;
                headerTable.put(itemName, node);
            }

            root = secondScan(headerTable);

        }
        else {
            itemCount= countFreq(transactions);
            for (Map.Entry<String, Integer> entry : itemCount.entrySet()) {
                String itemName = entry.getKey();
                Integer count = entry.getValue();
                if (count >= minSupport) {
                    FPNode node = new FPNode(itemName);
                    node.support = count;
                    headerTable.put(itemName, node);
                }
            }

            root = buildTree(transactions,headerTable);
        }

        if(root==null) return;
        if(root.children==null || root.children.size()==0) return;

        if(singleBranch(root)){
            ArrayList<FPNode> path = new ArrayList<>();
            FPNode curr = root;
            while(curr.children!=null && curr.children.size()>0){
                String childName = curr.children.keySet().iterator().next();
                curr = curr.children.get(childName);
                path.add(curr);
            }

            List<List<FPNode>> combinations = new ArrayList<>();
            combinations(path, combinations);

            for(List<FPNode> combine : combinations){
                int supp = 0;
                List<String> rule = new ArrayList<>();
                for(FPNode node : combine){
                    rule.add(node.itemName);
                    supp = node.support;
                }
                if(postModel!=null){
                    rule.addAll(postModel);
                }

                frqntPattern.put(rule, supp);
            }

            return;
        }

        for(FPNode header : headerTable.values()){

            List<String> rule = new ArrayList<>();
            rule.add(header.itemName);

            if (postModel != null) {
                rule.addAll(postModel);
            }

            frqntPattern.put(rule, header.support);

            List<String> newPostPattern = new ArrayList<>();
            newPostPattern.add(header.itemName);
            if (postModel != null) {
                newPostPattern.addAll(postModel);
            }

            List<List<String>> projectedTree = new LinkedList<List<String>>();
            FPNode nextNode = header;
            while((nextNode = nextNode.next)!=null){
                int leaf_supp = nextNode.support;

                LinkedList<String> path = new LinkedList<>();
                FPNode parent = nextNode;
                while(!(parent = parent.parent).itemName.equals("ROOT")){
                    path.push(parent.itemName);
                }
                if(path.size()==0)continue;

                while(leaf_supp-- >0){
                    projectedTree.add(path);
                }
            }
            FPGrowth(projectedTree, newPostPattern,i);
        }
    }

    private static void combinations(ArrayList<FPNode> path, List<List<FPNode>> combinations){
        if(path==null || path.size()==0)return;
        int length = path.size();
        for(int i = 1;i<Math.pow(2, length);i++){
            String bitmap = Integer.toBinaryString(i);
            List<FPNode> combine = new ArrayList<>();
            for(int j = 0;j<bitmap.length();j++){
                if(bitmap.charAt(j)=='1'){
                    combine.add(path.get(length-bitmap.length()+j));
                }
            }
            combinations.add(combine);
        }
    }

    private static boolean singleBranch(FPNode root) {
        boolean flag = true;
        while (root.children != null && root.children.size()>0) {
            if (root.children.size() > 1) {
                flag = false;
                break;
            }
            String childName = root.children.keySet().iterator().next();
            root = root.children.get(childName);
        }
        return flag;
    }

    private static HashMap<String, Integer> countFreq(List<List<String>> transactions){
        HashMap<String, Integer> itemCount = new HashMap<String, Integer>();
        for(List<String> transac: transactions){
            for(String item: transac){
                if(itemCount.containsKey(item)){
                    int count = itemCount.get(item);
                    itemCount.put(item, ++count);
                }
                else{
                    itemCount.put(item, 1);
                }
            }
        }

        return itemCount;
    }

}//class

class FPNode {

    int support;
    String itemName;
    HashMap<String, FPNode> children;
    FPNode next;
    FPNode parent;

    public FPNode(String name) {
        this.itemName = name;
        this.support = 1;
        this.children = new HashMap<String, FPNode>();
        this.next = null;
        this.parent = null;
    }

    public void attach(FPNode t) {
        FPNode node = this;
        while (node.next != null) {
            node = node.next;
        }
        node.next = t;
    }
}

