package v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Permit to solve all Brewable from my inventory
 */
public class Solver {
    private static final int DEPTH_LIMIT = 25;
    private static long mean_required_solve_time = 90000;
    private static long number_solve_turn = 1;
    
    private final Inventory myself;
    private final List<Receipt> learnable;
    private final List<Receipt> brewable;
    
    public Map<Receipt, Path> paths;
    
    /**
     * Create a copy of the universe and Receipt to test and simulate the resolution
     */
    public Solver() {
        this.paths = new HashMap<>();
        
        this.myself = Univers.myself.clone();
        this.learnable = Univers.cloneList(Univers.learnable);
        this.brewable = Univers.cloneList(Univers.brewable);
    }
    
    /**
     * Run the solver
     */
    public void solve() {
        //Solve for all
        for (Receipt brew : brewable) {
            if (Univers.INFO) {
                System.err.println("Receipt " + brew.actionId);
            }
            Path path = this.solve(brew, myself.clone());
            if (path != null) {
                if (Univers.INFO) {
                    System.err.println("|   Path len: " + path.nodes.size());
                }
                this.paths.put(brew, path);
            } else {
                if (Univers.INFO) {
                    System.err.println("|   No path");
                }
            }
        }
        if (Univers.INFO) {
            System.err.println("Solver end");
            System.err.println("Mean solve per turn time [ " + mean_required_solve_time / number_solve_turn + " nano ]\n");
        }
    }
    
    /**
     * Permit to solve
     *
     * @param destination
     * @param inventory
     * @return
     */
    private Path solve(Receipt destination, Inventory inventory) {
        List<PathNode> openList = new ArrayList<>(Univers.BUFFER);      //Allocate many space
        PathNode start = new PathNode();
        start.currentDelta = inventory.delta.clone();
        start.castables = Univers.cloneList(inventory.castable);
        start.learnable = Univers.cloneList(learnable);
        openList.add(start);
        
        PathNode arrival = null;
        //Tant qu'on à un élément dans la liste
        while (openList.size() > 0 && Timer.hasTime(mean_required_solve_time / number_solve_turn, 1000 * 1000)) {
            long startTime = Timer.currentElapsed();
            PathNode current = null;
            double minW = Double.MAX_VALUE;
            for (PathNode node : openList) {
                double w = node.weight(destination);
                if (minW > w) {
                    current = node;
                    minW = w;
                }
            }
            openList.remove(current);
            
            if (current.currentDelta.greaterThan(destination.delta.abs())) {
                //Arrived Need to create Brew PathNode
                arrival = new PathNode(current, destination);
                break;
            }
    
            //Create subnode for Learnable
            for (Receipt learn : current.learnable) {
                if (current.currentDelta.t1 < learn.tome_index)
                    continue;
    
                PathNode nextNode = new PathNode(current, learn);
    
                //Look if it's not a duplicated state with better depth
                keepOpenListClean(openList, nextNode);
            }
            
            //Create subnode for cast
            for (Receipt cast : current.castables) {
                PathNode nextNode;
                if (!cast.castable) {        //Look if need to rest
                    nextNode = new PathNode(current, Receipt.rest_receipt.clone());
                    if (nextNode.depth > DEPTH_LIMIT)                        //Stop at depth
                        break;
    
                    //Look if it's not a duplicated state with better depth
                    keepOpenListClean(openList, nextNode);
                } else {
                    if (cast.repeatable) {
                        int multiply = 1;
                        Delta nDelta = cast.delta.multiply(multiply);
                        while (current.currentDelta.feasable(nDelta)) {
                            nextNode = new PathNode(current, cast.clone(), multiply);
                            if (nextNode.depth > DEPTH_LIMIT)                        //Stop at depth
                                break;
                            
                            //Look if it's not a duplicated state with better depth
                            keepOpenListClean(openList, nextNode);
                            
                            multiply++;
                            nDelta = cast.delta.multiply(multiply);
                        }
                    } else {
                        //Need to check if cast is feasable
                        if (!current.currentDelta.feasable(cast.delta))
                            continue;
    
                        nextNode = new PathNode(current, cast);
                        if (nextNode.depth > DEPTH_LIMIT)                        //Stop at depth
                            break;
    
                        //Look if it's not a duplicated state with better depth
                        keepOpenListClean(openList, nextNode);
                    }
                }
            }
            
            mean_required_solve_time += (Timer.currentElapsed() - startTime);
            number_solve_turn++;
        }
        
        if (arrival != null) {
            //Arrived at the end
            //Need to rebuild the path
            return new Path(destination, arrival);
        }
        return null;
    }
    
    /**
     * Keep the openList clean
     *
     * @param openList The open node list
     * @param nextNode The next node
     */
    private void keepOpenListClean(List<PathNode> openList, PathNode nextNode) {
        List<PathNode> toRemove = new ArrayList<>();            //Node with more depth and same state
        boolean addCurrent = true;
        for (PathNode node : openList) {
            if (nextNode.currentDelta.equals(node.currentDelta))
                if (nextNode.depth < node.depth)
                    toRemove.add(node);
                else
                    addCurrent = false;
        }
        for (PathNode node : toRemove)
            openList.remove(node);
        
        
        if (addCurrent) {                 //Have found the same state with a better depth
            openList.add(0, nextNode);
            if (Univers.DEBUG) {
                System.err.println("|   " + nextNode.currentDelta.toString() + " depth: " + nextNode.depth + " id: " + nextNode.step.actionId);
            }
        }
    }
}
