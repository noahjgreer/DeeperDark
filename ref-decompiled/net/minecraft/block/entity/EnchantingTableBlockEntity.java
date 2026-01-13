/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.EnchantingTableBlockEntity
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextCodecs
 *  net.minecraft.util.Nameable
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class EnchantingTableBlockEntity
extends BlockEntity
implements Nameable {
    private static final Text CONTAINER_NAME_TEXT = Text.translatable((String)"container.enchant");
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float flipRandom;
    public float flipTurn;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float bookRotation;
    public float lastBookRotation;
    public float targetBookRotation;
    private static final Random RANDOM = Random.create();
    private @Nullable Text customName;

    public EnchantingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.ENCHANTING_TABLE, pos, state);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putNullable("CustomName", TextCodecs.CODEC, (Object)this.customName);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.customName = EnchantingTableBlockEntity.tryParseCustomName((ReadView)view, (String)"CustomName");
    }

    public static void tick(World world, BlockPos pos, BlockState state, EnchantingTableBlockEntity blockEntity) {
        float g;
        blockEntity.pageTurningSpeed = blockEntity.nextPageTurningSpeed;
        blockEntity.lastBookRotation = blockEntity.bookRotation;
        PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, false);
        if (playerEntity != null) {
            double d = playerEntity.getX() - ((double)pos.getX() + 0.5);
            double e = playerEntity.getZ() - ((double)pos.getZ() + 0.5);
            blockEntity.targetBookRotation = (float)MathHelper.atan2((double)e, (double)d);
            blockEntity.nextPageTurningSpeed += 0.1f;
            if (blockEntity.nextPageTurningSpeed < 0.5f || RANDOM.nextInt(40) == 0) {
                float f = blockEntity.flipRandom;
                do {
                    blockEntity.flipRandom += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while (f == blockEntity.flipRandom);
            }
        } else {
            blockEntity.targetBookRotation += 0.02f;
            blockEntity.nextPageTurningSpeed -= 0.1f;
        }
        while (blockEntity.bookRotation >= (float)Math.PI) {
            blockEntity.bookRotation -= (float)Math.PI * 2;
        }
        while (blockEntity.bookRotation < (float)(-Math.PI)) {
            blockEntity.bookRotation += (float)Math.PI * 2;
        }
        while (blockEntity.targetBookRotation >= (float)Math.PI) {
            blockEntity.targetBookRotation -= (float)Math.PI * 2;
        }
        while (blockEntity.targetBookRotation < (float)(-Math.PI)) {
            blockEntity.targetBookRotation += (float)Math.PI * 2;
        }
        for (g = blockEntity.targetBookRotation - blockEntity.bookRotation; g >= (float)Math.PI; g -= (float)Math.PI * 2) {
        }
        while (g < (float)(-Math.PI)) {
            g += (float)Math.PI * 2;
        }
        blockEntity.bookRotation += g * 0.4f;
        blockEntity.nextPageTurningSpeed = MathHelper.clamp((float)blockEntity.nextPageTurningSpeed, (float)0.0f, (float)1.0f);
        ++blockEntity.ticks;
        blockEntity.pageAngle = blockEntity.nextPageAngle;
        float h = (blockEntity.flipRandom - blockEntity.nextPageAngle) * 0.4f;
        float i = 0.2f;
        h = MathHelper.clamp((float)h, (float)-0.2f, (float)0.2f);
        blockEntity.flipTurn += (h - blockEntity.flipTurn) * 0.9f;
        blockEntity.nextPageAngle += blockEntity.flipTurn;
    }

    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return CONTAINER_NAME_TEXT;
    }

    public void setCustomName(@Nullable Text customName) {
        this.customName = customName;
    }

    public @Nullable Text getCustomName() {
        return this.customName;
    }

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.customName = (Text)components.get(DataComponentTypes.CUSTOM_NAME);
    }

    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CUSTOM_NAME, (Object)this.customName);
    }

    public void removeFromCopiedStackData(WriteView view) {
        view.remove("CustomName");
    }
}

