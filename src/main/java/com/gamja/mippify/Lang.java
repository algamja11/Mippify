package com.gamja.mippify;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

public class Lang {
    private static final HashMap<String, String> FALLBACK = new HashMap<>();
    private static final HashMap<String, String> CURRENT = new HashMap<>();

    public static MutableComponent getComponent(String key, Object... args) {
        return Component.literal(get(key, args));
    }

    public static String get(String key, Object... args) {
        String translated;
        if (CURRENT.containsKey(key)) {
            translated = CURRENT.get(key);
        } else {
            translated = FALLBACK.getOrDefault(key, key);
        }

        return String.format(translated, args);
    }

    public static void reloadLanguages() {
        String locale = Minecraft.getInstance().getLanguageManager().getSelected();
        loadLanguages("en_us", FALLBACK);
        loadLanguages(locale, CURRENT);
    }

    private static void loadLanguages(String locale, HashMap<String, String> map) {
        map.clear();

        Identifier path = Identifier.fromNamespaceAndPath(Mippify.MOD_ID, "lang/" + locale + ".lang");
        try {
            Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(path);
            if (res.isPresent()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(res.get().open(), StandardCharsets.UTF_8.newDecoder()));
                String curLine;
                while ((curLine = in.readLine()) != null) {
                    String[] kv = curLine.split("=", 2);
                    if (kv.length >= 2) {
                        map.put(kv[0], fixFormatting(kv[1]));
                    }
                }
                in.close();
            }
        } catch (Exception e) {
            Mippify.LOGGER.error("Failed to load languages!", e);
        }
    }

    private static String fixFormatting(String string) {
        String fixed = string;
        fixed = fixed.replace("\\n", "\n");

        return fixed;
    }
}
