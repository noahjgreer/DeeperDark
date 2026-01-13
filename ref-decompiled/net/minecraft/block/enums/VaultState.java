/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.dispenser.ItemDispenserBehavior
 *  net.minecraft.block.enums.VaultState
 *  net.minecraft.block.enums.VaultState$Light
 *  net.minecraft.block.vault.VaultConfig
 *  net.minecraft.block.vault.VaultServerData
 *  net.minecraft.block.vault.VaultSharedData
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.StringIdentifiable
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 */
package net.minecraft.block.enums;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.VaultState;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public sealed class VaultState
extends Enum<VaultState>
implements StringIdentifiable {
    public static final /* enum */ VaultState INACTIVE = new /* Unavailable Anonymous Inner Class!! */;
    public static final /* enum */ VaultState ACTIVE = new /* Unavailable Anonymous Inner Class!! */;
    public static final /* enum */ VaultState UNLOCKING = new /* Unavailable Anonymous Inner Class!! */;
    public static final /* enum */ VaultState EJECTING = new /* Unavailable Anonymous Inner Class!! */;
    private static final int field_48903 = 20;
    private static final int field_48904 = 20;
    private static final int field_48905 = 20;
    private static final int field_48906 = 20;
    private final String id;
    private final Light light;
    private static final /* synthetic */ VaultState[] field_48909;

    public static VaultState[] values() {
        return (VaultState[])field_48909.clone();
    }

    public static VaultState valueOf(String string) {
        return Enum.valueOf(VaultState.class, string);
    }

    VaultState(String id, Light light) {
        this.id = id;
        this.light = light;
    }

    public String asString() {
        return this.id;
    }

    public int getLuminance() {
        return this.light.luminance;
    }

    public VaultState update(ServerWorld world, BlockPos pos, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> VaultState.updateActiveState((ServerWorld)world, (BlockPos)pos, (VaultConfig)config, (VaultServerData)serverData, (VaultSharedData)sharedData, (double)config.activationRange());
            case 1 -> VaultState.updateActiveState((ServerWorld)world, (BlockPos)pos, (VaultConfig)config, (VaultServerData)serverData, (VaultSharedData)sharedData, (double)config.deactivationRange());
            case 2 -> {
                serverData.setStateUpdatingResumeTime(world.getTime() + 20L);
                yield EJECTING;
            }
            case 3 -> {
                if (serverData.getItemsToEject().isEmpty()) {
                    serverData.finishEjecting();
                    yield VaultState.updateActiveState((ServerWorld)world, (BlockPos)pos, (VaultConfig)config, (VaultServerData)serverData, (VaultSharedData)sharedData, (double)config.deactivationRange());
                }
                float f = serverData.getEjectSoundPitchModifier();
                this.ejectItem(world, pos, serverData.getItemToEject(), f);
                sharedData.setDisplayItem(serverData.getItemToDisplay());
                boolean bl = serverData.getItemsToEject().isEmpty();
                int i = bl ? 20 : 20;
                serverData.setStateUpdatingResumeTime(world.getTime() + (long)i);
                yield EJECTING;
            }
        };
    }

    private static VaultState updateActiveState(ServerWorld world, BlockPos pos, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, double radius) {
        sharedData.updateConnectedPlayers(world, pos, serverData, config, radius);
        serverData.setStateUpdatingResumeTime(world.getTime() + 20L);
        return sharedData.hasConnectedPlayers() ? ACTIVE : INACTIVE;
    }

    public void onStateChange(ServerWorld world, BlockPos pos, VaultState newState, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
        this.onChangedFrom(world, pos, config, sharedData);
        newState.onChangedTo(world, pos, config, sharedData, ominous);
    }

    protected void onChangedTo(ServerWorld world, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
    }

    protected void onChangedFrom(ServerWorld world, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
    }

    private void ejectItem(ServerWorld world, BlockPos pos, ItemStack stack, float pitchModifier) {
        ItemDispenserBehavior.spawnItem((World)world, (ItemStack)stack, (int)2, (Direction)Direction.UP, (Position)Vec3d.ofBottomCenter((Vec3i)pos).offset(Direction.UP, 1.2));
        world.syncWorldEvent(3017, pos, 0);
        world.playSound(null, pos, SoundEvents.BLOCK_VAULT_EJECT_ITEM, SoundCategory.BLOCKS, 1.0f, 0.8f + 0.4f * pitchModifier);
    }

    private static /* synthetic */ VaultState[] method_56807() {
        return new VaultState[]{INACTIVE, ACTIVE, UNLOCKING, EJECTING};
    }

    static {
        field_48909 = VaultState.method_56807();
    }
}

