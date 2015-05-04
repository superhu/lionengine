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
package com.b3dgs.lionengine.game.configurer;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionGroup;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collision group data from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see CollisionGroup
 */
public final class ConfigCollisionGroup
{
    /** Collision group root node. */
    public static final String COLLISIONS = Configurer.PREFIX + "collisions";
    /** Collision group node. */
    public static final String COLLISION = Configurer.PREFIX + "collision";
    /** Group name attribute. */
    public static final String GROUP = "group";

    /**
     * Create the collision group data from node.
     * 
     * @param root The node root reference.
     * @param map The map reference.
     * @return The collisions group data.
     * @throws LionEngineException If unable to read node.
     */
    public static Collection<CollisionGroup> create(XmlNode root, MapTileCollision map) throws LionEngineException
    {
        final Collection<CollisionGroup> collisions = new ArrayList<>();
        for (final XmlNode node : root.getChildren(COLLISION))
        {
            final Collection<CollisionFormula> formulas = new ArrayList<>();
            for (final XmlNode formula : node.getChildren(ConfigCollisionFormula.FORMULA))
            {
                final String name = formula.getText();
                formulas.add(map.getCollisionFormula(name));
            }
            final CollisionGroup collision = new CollisionGroup(node.readString(GROUP), formulas);
            collisions.add(collision);
        }
        return collisions;
    }

    /**
     * Export the collision group data as a node.
     * 
     * @param group The collision group to export.
     * @return The node reference.
     */
    public static XmlNode export(CollisionGroup group)
    {
        final XmlNode node = Stream.createXmlNode(COLLISIONS);
        node.writeString(GROUP, group.getGroup());
        for (final CollisionFormula formula : group.getFormulas())
        {
            node.add(ConfigCollisionFormula.export(formula));
        }
        return node;
    }

    /**
     * Disabled constructor.
     */
    private ConfigCollisionGroup()
    {
        throw new RuntimeException();
    }
}
