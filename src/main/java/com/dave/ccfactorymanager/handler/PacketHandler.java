package com.dave.ccfactorymanager.handler;

import com.dave.ccfactorymanager.network.MessageFactoryNames;
import com.dave.ccfactorymanager.network.MessageNameChange;
import com.dave.ccfactorymanager.reference.Reference;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());

	public static void init() {
		INSTANCE.registerMessage(MessageFactoryNames.class, MessageFactoryNames.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(MessageNameChange.class, MessageNameChange.class, 2, Side.SERVER);
	}
}
