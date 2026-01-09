package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.command.ArgumentGetter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.argument.RegistrySelectorArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BatchListener;
import net.minecraft.test.Batches;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.GameTestState;
import net.minecraft.test.RuntimeTestInstances;
import net.minecraft.test.TestAttemptConfig;
import net.minecraft.test.TestInstance;
import net.minecraft.test.TestInstanceBlockFinder;
import net.minecraft.test.TestInstanceFinder;
import net.minecraft.test.TestInstanceUtil;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestRunContext;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestStructurePlacer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import org.apache.commons.lang3.mutable.MutableInt;

public class TestCommand {
   public static final int field_33180 = 15;
   public static final int field_33181 = 200;
   public static final int field_53735 = 10;
   public static final int field_53736 = 100;
   private static final int field_33178 = 200;
   private static final int field_33179 = 1024;
   private static final int field_33182 = 3;
   private static final int field_33183 = 10000;
   private static final int field_33184 = 5;
   private static final int field_33185 = 5;
   private static final int field_33186 = 5;
   private static final SimpleCommandExceptionType NO_TESTS_TO_CLEAR_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.test.clear.error.no_tests"));
   private static final SimpleCommandExceptionType NO_TESTS_TO_RESET_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.test.reset.error.no_tests"));
   private static final SimpleCommandExceptionType TEST_INSTANCE_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.test.error.test_instance_not_found"));
   private static final SimpleCommandExceptionType EXPORT_STRUCTURES_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Could not find any structures to export"));
   private static final SimpleCommandExceptionType NO_TEST_INSTANCES_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.test.error.no_test_instances"));
   private static final Dynamic3CommandExceptionType NO_TEST_CONTAINING_POS_EXCEPTION = new Dynamic3CommandExceptionType((x, y, z) -> {
      return Text.stringifiedTranslatable("commands.test.error.no_test_containing_pos", x, y, z);
   });
   private static final DynamicCommandExceptionType TOO_LARGE_EXCEPTION = new DynamicCommandExceptionType((maxSize) -> {
      return Text.stringifiedTranslatable("commands.test.error.too_large", maxSize);
   });

   private static int reset(TestFinder finder) throws CommandSyntaxException {
      stop();
      int i = stream(finder.getCommandSource(), TestAttemptConfig.once(), finder).map((state) -> {
         return reset(finder.getCommandSource(), state);
      }).toList().size();
      if (i == 0) {
         throw NO_TESTS_TO_CLEAR_EXCEPTION.create();
      } else {
         finder.getCommandSource().sendFeedback(() -> {
            return Text.translatable("commands.test.reset.success", i);
         }, true);
         return i;
      }
   }

   private static int clear(TestFinder finder) throws CommandSyntaxException {
      stop();
      ServerCommandSource serverCommandSource = finder.getCommandSource();
      ServerWorld serverWorld = serverCommandSource.getWorld();
      TestRunContext.clearDebugMarkers(serverWorld);
      List list = finder.findTestPos().flatMap((pos) -> {
         return serverWorld.getBlockEntity(pos, BlockEntityType.TEST_INSTANCE_BLOCK).stream();
      }).map(TestInstanceBlockEntity::getBlockBox).toList();
      list.forEach((box) -> {
         TestInstanceUtil.clearArea(box, serverWorld);
      });
      if (list.isEmpty()) {
         throw NO_TESTS_TO_CLEAR_EXCEPTION.create();
      } else {
         serverCommandSource.sendFeedback(() -> {
            return Text.translatable("commands.test.clear.success", list.size());
         }, true);
         return list.size();
      }
   }

