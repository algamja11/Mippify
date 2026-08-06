package com.gamja.mippify.gui;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class ConfigWidgetSlider extends AbstractSliderButton implements IConfigWidget {
    private final ConfigHandler.Action action;
    private final Consumer<ConfigWidgetSlider> onUpdate;

    public ConfigWidgetSlider(ConfigHandler.Action action, int x, int y, int width, int height, Component message, double initialValue, Consumer<ConfigWidgetSlider> onUpdate) {
        super(x, y, width, height, message, initialValue);
        this.action = action;
        this.onUpdate = onUpdate;
    }

    @Override
    public ConfigHandler.Action getAction() {
        return action;
    }

    @Override
    protected void updateMessage() {
    }

    @Override
    protected void applyValue() {
        onUpdate.accept(this);
    }

    public double getValue() {
        return value;
    }
}
