/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.component.type;

import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

public record AttributeModifiersComponent.Display.Hidden() implements AttributeModifiersComponent.Display
{
    static final AttributeModifiersComponent.Display.Hidden INSTANCE = new AttributeModifiersComponent.Display.Hidden();
    static final MapCodec<AttributeModifiersComponent.Display.Hidden> CODEC = MapCodec.unit((Object)INSTANCE);
    static final PacketCodec<RegistryByteBuf, AttributeModifiersComponent.Display.Hidden> PACKET_CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public AttributeModifiersComponent.Display.Type getType() {
        return AttributeModifiersComponent.Display.Type.HIDDEN;
    }

    @Override
    public void addTooltip(Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
    }
}
