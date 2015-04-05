package com.dave.ccfactorymanager;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;

import com.dave.ccfactorymanager.converter.AdvancedItemStack;
import com.dave.ccfactorymanager.converter.ConverterAdvancedItemStack;
import com.dave.ccfactorymanager.converter.ConverterFluidTankInfo;
import com.dave.ccfactorymanager.converter.ConverterForgeDirection;
import com.dave.ccfactorymanager.handler.GuiHandler;
import com.dave.ccfactorymanager.handler.PacketHandler;
import com.dave.ccfactorymanager.handler.TickHandler;
import com.dave.ccfactorymanager.init.ModBlocks;
import com.dave.ccfactorymanager.init.ModItems;
import com.dave.ccfactorymanager.init.Recipes;
import com.dave.ccfactorymanager.proxy.IProxy;
import com.dave.ccfactorymanager.reference.Reference;
import com.theoriginalbit.framework.peripheral.LuaType;
import com.theoriginalbit.framework.peripheral.PeripheralProvider;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import dan200.computercraft.api.ComputerCraftAPI;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, name = Reference.MOD_NAME, dependencies = "required-after:ComputerCraft")
public class CCFactoryManager {
	@Mod.Instance(Reference.MOD_ID)
	public static CCFactoryManager instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static IProxy proxy;

	public static SimpleNetworkWrapper networkWrapper;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ModBlocks.init();
		ModItems.init();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler.init();
		Recipes.init();

		proxy.registerTileEntities();
		proxy.registerRenderers();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

		FMLCommonHandler.instance().bus().register(new TickHandler());

		LuaType.registerTypeConverter(new ConverterFluidTankInfo());
		LuaType.registerTypeConverter(new ConverterForgeDirection());
		LuaType.registerTypeConverter(new ConverterAdvancedItemStack());

		LuaType.registerClassToNameMapping(FluidTankInfo.class, "fluid_tank");
		LuaType.registerClassToNameMapping(ForgeDirection.class, "direction");
		LuaType.registerClassToNameMapping(AdvancedItemStack.class, "item_with_nbt");

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
	}
}
