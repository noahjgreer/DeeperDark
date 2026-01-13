/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.decoration;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public static class DisplayEntity.ItemDisplayEntity
extends DisplayEntity {
    private static final String ITEM_NBT_KEY = "item";
    private static final String ITEM_DISPLAY_NBT_KEY = "item_display";
    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(DisplayEntity.ItemDisplayEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Byte> ITEM_DISPLAY = DataTracker.registerData(DisplayEntity.ItemDisplayEntity.class, TrackedDataHandlerRegistry.BYTE);
    private final StackReference stackReference = StackReference.of(this::getItemStack, this::setItemStack);
    private @Nullable Data data;

    public DisplayEntity.ItemDisplayEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ITEM, ItemStack.EMPTY);
        builder.add(ITEM_DISPLAY, ItemDisplayContext.NONE.getIndex());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (ITEM.equals(data) || ITEM_DISPLAY.equals(data)) {
            this.renderingDataSet = true;
        }
    }

    public final ItemStack getItemStack() {
        return this.dataTracker.get(ITEM);
    }

    public final void setItemStack(ItemStack stack) {
        this.dataTracker.set(ITEM, stack);
    }

    public final void setItemDisplayContext(ItemDisplayContext context) {
        this.dataTracker.set(ITEM_DISPLAY, context.getIndex());
    }

    public final ItemDisplayContext getItemDisplayContext() {
        return ItemDisplayContext.FROM_INDEX.apply(this.dataTracker.get(ITEM_DISPLAY).byteValue());
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.setItemStack(view.read(ITEM_NBT_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY));
        this.setItemDisplayContext(view.read(ITEM_DISPLAY_NBT_KEY, ItemDisplayContext.CODEC).orElse(ItemDisplayContext.NONE));
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        ItemStack itemStack = this.getItemStack();
        if (!itemStack.isEmpty()) {
            view.put(ITEM_NBT_KEY, ItemStack.CODEC, itemStack);
        }
        view.put(ITEM_DISPLAY_NBT_KEY, ItemDisplayContext.CODEC, this.getItemDisplayContext());
    }

    @Override
    public @Nullable StackReference getStackReference(int slot) {
        if (slot == 0) {
            return this.stackReference;
        }
        return null;
    }

    public @Nullable Data getData() {
        return this.data;
    }

    @Override
    protected void refreshData(boolean shouldLerp, float lerpProgress) {
        ItemStack itemStack = this.getItemStack();
        itemStack.setHolder(this);
        this.data = new Data(itemStack, this.getItemDisplayContext());
    }

    public record Data(ItemStack itemStack, ItemDisplayContext itemTransform) {
    }
}