   private static int export(TestFinder finder) throws CommandSyntaxException {
      ServerCommandSource serverCommandSource = finder.getCommandSource();
      ServerWorld serverWorld = serverCommandSource.getWorld();
      int i = 0;
      boolean bl = true;

      for(Iterator iterator = finder.findTestPos().iterator(); iterator.hasNext(); ++i) {
         BlockPos blockPos = (BlockPos)iterator.next();
         BlockEntity var8 = serverWorld.getBlockEntity(blockPos);
         if (!(var8 instanceof TestInstanceBlockEntity)) {
            throw TEST_INSTANCE_NOT_FOUND_EXCEPTION.create();
         }

         TestInstanceBlockEntity testInstanceBlockEntity = (TestInstanceBlockEntity)var8;
         Objects.requireNonNull(serverCommandSource);
         if (!testInstanceBlockEntity.export(serverCommandSource::sendMessage)) {
            bl = false;
         }
      }

      if (i == 0) {
         throw EXPORT_STRUCTURES_NOT_FOUND_EXCEPTION.create();
      } else {
         String string = "Exported " + i + " structures";
         finder.getCommandSource().sendFeedback(() -> {
            return Text.literal(string);
         }, true);
         return bl ? 0 : 1;
      }
   }

   private static int start(TestFinder finder) {
      stop();
      ServerCommandSource serverCommandSource = finder.getCommandSource();
      ServerWorld serverWorld = serverCommandSource.getWorld();
      BlockPos blockPos = getStructurePos(serverCommandSource);
      Collection collection = Stream.concat(stream(serverCommandSource, TestAttemptConfig.once(), finder), stream(serverCommandSource, TestAttemptConfig.once(), finder, 0)).toList();
      TestRunContext.clearDebugMarkers(serverWorld);
      RuntimeTestInstances.clear();
      Collection collection2 = new ArrayList();
      Iterator var6 = collection.iterator();

      while(var6.hasNext()) {
         GameTestState gameTestState = (GameTestState)var6.next();
         BlockRotation[] var8 = BlockRotation.values();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            BlockRotation blockRotation = var8[var10];
            Collection collection3 = new ArrayList();

            for(int i = 0; i < 100; ++i) {
               GameTestState gameTestState2 = new GameTestState(gameTestState.getInstanceEntry(), blockRotation, serverWorld, new TestAttemptConfig(1, true));
               gameTestState2.setTestBlockPos(gameTestState.getPos());
               collection3.add(gameTestState2);
            }

            GameTestBatch gameTestBatch = Batches.create(collection3, gameTestState.getInstance().getEnvironment(), blockRotation.ordinal());
            collection2.add(gameTestBatch);
         }
      }

