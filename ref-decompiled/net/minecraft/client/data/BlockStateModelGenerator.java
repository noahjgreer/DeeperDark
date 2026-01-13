/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BeehiveBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ChiseledBookshelfBlock
 *  net.minecraft.block.CopperGolemStatueBlock
 *  net.minecraft.block.CopperGolemStatueBlock$Pose
 *  net.minecraft.block.CrafterBlock
 *  net.minecraft.block.CreakingHeartBlock
 *  net.minecraft.block.DriedGhastBlock
 *  net.minecraft.block.HangingMossBlock
 *  net.minecraft.block.LeveledCauldronBlock
 *  net.minecraft.block.LightBlock
 *  net.minecraft.block.MultifaceBlock
 *  net.minecraft.block.Oxidizable$OxidationLevel
 *  net.minecraft.block.PaleMossCarpetBlock
 *  net.minecraft.block.PitcherCropBlock
 *  net.minecraft.block.PropaguleBlock
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.SkullBlock$Type
 *  net.minecraft.block.SnifferEggBlock
 *  net.minecraft.block.TestBlock
 *  net.minecraft.block.VaultBlock
 *  net.minecraft.block.enums.Attachment
 *  net.minecraft.block.enums.BambooLeaves
 *  net.minecraft.block.enums.BlockFace
 *  net.minecraft.block.enums.BlockHalf
 *  net.minecraft.block.enums.ComparatorMode
 *  net.minecraft.block.enums.CreakingHeartState
 *  net.minecraft.block.enums.DoorHinge
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.block.enums.Orientation
 *  net.minecraft.block.enums.PistonType
 *  net.minecraft.block.enums.RailShape
 *  net.minecraft.block.enums.SculkSensorPhase
 *  net.minecraft.block.enums.SideChainPart
 *  net.minecraft.block.enums.SlabType
 *  net.minecraft.block.enums.StairShape
 *  net.minecraft.block.enums.TestBlockMode
 *  net.minecraft.block.enums.Thickness
 *  net.minecraft.block.enums.Tilt
 *  net.minecraft.block.enums.WallShape
 *  net.minecraft.block.enums.WireConnection
 *  net.minecraft.client.data.BlockModelDefinitionCreator
 *  net.minecraft.client.data.BlockStateModelGenerator
 *  net.minecraft.client.data.BlockStateModelGenerator$1
 *  net.minecraft.client.data.BlockStateModelGenerator$BlockTexturePool
 *  net.minecraft.client.data.BlockStateModelGenerator$ChiseledBookshelfModelCacheKey
 *  net.minecraft.client.data.BlockStateModelGenerator$CrossType
 *  net.minecraft.client.data.BlockStateModelGenerator$LogTexturePool
 *  net.minecraft.client.data.BlockStateModelGenerator$StateFactory
 *  net.minecraft.client.data.BlockStateVariantMap
 *  net.minecraft.client.data.BlockStateVariantMap$DoubleProperty
 *  net.minecraft.client.data.BlockStateVariantMap$SingleProperty
 *  net.minecraft.client.data.ItemModelOutput
 *  net.minecraft.client.data.ItemModels
 *  net.minecraft.client.data.Model
 *  net.minecraft.client.data.ModelIds
 *  net.minecraft.client.data.ModelSupplier
 *  net.minecraft.client.data.Models
 *  net.minecraft.client.data.MultipartBlockModelDefinitionCreator
 *  net.minecraft.client.data.TextureKey
 *  net.minecraft.client.data.TextureMap
 *  net.minecraft.client.data.TexturedModel
 *  net.minecraft.client.data.TexturedModel$Factory
 *  net.minecraft.client.data.VariantsBlockModelDefinitionCreator
 *  net.minecraft.client.render.item.model.ItemModel$Unbaked
 *  net.minecraft.client.render.item.model.special.BannerModelRenderer$Unbaked
 *  net.minecraft.client.render.item.model.special.BedModelRenderer$Unbaked
 *  net.minecraft.client.render.item.model.special.ChestModelRenderer
 *  net.minecraft.client.render.item.model.special.ChestModelRenderer$Unbaked
 *  net.minecraft.client.render.item.model.special.ConduitModelRenderer$Unbaked
 *  net.minecraft.client.render.item.model.special.CopperGolemStatueModelRenderer$Unbaked
 *  net.minecraft.client.render.item.model.special.DecoratedPotModelRenderer$Unbaked
 *  net.minecraft.client.render.item.model.special.HeadModelRenderer$Unbaked
 *  net.minecraft.client.render.item.model.special.PlayerHeadModelRenderer$Unbaked
 *  net.minecraft.client.render.item.model.special.ShulkerBoxModelRenderer$Unbaked
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer$Unbaked
 *  net.minecraft.client.render.item.tint.GrassTintSource
 *  net.minecraft.client.render.item.tint.TintSource
 *  net.minecraft.client.render.model.json.ModelVariant
 *  net.minecraft.client.render.model.json.ModelVariantOperator
 *  net.minecraft.client.render.model.json.MultipartModelCombinedCondition
 *  net.minecraft.client.render.model.json.MultipartModelCombinedCondition$LogicalOperator
 *  net.minecraft.client.render.model.json.MultipartModelCondition
 *  net.minecraft.client.render.model.json.MultipartModelConditionBuilder
 *  net.minecraft.client.render.model.json.WeightedVariant
 *  net.minecraft.data.family.BlockFamilies
 *  net.minecraft.data.family.BlockFamily
 *  net.minecraft.data.family.BlockFamily$Variant
 *  net.minecraft.item.Item
 *  net.minecraft.item.Items
 *  net.minecraft.state.State
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.collection.Pool
 *  net.minecraft.util.collection.Weighted
 *  net.minecraft.util.math.AxisRotation
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.CopperGolemStatueBlock;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.block.DriedGhastBlock;
import net.minecraft.block.HangingMossBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.LightBlock;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.PaleMossCarpetBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.PropaguleBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.SnifferEggBlock;
import net.minecraft.block.TestBlock;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.block.enums.CreakingHeartState;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.Orientation;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.block.enums.SideChainPart;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.TestBlockMode;
import net.minecraft.block.enums.Thickness;
import net.minecraft.block.enums.Tilt;
import net.minecraft.block.enums.WallShape;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.client.data.BlockModelDefinitionCreator;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.ItemModelOutput;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.MultipartBlockModelDefinitionCreator;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.TexturedModel;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.special.BannerModelRenderer;
import net.minecraft.client.render.item.model.special.BedModelRenderer;
import net.minecraft.client.render.item.model.special.ChestModelRenderer;
import net.minecraft.client.render.item.model.special.ConduitModelRenderer;
import net.minecraft.client.render.item.model.special.CopperGolemStatueModelRenderer;
import net.minecraft.client.render.item.model.special.DecoratedPotModelRenderer;
import net.minecraft.client.render.item.model.special.HeadModelRenderer;
import net.minecraft.client.render.item.model.special.PlayerHeadModelRenderer;
import net.minecraft.client.render.item.model.special.ShulkerBoxModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.tint.GrassTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.MultipartModelCombinedCondition;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.client.render.model.json.MultipartModelConditionBuilder;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.State;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BlockStateModelGenerator {
    public final Consumer<BlockModelDefinitionCreator> blockStateCollector;
    public final ItemModelOutput itemModelOutput;
    public final BiConsumer<Identifier, ModelSupplier> modelCollector;
    static final List<Block> UNORIENTABLE_TRAPDOORS = List.of(Blocks.OAK_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR);
    public static final ModelVariantOperator NO_OP = variant -> variant;
    public static final ModelVariantOperator UV_LOCK = ModelVariantOperator.UV_LOCK.withValue((Object)true);
    public static final ModelVariantOperator ROTATE_X_90 = ModelVariantOperator.ROTATION_X.withValue((Object)AxisRotation.R90);
    public static final ModelVariantOperator ROTATE_X_180 = ModelVariantOperator.ROTATION_X.withValue((Object)AxisRotation.R180);
    public static final ModelVariantOperator ROTATE_X_270 = ModelVariantOperator.ROTATION_X.withValue((Object)AxisRotation.R270);
    public static final ModelVariantOperator ROTATE_Y_90 = ModelVariantOperator.ROTATION_Y.withValue((Object)AxisRotation.R90);
    public static final ModelVariantOperator ROTATE_Y_180 = ModelVariantOperator.ROTATION_Y.withValue((Object)AxisRotation.R180);
    public static final ModelVariantOperator ROTATE_Y_270 = ModelVariantOperator.ROTATION_Y.withValue((Object)AxisRotation.R270);
    private static final Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> FLOWERBED_MODEL_1_CONDITION_FUNCTION = builder -> builder;
    private static final Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> FLOWERBED_MODEL_2_CONDITION_FUNCTION = builder -> builder.put((Property)Properties.FLOWER_AMOUNT, (Comparable)Integer.valueOf(2), (Comparable[])new Integer[]{3, 4});
    private static final Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> FLOWERBED_MODEL_3_CONDITION_FUNCTION = builder -> builder.put((Property)Properties.FLOWER_AMOUNT, (Comparable)Integer.valueOf(3), (Comparable[])new Integer[]{4});
    private static final Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> FLOWERBED_MODEL_4_CONDITION_FUNCTION = builder -> builder.put((Property)Properties.FLOWER_AMOUNT, (Comparable)Integer.valueOf(4));
    private static final Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> LEAF_LITTER_MODEL_1_CONDITION_FUNCTION = builder -> builder.put((Property)Properties.SEGMENT_AMOUNT, (Comparable)Integer.valueOf(1));
    private static final Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> LEAF_LITTER_MODEL_2_CONDITION_FUNCTION = builder -> builder.put((Property)Properties.SEGMENT_AMOUNT, (Comparable)Integer.valueOf(2), (Comparable[])new Integer[]{3});
    private static final Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> LEAF_LITTER_MODEL_3_CONDITION_FUNCTION = builder -> builder.put((Property)Properties.SEGMENT_AMOUNT, (Comparable)Integer.valueOf(3));
    private static final Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> LEAF_LITTER_MODEL_4_CONDITION_FUNCTION = builder -> builder.put((Property)Properties.SEGMENT_AMOUNT, (Comparable)Integer.valueOf(4));
    static final Map<Block, StateFactory> BASE_WITH_CUSTOM_GENERATOR = Map.of(Blocks.STONE, BlockStateModelGenerator::createStoneState, Blocks.DEEPSLATE, BlockStateModelGenerator::createDeepslateState, Blocks.MUD_BRICKS, BlockStateModelGenerator::createMudBrickState);
    private static final BlockStateVariantMap<ModelVariantOperator> NORTH_DEFAULT_ROTATION_OPERATIONS = BlockStateVariantMap.operations((Property)Properties.FACING).register((Comparable)Direction.DOWN, (Object)ROTATE_X_90).register((Comparable)Direction.UP, (Object)ROTATE_X_270).register((Comparable)Direction.NORTH, (Object)NO_OP).register((Comparable)Direction.SOUTH, (Object)ROTATE_Y_180).register((Comparable)Direction.WEST, (Object)ROTATE_Y_270).register((Comparable)Direction.EAST, (Object)ROTATE_Y_90);
    private static final BlockStateVariantMap<ModelVariantOperator> UP_DEFAULT_ROTATION_OPERATIONS = BlockStateVariantMap.operations((Property)Properties.FACING).register((Comparable)Direction.DOWN, (Object)ROTATE_X_180).register((Comparable)Direction.UP, (Object)NO_OP).register((Comparable)Direction.NORTH, (Object)ROTATE_X_90).register((Comparable)Direction.SOUTH, (Object)ROTATE_X_90.then(ROTATE_Y_180)).register((Comparable)Direction.WEST, (Object)ROTATE_X_90.then(ROTATE_Y_270)).register((Comparable)Direction.EAST, (Object)ROTATE_X_90.then(ROTATE_Y_90));
    private static final BlockStateVariantMap<ModelVariantOperator> EAST_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS = BlockStateVariantMap.operations((Property)Properties.HORIZONTAL_FACING).register((Comparable)Direction.EAST, (Object)NO_OP).register((Comparable)Direction.SOUTH, (Object)ROTATE_Y_90).register((Comparable)Direction.WEST, (Object)ROTATE_Y_180).register((Comparable)Direction.NORTH, (Object)ROTATE_Y_270);
    private static final BlockStateVariantMap<ModelVariantOperator> SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS = BlockStateVariantMap.operations((Property)Properties.HORIZONTAL_FACING).register((Comparable)Direction.SOUTH, (Object)NO_OP).register((Comparable)Direction.WEST, (Object)ROTATE_Y_90).register((Comparable)Direction.NORTH, (Object)ROTATE_Y_180).register((Comparable)Direction.EAST, (Object)ROTATE_Y_270);
    private static final BlockStateVariantMap<ModelVariantOperator> NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS = BlockStateVariantMap.operations((Property)Properties.HORIZONTAL_FACING).register((Comparable)Direction.EAST, (Object)ROTATE_Y_90).register((Comparable)Direction.SOUTH, (Object)ROTATE_Y_180).register((Comparable)Direction.WEST, (Object)ROTATE_Y_270).register((Comparable)Direction.NORTH, (Object)NO_OP);
    static final Map<Block, TexturedModel> TEXTURED_MODELS = ImmutableMap.builder().put((Object)Blocks.SANDSTONE, (Object)TexturedModel.SIDE_TOP_BOTTOM_WALL.get(Blocks.SANDSTONE)).put((Object)Blocks.RED_SANDSTONE, (Object)TexturedModel.SIDE_TOP_BOTTOM_WALL.get(Blocks.RED_SANDSTONE)).put((Object)Blocks.SMOOTH_SANDSTONE, (Object)TexturedModel.getCubeAll((Identifier)TextureMap.getSubId((Block)Blocks.SANDSTONE, (String)"_top"))).put((Object)Blocks.SMOOTH_RED_SANDSTONE, (Object)TexturedModel.getCubeAll((Identifier)TextureMap.getSubId((Block)Blocks.RED_SANDSTONE, (String)"_top"))).put((Object)Blocks.CUT_SANDSTONE, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.SANDSTONE).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getId((Block)Blocks.CUT_SANDSTONE)))).put((Object)Blocks.CUT_RED_SANDSTONE, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.RED_SANDSTONE).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getId((Block)Blocks.CUT_RED_SANDSTONE)))).put((Object)Blocks.QUARTZ_BLOCK, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.QUARTZ_BLOCK)).put((Object)Blocks.SMOOTH_QUARTZ, (Object)TexturedModel.getCubeAll((Identifier)TextureMap.getSubId((Block)Blocks.QUARTZ_BLOCK, (String)"_bottom"))).put((Object)Blocks.BLACKSTONE, (Object)TexturedModel.SIDE_END_WALL.get(Blocks.BLACKSTONE)).put((Object)Blocks.DEEPSLATE, (Object)TexturedModel.SIDE_END_WALL.get(Blocks.DEEPSLATE)).put((Object)Blocks.CHISELED_QUARTZ_BLOCK, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.CHISELED_QUARTZ_BLOCK).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getId((Block)Blocks.CHISELED_QUARTZ_BLOCK)))).put((Object)Blocks.CHISELED_SANDSTONE, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.CHISELED_SANDSTONE).textures(textureMap -> {
        textureMap.put(TextureKey.END, TextureMap.getSubId((Block)Blocks.SANDSTONE, (String)"_top"));
        textureMap.put(TextureKey.SIDE, TextureMap.getId((Block)Blocks.CHISELED_SANDSTONE));
    })).put((Object)Blocks.CHISELED_RED_SANDSTONE, (Object)TexturedModel.CUBE_COLUMN.get(Blocks.CHISELED_RED_SANDSTONE).textures(textureMap -> {
        textureMap.put(TextureKey.END, TextureMap.getSubId((Block)Blocks.RED_SANDSTONE, (String)"_top"));
        textureMap.put(TextureKey.SIDE, TextureMap.getId((Block)Blocks.CHISELED_RED_SANDSTONE));
    })).put((Object)Blocks.CHISELED_TUFF_BRICKS, (Object)TexturedModel.SIDE_END_WALL.get(Blocks.CHISELED_TUFF_BRICKS)).put((Object)Blocks.CHISELED_TUFF, (Object)TexturedModel.SIDE_END_WALL.get(Blocks.CHISELED_TUFF)).build();
    static final Map<BlockFamily.Variant, BiConsumer<BlockTexturePool, Block>> VARIANT_POOL_FUNCTIONS = ImmutableMap.builder().put((Object)BlockFamily.Variant.BUTTON, BlockTexturePool::button).put((Object)BlockFamily.Variant.DOOR, BlockTexturePool::door).put((Object)BlockFamily.Variant.CHISELED, BlockTexturePool::block).put((Object)BlockFamily.Variant.CRACKED, BlockTexturePool::block).put((Object)BlockFamily.Variant.CUSTOM_FENCE, BlockTexturePool::customFence).put((Object)BlockFamily.Variant.FENCE, BlockTexturePool::fence).put((Object)BlockFamily.Variant.CUSTOM_FENCE_GATE, BlockTexturePool::customFenceGate).put((Object)BlockFamily.Variant.FENCE_GATE, BlockTexturePool::fenceGate).put((Object)BlockFamily.Variant.SIGN, BlockTexturePool::sign).put((Object)BlockFamily.Variant.SLAB, BlockTexturePool::slab).put((Object)BlockFamily.Variant.STAIRS, BlockTexturePool::stairs).put((Object)BlockFamily.Variant.PRESSURE_PLATE, BlockTexturePool::pressurePlate).put((Object)BlockFamily.Variant.TRAPDOOR, BlockTexturePool::registerTrapdoor).put((Object)BlockFamily.Variant.WALL, BlockTexturePool::wall).build();
    private static final Map<Direction, ModelVariantOperator> CONNECTION_VARIANT_FUNCTIONS = ImmutableMap.of((Object)Direction.NORTH, (Object)NO_OP, (Object)Direction.EAST, (Object)ROTATE_Y_90.then(UV_LOCK), (Object)Direction.SOUTH, (Object)ROTATE_Y_180.then(UV_LOCK), (Object)Direction.WEST, (Object)ROTATE_Y_270.then(UV_LOCK), (Object)Direction.UP, (Object)ROTATE_X_270.then(UV_LOCK), (Object)Direction.DOWN, (Object)ROTATE_X_90.then(UV_LOCK));
    private static final Map<ChiseledBookshelfModelCacheKey, Identifier> CHISELED_BOOKSHELF_MODEL_CACHE = new HashMap();

    public static ModelVariant createModelVariant(Identifier id) {
        return new ModelVariant(id);
    }

    public static WeightedVariant createWeightedVariant(ModelVariant variant) {
        return new WeightedVariant(Pool.of((Object)variant));
    }

    public static WeightedVariant createWeightedVariant(ModelVariant ... variants) {
        return new WeightedVariant(Pool.of(Arrays.stream(variants).map(variant -> new Weighted(variant, 1)).toList()));
    }

    public static WeightedVariant createWeightedVariant(Identifier id) {
        return BlockStateModelGenerator.createWeightedVariant((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)id));
    }

    public static MultipartModelConditionBuilder createMultipartConditionBuilder() {
        return new MultipartModelConditionBuilder();
    }

    @SafeVarargs
    public static <T extends Enum<T>> MultipartModelConditionBuilder createMultipartConditionBuilderWith(EnumProperty<T> property, T value, T ... values) {
        return BlockStateModelGenerator.createMultipartConditionBuilder().put(property, value, values);
    }

    public static MultipartModelConditionBuilder createMultipartConditionBuilderWith(BooleanProperty property, boolean value) {
        return BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)property, (Comparable)Boolean.valueOf(value));
    }

    public static MultipartModelCondition or(MultipartModelConditionBuilder ... conditionBuilders) {
        return new MultipartModelCombinedCondition(MultipartModelCombinedCondition.LogicalOperator.OR, Stream.of(conditionBuilders).map(MultipartModelConditionBuilder::build).toList());
    }

    public static MultipartModelCondition and(MultipartModelConditionBuilder ... conditionBuilders) {
        return new MultipartModelCombinedCondition(MultipartModelCombinedCondition.LogicalOperator.AND, Stream.of(conditionBuilders).map(MultipartModelConditionBuilder::build).toList());
    }

    public static BlockModelDefinitionCreator createStoneState(Block block, ModelVariant variant, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)Models.CUBE_MIRRORED_ALL.upload(block, textures, modelCollector));
        return VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)BlockStateModelGenerator.modelWithMirroring((ModelVariant)variant, (ModelVariant)modelVariant));
    }

    public static BlockModelDefinitionCreator createMudBrickState(Block block, ModelVariant variant, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_NORTH_WEST_MIRRORED_ALL.upload(block, textures, modelCollector));
        return BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)weightedVariant);
    }

    public static BlockModelDefinitionCreator createDeepslateState(Block block, ModelVariant variant, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)Models.CUBE_COLUMN_MIRRORED.upload(block, textures, modelCollector));
        return VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)BlockStateModelGenerator.modelWithMirroring((ModelVariant)variant, (ModelVariant)modelVariant)).apply(BlockStateModelGenerator.createAxisRotatedVariantMap());
    }

    public BlockStateModelGenerator(Consumer<BlockModelDefinitionCreator> blockStateCollector, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        this.blockStateCollector = blockStateCollector;
        this.itemModelOutput = itemModelOutput;
        this.modelCollector = modelCollector;
    }

    public final void registerItemModel(Item item, Identifier modelId) {
        this.itemModelOutput.accept(item, ItemModels.basic((Identifier)modelId));
    }

    public void registerParentedItemModel(Block block, Identifier parentModelId) {
        this.itemModelOutput.accept(block.asItem(), ItemModels.basic((Identifier)parentModelId));
    }

    public final void registerTintedItemModel(Block block, Identifier modelId, TintSource tint) {
        this.itemModelOutput.accept(block.asItem(), ItemModels.tinted((Identifier)modelId, (TintSource[])new TintSource[]{tint}));
    }

    public final Identifier uploadItemModel(Item item) {
        return Models.GENERATED.upload(ModelIds.getItemModelId((Item)item), TextureMap.layer0((Item)item), this.modelCollector);
    }

    public Identifier uploadBlockItemModel(Item item, Block block) {
        return Models.GENERATED.upload(ModelIds.getItemModelId((Item)item), TextureMap.layer0((Block)block), this.modelCollector);
    }

    public final Identifier uploadBlockItemModel(Item item, Block block, String textureSuffix) {
        return Models.GENERATED.upload(ModelIds.getItemModelId((Item)item), TextureMap.layer0((Identifier)TextureMap.getSubId((Block)block, (String)textureSuffix)), this.modelCollector);
    }

    public Identifier uploadTwoLayerBlockItemModel(Item item, Block block, String layer1Suffix) {
        Identifier identifier = TextureMap.getId((Block)block);
        Identifier identifier2 = TextureMap.getSubId((Block)block, (String)layer1Suffix);
        return Models.GENERATED_TWO_LAYERS.upload(ModelIds.getItemModelId((Item)item), TextureMap.layered((Identifier)identifier, (Identifier)identifier2), this.modelCollector);
    }

    public void registerItemModel(Item item) {
        this.registerItemModel(item, this.uploadItemModel(item));
    }

    public final void registerItemModel(Block block) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            this.registerItemModel(item, this.uploadBlockItemModel(item, block));
        }
    }

    public final void registerItemModel(Block block, String textureSuffix) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            this.registerItemModel(item, this.uploadBlockItemModel(item, block, textureSuffix));
        }
    }

    public final void registerTwoLayerItemModel(Block block, String layer1Suffix) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            Identifier identifier = this.uploadTwoLayerBlockItemModel(item, block, layer1Suffix);
            this.registerItemModel(item, identifier);
        }
    }

    public static WeightedVariant modelWithYRotation(ModelVariant variant) {
        return BlockStateModelGenerator.createWeightedVariant((ModelVariant[])new ModelVariant[]{variant, variant.with(ROTATE_Y_90), variant.with(ROTATE_Y_180), variant.with(ROTATE_Y_270)});
    }

    public static WeightedVariant modelWithMirroring(ModelVariant variant, ModelVariant mirroredVariant) {
        return BlockStateModelGenerator.createWeightedVariant((ModelVariant[])new ModelVariant[]{variant, mirroredVariant, variant.with(ROTATE_Y_180), mirroredVariant.with(ROTATE_Y_180)});
    }

    public static BlockStateVariantMap<WeightedVariant> createBooleanModelMap(BooleanProperty property, WeightedVariant trueModel, WeightedVariant falseModel) {
        return BlockStateVariantMap.models((Property)property).register((Comparable)Boolean.valueOf(true), (Object)trueModel).register((Comparable)Boolean.valueOf(false), (Object)falseModel);
    }

    public final void registerMirrorable(Block block) {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)TexturedModel.CUBE_ALL.upload(block, this.modelCollector));
        ModelVariant modelVariant2 = BlockStateModelGenerator.createModelVariant((Identifier)TexturedModel.CUBE_MIRRORED_ALL.upload(block, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)BlockStateModelGenerator.modelWithMirroring((ModelVariant)modelVariant, (ModelVariant)modelVariant2)));
    }

    public final void registerRotatable(Block block) {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)TexturedModel.CUBE_ALL.upload(block, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)BlockStateModelGenerator.modelWithYRotation((ModelVariant)modelVariant)));
    }

    public final void registerBrushableBlock(Block block) {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block).with(BlockStateVariantMap.models((Property)Properties.DUSTED).generate(dusted -> {
            String string = "_" + dusted;
            Identifier identifier = TextureMap.getSubId((Block)block, (String)string);
            Identifier identifier2 = Models.CUBE_ALL.upload(block, string, new TextureMap().put(TextureKey.ALL, identifier), this.modelCollector);
            return BlockStateModelGenerator.createWeightedVariant((Identifier)identifier2);
        })));
        this.registerParentedItemModel(block, ModelIds.getBlockSubModelId((Block)block, (String)"_0"));
    }

    public static BlockModelDefinitionCreator createButtonBlockState(Block buttonBlock, WeightedVariant unpressedModel, WeightedVariant pressedModel) {
        return VariantsBlockModelDefinitionCreator.of((Block)buttonBlock).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.POWERED).register((Comparable)Boolean.valueOf(false), (Object)unpressedModel).register((Comparable)Boolean.valueOf(true), (Object)pressedModel)).apply((BlockStateVariantMap)BlockStateVariantMap.operations((Property)Properties.BLOCK_FACE, (Property)Properties.HORIZONTAL_FACING).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.EAST, (Object)ROTATE_Y_90).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.WEST, (Object)ROTATE_Y_270).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.SOUTH, (Object)ROTATE_Y_180).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.NORTH, (Object)NO_OP).register((Comparable)BlockFace.WALL, (Comparable)Direction.EAST, (Object)ROTATE_Y_90.then(ROTATE_X_90).then(UV_LOCK)).register((Comparable)BlockFace.WALL, (Comparable)Direction.WEST, (Object)ROTATE_Y_270.then(ROTATE_X_90).then(UV_LOCK)).register((Comparable)BlockFace.WALL, (Comparable)Direction.SOUTH, (Object)ROTATE_Y_180.then(ROTATE_X_90).then(UV_LOCK)).register((Comparable)BlockFace.WALL, (Comparable)Direction.NORTH, (Object)ROTATE_X_90.then(UV_LOCK)).register((Comparable)BlockFace.CEILING, (Comparable)Direction.EAST, (Object)ROTATE_Y_270.then(ROTATE_X_180)).register((Comparable)BlockFace.CEILING, (Comparable)Direction.WEST, (Object)ROTATE_Y_90.then(ROTATE_X_180)).register((Comparable)BlockFace.CEILING, (Comparable)Direction.SOUTH, (Object)ROTATE_X_180).register((Comparable)BlockFace.CEILING, (Comparable)Direction.NORTH, (Object)ROTATE_Y_180.then(ROTATE_X_180)));
    }

    public static BlockModelDefinitionCreator createDoorBlockState(Block doorBlock, WeightedVariant bottomLeftClosedModel, WeightedVariant bottomLeftOpenModel, WeightedVariant bottomRightClosedModel, WeightedVariant bottomRightOpenModel, WeightedVariant topLeftClosedModel, WeightedVariant topLeftOpenModel, WeightedVariant topRightClosedModel, WeightedVariant topRightOpenModel) {
        return VariantsBlockModelDefinitionCreator.of((Block)doorBlock).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.HORIZONTAL_FACING, (Property)Properties.DOUBLE_BLOCK_HALF, (Property)Properties.DOOR_HINGE, (Property)Properties.OPEN).register((Comparable)Direction.EAST, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(false), (Object)bottomLeftClosedModel).register((Comparable)Direction.SOUTH, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(false), (Object)bottomLeftClosedModel.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(false), (Object)bottomLeftClosedModel.apply(ROTATE_Y_180)).register((Comparable)Direction.NORTH, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(false), (Object)bottomLeftClosedModel.apply(ROTATE_Y_270)).register((Comparable)Direction.EAST, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(false), (Object)bottomRightClosedModel).register((Comparable)Direction.SOUTH, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(false), (Object)bottomRightClosedModel.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(false), (Object)bottomRightClosedModel.apply(ROTATE_Y_180)).register((Comparable)Direction.NORTH, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(false), (Object)bottomRightClosedModel.apply(ROTATE_Y_270)).register((Comparable)Direction.EAST, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(true), (Object)bottomLeftOpenModel.apply(ROTATE_Y_90)).register((Comparable)Direction.SOUTH, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(true), (Object)bottomLeftOpenModel.apply(ROTATE_Y_180)).register((Comparable)Direction.WEST, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(true), (Object)bottomLeftOpenModel.apply(ROTATE_Y_270)).register((Comparable)Direction.NORTH, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(true), (Object)bottomLeftOpenModel).register((Comparable)Direction.EAST, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(true), (Object)bottomRightOpenModel.apply(ROTATE_Y_270)).register((Comparable)Direction.SOUTH, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(true), (Object)bottomRightOpenModel).register((Comparable)Direction.WEST, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(true), (Object)bottomRightOpenModel.apply(ROTATE_Y_90)).register((Comparable)Direction.NORTH, (Comparable)DoubleBlockHalf.LOWER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(true), (Object)bottomRightOpenModel.apply(ROTATE_Y_180)).register((Comparable)Direction.EAST, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(false), (Object)topLeftClosedModel).register((Comparable)Direction.SOUTH, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(false), (Object)topLeftClosedModel.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(false), (Object)topLeftClosedModel.apply(ROTATE_Y_180)).register((Comparable)Direction.NORTH, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(false), (Object)topLeftClosedModel.apply(ROTATE_Y_270)).register((Comparable)Direction.EAST, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(false), (Object)topRightClosedModel).register((Comparable)Direction.SOUTH, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(false), (Object)topRightClosedModel.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(false), (Object)topRightClosedModel.apply(ROTATE_Y_180)).register((Comparable)Direction.NORTH, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(false), (Object)topRightClosedModel.apply(ROTATE_Y_270)).register((Comparable)Direction.EAST, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(true), (Object)topLeftOpenModel.apply(ROTATE_Y_90)).register((Comparable)Direction.SOUTH, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(true), (Object)topLeftOpenModel.apply(ROTATE_Y_180)).register((Comparable)Direction.WEST, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(true), (Object)topLeftOpenModel.apply(ROTATE_Y_270)).register((Comparable)Direction.NORTH, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.LEFT, (Comparable)Boolean.valueOf(true), (Object)topLeftOpenModel).register((Comparable)Direction.EAST, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(true), (Object)topRightOpenModel.apply(ROTATE_Y_270)).register((Comparable)Direction.SOUTH, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(true), (Object)topRightOpenModel).register((Comparable)Direction.WEST, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(true), (Object)topRightOpenModel.apply(ROTATE_Y_90)).register((Comparable)Direction.NORTH, (Comparable)DoubleBlockHalf.UPPER, (Comparable)DoorHinge.RIGHT, (Comparable)Boolean.valueOf(true), (Object)topRightOpenModel.apply(ROTATE_Y_180)));
    }

    public static BlockModelDefinitionCreator createCustomFenceBlockState(Block customFenceBlock, WeightedVariant postModel, WeightedVariant northSideModel, WeightedVariant eastSideModel, WeightedVariant southSideModel, WeightedVariant westSideModel) {
        return MultipartBlockModelDefinitionCreator.create((Block)customFenceBlock).with(postModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(true)), northSideModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST, (Comparable)Boolean.valueOf(true)), eastSideModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(true)), southSideModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST, (Comparable)Boolean.valueOf(true)), westSideModel);
    }

    public static BlockModelDefinitionCreator createFenceBlockState(Block fenceBlock, WeightedVariant postModel, WeightedVariant sideModel) {
        return MultipartBlockModelDefinitionCreator.create((Block)fenceBlock).with(postModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(true)), sideModel.apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST, (Comparable)Boolean.valueOf(true)), sideModel.apply(ROTATE_Y_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(true)), sideModel.apply(ROTATE_Y_180).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST, (Comparable)Boolean.valueOf(true)), sideModel.apply(ROTATE_Y_270).apply(UV_LOCK));
    }

    public static BlockModelDefinitionCreator createWallBlockState(Block wallBlock, WeightedVariant postModel, WeightedVariant lowSideModel, WeightedVariant tallSideModel) {
        return MultipartBlockModelDefinitionCreator.create((Block)wallBlock).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.UP, (Comparable)Boolean.valueOf(true)), postModel).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH_WALL_SHAPE, (Comparable)WallShape.LOW), lowSideModel.apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST_WALL_SHAPE, (Comparable)WallShape.LOW), lowSideModel.apply(ROTATE_Y_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH_WALL_SHAPE, (Comparable)WallShape.LOW), lowSideModel.apply(ROTATE_Y_180).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST_WALL_SHAPE, (Comparable)WallShape.LOW), lowSideModel.apply(ROTATE_Y_270).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH_WALL_SHAPE, (Comparable)WallShape.TALL), tallSideModel.apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST_WALL_SHAPE, (Comparable)WallShape.TALL), tallSideModel.apply(ROTATE_Y_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH_WALL_SHAPE, (Comparable)WallShape.TALL), tallSideModel.apply(ROTATE_Y_180).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST_WALL_SHAPE, (Comparable)WallShape.TALL), tallSideModel.apply(ROTATE_Y_270).apply(UV_LOCK));
    }

    public static BlockModelDefinitionCreator createFenceGateBlockState(Block fenceGateBlock, WeightedVariant openModel, WeightedVariant closedModel, WeightedVariant openWallModel, WeightedVariant closedWallModel, boolean uvlock) {
        return VariantsBlockModelDefinitionCreator.of((Block)fenceGateBlock).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.IN_WALL, (Property)Properties.OPEN).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Object)closedModel).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Object)closedWallModel).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Object)openModel).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Object)openWallModel)).apply(uvlock ? UV_LOCK : NO_OP).apply(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS);
    }

    public static BlockModelDefinitionCreator createStairsBlockState(Block stairsBlock, WeightedVariant innerModel, WeightedVariant straightModel, WeightedVariant outerModel) {
        return VariantsBlockModelDefinitionCreator.of((Block)stairsBlock).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.HORIZONTAL_FACING, (Property)Properties.BLOCK_HALF, (Property)Properties.STAIR_SHAPE).register((Comparable)Direction.EAST, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.STRAIGHT, (Object)straightModel).register((Comparable)Direction.WEST, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.STRAIGHT, (Object)straightModel.apply(ROTATE_Y_180).apply(UV_LOCK)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.STRAIGHT, (Object)straightModel.apply(ROTATE_Y_90).apply(UV_LOCK)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.STRAIGHT, (Object)straightModel.apply(ROTATE_Y_270).apply(UV_LOCK)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.OUTER_RIGHT, (Object)outerModel).register((Comparable)Direction.WEST, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.OUTER_RIGHT, (Object)outerModel.apply(ROTATE_Y_180).apply(UV_LOCK)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.OUTER_RIGHT, (Object)outerModel.apply(ROTATE_Y_90).apply(UV_LOCK)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.OUTER_RIGHT, (Object)outerModel.apply(ROTATE_Y_270).apply(UV_LOCK)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.OUTER_LEFT, (Object)outerModel.apply(ROTATE_Y_270).apply(UV_LOCK)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.OUTER_LEFT, (Object)outerModel.apply(ROTATE_Y_90).apply(UV_LOCK)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.OUTER_LEFT, (Object)outerModel).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.OUTER_LEFT, (Object)outerModel.apply(ROTATE_Y_180).apply(UV_LOCK)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.INNER_RIGHT, (Object)innerModel).register((Comparable)Direction.WEST, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.INNER_RIGHT, (Object)innerModel.apply(ROTATE_Y_180).apply(UV_LOCK)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.INNER_RIGHT, (Object)innerModel.apply(ROTATE_Y_90).apply(UV_LOCK)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.INNER_RIGHT, (Object)innerModel.apply(ROTATE_Y_270).apply(UV_LOCK)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.INNER_LEFT, (Object)innerModel.apply(ROTATE_Y_270).apply(UV_LOCK)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.INNER_LEFT, (Object)innerModel.apply(ROTATE_Y_90).apply(UV_LOCK)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.INNER_LEFT, (Object)innerModel).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.BOTTOM, (Comparable)StairShape.INNER_LEFT, (Object)innerModel.apply(ROTATE_Y_180).apply(UV_LOCK)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.TOP, (Comparable)StairShape.STRAIGHT, (Object)straightModel.apply(ROTATE_X_180).apply(UV_LOCK)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.TOP, (Comparable)StairShape.STRAIGHT, (Object)straightModel.apply(ROTATE_X_180).apply(ROTATE_Y_180).apply(UV_LOCK)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.TOP, (Comparable)StairShape.STRAIGHT, (Object)straightModel.apply(ROTATE_X_180).apply(ROTATE_Y_90).apply(UV_LOCK)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.TOP, (Comparable)StairShape.STRAIGHT, (Object)straightModel.apply(ROTATE_X_180).apply(ROTATE_Y_270).apply(UV_LOCK)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.TOP, (Comparable)StairShape.OUTER_RIGHT, (Object)outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_90).apply(UV_LOCK)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.TOP, (Comparable)StairShape.OUTER_RIGHT, (Object)outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_270).apply(UV_LOCK)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.TOP, (Comparable)StairShape.OUTER_RIGHT, (Object)outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_180).apply(UV_LOCK)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.TOP, (Comparable)StairShape.OUTER_RIGHT, (Object)outerModel.apply(ROTATE_X_180).apply(UV_LOCK)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.TOP, (Comparable)StairShape.OUTER_LEFT, (Object)outerModel.apply(ROTATE_X_180).apply(UV_LOCK)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.TOP, (Comparable)StairShape.OUTER_LEFT, (Object)outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_180).apply(UV_LOCK)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.TOP, (Comparable)StairShape.OUTER_LEFT, (Object)outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_90).apply(UV_LOCK)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.TOP, (Comparable)StairShape.OUTER_LEFT, (Object)outerModel.apply(ROTATE_X_180).apply(ROTATE_Y_270).apply(UV_LOCK)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.TOP, (Comparable)StairShape.INNER_RIGHT, (Object)innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_90).apply(UV_LOCK)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.TOP, (Comparable)StairShape.INNER_RIGHT, (Object)innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_270).apply(UV_LOCK)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.TOP, (Comparable)StairShape.INNER_RIGHT, (Object)innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_180).apply(UV_LOCK)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.TOP, (Comparable)StairShape.INNER_RIGHT, (Object)innerModel.apply(ROTATE_X_180).apply(UV_LOCK)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.TOP, (Comparable)StairShape.INNER_LEFT, (Object)innerModel.apply(ROTATE_X_180).apply(UV_LOCK)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.TOP, (Comparable)StairShape.INNER_LEFT, (Object)innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_180).apply(UV_LOCK)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.TOP, (Comparable)StairShape.INNER_LEFT, (Object)innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_90).apply(UV_LOCK)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.TOP, (Comparable)StairShape.INNER_LEFT, (Object)innerModel.apply(ROTATE_X_180).apply(ROTATE_Y_270).apply(UV_LOCK)));
    }

    public static BlockModelDefinitionCreator createOrientableTrapdoorBlockState(Block trapdoorBlock, WeightedVariant topModel, WeightedVariant bottomModel, WeightedVariant openModel) {
        return VariantsBlockModelDefinitionCreator.of((Block)trapdoorBlock).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.HORIZONTAL_FACING, (Property)Properties.BLOCK_HALF, (Property)Properties.OPEN).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(false), (Object)bottomModel).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(false), (Object)bottomModel.apply(ROTATE_Y_180)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(false), (Object)bottomModel.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(false), (Object)bottomModel.apply(ROTATE_Y_270)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(false), (Object)topModel).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(false), (Object)topModel.apply(ROTATE_Y_180)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(false), (Object)topModel.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(false), (Object)topModel.apply(ROTATE_Y_270)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(true), (Object)openModel).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_Y_180)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_Y_270)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_X_180).apply(ROTATE_Y_180)).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_X_180)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_X_180).apply(ROTATE_Y_270)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_X_180).apply(ROTATE_Y_90)));
    }

    public static BlockModelDefinitionCreator createTrapdoorBlockState(Block trapdoorBlock, WeightedVariant topModel, WeightedVariant bottomModel, WeightedVariant openModel) {
        return VariantsBlockModelDefinitionCreator.of((Block)trapdoorBlock).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.HORIZONTAL_FACING, (Property)Properties.BLOCK_HALF, (Property)Properties.OPEN).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(false), (Object)bottomModel).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(false), (Object)bottomModel).register((Comparable)Direction.EAST, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(false), (Object)bottomModel).register((Comparable)Direction.WEST, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(false), (Object)bottomModel).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(false), (Object)topModel).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(false), (Object)topModel).register((Comparable)Direction.EAST, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(false), (Object)topModel).register((Comparable)Direction.WEST, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(false), (Object)topModel).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(true), (Object)openModel).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_Y_180)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.BOTTOM, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_Y_270)).register((Comparable)Direction.NORTH, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(true), (Object)openModel).register((Comparable)Direction.SOUTH, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_Y_180)).register((Comparable)Direction.EAST, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)BlockHalf.TOP, (Comparable)Boolean.valueOf(true), (Object)openModel.apply(ROTATE_Y_270)));
    }

    public static VariantsBlockModelDefinitionCreator createSingletonBlockState(Block block, WeightedVariant model) {
        return VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)model);
    }

    public static BlockStateVariantMap<ModelVariantOperator> createAxisRotatedVariantMap() {
        return BlockStateVariantMap.operations((Property)Properties.AXIS).register((Comparable)Direction.Axis.Y, (Object)NO_OP).register((Comparable)Direction.Axis.Z, (Object)ROTATE_X_90).register((Comparable)Direction.Axis.X, (Object)ROTATE_X_90.then(ROTATE_Y_90));
    }

    public static BlockModelDefinitionCreator createUvLockedColumnBlockState(Block block, TextureMap textureMap, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_COLUMN_UV_LOCKED_X.upload(block, textureMap, modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_COLUMN_UV_LOCKED_Y.upload(block, textureMap, modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_COLUMN_UV_LOCKED_Z.upload(block, textureMap, modelCollector));
        return VariantsBlockModelDefinitionCreator.of((Block)block).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.AXIS).register((Comparable)Direction.Axis.X, (Object)weightedVariant).register((Comparable)Direction.Axis.Y, (Object)weightedVariant2).register((Comparable)Direction.Axis.Z, (Object)weightedVariant3));
    }

    public static BlockModelDefinitionCreator createAxisRotatedBlockState(Block block, WeightedVariant model) {
        return VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)model).apply(BlockStateModelGenerator.createAxisRotatedVariantMap());
    }

    public final void registerAxisRotated(Block block, WeightedVariant model) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState((Block)block, (WeightedVariant)model));
    }

    public void registerAxisRotated(Block block, TexturedModel.Factory modelFactory) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)modelFactory.upload(block, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState((Block)block, (WeightedVariant)weightedVariant));
    }

    public final void registerNorthDefaultHorizontalRotatable(Block block, TexturedModel.Factory modelFactory) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)modelFactory.upload(block, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)weightedVariant).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public static BlockModelDefinitionCreator createAxisRotatedBlockState(Block block, WeightedVariant verticalModel, WeightedVariant horizontalModel) {
        return VariantsBlockModelDefinitionCreator.of((Block)block).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.AXIS).register((Comparable)Direction.Axis.Y, (Object)verticalModel).register((Comparable)Direction.Axis.Z, (Object)horizontalModel.apply(ROTATE_X_90)).register((Comparable)Direction.Axis.X, (Object)horizontalModel.apply(ROTATE_X_90).apply(ROTATE_Y_90)));
    }

    public final void registerAxisRotated(Block block, TexturedModel.Factory verticalModelFactory, TexturedModel.Factory horizontalModelFactory) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)verticalModelFactory.upload(block, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)horizontalModelFactory.upload(block, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState((Block)block, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant2));
    }

    public final void registerCreakingHeart(Block block) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.END_FOR_TOP_CUBE_COLUMN.upload(block, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL.upload(block, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createCreakingHeartModel(TexturedModel.END_FOR_TOP_CUBE_COLUMN, block, "_awake"));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createCreakingHeartModel(TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL, block, "_awake"));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createCreakingHeartModel(TexturedModel.END_FOR_TOP_CUBE_COLUMN, block, "_dormant"));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createCreakingHeartModel(TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL, block, "_dormant"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.AXIS, (Property)CreakingHeartBlock.ACTIVE).register((Comparable)Direction.Axis.Y, (Comparable)CreakingHeartState.UPROOTED, (Object)weightedVariant).register((Comparable)Direction.Axis.Z, (Comparable)CreakingHeartState.UPROOTED, (Object)weightedVariant2.apply(ROTATE_X_90)).register((Comparable)Direction.Axis.X, (Comparable)CreakingHeartState.UPROOTED, (Object)weightedVariant2.apply(ROTATE_X_90).apply(ROTATE_Y_90)).register((Comparable)Direction.Axis.Y, (Comparable)CreakingHeartState.DORMANT, (Object)weightedVariant5).register((Comparable)Direction.Axis.Z, (Comparable)CreakingHeartState.DORMANT, (Object)weightedVariant6.apply(ROTATE_X_90)).register((Comparable)Direction.Axis.X, (Comparable)CreakingHeartState.DORMANT, (Object)weightedVariant6.apply(ROTATE_X_90).apply(ROTATE_Y_90)).register((Comparable)Direction.Axis.Y, (Comparable)CreakingHeartState.AWAKE, (Object)weightedVariant3).register((Comparable)Direction.Axis.Z, (Comparable)CreakingHeartState.AWAKE, (Object)weightedVariant4.apply(ROTATE_X_90)).register((Comparable)Direction.Axis.X, (Comparable)CreakingHeartState.AWAKE, (Object)weightedVariant4.apply(ROTATE_X_90).apply(ROTATE_Y_90))));
    }

    public final Identifier createCreakingHeartModel(TexturedModel.Factory texturedModelFactory, Block block, String suffix) {
        return texturedModelFactory.andThen(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)suffix)).put(TextureKey.END, TextureMap.getSubId((Block)block, (String)("_top" + suffix)))).upload(block, suffix, this.modelCollector);
    }

    public final Identifier createSubModel(Block block, String suffix, Model model, Function<Identifier, TextureMap> texturesFactory) {
        return model.upload(block, suffix, texturesFactory.apply(TextureMap.getSubId((Block)block, (String)suffix)), this.modelCollector);
    }

    public static BlockModelDefinitionCreator createPressurePlateBlockState(Block pressurePlateBlock, WeightedVariant upModel, WeightedVariant downModel) {
        return VariantsBlockModelDefinitionCreator.of((Block)pressurePlateBlock).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.POWERED, (WeightedVariant)downModel, (WeightedVariant)upModel));
    }

    public static BlockModelDefinitionCreator createSlabBlockState(Block slabBlock, WeightedVariant bottomModel, WeightedVariant topModel, WeightedVariant doubleModel) {
        return VariantsBlockModelDefinitionCreator.of((Block)slabBlock).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.SLAB_TYPE).register((Comparable)SlabType.BOTTOM, (Object)bottomModel).register((Comparable)SlabType.TOP, (Object)topModel).register((Comparable)SlabType.DOUBLE, (Object)doubleModel));
    }

    public void registerSimpleCubeAll(Block block) {
        this.registerSingleton(block, TexturedModel.CUBE_ALL);
    }

    public void registerSingleton(Block block, TexturedModel.Factory modelFactory) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)modelFactory.upload(block, this.modelCollector))));
    }

    public void registerTintedBlockAndItem(Block block, TexturedModel.Factory texturedModelFactory, int tintColor) {
        Identifier identifier = texturedModelFactory.upload(block, this.modelCollector);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)identifier)));
        this.registerTintedItemModel(block, identifier, ItemModels.constantTintSource((int)tintColor));
    }

    private void registerVine() {
        this.registerMultifaceBlockModel(Blocks.VINE);
        Identifier identifier = this.uploadBlockItemModel(Items.VINE, Blocks.VINE);
        this.registerTintedItemModel(Blocks.VINE, identifier, ItemModels.constantTintSource((int)-12012264));
    }

    public final void registerGrassTinted(Block block) {
        Identifier identifier = this.uploadBlockItemModel(block.asItem(), block);
        this.registerTintedItemModel(block, identifier, (TintSource)new GrassTintSource());
    }

    public final BlockTexturePool registerCubeAllModelTexturePool(Block block) {
        TexturedModel texturedModel = TEXTURED_MODELS.getOrDefault(block, TexturedModel.CUBE_ALL.get(block));
        return new BlockTexturePool(this, texturedModel.getTextures()).base(block, texturedModel.getModel());
    }

    public void registerHangingSign(Block base, Block hangingSign, Block wallHangingSign) {
        WeightedVariant weightedVariant = this.uploadParticleModel(hangingSign, base);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)hangingSign, (WeightedVariant)weightedVariant));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)wallHangingSign, (WeightedVariant)weightedVariant));
        this.registerItemModel(hangingSign.asItem());
    }

    public void registerDoor(Block doorBlock) {
        TextureMap textureMap = TextureMap.topBottom((Block)doorBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_BOTTOM_LEFT.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_BOTTOM_LEFT_OPEN.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_BOTTOM_RIGHT.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_BOTTOM_RIGHT_OPEN.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_TOP_LEFT.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_TOP_LEFT_OPEN.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant7 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_TOP_RIGHT.upload(doorBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant8 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_TOP_RIGHT_OPEN.upload(doorBlock, textureMap, this.modelCollector));
        this.registerItemModel(doorBlock.asItem());
        this.blockStateCollector.accept(BlockStateModelGenerator.createDoorBlockState((Block)doorBlock, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant3, (WeightedVariant)weightedVariant4, (WeightedVariant)weightedVariant5, (WeightedVariant)weightedVariant6, (WeightedVariant)weightedVariant7, (WeightedVariant)weightedVariant8));
    }

    public final void registerParentedDoor(Block parent, Block doorBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_BOTTOM_LEFT.getBlockSubModelId(parent));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_BOTTOM_LEFT_OPEN.getBlockSubModelId(parent));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_BOTTOM_RIGHT.getBlockSubModelId(parent));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_BOTTOM_RIGHT_OPEN.getBlockSubModelId(parent));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_TOP_LEFT.getBlockSubModelId(parent));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_TOP_LEFT_OPEN.getBlockSubModelId(parent));
        WeightedVariant weightedVariant7 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_TOP_RIGHT.getBlockSubModelId(parent));
        WeightedVariant weightedVariant8 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.DOOR_TOP_RIGHT_OPEN.getBlockSubModelId(parent));
        this.itemModelOutput.acceptAlias(parent.asItem(), doorBlock.asItem());
        this.blockStateCollector.accept(BlockStateModelGenerator.createDoorBlockState((Block)doorBlock, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant3, (WeightedVariant)weightedVariant4, (WeightedVariant)weightedVariant5, (WeightedVariant)weightedVariant6, (WeightedVariant)weightedVariant7, (WeightedVariant)weightedVariant8));
    }

    public void registerOrientableTrapdoor(Block trapdoorBlock) {
        TextureMap textureMap = TextureMap.texture((Block)trapdoorBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_ORIENTABLE_TRAPDOOR_TOP.upload(trapdoorBlock, textureMap, this.modelCollector));
        Identifier identifier = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM.upload(trapdoorBlock, textureMap, this.modelCollector);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN.upload(trapdoorBlock, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createOrientableTrapdoorBlockState((Block)trapdoorBlock, (WeightedVariant)weightedVariant, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)identifier), (WeightedVariant)weightedVariant2));
        this.registerParentedItemModel(trapdoorBlock, identifier);
    }

    public void registerTrapdoor(Block trapdoorBlock) {
        TextureMap textureMap = TextureMap.texture((Block)trapdoorBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TRAPDOOR_TOP.upload(trapdoorBlock, textureMap, this.modelCollector));
        Identifier identifier = Models.TEMPLATE_TRAPDOOR_BOTTOM.upload(trapdoorBlock, textureMap, this.modelCollector);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TRAPDOOR_OPEN.upload(trapdoorBlock, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createTrapdoorBlockState((Block)trapdoorBlock, (WeightedVariant)weightedVariant, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)identifier), (WeightedVariant)weightedVariant2));
        this.registerParentedItemModel(trapdoorBlock, identifier);
    }

    public final void registerParentedTrapdoor(Block parent, Block trapdoorBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TRAPDOOR_TOP.getBlockSubModelId(parent));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TRAPDOOR_BOTTOM.getBlockSubModelId(parent));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TRAPDOOR_OPEN.getBlockSubModelId(parent));
        this.itemModelOutput.acceptAlias(parent.asItem(), trapdoorBlock.asItem());
        this.blockStateCollector.accept(BlockStateModelGenerator.createTrapdoorBlockState((Block)trapdoorBlock, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant3));
    }

    private void registerBigDripleaf() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.BIG_DRIPLEAF));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.BIG_DRIPLEAF, (String)"_partial_tilt"));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.BIG_DRIPLEAF, (String)"_full_tilt"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.BIG_DRIPLEAF).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.TILT).register((Comparable)Tilt.NONE, (Object)weightedVariant).register((Comparable)Tilt.UNSTABLE, (Object)weightedVariant).register((Comparable)Tilt.PARTIAL, (Object)weightedVariant2).register((Comparable)Tilt.FULL, (Object)weightedVariant3)).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public final LogTexturePool createLogTexturePool(Block logBlock) {
        return new LogTexturePool(this, TextureMap.sideAndEndForTop((Block)logBlock));
    }

    public final void registerSimpleState(Block block) {
        this.registerStateWithModelReference(block, block);
    }

    public final void registerStateWithModelReference(Block block, Block modelReference) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)modelReference))));
    }

    public final void registerTintableCross(Block block, CrossType crossType) {
        this.registerItemModel(block.asItem(), crossType.registerItemModel(this, block));
        this.registerTintableCrossBlockState(block, crossType);
    }

    public final void registerTintableCross(Block block, CrossType tintType, TextureMap texture) {
        this.registerItemModel(block);
        this.registerTintableCrossBlockState(block, tintType, texture);
    }

    public final void registerTintableCrossBlockState(Block block, CrossType tintType) {
        TextureMap textureMap = tintType.getTextureMap(block);
        this.registerTintableCrossBlockState(block, tintType, textureMap);
    }

    public final void registerTintableCrossBlockState(Block block, CrossType tintType, TextureMap crossTexture) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)tintType.getCrossModel().upload(block, crossTexture, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)weightedVariant));
    }

    public final void registerTintableCrossBlockStateWithStages(Block block, CrossType tintType, Property<Integer> stageProperty, int ... stages) {
        if (stageProperty.getValues().size() != stages.length) {
            throw new IllegalArgumentException("missing values for property: " + String.valueOf(stageProperty));
        }
        this.registerItemModel(block.asItem());
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block).with(BlockStateVariantMap.models(stageProperty).generate(stage -> {
            String string = "_stage" + stages[stage];
            TextureMap textureMap = TextureMap.cross((Identifier)TextureMap.getSubId((Block)block, (String)string));
            return BlockStateModelGenerator.createWeightedVariant((Identifier)tintType.getCrossModel().upload(block, string, textureMap, this.modelCollector));
        })));
    }

    public final void registerFlowerPotPlantAndItem(Block block, Block flowerPotBlock, CrossType crossType) {
        this.registerItemModel(block.asItem(), crossType.registerItemModel(this, block));
        this.registerFlowerPotPlant(block, flowerPotBlock, crossType);
    }

    public final void registerFlowerPotPlant(Block plantBlock, Block flowerPotBlock, CrossType tintType) {
        this.registerTintableCrossBlockState(plantBlock, tintType);
        TextureMap textureMap = tintType.getFlowerPotTextureMap(plantBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)tintType.getFlowerPotCrossModel().upload(flowerPotBlock, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)flowerPotBlock, (WeightedVariant)weightedVariant));
    }

    public final void registerCoralFan(Block coralFanBlock, Block coralWallFanBlock) {
        TexturedModel texturedModel = TexturedModel.CORAL_FAN.get(coralFanBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)texturedModel.upload(coralFanBlock, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)coralFanBlock, (WeightedVariant)weightedVariant));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CORAL_WALL_FAN.upload(coralWallFanBlock, texturedModel.getTextures(), this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)coralWallFanBlock, (WeightedVariant)weightedVariant2).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
        this.registerItemModel(coralFanBlock);
    }

    public final void registerGourd(Block stemBlock, Block attachedStemBlock) {
        this.registerItemModel(stemBlock.asItem());
        TextureMap textureMap = TextureMap.stem((Block)stemBlock);
        TextureMap textureMap2 = TextureMap.stemAndUpper((Block)stemBlock, (Block)attachedStemBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.STEM_FRUIT.upload(attachedStemBlock, textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)attachedStemBlock, (WeightedVariant)weightedVariant).apply((BlockStateVariantMap)BlockStateVariantMap.operations((Property)Properties.HORIZONTAL_FACING).register((Comparable)Direction.WEST, (Object)NO_OP).register((Comparable)Direction.SOUTH, (Object)ROTATE_Y_270).register((Comparable)Direction.NORTH, (Object)ROTATE_Y_90).register((Comparable)Direction.EAST, (Object)ROTATE_Y_180)));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)stemBlock).with(BlockStateVariantMap.models((Property)Properties.AGE_7).generate(age -> BlockStateModelGenerator.createWeightedVariant((Identifier)Models.STEM_GROWTH_STAGES[age].upload(stemBlock, textureMap, this.modelCollector)))));
    }

    private void registerPitcherPlant() {
        Block block = Blocks.PITCHER_PLANT;
        this.registerItemModel(block.asItem());
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)block, (String)"_top"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)block, (String)"_bottom"));
        this.registerDoubleBlock(block, weightedVariant, weightedVariant2);
    }

    private void registerPitcherCrop() {
        Block block = Blocks.PITCHER_CROP;
        this.registerItemModel(block.asItem());
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block).with(BlockStateVariantMap.models((Property)PitcherCropBlock.AGE, (Property)Properties.DOUBLE_BLOCK_HALF).generate((age, half) -> switch (1.field_43383[half.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)block, (String)("_top_stage_" + age)));
            case 2 -> BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)block, (String)("_bottom_stage_" + age)));
        })));
    }

    public final void registerCoral(Block coral, Block deadCoral, Block coralBlock, Block deadCoralBlock, Block coralFan, Block deadCoralFan, Block coralWallFan, Block deadCoralWallFan) {
        this.registerTintableCross(coral, CrossType.NOT_TINTED);
        this.registerTintableCross(deadCoral, CrossType.NOT_TINTED);
        this.registerSimpleCubeAll(coralBlock);
        this.registerSimpleCubeAll(deadCoralBlock);
        this.registerCoralFan(coralFan, coralWallFan);
        this.registerCoralFan(deadCoralFan, deadCoralWallFan);
    }

    public final void registerDoubleBlock(Block doubleBlock, CrossType tintType) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(doubleBlock, "_top", tintType.getCrossModel(), TextureMap::cross));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(doubleBlock, "_bottom", tintType.getCrossModel(), TextureMap::cross));
        this.registerDoubleBlock(doubleBlock, weightedVariant, weightedVariant2);
    }

    public final void registerDoubleBlockAndItem(Block block, CrossType crossType) {
        this.registerItemModel(block, "_top");
        this.registerDoubleBlock(block, crossType);
    }

    public final void registerGrassTintedDoubleBlockAndItem(Block block) {
        Identifier identifier = this.uploadBlockItemModel(block.asItem(), block, "_top");
        this.registerTintedItemModel(block, identifier, (TintSource)new GrassTintSource());
        this.registerDoubleBlock(block, CrossType.TINTED);
    }

    private void registerSunflower() {
        this.registerItemModel(Blocks.SUNFLOWER, "_front");
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.SUNFLOWER, (String)"_top"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.SUNFLOWER, "_bottom", CrossType.NOT_TINTED.getCrossModel(), TextureMap::cross));
        this.registerDoubleBlock(Blocks.SUNFLOWER, weightedVariant, weightedVariant2);
    }

    private void registerTallSeagrass() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.TALL_SEAGRASS, "_top", Models.TEMPLATE_SEAGRASS, TextureMap::texture));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.TALL_SEAGRASS, "_bottom", Models.TEMPLATE_SEAGRASS, TextureMap::texture));
        this.registerDoubleBlock(Blocks.TALL_SEAGRASS, weightedVariant, weightedVariant2);
    }

    private void registerSmallDripleaf() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.SMALL_DRIPLEAF, (String)"_top"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.SMALL_DRIPLEAF, (String)"_bottom"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.SMALL_DRIPLEAF).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.DOUBLE_BLOCK_HALF).register((Comparable)DoubleBlockHalf.LOWER, (Object)weightedVariant2).register((Comparable)DoubleBlockHalf.UPPER, (Object)weightedVariant)).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public final void registerDoubleBlock(Block block, WeightedVariant upperModel, WeightedVariant lowerModel) {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.DOUBLE_BLOCK_HALF).register((Comparable)DoubleBlockHalf.LOWER, (Object)lowerModel).register((Comparable)DoubleBlockHalf.UPPER, (Object)upperModel)));
    }

    public final void registerTurnableRail(Block rail) {
        TextureMap textureMap = TextureMap.rail((Block)rail);
        TextureMap textureMap2 = TextureMap.rail((Identifier)TextureMap.getSubId((Block)rail, (String)"_corner"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.RAIL_FLAT.upload(rail, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.RAIL_CURVED.upload(rail, textureMap2, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_RAIL_RAISED_NE.upload(rail, textureMap, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_RAIL_RAISED_SW.upload(rail, textureMap, this.modelCollector));
        this.registerItemModel(rail);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)rail).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.RAIL_SHAPE).register((Comparable)RailShape.NORTH_SOUTH, (Object)weightedVariant).register((Comparable)RailShape.EAST_WEST, (Object)weightedVariant.apply(ROTATE_Y_90)).register((Comparable)RailShape.ASCENDING_EAST, (Object)weightedVariant3.apply(ROTATE_Y_90)).register((Comparable)RailShape.ASCENDING_WEST, (Object)weightedVariant4.apply(ROTATE_Y_90)).register((Comparable)RailShape.ASCENDING_NORTH, (Object)weightedVariant3).register((Comparable)RailShape.ASCENDING_SOUTH, (Object)weightedVariant4).register((Comparable)RailShape.SOUTH_EAST, (Object)weightedVariant2).register((Comparable)RailShape.SOUTH_WEST, (Object)weightedVariant2.apply(ROTATE_Y_90)).register((Comparable)RailShape.NORTH_WEST, (Object)weightedVariant2.apply(ROTATE_Y_180)).register((Comparable)RailShape.NORTH_EAST, (Object)weightedVariant2.apply(ROTATE_Y_270))));
    }

    public final void registerStraightRail(Block rail) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(rail, "", Models.RAIL_FLAT, TextureMap::rail));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(rail, "", Models.TEMPLATE_RAIL_RAISED_NE, TextureMap::rail));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(rail, "", Models.TEMPLATE_RAIL_RAISED_SW, TextureMap::rail));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(rail, "_on", Models.RAIL_FLAT, TextureMap::rail));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(rail, "_on", Models.TEMPLATE_RAIL_RAISED_NE, TextureMap::rail));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(rail, "_on", Models.TEMPLATE_RAIL_RAISED_SW, TextureMap::rail));
        this.registerItemModel(rail);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)rail).with(BlockStateVariantMap.models((Property)Properties.POWERED, (Property)Properties.STRAIGHT_RAIL_SHAPE).generate((powered, shape) -> switch (1.field_22833[shape.ordinal()]) {
            case 1 -> {
                if (powered.booleanValue()) {
                    yield weightedVariant4;
                }
                yield weightedVariant;
            }
            case 2 -> (powered != false ? weightedVariant4 : weightedVariant).apply(ROTATE_Y_90);
            case 3 -> (powered != false ? weightedVariant5 : weightedVariant2).apply(ROTATE_Y_90);
            case 4 -> (powered != false ? weightedVariant6 : weightedVariant3).apply(ROTATE_Y_90);
            case 5 -> {
                if (powered.booleanValue()) {
                    yield weightedVariant5;
                }
                yield weightedVariant2;
            }
            case 6 -> {
                if (powered.booleanValue()) {
                    yield weightedVariant6;
                }
                yield weightedVariant3;
            }
            default -> throw new UnsupportedOperationException("Fix you generator!");
        })));
    }

    public final void registerBuiltinWithParticle(Block block, Item particleSource) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.PARTICLE.upload(block, TextureMap.particle((Item)particleSource), this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)weightedVariant));
    }

    public final void registerBuiltinWithParticle(Block block, Identifier particleSource) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.PARTICLE.upload(block, TextureMap.particle((Identifier)particleSource), this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)weightedVariant));
    }

    public final WeightedVariant uploadParticleModel(Block block, Block particleSource) {
        return BlockStateModelGenerator.createWeightedVariant((Identifier)Models.PARTICLE.upload(block, TextureMap.particle((Block)particleSource), this.modelCollector));
    }

    public void registerBuiltinWithParticle(Block block, Block particleSource) {
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)this.uploadParticleModel(block, particleSource)));
    }

    public final void registerBuiltin(Block block) {
        this.registerBuiltinWithParticle(block, block);
    }

    public final void registerWoolAndCarpet(Block wool, Block carpet) {
        this.registerSimpleCubeAll(wool);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.CARPET.get(wool).upload(carpet, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)carpet, (WeightedVariant)weightedVariant));
    }

    public final void registerLeafLitter(Block leafLitter) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.TEMPLATE_LEAF_LITTER_1.upload(leafLitter, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.TEMPLATE_LEAF_LITTER_2.upload(leafLitter, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.TEMPLATE_LEAF_LITTER_3.upload(leafLitter, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.TEMPLATE_LEAF_LITTER_4.upload(leafLitter, this.modelCollector));
        this.registerItemModel(leafLitter.asItem());
        this.registerSegmentedBlock(leafLitter, weightedVariant, LEAF_LITTER_MODEL_1_CONDITION_FUNCTION, weightedVariant2, LEAF_LITTER_MODEL_2_CONDITION_FUNCTION, weightedVariant3, LEAF_LITTER_MODEL_3_CONDITION_FUNCTION, weightedVariant4, LEAF_LITTER_MODEL_4_CONDITION_FUNCTION);
    }

    public final void registerFlowerbed(Block flowerbed) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.FLOWERBED_1.upload(flowerbed, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.FLOWERBED_2.upload(flowerbed, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.FLOWERBED_3.upload(flowerbed, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.FLOWERBED_4.upload(flowerbed, this.modelCollector));
        this.registerItemModel(flowerbed.asItem());
        this.registerSegmentedBlock(flowerbed, weightedVariant, FLOWERBED_MODEL_1_CONDITION_FUNCTION, weightedVariant2, FLOWERBED_MODEL_2_CONDITION_FUNCTION, weightedVariant3, FLOWERBED_MODEL_3_CONDITION_FUNCTION, weightedVariant4, FLOWERBED_MODEL_4_CONDITION_FUNCTION);
    }

    public final void registerSegmentedBlock(Block block, WeightedVariant model1, Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> model1ConditionFunction, WeightedVariant model2, Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> model2ConditionFunction, WeightedVariant model3, Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> model3ConditionFunction, WeightedVariant model4, Function<MultipartModelConditionBuilder, MultipartModelConditionBuilder> model4ConditionFunction) {
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)block).with(model1ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.NORTH)), model1).with(model1ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.EAST)), model1.apply(ROTATE_Y_90)).with(model1ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.SOUTH)), model1.apply(ROTATE_Y_180)).with(model1ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.WEST)), model1.apply(ROTATE_Y_270)).with(model2ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.NORTH)), model2).with(model2ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.EAST)), model2.apply(ROTATE_Y_90)).with(model2ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.SOUTH)), model2.apply(ROTATE_Y_180)).with(model2ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.WEST)), model2.apply(ROTATE_Y_270)).with(model3ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.NORTH)), model3).with(model3ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.EAST)), model3.apply(ROTATE_Y_90)).with(model3ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.SOUTH)), model3.apply(ROTATE_Y_180)).with(model3ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.WEST)), model3.apply(ROTATE_Y_270)).with(model4ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.NORTH)), model4).with(model4ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.EAST)), model4.apply(ROTATE_Y_90)).with(model4ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.SOUTH)), model4.apply(ROTATE_Y_180)).with(model4ConditionFunction.apply(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)Direction.WEST)), model4.apply(ROTATE_Y_270)));
    }

    public final void registerRandomHorizontalRotations(TexturedModel.Factory modelFactory, Block ... blocks) {
        for (Block block : blocks) {
            ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)modelFactory.upload(block, this.modelCollector));
            this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)BlockStateModelGenerator.modelWithYRotation((ModelVariant)modelVariant)));
        }
    }

    public final void registerSouthDefaultHorizontalFacing(TexturedModel.Factory modelFactory, Block ... blocks) {
        for (Block block : blocks) {
            WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)modelFactory.upload(block, this.modelCollector));
            this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)weightedVariant).apply(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
        }
    }

    public final void registerGlassAndPane(Block glassBlock, Block glassPane) {
        this.registerSimpleCubeAll(glassBlock);
        TextureMap textureMap = TextureMap.paneAndTopForEdge((Block)glassBlock, (Block)glassPane);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_GLASS_PANE_POST.upload(glassPane, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_GLASS_PANE_SIDE.upload(glassPane, textureMap, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_GLASS_PANE_SIDE_ALT.upload(glassPane, textureMap, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_GLASS_PANE_NOSIDE.upload(glassPane, textureMap, this.modelCollector));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_GLASS_PANE_NOSIDE_ALT.upload(glassPane, textureMap, this.modelCollector));
        Item item = glassPane.asItem();
        this.registerItemModel(item, this.uploadBlockItemModel(item, glassBlock));
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)glassPane).with(weightedVariant).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(true)), weightedVariant2).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST, (Comparable)Boolean.valueOf(true)), weightedVariant2.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(true)), weightedVariant3).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST, (Comparable)Boolean.valueOf(true)), weightedVariant3.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(false)), weightedVariant4).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST, (Comparable)Boolean.valueOf(false)), weightedVariant5).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(false)), weightedVariant5.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST, (Comparable)Boolean.valueOf(false)), weightedVariant4.apply(ROTATE_Y_270)));
    }

    public final void registerCommandBlock(Block commandBlock) {
        TextureMap textureMap = TextureMap.sideFrontBack((Block)commandBlock);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_COMMAND_BLOCK.upload(commandBlock, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(commandBlock, "_conditional", Models.TEMPLATE_COMMAND_BLOCK, id -> textureMap.copyAndAdd(TextureKey.SIDE, id)));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)commandBlock).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.CONDITIONAL, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)).apply(NORTH_DEFAULT_ROTATION_OPERATIONS));
    }

    public final void registerAnvil(Block anvil) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.TEMPLATE_ANVIL.upload(anvil, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)anvil, (WeightedVariant)weightedVariant).apply(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public static WeightedVariant getBambooBlockStateVariants(int age) {
        String string = "_age" + age;
        return new WeightedVariant(Pool.of(IntStream.range(1, 5).mapToObj(i -> new Weighted((Object)BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.BAMBOO, (String)(i + string))), 1)).collect(Collectors.toList())));
    }

    private void registerBamboo() {
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)Blocks.BAMBOO).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.AGE_1, (Comparable)Integer.valueOf(0)), BlockStateModelGenerator.getBambooBlockStateVariants((int)0)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.AGE_1, (Comparable)Integer.valueOf(1)), BlockStateModelGenerator.getBambooBlockStateVariants((int)1)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.BAMBOO_LEAVES, (Comparable)BambooLeaves.SMALL), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.BAMBOO, (String)"_small_leaves"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.BAMBOO_LEAVES, (Comparable)BambooLeaves.LARGE), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.BAMBOO, (String)"_large_leaves"))));
    }

    private void registerBarrel() {
        Identifier identifier = TextureMap.getSubId((Block)Blocks.BARREL, (String)"_top_open");
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.CUBE_BOTTOM_TOP.upload(Blocks.BARREL, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.CUBE_BOTTOM_TOP.get(Blocks.BARREL).textures(textureMap -> textureMap.put(TextureKey.TOP, identifier)).upload(Blocks.BARREL, "_open", this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.BARREL).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.OPEN).register((Comparable)Boolean.valueOf(false), (Object)weightedVariant).register((Comparable)Boolean.valueOf(true), (Object)weightedVariant2)).apply(UP_DEFAULT_ROTATION_OPERATIONS));
    }

    public static <T extends Comparable<T>> BlockStateVariantMap<WeightedVariant> createValueFencedModelMap(Property<T> property, T fence, WeightedVariant aboveFenceModel, WeightedVariant belowFenceModel) {
        return BlockStateVariantMap.models(property).generate(value -> {
            boolean bl = value.compareTo(fence) >= 0;
            return bl ? aboveFenceModel : belowFenceModel;
        });
    }

    public final void registerBeehive(Block beehive, Function<Block, TextureMap> texturesFactory) {
        TextureMap textureMap = texturesFactory.apply(beehive).inherit(TextureKey.SIDE, TextureKey.PARTICLE);
        TextureMap textureMap2 = textureMap.copyAndAdd(TextureKey.FRONT, TextureMap.getSubId((Block)beehive, (String)"_front_honey"));
        Identifier identifier = Models.ORIENTABLE_WITH_BOTTOM.upload(beehive, "_empty", textureMap, this.modelCollector);
        Identifier identifier2 = Models.ORIENTABLE_WITH_BOTTOM.upload(beehive, "_honey", textureMap2, this.modelCollector);
        this.itemModelOutput.accept(beehive.asItem(), ItemModels.select((Property)BeehiveBlock.HONEY_LEVEL, (ItemModel.Unbaked)ItemModels.basic((Identifier)identifier), Map.of(5, ItemModels.basic((Identifier)identifier2))));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)beehive).with(BlockStateModelGenerator.createValueFencedModelMap((Property)BeehiveBlock.HONEY_LEVEL, (Comparable)Integer.valueOf(5), (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)identifier2), (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)identifier))).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public final void registerCrop(Block crop, Property<Integer> ageProperty, int ... ageTextureIndices) {
        this.registerItemModel(crop.asItem());
        if (ageProperty.getValues().size() != ageTextureIndices.length) {
            throw new IllegalArgumentException();
        }
        Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)crop).with(BlockStateVariantMap.models(ageProperty).generate(arg_0 -> this.method_67830(ageTextureIndices, (Int2ObjectMap)int2ObjectMap, crop, arg_0))));
    }

    private void registerBell() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.BELL, (String)"_floor"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.BELL, (String)"_ceiling"));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.BELL, (String)"_wall"));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.BELL, (String)"_between_walls"));
        this.registerItemModel(Items.BELL);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.BELL).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.HORIZONTAL_FACING, (Property)Properties.ATTACHMENT).register((Comparable)Direction.NORTH, (Comparable)Attachment.FLOOR, (Object)weightedVariant).register((Comparable)Direction.SOUTH, (Comparable)Attachment.FLOOR, (Object)weightedVariant.apply(ROTATE_Y_180)).register((Comparable)Direction.EAST, (Comparable)Attachment.FLOOR, (Object)weightedVariant.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)Attachment.FLOOR, (Object)weightedVariant.apply(ROTATE_Y_270)).register((Comparable)Direction.NORTH, (Comparable)Attachment.CEILING, (Object)weightedVariant2).register((Comparable)Direction.SOUTH, (Comparable)Attachment.CEILING, (Object)weightedVariant2.apply(ROTATE_Y_180)).register((Comparable)Direction.EAST, (Comparable)Attachment.CEILING, (Object)weightedVariant2.apply(ROTATE_Y_90)).register((Comparable)Direction.WEST, (Comparable)Attachment.CEILING, (Object)weightedVariant2.apply(ROTATE_Y_270)).register((Comparable)Direction.NORTH, (Comparable)Attachment.SINGLE_WALL, (Object)weightedVariant3.apply(ROTATE_Y_270)).register((Comparable)Direction.SOUTH, (Comparable)Attachment.SINGLE_WALL, (Object)weightedVariant3.apply(ROTATE_Y_90)).register((Comparable)Direction.EAST, (Comparable)Attachment.SINGLE_WALL, (Object)weightedVariant3).register((Comparable)Direction.WEST, (Comparable)Attachment.SINGLE_WALL, (Object)weightedVariant3.apply(ROTATE_Y_180)).register((Comparable)Direction.SOUTH, (Comparable)Attachment.DOUBLE_WALL, (Object)weightedVariant4.apply(ROTATE_Y_90)).register((Comparable)Direction.NORTH, (Comparable)Attachment.DOUBLE_WALL, (Object)weightedVariant4.apply(ROTATE_Y_270)).register((Comparable)Direction.EAST, (Comparable)Attachment.DOUBLE_WALL, (Object)weightedVariant4).register((Comparable)Direction.WEST, (Comparable)Attachment.DOUBLE_WALL, (Object)weightedVariant4.apply(ROTATE_Y_180))));
    }

    private void registerGrindstone() {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.GRINDSTONE, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.GRINDSTONE))).apply((BlockStateVariantMap)BlockStateVariantMap.operations((Property)Properties.BLOCK_FACE, (Property)Properties.HORIZONTAL_FACING).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.NORTH, (Object)NO_OP).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.EAST, (Object)ROTATE_Y_90).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.SOUTH, (Object)ROTATE_Y_180).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.WEST, (Object)ROTATE_Y_270).register((Comparable)BlockFace.WALL, (Comparable)Direction.NORTH, (Object)ROTATE_X_90).register((Comparable)BlockFace.WALL, (Comparable)Direction.EAST, (Object)ROTATE_X_90.then(ROTATE_Y_90)).register((Comparable)BlockFace.WALL, (Comparable)Direction.SOUTH, (Object)ROTATE_X_90.then(ROTATE_Y_180)).register((Comparable)BlockFace.WALL, (Comparable)Direction.WEST, (Object)ROTATE_X_90.then(ROTATE_Y_270)).register((Comparable)BlockFace.CEILING, (Comparable)Direction.SOUTH, (Object)ROTATE_X_180).register((Comparable)BlockFace.CEILING, (Comparable)Direction.WEST, (Object)ROTATE_X_180.then(ROTATE_Y_90)).register((Comparable)BlockFace.CEILING, (Comparable)Direction.NORTH, (Object)ROTATE_X_180.then(ROTATE_Y_180)).register((Comparable)BlockFace.CEILING, (Comparable)Direction.EAST, (Object)ROTATE_X_180.then(ROTATE_Y_270))));
    }

    public final void registerCooker(Block cooker, TexturedModel.Factory modelFactory) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)modelFactory.upload(cooker, this.modelCollector));
        Identifier identifier = TextureMap.getSubId((Block)cooker, (String)"_front_on");
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)modelFactory.get(cooker).textures(textures -> textures.put(TextureKey.FRONT, identifier)).upload(cooker, "_on", this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)cooker).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.LIT, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public final void registerCampfire(Block ... blocks) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"campfire_off"));
        for (Block block : blocks) {
            WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CAMPFIRE.upload(block, TextureMap.campfire((Block)block), this.modelCollector));
            this.registerItemModel(block.asItem());
            this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.LIT, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)).apply(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
        }
    }

    public final void registerAzalea(Block block) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_AZALEA.upload(block, TextureMap.sideAndTop((Block)block), this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)weightedVariant));
    }

    public final void registerPottedAzaleaBush(Block block) {
        WeightedVariant weightedVariant = block == Blocks.POTTED_FLOWERING_AZALEA_BUSH ? BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_POTTED_FLOWERING_AZALEA_BUSH.upload(block, TextureMap.pottedAzaleaBush((Block)block), this.modelCollector)) : BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_POTTED_AZALEA_BUSH.upload(block, TextureMap.pottedAzaleaBush((Block)block), this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)weightedVariant));
    }

    private void registerBookshelf() {
        TextureMap textureMap = TextureMap.sideEnd((Identifier)TextureMap.getId((Block)Blocks.BOOKSHELF), (Identifier)TextureMap.getId((Block)Blocks.OAK_PLANKS));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_COLUMN.upload(Blocks.BOOKSHELF, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)Blocks.BOOKSHELF, (WeightedVariant)weightedVariant));
    }

    private void registerRedstone() {
        this.registerItemModel(Items.REDSTONE);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)Blocks.REDSTONE_WIRE).with(BlockStateModelGenerator.or((MultipartModelConditionBuilder[])new MultipartModelConditionBuilder[]{BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH_WIRE_CONNECTION, (Comparable)WireConnection.NONE).put((Property)Properties.EAST_WIRE_CONNECTION, (Comparable)WireConnection.NONE).put((Property)Properties.SOUTH_WIRE_CONNECTION, (Comparable)WireConnection.NONE).put((Property)Properties.WEST_WIRE_CONNECTION, (Comparable)WireConnection.NONE), BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}).put((Property)Properties.EAST_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}).put((Property)Properties.SOUTH_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}).put((Property)Properties.WEST_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}).put((Property)Properties.NORTH_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP})}), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"redstone_dust_dot"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"redstone_dust_side0"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"redstone_dust_side_alt0"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"redstone_dust_side_alt1")).apply(ROTATE_Y_270)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST_WIRE_CONNECTION, (Comparable)WireConnection.SIDE, (Comparable[])new WireConnection[]{WireConnection.UP}), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"redstone_dust_side1")).apply(ROTATE_Y_270)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH_WIRE_CONNECTION, (Comparable)WireConnection.UP), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"redstone_dust_up"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST_WIRE_CONNECTION, (Comparable)WireConnection.UP), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"redstone_dust_up")).apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH_WIRE_CONNECTION, (Comparable)WireConnection.UP), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"redstone_dust_up")).apply(ROTATE_Y_180)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST_WIRE_CONNECTION, (Comparable)WireConnection.UP), BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"redstone_dust_up")).apply(ROTATE_Y_270)));
    }

    private void registerComparator() {
        this.registerItemModel(Items.COMPARATOR);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.COMPARATOR).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.COMPARATOR_MODE, (Property)Properties.POWERED).register((Comparable)ComparatorMode.COMPARE, (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.COMPARATOR))).register((Comparable)ComparatorMode.COMPARE, (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.COMPARATOR, (String)"_on"))).register((Comparable)ComparatorMode.SUBTRACT, (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.COMPARATOR, (String)"_subtract"))).register((Comparable)ComparatorMode.SUBTRACT, (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.COMPARATOR, (String)"_on_subtract")))).apply(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerSmoothStone() {
        TextureMap textureMap = TextureMap.all((Block)Blocks.SMOOTH_STONE);
        TextureMap textureMap2 = TextureMap.sideEnd((Identifier)TextureMap.getSubId((Block)Blocks.SMOOTH_STONE_SLAB, (String)"_side"), (Identifier)textureMap.getTexture(TextureKey.TOP));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.SLAB.upload(Blocks.SMOOTH_STONE_SLAB, textureMap2, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.SLAB_TOP.upload(Blocks.SMOOTH_STONE_SLAB, textureMap2, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_COLUMN.uploadWithoutVariant(Blocks.SMOOTH_STONE_SLAB, "_double", textureMap2, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState((Block)Blocks.SMOOTH_STONE_SLAB, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant3));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)Blocks.SMOOTH_STONE, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_ALL.upload(Blocks.SMOOTH_STONE, textureMap, this.modelCollector))));
    }

    private void registerBrewingStand() {
        this.registerItemModel(Items.BREWING_STAND);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)Blocks.BREWING_STAND).with(BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getId((Block)Blocks.BREWING_STAND))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HAS_BOTTLE_0, (Comparable)Boolean.valueOf(true)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.BREWING_STAND, (String)"_bottle0"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HAS_BOTTLE_1, (Comparable)Boolean.valueOf(true)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.BREWING_STAND, (String)"_bottle1"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HAS_BOTTLE_2, (Comparable)Boolean.valueOf(true)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.BREWING_STAND, (String)"_bottle2"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HAS_BOTTLE_0, (Comparable)Boolean.valueOf(false)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.BREWING_STAND, (String)"_empty0"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HAS_BOTTLE_1, (Comparable)Boolean.valueOf(false)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.BREWING_STAND, (String)"_empty1"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HAS_BOTTLE_2, (Comparable)Boolean.valueOf(false)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.BREWING_STAND, (String)"_empty2"))));
    }

    public final void registerMushroomBlock(Block mushroomBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_SINGLE_FACE.upload(mushroomBlock, TextureMap.texture((Block)mushroomBlock), this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"mushroom_block_inside"));
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)mushroomBlock).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(true)), weightedVariant).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(ROTATE_Y_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(ROTATE_Y_180).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(ROTATE_Y_270).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.UP, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(ROTATE_X_270).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.DOWN, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(ROTATE_X_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(false)), weightedVariant2).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST, (Comparable)Boolean.valueOf(false)), weightedVariant2.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(false)), weightedVariant2.apply(ROTATE_Y_180)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST, (Comparable)Boolean.valueOf(false)), weightedVariant2.apply(ROTATE_Y_270)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.UP, (Comparable)Boolean.valueOf(false)), weightedVariant2.apply(ROTATE_X_270)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.DOWN, (Comparable)Boolean.valueOf(false)), weightedVariant2.apply(ROTATE_X_90)));
        this.registerParentedItemModel(mushroomBlock, TexturedModel.CUBE_ALL.upload(mushroomBlock, "_inventory", this.modelCollector));
    }

    private void registerCake() {
        this.registerItemModel(Items.CAKE);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.CAKE).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.BITES).register((Comparable)Integer.valueOf(0), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.CAKE))).register((Comparable)Integer.valueOf(1), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CAKE, (String)"_slice1"))).register((Comparable)Integer.valueOf(2), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CAKE, (String)"_slice2"))).register((Comparable)Integer.valueOf(3), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CAKE, (String)"_slice3"))).register((Comparable)Integer.valueOf(4), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CAKE, (String)"_slice4"))).register((Comparable)Integer.valueOf(5), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CAKE, (String)"_slice5"))).register((Comparable)Integer.valueOf(6), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CAKE, (String)"_slice6")))));
    }

    private void registerCartographyTable() {
        TextureMap textureMap = new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId((Block)Blocks.CARTOGRAPHY_TABLE, (String)"_side3")).put(TextureKey.DOWN, TextureMap.getId((Block)Blocks.DARK_OAK_PLANKS)).put(TextureKey.UP, TextureMap.getSubId((Block)Blocks.CARTOGRAPHY_TABLE, (String)"_top")).put(TextureKey.NORTH, TextureMap.getSubId((Block)Blocks.CARTOGRAPHY_TABLE, (String)"_side3")).put(TextureKey.EAST, TextureMap.getSubId((Block)Blocks.CARTOGRAPHY_TABLE, (String)"_side3")).put(TextureKey.SOUTH, TextureMap.getSubId((Block)Blocks.CARTOGRAPHY_TABLE, (String)"_side1")).put(TextureKey.WEST, TextureMap.getSubId((Block)Blocks.CARTOGRAPHY_TABLE, (String)"_side2"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)Blocks.CARTOGRAPHY_TABLE, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE.upload(Blocks.CARTOGRAPHY_TABLE, textureMap, this.modelCollector))));
    }

    private void registerSmithingTable() {
        TextureMap textureMap = new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId((Block)Blocks.SMITHING_TABLE, (String)"_front")).put(TextureKey.DOWN, TextureMap.getSubId((Block)Blocks.SMITHING_TABLE, (String)"_bottom")).put(TextureKey.UP, TextureMap.getSubId((Block)Blocks.SMITHING_TABLE, (String)"_top")).put(TextureKey.NORTH, TextureMap.getSubId((Block)Blocks.SMITHING_TABLE, (String)"_front")).put(TextureKey.SOUTH, TextureMap.getSubId((Block)Blocks.SMITHING_TABLE, (String)"_front")).put(TextureKey.EAST, TextureMap.getSubId((Block)Blocks.SMITHING_TABLE, (String)"_side")).put(TextureKey.WEST, TextureMap.getSubId((Block)Blocks.SMITHING_TABLE, (String)"_side"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)Blocks.SMITHING_TABLE, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE.upload(Blocks.SMITHING_TABLE, textureMap, this.modelCollector))));
    }

    public final void registerCubeWithCustomTextures(Block block, Block otherTextureSource, BiFunction<Block, Block, TextureMap> texturesFactory) {
        TextureMap textureMap = texturesFactory.apply(block, otherTextureSource);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE.upload(block, textureMap, this.modelCollector))));
    }

    public void registerGeneric(Block block) {
        TextureMap textureMap = new TextureMap().put(TextureKey.PARTICLE, TextureMap.getSubId((Block)block, (String)"_particle")).put(TextureKey.DOWN, TextureMap.getSubId((Block)block, (String)"_down")).put(TextureKey.UP, TextureMap.getSubId((Block)block, (String)"_up")).put(TextureKey.NORTH, TextureMap.getSubId((Block)block, (String)"_north")).put(TextureKey.SOUTH, TextureMap.getSubId((Block)block, (String)"_south")).put(TextureKey.EAST, TextureMap.getSubId((Block)block, (String)"_east")).put(TextureKey.WEST, TextureMap.getSubId((Block)block, (String)"_west"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE.upload(block, textureMap, this.modelCollector))));
    }

    private void registerPumpkins() {
        TextureMap textureMap = TextureMap.sideEnd((Block)Blocks.PUMPKIN);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)Blocks.PUMPKIN, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.PUMPKIN))));
        this.registerNorthDefaultHorizontalRotatable(Blocks.CARVED_PUMPKIN, textureMap);
        this.registerNorthDefaultHorizontalRotatable(Blocks.JACK_O_LANTERN, textureMap);
    }

    public final void registerNorthDefaultHorizontalRotatable(Block block, TextureMap texture) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.ORIENTABLE.upload(block, texture.copyAndAdd(TextureKey.FRONT, TextureMap.getId((Block)block)), this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)weightedVariant).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerCauldrons() {
        this.registerItemModel(Items.CAULDRON);
        this.registerSimpleState(Blocks.CAULDRON);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)Blocks.LAVA_CAULDRON, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CAULDRON_FULL.upload(Blocks.LAVA_CAULDRON, TextureMap.cauldron((Identifier)TextureMap.getSubId((Block)Blocks.LAVA, (String)"_still")), this.modelCollector))));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.WATER_CAULDRON).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)LeveledCauldronBlock.LEVEL).register((Comparable)Integer.valueOf(1), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CAULDRON_LEVEL1.upload(Blocks.WATER_CAULDRON, "_level1", TextureMap.cauldron((Identifier)TextureMap.getSubId((Block)Blocks.WATER, (String)"_still")), this.modelCollector))).register((Comparable)Integer.valueOf(2), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CAULDRON_LEVEL2.upload(Blocks.WATER_CAULDRON, "_level2", TextureMap.cauldron((Identifier)TextureMap.getSubId((Block)Blocks.WATER, (String)"_still")), this.modelCollector))).register((Comparable)Integer.valueOf(3), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CAULDRON_FULL.upload(Blocks.WATER_CAULDRON, "_full", TextureMap.cauldron((Identifier)TextureMap.getSubId((Block)Blocks.WATER, (String)"_still")), this.modelCollector)))));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.POWDER_SNOW_CAULDRON).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)LeveledCauldronBlock.LEVEL).register((Comparable)Integer.valueOf(1), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CAULDRON_LEVEL1.upload(Blocks.POWDER_SNOW_CAULDRON, "_level1", TextureMap.cauldron((Identifier)TextureMap.getId((Block)Blocks.POWDER_SNOW)), this.modelCollector))).register((Comparable)Integer.valueOf(2), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CAULDRON_LEVEL2.upload(Blocks.POWDER_SNOW_CAULDRON, "_level2", TextureMap.cauldron((Identifier)TextureMap.getId((Block)Blocks.POWDER_SNOW)), this.modelCollector))).register((Comparable)Integer.valueOf(3), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CAULDRON_FULL.upload(Blocks.POWDER_SNOW_CAULDRON, "_full", TextureMap.cauldron((Identifier)TextureMap.getId((Block)Blocks.POWDER_SNOW)), this.modelCollector)))));
    }

    private void registerChorusFlower() {
        TextureMap textureMap = TextureMap.texture((Block)Blocks.CHORUS_FLOWER);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CHORUS_FLOWER.upload(Blocks.CHORUS_FLOWER, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.CHORUS_FLOWER, "_dead", Models.TEMPLATE_CHORUS_FLOWER, id -> textureMap.copyAndAdd(TextureKey.TEXTURE, id)));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.CHORUS_FLOWER).with(BlockStateModelGenerator.createValueFencedModelMap((Property)Properties.AGE_5, (Comparable)Integer.valueOf(5), (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)));
    }

    private void registerCrafter() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.CRAFTER));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CRAFTER, (String)"_triggered"));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CRAFTER, (String)"_crafting"));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CRAFTER, (String)"_crafting_triggered"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.CRAFTER).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.TRIGGERED, (Property)CrafterBlock.CRAFTING).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Object)weightedVariant).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Object)weightedVariant4).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Object)weightedVariant2).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Object)weightedVariant3)).apply(BlockStateVariantMap.operations((Property)Properties.ORIENTATION).generate(BlockStateModelGenerator::addJigsawOrientationToVariant)));
    }

    public final void registerDispenserLikeOrientable(Block block) {
        TextureMap textureMap = new TextureMap().put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.FURNACE, (String)"_top")).put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.FURNACE, (String)"_side")).put(TextureKey.FRONT, TextureMap.getSubId((Block)block, (String)"_front"));
        TextureMap textureMap2 = new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.FURNACE, (String)"_top")).put(TextureKey.FRONT, TextureMap.getSubId((Block)block, (String)"_front_vertical"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.ORIENTABLE.upload(block, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.ORIENTABLE_VERTICAL.upload(block, textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.FACING).register((Comparable)Direction.DOWN, (Object)weightedVariant2.apply(ROTATE_X_180)).register((Comparable)Direction.UP, (Object)weightedVariant2).register((Comparable)Direction.NORTH, (Object)weightedVariant).register((Comparable)Direction.EAST, (Object)weightedVariant.apply(ROTATE_Y_90)).register((Comparable)Direction.SOUTH, (Object)weightedVariant.apply(ROTATE_Y_180)).register((Comparable)Direction.WEST, (Object)weightedVariant.apply(ROTATE_Y_270))));
    }

    private void registerEndPortalFrame() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.END_PORTAL_FRAME));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.END_PORTAL_FRAME, (String)"_filled"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.END_PORTAL_FRAME).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.EYE).register((Comparable)Boolean.valueOf(false), (Object)weightedVariant).register((Comparable)Boolean.valueOf(true), (Object)weightedVariant2)).apply(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerChorusPlant() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CHORUS_PLANT, (String)"_side"));
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CHORUS_PLANT, (String)"_noside"));
        ModelVariant modelVariant2 = BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CHORUS_PLANT, (String)"_noside1"));
        ModelVariant modelVariant3 = BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CHORUS_PLANT, (String)"_noside2"));
        ModelVariant modelVariant4 = BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CHORUS_PLANT, (String)"_noside3"));
        ModelVariant modelVariant5 = modelVariant.with(UV_LOCK);
        ModelVariant modelVariant6 = modelVariant2.with(UV_LOCK);
        ModelVariant modelVariant7 = modelVariant3.with(UV_LOCK);
        ModelVariant modelVariant8 = modelVariant4.with(UV_LOCK);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)Blocks.CHORUS_PLANT).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(true)), weightedVariant).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(ROTATE_Y_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(ROTATE_Y_180).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(ROTATE_Y_270).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.UP, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(ROTATE_X_270).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.DOWN, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(ROTATE_X_90).apply(UV_LOCK)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(false)), new WeightedVariant(Pool.of((Weighted[])new Weighted[]{new Weighted((Object)modelVariant, 2), new Weighted((Object)modelVariant2, 1), new Weighted((Object)modelVariant3, 1), new Weighted((Object)modelVariant4, 1)}))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST, (Comparable)Boolean.valueOf(false)), new WeightedVariant(Pool.of((Weighted[])new Weighted[]{new Weighted((Object)modelVariant6.with(ROTATE_Y_90), 1), new Weighted((Object)modelVariant7.with(ROTATE_Y_90), 1), new Weighted((Object)modelVariant8.with(ROTATE_Y_90), 1), new Weighted((Object)modelVariant5.with(ROTATE_Y_90), 2)}))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(false)), new WeightedVariant(Pool.of((Weighted[])new Weighted[]{new Weighted((Object)modelVariant7.with(ROTATE_Y_180), 1), new Weighted((Object)modelVariant8.with(ROTATE_Y_180), 1), new Weighted((Object)modelVariant5.with(ROTATE_Y_180), 2), new Weighted((Object)modelVariant6.with(ROTATE_Y_180), 1)}))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST, (Comparable)Boolean.valueOf(false)), new WeightedVariant(Pool.of((Weighted[])new Weighted[]{new Weighted((Object)modelVariant8.with(ROTATE_Y_270), 1), new Weighted((Object)modelVariant5.with(ROTATE_Y_270), 2), new Weighted((Object)modelVariant6.with(ROTATE_Y_270), 1), new Weighted((Object)modelVariant7.with(ROTATE_Y_270), 1)}))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.UP, (Comparable)Boolean.valueOf(false)), new WeightedVariant(Pool.of((Weighted[])new Weighted[]{new Weighted((Object)modelVariant5.with(ROTATE_X_270), 2), new Weighted((Object)modelVariant8.with(ROTATE_X_270), 1), new Weighted((Object)modelVariant6.with(ROTATE_X_270), 1), new Weighted((Object)modelVariant7.with(ROTATE_X_270), 1)}))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.DOWN, (Comparable)Boolean.valueOf(false)), new WeightedVariant(Pool.of((Weighted[])new Weighted[]{new Weighted((Object)modelVariant8.with(ROTATE_X_90), 1), new Weighted((Object)modelVariant7.with(ROTATE_X_90), 1), new Weighted((Object)modelVariant6.with(ROTATE_X_90), 1), new Weighted((Object)modelVariant5.with(ROTATE_X_90), 2)}))));
    }

    private void registerComposter() {
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)Blocks.COMPOSTER).with(BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getId((Block)Blocks.COMPOSTER))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.LEVEL_8, (Comparable)Integer.valueOf(1)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.COMPOSTER, (String)"_contents1"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.LEVEL_8, (Comparable)Integer.valueOf(2)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.COMPOSTER, (String)"_contents2"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.LEVEL_8, (Comparable)Integer.valueOf(3)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.COMPOSTER, (String)"_contents3"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.LEVEL_8, (Comparable)Integer.valueOf(4)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.COMPOSTER, (String)"_contents4"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.LEVEL_8, (Comparable)Integer.valueOf(5)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.COMPOSTER, (String)"_contents5"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.LEVEL_8, (Comparable)Integer.valueOf(6)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.COMPOSTER, (String)"_contents6"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.LEVEL_8, (Comparable)Integer.valueOf(7)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.COMPOSTER, (String)"_contents7"))).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.LEVEL_8, (Comparable)Integer.valueOf(8)), BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.COMPOSTER, (String)"_contents_ready"))));
    }

    public final void registerCopperBulb(Block copperBulbBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_ALL.upload(copperBulbBlock, TextureMap.all((Block)copperBulbBlock), this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(copperBulbBlock, "_powered", Models.CUBE_ALL, TextureMap::all));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(copperBulbBlock, "_lit", Models.CUBE_ALL, TextureMap::all));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(copperBulbBlock, "_lit_powered", Models.CUBE_ALL, TextureMap::all));
        this.blockStateCollector.accept(BlockStateModelGenerator.createCopperBulbBlockState((Block)copperBulbBlock, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant3, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant4));
    }

    public static BlockModelDefinitionCreator createCopperBulbBlockState(Block block, WeightedVariant unlitUnpoweredModel, WeightedVariant litUnpoweredModel, WeightedVariant unlitPoweredModel, WeightedVariant litPoweredModel) {
        return VariantsBlockModelDefinitionCreator.of((Block)block).with(BlockStateVariantMap.models((Property)Properties.LIT, (Property)Properties.POWERED).generate((lit, powered) -> {
            if (lit.booleanValue()) {
                return powered != false ? litPoweredModel : litUnpoweredModel;
            }
            return powered != false ? unlitPoweredModel : unlitUnpoweredModel;
        }));
    }

    public final void registerWaxedCopperBulb(Block unwaxedCopperBulbBlock, Block waxedCopperBulbBlock) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)unwaxedCopperBulbBlock));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)unwaxedCopperBulbBlock, (String)"_powered"));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)unwaxedCopperBulbBlock, (String)"_lit"));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)unwaxedCopperBulbBlock, (String)"_lit_powered"));
        this.itemModelOutput.acceptAlias(unwaxedCopperBulbBlock.asItem(), waxedCopperBulbBlock.asItem());
        this.blockStateCollector.accept(BlockStateModelGenerator.createCopperBulbBlockState((Block)waxedCopperBulbBlock, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant3, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant4));
    }

    public final void registerAmethyst(Block block) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CROSS.upload(block, TextureMap.cross((Block)block), this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)weightedVariant).apply(UP_DEFAULT_ROTATION_OPERATIONS));
    }

    private void registerAmethysts() {
        this.registerAmethyst(Blocks.SMALL_AMETHYST_BUD);
        this.registerAmethyst(Blocks.MEDIUM_AMETHYST_BUD);
        this.registerAmethyst(Blocks.LARGE_AMETHYST_BUD);
        this.registerAmethyst(Blocks.AMETHYST_CLUSTER);
    }

    private void registerPointedDripstone() {
        BlockStateVariantMap.DoubleProperty doubleProperty = BlockStateVariantMap.models((Property)Properties.VERTICAL_DIRECTION, (Property)Properties.THICKNESS);
        for (Thickness thickness : Thickness.values()) {
            doubleProperty.register((Comparable)Direction.UP, (Comparable)thickness, (Object)this.getDripstoneVariant(Direction.UP, thickness));
        }
        for (Thickness thickness : Thickness.values()) {
            doubleProperty.register((Comparable)Direction.DOWN, (Comparable)thickness, (Object)this.getDripstoneVariant(Direction.DOWN, thickness));
        }
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.POINTED_DRIPSTONE).with((BlockStateVariantMap)doubleProperty));
    }

    public final WeightedVariant getDripstoneVariant(Direction direction, Thickness thickness) {
        String string = "_" + direction.asString() + "_" + thickness.asString();
        TextureMap textureMap = TextureMap.cross((Identifier)TextureMap.getSubId((Block)Blocks.POINTED_DRIPSTONE, (String)string));
        return BlockStateModelGenerator.createWeightedVariant((Identifier)Models.POINTED_DRIPSTONE.upload(Blocks.POINTED_DRIPSTONE, string, textureMap, this.modelCollector));
    }

    public final void registerNetherrackBottomCustomTop(Block block) {
        TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, TextureMap.getId((Block)Blocks.NETHERRACK)).put(TextureKey.TOP, TextureMap.getId((Block)block)).put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_BOTTOM_TOP.upload(block, textureMap, this.modelCollector))));
    }

    private void registerDaylightDetector() {
        Identifier identifier = TextureMap.getSubId((Block)Blocks.DAYLIGHT_DETECTOR, (String)"_side");
        TextureMap textureMap = new TextureMap().put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.DAYLIGHT_DETECTOR, (String)"_top")).put(TextureKey.SIDE, identifier);
        TextureMap textureMap2 = new TextureMap().put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.DAYLIGHT_DETECTOR, (String)"_inverted_top")).put(TextureKey.SIDE, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.DAYLIGHT_DETECTOR).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.INVERTED).register((Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_DAYLIGHT_DETECTOR.upload(Blocks.DAYLIGHT_DETECTOR, textureMap, this.modelCollector))).register((Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_DAYLIGHT_DETECTOR.upload(ModelIds.getBlockSubModelId((Block)Blocks.DAYLIGHT_DETECTOR, (String)"_inverted"), textureMap2, this.modelCollector)))));
    }

    public final void registerRod(Block block) {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)block))).apply(UP_DEFAULT_ROTATION_OPERATIONS));
    }

    public final void registerLightningRod(Block unwaxed, Block waxed) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.LIGHTNING_ROD, (String)"_on"));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_LIGHTNING_ROD.upload(unwaxed, TextureMap.texture((Block)unwaxed), this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)unwaxed).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.POWERED, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant2)).apply(UP_DEFAULT_ROTATION_OPERATIONS));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)waxed).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.POWERED, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant2)).apply(UP_DEFAULT_ROTATION_OPERATIONS));
        this.itemModelOutput.acceptAlias(unwaxed.asItem(), waxed.asItem());
    }

    private void registerFarmland() {
        TextureMap textureMap = new TextureMap().put(TextureKey.DIRT, TextureMap.getId((Block)Blocks.DIRT)).put(TextureKey.TOP, TextureMap.getId((Block)Blocks.FARMLAND));
        TextureMap textureMap2 = new TextureMap().put(TextureKey.DIRT, TextureMap.getId((Block)Blocks.DIRT)).put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.FARMLAND, (String)"_moist"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_FARMLAND.upload(Blocks.FARMLAND, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_FARMLAND.upload(TextureMap.getSubId((Block)Blocks.FARMLAND, (String)"_moist"), textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.FARMLAND).with(BlockStateModelGenerator.createValueFencedModelMap((Property)Properties.MOISTURE, (Comparable)Integer.valueOf(7), (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)));
    }

    public final WeightedVariant getFireFloorModels(Block texture) {
        return BlockStateModelGenerator.createWeightedVariant((ModelVariant[])new ModelVariant[]{BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId((Block)texture, (String)"_floor0"), TextureMap.fire0((Block)texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FIRE_FLOOR.upload(ModelIds.getBlockSubModelId((Block)texture, (String)"_floor1"), TextureMap.fire1((Block)texture), this.modelCollector))});
    }

    public final WeightedVariant getFireSideModels(Block texture) {
        return BlockStateModelGenerator.createWeightedVariant((ModelVariant[])new ModelVariant[]{BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId((Block)texture, (String)"_side0"), TextureMap.fire0((Block)texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FIRE_SIDE.upload(ModelIds.getBlockSubModelId((Block)texture, (String)"_side1"), TextureMap.fire1((Block)texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FIRE_SIDE_ALT.upload(ModelIds.getBlockSubModelId((Block)texture, (String)"_side_alt0"), TextureMap.fire0((Block)texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FIRE_SIDE_ALT.upload(ModelIds.getBlockSubModelId((Block)texture, (String)"_side_alt1"), TextureMap.fire1((Block)texture), this.modelCollector))});
    }

    public final WeightedVariant getFireUpModels(Block texture) {
        return BlockStateModelGenerator.createWeightedVariant((ModelVariant[])new ModelVariant[]{BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId((Block)texture, (String)"_up0"), TextureMap.fire0((Block)texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FIRE_UP.upload(ModelIds.getBlockSubModelId((Block)texture, (String)"_up1"), TextureMap.fire1((Block)texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId((Block)texture, (String)"_up_alt0"), TextureMap.fire0((Block)texture), this.modelCollector)), BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FIRE_UP_ALT.upload(ModelIds.getBlockSubModelId((Block)texture, (String)"_up_alt1"), TextureMap.fire1((Block)texture), this.modelCollector))});
    }

    private void registerFire() {
        MultipartModelConditionBuilder multipartModelConditionBuilder = BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(false)).put((Property)Properties.EAST, (Comparable)Boolean.valueOf(false)).put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(false)).put((Property)Properties.WEST, (Comparable)Boolean.valueOf(false)).put((Property)Properties.UP, (Comparable)Boolean.valueOf(false));
        WeightedVariant weightedVariant = this.getFireFloorModels(Blocks.FIRE);
        WeightedVariant weightedVariant2 = this.getFireSideModels(Blocks.FIRE);
        WeightedVariant weightedVariant3 = this.getFireUpModels(Blocks.FIRE);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)Blocks.FIRE).with(multipartModelConditionBuilder, weightedVariant).with(BlockStateModelGenerator.or((MultipartModelConditionBuilder[])new MultipartModelConditionBuilder[]{BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(true)), multipartModelConditionBuilder}), weightedVariant2).with(BlockStateModelGenerator.or((MultipartModelConditionBuilder[])new MultipartModelConditionBuilder[]{BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST, (Comparable)Boolean.valueOf(true)), multipartModelConditionBuilder}), weightedVariant2.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.or((MultipartModelConditionBuilder[])new MultipartModelConditionBuilder[]{BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(true)), multipartModelConditionBuilder}), weightedVariant2.apply(ROTATE_Y_180)).with(BlockStateModelGenerator.or((MultipartModelConditionBuilder[])new MultipartModelConditionBuilder[]{BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST, (Comparable)Boolean.valueOf(true)), multipartModelConditionBuilder}), weightedVariant2.apply(ROTATE_Y_270)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.UP, (Comparable)Boolean.valueOf(true)), weightedVariant3));
    }

    private void registerSoulFire() {
        WeightedVariant weightedVariant = this.getFireFloorModels(Blocks.SOUL_FIRE);
        WeightedVariant weightedVariant2 = this.getFireSideModels(Blocks.SOUL_FIRE);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)Blocks.SOUL_FIRE).with(weightedVariant).with(weightedVariant2).with(weightedVariant2.apply(ROTATE_Y_90)).with(weightedVariant2.apply(ROTATE_Y_180)).with(weightedVariant2.apply(ROTATE_Y_270)));
    }

    public final void registerLantern(Block lantern) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.TEMPLATE_LANTERN.upload(lantern, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.TEMPLATE_HANGING_LANTERN.upload(lantern, this.modelCollector));
        this.registerItemModel(lantern.asItem());
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)lantern).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.HANGING, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)));
    }

    public final void registerCopperLantern(Block unwaxed, Block waxed) {
        Identifier identifier = TexturedModel.TEMPLATE_LANTERN.upload(unwaxed, this.modelCollector);
        Identifier identifier2 = TexturedModel.TEMPLATE_HANGING_LANTERN.upload(unwaxed, this.modelCollector);
        this.registerItemModel(unwaxed.asItem());
        this.itemModelOutput.acceptAlias(unwaxed.asItem(), waxed.asItem());
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)unwaxed).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.HANGING, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)identifier2), (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)identifier))));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)waxed).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.HANGING, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)identifier2), (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)identifier))));
    }

    public final void registerCopperChain(Block unwaxed, Block waxed) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.TEMPLATE_CHAIN.upload(unwaxed, this.modelCollector));
        this.registerAxisRotated(unwaxed, weightedVariant);
        this.registerAxisRotated(waxed, weightedVariant);
    }

    private void registerMuddyMangroveRoots() {
        TextureMap textureMap = TextureMap.sideEnd((Identifier)TextureMap.getSubId((Block)Blocks.MUDDY_MANGROVE_ROOTS, (String)"_side"), (Identifier)TextureMap.getSubId((Block)Blocks.MUDDY_MANGROVE_ROOTS, (String)"_top"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_COLUMN.upload(Blocks.MUDDY_MANGROVE_ROOTS, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState((Block)Blocks.MUDDY_MANGROVE_ROOTS, (WeightedVariant)weightedVariant));
    }

    private void registerMangrovePropagule() {
        this.registerItemModel(Items.MANGROVE_PROPAGULE);
        Block block = Blocks.MANGROVE_PROPAGULE;
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)block));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.MANGROVE_PROPAGULE).with(BlockStateVariantMap.models((Property)PropaguleBlock.HANGING, (Property)PropaguleBlock.AGE).generate((hanging, age) -> hanging != false ? BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)block, (String)("_hanging_" + age))) : weightedVariant)));
    }

    private void registerFrostedIce() {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.FROSTED_ICE).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.AGE_3).register((Comparable)Integer.valueOf(0), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.FROSTED_ICE, "_0", Models.CUBE_ALL, TextureMap::all))).register((Comparable)Integer.valueOf(1), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.FROSTED_ICE, "_1", Models.CUBE_ALL, TextureMap::all))).register((Comparable)Integer.valueOf(2), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.FROSTED_ICE, "_2", Models.CUBE_ALL, TextureMap::all))).register((Comparable)Integer.valueOf(3), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.FROSTED_ICE, "_3", Models.CUBE_ALL, TextureMap::all)))));
    }

    private void registerTopSoils() {
        Identifier identifier = TextureMap.getId((Block)Blocks.DIRT);
        TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, identifier).inherit(TextureKey.BOTTOM, TextureKey.PARTICLE).put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.GRASS_BLOCK, (String)"_top")).put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.GRASS_BLOCK, (String)"_snow"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_BOTTOM_TOP.upload(Blocks.GRASS_BLOCK, "_snow", textureMap, this.modelCollector));
        Identifier identifier2 = ModelIds.getBlockModelId((Block)Blocks.GRASS_BLOCK);
        this.registerTopSoil(Blocks.GRASS_BLOCK, BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)identifier2)), weightedVariant);
        this.registerTintedItemModel(Blocks.GRASS_BLOCK, identifier2, (TintSource)new GrassTintSource());
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)TexturedModel.CUBE_BOTTOM_TOP.get(Blocks.MYCELIUM).textures(textures -> textures.put(TextureKey.BOTTOM, identifier)).upload(Blocks.MYCELIUM, this.modelCollector)));
        this.registerTopSoil(Blocks.MYCELIUM, weightedVariant2, weightedVariant);
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)TexturedModel.CUBE_BOTTOM_TOP.get(Blocks.PODZOL).textures(textures -> textures.put(TextureKey.BOTTOM, identifier)).upload(Blocks.PODZOL, this.modelCollector)));
        this.registerTopSoil(Blocks.PODZOL, weightedVariant3, weightedVariant);
    }

    public final void registerTopSoil(Block topSoil, WeightedVariant regularVariant, WeightedVariant snowyVariant) {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)topSoil).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.SNOWY).register((Comparable)Boolean.valueOf(true), (Object)snowyVariant).register((Comparable)Boolean.valueOf(false), (Object)regularVariant)));
    }

    private void registerCocoa() {
        this.registerItemModel(Items.COCOA_BEANS);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.COCOA).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.AGE_2).register((Comparable)Integer.valueOf(0), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.COCOA, (String)"_stage0"))).register((Comparable)Integer.valueOf(1), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.COCOA, (String)"_stage1"))).register((Comparable)Integer.valueOf(2), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.COCOA, (String)"_stage2")))).apply(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerDirtPath() {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.DIRT_PATH));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.DIRT_PATH, (WeightedVariant)BlockStateModelGenerator.modelWithYRotation((ModelVariant)modelVariant)));
    }

    public final void registerWeightedPressurePlate(Block weightedPressurePlate, Block textureSource) {
        TextureMap textureMap = TextureMap.texture((Block)textureSource);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.PRESSURE_PLATE_UP.upload(weightedPressurePlate, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.PRESSURE_PLATE_DOWN.upload(weightedPressurePlate, textureMap, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)weightedPressurePlate).with(BlockStateModelGenerator.createValueFencedModelMap((Property)Properties.POWER, (Comparable)Integer.valueOf(1), (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)));
    }

    private void registerHopper() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.HOPPER));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.HOPPER, (String)"_side"));
        this.registerItemModel(Items.HOPPER);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.HOPPER).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.HOPPER_FACING).register((Comparable)Direction.DOWN, (Object)weightedVariant).register((Comparable)Direction.NORTH, (Object)weightedVariant2).register((Comparable)Direction.EAST, (Object)weightedVariant2.apply(ROTATE_Y_90)).register((Comparable)Direction.SOUTH, (Object)weightedVariant2.apply(ROTATE_Y_180)).register((Comparable)Direction.WEST, (Object)weightedVariant2.apply(ROTATE_Y_270))));
    }

    public final void registerParented(Block modelSource, Block child) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)modelSource));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)child, (WeightedVariant)weightedVariant));
        this.itemModelOutput.acceptAlias(modelSource.asItem(), child.asItem());
    }

    public final void registerBars(Block block) {
        TextureMap textureMap = TextureMap.bars((Block)block);
        this.registerBars(block, Models.TEMPLATE_BARS_POST_ENDS.upload(block, textureMap, this.modelCollector), Models.TEMPLATE_BARS_POST.upload(block, textureMap, this.modelCollector), Models.TEMPLATE_BARS_CAP.upload(block, textureMap, this.modelCollector), Models.TEMPLATE_BARS_CAP_ALT.upload(block, textureMap, this.modelCollector), Models.TEMPLATE_BARS_SIDE.upload(block, textureMap, this.modelCollector), Models.TEMPLATE_BARS_SIDE_ALT.upload(block, textureMap, this.modelCollector));
        this.registerItemModel(block);
    }

    public final void registerCopperBars(Block unwaxedBlock, Block waxedBlock) {
        TextureMap textureMap = TextureMap.bars((Block)unwaxedBlock);
        Identifier identifier = Models.TEMPLATE_BARS_POST_ENDS.upload(unwaxedBlock, textureMap, this.modelCollector);
        Identifier identifier2 = Models.TEMPLATE_BARS_POST.upload(unwaxedBlock, textureMap, this.modelCollector);
        Identifier identifier3 = Models.TEMPLATE_BARS_CAP.upload(unwaxedBlock, textureMap, this.modelCollector);
        Identifier identifier4 = Models.TEMPLATE_BARS_CAP_ALT.upload(unwaxedBlock, textureMap, this.modelCollector);
        Identifier identifier5 = Models.TEMPLATE_BARS_SIDE.upload(unwaxedBlock, textureMap, this.modelCollector);
        Identifier identifier6 = Models.TEMPLATE_BARS_SIDE_ALT.upload(unwaxedBlock, textureMap, this.modelCollector);
        this.registerBars(unwaxedBlock, identifier, identifier2, identifier3, identifier4, identifier5, identifier6);
        this.registerBars(waxedBlock, identifier, identifier2, identifier3, identifier4, identifier5, identifier6);
        this.registerItemModel(unwaxedBlock);
        this.itemModelOutput.acceptAlias(unwaxedBlock.asItem(), waxedBlock.asItem());
    }

    public final void registerBars(Block block, Identifier postEndsModelId, Identifier postModelId, Identifier capModelId, Identifier capAltModelId, Identifier sideModelId, Identifier sideAltModelId) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)postEndsModelId);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)postModelId);
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)capModelId);
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)capAltModelId);
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant((Identifier)sideModelId);
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant((Identifier)sideAltModelId);
        this.blockStateCollector.accept(MultipartBlockModelDefinitionCreator.create((Block)block).with(weightedVariant).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(false)).put((Property)Properties.EAST, (Comparable)Boolean.valueOf(false)).put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(false)).put((Property)Properties.WEST, (Comparable)Boolean.valueOf(false)), weightedVariant2).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(true)).put((Property)Properties.EAST, (Comparable)Boolean.valueOf(false)).put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(false)).put((Property)Properties.WEST, (Comparable)Boolean.valueOf(false)), weightedVariant3).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(false)).put((Property)Properties.EAST, (Comparable)Boolean.valueOf(true)).put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(false)).put((Property)Properties.WEST, (Comparable)Boolean.valueOf(false)), weightedVariant3.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(false)).put((Property)Properties.EAST, (Comparable)Boolean.valueOf(false)).put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(true)).put((Property)Properties.WEST, (Comparable)Boolean.valueOf(false)), weightedVariant4).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(false)).put((Property)Properties.EAST, (Comparable)Boolean.valueOf(false)).put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(false)).put((Property)Properties.WEST, (Comparable)Boolean.valueOf(true)), weightedVariant4.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.NORTH, (Comparable)Boolean.valueOf(true)), weightedVariant5).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.EAST, (Comparable)Boolean.valueOf(true)), weightedVariant5.apply(ROTATE_Y_90)).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.SOUTH, (Comparable)Boolean.valueOf(true)), weightedVariant6).with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.WEST, (Comparable)Boolean.valueOf(true)), weightedVariant6.apply(ROTATE_Y_90)));
    }

    public final void registerNorthDefaultHorizontalRotatable(Block block) {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)block))).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerLever() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.LEVER));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.LEVER, (String)"_on"));
        this.registerItemModel(Blocks.LEVER);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.LEVER).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.POWERED, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant2)).apply((BlockStateVariantMap)BlockStateVariantMap.operations((Property)Properties.BLOCK_FACE, (Property)Properties.HORIZONTAL_FACING).register((Comparable)BlockFace.CEILING, (Comparable)Direction.NORTH, (Object)ROTATE_X_180.then(ROTATE_Y_180)).register((Comparable)BlockFace.CEILING, (Comparable)Direction.EAST, (Object)ROTATE_X_180.then(ROTATE_Y_270)).register((Comparable)BlockFace.CEILING, (Comparable)Direction.SOUTH, (Object)ROTATE_X_180).register((Comparable)BlockFace.CEILING, (Comparable)Direction.WEST, (Object)ROTATE_X_180.then(ROTATE_Y_90)).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.NORTH, (Object)NO_OP).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.EAST, (Object)ROTATE_Y_90).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.SOUTH, (Object)ROTATE_Y_180).register((Comparable)BlockFace.FLOOR, (Comparable)Direction.WEST, (Object)ROTATE_Y_270).register((Comparable)BlockFace.WALL, (Comparable)Direction.NORTH, (Object)ROTATE_X_90).register((Comparable)BlockFace.WALL, (Comparable)Direction.EAST, (Object)ROTATE_X_90.then(ROTATE_Y_90)).register((Comparable)BlockFace.WALL, (Comparable)Direction.SOUTH, (Object)ROTATE_X_90.then(ROTATE_Y_180)).register((Comparable)BlockFace.WALL, (Comparable)Direction.WEST, (Object)ROTATE_X_90.then(ROTATE_Y_270))));
    }

    private void registerLilyPad() {
        Identifier identifier = this.uploadBlockItemModel(Items.LILY_PAD, Blocks.LILY_PAD);
        this.registerTintedItemModel(Blocks.LILY_PAD, identifier, ItemModels.constantTintSource((int)-9321636));
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.LILY_PAD));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.LILY_PAD, (WeightedVariant)BlockStateModelGenerator.modelWithYRotation((ModelVariant)modelVariant)));
    }

    private void registerFrogspawn() {
        this.registerItemModel(Blocks.FROGSPAWN);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)Blocks.FROGSPAWN, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.FROGSPAWN))));
    }

    private void registerNetherPortal() {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.NETHER_PORTAL).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.HORIZONTAL_AXIS).register((Comparable)Direction.Axis.X, (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.NETHER_PORTAL, (String)"_ns"))).register((Comparable)Direction.Axis.Z, (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.NETHER_PORTAL, (String)"_ew")))));
    }

    private void registerNetherrack() {
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)TexturedModel.CUBE_ALL.upload(Blocks.NETHERRACK, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.NETHERRACK, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((ModelVariant[])new ModelVariant[]{modelVariant, modelVariant.with(ROTATE_X_90), modelVariant.with(ROTATE_X_180), modelVariant.with(ROTATE_X_270), modelVariant.with(ROTATE_Y_90), modelVariant.with(ROTATE_Y_90.then(ROTATE_X_90)), modelVariant.with(ROTATE_Y_90.then(ROTATE_X_180)), modelVariant.with(ROTATE_Y_90.then(ROTATE_X_270)), modelVariant.with(ROTATE_Y_180), modelVariant.with(ROTATE_Y_180.then(ROTATE_X_90)), modelVariant.with(ROTATE_Y_180.then(ROTATE_X_180)), modelVariant.with(ROTATE_Y_180.then(ROTATE_X_270)), modelVariant.with(ROTATE_Y_270), modelVariant.with(ROTATE_Y_270.then(ROTATE_X_90)), modelVariant.with(ROTATE_Y_270.then(ROTATE_X_180)), modelVariant.with(ROTATE_Y_270.then(ROTATE_X_270))})));
    }

    private void registerObserver() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.OBSERVER));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.OBSERVER, (String)"_on"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.OBSERVER).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.POWERED, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)).apply(NORTH_DEFAULT_ROTATION_OPERATIONS));
    }

    private void registerPistons() {
        TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, TextureMap.getSubId((Block)Blocks.PISTON, (String)"_bottom")).put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.PISTON, (String)"_side"));
        Identifier identifier = TextureMap.getSubId((Block)Blocks.PISTON, (String)"_top_sticky");
        Identifier identifier2 = TextureMap.getSubId((Block)Blocks.PISTON, (String)"_top");
        TextureMap textureMap2 = textureMap.copyAndAdd(TextureKey.PLATFORM, identifier);
        TextureMap textureMap3 = textureMap.copyAndAdd(TextureKey.PLATFORM, identifier2);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.PISTON, (String)"_base"));
        this.registerPiston(Blocks.PISTON, weightedVariant, textureMap3);
        this.registerPiston(Blocks.STICKY_PISTON, weightedVariant, textureMap2);
        Identifier identifier3 = Models.CUBE_BOTTOM_TOP.upload(Blocks.PISTON, "_inventory", textureMap.copyAndAdd(TextureKey.TOP, identifier2), this.modelCollector);
        Identifier identifier4 = Models.CUBE_BOTTOM_TOP.upload(Blocks.STICKY_PISTON, "_inventory", textureMap.copyAndAdd(TextureKey.TOP, identifier), this.modelCollector);
        this.registerParentedItemModel(Blocks.PISTON, identifier3);
        this.registerParentedItemModel(Blocks.STICKY_PISTON, identifier4);
    }

    public final void registerPiston(Block piston, WeightedVariant weightedVariant, TextureMap textures) {
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_PISTON.upload(piston, textures, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)piston).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.EXTENDED, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant2)).apply(NORTH_DEFAULT_ROTATION_OPERATIONS));
    }

    private void registerPistonHead() {
        TextureMap textureMap = new TextureMap().put(TextureKey.UNSTICKY, TextureMap.getSubId((Block)Blocks.PISTON, (String)"_top")).put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.PISTON, (String)"_side"));
        TextureMap textureMap2 = textureMap.copyAndAdd(TextureKey.PLATFORM, TextureMap.getSubId((Block)Blocks.PISTON, (String)"_top_sticky"));
        TextureMap textureMap3 = textureMap.copyAndAdd(TextureKey.PLATFORM, TextureMap.getSubId((Block)Blocks.PISTON, (String)"_top"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.PISTON_HEAD).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.SHORT, (Property)Properties.PISTON_TYPE).register((Comparable)Boolean.valueOf(false), (Comparable)PistonType.DEFAULT, (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_PISTON_HEAD.upload(Blocks.PISTON, "_head", textureMap3, this.modelCollector))).register((Comparable)Boolean.valueOf(false), (Comparable)PistonType.STICKY, (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_PISTON_HEAD.upload(Blocks.PISTON, "_head_sticky", textureMap2, this.modelCollector))).register((Comparable)Boolean.valueOf(true), (Comparable)PistonType.DEFAULT, (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_PISTON_HEAD_SHORT.upload(Blocks.PISTON, "_head_short", textureMap3, this.modelCollector))).register((Comparable)Boolean.valueOf(true), (Comparable)PistonType.STICKY, (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_PISTON_HEAD_SHORT.upload(Blocks.PISTON, "_head_short_sticky", textureMap2, this.modelCollector)))).apply(NORTH_DEFAULT_ROTATION_OPERATIONS));
    }

    private void registerTrialSpawner() {
        Block block = Blocks.TRIAL_SPAWNER;
        TextureMap textureMap = TextureMap.trialSpawner((Block)block, (String)"_side_inactive", (String)"_top_inactive");
        TextureMap textureMap2 = TextureMap.trialSpawner((Block)block, (String)"_side_active", (String)"_top_active");
        TextureMap textureMap3 = TextureMap.trialSpawner((Block)block, (String)"_side_active", (String)"_top_ejecting_reward");
        TextureMap textureMap4 = TextureMap.trialSpawner((Block)block, (String)"_side_inactive_ominous", (String)"_top_inactive_ominous");
        TextureMap textureMap5 = TextureMap.trialSpawner((Block)block, (String)"_side_active_ominous", (String)"_top_active_ominous");
        TextureMap textureMap6 = TextureMap.trialSpawner((Block)block, (String)"_side_active_ominous", (String)"_top_ejecting_reward_ominous");
        Identifier identifier = Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, textureMap, this.modelCollector);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, "_active", textureMap2, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, "_ejecting_reward", textureMap3, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, "_inactive_ominous", textureMap4, this.modelCollector));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, "_active_ominous", textureMap5, this.modelCollector));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_BOTTOM_TOP_INNER_FACES.upload(block, "_ejecting_reward_ominous", textureMap6, this.modelCollector));
        this.registerParentedItemModel(block, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block).with(BlockStateVariantMap.models((Property)Properties.TRIAL_SPAWNER_STATE, (Property)Properties.OMINOUS).generate((state, ominous) -> switch (1.field_47499[state.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1, 2 -> {
                if (ominous.booleanValue()) {
                    yield weightedVariant4;
                }
                yield weightedVariant;
            }
            case 3, 4, 5 -> {
                if (ominous.booleanValue()) {
                    yield weightedVariant5;
                }
                yield weightedVariant2;
            }
            case 6 -> ominous != false ? weightedVariant6 : weightedVariant3;
        })));
    }

    private void registerVault() {
        Block block = Blocks.VAULT;
        TextureMap textureMap = TextureMap.vault((Block)block, (String)"_front_off", (String)"_side_off", (String)"_top", (String)"_bottom");
        TextureMap textureMap2 = TextureMap.vault((Block)block, (String)"_front_on", (String)"_side_on", (String)"_top", (String)"_bottom");
        TextureMap textureMap3 = TextureMap.vault((Block)block, (String)"_front_ejecting", (String)"_side_on", (String)"_top", (String)"_bottom");
        TextureMap textureMap4 = TextureMap.vault((Block)block, (String)"_front_ejecting", (String)"_side_on", (String)"_top_ejecting", (String)"_bottom");
        Identifier identifier = Models.TEMPLATE_VAULT.upload(block, textureMap, this.modelCollector);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_VAULT.upload(block, "_active", textureMap2, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_VAULT.upload(block, "_unlocking", textureMap3, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_VAULT.upload(block, "_ejecting_reward", textureMap4, this.modelCollector));
        TextureMap textureMap5 = TextureMap.vault((Block)block, (String)"_front_off_ominous", (String)"_side_off_ominous", (String)"_top_ominous", (String)"_bottom_ominous");
        TextureMap textureMap6 = TextureMap.vault((Block)block, (String)"_front_on_ominous", (String)"_side_on_ominous", (String)"_top_ominous", (String)"_bottom_ominous");
        TextureMap textureMap7 = TextureMap.vault((Block)block, (String)"_front_ejecting_ominous", (String)"_side_on_ominous", (String)"_top_ominous", (String)"_bottom_ominous");
        TextureMap textureMap8 = TextureMap.vault((Block)block, (String)"_front_ejecting_ominous", (String)"_side_on_ominous", (String)"_top_ejecting_ominous", (String)"_bottom_ominous");
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_VAULT.upload(block, "_ominous", textureMap5, this.modelCollector));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_VAULT.upload(block, "_active_ominous", textureMap6, this.modelCollector));
        WeightedVariant weightedVariant7 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_VAULT.upload(block, "_unlocking_ominous", textureMap7, this.modelCollector));
        WeightedVariant weightedVariant8 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_VAULT.upload(block, "_ejecting_reward_ominous", textureMap8, this.modelCollector));
        this.registerParentedItemModel(block, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block).with(BlockStateVariantMap.models((Property)VaultBlock.VAULT_STATE, (Property)VaultBlock.OMINOUS).generate((state, ominous) -> switch (1.field_48979[state.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> {
                if (ominous.booleanValue()) {
                    yield weightedVariant5;
                }
                yield weightedVariant;
            }
            case 2 -> {
                if (ominous.booleanValue()) {
                    yield weightedVariant6;
                }
                yield weightedVariant2;
            }
            case 3 -> {
                if (ominous.booleanValue()) {
                    yield weightedVariant7;
                }
                yield weightedVariant3;
            }
            case 4 -> ominous != false ? weightedVariant8 : weightedVariant4;
        })).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerSculkSensor() {
        Identifier identifier = ModelIds.getBlockSubModelId((Block)Blocks.SCULK_SENSOR, (String)"_inactive");
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.SCULK_SENSOR, (String)"_active"));
        this.registerParentedItemModel(Blocks.SCULK_SENSOR, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.SCULK_SENSOR).with(BlockStateVariantMap.models((Property)Properties.SCULK_SENSOR_PHASE).generate(phase -> phase == SculkSensorPhase.ACTIVE || phase == SculkSensorPhase.COOLDOWN ? weightedVariant2 : weightedVariant)));
    }

    private void registerCalibratedSculkSensor() {
        Identifier identifier = ModelIds.getBlockSubModelId((Block)Blocks.CALIBRATED_SCULK_SENSOR, (String)"_inactive");
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.CALIBRATED_SCULK_SENSOR, (String)"_active"));
        this.registerParentedItemModel(Blocks.CALIBRATED_SCULK_SENSOR, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.CALIBRATED_SCULK_SENSOR).with(BlockStateVariantMap.models((Property)Properties.SCULK_SENSOR_PHASE).generate(phase -> phase == SculkSensorPhase.ACTIVE || phase == SculkSensorPhase.COOLDOWN ? weightedVariant2 : weightedVariant)).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerSculkShrieker() {
        Identifier identifier = Models.TEMPLATE_SCULK_SHRIEKER.upload(Blocks.SCULK_SHRIEKER, TextureMap.sculkShrieker((boolean)false), this.modelCollector);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_SCULK_SHRIEKER.upload(Blocks.SCULK_SHRIEKER, "_can_summon", TextureMap.sculkShrieker((boolean)true), this.modelCollector));
        this.registerParentedItemModel(Blocks.SCULK_SHRIEKER, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.SCULK_SHRIEKER).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.CAN_SUMMON, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)));
    }

    private void registerScaffolding() {
        Identifier identifier = ModelIds.getBlockSubModelId((Block)Blocks.SCAFFOLDING, (String)"_stable");
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)identifier);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.SCAFFOLDING, (String)"_unstable"));
        this.registerParentedItemModel(Blocks.SCAFFOLDING, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.SCAFFOLDING).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.BOTTOM, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)));
    }

    private void registerCaveVines() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.CAVE_VINES, "", Models.CROSS, TextureMap::cross));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.CAVE_VINES, "_lit", Models.CROSS, TextureMap::cross));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.CAVE_VINES).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.BERRIES, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.CAVE_VINES_PLANT, "", Models.CROSS, TextureMap::cross));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.CAVE_VINES_PLANT, "_lit", Models.CROSS, TextureMap::cross));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.CAVE_VINES_PLANT).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.BERRIES, (WeightedVariant)weightedVariant4, (WeightedVariant)weightedVariant3)));
    }

    private void registerRedstoneLamp() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.CUBE_ALL.upload(Blocks.REDSTONE_LAMP, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.REDSTONE_LAMP, "_on", Models.CUBE_ALL, TextureMap::all));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.REDSTONE_LAMP).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.LIT, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant)));
    }

    public final void registerTorch(Block torch, Block wallTorch) {
        TextureMap textureMap = TextureMap.torch((Block)torch);
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)torch, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TORCH.upload(torch, textureMap, this.modelCollector))));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)wallTorch, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TORCH_WALL.upload(wallTorch, textureMap, this.modelCollector))).apply(EAST_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
        this.registerItemModel(torch);
    }

    private void registerRedstoneTorch() {
        TextureMap textureMap = TextureMap.torch((Block)Blocks.REDSTONE_TORCH);
        TextureMap textureMap2 = TextureMap.torch((Identifier)TextureMap.getSubId((Block)Blocks.REDSTONE_TORCH, (String)"_off"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_REDSTONE_TORCH.upload(Blocks.REDSTONE_TORCH, textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TORCH_UNLIT.upload(Blocks.REDSTONE_TORCH, "_off", textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.REDSTONE_TORCH).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.LIT, (WeightedVariant)weightedVariant, (WeightedVariant)weightedVariant2)));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_REDSTONE_TORCH_WALL.upload(Blocks.REDSTONE_WALL_TORCH, textureMap, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TORCH_WALL_UNLIT.upload(Blocks.REDSTONE_WALL_TORCH, "_off", textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.REDSTONE_WALL_TORCH).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.LIT, (WeightedVariant)weightedVariant3, (WeightedVariant)weightedVariant4)).apply(EAST_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
        this.registerItemModel(Blocks.REDSTONE_TORCH);
    }

    private void registerRepeater() {
        this.registerItemModel(Items.REPEATER);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.REPEATER).with(BlockStateVariantMap.models((Property)Properties.DELAY, (Property)Properties.LOCKED, (Property)Properties.POWERED).generate((tick, locked, on) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('_').append(tick).append("tick");
            if (on.booleanValue()) {
                stringBuilder.append("_on");
            }
            if (locked.booleanValue()) {
                stringBuilder.append("_locked");
            }
            return BlockStateModelGenerator.createWeightedVariant((Identifier)TextureMap.getSubId((Block)Blocks.REPEATER, (String)stringBuilder.toString()));
        })).apply(SOUTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerSeaPickle() {
        this.registerItemModel(Items.SEA_PICKLE);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.SEA_PICKLE).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.PICKLES, (Property)Properties.WATERLOGGED).register((Comparable)Integer.valueOf(1), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"dead_sea_pickle")))).register((Comparable)Integer.valueOf(2), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"two_dead_sea_pickles")))).register((Comparable)Integer.valueOf(3), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"three_dead_sea_pickles")))).register((Comparable)Integer.valueOf(4), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"four_dead_sea_pickles")))).register((Comparable)Integer.valueOf(1), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"sea_pickle")))).register((Comparable)Integer.valueOf(2), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"two_sea_pickles")))).register((Comparable)Integer.valueOf(3), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"three_sea_pickles")))).register((Comparable)Integer.valueOf(4), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.modelWithYRotation((ModelVariant)BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"four_sea_pickles"))))));
    }

    private void registerSnows() {
        TextureMap textureMap = TextureMap.all((Block)Blocks.SNOW);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_ALL.upload(Blocks.SNOW_BLOCK, textureMap, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.SNOW).with(BlockStateVariantMap.models((Property)Properties.LAYERS).generate(layers -> layers < 8 ? BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.SNOW, (String)("_height" + layers * 2))) : weightedVariant)));
        this.registerParentedItemModel(Blocks.SNOW, ModelIds.getBlockSubModelId((Block)Blocks.SNOW, (String)"_height2"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)Blocks.SNOW_BLOCK, (WeightedVariant)weightedVariant));
    }

    private void registerStonecutter() {
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.STONECUTTER, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)Blocks.STONECUTTER))).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerStructureBlock() {
        Identifier identifier = TexturedModel.CUBE_ALL.upload(Blocks.STRUCTURE_BLOCK, this.modelCollector);
        this.registerParentedItemModel(Blocks.STRUCTURE_BLOCK, identifier);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.STRUCTURE_BLOCK).with(BlockStateVariantMap.models((Property)Properties.STRUCTURE_BLOCK_MODE).generate(mode -> BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.STRUCTURE_BLOCK, "_" + mode.asString(), Models.CUBE_ALL, TextureMap::all)))));
    }

    private void registerTestBlock() {
        HashMap<TestBlockMode, Identifier> map = new HashMap<TestBlockMode, Identifier>();
        for (TestBlockMode testBlockMode : TestBlockMode.values()) {
            map.put(testBlockMode, this.createSubModel(Blocks.TEST_BLOCK, "_" + testBlockMode.asString(), Models.CUBE_ALL, TextureMap::all));
        }
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.TEST_BLOCK).with(BlockStateVariantMap.models((Property)Properties.TEST_BLOCK_MODE).generate(mode -> BlockStateModelGenerator.createWeightedVariant((Identifier)((Identifier)map.get(mode))))));
        this.itemModelOutput.accept(Items.TEST_BLOCK, ItemModels.select((Property)TestBlock.MODE, (ItemModel.Unbaked)ItemModels.basic((Identifier)((Identifier)map.get(TestBlockMode.START))), Map.of(TestBlockMode.FAIL, ItemModels.basic((Identifier)((Identifier)map.get(TestBlockMode.FAIL))), TestBlockMode.LOG, ItemModels.basic((Identifier)((Identifier)map.get(TestBlockMode.LOG))), TestBlockMode.ACCEPT, ItemModels.basic((Identifier)((Identifier)map.get(TestBlockMode.ACCEPT))))));
    }

    private void registerSweetBerryBush() {
        this.registerItemModel(Items.SWEET_BERRIES);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.SWEET_BERRY_BUSH).with(BlockStateVariantMap.models((Property)Properties.AGE_3).generate(stage -> BlockStateModelGenerator.createWeightedVariant((Identifier)this.createSubModel(Blocks.SWEET_BERRY_BUSH, "_stage" + stage, Models.CROSS, TextureMap::cross)))));
    }

    private void registerTripwire() {
        this.registerItemModel(Items.STRING);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.TRIPWIRE).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.ATTACHED, (Property)Properties.EAST, (Property)Properties.NORTH, (Property)Properties.SOUTH, (Property)Properties.WEST).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_ns"))).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_n")).apply(ROTATE_Y_90)).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_n"))).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_n")).apply(ROTATE_Y_180)).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_n")).apply(ROTATE_Y_270)).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_ne"))).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_ne")).apply(ROTATE_Y_90)).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_ne")).apply(ROTATE_Y_180)).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_ne")).apply(ROTATE_Y_270)).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_ns"))).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_ns")).apply(ROTATE_Y_90)).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_nse"))).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_nse")).apply(ROTATE_Y_90)).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_nse")).apply(ROTATE_Y_180)).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_nse")).apply(ROTATE_Y_270)).register((Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_nsew"))).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_ns"))).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_n"))).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_n")).apply(ROTATE_Y_180)).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_n")).apply(ROTATE_Y_90)).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_n")).apply(ROTATE_Y_270)).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_ne"))).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_ne")).apply(ROTATE_Y_90)).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_ne")).apply(ROTATE_Y_180)).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_ne")).apply(ROTATE_Y_270)).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_ns"))).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_ns")).apply(ROTATE_Y_90)).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_nse"))).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_nse")).apply(ROTATE_Y_90)).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_nse")).apply(ROTATE_Y_180)).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(false), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_nse")).apply(ROTATE_Y_270)).register((Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Comparable)Boolean.valueOf(true), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE, (String)"_attached_nsew")))));
    }

    private void registerTripwireHook() {
        this.registerItemModel(Blocks.TRIPWIRE_HOOK);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.TRIPWIRE_HOOK).with(BlockStateVariantMap.models((Property)Properties.ATTACHED, (Property)Properties.POWERED).generate((attached, on) -> BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.TRIPWIRE_HOOK, (String)((attached != false ? "_attached" : "") + (on != false ? "_on" : "")))))).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    public final ModelVariant getTurtleEggModel(int eggs, String prefix, TextureMap textures) {
        return switch (eggs) {
            case 1 -> BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_TURTLE_EGG.upload(ModelIds.getMinecraftNamespacedBlock((String)(prefix + "turtle_egg")), textures, this.modelCollector));
            case 2 -> BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_TWO_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock((String)("two_" + prefix + "turtle_eggs")), textures, this.modelCollector));
            case 3 -> BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_THREE_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock((String)("three_" + prefix + "turtle_eggs")), textures, this.modelCollector));
            case 4 -> BlockStateModelGenerator.createModelVariant((Identifier)Models.TEMPLATE_FOUR_TURTLE_EGGS.upload(ModelIds.getMinecraftNamespacedBlock((String)("four_" + prefix + "turtle_eggs")), textures, this.modelCollector));
            default -> throw new UnsupportedOperationException();
        };
    }

    public final ModelVariant getTurtleEggModel(int eggs, int cracks) {
        return switch (cracks) {
            case 0 -> this.getTurtleEggModel(eggs, "", TextureMap.all((Identifier)TextureMap.getId((Block)Blocks.TURTLE_EGG)));
            case 1 -> this.getTurtleEggModel(eggs, "slightly_cracked_", TextureMap.all((Identifier)TextureMap.getSubId((Block)Blocks.TURTLE_EGG, (String)"_slightly_cracked")));
            case 2 -> this.getTurtleEggModel(eggs, "very_cracked_", TextureMap.all((Identifier)TextureMap.getSubId((Block)Blocks.TURTLE_EGG, (String)"_very_cracked")));
            default -> throw new UnsupportedOperationException();
        };
    }

    private void registerTurtleEgg() {
        this.registerItemModel(Items.TURTLE_EGG);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.TURTLE_EGG).with(BlockStateVariantMap.models((Property)Properties.EGGS, (Property)Properties.HATCH).generate((eggs, hatch) -> BlockStateModelGenerator.modelWithYRotation((ModelVariant)this.getTurtleEggModel(eggs.intValue(), hatch.intValue())))));
    }

    private void registerDriedGhast() {
        Identifier identifier = ModelIds.getBlockSubModelId((Block)Blocks.DRIED_GHAST, (String)"_hydration_0");
        this.registerParentedItemModel(Blocks.DRIED_GHAST, identifier);
        Function<Integer, Identifier> function = hydration -> {
            String string = switch (hydration) {
                case 1 -> "_hydration_1";
                case 2 -> "_hydration_2";
                case 3 -> "_hydration_3";
                default -> "_hydration_0";
            };
            TextureMap textureMap = TextureMap.driedGhast((String)string);
            return Models.DRIED_GHAST.upload(Blocks.DRIED_GHAST, string, textureMap, this.modelCollector);
        };
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.DRIED_GHAST).with(BlockStateVariantMap.models((Property)DriedGhastBlock.HYDRATION).generate(hydration -> BlockStateModelGenerator.createWeightedVariant((Identifier)((Identifier)function.apply((Integer)hydration))))).apply(NORTH_DEFAULT_HORIZONTAL_ROTATION_OPERATIONS));
    }

    private void registerSnifferEgg() {
        this.registerItemModel(Items.SNIFFER_EGG);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.SNIFFER_EGG).with(BlockStateVariantMap.models((Property)SnifferEggBlock.HATCH).generate(hatch -> {
            String string = switch (hatch) {
                case 1 -> "_slightly_cracked";
                case 2 -> "_very_cracked";
                default -> "_not_cracked";
            };
            TextureMap textureMap = TextureMap.snifferEgg((String)string);
            return BlockStateModelGenerator.createWeightedVariant((Identifier)Models.SNIFFER_EGG.upload(Blocks.SNIFFER_EGG, string, textureMap, this.modelCollector));
        })));
    }

    public final void registerMultifaceBlock(Block block) {
        this.registerItemModel(block);
        this.registerMultifaceBlockModel(block);
    }

    public final void registerMultifaceBlock(Block block, Item item) {
        this.registerItemModel(item);
        this.registerMultifaceBlockModel(block);
    }

    public static <T extends Property<?>> Map<T, ModelVariantOperator> collectMultifaceOperators(State<?, ?> state, Function<Direction, T> propertyGetter) {
        ImmutableMap.Builder builder = ImmutableMap.builderWithExpectedSize((int)CONNECTION_VARIANT_FUNCTIONS.size());
        CONNECTION_VARIANT_FUNCTIONS.forEach((direction, operator) -> {
            Property property = (Property)propertyGetter.apply((Direction)direction);
            if (state.contains(property)) {
                builder.put((Object)property, operator);
            }
        });
        return builder.build();
    }

    public final void registerMultifaceBlockModel(Block block) {
        Map map = BlockStateModelGenerator.collectMultifaceOperators((State)block.getDefaultState(), MultifaceBlock::getProperty);
        MultipartModelConditionBuilder multipartModelConditionBuilder = BlockStateModelGenerator.createMultipartConditionBuilder();
        map.forEach((property, operator) -> multipartModelConditionBuilder.put(property, (Comparable)Boolean.valueOf(false)));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)block));
        MultipartBlockModelDefinitionCreator multipartBlockModelDefinitionCreator = MultipartBlockModelDefinitionCreator.create((Block)block);
        map.forEach((property, operator) -> {
            multipartBlockModelDefinitionCreator.with(BlockStateModelGenerator.createMultipartConditionBuilder().put(property, (Comparable)Boolean.valueOf(true)), weightedVariant.apply(operator));
            multipartBlockModelDefinitionCreator.with(multipartModelConditionBuilder, weightedVariant.apply(operator));
        });
        this.blockStateCollector.accept(multipartBlockModelDefinitionCreator);
    }

    public final void registerPaleMossCarpet(Block block) {
        Map map = BlockStateModelGenerator.collectMultifaceOperators((State)block.getDefaultState(), PaleMossCarpetBlock::getWallShape);
        MultipartModelConditionBuilder multipartModelConditionBuilder = BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)PaleMossCarpetBlock.BOTTOM, (Comparable)Boolean.valueOf(false));
        map.forEach((property, operator) -> multipartModelConditionBuilder.put(property, (Comparable)WallShape.NONE));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.CARPET.upload(block, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.MOSSY_CARPET_SIDE.get(block).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side_tall"))).upload(block, "_side_tall", this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.MOSSY_CARPET_SIDE.get(block).textures(textureMap -> textureMap.put(TextureKey.SIDE, TextureMap.getSubId((Block)block, (String)"_side_small"))).upload(block, "_side_small", this.modelCollector));
        MultipartBlockModelDefinitionCreator multipartBlockModelDefinitionCreator = MultipartBlockModelDefinitionCreator.create((Block)block);
        multipartBlockModelDefinitionCreator.with(BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)PaleMossCarpetBlock.BOTTOM, (Comparable)Boolean.valueOf(true)), weightedVariant);
        multipartBlockModelDefinitionCreator.with(multipartModelConditionBuilder, weightedVariant);
        map.forEach((property, operator) -> {
            multipartBlockModelDefinitionCreator.with(BlockStateModelGenerator.createMultipartConditionBuilder().put(property, (Comparable)WallShape.TALL), weightedVariant2.apply(operator));
            multipartBlockModelDefinitionCreator.with(BlockStateModelGenerator.createMultipartConditionBuilder().put(property, (Comparable)WallShape.LOW), weightedVariant3.apply(operator));
            multipartBlockModelDefinitionCreator.with(multipartModelConditionBuilder, weightedVariant2.apply(operator));
        });
        this.blockStateCollector.accept(multipartBlockModelDefinitionCreator);
    }

    public final void registerHangingMoss(Block block) {
        this.registerItemModel(block);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)block).with(BlockStateVariantMap.models((Property)HangingMossBlock.TIP).generate(tip -> {
            String string = tip != false ? "_tip" : "";
            TextureMap textureMap = TextureMap.cross((Identifier)TextureMap.getSubId((Block)block, (String)string));
            return BlockStateModelGenerator.createWeightedVariant((Identifier)CrossType.NOT_TINTED.getCrossModel().upload(block, string, textureMap, this.modelCollector));
        })));
    }

    private void registerSculkCatalyst() {
        Identifier identifier = TextureMap.getSubId((Block)Blocks.SCULK_CATALYST, (String)"_bottom");
        TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, identifier).put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.SCULK_CATALYST, (String)"_top")).put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.SCULK_CATALYST, (String)"_side"));
        TextureMap textureMap2 = new TextureMap().put(TextureKey.BOTTOM, identifier).put(TextureKey.TOP, TextureMap.getSubId((Block)Blocks.SCULK_CATALYST, (String)"_top_bloom")).put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.SCULK_CATALYST, (String)"_side_bloom"));
        Identifier identifier2 = Models.CUBE_BOTTOM_TOP.upload(Blocks.SCULK_CATALYST, textureMap, this.modelCollector);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)identifier2);
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_BOTTOM_TOP.upload(Blocks.SCULK_CATALYST, "_bloom", textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.SCULK_CATALYST).with(BlockStateVariantMap.models((Property)Properties.BLOOM).generate(bloom -> bloom != false ? weightedVariant2 : weightedVariant)));
        this.registerParentedItemModel(Blocks.SCULK_CATALYST, identifier2);
    }

    public final void registerShelf(Block block, Block block2) {
        TextureMap textureMap = new TextureMap().put(TextureKey.ALL, TextureMap.getId((Block)block)).put(TextureKey.PARTICLE, TextureMap.getId((Block)block2));
        MultipartBlockModelDefinitionCreator multipartBlockModelDefinitionCreator = MultipartBlockModelDefinitionCreator.create((Block)block);
        this.registerShelf(block, textureMap, multipartBlockModelDefinitionCreator, Models.TEMPLATE_SHELF_BODY, null, null);
        this.registerShelf(block, textureMap, multipartBlockModelDefinitionCreator, Models.TEMPLATE_SHELF_UNPOWERED, Boolean.valueOf(false), null);
        this.registerShelf(block, textureMap, multipartBlockModelDefinitionCreator, Models.TEMPLATE_SHELF_UNCONNECTED, Boolean.valueOf(true), SideChainPart.UNCONNECTED);
        this.registerShelf(block, textureMap, multipartBlockModelDefinitionCreator, Models.TEMPLATE_SHELF_LEFT, Boolean.valueOf(true), SideChainPart.LEFT);
        this.registerShelf(block, textureMap, multipartBlockModelDefinitionCreator, Models.TEMPLATE_SHELF_CENTER, Boolean.valueOf(true), SideChainPart.CENTER);
        this.registerShelf(block, textureMap, multipartBlockModelDefinitionCreator, Models.TEMPLATE_SHELF_RIGHT, Boolean.valueOf(true), SideChainPart.RIGHT);
        this.blockStateCollector.accept(multipartBlockModelDefinitionCreator);
        this.registerParentedItemModel(block, Models.TEMPLATE_SHELF_INVENTORY.upload(block, textureMap, this.modelCollector));
    }

    public final void registerShelf(Block block, TextureMap textureMap, MultipartBlockModelDefinitionCreator definitionCreator, Model model, @Nullable Boolean powered, @Nullable SideChainPart sideChain) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)model.upload(block, textureMap, this.modelCollector));
        BlockStateModelGenerator.forEachHorizontalDirection((T facing, U operator) -> definitionCreator.with(BlockStateModelGenerator.createSideChainModelCondition((Direction)facing, (Boolean)powered, (SideChainPart)sideChain), weightedVariant.apply(operator)));
    }

    public static void forEachHorizontalDirection(BiConsumer<Direction, ModelVariantOperator> biConsumer) {
        List.of(Pair.of((Object)Direction.NORTH, (Object)NO_OP), Pair.of((Object)Direction.EAST, (Object)ROTATE_Y_90), Pair.of((Object)Direction.SOUTH, (Object)ROTATE_Y_180), Pair.of((Object)Direction.WEST, (Object)ROTATE_Y_270)).forEach(pair -> {
            Direction direction = (Direction)pair.getFirst();
            ModelVariantOperator modelVariantOperator = (ModelVariantOperator)pair.getSecond();
            biConsumer.accept(direction, modelVariantOperator);
        });
    }

    public static MultipartModelCondition createSideChainModelCondition(Direction facing, @Nullable Boolean powered, @Nullable SideChainPart sideChain) {
        MultipartModelConditionBuilder multipartModelConditionBuilder = BlockStateModelGenerator.createMultipartConditionBuilderWith((EnumProperty)Properties.HORIZONTAL_FACING, (Enum)facing, (Enum[])new Direction[0]);
        if (powered == null) {
            return multipartModelConditionBuilder.build();
        }
        MultipartModelConditionBuilder multipartModelConditionBuilder2 = BlockStateModelGenerator.createMultipartConditionBuilderWith((BooleanProperty)Properties.POWERED, (boolean)powered);
        return sideChain != null ? BlockStateModelGenerator.and((MultipartModelConditionBuilder[])new MultipartModelConditionBuilder[]{multipartModelConditionBuilder, multipartModelConditionBuilder2, BlockStateModelGenerator.createMultipartConditionBuilderWith((EnumProperty)Properties.SIDE_CHAIN, (Enum)sideChain, (Enum[])new SideChainPart[0])}) : BlockStateModelGenerator.and((MultipartModelConditionBuilder[])new MultipartModelConditionBuilder[]{multipartModelConditionBuilder, multipartModelConditionBuilder2});
    }

    private void registerChiseledBookshelf() {
        Block block = Blocks.CHISELED_BOOKSHELF;
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)block));
        MultipartBlockModelDefinitionCreator multipartBlockModelDefinitionCreator = MultipartBlockModelDefinitionCreator.create((Block)block);
        BlockStateModelGenerator.forEachHorizontalDirection((T facing, U operator) -> {
            MultipartModelCondition multipartModelCondition = BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)Properties.HORIZONTAL_FACING, (Comparable)facing).build();
            multipartBlockModelDefinitionCreator.with(multipartModelCondition, weightedVariant.apply(operator).apply(UV_LOCK));
            this.supplyChiseledBookshelfModels(multipartBlockModelDefinitionCreator, multipartModelCondition, operator);
        });
        this.blockStateCollector.accept(multipartBlockModelDefinitionCreator);
        this.registerParentedItemModel(block, ModelIds.getBlockSubModelId((Block)block, (String)"_inventory"));
        CHISELED_BOOKSHELF_MODEL_CACHE.clear();
    }

    public final void supplyChiseledBookshelfModels(MultipartBlockModelDefinitionCreator blockStateSupplier, MultipartModelCondition facingCondition, ModelVariantOperator rotation) {
        List.of(Pair.of((Object)ChiseledBookshelfBlock.SLOT_0_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_LEFT), Pair.of((Object)ChiseledBookshelfBlock.SLOT_1_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_MID), Pair.of((Object)ChiseledBookshelfBlock.SLOT_2_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_RIGHT), Pair.of((Object)ChiseledBookshelfBlock.SLOT_3_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT), Pair.of((Object)ChiseledBookshelfBlock.SLOT_4_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_MID), Pair.of((Object)ChiseledBookshelfBlock.SLOT_5_OCCUPIED, (Object)Models.TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT)).forEach(pair -> {
            BooleanProperty booleanProperty = (BooleanProperty)pair.getFirst();
            Model model = (Model)pair.getSecond();
            this.supplyChiseledBookshelfModel(blockStateSupplier, facingCondition, rotation, booleanProperty, model, true);
            this.supplyChiseledBookshelfModel(blockStateSupplier, facingCondition, rotation, booleanProperty, model, false);
        });
    }

    public final void supplyChiseledBookshelfModel(MultipartBlockModelDefinitionCreator blockStateSupplier, MultipartModelCondition facingCondition, ModelVariantOperator rotation, BooleanProperty property, Model model, boolean occupied) {
        String string = occupied ? "_occupied" : "_empty";
        TextureMap textureMap = new TextureMap().put(TextureKey.TEXTURE, TextureMap.getSubId((Block)Blocks.CHISELED_BOOKSHELF, (String)string));
        ChiseledBookshelfModelCacheKey chiseledBookshelfModelCacheKey = new ChiseledBookshelfModelCacheKey(model, string);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)CHISELED_BOOKSHELF_MODEL_CACHE.computeIfAbsent(chiseledBookshelfModelCacheKey, key -> model.upload(Blocks.CHISELED_BOOKSHELF, string, textureMap, this.modelCollector)));
        blockStateSupplier.with((MultipartModelCondition)new MultipartModelCombinedCondition(MultipartModelCombinedCondition.LogicalOperator.AND, List.of(facingCondition, BlockStateModelGenerator.createMultipartConditionBuilder().put((Property)property, (Comparable)Boolean.valueOf(occupied)).build())), weightedVariant.apply(rotation));
    }

    private void registerMagmaBlock() {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_ALL.upload(Blocks.MAGMA_BLOCK, TextureMap.all((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"magma")), this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)Blocks.MAGMA_BLOCK, (WeightedVariant)weightedVariant));
    }

    public final void registerShulkerBox(Block shulkerBox, @Nullable DyeColor color) {
        this.registerBuiltin(shulkerBox);
        Item item = shulkerBox.asItem();
        Identifier identifier = Models.TEMPLATE_SHULKER_BOX.upload(item, TextureMap.particle((Block)shulkerBox), this.modelCollector);
        ItemModel.Unbaked unbaked = color != null ? ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)new ShulkerBoxModelRenderer.Unbaked(color)) : ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)new ShulkerBoxModelRenderer.Unbaked());
        this.itemModelOutput.accept(item, unbaked);
    }

    public final void registerPlantPart(Block plant, Block plantStem, CrossType tintType) {
        this.registerTintableCrossBlockState(plant, tintType);
        this.registerTintableCrossBlockState(plantStem, tintType);
    }

    private void registerInfestedStone() {
        Identifier identifier = ModelIds.getBlockModelId((Block)Blocks.STONE);
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)identifier);
        ModelVariant modelVariant2 = BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.STONE, (String)"_mirrored"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.INFESTED_STONE, (WeightedVariant)BlockStateModelGenerator.modelWithMirroring((ModelVariant)modelVariant, (ModelVariant)modelVariant2)));
        this.registerParentedItemModel(Blocks.INFESTED_STONE, identifier);
    }

    private void registerInfestedDeepslate() {
        Identifier identifier = ModelIds.getBlockModelId((Block)Blocks.DEEPSLATE);
        ModelVariant modelVariant = BlockStateModelGenerator.createModelVariant((Identifier)identifier);
        ModelVariant modelVariant2 = BlockStateModelGenerator.createModelVariant((Identifier)ModelIds.getBlockSubModelId((Block)Blocks.DEEPSLATE, (String)"_mirrored"));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.INFESTED_DEEPSLATE, (WeightedVariant)BlockStateModelGenerator.modelWithMirroring((ModelVariant)modelVariant, (ModelVariant)modelVariant2)).apply(BlockStateModelGenerator.createAxisRotatedVariantMap()));
        this.registerParentedItemModel(Blocks.INFESTED_DEEPSLATE, identifier);
    }

    public final void registerRoots(Block root, Block pottedRoot) {
        this.registerTintableCross(root, CrossType.NOT_TINTED);
        TextureMap textureMap = TextureMap.plant((Identifier)TextureMap.getSubId((Block)root, (String)"_pot"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)CrossType.NOT_TINTED.getFlowerPotCrossModel().upload(pottedRoot, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)pottedRoot, (WeightedVariant)weightedVariant));
    }

    private void registerRespawnAnchor() {
        Identifier identifier = TextureMap.getSubId((Block)Blocks.RESPAWN_ANCHOR, (String)"_bottom");
        Identifier identifier2 = TextureMap.getSubId((Block)Blocks.RESPAWN_ANCHOR, (String)"_top_off");
        Identifier identifier3 = TextureMap.getSubId((Block)Blocks.RESPAWN_ANCHOR, (String)"_top");
        Identifier[] identifiers = new Identifier[5];
        for (int i = 0; i < 5; ++i) {
            TextureMap textureMap = new TextureMap().put(TextureKey.BOTTOM, identifier).put(TextureKey.TOP, i == 0 ? identifier2 : identifier3).put(TextureKey.SIDE, TextureMap.getSubId((Block)Blocks.RESPAWN_ANCHOR, (String)("_side" + i)));
            identifiers[i] = Models.CUBE_BOTTOM_TOP.upload(Blocks.RESPAWN_ANCHOR, "_" + i, textureMap, this.modelCollector);
        }
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.RESPAWN_ANCHOR).with(BlockStateVariantMap.models((Property)Properties.CHARGES).generate(charges -> BlockStateModelGenerator.createWeightedVariant((Identifier)identifiers[charges]))));
        this.registerParentedItemModel(Blocks.RESPAWN_ANCHOR, identifiers[0]);
    }

    public static ModelVariantOperator addJigsawOrientationToVariant(Orientation orientation) {
        return switch (1.field_23399[orientation.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> ROTATE_X_90;
            case 2 -> ROTATE_X_90.then(ROTATE_Y_180);
            case 3 -> ROTATE_X_90.then(ROTATE_Y_270);
            case 4 -> ROTATE_X_90.then(ROTATE_Y_90);
            case 5 -> ROTATE_X_270.then(ROTATE_Y_180);
            case 6 -> ROTATE_X_270;
            case 7 -> ROTATE_X_270.then(ROTATE_Y_90);
            case 8 -> ROTATE_X_270.then(ROTATE_Y_270);
            case 9 -> NO_OP;
            case 10 -> ROTATE_Y_180;
            case 11 -> ROTATE_Y_270;
            case 12 -> ROTATE_Y_90;
        };
    }

    private void registerJigsaw() {
        Identifier identifier = TextureMap.getSubId((Block)Blocks.JIGSAW, (String)"_top");
        Identifier identifier2 = TextureMap.getSubId((Block)Blocks.JIGSAW, (String)"_bottom");
        Identifier identifier3 = TextureMap.getSubId((Block)Blocks.JIGSAW, (String)"_side");
        Identifier identifier4 = TextureMap.getSubId((Block)Blocks.JIGSAW, (String)"_lock");
        TextureMap textureMap = new TextureMap().put(TextureKey.DOWN, identifier3).put(TextureKey.WEST, identifier3).put(TextureKey.EAST, identifier3).put(TextureKey.PARTICLE, identifier).put(TextureKey.NORTH, identifier).put(TextureKey.SOUTH, identifier2).put(TextureKey.UP, identifier4);
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.JIGSAW, (WeightedVariant)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.CUBE_DIRECTIONAL.upload(Blocks.JIGSAW, textureMap, this.modelCollector))).apply(BlockStateVariantMap.operations((Property)Properties.ORIENTATION).generate(BlockStateModelGenerator::addJigsawOrientationToVariant)));
    }

    private void registerPetrifiedOakSlab() {
        Block block = Blocks.OAK_PLANKS;
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getBlockModelId((Block)block));
        TextureMap textureMap = TextureMap.all((Block)block);
        Block block2 = Blocks.PETRIFIED_OAK_SLAB;
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.SLAB.upload(block2, textureMap, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.SLAB_TOP.upload(block2, textureMap, this.modelCollector));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSlabBlockState((Block)block2, (WeightedVariant)weightedVariant2, (WeightedVariant)weightedVariant3, (WeightedVariant)weightedVariant));
    }

    public final void registerSkull(Block block, Block wallBlock, SkullBlock.SkullType type, Identifier baseModelId) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"skull"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)weightedVariant));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)wallBlock, (WeightedVariant)weightedVariant));
        if (type == SkullBlock.Type.PLAYER) {
            this.itemModelOutput.accept(block.asItem(), ItemModels.special((Identifier)baseModelId, (SpecialModelRenderer.Unbaked)new PlayerHeadModelRenderer.Unbaked()));
        } else {
            this.itemModelOutput.accept(block.asItem(), ItemModels.special((Identifier)baseModelId, (SpecialModelRenderer.Unbaked)new HeadModelRenderer.Unbaked(type)));
        }
    }

    private void registerSkulls() {
        Identifier identifier = ModelIds.getMinecraftNamespacedItem((String)"template_skull");
        this.registerSkull(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, (SkullBlock.SkullType)SkullBlock.Type.CREEPER, identifier);
        this.registerSkull(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, (SkullBlock.SkullType)SkullBlock.Type.PLAYER, identifier);
        this.registerSkull(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, (SkullBlock.SkullType)SkullBlock.Type.ZOMBIE, identifier);
        this.registerSkull(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, (SkullBlock.SkullType)SkullBlock.Type.SKELETON, identifier);
        this.registerSkull(Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, (SkullBlock.SkullType)SkullBlock.Type.WITHER_SKELETON, identifier);
        this.registerSkull(Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD, (SkullBlock.SkullType)SkullBlock.Type.PIGLIN, identifier);
        this.registerSkull(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, (SkullBlock.SkullType)SkullBlock.Type.DRAGON, ModelIds.getItemModelId((Item)Items.DRAGON_HEAD));
    }

    private void registerCopperGolemStatues() {
        this.registerCopperGolemStatue(Blocks.COPPER_GOLEM_STATUE, Blocks.COPPER_BLOCK, Oxidizable.OxidationLevel.UNAFFECTED);
        this.registerCopperGolemStatue(Blocks.EXPOSED_COPPER_GOLEM_STATUE, Blocks.EXPOSED_COPPER, Oxidizable.OxidationLevel.EXPOSED);
        this.registerCopperGolemStatue(Blocks.WEATHERED_COPPER_GOLEM_STATUE, Blocks.WEATHERED_COPPER, Oxidizable.OxidationLevel.WEATHERED);
        this.registerCopperGolemStatue(Blocks.OXIDIZED_COPPER_GOLEM_STATUE, Blocks.OXIDIZED_COPPER, Oxidizable.OxidationLevel.OXIDIZED);
        this.registerParented(Blocks.COPPER_GOLEM_STATUE, Blocks.WAXED_COPPER_GOLEM_STATUE);
        this.registerParented(Blocks.EXPOSED_COPPER_GOLEM_STATUE, Blocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE);
        this.registerParented(Blocks.WEATHERED_COPPER_GOLEM_STATUE, Blocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE);
        this.registerParented(Blocks.OXIDIZED_COPPER_GOLEM_STATUE, Blocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE);
    }

    public final void registerCopperGolemStatue(Block block, Block particleBlock, Oxidizable.OxidationLevel oxidationLevel) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.PARTICLE.upload(block, TextureMap.particle((Identifier)TextureMap.getId((Block)particleBlock)), this.modelCollector));
        Identifier identifier = ModelIds.getMinecraftNamespacedItem((String)"template_copper_golem_statue");
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)weightedVariant));
        this.itemModelOutput.accept(block.asItem(), ItemModels.select((Property)CopperGolemStatueBlock.POSE, (ItemModel.Unbaked)ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)new CopperGolemStatueModelRenderer.Unbaked(oxidationLevel, CopperGolemStatueBlock.Pose.STANDING)), Map.of(CopperGolemStatueBlock.Pose.SITTING, ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)new CopperGolemStatueModelRenderer.Unbaked(oxidationLevel, CopperGolemStatueBlock.Pose.SITTING)), CopperGolemStatueBlock.Pose.STAR, ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)new CopperGolemStatueModelRenderer.Unbaked(oxidationLevel, CopperGolemStatueBlock.Pose.STAR)), CopperGolemStatueBlock.Pose.RUNNING, ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)new CopperGolemStatueModelRenderer.Unbaked(oxidationLevel, CopperGolemStatueBlock.Pose.RUNNING)))));
    }

    public final void registerBanner(Block block, Block wallBlock, DyeColor color) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"banner"));
        Identifier identifier = ModelIds.getMinecraftNamespacedItem((String)"template_banner");
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)weightedVariant));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)wallBlock, (WeightedVariant)weightedVariant));
        Item item = block.asItem();
        this.itemModelOutput.accept(item, ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)new BannerModelRenderer.Unbaked(color)));
    }

    private void registerBanners() {
        this.registerBanner(Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER, DyeColor.WHITE);
        this.registerBanner(Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER, DyeColor.ORANGE);
        this.registerBanner(Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER, DyeColor.MAGENTA);
        this.registerBanner(Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, DyeColor.LIGHT_BLUE);
        this.registerBanner(Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER, DyeColor.YELLOW);
        this.registerBanner(Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER, DyeColor.LIME);
        this.registerBanner(Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER, DyeColor.PINK);
        this.registerBanner(Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER, DyeColor.GRAY);
        this.registerBanner(Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, DyeColor.LIGHT_GRAY);
        this.registerBanner(Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER, DyeColor.CYAN);
        this.registerBanner(Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER, DyeColor.PURPLE);
        this.registerBanner(Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER, DyeColor.BLUE);
        this.registerBanner(Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER, DyeColor.BROWN);
        this.registerBanner(Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER, DyeColor.GREEN);
        this.registerBanner(Blocks.RED_BANNER, Blocks.RED_WALL_BANNER, DyeColor.RED);
        this.registerBanner(Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER, DyeColor.BLACK);
    }

    public final void registerChest(Block block, Block particleSource, Identifier texture, boolean christmas) {
        this.registerBuiltinWithParticle(block, particleSource);
        Item item = block.asItem();
        Identifier identifier = Models.TEMPLATE_CHEST.upload(item, TextureMap.particle((Block)particleSource), this.modelCollector);
        ItemModel.Unbaked unbaked = ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)new ChestModelRenderer.Unbaked(texture));
        if (christmas) {
            ItemModel.Unbaked unbaked2 = ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)new ChestModelRenderer.Unbaked(ChestModelRenderer.CHRISTMAS_ID));
            this.itemModelOutput.accept(item, ItemModels.christmasSelect((ItemModel.Unbaked)unbaked2, (ItemModel.Unbaked)unbaked));
        } else {
            this.itemModelOutput.accept(item, unbaked);
        }
    }

    private void registerChests() {
        this.registerChest(Blocks.CHEST, Blocks.OAK_PLANKS, ChestModelRenderer.NORMAL_ID, true);
        this.registerChest(Blocks.TRAPPED_CHEST, Blocks.OAK_PLANKS, ChestModelRenderer.TRAPPED_ID, true);
        this.registerChest(Blocks.ENDER_CHEST, Blocks.OBSIDIAN, ChestModelRenderer.ENDER_ID, false);
    }

    private void registerCopperChests() {
        this.registerChest(Blocks.COPPER_CHEST, Blocks.COPPER_BLOCK, ChestModelRenderer.COPPER_ID, false);
        this.registerChest(Blocks.EXPOSED_COPPER_CHEST, Blocks.EXPOSED_COPPER, ChestModelRenderer.EXPOSED_COPPER_ID, false);
        this.registerChest(Blocks.WEATHERED_COPPER_CHEST, Blocks.WEATHERED_COPPER, ChestModelRenderer.WEATHERED_COPPER_ID, false);
        this.registerChest(Blocks.OXIDIZED_COPPER_CHEST, Blocks.OXIDIZED_COPPER, ChestModelRenderer.OXIDIZED_COPPER_ID, false);
        this.registerParented(Blocks.COPPER_CHEST, Blocks.WAXED_COPPER_CHEST);
        this.registerParented(Blocks.EXPOSED_COPPER_CHEST, Blocks.WAXED_EXPOSED_COPPER_CHEST);
        this.registerParented(Blocks.WEATHERED_COPPER_CHEST, Blocks.WAXED_WEATHERED_COPPER_CHEST);
        this.registerParented(Blocks.OXIDIZED_COPPER_CHEST, Blocks.WAXED_OXIDIZED_COPPER_CHEST);
    }

    public final void registerBed(Block block, Block particleSource, DyeColor color) {
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)ModelIds.getMinecraftNamespacedBlock((String)"bed"));
        this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState((Block)block, (WeightedVariant)weightedVariant));
        Item item = block.asItem();
        Identifier identifier = Models.TEMPLATE_BED.upload(ModelIds.getItemModelId((Item)item), TextureMap.particle((Block)particleSource), this.modelCollector);
        this.itemModelOutput.accept(item, ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)new BedModelRenderer.Unbaked(color)));
    }

    private void registerBeds() {
        this.registerBed(Blocks.WHITE_BED, Blocks.WHITE_WOOL, DyeColor.WHITE);
        this.registerBed(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL, DyeColor.ORANGE);
        this.registerBed(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL, DyeColor.MAGENTA);
        this.registerBed(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE);
        this.registerBed(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL, DyeColor.YELLOW);
        this.registerBed(Blocks.LIME_BED, Blocks.LIME_WOOL, DyeColor.LIME);
        this.registerBed(Blocks.PINK_BED, Blocks.PINK_WOOL, DyeColor.PINK);
        this.registerBed(Blocks.GRAY_BED, Blocks.GRAY_WOOL, DyeColor.GRAY);
        this.registerBed(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL, DyeColor.LIGHT_GRAY);
        this.registerBed(Blocks.CYAN_BED, Blocks.CYAN_WOOL, DyeColor.CYAN);
        this.registerBed(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL, DyeColor.PURPLE);
        this.registerBed(Blocks.BLUE_BED, Blocks.BLUE_WOOL, DyeColor.BLUE);
        this.registerBed(Blocks.BROWN_BED, Blocks.BROWN_WOOL, DyeColor.BROWN);
        this.registerBed(Blocks.GREEN_BED, Blocks.GREEN_WOOL, DyeColor.GREEN);
        this.registerBed(Blocks.RED_BED, Blocks.RED_WOOL, DyeColor.RED);
        this.registerBed(Blocks.BLACK_BED, Blocks.BLACK_WOOL, DyeColor.BLACK);
    }

    public final void registerSpecialItemModel(Block block, SpecialModelRenderer.Unbaked specialModel) {
        Item item = block.asItem();
        Identifier identifier = ModelIds.getItemModelId((Item)item);
        this.itemModelOutput.accept(item, ItemModels.special((Identifier)identifier, (SpecialModelRenderer.Unbaked)specialModel));
    }

    public void register() {
        BlockFamilies.getFamilies().filter(BlockFamily::shouldGenerateModels).forEach(family -> this.registerCubeAllModelTexturePool(family.getBaseBlock()).family(family));
        this.registerCubeAllModelTexturePool(Blocks.CUT_COPPER).family(BlockFamilies.CUT_COPPER).parented(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER).parented(Blocks.CHISELED_COPPER, Blocks.WAXED_CHISELED_COPPER).family(BlockFamilies.WAXED_CUT_COPPER);
        this.registerCubeAllModelTexturePool(Blocks.EXPOSED_CUT_COPPER).family(BlockFamilies.EXPOSED_CUT_COPPER).parented(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER).parented(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER).family(BlockFamilies.WAXED_EXPOSED_CUT_COPPER);
        this.registerCubeAllModelTexturePool(Blocks.WEATHERED_CUT_COPPER).family(BlockFamilies.WEATHERED_CUT_COPPER).parented(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER).parented(Blocks.WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER).family(BlockFamilies.WAXED_WEATHERED_CUT_COPPER);
        this.registerCubeAllModelTexturePool(Blocks.OXIDIZED_CUT_COPPER).family(BlockFamilies.OXIDIZED_CUT_COPPER).parented(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER).parented(Blocks.OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER).family(BlockFamilies.WAXED_OXIDIZED_CUT_COPPER);
        this.registerCopperBulb(Blocks.COPPER_BULB);
        this.registerCopperBulb(Blocks.EXPOSED_COPPER_BULB);
        this.registerCopperBulb(Blocks.WEATHERED_COPPER_BULB);
        this.registerCopperBulb(Blocks.OXIDIZED_COPPER_BULB);
        this.registerWaxedCopperBulb(Blocks.COPPER_BULB, Blocks.WAXED_COPPER_BULB);
        this.registerWaxedCopperBulb(Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB);
        this.registerWaxedCopperBulb(Blocks.WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB);
        this.registerWaxedCopperBulb(Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB);
        this.registerSimpleState(Blocks.AIR);
        this.registerStateWithModelReference(Blocks.CAVE_AIR, Blocks.AIR);
        this.registerStateWithModelReference(Blocks.VOID_AIR, Blocks.AIR);
        this.registerSimpleState(Blocks.BEACON);
        this.registerSimpleState(Blocks.CACTUS);
        this.registerStateWithModelReference(Blocks.BUBBLE_COLUMN, Blocks.WATER);
        this.registerSimpleState(Blocks.DRAGON_EGG);
        this.registerSimpleState(Blocks.DRIED_KELP_BLOCK);
        this.registerSimpleState(Blocks.ENCHANTING_TABLE);
        this.registerSimpleState(Blocks.FLOWER_POT);
        this.registerItemModel(Items.FLOWER_POT);
        this.registerSimpleState(Blocks.HONEY_BLOCK);
        this.registerSimpleState(Blocks.WATER);
        this.registerSimpleState(Blocks.LAVA);
        this.registerSimpleState(Blocks.SLIME_BLOCK);
        this.registerItemModel(Items.IRON_CHAIN);
        Items.COPPER_CHAINS.getWaxingMap().forEach((arg_0, arg_1) -> this.registerWaxable(arg_0, arg_1));
        this.registerCandle(Blocks.WHITE_CANDLE, Blocks.WHITE_CANDLE_CAKE);
        this.registerCandle(Blocks.ORANGE_CANDLE, Blocks.ORANGE_CANDLE_CAKE);
        this.registerCandle(Blocks.MAGENTA_CANDLE, Blocks.MAGENTA_CANDLE_CAKE);
        this.registerCandle(Blocks.LIGHT_BLUE_CANDLE, Blocks.LIGHT_BLUE_CANDLE_CAKE);
        this.registerCandle(Blocks.YELLOW_CANDLE, Blocks.YELLOW_CANDLE_CAKE);
        this.registerCandle(Blocks.LIME_CANDLE, Blocks.LIME_CANDLE_CAKE);
        this.registerCandle(Blocks.PINK_CANDLE, Blocks.PINK_CANDLE_CAKE);
        this.registerCandle(Blocks.GRAY_CANDLE, Blocks.GRAY_CANDLE_CAKE);
        this.registerCandle(Blocks.LIGHT_GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE_CAKE);
        this.registerCandle(Blocks.CYAN_CANDLE, Blocks.CYAN_CANDLE_CAKE);
        this.registerCandle(Blocks.PURPLE_CANDLE, Blocks.PURPLE_CANDLE_CAKE);
        this.registerCandle(Blocks.BLUE_CANDLE, Blocks.BLUE_CANDLE_CAKE);
        this.registerCandle(Blocks.BROWN_CANDLE, Blocks.BROWN_CANDLE_CAKE);
        this.registerCandle(Blocks.GREEN_CANDLE, Blocks.GREEN_CANDLE_CAKE);
        this.registerCandle(Blocks.RED_CANDLE, Blocks.RED_CANDLE_CAKE);
        this.registerCandle(Blocks.BLACK_CANDLE, Blocks.BLACK_CANDLE_CAKE);
        this.registerCandle(Blocks.CANDLE, Blocks.CANDLE_CAKE);
        this.registerSimpleState(Blocks.POTTED_BAMBOO);
        this.registerSimpleState(Blocks.POTTED_CACTUS);
        this.registerSimpleState(Blocks.POWDER_SNOW);
        this.registerSimpleState(Blocks.SPORE_BLOSSOM);
        this.registerAzalea(Blocks.AZALEA);
        this.registerAzalea(Blocks.FLOWERING_AZALEA);
        this.registerPottedAzaleaBush(Blocks.POTTED_AZALEA_BUSH);
        this.registerPottedAzaleaBush(Blocks.POTTED_FLOWERING_AZALEA_BUSH);
        this.registerCaveVines();
        this.registerWoolAndCarpet(Blocks.MOSS_BLOCK, Blocks.MOSS_CARPET);
        this.registerPaleMossCarpet(Blocks.PALE_MOSS_CARPET);
        this.registerHangingMoss(Blocks.PALE_HANGING_MOSS);
        this.registerSimpleCubeAll(Blocks.PALE_MOSS_BLOCK);
        this.registerFlowerbed(Blocks.PINK_PETALS);
        this.registerFlowerbed(Blocks.WILDFLOWERS);
        this.registerLeafLitter(Blocks.LEAF_LITTER);
        this.registerTintableCrossBlockState(Blocks.FIREFLY_BUSH, CrossType.EMISSIVE_NOT_TINTED);
        this.registerItemModel(Items.FIREFLY_BUSH);
        this.registerBuiltinWithParticle(Blocks.BARRIER, Items.BARRIER);
        this.registerItemModel(Items.BARRIER);
        this.registerLightBlock();
        this.registerBuiltinWithParticle(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
        this.registerItemModel(Items.STRUCTURE_VOID);
        this.registerBuiltinWithParticle(Blocks.MOVING_PISTON, TextureMap.getSubId((Block)Blocks.PISTON, (String)"_side"));
        this.registerSimpleCubeAll(Blocks.COAL_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_COAL_ORE);
        this.registerSimpleCubeAll(Blocks.COAL_BLOCK);
        this.registerSimpleCubeAll(Blocks.DIAMOND_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_DIAMOND_ORE);
        this.registerSimpleCubeAll(Blocks.DIAMOND_BLOCK);
        this.registerSimpleCubeAll(Blocks.EMERALD_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_EMERALD_ORE);
        this.registerSimpleCubeAll(Blocks.EMERALD_BLOCK);
        this.registerSimpleCubeAll(Blocks.GOLD_ORE);
        this.registerSimpleCubeAll(Blocks.NETHER_GOLD_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_GOLD_ORE);
        this.registerSimpleCubeAll(Blocks.GOLD_BLOCK);
        this.registerSimpleCubeAll(Blocks.IRON_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_IRON_ORE);
        this.registerSimpleCubeAll(Blocks.IRON_BLOCK);
        this.registerSingleton(Blocks.ANCIENT_DEBRIS, TexturedModel.CUBE_COLUMN);
        this.registerSimpleCubeAll(Blocks.NETHERITE_BLOCK);
        this.registerSimpleCubeAll(Blocks.LAPIS_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_LAPIS_ORE);
        this.registerSimpleCubeAll(Blocks.LAPIS_BLOCK);
        this.registerSimpleCubeAll(Blocks.RESIN_BLOCK);
        this.registerSimpleCubeAll(Blocks.NETHER_QUARTZ_ORE);
        this.registerSimpleCubeAll(Blocks.REDSTONE_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_REDSTONE_ORE);
        this.registerSimpleCubeAll(Blocks.REDSTONE_BLOCK);
        this.registerSimpleCubeAll(Blocks.GILDED_BLACKSTONE);
        this.registerSimpleCubeAll(Blocks.BLUE_ICE);
        this.registerSimpleCubeAll(Blocks.CLAY);
        this.registerSimpleCubeAll(Blocks.COARSE_DIRT);
        this.registerSimpleCubeAll(Blocks.CRYING_OBSIDIAN);
        this.registerSimpleCubeAll(Blocks.END_STONE);
        this.registerSimpleCubeAll(Blocks.GLOWSTONE);
        this.registerSimpleCubeAll(Blocks.GRAVEL);
        this.registerSimpleCubeAll(Blocks.HONEYCOMB_BLOCK);
        this.registerSimpleCubeAll(Blocks.ICE);
        this.registerSingleton(Blocks.JUKEBOX, TexturedModel.CUBE_TOP);
        this.registerSingleton(Blocks.LODESTONE, TexturedModel.CUBE_COLUMN);
        this.registerSingleton(Blocks.MELON, TexturedModel.CUBE_COLUMN);
        this.registerSimpleState(Blocks.MANGROVE_ROOTS);
        this.registerSimpleState(Blocks.POTTED_MANGROVE_PROPAGULE);
        this.registerSimpleCubeAll(Blocks.NETHER_WART_BLOCK);
        this.registerSimpleCubeAll(Blocks.NOTE_BLOCK);
        this.registerSimpleCubeAll(Blocks.PACKED_ICE);
        this.registerSimpleCubeAll(Blocks.OBSIDIAN);
        this.registerSimpleCubeAll(Blocks.QUARTZ_BRICKS);
        this.registerSimpleCubeAll(Blocks.SEA_LANTERN);
        this.registerSimpleCubeAll(Blocks.SHROOMLIGHT);
        this.registerSimpleCubeAll(Blocks.SOUL_SAND);
        this.registerSimpleCubeAll(Blocks.SOUL_SOIL);
        this.registerSingleton(Blocks.SPAWNER, TexturedModel.CUBE_ALL_INNER_FACES);
        this.registerCreakingHeart(Blocks.CREAKING_HEART);
        this.registerSimpleCubeAll(Blocks.SPONGE);
        this.registerSingleton(Blocks.SEAGRASS, TexturedModel.TEMPLATE_SEAGRASS);
        this.registerItemModel(Items.SEAGRASS);
        this.registerSingleton(Blocks.TNT, TexturedModel.CUBE_BOTTOM_TOP);
        this.registerSingleton(Blocks.TARGET, TexturedModel.CUBE_COLUMN);
        this.registerSimpleCubeAll(Blocks.WARPED_WART_BLOCK);
        this.registerSimpleCubeAll(Blocks.WET_SPONGE);
        this.registerSimpleCubeAll(Blocks.AMETHYST_BLOCK);
        this.registerSimpleCubeAll(Blocks.BUDDING_AMETHYST);
        this.registerSimpleCubeAll(Blocks.CALCITE);
        this.registerSimpleCubeAll(Blocks.DRIPSTONE_BLOCK);
        this.registerSimpleCubeAll(Blocks.RAW_IRON_BLOCK);
        this.registerSimpleCubeAll(Blocks.RAW_COPPER_BLOCK);
        this.registerSimpleCubeAll(Blocks.RAW_GOLD_BLOCK);
        this.registerMirrorable(Blocks.SCULK);
        this.registerSimpleState(Blocks.HEAVY_CORE);
        this.registerPetrifiedOakSlab();
        this.registerSimpleCubeAll(Blocks.COPPER_ORE);
        this.registerSimpleCubeAll(Blocks.DEEPSLATE_COPPER_ORE);
        this.registerSimpleCubeAll(Blocks.COPPER_BLOCK);
        this.registerSimpleCubeAll(Blocks.EXPOSED_COPPER);
        this.registerSimpleCubeAll(Blocks.WEATHERED_COPPER);
        this.registerSimpleCubeAll(Blocks.OXIDIZED_COPPER);
        this.registerParented(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK);
        this.registerParented(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER);
        this.registerParented(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER);
        this.registerParented(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER);
        this.registerDoor(Blocks.COPPER_DOOR);
        this.registerDoor(Blocks.EXPOSED_COPPER_DOOR);
        this.registerDoor(Blocks.WEATHERED_COPPER_DOOR);
        this.registerDoor(Blocks.OXIDIZED_COPPER_DOOR);
        this.registerParentedDoor(Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR);
        this.registerParentedDoor(Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR);
        this.registerParentedDoor(Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR);
        this.registerParentedDoor(Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR);
        this.registerTrapdoor(Blocks.COPPER_TRAPDOOR);
        this.registerTrapdoor(Blocks.EXPOSED_COPPER_TRAPDOOR);
        this.registerTrapdoor(Blocks.WEATHERED_COPPER_TRAPDOOR);
        this.registerTrapdoor(Blocks.OXIDIZED_COPPER_TRAPDOOR);
        this.registerParentedTrapdoor(Blocks.COPPER_TRAPDOOR, Blocks.WAXED_COPPER_TRAPDOOR);
        this.registerParentedTrapdoor(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
        this.registerParentedTrapdoor(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
        this.registerParentedTrapdoor(Blocks.OXIDIZED_COPPER_TRAPDOOR, Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
        this.registerSimpleCubeAll(Blocks.COPPER_GRATE);
        this.registerSimpleCubeAll(Blocks.EXPOSED_COPPER_GRATE);
        this.registerSimpleCubeAll(Blocks.WEATHERED_COPPER_GRATE);
        this.registerSimpleCubeAll(Blocks.OXIDIZED_COPPER_GRATE);
        this.registerParented(Blocks.COPPER_GRATE, Blocks.WAXED_COPPER_GRATE);
        this.registerParented(Blocks.EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE);
        this.registerParented(Blocks.WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE);
        this.registerParented(Blocks.OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE);
        this.registerLightningRod(Blocks.LIGHTNING_ROD, Blocks.WAXED_LIGHTNING_ROD);
        this.registerLightningRod(Blocks.EXPOSED_LIGHTNING_ROD, Blocks.WAXED_EXPOSED_LIGHTNING_ROD);
        this.registerLightningRod(Blocks.WEATHERED_LIGHTNING_ROD, Blocks.WAXED_WEATHERED_LIGHTNING_ROD);
        this.registerLightningRod(Blocks.OXIDIZED_LIGHTNING_ROD, Blocks.WAXED_OXIDIZED_LIGHTNING_ROD);
        this.registerWeightedPressurePlate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
        this.registerWeightedPressurePlate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
        this.registerShelf(Blocks.ACACIA_SHELF, Blocks.STRIPPED_ACACIA_LOG);
        this.registerShelf(Blocks.BAMBOO_SHELF, Blocks.STRIPPED_BAMBOO_BLOCK);
        this.registerShelf(Blocks.BIRCH_SHELF, Blocks.STRIPPED_BIRCH_LOG);
        this.registerShelf(Blocks.CHERRY_SHELF, Blocks.STRIPPED_CHERRY_LOG);
        this.registerShelf(Blocks.CRIMSON_SHELF, Blocks.STRIPPED_CRIMSON_STEM);
        this.registerShelf(Blocks.DARK_OAK_SHELF, Blocks.STRIPPED_DARK_OAK_LOG);
        this.registerShelf(Blocks.JUNGLE_SHELF, Blocks.STRIPPED_JUNGLE_LOG);
        this.registerShelf(Blocks.MANGROVE_SHELF, Blocks.STRIPPED_MANGROVE_LOG);
        this.registerShelf(Blocks.OAK_SHELF, Blocks.STRIPPED_OAK_LOG);
        this.registerShelf(Blocks.PALE_OAK_SHELF, Blocks.STRIPPED_PALE_OAK_LOG);
        this.registerShelf(Blocks.SPRUCE_SHELF, Blocks.STRIPPED_SPRUCE_LOG);
        this.registerShelf(Blocks.WARPED_SHELF, Blocks.STRIPPED_WARPED_STEM);
        this.registerAmethysts();
        this.registerBookshelf();
        this.registerChiseledBookshelf();
        this.registerBrewingStand();
        this.registerCake();
        this.registerCampfire(new Block[]{Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE});
        this.registerCartographyTable();
        this.registerCauldrons();
        this.registerChorusFlower();
        this.registerChorusPlant();
        this.registerComposter();
        this.registerDaylightDetector();
        this.registerEndPortalFrame();
        this.registerRod(Blocks.END_ROD);
        this.registerFarmland();
        this.registerFire();
        this.registerSoulFire();
        this.registerFrostedIce();
        this.registerTopSoils();
        this.registerCocoa();
        this.registerDirtPath();
        this.registerGrindstone();
        this.registerHopper();
        this.registerBars(Blocks.IRON_BARS);
        Blocks.COPPER_BARS.getWaxingMap().forEach((arg_0, arg_1) -> this.registerCopperBars(arg_0, arg_1));
        this.registerLever();
        this.registerLilyPad();
        this.registerNetherPortal();
        this.registerNetherrack();
        this.registerObserver();
        this.registerPistons();
        this.registerPistonHead();
        this.registerScaffolding();
        this.registerRedstoneTorch();
        this.registerRedstoneLamp();
        this.registerRepeater();
        this.registerSeaPickle();
        this.registerSmithingTable();
        this.registerSnows();
        this.registerStonecutter();
        this.registerStructureBlock();
        this.registerSweetBerryBush();
        this.registerTestBlock();
        this.registerSimpleCubeAll(Blocks.TEST_INSTANCE_BLOCK);
        this.registerTripwire();
        this.registerTripwireHook();
        this.registerTurtleEgg();
        this.registerSnifferEgg();
        this.registerDriedGhast();
        this.registerVine();
        this.registerMultifaceBlock(Blocks.GLOW_LICHEN);
        this.registerMultifaceBlock(Blocks.SCULK_VEIN);
        this.registerMultifaceBlock(Blocks.RESIN_CLUMP, Items.RESIN_CLUMP);
        this.registerMagmaBlock();
        this.registerJigsaw();
        this.registerSculkSensor();
        this.registerCalibratedSculkSensor();
        this.registerSculkShrieker();
        this.registerFrogspawn();
        this.registerMangrovePropagule();
        this.registerMuddyMangroveRoots();
        this.registerTrialSpawner();
        this.registerVault();
        this.registerNorthDefaultHorizontalRotatable(Blocks.LADDER);
        this.registerItemModel(Blocks.LADDER);
        this.registerNorthDefaultHorizontalRotatable(Blocks.LECTERN);
        this.registerBigDripleaf();
        this.registerNorthDefaultHorizontalRotatable(Blocks.BIG_DRIPLEAF_STEM);
        this.registerTorch(Blocks.TORCH, Blocks.WALL_TORCH);
        this.registerTorch(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH);
        this.registerTorch(Blocks.COPPER_TORCH, Blocks.COPPER_WALL_TORCH);
        this.registerCubeWithCustomTextures(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, TextureMap::frontSideWithCustomBottom);
        this.registerCubeWithCustomTextures(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, TextureMap::frontTopSide);
        this.registerNetherrackBottomCustomTop(Blocks.CRIMSON_NYLIUM);
        this.registerNetherrackBottomCustomTop(Blocks.WARPED_NYLIUM);
        this.registerDispenserLikeOrientable(Blocks.DISPENSER);
        this.registerDispenserLikeOrientable(Blocks.DROPPER);
        this.registerCrafter();
        this.registerLantern(Blocks.LANTERN);
        this.registerLantern(Blocks.SOUL_LANTERN);
        Blocks.COPPER_LANTERNS.getWaxingMap().forEach((arg_0, arg_1) -> this.registerCopperLantern(arg_0, arg_1));
        this.registerAxisRotated(Blocks.IRON_CHAIN, BlockStateModelGenerator.createWeightedVariant((Identifier)TexturedModel.TEMPLATE_CHAIN.upload(Blocks.IRON_CHAIN, this.modelCollector)));
        Blocks.COPPER_CHAINS.getWaxingMap().forEach((arg_0, arg_1) -> this.registerCopperChain(arg_0, arg_1));
        this.registerAxisRotated(Blocks.BASALT, TexturedModel.CUBE_COLUMN);
        this.registerAxisRotated(Blocks.POLISHED_BASALT, TexturedModel.CUBE_COLUMN);
        this.registerSimpleCubeAll(Blocks.SMOOTH_BASALT);
        this.registerAxisRotated(Blocks.BONE_BLOCK, TexturedModel.CUBE_COLUMN);
        this.registerRotatable(Blocks.DIRT);
        this.registerRotatable(Blocks.ROOTED_DIRT);
        this.registerRotatable(Blocks.SAND);
        this.registerBrushableBlock(Blocks.SUSPICIOUS_SAND);
        this.registerBrushableBlock(Blocks.SUSPICIOUS_GRAVEL);
        this.registerRotatable(Blocks.RED_SAND);
        this.registerMirrorable(Blocks.BEDROCK);
        this.registerSingleton(Blocks.REINFORCED_DEEPSLATE, TexturedModel.CUBE_BOTTOM_TOP);
        this.registerAxisRotated(Blocks.HAY_BLOCK, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.PURPUR_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.QUARTZ_PILLAR, TexturedModel.END_FOR_TOP_CUBE_COLUMN, TexturedModel.END_FOR_TOP_CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.OCHRE_FROGLIGHT, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.VERDANT_FROGLIGHT, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        this.registerAxisRotated(Blocks.PEARLESCENT_FROGLIGHT, TexturedModel.CUBE_COLUMN, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        this.registerNorthDefaultHorizontalRotatable(Blocks.LOOM, TexturedModel.ORIENTABLE_WITH_BOTTOM);
        this.registerPumpkins();
        this.registerBeehive(Blocks.BEE_NEST, TextureMap::sideFrontTopBottom);
        this.registerBeehive(Blocks.BEEHIVE, TextureMap::sideFrontEnd);
        this.registerCrop(Blocks.BEETROOTS, (Property)Properties.AGE_3, new int[]{0, 1, 2, 3});
        this.registerCrop(Blocks.CARROTS, (Property)Properties.AGE_7, new int[]{0, 0, 1, 1, 2, 2, 2, 3});
        this.registerCrop(Blocks.NETHER_WART, (Property)Properties.AGE_3, new int[]{0, 1, 1, 2});
        this.registerCrop(Blocks.POTATOES, (Property)Properties.AGE_7, new int[]{0, 0, 1, 1, 2, 2, 2, 3});
        this.registerCrop(Blocks.WHEAT, (Property)Properties.AGE_7, new int[]{0, 1, 2, 3, 4, 5, 6, 7});
        this.registerTintableCrossBlockStateWithStages(Blocks.TORCHFLOWER_CROP, CrossType.NOT_TINTED, (Property)Properties.AGE_1, new int[]{0, 1});
        this.registerPitcherCrop();
        this.registerPitcherPlant();
        this.registerBanners();
        this.registerBeds();
        this.registerSkulls();
        this.registerChests();
        this.registerCopperChests();
        this.registerShulkerBox(Blocks.SHULKER_BOX, null);
        this.registerShulkerBox(Blocks.WHITE_SHULKER_BOX, DyeColor.WHITE);
        this.registerShulkerBox(Blocks.ORANGE_SHULKER_BOX, DyeColor.ORANGE);
        this.registerShulkerBox(Blocks.MAGENTA_SHULKER_BOX, DyeColor.MAGENTA);
        this.registerShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX, DyeColor.LIGHT_BLUE);
        this.registerShulkerBox(Blocks.YELLOW_SHULKER_BOX, DyeColor.YELLOW);
        this.registerShulkerBox(Blocks.LIME_SHULKER_BOX, DyeColor.LIME);
        this.registerShulkerBox(Blocks.PINK_SHULKER_BOX, DyeColor.PINK);
        this.registerShulkerBox(Blocks.GRAY_SHULKER_BOX, DyeColor.GRAY);
        this.registerShulkerBox(Blocks.LIGHT_GRAY_SHULKER_BOX, DyeColor.LIGHT_GRAY);
        this.registerShulkerBox(Blocks.CYAN_SHULKER_BOX, DyeColor.CYAN);
        this.registerShulkerBox(Blocks.PURPLE_SHULKER_BOX, DyeColor.PURPLE);
        this.registerShulkerBox(Blocks.BLUE_SHULKER_BOX, DyeColor.BLUE);
        this.registerShulkerBox(Blocks.BROWN_SHULKER_BOX, DyeColor.BROWN);
        this.registerShulkerBox(Blocks.GREEN_SHULKER_BOX, DyeColor.GREEN);
        this.registerShulkerBox(Blocks.RED_SHULKER_BOX, DyeColor.RED);
        this.registerShulkerBox(Blocks.BLACK_SHULKER_BOX, DyeColor.BLACK);
        this.registerCopperGolemStatues();
        this.registerBuiltin(Blocks.CONDUIT);
        this.registerSpecialItemModel(Blocks.CONDUIT, (SpecialModelRenderer.Unbaked)new ConduitModelRenderer.Unbaked());
        this.registerBuiltinWithParticle(Blocks.DECORATED_POT, Blocks.TERRACOTTA);
        this.registerSpecialItemModel(Blocks.DECORATED_POT, (SpecialModelRenderer.Unbaked)new DecoratedPotModelRenderer.Unbaked());
        this.registerBuiltinWithParticle(Blocks.END_PORTAL, Blocks.OBSIDIAN);
        this.registerBuiltinWithParticle(Blocks.END_GATEWAY, Blocks.OBSIDIAN);
        this.registerSimpleCubeAll(Blocks.AZALEA_LEAVES);
        this.registerSimpleCubeAll(Blocks.FLOWERING_AZALEA_LEAVES);
        this.registerSimpleCubeAll(Blocks.WHITE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.ORANGE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.MAGENTA_CONCRETE);
        this.registerSimpleCubeAll(Blocks.LIGHT_BLUE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.YELLOW_CONCRETE);
        this.registerSimpleCubeAll(Blocks.LIME_CONCRETE);
        this.registerSimpleCubeAll(Blocks.PINK_CONCRETE);
        this.registerSimpleCubeAll(Blocks.GRAY_CONCRETE);
        this.registerSimpleCubeAll(Blocks.LIGHT_GRAY_CONCRETE);
        this.registerSimpleCubeAll(Blocks.CYAN_CONCRETE);
        this.registerSimpleCubeAll(Blocks.PURPLE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.BLUE_CONCRETE);
        this.registerSimpleCubeAll(Blocks.BROWN_CONCRETE);
        this.registerSimpleCubeAll(Blocks.GREEN_CONCRETE);
        this.registerSimpleCubeAll(Blocks.RED_CONCRETE);
        this.registerSimpleCubeAll(Blocks.BLACK_CONCRETE);
        this.registerRandomHorizontalRotations(TexturedModel.CUBE_ALL, new Block[]{Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER});
        this.registerSimpleCubeAll(Blocks.TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.WHITE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.ORANGE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.MAGENTA_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.LIGHT_BLUE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.YELLOW_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.LIME_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.PINK_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.GRAY_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.LIGHT_GRAY_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.CYAN_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.PURPLE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.BLUE_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.BROWN_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.GREEN_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.RED_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.BLACK_TERRACOTTA);
        this.registerSimpleCubeAll(Blocks.TINTED_GLASS);
        this.registerGlassAndPane(Blocks.GLASS, Blocks.GLASS_PANE);
        this.registerGlassAndPane(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
        this.registerGlassAndPane(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
        this.registerSouthDefaultHorizontalFacing(TexturedModel.TEMPLATE_GLAZED_TERRACOTTA, new Block[]{Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA});
        this.registerWoolAndCarpet(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
        this.registerWoolAndCarpet(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
        this.registerWoolAndCarpet(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
        this.registerWoolAndCarpet(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
        this.registerWoolAndCarpet(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
        this.registerWoolAndCarpet(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
        this.registerWoolAndCarpet(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
        this.registerWoolAndCarpet(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
        this.registerWoolAndCarpet(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
        this.registerWoolAndCarpet(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
        this.registerWoolAndCarpet(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
        this.registerWoolAndCarpet(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
        this.registerWoolAndCarpet(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
        this.registerWoolAndCarpet(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
        this.registerWoolAndCarpet(Blocks.RED_WOOL, Blocks.RED_CARPET);
        this.registerWoolAndCarpet(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
        this.registerSimpleCubeAll(Blocks.MUD);
        this.registerSimpleCubeAll(Blocks.PACKED_MUD);
        this.registerFlowerPotPlant(Blocks.FERN, Blocks.POTTED_FERN, CrossType.TINTED);
        this.registerGrassTinted(Blocks.FERN);
        this.registerFlowerPotPlantAndItem(Blocks.DANDELION, Blocks.POTTED_DANDELION, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.POPPY, Blocks.POTTED_POPPY, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.OPEN_EYEBLOSSOM, Blocks.POTTED_OPEN_EYEBLOSSOM, CrossType.EMISSIVE_NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.CLOSED_EYEBLOSSOM, Blocks.POTTED_CLOSED_EYEBLOSSOM, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, CrossType.NOT_TINTED);
        this.registerFlowerPotPlantAndItem(Blocks.TORCHFLOWER, Blocks.POTTED_TORCHFLOWER, CrossType.NOT_TINTED);
        this.registerPointedDripstone();
        this.registerMushroomBlock(Blocks.BROWN_MUSHROOM_BLOCK);
        this.registerMushroomBlock(Blocks.RED_MUSHROOM_BLOCK);
        this.registerMushroomBlock(Blocks.MUSHROOM_STEM);
        this.registerTintableCrossBlockState(Blocks.SHORT_GRASS, CrossType.TINTED);
        this.registerGrassTinted(Blocks.SHORT_GRASS);
        this.registerTintableCross(Blocks.SHORT_DRY_GRASS, CrossType.NOT_TINTED);
        this.registerTintableCross(Blocks.TALL_DRY_GRASS, CrossType.NOT_TINTED);
        this.registerTintableCrossBlockState(Blocks.BUSH, CrossType.TINTED);
        this.registerGrassTinted(Blocks.BUSH);
        this.registerTintableCrossBlockState(Blocks.SUGAR_CANE, CrossType.TINTED);
        this.registerItemModel(Items.SUGAR_CANE);
        this.registerPlantPart(Blocks.KELP, Blocks.KELP_PLANT, CrossType.NOT_TINTED);
        this.registerItemModel(Items.KELP);
        this.registerTintableCrossBlockState(Blocks.HANGING_ROOTS, CrossType.NOT_TINTED);
        this.registerPlantPart(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, CrossType.NOT_TINTED);
        this.registerPlantPart(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT, CrossType.NOT_TINTED);
        this.registerItemModel(Blocks.WEEPING_VINES, "_plant");
        this.registerItemModel(Blocks.TWISTING_VINES, "_plant");
        this.registerTintableCross(Blocks.BAMBOO_SAPLING, CrossType.TINTED, TextureMap.cross((Identifier)TextureMap.getSubId((Block)Blocks.BAMBOO, (String)"_stage0")));
        this.registerBamboo();
        this.registerTintableCross(Blocks.CACTUS_FLOWER, CrossType.NOT_TINTED);
        this.registerTintableCross(Blocks.COBWEB, CrossType.NOT_TINTED);
        this.registerDoubleBlockAndItem(Blocks.LILAC, CrossType.NOT_TINTED);
        this.registerDoubleBlockAndItem(Blocks.ROSE_BUSH, CrossType.NOT_TINTED);
        this.registerDoubleBlockAndItem(Blocks.PEONY, CrossType.NOT_TINTED);
        this.registerGrassTintedDoubleBlockAndItem(Blocks.TALL_GRASS);
        this.registerGrassTintedDoubleBlockAndItem(Blocks.LARGE_FERN);
        this.registerSunflower();
        this.registerTallSeagrass();
        this.registerSmallDripleaf();
        this.registerCoral(Blocks.TUBE_CORAL, Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN);
        this.registerCoral(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN);
        this.registerCoral(Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN);
        this.registerCoral(Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN);
        this.registerCoral(Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN);
        this.registerGourd(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
        this.registerGourd(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        this.createLogTexturePool(Blocks.MANGROVE_LOG).log(Blocks.MANGROVE_LOG).wood(Blocks.MANGROVE_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_MANGROVE_LOG).log(Blocks.STRIPPED_MANGROVE_LOG).wood(Blocks.STRIPPED_MANGROVE_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN);
        this.registerTintedBlockAndItem(Blocks.MANGROVE_LEAVES, TexturedModel.LEAVES, -7158200);
        this.createLogTexturePool(Blocks.ACACIA_LOG).log(Blocks.ACACIA_LOG).wood(Blocks.ACACIA_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_ACACIA_LOG).log(Blocks.STRIPPED_ACACIA_LOG).wood(Blocks.STRIPPED_ACACIA_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES, -12012264);
        this.createLogTexturePool(Blocks.CHERRY_LOG).uvLockedLog(Blocks.CHERRY_LOG).wood(Blocks.CHERRY_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_CHERRY_LOG).uvLockedLog(Blocks.STRIPPED_CHERRY_LOG).wood(Blocks.STRIPPED_CHERRY_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.CHERRY_SAPLING, Blocks.POTTED_CHERRY_SAPLING, CrossType.NOT_TINTED);
        this.registerSingleton(Blocks.CHERRY_LEAVES, TexturedModel.LEAVES);
        this.createLogTexturePool(Blocks.BIRCH_LOG).log(Blocks.BIRCH_LOG).wood(Blocks.BIRCH_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_BIRCH_LOG).log(Blocks.STRIPPED_BIRCH_LOG).wood(Blocks.STRIPPED_BIRCH_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES, -8345771);
        this.createLogTexturePool(Blocks.OAK_LOG).log(Blocks.OAK_LOG).wood(Blocks.OAK_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_OAK_LOG).log(Blocks.STRIPPED_OAK_LOG).wood(Blocks.STRIPPED_OAK_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_OAK_LOG, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.OAK_LEAVES, TexturedModel.LEAVES, -12012264);
        this.createLogTexturePool(Blocks.SPRUCE_LOG).log(Blocks.SPRUCE_LOG).wood(Blocks.SPRUCE_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_SPRUCE_LOG).log(Blocks.STRIPPED_SPRUCE_LOG).wood(Blocks.STRIPPED_SPRUCE_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES, -10380959);
        this.createLogTexturePool(Blocks.DARK_OAK_LOG).log(Blocks.DARK_OAK_LOG).wood(Blocks.DARK_OAK_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_DARK_OAK_LOG).log(Blocks.STRIPPED_DARK_OAK_LOG).wood(Blocks.STRIPPED_DARK_OAK_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES, -12012264);
        this.createLogTexturePool(Blocks.PALE_OAK_LOG).log(Blocks.PALE_OAK_LOG).wood(Blocks.PALE_OAK_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_PALE_OAK_LOG).log(Blocks.STRIPPED_PALE_OAK_LOG).wood(Blocks.STRIPPED_PALE_OAK_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_PALE_OAK_LOG, Blocks.PALE_OAK_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.PALE_OAK_SAPLING, Blocks.POTTED_PALE_OAK_SAPLING, CrossType.NOT_TINTED);
        this.registerSingleton(Blocks.PALE_OAK_LEAVES, TexturedModel.LEAVES);
        this.createLogTexturePool(Blocks.JUNGLE_LOG).log(Blocks.JUNGLE_LOG).wood(Blocks.JUNGLE_WOOD);
        this.createLogTexturePool(Blocks.STRIPPED_JUNGLE_LOG).log(Blocks.STRIPPED_JUNGLE_LOG).wood(Blocks.STRIPPED_JUNGLE_WOOD);
        this.registerHangingSign(Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, CrossType.NOT_TINTED);
        this.registerTintedBlockAndItem(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES, -12012264);
        this.createLogTexturePool(Blocks.CRIMSON_STEM).stem(Blocks.CRIMSON_STEM).wood(Blocks.CRIMSON_HYPHAE);
        this.createLogTexturePool(Blocks.STRIPPED_CRIMSON_STEM).stem(Blocks.STRIPPED_CRIMSON_STEM).wood(Blocks.STRIPPED_CRIMSON_HYPHAE);
        this.registerHangingSign(Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, CrossType.NOT_TINTED);
        this.registerRoots(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
        this.createLogTexturePool(Blocks.WARPED_STEM).stem(Blocks.WARPED_STEM).wood(Blocks.WARPED_HYPHAE);
        this.createLogTexturePool(Blocks.STRIPPED_WARPED_STEM).stem(Blocks.STRIPPED_WARPED_STEM).wood(Blocks.STRIPPED_WARPED_HYPHAE);
        this.registerHangingSign(Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);
        this.registerFlowerPotPlantAndItem(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, CrossType.NOT_TINTED);
        this.registerRoots(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
        this.createLogTexturePool(Blocks.BAMBOO_BLOCK).uvLockedLog(Blocks.BAMBOO_BLOCK);
        this.createLogTexturePool(Blocks.STRIPPED_BAMBOO_BLOCK).uvLockedLog(Blocks.STRIPPED_BAMBOO_BLOCK);
        this.registerHangingSign(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
        this.registerTintableCrossBlockState(Blocks.NETHER_SPROUTS, CrossType.NOT_TINTED);
        this.registerItemModel(Items.NETHER_SPROUTS);
        this.registerDoor(Blocks.IRON_DOOR);
        this.registerTrapdoor(Blocks.IRON_TRAPDOOR);
        this.registerSmoothStone();
        this.registerTurnableRail(Blocks.RAIL);
        this.registerStraightRail(Blocks.POWERED_RAIL);
        this.registerStraightRail(Blocks.DETECTOR_RAIL);
        this.registerStraightRail(Blocks.ACTIVATOR_RAIL);
        this.registerComparator();
        this.registerCommandBlock(Blocks.COMMAND_BLOCK);
        this.registerCommandBlock(Blocks.REPEATING_COMMAND_BLOCK);
        this.registerCommandBlock(Blocks.CHAIN_COMMAND_BLOCK);
        this.registerAnvil(Blocks.ANVIL);
        this.registerAnvil(Blocks.CHIPPED_ANVIL);
        this.registerAnvil(Blocks.DAMAGED_ANVIL);
        this.registerBarrel();
        this.registerBell();
        this.registerCooker(Blocks.FURNACE, TexturedModel.ORIENTABLE);
        this.registerCooker(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE);
        this.registerCooker(Blocks.SMOKER, TexturedModel.ORIENTABLE_WITH_BOTTOM);
        this.registerRedstone();
        this.registerRespawnAnchor();
        this.registerSculkCatalyst();
        this.registerParented(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
        this.registerParented(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE);
        this.registerParented(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
        this.registerParented(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
        this.registerInfestedStone();
        this.registerParented(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
        this.registerInfestedDeepslate();
    }

    private void registerLightBlock() {
        ItemModel.Unbaked unbaked = ItemModels.basic((Identifier)this.uploadItemModel(Items.LIGHT));
        HashMap<Integer, ItemModel.Unbaked> map = new HashMap<Integer, ItemModel.Unbaked>(16);
        BlockStateVariantMap.SingleProperty singleProperty = BlockStateVariantMap.models((Property)Properties.LEVEL_15);
        for (int i = 0; i <= 15; ++i) {
            String string = String.format(Locale.ROOT, "_%02d", i);
            Identifier identifier = TextureMap.getSubId((Item)Items.LIGHT, (String)string);
            singleProperty.register((Comparable)Integer.valueOf(i), (Object)BlockStateModelGenerator.createWeightedVariant((Identifier)Models.PARTICLE.upload(Blocks.LIGHT, string, TextureMap.particle((Identifier)identifier), this.modelCollector)));
            ItemModel.Unbaked unbaked2 = ItemModels.basic((Identifier)Models.GENERATED.upload(ModelIds.getItemSubModelId((Item)Items.LIGHT, (String)string), TextureMap.layer0((Identifier)identifier), this.modelCollector));
            map.put(i, unbaked2);
        }
        this.itemModelOutput.accept(Items.LIGHT, ItemModels.select((Property)LightBlock.LEVEL_15, (ItemModel.Unbaked)unbaked, map));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)Blocks.LIGHT).with((BlockStateVariantMap)singleProperty));
    }

    public final void registerWaxable(Item unwaxed, Item waxed) {
        Identifier identifier = this.uploadItemModel(unwaxed);
        this.registerItemModel(unwaxed, identifier);
        this.registerItemModel(waxed, identifier);
    }

    public final void registerCandle(Block candle, Block cake) {
        this.registerItemModel(candle.asItem());
        TextureMap textureMap = TextureMap.all((Identifier)TextureMap.getId((Block)candle));
        TextureMap textureMap2 = TextureMap.all((Identifier)TextureMap.getSubId((Block)candle, (String)"_lit"));
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CANDLE.upload(candle, "_one_candle", textureMap, this.modelCollector));
        WeightedVariant weightedVariant2 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TWO_CANDLES.upload(candle, "_two_candles", textureMap, this.modelCollector));
        WeightedVariant weightedVariant3 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_THREE_CANDLES.upload(candle, "_three_candles", textureMap, this.modelCollector));
        WeightedVariant weightedVariant4 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_FOUR_CANDLES.upload(candle, "_four_candles", textureMap, this.modelCollector));
        WeightedVariant weightedVariant5 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CANDLE.upload(candle, "_one_candle_lit", textureMap2, this.modelCollector));
        WeightedVariant weightedVariant6 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_TWO_CANDLES.upload(candle, "_two_candles_lit", textureMap2, this.modelCollector));
        WeightedVariant weightedVariant7 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_THREE_CANDLES.upload(candle, "_three_candles_lit", textureMap2, this.modelCollector));
        WeightedVariant weightedVariant8 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_FOUR_CANDLES.upload(candle, "_four_candles_lit", textureMap2, this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)candle).with((BlockStateVariantMap)BlockStateVariantMap.models((Property)Properties.CANDLES, (Property)Properties.LIT).register((Comparable)Integer.valueOf(1), (Comparable)Boolean.valueOf(false), (Object)weightedVariant).register((Comparable)Integer.valueOf(2), (Comparable)Boolean.valueOf(false), (Object)weightedVariant2).register((Comparable)Integer.valueOf(3), (Comparable)Boolean.valueOf(false), (Object)weightedVariant3).register((Comparable)Integer.valueOf(4), (Comparable)Boolean.valueOf(false), (Object)weightedVariant4).register((Comparable)Integer.valueOf(1), (Comparable)Boolean.valueOf(true), (Object)weightedVariant5).register((Comparable)Integer.valueOf(2), (Comparable)Boolean.valueOf(true), (Object)weightedVariant6).register((Comparable)Integer.valueOf(3), (Comparable)Boolean.valueOf(true), (Object)weightedVariant7).register((Comparable)Integer.valueOf(4), (Comparable)Boolean.valueOf(true), (Object)weightedVariant8)));
        WeightedVariant weightedVariant9 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CAKE_WITH_CANDLE.upload(cake, TextureMap.candleCake((Block)candle, (boolean)false), this.modelCollector));
        WeightedVariant weightedVariant10 = BlockStateModelGenerator.createWeightedVariant((Identifier)Models.TEMPLATE_CAKE_WITH_CANDLE.upload(cake, "_lit", TextureMap.candleCake((Block)candle, (boolean)true), this.modelCollector));
        this.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of((Block)cake).with(BlockStateModelGenerator.createBooleanModelMap((BooleanProperty)Properties.LIT, (WeightedVariant)weightedVariant10, (WeightedVariant)weightedVariant9)));
    }

    private /* synthetic */ WeightedVariant method_67830(int[] is, Int2ObjectMap int2ObjectMap, Block block, Integer age) {
        int i = is[age];
        return BlockStateModelGenerator.createWeightedVariant((Identifier)((Identifier)int2ObjectMap.computeIfAbsent(i, stage -> this.createSubModel(block, "_stage" + stage, Models.CROP, TextureMap::crop))));
    }
}

