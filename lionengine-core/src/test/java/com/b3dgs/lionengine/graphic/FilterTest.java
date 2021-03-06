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
package com.b3dgs.lionengine.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphic.engine.FilterNone;

/**
 * Test {@link Filter}.
 */
public final class FilterTest
{
    /**
     * Test default filter.
     */
    @Test
    public void testFilter()
    {
        final Transform transform = FilterNone.INSTANCE.getTransform(1.0, 1.0);

        assertEquals(1.0, transform.getScaleX());
        assertEquals(1.0, transform.getScaleY());
        assertEquals(0, transform.getInterpolation());

        transform.setInterpolation(true);
        transform.scale(0.0, 0.0);

        assertEquals(0, transform.getInterpolation());
    }
}
