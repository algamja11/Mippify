package com.gamja.mippify.render;

import com.gamja.mippify.Mippify;
import com.gamja.mippify.ReflectionUtils;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.Transparency;
import java.lang.reflect.Method;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

@SuppressWarnings("ConstantConditions")
public class MipGenerator {
    private static final Method ALPHA_TEST_COVERAGE;
    private static final Method SCALE_ALPHA_TO_COVERAGE;
    private static final Method DARKENED_ALPHA_BLEND;

    private static final String ITEM_PREFIX;
    private static final float ALPHA_CUTOFF;
    private static final float STRICT_ALPHA_CUTOFF;

    private MipGenerator() {
    }

    static {
        Class<?> mipGenerator = MipmapGenerator.class;

        ALPHA_TEST_COVERAGE = ReflectionUtils.tryGetMethod(mipGenerator, "alphaTestCoverage", NativeImage.class, float.class, float.class);
        SCALE_ALPHA_TO_COVERAGE = ReflectionUtils.tryGetMethod(mipGenerator, "scaleAlphaToCoverage", NativeImage.class, float.class, float.class, float.class);
        DARKENED_ALPHA_BLEND = ReflectionUtils.tryGetMethod(mipGenerator, "darkenedAlphaBlend", int.class, int.class, int.class, int.class);

        ITEM_PREFIX = (String) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(mipGenerator, "ITEM_PREFIX"), null);
        ALPHA_CUTOFF = (float) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(mipGenerator, "ALPHA_CUTOFF"), null);
        STRICT_ALPHA_CUTOFF = (float) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(mipGenerator, "STRICT_ALPHA_CUTOFF"), null);
    }

    private static float alphaTestCoverage(final NativeImage image, final float alphaRef, final float alphaScale) {
        return (float) ReflectionUtils.tryInvoke(ALPHA_TEST_COVERAGE, null, image, alphaRef, alphaScale);
    }

    private static void scaleAlphaToCoverage(final NativeImage image, final float desiredCoverage, final float alphaRef, final float alphaCutoffBias) {
        ReflectionUtils.tryInvoke(SCALE_ALPHA_TO_COVERAGE, null, image, desiredCoverage, alphaRef, alphaCutoffBias);
    }

    public static NativeImage[] generateMipLevels(Identifier name, NativeImage[] currentMips, int newMipLevel, MipmapStrategy mipmapStrategy, float alphaCutoffBias, Transparency transparency) {
        if (mipmapStrategy == MipmapStrategy.AUTO) {
            mipmapStrategy = transparency.hasTransparent() ? MipmapStrategy.CUTOUT : MipmapStrategy.MEAN;
        }

        boolean isCutoutMip = mipmapStrategy == MipmapStrategy.CUTOUT || mipmapStrategy == MipmapStrategy.STRICT_CUTOUT || mipmapStrategy == MipmapStrategy.DARK_CUTOUT;

        if (currentMips.length == 1 && !name.getPath().startsWith(ITEM_PREFIX) && isCutoutMip) {
            if (mipmapStrategy == MipmapStrategy.DARK_CUTOUT) {
                TextureUtil.fillEmptyAreasWithDarkColor(currentMips[0]);
            } else {
                if (Mippify.config().fastEdge) {
                    fixTransparentColor(currentMips[0]);
                } else {
                    TextureUtil.solidify(currentMips[0]);
                }
            }
        }

        if (newMipLevel + 1 <= currentMips.length) {
            return currentMips;
        } else {
            NativeImage[] result = new NativeImage[newMipLevel + 1];
            result[0] = currentMips[0];
            float cutoutRef = mipmapStrategy == MipmapStrategy.STRICT_CUTOUT ? STRICT_ALPHA_CUTOFF : ALPHA_CUTOFF;
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
                            int color1 = lastData.getPixel(x * 2 + 0, y * 2 + 0);
                            int color2 = lastData.getPixel(x * 2 + 1, y * 2 + 0);
                            int color3 = lastData.getPixel(x * 2 + 0, y * 2 + 1);
                            int color4 = lastData.getPixel(x * 2 + 1, y * 2 + 1);

                            int color;
                            if (Mippify.config().smoothing) {
                                color = alphaBlend(color1, color2, color3, color4);
                            } else {
                                if (mipmapStrategy == MipmapStrategy.DARK_CUTOUT) {
                                    color = darkenedAlphaBlend(color1, color2, color3, color4);
                                } else {
                                    color = ARGB.meanLinear(color1, color2, color3, color4);
                                }
                            }

                            data.setPixel(x, y, color);
                        }
                    }

                    result[level] = data;
                }

                if (isCutoutMip && !Mippify.config().smoothing) {
                    scaleAlphaToCoverage(result[level], originalCoverage, cutoutRef, alphaCutoffBias);
                }
            }

            return result;
        }
    }

    private static int darkenedAlphaBlend(final int color1, final int color2, final int color3, final int color4) {
        return (int) ReflectionUtils.tryInvoke(DARKENED_ALPHA_BLEND, null, color1, color2, color3, color4);
    }

    private static int alphaBlend(int c1, int c2, int c3, int c4) {
        int cx1 = alphaBlend(c1, c2);
        int cx2 = alphaBlend(c3, c4);
        return alphaBlend(cx1, cx2);
    }

    private static int alphaBlend(int c1, int c2) {
        int a1 = ARGB.alpha(c1);
        int a2 = ARGB.alpha(c2);
        int a = (a1 + a2) / 2;
        if (a1 == 0 && a2 == 0) {
            a1 = 1;
            a2 = 1;
        } else {
            if (a1 == 0) {
                c1 = c2;
                a = a2;
            }
            if (a2 == 0) {
                c2 = c1;
                a = a1;
            }
        }
        int r1 = ARGB.red(c1) * a1;
        int g1 = ARGB.green(c1) * a1;
        int b1 = ARGB.blue(c1) * a1;
        int r2 = ARGB.red(c2) * a2;
        int g2 = ARGB.green(c2) * a2;
        int b2 = ARGB.blue(c2) * a2;
        int r = (r1 + r2) / (a1 + a2);
        int g = (g1 + g2) / (a1 + a2);
        int b = (b1 + b2) / (a1 + a2);

        return ARGB.color(a, r, g, b);
    }

    public static void fixTransparentColor(NativeImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        long pass = 0L;
        long r = 0L;
        long g = 0L;
        long b = 0L;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int pixel = image.getPixel(x, y);
                if (ARGB.alpha(pixel) != 0) {
                    r += ARGB.red(pixel);
                    g += ARGB.green(pixel);
                    b += ARGB.blue(pixel);
                    ++pass;
                }
            }
        }

        if (pass > 0) {
            int color = ARGB.color(0, (int)(r / pass), (int)(g / pass), (int)(b / pass));
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    int pixel = image.getPixel(x, y);
                    if (ARGB.alpha(pixel) == 0) {
                        image.setPixel(x, y, color);
                    }
                }
            }
        }
    }
}
