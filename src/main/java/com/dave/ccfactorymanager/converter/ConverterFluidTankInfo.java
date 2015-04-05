package com.dave.ccfactorymanager.converter;

import java.util.HashMap;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.theoriginalbit.framework.peripheral.converter.ITypeConverter;

import dan200.computercraft.api.lua.LuaException;

public class ConverterFluidTankInfo implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class<?> expected) throws LuaException {
		return null;
	}

	@Override
	public Object toLua(Object obj) throws LuaException {
		if (obj instanceof FluidTankInfo) {
			FluidTankInfo tank = (FluidTankInfo) obj;
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("capacity", tank.capacity);

			if (tank.fluid != null) {
				FluidStack fluidStack = tank.fluid;
				HashMap<String, Object> contents = new HashMap<String, Object>();

				contents.put("amount", fluidStack.amount);
				contents.put("id", fluidStack.fluidID);

				Fluid fluid = fluidStack.getFluid();
				if (fluid != null) {
					contents.put("name", fluid.getName());
					contents.put("rawName", fluid.getLocalizedName(fluidStack));
				}

				map.put("contents", contents);
			}

			return map;
		}
		return null;
	}

}
