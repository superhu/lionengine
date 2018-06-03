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
package com.b3dgs.lionengine.graphic.engine;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Single sequence mock.
 */
final class SequenceEngineTerminateMock extends Sequence
{
    /** Force termination. */
    public static final AtomicBoolean CLOSE = new AtomicBoolean();

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    SequenceEngineTerminateMock(Context context)
    {
        super(context);
    }

    @Override
    public void load()
    {
        Engine.terminate();
    }

    @Override
    public void update(double extrp)
    {
        if (CLOSE.compareAndSet(true, false))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        // Mock
    }
}