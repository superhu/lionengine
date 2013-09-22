package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.EffectType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Big Potion item. Give all heart to the player.
 */
public final class PotionBig
        extends EntityItem
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public PotionBig(Context context)
    {
        super(context, EntityType.POTION_BIG, EffectType.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        entity.stats.fillHeart();
    }
}
