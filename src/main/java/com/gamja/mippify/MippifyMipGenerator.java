package com.gamja.mippify;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.Transparency;
import java.lang.reflect.Method;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

@SuppressWarnings("ConstantConditions")
public class MippifyMipGenerator {
    private final MippifyConfig config;

    private final Method alphaTestCoverage;
    private final Method scaleAlphaToCoverage;
    private final Method darkenedAlphaBlend;

    private final String ITEM_PREFIX;
    private final float ALPHA_CUTOFF;
    private final float STRICT_ALPHA_CUTOFF;

    public MippifyMipGenerator() {
        Class<?> mipGenerator = MipmapGenerator.class;

        alphaTestCoverage = ReflectionUtils.tryGetMethod(mipGenerator, "alphaTestCoverage", NativeImage.class, float.class, float.class);
        scaleAlphaToCoverage = ReflectionUtils.tryGetMethod(mipGenerator, "scaleAlphaToCoverage", NativeImage.class, float.class, float.class, float.class);
        darkenedAlphaBlend = ReflectionUtils.tryGetMethod(mipGenerator, "darkenedAlphaBlend", int.class, int.class, int.class, int.class);

        ITEM_PREFIX = (String) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(mipGenerator, "ITEM_PREFIX"), null);
        ALPHA_CUTOFF = (float) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(mipGenerator, "ALPHA_CUTOFF"), null);
        STRICT_ALPHA_CUTOFF = (float) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(mipGenerator, "STRICT_ALPHA_CUTOFF"), null);


        config = Mippify.config();
    }

    private float alphaTestCoverage(final NativeImage image, final float alphaRef, final float alphaScale) {
        return (float) ReflectionUtils.tryInvoke(alphaTestCoverage, null, image, alphaRef, alphaScale);
    }

    private void scaleAlphaToCoverage(final NativeImage image, final float desiredCoverage, final float alphaRef, final float alphaCutoffBias) {
        ReflectionUtils.tryInvoke(scaleAlphaToCoverage, null, image, desiredCoverage, alphaRef, alphaCutoffBias);
    }

    public NativeImage[] generateMipLevels(Identifier name, NativeImage[] currentMips, int newMipLevel, MipmapStrategy mipmapStrategy, float alphaCutoffBias, Transparency transparency) {
        if (mipmapStrategy == MipmapStrategy.AUTO) {
            mipmapStrategy = transparency.hasTransparent() ? MipmapStrategy.CUTOUT : MipmapStrategy.MEAN;
        }

        if (currentMips.length == 1 && !name.getPath().startsWith(ITEM_PREFIX)) {
            if (config.fastEdge) {
                if (mipmapStrategy == MipmapStrategy.CUTOUT || mipmapStrategy == MipmapStrategy.STRICT_CUTOUT) {
                    fixTransparentColor(currentMips[0]);
                }
            } else {
                if (mipmapStrategy != MipmapStrategy.CUTOUT && mipmapStrategy != MipmapStrategy.STRICT_CUTOUT) {
                    if (mipmapStrategy == MipmapStrategy.DARK_CUTOUT) {
                        TextureUtil.fillEmptyAreasWithDarkColor(currentMips[0]);
                    }
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
            boolean isCutoutMip = mipmapStrategy == MipmapStrategy.CUTOUT || mipmapStrategy == MipmapStrategy.STRICT_CUTOUT || mipmapStrategy == MipmapStrategy.DARK_CUTOUT;
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
                            if (config.smoothing) {
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

                if (isCutoutMip && !config.smoothing) {
                    scaleAlphaToCoverage(result[level], originalCoverage, cutoutRef, alphaCutoffBias);
                }
            }

            return result;
        }
    }

    private int darkenedAlphaBlend(final int color1, final int color2, final int color3, final int color4) {
        return (int) ReflectionUtils.tryInvoke(darkenedAlphaBlend, null, color1, color2, color3, color4);
    }

    private int alphaBlend(int c1, int c2, int c3, int c4) {
        int cx1 = alphaBlend(c1, c2);
        int cx2 = alphaBlend(c3, c4);
        return alphaBlend(cx1, cx2);
    }

    private int alphaBlend(int c1, int c2) {
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

    public void fixTransparentColor(NativeImage image) {
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
