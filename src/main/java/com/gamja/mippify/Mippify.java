package com.gamja.mippify;

import com.gamja.mippify.access.Mappings;
import com.gamja.mippify.access.ReflectionUtils;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mippify implements ClientModInitializer {
	public static final String MOD_ID = "mippify";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static MippifyConfig config;

	@Override
	public void onInitializeClient() {
        config = new MippifyConfig();
        config.loadAll();
	}

    public static MippifyConfig config() {
        return config;
    }

    public static boolean hasSodium() {
        return ReflectionUtils.hasClass(Mappings.get("mod.sodium.class.SodiumClientMod"));
    }
}
