/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.minecraft.block.BeehiveBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CampfireBlock
 *  net.minecraft.block.FireBlock
 *  net.minecraft.block.entity.BeehiveBlockEntity
 *  net.minecraft.block.entity.BeehiveBlockEntity$Bee
 *  net.minecraft.block.entity.BeehiveBlockEntity$BeeData
 *  net.minecraft.block.entity.BeehiveBlockEntity$BeeState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.BeesComponent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.BeeEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.annotation.Debug
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.DebugTrackable$Tracker
 *  net.minecraft.world.debug.data.BeeHiveDebugData
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BeesComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.DebugTrackable;
import net.minecraft.world.debug.data.BeeHiveDebugData;
import net.minecraft.world.event.GameEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
public class BeehiveBlockEntity
extends BlockEntity {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String FLOWER_POS_KEY = "flower_pos";
    private static final String BEES_KEY = "bees";
    static final List<String> IRRELEVANT_BEE_NBT_KEYS = Arrays.asList("Air", "drop_chances", "equipment", "Brain", "CanPickUpLoot", "DeathTime", "fall_distance", "FallFlying", "Fire", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "sleeping_pos", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "hive_pos", "Passengers", "leash", "UUID");
    public static final int MAX_BEE_COUNT = 3;
    private static final int ANGERED_CANNOT_ENTER_HIVE_TICKS = 400;
    private static final int MIN_OCCUPATION_TICKS_WITH_NECTAR = 2400;
    public static final int MIN_OCCUPATION_TICKS_WITHOUT_NECTAR = 600;
    private final List<Bee> bees = Lists.newArrayList();
    private @Nullable BlockPos flowerPos;

    public BeehiveBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.BEEHIVE, pos, state);
    }

    public void markDirty() {
        if (this.isNearFire()) {
            this.angerBees(null, this.world.getBlockState(this.getPos()), BeeState.EMERGENCY);
        }
        super.markDirty();
    }

    public boolean isNearFire() {
        if (this.world == null) {
            return false;
        }
        for (BlockPos blockPos : BlockPos.iterate((BlockPos)this.pos.add(-1, -1, -1), (BlockPos)this.pos.add(1, 1, 1))) {
            if (!(this.world.getBlockState(blockPos).getBlock() instanceof FireBlock)) continue;
            return true;
        }
        return false;
    }

    public boolean hasNoBees() {
        return this.bees.isEmpty();
    }

    public boolean isFullOfBees() {
        return this.bees.size() == 3;
    }

    public void angerBees(@Nullable PlayerEntity player, BlockState state, BeeState beeState) {
        List list = this.tryReleaseBee(state, beeState);
        if (player != null) {
            for (Entity entity : list) {
                if (!(entity instanceof BeeEntity)) continue;
                BeeEntity beeEntity = (BeeEntity)entity;
                if (!(player.getEntityPos().squaredDistanceTo(entity.getEntityPos()) <= 16.0)) continue;
                if (!this.isSmoked()) {
                    beeEntity.setTarget((LivingEntity)player);
                    continue;
                }
                beeEntity.setCannotEnterHiveTicks(400);
            }
        }
    }

    private List<Entity> tryReleaseBee(BlockState state, BeeState beeState) {
        ArrayList list = Lists.newArrayList();
        this.bees.removeIf(bee -> BeehiveBlockEntity.releaseBee((World)this.world, (BlockPos)this.pos, (BlockState)state, (BeeData)bee.createData(), (List)list, (BeeState)beeState, (BlockPos)this.flowerPos));
        if (!list.isEmpty()) {
            super.markDirty();
        }
        return list;
    }

    @Debug
    public int getBeeCount() {
        return this.bees.size();
    }

    public static int getHoneyLevel(BlockState state) {
        return (Integer)state.get((Property)BeehiveBlock.HONEY_LEVEL);
    }

    @Debug
    public boolean isSmoked() {
        return CampfireBlock.isLitCampfireInRange((World)this.world, (BlockPos)this.getPos());
    }

    public void tryEnterHive(BeeEntity entity) {
        if (this.bees.size() >= 3) {
            return;
        }
        entity.stopRiding();
        entity.removeAllPassengers();
        entity.detachLeash();
        this.addBee(BeeData.of((Entity)entity));
        if (this.world != null) {
            if (entity.hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
                this.flowerPos = entity.getFlowerPos();
            }
            BlockPos blockPos = this.getPos();
            this.world.playSound(null, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
            this.world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of((Entity)entity, (BlockState)this.getCachedState()));
        }
        entity.discard();
        super.markDirty();
    }

    public void addBee(BeeData bee) {
        this.bees.add(new Bee(bee));
    }

    private static boolean releaseBee(World world, BlockPos pos, BlockState state, BeeData bee, @Nullable List<Entity> entities, BeeState beeState, @Nullable BlockPos flowerPos) {
        boolean bl;
        if (((Boolean)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.BEES_STAY_IN_HIVE_GAMEPLAY, pos)).booleanValue() && beeState != BeeState.EMERGENCY) {
            return false;
        }
        Direction direction = (Direction)state.get((Property)BeehiveBlock.FACING);
        BlockPos blockPos = pos.offset(direction);
        boolean bl2 = bl = !world.getBlockState(blockPos).getCollisionShape((BlockView)world, blockPos).isEmpty();
        if (bl && beeState != BeeState.EMERGENCY) {
            return false;
        }
        Entity entity = bee.loadEntity(world, pos);
        if (entity != null) {
            if (entity instanceof BeeEntity) {
                BeeEntity beeEntity = (BeeEntity)entity;
                if (flowerPos != null && !beeEntity.hasFlower() && world.random.nextFloat() < 0.9f) {
                    beeEntity.setFlowerPos(flowerPos);
                }
                if (beeState == BeeState.HONEY_DELIVERED) {
                    int i;
                    beeEntity.onHoneyDelivered();
                    if (state.isIn(BlockTags.BEEHIVES, statex -> statex.contains((Property)BeehiveBlock.HONEY_LEVEL)) && (i = BeehiveBlockEntity.getHoneyLevel((BlockState)state)) < 5) {
                        int j;
                        int n = j = world.random.nextInt(100) == 0 ? 2 : 1;
                        if (i + j > 5) {
                            --j;
                        }
                        world.setBlockState(pos, (BlockState)state.with((Property)BeehiveBlock.HONEY_LEVEL, (Comparable)Integer.valueOf(i + j)));
                    }
                }
                if (entities != null) {
                    entities.add((Entity)beeEntity);
                }
                float f = entity.getWidth();
                double d = bl ? 0.0 : 0.55 + (double)(f / 2.0f);
                double e = (double)pos.getX() + 0.5 + d * (double)direction.getOffsetX();
                double g = (double)pos.getY() + 0.5 - (double)(entity.getHeight() / 2.0f);
                double h = (double)pos.getZ() + 0.5 + d * (double)direction.getOffsetZ();
                entity.refreshPositionAndAngles(e, g, h, entity.getYaw(), entity.getPitch());
            }
            world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((Entity)entity, (BlockState)world.getBlockState(pos)));
            return world.spawnEntity(entity);
        }
        return false;
    }

    private boolean hasFlowerPos() {
        return this.flowerPos != null;
    }

    private static void tickBees(World world, BlockPos pos, BlockState state, List<Bee> bees, @Nullable BlockPos flowerPos) {
        boolean bl = false;
        Iterator<Bee> iterator = bees.iterator();
        while (iterator.hasNext()) {
            BeeState beeState;
            Bee bee = iterator.next();
            if (!bee.canExitHive()) continue;
            BeeState beeState2 = beeState = bee.hasNectar() ? BeeState.HONEY_DELIVERED : BeeState.BEE_RELEASED;
            if (!BeehiveBlockEntity.releaseBee((World)world, (BlockPos)pos, (BlockState)state, (BeeData)bee.createData(), null, (BeeState)beeState, (BlockPos)flowerPos)) continue;
            bl = true;
            iterator.remove();
        }
        if (bl) {
            BeehiveBlockEntity.markDirty((World)world, (BlockPos)pos, (BlockState)state);
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, BeehiveBlockEntity blockEntity) {
        BeehiveBlockEntity.tickBees((World)world, (BlockPos)pos, (BlockState)state, (List)blockEntity.bees, (BlockPos)blockEntity.flowerPos);
        if (!blockEntity.bees.isEmpty() && world.getRandom().nextDouble() < 0.005) {
            double d = (double)pos.getX() + 0.5;
            double e = pos.getY();
            double f = (double)pos.getZ() + 0.5;
            world.playSound(null, d, e, f, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.bees.clear();
        view.read("bees", BeeData.LIST_CODEC).orElse(List.of()).forEach(arg_0 -> this.addBee(arg_0));
        this.flowerPos = view.read("flower_pos", BlockPos.CODEC).orElse(null);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("bees", BeeData.LIST_CODEC, (Object)this.createBeesData());
        view.putNullable("flower_pos", BlockPos.CODEC, (Object)this.flowerPos);
    }

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.bees.clear();
        List list = ((BeesComponent)components.getOrDefault(DataComponentTypes.BEES, (Object)BeesComponent.DEFAULT)).bees();
        list.forEach(arg_0 -> this.addBee(arg_0));
    }

    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.BEES, (Object)new BeesComponent(this.createBeesData()));
    }

    public void removeFromCopiedStackData(WriteView view) {
        super.removeFromCopiedStackData(view);
        view.remove("bees");
    }

    private List<BeeData> createBeesData() {
        return this.bees.stream().map(Bee::createData).toList();
    }

    public void registerTracking(ServerWorld world, DebugTrackable.Tracker tracker) {
        tracker.track(DebugSubscriptionTypes.BEE_HIVES, () -> BeeHiveDebugData.fromBeehive((BeehiveBlockEntity)this));
    }
}

