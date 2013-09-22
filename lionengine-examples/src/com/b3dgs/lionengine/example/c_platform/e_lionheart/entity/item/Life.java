package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.EffectType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Life item. Give a life to the player when taken.
 */
public final class Life
        extends EntityItem
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Life(Context context)
    {
        super(context, EntityType.LIFE, EffectType.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        entity.stats.increaseLife();
    }
}
