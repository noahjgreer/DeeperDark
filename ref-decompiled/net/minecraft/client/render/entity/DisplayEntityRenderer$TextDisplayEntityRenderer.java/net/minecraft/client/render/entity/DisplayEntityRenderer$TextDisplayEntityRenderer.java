/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.render.entity;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.DisplayEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TextDisplayEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public static class DisplayEntityRenderer.TextDisplayEntityRenderer
extends DisplayEntityRenderer<DisplayEntity.TextDisplayEntity, DisplayEntity.TextDisplayEntity.Data, TextDisplayEntityRenderState> {
    private final TextRenderer displayTextRenderer;

    protected DisplayEntityRenderer.TextDisplayEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.displayTextRenderer = context.getTextRenderer();
    }

    @Override
    public TextDisplayEntityRenderState createRenderState() {
        return new TextDisplayEntityRenderState();
    }

    @Override
    public void updateRenderState(DisplayEntity.TextDisplayEntity textDisplayEntity, TextDisplayEntityRenderState textDisplayEntityRenderState, float f) {
        super.updateRenderState(textDisplayEntity, textDisplayEntityRenderState, f);
        textDisplayEntityRenderState.data = textDisplayEntity.getData();
        textDisplayEntityRenderState.textLines = textDisplayEntity.splitLines(this::getLines);
    }

    private DisplayEntity.TextDisplayEntity.TextLines getLines(Text text, int width) {
        List<OrderedText> list = this.displayTextRenderer.wrapLines(text, width);
        ArrayList<DisplayEntity.TextDisplayEntity.TextLine> list2 = new ArrayList<DisplayEntity.TextDisplayEntity.TextLine>(list.size());
        int i = 0;
        for (OrderedText orderedText : list) {
            int j = this.displayTextRenderer.getWidth(orderedText);
            i = Math.max(i, j);
            list2.add(new DisplayEntity.TextDisplayEntity.TextLine(orderedText, j));
        }
        return new DisplayEntity.TextDisplayEntity.TextLines(list2, i);
    }

    @Override
    public void render(TextDisplayEntityRenderState textDisplayEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, float f) {
        int j;
        float g;
        DisplayEntity.TextDisplayEntity.Data data = textDisplayEntityRenderState.data;
        byte b = data.flags();
        boolean bl = (b & 2) != 0;
        boolean bl2 = (b & 4) != 0;
        boolean bl3 = (b & 1) != 0;
        DisplayEntity.TextDisplayEntity.TextAlignment textAlignment = DisplayEntity.TextDisplayEntity.getAlignment(b);
        byte c = (byte)data.textOpacity().lerp(f);
        if (bl2) {
            g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
            j = (int)(g * 255.0f) << 24;
        } else {
            j = data.backgroundColor().lerp(f);
        }
        g = 0.0f;
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        matrix4f.rotate((float)Math.PI, 0.0f, 1.0f, 0.0f);
        matrix4f.scale(-0.025f, -0.025f, -0.025f);
        DisplayEntity.TextDisplayEntity.TextLines textLines = textDisplayEntityRenderState.textLines;
        boolean k = true;
        int l = this.displayTextRenderer.fontHeight + 1;
        int m = textLines.width();
        int n = textLines.lines().size() * l - 1;
        matrix4f.translate(1.0f - (float)m / 2.0f, (float)(-n), 0.0f);
        if (j != 0) {
            orderedRenderCommandQueue.submitCustom(matrixStack, bl ? RenderLayers.textBackgroundSeeThrough() : RenderLayers.textBackground(), (matricesEntry, vertexConsumer) -> {
                vertexConsumer.vertex(matricesEntry, -1.0f, -1.0f, 0.0f).color(j).light(i);
                vertexConsumer.vertex(matricesEntry, -1.0f, (float)n, 0.0f).color(j).light(i);
                vertexConsumer.vertex(matricesEntry, (float)m, (float)n, 0.0f).color(j).light(i);
                vertexConsumer.vertex(matricesEntry, (float)m, -1.0f, 0.0f).color(j).light(i);
            });
        }
        RenderCommandQueue renderCommandQueue = orderedRenderCommandQueue.getBatchingQueue(j != 0 ? 1 : 0);
        for (DisplayEntity.TextDisplayEntity.TextLine textLine : textLines.lines()) {
            float h = switch (textAlignment) {
                default -> throw new MatchException(null, null);
                case DisplayEntity.TextDisplayEntity.TextAlignment.LEFT -> 0.0f;
                case DisplayEntity.TextDisplayEntity.TextAlignment.RIGHT -> m - textLine.width();
                case DisplayEntity.TextDisplayEntity.TextAlignment.CENTER -> (float)m / 2.0f - (float)textLine.width() / 2.0f;
            };
            renderCommandQueue.submitText(matrixStack, h, g, textLine.contents(), bl3, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.POLYGON_OFFSET, i, c << 24 | 0xFFFFFF, 0, 0);
            g += (float)l;
        }
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return super.getShadowRadius((DisplayEntityRenderState)state);
    }

    @Override
    protected /* synthetic */ int getBlockLight(Entity entity, BlockPos pos) {
        return super.getBlockLight((DisplayEntity)entity, pos);
    }

    @Override
    protected /* synthetic */ int getSkyLight(Entity entity, BlockPos pos) {
        return super.getSkyLight((DisplayEntity)entity, pos);
    }
}
