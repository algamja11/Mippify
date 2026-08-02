package com.gamja.mippify.gui;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class SliderWidget extends AbstractSliderButton {
    private final Consumer<SliderWidget> onUpdate;

    public SliderWidget(int x, int y, int width, int height, Component message, double initialValue, Consumer<SliderWidget> onUpdate) {
        super(x, y, width, height, message, initialValue);
        this.onUpdate = onUpdate;
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
