import java.sql.Array;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.Math.*;

public class AStarPathingStrategy implements PathingStrategy {

    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {

        List<Point> path = new LinkedList<>();
        ArrayList<Point> close = new ArrayList<>();
        ArrayList<Point> visited = new ArrayList<>();
        ArrayList<Cell> open = new ArrayList<>();

        Cell currPos = new Cell(start, 0, 1000, null);
        open.add(currPos);
        close.add(currPos.getPoint());
        List<Point> cardinalDirections;
        ArrayList<Point>  tempClose = new ArrayList<Point>();
        boolean flag;
        boolean noPathFlag = false;
        Cell finalCell = currPos;

        while (currPos.getHCost() != 1 && open.size() > 0) {  //Check if any points in close are within 1 from dest
            cardinalDirections = potentialNeighbors.apply(currPos.getPoint())
                    .filter(canPassThrough)
                    .filter(pt ->
                            !pt.equals(start)
                                    && !pt.equals(end))
                    .collect(Collectors.toList());

            open.remove(currPos);


            for (int i = 0; i < cardinalDirections.size(); i++) {  // should be giving multiple points only giving 1
                Point p = cardinalDirections.get(i);
                int x = p.x;
                int y = p.y;

                if(inVisited(p, visited)) {
                    continue;
                }
                if(inOpen(p, open)) {
                    continue;
                }


                if (currPos.getPoint().x == p.x && currPos.getPoint().y-1 == p.y) {
                    Cell up = new Cell(p, p.calcGCost(end), p.calcHCost(end), currPos);
                    open.add(up);
                }
                else if (currPos.getPoint().x == p.x && currPos.getPoint().y+1 == p.y) {
                    Cell down = new Cell(p, p.calcGCost(end), p.calcHCost(end), currPos);
                    open.add(down);
                }
                else if (currPos.getPoint().x-1 == p.x && currPos.getPoint().y == p.y) {
                    Cell left = new Cell(p, p.calcGCost(end), p.calcHCost(end), currPos);
                    open.add(left);
                }
                else if (currPos.getPoint().x + 1 == p.x && currPos.getPoint().y == p.y) {
                    Cell right = new Cell(p, p.calcGCost(end), p.calcHCost(end), currPos);
                    open.add(right);
                }
            }
            flag = false;
            for (Cell cell : open) {
                if (cell.getFCost() <= currPos.getFCost() && !inVisited(cell.getPoint(), visited)) {
                    currPos = cell;
                    flag = true;
                }
            }

            //backtracking loop
            if (!flag) {
                int temp = 1000;
                for(Cell cell : open) {
                    if(cell.getFCost() <= temp && !inVisited(cell.getPoint(), visited)) {
                        currPos = cell;
                    }
                }
            }
            if(!close.contains(currPos.getPoint())) {
                close = tempClose;
                close.add(currPos.getPoint());
                visited.add(currPos.getPoint());
            }

        }

        int index = visited.size()-1;
        if(index>=0) {
            Point finalPoint = visited.get(index);
            for (Cell cell : open) {
                if (finalPoint == cell.getPoint()) {
                    finalCell = cell;
                }
            }
        }
        while(finalCell.getParent() != null) {
            path.add(finalCell.getPoint());
            finalCell = finalCell.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    public static boolean inVisited(Point p, ArrayList<Point> visited) {
        for (Point other : visited) {
            if(p.equals(other)) {
                return true;
            }
        }
        return false;
    }

    public static boolean inOpen(Point p, ArrayList<Cell> open) {
        for (Cell other : open) {
            if(p.equals(other.getPoint())) {
                return true;
            }
        }
        return false;
    }

}
