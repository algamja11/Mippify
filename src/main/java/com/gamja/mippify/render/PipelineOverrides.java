package com.gamja.mippify.render;

import com.gamja.mippify.Mippify;
import com.gamja.mippify.access.Mappings;
import com.gamja.mippify.access.ReflectionUtils;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.HashMap;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

@SuppressWarnings("ConstantConditions")
public class PipelineOverrides {
    private static final HashMap<Identifier, MutablePipeline> PIPELINE_OVERRIDES = new HashMap<>();

    private static final Identifier EMPTY_ID = Identifier.parse("");
    private static final RenderPipeline.Snippet BLOCK_SNIPPET;
    private static final RenderPipeline.Snippet TERRAIN_SNIPPET;

    public static final MutablePipeline CUTOUT_BLOCK;
    public static final MutablePipeline CUTOUT_TERRAIN;
    public static final MutablePipeline TRANSLUCENT_TERRAIN;
    public static final MutablePipeline TRANSLUCENT_BLOCK;

    static {
        Class<?> pipelines = RenderPipelines.class;

        BLOCK_SNIPPET = (RenderPipeline.Snippet) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(pipelines, Mappings.get("field.RenderPipelines.BLOCK_SNIPPET")), null);
        TERRAIN_SNIPPET = (RenderPipeline.Snippet) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(pipelines, Mappings.get("field.RenderPipelines.TERRAIN_SNIPPET")), null);

        CUTOUT_BLOCK = register(new MutablePipeline(Identifier.fromNamespaceAndPath(Mippify.MOD_ID, "pipeline/cutout_block")));
        CUTOUT_TERRAIN = register(new MutablePipeline(Identifier.fromNamespaceAndPath(Mippify.MOD_ID, "pipeline/cutout_terrain")));
        TRANSLUCENT_TERRAIN = register(new MutablePipeline(Identifier.fromNamespaceAndPath(Mippify.MOD_ID, "pipeline/translucent_terrain")));
        TRANSLUCENT_BLOCK = register(new MutablePipeline(Identifier.fromNamespaceAndPath(Mippify.MOD_ID, "pipeline/translucent_block")));

        updatePipelines(true);
    }

    public static MutablePipeline get(Identifier path) {
        return PIPELINE_OVERRIDES.get(path);
    }

    public static MutablePipeline register(MutablePipeline override) {
        return register(Identifier.withDefaultNamespace(override.getLocation().getPath()), override);
    }

    public static MutablePipeline register(Identifier path, MutablePipeline override) {
        PIPELINE_OVERRIDES.put(path, override);
        return override;
    }

    public static void updatePipelines(boolean preload) {
        Mippify.LOGGER.info("*** Updating Pipelines ***");

        boolean enabled = !preload && Mippify.config().enableMod && Mippify.config().smoothing;
        float cutoutFactor = enabled ? 0.1F : 0.5F;
        float translucentFactor = enabled ? 0.01F : 0.1F;

        CUTOUT_BLOCK.set(
                RenderPipeline.builder(BLOCK_SNIPPET)
                        .withLocation(EMPTY_ID)
                        .withShaderDefine("ALPHA_CUTOUT", cutoutFactor)
                        .build()
        );

        CUTOUT_TERRAIN.set(
                RenderPipeline.builder(TERRAIN_SNIPPET)
                        .withLocation(EMPTY_ID)
                        .withShaderDefine("ALPHA_CUTOUT", cutoutFactor)
                        .build()
        );

        TRANSLUCENT_TERRAIN.set(
                RenderPipeline.builder(TERRAIN_SNIPPET)
                        .withLocation(EMPTY_ID)
                        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                        .withShaderDefine("ALPHA_CUTOUT", translucentFactor)
                        .build()
        );

        TRANSLUCENT_BLOCK.set(
                RenderPipeline.builder(BLOCK_SNIPPET)
                        .withLocation(EMPTY_ID)
                        .withShaderDefine("ALPHA_CUTOUT", translucentFactor)
                        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                        .withDepthStencilState(DepthStencilState.DEFAULT)
                        .build()
        );

        Mippify.LOGGER.info("*** Pipelines Updated ***");
    }
}
