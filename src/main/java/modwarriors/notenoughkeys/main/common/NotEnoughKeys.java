package modwarriors.notenoughkeys.main.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by TheTemportalist on 4/10/2016.
 *
 * @author TheTemportalist
 */
@Mod(modid = NotEnoughKeys.MOD_ID, name = NotEnoughKeys.MOD_NAME,
		version = NotEnoughKeys.MOD_VERSION,
		dependencies = ""
)
public class NotEnoughKeys {

	static final String MOD_ID = "notenoughkeys";
	static final String MOD_NAME = "NotEnoughKeys";
	static final String MOD_VERSION = "0.0.0";
	private static final String proxyClient = "modwarriors.notenoughkeys.main.client.ProxyClient";
	private static final String proxyServer = "modwarriors.notenoughkeys.main.client.ProxyServer";

	@SidedProxy(clientSide = NotEnoughKeys.proxyClient, serverSide = NotEnoughKeys.proxyServer)
	private static ProxyCommon proxy;

	//@Mod.Instance(value = NotEnoughKeys.MOD_ID)
	//NotEnoughKeys instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		NotEnoughKeys.proxy.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

}
