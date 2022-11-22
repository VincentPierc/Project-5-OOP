public class Cell {
    private final Point p;
    private int gCost;
    private int hCost;
    private Cell parent;
    public Cell(Point p, int gCost, int hCost, Cell parent) {
        this.p = p;
        this.gCost = gCost;
        this.hCost = hCost;
        this.parent = parent;
    }

    public Point getPoint() {
        return this.p;
    }
    public int getGCost() {
        return this.gCost;
    }
    public int getHCost() {
        return this.hCost;
    }
    public int getFCost(){
        return this.hCost + this.gCost;
    }

    public Cell getParent() { return this.parent; }
}
