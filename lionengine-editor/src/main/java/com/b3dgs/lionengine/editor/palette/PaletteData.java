/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.palette;

/**
 * Describe the palette data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PaletteData
{
    /** Palette name. */
    private final String name;
    /** Palette id. */
    private final String id;
    /** Palette associated object. */
    private final Object object;
    /** Palette representation view. */
    private final PaletteView view;

    /**
     * Constructor.
     * 
     * @param name The palette name.
     * @param id The palette unique ID.
     * @param object The associated object.
     * @param view The associated view.
     */
    public PaletteData(String name, String id, Object object, PaletteView view)
    {
        this.name = name;
        this.id = id;
        this.object = object;
        this.view = view;
    }

    /**
     * Get the palette name.
     * 
     * @return The palette name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the palette ID.
     * 
     * @return The palette ID.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Get the palette object.
     * 
     * @return The associated object.
     */
    public Object getObject()
    {
        return object;
    }

    /**
     * Get the associated view.
     * 
     * @return The associated view.
     */
    public PaletteView getView()
    {
        return view;
    }
}
