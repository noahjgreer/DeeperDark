package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SkullBlock extends AbstractSkullBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(SkullBlock.SkullType.CODEC.fieldOf("kind").forGetter(AbstractSkullBlock::getSkullType), createSettingsCodec()).apply(instance, SkullBlock::new);
   });
   public static final int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
   private static final int MAX_ROTATIONS;
   public static final IntProperty ROTATION;
   private static final VoxelShape SHAPE;
   private static final VoxelShape PIGLIN_SHAPE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public SkullBlock(SkullType skullType, AbstractBlock.Settings settings) {
      super(skullType, settings);
      this.setDefaultState((BlockState)this.getDefaultState().with(ROTATION, 0));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return this.getSkullType() == SkullBlock.Type.PIGLIN ? PIGLIN_SHAPE : SHAPE;
   }

   protected VoxelShape getCullingShape(BlockState state) {
      return VoxelShapes.empty();
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)super.getPlacementState(ctx).with(ROTATION, RotationPropertyHelper.fromYaw(ctx.getPlayerYaw()));
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(ROTATION, rotation.rotate((Integer)state.get(ROTATION), MAX_ROTATIONS));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return (BlockState)state.with(ROTATION, mirror.mirror((Integer)state.get(ROTATION), MAX_ROTATIONS));
   }

   protected void appendProperties(StateManager.Builder builder) {
      super.appendProperties(builder);
      builder.add(ROTATION);
   }

   static {
      MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
      ROTATION = Properties.ROTATION;
      SHAPE = Block.createColumnShape(8.0, 0.0, 8.0);
      PIGLIN_SHAPE = Block.createColumnShape(10.0, 0.0, 8.0);
   }

   public interface SkullType extends StringIdentifiable {
      Map TYPES = new Object2ObjectArrayMap();
      Codec CODEC;

      static {
         Function var10000 = StringIdentifiable::asString;
         Map var10001 = TYPES;
         Objects.requireNonNull(var10001);
         CODEC = Codec.stringResolver(var10000, var10001::get);
      }
   }

   public static enum Type implements SkullType {
      SKELETON("skeleton"),
      WITHER_SKELETON("wither_skeleton"),
      PLAYER("player"),
      ZOMBIE("zombie"),
      CREEPER("creeper"),
      PIGLIN("piglin"),
      DRAGON("dragon");

      private final String id;

      private Type(final String id) {
         this.id = id;
         TYPES.put(id, this);
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static Type[] method_36710() {
         return new Type[]{SKELETON, WITHER_SKELETON, PLAYER, ZOMBIE, CREEPER, PIGLIN, DRAGON};
      }
   }
}
