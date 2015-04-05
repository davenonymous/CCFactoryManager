package com.dave.ccfactorymanager.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.dave.ccfactorymanager.init.ModBlocks;
import com.dave.ccfactorymanager.reference.Reference;

public class CreativeTabCCFactoryManager {
	public static final CreativeTabs CCFACTORYMANAGER_TAB = new CreativeTabs(Reference.MOD_ID.toLowerCase()) {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(ModBlocks.block_factory_controller);
		}
	};
}
