package com.gamja.mippify.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MipmapGenerator.class)
public class MixinMipGenerator {

    @Inject(method = "scaleAlphaToCoverage", at = @At("HEAD"), cancellable = true)
    private static void scaleAlphaToCoverage(NativeImage image, float desiredCoverage, float alphaRef, float alphaCutoffBias, CallbackInfo ci) {
        int coverage = (int)(255.0F * desiredCoverage);

        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int pixel = image.getPixel(x, y);
                int a = Math.min(ARGB.alpha(pixel) + coverage, 255);
                int r = ARGB.red(pixel);
                int g = ARGB.green(pixel);
                int b = ARGB.blue(pixel);

                image.setPixel(x, y, ARGB.color(a, r, g, b));
            }
        }

        ci.cancel();
    }
}