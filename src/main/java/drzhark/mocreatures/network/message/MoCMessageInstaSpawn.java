package drzhark.mocreatures.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.network.MoCServerPacketQueue;
import drzhark.mocreatures.utils.MoCLog;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MoCMessageInstaSpawn implements IMessage, IMessageHandler<MoCMessageInstaSpawn, IMessage> {

    private int entityId;
    private int numberToSpawn;

    public MoCMessageInstaSpawn() {}

    public MoCMessageInstaSpawn(int entityId, int numberToSpawn)
    {
        this.entityId = entityId;
        this.numberToSpawn = numberToSpawn;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.numberToSpawn);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.entityId = buffer.readInt();
        this.numberToSpawn = buffer.readInt();
    }

    @Override
    public IMessage onMessage(MoCMessageInstaSpawn message, MessageContext ctx)
    {
        final EntityPlayer player = ctx.getServerHandler().playerEntity;
        final int requestedEntityId = message.entityId;
        final int requestedCount = message.numberToSpawn;
        MoCServerPacketQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                if ((MoCreatures.proxy.getProxyMode() == 1 && MoCreatures.proxy.allowInstaSpawn) || MoCreatures.proxy.getProxyMode() == 2) {
                    MoCTools.spawnNearPlayer(player, requestedEntityId, requestedCount);
                    if (MoCreatures.proxy.debug) MoCLog.logger.info("Player " + player.getCommandSenderName() + " used MoC instaspawner and got " + requestedCount + " creatures spawned");
                } else if (MoCreatures.proxy.debug) {
                    MoCLog.logger.info("Player " + player.getCommandSenderName() + " tried to use MoC instaspawner, but the allowInstaSpawn setting is set to " + MoCreatures.proxy.allowInstaSpawn);
                }
            }
        });
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageInstaSpawn - entityId:%s, numberToSpawn:%s", this.entityId, this.numberToSpawn);
    }
}
