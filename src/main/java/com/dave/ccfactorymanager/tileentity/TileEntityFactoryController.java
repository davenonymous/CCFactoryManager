package com.dave.ccfactorymanager.tileentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

import com.dave.ccfactorymanager.block.IFactoryCable;
import com.dave.ccfactorymanager.converter.AdvancedItemStack;
import com.dave.ccfactorymanager.util.RelativePos;
import com.theoriginalbit.framework.peripheral.annotation.LuaPeripheral;
import com.theoriginalbit.framework.peripheral.annotation.function.LuaFunction;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

@LuaPeripheral("factory_controller")
public class TileEntityFactoryController extends TileEntityDefault {

	HashMap<RelativePos, String> nameByPos = new HashMap<RelativePos, String>();
	HashMap<String, RelativePos> posByName = new HashMap<String, RelativePos>();

	public boolean bClientsNeedUpdate = false;

	public TileEntityFactoryController() {
		super();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound compound = new NBTTagCompound();

		for (RelativePos key : nameByPos.keySet()) {
			String name = nameByPos.get(key);
			compound.setString(key.toString(), name);
		}

		tag.setTag("targets", compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		nameByPos.clear();
		posByName.clear();

		if (tag.hasKey("targets")) {
			NBTTagCompound compound = tag.getCompoundTag("targets");
			Collection<String> tags = compound.func_150296_c();
			for (String key : tags) {
				RelativePos pos = new RelativePos(key);
				String name = compound.getString(key);
				nameByPos.put(pos, name);
				posByName.put(name, pos);
			}
		}
	}

	/*
	 * RF HANDLING
	 */
	@LuaFunction
	public Object getEnergyInfoByPos(int x, int y, int z, ForgeDirection side) throws Exception {
		RelativePos sourcePos = new RelativePos(x, y, z);
		return getEnergyInfo(sourcePos, side);
	}

	@LuaFunction
	public Object getEnergyInfo(String source, ForgeDirection side) throws Exception {
		if (!posByName.containsKey(source)) {
			throw new Exception("Invalid source '" + source + "'. Not specified, use setName(x,y,z,name) first.");
		}

		RelativePos sourcePos = posByName.get(source);
		return getEnergyInfo(sourcePos, side);
	}

	private Object getEnergyInfo(RelativePos sourcePos, ForgeDirection side) throws Exception {
		TileEntity sourceTile = worldObj.getTileEntity(xCoord + sourcePos.x, yCoord + sourcePos.y, zCoord + sourcePos.z);
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		if (sourceTile instanceof IEnergyProvider) {
			result.put("stored", ((IEnergyProvider) sourceTile).getEnergyStored(side));
			result.put("capacity", ((IEnergyProvider) sourceTile).getMaxEnergyStored(side));
		}
		if (sourceTile instanceof IEnergyReceiver) {
			result.put("stored", ((IEnergyReceiver) sourceTile).getEnergyStored(side));
			result.put("capacity", ((IEnergyReceiver) sourceTile).getMaxEnergyStored(side));
		}

		if (result.size() > 0) {
			return result;
		}

		// TODO: If side is empty, return details about all sides.

		throw new Exception("Source tile is no energy handler.");
	}

	@LuaFunction
	public int transferEnergy(String source, ForgeDirection sourceSide, String target, ForgeDirection targetSide, int amount) throws Exception {
		if (!posByName.containsKey(source)) {
			throw new Exception("Invalid source '" + source + "'. Not specified, use setName(x,y,z,name) first.");
		}

		if (!posByName.containsKey(target)) {
			throw new Exception("Invalid target '" + target + "'. Not specified, use setName(x,y,z,name) first.");
		}

		if (amount <= 0) {
			throw new Exception("At least 1 RF needs to be transferred.");
		}

		RelativePos sourcePos = posByName.get(source);
		TileEntity sourceTile = worldObj.getTileEntity(xCoord + sourcePos.x, yCoord + sourcePos.y, zCoord + sourcePos.z);
		if (!(sourceTile instanceof IEnergyProvider)) {
			throw new Exception("Source tile is no energy provider.");
		}

		RelativePos targetPos = posByName.get(target);
		TileEntity targetTile = worldObj.getTileEntity(xCoord + targetPos.x, yCoord + targetPos.y, zCoord + targetPos.z);
		if (!(targetTile instanceof IEnergyReceiver)) {
			throw new Exception("Target tile is no energy receiver.");
		}

		IEnergyProvider provider = (IEnergyProvider) sourceTile;
		IEnergyReceiver receiver = (IEnergyReceiver) targetTile;

		int extractedSim = provider.extractEnergy(sourceSide, amount, true);
		if (extractedSim > 0) {
			int receivedSim = receiver.receiveEnergy(targetSide, extractedSim, true);
			if (receivedSim > 0) {
				int extracted = provider.extractEnergy(sourceSide, receivedSim, false);
				int received = receiver.receiveEnergy(targetSide, extracted, false);

				sourceTile.markDirty();
				targetTile.markDirty();

				return received;
			}
		}

		return 0;
	}

	/*
	 * FLUID HANDLING
	 */
	private Object getTankInfo(RelativePos sourcePos, ForgeDirection side) throws Exception {
		TileEntity sourceTile = worldObj.getTileEntity(xCoord + sourcePos.x, yCoord + sourcePos.y, zCoord + sourcePos.z);
		if (!(sourceTile instanceof IFluidHandler)) {
			throw new Exception("Source tile is no fluid handler.");
		}

		IFluidHandler fluidHandler = (IFluidHandler) sourceTile;

		// TODO: side should be an optional parameter
		if (side == ForgeDirection.UNKNOWN) {
			HashMap<ForgeDirection, FluidTankInfo[]> map = new HashMap<ForgeDirection, FluidTankInfo[]>();
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				FluidTankInfo info[] = fluidHandler.getTankInfo(dir);
				map.put(dir, info);
			}
			return map;
		} else {
			FluidTankInfo info[] = fluidHandler.getTankInfo(side);
			return info;
		}
	}

