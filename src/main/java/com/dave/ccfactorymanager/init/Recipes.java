package com.dave.ccfactorymanager.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class Recipes {
	public static void init() {
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ModBlocks.block_factory_controller),
				"ggg",
				"grg",
				"sps",
				'g', "ingotGold",
				'r', new ItemStack(Blocks.redstone_block),
				's', "stone",
				'p', new ItemStack(Blocks.piston)
				));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ModBlocks.block_factory_cable, 8, 0),
				"cwc",
				"grg",
				"cwc",
				'c', "blockGlass",
				'w', new ItemStack(Blocks.light_weighted_pressure_plate),
				'g', "ingotGold",
				'r', new ItemStack(Items.redstone)
				));
	}
}
