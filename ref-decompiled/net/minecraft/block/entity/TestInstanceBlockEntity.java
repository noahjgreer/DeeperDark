/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.entity.BeamEmitter
 *  net.minecraft.block.entity.BeamEmitter$BeamSegment
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.StructureBlockBlockEntity
 *  net.minecraft.block.entity.StructureBoxRendering
 *  net.minecraft.block.entity.StructureBoxRendering$RenderMode
 *  net.minecraft.block.entity.StructureBoxRendering$StructureBox
 *  net.minecraft.block.entity.TestInstanceBlockEntity
 *  net.minecraft.block.entity.TestInstanceBlockEntity$1
 *  net.minecraft.block.entity.TestInstanceBlockEntity$Data
 *  net.minecraft.block.entity.TestInstanceBlockEntity$Error
 *  net.minecraft.block.entity.TestInstanceBlockEntity$Status
 *  net.minecraft.data.DataWriter
 *  net.minecraft.data.dev.NbtProvider
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.entry.RegistryEntry$Reference
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.TestCommand
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.structure.StructurePlacementData
 *  net.minecraft.structure.StructureTemplate
 *  net.minecraft.test.GameTestState
 *  net.minecraft.test.RuntimeTestInstances
 *  net.minecraft.test.TestAttemptConfig
 *  net.minecraft.test.TestInstance
 *  net.minecraft.test.TestInstanceUtil
 *  net.minecraft.test.TestManager
 *  net.minecraft.test.TestRunContext
 *  net.minecraft.test.TestRunContext$Builder
 *  net.minecraft.text.Text
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockBox
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.path.PathUtil
 *  net.minecraft.world.ServerWorldAccess
 *  net.minecraft.world.World
 */
