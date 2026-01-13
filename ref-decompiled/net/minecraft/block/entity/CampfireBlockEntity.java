/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CampfireBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.CampfireBlockEntity
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ContainerComponent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.recipe.CampfireCookingRecipe
 *  net.minecraft.recipe.RecipeEntry
 *  net.minecraft.recipe.RecipeType
 *  net.minecraft.recipe.ServerRecipeManager$MatchGetter
 *  net.minecraft.recipe.input.RecipeInput
 *  net.minecraft.recipe.input.SingleStackRecipeInput
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.NbtWriteView
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.Clearable
 *  net.minecraft.util.ErrorReporter
 *  net.minecraft.util.ErrorReporter$Logging
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Clearable;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
public class CampfireBlockEntity
extends BlockEntity
implements Clearable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_31330 = 2;
    private static final int field_31331 = 4;
    private final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize((int)4, (Object)ItemStack.EMPTY);
    private final int[] cookingTimes = new int[4];
    private final int[] cookingTotalTimes = new int[4];

    public CampfireBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.CAMPFIRE, pos, state);
    }

    public static void litServerTick(ServerWorld world, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, ServerRecipeManager.MatchGetter<SingleStackRecipeInput, CampfireCookingRecipe> recipeMatchGetter) {
        boolean bl = false;
        for (int i = 0; i < blockEntity.itemsBeingCooked.size(); ++i) {
            SingleStackRecipeInput singleStackRecipeInput;
            ItemStack itemStack2;
            ItemStack itemStack = (ItemStack)blockEntity.itemsBeingCooked.get(i);
            if (itemStack.isEmpty()) continue;
            bl = true;
            int n = i;
            blockEntity.cookingTimes[n] = blockEntity.cookingTimes[n] + 1;
            if (blockEntity.cookingTimes[i] < blockEntity.cookingTotalTimes[i] || !(itemStack2 = recipeMatchGetter.getFirstMatch((RecipeInput)(singleStackRecipeInput = new SingleStackRecipeInput(itemStack)), world).map(recipe -> ((CampfireCookingRecipe)recipe.value()).craft(singleStackRecipeInput, (RegistryWrapper.WrapperLookup)world.getRegistryManager())).orElse(itemStack)).isItemEnabled(world.getEnabledFeatures())) continue;
            ItemScatterer.spawn((World)world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (ItemStack)itemStack2);
            blockEntity.itemsBeingCooked.set(i, (Object)ItemStack.EMPTY);
            world.updateListeners(pos, state, state, 3);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((BlockState)state));
        }
        if (bl) {
            CampfireBlockEntity.markDirty((World)world, (BlockPos)pos, (BlockState)state);
        }
    }

    public static void unlitServerTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire) {
        boolean bl = false;
        for (int i = 0; i < campfire.itemsBeingCooked.size(); ++i) {
            if (campfire.cookingTimes[i] <= 0) continue;
            bl = true;
            campfire.cookingTimes[i] = MathHelper.clamp((int)(campfire.cookingTimes[i] - 2), (int)0, (int)campfire.cookingTotalTimes[i]);
        }
        if (bl) {
            CampfireBlockEntity.markDirty((World)world, (BlockPos)pos, (BlockState)state);
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire) {
        int i;
        Random random = world.random;
        if (random.nextFloat() < 0.11f) {
            for (i = 0; i < random.nextInt(2) + 2; ++i) {
                CampfireBlock.spawnSmokeParticle((World)world, (BlockPos)pos, (boolean)((Boolean)state.get((Property)CampfireBlock.SIGNAL_FIRE)), (boolean)false);
            }
        }
        i = ((Direction)state.get((Property)CampfireBlock.FACING)).getHorizontalQuarterTurns();
        for (int j = 0; j < campfire.itemsBeingCooked.size(); ++j) {
            if (((ItemStack)campfire.itemsBeingCooked.get(j)).isEmpty() || !(random.nextFloat() < 0.2f)) continue;
            Direction direction = Direction.fromHorizontalQuarterTurns((int)Math.floorMod(j + i, 4));
            float f = 0.3125f;
            double d = (double)pos.getX() + 0.5 - (double)((float)direction.getOffsetX() * 0.3125f) + (double)((float)direction.rotateYClockwise().getOffsetX() * 0.3125f);
            double e = (double)pos.getY() + 0.5;
            double g = (double)pos.getZ() + 0.5 - (double)((float)direction.getOffsetZ() * 0.3125f) + (double)((float)direction.rotateYClockwise().getOffsetZ() * 0.3125f);
            for (int k = 0; k < 4; ++k) {
                world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
            }
        }
    }

    public DefaultedList<ItemStack> getItemsBeingCooked() {
        return this.itemsBeingCooked;
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.itemsBeingCooked.clear();
        Inventories.readData((ReadView)view, (DefaultedList)this.itemsBeingCooked);
        view.getOptionalIntArray("CookingTimes").ifPresentOrElse(is -> System.arraycopy(is, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, ((int[])is).length)), () -> Arrays.fill(this.cookingTimes, 0));
        view.getOptionalIntArray("CookingTotalTimes").ifPresentOrElse(is -> System.arraycopy(is, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, ((int[])is).length)), () -> Arrays.fill(this.cookingTotalTimes, 0));
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData((WriteView)view, (DefaultedList)this.itemsBeingCooked, (boolean)true);
        view.putIntArray("CookingTimes", this.cookingTimes);
        view.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getReporterContext(), LOGGER);){
            NbtWriteView nbtWriteView = NbtWriteView.create((ErrorReporter)logging, (RegistryWrapper.WrapperLookup)registries);
            Inventories.writeData((WriteView)nbtWriteView, (DefaultedList)this.itemsBeingCooked, (boolean)true);
            NbtCompound nbtCompound = nbtWriteView.getNbt();
            return nbtCompound;
        }
    }

    public boolean addItem(ServerWorld world, @Nullable LivingEntity entity, ItemStack stack) {
        for (int i = 0; i < this.itemsBeingCooked.size(); ++i) {
            ItemStack itemStack = (ItemStack)this.itemsBeingCooked.get(i);
            if (!itemStack.isEmpty()) continue;
            Optional optional = world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, (RecipeInput)new SingleStackRecipeInput(stack), (World)world);
            if (optional.isEmpty()) {
                return false;
            }
            this.cookingTotalTimes[i] = ((CampfireCookingRecipe)((RecipeEntry)optional.get()).value()).getCookingTime();
            this.cookingTimes[i] = 0;
            this.itemsBeingCooked.set(i, (Object)stack.splitUnlessCreative(1, entity));
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of((Entity)entity, (BlockState)this.getCachedState()));
            this.updateListeners();
            return true;
        }
        return false;
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    public void clear() {
        this.itemsBeingCooked.clear();
    }

    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        if (this.world != null) {
            ItemScatterer.spawn((World)this.world, (BlockPos)pos, (DefaultedList)this.getItemsBeingCooked());
        }
    }

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        ((ContainerComponent)components.getOrDefault(DataComponentTypes.CONTAINER, (Object)ContainerComponent.DEFAULT)).copyTo(this.getItemsBeingCooked());
    }

    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CONTAINER, (Object)ContainerComponent.fromStacks((List)this.getItemsBeingCooked()));
    }

    public void removeFromCopiedStackData(WriteView view) {
        view.remove("Items");
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

