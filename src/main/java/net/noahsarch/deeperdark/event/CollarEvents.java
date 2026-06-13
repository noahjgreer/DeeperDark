package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.component.CollarFuelData;
import net.noahsarch.deeperdark.component.ModComponents;
import net.noahsarch.deeperdark.block.ModBlocks;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.item.CollarItem;
import net.noahsarch.deeperdark.item.CollarTier;
import net.noahsarch.deeperdark.item.ItemMagnetItem;
import net.noahsarch.deeperdark.event.ItemMagnetHandler;
import net.noahsarch.deeperdark.sound.ModSounds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CollarEvents {

    private static final Map<UUID, Integer> fireTickMap = new HashMap<>();
    private static final Map<UUID, Integer> lavaTickMap = new HashMap<>();
    private static final Map<UUID, Integer> jingleCooldown = new HashMap<>();
    private static final Map<UUID, ResourceKey<Level>> lastDimension = new HashMap<>();
    private static final Map<UUID, BlockPos> redstoneActivatedMap = new HashMap<>();
    // Reference-counted set of wire positions currently powered by a player trinket.
    // Queried by RedStoneWireBlockMixin to suppress engine recalculation.
    private static final Map<BlockPos, Integer> wirePlayerCount = new HashMap<>();

    public static boolean isPlayerPoweredWire(BlockPos pos) {
        return wirePlayerCount.containsKey(pos);
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ItemStack collar = ((CollarHolder) player).deeperdark$getCollarItem();
                if (collar.isEmpty() || !(collar.getItem() instanceof CollarItem)) {
                    fireTickMap.remove(player.getUUID());
                    lavaTickMap.remove(player.getUUID());
                    jingleCooldown.remove(player.getUUID());
                    BlockPos prevRedstone = redstoneActivatedMap.remove(player.getUUID());
                    if (prevRedstone != null) {
                        releaseRedstone((ServerLevel) player.level(), prevRedstone);
                    }
                    continue;
                }
                tickCollarEffects(player, collar, (ServerLevel) player.level());
            }
        });
    }

    private static void tickCollarEffects(ServerPlayer player, ItemStack collar, ServerLevel level) {
        ItemContainerContents contents = collar.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> trinkets = NonNullList.withSize(5, ItemStack.EMPTY);
        contents.copyInto(trinkets);

        boolean trinketsDirty = false;
        boolean hasBell = false;
        boolean hasAttractable = false;
        boolean hasSponge = false;
        boolean hasGold = false;
        boolean hasMagnet = false;
        boolean hasBlazeRod = false;
        boolean hasGlowBerries = false;
        boolean hasPumpkin = false;
        boolean hasRedstone = false;
        boolean hasLavaBucket = false;
        boolean hasCraftingTable = false;
        boolean hasArrow = false;

        for (int i = 0; i < trinkets.size(); i++) {
            ItemStack trinket = trinkets.get(i);
            if (trinket.isEmpty()) continue;

            if (trinket.is(Items.GLOW_BERRIES)) {
                hasGlowBerries = true;
                // TODO: dynamic lighting — requires a client-side mixin or LambDynamicLights integration
            }

            if (trinket.is(Items.BLAZE_ROD)) {
                hasBlazeRod = true;
                trinketsDirty |= tickBlazeRod(player, trinket, level);
            }

            if (trinket.getItem() instanceof ItemMagnetItem magnet) {
                hasMagnet = true;
                tickCollarMagnet(level, player, magnet.getMagnetType());
            }

            if (trinket.is(Items.BELL)) {
                hasBell = true;
            }

            if (trinket.is(Items.GOLD_INGOT)) {
                hasGold = true;
            }

            if (trinket.is(Items.SPONGE) || trinket.is(Items.WET_SPONGE)) {
                hasSponge = true;
                trinketsDirty |= tickSponge(player, trinket, level, i, trinkets);
            }

            if (trinket.is(Items.CARVED_PUMPKIN) || trinket.is(Items.PUMPKIN) || trinket.is(Items.JACK_O_LANTERN)) {
                hasPumpkin = true;
            }

            if (trinket.is(Items.REDSTONE_BLOCK) || trinket.is(Items.REDSTONE_TORCH)) {
                hasRedstone = true;
            }

            if (trinket.is(Items.LAVA_BUCKET)) {
                hasLavaBucket = true;
            }

            if (trinket.is(Items.CRAFTING_TABLE)) {
                hasCraftingTable = true;
            }

            if (trinket.is(Items.ARROW)) {
                hasArrow = true;
            }

            if (isAttractable(trinket)) {
                hasAttractable = true;
            }
        }

        // Fuel-based fire resistance and water breathing (tiered collars only)
        CollarItem collarItem = (CollarItem) collar.getItem();
        if (collarItem.getTier() != null) {
            trinketsDirty |= tickFuelEffects(player, collar, collarItem.getTier(), level);
        }

        if (hasBell) {
            tickBellJingle(player, level);
        }

        if (hasAttractable) {
            tickMobAttraction(player, trinkets, level);
        }

        if (hasPumpkin) {
            tickPumpkinEnderman(player, level);
        }

        tickRedstoneTrinket(hasRedstone, player, level);

        if (hasLavaBucket) {
            tickLavaBucketSnowMelt(player, level);
        }

        // Update CustomModelData flags so the item model can conditionally show trinket layers.
        // Flag indices: 0=sponge, 1=gold, 2=bell, 3=magnet, 4=blaze_rod, 5=glow_berries, 6=crafting_table, 7=redstone, 8=arrow
        List<Boolean> flags = List.of(hasSponge, hasGold, hasBell, hasMagnet, hasBlazeRod, hasGlowBerries, hasCraftingTable, hasRedstone, hasArrow);
        CustomModelData existing = collar.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.EMPTY);
        CustomModelData updated = new CustomModelData(existing.floats(), flags, existing.strings(), existing.colors());
        if (!updated.equals(existing)) {
            collar.set(DataComponents.CUSTOM_MODEL_DATA, updated);
            trinketsDirty = true;
        }

        // Nether recharge for wet sponge
        ResourceKey<Level> dim = level.dimension();
        ResourceKey<Level> prevDim = lastDimension.get(player.getUUID());
        if (prevDim != null && !prevDim.equals(dim) && dim.equals(Level.NETHER)) {
            for (int i = 0; i < trinkets.size(); i++) {
                if (trinkets.get(i).is(Items.WET_SPONGE)) {
                    trinkets.set(i, new ItemStack(Items.SPONGE));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 1.5f);
                    trinketsDirty = true;
                }
            }
        }
        lastDimension.put(player.getUUID(), dim);

        if (trinketsDirty) {
            collar.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(trinkets));
        }
    }

    private static boolean tickFuelEffects(ServerPlayer player, ItemStack collar, CollarTier tier, ServerLevel level) {
        CollarFuelData fuel = collar.getOrDefault(ModComponents.COLLAR_FUEL, CollarFuelData.EMPTY);
        int fireTicks  = fuel.fireTicks();
        int waterTicks = fuel.waterTicks();
        boolean dirty = false;

        // Fire damage prevention: drain 1 fire tick per game tick while burning.
        // Actual fire damage is cancelled by the ServerPlayerEntityMixin hurtServer() injection.
        if (fireTicks > 0 && player.isOnFire() && !player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            fireTicks = Math.max(0, fireTicks - 1);
            if (fireTicks == 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 2.0f);
            }
            dirty = true;
        }

        // Drowning prevention: drain 1 water tick per game tick while submerged with no air.
        // The actual drown damage is cancelled by the ServerPlayerEntityMixin hurtServer() injection.
        if (waterTicks > 0 && player.isUnderWater() && player.getAirSupply() <= 0) {
            waterTicks = Math.max(0, waterTicks - 1);
            if (waterTicks == 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 2.0f);
            }
            dirty = true;
        }

        if (dirty) {
            collar.set(ModComponents.COLLAR_FUEL, new CollarFuelData(fireTicks, waterTicks));
        }
        return dirty;
    }

    private static boolean tickBlazeRod(ServerPlayer player, ItemStack rod, ServerLevel level) {
        boolean dirty = false;
        if (!rod.has(DataComponents.MAX_DAMAGE)) {
            rod.set(DataComponents.MAX_STACK_SIZE, 1);
            rod.set(DataComponents.MAX_DAMAGE, 1200);
            dirty = true;
        }

        int maxDamage = rod.getOrDefault(DataComponents.MAX_DAMAGE, 1200);
        int damage = rod.getOrDefault(DataComponents.DAMAGE, 0);

        if (damage >= maxDamage) return dirty;

        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false));

        if (player.isOnFire()) {
            player.clearFire();
            int newDamage = damage + 1;
            rod.set(DataComponents.DAMAGE, newDamage);
            if (newDamage >= maxDamage) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 2.0f);
            }
            return true;
        }
        return dirty;
    }

    private static boolean tickSponge(ServerPlayer player, ItemStack sponge, ServerLevel level,
                                       int slotIndex, NonNullList<ItemStack> trinkets) {
        if (sponge.is(Items.WET_SPONGE)) {
            return tickWetSpongeRecharge(player, level, slotIndex, trinkets);
        }

        if (!sponge.has(DataComponents.MAX_DAMAGE)) {
            sponge.set(DataComponents.MAX_STACK_SIZE, 1);
            sponge.set(DataComponents.MAX_DAMAGE, 2400);
            // Persist the initialization even if the player isn't underwater this tick
            if (!player.isUnderWater()) return true;
        }

        int maxDamage = sponge.getOrDefault(DataComponents.MAX_DAMAGE, 2400);
        int damage = sponge.getOrDefault(DataComponents.DAMAGE, 0);

        if (damage >= maxDamage) return false;

        if (player.isUnderWater()) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, false, false));
            int newDamage = damage + 1;
            sponge.set(DataComponents.DAMAGE, newDamage);
            if (newDamage >= maxDamage) {
                trinkets.set(slotIndex, new ItemStack(Items.WET_SPONGE));
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SPONGE_ABSORB, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            return true;
        }
        return false;
    }

    private static boolean tickWetSpongeRecharge(ServerPlayer player, ServerLevel level,
                                                  int slotIndex, NonNullList<ItemStack> trinkets) {
        UUID uuid = player.getUUID();
        if (player.isInLava()) {
            int t = lavaTickMap.getOrDefault(uuid, 0) + 1;
            lavaTickMap.put(uuid, t);
            fireTickMap.remove(uuid);
            if (t >= 300) {
                trinkets.set(slotIndex, new ItemStack(Items.SPONGE));
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 1.5f);
                lavaTickMap.remove(uuid);
                return true;
            }
        } else if (player.isOnFire()) {
            int t = fireTickMap.getOrDefault(uuid, 0) + 1;
            fireTickMap.put(uuid, t);
            lavaTickMap.remove(uuid);
            if (t >= 600) {
                trinkets.set(slotIndex, new ItemStack(Items.SPONGE));
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 1.5f);
                fireTickMap.remove(uuid);
                return true;
            }
        } else {
            fireTickMap.remove(uuid);
            lavaTickMap.remove(uuid);
        }
        return false;
    }

    private static void tickCollarMagnet(ServerLevel level, ServerPlayer player, ItemMagnetItem.MagnetType type) {
        DeeperDarkConfig.ItemMagnetVariantConfig cfg = getMagnetConfig(DeeperDarkConfig.get(), type);
        double radius = cfg.radius * 0.5;
        double passiveStrength = cfg.passiveStrength * 0.75;
        double xpRadius = radius * 1.5;

        Vec3 center = player.position();
        AABB searchBox = new AABB(
            center.x - xpRadius, center.y - xpRadius, center.z - xpRadius,
            center.x + xpRadius, center.y + xpRadius, center.z + xpRadius
        );

        UUID playerUUID = player.getUUID();
        for (net.minecraft.world.entity.item.ItemEntity item : level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, searchBox)) {
            if (ItemMagnetHandler.isDroppedByPlayer(item.getUUID(), playerUUID)) continue;
            Vec3 diff = center.subtract(item.position());
            double dist = diff.length();
            if (dist < 0.5 || dist > radius) continue;
            double t = 1.0 - (dist / radius);
            item.setDeltaMovement(diff.normalize().scale(passiveStrength * t * t * 0.5));
            item.hurtMarked = true;
        }

        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, searchBox)) {
            Vec3 diff = center.subtract(orb.position());
            double dist = diff.length();
            if (dist < 0.5 || dist > xpRadius) continue;
            double t = 1.0 - (dist / xpRadius);
            orb.setDeltaMovement(diff.normalize().scale(passiveStrength * t * t * 0.75));
            orb.hurtMarked = true;
        }
    }

    private static DeeperDarkConfig.ItemMagnetVariantConfig getMagnetConfig(DeeperDarkConfig.ConfigInstance cfg, ItemMagnetItem.MagnetType type) {
        return switch (type) {
            case COPPER -> cfg.itemMagnet.copper;
            case IRON -> cfg.itemMagnet.iron;
            case GOLDEN -> cfg.itemMagnet.gold;
            case DIAMOND -> cfg.itemMagnet.diamond;
            case NETHERITE -> cfg.itemMagnet.netherite;
        };
    }

    private static void tickBellJingle(ServerPlayer player, ServerLevel level) {
        UUID uuid = player.getUUID();
        int cooldown = jingleCooldown.getOrDefault(uuid, 0);
        if (cooldown > 0) {
            jingleCooldown.put(uuid, cooldown - 1);
            return;
        }
        if (player.onGround() && player.getDeltaMovement().horizontalDistanceSqr() > 0.004) {
            float pitch = 0.9f + level.getRandom().nextFloat() * 0.2f;
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.COLLAR_JINGLE, SoundSource.PLAYERS, 0.6f, pitch);
            jingleCooldown.put(uuid, 8);
        }
    }

    private static void tickMobAttraction(ServerPlayer player, NonNullList<ItemStack> trinkets, ServerLevel level) {
        double range = 8.0;
        AABB searchBox = new AABB(
            player.getX() - range, player.getY() - range, player.getZ() - range,
            player.getX() + range, player.getY() + range, player.getZ() + range
        );

        List<PathfinderMob> nearbyAnimals = level.getEntitiesOfClass(PathfinderMob.class, searchBox,
            a -> !a.isLeashed());

        for (PathfinderMob animal : nearbyAnimals) {
            for (ItemStack trinket : trinkets) {
                if (isAttractableTo(trinket, animal)) {
                    animal.getNavigation().moveTo(player, 1.1);
                    break;
                }
            }
        }
    }

    private static void tickPumpkinEnderman(ServerPlayer player, ServerLevel level) {
        double range = 16.0;
        AABB box = new AABB(
            player.getX() - range, player.getY() - range, player.getZ() - range,
            player.getX() + range, player.getY() + range, player.getZ() + range
        );
        level.getEntitiesOfClass(EnderMan.class, box).forEach(enderman -> {
            var angerRef = enderman.getPersistentAngerTarget();
            boolean angryAtPlayer = player.equals(enderman.getTarget())
                || (angerRef != null && player.getUUID().equals(angerRef.getUUID()));
            if (angryAtPlayer) {
                enderman.setTarget(null);
            }
        });
    }

    private static boolean isAttractable(ItemStack stack) {
        return stack.is(Items.WHEAT) || stack.is(Items.WHEAT_SEEDS) || stack.is(Items.BEETROOT_SEEDS)
            || stack.is(Items.PUMPKIN_SEEDS) || stack.is(Items.MELON_SEEDS) || stack.is(Items.CARROT)
            || stack.is(Items.BEETROOT) || stack.is(Items.BONE) || stack.is(Items.COD)
            || stack.is(Items.SALMON) || stack.is(Items.TROPICAL_FISH) || stack.is(Items.COOKED_COD)
            || stack.is(Items.COOKED_SALMON) || stack.is(Items.SWEET_BERRIES) || stack.is(Items.BAMBOO)
            || stack.is(Items.APPLE) || stack.is(Items.GOLDEN_APPLE) || stack.is(Items.GOLDEN_CARROT);
    }

    private static void tickRedstoneTrinket(boolean hasRedstone, ServerPlayer player, ServerLevel level) {
        UUID uuid = player.getUUID();
        BlockPos prev = redstoneActivatedMap.get(uuid);

        if (!hasRedstone) {
            if (prev != null) {
                redstoneActivatedMap.remove(uuid);
                releaseRedstone(level, prev);
            }
            return;
        }

        BlockPos feetPos = player.blockPosition();
        BlockState feetState = level.getBlockState(feetPos);

        // Ignite any gunpowder trail the player is standing on
        if (feetState.is(ModBlocks.GUNPOWDER_TRAIL)) {
            level.scheduleTick(feetPos, feetState.getBlock(), 2);
        }

        BlockPos onWire = feetState.is(Blocks.REDSTONE_WIRE) ? feetPos : null;

        if (onWire != null) {
            if (!onWire.equals(prev)) {
                // Acquire the new wire BEFORE releasing the old one so the signal never
                // momentarily drops to 0 mid-trail.  If we released first, the trail would
                // go 0 → powered again, creating a rising edge that re-fires note blocks etc.
                redstoneActivatedMap.put(uuid, onWire);
                acquireRedstone(level, onWire);
                if (prev != null) releaseRedstone(level, prev);
            }
            // While on the same wire the mixin holds power at 15 — nothing to do each tick
        } else if (prev != null) {
            redstoneActivatedMap.remove(uuid);
            releaseRedstone(level, prev);
        }
    }

    private static void acquireRedstone(ServerLevel level, BlockPos pos) {
        wirePlayerCount.merge(pos, 1, Integer::sum);
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.REDSTONE_WIRE)) {
            // Flag 2: update server state + clients, no neighbor cascade here
            level.setBlock(pos, state.setValue(RedStoneWireBlock.POWER, 15), 2);
            // One neighbour update so connected devices (pistons, note blocks, etc.) fire once
            level.updateNeighborsAt(pos, Blocks.REDSTONE_WIRE);
        }
    }

    private static void releaseRedstone(ServerLevel level, BlockPos pos) {
        int newCount = wirePlayerCount.getOrDefault(pos, 1) - 1;
        if (newCount <= 0) {
            wirePlayerCount.remove(pos);
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.REDSTONE_WIRE)) {
                level.setBlock(pos, state.setValue(RedStoneWireBlock.POWER, 0), 2);
                level.updateNeighborsAt(pos, Blocks.REDSTONE_WIRE);
            }
        } else {
            wirePlayerCount.put(pos, newCount);
        }
    }

    private static void tickLavaBucketSnowMelt(ServerPlayer player, ServerLevel level) {
        // Mimic vanilla randomTick scatter: 3 random attempts per tick over a small radius
        RandomSource random = level.getRandom();
        int radius = 3;
        BlockPos center = player.blockPosition();
        for (int i = 0; i < 3; i++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dy = random.nextInt(3) - 1;
            int dz = random.nextInt(radius * 2 + 1) - radius;
            BlockPos pos = center.offset(dx, dy, dz);
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.SNOW)) {
                Block.dropResources(state, level, pos);
                level.removeBlock(pos, false);
            } else if (state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.POWDER_SNOW)) {
                level.removeBlock(pos, false);
            } else if (state.is(Blocks.ICE) || state.is(Blocks.FROSTED_ICE)) {
                if (level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
                    level.removeBlock(pos, false);
                } else {
                    level.setBlockAndUpdate(pos, IceBlock.meltsInto());
                    level.neighborChanged(pos, Blocks.WATER, null);
                }
            }
        }
    }

    private static boolean isAttractableTo(ItemStack stack, PathfinderMob mob) {
        if (mob instanceof Cow || mob instanceof Sheep || mob instanceof Goat) {
            return stack.is(Items.WHEAT);
        }
        if (mob instanceof Chicken) {
            return stack.is(Items.WHEAT_SEEDS) || stack.is(Items.BEETROOT_SEEDS)
                || stack.is(Items.PUMPKIN_SEEDS) || stack.is(Items.MELON_SEEDS);
        }
        if (mob instanceof Pig) {
            return stack.is(Items.CARROT) || stack.is(Items.BEETROOT);
        }
        if (mob instanceof Wolf wolf && wolf.isTame()) {
            return stack.is(Items.BONE);
        }
        if (mob instanceof Cat || mob instanceof Ocelot) {
            return stack.is(Items.COD) || stack.is(Items.SALMON) || stack.is(Items.TROPICAL_FISH)
                || stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON);
        }
        if (mob instanceof Fox) {
            return stack.is(Items.SWEET_BERRIES) || stack.is(Items.GLOW_BERRIES);
        }
        if (mob instanceof Panda) {
            return stack.is(Items.BAMBOO);
        }
        if (mob instanceof AbstractHorse) {
            return stack.is(Items.APPLE) || stack.is(Items.GOLDEN_APPLE) || stack.is(Items.GOLDEN_CARROT)
                || stack.is(Items.WHEAT) || stack.is(Items.SUGAR) || stack.is(Items.HAY_BLOCK);
        }
        return false;
    }
}
