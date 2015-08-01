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

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.collision.CollisionConstraint;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.collision.CollisionRange;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collisions formula from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see CollisionFormula
 */
public final class ConfigCollisionFormula
{
    /** Collision formula root node. */
    public static final String FORMULAS = Configurer.PREFIX + "formulas";
    /** Collision formula node. */
    public static final String FORMULA = Configurer.PREFIX + "formula";
    /** The formula name attribute. */
    public static final String NAME = "name";

    /**
     * Create the formula data from node.
     * 
     * @param root The root reference.
     * @return The collision formula data.
     * @throws LionEngineException If error when reading data.
     */
    public static ConfigCollisionFormula create(XmlNode root) throws LionEngineException
    {
        final Map<String, CollisionFormula> collisions = new HashMap<String, CollisionFormula>(0);
        for (final XmlNode node : root.getChildren(FORMULA))
        {
            final String name = node.readString(NAME);
            final CollisionFormula collision = createCollision(node);
            collisions.put(name, collision);
        }
        return new ConfigCollisionFormula(collisions);
    }

    /**
     * Export the current formula data to the formula node.
     * 
     * @param root The root node.
     * @param formula The formula reference.
     * @throws LionEngineException If error on writing.
     */
    public static void export(XmlNode root, CollisionFormula formula) throws LionEngineException
    {
        final XmlNode node = root.createChild(FORMULA);
        node.writeString(NAME, formula.getName());

        ConfigCollisionRange.export(node, formula.getRange());
        ConfigCollisionFunction.export(node, formula.getFunction());
        ConfigCollisionConstraint.export(node, formula.getConstraint());
    }

    /**
     * Create a collision formula from its node.
     * 
     * @param node The collision formula node.
     * @return The tile collision formula instance.
     * @throws LionEngineException If error when reading data.
     */
    private static CollisionFormula createCollision(XmlNode node) throws LionEngineException
    {
        final String name = node.readString(NAME);
        final CollisionRange range = ConfigCollisionRange.create(node.getChild(ConfigCollisionRange.RANGE));
        final CollisionFunction function = ConfigCollisionFunction.create(node);
        final CollisionConstraint constraint = ConfigCollisionConstraint.create(node);

        return new CollisionFormula(name, range, function, constraint);
    }

    /** Collision formulas list. */
    private final Map<String, CollisionFormula> formulas;

    /**
     * Disabled constructor.
     */
    private ConfigCollisionFormula()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }

    /**
     * Create a collision formula config map.
     * 
     * @param formulas The collisions formula mapping.
     */
    private ConfigCollisionFormula(Map<String, CollisionFormula> formulas)
    {
        this.formulas = formulas;
    }

    /**
     * Clear the formulas data.
     */
    public void clear()
    {
        formulas.clear();
    }

    /**
     * Get a collision formula data from its name.
     * 
     * @param name The formula name.
     * @return The formula reference.
     * @throws LionEngineException If the formula with the specified name is not found.
     */
    public CollisionFormula getFormula(String name) throws LionEngineException
    {
        final CollisionFormula collision = formulas.get(name);
        Check.notNull(collision);
        return collision;
    }

    /**
     * Get all formulas.
     * 
     * @return The formulas map, where key is the formula name.
     */
    public Map<String, CollisionFormula> getFormulas()
    {
        return formulas;
    }
}
