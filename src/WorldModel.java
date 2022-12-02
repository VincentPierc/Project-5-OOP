import processing.core.PImage;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRows;
    private int numCols;
    private Background[][] background;
    private Entity[][] occupancy;
    private Set<Entity> entities;



    public WorldModel() {

    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCols() {
        return this.numCols;
    }

    public Set<Entity> getEntities() {
        return this.entities;
    }

    public Optional<Entity> findNearest(Point pos, Class kind) {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : this.entities) {
            if (kind.isInstance(entity)) {
                ofType.add(entity);
            }
        }
        return nearestEntity(ofType, pos);
    }

    public int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = distanceSquared(nearest.getEntityPosition(), pos);

            for (Entity other : entities) {
                int otherDistance = distanceSquared(other.getEntityPosition(), pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.getEntityPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = this.getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            this.setOccupancyCell(pos, entity);
            entity.setEntityPosition(pos);
        }
    }

    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt(entity.getEntityPosition());
    }

    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setEntityPosition(new Point(-1,-1));
            this.entities.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground) {
        this.parseSaveFile(saveFile, imageStore, defaultBackground);
        if (this.background == null) {
            this.background = new Background[this.numRows][this.numCols];
            for (Background[] row : this.background) {
                Arrays.fill(row, defaultBackground);
            }
        }
        if (this.occupancy == null) {
            this.occupancy = new Entity[this.numRows][this.numCols];
            this.entities = new HashSet<>();
        }
    }

    public void setBackgroundSnow(Point pos, ImageStore imageStore) {
        for(int x = -1; x<2; x++) {
            for (int y = -1; y < 2; y++) {
                Point temp = new Point (pos.x+x, pos.y+y);
                if(temp.x <0 || temp.y < 0) { continue; }
                setBackgroundCell(temp, createSnowBackground(imageStore));
                Optional<Entity> dude = this.getOccupant(temp);
                if(dude.isPresent()) {
                    Entity entity = dude.get();
//                    if(entity instanceof DudeNotFull ) {
//                        imageStore.processImageLine();
//                        entity.images = imageStore.getImageList("deer").get(0);
//                    }
                }
            }
        }
    }

    public void parseSaveFile(Scanner saveFile, ImageStore imageStore, Background defaultBackground) {
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while (saveFile.hasNextLine()) {
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if (line.endsWith(":")) {
                headerLine = lineCounter;
                lastHeader = line;
                switch (line) {
                    case "Backgrounds:" -> this.background = new Background[this.numRows][this.numCols];
                    case "Entities:" -> {
                        this.occupancy = new Entity[this.numRows][this.numCols];
                        this.entities = new HashSet<>();
                    }
                }
            } else {
                switch (lastHeader) {
                    case "Rows:" -> this.numRows = Integer.parseInt(line);
                    case "Cols:" -> this.numCols = Integer.parseInt(line);
                    case "Backgrounds:" ->
                            this.parseBackgroundRow(line, lineCounter - headerLine - 1, imageStore);
                    case "Entities:" -> this.parseEntity(line, imageStore);
                }
            }
        }
    }

    public Background getBackgroundCell(Point pos) {
        return this.background[pos.y][pos.x];
    }

    public void setBackgroundCell(Point pos, Background background) {
        this.background[pos.y][pos.x] = background;
    }


    public static Background createSnowBackground(ImageStore imageStore) {
        return new Background("snow", imageStore.getImageList("snow"));
    }
    public Optional<PImage> getBackgroundImage(Point pos) {
        if (this.withinBounds(pos)) {
            return Optional.of(this.getBackgroundCell(pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    public void parseSapling(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[Functions.SAPLING_HEALTH]);
            Sapling sapling = new Sapling(id, pt, imageStore.getImageList(Functions.SAPLING_KEY), Functions.SAPLING_ACTION_ANIMATION_PERIOD, Functions.SAPLING_ACTION_ANIMATION_PERIOD, health, Functions.SAPLING_HEALTH_LIMIT);
            //Entity entity = Entity.createSapling(id, pt, imageStore.getImageList( Functions.SAPLING_KEY), health);
            this.tryAddEntity(sapling);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.SAPLING_KEY, Functions.SAPLING_NUM_PROPERTIES));
        }
    }

    public void parseDude(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.DUDE_NUM_PROPERTIES) {
            DudeNotFull dude = new DudeNotFull(id, pt, imageStore.getImageList(Functions.DUDE_KEY),
                    Double.parseDouble(properties[Functions.DUDE_ANIMATION_PERIOD]), Double.parseDouble(properties[Functions.DUDE_ACTION_PERIOD]),
                    Integer.parseInt(properties[Functions.DUDE_LIMIT]), 0);
            //Entity entity = Entity.createDudeNotFull(id, pt, Double.parseDouble(properties[Functions.DUDE_ACTION_PERIOD]), Double.parseDouble(properties[Functions.DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[Functions.DUDE_LIMIT]), imageStore.getImageList(Functions.DUDE_KEY));
            this.tryAddEntity(dude);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.DUDE_KEY, Functions.DUDE_NUM_PROPERTIES));
        }
    }

    public void parseFairy(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.FAIRY_NUM_PROPERTIES) {
            Fairy fairy = new Fairy(id, pt, imageStore.getImageList(Functions.FAIRY_KEY),Double.parseDouble(properties[Functions.FAIRY_ANIMATION_PERIOD]),
                    Double.parseDouble(properties[Functions.FAIRY_ACTION_PERIOD]),0, 5);
            //Entity entity = Entity.createFairy(id, pt, Double.parseDouble(properties[Functions.FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[Functions.FAIRY_ANIMATION_PERIOD]), imageStore.getImageList(Functions.FAIRY_KEY));
            this.tryAddEntity(fairy);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.FAIRY_KEY, Functions.FAIRY_NUM_PROPERTIES));
        }
    }

    public void parseTree(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.TREE_NUM_PROPERTIES) {
            Tree tree = new Tree(id, pt, imageStore.getImageList(Functions.TREE_KEY), Double.parseDouble(properties[Functions.TREE_ANIMATION_PERIOD]),
                    Double.parseDouble(properties[Functions.TREE_ACTION_PERIOD]), Functions.TREE_HEALTH, Functions.TREE_HEALTH_MAX);

            this.tryAddEntity(tree);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.TREE_KEY, Functions.TREE_NUM_PROPERTIES));
        }
    }

    public void parseObstacle(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.OBSTACLE_NUM_PROPERTIES) {
            Obstacle obstacle = new Obstacle(id, pt, imageStore.getImageList(Functions.OBSTACLE_KEY), Double.parseDouble(properties[Functions.OBSTACLE_ANIMATION_PERIOD]));
            //Entity entity = Entity.createObstacle(id, pt, Double.parseDouble(properties[Functions.OBSTACLE_ANIMATION_PERIOD]), imageStore.getImageList(Functions.OBSTACLE_KEY));
            this.tryAddEntity(obstacle);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.OBSTACLE_KEY, Functions.OBSTACLE_NUM_PROPERTIES));
        }
    }

    public void parseHouse(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.HOUSE_NUM_PROPERTIES) {
            House house = new House(id, pt, imageStore.getImageList(Functions.HOUSE_KEY));
            //Entity entity = Entity.createHouse(id, pt, imageStore.getImageList(Functions.HOUSE_KEY));
            this.tryAddEntity(house);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.HOUSE_KEY, Functions.HOUSE_NUM_PROPERTIES));
        }
    }

    public void parseStump(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == Functions.STUMP_NUM_PROPERTIES) {
            Stump stump = new Stump(id, pt, imageStore.getImageList(Functions.STUMP_KEY));
            //Entity entity = Entity.createStump(id, pt, imageStore.getImageList(Functions.STUMP_KEY));
            this.tryAddEntity(stump);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", Functions.STUMP_KEY, Functions.STUMP_NUM_PROPERTIES));
        }
    }

    public void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.getEntityPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }

    public boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0 && pos.x < this.numCols;
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    /*
           Assumes that there is no entity currently occupying the
           intended destination cell.
        */
    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getEntityPosition())) {
            this.setOccupancyCell(entity.getEntityPosition(), entity);
            this.entities.add(entity);
        }
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.y][pos.x];
    }

    public void setOccupancyCell(Point pos, Entity entity) {
        this.occupancy[pos.y][pos.x] = entity;
    }

    public void parseBackgroundRow(String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if (row < this.numRows) {
            int rows = Math.min(cells.length, this.numCols);
            for (int col = 0; col < rows; col++) {
                this.background[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
            }
        }
    }

    public void parseEntity(String line, ImageStore imageStore) {
        String[] properties = line.split(" ", Functions.ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= Functions.ENTITY_NUM_PROPERTIES) {
            String key = properties[Functions.PROPERTY_KEY];
            String id = properties[Functions.PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[Functions.PROPERTY_COL]), Integer.parseInt(properties[Functions.PROPERTY_ROW]));

            properties = properties.length == Functions.ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[Functions.ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case Functions.OBSTACLE_KEY -> this.parseObstacle(properties, pt, id, imageStore);
                case Functions.DUDE_KEY -> this.parseDude(properties, pt, id, imageStore);
                case Functions.FAIRY_KEY -> this.parseFairy(properties, pt, id, imageStore);
                case Functions.HOUSE_KEY -> this.parseHouse(properties, pt, id, imageStore);
                case Functions.TREE_KEY -> this.parseTree(properties, pt, id, imageStore);
                case Functions.SAPLING_KEY -> this.parseSapling(properties, pt, id, imageStore);
                case Functions.STUMP_KEY -> this.parseStump(properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        } else {
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }


    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }
}