      TestStructurePlacer testStructurePlacer = new TestStructurePlacer(blockPos, 10, true);
      TestRunContext testRunContext = TestRunContext.Builder.of(collection2, serverWorld).batcher(Batches.batcher(100)).initialSpawner(testStructurePlacer).reuseSpawner(testStructurePlacer).stopAfterFailure(true).build();
      return start(serverCommandSource, testRunContext);
   }

   private static int start(TestFinder finder, TestAttemptConfig config, int rotationSteps, int testsPerRow) {
      stop();
      ServerCommandSource serverCommandSource = finder.getCommandSource();
      ServerWorld serverWorld = serverCommandSource.getWorld();
      BlockPos blockPos = getStructurePos(serverCommandSource);
      Collection collection = Stream.concat(stream(serverCommandSource, config, finder), stream(serverCommandSource, config, finder, rotationSteps)).toList();
      if (collection.isEmpty()) {
         serverCommandSource.sendFeedback(() -> {
            return Text.translatable("commands.test.no_tests");
         }, false);
         return 0;
      } else {
         TestRunContext.clearDebugMarkers(serverWorld);
         RuntimeTestInstances.clear();
         serverCommandSource.sendFeedback(() -> {
            return Text.translatable("commands.test.run.running", collection.size());
         }, false);
         TestRunContext testRunContext = TestRunContext.Builder.ofStates(collection, serverWorld).initialSpawner(new TestStructurePlacer(blockPos, testsPerRow, false)).build();
         return start(serverCommandSource, testRunContext);
      }
   }

   private static int locate(TestFinder finder) throws CommandSyntaxException {
      finder.getCommandSource().sendMessage(Text.translatable("commands.test.locate.started"));
      MutableInt mutableInt = new MutableInt(0);
      BlockPos blockPos = BlockPos.ofFloored(finder.getCommandSource().getPosition());
      finder.findTestPos().forEach((pos) -> {
         BlockEntity blockEntity = finder.getCommandSource().getWorld().getBlockEntity(pos);
         if (blockEntity instanceof TestInstanceBlockEntity testInstanceBlockEntity) {
            Direction direction = testInstanceBlockEntity.getRotation().rotate(Direction.NORTH);
            BlockPos blockPos2 = testInstanceBlockEntity.getPos().offset((Direction)direction, 2);
            int i = (int)direction.getOpposite().getPositiveHorizontalDegrees();
            String string = String.format(Locale.ROOT, "/tp @s %d %d %d %d 0", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), i);
            int j = blockPos.getX() - pos.getX();
            int k = blockPos.getZ() - pos.getZ();
            int l = MathHelper.floor(MathHelper.sqrt((float)(j * j + k * k)));
            MutableText text = Texts.bracketed(Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ())).styled((style) -> {
               return style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent.SuggestCommand(string)).withHoverEvent(new HoverEvent.ShowText(Text.translatable("chat.coordinates.tooltip")));
            });
            finder.getCommandSource().sendFeedback(() -> {
               return Text.translatable("commands.test.locate.found", text, l);
            }, false);
            mutableInt.increment();
         }
      });
      int i = mutableInt.intValue();
      if (i == 0) {
         throw NO_TEST_INSTANCES_EXCEPTION.create();
      } else {
         finder.getCommandSource().sendFeedback(() -> {
            return Text.translatable("commands.test.locate.done", i);
         }, true);
         return i;
      }
   }

   private static ArgumentBuilder testAttemptConfig(ArgumentBuilder builder, ArgumentGetter finderGetter, Function extraConfigAdder) {
      return builder.executes((context) -> {
         return start((TestFinder)finderGetter.apply(context), TestAttemptConfig.once(), 0, 8);
      }).then(((RequiredArgumentBuilder)CommandManager.argument("numberOfTimes", IntegerArgumentType.integer(0)).executes((context) -> {
         return start((TestFinder)finderGetter.apply(context), new TestAttemptConfig(IntegerArgumentType.getInteger(context, "numberOfTimes"), false), 0, 8);
      })).then((ArgumentBuilder)extraConfigAdder.apply(CommandManager.argument("untilFailed", BoolArgumentType.bool()).executes((context) -> {
         return start((TestFinder)finderGetter.apply(context), new TestAttemptConfig(IntegerArgumentType.getInteger(context, "numberOfTimes"), BoolArgumentType.getBool(context, "untilFailed")), 0, 8);
      }))));
   }

   private static ArgumentBuilder testAttemptConfig(ArgumentBuilder builder, ArgumentGetter finderGetter) {
      return testAttemptConfig(builder, finderGetter, (extraConfigAdder) -> {
         return extraConfigAdder;
      });
   }

   private static ArgumentBuilder testAttemptAndPlacementConfig(ArgumentBuilder builder, ArgumentGetter finderGetter) {
      return testAttemptConfig(builder, finderGetter, (builder2) -> {
         return builder2.then(((RequiredArgumentBuilder)CommandManager.argument("rotationSteps", IntegerArgumentType.integer()).executes((context) -> {
            return start((TestFinder)finderGetter.apply(context), new TestAttemptConfig(IntegerArgumentType.getInteger(context, "numberOfTimes"), BoolArgumentType.getBool(context, "untilFailed")), IntegerArgumentType.getInteger(context, "rotationSteps"), 8);
         })).then(CommandManager.argument("testsPerRow", IntegerArgumentType.integer()).executes((context) -> {
            return start((TestFinder)finderGetter.apply(context), new TestAttemptConfig(IntegerArgumentType.getInteger(context, "numberOfTimes"), BoolArgumentType.getBool(context, "untilFailed")), IntegerArgumentType.getInteger(context, "rotationSteps"), IntegerArgumentType.getInteger(context, "testsPerRow"));
         })));
      });
   }

   public static void register(CommandDispatcher dispatcher, CommandRegistryAccess registryAccess) {
      ArgumentBuilder argumentBuilder = testAttemptAndPlacementConfig(CommandManager.argument("onlyRequiredTests", BoolArgumentType.bool()), (context) -> {
         return TestFinder.builder().failed(context, BoolArgumentType.getBool(context, "onlyRequiredTests"));
      });
      LiteralArgumentBuilder var10000 = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("test").requires(CommandManager.requirePermissionLevel(2))).then(CommandManager.literal("run").then(testAttemptAndPlacementConfig(CommandManager.argument("tests", RegistrySelectorArgumentType.selector(registryAccess, RegistryKeys.TEST_INSTANCE)), (context) -> {
         return TestFinder.builder().selector(context, RegistrySelectorArgumentType.getEntries(context, "tests"));
      })))).then(CommandManager.literal("runmultiple").then(((RequiredArgumentBuilder)CommandManager.argument("tests", RegistrySelectorArgumentType.selector(registryAccess, RegistryKeys.TEST_INSTANCE)).executes((context) -> {
         return start(TestFinder.builder().selector(context, RegistrySelectorArgumentType.getEntries(context, "tests")), TestAttemptConfig.once(), 0, 8);
      })).then(CommandManager.argument("amount", IntegerArgumentType.integer()).executes((context) -> {
         return start(TestFinder.builder().repeat(IntegerArgumentType.getInteger(context, "amount")).selector(context, RegistrySelectorArgumentType.getEntries(context, "tests")), TestAttemptConfig.once(), 0, 8);
      }))));
      LiteralArgumentBuilder var10001 = CommandManager.literal("runthese");
      TestFinder.Builder var10002 = TestFinder.builder();
      Objects.requireNonNull(var10002);
      var10000 = (LiteralArgumentBuilder)var10000.then(testAttemptConfig(var10001, var10002::allStructures));
      var10001 = CommandManager.literal("runclosest");
      var10002 = TestFinder.builder();
      Objects.requireNonNull(var10002);
      var10000 = (LiteralArgumentBuilder)var10000.then(testAttemptConfig(var10001, var10002::nearest));
      var10001 = CommandManager.literal("runthat");
      var10002 = TestFinder.builder();
      Objects.requireNonNull(var10002);
      var10000 = (LiteralArgumentBuilder)var10000.then(testAttemptConfig(var10001, var10002::targeted));
      ArgumentBuilder var4 = CommandManager.literal("runfailed").then(argumentBuilder);
      var10002 = TestFinder.builder();
      Objects.requireNonNull(var10002);
      LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)var10000.then(testAttemptAndPlacementConfig(var4, var10002::failed))).then(CommandManager.literal("verify").then(CommandManager.argument("tests", RegistrySelectorArgumentType.selector(registryAccess, RegistryKeys.TEST_INSTANCE)).executes((context) -> {
         return start(TestFinder.builder().selector(context, RegistrySelectorArgumentType.getEntries(context, "tests")));
      })))).then(CommandManager.literal("locate").then(CommandManager.argument("tests", RegistrySelectorArgumentType.selector(registryAccess, RegistryKeys.TEST_INSTANCE)).executes((context) -> {
         return locate(TestFinder.builder().selector(context, RegistrySelectorArgumentType.getEntries(context, "tests")));
      })))).then(CommandManager.literal("resetclosest").executes((context) -> {
         return reset(TestFinder.builder().nearest(context));
      }))).then(CommandManager.literal("resetthese").executes((context) -> {
         return reset(TestFinder.builder().allStructures(context));
      }))).then(CommandManager.literal("resetthat").executes((context) -> {
         return reset(TestFinder.builder().targeted(context));
      }))).then(CommandManager.literal("clearthat").executes((context) -> {
         return clear(TestFinder.builder().targeted(context));
      }))).then(CommandManager.literal("clearthese").executes((context) -> {
         return clear(TestFinder.builder().allStructures(context));
      }))).then(((LiteralArgumentBuilder)CommandManager.literal("clearall").executes((context) -> {
         return clear(TestFinder.builder().surface(context, 200));
      })).then(CommandManager.argument("radius", IntegerArgumentType.integer()).executes((context) -> {
         return clear(TestFinder.builder().surface(context, MathHelper.clamp(IntegerArgumentType.getInteger(context, "radius"), 0, 1024)));
      })))).then(CommandManager.literal("stop").executes((context) -> {
         return stop();
      }))).then(((LiteralArgumentBuilder)CommandManager.literal("pos").executes((context) -> {
         return executePos((ServerCommandSource)context.getSource(), "pos");
      })).then(CommandManager.argument("var", StringArgumentType.word()).executes((context) -> {
         return executePos((ServerCommandSource)context.getSource(), StringArgumentType.getString(context, "var"));
      })))).then(CommandManager.literal("create").then(((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(TestCommand::suggestTestFunctions).executes((context) -> {
         return executeCreate((ServerCommandSource)context.getSource(), IdentifierArgumentType.getIdentifier(context, "id"), 5, 5, 5);
      })).then(((RequiredArgumentBuilder)CommandManager.argument("width", IntegerArgumentType.integer()).executes((context) -> {
         return executeCreate((ServerCommandSource)context.getSource(), IdentifierArgumentType.getIdentifier(context, "id"), IntegerArgumentType.getInteger(context, "width"), IntegerArgumentType.getInteger(context, "width"), IntegerArgumentType.getInteger(context, "width"));
      })).then(CommandManager.argument("height", IntegerArgumentType.integer()).then(CommandManager.argument("depth", IntegerArgumentType.integer()).executes((context) -> {
         return executeCreate((ServerCommandSource)context.getSource(), IdentifierArgumentType.getIdentifier(context, "id"), IntegerArgumentType.getInteger(context, "width"), IntegerArgumentType.getInteger(context, "height"), IntegerArgumentType.getInteger(context, "depth"));
      }))))));
      if (SharedConstants.isDevelopment) {
         literalArgumentBuilder = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(CommandManager.literal("export").then(CommandManager.argument("test", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.TEST_INSTANCE)).executes((context) -> {
            return executeExport((ServerCommandSource)context.getSource(), RegistryEntryReferenceArgumentType.getRegistryEntry(context, "test", RegistryKeys.TEST_INSTANCE));
         })))).then(CommandManager.literal("exportclosest").executes((context) -> {
            return export(TestFinder.builder().nearest(context));
         }))).then(CommandManager.literal("exportthese").executes((context) -> {
            return export(TestFinder.builder().allStructures(context));
         }))).then(CommandManager.literal("exportthat").executes((context) -> {
            return export(TestFinder.builder().targeted(context));
         }));
      }

      dispatcher.register(literalArgumentBuilder);
   }

   public static CompletableFuture suggestTestFunctions(CommandContext context, SuggestionsBuilder builder) {
      Stream stream = ((ServerCommandSource)context.getSource()).getRegistryManager().getOrThrow(RegistryKeys.TEST_FUNCTION).streamEntries().map(RegistryEntry::getIdAsString);
      return CommandSource.suggestMatching(stream, builder);
   }

   private static int reset(ServerCommandSource source, GameTestState state) {
      TestInstanceBlockEntity testInstanceBlockEntity = state.getTestInstanceBlockEntity();
      Objects.requireNonNull(source);
      testInstanceBlockEntity.reset(source::sendMessage);
      return 1;
   }

   private static Stream stream(ServerCommandSource source, TestAttemptConfig config, TestInstanceBlockFinder finder) {
      return finder.findTestPos().map((pos) -> {
         return find(pos, source, config);
      }).flatMap(Optional::stream);
   }

   private static Stream stream(ServerCommandSource source, TestAttemptConfig config, TestInstanceFinder finder, int rotationSteps) {
      return finder.findTests().filter((instance) -> {
         return checkStructure(source, ((TestInstance)instance.value()).getStructure());
      }).map((instance) -> {
         return new GameTestState(instance, TestInstanceUtil.getRotation(rotationSteps), source.getWorld(), config);
      });
   }

   private static Optional find(BlockPos pos, ServerCommandSource source, TestAttemptConfig config) {
      ServerWorld serverWorld = source.getWorld();
      BlockEntity var5 = serverWorld.getBlockEntity(pos);
      if (var5 instanceof TestInstanceBlockEntity testInstanceBlockEntity) {
         Optional var10000 = testInstanceBlockEntity.getTestKey();
         Registry var10001 = source.getRegistryManager().getOrThrow(RegistryKeys.TEST_INSTANCE);
         Objects.requireNonNull(var10001);
         Optional optional = var10000.flatMap(var10001::getOptional);
         if (optional.isEmpty()) {
            source.sendError(Text.translatable("commands.test.error.non_existant_test", testInstanceBlockEntity.getTestName()));
            return Optional.empty();
         } else {
            RegistryEntry.Reference reference = (RegistryEntry.Reference)optional.get();
            GameTestState gameTestState = new GameTestState(reference, testInstanceBlockEntity.getRotation(), serverWorld, config);
            gameTestState.setTestBlockPos(pos);
            return !checkStructure(source, gameTestState.getStructure()) ? Optional.empty() : Optional.of(gameTestState);
         }
      } else {
         source.sendError(Text.translatable("commands.test.error.test_instance_not_found.position", pos.getX(), pos.getY(), pos.getZ()));
         return Optional.empty();
      }
   }

   private static int executeCreate(ServerCommandSource source, Identifier id, int x, int y, int z) throws CommandSyntaxException {
      if (x <= 48 && y <= 48 && z <= 48) {
         ServerWorld serverWorld = source.getWorld();
         BlockPos blockPos = getStructurePos(source);
         TestInstanceBlockEntity testInstanceBlockEntity = TestInstanceUtil.createTestInstanceBlockEntity(id, blockPos, new Vec3i(x, y, z), BlockRotation.NONE, serverWorld);
         BlockPos blockPos2 = testInstanceBlockEntity.getStructurePos();
         BlockPos blockPos3 = blockPos2.add(x - 1, 0, z - 1);
         BlockPos.stream(blockPos2, blockPos3).forEach((pos) -> {
            serverWorld.setBlockState(pos, Blocks.BEDROCK.getDefaultState());
         });
         source.sendFeedback(() -> {
            return Text.translatable("commands.test.create.success", testInstanceBlockEntity.getTestName());
         }, true);
         return 1;
      } else {
         throw TOO_LARGE_EXCEPTION.create(48);
      }
   }

   private static int executePos(ServerCommandSource source, String variableName) throws CommandSyntaxException {
      BlockHitResult blockHitResult = (BlockHitResult)source.getPlayerOrThrow().raycast(10.0, 1.0F, false);
      BlockPos blockPos = blockHitResult.getBlockPos();
      ServerWorld serverWorld = source.getWorld();
      Optional optional = TestInstanceUtil.findContainingTestInstanceBlock(blockPos, 15, serverWorld);
      if (optional.isEmpty()) {
         optional = TestInstanceUtil.findContainingTestInstanceBlock(blockPos, 200, serverWorld);
      }

      if (optional.isEmpty()) {
         throw NO_TEST_CONTAINING_POS_EXCEPTION.create(blockPos.getX(), blockPos.getY(), blockPos.getZ());
      } else {
         BlockEntity var7 = serverWorld.getBlockEntity((BlockPos)optional.get());
         if (var7 instanceof TestInstanceBlockEntity) {
            TestInstanceBlockEntity testInstanceBlockEntity = (TestInstanceBlockEntity)var7;
            BlockPos blockPos2 = testInstanceBlockEntity.getStructurePos();
            BlockPos blockPos3 = blockPos.subtract(blockPos2);
            int var10000 = blockPos3.getX();
            String string = "" + var10000 + ", " + blockPos3.getY() + ", " + blockPos3.getZ();
            String string2 = testInstanceBlockEntity.getTestName().getString();
            MutableText text = Text.translatable("commands.test.coordinates", blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()).setStyle(Style.EMPTY.withBold(true).withColor(Formatting.GREEN).withHoverEvent(new HoverEvent.ShowText(Text.translatable("commands.test.coordinates.copy"))).withClickEvent(new ClickEvent.CopyToClipboard("final BlockPos " + variableName + " = new BlockPos(" + string + ");")));
            source.sendFeedback(() -> {
               return Text.translatable("commands.test.relative_position", string2, text);
            }, false);
            DebugInfoSender.addGameTestMarker(serverWorld, new BlockPos(blockPos), string, -2147418368, 10000);
            return 1;
         } else {
            throw TEST_INSTANCE_NOT_FOUND_EXCEPTION.create();
         }
      }
   }

   private static int stop() {
      TestManager.INSTANCE.clear();
      return 1;
   }

   public static int start(ServerCommandSource source, TestRunContext context) {
      context.addBatchListener(new ReportingBatchListener(source));
      TestSet testSet = new TestSet(context.getStates());
      testSet.addListener((TestListener)(new Listener(source, testSet)));
      testSet.addListener((state) -> {
         RuntimeTestInstances.add(state.getInstanceEntry());
      });
      context.start();
      return 1;
   }

   private static int executeExport(ServerCommandSource source, RegistryEntry instance) {
      ServerWorld var10000 = source.getWorld();
      Identifier var10001 = ((TestInstance)instance.value()).getStructure();
      Objects.requireNonNull(source);
      return !TestInstanceBlockEntity.exportData(var10000, var10001, source::sendMessage) ? 0 : 1;
   }

   private static boolean checkStructure(ServerCommandSource source, Identifier templateId) {
      if (source.getWorld().getStructureTemplateManager().getTemplate(templateId).isEmpty()) {
         source.sendError(Text.translatable("commands.test.error.structure_not_found", Text.of(templateId)));
         return false;
      } else {
         return true;
      }
   }

   private static BlockPos getStructurePos(ServerCommandSource source) {
      BlockPos blockPos = BlockPos.ofFloored(source.getPosition());
      int i = source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY();
      return new BlockPos(blockPos.getX(), i, blockPos.getZ() + 3);
   }

   static record ReportingBatchListener(ServerCommandSource source) implements BatchListener {
      ReportingBatchListener(ServerCommandSource serverCommandSource) {
         this.source = serverCommandSource;
      }

      public void onStarted(GameTestBatch batch) {
         this.source.sendFeedback(() -> {
            return Text.translatable("commands.test.batch.starting", batch.environment().getIdAsString(), batch.index());
         }, true);
      }

      public void onFinished(GameTestBatch batch) {
      }

      public ServerCommandSource source() {
         return this.source;
      }
   }

   public static record Listener(ServerCommandSource source, TestSet tests) implements TestListener {
      public Listener(ServerCommandSource serverCommandSource, TestSet tests) {
         this.source = serverCommandSource;
         this.tests = tests;
      }

      public void onStarted(GameTestState test) {
      }

      public void onPassed(GameTestState test, TestRunContext context) {
         this.onFinished();
      }

      public void onFailed(GameTestState test, TestRunContext context) {
         this.onFinished();
      }

      public void onRetry(GameTestState lastState, GameTestState nextState, TestRunContext context) {
         this.tests.add(nextState);
      }

      private void onFinished() {
         if (this.tests.isDone()) {
            this.source.sendFeedback(() -> {
               return Text.translatable("commands.test.summary", this.tests.getTestCount()).formatted(Formatting.WHITE);
            }, true);
            if (this.tests.failed()) {
               this.source.sendError(Text.translatable("commands.test.summary.failed", this.tests.getFailedRequiredTestCount()));
            } else {
               this.source.sendFeedback(() -> {
                  return Text.translatable("commands.test.summary.all_required_passed").formatted(Formatting.GREEN);
               }, true);
            }

            if (this.tests.hasFailedOptionalTests()) {
               this.source.sendMessage(Text.translatable("commands.test.summary.optional_failed", this.tests.getFailedOptionalTestCount()));
            }
         }

      }

      public ServerCommandSource source() {
         return this.source;
      }

      public TestSet tests() {
         return this.tests;
      }
   }
}
