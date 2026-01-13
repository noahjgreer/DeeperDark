/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CopperGolemStatueBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.client.render.block.entity.model.CopperGolemStatueModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.model.special.CopperGolemStatueModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.entity.passive.CopperGolemOxidationLevels;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record CopperGolemStatueModelRenderer.Unbaked(Identifier texture, CopperGolemStatueBlock.Pose pose) implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<CopperGolemStatueModelRenderer.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(CopperGolemStatueModelRenderer.Unbaked::texture), (App)CopperGolemStatueBlock.Pose.CODEC.fieldOf("pose").forGetter(CopperGolemStatueModelRenderer.Unbaked::pose)).apply((Applicative)instance, CopperGolemStatueModelRenderer.Unbaked::new));

    public CopperGolemStatueModelRenderer.Unbaked(Oxidizable.OxidationLevel oxidationLevel, CopperGolemStatueBlock.Pose pose) {
        this(CopperGolemOxidationLevels.get(oxidationLevel).texture(), pose);
    }

    public MapCodec<CopperGolemStatueModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        CopperGolemStatueModel copperGolemStatueModel = new CopperGolemStatueModel(context.entityModelSet().getModelPart(CopperGolemStatueModelRenderer.Unbaked.getLayer(this.pose)));
        return new CopperGolemStatueModelRenderer(copperGolemStatueModel, this.texture);
    }

    private static EntityModelLayer getLayer(CopperGolemStatueBlock.Pose pose) {
        return switch (pose) {
            default -> throw new MatchException(null, null);
            case CopperGolemStatueBlock.Pose.STANDING -> EntityModelLayers.COPPER_GOLEM;
            case CopperGolemStatueBlock.Pose.SITTING -> EntityModelLayers.COPPER_GOLEM_SITTING;
            case CopperGolemStatueBlock.Pose.STAR -> EntityModelLayers.COPPER_GOLEM_STAR;
            case CopperGolemStatueBlock.Pose.RUNNING -> EntityModelLayers.COPPER_GOLEM_RUNNING;
        };
    }
}
