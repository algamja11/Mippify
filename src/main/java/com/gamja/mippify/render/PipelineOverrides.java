package com.gamja.mippify.render;

import com.gamja.mippify.Mippify;
import com.gamja.mippify.ReflectionUtils;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.HashMap;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

@SuppressWarnings("ConstantConditions")
public class PipelineOverrides {
    private static final HashMap<Identifier, RenderPipeline> PIPELINE_OVERRIDES = new HashMap<>();

    public static final RenderPipeline CUTOUT_BLOCK;
    public static final RenderPipeline CUTOUT_TERRAIN;
    public static final RenderPipeline TRANSLUCENT_TERRAIN;
    public static final RenderPipeline TRANSLUCENT_BLOCK;

    public static RenderPipeline get(Identifier path) {
        return PIPELINE_OVERRIDES.get(path);
    }

    public static RenderPipeline register(RenderPipeline override) {
        return register(Identifier.withDefaultNamespace(override.getLocation().getPath()), override);
    }

    public static RenderPipeline register(Identifier path, RenderPipeline override) {
        return PIPELINE_OVERRIDES.put(path, override);
    }

    static {
        Class<?> pipelines = RenderPipelines.class;

        RenderPipeline.Snippet blockSnippet = (RenderPipeline.Snippet) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(pipelines, "BLOCK_SNIPPET"), null);
        RenderPipeline.Snippet terrainSnippet = (RenderPipeline.Snippet) ReflectionUtils.tryGet(ReflectionUtils.tryGetField(pipelines, "TERRAIN_SNIPPET"), null);

        CUTOUT_BLOCK = register(
                RenderPipeline.builder(blockSnippet)
                        .withLocation(Identifier.fromNamespaceAndPath(Mippify.MOD_ID, "pipeline/cutout_block"))
                        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                        .build()
        );

        CUTOUT_TERRAIN = register(
                RenderPipeline.builder(terrainSnippet)
                        .withLocation(Identifier.fromNamespaceAndPath(Mippify.MOD_ID, "pipeline/cutout_terrain"))
                        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                        .build()
        );

        TRANSLUCENT_TERRAIN = register(
                RenderPipeline.builder(terrainSnippet)
                .withLocation(Identifier.fromNamespaceAndPath(Mippify.MOD_ID, "pipeline/translucent_terrain"))
                .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                .withShaderDefine("ALPHA_CUTOUT", 0.01F)
                .build()
        );

        TRANSLUCENT_BLOCK = register(
                RenderPipeline.builder(blockSnippet)
                        .withLocation(Identifier.fromNamespaceAndPath(Mippify.MOD_ID, "pipeline/translucent_block"))
                        .withShaderDefine("ALPHA_CUTOUT", 0.01F)
                        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                        .withDepthStencilState(DepthStencilState.DEFAULT)
                        .build()
        );
    }
}
