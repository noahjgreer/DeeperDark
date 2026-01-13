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
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ConditionItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.property.PropertyTester;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DataCache;
import net.minecraft.registry.ContextSwapper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ConditionItemModel.Unbaked(BooleanProperty property, ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) implements ItemModel.Unbaked
{
    public static final MapCodec<ConditionItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BooleanProperties.CODEC.forGetter(ConditionItemModel.Unbaked::property), (App)ItemModelTypes.CODEC.fieldOf("on_true").forGetter(ConditionItemModel.Unbaked::onTrue), (App)ItemModelTypes.CODEC.fieldOf("on_false").forGetter(ConditionItemModel.Unbaked::onFalse)).apply((Applicative)instance, ConditionItemModel.Unbaked::new));

    public MapCodec<ConditionItemModel.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public ItemModel bake(ItemModel.BakeContext context) {
        return new ConditionItemModel(this.makeWorldIndependentProperty(this.property, context.contextSwapper()), this.onTrue.bake(context), this.onFalse.bake(context));
    }

    private PropertyTester makeWorldIndependentProperty(BooleanProperty property, @Nullable ContextSwapper contextSwapper) {
        if (contextSwapper == null) {
            return property;
        }
        DataCache<ClientWorld, PropertyTester> dataCache = new DataCache<ClientWorld, PropertyTester>(world -> ConditionItemModel.Unbaked.swapContext(property, contextSwapper, world));
        return (stack, world, entity, seed, transformationMode) -> {
            BooleanProperty propertyTester = world == null ? property : (PropertyTester)dataCache.compute(world);
            return propertyTester.test(stack, world, entity, seed, transformationMode);
        };
    }

    private static <T extends BooleanProperty> T swapContext(T value, ContextSwapper contextSwapper, ClientWorld world) {
        return contextSwapper.swapContext(value.getCodec().codec(), value, world.getRegistryManager()).result().orElse(value);
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        this.onTrue.resolve(resolver);
        this.onFalse.resolve(resolver);
    }
}
