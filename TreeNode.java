import java.util.LinkedList;

class TreeNode {
    String data;
    LinkedList<TreeNode> children;

    TreeNode(String d) {
        data = d;
        children = new LinkedList<>();
    }
}
