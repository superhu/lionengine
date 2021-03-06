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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.Mover;

/**
 * Represents something that can be transformed with a translation or a size modification.
 */
@FeatureInterface
public interface Transformable extends Feature, Mover, Shape
{
    /**
     * Add a listener.
     * 
     * @param listener The listener to add.
     */
    void addListener(TransformableListener listener);

    /**
     * Remove a listener.
     * 
     * @param listener The listener to remove.
     */
    void removeListener(TransformableListener listener);

    /**
     * Set surface size. Old size is stored.
     * 
     * @param width The width.
     * @param height The height.
     */
    void setSize(int width, int height);

    /**
     * Transform the transformable.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The width.
     * @param height The height.
     */
    void transform(double x, double y, int width, int height);

    /**
     * Get the old width.
     * 
     * @return The old width.
     */
    int getOldWidth();

    /**
     * Get the old height.
     * 
     * @return The old height.
     */
    int getOldHeight();
}
