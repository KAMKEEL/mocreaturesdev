package drzhark.mocreatures.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.network.MoCServerPacketQueue;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MoCMessageEntityJump implements IMessage, IMessageHandler<MoCMessageEntityJump, IMessage> {

    public MoCMessageEntityJump() {}

    @Override
    public void toBytes(ByteBuf buffer)
    {
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
    }

    @Override
    public IMessage onMessage(MoCMessageEntityJump message, MessageContext ctx)
    {
        final EntityPlayer player = ctx.getServerHandler().playerEntity;
        MoCServerPacketQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                if (player.ridingEntity instanceof IMoCEntity) {
                    ((IMoCEntity) player.ridingEntity).makeEntityJump();
                }
            }
        });
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageEntityJump");
    }
}
