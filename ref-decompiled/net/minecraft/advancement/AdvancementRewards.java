/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.advancement.AdvancementRewards
 *  net.minecraft.command.permission.LeveledPermissionPredicate
 *  net.minecraft.command.permission.PermissionPredicate
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.LootTable
 *  net.minecraft.loot.context.LootContextParameters
 *  net.minecraft.loot.context.LootContextTypes
 *  net.minecraft.loot.context.LootWorldContext
 *  net.minecraft.loot.context.LootWorldContext$Builder
 *  net.minecraft.recipe.Recipe
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.function.LazyContainer
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 */
package net.minecraft.advancement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.LazyContainer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public record AdvancementRewards(int experience, List<RegistryKey<LootTable>> loot, List<RegistryKey<Recipe<?>>> recipes, Optional<LazyContainer> function) {
    private final int experience;
    private final List<RegistryKey<LootTable>> loot;
    private final List<RegistryKey<Recipe<?>>> recipes;
    private final Optional<LazyContainer> function;
    public static final Codec<AdvancementRewards> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.optionalFieldOf("experience", (Object)0).forGetter(AdvancementRewards::experience), (App)LootTable.TABLE_KEY.listOf().optionalFieldOf("loot", List.of()).forGetter(AdvancementRewards::loot), (App)Recipe.KEY_CODEC.listOf().optionalFieldOf("recipes", List.of()).forGetter(AdvancementRewards::recipes), (App)LazyContainer.CODEC.optionalFieldOf("function").forGetter(AdvancementRewards::function)).apply((Applicative)instance, AdvancementRewards::new));
    public static final AdvancementRewards NONE = new AdvancementRewards(0, List.of(), List.of(), Optional.empty());

    public AdvancementRewards(int experience, List<RegistryKey<LootTable>> loot, List<RegistryKey<Recipe<?>>> recipes, Optional<LazyContainer> function) {
        this.experience = experience;
        this.loot = loot;
        this.recipes = recipes;
        this.function = function;
    }

    public void apply(ServerPlayerEntity player) {
        player.addExperience(this.experience);
        ServerWorld serverWorld = player.getEntityWorld();
        MinecraftServer minecraftServer = serverWorld.getServer();
        LootWorldContext lootWorldContext = new LootWorldContext.Builder(serverWorld).add(LootContextParameters.THIS_ENTITY, (Object)player).add(LootContextParameters.ORIGIN, (Object)player.getEntityPos()).build(LootContextTypes.ADVANCEMENT_REWARD);
        boolean bl = false;
        for (RegistryKey registryKey : this.loot) {
            for (ItemStack itemStack : minecraftServer.getReloadableRegistries().getLootTable(registryKey).generateLoot(lootWorldContext)) {
                if (player.giveItemStack(itemStack)) {
                    serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                    bl = true;
                    continue;
                }
                ItemEntity itemEntity = player.dropItem(itemStack, false);
                if (itemEntity == null) continue;
                itemEntity.resetPickupDelay();
                itemEntity.setOwner(player.getUuid());
            }
        }
        if (bl) {
            player.currentScreenHandler.sendContentUpdates();
        }
        if (!this.recipes.isEmpty()) {
            player.unlockRecipes(this.recipes);
        }
        this.function.flatMap(function -> function.get(minecraftServer.getCommandFunctionManager())).ifPresent(function -> minecraftServer.getCommandFunctionManager().execute(function, player.getCommandSource().withSilent().withPermissions((PermissionPredicate)LeveledPermissionPredicate.GAMEMASTERS)));
    }

    public int experience() {
        return this.experience;
    }

    public List<RegistryKey<LootTable>> loot() {
        return this.loot;
    }

    public List<RegistryKey<Recipe<?>>> recipes() {
        return this.recipes;
    }

    public Optional<LazyContainer> function() {
        return this.function;
    }
}

