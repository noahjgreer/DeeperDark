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
 *  net.minecraft.client.render.entity.AbstractDonkeyEntityRenderer
 *  net.minecraft.client.render.entity.AbstractDonkeyEntityRenderer$Type
 *  net.minecraft.client.render.entity.AllayEntityRenderer
 *  net.minecraft.client.render.entity.ArmadilloEntityRenderer
 *  net.minecraft.client.render.entity.ArmorStandEntityRenderer
 *  net.minecraft.client.render.entity.ArrowEntityRenderer
 *  net.minecraft.client.render.entity.AxolotlEntityRenderer
 *  net.minecraft.client.render.entity.BatEntityRenderer
 *  net.minecraft.client.render.entity.BeeEntityRenderer
 *  net.minecraft.client.render.entity.BlazeEntityRenderer
 *  net.minecraft.client.render.entity.BoatEntityRenderer
 *  net.minecraft.client.render.entity.BoggedEntityRenderer
 *  net.minecraft.client.render.entity.BreezeEntityRenderer
 *  net.minecraft.client.render.entity.CamelEntityRenderer
 *  net.minecraft.client.render.entity.CamelHuskEntityRenderer
 *  net.minecraft.client.render.entity.CatEntityRenderer
 *  net.minecraft.client.render.entity.CaveSpiderEntityRenderer
 *  net.minecraft.client.render.entity.ChickenEntityRenderer
 *  net.minecraft.client.render.entity.CodEntityRenderer
 *  net.minecraft.client.render.entity.CopperGolemEntityRenderer
 *  net.minecraft.client.render.entity.CowEntityRenderer
 *  net.minecraft.client.render.entity.CreakingEntityRenderer
 *  net.minecraft.client.render.entity.CreeperEntityRenderer
 *  net.minecraft.client.render.entity.DisplayEntityRenderer$BlockDisplayEntityRenderer
 *  net.minecraft.client.render.entity.DisplayEntityRenderer$ItemDisplayEntityRenderer
 *  net.minecraft.client.render.entity.DisplayEntityRenderer$TextDisplayEntityRenderer
 *  net.minecraft.client.render.entity.DolphinEntityRenderer
 *  net.minecraft.client.render.entity.DragonFireballEntityRenderer
 *  net.minecraft.client.render.entity.DrownedEntityRenderer
 *  net.minecraft.client.render.entity.ElderGuardianEntityRenderer
 *  net.minecraft.client.render.entity.EmptyEntityRenderer
 *  net.minecraft.client.render.entity.EndCrystalEntityRenderer
 *  net.minecraft.client.render.entity.EnderDragonEntityRenderer
 *  net.minecraft.client.render.entity.EndermanEntityRenderer
 *  net.minecraft.client.render.entity.EndermiteEntityRenderer
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactories
 *  net.minecraft.client.render.entity.EntityRendererFactory
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.EvokerEntityRenderer
 *  net.minecraft.client.render.entity.EvokerFangsEntityRenderer
 *  net.minecraft.client.render.entity.ExperienceOrbEntityRenderer
 *  net.minecraft.client.render.entity.FallingBlockEntityRenderer
 *  net.minecraft.client.render.entity.FireworkRocketEntityRenderer
 *  net.minecraft.client.render.entity.FishingBobberEntityRenderer
 *  net.minecraft.client.render.entity.FlyingItemEntityRenderer
 *  net.minecraft.client.render.entity.FoxEntityRenderer
 *  net.minecraft.client.render.entity.FrogEntityRenderer
 *  net.minecraft.client.render.entity.GhastEntityRenderer
 *  net.minecraft.client.render.entity.GiantEntityRenderer
 *  net.minecraft.client.render.entity.GlowSquidEntityRenderer
 *  net.minecraft.client.render.entity.GoatEntityRenderer
 *  net.minecraft.client.render.entity.GuardianEntityRenderer
 *  net.minecraft.client.render.entity.HappyGhastEntityRenderer
 *  net.minecraft.client.render.entity.HoglinEntityRenderer
 *  net.minecraft.client.render.entity.HorseEntityRenderer
 *  net.minecraft.client.render.entity.HuskEntityRenderer
 *  net.minecraft.client.render.entity.IllusionerEntityRenderer
 *  net.minecraft.client.render.entity.IronGolemEntityRenderer
 *  net.minecraft.client.render.entity.ItemEntityRenderer
 *  net.minecraft.client.render.entity.ItemFrameEntityRenderer
 *  net.minecraft.client.render.entity.LeashKnotEntityRenderer
 *  net.minecraft.client.render.entity.LightningEntityRenderer
 *  net.minecraft.client.render.entity.LlamaEntityRenderer
 *  net.minecraft.client.render.entity.LlamaSpitEntityRenderer
 *  net.minecraft.client.render.entity.MagmaCubeEntityRenderer
 *  net.minecraft.client.render.entity.MinecartEntityRenderer
 *  net.minecraft.client.render.entity.MooshroomEntityRenderer
 *  net.minecraft.client.render.entity.NautilusEntityRenderer
 *  net.minecraft.client.render.entity.OcelotEntityRenderer
 *  net.minecraft.client.render.entity.OminousItemSpawnerEntityRenderer
 *  net.minecraft.client.render.entity.PaintingEntityRenderer
 *  net.minecraft.client.render.entity.PandaEntityRenderer
 *  net.minecraft.client.render.entity.ParchedEntityRenderer
 *  net.minecraft.client.render.entity.ParrotEntityRenderer
 *  net.minecraft.client.render.entity.PhantomEntityRenderer
 *  net.minecraft.client.render.entity.PigEntityRenderer
 *  net.minecraft.client.render.entity.PiglinEntityRenderer
 *  net.minecraft.client.render.entity.PillagerEntityRenderer
 *  net.minecraft.client.render.entity.PlayerEntityRenderer
 *  net.minecraft.client.render.entity.PolarBearEntityRenderer
 *  net.minecraft.client.render.entity.PufferfishEntityRenderer
 *  net.minecraft.client.render.entity.RabbitEntityRenderer
 *  net.minecraft.client.render.entity.RaftEntityRenderer
 *  net.minecraft.client.render.entity.RavagerEntityRenderer
 *  net.minecraft.client.render.entity.SalmonEntityRenderer
 *  net.minecraft.client.render.entity.SheepEntityRenderer
 *  net.minecraft.client.render.entity.ShulkerBulletEntityRenderer
 *  net.minecraft.client.render.entity.ShulkerEntityRenderer
 *  net.minecraft.client.render.entity.SilverfishEntityRenderer
 *  net.minecraft.client.render.entity.SkeletonEntityRenderer
 *  net.minecraft.client.render.entity.SlimeEntityRenderer
 *  net.minecraft.client.render.entity.SnifferEntityRenderer
 *  net.minecraft.client.render.entity.SnowGolemEntityRenderer
 *  net.minecraft.client.render.entity.SpectralArrowEntityRenderer
 *  net.minecraft.client.render.entity.SpiderEntityRenderer
 *  net.minecraft.client.render.entity.SquidEntityRenderer
 *  net.minecraft.client.render.entity.StrayEntityRenderer
 *  net.minecraft.client.render.entity.StriderEntityRenderer
 *  net.minecraft.client.render.entity.TadpoleEntityRenderer
 *  net.minecraft.client.render.entity.TntEntityRenderer
 *  net.minecraft.client.render.entity.TntMinecartEntityRenderer
 *  net.minecraft.client.render.entity.TridentEntityRenderer
 *  net.minecraft.client.render.entity.TropicalFishEntityRenderer
 *  net.minecraft.client.render.entity.TurtleEntityRenderer
 *  net.minecraft.client.render.entity.UndeadHorseEntityRenderer
 *  net.minecraft.client.render.entity.UndeadHorseEntityRenderer$Type
 *  net.minecraft.client.render.entity.VexEntityRenderer
 *  net.minecraft.client.render.entity.VillagerEntityRenderer
 *  net.minecraft.client.render.entity.VindicatorEntityRenderer
 *  net.minecraft.client.render.entity.WanderingTraderEntityRenderer
 *  net.minecraft.client.render.entity.WardenEntityRenderer
 *  net.minecraft.client.render.entity.WindChargeEntityRenderer
 *  net.minecraft.client.render.entity.WitchEntityRenderer
 *  net.minecraft.client.render.entity.WitherEntityRenderer
 *  net.minecraft.client.render.entity.WitherSkeletonEntityRenderer
 *  net.minecraft.client.render.entity.WitherSkullEntityRenderer
 *  net.minecraft.client.render.entity.WolfEntityRenderer
 *  net.minecraft.client.render.entity.ZoglinEntityRenderer
 *  net.minecraft.client.render.entity.ZombieEntityRenderer
 *  net.minecraft.client.render.entity.ZombieNautilusEntityRenderer
 *  net.minecraft.client.render.entity.ZombieVillagerEntityRenderer
 *  net.minecraft.client.render.entity.ZombifiedPiglinEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.SquidEntityModel
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.PlayerLikeEntity
 *  net.minecraft.entity.player.PlayerSkinType
 *  net.minecraft.registry.Registries
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

