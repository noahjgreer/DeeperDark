/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class TropicalFishEntity.Pattern
extends Enum<TropicalFishEntity.Pattern>
implements StringIdentifiable,
TooltipAppender {
    public static final /* enum */ TropicalFishEntity.Pattern KOB = new TropicalFishEntity.Pattern("kob", TropicalFishEntity.Size.SMALL, 0);
    public static final /* enum */ TropicalFishEntity.Pattern SUNSTREAK = new TropicalFishEntity.Pattern("sunstreak", TropicalFishEntity.Size.SMALL, 1);
    public static final /* enum */ TropicalFishEntity.Pattern SNOOPER = new TropicalFishEntity.Pattern("snooper", TropicalFishEntity.Size.SMALL, 2);
    public static final /* enum */ TropicalFishEntity.Pattern DASHER = new TropicalFishEntity.Pattern("dasher", TropicalFishEntity.Size.SMALL, 3);
    public static final /* enum */ TropicalFishEntity.Pattern BRINELY = new TropicalFishEntity.Pattern("brinely", TropicalFishEntity.Size.SMALL, 4);
    public static final /* enum */ TropicalFishEntity.Pattern SPOTTY = new TropicalFishEntity.Pattern("spotty", TropicalFishEntity.Size.SMALL, 5);
    public static final /* enum */ TropicalFishEntity.Pattern FLOPPER = new TropicalFishEntity.Pattern("flopper", TropicalFishEntity.Size.LARGE, 0);
    public static final /* enum */ TropicalFishEntity.Pattern STRIPEY = new TropicalFishEntity.Pattern("stripey", TropicalFishEntity.Size.LARGE, 1);
    public static final /* enum */ TropicalFishEntity.Pattern GLITTER = new TropicalFishEntity.Pattern("glitter", TropicalFishEntity.Size.LARGE, 2);
    public static final /* enum */ TropicalFishEntity.Pattern BLOCKFISH = new TropicalFishEntity.Pattern("blockfish", TropicalFishEntity.Size.LARGE, 3);
    public static final /* enum */ TropicalFishEntity.Pattern BETTY = new TropicalFishEntity.Pattern("betty", TropicalFishEntity.Size.LARGE, 4);
    public static final /* enum */ TropicalFishEntity.Pattern CLAYFISH = new TropicalFishEntity.Pattern("clayfish", TropicalFishEntity.Size.LARGE, 5);
    public static final Codec<TropicalFishEntity.Pattern> CODEC;
    private static final IntFunction<TropicalFishEntity.Pattern> INDEX_MAPPER;
    public static final PacketCodec<ByteBuf, TropicalFishEntity.Pattern> PACKET_CODEC;
    private final String id;
    private final Text text;
    private final TropicalFishEntity.Size size;
    private final int index;
    private static final /* synthetic */ TropicalFishEntity.Pattern[] field_6886;

    public static TropicalFishEntity.Pattern[] values() {
        return (TropicalFishEntity.Pattern[])field_6886.clone();
    }

    public static TropicalFishEntity.Pattern valueOf(String string) {
        return Enum.valueOf(TropicalFishEntity.Pattern.class, string);
    }

    private TropicalFishEntity.Pattern(String id, TropicalFishEntity.Size size, int index) {
        this.id = id;
        this.size = size;
        this.index = size.index | index << 8;
        this.text = Text.translatable("entity.minecraft.tropical_fish.type." + this.id);
    }

    public static TropicalFishEntity.Pattern byIndex(int index) {
        return INDEX_MAPPER.apply(index);
    }

    public TropicalFishEntity.Size getSize() {
        return this.size;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public Text getText() {
        return this.text;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        DyeColor dyeColor = components.getOrDefault(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, DEFAULT_VARIANT.baseColor());
        DyeColor dyeColor2 = components.getOrDefault(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, DEFAULT_VARIANT.patternColor());
        Formatting[] formattings = new Formatting[]{Formatting.ITALIC, Formatting.GRAY};
        int i = COMMON_VARIANTS.indexOf(new TropicalFishEntity.Variant(this, dyeColor, dyeColor2));
        if (i != -1) {
            textConsumer.accept(Text.translatable(TropicalFishEntity.getToolTipForVariant(i)).formatted(formattings));
            return;
        }
        textConsumer.accept(this.text.copyContentOnly().formatted(formattings));
        MutableText mutableText = Text.translatable("color.minecraft." + dyeColor.getId());
        if (dyeColor != dyeColor2) {
            mutableText.append(", ").append(Text.translatable("color.minecraft." + dyeColor2.getId()));
        }
        mutableText.formatted(formattings);
        textConsumer.accept(mutableText);
    }

    private static /* synthetic */ TropicalFishEntity.Pattern[] method_36643() {
        return new TropicalFishEntity.Pattern[]{KOB, SUNSTREAK, SNOOPER, DASHER, BRINELY, SPOTTY, FLOPPER, STRIPEY, GLITTER, BLOCKFISH, BETTY, CLAYFISH};
    }

    static {
        field_6886 = TropicalFishEntity.Pattern.method_36643();
        CODEC = StringIdentifiable.createCodec(TropicalFishEntity.Pattern::values);
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(TropicalFishEntity.Pattern::getIndex, TropicalFishEntity.Pattern.values(), KOB);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, TropicalFishEntity.Pattern::getIndex);
    }
}
