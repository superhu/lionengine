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
package com.b3dgs.lionengine.game.object;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.configurer.ConfigObject;
import com.b3dgs.lionengine.game.configurer.Configurer;

/**
 * Performs a list of {@link Setup} considering their corresponding {@link Media} pointing to an XML file. This way it
 * is possible to create new object instances related to their {@link Setup} by sharing the same resources.
 * <p>
 * Any object created by the factory from a {@link Media} must have the following public constructor:
 * </p>
 * <ul>
 * <li>{@link ObjectGame#ObjectGame(Setup, Services)}</li>
 * </ul>
 * <p>
 * The factory uses the {@link ClassLoader#getSystemClassLoader()}, but it is possible to set a custom one with
 * {@link #setClassLoader(ClassLoader)}. Should be used in an OSGI environment for example.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Factory
{
    /** Data file extension. */
    public static final String FILE_DATA_EXTENSION = "xml";
    /** Setup class error. */
    private static final String ERROR_SETUP_CLASS = "Setup class not found !";
    /** Setup error. */
    private static final String ERROR_SETUP = "Setup not found for: ";
    /** Constructor error. */
    private static final String ERROR_CONSTRUCTOR = "Unable to create the following type: ";
    /** Construction error. */
    private static final String ERROR_CONSTRUCTOR_MISSING = "No recognized constructor found for: ";

    /**
     * Create a class instance with its parameters.
     * 
     * @param <T> The element type used.
     * @param type The class type to instantiate.
     * @param paramTypes The class base type for each parameter.
     * @param params The constructor parameters.
     * @return The class instance.
     * @throws LionEngineException If unable to create the instance or type is <code>null</code>.
     * @throws NoSuchMethodException If constructor has not been found.
     */
    public static <T> T create(Class<?> type, Class<?>[] paramTypes, Object... params) throws LionEngineException,
            NoSuchMethodException
    {
        Check.notNull(type);
        try
        {
            final Constructor<?> constructor = type.getDeclaredConstructor(paramTypes);
            final boolean accessible = constructor.isAccessible();
            if (!accessible)
            {
                constructor.setAccessible(true);
            }
            @SuppressWarnings("unchecked")
            final T object = (T) constructor.newInstance(params);
            if (constructor.isAccessible() != accessible)
            {
                constructor.setAccessible(accessible);
            }
            return object;
        }
        catch (final NoSuchMethodException exception)
        {
            throw exception;
        }
        catch (final ReflectiveOperationException
                     | IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR + type);
        }
    }

    /** Setups list. */
    private final Map<Media, Setup> setups = new HashMap<>();
    /** Services reference. */
    private final Services services;
    /** Class loader. */
    private ClassLoader classLoader;

    /**
     * Create a factory.
     * 
     * @param services The services reference.
     */
    public Factory(Services services)
    {
        super();
        this.services = services;
        classLoader = ClassLoader.getSystemClassLoader();
    }

    /**
     * Create an object from its {@link Media} using a generic way. The concerned classes to instantiate and its
     * constructor must be public, and must have the following parameters: ({@link Setup}, {@link Services}).
     * 
     * @param media The object media.
     * @return The object instance.
     * @throws LionEngineException If {@link Media} is <code>null</code>, {@link Setup} not found, or {@link Services}
     *             missing service.
     * @see ObjectGame#ObjectGame(Setup, Services)
     */
    public <E extends ObjectGame> E create(Media media) throws LionEngineException
    {
        final Setup setup = getSetup(media);
        final Class<?> type = setup.getConfigClass(classLoader);
        try
        {
            final E object = create(type, new Class<?>[]
            {
                    setup.getClass(), Services.class
            }, setup, services);
            final Integer id = HandledObjectsImpl.getFreeId();
            object.setId(id);
            object.prepareTraits(setup, services);
            return object;
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR_MISSING + media);
        }
    }

    /**
     * Clear all loaded setup and their configuration.
     */
    public void clear()
    {
        for (final Setup setup : setups.values())
        {
            setup.clear();
        }
        setups.clear();
    }

    /**
     * Set an external class loader.
     * 
     * @param classLoader The external class loader.
     */
    public void setClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    /**
     * Get a setup reference from its media.
     * 
     * @param media The setup media.
     * @return The setup reference.
     * @throws LionEngineException If no setup found for the media.
     */
    public Setup getSetup(Media media) throws LionEngineException
    {
        Check.notNull(media);
        if (!setups.containsKey(media))
        {
            setups.put(media, createSetup(media));
        }
        final Setup setup = setups.get(media);
        if (setup == null)
        {
            throw new LionEngineException(ERROR_SETUP, media.getPath());
        }
        return setup;
    }

    /**
     * Create a setup from its media.
     * 
     * @param media The media reference.
     * @return The setup instance.
     */
    private Setup createSetup(Media media)
    {
        final Configurer configurer = new Configurer(media);
        try
        {
            final ConfigObject configObject = ConfigObject.create(configurer);
            final Class<?> setupClass = classLoader.loadClass(configObject.getSetupName());
            return create(setupClass, new Class<?>[]
            {
                Media.class
            }, media);
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, ERROR_SETUP_CLASS);
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR_MISSING + media.getPath());
        }
    }
}
