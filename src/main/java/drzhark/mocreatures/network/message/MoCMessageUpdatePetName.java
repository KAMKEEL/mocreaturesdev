package drzhark.mocreatures.network.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.MoCPetData;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCTameable;
import drzhark.mocreatures.network.MoCServerPacketQueue;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;


public class MoCMessageUpdatePetName implements IMessage, IMessageHandler<MoCMessageUpdatePetName, IMessage> {

    private static final int MAX_NAME_LENGTH = 32;

    String name;
    int entityId;

    public MoCMessageUpdatePetName() {}

    public MoCMessageUpdatePetName(int entityId)
    {
        this.entityId = entityId;
    }

    public MoCMessageUpdatePetName(int entityId, String name)
    {
        this.entityId = entityId;
        this.name = name;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, this.name);
        buffer.writeInt(this.entityId);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.name = ByteBufUtils.readUTF8String(buffer);
        this.entityId = buffer.readInt();
    }

    @Override
    public IMessage onMessage(MoCMessageUpdatePetName message, MessageContext ctx)
    {
        final EntityPlayer player = ctx.getServerHandler().playerEntity;
        final int requestedEntityId = message.entityId;
        final String requestedName = limitName(message.name);
        MoCServerPacketQueue.enqueue(new Runnable() {
            @Override
            public void run() {
                renamePet(player, requestedEntityId, requestedName);
            }
        });
        return null;
    }

    private static String limitName(String name) {
        if (name == null) {
            return "";
        }
        int codePoints = name.codePointCount(0, name.length());
        if (codePoints <= MAX_NAME_LENGTH) {
            return name;
        }
        return name.substring(0, name.offsetByCodePoints(0, MAX_NAME_LENGTH));
    }

    private static void renamePet(EntityPlayer player, int entityId, String name) {
        Entity entity = player.worldObj.getEntityByID(entityId);
        if (!(entity instanceof IMoCTameable)) {
            return;
        }

        IMoCTameable pet = (IMoCTameable) entity;
        String ownerName = pet.getOwnerName();
        if (!pet.getIsTamed() || ownerName == null || !ownerName.equals(player.getCommandSenderName())) {
            return;
        }

        pet.setName(name);
        if (MoCreatures.instance.mapData == null || pet.getOwnerPetId() == -1) {
            return;
        }

        MoCPetData petData = MoCreatures.instance.mapData.getPetData(ownerName);
        if (petData == null) {
            return;
        }

        NBTTagList pets = petData.getOwnerRootNBT().getTagList("TamedList", 10);
        for (int i = 0; i < pets.tagCount(); i++) {
            NBTTagCompound petTag = pets.getCompoundTagAt(i);
            if (petTag.getInteger("PetId") == pet.getOwnerPetId()) {
                petTag.setString("Name", name);
                MoCreatures.instance.mapData.markDirty();
                return;
            }
        }
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageUpdatePetName - entityId:%s, name:%s", this.entityId, this.name);
    }
}
