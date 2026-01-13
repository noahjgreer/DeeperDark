/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.model.ChestBlockModel
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.model.special.ChestModelRenderer
 *  net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.util.Identifier
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.item.model.special;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class ChestModelRenderer
implements SimpleSpecialModelRenderer {
    public static final Identifier CHRISTMAS_ID = Identifier.ofVanilla((String)"christmas");
    public static final Identifier NORMAL_ID = Identifier.ofVanilla((String)"normal");
    public static final Identifier TRAPPED_ID = Identifier.ofVanilla((String)"trapped");
    public static final Identifier ENDER_ID = Identifier.ofVanilla((String)"ender");
    public static final Identifier COPPER_ID = Identifier.ofVanilla((String)"copper");
    public static final Identifier EXPOSED_COPPER_ID = Identifier.ofVanilla((String)"copper_exposed");
    public static final Identifier WEATHERED_COPPER_ID = Identifier.ofVanilla((String)"copper_weathered");
    public static final Identifier OXIDIZED_COPPER_ID = Identifier.ofVanilla((String)"copper_oxidized");
    private final SpriteHolder spriteHolder;
    private final ChestBlockModel model;
    private final SpriteIdentifier textureId;
    private final float openness;

    public ChestModelRenderer(SpriteHolder spriteHolder, ChestBlockModel model, SpriteIdentifier textureId, float openness) {
        this.spriteHolder = spriteHolder;
        this.model = model;
        this.textureId = textureId;
        this.openness = openness;
    }

    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        queue.submitModel((Model)this.model, (Object)Float.valueOf(this.openness), matrices, this.textureId.getRenderLayer(RenderLayers::entitySolid), light, overlay, -1, this.spriteHolder.getSprite(this.textureId), i, null);
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        this.model.setAngles(Float.valueOf(this.openness));
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }
}

