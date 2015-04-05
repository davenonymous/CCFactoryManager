package com.dave.ccfactorymanager.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.dave.ccfactorymanager.reference.Names;
import com.dave.ccfactorymanager.reference.Reference;

public class BlockFactoryCable extends BlockDefault implements IFactoryCable {

	private IIcon[] icons;

	public BlockFactoryCable() {
		super();

		this.setBlockName(Names.Blocks.BLOCK_FACTORY_CABLE);
		this.setBlockTextureName(Names.Blocks.BLOCK_FACTORY_CABLE);
		this.setHardness(3.0F);
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		icons = new IIcon[16];

		for (int i = 0; i < 16; i++) {
			icons[i] = iconRegister.registerIcon(Reference.MOD_ID + ":cable_" + String.format("%4s", Integer.toBinaryString(i)).replace(' ', '0'));
		}
	}

	@Override
	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		return icons[0];
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		boolean left = false;
		boolean right = false;
		boolean top = false;
		boolean bottom = false;
		if (side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal()) {
			if (world.getBlock(x - 1, y, z) instanceof IFactoryCable) {
				left = true;
			}

			if (world.getBlock(x + 1, y, z) instanceof IFactoryCable) {
				right = true;
			}

			if (world.getBlock(x, y, z - 1) instanceof IFactoryCable) {
				top = true;
			}

			if (world.getBlock(x, y, z + 1) instanceof IFactoryCable) {
				bottom = true;
			}
		} else {
			if (world.getBlock(x, y + 1, z) instanceof IFactoryCable) {
				top = true;
			}

			if (world.getBlock(x, y - 1, z) instanceof IFactoryCable) {
				bottom = true;
			}
		}

		if (side == ForgeDirection.NORTH.ordinal()) {
			if (world.getBlock(x + 1, y, z) instanceof IFactoryCable) {
				left = true;
			}

			if (world.getBlock(x - 1, y, z) instanceof IFactoryCable) {
				right = true;
			}
		}

		if (side == ForgeDirection.SOUTH.ordinal()) {
			if (world.getBlock(x - 1, y, z) instanceof IFactoryCable) {
				left = true;
			}

			if (world.getBlock(x + 1, y, z) instanceof IFactoryCable) {
				right = true;
			}
		}

		if (side == ForgeDirection.WEST.ordinal()) {
			if (world.getBlock(x, y, z - 1) instanceof IFactoryCable) {
				left = true;
			}

			if (world.getBlock(x, y, z + 1) instanceof IFactoryCable) {
				right = true;
			}
		}

		if (side == ForgeDirection.EAST.ordinal()) {
			if (world.getBlock(x, y, z + 1) instanceof IFactoryCable) {
				left = true;
			}

			if (world.getBlock(x, y, z - 1) instanceof IFactoryCable) {
				right = true;
			}
		}

		int result = 0;
		if (top)
			result += 8;
		if (bottom)
			result += 4;
		if (left)
			result += 2;
		if (right)
			result += 1;

		return icons[result];
	}
}
