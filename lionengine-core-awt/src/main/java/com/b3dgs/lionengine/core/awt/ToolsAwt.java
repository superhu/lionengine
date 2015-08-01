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
package com.b3dgs.lionengine.core.awt;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferInt;
import java.awt.image.Kernel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Misc tools for AWT.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ToolsAwt
{
    /** Graphics environment. */
    private static final GraphicsEnvironment ENV = GraphicsEnvironment.getLocalGraphicsEnvironment();
    /** Graphics device. */
    private static final GraphicsDevice DEV = ENV.getDefaultScreenDevice();
    /** Graphics configuration. */
    private static final GraphicsConfiguration CONFIG = DEV.getDefaultConfiguration();
    /** Fraction. */
    private static final float DIV = 1 / 9f;
    /** Bilinear filter. */
    private static final float[] BILINEAR_FILTER = new float[]
    {
        DIV, DIV, DIV, DIV, DIV, DIV, DIV, DIV, DIV
    };

    /**
     * Create an image.
     * 
     * @param width The image width.
     * @param height The image height.
     * @param transparency The image transparency.
     * @return The image instance.
     * @throws LionEngineException If negative size.
     */
    static BufferedImage createImage(int width, int height, int transparency) throws LionEngineException
    {
        Check.superiorOrEqual(width, 0);
        Check.superiorOrEqual(height, 0);
        return CONFIG.createCompatibleImage(width, height, transparency);
    }

    /**
     * Get an image from an input stream.
     * 
     * @param input The image input stream.
     * @return The loaded image.
     * @throws IOException If error when reading image.
     */
    static BufferedImage getImage(InputStream input) throws IOException
    {
        final BufferedImage buffer = ImageIO.read(input);
        if (buffer == null)
        {
            throw new IOException("Invalid image !");
        }
        final BufferedImage image = copyImage(buffer, buffer.getTransparency());
        return image;
    }

    /**
     * Save image to output stream.
     * 
     * @param image The image to save.
     * @param output The output stream.
     * @throws IOException If error when saving image.
     */
    static void saveImage(BufferedImage image, OutputStream output) throws IOException
    {
        ImageIO.write(image, "png", output);
    }

    /**
     * Create an image.
     * 
     * @param image The image reference.
     * @param transparency The transparency;
     * @return The image copy.
     */
    static BufferedImage copyImage(BufferedImage image, int transparency)
    {
        final BufferedImage copy = createImage(image.getWidth(), image.getHeight(), transparency);
        final Graphics2D g = copy.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return copy;
    }

    /**
     * Get the image pixels data.
     * 
     * @param image The image reference.
     * @return The pixels array.
     */
    static int[] getImageData(BufferedImage image)
    {
        return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    /**
     * Apply a bilinear filter to the image.
     * 
     * @param image The image reference.
     * @return The filtered image.
     */
    static BufferedImage applyBilinearFilter(BufferedImage image)
    {
        final Kernel kernel = new Kernel(3, 3, BILINEAR_FILTER);
        final ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }

    /**
     * Apply a mask to an existing image.
     * 
     * @param image The existing image.
     * @param rgba The rgba color value.
     * @return The masked image.
     */
    static BufferedImage applyMask(BufferedImage image, int rgba)
    {
        final BufferedImage mask = copyImage(image, Transparency.BITMASK);
        final int height = mask.getHeight();
        final int width = mask.getWidth();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int col = mask.getRGB(x, y);
                final int flag = 0x00ffffff;
                if (col == rgba)
                {
                    mask.setRGB(x, y, col & flag);
                }
            }
        }
        return mask;
    }

    /**
     * Rotate an image with an angle in degree.
     * 
     * @param image The input image.
     * @param angle The angle in degree to apply.
     * @return The rotated image.
     */
    static BufferedImage rotate(BufferedImage image, int angle)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage rotated = createImage(width, height, transparency);
        final Graphics2D g = rotated.createGraphics();

        optimizeGraphics(g);
        g.rotate(Math.toRadians(angle), width / 2.0, height / 2.0);
        g.drawImage(image, null, 0, 0);
        g.dispose();

        return rotated;
    }

    /**
     * Resize input image buffer.
     * 
     * @param image The input image buffer.
     * @param width The new width.
     * @param height The new height.
     * @return The new image buffer with new size.
     */
    static BufferedImage resize(BufferedImage image, int width, int height)
    {
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage resized = createImage(width, height, transparency);
        final Graphics2D g = resized.createGraphics();

        optimizeGraphics(g);
        g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();

        return resized;
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image buffer.
     * @return The flipped image buffer as a new instance.
     */
    static BufferedImage flipHorizontal(BufferedImage image)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final BufferedImage flipped = createImage(width, height, image.getColorModel().getTransparency());
        final Graphics2D g = flipped.createGraphics();

        optimizeGraphics(g);
        g.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
        g.dispose();

        return flipped;
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image buffer.
     * @return The flipped image buffer as a new instance.
     */
    static BufferedImage flipVertical(BufferedImage image)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final BufferedImage flipped = createImage(width, height, image.getColorModel().getTransparency());
        final Graphics2D g = flipped.createGraphics();

        optimizeGraphics(g);
        g.drawImage(image, 0, 0, width, height, 0, height, width, 0, null);
        g.dispose();

        return flipped;
    }

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions (> 0).
     * @param v The number of vertical divisions (> 0).
     * @return The splited images array (can not be empty).
     */
    static BufferedImage[] splitImage(BufferedImage image, int h, int v)
    {
        final int width = image.getWidth() / h;
        final int height = image.getHeight() / v;
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage[] images = new BufferedImage[h * v];
        int frame = 0;

        for (int y = 0; y < v; y++)
        {
            for (int x = 0; x < h; x++)
            {
                images[frame] = createImage(width, height, transparency);
                final Graphics2D g = images[frame].createGraphics();
                optimizeGraphics(g);
                g.drawImage(image, 0, 0, width, height, x * width, y * height, (x + 1) * width, (y + 1) * height, null);
                g.dispose();
                frame++;
            }
        }

        return images;
    }

    /**
     * Get raster buffer from data.
     * 
     * @param image The image buffer.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     * @param size The reference size.
     * @return The rastered image.
     */
    static BufferedImage getRasterBuffer(BufferedImage image, int fr, int fg, int fb, int er, int eg, int eb, int size)
    {
        final boolean method = true;
        final BufferedImage raster = createImage(image.getWidth(), image.getHeight(), image.getTransparency());

        final int divisorRed = 0x010000;
        final int divisorGreen = 0x000100;
        final int divisorBlue = 0x000001;

        final double sr = -((er - fr) / divisorRed) / (double) size;
        final double sg = -((eg - fg) / divisorGreen) / (double) size;
        final double sb = -((eb - fb) / divisorBlue) / (double) size;

        if (method)
        {
            for (int i = 0; i < raster.getWidth(); i++)
            {
                for (int j = 0; j < raster.getHeight(); j++)
                {
                    final int r = (int) (sr * (j % size)) * divisorRed;
                    final int g = (int) (sg * (j % size)) * divisorGreen;
                    final int b = (int) (sb * (j % size)) * divisorBlue;

                    raster.setRGB(i, j, ColorRgba.filterRgb(image.getRGB(i, j), fr + r, fg + g, fb + b));
                }
            }
        }
        else
        {
            final int[] org = getImageData(image);
            final int width = raster.getWidth();
            final int height = raster.getHeight();
            final int[] pixels = getImageData(raster);

            for (int j = 0; j < height; j++)
            {
                for (int i = 0; i < width; i++)
                {
                    pixels[j * width + i] = ColorRgba.filterRgb(org[j * width + i], fr, fg, fb);
                }
            }
        }

        return raster;
    }

    /**
     * Create a hidden cursor.
     * 
     * @return Hidden cursor, or default cursor if not able to create it.
     */
    static Cursor createHiddenCursor()
    {
        try
        {
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            final Dimension dim = toolkit.getBestCursorSize(1, 1);
            final BufferedImage cursor = createImage(dim.width, dim.height, Transparency.BITMASK);
            final BufferedImage buffer = applyMask(cursor, Color.BLACK.getRGB());
            return toolkit.createCustomCursor(buffer, new Point(0, 0), "hiddenCursor");
        }
        catch (final Throwable exception)
        {
            Verbose.exception(ToolsAwt.class, "createHiddenCursor", exception);
            return Cursor.getDefaultCursor();
        }
    }

    /**
     * Enable all graphics improvement. May decrease overall performances.
     * 
     * @param g The graphic context.
     */
    static void optimizeGraphicsQuality(Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * Disable all graphics improvement. May increase overall performances.
     * 
     * @param g The graphic context.
     */
    static void optimizeGraphicsSpeed(Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    /**
     * Optimize graphic to performance mode.
     * 
     * @param g The graphic context.
     */
    static void optimizeGraphics(Graphics2D g)
    {
        optimizeGraphicsSpeed(g);
    }

    /**
     * Private constructor.
     */
    private ToolsAwt()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