	@LuaFunction
	public Object getTankInfo(String source, ForgeDirection side) throws Exception {
		if (!posByName.containsKey(source)) {
			throw new Exception("Invalid source '" + source + "'. Not specified, use setName(x,y,z,name) first.");
		}

		RelativePos sourcePos = posByName.get(source);
		return getTankInfo(sourcePos, side);
	}

	@LuaFunction
	public Object getTankInfoByPos(int x, int y, int z, ForgeDirection side) throws Exception {
		RelativePos sourcePos = new RelativePos(x, y, z);
		return getTankInfo(sourcePos, side);
	}

	@LuaFunction
	public int transferFluid(String source, ForgeDirection sourceSide, int sourceTankId, String target, ForgeDirection targetSide, int amount) throws Exception {
		sourceTankId--;

		if (!posByName.containsKey(source)) {
			throw new Exception("Invalid source '" + source + "'. Not specified, use setName(x,y,z,name) first.");
		}

		if (!posByName.containsKey(target)) {
			throw new Exception("Invalid target '" + target + "'. Not specified, use setName(x,y,z,name) first.");
		}

		if (amount <= 0) {
			throw new Exception("At least 1mb needs to be transferred.");
		}

		RelativePos sourcePos = posByName.get(source);
		TileEntity sourceTile = worldObj.getTileEntity(xCoord + sourcePos.x, yCoord + sourcePos.y, zCoord + sourcePos.z);
		if (!(sourceTile instanceof IFluidHandler)) {
			throw new Exception("Source tile is no fluid handler.");
		}

		RelativePos targetPos = posByName.get(target);
		TileEntity targetTile = worldObj.getTileEntity(xCoord + targetPos.x, yCoord + targetPos.y, zCoord + targetPos.z);
		if (!(targetTile instanceof IFluidHandler)) {
			throw new Exception("Target tile is no fluid handler.");
		}

		IFluidHandler sourceHandler = (IFluidHandler) sourceTile;
		FluidTankInfo sourceTankInfos[] = sourceHandler.getTankInfo(sourceSide);
		if (sourceTankInfos.length == 0) {
			return 0;
		}

		if (sourceTankId >= sourceTankInfos.length) {
			throw new Exception("Source tile has no tank " + sourceTankId + " on side '" + sourceSide + "'.");
		}

		FluidTankInfo sourceTankInfo = sourceTankInfos[sourceTankId];
		if (sourceTankInfo.fluid == null) {
			return 0;
		}

		FluidStack sourceFluid = sourceTankInfo.fluid.copy();

		if (sourceHandler.canDrain(sourceSide, sourceFluid.getFluid())) {
			sourceFluid.amount = amount;
			FluidStack sourceDrainageSim = sourceHandler.drain(sourceSide, sourceFluid, false);
			if (sourceDrainageSim.amount > 0) {
				// Drain simulation successful

				IFluidHandler targetHandler = (IFluidHandler) targetTile;
				if (targetHandler.canFill(targetSide, sourceFluid.getFluid())) {
					int filledSim = targetHandler.fill(targetSide, sourceDrainageSim, false);
					if (filledSim > 0) {
						// Fill simulation successful

						FluidStack sourceDrainage = sourceHandler.drain(sourceSide, sourceFluid, true);
						int filled = targetHandler.fill(targetSide, sourceDrainage, true);
						sourceTile.markDirty();
						targetTile.markDirty();
						return filled;
					}
				}
			}
		}

		return 0;
	}

