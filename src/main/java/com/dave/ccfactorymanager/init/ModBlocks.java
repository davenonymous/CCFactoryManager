package com.dave.ccfactorymanager.init;

import com.dave.ccfactorymanager.block.BlockFactoryCable;
import com.dave.ccfactorymanager.block.BlockFactoryController;
import com.dave.ccfactorymanager.reference.Names;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {
	public static final BlockFactoryController block_factory_controller = new BlockFactoryController();
	public static final BlockFactoryCable block_factory_cable = new BlockFactoryCable();

	public static void init() {
		GameRegistry.registerBlock(block_factory_controller, Names.Blocks.BLOCK_FACTORY_CONTROLLER);
		GameRegistry.registerBlock(block_factory_cable, Names.Blocks.BLOCK_FACTORY_CABLE);
	}
}
