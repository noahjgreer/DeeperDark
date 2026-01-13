/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractDonkeyEntityRenderer;
import net.minecraft.client.render.entity.AllayEntityRenderer;
import net.minecraft.client.render.entity.ArmadilloEntityRenderer;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.AxolotlEntityRenderer;
import net.minecraft.client.render.entity.BatEntityRenderer;
import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.client.render.entity.BlazeEntityRenderer;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.render.entity.BoggedEntityRenderer;
import net.minecraft.client.render.entity.BreezeEntityRenderer;
import net.minecraft.client.render.entity.CamelEntityRenderer;
import net.minecraft.client.render.entity.CamelHuskEntityRenderer;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.CaveSpiderEntityRenderer;
import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.client.render.entity.CodEntityRenderer;
import net.minecraft.client.render.entity.CopperGolemEntityRenderer;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.client.render.entity.CreakingEntityRenderer;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.render.entity.DolphinEntityRenderer;
import net.minecraft.client.render.entity.DragonFireballEntityRenderer;
import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.render.entity.EndermiteEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EvokerEntityRenderer;
import net.minecraft.client.render.entity.EvokerFangsEntityRenderer;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.render.entity.FireworkRocketEntityRenderer;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.FoxEntityRenderer;
import net.minecraft.client.render.entity.FrogEntityRenderer;
import net.minecraft.client.render.entity.GhastEntityRenderer;
import net.minecraft.client.render.entity.GiantEntityRenderer;
import net.minecraft.client.render.entity.GlowSquidEntityRenderer;
import net.minecraft.client.render.entity.GoatEntityRenderer;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.render.entity.HappyGhastEntityRenderer;
import net.minecraft.client.render.entity.HoglinEntityRenderer;
import net.minecraft.client.render.entity.HorseEntityRenderer;
import net.minecraft.client.render.entity.HuskEntityRenderer;
import net.minecraft.client.render.entity.IllusionerEntityRenderer;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.entity.LeashKnotEntityRenderer;
import net.minecraft.client.render.entity.LightningEntityRenderer;
import net.minecraft.client.render.entity.LlamaEntityRenderer;
import net.minecraft.client.render.entity.LlamaSpitEntityRenderer;
import net.minecraft.client.render.entity.MagmaCubeEntityRenderer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.entity.MooshroomEntityRenderer;
import net.minecraft.client.render.entity.NautilusEntityRenderer;
import net.minecraft.client.render.entity.OcelotEntityRenderer;
import net.minecraft.client.render.entity.OminousItemSpawnerEntityRenderer;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.render.entity.PandaEntityRenderer;
import net.minecraft.client.render.entity.ParchedEntityRenderer;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.PhantomEntityRenderer;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.PillagerEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PolarBearEntityRenderer;
import net.minecraft.client.render.entity.PufferfishEntityRenderer;
import net.minecraft.client.render.entity.RabbitEntityRenderer;
import net.minecraft.client.render.entity.RaftEntityRenderer;
import net.minecraft.client.render.entity.RavagerEntityRenderer;
import net.minecraft.client.render.entity.SalmonEntityRenderer;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.ShulkerBulletEntityRenderer;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.SilverfishEntityRenderer;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.SnifferEntityRenderer;
import net.minecraft.client.render.entity.SnowGolemEntityRenderer;
import net.minecraft.client.render.entity.SpectralArrowEntityRenderer;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.SquidEntityRenderer;
import net.minecraft.client.render.entity.StrayEntityRenderer;
import net.minecraft.client.render.entity.StriderEntityRenderer;
import net.minecraft.client.render.entity.TadpoleEntityRenderer;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.TropicalFishEntityRenderer;
import net.minecraft.client.render.entity.TurtleEntityRenderer;
import net.minecraft.client.render.entity.UndeadHorseEntityRenderer;
import net.minecraft.client.render.entity.VexEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.VindicatorEntityRenderer;
import net.minecraft.client.render.entity.WanderingTraderEntityRenderer;
import net.minecraft.client.render.entity.WardenEntityRenderer;
import net.minecraft.client.render.entity.WindChargeEntityRenderer;
import net.minecraft.client.render.entity.WitchEntityRenderer;
import net.minecraft.client.render.entity.WitherEntityRenderer;
import net.minecraft.client.render.entity.WitherSkeletonEntityRenderer;
import net.minecraft.client.render.entity.WitherSkullEntityRenderer;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.ZoglinEntityRenderer;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.ZombieNautilusEntityRenderer;
import net.minecraft.client.render.entity.ZombieVillagerEntityRenderer;
import net.minecraft.client.render.entity.ZombifiedPiglinEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class EntityRendererFactories {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<EntityType<?>, EntityRendererFactory<?>> RENDERER_FACTORIES = new Object2ObjectOpenHashMap();

    public static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
        RENDERER_FACTORIES.put(type, factory);
    }

    public static Map<EntityType<?>, EntityRenderer<?, ?>> reloadEntityRenderers(EntityRendererFactory.Context ctx) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        RENDERER_FACTORIES.forEach((entityType, factory) -> {
            try {
                builder.put(entityType, factory.create(ctx));
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("Failed to create model for " + String.valueOf(Registries.ENTITY_TYPE.getId((EntityType<?>)entityType)), exception);
            }
        });
        return builder.build();
    }

    public static <T extends PlayerLikeEntity> Map<PlayerSkinType, PlayerEntityRenderer<T>> reloadPlayerRenderers(EntityRendererFactory.Context ctx) {
        try {
            return Map.of(PlayerSkinType.WIDE, new PlayerEntityRenderer(ctx, false), PlayerSkinType.SLIM, new PlayerEntityRenderer(ctx, true));
        }
        catch (Exception exception) {
            throw new IllegalArgumentException("Failed to create avatar models", exception);
        }
    }

    public static boolean isMissingRendererFactories() {
        boolean bl = true;
        for (EntityType entityType : Registries.ENTITY_TYPE) {
            if (entityType == EntityType.PLAYER || entityType == EntityType.MANNEQUIN || RENDERER_FACTORIES.containsKey(entityType)) continue;
            LOGGER.warn("No renderer registered for {}", (Object)Registries.ENTITY_TYPE.getId(entityType));
            bl = false;
        }
        return !bl;
    }

    static {
        EntityRendererFactories.register(EntityType.ACACIA_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.ACACIA_BOAT));
        EntityRendererFactories.register(EntityType.ACACIA_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.ACACIA_CHEST_BOAT));
        EntityRendererFactories.register(EntityType.ALLAY, AllayEntityRenderer::new);
        EntityRendererFactories.register(EntityType.AREA_EFFECT_CLOUD, EmptyEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ARMADILLO, ArmadilloEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ARMOR_STAND, ArmorStandEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ARROW, ArrowEntityRenderer::new);
        EntityRendererFactories.register(EntityType.AXOLOTL, AxolotlEntityRenderer::new);
        EntityRendererFactories.register(EntityType.BAMBOO_CHEST_RAFT, context -> new RaftEntityRenderer(context, EntityModelLayers.BAMBOO_CHEST_BOAT));
        EntityRendererFactories.register(EntityType.BAMBOO_RAFT, context -> new RaftEntityRenderer(context, EntityModelLayers.BAMBOO_BOAT));
        EntityRendererFactories.register(EntityType.BAT, BatEntityRenderer::new);
        EntityRendererFactories.register(EntityType.BEE, BeeEntityRenderer::new);
        EntityRendererFactories.register(EntityType.BIRCH_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.BIRCH_BOAT));
        EntityRendererFactories.register(EntityType.BIRCH_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.BIRCH_CHEST_BOAT));
        EntityRendererFactories.register(EntityType.BLAZE, BlazeEntityRenderer::new);
        EntityRendererFactories.register(EntityType.BLOCK_DISPLAY, DisplayEntityRenderer.BlockDisplayEntityRenderer::new);
        EntityRendererFactories.register(EntityType.BOGGED, BoggedEntityRenderer::new);
        EntityRendererFactories.register(EntityType.BREEZE, BreezeEntityRenderer::new);
        EntityRendererFactories.register(EntityType.BREEZE_WIND_CHARGE, WindChargeEntityRenderer::new);
        EntityRendererFactories.register(EntityType.CAMEL, CamelEntityRenderer::new);
        EntityRendererFactories.register(EntityType.CAMEL_HUSK, CamelHuskEntityRenderer::new);
        EntityRendererFactories.register(EntityType.CAT, CatEntityRenderer::new);
        EntityRendererFactories.register(EntityType.CAVE_SPIDER, CaveSpiderEntityRenderer::new);
        EntityRendererFactories.register(EntityType.CHERRY_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.CHERRY_BOAT));
        EntityRendererFactories.register(EntityType.CHERRY_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.CHERRY_CHEST_BOAT));
        EntityRendererFactories.register(EntityType.CHEST_MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.CHEST_MINECART));
        EntityRendererFactories.register(EntityType.CHICKEN, ChickenEntityRenderer::new);
        EntityRendererFactories.register(EntityType.COD, CodEntityRenderer::new);
        EntityRendererFactories.register(EntityType.COMMAND_BLOCK_MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.COMMAND_BLOCK_MINECART));
        EntityRendererFactories.register(EntityType.COPPER_GOLEM, CopperGolemEntityRenderer::new);
        EntityRendererFactories.register(EntityType.COW, CowEntityRenderer::new);
        EntityRendererFactories.register(EntityType.CREAKING, CreakingEntityRenderer::new);
        EntityRendererFactories.register(EntityType.CREEPER, CreeperEntityRenderer::new);
        EntityRendererFactories.register(EntityType.DARK_OAK_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.DARK_OAK_BOAT));
        EntityRendererFactories.register(EntityType.DARK_OAK_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.DARK_OAK_CHEST_BOAT));
        EntityRendererFactories.register(EntityType.DOLPHIN, DolphinEntityRenderer::new);
        EntityRendererFactories.register(EntityType.DONKEY, context -> new AbstractDonkeyEntityRenderer(context, AbstractDonkeyEntityRenderer.Type.DONKEY));
        EntityRendererFactories.register(EntityType.DRAGON_FIREBALL, DragonFireballEntityRenderer::new);
        EntityRendererFactories.register(EntityType.DROWNED, DrownedEntityRenderer::new);
        EntityRendererFactories.register(EntityType.EGG, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ELDER_GUARDIAN, ElderGuardianEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ENDERMAN, EndermanEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ENDERMITE, EndermiteEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ENDER_DRAGON, EnderDragonEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ENDER_PEARL, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register(EntityType.END_CRYSTAL, EndCrystalEntityRenderer::new);
        EntityRendererFactories.register(EntityType.EVOKER, EvokerEntityRenderer::new);
        EntityRendererFactories.register(EntityType.EVOKER_FANGS, EvokerFangsEntityRenderer::new);
        EntityRendererFactories.register(EntityType.EXPERIENCE_BOTTLE, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register(EntityType.EXPERIENCE_ORB, ExperienceOrbEntityRenderer::new);
        EntityRendererFactories.register(EntityType.EYE_OF_ENDER, context -> new FlyingItemEntityRenderer(context, 1.0f, true));
        EntityRendererFactories.register(EntityType.FALLING_BLOCK, FallingBlockEntityRenderer::new);
        EntityRendererFactories.register(EntityType.FIREBALL, context -> new FlyingItemEntityRenderer(context, 3.0f, true));
        EntityRendererFactories.register(EntityType.FIREWORK_ROCKET, FireworkRocketEntityRenderer::new);
        EntityRendererFactories.register(EntityType.FISHING_BOBBER, FishingBobberEntityRenderer::new);
        EntityRendererFactories.register(EntityType.FOX, FoxEntityRenderer::new);
        EntityRendererFactories.register(EntityType.FROG, FrogEntityRenderer::new);
        EntityRendererFactories.register(EntityType.FURNACE_MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.FURNACE_MINECART));
        EntityRendererFactories.register(EntityType.GHAST, GhastEntityRenderer::new);
        EntityRendererFactories.register(EntityType.HAPPY_GHAST, HappyGhastEntityRenderer::new);
        EntityRendererFactories.register(EntityType.GIANT, context -> new GiantEntityRenderer(context, 6.0f));
        EntityRendererFactories.register(EntityType.GLOW_ITEM_FRAME, ItemFrameEntityRenderer::new);
        EntityRendererFactories.register(EntityType.GLOW_SQUID, context -> new GlowSquidEntityRenderer(context, new SquidEntityModel(context.getPart(EntityModelLayers.GLOW_SQUID)), new SquidEntityModel(context.getPart(EntityModelLayers.GLOW_SQUID_BABY))));
        EntityRendererFactories.register(EntityType.GOAT, GoatEntityRenderer::new);
        EntityRendererFactories.register(EntityType.GUARDIAN, GuardianEntityRenderer::new);
        EntityRendererFactories.register(EntityType.HOGLIN, HoglinEntityRenderer::new);
        EntityRendererFactories.register(EntityType.HOPPER_MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.HOPPER_MINECART));
        EntityRendererFactories.register(EntityType.HORSE, HorseEntityRenderer::new);
        EntityRendererFactories.register(EntityType.HUSK, HuskEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ILLUSIONER, IllusionerEntityRenderer::new);
        EntityRendererFactories.register(EntityType.INTERACTION, EmptyEntityRenderer::new);
        EntityRendererFactories.register(EntityType.IRON_GOLEM, IronGolemEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ITEM, ItemEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ITEM_DISPLAY, DisplayEntityRenderer.ItemDisplayEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ITEM_FRAME, ItemFrameEntityRenderer::new);
        EntityRendererFactories.register(EntityType.JUNGLE_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.JUNGLE_BOAT));
        EntityRendererFactories.register(EntityType.JUNGLE_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.JUNGLE_CHEST_BOAT));
        EntityRendererFactories.register(EntityType.LEASH_KNOT, LeashKnotEntityRenderer::new);
        EntityRendererFactories.register(EntityType.LIGHTNING_BOLT, LightningEntityRenderer::new);
        EntityRendererFactories.register(EntityType.LINGERING_POTION, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register(EntityType.LLAMA, context -> new LlamaEntityRenderer(context, EntityModelLayers.LLAMA, EntityModelLayers.LLAMA_BABY));
        EntityRendererFactories.register(EntityType.LLAMA_SPIT, LlamaSpitEntityRenderer::new);
        EntityRendererFactories.register(EntityType.MAGMA_CUBE, MagmaCubeEntityRenderer::new);
        EntityRendererFactories.register(EntityType.MANGROVE_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.MANGROVE_BOAT));
        EntityRendererFactories.register(EntityType.MANGROVE_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.MANGROVE_CHEST_BOAT));
        EntityRendererFactories.register(EntityType.MARKER, EmptyEntityRenderer::new);
        EntityRendererFactories.register(EntityType.MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.MINECART));
        EntityRendererFactories.register(EntityType.MOOSHROOM, MooshroomEntityRenderer::new);
        EntityRendererFactories.register(EntityType.MULE, context -> new AbstractDonkeyEntityRenderer(context, AbstractDonkeyEntityRenderer.Type.MULE));
        EntityRendererFactories.register(EntityType.NAUTILUS, NautilusEntityRenderer::new);
        EntityRendererFactories.register(EntityType.OAK_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.OAK_BOAT));
        EntityRendererFactories.register(EntityType.OAK_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.OAK_CHEST_BOAT));
        EntityRendererFactories.register(EntityType.OCELOT, OcelotEntityRenderer::new);
        EntityRendererFactories.register(EntityType.OMINOUS_ITEM_SPAWNER, OminousItemSpawnerEntityRenderer::new);
        EntityRendererFactories.register(EntityType.PAINTING, PaintingEntityRenderer::new);
        EntityRendererFactories.register(EntityType.PALE_OAK_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.PALE_OAK_BOAT));
        EntityRendererFactories.register(EntityType.PALE_OAK_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.PALE_OAK_CHEST_BOAT));
        EntityRendererFactories.register(EntityType.PANDA, PandaEntityRenderer::new);
        EntityRendererFactories.register(EntityType.PARCHED, ParchedEntityRenderer::new);
        EntityRendererFactories.register(EntityType.PARROT, ParrotEntityRenderer::new);
        EntityRendererFactories.register(EntityType.PHANTOM, PhantomEntityRenderer::new);
        EntityRendererFactories.register(EntityType.PIG, PigEntityRenderer::new);
        EntityRendererFactories.register(EntityType.PIGLIN, context -> new PiglinEntityRenderer(context, EntityModelLayers.PIGLIN, EntityModelLayers.PIGLIN_BABY, EntityModelLayers.PIGLIN_EQUIPMENT, EntityModelLayers.PIGLIN_BABY_EQUIPMENT));
        EntityRendererFactories.register(EntityType.PIGLIN_BRUTE, context -> new PiglinEntityRenderer(context, EntityModelLayers.PIGLIN_BRUTE, EntityModelLayers.PIGLIN_BRUTE, EntityModelLayers.PIGLIN_BRUTE_EQUIPMENT, EntityModelLayers.PIGLIN_BRUTE_EQUIPMENT));
        EntityRendererFactories.register(EntityType.PILLAGER, PillagerEntityRenderer::new);
        EntityRendererFactories.register(EntityType.POLAR_BEAR, PolarBearEntityRenderer::new);
        EntityRendererFactories.register(EntityType.PUFFERFISH, PufferfishEntityRenderer::new);
        EntityRendererFactories.register(EntityType.RABBIT, RabbitEntityRenderer::new);
        EntityRendererFactories.register(EntityType.RAVAGER, RavagerEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SALMON, SalmonEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SHEEP, SheepEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SHULKER, ShulkerEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SHULKER_BULLET, ShulkerBulletEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SILVERFISH, SilverfishEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SKELETON, SkeletonEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SKELETON_HORSE, context -> new UndeadHorseEntityRenderer(context, UndeadHorseEntityRenderer.Type.SKELETON));
        EntityRendererFactories.register(EntityType.SLIME, SlimeEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SMALL_FIREBALL, context -> new FlyingItemEntityRenderer(context, 0.75f, true));
        EntityRendererFactories.register(EntityType.SNIFFER, SnifferEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SNOWBALL, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SNOW_GOLEM, SnowGolemEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SPAWNER_MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.SPAWNER_MINECART));
        EntityRendererFactories.register(EntityType.SPECTRAL_ARROW, SpectralArrowEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SPIDER, SpiderEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SPLASH_POTION, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register(EntityType.SPRUCE_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.SPRUCE_BOAT));
        EntityRendererFactories.register(EntityType.SPRUCE_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.SPRUCE_CHEST_BOAT));
        EntityRendererFactories.register(EntityType.SQUID, context -> new SquidEntityRenderer(context, new SquidEntityModel(context.getPart(EntityModelLayers.SQUID)), new SquidEntityModel(context.getPart(EntityModelLayers.SQUID_BABY))));
        EntityRendererFactories.register(EntityType.STRAY, StrayEntityRenderer::new);
        EntityRendererFactories.register(EntityType.STRIDER, StriderEntityRenderer::new);
        EntityRendererFactories.register(EntityType.TADPOLE, TadpoleEntityRenderer::new);
        EntityRendererFactories.register(EntityType.TEXT_DISPLAY, DisplayEntityRenderer.TextDisplayEntityRenderer::new);
        EntityRendererFactories.register(EntityType.TNT, TntEntityRenderer::new);
        EntityRendererFactories.register(EntityType.TNT_MINECART, TntMinecartEntityRenderer::new);
        EntityRendererFactories.register(EntityType.TRADER_LLAMA, context -> new LlamaEntityRenderer(context, EntityModelLayers.TRADER_LLAMA, EntityModelLayers.TRADER_LLAMA_BABY));
        EntityRendererFactories.register(EntityType.TRIDENT, TridentEntityRenderer::new);
        EntityRendererFactories.register(EntityType.TROPICAL_FISH, TropicalFishEntityRenderer::new);
        EntityRendererFactories.register(EntityType.TURTLE, TurtleEntityRenderer::new);
        EntityRendererFactories.register(EntityType.VEX, VexEntityRenderer::new);
        EntityRendererFactories.register(EntityType.VILLAGER, VillagerEntityRenderer::new);
        EntityRendererFactories.register(EntityType.VINDICATOR, VindicatorEntityRenderer::new);
        EntityRendererFactories.register(EntityType.WANDERING_TRADER, WanderingTraderEntityRenderer::new);
        EntityRendererFactories.register(EntityType.WARDEN, WardenEntityRenderer::new);
        EntityRendererFactories.register(EntityType.WIND_CHARGE, WindChargeEntityRenderer::new);
        EntityRendererFactories.register(EntityType.WITCH, WitchEntityRenderer::new);
        EntityRendererFactories.register(EntityType.WITHER, WitherEntityRenderer::new);
        EntityRendererFactories.register(EntityType.WITHER_SKELETON, WitherSkeletonEntityRenderer::new);
        EntityRendererFactories.register(EntityType.WITHER_SKULL, WitherSkullEntityRenderer::new);
        EntityRendererFactories.register(EntityType.WOLF, WolfEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ZOGLIN, ZoglinEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ZOMBIE, ZombieEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ZOMBIE_HORSE, context -> new UndeadHorseEntityRenderer(context, UndeadHorseEntityRenderer.Type.ZOMBIE));
        EntityRendererFactories.register(EntityType.ZOMBIE_NAUTILUS, ZombieNautilusEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ZOMBIE_VILLAGER, ZombieVillagerEntityRenderer::new);
        EntityRendererFactories.register(EntityType.ZOMBIFIED_PIGLIN, context -> new ZombifiedPiglinEntityRenderer(context, EntityModelLayers.ZOMBIFIED_PIGLIN, EntityModelLayers.ZOMBIFIED_PIGLIN_BABY, EntityModelLayers.ZOMBIFIED_PIGLIN_EQUIPMENT, EntityModelLayers.ZOMBIFIED_PIGLIN_BABY_EQUIPMENT));
    }
}