/*
 * Exception performing whole class analysis ignored.
 */
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
                builder.put(entityType, (Object)factory.create(ctx));
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("Failed to create model for " + String.valueOf(Registries.ENTITY_TYPE.getId(entityType)), exception);
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
            LOGGER.warn("No renderer registered for {}", (Object)Registries.ENTITY_TYPE.getId((Object)entityType));
            bl = false;
        }
        return !bl;
    }

    static {
        EntityRendererFactories.register((EntityType)EntityType.ACACIA_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.ACACIA_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.ACACIA_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.ACACIA_CHEST_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.ALLAY, AllayEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.AREA_EFFECT_CLOUD, EmptyEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ARMADILLO, ArmadilloEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ARMOR_STAND, ArmorStandEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ARROW, ArrowEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.AXOLOTL, AxolotlEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.BAMBOO_CHEST_RAFT, context -> new RaftEntityRenderer(context, EntityModelLayers.BAMBOO_CHEST_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.BAMBOO_RAFT, context -> new RaftEntityRenderer(context, EntityModelLayers.BAMBOO_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.BAT, BatEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.BEE, BeeEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.BIRCH_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.BIRCH_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.BIRCH_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.BIRCH_CHEST_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.BLAZE, BlazeEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.BLOCK_DISPLAY, DisplayEntityRenderer.BlockDisplayEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.BOGGED, BoggedEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.BREEZE, BreezeEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.BREEZE_WIND_CHARGE, WindChargeEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.CAMEL, CamelEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.CAMEL_HUSK, CamelHuskEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.CAT, CatEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.CAVE_SPIDER, CaveSpiderEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.CHERRY_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.CHERRY_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.CHERRY_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.CHERRY_CHEST_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.CHEST_MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.CHEST_MINECART));
        EntityRendererFactories.register((EntityType)EntityType.CHICKEN, ChickenEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.COD, CodEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.COMMAND_BLOCK_MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.COMMAND_BLOCK_MINECART));
        EntityRendererFactories.register((EntityType)EntityType.COPPER_GOLEM, CopperGolemEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.COW, CowEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.CREAKING, CreakingEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.CREEPER, CreeperEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.DARK_OAK_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.DARK_OAK_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.DARK_OAK_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.DARK_OAK_CHEST_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.DOLPHIN, DolphinEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.DONKEY, context -> new AbstractDonkeyEntityRenderer(context, AbstractDonkeyEntityRenderer.Type.DONKEY));
        EntityRendererFactories.register((EntityType)EntityType.DRAGON_FIREBALL, DragonFireballEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.DROWNED, DrownedEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.EGG, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ELDER_GUARDIAN, ElderGuardianEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ENDERMAN, EndermanEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ENDERMITE, EndermiteEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ENDER_DRAGON, EnderDragonEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ENDER_PEARL, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.END_CRYSTAL, EndCrystalEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.EVOKER, EvokerEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.EVOKER_FANGS, EvokerFangsEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.EXPERIENCE_BOTTLE, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.EXPERIENCE_ORB, ExperienceOrbEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.EYE_OF_ENDER, context -> new FlyingItemEntityRenderer(context, 1.0f, true));
        EntityRendererFactories.register((EntityType)EntityType.FALLING_BLOCK, FallingBlockEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.FIREBALL, context -> new FlyingItemEntityRenderer(context, 3.0f, true));
        EntityRendererFactories.register((EntityType)EntityType.FIREWORK_ROCKET, FireworkRocketEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.FISHING_BOBBER, FishingBobberEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.FOX, FoxEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.FROG, FrogEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.FURNACE_MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.FURNACE_MINECART));
        EntityRendererFactories.register((EntityType)EntityType.GHAST, GhastEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.HAPPY_GHAST, HappyGhastEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.GIANT, context -> new GiantEntityRenderer(context, 6.0f));
        EntityRendererFactories.register((EntityType)EntityType.GLOW_ITEM_FRAME, ItemFrameEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.GLOW_SQUID, context -> new GlowSquidEntityRenderer(context, new SquidEntityModel(context.getPart(EntityModelLayers.GLOW_SQUID)), new SquidEntityModel(context.getPart(EntityModelLayers.GLOW_SQUID_BABY))));
        EntityRendererFactories.register((EntityType)EntityType.GOAT, GoatEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.GUARDIAN, GuardianEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.HOGLIN, HoglinEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.HOPPER_MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.HOPPER_MINECART));
        EntityRendererFactories.register((EntityType)EntityType.HORSE, HorseEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.HUSK, HuskEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ILLUSIONER, IllusionerEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.INTERACTION, EmptyEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.IRON_GOLEM, IronGolemEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ITEM, ItemEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ITEM_DISPLAY, DisplayEntityRenderer.ItemDisplayEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ITEM_FRAME, ItemFrameEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.JUNGLE_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.JUNGLE_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.JUNGLE_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.JUNGLE_CHEST_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.LEASH_KNOT, LeashKnotEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.LIGHTNING_BOLT, LightningEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.LINGERING_POTION, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.LLAMA, context -> new LlamaEntityRenderer(context, EntityModelLayers.LLAMA, EntityModelLayers.LLAMA_BABY));
        EntityRendererFactories.register((EntityType)EntityType.LLAMA_SPIT, LlamaSpitEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.MAGMA_CUBE, MagmaCubeEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.MANGROVE_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.MANGROVE_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.MANGROVE_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.MANGROVE_CHEST_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.MARKER, EmptyEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.MINECART));
        EntityRendererFactories.register((EntityType)EntityType.MOOSHROOM, MooshroomEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.MULE, context -> new AbstractDonkeyEntityRenderer(context, AbstractDonkeyEntityRenderer.Type.MULE));
        EntityRendererFactories.register((EntityType)EntityType.NAUTILUS, NautilusEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.OAK_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.OAK_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.OAK_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.OAK_CHEST_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.OCELOT, OcelotEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.OMINOUS_ITEM_SPAWNER, OminousItemSpawnerEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.PAINTING, PaintingEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.PALE_OAK_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.PALE_OAK_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.PALE_OAK_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.PALE_OAK_CHEST_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.PANDA, PandaEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.PARCHED, ParchedEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.PARROT, ParrotEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.PHANTOM, PhantomEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.PIG, PigEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.PIGLIN, context -> new PiglinEntityRenderer(context, EntityModelLayers.PIGLIN, EntityModelLayers.PIGLIN_BABY, EntityModelLayers.PIGLIN_EQUIPMENT, EntityModelLayers.PIGLIN_BABY_EQUIPMENT));
        EntityRendererFactories.register((EntityType)EntityType.PIGLIN_BRUTE, context -> new PiglinEntityRenderer(context, EntityModelLayers.PIGLIN_BRUTE, EntityModelLayers.PIGLIN_BRUTE, EntityModelLayers.PIGLIN_BRUTE_EQUIPMENT, EntityModelLayers.PIGLIN_BRUTE_EQUIPMENT));
        EntityRendererFactories.register((EntityType)EntityType.PILLAGER, PillagerEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.POLAR_BEAR, PolarBearEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.PUFFERFISH, PufferfishEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.RABBIT, RabbitEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.RAVAGER, RavagerEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SALMON, SalmonEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SHEEP, SheepEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SHULKER, ShulkerEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SHULKER_BULLET, ShulkerBulletEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SILVERFISH, SilverfishEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SKELETON, SkeletonEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SKELETON_HORSE, context -> new UndeadHorseEntityRenderer(context, UndeadHorseEntityRenderer.Type.SKELETON));
        EntityRendererFactories.register((EntityType)EntityType.SLIME, SlimeEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SMALL_FIREBALL, context -> new FlyingItemEntityRenderer(context, 0.75f, true));
        EntityRendererFactories.register((EntityType)EntityType.SNIFFER, SnifferEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SNOWBALL, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SNOW_GOLEM, SnowGolemEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SPAWNER_MINECART, context -> new MinecartEntityRenderer(context, EntityModelLayers.SPAWNER_MINECART));
        EntityRendererFactories.register((EntityType)EntityType.SPECTRAL_ARROW, SpectralArrowEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SPIDER, SpiderEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SPLASH_POTION, FlyingItemEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.SPRUCE_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.SPRUCE_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.SPRUCE_CHEST_BOAT, context -> new BoatEntityRenderer(context, EntityModelLayers.SPRUCE_CHEST_BOAT));
        EntityRendererFactories.register((EntityType)EntityType.SQUID, context -> new SquidEntityRenderer(context, new SquidEntityModel(context.getPart(EntityModelLayers.SQUID)), new SquidEntityModel(context.getPart(EntityModelLayers.SQUID_BABY))));
        EntityRendererFactories.register((EntityType)EntityType.STRAY, StrayEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.STRIDER, StriderEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.TADPOLE, TadpoleEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.TEXT_DISPLAY, DisplayEntityRenderer.TextDisplayEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.TNT, TntEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.TNT_MINECART, TntMinecartEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.TRADER_LLAMA, context -> new LlamaEntityRenderer(context, EntityModelLayers.TRADER_LLAMA, EntityModelLayers.TRADER_LLAMA_BABY));
        EntityRendererFactories.register((EntityType)EntityType.TRIDENT, TridentEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.TROPICAL_FISH, TropicalFishEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.TURTLE, TurtleEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.VEX, VexEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.VILLAGER, VillagerEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.VINDICATOR, VindicatorEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.WANDERING_TRADER, WanderingTraderEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.WARDEN, WardenEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.WIND_CHARGE, WindChargeEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.WITCH, WitchEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.WITHER, WitherEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.WITHER_SKELETON, WitherSkeletonEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.WITHER_SKULL, WitherSkullEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.WOLF, WolfEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ZOGLIN, ZoglinEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ZOMBIE, ZombieEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ZOMBIE_HORSE, context -> new UndeadHorseEntityRenderer(context, UndeadHorseEntityRenderer.Type.ZOMBIE));
        EntityRendererFactories.register((EntityType)EntityType.ZOMBIE_NAUTILUS, ZombieNautilusEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ZOMBIE_VILLAGER, ZombieVillagerEntityRenderer::new);
        EntityRendererFactories.register((EntityType)EntityType.ZOMBIFIED_PIGLIN, context -> new ZombifiedPiglinEntityRenderer(context, EntityModelLayers.ZOMBIFIED_PIGLIN, EntityModelLayers.ZOMBIFIED_PIGLIN_BABY, EntityModelLayers.ZOMBIFIED_PIGLIN_EQUIPMENT, EntityModelLayers.ZOMBIFIED_PIGLIN_BABY_EQUIPMENT));
    }
}

