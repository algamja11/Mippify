package com.gamja.mippify.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;

/**
 * Edited from {@link com.mojang.blaze3d.pipeline.RenderPipeline}
 */

public class MutablePipeline extends RenderPipeline {
    private static final Identifier EMPTY_ID = Identifier.parse("");
    private Identifier vertexShader;
    private Identifier fragmentShader;
    private ShaderDefines shaderDefines;
    private List<BindGroupLayout> bindGroupLayouts;
    private DepthStencilState depthStencilState;
    private PolygonMode polygonMode;
    private boolean cull;
    private ColorTargetState[] colorTargetStates;
    private VertexFormat[] vertexFormatPerBuffer;
    private PrimitiveTopology primitiveTopology;

    public MutablePipeline(Identifier location) {
        this(location, EMPTY_ID, EMPTY_ID, ShaderDefines.EMPTY, List.of(), new ColorTargetState[1], DepthStencilState.DEFAULT, PolygonMode.FILL, false, new VertexFormat[16], PrimitiveTopology.LINES, -1);
    }

    public MutablePipeline(Identifier location, Identifier vertexShader, Identifier fragmentShader, ShaderDefines shaderDefines, List<BindGroupLayout> bindGroupLayouts, ColorTargetState[] colorTargetStates, DepthStencilState depthStencilState, PolygonMode polygonMode, boolean cull, VertexFormat[] vertexFormatPerBuffer, PrimitiveTopology primitiveTopology, int sortKey) {
        super(location, vertexShader, fragmentShader, shaderDefines, bindGroupLayouts, colorTargetStates, depthStencilState, polygonMode, cull, vertexFormatPerBuffer, primitiveTopology, sortKey);
    }

    public void set(RenderPipeline pipeline) {
        set(pipeline.getVertexShader(), pipeline.getFragmentShader(), pipeline.getShaderDefines(), pipeline.getBindGroupLayouts(), pipeline.getColorTargetStates(), pipeline.getDepthStencilState(), pipeline.getPolygonMode(), pipeline.isCull(), pipeline.getVertexFormatBindings(), pipeline.getPrimitiveTopology());
    }

    public void set(Identifier vertexShader, Identifier fragmentShader, ShaderDefines shaderDefines, List<BindGroupLayout> bindGroupLayouts, ColorTargetState[] colorTargetStates, DepthStencilState depthStencilState, PolygonMode polygonMode, boolean cull, VertexFormat[] vertexFormatPerBuffer, PrimitiveTopology primitiveTopology) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.shaderDefines = shaderDefines;
        this.bindGroupLayouts = bindGroupLayouts;
        this.depthStencilState = depthStencilState;
        this.polygonMode = polygonMode;
        this.cull = cull;
        this.colorTargetStates = colorTargetStates;
        this.primitiveTopology = primitiveTopology;
        this.vertexFormatPerBuffer = new VertexFormat[16];
        System.arraycopy(vertexFormatPerBuffer, 0, this.vertexFormatPerBuffer, 0, this.vertexFormatPerBuffer.length);
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
    public ColorTargetState[] getColorTargetStates() {
        return this.colorTargetStates;
    }

    @Override
    public ColorTargetState getColorTargetState() {
        return this.colorTargetStates[0];
    }

    @Override
    public DepthStencilState getDepthStencilState() {
        return this.depthStencilState;
    }

    @Override
    public VertexFormat[] getVertexFormatBindings() {
        return this.vertexFormatPerBuffer;
    }

    @Override
    public VertexFormat getVertexFormatBinding(final int bindingIndex) {
        return this.vertexFormatPerBuffer[bindingIndex];
    }

    @Override
    public PrimitiveTopology getPrimitiveTopology() {
        return this.primitiveTopology;
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
    public List<BindGroupLayout> getBindGroupLayouts() {
        return this.bindGroupLayouts;
    }

    @Override
    public boolean wantsDepthTexture() {
        return this.depthStencilState != null;
    }
}
