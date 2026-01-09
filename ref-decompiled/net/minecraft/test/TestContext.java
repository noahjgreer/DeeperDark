package net.minecraft.test;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.FillBiomeCommand;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestContext {
   private final GameTestState test;
   private boolean hasFinalClause;

   public TestContext(GameTestState test) {
      this.test = test;
   }

   public GameTestException createError(Text message) {
      return new GameTestException(message, this.test.getTick());
   }

   public GameTestException createError(String translationKey, Object... args) {
      return this.createError(Text.stringifiedTranslatable(translationKey, args));
   }

   public PositionedException createError(BlockPos pos, Text message) {
      return new PositionedException(message, this.getAbsolutePos(pos), pos, this.test.getTick());
   }

   public PositionedException createError(BlockPos pos, String translationKey, Object... args) {
      return this.createError((BlockPos)pos, (Text)Text.stringifiedTranslatable(translationKey, args));
   }

   public ServerWorld getWorld() {
      return this.test.getWorld();
   }

   public BlockState getBlockState(BlockPos pos) {
      return this.getWorld().getBlockState(this.getAbsolutePos(pos));
   }

   public BlockEntity getBlockEntity(BlockPos pos, Class clazz) {
      BlockEntity blockEntity = this.getWorld().getBlockEntity(this.getAbsolutePos(pos));
      if (blockEntity == null) {
         throw this.createError(pos, "test.error.missing_block_entity");
      } else if (clazz.isInstance(blockEntity)) {
         return (BlockEntity)clazz.cast(blockEntity);
      } else {
         throw this.createError(pos, "test.error.wrong_block_entity", blockEntity.getType().getRegistryEntry().getIdAsString());
      }
   }

   public void killAllEntities() {
      this.killAllEntities(Entity.class);
   }

   public void killAllEntities(Class entityClass) {
      Box box = this.getTestBox();
      List list = this.getWorld().getEntitiesByClass(entityClass, box.expand(1.0), (entity) -> {
         return !(entity instanceof PlayerEntity);
      });
      list.forEach((entity) -> {
         entity.kill(this.getWorld());
      });
   }

   public ItemEntity spawnItem(Item item, Vec3d pos) {
      ServerWorld serverWorld = this.getWorld();
      Vec3d vec3d = this.getAbsolute(pos);
      ItemEntity itemEntity = new ItemEntity(serverWorld, vec3d.x, vec3d.y, vec3d.z, new ItemStack(item, 1));
      itemEntity.setVelocity(0.0, 0.0, 0.0);
      serverWorld.spawnEntity(itemEntity);
      return itemEntity;
   }

   public ItemEntity spawnItem(Item item, float x, float y, float z) {
      return this.spawnItem(item, new Vec3d((double)x, (double)y, (double)z));
   }

   public ItemEntity spawnItem(Item item, BlockPos pos) {
      return this.spawnItem(item, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
   }

   public Entity spawnEntity(EntityType type, BlockPos pos) {
      return this.spawnEntity(type, Vec3d.ofBottomCenter(pos));
   }

   public Entity spawnEntity(EntityType type, Vec3d pos) {
      ServerWorld serverWorld = this.getWorld();
      Entity entity = type.create(serverWorld, SpawnReason.STRUCTURE);
      if (entity == null) {
         throw this.createError(BlockPos.ofFloored(pos), "test.error.spawn_failure", type.getRegistryEntry().getIdAsString());
      } else {
         if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)entity;
            mobEntity.setPersistent();
         }

         Vec3d vec3d = this.getAbsolute(pos);
         entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, entity.getYaw(), entity.getPitch());
         serverWorld.spawnEntity(entity);
         return entity;
      }
   }

   public void damage(Entity entity, DamageSource damageSource, float amount) {
      entity.damage(this.getWorld(), damageSource, amount);
   }

   public void killEntity(Entity entity) {
      entity.kill(this.getWorld());
   }

   public Entity expectEntityInWorld(EntityType type) {
      return this.expectEntity(type, 0, 0, 0, 2.147483647E9);
   }

   public Entity expectEntity(EntityType type, int x, int y, int z, double margin) {
      List list = this.getEntitiesAround(type, x, y, z, margin);
      if (list.isEmpty()) {
         throw this.createError("test.error.expected_entity_around", type.getName(), x, y, z);
      } else if (list.size() > 1) {
         throw this.createError("test.error.too_many_entities", type.getUntranslatedName(), x, y, z, list.size());
      } else {
         Vec3d vec3d = this.getAbsolute(new Vec3d((double)x, (double)y, (double)z));
         list.sort((a, b) -> {
            double d = a.getPos().distanceTo(vec3d);
            double e = b.getPos().distanceTo(vec3d);
            return Double.compare(d, e);
         });
         return (Entity)list.get(0);
      }
   }

   public List getEntitiesAround(EntityType type, int x, int y, int z, double margin) {
      return this.getEntitiesAround(type, Vec3d.ofBottomCenter(new BlockPos(x, y, z)), margin);
   }

   public List getEntitiesAround(EntityType type, Vec3d pos, double margin) {
      ServerWorld serverWorld = this.getWorld();
      Vec3d vec3d = this.getAbsolute(pos);
      Box box = this.test.getBoundingBox();
      Box box2 = new Box(vec3d.add(-margin, -margin, -margin), vec3d.add(margin, margin, margin));
      return serverWorld.getEntitiesByType(type, box, (entity) -> {
         return entity.getBoundingBox().intersects(box2) && entity.isAlive();
      });
   }

   public Entity spawnEntity(EntityType type, int x, int y, int z) {
      return this.spawnEntity(type, new BlockPos(x, y, z));
   }

   public Entity spawnEntity(EntityType type, float x, float y, float z) {
      return this.spawnEntity(type, new Vec3d((double)x, (double)y, (double)z));
   }

   public MobEntity spawnMob(EntityType type, BlockPos pos) {
      MobEntity mobEntity = (MobEntity)this.spawnEntity(type, pos);
      mobEntity.clearGoalsAndTasks();
      return mobEntity;
   }

   public MobEntity spawnMob(EntityType type, int x, int y, int z) {
      return this.spawnMob(type, new BlockPos(x, y, z));
   }

   public MobEntity spawnMob(EntityType type, Vec3d pos) {
      MobEntity mobEntity = (MobEntity)this.spawnEntity(type, pos);
      mobEntity.clearGoalsAndTasks();
      return mobEntity;
   }

   public MobEntity spawnMob(EntityType type, float x, float y, float z) {
      return this.spawnMob(type, new Vec3d((double)x, (double)y, (double)z));
   }

   public void setEntityPos(MobEntity entity, float x, float y, float z) {
      Vec3d vec3d = this.getAbsolute(new Vec3d((double)x, (double)y, (double)z));
      entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, entity.getYaw(), entity.getPitch());
   }

   public TimedTaskRunner startMovingTowards(MobEntity entity, BlockPos pos, float speed) {
      return this.createTimedTaskRunner().expectMinDurationAndRun(2, () -> {
         Path path = entity.getNavigation().findPathTo((BlockPos)this.getAbsolutePos(pos), 0);
         entity.getNavigation().startMovingAlong(path, (double)speed);
      });
   }

   public void pushButton(int x, int y, int z) {
      this.pushButton(new BlockPos(x, y, z));
   }

   public void pushButton(BlockPos pos) {
      this.expectBlockIn(BlockTags.BUTTONS, pos);
      BlockPos blockPos = this.getAbsolutePos(pos);
      BlockState blockState = this.getWorld().getBlockState(blockPos);
      ButtonBlock buttonBlock = (ButtonBlock)blockState.getBlock();
      buttonBlock.powerOn(blockState, this.getWorld(), blockPos, (PlayerEntity)null);
   }

   public void useBlock(BlockPos pos) {
      this.useBlock(pos, this.createMockPlayer(GameMode.CREATIVE));
   }

   public void useBlock(BlockPos pos, PlayerEntity player) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      this.useBlock(pos, player, new BlockHitResult(Vec3d.ofCenter(blockPos), Direction.NORTH, blockPos, true));
   }

   public void useBlock(BlockPos pos, PlayerEntity player, BlockHitResult result) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      BlockState blockState = this.getWorld().getBlockState(blockPos);
      Hand hand = Hand.MAIN_HAND;
      ActionResult actionResult = blockState.onUseWithItem(player.getStackInHand(hand), this.getWorld(), player, hand, result);
      if (!actionResult.isAccepted()) {
         if (!(actionResult instanceof ActionResult.PassToDefaultBlockAction) || !blockState.onUse(this.getWorld(), player, result).isAccepted()) {
            ItemUsageContext itemUsageContext = new ItemUsageContext(player, hand, result);
            player.getStackInHand(hand).useOnBlock(itemUsageContext);
         }
      }
   }

   public LivingEntity drown(LivingEntity entity) {
      entity.setAir(0);
      entity.setHealth(0.25F);
      return entity;
   }

   public LivingEntity setHealthLow(LivingEntity entity) {
      entity.setHealth(0.25F);
      return entity;
   }

   public PlayerEntity createMockPlayer(final GameMode gameMode) {
      return new PlayerEntity(this, this.getWorld(), new GameProfile(UUID.randomUUID(), "test-mock-player")) {
         @NotNull
         public GameMode getGameMode() {
            return gameMode;
         }

         public boolean isControlledByPlayer() {
            return false;
         }
      };
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public ServerPlayerEntity createMockCreativeServerPlayerInWorld() {
      ConnectedClientData connectedClientData = ConnectedClientData.createDefault(new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
      ServerPlayerEntity serverPlayerEntity = new ServerPlayerEntity(this, this.getWorld().getServer(), this.getWorld(), connectedClientData.gameProfile(), connectedClientData.syncedOptions()) {
         public GameMode getGameMode() {
            return GameMode.CREATIVE;
         }
      };
      ClientConnection clientConnection = new ClientConnection(NetworkSide.SERVERBOUND);
      new EmbeddedChannel(new ChannelHandler[]{clientConnection});
      this.getWorld().getServer().getPlayerManager().onPlayerConnect(clientConnection, serverPlayerEntity, connectedClientData);
      return serverPlayerEntity;
   }

   public void toggleLever(int x, int y, int z) {
      this.toggleLever(new BlockPos(x, y, z));
   }

   public void toggleLever(BlockPos pos) {
      this.expectBlock(Blocks.LEVER, pos);
      BlockPos blockPos = this.getAbsolutePos(pos);
      BlockState blockState = this.getWorld().getBlockState(blockPos);
      LeverBlock leverBlock = (LeverBlock)blockState.getBlock();
      leverBlock.togglePower(blockState, this.getWorld(), blockPos, (PlayerEntity)null);
   }

   public void putAndRemoveRedstoneBlock(BlockPos pos, long delay) {
      this.setBlockState(pos, Blocks.REDSTONE_BLOCK);
      this.waitAndRun(delay, () -> {
         this.setBlockState(pos, Blocks.AIR);
      });
   }

   public void removeBlock(BlockPos pos) {
      this.getWorld().breakBlock(this.getAbsolutePos(pos), false, (Entity)null);
   }

   public void setBlockState(int x, int y, int z, Block block) {
      this.setBlockState(new BlockPos(x, y, z), block);
   }

   public void setBlockState(int x, int y, int z, BlockState state) {
      this.setBlockState(new BlockPos(x, y, z), state);
   }

   public void setBlockState(BlockPos pos, Block block) {
      this.setBlockState(pos, block.getDefaultState());
   }

   public void setBlockState(BlockPos pos, BlockState state) {
      this.getWorld().setBlockState(this.getAbsolutePos(pos), state, 3);
   }

   public void useNightTime() {
      this.setTime(13000);
   }

   public void setTime(int timeOfDay) {
      this.getWorld().setTimeOfDay((long)timeOfDay);
   }

   public void expectBlock(Block block, int x, int y, int z) {
      this.expectBlock(block, new BlockPos(x, y, z));
   }

   public void expectBlock(Block block, BlockPos pos) {
      BlockState blockState = this.getBlockState(pos);
      this.checkBlock(pos, (block1) -> {
         return blockState.isOf(block);
      }, (actualBlock) -> {
         return Text.translatable("test.error.expected_block", block.getName(), actualBlock.getName());
      });
   }

   public void dontExpectBlock(Block block, int x, int y, int z) {
      this.dontExpectBlock(block, new BlockPos(x, y, z));
   }

   public void dontExpectBlock(Block block, BlockPos pos) {
      this.checkBlock(pos, (block1) -> {
         return !this.getBlockState(pos).isOf(block);
      }, (actualBlock) -> {
         return Text.translatable("test.error.unexpected_block", block.getName());
      });
   }

   public void expectBlockIn(TagKey tag, BlockPos pos) {
      this.checkBlockState(pos, (state) -> {
         return state.isIn(tag);
      }, (state) -> {
         return Text.translatable("test.error.expected_block_tag", Text.of(tag.id()), state.getBlock().getName());
      });
   }

   public void expectBlockAtEnd(Block block, int x, int y, int z) {
      this.expectBlockAtEnd(block, new BlockPos(x, y, z));
   }

   public void expectBlockAtEnd(Block block, BlockPos pos) {
      this.addInstantFinalTask(() -> {
         this.expectBlock(block, pos);
      });
   }

   public void checkBlock(BlockPos pos, Predicate predicate, Function messageGetter) {
      this.checkBlockState(pos, (state) -> {
         return predicate.test(state.getBlock());
      }, (state) -> {
         return (Text)messageGetter.apply(state.getBlock());
      });
   }

   public void expectBlockProperty(BlockPos pos, Property property, Comparable value) {
      BlockState blockState = this.getBlockState(pos);
      boolean bl = blockState.contains(property);
      if (!bl) {
         throw this.createError(pos, "test.error.block_property_missing", property.getName(), value);
      } else if (!blockState.get(property).equals(value)) {
         throw this.createError(pos, "test.error.block_property_mismatch", property.getName(), value, blockState.get(property));
      }
   }

   public void checkBlockProperty(BlockPos pos, Property property, Predicate predicate, Text message) {
      this.checkBlockState(pos, (state) -> {
         if (!state.contains(property)) {
            return false;
         } else {
            Comparable comparable = state.get(property);
            return predicate.test(comparable);
         }
      }, (state) -> {
         return message;
      });
   }

   public void expectBlockState(BlockPos pos, BlockState state) {
      BlockState blockState = this.getBlockState(pos);
      if (!blockState.equals(state)) {
         throw this.createError(pos, "test.error.state_not_equal", state, blockState);
      }
   }

   public void checkBlockState(BlockPos pos, Predicate predicate, Function messageGetter) {
      BlockState blockState = this.getBlockState(pos);
      if (!predicate.test(blockState)) {
         throw this.createError(pos, (Text)messageGetter.apply(blockState));
      }
   }

   public void checkBlockEntity(BlockPos pos, Class clazz, Predicate predicate, Supplier messageGetter) {
      BlockEntity blockEntity = this.getBlockEntity(pos, clazz);
      if (!predicate.test(blockEntity)) {
         throw this.createError(pos, (Text)messageGetter.get());
      }
   }

   public void expectRedstonePower(BlockPos pos, Direction direction, IntPredicate powerPredicate, Supplier messageGetter) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      ServerWorld serverWorld = this.getWorld();
      BlockState blockState = serverWorld.getBlockState(blockPos);
      int i = blockState.getWeakRedstonePower(serverWorld, blockPos, direction);
      if (!powerPredicate.test(i)) {
         throw this.createError(pos, (Text)messageGetter.get());
      }
   }

   public void expectEntity(EntityType type) {
      List list = this.getWorld().getEntitiesByType(type, this.getTestBox(), Entity::isAlive);
      if (list.isEmpty()) {
         throw this.createError("test.error.expected_entity_in_test", type.getName());
      }
   }

   public void expectEntityAt(EntityType type, int x, int y, int z) {
      this.expectEntityAt(type, new BlockPos(x, y, z));
   }

   public void expectEntityAt(EntityType type, BlockPos pos) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      List list = this.getWorld().getEntitiesByType(type, new Box(blockPos), Entity::isAlive);
      if (list.isEmpty()) {
         throw this.createError(pos, "test.error.expected_entity", type.getName());
      }
   }

   public void expectEntityInside(EntityType type, Box box) {
      Box box2 = this.getAbsolute(box);
      List list = this.getWorld().getEntitiesByType(type, box2, Entity::isAlive);
      if (list.isEmpty()) {
         throw this.createError(BlockPos.ofFloored(box.getCenter()), "test.error.expected_entity", type.getName());
      }
   }

   public void expectEntities(EntityType type, int amount) {
      List list = this.getWorld().getEntitiesByType(type, this.getTestBox(), Entity::isAlive);
      if (list.size() != amount) {
         throw this.createError("test.error.expected_entity_count", amount, type.getName(), list.size());
      }
   }

   public void expectEntitiesAround(EntityType type, BlockPos pos, int amount, double radius) {
      this.getAbsolutePos(pos);
      List list = this.getEntitiesAround(type, pos, radius);
      if (list.size() != amount) {
         throw this.createError(pos, "test.error.expected_entity_count", amount, type.getName(), list.size());
      }
   }

   public void expectEntityAround(EntityType type, BlockPos pos, double radius) {
      List list = this.getEntitiesAround(type, pos, radius);
      if (list.isEmpty()) {
         this.getAbsolutePos(pos);
         throw this.createError(pos, "test.error.expected_entity", type.getName());
      }
   }

   public List getEntitiesAround(EntityType type, BlockPos pos, double radius) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      return this.getWorld().getEntitiesByType(type, (new Box(blockPos)).expand(radius), Entity::isAlive);
   }

   public List getEntities(EntityType type) {
      return this.getWorld().getEntitiesByType(type, this.getTestBox(), Entity::isAlive);
   }

   public void expectEntityAt(Entity entity, int x, int y, int z) {
      this.expectEntityAt(entity, new BlockPos(x, y, z));
   }

   public void expectEntityAt(Entity entity, BlockPos pos) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      List list = this.getWorld().getEntitiesByType(entity.getType(), new Box(blockPos), Entity::isAlive);
      list.stream().filter((e) -> {
         return e == entity;
      }).findFirst().orElseThrow(() -> {
         return this.createError(pos, "test.error.expected_entity", entity.getType().getName());
      });
   }

   public void expectItemsAt(Item item, BlockPos pos, double radius, int amount) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      List list = this.getWorld().getEntitiesByType(EntityType.ITEM, (new Box(blockPos)).expand(radius), Entity::isAlive);
      int i = 0;
      Iterator var9 = list.iterator();

      while(var9.hasNext()) {
         ItemEntity itemEntity = (ItemEntity)var9.next();
         ItemStack itemStack = itemEntity.getStack();
         if (itemStack.isOf(item)) {
            i += itemStack.getCount();
         }
      }

      if (i != amount) {
         throw this.createError(pos, "test.error.expected_items_count", amount, item.getName(), i);
      }
   }

   public void expectItemAt(Item item, BlockPos pos, double radius) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      List list = this.getWorld().getEntitiesByType(EntityType.ITEM, (new Box(blockPos)).expand(radius), Entity::isAlive);
      Iterator var7 = list.iterator();

      ItemEntity itemEntity;
      do {
         if (!var7.hasNext()) {
            throw this.createError(pos, "test.error.expected_item", item.getName());
         }

         Entity entity = (Entity)var7.next();
         itemEntity = (ItemEntity)entity;
      } while(!itemEntity.getStack().getItem().equals(item));

   }

   public void dontExpectItemAt(Item item, BlockPos pos, double radius) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      List list = this.getWorld().getEntitiesByType(EntityType.ITEM, (new Box(blockPos)).expand(radius), Entity::isAlive);
      Iterator var7 = list.iterator();

      ItemEntity itemEntity;
      do {
         if (!var7.hasNext()) {
            return;
         }

         Entity entity = (Entity)var7.next();
         itemEntity = (ItemEntity)entity;
      } while(!itemEntity.getStack().getItem().equals(item));

      throw this.createError(pos, "test.error.unexpected_item", item.getName());
   }

   public void expectItem(Item item) {
      List list = this.getWorld().getEntitiesByType(EntityType.ITEM, this.getTestBox(), Entity::isAlive);
      Iterator var3 = list.iterator();

      ItemEntity itemEntity;
      do {
         if (!var3.hasNext()) {
            throw this.createError("test.error.expected_item", item.getName());
         }

         Entity entity = (Entity)var3.next();
         itemEntity = (ItemEntity)entity;
      } while(!itemEntity.getStack().getItem().equals(item));

   }

   public void dontExpectItem(Item item) {
      List list = this.getWorld().getEntitiesByType(EntityType.ITEM, this.getTestBox(), Entity::isAlive);
      Iterator var3 = list.iterator();

      ItemEntity itemEntity;
      do {
         if (!var3.hasNext()) {
            return;
         }

         Entity entity = (Entity)var3.next();
         itemEntity = (ItemEntity)entity;
      } while(!itemEntity.getStack().getItem().equals(item));

      throw this.createError("test.error.unexpected_item", item.getName());
   }

   public void dontExpectEntity(EntityType type) {
      List list = this.getWorld().getEntitiesByType(type, this.getTestBox(), Entity::isAlive);
      if (!list.isEmpty()) {
         throw this.createError(((Entity)list.getFirst()).getBlockPos(), "test.error.unexpected_entity", type.getName());
      }
   }

   public void dontExpectEntityAt(EntityType type, int x, int y, int z) {
      this.dontExpectEntityAt(type, new BlockPos(x, y, z));
   }

   public void dontExpectEntityAt(EntityType type, BlockPos pos) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      List list = this.getWorld().getEntitiesByType(type, new Box(blockPos), Entity::isAlive);
      if (!list.isEmpty()) {
         throw this.createError(pos, "test.error.unexpected_entity", type.getName());
      }
   }

   public void dontExpectEntityBetween(EntityType type, Box box) {
      Box box2 = this.getAbsolute(box);
      List list = this.getWorld().getEntitiesByType(type, box2, Entity::isAlive);
      if (!list.isEmpty()) {
         throw this.createError(((Entity)list.getFirst()).getBlockPos(), "test.error.unexpected_entity", type.getName());
      }
   }

   public void expectEntityToTouch(EntityType type, double x, double y, double z) {
      Vec3d vec3d = new Vec3d(x, y, z);
      Vec3d vec3d2 = this.getAbsolute(vec3d);
      Predicate predicate = (entity) -> {
         return entity.getBoundingBox().intersects(vec3d2, vec3d2);
      };
      List list = this.getWorld().getEntitiesByType(type, this.getTestBox(), predicate);
      if (list.isEmpty()) {
         throw this.createError("test.error.expected_entity_touching", type.getName(), vec3d2.getX(), vec3d2.getY(), vec3d2.getZ(), x, y, z);
      }
   }

   public void dontExpectEntityToTouch(EntityType type, double x, double y, double z) {
      Vec3d vec3d = new Vec3d(x, y, z);
      Vec3d vec3d2 = this.getAbsolute(vec3d);
      Predicate predicate = (entity) -> {
         return !entity.getBoundingBox().intersects(vec3d2, vec3d2);
      };
      List list = this.getWorld().getEntitiesByType(type, this.getTestBox(), predicate);
      if (list.isEmpty()) {
         throw this.createError("test.error.expected_entity_not_touching", type.getName(), vec3d2.getX(), vec3d2.getY(), vec3d2.getZ(), x, y, z);
      }
   }

   public void expectEntity(BlockPos pos, EntityType type, Predicate predicate) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      List list = this.getWorld().getEntitiesByType(type, new Box(blockPos), Entity::isAlive);
      if (list.isEmpty()) {
         throw this.createError(pos, "test.error.expected_entity", type.getName());
      } else {
         Iterator var6 = list.iterator();

         Entity entity;
         do {
            if (!var6.hasNext()) {
               return;
            }

            entity = (Entity)var6.next();
         } while(predicate.test(entity));

         throw this.createError(entity.getBlockPos(), "test.error.expected_entity_data_predicate", entity.getName());
      }
   }

   public void expectEntityWithData(BlockPos pos, EntityType type, Function entityDataGetter, @Nullable Object data) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      List list = this.getWorld().getEntitiesByType(type, new Box(blockPos), Entity::isAlive);
      if (list.isEmpty()) {
         throw this.createError(pos, "test.error.expected_entity", type.getName());
      } else {
         Iterator var7 = list.iterator();

         Object object;
         do {
            if (!var7.hasNext()) {
               return;
            }

            Entity entity = (Entity)var7.next();
            object = entityDataGetter.apply(entity);
         } while(Objects.equals(object, data));

         throw this.createError(pos, "test.error.expected_entity_data", data, object);
      }
   }

   public void expectEntityHoldingItem(BlockPos pos, EntityType entityType, Item item) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      List list = this.getWorld().getEntitiesByType(entityType, new Box(blockPos), Entity::isAlive);
      if (list.isEmpty()) {
         throw this.createError(pos, "test.error.expected_entity", entityType.getName());
      } else {
         Iterator var6 = list.iterator();

         LivingEntity livingEntity;
         do {
            if (!var6.hasNext()) {
               throw this.createError(pos, "test.error.expected_entity_holding", item.getName());
            }

            livingEntity = (LivingEntity)var6.next();
         } while(!livingEntity.isHolding(item));

      }
   }

   public void expectEntityWithItem(BlockPos pos, EntityType entityType, Item item) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      List list = this.getWorld().getEntitiesByType(entityType, new Box(blockPos), (entityx) -> {
         return ((Entity)entityx).isAlive();
      });
      if (list.isEmpty()) {
         throw this.createError(pos, "test.error.expected_entity", entityType.getName());
      } else {
         Iterator var6 = list.iterator();

         Entity entity;
         do {
            if (!var6.hasNext()) {
               throw this.createError(pos, "test.error.expected_entity_having", item.getName());
            }

            entity = (Entity)var6.next();
         } while(!((InventoryOwner)entity).getInventory().containsAny((stack) -> {
            return stack.isOf(item);
         }));

      }
   }

   public void expectEmptyContainer(BlockPos pos) {
      LockableContainerBlockEntity lockableContainerBlockEntity = (LockableContainerBlockEntity)this.getBlockEntity(pos, LockableContainerBlockEntity.class);
      if (!lockableContainerBlockEntity.isEmpty()) {
         throw this.createError(pos, "test.error.expected_empty_container");
      }
   }

   public void expectContainerWithSingle(BlockPos pos, Item item) {
      LockableContainerBlockEntity lockableContainerBlockEntity = (LockableContainerBlockEntity)this.getBlockEntity(pos, LockableContainerBlockEntity.class);
      if (lockableContainerBlockEntity.count(item) != 1) {
         throw this.createError(pos, "test.error.expected_container_contents_single", item.getName());
      }
   }

   public void expectContainerWith(BlockPos pos, Item item) {
      LockableContainerBlockEntity lockableContainerBlockEntity = (LockableContainerBlockEntity)this.getBlockEntity(pos, LockableContainerBlockEntity.class);
      if (lockableContainerBlockEntity.count(item) == 0) {
         throw this.createError(pos, "test.error.expected_container_contents", item.getName());
      }
   }

   public void expectSameStates(BlockBox checkedBlockBox, BlockPos correctStatePos) {
      BlockPos.stream(checkedBlockBox).forEach((checkedPos) -> {
         BlockPos blockPos2 = correctStatePos.add(checkedPos.getX() - checkedBlockBox.getMinX(), checkedPos.getY() - checkedBlockBox.getMinY(), checkedPos.getZ() - checkedBlockBox.getMinZ());
         this.expectSameStates(checkedPos, blockPos2);
      });
   }

   public void expectSameStates(BlockPos checkedPos, BlockPos correctStatePos) {
      BlockState blockState = this.getBlockState(checkedPos);
      BlockState blockState2 = this.getBlockState(correctStatePos);
      if (blockState != blockState2) {
         throw this.createError(checkedPos, "test.error.state_not_equal", blockState2, blockState);
      }
   }

   public void expectContainerWith(long delay, BlockPos pos, Item item) {
      this.runAtTick(delay, () -> {
         this.expectContainerWithSingle(pos, item);
      });
   }

   public void expectEmptyContainer(long delay, BlockPos pos) {
      this.runAtTick(delay, () -> {
         this.expectEmptyContainer(pos);
      });
   }

   public void expectEntityWithDataEnd(BlockPos pos, EntityType type, Function entityDataGetter, Object data) {
      this.addInstantFinalTask(() -> {
         this.expectEntityWithData(pos, type, entityDataGetter, data);
      });
   }

   public void expectEntityIn(Entity entity, Box box, Text message) {
      if (!box.contains(this.getRelative(entity.getPos()))) {
         throw this.createError(message);
      }
   }

   public void testEntity(Entity entity, Predicate predicate, Text message) {
      if (!predicate.test(entity)) {
         throw this.createError(entity.getBlockPos(), "test.error.entity_property", entity.getName(), message);
      }
   }

   public void testEntityProperty(Entity entity, Function propertyGetter, Object value, Text message) {
      Object object = propertyGetter.apply(entity);
      if (!object.equals(value)) {
         throw this.createError(entity.getBlockPos(), "test.error.entity_property_details", entity.getName(), message, object, value);
      }
   }

   public void expectEntityHasEffect(LivingEntity entity, RegistryEntry effect, int amplifier) {
      StatusEffectInstance statusEffectInstance = entity.getStatusEffect(effect);
      if (statusEffectInstance == null || statusEffectInstance.getAmplifier() != amplifier) {
         throw this.createError("test.error.expected_entity_effect", entity.getName(), PotionContentsComponent.getEffectText(effect, amplifier));
      }
   }

   public void expectEntityAtEnd(EntityType type, int x, int y, int z) {
      this.expectEntityAtEnd(type, new BlockPos(x, y, z));
   }

   public void expectEntityAtEnd(EntityType type, BlockPos pos) {
      this.addInstantFinalTask(() -> {
         this.expectEntityAt(type, pos);
      });
   }

   public void dontExpectEntityAtEnd(EntityType type, int x, int y, int z) {
      this.dontExpectEntityAtEnd(type, new BlockPos(x, y, z));
   }

   public void dontExpectEntityAtEnd(EntityType type, BlockPos pos) {
      this.addInstantFinalTask(() -> {
         this.dontExpectEntityAt(type, pos);
      });
   }

   public void complete() {
      this.test.completeIfSuccessful();
   }

   private void markFinalCause() {
      if (this.hasFinalClause) {
         throw new IllegalStateException("This test already has final clause");
      } else {
         this.hasFinalClause = true;
      }
   }

   public void addFinalTask(Runnable runnable) {
      this.markFinalCause();
      this.test.createTimedTaskRunner().createAndAdd(0L, runnable).completeIfSuccessful();
   }

   public void addInstantFinalTask(Runnable runnable) {
      this.markFinalCause();
      this.test.createTimedTaskRunner().createAndAdd(runnable).completeIfSuccessful();
   }

   public void addFinalTaskWithDuration(int duration, Runnable runnable) {
      this.markFinalCause();
      this.test.createTimedTaskRunner().createAndAdd((long)duration, runnable).completeIfSuccessful();
   }

   public void runAtTick(long tick, Runnable runnable) {
      this.test.runAtTick(tick, runnable);
   }

   public void waitAndRun(long ticks, Runnable runnable) {
      this.runAtTick((long)this.test.getTick() + ticks, runnable);
   }

   public void forceRandomTick(BlockPos pos) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      ServerWorld serverWorld = this.getWorld();
      serverWorld.getBlockState(blockPos).randomTick(serverWorld, blockPos, serverWorld.random);
   }

   public void forceScheduledTick(BlockPos pos) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      ServerWorld serverWorld = this.getWorld();
      serverWorld.getBlockState(blockPos).scheduledTick(serverWorld, blockPos, serverWorld.random);
   }

   public void forceTickIceAndSnow(BlockPos pos) {
      BlockPos blockPos = this.getAbsolutePos(pos);
      ServerWorld serverWorld = this.getWorld();
      serverWorld.tickIceAndSnow(blockPos);
   }

   public void forceTickIceAndSnow() {
      Box box = this.getRelativeTestBox();
      int i = (int)Math.floor(box.maxX);
      int j = (int)Math.floor(box.maxZ);
      int k = (int)Math.floor(box.maxY);

      for(int l = (int)Math.floor(box.minX); l < i; ++l) {
         for(int m = (int)Math.floor(box.minZ); m < j; ++m) {
            this.forceTickIceAndSnow(new BlockPos(l, k, m));
         }
      }

   }

   public int getRelativeTopY(Heightmap.Type heightmap, int x, int z) {
      BlockPos blockPos = this.getAbsolutePos(new BlockPos(x, 0, z));
      return this.getRelativePos(this.getWorld().getTopPosition(heightmap, blockPos)).getY();
   }

   public void throwPositionedException(Text message, BlockPos pos) {
      throw this.createError(pos, message);
   }

   public void throwPositionedException(Text message, Entity entity) {
      throw this.createError(entity.getBlockPos(), message);
   }

   public void throwGameTestException(Text message) {
      throw this.createError(message);
   }

   public void addTask(Runnable task) {
      this.test.createTimedTaskRunner().createAndAdd(task).fail(() -> {
         return this.createError("test.error.fail");
      });
   }

   public void runAtEveryTick(Runnable task) {
      LongStream.range((long)this.test.getTick(), (long)this.test.getTickLimit()).forEach((tick) -> {
         GameTestState var10000 = this.test;
         Objects.requireNonNull(task);
         var10000.runAtTick(tick, task::run);
      });
   }

   public TimedTaskRunner createTimedTaskRunner() {
      return this.test.createTimedTaskRunner();
   }

   public BlockPos getAbsolutePos(BlockPos pos) {
      BlockPos blockPos = this.test.getOrigin();
      BlockPos blockPos2 = blockPos.add(pos);
      return StructureTemplate.transformAround(blockPos2, BlockMirror.NONE, this.test.getRotation(), blockPos);
   }

   public BlockPos getRelativePos(BlockPos pos) {
      BlockPos blockPos = this.test.getOrigin();
      BlockRotation blockRotation = this.test.getRotation().rotate(BlockRotation.CLOCKWISE_180);
      BlockPos blockPos2 = StructureTemplate.transformAround(pos, BlockMirror.NONE, blockRotation, blockPos);
      return blockPos2.subtract(blockPos);
   }

   public Box getAbsolute(Box box) {
      Vec3d vec3d = this.getAbsolute(box.getMinPos());
      Vec3d vec3d2 = this.getAbsolute(box.getMaxPos());
      return new Box(vec3d, vec3d2);
   }

   public Box getRelative(Box box) {
      Vec3d vec3d = this.getRelative(box.getMinPos());
      Vec3d vec3d2 = this.getRelative(box.getMaxPos());
      return new Box(vec3d, vec3d2);
   }

   public Vec3d getAbsolute(Vec3d pos) {
      Vec3d vec3d = Vec3d.of(this.test.getOrigin());
      return StructureTemplate.transformAround(vec3d.add(pos), BlockMirror.NONE, this.test.getRotation(), this.test.getOrigin());
   }

   public Vec3d getRelative(Vec3d pos) {
      Vec3d vec3d = Vec3d.of(this.test.getOrigin());
      return StructureTemplate.transformAround(pos.subtract(vec3d), BlockMirror.NONE, this.test.getRotation(), this.test.getOrigin());
   }

   public BlockRotation getRotation() {
      return this.test.getRotation();
   }

   public void assertTrue(boolean condition, Text message) {
      if (!condition) {
         throw this.createError(message);
      }
   }

   public void assertEquals(Object expected, Object value, Text message) {
      if (!expected.equals(value)) {
         throw this.createError("test.error.value_not_equal", message, expected, value);
      }
   }

   public void assertFalse(boolean condition, Text message) {
      this.assertTrue(!condition, message);
   }

   public long getTick() {
      return (long)this.test.getTick();
   }

   public Box getTestBox() {
      return this.test.getBoundingBox();
   }

   private Box getRelativeTestBox() {
      Box box = this.test.getBoundingBox();
      BlockRotation blockRotation = this.test.getRotation();
      switch (blockRotation) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            return new Box(0.0, 0.0, 0.0, box.getLengthZ(), box.getLengthY(), box.getLengthX());
         default:
            return new Box(0.0, 0.0, 0.0, box.getLengthX(), box.getLengthY(), box.getLengthZ());
      }
   }

   public void forEachRelativePos(Consumer posConsumer) {
      Box box = this.getRelativeTestBox().shrink(1.0, 1.0, 1.0);
      BlockPos.Mutable.stream(box).forEach(posConsumer);
   }

   public void forEachRemainingTick(Runnable runnable) {
      LongStream.range((long)this.test.getTick(), (long)this.test.getTickLimit()).forEach((tick) -> {
         GameTestState var10000 = this.test;
         Objects.requireNonNull(runnable);
         var10000.runAtTick(tick, runnable::run);
      });
   }

   public void useStackOnBlock(PlayerEntity player, ItemStack stack, BlockPos pos, Direction direction) {
      BlockPos blockPos = this.getAbsolutePos(pos.offset(direction));
      BlockHitResult blockHitResult = new BlockHitResult(Vec3d.ofCenter(blockPos), direction, blockPos, false);
      ItemUsageContext itemUsageContext = new ItemUsageContext(player, Hand.MAIN_HAND, blockHitResult);
      stack.useOnBlock(itemUsageContext);
   }

   public void setBiome(RegistryKey biome) {
      Box box = this.getTestBox();
      BlockPos blockPos = BlockPos.ofFloored(box.minX, box.minY, box.minZ);
      BlockPos blockPos2 = BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ);
      Either either = FillBiomeCommand.fillBiome(this.getWorld(), blockPos, blockPos2, this.getWorld().getRegistryManager().getOrThrow(RegistryKeys.BIOME).getOrThrow(biome));
      if (either.right().isPresent()) {
         throw this.createError("test.error.set_biome");
      }
   }
}
