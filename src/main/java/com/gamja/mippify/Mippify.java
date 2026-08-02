package com.gamja.mippify;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mippify implements ClientModInitializer {
	public static final String MOD_ID = "mippify";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static MippifyConfig config;
    private static MippifyMipGenerator mipGenerator;

	@Override
	public void onInitializeClient() {
        config = new MippifyConfig();
        config.loadAll();
        mipGenerator = new MippifyMipGenerator();
	}

    public static MippifyConfig config() {
        return config;
    }

    public static MippifyMipGenerator mipGenerator() {
        return mipGenerator;
    }
}
