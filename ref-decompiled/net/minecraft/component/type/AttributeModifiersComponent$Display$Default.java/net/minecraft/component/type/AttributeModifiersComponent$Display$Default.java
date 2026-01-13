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
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jspecify.annotations.Nullable;

public record AttributeModifiersComponent.Display.Default() implements AttributeModifiersComponent.Display
{
    static final AttributeModifiersComponent.Display.Default INSTANCE = new AttributeModifiersComponent.Display.Default();
    static final MapCodec<AttributeModifiersComponent.Display.Default> CODEC = MapCodec.unit((Object)INSTANCE);
    static final PacketCodec<RegistryByteBuf, AttributeModifiersComponent.Display.Default> PACKET_CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public AttributeModifiersComponent.Display.Type getType() {
        return AttributeModifiersComponent.Display.Type.DEFAULT;
    }

    @Override
    public void addTooltip(Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
        double d = modifier.value();
        boolean bl = false;
        if (player != null) {
            if (modifier.idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID)) {
                d += player.getAttributeBaseValue(EntityAttributes.ATTACK_DAMAGE);
                bl = true;
            } else if (modifier.idMatches(Item.BASE_ATTACK_SPEED_MODIFIER_ID)) {
                d += player.getAttributeBaseValue(EntityAttributes.ATTACK_SPEED);
                bl = true;
            }
        }
        double e = modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE || modifier.operation() == EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL ? d * 100.0 : (attribute.matches(EntityAttributes.KNOCKBACK_RESISTANCE) ? d * 10.0 : d);
        if (bl) {
            textConsumer.accept(ScreenTexts.space().append(Text.translatable("attribute.modifier.equals." + modifier.operation().getId(), DECIMAL_FORMAT.format(e), Text.translatable(attribute.value().getTranslationKey()))).formatted(Formatting.DARK_GREEN));
        } else if (d > 0.0) {
            textConsumer.accept(Text.translatable("attribute.modifier.plus." + modifier.operation().getId(), DECIMAL_FORMAT.format(e), Text.translatable(attribute.value().getTranslationKey())).formatted(attribute.value().getFormatting(true)));
        } else if (d < 0.0) {
            textConsumer.accept(Text.translatable("attribute.modifier.take." + modifier.operation().getId(), DECIMAL_FORMAT.format(-e), Text.translatable(attribute.value().getTranslationKey())).formatted(attribute.value().getFormatting(false)));
        }
    }
}