package net.minecraft.block.entity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeamEmitter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.StructureBoxRendering;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.data.DataWriter;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TestCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.test.GameTestState;
import net.minecraft.test.RuntimeTestInstances;
import net.minecraft.test.TestAttemptConfig;
import net.minecraft.test.TestInstance;
import net.minecraft.test.TestInstanceUtil;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestRunContext;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class TestInstanceBlockEntity
extends BlockEntity
implements BeamEmitter,
StructureBoxRendering {
    private static final Text INVALID_TEST_TEXT = Text.translatable((String)"test_instance_block.invalid_test");
    private static final List<BeamEmitter.BeamSegment> CLEARED_BEAM_SEGMENTS = List.of();
    private static final List<BeamEmitter.BeamSegment> RUNNING_BEAM_SEGMENTS = List.of(new BeamEmitter.BeamSegment(ColorHelper.getArgb((int)128, (int)128, (int)128)));
    private static final List<BeamEmitter.BeamSegment> SUCCESS_BEAM_SEGMENTS = List.of(new BeamEmitter.BeamSegment(ColorHelper.getArgb((int)0, (int)255, (int)0)));
    private static final List<BeamEmitter.BeamSegment> REQUIRED_FAIL_BEAM_SEGMENTS = List.of(new BeamEmitter.BeamSegment(ColorHelper.getArgb((int)255, (int)0, (int)0)));
    private static final List<BeamEmitter.BeamSegment> OPTIONAL_FAIL_BEAM_SEGMENTS = List.of(new BeamEmitter.BeamSegment(ColorHelper.getArgb((int)255, (int)128, (int)0)));
    private static final Vec3i STRUCTURE_OFFSET = new Vec3i(0, 1, 1);
    private Data data;
    private final List<Error> errors = new ArrayList();

    public TestInstanceBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.TEST_INSTANCE_BLOCK, pos, state);
        this.data = new Data(Optional.empty(), Vec3i.ZERO, BlockRotation.NONE, false, Status.CLEARED, Optional.empty());
    }

    public void setData(Data data) {
        this.data = data;
        this.markDirty();
    }

    public static Optional<Vec3i> getStructureSize(ServerWorld world, RegistryKey<TestInstance> testInstance) {
        return TestInstanceBlockEntity.getStructureTemplate((ServerWorld)world, testInstance).map(StructureTemplate::getSize);
    }

    public BlockBox getBlockBox() {
        BlockPos blockPos = this.getStructurePos();
        BlockPos blockPos2 = blockPos.add(this.getTransformedSize()).add(-1, -1, -1);
        return BlockBox.create((Vec3i)blockPos, (Vec3i)blockPos2);
    }

    public Box getBox() {
        return Box.from((BlockBox)this.getBlockBox());
    }

    private static Optional<StructureTemplate> getStructureTemplate(ServerWorld world, RegistryKey<TestInstance> testInstance) {
        return world.getRegistryManager().getOptionalEntry(testInstance).map(entry -> ((TestInstance)entry.value()).getStructure()).flatMap(structureId -> world.getStructureTemplateManager().getTemplate(structureId));
    }

    public Optional<RegistryKey<TestInstance>> getTestKey() {
        return this.data.test();
    }

    public Text getTestName() {
        return this.getTestKey().map(key -> Text.literal((String)key.getValue().toString())).orElse(INVALID_TEST_TEXT);
    }

    private Optional<RegistryEntry.Reference<TestInstance>> getTestEntry() {
        return this.getTestKey().flatMap(arg_0 -> ((DynamicRegistryManager)this.world.getRegistryManager()).getOptionalEntry(arg_0));
    }

    public boolean shouldIgnoreEntities() {
        return this.data.ignoreEntities();
    }

    public Vec3i getSize() {
        return this.data.size();
    }

    public BlockRotation getRotation() {
        return this.getTestEntry().map(RegistryEntry::value).map(TestInstance::getRotation).orElse(BlockRotation.NONE).rotate(this.data.rotation());
    }

    public Optional<Text> getErrorMessage() {
        return this.data.errorMessage();
    }

    public void setErrorMessage(Text errorMessage) {
        this.setData(this.data.withErrorMessage(errorMessage));
    }

    public void setFinished() {
        this.setData(this.data.withStatus(Status.FINISHED));
    }

    public void setRunning() {
        this.setData(this.data.withStatus(Status.RUNNING));
    }

    public void markDirty() {
        super.markDirty();
        if (this.world instanceof ServerWorld) {
            this.world.updateListeners(this.getPos(), Blocks.AIR.getDefaultState(), this.getCachedState(), 3);
        }
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createComponentlessNbt(registries);
    }

    protected void readData(ReadView view) {
        view.read("data", Data.CODEC).ifPresent(arg_0 -> this.setData(arg_0));
        this.errors.clear();
        this.errors.addAll(view.read("errors", Error.LIST_CODEC).orElse(List.of()));
    }

    protected void writeData(WriteView view) {
        view.put("data", Data.CODEC, (Object)this.data);
        if (!this.errors.isEmpty()) {
            view.put("errors", Error.LIST_CODEC, (Object)this.errors);
        }
    }

    public StructureBoxRendering.RenderMode getRenderMode() {
        return StructureBoxRendering.RenderMode.BOX;
    }

    public BlockPos getStructurePos() {
        return TestInstanceBlockEntity.getStructurePos((BlockPos)this.getPos());
    }

    public static BlockPos getStructurePos(BlockPos pos) {
        return pos.add(STRUCTURE_OFFSET);
    }

    public StructureBoxRendering.StructureBox getStructureBox() {
        return new StructureBoxRendering.StructureBox(new BlockPos(STRUCTURE_OFFSET), this.getTransformedSize());
    }

    public List<BeamEmitter.BeamSegment> getBeamSegments() {
        return switch (this.data.status().ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> CLEARED_BEAM_SEGMENTS;
            case 1 -> RUNNING_BEAM_SEGMENTS;
            case 2 -> this.getErrorMessage().isEmpty() ? SUCCESS_BEAM_SEGMENTS : (this.getTestEntry().map(RegistryEntry::value).map(TestInstance::isRequired).orElse(true) != false ? REQUIRED_FAIL_BEAM_SEGMENTS : OPTIONAL_FAIL_BEAM_SEGMENTS);
        };
    }

    private Vec3i getTransformedSize() {
        Vec3i vec3i = this.getSize();
        BlockRotation blockRotation = this.getRotation();
        boolean bl = blockRotation == BlockRotation.CLOCKWISE_90 || blockRotation == BlockRotation.COUNTERCLOCKWISE_90;
        int i = bl ? vec3i.getZ() : vec3i.getX();
        int j = bl ? vec3i.getX() : vec3i.getZ();
        return new Vec3i(i, vec3i.getY(), j);
    }

    public void reset(Consumer<Text> messageConsumer) {
        this.clearBarriers();
        this.clearErrors();
        boolean bl = this.placeStructure();
        if (bl) {
            messageConsumer.accept((Text)Text.translatable((String)"test_instance_block.reset_success", (Object[])new Object[]{this.getTestName()}).formatted(Formatting.GREEN));
        }
        this.setData(this.data.withStatus(Status.CLEARED));
    }

    public Optional<Identifier> saveStructure(Consumer<Text> messageConsumer) {
        Optional optional = this.getTestEntry();
        Optional<Identifier> optional2 = optional.isPresent() ? Optional.of(((TestInstance)((RegistryEntry.Reference)optional.get()).value()).getStructure()) : this.getTestKey().map(RegistryKey::getValue);
        if (optional2.isEmpty()) {
            BlockPos blockPos = this.getPos();
            messageConsumer.accept((Text)Text.translatable((String)"test_instance_block.error.unable_to_save", (Object[])new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()}).formatted(Formatting.RED));
            return optional2;
        }
        World world = this.world;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            StructureBlockBlockEntity.saveStructure((ServerWorld)serverWorld, (Identifier)optional2.get(), (BlockPos)this.getStructurePos(), (Vec3i)this.getSize(), (boolean)this.shouldIgnoreEntities(), (String)"", (boolean)true, List.of(Blocks.AIR));
        }
        return optional2;
    }

    public boolean export(Consumer<Text> messageConsumer) {
        World world;
        Optional optional = this.saveStructure(messageConsumer);
        if (optional.isEmpty() || !((world = this.world) instanceof ServerWorld)) {
            return false;
        }
        ServerWorld serverWorld = (ServerWorld)world;
        return TestInstanceBlockEntity.exportData((ServerWorld)serverWorld, (Identifier)((Identifier)optional.get()), messageConsumer);
    }

    public static boolean exportData(ServerWorld world, Identifier structureId, Consumer<Text> messageConsumer) {
        Path path = TestInstanceUtil.testStructuresDirectoryName;
        Path path2 = world.getStructureTemplateManager().getTemplatePath(structureId, ".nbt");
        Path path3 = NbtProvider.convertNbtToSnbt((DataWriter)DataWriter.UNCACHED, (Path)path2, (String)structureId.getPath(), (Path)path.resolve(structureId.getNamespace()).resolve("structure"));
        if (path3 == null) {
            messageConsumer.accept((Text)Text.literal((String)("Failed to export " + String.valueOf(path2))).formatted(Formatting.RED));
            return true;
        }
        try {
            PathUtil.createDirectories((Path)path3.getParent());
        }
        catch (IOException iOException) {
            messageConsumer.accept((Text)Text.literal((String)("Could not create folder " + String.valueOf(path3.getParent()))).formatted(Formatting.RED));
            return true;
        }
        messageConsumer.accept((Text)Text.literal((String)("Exported " + String.valueOf(structureId) + " to " + String.valueOf(path3.toAbsolutePath()))));
        return false;
    }

    public void start(Consumer<Text> messageConsumer) {
        World world = this.world;
        if (!(world instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)world;
        Optional optional = this.getTestEntry();
        BlockPos blockPos = this.getPos();
        if (optional.isEmpty()) {
            messageConsumer.accept((Text)Text.translatable((String)"test_instance_block.error.no_test", (Object[])new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()}).formatted(Formatting.RED));
            return;
        }
        if (!this.placeStructure()) {
            messageConsumer.accept((Text)Text.translatable((String)"test_instance_block.error.no_test_structure", (Object[])new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()}).formatted(Formatting.RED));
            return;
        }
        this.clearErrors();
        TestManager.INSTANCE.clear();
        RuntimeTestInstances.clear();
        messageConsumer.accept((Text)Text.translatable((String)"test_instance_block.starting", (Object[])new Object[]{((RegistryEntry.Reference)optional.get()).getIdAsString()}));
        GameTestState gameTestState = new GameTestState((RegistryEntry.Reference)optional.get(), this.data.rotation(), serverWorld, TestAttemptConfig.once());
        gameTestState.setTestBlockPos(blockPos);
        TestRunContext testRunContext = TestRunContext.Builder.ofStates(List.of(gameTestState), (ServerWorld)serverWorld).build();
        TestCommand.start((ServerCommandSource)serverWorld.getServer().getCommandSource(), (TestRunContext)testRunContext);
    }

    public boolean placeStructure() {
        World world = this.world;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            Optional optional = this.data.test().flatMap(template -> TestInstanceBlockEntity.getStructureTemplate((ServerWorld)serverWorld, (RegistryKey)template));
            if (optional.isPresent()) {
                this.placeStructure(serverWorld, (StructureTemplate)optional.get());
                return true;
            }
        }
        return false;
    }

    private void placeStructure(ServerWorld world, StructureTemplate template) {
        StructurePlacementData structurePlacementData = new StructurePlacementData().setRotation(this.getRotation()).setIgnoreEntities(this.data.ignoreEntities()).setUpdateNeighbors(true);
        BlockPos blockPos = this.getStartPos();
        this.setChunksForced();
        TestInstanceUtil.clearArea((BlockBox)this.getBlockBox(), (ServerWorld)world);
        this.discardEntities();
        template.place((ServerWorldAccess)world, blockPos, blockPos, structurePlacementData, world.getRandom(), 818);
    }

    private void discardEntities() {
        this.world.getOtherEntities(null, this.getBox()).stream().filter(entity -> !(entity instanceof PlayerEntity)).forEach(Entity::discard);
    }

    private void setChunksForced() {
        World world = this.world;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            this.getBlockBox().streamChunkPos().forEach(pos -> serverWorld.setChunkForced(pos.x, pos.z, true));
        }
    }

    public BlockPos getStartPos() {
        Vec3i vec3i = this.getSize();
        BlockRotation blockRotation = this.getRotation();
        BlockPos blockPos = this.getStructurePos();
        return switch (1.field_56011[blockRotation.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> blockPos;
            case 2 -> blockPos.add(vec3i.getZ() - 1, 0, 0);
            case 3 -> blockPos.add(vec3i.getX() - 1, 0, vec3i.getZ() - 1);
            case 4 -> blockPos.add(0, 0, vec3i.getX() - 1);
        };
    }

    public void placeBarriers() {
        this.forEachPos(pos -> {
            if (!this.world.getBlockState(pos).isOf(Blocks.TEST_INSTANCE_BLOCK)) {
                this.world.setBlockState(pos, Blocks.BARRIER.getDefaultState());
            }
        });
    }

    public void clearBarriers() {
        this.forEachPos(pos -> {
            if (this.world.getBlockState(pos).isOf(Blocks.BARRIER)) {
                this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        });
    }

    public void forEachPos(Consumer<BlockPos> posConsumer) {
        Box box = this.getBox();
        boolean bl = this.getTestEntry().map(entry -> ((TestInstance)entry.value()).requiresSkyAccess()).orElse(false) == false;
        BlockPos blockPos = BlockPos.ofFloored((double)box.minX, (double)box.minY, (double)box.minZ).add(-1, -1, -1);
        BlockPos blockPos2 = BlockPos.ofFloored((double)box.maxX, (double)box.maxY, (double)box.maxZ);
        BlockPos.stream((BlockPos)blockPos, (BlockPos)blockPos2).forEach(pos -> {
            boolean bl3;
            boolean bl2 = pos.getX() == blockPos.getX() || pos.getX() == blockPos2.getX() || pos.getZ() == blockPos.getZ() || pos.getZ() == blockPos2.getZ() || pos.getY() == blockPos.getY();
            boolean bl4 = bl3 = pos.getY() == blockPos2.getY();
            if (bl2 || bl3 && bl) {
                posConsumer.accept((BlockPos)pos);
            }
        });
    }

    public void addError(BlockPos pos, Text message) {
        this.errors.add(new Error(pos, message));
        this.markDirty();
    }

    public void clearErrors() {
        if (!this.errors.isEmpty()) {
            this.errors.clear();
            this.markDirty();
        }
    }

    public List<Error> getErrors() {
        return this.errors;
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

