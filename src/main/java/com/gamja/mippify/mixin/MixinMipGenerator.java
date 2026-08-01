package com.gamja.mippify.mixin;

import com.gamja.mippify.Mippify;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.Transparency;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MipmapGenerator.class)
public class MixinMipGenerator {
    @Shadow
    private static float alphaTestCoverage(NativeImage image, float alphaRef, float alphaScale) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow private static void scaleAlphaToCoverage(NativeImage image, float desiredCoverage, float alphaRef, float alphaCutoffBias) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(method = "scaleAlphaToCoverage", at = @At("HEAD"), cancellable = true)
    private static void mippify$scaleAlphaToCoverage(NativeImage image, float desiredCoverage, float alphaRef, float alphaCutoffBias, CallbackInfo ci) {
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

    @Inject(method = "generateMipLevels", at = @At("HEAD"), cancellable = true)
    private static void mippify$generateMipLevels(Identifier name, NativeImage[] currentMips, int newMipLevel, MipmapStrategy mipmapStrategy, float alphaCutoffBias, Transparency transparency, CallbackInfoReturnable<NativeImage[]> cir) {
        if (!Mippify.config().fixMipGeneration) {
            return;
        }
        int mipBlurSize = Mippify.config().mipBlurSize;

        if (mipmapStrategy == MipmapStrategy.AUTO) {
            mipmapStrategy = transparency.hasTransparent() ? MipmapStrategy.CUTOUT : MipmapStrategy.MEAN;
        }

        if (currentMips.length == 1 && !name.getPath().startsWith("item/")) {
            if (mipmapStrategy != MipmapStrategy.CUTOUT && mipmapStrategy != MipmapStrategy.STRICT_CUTOUT) {
                if (mipmapStrategy == MipmapStrategy.DARK_CUTOUT) {
                    TextureUtil.fillEmptyAreasWithDarkColor(currentMips[0]);
                }
            } else {
                TextureUtil.solidify(currentMips[0]);
            }
        }

        if (newMipLevel + 1 <= currentMips.length) {
            cir.setReturnValue(currentMips);
        } else {
            NativeImage[] result = new NativeImage[newMipLevel + 1];
            result[0] = currentMips[0];
            boolean isCutoutMip = mipmapStrategy == MipmapStrategy.CUTOUT || mipmapStrategy == MipmapStrategy.STRICT_CUTOUT || mipmapStrategy == MipmapStrategy.DARK_CUTOUT;
            float cutoutRef = mipmapStrategy == MipmapStrategy.STRICT_CUTOUT ? 0.3F : 0.5F;
            float originalCoverage = isCutoutMip ? alphaTestCoverage(currentMips[0], cutoutRef, 1.0F) : 0.0F;

            for (int level = 1; level <= newMipLevel; ++level) {
                if (level < currentMips.length) {
                    result[level] = currentMips[level];
                } else {
                    NativeImage lastData = result[level - 1];
                    NativeImage data = new NativeImage(lastData.getWidth() >> 1, lastData.getHeight() >> 1, false);
                    int width = data.getWidth();
                    int height = data.getHeight();

                    for (int x = 0; x < width; ++x) {
                        for (int y = 0; y < height; ++y) {
                            int color = mippify$boxBlur(lastData, x * 2, y * 2, mipBlurSize);
                            data.setPixel(x, y, color);
                        }
                    }

                    result[level] = data;
                }

                if (isCutoutMip) {
                    scaleAlphaToCoverage(result[level], originalCoverage, cutoutRef, alphaCutoffBias);
                }
            }

            cir.setReturnValue(result);
        }
    }

    @Unique
    private static int mippify$getPixel(NativeImage image, int x, int y) {
        return image.getPixel(Math.floorMod(x, image.getWidth()), Math.floorMod(y, image.getHeight()));
    }

    @Unique
    private static int mippify$boxBlur(NativeImage image, int x, int y, int blurSize) {
        int sum = 0;
        int a = 0;
        int r = 0;
        int g = 0;
        int b = 0;

        for (int i = -blurSize; i < blurSize; ++i) {
            for (int j = -blurSize; j < blurSize; ++j) {
                int color = mippify$getPixel(image, x + i, y + j);
                a += ARGB.alpha(color);
                r += ARGB.red(color);
                g += ARGB.green(color);
                b += ARGB.blue(color);

                ++sum;
            }
        }

        return ARGB.color(a / sum, r / sum, g / sum, b / sum);
    }
}