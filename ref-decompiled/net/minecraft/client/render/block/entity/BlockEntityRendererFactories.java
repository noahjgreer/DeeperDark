/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.client.render.block.entity.BannerBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BedBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BellBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactories
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.BrushableBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.CampfireBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.ChestBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.CopperGolemStatueBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.DecoratedPotBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.EndGatewayBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.LecternBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.PistonBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.ShelfBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.SignBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.SkullBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.TestInstanceBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.VaultBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.registry.Registries
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.BrushableBlockEntityRenderer;
import net.minecraft.client.render.block.entity.CampfireBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.CopperGolemStatueBlockEntityRenderer;
import net.minecraft.client.render.block.entity.DecoratedPotBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndGatewayBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.LecternBlockEntityRenderer;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ShelfBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.client.render.block.entity.TestInstanceBlockEntityRenderer;
import net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.VaultBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.registry.Registries;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BlockEntityRendererFactories {
    private static final Map<BlockEntityType<?>, BlockEntityRendererFactory<?, ?>> FACTORIES = Maps.newHashMap();

    public static <T extends BlockEntity, S extends BlockEntityRenderState> void register(BlockEntityType<? extends T> type, BlockEntityRendererFactory<T, S> factory) {
        FACTORIES.put(type, factory);
    }

    public static Map<BlockEntityType<?>, BlockEntityRenderer<?, ?>> reload(BlockEntityRendererFactory.Context args) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        FACTORIES.forEach((type, factory) -> {
            try {
                builder.put(type, (Object)factory.create(args));
            }
            catch (Exception exception) {
                throw new IllegalStateException("Failed to create model for " + String.valueOf(Registries.BLOCK_ENTITY_TYPE.getId(type)), exception);
            }
        });
        return builder.build();
    }

    static {
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.SIGN, SignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.HANGING_SIGN, HangingSignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.MOB_SPAWNER, MobSpawnerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.PISTON, context -> new PistonBlockEntityRenderer());
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.CHEST, ChestBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.ENDER_CHEST, ChestBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.TRAPPED_CHEST, ChestBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.ENCHANTING_TABLE, EnchantingTableBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.LECTERN, LecternBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.END_PORTAL, context -> new EndPortalBlockEntityRenderer());
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.END_GATEWAY, context -> new EndGatewayBlockEntityRenderer());
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.BEACON, context -> new BeaconBlockEntityRenderer());
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.SKULL, SkullBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.BANNER, BannerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.STRUCTURE_BLOCK, context -> new StructureBlockBlockEntityRenderer());
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.TEST_INSTANCE_BLOCK, context -> new TestInstanceBlockEntityRenderer());
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.BED, BedBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.CONDUIT, ConduitBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.BELL, BellBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.CAMPFIRE, CampfireBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.BRUSHABLE_BLOCK, BrushableBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.DECORATED_POT, DecoratedPotBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.TRIAL_SPAWNER, TrialSpawnerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.VAULT, VaultBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.COPPER_GOLEM_STATUE, CopperGolemStatueBlockEntityRenderer::new);
        BlockEntityRendererFactories.register((BlockEntityType)BlockEntityType.SHELF, ShelfBlockEntityRenderer::new);
    }
}

