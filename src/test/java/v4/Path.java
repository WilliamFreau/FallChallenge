package v4;

import java.util.LinkedList;
import java.util.List;

/**
 * Class utilisé pour modéliser un path vers une receipt
 */
public class Path {
    public Receipt destination;
    public List<PathNode> nodes;
    
    public Path(Receipt destination, PathNode terminal) {
        this.destination = destination;
        this.nodes = new LinkedList<>();
        
        rebuildPath(terminal);
    }
    
    private void rebuildPath(PathNode p) {
        PathNode current = p;
        while (current.parent != null) {
            this.nodes.add(0, current);
            current = current.parent;
        }
        
        if (Univers.INFO) {
            System.err.print("[ ");
            for (PathNode node : nodes) {
                System.err.print(node.step.actionId + " ");
            }
            System.err.println("]");
        }
    }
}
