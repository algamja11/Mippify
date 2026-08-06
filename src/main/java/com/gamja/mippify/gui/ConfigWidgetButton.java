package com.gamja.mippify.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ConfigWidgetButton extends Button.Plain implements IConfigWidget {
    private final ConfigHandler.Action action;

    public ConfigWidgetButton(ConfigHandler.Action action, int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.action = action;
    }

    @Override
    public ConfigHandler.Action getAction() {
        return action;
    }
}
