package com.gamja.mippify.gui;

import com.gamja.mippify.Mippify;
import com.gamja.mippify.MippifyConfig;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ConfigHandler {
    public enum Action {
        ENABLE_MOD("mippify.enableMod", 0),
        SMOOTHING("mippify.smoothing", 0),
        FAST_EDGE("mippify.fastEdge", 0)
        ;

        private final String key;
        private final int type;

        Action(String key, int type) {
            this.key = key;
            this.type = type;
        }

        public String key() {
            return key;
        }

        public int type() {
            return type;
        }
    }

    public static Component getMessage(Action action) {
        MutableComponent message = Component.translatable(action.key()).append(": ");
        if (action.type() == 0) {
            message.append(boolConfig(action) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
        } else if (action.type() == 1) {
            message.append(doubleConfigDisplay(action));
        }

        return message;
    }

    public static void updateConfig(Action action) {
        MippifyConfig config = Mippify.config();
        switch (action) {
            case ENABLE_MOD -> config.enableMod = !config.enableMod;
            case SMOOTHING -> config.smoothing = !config.smoothing;
            case FAST_EDGE -> config.fastEdge = !config.fastEdge;
        }
    }

    public static void updateConfig(Action action, double value) {
        MippifyConfig config = Mippify.config();
        switch (action) {
//            case A -> config.a = value;
        }
    }

    public static boolean boolConfig(Action action) {
        MippifyConfig config = Mippify.config();
        return switch (action) {
            case ENABLE_MOD -> config.enableMod;
            case SMOOTHING -> config.smoothing;
            case FAST_EDGE -> config.fastEdge;
            default -> false;
        };
    }

    public static double doubleConfig(Action action) {
        MippifyConfig config = Mippify.config();
        return switch (action) {
//            case A -> config.a;
            default -> -1.0;
        };
    }

    public static Component doubleConfigDisplay(Action action) {
        MippifyConfig config = Mippify.config();
        return switch (action) {
//            case A -> Component.literal(config.a.toString());
            default -> Component.literal("unknown");
        };
    }
}
