/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.raster;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.FactoryMediaDefault;
import com.b3dgs.lionengine.Medias;

/**
 * Test {@link Raster}.
 */
public final class RasterTest
{
    /**
     * Prepare test.
     * 
     * @throws IOException If error.
     */
    @BeforeAll
    public static void setUp() throws IOException
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(RasterTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test field.
     */
    @Test
    public void testField()
    {
        final RasterData red = new RasterData(0, 0, 0, 0, 0, 0);
        final RasterData green = new RasterData(0, 0, 0, 0, 0, 0);
        final RasterData blue = new RasterData(0, 0, 0, 0, 0, 0);
        final Raster raster = new Raster(red, green, blue);

        assertEquals(red, raster.getRed());
        assertEquals(green, raster.getGreen());
        assertEquals(blue, raster.getBlue());
    }

    /**
     * Test color.
     */
    @Test
    public void testColor()
    {
        final RasterData raster = new RasterData(0, 0, 0, 0, 0, 0);
        final RasterColor color = RasterColor.load(raster, 0, 0, false);

        assertEquals(0, color.getStart());
        assertEquals(0, color.getEnd());

        final RasterData raster2 = new RasterData(200, 10, 10, 100, 10, 0);
        final RasterColor color2 = RasterColor.load(raster2, 0, 90, false);

        assertEquals(130, color2.getStart());
        assertEquals(130, color2.getEnd());
    }

    /**
     * Test color smoothed.
     */
    @Test
    public void testColorSmooth()
    {
        final RasterData raster = new RasterData(200, 10, 10, 100, 10, 0);
        final RasterColor color = RasterColor.load(raster, 0, 90, true);

        assertEquals(130, color.getStart());
        assertEquals(120, color.getEnd());

        final RasterData raster2 = new RasterData(200, 10, 10, 100, 10, 0);
        final RasterColor color2 = RasterColor.load(raster2, 1, 90, true);

        assertEquals(160, color2.getStart());
        assertEquals(170, color2.getEnd());
    }

    /**
     * Test load.
     */
    @Test
    public void testLoad()
    {
        assertNotNull(Raster.load(Medias.create("raster.xml")));
    }

    /**
     * Test load failure.
     */
    @Test
    public void testLoadFailure()
    {
        assertThrows(() -> Raster.load(Medias.create("raster_error.xml")), "Node not found: Red");
    }
}
