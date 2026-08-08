package com.gamja.mippify.render;

import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;

/**
 * Edited from {@link com.mojang.blaze3d.pipeline.RenderPipeline}
 */

public class MutablePipeline extends RenderPipeline {
    private static Identifier EMPTY_ID = Identifier.parse("");
    private Identifier vertexShader;
    private Identifier fragmentShader;
    private ShaderDefines shaderDefines;
    private List<String> samplers;
    private List<UniformDescription> uniforms;
    private DepthStencilState depthStencilState;
    private PolygonMode polygonMode;
    private boolean cull;
    private ColorTargetState colorTargetState;
    private VertexFormat vertexFormat;
    private VertexFormat.Mode vertexFormatMode;

    public MutablePipeline(Identifier location) {
        this(location, EMPTY_ID, EMPTY_ID, ShaderDefines.EMPTY, List.of(), List.of(), ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, PolygonMode.FILL, false, DefaultVertexFormat.EMPTY, VertexFormat.Mode.LINES, -1);
    }

    public MutablePipeline(Identifier location, Identifier vertexShader, Identifier fragmentShader, ShaderDefines shaderDefines, List<String> samplers, List<UniformDescription> uniforms, ColorTargetState colorTargetState, DepthStencilState depthStencilState, PolygonMode polygonMode, boolean cull, VertexFormat vertexFormat, VertexFormat.Mode vertexFormatMode, int sortKey) {
        super(location, vertexShader, fragmentShader, shaderDefines, samplers, uniforms, colorTargetState, depthStencilState, polygonMode, cull, vertexFormat, vertexFormatMode, sortKey);
    }

    public void set(RenderPipeline pipeline) {
        set(pipeline.getVertexShader(), pipeline.getFragmentShader(), pipeline.getShaderDefines(), pipeline.getSamplers(), pipeline.getUniforms(), pipeline.getColorTargetState(), pipeline.getDepthStencilState(), pipeline.getPolygonMode(), pipeline.isCull(), pipeline.getVertexFormat(), pipeline.getVertexFormatMode());
    }

    public void set(Identifier vertexShader, Identifier fragmentShader, ShaderDefines shaderDefines, List<String> samplers, List<UniformDescription> uniforms, ColorTargetState colorTargetState,  DepthStencilState depthStencilState, PolygonMode polygonMode, boolean cull, VertexFormat vertexFormat, VertexFormat.Mode vertexFormatMode) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.shaderDefines = shaderDefines;
        this.samplers = samplers;
        this.uniforms = uniforms;
        this.depthStencilState = depthStencilState;
        this.polygonMode = polygonMode;
        this.cull = cull;
        this.colorTargetState = colorTargetState;
        this.vertexFormat = vertexFormat;
        this.vertexFormatMode = vertexFormatMode;
    }

    @Override
    public PolygonMode getPolygonMode() {
        return this.polygonMode;
    }

    @Override
    public boolean isCull() {
        return this.cull;
    }

    @Override
    public ColorTargetState getColorTargetState() {
        return this.colorTargetState;
    }

    @Override
    public DepthStencilState getDepthStencilState() {
        return this.depthStencilState;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    @Override
    public VertexFormat.Mode getVertexFormatMode() {
        return this.vertexFormatMode;
    }

    @Override
    public Identifier getVertexShader() {
        return this.vertexShader;
    }

    @Override
    public Identifier getFragmentShader() {
        return this.fragmentShader;
    }

    @Override
    public ShaderDefines getShaderDefines() {
        return this.shaderDefines;
    }

    @Override
    public List<String> getSamplers() {
        return this.samplers;
    }

    @Override
    public List<UniformDescription> getUniforms() {
        return this.uniforms;
    }

    @Override
    public boolean wantsDepthTexture() {
        return this.depthStencilState != null;
    }
}
