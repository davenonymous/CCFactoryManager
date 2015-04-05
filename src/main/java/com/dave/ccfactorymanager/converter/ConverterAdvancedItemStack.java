package com.dave.ccfactorymanager.converter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import org.apache.commons.codec.binary.Hex;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import com.dave.ccfactorymanager.util.LogHelper;
import com.theoriginalbit.framework.peripheral.converter.ITypeConverter;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import dan200.computercraft.api.lua.LuaException;

public class ConverterAdvancedItemStack implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class<?> expected) throws LuaException {
		return null;
	}

	@Override
	public Object toLua(Object obj) throws LuaException {
		if (obj instanceof AdvancedItemStack) {
			ItemStack stack = ((AdvancedItemStack) obj).stack;

			HashMap<String, Object> map = new HashMap<String, Object>();

			UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(stack.getItem());

			if (id != null) {
				map.put("id", id.toString());
				map.put("name", id.name);
				map.put("mod_id", id.modId);
			} else {
				map.put("id", "?");
				map.put("name", "?");
				map.put("mod_id", "?");
			}

			map.put("display_name", getNameForItemStack(stack));
			map.put("raw_name", getRawNameForStack(stack));

			int oreIds[] = OreDictionary.getOreIDs(stack);
			if (oreIds.length > 0) {
				HashMap<String, Boolean> oreList = new HashMap<String, Boolean>();
				for (int oreId : oreIds) {
					oreList.put(OreDictionary.getOreName(oreId), true);
				}
				map.put("ore_dict", oreList);
			}

			map.put("qty", stack.stackSize);
			map.put("dmg", stack.getItemDamage());
			map.put("max_dmg", stack.getItem().getMaxDamage());
			map.put("max_size", stack.getMaxStackSize());

			if (stack.hasTagCompound()) {
				String hash = getNBTHash(stack.getTagCompound());
				map.put("nbt_id", hash);
			}

			// TODO: Add api-able item details

			return map;
		}

		return null;
	}

	public static String getNBTHash(NBTTagCompound tag) {
		String result = "00000000000000000000000000000000";
		try {
			byte[] compressed = CompressedStreamTools.compress(tag);
			//result = Base64.encode(compressed);
			byte[] digest = MessageDigest.getInstance("MD5").digest(compressed);
			result = new String(Hex.encodeHex(digest));
		} catch (IOException e) {
			LogHelper.fatal("Could not compress NBT Tag using CompressedStreamTools. Stack comparison with NBT data will not work!");
		} catch (NoSuchAlgorithmException e) {
			LogHelper.fatal("MD5 digest algorithm does not exist. Stack comparison with NBT data will not work!");
		}
		return result;
	}

	private static String getNameForItemStack(ItemStack is) {
		try {
			return is.getDisplayName();
		} catch (Exception e) {}

		try {
			return is.getUnlocalizedName();
		} catch (Exception e2) {}

		return "unknown";
	}

	private static String getRawNameForStack(ItemStack is) {
		try {
			return is.getUnlocalizedName().toLowerCase();
		} catch (Exception e) {}

		return "unknown";
	}
}
