/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class ShulkerBoxModelRenderer
implements SimpleSpecialModelRenderer {
    private final ShulkerBoxBlockEntityRenderer blockEntityRenderer;
    private final float openness;
    private final Direction facing;
    private final SpriteIdentifier textureId;

    public ShulkerBoxModelRenderer(ShulkerBoxBlockEntityRenderer blockEntityRenderer, float openness, Direction facing, SpriteIdentifier textureId) {
        this.blockEntityRenderer = blockEntityRenderer;
        this.openness = openness;
        this.facing = facing;
        this.textureId = textureId;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        this.blockEntityRenderer.render(matrices, queue, light, overlay, this.facing, this.openness, null, this.textureId, i);
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        this.blockEntityRenderer.collectVertices(this.facing, this.openness, consumer);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(Identifier texture, float openness, Direction facing) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture), (App)Codec.FLOAT.optionalFieldOf("openness", (Object)Float.valueOf(0.0f)).forGetter(Unbaked::openness), (App)Direction.CODEC.optionalFieldOf("orientation", Direction.UP).forGetter(Unbaked::facing)).apply((Applicative)instance, Unbaked::new));

        public Unbaked() {
            this(Identifier.ofVanilla("shulker"), 0.0f, Direction.UP);
        }

        public Unbaked(DyeColor color) {
            this(TexturedRenderLayers.createShulkerId(color), 0.0f, Direction.UP);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            return new ShulkerBoxModelRenderer(new ShulkerBoxBlockEntityRenderer(context), this.openness, this.facing, TexturedRenderLayers.SHULKER_SPRITE_MAPPER.map(this.texture));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Unbaked.class, "texture;openness;orientation", "texture", "openness", "facing"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Unbaked.class, "texture;openness;orientation", "texture", "openness", "facing"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Unbaked.class, "texture;openness;orientation", "texture", "openness", "facing"}, this, object);
        }
    }
}
