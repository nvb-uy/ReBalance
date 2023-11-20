package elocindev.rebalance;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elocindev.necronomicon.api.config.v1.NecConfigAPI;
import elocindev.rebalance.config.ReBalanceConfig;

public class ReBalance implements ModInitializer {
	public static final String MODID = "rebalance";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static ReBalanceConfig CONFIG = ReBalanceConfig.INSTANCE;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success)
		-> {
			NecConfigAPI.registerConfig(ReBalanceConfig.class);
			CONFIG = ReBalanceConfig.INSTANCE;
		});

		NecConfigAPI.registerConfig(ReBalanceConfig.class);
		CONFIG = ReBalanceConfig.INSTANCE;
	}
}