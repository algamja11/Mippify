package com.gamja.mippify.mixin;

import com.gamja.mippify.Lang;
import com.gamja.mippify.gui.GuiMippifyConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.GameLoadCookie;
import net.minecraft.client.Minecraft;
import org.lwjgl.sdl.SDLScancode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "tick", at = @At("RETURN"))
    private void mippify$tick(CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gui.screen() == null && InputConstants.isKeyDown(SDLScancode.SDL_SCANCODE_M)) {
            minecraft.gui.setScreen(new GuiMippifyConfig(null));
        }
    }

    @Inject(method = "onResourceLoadFinished", at = @At("RETURN"))
    private void mippify$onResourceLoadFinished(GameLoadCookie loadCookie, CallbackInfo ci) {
        Lang.reloadLanguages();
    }
}
