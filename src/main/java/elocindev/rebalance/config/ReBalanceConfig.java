package elocindev.rebalance.config;

import elocindev.necronomicon.api.config.v1.NecConfigAPI;
import elocindev.necronomicon.config.NecConfig;

public class ReBalanceConfig {
    @NecConfig
    public static ReBalanceConfig INSTANCE;

    public static String getFile() {
        return NecConfigAPI.getFile("rebalance.json");
    }

    public boolean enable_dynamic_rebalance = true;
    public float reduction_amount = 15F;
    public float attack_speed_weight = 0.2F;
    public float health_minimum = 50F;
    public float health_threshold = 1F;

    public boolean enable_pvp_rebalance = false;
    public float pvp_damage_multiplier = 1.00F;

    public boolean enable_global_reduction = false;
    public float global_reduction_multiplier = 1.00F;
    public float global_reduction_start = 4.00F;

    public boolean enable_debug = false;
}
