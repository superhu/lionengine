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
package com.b3dgs.lionengine.audio.sc68;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.OperatingSystem;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Verbose;
import com.sun.jna.Native;

/**
 * Handle audio sc68.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class AudioSc68
{
    /** Standard library name. */
    private static final String LIBRARY_NAME = "sc68player";
    /** DLL extension. */
    private static final String EXTENSION_DLL = ".dll";
    /** SO extension. */
    private static final String EXTENSION_SO = ".so";
    /** Windows system. */
    private static final String SYSTEM_WINDOW = "win32-";
    /** Linux system. */
    private static final String SYSTEM_LINUX = "linux-";
    /** 64bits architecture. */
    private static final String ARCHITECTURE_X64 = "x86-64";
    /** 32bits architecture. */
    private static final String ARCHITECTURE_X86 = "x86";
    /** Load library error. */
    private static final String ERROR_LOAD_LIBRARY = "Error on loading SC68 Library: ";

    /**
     * Create a sc68 player.
     * 
     * @return The sc68 player instance.
     * @throws LionEngineException If unable to load library.
     */
    public static Sc68 createSc68Player() throws LionEngineException
    {
        final AudioSc68 sc68 = new AudioSc68();
        return new Sc68Player(sc68.getBinding());
    }

    /**
     * Load the library.
     * 
     * @param library The library path.
     * @return The library binding.
     * @throws LionEngineException If error on loading.
     */
    private static Sc68Binding loadLibrary(String library) throws LionEngineException
    {
        final InputStream input = AudioSc68.class.getResourceAsStream(library);
        try
        {
            final File tempLib = UtilFile.getCopy(library, input);
            final Sc68Binding binding = (Sc68Binding) Native.loadLibrary(tempLib.getCanonicalPath(), Sc68Binding.class);
            Verbose.info("Library ", library, " loaded");
            return binding;
        }
        catch (final Throwable exception)
        {
            throw new LionEngineException(exception, AudioSc68.ERROR_LOAD_LIBRARY, library);
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (final IOException exception2)
            {
                Verbose.exception(Sc68Binding.class, "loadLibrary", exception2);
            }
        }
    }

    /**
     * Get the library system.
     * 
     * @return The library system.
     */
    private static String getLibrarySystem()
    {
        switch (OperatingSystem.getOperatingSystem())
        {
            case UNIX:
            case MAC:
            case SOLARIS:
            case UNKNOWN:
                return AudioSc68.SYSTEM_LINUX;
            case WINDOWS:
                return AudioSc68.SYSTEM_WINDOW;
            default:
                throw new RuntimeException();
        }
    }

    /**
     * Get the library extension.
     * 
     * @return The library extension.
     */
    private static String getLibraryExtension()
    {
        switch (OperatingSystem.getOperatingSystem())
        {
            case UNIX:
            case MAC:
            case SOLARIS:
            case UNKNOWN:
                return AudioSc68.EXTENSION_SO;
            case WINDOWS:
                return AudioSc68.EXTENSION_DLL;
            default:
                throw new RuntimeException();
        }
    }

    /**
     * Get the library architecture.
     * 
     * @return The library architecture.
     */
    private static String getLibraryArchitecture()
    {
        switch (OperatingSystem.getArchitecture())
        {
            case X86:
            case UNKNOWN:
                return AudioSc68.ARCHITECTURE_X86;
            case X64:
                return AudioSc68.ARCHITECTURE_X64;
            default:
                throw new RuntimeException();
        }
    }

    /** Sc68 binding. */
    private final Sc68Binding bind;

    /**
     * Private constructor.
     * 
     * @throws LionEngineException If unable to load library.
     */
    private AudioSc68() throws LionEngineException
    {
        final String ext = AudioSc68.getLibraryExtension();
        final String sys = AudioSc68.getLibrarySystem();
        final String arch = AudioSc68.getLibraryArchitecture();

        final String name = AudioSc68.LIBRARY_NAME + ext;
        final String library = sys + arch + '/' + name;
        bind = AudioSc68.loadLibrary(library);
    }

    /**
     * Get the binding reference.
     * 
     * @return The binding reference.
     */
    private Sc68Binding getBinding()
    {
        return bind;
    }
}
