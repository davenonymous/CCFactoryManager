package com.dave.ccfactorymanager.network;

import com.dave.ccfactorymanager.tileentity.TileEntityFactoryController;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageNameChange extends MessageXYZ<MessageNameChange> {
	protected int relX, relY, relZ;
	protected String name;

	public MessageNameChange() {}

	public MessageNameChange(TileEntity te, int x, int y, int z, String name) {
		super(te);
		this.relX = x;
		this.relY = y;
		this.relZ = z;
		this.name = name;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(relX);
		buf.writeInt(relY);
		buf.writeInt(relZ);
		ByteBufUtils.writeUTF8String(buf, name);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		relX = buf.readInt();
		relY = buf.readInt();
		relZ = buf.readInt();
		name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void handleClientSide(MessageNameChange message, EntityPlayer player) {}

	@Override
	public void handleServerSide(MessageNameChange message, EntityPlayer player) {
		TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
		if (te instanceof TileEntityFactoryController) {
			((TileEntityFactoryController) te).setName(message.relX, message.relY, message.relZ, message.name);
		}
	}

}
