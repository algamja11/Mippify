package com.gamja.mippify;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

// TODO: implement settings
public class GuiMippifyConfig extends Screen {
    private final Screen parentGui;
    private final HeaderAndFooterLayout layout;

    public GuiMippifyConfig(Screen parentGui) {
        super(Component.translatable("mippify.gui.title"));
        this.parentGui = parentGui;
        layout = new HeaderAndFooterLayout(this);
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

        layout.addToContents(new StringWidget(Component.literal("..."), getFont()));
    }

    @Override
    public void onClose() {
        super.onClose();
        minecraft.gui.setScreen(parentGui);
    }
}
