import java.util.*;

import processing.core.PImage;

public abstract class Entity {
    private String id;
    private Point position;
    private List<PImage> images;

    private int imageIndex;


    public Point getEntityPosition() {
        return this.position;
    }

    public String getEntityID() {
        return this.id;
    }

    public void setEntityPosition(Point pos) {
        this.position = pos;
    }

    public Entity(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex % this.images.size());
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }
}
