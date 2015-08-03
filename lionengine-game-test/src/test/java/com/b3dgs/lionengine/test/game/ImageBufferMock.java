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
package com.b3dgs.lionengine.test.game;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;

/**
 * Mock image buffer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImageBufferMock implements ImageBuffer
{
    /** Width. */
    private final int width;
    /** Height. */
    private final int height;
    /** Rgba. */
    private final int[] rgba;
    /** Transparency. */
    private final Transparency transparency;

    /**
     * Constructor.
     * 
     * @param width The buffer width.
     * @param height The buffer height.
     * @param transparency The transparency.
     */
    public ImageBufferMock(int width, int height, Transparency transparency)
    {
        this.width = width;
        this.height = height;
        this.transparency = transparency;
        rgba = new int[width * height];
    }

    @Override
    public void prepare() throws LionEngineException
    {
        // Nothing to do
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicMock();
    }

    @Override
    public void dispose()
    {
        // Nothing to do
    }

    @Override
    public void setRgb(int x, int y, int rgb)
    {
        rgba[x % width + y * width] = rgb;
    }

    @Override
    public void setRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        System.arraycopy(rgbArray, 0, rgba, 0, w * h);
    }

    @Override
    public int getRgb(int x, int y)
    {
        return rgba[x % width + y * width];
    }

    @Override
    public int[] getRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        System.arraycopy(rgba, 0, rgbArray, 0, w * h);
        return rgbArray;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public Transparency getTransparency()
    {
        return transparency;
    }
}