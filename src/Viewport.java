public final class Viewport {
    private int row;
    private int col;
    private int numRows;
    private int numCols;

    public Viewport(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCols() {
        return this.numCols;
    }
    public Point viewportToWorld(int col, int row) {
        return new Point(col + this.col, row + this.row);
    }

    public Point worldToViewport(int col, int row) {
        return new Point(col - this.col, row - this.row);
    }

    public int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }

    public void shiftView(WorldView view, int colDelta, int rowDelta) {
        int newCol = view.getViewport().clamp(view.getViewport().col + colDelta, 0, view.getWorldModel().getNumCols() - view.getViewport().numCols);
        int newRow = view.getViewport().clamp(view.getViewport().row + rowDelta, 0, view.getWorldModel().getNumRows() - view.getViewport().numRows);

        view.getViewport().shift(newCol, newRow);
    }

    public void shift(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public boolean contains(Point p) {
        return p.y >= this.row && p.y < this.row + this.numRows && p.x >= this.col && p.x < this.col + this.numCols;
    }
}
