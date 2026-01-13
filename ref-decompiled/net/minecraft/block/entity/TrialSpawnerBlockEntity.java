/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.SharedConstants
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.TrialSpawnerBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.Spawner
 *  net.minecraft.block.entity.TrialSpawnerBlockEntity
 *  net.minecraft.block.enums.TrialSpawnerState
 *  net.minecraft.block.spawner.EntityDetector
 *  net.minecraft.block.spawner.EntityDetector$Selector
 *  net.minecraft.block.spawner.TrialSpawnerLogic
 *  net.minecraft.block.spawner.TrialSpawnerLogic$FullConfig
 *  net.minecraft.block.spawner.TrialSpawnerLogic$TrialSpawner
 *  net.minecraft.entity.EntityType
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 */
package net.minecraft.block.entity;

import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrialSpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Spawner;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.EntityDetector;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class TrialSpawnerBlockEntity
extends BlockEntity
implements Spawner,
TrialSpawnerLogic.TrialSpawner {
    private final TrialSpawnerLogic logic = this.createDefaultLogic();

    public TrialSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.TRIAL_SPAWNER, pos, state);
    }

    private TrialSpawnerLogic createDefaultLogic() {
        EntityDetector entityDetector = SharedConstants.TRIAL_SPAWNER_DETECTS_SHEEP_AS_PLAYERS ? EntityDetector.SHEEP : EntityDetector.SURVIVAL_PLAYERS;
        EntityDetector.Selector selector = EntityDetector.Selector.IN_WORLD;
        return new TrialSpawnerLogic(TrialSpawnerLogic.FullConfig.DEFAULT, (TrialSpawnerLogic.TrialSpawner)this, entityDetector, selector);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.logic.readData(view);
        if (this.world != null) {
            this.updateListeners();
        }
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        this.logic.writeData(view);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.logic.getData().getSpawnDataNbt((TrialSpawnerState)this.getCachedState().get((Property)TrialSpawnerBlock.TRIAL_SPAWNER_STATE));
    }

    public void setEntityType(EntityType<?> type, Random random) {
        if (this.world == null) {
            Util.logErrorOrPause((String)"Expected non-null level");
            return;
        }
        this.logic.setEntityType(type, this.world);
        this.markDirty();
    }

    public TrialSpawnerLogic getSpawner() {
        return this.logic;
    }

    public TrialSpawnerState getSpawnerState() {
        if (!this.getCachedState().contains((Property)Properties.TRIAL_SPAWNER_STATE)) {
            return TrialSpawnerState.INACTIVE;
        }
        return (TrialSpawnerState)this.getCachedState().get((Property)Properties.TRIAL_SPAWNER_STATE);
    }

    public void setSpawnerState(World world, TrialSpawnerState spawnerState) {
        this.markDirty();
        world.setBlockState(this.pos, (BlockState)this.getCachedState().with((Property)Properties.TRIAL_SPAWNER_STATE, (Comparable)spawnerState));
    }

    public void updateListeners() {
        this.markDirty();
        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

