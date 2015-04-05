package com.dave.ccfactorymanager.converter;

import net.minecraftforge.common.util.ForgeDirection;

import com.theoriginalbit.framework.peripheral.converter.ITypeConverter;

public class ConverterForgeDirection implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class<?> expected) {
		if (!(expected == ForgeDirection.class && obj instanceof String)) {
			return null;
		}

		String in = (String) obj;
		try {
			ForgeDirection dir = ForgeDirection.valueOf(in.toUpperCase());
			return dir;
		} catch (Exception e) {
			return ForgeDirection.UNKNOWN;
		}

	}

	@Override
	public Object toLua(Object obj) {
		if (!(obj instanceof ForgeDirection)) {
			return null;
		}

		ForgeDirection dir = (ForgeDirection) obj;
		return dir.toString().toLowerCase();
	}

}
