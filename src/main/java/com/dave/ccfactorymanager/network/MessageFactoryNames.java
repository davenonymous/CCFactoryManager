package com.dave.ccfactorymanager.network;

import io.netty.buffer.ByteBuf;

import com.dave.ccfactorymanager.tileentity.TileEntityFactoryController;
import cpw.mods.fml.common.network.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class MessageFactoryNames extends MessageXYZ<MessageFactoryNames> {
	private NBTTagCompound data;

	public MessageFactoryNames() {}

	public MessageFactoryNames(TileEntity te) {
		super(te);
		this.data = new NBTTagCompound();
		te.writeToNBT(this.data);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		ByteBufUtils.writeTag(buf, data);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void handleClientSide(MessageFactoryNames message, EntityPlayer player) {
		TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
		if (te instanceof TileEntityFactoryController) {
			((TileEntityFactoryController) te).readFromNBT(message.data);
		}
	}

	@Override
	public void handleServerSide(MessageFactoryNames message, EntityPlayer player) {}
}
