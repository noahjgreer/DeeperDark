/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jspecify.annotations.Nullable;

public static interface AttributeModifiersComponent.Display {
    public static final Codec<AttributeModifiersComponent.Display> CODEC = Type.CODEC.dispatch("type", AttributeModifiersComponent.Display::getType, type -> type.codec);
    public static final PacketCodec<RegistryByteBuf, AttributeModifiersComponent.Display> PACKET_CODEC = Type.PACKET_CODEC.cast().dispatch(AttributeModifiersComponent.Display::getType, Type::getPacketCodec);

    public static AttributeModifiersComponent.Display getDefault() {
        return Default.INSTANCE;
    }

    public static AttributeModifiersComponent.Display getHidden() {
        return Hidden.INSTANCE;
    }

    public static AttributeModifiersComponent.Display createOverride(Text text) {
        return new Override(text);
    }

    public Type getType();

    public void addTooltip(Consumer<Text> var1, @Nullable PlayerEntity var2, RegistryEntry<EntityAttribute> var3, EntityAttributeModifier var4);

    public record Default() implements AttributeModifiersComponent.Display
    {
        static final Default INSTANCE = new Default();
        static final MapCodec<Default> CODEC = MapCodec.unit((Object)INSTANCE);
        static final PacketCodec<RegistryByteBuf, Default> PACKET_CODEC = PacketCodec.unit(INSTANCE);

        @java.lang.Override
        public Type getType() {
            return Type.DEFAULT;
        }

        @java.lang.Override
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

    public record Hidden() implements AttributeModifiersComponent.Display
    {
        static final Hidden INSTANCE = new Hidden();
        static final MapCodec<Hidden> CODEC = MapCodec.unit((Object)INSTANCE);
        static final PacketCodec<RegistryByteBuf, Hidden> PACKET_CODEC = PacketCodec.unit(INSTANCE);

        @java.lang.Override
        public Type getType() {
            return Type.HIDDEN;
        }

        @java.lang.Override
        public void addTooltip(Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
        }
    }

    public record Override(Text value) implements AttributeModifiersComponent.Display
    {
        static final MapCodec<Override> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)TextCodecs.CODEC.fieldOf("value").forGetter(Override::value)).apply((Applicative)instance, Override::new));
        static final PacketCodec<RegistryByteBuf, Override> PACKET_CODEC = PacketCodec.tuple(TextCodecs.REGISTRY_PACKET_CODEC, Override::value, Override::new);

        @java.lang.Override
        public Type getType() {
            return Type.OVERRIDE;
        }

        @java.lang.Override
        public void addTooltip(Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
            textConsumer.accept(this.value);
        }

        @java.lang.Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Override.class, "component", "value"}, this);
        }

        @java.lang.Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Override.class, "component", "value"}, this);
        }

        @java.lang.Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Override.class, "component", "value"}, this, object);
        }
    }

    public static final class Type
    extends Enum<Type>
    implements StringIdentifiable {
        public static final /* enum */ Type DEFAULT = new Type("default", 0, Default.CODEC, Default.PACKET_CODEC);
        public static final /* enum */ Type HIDDEN = new Type("hidden", 1, Hidden.CODEC, Hidden.PACKET_CODEC);
        public static final /* enum */ Type OVERRIDE = new Type("override", 2, Override.CODEC, Override.PACKET_CODEC);
        static final Codec<Type> CODEC;
        private static final IntFunction<Type> INDEX_MAPPER;
        static final PacketCodec<ByteBuf, Type> PACKET_CODEC;
        private final String id;
        private final int index;
        final MapCodec<? extends AttributeModifiersComponent.Display> codec;
        private final PacketCodec<RegistryByteBuf, ? extends AttributeModifiersComponent.Display> packetCodec;
        private static final /* synthetic */ Type[] field_59749;

        public static Type[] values() {
            return (Type[])field_59749.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(String id, int index, MapCodec<? extends AttributeModifiersComponent.Display> codec, PacketCodec<RegistryByteBuf, ? extends AttributeModifiersComponent.Display> packetCodec) {
            this.id = id;
            this.index = index;
            this.codec = codec;
            this.packetCodec = packetCodec;
        }

        @java.lang.Override
        public String asString() {
            return this.id;
        }

        private int getIndex() {
            return this.index;
        }

        private PacketCodec<RegistryByteBuf, ? extends AttributeModifiersComponent.Display> getPacketCodec() {
            return this.packetCodec;
        }

        private static /* synthetic */ Type[] method_70738() {
            return new Type[]{DEFAULT, HIDDEN, OVERRIDE};
        }

        static {
            field_59749 = Type.method_70738();
            CODEC = StringIdentifiable.createCodec(Type::values);
            INDEX_MAPPER = ValueLists.createIndexToValueFunction(Type::getIndex, Type.values(), ValueLists.OutOfBoundsHandling.ZERO);
            PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, Type::getIndex);
        }
    }
}
