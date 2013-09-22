package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import com.b3dgs.lionengine.example.d_rts.e_skills.HandlerEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.SkillType;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.UnitAttacker;
import com.b3dgs.lionengine.game.entity.EntityNotFoundException;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverServices;

/**
 * Attack melee implementation.
 */
final class AttackAxe
        extends Skill
{
    /** Handler reference. */
    private final HandlerEntity handler;

    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     * @param handler The handler reference.
     */
    AttackAxe(SetupSkill setup, HandlerEntity handler)
    {
        super(SkillType.ATTACK_AXE, setup);
        this.handler = handler;
        setOrder(true);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        try
        {
            if (owner instanceof UnitAttacker)
            {
                ((UnitAttacker) owner).attack(handler.getEntityAt(destX, destY));
            }
        }
        catch (final EntityNotFoundException exception)
        {
            if (owner instanceof MoverServices)
            {
                ((MoverServices) owner).setDestination(destX, destY);
            }
        }
    }
}
