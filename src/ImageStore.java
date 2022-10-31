import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PImage;

public final class ImageStore {
    private Map<String, List<PImage>> images;
    private List<PImage> defaultImages;

    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;
    public ImageStore(PImage defaultImage) {
        this.images = new HashMap<>();
        defaultImages = new LinkedList<>();
        defaultImages.add(defaultImage);
    }

    /*
          Called with color for which alpha should be set and alpha value.
          setAlpha(img, color(255, 255, 255), 0));
        */
    public void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & Functions.COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & Functions.COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }

    public List<PImage> getImageList(String key) {
        return this.images.getOrDefault(key, this.defaultImages);
    }

    public List<PImage> getImages(String key) {
        return this.images.computeIfAbsent(key, k -> new LinkedList<>());
    }

    public void processImageLine(String line, PApplet screen) {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = this.getImages(key);
                imgs.add(img);

                if (attrs.length >= Functions.KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
                    int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
                    int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }




}
