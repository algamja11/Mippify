package com.gamja.mippify.mixin;

import com.gamja.mippify.Lang;
import com.gamja.mippify.MippifyUtils;
import com.gamja.mippify.gui.GuiMippifyConfig;
import net.minecraft.client.GameLoadCookie;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "tick", at = @At("RETURN"))
    private void mippify$tick(CallbackInfo ci) {
        if (MippifyUtils.getScreen() == null && MippifyUtils.getKeyDown(GLFW.GLFW_KEY_M)){
            MippifyUtils.setScreen(new GuiMippifyConfig(null));
        }
    }

    @Inject(method = "onResourceLoadFinished", at = @At("RETURN"))
    private void mippify$onResourceLoadFinished(GameLoadCookie loadCookie, CallbackInfo ci) {
        Lang.reloadLanguages();
    }
}
