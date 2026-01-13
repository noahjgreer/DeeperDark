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
 *  net.minecraft.client.render.item.property.bool.BooleanProperty
 *  net.minecraft.client.render.item.property.bool.ComponentBooleanProperty
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.predicate.component.ComponentPredicate
 *  net.minecraft.predicate.component.ComponentPredicate$Typed
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.bool;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.component.ComponentPredicate;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ComponentBooleanProperty(ComponentPredicate.Typed<?> predicate) implements BooleanProperty
{
    private final ComponentPredicate.Typed<?> predicate;
    public static final MapCodec<ComponentBooleanProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ComponentPredicate.createCodec((String)"predicate").forGetter(ComponentBooleanProperty::predicate)).apply((Applicative)instance, ComponentBooleanProperty::new));

    public ComponentBooleanProperty(ComponentPredicate.Typed<?> predicate) {
        this.predicate = predicate;
    }

    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return this.predicate.predicate().test((ComponentsAccess)stack);
    }

    public MapCodec<ComponentBooleanProperty> getCodec() {
        return CODEC;
    }

    public ComponentPredicate.Typed<?> predicate() {
        return this.predicate;
    }
}

