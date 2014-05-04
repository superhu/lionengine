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
package com.b3dgs.lionengine.example.pong;

import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.game.CameraGame;

/**
 * This is where the game loop is running.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);
    /** Number of lines in the middle. */
    private static final int LINES = 30;

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Text drawer. */
    private final Text text;
    /** Camera. */
    private final CameraGame camera;
    /** Rackets. */
    private final Set<Racket> rackets;
    /** Ball. */
    private final Ball ball;
    /** Handler. */
    private final Handler handler;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        text = Core.GRAPHIC.createText(Text.SANS_SERIF, 16, TextStyle.NORMAL);
        camera = new CameraGame();
        rackets = new HashSet<>(2);
        ball = new Ball(getWidth(), getHeight());
        handler = new Handler(getWidth(), getHeight(), rackets, ball);
        setSystemCursorVisible(false);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        camera.setView(0, 0, getWidth(), getHeight());
        // Add a player on left
        rackets.add(new Racket(getWidth(), getHeight(), 10, getHeight() / 2, true));
        // Add a player on right
        rackets.add(new Racket(getWidth(), getHeight(), getWidth() - 10, getHeight() / 2, true));
        handler.setRacketSpeed(3.0);
        handler.engage();
    }

    @Override
    protected void update(double extrp)
    {
        handler.update(extrp, keyboard);
        // Terminate
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        // Clear screen
        g.clear(0, 0, getWidth(), getHeight());

        // Draw middle line
        final int size = getHeight() / Scene.LINES;
        g.setColor(ColorRgba.GRAY);

        for (int i = 0; i < Scene.LINES; i++)
        {
            g.drawRect(getWidth() / 2 - 4, i * size + size / 4, 4, size / 2, true);
        }

        // Draw rackets
        for (final Racket racket : rackets)
        {
            racket.render(g, camera);
        }

        // Draw ball
        ball.render(g, camera);

        // Display scores
        text.setColor(ColorRgba.BLUE);
        text.draw(g, getWidth() / 4, 0, Align.CENTER, String.valueOf(handler.getScoreLeft()));
        text.draw(g, getWidth() / 2 + getWidth() / 4, 0, Align.CENTER, String.valueOf(handler.getScoreRight()));
    }
}