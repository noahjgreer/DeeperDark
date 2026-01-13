/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Portal$Effect
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.entity.CommandBlockBlockEntity
 *  net.minecraft.block.entity.HangingSignBlockEntity
 *  net.minecraft.block.entity.JigsawBlockEntity
 *  net.minecraft.block.entity.SignBlockEntity
 *  net.minecraft.block.entity.StructureBlockBlockEntity
 *  net.minecraft.block.entity.TestBlockEntity
 *  net.minecraft.block.entity.TestInstanceBlockEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.BookEditScreen
 *  net.minecraft.client.gui.screen.ingame.CommandBlockScreen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.screen.ingame.HangingSignEditScreen
 *  net.minecraft.client.gui.screen.ingame.JigsawBlockScreen
 *  net.minecraft.client.gui.screen.ingame.MinecartCommandBlockScreen
 *  net.minecraft.client.gui.screen.ingame.SignEditScreen
 *  net.minecraft.client.gui.screen.ingame.StructureBlockScreen
 *  net.minecraft.client.gui.screen.ingame.TestBlockScreen
 *  net.minecraft.client.gui.screen.ingame.TestInstanceBlockScreen
 *  net.minecraft.client.gui.screen.world.LevelLoadingScreen
 *  net.minecraft.client.input.Input
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.recipebook.ClientRecipeBook
 *  net.minecraft.client.sound.AmbientSoundLoops$Underwater
 *  net.minecraft.client.sound.AmbientSoundPlayer
 *  net.minecraft.client.sound.BiomeEffectSoundPlayer
 *  net.minecraft.client.sound.BubbleColumnSoundPlayer
 *  net.minecraft.client.sound.ElytraSoundInstance
 *  net.minecraft.client.sound.EntityRidingSoundInstance
 *  net.minecraft.client.sound.MinecartInsideSoundInstance
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.util.ClientPlayerTickable
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.command.permission.LeveledPermissionPredicate
 *  net.minecraft.command.permission.PermissionPredicate
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.AttackRangeComponent
 *  net.minecraft.component.type.UseEffectsComponent
 *  net.minecraft.component.type.WritableBookContentComponent
 *  net.minecraft.dialog.type.Dialog
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.entity.JumpingMount
 *  net.minecraft.entity.MovementType
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.entity.data.TrackedData
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.passive.AbstractNautilusEntity
 *  net.minecraft.entity.passive.HappyGhastEntity
 *  net.minecraft.entity.player.PlayerAbilities
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileUtil
 *  net.minecraft.entity.vehicle.AbstractBoatEntity
 *  net.minecraft.entity.vehicle.AbstractMinecartEntity
 *  net.minecraft.entity.vehicle.CommandBlockMinecartEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket$Mode
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 *  net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$Full
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$LookAndOnGround
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$OnGroundOnly
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$PositionAndOnGround
 *  net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket
 *  net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.predicate.entity.EntityPredicates
 *  net.minecraft.recipe.NetworkRecipeId
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.StatHandler
 *  net.minecraft.text.Text
 *  net.minecraft.util.Arm
 *  net.minecraft.util.ClickType
 *  net.minecraft.util.Cooldown
 *  net.minecraft.util.Hand
 *  net.minecraft.util.PlayerInput
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.Direction$AxisDirection
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.GameMode
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Portal;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.TestBlockEntity;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.screen.ingame.MinecartCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.screen.ingame.TestBlockScreen;
import net.minecraft.client.gui.screen.ingame.TestInstanceBlockScreen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.sound.AmbientSoundLoops;
import net.minecraft.client.sound.AmbientSoundPlayer;
import net.minecraft.client.sound.BiomeEffectSoundPlayer;
import net.minecraft.client.sound.BubbleColumnSoundPlayer;
import net.minecraft.client.sound.ElytraSoundInstance;
import net.minecraft.client.sound.EntityRidingSoundInstance;
import net.minecraft.client.sound.MinecartInsideSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttackRangeComponent;
import net.minecraft.component.type.UseEffectsComponent;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AbstractNautilusEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.ClickType;
import net.minecraft.util.Cooldown;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ClientPlayerEntity
extends AbstractClientPlayerEntity {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_32671 = 20;
    private static final int field_32672 = 600;
    private static final int field_32673 = 100;
    private static final float field_32674 = 0.6f;
    private static final double field_32675 = 0.35;
    private static final double MAX_SOFT_COLLISION_RADIANS = 0.13962633907794952;
    public final ClientPlayNetworkHandler networkHandler;
    private final StatHandler statHandler;
    private final ClientRecipeBook recipeBook;
    private final Cooldown itemDropCooldown = new Cooldown(20, 1280);
    private final List<ClientPlayerTickable> tickables = Lists.newArrayList();
    private PermissionPredicate permissions = PermissionPredicate.NONE;
    private double lastXClient;
    private double lastYClient;
    private double lastZClient;
    private float lastYawClient;
    private float lastPitchClient;
    private boolean lastOnGround;
    private boolean lastHorizontalCollision;
    private boolean inSneakingPose;
    private boolean lastSprinting;
    private int ticksSinceLastPositionPacketSent;
    private boolean healthInitialized;
    public Input input = new Input();
    private PlayerInput lastPlayerInput;
    protected final MinecraftClient client;
    protected int ticksLeftToDoubleTapSprint;
    private static final int field_63570 = Integer.MIN_VALUE;
    private static final int field_63571 = -2147483647;
    public int experienceBarDisplayStartTime = Integer.MIN_VALUE;
    public float renderYaw;
    public float renderPitch;
    public float lastRenderYaw;
    public float lastRenderPitch;
    private int mountJumpTicks;
    private float mountJumpStrength;
    public float nauseaIntensity;
    public float lastNauseaIntensity;
    private boolean usingItem;
    private @Nullable Hand activeHand;
    private boolean riding;
    private boolean autoJumpEnabled = true;
    private int ticksToNextAutoJump;
    private boolean falling;
    private int underwaterVisibilityTicks;
    private boolean showsDeathScreen = true;
    private boolean limitedCraftingEnabled = false;

    public ClientPlayerEntity(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, PlayerInput lastPlayerInput, boolean lastSprinting) {
        super(world, networkHandler.getProfile());
        this.client = client;
        this.networkHandler = networkHandler;
        this.statHandler = stats;
        this.recipeBook = recipeBook;
        this.lastPlayerInput = lastPlayerInput;
        this.lastSprinting = lastSprinting;
        this.tickables.add(new AmbientSoundPlayer(this, client.getSoundManager()));
        this.tickables.add(new BubbleColumnSoundPlayer(this));
        this.tickables.add(new BiomeEffectSoundPlayer(this, client.getSoundManager()));
    }

    public void heal(float amount) {
    }

    public boolean startRiding(Entity entity, boolean force, boolean emitEvent) {
        if (!super.startRiding(entity, force, emitEvent)) {
            return false;
        }
        if (entity instanceof AbstractMinecartEntity) {
            AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)entity;
            this.client.getSoundManager().play((SoundInstance)new MinecartInsideSoundInstance((PlayerEntity)this, abstractMinecartEntity, true, SoundEvents.ENTITY_MINECART_INSIDE_UNDERWATER, 0.0f, 0.75f, 1.0f));
            this.client.getSoundManager().play((SoundInstance)new MinecartInsideSoundInstance((PlayerEntity)this, abstractMinecartEntity, false, SoundEvents.ENTITY_MINECART_INSIDE, 0.0f, 0.75f, 1.0f));
        } else if (entity instanceof HappyGhastEntity) {
            HappyGhastEntity happyGhastEntity = (HappyGhastEntity)entity;
            this.client.getSoundManager().play((SoundInstance)new EntityRidingSoundInstance((PlayerEntity)this, (Entity)happyGhastEntity, false, SoundEvents.ENTITY_HAPPY_GHAST_RIDING, happyGhastEntity.getSoundCategory(), 0.0f, 1.0f, 5.0f));
        } else if (entity instanceof AbstractNautilusEntity) {
            AbstractNautilusEntity abstractNautilusEntity = (AbstractNautilusEntity)entity;
            this.client.getSoundManager().play((SoundInstance)new EntityRidingSoundInstance((PlayerEntity)this, (Entity)abstractNautilusEntity, true, SoundEvents.ENTITY_NAUTILUS_RIDING, abstractNautilusEntity.getSoundCategory(), 0.0f, 1.0f, 5.0f));
        }
        return true;
    }

    public void dismountVehicle() {
        super.dismountVehicle();
        this.riding = false;
    }

    public float getPitch(float tickProgress) {
        return this.getPitch();
    }

    public float getYaw(float tickProgress) {
        if (this.hasVehicle()) {
            return super.getYaw(tickProgress);
        }
        return this.getYaw();
    }

    public void tick() {
        if (!this.networkHandler.isLoaded()) {
            return;
        }
        this.itemDropCooldown.tick();
        super.tick();
        if (!this.lastPlayerInput.equals((Object)this.input.playerInput)) {
            this.networkHandler.sendPacket((Packet)new PlayerInputC2SPacket(this.input.playerInput));
            this.lastPlayerInput = this.input.playerInput;
        }
        if (this.hasVehicle()) {
            this.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.isOnGround(), this.horizontalCollision));
            Entity entity = this.getRootVehicle();
            if (entity != this && entity.isLogicalSideForUpdatingMovement()) {
                this.networkHandler.sendPacket((Packet)VehicleMoveC2SPacket.fromVehicle((Entity)entity));
                this.sendSprintingPacket();
            }
        } else {
            this.sendMovementPackets();
        }
        for (ClientPlayerTickable clientPlayerTickable : this.tickables) {
            clientPlayerTickable.tick();
        }
    }

    public float getMoodPercentage() {
        for (ClientPlayerTickable clientPlayerTickable : this.tickables) {
            if (!(clientPlayerTickable instanceof BiomeEffectSoundPlayer)) continue;
            return ((BiomeEffectSoundPlayer)clientPlayerTickable).getMoodPercentage();
        }
        return 0.0f;
    }

    private void sendMovementPackets() {
        this.sendSprintingPacket();
        if (this.isCamera()) {
            boolean bl2;
            double d = this.getX() - this.lastXClient;
            double e = this.getY() - this.lastYClient;
            double f = this.getZ() - this.lastZClient;
            double g = this.getYaw() - this.lastYawClient;
            double h = this.getPitch() - this.lastPitchClient;
            ++this.ticksSinceLastPositionPacketSent;
            boolean bl = MathHelper.squaredMagnitude((double)d, (double)e, (double)f) > MathHelper.square((double)2.0E-4) || this.ticksSinceLastPositionPacketSent >= 20;
            boolean bl3 = bl2 = g != 0.0 || h != 0.0;
            if (bl && bl2) {
                this.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.Full(this.getEntityPos(), this.getYaw(), this.getPitch(), this.isOnGround(), this.horizontalCollision));
            } else if (bl) {
                this.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(this.getEntityPos(), this.isOnGround(), this.horizontalCollision));
            } else if (bl2) {
                this.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.isOnGround(), this.horizontalCollision));
            } else if (this.lastOnGround != this.isOnGround() || this.lastHorizontalCollision != this.horizontalCollision) {
                this.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.OnGroundOnly(this.isOnGround(), this.horizontalCollision));
            }
            if (bl) {
                this.lastXClient = this.getX();
                this.lastYClient = this.getY();
                this.lastZClient = this.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }
            if (bl2) {
                this.lastYawClient = this.getYaw();
                this.lastPitchClient = this.getPitch();
            }
            this.lastOnGround = this.isOnGround();
            this.lastHorizontalCollision = this.horizontalCollision;
            this.autoJumpEnabled = (Boolean)this.client.options.getAutoJump().getValue();
        }
    }

    private void sendSprintingPacket() {
        boolean bl = this.isSprinting();
        if (bl != this.lastSprinting) {
            ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.START_SPRINTING : ClientCommandC2SPacket.Mode.STOP_SPRINTING;
            this.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this, mode));
            this.lastSprinting = bl;
        }
    }

    public boolean dropSelectedItem(boolean entireStack) {
        PlayerActionC2SPacket.Action action = entireStack ? PlayerActionC2SPacket.Action.DROP_ALL_ITEMS : PlayerActionC2SPacket.Action.DROP_ITEM;
        ItemStack itemStack = this.getInventory().dropSelectedItem(entireStack);
        this.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(action, BlockPos.ORIGIN, Direction.DOWN));
        return !itemStack.isEmpty();
    }

    public void swingHand(Hand hand) {
        super.swingHand(hand);
        this.networkHandler.sendPacket((Packet)new HandSwingC2SPacket(hand));
    }

    public void requestRespawn() {
        this.networkHandler.sendPacket((Packet)new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
        KeyBinding.untoggleStickyKeys();
    }

    public void closeHandledScreen() {
        this.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(this.currentScreenHandler.syncId));
        this.closeScreen();
    }

    public void closeScreen() {
        super.closeHandledScreen();
        this.client.setScreen(null);
    }

    public void updateHealth(float health) {
        if (this.healthInitialized) {
            float f = this.getHealth() - health;
            if (f <= 0.0f) {
                this.setHealth(health);
                if (f < 0.0f) {
                    this.timeUntilRegen = 10;
                }
            } else {
                this.lastDamageTaken = f;
                this.timeUntilRegen = 20;
                this.setHealth(health);
                this.hurtTime = this.maxHurtTime = 10;
            }
        } else {
            this.setHealth(health);
            this.healthInitialized = true;
        }
    }

    public void sendAbilitiesUpdate() {
        this.networkHandler.sendPacket((Packet)new UpdatePlayerAbilitiesC2SPacket(this.getAbilities()));
    }

    public void setReducedDebugInfo(boolean reducedDebugInfo) {
        super.setReducedDebugInfo(reducedDebugInfo);
        this.client.debugHudEntryList.updateVisibleEntries();
    }

    public boolean isMainPlayer() {
        return true;
    }

    public boolean isHoldingOntoLadder() {
        return !this.getAbilities().flying && super.isHoldingOntoLadder();
    }

    public boolean shouldSpawnSprintingParticles() {
        return !this.getAbilities().flying && super.shouldSpawnSprintingParticles();
    }

    protected void startRidingJump() {
        this.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this, ClientCommandC2SPacket.Mode.START_RIDING_JUMP, MathHelper.floor((float)(this.getMountJumpStrength() * 100.0f))));
    }

    public void openRidingInventory() {
        this.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
    }

    public StatHandler getStatHandler() {
        return this.statHandler;
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void onRecipeDisplayed(NetworkRecipeId recipeId) {
        if (this.recipeBook.isHighlighted(recipeId)) {
            this.recipeBook.unmarkHighlighted(recipeId);
            this.networkHandler.sendPacket((Packet)new RecipeBookDataC2SPacket(recipeId));
        }
    }

    public PermissionPredicate getPermissions() {
        return this.permissions;
    }

    public void setPermissions(PermissionPredicate permissions) {
        this.permissions = permissions;
    }

    public void sendMessage(Text message, boolean overlay) {
        this.client.getMessageHandler().onGameMessage(message, overlay);
    }

    private void pushOutOfBlocks(double x, double z) {
        Direction[] directions;
        BlockPos blockPos = BlockPos.ofFloored((double)x, (double)this.getY(), (double)z);
        if (!this.wouldCollideAt(blockPos)) {
            return;
        }
        double d = x - (double)blockPos.getX();
        double e = z - (double)blockPos.getZ();
        Direction direction = null;
        double f = Double.MAX_VALUE;
        for (Direction direction2 : directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}) {
            double h;
            double g = direction2.getAxis().choose(d, 0.0, e);
            double d2 = h = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - g : g;
            if (!(h < f) || this.wouldCollideAt(blockPos.offset(direction2))) continue;
            f = h;
            direction = direction2;
        }
        if (direction != null) {
            Vec3d vec3d = this.getVelocity();
            if (direction.getAxis() == Direction.Axis.X) {
                this.setVelocity(0.1 * (double)direction.getOffsetX(), vec3d.y, vec3d.z);
            } else {
                this.setVelocity(vec3d.x, vec3d.y, 0.1 * (double)direction.getOffsetZ());
            }
        }
    }

    private boolean wouldCollideAt(BlockPos pos) {
        Box box = this.getBoundingBox();
        Box box2 = new Box((double)pos.getX(), box.minY, (double)pos.getZ(), (double)pos.getX() + 1.0, box.maxY, (double)pos.getZ() + 1.0).contract(1.0E-7);
        return this.getEntityWorld().canCollide((Entity)this, box2);
    }

    public void setExperience(float progress, int total, int level) {
        if (progress != this.experienceProgress) {
            this.updateExperienceBarDisplayStartTime();
        }
        this.experienceProgress = progress;
        this.totalExperience = total;
        this.experienceLevel = level;
    }

    private void updateExperienceBarDisplayStartTime() {
        this.experienceBarDisplayStartTime = this.experienceBarDisplayStartTime == Integer.MIN_VALUE ? -2147483647 : this.age;
    }

    public void handleStatus(byte status) {
        switch (status) {
            case 24: {
                this.setPermissions(PermissionPredicate.NONE);
                break;
            }
            case 25: {
                this.setPermissions((PermissionPredicate)LeveledPermissionPredicate.MODERATORS);
                break;
            }
            case 26: {
                this.setPermissions((PermissionPredicate)LeveledPermissionPredicate.GAMEMASTERS);
                break;
            }
            case 27: {
                this.setPermissions((PermissionPredicate)LeveledPermissionPredicate.ADMINS);
                break;
            }
            case 28: {
                this.setPermissions((PermissionPredicate)LeveledPermissionPredicate.OWNERS);
                break;
            }
            default: {
                super.handleStatus(status);
            }
        }
    }

    public void setShowsDeathScreen(boolean showsDeathScreen) {
        this.showsDeathScreen = showsDeathScreen;
    }

    public boolean showsDeathScreen() {
        return this.showsDeathScreen;
    }

    public void setLimitedCraftingEnabled(boolean limitedCraftingEnabled) {
        this.limitedCraftingEnabled = limitedCraftingEnabled;
    }

    public boolean isLimitedCraftingEnabled() {
        return this.limitedCraftingEnabled;
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        this.getEntityWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch, false);
    }

    public void setCurrentHand(Hand hand) {
        ItemStack itemStack = this.getStackInHand(hand);
        if (itemStack.isEmpty() || this.isUsingItem()) {
            return;
        }
        super.setCurrentHand(hand);
        this.usingItem = true;
        this.activeHand = hand;
    }

    public boolean isUsingItem() {
        return this.usingItem;
    }

    private boolean isBlockedFromSprinting() {
        return this.isUsingItem() && !((UseEffectsComponent)this.activeItemStack.getOrDefault(DataComponentTypes.USE_EFFECTS, (Object)UseEffectsComponent.DEFAULT)).canSprint();
    }

    private float getActiveItemSpeedMultiplier() {
        return ((UseEffectsComponent)this.activeItemStack.getOrDefault(DataComponentTypes.USE_EFFECTS, (Object)UseEffectsComponent.DEFAULT)).speedMultiplier();
    }

    public void clearActiveItem() {
        super.clearActiveItem();
        this.usingItem = false;
    }

    public Hand getActiveHand() {
        return Objects.requireNonNullElse(this.activeHand, Hand.MAIN_HAND);
    }

    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (LIVING_FLAGS.equals(data)) {
            Hand hand;
            boolean bl = ((Byte)this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
            Hand hand2 = hand = ((Byte)this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if (bl && !this.usingItem) {
                this.setCurrentHand(hand);
            } else if (!bl && this.usingItem) {
                this.clearActiveItem();
            }
        }
        if (FLAGS.equals(data) && this.isGliding() && !this.falling) {
            this.client.getSoundManager().play((SoundInstance)new ElytraSoundInstance(this));
        }
    }

    public @Nullable JumpingMount getJumpingMount() {
        JumpingMount jumpingMount;
        Entity entity = this.getControllingVehicle();
        return entity instanceof JumpingMount && (jumpingMount = (JumpingMount)entity).canJump() ? jumpingMount : null;
    }

    public float getMountJumpStrength() {
        return this.mountJumpStrength;
    }

    public boolean shouldFilterText() {
        return this.client.shouldFilterText();
    }

    public void openEditSignScreen(SignBlockEntity sign, boolean front) {
        if (sign instanceof HangingSignBlockEntity) {
            HangingSignBlockEntity hangingSignBlockEntity = (HangingSignBlockEntity)sign;
            this.client.setScreen((Screen)new HangingSignEditScreen((SignBlockEntity)hangingSignBlockEntity, front, this.client.shouldFilterText()));
        } else {
            this.client.setScreen((Screen)new SignEditScreen(sign, front, this.client.shouldFilterText()));
        }
    }

    public void openCommandBlockMinecartScreen(CommandBlockMinecartEntity minecart) {
        this.client.setScreen((Screen)new MinecartCommandBlockScreen(minecart));
    }

    public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {
        this.client.setScreen((Screen)new CommandBlockScreen(commandBlock));
    }

    public void openStructureBlockScreen(StructureBlockBlockEntity structureBlock) {
        this.client.setScreen((Screen)new StructureBlockScreen(structureBlock));
    }

    public void openTestBlockScreen(TestBlockEntity testBlock) {
        this.client.setScreen((Screen)new TestBlockScreen(testBlock));
    }

    public void openTestInstanceBlockScreen(TestInstanceBlockEntity testInstanceBlock) {
        this.client.setScreen((Screen)new TestInstanceBlockScreen(testInstanceBlock));
    }

    public void openJigsawScreen(JigsawBlockEntity jigsaw) {
        this.client.setScreen((Screen)new JigsawBlockScreen(jigsaw));
    }

    public void openDialog(RegistryEntry<Dialog> dialog) {
        this.networkHandler.showDialog(dialog, this.client.currentScreen);
    }

    public void useBook(ItemStack book, Hand hand) {
        WritableBookContentComponent writableBookContentComponent = (WritableBookContentComponent)book.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
        if (writableBookContentComponent != null) {
            this.client.setScreen((Screen)new BookEditScreen((PlayerEntity)this, book, hand, writableBookContentComponent));
        }
    }

    public void addCritParticles(Entity target) {
        this.client.particleManager.addEmitter(target, (ParticleEffect)ParticleTypes.CRIT);
    }

    public void addEnchantedHitParticles(Entity target) {
        this.client.particleManager.addEmitter(target, (ParticleEffect)ParticleTypes.ENCHANTED_HIT);
    }

    public boolean isSneaking() {
        return this.input.playerInput.sneak();
    }

    public boolean isInSneakingPose() {
        return this.inSneakingPose;
    }

    public boolean shouldSlowDown() {
        return this.isInSneakingPose() || this.isCrawling();
    }

    public void tickMovementInput() {
        if (this.isCamera()) {
            Vec2f vec2f = this.applyMovementSpeedFactors(this.input.getMovementInput());
            this.sidewaysSpeed = vec2f.x;
            this.forwardSpeed = vec2f.y;
            this.jumping = this.input.playerInput.jump();
            this.lastRenderYaw = this.renderYaw;
            this.lastRenderPitch = this.renderPitch;
            this.renderPitch += (this.getPitch() - this.renderPitch) * 0.5f;
            this.renderYaw += (this.getYaw() - this.renderYaw) * 0.5f;
        } else {
            super.tickMovementInput();
        }
    }

    private Vec2f applyMovementSpeedFactors(Vec2f input) {
        if (input.lengthSquared() == 0.0f) {
            return input;
        }
        Vec2f vec2f = input.multiply(0.98f);
        if (this.isUsingItem() && !this.hasVehicle()) {
            vec2f = vec2f.multiply(this.getActiveItemSpeedMultiplier());
        }
        if (this.shouldSlowDown()) {
            float f = (float)this.getAttributeValue(EntityAttributes.SNEAKING_SPEED);
            vec2f = vec2f.multiply(f);
        }
        return ClientPlayerEntity.applyDirectionalMovementSpeedFactors((Vec2f)vec2f);
    }

    private static Vec2f applyDirectionalMovementSpeedFactors(Vec2f vec) {
        float f = vec.length();
        if (f <= 0.0f) {
            return vec;
        }
        Vec2f vec2f = vec.multiply(1.0f / f);
        float g = ClientPlayerEntity.getDirectionalMovementSpeedMultiplier((Vec2f)vec2f);
        float h = Math.min(f * g, 1.0f);
        return vec2f.multiply(h);
    }

    private static float getDirectionalMovementSpeedMultiplier(Vec2f vec) {
        float f = Math.abs(vec.x);
        float g = Math.abs(vec.y);
        float h = g > f ? f / g : g / f;
        return MathHelper.sqrt((float)(1.0f + MathHelper.square((float)h)));
    }

    protected boolean isCamera() {
        return this.client.getCameraEntity() == this;
    }

    public void init() {
        this.setPose(EntityPose.STANDING);
        if (this.getEntityWorld() != null) {
            for (double d = this.getY(); d > (double)this.getEntityWorld().getBottomY() && d <= (double)this.getEntityWorld().getTopYInclusive(); d += 1.0) {
                this.setPosition(this.getX(), d, this.getZ());
                if (this.getEntityWorld().isSpaceEmpty((Entity)this)) break;
            }
            this.setVelocity(Vec3d.ZERO);
            this.setPitch(0.0f);
        }
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    public void tickMovement() {
        JumpingMount jumpingMount;
        int i;
        if (this.ticksLeftToDoubleTapSprint > 0) {
            --this.ticksLeftToDoubleTapSprint;
        }
        if (!(this.client.currentScreen instanceof LevelLoadingScreen)) {
            this.tickNausea(this.getCurrentPortalEffect() == Portal.Effect.CONFUSION);
            this.tickPortalCooldown();
        }
        boolean bl = this.input.playerInput.jump();
        boolean bl2 = this.input.playerInput.sneak();
        boolean bl3 = this.input.hasForwardMovement();
        PlayerAbilities playerAbilities = this.getAbilities();
        this.inSneakingPose = !playerAbilities.flying && !this.isSwimming() && !this.hasVehicle() && this.canChangeIntoPose(EntityPose.CROUCHING) && (this.isSneaking() || !this.isSleeping() && !this.canChangeIntoPose(EntityPose.STANDING));
        this.input.tick();
        this.client.getTutorialManager().onMovement(this.input);
        boolean bl4 = false;
        if (this.ticksToNextAutoJump > 0) {
            --this.ticksToNextAutoJump;
            bl4 = true;
            this.input.jump();
        }
        if (!this.noClip) {
            this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35, this.getZ() + (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35, this.getZ() - (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35, this.getZ() - (double)this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35, this.getZ() + (double)this.getWidth() * 0.35);
        }
        if (bl2 || this.isBlockedFromSprinting() && !this.hasVehicle() || this.input.playerInput.backward()) {
            this.ticksLeftToDoubleTapSprint = 0;
        }
        if (this.canStartSprinting()) {
            if (!bl3) {
                if (this.ticksLeftToDoubleTapSprint > 0) {
                    this.setSprinting(true);
                } else {
                    this.ticksLeftToDoubleTapSprint = (Integer)this.client.options.getSprintWindow().getValue();
                }
            }
            if (this.input.playerInput.sprint()) {
                this.setSprinting(true);
            }
        }
        if (this.isSprinting()) {
            if (this.isSwimming()) {
                if (this.shouldStopSwimSprinting()) {
                    this.setSprinting(false);
                }
            } else if (this.shouldStopSprinting()) {
                this.setSprinting(false);
            }
        }
        boolean bl5 = false;
        if (playerAbilities.allowFlying) {
            if (this.client.interactionManager.isFlyingLocked()) {
                if (!playerAbilities.flying) {
                    playerAbilities.flying = true;
                    bl5 = true;
                    this.sendAbilitiesUpdate();
                }
            } else if (!bl && this.input.playerInput.jump() && !bl4) {
                if (this.abilityResyncCountdown == 0) {
                    this.abilityResyncCountdown = 7;
                } else if (!(this.isSwimming() || this.getVehicle() != null && this.getJumpingMount() == null)) {
                    boolean bl6 = playerAbilities.flying = !playerAbilities.flying;
                    if (playerAbilities.flying && this.isOnGround()) {
                        this.jump();
                    }
                    bl5 = true;
                    this.sendAbilitiesUpdate();
                    this.abilityResyncCountdown = 0;
                }
            }
        }
        if (this.input.playerInput.jump() && !bl5 && !bl && !this.isClimbing() && this.checkGliding()) {
            this.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
        this.falling = this.isGliding();
        if (this.isTouchingWater() && this.input.playerInput.sneak() && this.shouldSwimInFluids()) {
            this.knockDownwards();
        }
        if (this.isSubmergedIn(FluidTags.WATER)) {
            i = this.isSpectator() ? 10 : 1;
            this.underwaterVisibilityTicks = MathHelper.clamp((int)(this.underwaterVisibilityTicks + i), (int)0, (int)600);
        } else if (this.underwaterVisibilityTicks > 0) {
            this.isSubmergedIn(FluidTags.WATER);
            this.underwaterVisibilityTicks = MathHelper.clamp((int)(this.underwaterVisibilityTicks - 10), (int)0, (int)600);
        }
        if (playerAbilities.flying && this.isCamera()) {
            i = 0;
            if (this.input.playerInput.sneak()) {
                --i;
            }
            if (this.input.playerInput.jump()) {
                ++i;
            }
            if (i != 0) {
                this.setVelocity(this.getVelocity().add(0.0, (double)((float)i * playerAbilities.getFlySpeed() * 3.0f), 0.0));
            }
        }
        if ((jumpingMount = this.getJumpingMount()) != null && jumpingMount.getJumpCooldown() == 0) {
            if (this.mountJumpTicks < 0) {
                ++this.mountJumpTicks;
                if (this.mountJumpTicks == 0) {
                    this.mountJumpStrength = 0.0f;
                }
            }
            if (bl && !this.input.playerInput.jump()) {
                this.mountJumpTicks = -10;
                jumpingMount.setJumpStrength(MathHelper.floor((float)(this.getMountJumpStrength() * 100.0f)));
                this.startRidingJump();
            } else if (!bl && this.input.playerInput.jump()) {
                this.mountJumpTicks = 0;
                this.mountJumpStrength = 0.0f;
            } else if (bl) {
                ++this.mountJumpTicks;
                this.mountJumpStrength = this.mountJumpTicks < 10 ? (float)this.mountJumpTicks * 0.1f : 0.8f + 2.0f / (float)(this.mountJumpTicks - 9) * 0.1f;
            }
        } else {
            this.mountJumpStrength = 0.0f;
        }
        super.tickMovement();
        if (this.isOnGround() && playerAbilities.flying && !this.client.interactionManager.isFlyingLocked()) {
            playerAbilities.flying = false;
            this.sendAbilitiesUpdate();
        }
    }

    private boolean shouldStopSprinting() {
        return !this.canSprint(this.getAbilities().flying) || !this.input.hasForwardMovement() || this.horizontalCollision && !this.collidedSoftly;
    }

    private boolean shouldStopSwimSprinting() {
        return !this.canSprint(true) || !this.isTouchingWater() || !this.input.hasForwardMovement() && !this.isOnGround() && !this.input.playerInput.sneak();
    }

    public Portal.Effect getCurrentPortalEffect() {
        return this.portalManager == null ? Portal.Effect.NONE : this.portalManager.getEffect();
    }

    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    private void tickNausea(boolean fromPortalEffect) {
        this.lastNauseaIntensity = this.nauseaIntensity;
        float f = 0.0f;
        if (fromPortalEffect && this.portalManager != null && this.portalManager.isInPortal()) {
            if (this.client.currentScreen != null && !this.client.currentScreen.keepOpenThroughPortal()) {
                if (this.client.currentScreen instanceof HandledScreen) {
                    this.closeHandledScreen();
                }
                this.client.setScreen(null);
            }
            if (this.nauseaIntensity == 0.0f) {
                this.client.getSoundManager().play((SoundInstance)PositionedSoundInstance.ambient((SoundEvent)SoundEvents.BLOCK_PORTAL_TRIGGER, (float)(this.random.nextFloat() * 0.4f + 0.8f), (float)0.25f));
            }
            f = 0.0125f;
            this.portalManager.setInPortal(false);
        } else if (this.nauseaIntensity > 0.0f) {
            f = -0.05f;
        }
        this.nauseaIntensity = MathHelper.clamp((float)(this.nauseaIntensity + f), (float)0.0f, (float)1.0f);
    }

    public void tickRiding() {
        super.tickRiding();
        this.riding = false;
        Entity entity = this.getControllingVehicle();
        if (entity instanceof AbstractBoatEntity) {
            AbstractBoatEntity abstractBoatEntity = (AbstractBoatEntity)entity;
            abstractBoatEntity.setInputs(this.input.playerInput.left(), this.input.playerInput.right(), this.input.playerInput.forward(), this.input.playerInput.backward());
            this.riding |= this.input.playerInput.left() || this.input.playerInput.right() || this.input.playerInput.forward() || this.input.playerInput.backward();
        }
    }

    public boolean isRiding() {
        return this.riding;
    }

    public void move(MovementType type, Vec3d movement) {
        double d = this.getX();
        double e = this.getZ();
        super.move(type, movement);
        float f = (float)(this.getX() - d);
        float g = (float)(this.getZ() - e);
        this.autoJump(f, g);
        this.addDistanceMoved(MathHelper.hypot((float)f, (float)g) * 0.6f);
    }

    public boolean isAutoJumpEnabled() {
        return this.autoJumpEnabled;
    }

    public boolean shouldRotateWithMinecart() {
        return (Boolean)this.client.options.getRotateWithMinecart().getValue();
    }

    protected void autoJump(float dx, float dz) {
        float j;
        if (!this.shouldAutoJump()) {
            return;
        }
        Vec3d vec3d = this.getEntityPos();
        Vec3d vec3d2 = vec3d.add((double)dx, 0.0, (double)dz);
        Vec3d vec3d3 = new Vec3d((double)dx, 0.0, (double)dz);
        float f = this.getMovementSpeed();
        float g = (float)vec3d3.lengthSquared();
        if (g <= 0.001f) {
            Vec2f vec2f = this.input.getMovementInput();
            float h = f * vec2f.x;
            float i = f * vec2f.y;
            j = MathHelper.sin((double)(this.getYaw() * ((float)Math.PI / 180)));
            float k = MathHelper.cos((double)(this.getYaw() * ((float)Math.PI / 180)));
            vec3d3 = new Vec3d((double)(h * k - i * j), vec3d3.y, (double)(i * k + h * j));
            g = (float)vec3d3.lengthSquared();
            if (g <= 0.001f) {
                return;
            }
        }
        float l = MathHelper.inverseSqrt((float)g);
        Vec3d vec3d4 = vec3d3.multiply((double)l);
        Vec3d vec3d5 = this.getRotationVecClient();
        j = (float)(vec3d5.x * vec3d4.x + vec3d5.z * vec3d4.z);
        if (j < -0.15f) {
            return;
        }
        ShapeContext shapeContext = ShapeContext.of((Entity)this);
        BlockPos blockPos = BlockPos.ofFloored((double)this.getX(), (double)this.getBoundingBox().maxY, (double)this.getZ());
        BlockState blockState = this.getEntityWorld().getBlockState(blockPos);
        if (!blockState.getCollisionShape((BlockView)this.getEntityWorld(), blockPos, shapeContext).isEmpty()) {
            return;
        }
        blockPos = blockPos.up();
        BlockState blockState2 = this.getEntityWorld().getBlockState(blockPos);
        if (!blockState2.getCollisionShape((BlockView)this.getEntityWorld(), blockPos, shapeContext).isEmpty()) {
            return;
        }
        float m = 7.0f;
        float n = 1.2f;
        if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            n += (float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.75f;
        }
        float o = Math.max(f * 7.0f, 1.0f / l);
        Vec3d vec3d6 = vec3d;
        Vec3d vec3d7 = vec3d2.add(vec3d4.multiply((double)o));
        float p = this.getWidth();
        float q = this.getHeight();
        Box box = new Box(vec3d6, vec3d7.add(0.0, (double)q, 0.0)).expand((double)p, 0.0, (double)p);
        vec3d6 = vec3d6.add(0.0, (double)0.51f, 0.0);
        vec3d7 = vec3d7.add(0.0, (double)0.51f, 0.0);
        Vec3d vec3d8 = vec3d4.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        Vec3d vec3d9 = vec3d8.multiply((double)(p * 0.5f));
        Vec3d vec3d10 = vec3d6.subtract(vec3d9);
        Vec3d vec3d11 = vec3d7.subtract(vec3d9);
        Vec3d vec3d12 = vec3d6.add(vec3d9);
        Vec3d vec3d13 = vec3d7.add(vec3d9);
        Iterable iterable = this.getEntityWorld().getCollisions((Entity)this, box);
        Iterator iterator = StreamSupport.stream(iterable.spliterator(), false).flatMap(shape -> shape.getBoundingBoxes().stream()).iterator();
        float r = Float.MIN_VALUE;
        while (iterator.hasNext()) {
            Box box2 = (Box)iterator.next();
            if (!box2.intersects(vec3d10, vec3d11) && !box2.intersects(vec3d12, vec3d13)) continue;
            r = (float)box2.maxY;
            Vec3d vec3d14 = box2.getCenter();
            BlockPos blockPos2 = BlockPos.ofFloored((Position)vec3d14);
            int s = 1;
            while ((float)s < n) {
                BlockPos blockPos3 = blockPos2.up(s);
                BlockState blockState3 = this.getEntityWorld().getBlockState(blockPos3);
                VoxelShape voxelShape = blockState3.getCollisionShape((BlockView)this.getEntityWorld(), blockPos3, shapeContext);
                if (!voxelShape.isEmpty() && (double)(r = (float)voxelShape.getMax(Direction.Axis.Y) + (float)blockPos3.getY()) - this.getY() > (double)n) {
                    return;
                }
                if (s > 1) {
                    blockPos = blockPos.up();
                    BlockState blockState4 = this.getEntityWorld().getBlockState(blockPos);
                    if (!blockState4.getCollisionShape((BlockView)this.getEntityWorld(), blockPos, shapeContext).isEmpty()) {
                        return;
                    }
                }
                ++s;
            }
            break block0;
        }
        if (r == Float.MIN_VALUE) {
            return;
        }
        float t = (float)((double)r - this.getY());
        if (t <= 0.5f || t > n) {
            return;
        }
        this.ticksToNextAutoJump = 1;
    }

    protected boolean hasCollidedSoftly(Vec3d adjustedMovement) {
        float f = this.getYaw() * ((float)Math.PI / 180);
        double d = MathHelper.sin((double)f);
        double e = MathHelper.cos((double)f);
        double g = (double)this.sidewaysSpeed * e - (double)this.forwardSpeed * d;
        double h = (double)this.forwardSpeed * e + (double)this.sidewaysSpeed * d;
        double i = MathHelper.square((double)g) + MathHelper.square((double)h);
        double j = MathHelper.square((double)adjustedMovement.x) + MathHelper.square((double)adjustedMovement.z);
        if (i < (double)1.0E-5f || j < (double)1.0E-5f) {
            return false;
        }
        double k = g * adjustedMovement.x + h * adjustedMovement.z;
        double l = Math.acos(k / Math.sqrt(i * j));
        return l < 0.13962633907794952;
    }

    private boolean shouldAutoJump() {
        return this.isAutoJumpEnabled() && this.ticksToNextAutoJump <= 0 && this.isOnGround() && !this.clipAtLedge() && !this.hasVehicle() && this.hasMovementInput() && (double)this.getJumpVelocityMultiplier() >= 1.0;
    }

    private boolean hasMovementInput() {
        return this.input.getMovementInput().lengthSquared() > 0.0f;
    }

    private boolean canSprint(boolean allowTouchingWater) {
        return !this.hasBlindnessEffect() && (this.hasVehicle() ? this.canVehicleSprint(this.getVehicle()) : this.canSprintOrFly()) && (allowTouchingWater || !this.isPartlyTouchingWater());
    }

    private boolean canStartSprinting() {
        return !(this.isSprinting() || !this.input.hasForwardMovement() || !this.canSprint(this.getAbilities().flying) || this.isBlockedFromSprinting() || this.isGliding() && !this.isSubmergedInWater() || this.shouldSlowDown() && !this.isSubmergedInWater());
    }

    private boolean canVehicleSprint(Entity vehicle) {
        return vehicle.canSprintAsVehicle() && vehicle.isLogicalSideForUpdatingMovement();
    }

    public float getUnderwaterVisibility() {
        if (!this.isSubmergedIn(FluidTags.WATER)) {
            return 0.0f;
        }
        float f = 600.0f;
        float g = 100.0f;
        if ((float)this.underwaterVisibilityTicks >= 600.0f) {
            return 1.0f;
        }
        float h = MathHelper.clamp((float)((float)this.underwaterVisibilityTicks / 100.0f), (float)0.0f, (float)1.0f);
        float i = (float)this.underwaterVisibilityTicks < 100.0f ? 0.0f : MathHelper.clamp((float)(((float)this.underwaterVisibilityTicks - 100.0f) / 500.0f), (float)0.0f, (float)1.0f);
        return h * 0.6f + i * 0.39999998f;
    }

    public void onGameModeChanged(GameMode gameMode) {
        if (gameMode == GameMode.SPECTATOR) {
            this.setVelocity(this.getVelocity().withAxis(Direction.Axis.Y, 0.0));
        }
    }

    public boolean isSubmergedInWater() {
        return this.isSubmergedInWater;
    }

    protected boolean updateWaterSubmersionState() {
        boolean bl = this.isSubmergedInWater;
        boolean bl2 = super.updateWaterSubmersionState();
        if (this.isSpectator()) {
            return this.isSubmergedInWater;
        }
        if (!bl && bl2) {
            this.getEntityWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
            this.client.getSoundManager().play((SoundInstance)new AmbientSoundLoops.Underwater(this));
        }
        if (bl && !bl2) {
            this.getEntityWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
        }
        return this.isSubmergedInWater;
    }

    public Vec3d getLeashPos(float tickProgress) {
        if (this.client.options.getPerspective().isFirstPerson()) {
            float f = MathHelper.lerp((float)(tickProgress * 0.5f), (float)this.getYaw(), (float)this.lastYaw) * ((float)Math.PI / 180);
            float g = MathHelper.lerp((float)(tickProgress * 0.5f), (float)this.getPitch(), (float)this.lastPitch) * ((float)Math.PI / 180);
            double d = this.getMainArm() == Arm.RIGHT ? -1.0 : 1.0;
            Vec3d vec3d = new Vec3d(0.39 * d, -0.6, 0.3);
            return vec3d.rotateX(-g).rotateY(-f).add(this.getCameraPosVec(tickProgress));
        }
        return super.getLeashPos(tickProgress);
    }

    public void onPickupSlotClick(ItemStack cursorStack, ItemStack slotStack, ClickType clickType) {
        this.client.getTutorialManager().onPickupSlotClick(cursorStack, slotStack, clickType);
    }

    public float getBodyYaw() {
        return this.getYaw();
    }

    public void dropCreativeStack(ItemStack stack) {
        this.client.interactionManager.dropCreativeStack(stack);
    }

    public boolean canDropItems() {
        return this.itemDropCooldown.canUse();
    }

    public Cooldown getItemDropCooldown() {
        return this.itemDropCooldown;
    }

    public PlayerInput getLastPlayerInput() {
        return this.lastPlayerInput;
    }

    public HitResult getCrosshairTarget(float tickProgress, Entity cameraEntity) {
        ItemStack itemStack = this.getActiveOrMainHandStack();
        AttackRangeComponent attackRangeComponent = (AttackRangeComponent)itemStack.get(DataComponentTypes.ATTACK_RANGE);
        double d = this.getBlockInteractionRange();
        HitResult hitResult = null;
        if (attackRangeComponent != null && (hitResult = attackRangeComponent.getHitResult(cameraEntity, tickProgress, EntityPredicates.CAN_HIT)) instanceof BlockHitResult) {
            hitResult = ClientPlayerEntity.checkCrosshairTargetRange((HitResult)hitResult, (Vec3d)cameraEntity.getCameraPosVec(tickProgress), (double)d);
        }
        if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
            double e = this.getEntityInteractionRange();
            hitResult = ClientPlayerEntity.getCrosshairTarget((Entity)cameraEntity, (double)d, (double)e, (float)tickProgress);
        }
        return hitResult;
    }

    private static HitResult getCrosshairTarget(Entity cameraEntity, double blockInteractionRange, double entityInteractionRange, float tickProgress) {
        double d = Math.max(blockInteractionRange, entityInteractionRange);
        double e = MathHelper.square((double)d);
        Vec3d vec3d = cameraEntity.getCameraPosVec(tickProgress);
        HitResult hitResult = cameraEntity.raycast(d, tickProgress, false);
        double f = hitResult.getPos().squaredDistanceTo(vec3d);
        if (hitResult.getType() != HitResult.Type.MISS) {
            e = f;
            d = Math.sqrt(e);
        }
        Vec3d vec3d2 = cameraEntity.getRotationVec(tickProgress);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float g = 1.0f;
        Box box = cameraEntity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast((Entity)cameraEntity, (Vec3d)vec3d, (Vec3d)vec3d3, (Box)box, (Predicate)EntityPredicates.CAN_HIT, (double)e);
        if (entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(vec3d) < f) {
            return ClientPlayerEntity.checkCrosshairTargetRange((HitResult)entityHitResult, (Vec3d)vec3d, (double)entityInteractionRange);
        }
        return ClientPlayerEntity.checkCrosshairTargetRange((HitResult)hitResult, (Vec3d)vec3d, (double)blockInteractionRange);
    }

    private static HitResult checkCrosshairTargetRange(HitResult hitResult, Vec3d cameraPos, double range) {
        Vec3d vec3d = hitResult.getPos();
        if (!vec3d.isInRange((Position)cameraPos, range)) {
            Vec3d vec3d2 = hitResult.getPos();
            Direction direction = Direction.getFacing((double)(vec3d2.x - cameraPos.x), (double)(vec3d2.y - cameraPos.y), (double)(vec3d2.z - cameraPos.z));
            return BlockHitResult.createMissed((Vec3d)vec3d2, (Direction)direction, (BlockPos)BlockPos.ofFloored((Position)vec3d2));
        }
        return hitResult;
    }
}

