package com.dave.ccfactorymanager.proxy;

import net.minecraft.entity.player.EntityPlayer;

import com.dave.ccfactorymanager.reference.Names;
import com.dave.ccfactorymanager.tileentity.TileEntityFactoryController;

import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IProxy {

	@Override
	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityFactoryController.class, Names.Blocks.BLOCK_FACTORY_CONTROLLER);
	}

	@Override
	public void registerRenderers() {}

	@Override
	public EntityPlayer getClientPlayer() {
		return null;
	}
}
