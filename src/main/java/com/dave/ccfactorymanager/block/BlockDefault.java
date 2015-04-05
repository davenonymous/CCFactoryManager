package com.dave.ccfactorymanager.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

import com.dave.ccfactorymanager.creativetab.CreativeTabCCFactoryManager;
import com.dave.ccfactorymanager.reference.Reference;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDefault extends Block {
	public BlockDefault(Material material) {
		super(material);
	}

	public BlockDefault() {
		super(Material.wood);
		setHardness(2.5F);
		setLightOpacity(255);
		setLightLevel(0);
		setHarvestLevel("axe", 0);

		setCreativeTab(CreativeTabCCFactoryManager.CCFACTORYMANAGER_TAB);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("tile.%s:%s", Reference.MOD_ID.toLowerCase(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(String.format("%s:%s", Reference.MOD_ID.toLowerCase(), this.getTextureName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
}
