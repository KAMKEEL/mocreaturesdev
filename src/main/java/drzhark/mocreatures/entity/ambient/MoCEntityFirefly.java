package drzhark.mocreatures.entity.ambient;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityInsect;

public class MoCEntityFirefly extends MoCEntityInsect
{
    private int soundCount;

    public MoCEntityFirefly(World world)
    {
        super(world);
        texture = "firefly.png";
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
            if (getIsFlying() && --soundCount <= 0)
            {
                EntityPlayer ep = worldObj.getClosestPlayerToEntity(this, 5D);
                if (ep != null)
                {
                    MoCTools.playCustomSound(this, "cricketfly", this.worldObj);
                    soundCount = 21;
                }
                else
                {
                    soundCount = 10;
                }
            }

            if (getIsFlying() && rand.nextInt(500) == 0)
            {
                setIsFlying(false);
            }
        }
    }

    @Override
    protected float getFlyingSpeed()
    {
        return 0.3F;
    }

    @Override
    protected float getWalkingSpeed()
    {
        return 0.2F;
    }
}
