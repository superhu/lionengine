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
package com.b3dgs.lionengine.headless.graphic;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;

/**
 * Test {@link EngineHeadless}.
 */
public final class EngineHeadlessTest
{
    /**
     * Test start without resources.
     */
    @Test(expected = LionEngineException.class)
    public void testNullResources()
    {
        EngineHeadless.start(EngineHeadlessTest.class.getName(), Version.DEFAULT, (String) null);
        try
        {
            Assert.assertTrue(Engine.isStarted());
            EngineHeadless.start(EngineHeadlessTest.class.getName(), Version.DEFAULT, (String) null);
        }
        finally
        {
            Engine.terminate();
        }
    }

    /**
     * Test start with resources.
     */
    @Test(expected = LionEngineException.class)
    public void testResources()
    {
        EngineHeadless.start(EngineHeadlessTest.class.getName(), Version.DEFAULT, Constant.EMPTY_STRING);
        try
        {
            Assert.assertTrue(Engine.isStarted());
            EngineHeadless.start(EngineHeadlessTest.class.getName(), Version.DEFAULT, Constant.EMPTY_STRING);
        }
        finally
        {
            Engine.terminate();
        }
    }

    /**
     * Test start with class.
     */
    @Test(expected = LionEngineException.class)
    public void testClass()
    {
        EngineHeadless.start(EngineHeadlessTest.class.getName(), Version.DEFAULT, EngineHeadlessTest.class);
        try
        {
            Assert.assertTrue(Engine.isStarted());
            EngineHeadless.start(EngineHeadlessTest.class.getName(), Version.DEFAULT, EngineHeadlessTest.class);
        }
        finally
        {
            Engine.terminate();
        }
    }
}
