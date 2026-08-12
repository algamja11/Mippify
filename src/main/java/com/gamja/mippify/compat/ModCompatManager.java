package com.gamja.mippify.compat;

import com.gamja.mippify.access.Mappings;
import com.gamja.mippify.access.ReflectionUtils;

public class ModCompatManager {
    private ModCompatManager() {
    }

    public static boolean hasSodium() {
        return ReflectionUtils.hasClass(Mappings.get("mod.sodium.class.SodiumClientMod"));
    }
}
