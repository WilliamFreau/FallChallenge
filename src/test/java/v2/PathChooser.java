package v2;

/**
 * Class static permettant de choisir le meuilleur chemin parmis tout ceux possible
 */
public class PathChooser {
    /**
     * @param solver
     * @return
     */
    public static Path getPath(Solver solver) {
        Path path = null;
        
        int minBrewRemain = Math.min(Univers.NUMBER_BREW_TO_STOP - Univers.MY_BREW_NUMBER,
                Univers.NUMBER_BREW_TO_STOP - Univers.OPPONENT_BREW_NUMBER);
        int minScoreToFirst = ((Univers.opponent.score + Univers.opponent.delta.getEndScoreBonus()) - (Univers.myself.score + Univers.myself.delta.getEndScoreBonus()));
        if (minScoreToFirst < 0)
            minScoreToFirst = 0;
        double requireScorePerBrewToWin = (double) minScoreToFirst / (double) minBrewRemain;
        if (Univers.INFO)
            System.err.println("Require score per Brew: " + requireScorePerBrewToWin);
        
        if ((Univers.MY_BREW_NUMBER == (Univers.NUMBER_BREW_TO_STOP - 1) || Univers.OPPONENT_BREW_NUMBER == (Univers.NUMBER_BREW_TO_STOP - 1))
                && Univers.myself.score > Univers.opponent.score) {
            //Need to finish quickly!
            if (Univers.INFO) {
                System.err.println("Need to finish quickly");
            }
            //Take min Path length
            double min = Double.MAX_VALUE;
            for (Path p : solver.paths.values()) {
                if (p.nodes.size() < min) {
                    path = p;
                    min = p.nodes.size();
                }
            }
        } else if (Univers.currentDestination == null || Univers.destNoMoreBrewable() || solver.paths.get(Univers.currentDestination) == null) {
            double avgLength = 0;
            double avgScore = 0;
            int nb = 0;
            for (Path pavg : solver.paths.values()) {
                if (pavg != null) {
                    avgLength += pavg.nodes.size();
                    avgScore += pavg.destination.price;
                    nb++;
                }
            }
            avgLength = avgLength / nb;                       //Contains the average path length
            avgScore = avgScore / nb;                       //Contains the average path length
            double min = Double.MAX_VALUE;
            for (Path p : solver.paths.values()) {
                if ((minBrewRemain <= 3 && p.destination.price > requireScorePerBrewToWin) || (minBrewRemain > 3 && p.destination.price >= avgScore))  //filter to stay first
                    if ((p.nodes.size() - avgLength) < min) {
                        path = p;
                        min = (p.nodes.size() - avgLength);
                    }
            }
            if (path == null) {
                min = Double.MAX_VALUE;
                for (Path p : solver.paths.values()) {
                    if (p.nodes.size() < min) {
                        path = p;
                        min = p.nodes.size();
                    }
                }
            }
            if (path != null) {
                Univers.currentDestination = path.destination;
                if (Univers.INFO) {
                    System.err.println("New destination: " + path.destination.actionId);
                }
            }
        } else {
            if (Univers.MY_BREW_NUMBER == (Univers.NUMBER_BREW_TO_STOP - 1) || Univers.OPPONENT_BREW_NUMBER == (Univers.NUMBER_BREW_TO_STOP - 1)) {
                //Me or opponent is at one to the end
                //Try to stole the victory
                if (!(Univers.currentDestination.price + (Univers.myself.score + Univers.myself.delta.getEndScoreBonus())
                        > (Univers.opponent.score + Univers.opponent.delta.getEndScoreBonus()))
                ) {
                    //The current destination is not enought to won!
                    //Change destination to the receipt who permit to me to won and closet
                    int missingScore = Univers.opponent.score - Univers.myself.score;
                    
                    Path choosen = null;
                    int dist = Integer.MAX_VALUE;
                    for (Path p : solver.paths.values()) {
                        if (p.destination.price >= missingScore && p.nodes.size() < dist) {
                            dist = p.nodes.size();
                            choosen = p;
                        }
                    }
                    
                    if (choosen != null) {
                        if (Univers.INFO) {
                            System.err.println("New dest because of score! " + choosen.destination.actionId);
                        }
                        Univers.currentDestination = choosen.destination;
                    } else {    //Missing point and no Receipt to won
                        //Try to increase number of non T1 in my inventory!
                        
                        if (Univers.INFO) {
                            System.err.println("Missing point and no Receipt to won ");
                        }
                    }
                }
            }
            path = solver.paths.get(Univers.currentDestination);
            
        }
        
        return path;
    }
}
