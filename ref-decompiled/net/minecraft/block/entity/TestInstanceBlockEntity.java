package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataWriter;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
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
import net.minecraft.text.TextCodecs;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.World;

public class TestInstanceBlockEntity extends BlockEntity implements BeamEmitter, StructureBoxRendering {
   private static final Text INVALID_TEST_TEXT = Text.translatable("test_instance_block.invalid_test");
   private static final List CLEARED_BEAM_SEGMENTS = List.of();
   private static final List RUNNING_BEAM_SEGMENTS = List.of(new BeamEmitter.BeamSegment(ColorHelper.getArgb(128, 128, 128)));
   private static final List SUCCESS_BEAM_SEGMENTS = List.of(new BeamEmitter.BeamSegment(ColorHelper.getArgb(0, 255, 0)));
   private static final List REQUIRED_FAIL_BEAM_SEGMENTS = List.of(new BeamEmitter.BeamSegment(ColorHelper.getArgb(255, 0, 0)));
   private static final List OPTIONAL_FAIL_BEAM_SEGMENTS = List.of(new BeamEmitter.BeamSegment(ColorHelper.getArgb(255, 128, 0)));
   private static final Vec3i STRUCTURE_OFFSET = new Vec3i(0, 1, 1);
   private Data data;

   public TestInstanceBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.TEST_INSTANCE_BLOCK, pos, state);
      this.data = new Data(Optional.empty(), Vec3i.ZERO, BlockRotation.NONE, false, TestInstanceBlockEntity.Status.CLEARED, Optional.empty());
   }

   public void setData(Data data) {
      this.data = data;
      this.markDirty();
   }

   public static Optional getStructureSize(ServerWorld world, RegistryKey testInstance) {
      return getStructureTemplate(world, testInstance).map(StructureTemplate::getSize);
   }

   public BlockBox getBlockBox() {
      BlockPos blockPos = this.getStructurePos();
      BlockPos blockPos2 = blockPos.add(this.getTransformedSize()).add(-1, -1, -1);
      return BlockBox.create(blockPos, blockPos2);
   }

   public Box getBox() {
      return Box.from(this.getBlockBox());
   }

   private static Optional getStructureTemplate(ServerWorld world, RegistryKey testInstance) {
      return world.getRegistryManager().getOptionalEntry(testInstance).map((entry) -> {
         return ((TestInstance)entry.value()).getStructure();
      }).flatMap((structureId) -> {
         return world.getStructureTemplateManager().getTemplate(structureId);
      });
   }

   public Optional getTestKey() {
      return this.data.test();
   }

   public Text getTestName() {
      return (Text)this.getTestKey().map((key) -> {
         return Text.literal(key.getValue().toString());
      }).orElse(INVALID_TEST_TEXT);
   }

   private Optional getTestEntry() {
      Optional var10000 = this.getTestKey();
      DynamicRegistryManager var10001 = this.world.getRegistryManager();
      Objects.requireNonNull(var10001);
      return var10000.flatMap(var10001::getOptionalEntry);
   }

   public boolean shouldIgnoreEntities() {
      return this.data.ignoreEntities();
   }

   public Vec3i getSize() {
      return this.data.size();
   }

   public BlockRotation getRotation() {
      return ((BlockRotation)this.getTestEntry().map(RegistryEntry::value).map(TestInstance::getRotation).orElse(BlockRotation.NONE)).rotate(this.data.rotation());
   }

   public Optional getErrorMessage() {
      return this.data.errorMessage();
   }

   public void setErrorMessage(Text errorMessage) {
      this.setData(this.data.withErrorMessage(errorMessage));
   }

   public void setFinished() {
      this.setData(this.data.withStatus(TestInstanceBlockEntity.Status.FINISHED));
      this.clearBarriers();
   }

   public void setRunning() {
      this.setData(this.data.withStatus(TestInstanceBlockEntity.Status.RUNNING));
   }

   public void markDirty() {
      super.markDirty();
      if (this.world instanceof ServerWorld) {
         this.world.updateListeners(this.getPos(), Blocks.AIR.getDefaultState(), this.getCachedState(), 3);
      }

   }

   public BlockEntityUpdateS2CPacket toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return this.createComponentlessNbt(registries);
   }

   protected void readData(ReadView view) {
      view.read("data", TestInstanceBlockEntity.Data.CODEC).ifPresent(this::setData);
   }

   protected void writeData(WriteView view) {
      view.put("data", TestInstanceBlockEntity.Data.CODEC, this.data);
   }

   public StructureBoxRendering.RenderMode getRenderMode() {
      return StructureBoxRendering.RenderMode.BOX;
   }

   public BlockPos getStructurePos() {
      return getStructurePos(this.getPos());
   }

   public static BlockPos getStructurePos(BlockPos pos) {
      return pos.add(STRUCTURE_OFFSET);
   }

   public StructureBoxRendering.StructureBox getStructureBox() {
      return new StructureBoxRendering.StructureBox(new BlockPos(STRUCTURE_OFFSET), this.getTransformedSize());
   }

   public List getBeamSegments() {
      List var10000;
      switch (this.data.status().ordinal()) {
         case 0:
            var10000 = CLEARED_BEAM_SEGMENTS;
            break;
         case 1:
            var10000 = RUNNING_BEAM_SEGMENTS;
            break;
         case 2:
            var10000 = this.getErrorMessage().isEmpty() ? SUCCESS_BEAM_SEGMENTS : ((Boolean)this.getTestEntry().map(RegistryEntry::value).map(TestInstance::isRequired).orElse(true) ? REQUIRED_FAIL_BEAM_SEGMENTS : OPTIONAL_FAIL_BEAM_SEGMENTS);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private Vec3i getTransformedSize() {
      Vec3i vec3i = this.getSize();
      BlockRotation blockRotation = this.getRotation();
      boolean bl = blockRotation == BlockRotation.CLOCKWISE_90 || blockRotation == BlockRotation.COUNTERCLOCKWISE_90;
      int i = bl ? vec3i.getZ() : vec3i.getX();
      int j = bl ? vec3i.getX() : vec3i.getZ();
      return new Vec3i(i, vec3i.getY(), j);
   }

   public void reset(Consumer messageConsumer) {
      this.clearBarriers();
      boolean bl = this.placeStructure();
      if (bl) {
         messageConsumer.accept(Text.translatable("test_instance_block.reset_success", this.getTestName()).formatted(Formatting.GREEN));
      }

      this.setData(this.data.withStatus(TestInstanceBlockEntity.Status.CLEARED));
   }

   public Optional saveStructure(Consumer messageConsumer) {
      Optional optional = this.getTestEntry();
      Optional optional2;
      if (optional.isPresent()) {
         optional2 = Optional.of(((TestInstance)((RegistryEntry.Reference)optional.get()).value()).getStructure());
      } else {
         optional2 = this.getTestKey().map(RegistryKey::getValue);
      }

      if (optional2.isEmpty()) {
         BlockPos blockPos = this.getPos();
         messageConsumer.accept(Text.translatable("test_instance_block.error.unable_to_save", blockPos.getX(), blockPos.getY(), blockPos.getZ()).formatted(Formatting.RED));
         return optional2;
      } else {
         World var5 = this.world;
         if (var5 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var5;
            StructureBlockBlockEntity.saveStructure(serverWorld, (Identifier)optional2.get(), this.getStructurePos(), this.getSize(), this.shouldIgnoreEntities(), "", true, List.of(Blocks.AIR));
         }

         return optional2;
      }
   }

   public boolean export(Consumer messageConsumer) {
      Optional optional = this.saveStructure(messageConsumer);
      if (!optional.isEmpty()) {
         World var4 = this.world;
         if (var4 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var4;
            return exportData(serverWorld, (Identifier)optional.get(), messageConsumer);
         }
      }

      return false;
   }

   public static boolean exportData(ServerWorld world, Identifier structureId, Consumer messageConsumer) {
      Path path = TestInstanceUtil.testStructuresDirectoryName;
      Path path2 = world.getStructureTemplateManager().getTemplatePath(structureId, ".nbt");
      Path path3 = NbtProvider.convertNbtToSnbt(DataWriter.UNCACHED, path2, structureId.getPath(), path.resolve(structureId.getNamespace()).resolve("structure"));
      if (path3 == null) {
         messageConsumer.accept(Text.literal("Failed to export " + String.valueOf(path2)).formatted(Formatting.RED));
         return true;
      } else {
         try {
            PathUtil.createDirectories(path3.getParent());
         } catch (IOException var7) {
            messageConsumer.accept(Text.literal("Could not create folder " + String.valueOf(path3.getParent())).formatted(Formatting.RED));
            return true;
         }

         String var10001 = String.valueOf(structureId);
         messageConsumer.accept(Text.literal("Exported " + var10001 + " to " + String.valueOf(path3.toAbsolutePath())));
         return false;
      }
   }

   public void start(Consumer messageConsumer) {
      World var3 = this.world;
      if (var3 instanceof ServerWorld serverWorld) {
         Optional optional = this.getTestEntry();
         BlockPos blockPos = this.getPos();
         if (optional.isEmpty()) {
            messageConsumer.accept(Text.translatable("test_instance_block.error.no_test", blockPos.getX(), blockPos.getY(), blockPos.getZ()).formatted(Formatting.RED));
         } else if (!this.placeStructure()) {
            messageConsumer.accept(Text.translatable("test_instance_block.error.no_test_structure", blockPos.getX(), blockPos.getY(), blockPos.getZ()).formatted(Formatting.RED));
         } else {
            TestRunContext.clearDebugMarkers(serverWorld);
            TestManager.INSTANCE.clear();
            RuntimeTestInstances.clear();
            messageConsumer.accept(Text.translatable("test_instance_block.starting", ((RegistryEntry.Reference)optional.get()).getIdAsString()));
            GameTestState gameTestState = new GameTestState((RegistryEntry.Reference)optional.get(), this.data.rotation(), serverWorld, TestAttemptConfig.once());
            gameTestState.setTestBlockPos(blockPos);
            TestRunContext testRunContext = TestRunContext.Builder.ofStates(List.of(gameTestState), serverWorld).build();
            TestCommand.start(serverWorld.getServer().getCommandSource(), testRunContext);
         }
      }
   }

   public boolean placeStructure() {
      World var2 = this.world;
      if (var2 instanceof ServerWorld serverWorld) {
         Optional optional = this.data.test().flatMap((template) -> {
            return getStructureTemplate(serverWorld, template);
         });
         if (optional.isPresent()) {
            this.placeStructure(serverWorld, (StructureTemplate)optional.get());
            return true;
         }
      }

      return false;
   }

   private void placeStructure(ServerWorld world, StructureTemplate template) {
      StructurePlacementData structurePlacementData = (new StructurePlacementData()).setRotation(this.getRotation()).setIgnoreEntities(this.data.ignoreEntities()).setUpdateNeighbors(true);
      BlockPos blockPos = this.getStartPos();
      this.setChunksForced();
      this.discardEntities();
      template.place(world, blockPos, blockPos, structurePlacementData, world.getRandom(), 818);
   }

   private void discardEntities() {
      this.world.getOtherEntities((Entity)null, this.getBox()).stream().filter((entity) -> {
         return !(entity instanceof PlayerEntity);
      }).forEach(Entity::discard);
   }

   private void setChunksForced() {
      World var2 = this.world;
      if (var2 instanceof ServerWorld serverWorld) {
         this.getBlockBox().streamChunkPos().forEach((pos) -> {
            serverWorld.setChunkForced(pos.x, pos.z, true);
         });
      }

   }

   public BlockPos getStartPos() {
      Vec3i vec3i = this.getSize();
      BlockRotation blockRotation = this.getRotation();
      BlockPos blockPos = this.getStructurePos();
      BlockPos var10000;
      switch (blockRotation) {
         case NONE:
            var10000 = blockPos;
            break;
         case CLOCKWISE_90:
            var10000 = blockPos.add(vec3i.getZ() - 1, 0, 0);
            break;
         case CLOCKWISE_180:
            var10000 = blockPos.add(vec3i.getX() - 1, 0, vec3i.getZ() - 1);
            break;
         case COUNTERCLOCKWISE_90:
            var10000 = blockPos.add(0, 0, vec3i.getX() - 1);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void placeBarriers() {
      this.forEachPos((pos) -> {
         if (!this.world.getBlockState(pos).isOf(Blocks.TEST_INSTANCE_BLOCK)) {
            this.world.setBlockState(pos, Blocks.BARRIER.getDefaultState());
         }

      });
   }

   public void clearBarriers() {
      this.forEachPos((pos) -> {
         if (this.world.getBlockState(pos).isOf(Blocks.BARRIER)) {
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
         }

      });
   }

   public void forEachPos(Consumer posConsumer) {
      Box box = this.getBox();
      boolean bl = !(Boolean)this.getTestEntry().map((entry) -> {
         return ((TestInstance)entry.value()).requiresSkyAccess();
      }).orElse(false);
      BlockPos blockPos = BlockPos.ofFloored(box.minX, box.minY, box.minZ).add(-1, -1, -1);
      BlockPos blockPos2 = BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ);
      BlockPos.stream(blockPos, blockPos2).forEach((pos) -> {
         boolean bl2 = pos.getX() == blockPos.getX() || pos.getX() == blockPos2.getX() || pos.getZ() == blockPos.getZ() || pos.getZ() == blockPos2.getZ() || pos.getY() == blockPos.getY();
         boolean bl3 = pos.getY() == blockPos2.getY();
         if (bl2 || bl3 && bl) {
            posConsumer.accept(pos);
         }

      });
   }

   // $FF: synthetic method
   public Packet toUpdatePacket() {
      return this.toUpdatePacket();
   }

   public static record Data(Optional test, Vec3i size, BlockRotation rotation, boolean ignoreEntities, Status status, Optional errorMessage) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(RegistryKey.createCodec(RegistryKeys.TEST_INSTANCE).optionalFieldOf("test").forGetter(Data::test), Vec3i.CODEC.fieldOf("size").forGetter(Data::size), BlockRotation.CODEC.fieldOf("rotation").forGetter(Data::rotation), Codec.BOOL.fieldOf("ignore_entities").forGetter(Data::ignoreEntities), TestInstanceBlockEntity.Status.CODEC.fieldOf("status").forGetter(Data::status), TextCodecs.CODEC.optionalFieldOf("error_message").forGetter(Data::errorMessage)).apply(instance, Data::new);
      });
      public static final PacketCodec PACKET_CODEC;

      public Data(Optional optional, Vec3i vec3i, BlockRotation blockRotation, boolean bl, Status status, Optional optional2) {
         this.test = optional;
         this.size = vec3i;
         this.rotation = blockRotation;
         this.ignoreEntities = bl;
         this.status = status;
         this.errorMessage = optional2;
      }

      public Data withSize(Vec3i size) {
         return new Data(this.test, size, this.rotation, this.ignoreEntities, this.status, this.errorMessage);
      }

      public Data withStatus(Status status) {
         return new Data(this.test, this.size, this.rotation, this.ignoreEntities, status, Optional.empty());
      }

      public Data withErrorMessage(Text errorMessage) {
         return new Data(this.test, this.size, this.rotation, this.ignoreEntities, TestInstanceBlockEntity.Status.FINISHED, Optional.of(errorMessage));
      }

      public Optional test() {
         return this.test;
      }

      public Vec3i size() {
         return this.size;
      }

      public BlockRotation rotation() {
         return this.rotation;
      }

      public boolean ignoreEntities() {
         return this.ignoreEntities;
      }

      public Status status() {
         return this.status;
      }

      public Optional errorMessage() {
         return this.errorMessage;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(RegistryKey.createPacketCodec(RegistryKeys.TEST_INSTANCE)), Data::test, Vec3i.PACKET_CODEC, Data::size, BlockRotation.PACKET_CODEC, Data::rotation, PacketCodecs.BOOLEAN, Data::ignoreEntities, TestInstanceBlockEntity.Status.PACKET_CODEC, Data::status, PacketCodecs.optional(TextCodecs.REGISTRY_PACKET_CODEC), Data::errorMessage, Data::new);
      }
   }

   public static enum Status implements StringIdentifiable {
      CLEARED("cleared", 0),
      RUNNING("running", 1),
      FINISHED("finished", 2);

      private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction((status) -> {
         return status.index;
      }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      public static final Codec CODEC = StringIdentifiable.createCodec(Status::values);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(Status::fromIndex, (status) -> {
         return status.index;
      });
      private final String id;
      private final int index;

      private Status(final String id, final int index) {
         this.id = id;
         this.index = index;
      }

      public String asString() {
         return this.id;
      }

      public static Status fromIndex(int index) {
         return (Status)INDEX_MAPPER.apply(index);
      }

      // $FF: synthetic method
      private static Status[] method_66777() {
         return new Status[]{CLEARED, RUNNING, FINISHED};
      }
   }
}
