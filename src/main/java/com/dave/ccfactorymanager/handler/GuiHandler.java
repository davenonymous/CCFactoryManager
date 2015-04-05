package com.dave.ccfactorymanager.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.dave.ccfactorymanager.gui.GuiId;
import com.dave.ccfactorymanager.gui.container.ContainerController;
import com.dave.ccfactorymanager.gui.render.GuiController;
import com.dave.ccfactorymanager.tileentity.TileEntityFactoryController;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public GuiHandler() {}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GuiId.BLOCK_FACTORY_CONTROLLER.ordinal()) {
			TileEntityFactoryController te = (TileEntityFactoryController) world.getTileEntity(x, y, z);
			return new ContainerController(player.inventory, te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GuiId.BLOCK_FACTORY_CONTROLLER.ordinal()) {
			TileEntityFactoryController te = (TileEntityFactoryController) world.getTileEntity(x, y, z);
			return new GuiController(player.inventory, te);
		}
		return null;
	}

}
