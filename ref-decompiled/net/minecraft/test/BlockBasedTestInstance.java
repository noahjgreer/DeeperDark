package net.minecraft.test;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TestBlock;
import net.minecraft.block.entity.TestBlockEntity;
import net.minecraft.block.enums.TestBlockMode;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class BlockBasedTestInstance extends TestInstance {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(TestData.CODEC.forGetter(TestInstance::getData)).apply(instance, BlockBasedTestInstance::new);
   });

   public BlockBasedTestInstance(TestData testData) {
      super(testData);
   }

   public void start(TestContext context) {
      BlockPos blockPos = this.findStartBlockPos(context);
      TestBlockEntity testBlockEntity = (TestBlockEntity)context.getBlockEntity(blockPos, TestBlockEntity.class);
      testBlockEntity.trigger();
      context.forEachRemainingTick(() -> {
         List list = this.findTestBlocks(context, TestBlockMode.ACCEPT);
         if (list.isEmpty()) {
            context.throwGameTestException(Text.translatable("test_block.error.missing", TestBlockMode.ACCEPT.getName()));
         }

         boolean bl = list.stream().map((pos) -> {
            return (TestBlockEntity)context.getBlockEntity(pos, TestBlockEntity.class);
         }).anyMatch(TestBlockEntity::hasTriggered);
         if (bl) {
            context.complete();
         } else {
            this.handleTrigger(context, TestBlockMode.FAIL, (testBlockEntity) -> {
               context.throwGameTestException(Text.literal(testBlockEntity.getMessage()));
            });
            this.handleTrigger(context, TestBlockMode.LOG, TestBlockEntity::trigger);
         }

      });
   }

   private void handleTrigger(TestContext context, TestBlockMode mode, Consumer callback) {
      List list = this.findTestBlocks(context, mode);
      Iterator var5 = list.iterator();

      while(var5.hasNext()) {
         BlockPos blockPos = (BlockPos)var5.next();
         TestBlockEntity testBlockEntity = (TestBlockEntity)context.getBlockEntity(blockPos, TestBlockEntity.class);
         if (testBlockEntity.hasTriggered()) {
            callback.accept(testBlockEntity);
            testBlockEntity.reset();
         }
      }

   }

   private BlockPos findStartBlockPos(TestContext context) {
      List list = this.findTestBlocks(context, TestBlockMode.START);
      if (list.isEmpty()) {
         context.throwGameTestException(Text.translatable("test_block.error.missing", TestBlockMode.START.getName()));
      }

      if (list.size() != 1) {
         context.throwGameTestException(Text.translatable("test_block.error.too_many", TestBlockMode.START.getName()));
      }

      return (BlockPos)list.getFirst();
   }

   private List findTestBlocks(TestContext context, TestBlockMode mode) {
      List list = new ArrayList();
      context.forEachRelativePos((pos) -> {
         BlockState blockState = context.getBlockState(pos);
         if (blockState.isOf(Blocks.TEST_BLOCK) && blockState.get(TestBlock.MODE) == mode) {
            list.add(pos.toImmutable());
         }

      });
      return list;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   protected MutableText getTypeDescription() {
      return Text.translatable("test_instance.type.block_based");
   }
}
