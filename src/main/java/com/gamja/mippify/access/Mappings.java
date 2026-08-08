package com.gamja.mippify.access;

import com.gamja.mippify.Mippify;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.ClientBrandRetriever;

public class Mappings {
    private static final HashMap<String, String> MAPPINGS = new HashMap<>();
    private static boolean initialized = false;

    private Mappings() {
    }

    public static String get(String key) {
        if (!initialized) {
            init();
        }
        return MAPPINGS.get(key);
    }

    private static void init() {
        String loader;
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            loader = "devenv";
        } else {
            loader = ClientBrandRetriever.getClientModName();
        }
        String path = "/assets/mippify/" + loader + ".mappings";

        try (InputStream is = Mappings.class.getResourceAsStream(path)) {
            if (is != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8.newDecoder()));
                String curLine;
                while ((curLine = in.readLine()) != null) {
                    String[] kv = curLine.split(":", 2);
                    if (kv.length >= 2) {
                        MAPPINGS.put(kv[0], kv[1]);
                    }
                }
                in.close();
            }
        } catch (Exception e) {
            Mippify.LOGGER.error("Failed to load mappings!", e);
        }

        initialized = true;
    }
}
