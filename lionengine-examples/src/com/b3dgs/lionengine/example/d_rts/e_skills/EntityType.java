package com.b3dgs.lionengine.example.d_rts.e_skills;

import java.util.Locale;

/**
 * List of entity types.
 */
public enum EntityType
{
    /** Peon unit. */
    PEON,
    /** Grunt unit. */
    GRUNT,
    /** Spearman unit. */
    SPEARMAN,
    /** TownHall building. */
    TOWNHALL_ORC,
    /** Barracks building. */
    BARRACKS_ORC,
    /** Gold mine building. */
    GOLD_MINE;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
