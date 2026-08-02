package com.gamja.mippify.gui;

import com.gamja.mippify.Mippify;
import com.gamja.mippify.MippifyConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class GuiMippifyConfig extends Screen {
    private final Screen parentGui;
    private final HeaderAndFooterLayout layout;
    private final MippifyConfig config;

    private boolean anyChanged = false;

    public GuiMippifyConfig(Screen parentGui) {
        super(Component.translatable("mippify.gui.title"));
        this.parentGui = parentGui;
        layout = new HeaderAndFooterLayout(this);
        config = Mippify.config();
    }

    @Override
    public void init() {
        clearWidgets();
        setupScreen();
        layout.visitWidgets(this::addRenderableWidget);
        layout.arrangeElements();
    }

    private int calcX(int i) {
        return width / 2 - 155 + i % 2 * 160;
    }

    private int calcY(int i) {
        return height / 6 + 21 * (i / 2) - 12;
    }

    private void setupScreen() {
        layout.addTitleHeader(getTitle(), getFont());
        layout.addToFooter(new Button.Builder(CommonComponents.GUI_DONE, (_) -> onClose()).size(200, 20).build());

        setupConfigs();
    }

    @Override
    public void onClose() {
        super.onClose();
        minecraft.gui.setScreen(parentGui);
        config.saveAll();

        if (anyChanged) {
            minecraft.reloadResourcePacks();
        }
    }

    private void setupConfigs() {
        int i = 0;
        for (ConfigHandler.Action action : ConfigHandler.Action.values()) {
            int x = calcX(i);
            int y = calcY(i);
            int w = 150;
            int h = 20;

            Component message = ConfigHandler.getMessage(action);
            if (action.type() == 0) {
                addRenderableWidget(new Button.Builder(message, (button) -> {
                    ConfigHandler.updateConfig(action);
                    button.setMessage(ConfigHandler.getMessage(action));

                    anyChanged = true;
                }).bounds(x, y, w, h).build());

            } else if (action.type() == 1) {
                addRenderableWidget(new SliderWidget(x, y, w, h, message, ConfigHandler.doubleConfig(action), (slider) -> {
                    ConfigHandler.updateConfig(action, slider.getValue());
                    slider.setMessage(ConfigHandler.getMessage(action));

                    anyChanged = true;
                }));

            }

            ++i;
        }
    }
}
