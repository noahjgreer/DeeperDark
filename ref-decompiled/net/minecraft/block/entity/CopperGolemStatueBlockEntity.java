/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CopperGolemStatueBlock
 *  net.minecraft.block.CopperGolemStatueBlock$Pose
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.CopperGolemStatueBlockEntity
 *  net.minecraft.component.ComponentMap
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.BlockStateComponent
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.passive.CopperGolemEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.state.property.Property
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.CopperGolemStatueBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

public class CopperGolemStatueBlockEntity
extends BlockEntity {
    public CopperGolemStatueBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.COPPER_GOLEM_STATUE, pos, state);
    }

    public void copyDataFrom(CopperGolemEntity copperGolemEntity) {
        this.setComponents(ComponentMap.builder().addAll(this.getComponents()).add(DataComponentTypes.CUSTOM_NAME, (Object)copperGolemEntity.getCustomName()).build());
        super.markDirty();
    }

    public @Nullable CopperGolemEntity createCopperGolem(BlockState state) {
        CopperGolemEntity copperGolemEntity = (CopperGolemEntity)EntityType.COPPER_GOLEM.create(this.world, SpawnReason.TRIGGERED);
        if (copperGolemEntity != null) {
            copperGolemEntity.setCustomName((Text)this.getComponents().get(DataComponentTypes.CUSTOM_NAME));
            return this.setupEntity(state, copperGolemEntity);
        }
        return null;
    }

    private CopperGolemEntity setupEntity(BlockState state, CopperGolemEntity entity) {
        BlockPos blockPos = this.getPos();
        entity.refreshPositionAndAngles(blockPos.toCenterPos().x, (double)blockPos.getY(), blockPos.toCenterPos().z, ((Direction)state.get((Property)CopperGolemStatueBlock.FACING)).getPositiveHorizontalDegrees(), 0.0f);
        entity.headYaw = entity.getYaw();
        entity.bodyYaw = entity.getYaw();
        entity.playSpawnSound();
        return entity;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public ItemStack withComponents(ItemStack stack, CopperGolemStatueBlock.Pose pose) {
        stack.applyComponentsFrom(this.createComponentMap());
        stack.set(DataComponentTypes.BLOCK_STATE, (Object)BlockStateComponent.DEFAULT.with((Property)CopperGolemStatueBlock.POSE, (Comparable)pose));
        return stack;
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

