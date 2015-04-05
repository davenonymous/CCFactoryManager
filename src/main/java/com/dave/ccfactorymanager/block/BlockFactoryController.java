package com.dave.ccfactorymanager.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.dave.ccfactorymanager.CCFactoryManager;
import com.dave.ccfactorymanager.gui.GuiId;
import com.dave.ccfactorymanager.reference.Names;
import com.dave.ccfactorymanager.reference.Reference;
import com.dave.ccfactorymanager.tileentity.TileEntityFactoryController;

public class BlockFactoryController extends BlockDefault implements ITileEntityProvider, IFactoryCable {

	private IIcon[] icons;

	public BlockFactoryController() {
		super();

		this.setBlockName(Names.Blocks.BLOCK_FACTORY_CONTROLLER);
		this.setBlockTextureName(Names.Blocks.BLOCK_FACTORY_CONTROLLER);
		this.setHardness(3.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityFactoryController();
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float faceX, float faceY, float faceZ) {
		if (player.isSneaking()) {
			return false;
		}

		if (!world.isRemote && player instanceof EntityPlayerMP) {
			player.openGui(CCFactoryManager.instance, GuiId.BLOCK_FACTORY_CONTROLLER.ordinal(), world, x, y, z);
		}

		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		icons = new IIcon[16];

		for (int i = 0; i < 16; i++) {
			icons[i] = iconRegister.registerIcon(Reference.MOD_ID + ":controller_" + String.format("%4s", Integer.toBinaryString(i)).replace(' ', '0'));
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
