package com.dave.ccfactorymanager.gui.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;

import com.dave.ccfactorymanager.handler.PacketHandler;
import com.dave.ccfactorymanager.network.MessageFactoryNames;
import com.dave.ccfactorymanager.tileentity.TileEntityFactoryController;

public class ContainerController extends ContainerDefault {
	private TileEntityFactoryController tileEntity;

	public ContainerController(InventoryPlayer inventoryPlayer, TileEntityFactoryController tileEntity) {
		this.tileEntity = tileEntity;

		addPlayerSlots(inventoryPlayer, 8, 128);

		if (!tileEntity.getWorldObj().isRemote) {
			tileEntity.bClientsNeedUpdate = true;
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (tileEntity.bClientsNeedUpdate && crafters.size() > 0) {
			NBTTagCompound tag = new NBTTagCompound();
			tileEntity.writeToNBT(tag);

			for (ICrafting crafter : (List<ICrafting>) crafters) {
				PacketHandler.INSTANCE.sendTo(new MessageFactoryNames(tileEntity), (EntityPlayerMP) crafter);
			}

			tileEntity.bClientsNeedUpdate = false;
		}
	}
}
