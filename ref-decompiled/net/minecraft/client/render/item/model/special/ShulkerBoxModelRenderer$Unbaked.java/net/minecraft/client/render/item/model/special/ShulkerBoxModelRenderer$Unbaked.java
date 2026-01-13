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
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.item.model.special.ShulkerBoxModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public record ShulkerBoxModelRenderer.Unbaked(Identifier texture, float openness, Direction facing) implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<ShulkerBoxModelRenderer.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(ShulkerBoxModelRenderer.Unbaked::texture), (App)Codec.FLOAT.optionalFieldOf("openness", (Object)Float.valueOf(0.0f)).forGetter(ShulkerBoxModelRenderer.Unbaked::openness), (App)Direction.CODEC.optionalFieldOf("orientation", Direction.UP).forGetter(ShulkerBoxModelRenderer.Unbaked::facing)).apply((Applicative)instance, ShulkerBoxModelRenderer.Unbaked::new));

    public ShulkerBoxModelRenderer.Unbaked() {
        this(Identifier.ofVanilla("shulker"), 0.0f, Direction.UP);
    }

    public ShulkerBoxModelRenderer.Unbaked(DyeColor color) {
        this(TexturedRenderLayers.createShulkerId(color), 0.0f, Direction.UP);
    }

    public MapCodec<ShulkerBoxModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        return new ShulkerBoxModelRenderer(new ShulkerBoxBlockEntityRenderer(context), this.openness, this.facing, TexturedRenderLayers.SHULKER_SPRITE_MAPPER.map(this.texture));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ShulkerBoxModelRenderer.Unbaked.class, "texture;openness;orientation", "texture", "openness", "facing"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ShulkerBoxModelRenderer.Unbaked.class, "texture;openness;orientation", "texture", "openness", "facing"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ShulkerBoxModelRenderer.Unbaked.class, "texture;openness;orientation", "texture", "openness", "facing"}, this, object);
    }
}
