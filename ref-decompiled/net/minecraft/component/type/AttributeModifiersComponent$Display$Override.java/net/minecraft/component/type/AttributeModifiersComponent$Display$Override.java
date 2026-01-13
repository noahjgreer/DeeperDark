/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Consumer;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jspecify.annotations.Nullable;

public record AttributeModifiersComponent.Display.Override(Text value) implements AttributeModifiersComponent.Display
{
    static final MapCodec<AttributeModifiersComponent.Display.Override> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)TextCodecs.CODEC.fieldOf("value").forGetter(AttributeModifiersComponent.Display.Override::value)).apply((Applicative)instance, AttributeModifiersComponent.Display.Override::new));
    static final PacketCodec<RegistryByteBuf, AttributeModifiersComponent.Display.Override> PACKET_CODEC = PacketCodec.tuple(TextCodecs.REGISTRY_PACKET_CODEC, AttributeModifiersComponent.Display.Override::value, AttributeModifiersComponent.Display.Override::new);

    @Override
    public AttributeModifiersComponent.Display.Type getType() {
        return AttributeModifiersComponent.Display.Type.OVERRIDE;
    }

    @Override
    public void addTooltip(Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
        textConsumer.accept(this.value);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{AttributeModifiersComponent.Display.Override.class, "component", "value"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AttributeModifiersComponent.Display.Override.class, "component", "value"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AttributeModifiersComponent.Display.Override.class, "component", "value"}, this, object);
    }
}
