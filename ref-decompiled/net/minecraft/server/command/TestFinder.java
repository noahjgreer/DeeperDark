package net.minecraft.server.command;

import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.test.RuntimeTestInstances;
import net.minecraft.test.TestInstance;
import net.minecraft.test.TestInstanceBlockFinder;
import net.minecraft.test.TestInstanceFinder;
import net.minecraft.test.TestInstanceUtil;
import net.minecraft.util.math.BlockPos;

public class TestFinder implements TestInstanceFinder, TestInstanceBlockFinder {
   static final TestInstanceFinder NOOP_TEST_FUNCTION_FINDER = Stream::empty;
   static final TestInstanceBlockFinder NOOP_TEST_INSTANCE_BLOCK_FINDER = Stream::empty;
   private final TestInstanceFinder instanceFinder;
   private final TestInstanceBlockFinder blockFinder;
   private final ServerCommandSource commandSource;

   public Stream findTestPos() {
      return this.blockFinder.findTestPos();
   }

   public static Builder builder() {
      return new Builder();
   }

   TestFinder(ServerCommandSource commandSource, TestInstanceFinder instanceFinder, TestInstanceBlockFinder blockFinder) {
      this.commandSource = commandSource;
      this.instanceFinder = instanceFinder;
      this.blockFinder = blockFinder;
   }

   public ServerCommandSource getCommandSource() {
      return this.commandSource;
   }

   public Stream findTests() {
      return this.instanceFinder.findTests();
   }

   public static class Builder {
      private final UnaryOperator testInstanceFinderMapper;
      private final UnaryOperator testInstanceBlockFinderMapper;

      public Builder() {
         this.testInstanceFinderMapper = (finder) -> {
            return finder;
         };
         this.testInstanceBlockFinderMapper = (finder) -> {
            return finder;
         };
      }

      private Builder(UnaryOperator testInstanceFinderMapper, UnaryOperator testInstanceBlockFinderMapper) {
         this.testInstanceFinderMapper = testInstanceFinderMapper;
         this.testInstanceBlockFinderMapper = testInstanceBlockFinderMapper;
      }

      public Builder repeat(int count) {
         return new Builder(repeating(count), repeating(count));
      }

      private static UnaryOperator repeating(int count) {
         return (supplier) -> {
            List list = new LinkedList();
            List list2 = ((Stream)supplier.get()).toList();

            for(int j = 0; j < count; ++j) {
               list.addAll(list2);
            }

            Objects.requireNonNull(list);
            return list::stream;
         };
      }

      private TestFinder build(ServerCommandSource source, TestInstanceFinder instanceFinder, TestInstanceBlockFinder blockFinder) {
         UnaryOperator var10003 = this.testInstanceFinderMapper;
         Objects.requireNonNull(instanceFinder);
         Supplier var4 = (Supplier)var10003.apply(instanceFinder::findTests);
         Objects.requireNonNull(var4);
         TestInstanceFinder var5 = var4::get;
         UnaryOperator var10004 = this.testInstanceBlockFinderMapper;
         Objects.requireNonNull(blockFinder);
         Supplier var6 = (Supplier)var10004.apply(blockFinder::findTestPos);
         Objects.requireNonNull(var6);
         return new TestFinder(source, var5, var6::get);
      }

      public TestFinder surface(CommandContext context, int radius) {
         ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
         BlockPos blockPos = BlockPos.ofFloored(serverCommandSource.getPosition());
         return this.build(serverCommandSource, TestFinder.NOOP_TEST_FUNCTION_FINDER, () -> {
            return TestInstanceUtil.findTestInstanceBlocks(blockPos, radius, serverCommandSource.getWorld());
         });
      }

      public TestFinder nearest(CommandContext context) {
         ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
         BlockPos blockPos = BlockPos.ofFloored(serverCommandSource.getPosition());
         return this.build(serverCommandSource, TestFinder.NOOP_TEST_FUNCTION_FINDER, () -> {
            return TestInstanceUtil.findNearestTestInstanceBlock(blockPos, 15, serverCommandSource.getWorld()).stream();
         });
      }

      public TestFinder allStructures(CommandContext context) {
         ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
         BlockPos blockPos = BlockPos.ofFloored(serverCommandSource.getPosition());
         return this.build(serverCommandSource, TestFinder.NOOP_TEST_FUNCTION_FINDER, () -> {
            return TestInstanceUtil.findTestInstanceBlocks(blockPos, 200, serverCommandSource.getWorld());
         });
      }

      public TestFinder targeted(CommandContext context) {
         ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
         return this.build(serverCommandSource, TestFinder.NOOP_TEST_FUNCTION_FINDER, () -> {
            return TestInstanceUtil.findTargetedTestInstanceBlock(BlockPos.ofFloored(serverCommandSource.getPosition()), serverCommandSource.getPlayer().getCameraEntity(), serverCommandSource.getWorld());
         });
      }

      public TestFinder failed(CommandContext context, boolean onlyRequired) {
         return this.build((ServerCommandSource)context.getSource(), () -> {
            return RuntimeTestInstances.stream().filter((instance) -> {
               return !onlyRequired || ((TestInstance)instance.value()).isRequired();
            });
         }, TestFinder.NOOP_TEST_INSTANCE_BLOCK_FINDER);
      }

      public TestFinder selector(CommandContext context, Collection selected) {
         ServerCommandSource var10001 = (ServerCommandSource)context.getSource();
         Objects.requireNonNull(selected);
         return this.build(var10001, selected::stream, TestFinder.NOOP_TEST_INSTANCE_BLOCK_FINDER);
      }

      public TestFinder failed(CommandContext context) {
         return this.failed(context, false);
      }
   }
}
