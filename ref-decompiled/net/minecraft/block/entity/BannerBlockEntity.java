/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.AbstractBannerBlock
 *  net.minecraft.block.BannerBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BannerBlockEntity
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.BannerPatternsComponent
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextCodecs
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Nameable
 *  net.minecraft.util.math.BlockPos
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BannerBlockEntity
extends BlockEntity
implements Nameable {
    public static final int MAX_PATTERN_COUNT = 6;
    private static final String PATTERNS_KEY = "patterns";
    private static final Text BLOCK_NAME = Text.translatable((String)"block.minecraft.banner");
    private @Nullable Text customName;
    private final DyeColor baseColor;
    private BannerPatternsComponent patterns = BannerPatternsComponent.DEFAULT;

    public BannerBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ((AbstractBannerBlock)state.getBlock()).getColor());
    }

    public BannerBlockEntity(BlockPos pos, BlockState state, DyeColor baseColor) {
        super(BlockEntityType.BANNER, pos, state);
        this.baseColor = baseColor;
    }

    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return BLOCK_NAME;
    }

    public @Nullable Text getCustomName() {
        return this.customName;
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.patterns.equals((Object)BannerPatternsComponent.DEFAULT)) {
            view.put("patterns", BannerPatternsComponent.CODEC, (Object)this.patterns);
        }
        view.putNullable("CustomName", TextCodecs.CODEC, (Object)this.customName);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.customName = BannerBlockEntity.tryParseCustomName((ReadView)view, (String)"CustomName");
        this.patterns = view.read("patterns", BannerPatternsComponent.CODEC).orElse(BannerPatternsComponent.DEFAULT);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createNbt(registries);
    }

    public BannerPatternsComponent getPatterns() {
        return this.patterns;
    }

    public ItemStack getPickStack() {
        ItemStack itemStack = new ItemStack((ItemConvertible)BannerBlock.getForColor((DyeColor)this.baseColor));
        itemStack.applyComponentsFrom(this.createComponentMap());
        return itemStack;
    }

    public DyeColor getColorForState() {
        return this.baseColor;
    }

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.patterns = (BannerPatternsComponent)components.getOrDefault(DataComponentTypes.BANNER_PATTERNS, (Object)BannerPatternsComponent.DEFAULT);
        this.customName = (Text)components.get(DataComponentTypes.CUSTOM_NAME);
    }

    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.BANNER_PATTERNS, (Object)this.patterns);
        builder.add(DataComponentTypes.CUSTOM_NAME, (Object)this.customName);
    }

    public void removeFromCopiedStackData(WriteView view) {
        view.remove("patterns");
        view.remove("CustomName");
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