	/*
	 *  ITEM HANDLING
	 */
	@LuaFunction
	public int transferItem(String source, int sourceSlot, String target, int quantity, int targetSlot) throws Exception {
		// Offset lua indexes
		sourceSlot--;
		targetSlot--;

		if (!posByName.containsKey(source)) {
			throw new Exception("Invalid source '" + source + "'. Not specified, use setName(x,y,z,name) first.");
		}

		if (!posByName.containsKey(target)) {
			throw new Exception("Invalid target '" + target + "'. Not specified, use setName(x,y,z,name) first.");
		}

		// TODO: quantity should be an optional argument 
		if (quantity <= 0) {
			throw new Exception("At least 1 item needs to be transferred.");
		}

		RelativePos sourcePos = posByName.get(source);
		TileEntity sourceTile = worldObj.getTileEntity(xCoord + sourcePos.x, yCoord + sourcePos.y, zCoord + sourcePos.z);
		if (!(sourceTile instanceof IInventory)) {
			throw new Exception("Source tile is not an inventory.");
		}

		RelativePos targetPos = posByName.get(target);
		TileEntity targetTile = worldObj.getTileEntity(xCoord + targetPos.x, yCoord + targetPos.y, zCoord + targetPos.z);

		if (!(targetTile instanceof IInventory)) {
			throw new Exception("Target tile is not an inventory.");
		}

		IInventory sourceInventory = (IInventory) sourceTile;
		if (!isSlotAccessibleFromAnySide(sourceInventory, sourceSlot)) {
			throw new Exception("Slot " + sourceSlot + " in source tile '" + source + "' can not be accessed.");
		}

		ItemStack sourceStack = sourceInventory.getStackInSlot(sourceSlot);
		if (sourceStack == null || sourceStack.stackSize == 0) {
			return 0;
		}

		IInventory targetInventory = (IInventory) targetTile;
		if (targetSlot == -1) {
			for (int slot = 0; slot < targetInventory.getSizeInventory(); slot++) {
				if (!isSlotAccessibleFromAnySide(targetInventory, slot)) {
					continue;
				}

				if (!canItemMergeIntoSlot(targetInventory, slot, sourceStack)) {
					continue;
				}

				targetSlot = slot;
				break;
			}
		}

		if (targetSlot == -1) {
			return 0;
		}

		if (!isSlotAccessibleFromAnySide(targetInventory, targetSlot)) {
			throw new Exception("Slot " + targetSlot + " in target tile '" + target + "' can not be accessed.");
		}

		int max = Math.min(sourceStack.getMaxStackSize(), targetInventory.getInventoryStackLimit());
		max = Math.min(max, quantity);

		ItemStack targetStack = targetInventory.getStackInSlot(targetSlot);
		if (targetStack == null) {
			// Target slot is empty

			// Oh dear. There are some mods using FMLCommonHandler.instance().getEffectiveSide().isServer()
			// to determine whether something is run on the server-side. isServer() is using the name of the
			// current thread to determine this. But ComputerCraft is running it's lua stuff in a separate
			// thread - running only on the server-side.
			// This makes some mods think their code is being executed on the client - from server-side code.
			// Ouch! In cases where we notice real problems with this we have to rename the current thread.
			//  * AE2s AppEngInternalInventory does not run its own onChangeInventory() making interaction
			//    with most AE2 stuff impossible.
			Thread thr = Thread.currentThread();
			String oldThreadName = thr.getName();
			thr.setName("Server thread");

			int transferred = 0;
			if (sourceStack.stackSize <= max) {
				// There is enough room for the whole stack				
				targetInventory.setInventorySlotContents(targetSlot, sourceStack.copy());
				sourceInventory.setInventorySlotContents(sourceSlot, null);
				transferred = sourceStack.stackSize;
			} else {
				// Stack needs to be split				
				ItemStack copy = sourceStack.copy();
				int before = copy.stackSize;

				targetInventory.setInventorySlotContents(targetSlot, copy.splitStack(max));
				int after = copy.stackSize;
				sourceInventory.setInventorySlotContents(sourceSlot, copy);
				transferred = before - after;
			}

			targetInventory.markDirty();
			sourceInventory.markDirty();
			thr.setName(oldThreadName);

			return transferred;
		}

		if (!canItemMergeIntoSlot(targetInventory, targetSlot, sourceStack)) {
			return 0;
		}

		// At this point we know the targetStack contains the same kind of item
		max = targetInventory.getInventoryStackLimit();
		if (sourceStack.stackSize <= max) {
			int amount = Math.min(sourceStack.stackSize, max - targetStack.stackSize);

			if (amount > 0) {
				Thread thr = Thread.currentThread();
				String oldThreadName = thr.getName();
				thr.setName("Server thread");
				ItemStack sourceCopy = sourceStack.copy();
				ItemStack targetCopy = targetStack.copy();

				targetCopy.stackSize += amount;
				sourceCopy.stackSize -= amount;
				if (sourceCopy.stackSize < 1) {
					sourceCopy = null;
				}
				targetInventory.setInventorySlotContents(targetSlot, targetCopy);
				sourceInventory.setInventorySlotContents(sourceSlot, sourceCopy);

				targetInventory.markDirty();
				sourceInventory.markDirty();
				thr.setName(oldThreadName);
				return amount;
			}
		}

		return 0;
	}

