package v2;

public class Heuristique {
    
    public static double compute(PathNode pathNode, Receipt destination) {
        switch (Univers.heuristique_formula) {
            case 2:
                return formula2(pathNode, destination);
            case 3:
                return formula3(pathNode, destination);
            case 4:
                return formula4(pathNode, destination);
            case 5:
                return formula5(pathNode, destination);
            default:
                return defaultFormulas(pathNode, destination);
        }
    }
    
    /**
     * -10 on Brew
     *
     * @param node
     * @param destination
     * @return
     */
    public static double formula5(PathNode node, Receipt destination) {
        Delta d = node.currentDelta.getMissings(destination.delta);
        return d.abs().sum() * node.depth + node.castables.size() * 1.5d - (node.step != null && Univers.BREW.equals(node.step.action) ? 30 : 0);
    }
    
    /**
     * default cast size importance and no -30 on Brew
     *
     * @param node
     * @param destination
     * @return
     */
    public static double formula4(PathNode node, Receipt destination) {
        Delta d = node.currentDelta.getMissings(destination.delta);
        return d.abs().sum() * node.depth + node.castables.size() * 1.5d - (node.step != null && Univers.BREW.equals(node.step.action) ? 30 : 0);
    }
    
    /**
     * Increase castSize importance
     *
     * @param node
     * @param destination
     * @return
     */
    public static double formula3(PathNode node, Receipt destination) {
        Delta d = node.currentDelta.getMissings(destination.delta);
        return d.abs().sum() * node.depth + node.castables.size() * 1.5d - (node.step != null && Univers.BREW.equals(node.step.action) ? 30 : 0);
    }
    
    /**
     * Reduce castSize importance
     *
     * @param node
     * @param destination
     * @return
     */
    public static double formula2(PathNode node, Receipt destination) {
        Delta d = node.currentDelta.getMissings(destination.delta);
        return d.abs().sum() * node.depth + node.castables.size() * 1.5d - (node.step != null && Univers.BREW.equals(node.step.action) ? 30 : 0);
    }
    
    /**
     * Formula arrived at rank 41 in Bronze
     *
     * @param node
     * @param destination
     * @return
     */
    public static double defaultFormulas(PathNode node, Receipt destination) {
        Delta d = node.currentDelta.getMissings(destination.delta);
        return d.abs().sum() * node.depth + node.castables.size() * 1.5d - (node.step != null && Univers.BREW.equals(node.step.action) ? 30 : 0);
    }
}
