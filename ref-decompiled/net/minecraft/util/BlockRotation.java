package net.minecraft.util;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.random.Random;

public enum BlockRotation implements StringIdentifiable {
   NONE(0, "none", DirectionTransformation.IDENTITY),
   CLOCKWISE_90(1, "clockwise_90", DirectionTransformation.ROT_90_Y_NEG),
   CLOCKWISE_180(2, "180", DirectionTransformation.ROT_180_FACE_XZ),
   COUNTERCLOCKWISE_90(3, "counterclockwise_90", DirectionTransformation.ROT_90_Y_POS);

   public static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction(BlockRotation::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.WRAP);
   public static final Codec CODEC = StringIdentifiable.createCodec(BlockRotation::values);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, BlockRotation::getIndex);
   /** @deprecated */
   @Deprecated
   public static final Codec ENUM_NAME_CODEC = Codecs.enumByName(BlockRotation::valueOf);
   private final int index;
   private final String id;
   private final DirectionTransformation directionTransformation;

   private BlockRotation(final int index, final String id, final DirectionTransformation directionTransformation) {
      this.index = index;
      this.id = id;
      this.directionTransformation = directionTransformation;
   }

   public BlockRotation rotate(BlockRotation rotation) {
      BlockRotation var10000;
      switch (rotation.ordinal()) {
         case 1:
            switch (this.ordinal()) {
               case 0:
                  var10000 = CLOCKWISE_90;
                  return var10000;
               case 1:
                  var10000 = CLOCKWISE_180;
                  return var10000;
               case 2:
                  var10000 = COUNTERCLOCKWISE_90;
                  return var10000;
               case 3:
                  var10000 = NONE;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case 2:
            switch (this.ordinal()) {
               case 0:
                  var10000 = CLOCKWISE_180;
                  return var10000;
               case 1:
                  var10000 = COUNTERCLOCKWISE_90;
                  return var10000;
               case 2:
                  var10000 = NONE;
                  return var10000;
               case 3:
                  var10000 = CLOCKWISE_90;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case 3:
            switch (this.ordinal()) {
               case 0:
                  var10000 = COUNTERCLOCKWISE_90;
                  return var10000;
               case 1:
                  var10000 = NONE;
                  return var10000;
               case 2:
                  var10000 = CLOCKWISE_90;
                  return var10000;
               case 3:
                  var10000 = CLOCKWISE_180;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         default:
            var10000 = this;
            return var10000;
      }
   }

   public DirectionTransformation getDirectionTransformation() {
      return this.directionTransformation;
   }

   public Direction rotate(Direction direction) {
      if (direction.getAxis() == Direction.Axis.Y) {
         return direction;
      } else {
         Direction var10000;
         switch (this.ordinal()) {
            case 1:
               var10000 = direction.rotateYClockwise();
               break;
            case 2:
               var10000 = direction.getOpposite();
               break;
            case 3:
               var10000 = direction.rotateYCounterclockwise();
               break;
            default:
               var10000 = direction;
         }

         return var10000;
      }
   }

   public int rotate(int rotation, int fullTurn) {
      int var10000;
      switch (this.ordinal()) {
         case 1:
            var10000 = (rotation + fullTurn / 4) % fullTurn;
            break;
         case 2:
            var10000 = (rotation + fullTurn / 2) % fullTurn;
            break;
         case 3:
            var10000 = (rotation + fullTurn * 3 / 4) % fullTurn;
            break;
         default:
            var10000 = rotation;
      }

      return var10000;
   }

   public static BlockRotation random(Random random) {
      return (BlockRotation)Util.getRandom((Object[])values(), random);
   }

   public static List randomRotationOrder(Random random) {
      return Util.copyShuffled((Object[])values(), random);
   }

   public String asString() {
      return this.id;
   }

   private int getIndex() {
      return this.index;
   }

   // $FF: synthetic method
   private static BlockRotation[] method_36709() {
      return new BlockRotation[]{NONE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90};
   }
}
