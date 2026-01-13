/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.block.entity.Sherds
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$TooltipContext
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.tooltip.TooltipAppender
 *  net.minecraft.item.tooltip.TooltipType
 *  net.minecraft.network.RegistryByteBuf
 *  net.minecraft.network.codec.PacketCodec
 *  net.minecraft.network.codec.PacketCodecs
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 */
package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/*
 * Exception performing whole class analysis ignored.
 */
public record Sherds(Optional<Item> back, Optional<Item> left, Optional<Item> right, Optional<Item> front) implements TooltipAppender
{
    private final Optional<Item> back;
    private final Optional<Item> left;
    private final Optional<Item> right;
    private final Optional<Item> front;
    public static final Sherds DEFAULT = new Sherds(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    public static final Codec<Sherds> CODEC = Registries.ITEM.getCodec().sizeLimitedListOf(4).xmap(Sherds::new, Sherds::toList);
    public static final PacketCodec<RegistryByteBuf, Sherds> PACKET_CODEC = PacketCodecs.registryValue((RegistryKey)RegistryKeys.ITEM).collect(PacketCodecs.toList((int)4)).xmap(Sherds::new, Sherds::toList);

    private Sherds(List<Item> sherds) {
        this(Sherds.getSherd(sherds, (int)0), Sherds.getSherd(sherds, (int)1), Sherds.getSherd(sherds, (int)2), Sherds.getSherd(sherds, (int)3));
    }

    public Sherds(Item back, Item left, Item right, Item front) {
        this(List.of(back, left, right, front));
    }

    public Sherds(Optional<Item> back, Optional<Item> left, Optional<Item> right, Optional<Item> front) {
        this.back = back;
        this.left = left;
        this.right = right;
        this.front = front;
    }

    private static Optional<Item> getSherd(List<Item> sherds, int index) {
        if (index >= sherds.size()) {
            return Optional.empty();
        }
        Item item = sherds.get(index);
        return item == Items.BRICK ? Optional.empty() : Optional.of(item);
    }

    public List<Item> toList() {
        return Stream.of(this.back, this.left, this.right, this.front).map(item -> item.orElse(Items.BRICK)).toList();
    }

    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        if (this.equals((Object)DEFAULT)) {
            return;
        }
        textConsumer.accept(ScreenTexts.EMPTY);
        Sherds.appendSherdTooltip(textConsumer, (Optional)this.front);
        Sherds.appendSherdTooltip(textConsumer, (Optional)this.left);
        Sherds.appendSherdTooltip(textConsumer, (Optional)this.right);
        Sherds.appendSherdTooltip(textConsumer, (Optional)this.back);
    }

    private static void appendSherdTooltip(Consumer<Text> textConsumer, Optional<Item> sherdItem) {
        textConsumer.accept((Text)new ItemStack((ItemConvertible)sherdItem.orElse(Items.BRICK), 1).getName().copyContentOnly().formatted(Formatting.GRAY));
    }

    public Optional<Item> back() {
        return this.back;
    }

    public Optional<Item> left() {
        return this.left;
    }

    public Optional<Item> right() {
        return this.right;
    }

    public Optional<Item> front() {
        return this.front;
    }
}