	private static boolean canItemMergeIntoSlot(IInventory inventory, int slot, ItemStack stack) {
		ItemStack target = inventory.getStackInSlot(slot);
		return (target == null || (target.getItem() == stack.getItem() &&
				target.isStackable() &&
				inventory.isItemValidForSlot(slot, stack) &&
				target.stackSize < target.getMaxStackSize() &&
				target.stackSize < inventory.getInventoryStackLimit() &&
				(!target.getHasSubtypes() || target.getItemDamage() == stack.getItemDamage()) && ItemStack.areItemStackTagsEqual(target, stack)));
	}

	private static boolean isSlotAccessibleFromAnySide(IInventory inventory, int slot) {
		if (inventory instanceof ISidedInventory) {
			ISidedInventory sidedInventory = (ISidedInventory) inventory;
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				int[] accessible = sidedInventory.getAccessibleSlotsFromSide(dir.ordinal());
				for (int i = 0; i < accessible.length; i++) {
					if (accessible[i] == slot) {
						return true;
					}
				}
			}

			return false;
		}

		return true;
	}

	@LuaFunction
	public int getInventorySize(String source) throws Exception {
		RelativePos sourcePos = posByName.get(source);
		TileEntity sourceTile = worldObj.getTileEntity(xCoord + sourcePos.x, yCoord + sourcePos.y, zCoord + sourcePos.z);
		if (!(sourceTile instanceof IInventory)) {
			throw new Exception("Source tile is not an inventory.");
		}

		IInventory sourceInventory = (IInventory) sourceTile;
		return sourceInventory.getSizeInventory();
	}

	private HashMap<Integer, AdvancedItemStack> getAllItems(RelativePos sourcePos) throws Exception {
		TileEntity sourceTile = worldObj.getTileEntity(xCoord + sourcePos.x, yCoord + sourcePos.y, zCoord + sourcePos.z);
		if (!(sourceTile instanceof IInventory)) {
			throw new Exception("Source tile is not an inventory.");
		}

		HashMap<Integer, AdvancedItemStack> map = new HashMap<Integer, AdvancedItemStack>();
		IInventory sourceInventory = (IInventory) sourceTile;
		for (int slot = 0; slot < sourceInventory.getSizeInventory(); slot++) {
			if (!isSlotAccessibleFromAnySide(sourceInventory, slot)) {
				continue;
			}

			ItemStack content = sourceInventory.getStackInSlot(slot);
			if (content == null || content.stackSize == 0) {
				continue;
			}

			AdvancedItemStack stack = new AdvancedItemStack(content.copy());
			map.put(slot + 1, stack);
		}

		return map;
	}

	@LuaFunction
	public HashMap<Integer, AdvancedItemStack> getAllItemsByPos(int x, int y, int z) throws Exception {
		RelativePos sourcePos = new RelativePos(x, y, z);
		return getAllItems(sourcePos);
	}

	@LuaFunction
	public HashMap<Integer, AdvancedItemStack> getAllItems(String source) throws Exception {
		if (!posByName.containsKey(source)) {
			throw new Exception("Invalid source '" + source + "'. Not specified, use setName(x,y,z,name) first.");
		}

		RelativePos sourcePos = posByName.get(source);
		return getAllItems(sourcePos);
	}

	/*
	 * Target/Name handling
	 */

	@LuaFunction
	public void resetNames() {
		nameByPos.clear();
		posByName.clear();
		this.bClientsNeedUpdate = true;
		this.markDirty();
	}

	@LuaFunction
	public void setName(int x, int y, int z, String name) {
		RelativePos pos = new RelativePos(x, y, z);
		String prevName = nameByPos.get(pos);
		posByName.remove(prevName);

		if (name.length() == 0) {
			// Wants to delete an entry
			if (nameByPos.containsKey(pos)) {
				// Entry exists
				nameByPos.remove(pos);
				posByName.remove(prevName);
			}
		} else {
			if (posByName.containsKey(name)) {
				// Entry already exists.

				// Remove the old mapping first
				RelativePos originalPos = posByName.get(name);
				nameByPos.remove(originalPos);
			}

			// Then overwrite the name mapping
			posByName.put(name, pos);
			nameByPos.put(pos, name);
		}
		this.bClientsNeedUpdate = true;
		this.markDirty();
	}

	@LuaFunction
	public String getName(int x, int y, int z) {
		RelativePos pos = new RelativePos(x, y, z);
		if (!nameByPos.containsKey(pos)) {
			return "";
		}

		return nameByPos.get(pos);
	}

	@LuaFunction
	public Map<String, Integer> getTarget(String name) {
		if (!posByName.containsKey(name)) {
			return null;
		}

		RelativePos pos = posByName.get(name);

		HashMap<String, Integer> posMap = new HashMap<String, Integer>();
		posMap.put("x", pos.x);
		posMap.put("y", pos.y);
		posMap.put("z", pos.z);

		return posMap;
	}

	private ArrayList<String> getTypes(RelativePos pos) throws Exception {
		TileEntity tile = worldObj.getTileEntity(xCoord + pos.x, yCoord + pos.y, zCoord + pos.z);
		if (tile == null) {
			throw new Exception("Not a valid target!");
		}

		ArrayList<String> types = new ArrayList<String>();
		if (tile instanceof IInventory) {
			types.add("item");
		}
		if (tile instanceof IFluidHandler) {
			types.add("fluid");
		}
		if (tile instanceof IEnergyProvider) {
			types.add("energy-provider");
		}
		if (tile instanceof IEnergyReceiver) {
			types.add("energy-receiver");
		}

		return types;
	}

	private HashMap<String, Boolean> getCapabilities(RelativePos sourcePos) throws Exception {
		ArrayList<String> types = getTypes(sourcePos);
		HashMap<String, Boolean> result = new HashMap<String, Boolean>();
		for (String capability : types) {
			result.put(capability, true);
		}

		return result;
	}

	@LuaFunction
	public HashMap<String, Boolean> getCapabilities(String source) throws Exception {
		if (!posByName.containsKey(source)) {
			throw new Exception("Invalid source '" + source + "'. Not specified, use setName(x,y,z,name) first.");
		}

		RelativePos sourcePos = posByName.get(source);
		return getCapabilities(sourcePos);
	}

	@LuaFunction
	public HashMap<String, Boolean> getCapabilitiesByPos(int x, int y, int z) throws Exception {
		RelativePos sourcePos = new RelativePos(x, y, z);
		return getCapabilities(sourcePos);
	}

	@LuaFunction
	public ArrayList<Object> getTargets() {
		ArrayList<Object> list = new ArrayList<Object>();

		for (RelativePos pos : getInterestingTiles()) {
			TileEntity tile = worldObj.getTileEntity(xCoord + pos.x, yCoord + pos.y, zCoord + pos.z);

			ArrayList<String> types;
			try {
				types = getTypes(pos);
			} catch (Exception e) {
				// This should never occur, since getInterestingTiles already filtered
				// all RelativePos to only TileEntity's.
				continue;
			}

			if (types.size() == 0) {
				continue;
			}

			HashMap<String, Object> map = new HashMap<String, Object>();

			Block block = worldObj.getBlock(xCoord + pos.x, yCoord + pos.y, zCoord + pos.z);
			int meta = worldObj.getBlockMetadata(xCoord + pos.x, yCoord + pos.y, zCoord + pos.z);
			Item item = Item.getItemFromBlock(block);
			if (item == null) {
				map.put("Name", block.getLocalizedName());
			} else {
				ItemStack asStack = new ItemStack(item, 1, meta);
				map.put("Name", asStack.getDisplayName());
			}

			map.put("Block", Block.blockRegistry.getNameForObject(block));
			map.put("Meta", meta);
			String targetName = getName(pos.x, pos.y, pos.z);
			if (targetName.length() > 0) {
				map.put("Target", targetName);
			}

			map.put("Types", types);

			HashMap<String, Integer> posMap = new HashMap<String, Integer>();
			posMap.put("x", pos.x);
			posMap.put("y", pos.y);
			posMap.put("z", pos.z);

			map.put("Pos", posMap);
			list.add(map);
		}

		java.util.Collections.sort(list, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				String name1 = (String) ((HashMap<String, Object>) o1).get("Name");
				String name2 = (String) ((HashMap<String, Object>) o2).get("Name");
				return name1.compareToIgnoreCase(name2);
			}
		});

		return list;
	}

	// Recursor start
	private ArrayList<RelativePos> seenList;

	private ArrayList<RelativePos> getInterestingTiles() {
		ArrayList<RelativePos> tileList = new ArrayList<RelativePos>();
		seenList = new ArrayList<RelativePos>();
		addInterestingTiles(tileList, new RelativePos(0, 0, 0), ForgeDirection.UNKNOWN);
		return tileList;
	}

	// Recursive method
	private void addInterestingTiles(ArrayList<RelativePos> list, RelativePos pos, ForgeDirection ignoreSide) {
		// Skip all positions we've already looked at. Breaks recursion in closed loops.
		if (seenList.contains(pos)) {
			return;
		}
		seenList.add(pos);

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (dir == ignoreSide) {
				continue;
			}

			RelativePos newPos = new RelativePos(pos.x + dir.offsetX, pos.y + dir.offsetY, pos.z + dir.offsetZ);
			if (isCable(newPos)) {
				addInterestingTiles(list, newPos, dir.getOpposite());
			} else {
				TileEntity tile = worldObj.getTileEntity(xCoord + newPos.x, yCoord + newPos.y, zCoord + newPos.z);
				if (tile == null) {
					continue;
				}
				if (!list.contains(newPos)) {
					list.add(newPos);
				}
			}
		}
	}

	private boolean isCable(RelativePos pos) {
		Block b = worldObj.getBlock(xCoord + pos.x, yCoord + pos.y, zCoord + pos.z);
		return (b instanceof IFactoryCable);
	}
}
