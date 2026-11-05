package ch.epfl.chacun.gui;

import javafx.scene.image.Image;


/**
 * Utility class for loading images for tiles and markers.
 *
 * @author Tino Bianconi (379733)
 */
public final class ImageLoader {

    public static final int LARGE_TILE_PIXEL_SIZE = 512;
    public static final int LARGE_TILE_FIT_SIZE = 256;
    public static final int NORMAL_TILE_PIXEL_SIZE	= 256;
    public static final int NORMAL_TILE_FIT_SIZE =	128;
    public static final int MARKER_PIXEL_SIZE = 96;
    public static final int MARKER_FIT_SIZE = 48;

    /**
     * Private constructor to prevent instantiation
     */
    private ImageLoader() {}

    /**
     * Loads the normal image for a tile with the specified ID.
     *
     * @param id The ID of the tile.
     * @return The Image object for the normal tile.
     */
    public static Image normalImageForTile(int id) {
        return new Image(STR. "/\{NORMAL_TILE_PIXEL_SIZE}/\{ String.format("%02d", id) }.jpg" );
    }

    /**
     * Loads the large image for a tile with the specified ID.
     *
     * @param id The ID of the tile.
     * @return The Image object for the large tile.
     */
    public static Image largeImageForTile(int id){
        return new Image(STR. "/\{LARGE_TILE_PIXEL_SIZE}/\{ String.format("%02d", id) }.jpg" );
    }

}
