package com.dave.ccfactorymanager.proxy;

import net.minecraft.entity.player.EntityPlayer;

public interface IProxy {
	public abstract void registerTileEntities();

	public abstract void registerRenderers();

	public abstract EntityPlayer getClientPlayer();
}
