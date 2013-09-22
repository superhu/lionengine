package com.b3dgs.lionengine.example.d_rts.e_skills;

import com.b3dgs.lionengine.game.rts.map.TileRts;

/**
 * Tile implementation, using TileModel. Nothing special here, just to show the default inheritance.
 */
public final class Tile
        extends TileRts<TileCollision, ResourceType>
{
    /**
     * Standard complete constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     */
    Tile(int width, int height)
    {
        super(width, height);
    }

    /*
     * TileRts
     */

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        try
        {
            return TileCollision.valueOf(collision);
        }
        catch (IllegalArgumentException
               | NullPointerException exception)
        {
            return TileCollision.NONE;
        }
    }

    @Override
    public void checkResourceType(TileCollision collision)
    {
        switch (collision.getGroup())
        {
            case TREE:
                setResourceType(ResourceType.WOOD);
                break;
            default:
                setResourceType(ResourceType.NONE);
                break;
        }
    }

    @Override
    public boolean checkBlocking(TileCollision collision)
    {
        return TileCollisionGroup.GROUND != collision.getGroup();
    }

    @Override
    public boolean hasResources()
    {
        return getResourceType() != ResourceType.NONE;
    }
}
