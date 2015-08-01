/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.Features;
import com.b3dgs.lionengine.game.collision.TileGroup;
import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Abstract representation of a standard tile based map. This class uses a List of List to store tiles, a TreeMap to
 * store sheets references (SpriteTiled), and collisions.
 * <p>
 * The way to prepare a map is the following:
 * </p>
 * 
 * <pre>
 * {@link #create(int, int)} // prepare memory to store tiles
 * {@link #loadSheets(Media)} // load tile sheets
 * {@link #loadGroups(Media)} // load tile groups
 * </pre>
 * 
 * <p>
 * Or import a map from a level rip with {@link #create(Media, Media, Media)}.
 * </p>
 * <p>
 * A simple call to {@link #load(FileReading)} will automatically perform theses operations.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Tile
 */
public class MapTileGame implements MapTile
{
    /** Info loading sheets. */
    private static final String INFO_LOAD_SHEETS = "Loading sheets from: ";
    /** Info loading groups. */
    private static final String INFO_LOAD_GROUPS = "Loading groups from: ";
    /** Error sheet missing message. */
    private static final String ERROR_SHEET_MISSING = "Sheet missing: ";
    /** Error group missing message. */
    private static final String ERROR_GROUP_MISSING = "Group missing: ";
    /** Construction error. */
    private static final String ERROR_CONSTRUCTOR_MISSING = "No recognized constructor found for: ";

    /**
     * Find group for tile.
     * 
     * @param tile The tile reference.
     * @param groups The groups list.
     * @return The tile group, <code>null</code> if none.
     */
    private static String getGroup(Tile tile, Collection<TileGroup> groups)
    {
        for (final TileGroup group : groups)
        {
            if (group.contains(tile))
            {
                return group.getName();
            }
        }
        return null;
    }

    /** Sheets list. */
    private final Map<Integer, SpriteTiled> sheets = new HashMap<Integer, SpriteTiled>();
    /** Features list. */
    private final Features<MapTileFeature> features = new Features<MapTileFeature>(MapTileFeature.class);
    /** Tile groups list. */
    private final Map<String, TileGroup> groups = new HashMap<String, TileGroup>();
    /** Viewer reference. */
    private final Viewer viewer;
    /** Services reference. */
    private final Services services;
    /** Sheet configuration file. */
    private Media sheetsConfig;
    /** Groups configuration file. */
    private Media groupsConfig;
    /** Tile width. */
    private int tileWidth;
    /** Tile height. */
    private int tileHeight;
    /** Number of horizontal tiles. */
    private int widthInTile;
    /** Number of vertical tiles. */
    private int heightInTile;
    /** Map radius. */
    private int radius;
    /** Tiles map. */
    private List<List<Tile>> tiles;
    /** Tile renderer. */
    private MapTileRenderer renderer;

    /**
     * Create a map tile. Rendering is not enable and must not be used ({@link #render(Graphic)}). Use
     * {@link #MapTileGame(Services)} instead if rendering will be used.
     */
    public MapTileGame()
    {
        viewer = null;
        services = null;
    }

    /**
     * Create a map tile.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @throws LionEngineException If service not found.
     */
    public MapTileGame(Services services) throws LionEngineException
    {
        this.services = services;
        viewer = services.get(Viewer.class);
        sheetsConfig = null;
    }

    /**
     * Render map from starting position, showing a specified area, including a specific offset.
     * 
     * @param g The graphic output.
     * @param screenHeight The view height (rendering start from bottom).
     * @param sx The starting x (view real location x).
     * @param sy The starting y (view real location y).
     * @param inTileWidth The number of rendered tiles in width.
     * @param inTileHeight The number of rendered tiles in height.
     * @param offsetX The horizontal map offset.
     * @param offsetY The vertical map offset.
     */
    protected void render(Graphic g,
                          int screenHeight,
                          int sx,
                          int sy,
                          int inTileWidth,
                          int inTileHeight,
                          int offsetX,
                          int offsetY)
    {
        // Each vertical tiles
        for (int v = 0; v <= inTileHeight; v++)
        {
            final int ty = v + (sy - offsetY) / tileHeight;
            if (!(ty < 0 || ty >= heightInTile))
            {
                // Each horizontal tiles
                for (int h = 0; h <= inTileWidth; h++)
                {
                    final int tx = h + (sx - offsetX) / tileWidth;
                    if (!(tx < 0 || tx >= widthInTile))
                    {
                        renderTile(g, tx, ty, sx, sy, screenHeight);
                    }
                }
            }
        }
    }

    /**
     * Save tile. Data are saved this way:
     * 
     * <pre>
     * (integer) sheet number
     * (integer) index number inside sheet
     * (integer) tile location x % AbstractMapTile.BLOC_SIZE
     * (integer tile location y
     * </pre>
     * 
     * @param file The file writer reference.
     * @param tile The tile to save.
     * @throws IOException If error on writing.
     */
    protected void saveTile(FileWriting file, Tile tile) throws IOException
    {
        file.writeInteger(tile.getSheet().intValue());
        file.writeInteger(tile.getNumber());
        file.writeInteger(tile.getX() / tileWidth % MapTile.BLOC_SIZE);
        file.writeInteger(tile.getY() / tileHeight);
    }

    /**
     * Load tile. Data are loaded this way:
     * 
     * <pre>
     * (integer) sheet number
     * (integer) index number inside sheet
     * (integer) tile location x
     * (integer tile location y
     * </pre>
     * 
     * @param file The file reader reference.
     * @param i The last loaded tile number.
     * @return The loaded tile.
     * @throws IOException If error on reading.
     */
    protected Tile loadTile(FileReading file, int i) throws IOException
    {
        Check.notNull(file);

        final int sheet = file.readInteger();
        final int number = file.readInteger();
        final int x = file.readInteger() * tileWidth + i * MapTile.BLOC_SIZE * getTileWidth();
        final int y = file.readInteger() * tileHeight;
        final Tile tile = createTile();

        tile.setSheet(Integer.valueOf(sheet));
        tile.setNumber(number);
        tile.setX(x);
        tile.setY(y);

        return tile;
    }

    /**
     * Get the tile at location and render it.
     * 
     * @param g The graphic output.
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     * @param sx The starting horizontal location.
     * @param sy The starting vertical location.
     * @param screenHeight The view height (rendering start from bottom).
     */
    private void renderTile(Graphic g, int tx, int ty, int sx, int sy, int screenHeight)
    {
        final Tile tile = getTile(tx, ty);
        if (tile != null)
        {
            final int x = tile.getX() - sx;
            final int y = -tile.getY() - tile.getHeight() + sy + screenHeight;
            renderer.renderTile(g, tile, x, y);
        }
    }

    /**
     * Count the active tiles.
     * 
     * @param widthInTile The horizontal tiles.
     * @param step The step number.
     * @param s The s value.
     * @return The active tiles.
     */
    private int countTiles(int widthInTile, int step, int s)
    {
        int count = 0;
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < heightInTile; ty++)
            {
                if (getTile(tx + s * step, ty) != null)
                {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Save the active tiles.
     * 
     * @param file The output file.
     * @param widthInTile The horizontal tiles.
     * @param step The step number.
     * @param s The s value.
     * @throws IOException If error on saving.
     */
    private void saveTiles(FileWriting file, int widthInTile, int step, int s) throws IOException
    {
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < heightInTile; ty++)
            {
                final Tile tile = getTile(tx + s * step, ty);
                if (tile != null)
                {
                    saveTile(file, tile);
                }
            }
        }
    }

    /*
     * MapTile
     */

    @Override
    public void create(int widthInTile, int heightInTile) throws LionEngineException
    {
        Check.superiorStrict(widthInTile, 0);
        Check.superiorStrict(heightInTile, 0);

        this.widthInTile = widthInTile;
        this.heightInTile = heightInTile;
        radius = (int) Math.ceil(StrictMath.sqrt(widthInTile * widthInTile + heightInTile * heightInTile));
        tiles = new ArrayList<List<Tile>>(heightInTile);

        for (int v = 0; v < heightInTile; v++)
        {
            tiles.add(v, new ArrayList<Tile>(widthInTile));
            for (int h = 0; h < widthInTile; h++)
            {
                tiles.get(v).add(h, null);
            }
        }
        if (renderer == null)
        {
            renderer = this;
        }
    }

    @Override
    public Tile createTile()
    {
        return new TileGame(tileWidth, tileHeight);
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        final Media sheetsConfig = Medias.create(file.readString());
        final Media groupsConfig = Medias.create(file.readString());
        final int width = file.readShort();
        final int height = file.readShort();

        create(width, height);
        loadSheets(sheetsConfig);
        loadGroups(groupsConfig);

        final int t = file.readShort();
        for (int v = 0; v < t; v++)
        {
            final int n = file.readShort();
            for (int h = 0; h < n; h++)
            {
                final Tile tile = loadTile(file, v);
                if (tile.getSheet().intValue() > getSheetsNumber())
                {
                    throw new IOException(ERROR_SHEET_MISSING + Constant.DOUBLE_DOT + tile.getSheet().toString());
                }
                final int tx = tile.getX() / getTileWidth();
                final int ty = tile.getY() / getTileHeight();
                final List<Tile> list = tiles.get(ty);
                list.set(tx, tile);
            }
        }
    }

    @Override
    public void save(FileWriting file) throws IOException
    {
        // Header
        file.writeString(sheetsConfig.getPath());
        file.writeString(groupsConfig.getPath());
        file.writeShort((short) widthInTile);
        file.writeShort((short) heightInTile);

        final int step = MapTile.BLOC_SIZE;
        final int x = Math.min(step, widthInTile);
        final int t = (int) Math.ceil(widthInTile / (double) step);

        file.writeShort((short) t);
        for (int s = 0; s < t; s++)
        {
            final int count = countTiles(x, step, s);
            file.writeShort((short) count);
            saveTiles(file, t, step, s);
        }
    }

    @Override
    public void create(Media levelrip) throws LionEngineException
    {
        create(levelrip,
               Medias.create(levelrip.getParentPath(), DEFAULT_SHEETS_FILE),
               Medias.create(levelrip.getParentPath(), DEFAULT_GROUPS_FILE));
    }

    @Override
    public void create(Media levelrip, Media sheetsConfig, Media groupsConfig) throws LionEngineException
    {
        clear();
        final LevelRipConverter rip = new LevelRipConverter(levelrip, sheetsConfig, this);
        rip.start();

        final int errors = rip.getErrors();
        if (errors > 0)
        {
            Verbose.warning(getClass(), "create", "Number of missing tiles: " + errors);
        }
        this.sheetsConfig = sheetsConfig;
        this.groupsConfig = groupsConfig;
        loadGroups(groupsConfig);
    }

    @Override
    public <F extends MapTileFeature> F createFeature(Class<F> feature) throws LionEngineException
    {
        try
        {
            final F instance = UtilReflection.create(feature, new Class<?>[]
            {
                Services.class
            }, services);
            services.add(instance);
            addFeature(instance);
            return instance;
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR_MISSING + feature);
        }
    }

    @Override
    public void loadSheets(Media sheetsConfig) throws LionEngineException
    {
        Verbose.info(INFO_LOAD_SHEETS, sheetsConfig.getFile().getPath());
        this.sheetsConfig = sheetsConfig;
        final XmlNode root = Stream.loadXml(sheetsConfig);

        // Retrieve tile size
        final XmlNode tileSize = root.getChild(MapTile.NODE_TILE_SIZE);
        tileWidth = tileSize.readInteger(MapTile.ATTRIBUTE_TILE_WIDTH);
        tileHeight = tileSize.readInteger(MapTile.ATTRIBUTE_TILE_HEIGHT);

        // Retrieve sheets list
        final Collection<XmlNode> children = root.getChildren(MapTile.NODE_TILE_SHEET);
        final String[] files = new String[children.size()];
        int sheetNumber = 0;
        for (final XmlNode child : children)
        {
            files[sheetNumber] = child.getText();
            sheetNumber++;
        }

        // Load sheets from list
        final String path = sheetsConfig.getPath();
        final String folder = path.substring(0, path.length() - sheetsConfig.getFile().getName().length());
        sheets.clear();
        for (int sheet = 0; sheet < files.length; sheet++)
        {
            final Media media = Medias.create(folder, files[sheet]);
            final SpriteTiled sprite = Drawable.loadSpriteTiled(media, tileWidth, tileHeight);
            sprite.load();
            sprite.prepare();
            sheets.put(Integer.valueOf(sheet), sprite);
        }
    }

    @Override
    public void loadGroups(Media groupsConfig) throws LionEngineException
    {
        Verbose.info(INFO_LOAD_GROUPS, groupsConfig.getFile().getPath());
        this.groupsConfig = groupsConfig;

        groups.clear();
        final XmlNode nodeGroups = Stream.loadXml(groupsConfig);
        final Collection<TileGroup> groups = ConfigTileGroup.create(nodeGroups);
        for (final TileGroup group : groups)
        {
            this.groups.put(group.getName(), group);
        }

        for (int ty = 0; ty < heightInTile; ty++)
        {
            for (int tx = 0; tx < widthInTile; tx++)
            {
                final Tile tile = getTile(tx, ty);
                if (tile != null)
                {
                    tile.setGroup(getGroup(tile, groups));
                }
            }
        }
        groups.clear();
    }

    @Override
    public void append(MapTile map, int offsetX, int offsetY)
    {
        Check.notNull(map);

        final int newWidth = widthInTile - (widthInTile - offsetX) + map.getInTileWidth();
        final int newHeight = heightInTile - (heightInTile - offsetY) + map.getInTileHeight();

        // Adjust height
        final int sizeV = tiles.size();
        for (int v = 0; v < newHeight - sizeV; v++)
        {
            tiles.add(new ArrayList<Tile>(newWidth));
        }

        for (int cty = 0; cty < map.getInTileHeight(); cty++)
        {
            final int ty = offsetY + cty;

            // Adjust width
            final int sizeH = tiles.get(ty).size();
            for (int h = 0; h < newWidth - sizeH; h++)
            {
                tiles.get(ty).add(null);
            }

            for (int ctx = 0; ctx < map.getInTileWidth(); ctx++)
            {
                final int tx = offsetX + ctx;
                final Tile tile = map.getTile(ctx, cty);
                if (tile != null)
                {
                    setTile(tx, ty, tile);
                }
            }
        }

        widthInTile = newWidth;
        heightInTile = newHeight;
        radius = (int) Math.ceil(StrictMath.sqrt(widthInTile * widthInTile + heightInTile * heightInTile));
    }

    @Override
    public void clear()
    {
        if (tiles != null)
        {
            for (int ty = 0; ty < tiles.size(); ty++)
            {
                final List<Tile> list = tiles.get(ty);
                if (list != null)
                {
                    list.clear();
                }
            }
            tiles.clear();
        }
    }

    @Override
    public void addFeature(MapTileFeature feature)
    {
        features.add(feature);
    }

    @Override
    public void render(Graphic g)
    {
        render(g,
               viewer.getHeight(),
               (int) Math.ceil(viewer.getX()),
               (int) Math.ceil(viewer.getY()),
               (int) Math.ceil(viewer.getWidth() / (double) tileWidth),
               (int) Math.ceil(viewer.getHeight() / (double) tileHeight),
               -viewer.getViewX(),
               viewer.getViewY());
    }

    @Override
    public void renderTile(Graphic g, Tile tile, int x, int y)
    {
        final SpriteTiled sprite = getSheet(tile.getSheet());
        sprite.setLocation(x, y);
        sprite.setTile(tile.getNumber());
        sprite.render(g);
    }

    @Override
    public void setTileRenderer(MapTileRenderer renderer) throws LionEngineException
    {
        Check.notNull(renderer);
        this.renderer = renderer;
    }

    @Override
    public void setTile(int tx, int ty, Tile tile) throws LionEngineException
    {
        Check.inferiorStrict(tx, getInTileWidth());
        Check.inferiorStrict(ty, getInTileHeight());

        tile.setX(tx * tileWidth);
        tile.setY(ty * tileHeight);
        tiles.get(ty).set(tx, tile);
    }

    @Override
    public Tile getTile(int tx, int ty)
    {
        try
        {
            return tiles.get(ty).get(tx);
        }
        catch (final IndexOutOfBoundsException exception)
        {
            return null;
        }
    }

    @Override
    public Tile getTile(Localizable localizable, int offsetX, int offsetY)
    {
        return getTileAt(localizable.getX() + offsetX, localizable.getY() + offsetY);
    }

    @Override
    public Tile getTileAt(double x, double y)
    {
        final int tx = (int) Math.floor(x / getTileWidth());
        final int ty = (int) Math.floor(y / getTileHeight());
        return getTile(tx, ty);
    }

    @Override
    public Collection<Tile> getTilesHit(double ox, double oy, double x, double y)
    {
        // Distance calculation
        final double dh = x - ox;
        final double dv = y - oy;

        // Search vector and number of search steps
        final double norm = Math.sqrt(dh * dh + dv * dv);
        final double sx = dh / norm;
        final double sy = dv / norm;

        double h = ox;
        double v = oy;

        final Collection<Tile> found = new ArrayList<Tile>();
        for (int count = 0; count < norm; count++)
        {
            v += sy;
            Tile tile = getTileAt(UtilMath.getRound(sx, h), UtilMath.getRound(sy, v));
            if (tile != null && !found.contains(tile))
            {
                found.add(tile);
            }

            h += sx;
            tile = getTileAt(UtilMath.getRound(sx, h), UtilMath.getRound(sy, v));
            if (tile != null && !found.contains(tile))
            {
                found.add(tile);
            }
        }
        return found;
    }

    @Override
    public int getInTileX(Localizable localizable)
    {
        return (int) Math.floor(localizable.getX() / getTileWidth());
    }

    @Override
    public int getInTileY(Localizable localizable)
    {
        return (int) Math.floor(localizable.getY() / getTileHeight());
    }

    @Override
    public Media getSheetsConfig()
    {
        return sheetsConfig;
    }

    @Override
    public Media getGroupsConfig()
    {
        return groupsConfig;
    }

    @Override
    public Collection<Integer> getSheets()
    {
        return sheets.keySet();
    }

    @Override
    public SpriteTiled getSheet(Integer sheet) throws LionEngineException
    {
        if (!sheets.containsKey(sheet))
        {
            throw new LionEngineException(ERROR_SHEET_MISSING, sheet.toString());
        }
        return sheets.get(sheet);
    }

    @Override
    public TileGroup getGroup(String name)
    {
        if (groups.containsKey(name))
        {
            return groups.get(name);
        }
        throw new LionEngineException(ERROR_GROUP_MISSING, name);
    }

    @Override
    public Collection<TileGroup> getGroups()
    {
        return groups.values();
    }

    @Override
    public <C extends MapTileFeature> C getFeature(Class<C> feature) throws LionEngineException
    {
        return features.get(feature);
    }

    @Override
    public <C extends MapTileFeature> boolean hasFeature(Class<C> feature)
    {
        return features.contains(feature);
    }

    @Override
    public int getSheetsNumber()
    {
        return sheets.size();
    }

    @Override
    public int getTilesNumber()
    {
        int tilesNumber = 0;
        for (int ty = 0; ty < heightInTile; ty++)
        {
            for (int tx = 0; tx < widthInTile; tx++)
            {
                final Tile tile = getTile(tx, ty);
                if (tile != null)
                {
                    tilesNumber++;
                }
            }
        }
        return tilesNumber;
    }

    @Override
    public int getTileWidth()
    {
        return tileWidth;
    }

    @Override
    public int getTileHeight()
    {
        return tileHeight;
    }

    @Override
    public int getInTileWidth()
    {
        return widthInTile;
    }

    @Override
    public int getInTileHeight()
    {
        return heightInTile;
    }

    @Override
    public int getInTileRadius()
    {
        return radius;
    }

    @Override
    public Iterable<? extends MapTileFeature> getFeatures()
    {
        return features.getAll();
    }

    @Override
    public boolean isCreated()
    {
        return tiles != null;
    }
}
