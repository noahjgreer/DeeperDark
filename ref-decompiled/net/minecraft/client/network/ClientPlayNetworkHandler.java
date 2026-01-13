/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.hash.HashCode
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.advancement.AdvancementEntry
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.CommandBlockBlockEntity
 *  net.minecraft.block.entity.SignBlockEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.ChatHud$ChatMethod
 *  net.minecraft.client.gui.hud.ChatHud$ChatState
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.CreditsScreen
 *  net.minecraft.client.gui.screen.DeathScreen
 *  net.minecraft.client.gui.screen.DeathScreen$TitleScreenConfirmScreen
 *  net.minecraft.client.gui.screen.DemoScreen
 *  net.minecraft.client.gui.screen.ReconfiguringScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.StatsScreen
 *  net.minecraft.client.gui.screen.dialog.DialogNetworkAccess
 *  net.minecraft.client.gui.screen.ingame.BookScreen
 *  net.minecraft.client.gui.screen.ingame.BookScreen$Contents
 *  net.minecraft.client.gui.screen.ingame.CommandBlockScreen
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
 *  net.minecraft.client.gui.screen.ingame.HandledScreens
 *  net.minecraft.client.gui.screen.ingame.HorseScreen
 *  net.minecraft.client.gui.screen.ingame.NautilusScreen
 *  net.minecraft.client.gui.screen.ingame.TestInstanceBlockScreen
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookProvider
 *  net.minecraft.client.gui.screen.world.LevelLoadingScreen
 *  net.minecraft.client.gui.screen.world.LevelLoadingScreen$WorldEntryReason
 *  net.minecraft.client.input.KeyboardInput
 *  net.minecraft.client.network.ChunkBatchSizeCalculator
 *  net.minecraft.client.network.ClientAdvancementManager
 *  net.minecraft.client.network.ClientCommandSource
 *  net.minecraft.client.network.ClientCommonNetworkHandler
 *  net.minecraft.client.network.ClientConfigurationNetworkHandler
 *  net.minecraft.client.network.ClientConnectionState
 *  net.minecraft.client.network.ClientDebugSubscriptionManager
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.ClientPlayNetworkHandler$3
 *  net.minecraft.client.network.ClientPlayNetworkHandler$CommandRunResult
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.client.network.DataQueryHandler
 *  net.minecraft.client.network.OtherClientPlayerEntity
 *  net.minecraft.client.network.PingMeasurer
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.ServerList
 *  net.minecraft.client.particle.ItemPickupParticle
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.recipebook.ClientRecipeBook
 *  net.minecraft.client.recipebook.ClientRecipeManager
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.search.SearchManager
 *  net.minecraft.client.sound.AggressiveBeeSoundInstance
 *  net.minecraft.client.sound.GuardianAttackSoundInstance
 *  net.minecraft.client.sound.MovingMinecartSoundInstance
 *  net.minecraft.client.sound.PassiveBeeSoundInstance
 *  net.minecraft.client.sound.SnifferDigSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.TickableSoundInstance
 *  net.minecraft.client.toast.RecipeToast
 *  net.minecraft.client.toast.SystemToast
 *  net.minecraft.client.toast.SystemToast$Type
 *  net.minecraft.client.toast.Toast
 *  net.minecraft.client.toast.ToastManager
 *  net.minecraft.client.world.ClientChunkLoadProgress
 *  net.minecraft.client.world.ClientWaypointHandler
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.client.world.ClientWorld$Properties
 *  net.minecraft.client.world.DataCache
 *  net.minecraft.command.CommandRegistryAccess
 *  net.minecraft.command.argument.SignedArgumentList
 *  net.minecraft.command.permission.Permission
 *  net.minecraft.command.permission.Permission$Atom
 *  net.minecraft.command.permission.PermissionCheck
 *  net.minecraft.command.permission.PermissionCheck$Require
 *  net.minecraft.command.permission.PermissionPredicate
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.MapIdComponent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.EntityPosition
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.ExperienceOrbEntity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.Leashable
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.TrackedPosition
 *  net.minecraft.entity.attribute.AttributeContainer
 *  net.minecraft.entity.attribute.EntityAttributeInstance
 *  net.minecraft.entity.attribute.EntityAttributeModifier
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.mob.GuardianEntity
 *  net.minecraft.entity.passive.AbstractHorseEntity
 *  net.minecraft.entity.passive.AbstractNautilusEntity
 *  net.minecraft.entity.passive.BeeEntity
 *  net.minecraft.entity.passive.SnifferEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.entity.projectile.ExplosiveProjectileEntity
 *  net.minecraft.entity.vehicle.AbstractBoatEntity
 *  net.minecraft.entity.vehicle.AbstractMinecartEntity
 *  net.minecraft.entity.vehicle.ExperimentalMinecartController
 *  net.minecraft.entity.vehicle.MinecartController
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.SimpleInventory
 *  net.minecraft.item.FuelRegistry
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemGroups
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.map.MapState
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.network.NetworkThreadUtils
 *  net.minecraft.network.PacketApplyBatcher
 *  net.minecraft.network.encryption.ClientPlayerSession
 *  net.minecraft.network.encryption.NetworkEncryptionUtils$SecureRandomUtil
 *  net.minecraft.network.encryption.PlayerKeyPair
 *  net.minecraft.network.encryption.PlayerPublicKey$PublicKeyException
 *  net.minecraft.network.encryption.PublicPlayerSession
 *  net.minecraft.network.encryption.PublicPlayerSession$Serialized
 *  net.minecraft.network.encryption.SignatureVerifier
 *  net.minecraft.network.listener.ClientPlayPacketListener
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.network.listener.TickablePacketListener
 *  net.minecraft.network.message.ArgumentSignatureDataMap
 *  net.minecraft.network.message.LastSeenMessagesCollector
 *  net.minecraft.network.message.LastSeenMessagesCollector$LastSeenMessages
 *  net.minecraft.network.message.MessageBody
 *  net.minecraft.network.message.MessageChain$Packer
 *  net.minecraft.network.message.MessageLink
 *  net.minecraft.network.message.MessageSignatureData
 *  net.minecraft.network.message.MessageSignatureStorage
 *  net.minecraft.network.message.SignedMessage
 *  net.minecraft.network.packet.CustomPayload
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket
 *  net.minecraft.network.packet.c2s.common.SyncedClientOptions
 *  net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket
 *  net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket
 *  net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket
 *  net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket$Mode
 *  net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket
 *  net.minecraft.network.packet.c2s.play.MessageAcknowledgmentC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerLoadedC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$Full
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$LookAndOnGround
 *  net.minecraft.network.packet.c2s.play.PlayerSessionC2SPacket
 *  net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket
 *  net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket
 *  net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket
 *  net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.BlockEventS2CPacket
 *  net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.BlockValueDebugS2CPacket
 *  net.minecraft.network.packet.s2c.play.BossBarS2CPacket
 *  net.minecraft.network.packet.s2c.play.BundleS2CPacket
 *  net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket
 *  net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket
 *  net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket$Serialized
 *  net.minecraft.network.packet.s2c.play.ChunkData
 *  net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket
 *  net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket
 *  net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket
 *  net.minecraft.network.packet.s2c.play.ChunkSentS2CPacket
 *  net.minecraft.network.packet.s2c.play.ChunkValueDebugS2CPacket
 *  net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket
 *  net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
 *  net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket
 *  net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket
 *  net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket$NodeFactory
 *  net.minecraft.network.packet.s2c.play.CommonPlayerSpawnInfo
 *  net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket
 *  net.minecraft.network.packet.s2c.play.DamageTiltS2CPacket
 *  net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.DebugSampleS2CPacket
 *  net.minecraft.network.packet.s2c.play.DifficultyS2CPacket
 *  net.minecraft.network.packet.s2c.play.EndCombatS2CPacket
 *  net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket
 *  net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket$Entry
 *  net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityValueDebugS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.EventDebugS2CPacket
 *  net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
 *  net.minecraft.network.packet.s2c.play.GameJoinS2CPacket
 *  net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket
 *  net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket$Reason
 *  net.minecraft.network.packet.s2c.play.GameTestHighlightPosS2CPacket
 *  net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.InventoryS2CPacket
 *  net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket
 *  net.minecraft.network.packet.s2c.play.LightData
 *  net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.LookAtS2CPacket
 *  net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.MoveMinecartAlongTrackS2CPacket
 *  net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket
 *  net.minecraft.network.packet.s2c.play.OpenMountScreenS2CPacket
 *  net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
 *  net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket
 *  net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.ParticleS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerListS2CPacket$Action
 *  net.minecraft.network.packet.s2c.play.PlayerListS2CPacket$Entry
 *  net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerRotationS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket
 *  net.minecraft.network.packet.s2c.play.PositionFlag
 *  net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.ProjectilePowerS2CPacket
 *  net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket
 *  net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket$Entry
 *  net.minecraft.network.packet.s2c.play.RecipeBookRemoveS2CPacket
 *  net.minecraft.network.packet.s2c.play.RecipeBookSettingsS2CPacket
 *  net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket
 *  net.minecraft.network.packet.s2c.play.RemoveMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket
 *  net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.ScoreboardScoreResetS2CPacket
 *  net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket
 *  net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket
 *  net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket
 *  net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket
 *  net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket
 *  net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket
 *  net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket
 *  net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket
 *  net.minecraft.network.packet.s2c.play.StartChunkSendS2CPacket
 *  net.minecraft.network.packet.s2c.play.StatisticsS2CPacket
 *  net.minecraft.network.packet.s2c.play.StopSoundS2CPacket
 *  net.minecraft.network.packet.s2c.play.SubtitleS2CPacket
 *  net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket
 *  net.minecraft.network.packet.s2c.play.TeamS2CPacket
 *  net.minecraft.network.packet.s2c.play.TeamS2CPacket$Operation
 *  net.minecraft.network.packet.s2c.play.TestInstanceBlockStatusS2CPacket
 *  net.minecraft.network.packet.s2c.play.TickStepS2CPacket
 *  net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket
 *  net.minecraft.network.packet.s2c.play.TitleS2CPacket
 *  net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket
 *  net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket
 *  net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket
 *  net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket
 *  net.minecraft.network.packet.s2c.play.WaypointS2CPacket
 *  net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket
 *  net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket
 *  net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket
 *  net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket
 *  net.minecraft.network.packet.s2c.play.WorldBorderWarningBlocksChangedS2CPacket
 *  net.minecraft.network.packet.s2c.play.WorldBorderWarningTimeChangedS2CPacket
 *  net.minecraft.network.packet.s2c.play.WorldEventS2CPacket
 *  net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.query.PingResultS2CPacket
 *  net.minecraft.network.state.ConfigurationStates
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.recipe.BrewingRecipeRegistry
 *  net.minecraft.recipe.NetworkRecipeId
 *  net.minecraft.recipe.RecipeManager
 *  net.minecraft.recipe.display.CuttingRecipeDisplay$Grouping
 *  net.minecraft.recipe.display.RecipeDisplay
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.Registry$PendingTagLoad
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryOps
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.SerializableRegistries
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.TagPacketSerializer$Serialized
 *  net.minecraft.resource.featuretoggle.FeatureSet
 *  net.minecraft.scoreboard.ScoreAccess
 *  net.minecraft.scoreboard.ScoreHolder
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.scoreboard.ScoreboardCriterion
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.scoreboard.number.NumberFormat
 *  net.minecraft.screen.HorseScreenHandler
 *  net.minecraft.screen.MerchantScreenHandler
 *  net.minecraft.screen.MountScreenHandler
 *  net.minecraft.screen.NautilusScreenHandler
 *  net.minecraft.screen.PlayerScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.ScreenHandlerType
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.screen.sync.ComponentChangesHash$ComponentHasher
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stat
 *  net.minecraft.stat.StatHandler
 *  net.minecraft.storage.NbtReadView
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.ErrorReporter
 *  net.minecraft.util.ErrorReporter$Logging
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.dynamic.HashCodeOps
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.GameMode
 *  net.minecraft.world.LightType
 *  net.minecraft.world.World
 *  net.minecraft.world.border.WorldBorder
 *  net.minecraft.world.chunk.ChunkNibbleArray
 *  net.minecraft.world.chunk.ChunkSection
 *  net.minecraft.world.chunk.WorldChunk
 *  net.minecraft.world.chunk.light.LightingProvider
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.tick.TickManager
 *  net.minecraft.world.waypoint.TrackedWaypointHandler
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.ingame.NautilusScreen;
import net.minecraft.client.gui.screen.ingame.TestInstanceBlockScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ChunkBatchSizeCalculator;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientDebugSubscriptionManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PingMeasurer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.ClientRecipeManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.sound.AggressiveBeeSoundInstance;
import net.minecraft.client.sound.GuardianAttackSoundInstance;
import net.minecraft.client.sound.MovingMinecartSoundInstance;
import net.minecraft.client.sound.PassiveBeeSoundInstance;
import net.minecraft.client.sound.SnifferDigSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.world.ClientChunkLoadProgress;
import net.minecraft.client.world.ClientWaypointHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DataCache;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionCheck;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AbstractNautilusEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketApplyBatcher;
import net.minecraft.network.encryption.ClientPlayerSession;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageSignatureStorage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.MessageAcknowledgmentC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerLoadedC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerSessionC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockValueDebugS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkSentS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkValueDebugS2CPacket;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.CommonPlayerSpawnInfo;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.DamageTiltS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DebugSampleS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityValueDebugS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EventDebugS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.GameTestHighlightPosS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MoveMinecartAlongTrackS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenMountScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRotationS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProjectilePowerS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookSettingsS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreResetS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.StartChunkSendS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TestInstanceBlockStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.TickStepS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WaypointS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningBlocksChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningTimeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.MountScreenHandler;
import net.minecraft.screen.NautilusScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.storage.NbtReadView;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.HashCodeOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.tick.TickManager;
import net.minecraft.world.waypoint.TrackedWaypointHandler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ClientPlayNetworkHandler
extends ClientCommonNetworkHandler
implements ClientPlayPacketListener,
TickablePacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text UNSECURE_SERVER_TOAST_TITLE = Text.translatable((String)"multiplayer.unsecureserver.toast.title");
    private static final Text UNSECURE_SERVER_TOAST_TEXT = Text.translatable((String)"multiplayer.unsecureserver.toast");
    private static final Text INVALID_PACKET_TEXT = Text.translatable((String)"multiplayer.disconnect.invalid_packet");
    private static final Text RECONFIGURING_TEXT = Text.translatable((String)"connect.reconfiguring");
    private static final Text BAD_CHAT_INDEX_TEXT = Text.translatable((String)"multiplayer.disconnect.bad_chat_index");
    private static final Text CONFIRM_COMMAND_TITLE_TEXT = Text.translatable((String)"multiplayer.confirm_command.title");
    private static final Text CONFIRM_RUN_COMMAND_TEXT = Text.translatable((String)"multiplayer.confirm_command.run_command");
    private static final Text CONFIRM_SUGGEST_COMMAND_TEXT = Text.translatable((String)"multiplayer.confirm_command.suggest_command");
    private static final int ACKNOWLEDGMENT_BATCH_SIZE = 64;
    public static final int field_54852 = 64;
    private static final Permission RESTRICTED_PERMISSION = Permission.Atom.ofVanilla((String)"client/commands/restricted");
    static final PermissionCheck RESTRICTED_PERMISSION_CHECK = new PermissionCheck.Require(RESTRICTED_PERMISSION);
    private static final PermissionPredicate RESTRICTED_PERMISSION_PREDICATE = permission -> permission.equals((Object)RESTRICTED_PERMISSION);
    private static final CommandTreeS2CPacket.NodeFactory<ClientCommandSource> COMMAND_NODE_FACTORY = new /* Unavailable Anonymous Inner Class!! */;
    private final GameProfile profile;
    private ClientWorld world;
    private ClientWorld.Properties worldProperties;
    private final Map<UUID, PlayerListEntry> playerListEntries = Maps.newHashMap();
    private final Set<PlayerListEntry> listedPlayerListEntries = new ReferenceOpenHashSet();
    private final ClientAdvancementManager advancementHandler;
    private final ClientCommandSource commandSource;
    private final ClientCommandSource restrictedCommandSource;
    private final DataQueryHandler dataQueryHandler = new DataQueryHandler(this);
    private int chunkLoadDistance = 3;
    private int simulationDistance = 3;
    private final Random random = Random.createThreadSafe();
    private CommandDispatcher<ClientCommandSource> commandDispatcher = new CommandDispatcher();
    private ClientRecipeManager recipeManager = new ClientRecipeManager(Map.of(), CuttingRecipeDisplay.Grouping.empty());
    private final UUID sessionId = UUID.randomUUID();
    private Set<RegistryKey<World>> worldKeys;
    private final DynamicRegistryManager.Immutable combinedDynamicRegistries;
    private final FeatureSet enabledFeatures;
    private final BrewingRecipeRegistry brewingRecipeRegistry;
    private FuelRegistry fuelRegistry;
    private final ComponentChangesHash.ComponentHasher componentHasher;
    private OptionalInt removedPlayerVehicleId = OptionalInt.empty();
    private @Nullable ClientPlayerSession session;
    private MessageChain.Packer messagePacker = MessageChain.Packer.NONE;
    private int globalChatMessageIndex;
    private LastSeenMessagesCollector lastSeenMessagesCollector = new LastSeenMessagesCollector(20);
    private MessageSignatureStorage signatureStorage = MessageSignatureStorage.create();
    private @Nullable CompletableFuture<Optional<PlayerKeyPair>> profileKeyPairFuture;
    private @Nullable SyncedClientOptions syncedOptions;
    private final ChunkBatchSizeCalculator chunkBatchSizeCalculator = new ChunkBatchSizeCalculator();
    private final PingMeasurer pingMeasurer;
    private final ClientDebugSubscriptionManager debugSubscriptionManager;
    private @Nullable ClientChunkLoadProgress chunkLoadProgress;
    private boolean secureChatEnforced;
    private volatile boolean worldCleared;
    private final Scoreboard scoreboard = new Scoreboard();
    private final ClientWaypointHandler waypointHandler = new ClientWaypointHandler();
    private final SearchManager searchManager = new SearchManager();
    private final List<WeakReference<DataCache<?, ?>>> cachedData = new ArrayList();
    private boolean loaded;

    public ClientPlayNetworkHandler(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
        super(client, clientConnection, clientConnectionState);
        this.profile = clientConnectionState.localGameProfile();
        this.combinedDynamicRegistries = clientConnectionState.receivedRegistries();
        RegistryOps registryOps = this.combinedDynamicRegistries.getOps((DynamicOps)HashCodeOps.INSTANCE);
        this.componentHasher = component -> ((HashCode)component.encode((DynamicOps)registryOps).getOrThrow(error -> new IllegalArgumentException("Failed to hash " + String.valueOf(component) + ": " + error))).asInt();
        this.enabledFeatures = clientConnectionState.enabledFeatures();
        this.advancementHandler = new ClientAdvancementManager(client, this.worldSession);
        PermissionPredicate permissionPredicate = permission -> {
            ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
            return clientPlayerEntity != null && clientPlayerEntity.getPermissions().hasPermission(permission);
        };
        this.commandSource = new ClientCommandSource(this, client, permissionPredicate.or(RESTRICTED_PERMISSION_PREDICATE));
        this.restrictedCommandSource = new ClientCommandSource(this, client, PermissionPredicate.NONE);
        this.pingMeasurer = new PingMeasurer(this, client.getDebugHud().getPingLog());
        this.debugSubscriptionManager = new ClientDebugSubscriptionManager(this, client.getDebugHud());
        if (clientConnectionState.chatState() != null) {
            client.inGameHud.getChatHud().restoreChatState(clientConnectionState.chatState());
        }
        this.brewingRecipeRegistry = BrewingRecipeRegistry.create((FeatureSet)this.enabledFeatures);
        this.fuelRegistry = FuelRegistry.createDefault((RegistryWrapper.WrapperLookup)clientConnectionState.receivedRegistries(), (FeatureSet)this.enabledFeatures);
        this.chunkLoadProgress = clientConnectionState.chunkLoadProgress();
    }

    public ClientCommandSource getCommandSource() {
        return this.commandSource;
    }

    public void unloadWorld() {
        this.worldCleared = true;
        this.clearWorld();
        this.worldSession.onUnload();
    }

    public void clearWorld() {
        this.clearCachedData();
        this.world = null;
        this.chunkLoadProgress = null;
    }

    private void clearCachedData() {
        for (WeakReference weakReference : this.cachedData) {
            DataCache dataCache = (DataCache)weakReference.get();
            if (dataCache == null) continue;
            dataCache.clean();
        }
        this.cachedData.clear();
    }

    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }

    public void onGameJoin(GameJoinS2CPacket packet) {
        ClientWorld.Properties properties;
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
        CommonPlayerSpawnInfo commonPlayerSpawnInfo = packet.commonPlayerSpawnInfo();
        ArrayList list = Lists.newArrayList((Iterable)packet.dimensionIds());
        Collections.shuffle(list);
        this.worldKeys = Sets.newLinkedHashSet((Iterable)list);
        RegistryKey registryKey = commonPlayerSpawnInfo.dimension();
        RegistryEntry registryEntry = commonPlayerSpawnInfo.dimensionType();
        this.chunkLoadDistance = packet.viewDistance();
        this.simulationDistance = packet.simulationDistance();
        boolean bl = commonPlayerSpawnInfo.isDebug();
        boolean bl2 = commonPlayerSpawnInfo.isFlat();
        int i = commonPlayerSpawnInfo.seaLevel();
        this.worldProperties = properties = new ClientWorld.Properties(Difficulty.NORMAL, packet.hardcore(), bl2);
        this.world = new ClientWorld(this, properties, registryKey, registryEntry, this.chunkLoadDistance, this.simulationDistance, this.client.worldRenderer, bl, commonPlayerSpawnInfo.seed(), i);
        this.client.joinWorld(this.world);
        if (this.client.player == null) {
            this.client.player = this.client.interactionManager.createPlayer(this.world, new StatHandler(), new ClientRecipeBook());
            this.client.player.setYaw(-180.0f);
            if (this.client.getServer() != null) {
                this.client.getServer().setLocalPlayerUuid(this.client.player.getUuid());
            }
        }
        this.setLoaded(false);
        this.debugSubscriptionManager.clearAllSubscriptions();
        this.client.worldRenderer.debugRenderer.initRenderers();
        this.client.player.init();
        this.client.player.setId(packet.playerEntityId());
        this.world.addEntity((Entity)this.client.player);
        this.client.player.input = new KeyboardInput(this.client.options);
        this.client.interactionManager.copyAbilities((PlayerEntity)this.client.player);
        this.client.setCameraEntity((Entity)this.client.player);
        this.startWorldLoading(this.client.player, this.world, LevelLoadingScreen.WorldEntryReason.OTHER);
        this.client.player.setReducedDebugInfo(packet.reducedDebugInfo());
        this.client.player.setShowsDeathScreen(packet.showDeathScreen());
        this.client.player.setLimitedCraftingEnabled(packet.doLimitedCrafting());
        this.client.player.setLastDeathPos(commonPlayerSpawnInfo.lastDeathLocation());
        this.client.player.setPortalCooldown(commonPlayerSpawnInfo.portalCooldown());
        this.client.interactionManager.setGameModes(commonPlayerSpawnInfo.gameMode(), commonPlayerSpawnInfo.lastGameMode());
        this.client.options.setServerViewDistance(packet.viewDistance());
        this.session = null;
        this.messagePacker = MessageChain.Packer.NONE;
        this.globalChatMessageIndex = 0;
        this.lastSeenMessagesCollector = new LastSeenMessagesCollector(20);
        this.signatureStorage = MessageSignatureStorage.create();
        if (this.connection.isEncrypted()) {
            this.fetchProfileKey();
        }
        this.worldSession.setGameMode(commonPlayerSpawnInfo.gameMode(), packet.hardcore());
        this.client.getQuickPlayLogger().save(this.client);
        this.secureChatEnforced = packet.enforcesSecureChat();
        if (this.serverInfo != null && !this.seenInsecureChatWarning && !this.isSecureChatEnforced()) {
            SystemToast systemToast = SystemToast.create((MinecraftClient)this.client, (SystemToast.Type)SystemToast.Type.UNSECURE_SERVER_WARNING, (Text)UNSECURE_SERVER_TOAST_TITLE, (Text)UNSECURE_SERVER_TOAST_TEXT);
            this.client.getToastManager().add((Toast)systemToast);
            this.seenInsecureChatWarning = true;
        }
    }

    public void onEntitySpawn(EntitySpawnS2CPacket packet) {
        PlayerEntity playerEntity;
        UUID uUID;
        PlayerListEntry playerListEntry;
        Entity entity;
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.getEntityId()) {
            this.removedPlayerVehicleId = OptionalInt.empty();
        }
        if ((entity = this.createEntity(packet)) != null) {
            entity.onSpawnPacket(packet);
            this.world.addEntity(entity);
            this.playSpawnSound(entity);
        } else {
            LOGGER.warn("Skipping Entity with id {}", (Object)packet.getEntityType());
        }
        if (entity instanceof PlayerEntity && (playerListEntry = (PlayerListEntry)this.playerListEntries.get(uUID = (playerEntity = (PlayerEntity)entity).getUuid())) != null) {
            this.seenPlayers.put(uUID, playerListEntry);
        }
    }

    private @Nullable Entity createEntity(EntitySpawnS2CPacket packet) {
        EntityType entityType = packet.getEntityType();
        if (entityType == EntityType.PLAYER) {
            PlayerListEntry playerListEntry = this.getPlayerListEntry(packet.getUuid());
            if (playerListEntry == null) {
                LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", (Object)packet.getUuid());
                return null;
            }
            return new OtherClientPlayerEntity(this.world, playerListEntry.getProfile());
        }
        return entityType.create((World)this.world, SpawnReason.LOAD);
    }

    private void playSpawnSound(Entity entity) {
        if (entity instanceof AbstractMinecartEntity) {
            AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)entity;
            this.client.getSoundManager().play((SoundInstance)new MovingMinecartSoundInstance(abstractMinecartEntity));
        } else if (entity instanceof BeeEntity) {
            BeeEntity beeEntity = (BeeEntity)entity;
            boolean bl = beeEntity.hasAngerTime();
            Object abstractBeeSoundInstance = bl ? new AggressiveBeeSoundInstance(beeEntity) : new PassiveBeeSoundInstance(beeEntity);
            this.client.getSoundManager().playNextTick((TickableSoundInstance)abstractBeeSoundInstance);
        }
    }

    public void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (entity == null) {
            return;
        }
        entity.setVelocityClient(packet.getVelocity());
    }

    public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.id());
        if (entity != null) {
            entity.getDataTracker().writeUpdatedEntries(packet.trackedValues());
        }
    }

    public void onEntityPositionSync(EntityPositionSyncS2CPacket packet) {
        boolean bl;
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.id());
        if (entity == null) {
            return;
        }
        Vec3d vec3d = packet.values().position();
        entity.getTrackedPosition().setPos(vec3d);
        if (entity.isLogicalSideForUpdatingMovement()) {
            return;
        }
        float f = packet.values().yaw();
        float g = packet.values().pitch();
        boolean bl2 = bl = entity.getEntityPos().squaredDistanceTo(vec3d) > 4096.0;
        if (this.world.hasEntity(entity) && !bl) {
            entity.updateTrackedPositionAndAngles(vec3d, f, g);
        } else {
            entity.refreshPositionAndAngles(vec3d, f, g);
        }
        if (!entity.isInterpolating() && entity.hasPassengerDeep((Entity)this.client.player)) {
            entity.updatePassengerPosition((Entity)this.client.player);
            this.client.player.resetPosition();
        }
        entity.setOnGround(packet.onGround());
    }

    public void onEntityPosition(EntityPositionS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.entityId());
        if (entity == null) {
            if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.entityId()) {
                LOGGER.debug("Trying to teleport entity with id {}, that was formerly player vehicle, applying teleport to player instead", (Object)packet.entityId());
                ClientPlayNetworkHandler.setPosition((EntityPosition)packet.change(), (Set)packet.relatives(), (Entity)this.client.player, (boolean)false);
                this.connection.send((Packet)new PlayerMoveC2SPacket.Full(this.client.player.getX(), this.client.player.getY(), this.client.player.getZ(), this.client.player.getYaw(), this.client.player.getPitch(), false, false));
            }
            return;
        }
        boolean bl = packet.relatives().contains(PositionFlag.X) || packet.relatives().contains(PositionFlag.Y) || packet.relatives().contains(PositionFlag.Z);
        boolean bl2 = this.world.hasEntity(entity) || !entity.isLogicalSideForUpdatingMovement() || bl;
        boolean bl3 = ClientPlayNetworkHandler.setPosition((EntityPosition)packet.change(), (Set)packet.relatives(), (Entity)entity, (boolean)bl2);
        entity.setOnGround(packet.onGround());
        if (!bl3 && entity.hasPassengerDeep((Entity)this.client.player)) {
            entity.updatePassengerPosition((Entity)this.client.player);
            this.client.player.resetPosition();
            if (entity.isLogicalSideForUpdatingMovement()) {
                this.connection.send((Packet)VehicleMoveC2SPacket.fromVehicle((Entity)entity));
            }
        }
    }

    public void onUpdateTickRate(UpdateTickRateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (this.client.world == null) {
            return;
        }
        TickManager tickManager = this.client.world.getTickManager();
        tickManager.setTickRate(packet.tickRate());
        tickManager.setFrozen(packet.isFrozen());
    }

    public void onTickStep(TickStepS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (this.client.world == null) {
            return;
        }
        TickManager tickManager = this.client.world.getTickManager();
        tickManager.setStepTicks(packet.tickSteps());
    }

    public void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (PlayerInventory.isValidHotbarIndex((int)packet.slot())) {
            this.client.player.getInventory().setSelectedSlot(packet.slot());
        }
    }

    public void onEntity(EntityS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = packet.getEntity((World)this.world);
        if (entity == null) {
            return;
        }
        if (entity.isLogicalSideForUpdatingMovement()) {
            TrackedPosition trackedPosition = entity.getTrackedPosition();
            Vec3d vec3d = trackedPosition.withDelta((long)packet.getDeltaX(), (long)packet.getDeltaY(), (long)packet.getDeltaZ());
            trackedPosition.setPos(vec3d);
            return;
        }
        if (packet.isPositionChanged()) {
            TrackedPosition trackedPosition = entity.getTrackedPosition();
            Vec3d vec3d = trackedPosition.withDelta((long)packet.getDeltaX(), (long)packet.getDeltaY(), (long)packet.getDeltaZ());
            trackedPosition.setPos(vec3d);
            if (packet.hasRotation()) {
                entity.updateTrackedPositionAndAngles(vec3d, packet.getYaw(), packet.getPitch());
            } else {
                entity.updateTrackedPosition(vec3d);
            }
        } else if (packet.hasRotation()) {
            entity.updateTrackedAngles(packet.getYaw(), packet.getPitch());
        }
        entity.setOnGround(packet.isOnGround());
    }

    public void onMoveMinecartAlongTrack(MoveMinecartAlongTrackS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = packet.getEntity((World)this.world);
        if (!(entity instanceof AbstractMinecartEntity)) {
            return;
        }
        AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)entity;
        MinecartController minecartController = abstractMinecartEntity.getController();
        if (minecartController instanceof ExperimentalMinecartController) {
            ExperimentalMinecartController experimentalMinecartController = (ExperimentalMinecartController)minecartController;
            experimentalMinecartController.stagingLerpSteps.addAll(packet.lerpSteps());
        }
    }

    public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = packet.getEntity((World)this.world);
        if (entity == null) {
            return;
        }
        entity.updateTrackedHeadRotation(packet.getHeadYaw(), 3);
    }

    public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        packet.getEntityIds().forEach(id -> {
            Entity entity = this.world.getEntityById(id);
            if (entity == null) {
                return;
            }
            if (entity.hasPassengerDeep((Entity)this.client.player)) {
                LOGGER.debug("Remove entity {}:{} that has player as passenger", (Object)entity.getType(), (Object)id);
                this.removedPlayerVehicleId = OptionalInt.of(id);
            }
            this.world.removeEntity(id, Entity.RemovalReason.DISCARDED);
            this.debugSubscriptionManager.removeEntity(entity);
        });
    }

    public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ClientPlayerEntity playerEntity = this.client.player;
        if (!playerEntity.hasVehicle()) {
            ClientPlayNetworkHandler.setPosition((EntityPosition)packet.change(), (Set)packet.relatives(), (Entity)playerEntity, (boolean)false);
        }
        this.connection.send((Packet)new TeleportConfirmC2SPacket(packet.teleportId()));
        this.connection.send((Packet)new PlayerMoveC2SPacket.Full(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), playerEntity.getYaw(), playerEntity.getPitch(), false, false));
    }

    private static boolean setPosition(EntityPosition pos, Set<PositionFlag> flags, Entity entity, boolean bl) {
        boolean bl2;
        EntityPosition entityPosition = EntityPosition.fromEntity((Entity)entity);
        EntityPosition entityPosition2 = EntityPosition.apply((EntityPosition)entityPosition, (EntityPosition)pos, flags);
        boolean bl3 = bl2 = entityPosition.position().squaredDistanceTo(entityPosition2.position()) > 4096.0;
        if (bl && !bl2) {
            entity.updateTrackedPositionAndAngles(entityPosition2.position(), entityPosition2.yaw(), entityPosition2.pitch());
            entity.setVelocity(entityPosition2.deltaMovement());
            return true;
        }
        entity.setPosition(entityPosition2.position());
        entity.setVelocity(entityPosition2.deltaMovement());
        entity.setYaw(entityPosition2.yaw());
        entity.setPitch(entityPosition2.pitch());
        EntityPosition entityPosition3 = new EntityPosition(entity.getLastRenderPos(), Vec3d.ZERO, entity.lastYaw, entity.lastPitch);
        EntityPosition entityPosition4 = EntityPosition.apply((EntityPosition)entityPosition3, (EntityPosition)pos, flags);
        entity.setLastPositionAndAngles(entityPosition4.position(), entityPosition4.yaw(), entityPosition4.pitch());
        return false;
    }

    public void onPlayerRotation(PlayerRotationS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ClientPlayerEntity playerEntity = this.client.player;
        Set set = PositionFlag.ofRot((boolean)packet.relativeYaw(), (boolean)packet.relativePitch());
        EntityPosition entityPosition = EntityPosition.fromEntity((Entity)playerEntity);
        EntityPosition entityPosition2 = EntityPosition.apply((EntityPosition)entityPosition, (EntityPosition)entityPosition.withRotation(packet.yaw(), packet.pitch()), (Set)set);
        playerEntity.setYaw(entityPosition2.yaw());
        playerEntity.setPitch(entityPosition2.pitch());
        playerEntity.updateLastAngles();
        this.connection.send((Packet)new PlayerMoveC2SPacket.LookAndOnGround(playerEntity.getYaw(), playerEntity.getPitch(), false, false));
    }

    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        packet.visitUpdates((pos, state) -> this.world.handleBlockUpdate(pos, state, 19));
    }

    public void onChunkData(ChunkDataS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        int i = packet.getChunkX();
        int j = packet.getChunkZ();
        this.loadChunk(i, j, packet.getChunkData());
        LightData lightData = packet.getLightData();
        this.world.enqueueChunkUpdate(() -> {
            this.readLightData(i, j, lightData, false);
            WorldChunk worldChunk = this.world.getChunkManager().getWorldChunk(i, j, false);
            if (worldChunk != null) {
                this.scheduleRenderChunk(worldChunk, i, j);
                this.client.worldRenderer.scheduleNeighborUpdates(worldChunk.getPos());
            }
        });
    }

    public void onChunkBiomeData(ChunkBiomeDataS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        for (ChunkBiomeDataS2CPacket.Serialized serialized : packet.chunkBiomeData()) {
            this.world.getChunkManager().onChunkBiomeData(serialized.pos().x, serialized.pos().z, serialized.toReadingBuf());
        }
        for (ChunkBiomeDataS2CPacket.Serialized serialized : packet.chunkBiomeData()) {
            this.world.resetChunkColor(new ChunkPos(serialized.pos().x, serialized.pos().z));
        }
        for (ChunkBiomeDataS2CPacket.Serialized serialized : packet.chunkBiomeData()) {
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    for (int k = this.world.getBottomSectionCoord(); k <= this.world.getTopSectionCoord(); ++k) {
                        this.client.worldRenderer.scheduleChunkRender(serialized.pos().x + i, k, serialized.pos().z + j);
                    }
                }
            }
        }
    }

    private void loadChunk(int x, int z, ChunkData chunkData) {
        this.world.getChunkManager().loadChunkFromPacket(x, z, chunkData.getSectionsDataBuf(), chunkData.getHeightmap(), chunkData.getBlockEntities(x, z));
    }

    private void scheduleRenderChunk(WorldChunk chunk, int x, int z) {
        LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
        ChunkSection[] chunkSections = chunk.getSectionArray();
        ChunkPos chunkPos = chunk.getPos();
        for (int i = 0; i < chunkSections.length; ++i) {
            ChunkSection chunkSection = chunkSections[i];
            int j = this.world.sectionIndexToCoord(i);
            lightingProvider.setSectionStatus(ChunkSectionPos.from((ChunkPos)chunkPos, (int)j), chunkSection.isEmpty());
        }
        this.world.scheduleChunkRenders(x - 1, this.world.getBottomSectionCoord(), z - 1, x + 1, this.world.getTopSectionCoord(), z + 1);
    }

    public void onUnloadChunk(UnloadChunkS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.world.getChunkManager().unload(packet.pos());
        this.debugSubscriptionManager.removeChunk(packet.pos());
        this.unloadChunk(packet);
    }

    private void unloadChunk(UnloadChunkS2CPacket packet) {
        ChunkPos chunkPos = packet.pos();
        this.world.enqueueChunkUpdate(() -> {
            int i;
            LightingProvider lightingProvider = this.world.getLightingProvider();
            lightingProvider.setColumnEnabled(chunkPos, false);
            for (i = lightingProvider.getBottomY(); i < lightingProvider.getTopY(); ++i) {
                ChunkSectionPos chunkSectionPos = ChunkSectionPos.from((ChunkPos)chunkPos, (int)i);
                lightingProvider.enqueueSectionData(LightType.BLOCK, chunkSectionPos, null);
                lightingProvider.enqueueSectionData(LightType.SKY, chunkSectionPos, null);
            }
            for (i = this.world.getBottomSectionCoord(); i <= this.world.getTopSectionCoord(); ++i) {
                lightingProvider.setSectionStatus(ChunkSectionPos.from((ChunkPos)chunkPos, (int)i), true);
            }
        });
    }

    public void onBlockUpdate(BlockUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.world.handleBlockUpdate(packet.getPos(), packet.getState(), 19);
    }

    public void onEnterReconfiguration(EnterReconfigurationS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.getMessageHandler().processAll();
        this.sendAcknowledgment();
        ChatHud.ChatState chatState = this.client.inGameHud.getChatHud().toChatState();
        this.client.enterReconfiguration((Screen)new ReconfiguringScreen(RECONFIGURING_TEXT, this.connection));
        this.connection.transitionInbound(ConfigurationStates.S2C, (PacketListener)new ClientConfigurationNetworkHandler(this.client, this.connection, new ClientConnectionState(new ClientChunkLoadProgress(), this.profile, this.worldSession, this.combinedDynamicRegistries, this.enabledFeatures, this.brand, this.serverInfo, this.postDisconnectScreen, this.serverCookies, chatState, this.customReportDetails, this.getServerLinks(), this.seenPlayers, this.seenInsecureChatWarning)));
        this.sendPacket((Packet)AcknowledgeReconfigurationC2SPacket.INSTANCE);
        this.connection.transitionOutbound(ConfigurationStates.C2S);
    }

    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getEntityId());
        LivingEntity livingEntity = (LivingEntity)this.world.getEntityById(packet.getCollectorEntityId());
        if (livingEntity == null) {
            livingEntity = this.client.player;
        }
        if (entity != null) {
            if (entity instanceof ExperienceOrbEntity) {
                this.world.playSoundClient(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1f, (this.random.nextFloat() - this.random.nextFloat()) * 0.35f + 0.9f, false);
            } else {
                this.world.playSoundClient(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, (this.random.nextFloat() - this.random.nextFloat()) * 1.4f + 2.0f, false);
            }
            EntityRenderState entityRenderState = this.client.getEntityRenderDispatcher().getAndUpdateRenderState(entity, 1.0f);
            this.client.particleManager.addParticle((Particle)new ItemPickupParticle(this.world, entityRenderState, (Entity)livingEntity, entity.getVelocity()));
            if (entity instanceof ItemEntity) {
                ItemEntity itemEntity = (ItemEntity)entity;
                ItemStack itemStack = itemEntity.getStack();
                if (!itemStack.isEmpty()) {
                    itemStack.decrement(packet.getStackAmount());
                }
                if (itemStack.isEmpty()) {
                    this.world.removeEntity(packet.getEntityId(), Entity.RemovalReason.DISCARDED);
                }
            } else if (!(entity instanceof ExperienceOrbEntity)) {
                this.world.removeEntity(packet.getEntityId(), Entity.RemovalReason.DISCARDED);
            }
        }
    }

    public void onGameMessage(GameMessageS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.getMessageHandler().onGameMessage(packet.content(), packet.overlay());
    }

    public void onChatMessage(ChatMessageS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        int i = this.globalChatMessageIndex++;
        if (packet.globalIndex() != i) {
            LOGGER.error("Missing or out-of-order chat message from server, expected index {} but got {}", (Object)i, (Object)packet.globalIndex());
            this.connection.disconnect(BAD_CHAT_INDEX_TEXT);
            return;
        }
        Optional optional = packet.body().toBody(this.signatureStorage);
        if (optional.isEmpty()) {
            LOGGER.error("Message from player with ID {} referenced unrecognized signature id", (Object)packet.sender());
            this.connection.disconnect(INVALID_PACKET_TEXT);
            return;
        }
        this.signatureStorage.add((MessageBody)optional.get(), packet.signature());
        UUID uUID = packet.sender();
        PlayerListEntry playerListEntry = this.getPlayerListEntry(uUID);
        if (playerListEntry == null) {
            LOGGER.error("Received player chat packet for unknown player with ID: {}", (Object)uUID);
            this.client.getMessageHandler().onUnverifiedMessage(uUID, packet.signature(), packet.serializedParameters());
            return;
        }
        PublicPlayerSession publicPlayerSession = playerListEntry.getSession();
        MessageLink messageLink = publicPlayerSession != null ? new MessageLink(packet.index(), uUID, publicPlayerSession.sessionId()) : MessageLink.of((UUID)uUID);
        SignedMessage signedMessage = new SignedMessage(messageLink, packet.signature(), (MessageBody)optional.get(), packet.unsignedContent(), packet.filterMask());
        signedMessage = playerListEntry.getMessageVerifier().ensureVerified(signedMessage);
        if (signedMessage != null) {
            this.client.getMessageHandler().onChatMessage(signedMessage, playerListEntry.getProfile(), packet.serializedParameters());
        } else {
            this.client.getMessageHandler().onUnverifiedMessage(uUID, packet.signature(), packet.serializedParameters());
        }
    }

    public void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.getMessageHandler().onProfilelessMessage(packet.message(), packet.chatType());
    }

    public void onRemoveMessage(RemoveMessageS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Optional optional = packet.messageSignature().getSignature(this.signatureStorage);
        if (optional.isEmpty()) {
            this.connection.disconnect(INVALID_PACKET_TEXT);
            return;
        }
        this.lastSeenMessagesCollector.remove((MessageSignatureData)optional.get());
        if (!this.client.getMessageHandler().removeDelayedMessage((MessageSignatureData)optional.get())) {
            this.client.inGameHud.getChatHud().removeMessage((MessageSignatureData)optional.get());
        }
    }

    public void onEntityAnimation(EntityAnimationS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (entity == null) {
            return;
        }
        if (packet.getAnimationId() == 0) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.swingHand(Hand.MAIN_HAND);
        } else if (packet.getAnimationId() == 3) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.swingHand(Hand.OFF_HAND);
        } else if (packet.getAnimationId() == 2) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            playerEntity.wakeUp(false, false);
        } else if (packet.getAnimationId() == 4) {
            this.client.particleManager.addEmitter(entity, (ParticleEffect)ParticleTypes.CRIT);
        } else if (packet.getAnimationId() == 5) {
            this.client.particleManager.addEmitter(entity, (ParticleEffect)ParticleTypes.ENCHANTED_HIT);
        }
    }

    public void onDamageTilt(DamageTiltS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.id());
        if (entity == null) {
            return;
        }
        entity.animateDamage(packet.yaw());
    }

    public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.world.setTime(packet.time(), packet.timeOfDay(), packet.tickDayTime());
        this.worldSession.setTick(packet.time());
    }

    public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.world.setSpawnPoint(packet.respawnData());
    }

    public void onEntityPassengersSet(EntityPassengersSetS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (entity == null) {
            LOGGER.warn("Received passengers for unknown entity");
            return;
        }
        boolean bl = entity.hasPassengerDeep((Entity)this.client.player);
        entity.removeAllPassengers();
        for (int i : packet.getPassengerIds()) {
            Entity entity2 = this.world.getEntityById(i);
            if (entity2 == null) continue;
            entity2.startRiding(entity, true, false);
            if (entity2 != this.client.player) continue;
            this.removedPlayerVehicleId = OptionalInt.empty();
            if (bl) continue;
            if (entity instanceof AbstractBoatEntity) {
                this.client.player.lastYaw = entity.getYaw();
                this.client.player.setYaw(entity.getYaw());
                this.client.player.setHeadYaw(entity.getYaw());
            }
            MutableText text = Text.translatable((String)"mount.onboard", (Object[])new Object[]{this.client.options.sneakKey.getBoundKeyLocalizedText()});
            this.client.inGameHud.setOverlayMessage((Text)text, false);
            this.client.getNarratorManager().narrateSystemImmediately((Text)text);
        }
    }

    public void onEntityAttach(EntityAttachS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getAttachedEntityId());
        if (entity instanceof Leashable) {
            Leashable leashable = (Leashable)entity;
            leashable.setUnresolvedLeashHolderId(packet.getHoldingEntityId());
        }
    }

    private static ItemStack getActiveDeathProtector(PlayerEntity player) {
        for (Hand hand : Hand.values()) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (!itemStack.contains(DataComponentTypes.DEATH_PROTECTION)) continue;
            return itemStack;
        }
        return new ItemStack((ItemConvertible)Items.TOTEM_OF_UNDYING);
    }

    public void onEntityStatus(EntityStatusS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = packet.getEntity((World)this.world);
        if (entity != null) {
            switch (packet.getStatus()) {
                case 63: {
                    this.client.getSoundManager().play((SoundInstance)new SnifferDigSoundInstance((SnifferEntity)entity));
                    break;
                }
                case 21: {
                    this.client.getSoundManager().play((SoundInstance)new GuardianAttackSoundInstance((GuardianEntity)entity));
                    break;
                }
                case 35: {
                    int i = 40;
                    this.client.particleManager.addEmitter(entity, (ParticleEffect)ParticleTypes.TOTEM_OF_UNDYING, 30);
                    this.world.playSoundClient(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0f, 1.0f, false);
                    if (entity != this.client.player) break;
                    this.client.gameRenderer.showFloatingItem(ClientPlayNetworkHandler.getActiveDeathProtector((PlayerEntity)this.client.player));
                    break;
                }
                default: {
                    entity.handleStatus(packet.getStatus());
                }
            }
        }
    }

    public void onEntityDamage(EntityDamageS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.entityId());
        if (entity == null) {
            return;
        }
        entity.onDamaged(packet.createDamageSource((World)this.world));
    }

    public void onHealthUpdate(HealthUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.player.updateHealth(packet.getHealth());
        this.client.player.getHungerManager().setFoodLevel(packet.getFood());
        this.client.player.getHungerManager().setSaturationLevel(packet.getSaturation());
    }

    public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.player.setExperience(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
    }

    public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        CommonPlayerSpawnInfo commonPlayerSpawnInfo = packet.commonPlayerSpawnInfo();
        RegistryKey registryKey = commonPlayerSpawnInfo.dimension();
        RegistryEntry registryEntry = commonPlayerSpawnInfo.dimensionType();
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        RegistryKey registryKey2 = clientPlayerEntity.getEntityWorld().getRegistryKey();
        boolean bl = registryKey != registryKey2;
        LevelLoadingScreen.WorldEntryReason worldEntryReason = this.getWorldEntryReason(clientPlayerEntity.isDead(), registryKey, registryKey2);
        if (bl) {
            ClientWorld.Properties properties;
            Map map = this.world.getMapStates();
            boolean bl2 = commonPlayerSpawnInfo.isDebug();
            boolean bl3 = commonPlayerSpawnInfo.isFlat();
            int i = commonPlayerSpawnInfo.seaLevel();
            this.worldProperties = properties = new ClientWorld.Properties(this.worldProperties.getDifficulty(), this.worldProperties.isHardcore(), bl3);
            this.world = new ClientWorld(this, properties, registryKey, registryEntry, this.chunkLoadDistance, this.simulationDistance, this.client.worldRenderer, bl2, commonPlayerSpawnInfo.seed(), i);
            this.world.putMapStates(map);
            this.client.joinWorld(this.world);
            this.debugSubscriptionManager.clearValues();
        }
        this.client.setCameraEntity(null);
        if (clientPlayerEntity.shouldCloseHandledScreenOnRespawn()) {
            clientPlayerEntity.closeHandledScreen();
        }
        ClientPlayerEntity clientPlayerEntity2 = packet.hasFlag((byte)2) ? this.client.interactionManager.createPlayer(this.world, clientPlayerEntity.getStatHandler(), clientPlayerEntity.getRecipeBook(), clientPlayerEntity.getLastPlayerInput(), clientPlayerEntity.isSprinting()) : this.client.interactionManager.createPlayer(this.world, clientPlayerEntity.getStatHandler(), clientPlayerEntity.getRecipeBook());
        this.setLoaded(false);
        this.startWorldLoading(clientPlayerEntity2, this.world, worldEntryReason);
        clientPlayerEntity2.setId(clientPlayerEntity.getId());
        this.client.player = clientPlayerEntity2;
        if (bl) {
            this.client.getMusicTracker().stop();
        }
        this.client.setCameraEntity((Entity)clientPlayerEntity2);
        if (packet.hasFlag((byte)2)) {
            List list = clientPlayerEntity.getDataTracker().getChangedEntries();
            if (list != null) {
                clientPlayerEntity2.getDataTracker().writeUpdatedEntries(list);
            }
            clientPlayerEntity2.setVelocity(clientPlayerEntity.getVelocity());
            clientPlayerEntity2.setYaw(clientPlayerEntity.getYaw());
            clientPlayerEntity2.setPitch(clientPlayerEntity.getPitch());
        } else {
            clientPlayerEntity2.init();
            clientPlayerEntity2.setYaw(-180.0f);
        }
        if (packet.hasFlag((byte)1)) {
            clientPlayerEntity2.getAttributes().setFrom(clientPlayerEntity.getAttributes());
        } else {
            clientPlayerEntity2.getAttributes().setBaseFrom(clientPlayerEntity.getAttributes());
        }
        this.world.addEntity((Entity)clientPlayerEntity2);
        clientPlayerEntity2.input = new KeyboardInput(this.client.options);
        this.client.interactionManager.copyAbilities((PlayerEntity)clientPlayerEntity2);
        clientPlayerEntity2.setReducedDebugInfo(clientPlayerEntity.hasReducedDebugInfo());
        clientPlayerEntity2.setShowsDeathScreen(clientPlayerEntity.showsDeathScreen());
        clientPlayerEntity2.setLastDeathPos(commonPlayerSpawnInfo.lastDeathLocation());
        clientPlayerEntity2.setPortalCooldown(commonPlayerSpawnInfo.portalCooldown());
        clientPlayerEntity2.nauseaIntensity = clientPlayerEntity.nauseaIntensity;
        clientPlayerEntity2.lastNauseaIntensity = clientPlayerEntity.lastNauseaIntensity;
        if (this.client.currentScreen instanceof DeathScreen || this.client.currentScreen instanceof DeathScreen.TitleScreenConfirmScreen) {
            this.client.setScreen(null);
        }
        this.client.interactionManager.setGameModes(commonPlayerSpawnInfo.gameMode(), commonPlayerSpawnInfo.lastGameMode());
    }

    private LevelLoadingScreen.WorldEntryReason getWorldEntryReason(boolean dead, RegistryKey<World> from, RegistryKey<World> to) {
        LevelLoadingScreen.WorldEntryReason worldEntryReason = LevelLoadingScreen.WorldEntryReason.OTHER;
        if (!dead) {
            if (from == World.NETHER || to == World.NETHER) {
                worldEntryReason = LevelLoadingScreen.WorldEntryReason.NETHER_PORTAL;
            } else if (from == World.END || to == World.END) {
                worldEntryReason = LevelLoadingScreen.WorldEntryReason.END_PORTAL;
            }
        }
        return worldEntryReason;
    }

    public void onExplosion(ExplosionS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Vec3d vec3d = packet.center();
        this.client.world.playSoundClient(vec3d.getX(), vec3d.getY(), vec3d.getZ(), (SoundEvent)packet.explosionSound().value(), SoundCategory.BLOCKS, 4.0f, (1.0f + (this.client.world.random.nextFloat() - this.client.world.random.nextFloat()) * 0.2f) * 0.7f, false);
        this.client.world.addParticleClient(packet.explosionParticle(), vec3d.getX(), vec3d.getY(), vec3d.getZ(), 1.0, 0.0, 0.0);
        this.client.world.addBlockParticleEffects(vec3d, packet.radius(), packet.blockCount(), packet.blockParticles());
        packet.playerKnockback().ifPresent(arg_0 -> ((ClientPlayerEntity)this.client.player).addVelocityInternal(arg_0));
    }

    public void onOpenMountScreen(OpenMountScreenS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getMountId());
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        int i = packet.getSlotColumnCount();
        SimpleInventory simpleInventory = new SimpleInventory(MountScreenHandler.getSlotCount((int)i));
        if (entity instanceof AbstractHorseEntity) {
            AbstractHorseEntity abstractHorseEntity = (AbstractHorseEntity)entity;
            HorseScreenHandler horseScreenHandler = new HorseScreenHandler(packet.getSyncId(), clientPlayerEntity.getInventory(), (Inventory)simpleInventory, abstractHorseEntity, i);
            clientPlayerEntity.currentScreenHandler = horseScreenHandler;
            this.client.setScreen((Screen)new HorseScreen(horseScreenHandler, clientPlayerEntity.getInventory(), abstractHorseEntity, i));
        } else if (entity instanceof AbstractNautilusEntity) {
            AbstractNautilusEntity abstractNautilusEntity = (AbstractNautilusEntity)entity;
            NautilusScreenHandler nautilusScreenHandler = new NautilusScreenHandler(packet.getSyncId(), clientPlayerEntity.getInventory(), (Inventory)simpleInventory, abstractNautilusEntity, i);
            clientPlayerEntity.currentScreenHandler = nautilusScreenHandler;
            this.client.setScreen((Screen)new NautilusScreen(nautilusScreenHandler, clientPlayerEntity.getInventory(), abstractNautilusEntity, i));
        }
    }

    public void onOpenScreen(OpenScreenS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        HandledScreens.open((ScreenHandlerType)packet.getScreenHandlerType(), (MinecraftClient)this.client, (int)packet.getSyncId(), (Text)packet.getName());
    }

    public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet) {
        CreativeInventoryScreen creativeInventoryScreen;
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ClientPlayerEntity playerEntity = this.client.player;
        ItemStack itemStack = packet.getStack();
        int i = packet.getSlot();
        this.client.getTutorialManager().onSlotUpdate(itemStack);
        Screen screen = this.client.currentScreen;
        boolean bl = screen instanceof CreativeInventoryScreen ? !(creativeInventoryScreen = (CreativeInventoryScreen)screen).isInventoryTabSelected() : false;
        if (packet.getSyncId() == 0) {
            ItemStack itemStack2;
            if (PlayerScreenHandler.isInHotbar((int)i) && !itemStack.isEmpty() && ((itemStack2 = playerEntity.playerScreenHandler.getSlot(i).getStack()).isEmpty() || itemStack2.getCount() < itemStack.getCount())) {
                itemStack.setBobbingAnimationTime(5);
            }
            playerEntity.playerScreenHandler.setStackInSlot(i, packet.getRevision(), itemStack);
        } else if (!(packet.getSyncId() != playerEntity.currentScreenHandler.syncId || packet.getSyncId() == 0 && bl)) {
            playerEntity.currentScreenHandler.setStackInSlot(i, packet.getRevision(), itemStack);
        }
        if (this.client.currentScreen instanceof CreativeInventoryScreen) {
            playerEntity.playerScreenHandler.setReceivedStack(i, itemStack);
            playerEntity.playerScreenHandler.sendContentUpdates();
        }
    }

    public void onSetCursorItem(SetCursorItemS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.getTutorialManager().onSlotUpdate(packet.contents());
        if (!(this.client.currentScreen instanceof CreativeInventoryScreen)) {
            this.client.player.currentScreenHandler.setCursorStack(packet.contents());
        }
    }

    public void onSetPlayerInventory(SetPlayerInventoryS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.getTutorialManager().onSlotUpdate(packet.contents());
        this.client.player.getInventory().setStack(packet.slot(), packet.contents());
    }

    public void onInventory(InventoryS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ClientPlayerEntity playerEntity = this.client.player;
        if (packet.syncId() == 0) {
            playerEntity.playerScreenHandler.updateSlotStacks(packet.revision(), packet.contents(), packet.cursorStack());
        } else if (packet.syncId() == playerEntity.currentScreenHandler.syncId) {
            playerEntity.currentScreenHandler.updateSlotStacks(packet.revision(), packet.contents(), packet.cursorStack());
        }
    }

    public void onSignEditorOpen(SignEditorOpenS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        BlockPos blockPos = packet.getPos();
        BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
        if (blockEntity instanceof SignBlockEntity) {
            SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
            this.client.player.openEditSignScreen(signBlockEntity, packet.isFront());
        } else {
            LOGGER.warn("Ignoring openTextEdit on an invalid entity: {} at pos {}", (Object)this.world.getBlockEntity(blockPos), (Object)blockPos);
        }
    }

    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        BlockPos blockPos = packet.getPos();
        this.client.world.getBlockEntity(blockPos, packet.getBlockEntityType()).ifPresent(blockEntity -> {
            try (ErrorReporter.Logging logging = new ErrorReporter.Logging(blockEntity.getReporterContext(), LOGGER);){
                blockEntity.read(NbtReadView.create((ErrorReporter)logging, (RegistryWrapper.WrapperLookup)this.combinedDynamicRegistries, (NbtCompound)packet.getNbt()));
            }
            if (blockEntity instanceof CommandBlockBlockEntity && this.client.currentScreen instanceof CommandBlockScreen) {
                ((CommandBlockScreen)this.client.currentScreen).updateCommandBlock();
            }
        });
    }

    public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ClientPlayerEntity playerEntity = this.client.player;
        if (playerEntity.currentScreenHandler.syncId == packet.getSyncId()) {
            playerEntity.currentScreenHandler.setProperty(packet.getPropertyId(), packet.getValue());
        }
    }

    public void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            packet.getEquipmentList().forEach(pair -> livingEntity.equipStack((EquipmentSlot)pair.getFirst(), (ItemStack)pair.getSecond()));
        }
    }

    public void onCloseScreen(CloseScreenS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.player.closeScreen();
    }

    public void onBlockEvent(BlockEventS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.world.addSyncedBlockEvent(packet.getPos(), packet.getBlock(), packet.getType(), packet.getData());
    }

    public void onBlockBreakingProgress(BlockBreakingProgressS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.world.setBlockBreakingInfo(packet.getEntityId(), packet.getPos(), packet.getProgress());
    }

    public void onGameStateChange(GameStateChangeS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ClientPlayerEntity playerEntity = this.client.player;
        GameStateChangeS2CPacket.Reason reason = packet.getReason();
        float f = packet.getValue();
        int i = MathHelper.floor((float)(f + 0.5f));
        if (reason == GameStateChangeS2CPacket.NO_RESPAWN_BLOCK) {
            playerEntity.sendMessage((Text)Text.translatable((String)"block.minecraft.spawn.not_valid"), false);
        } else if (reason == GameStateChangeS2CPacket.RAIN_STARTED) {
            this.world.getLevelProperties().setRaining(true);
            this.world.setRainGradient(0.0f);
        } else if (reason == GameStateChangeS2CPacket.RAIN_STOPPED) {
            this.world.getLevelProperties().setRaining(false);
            this.world.setRainGradient(1.0f);
        } else if (reason == GameStateChangeS2CPacket.GAME_MODE_CHANGED) {
            this.client.interactionManager.setGameMode(GameMode.byIndex((int)i));
        } else if (reason == GameStateChangeS2CPacket.GAME_WON) {
            this.client.setScreen((Screen)new CreditsScreen(true, () -> {
                this.client.player.networkHandler.sendPacket((Packet)new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
                this.client.setScreen(null);
            }));
        } else if (reason == GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN) {
            GameOptions gameOptions = this.client.options;
            MutableText text = null;
            if (f == 0.0f) {
                this.client.setScreen((Screen)new DemoScreen());
            } else if (f == 101.0f) {
                text = Text.translatable((String)"demo.help.movement", (Object[])new Object[]{gameOptions.forwardKey.getBoundKeyLocalizedText(), gameOptions.leftKey.getBoundKeyLocalizedText(), gameOptions.backKey.getBoundKeyLocalizedText(), gameOptions.rightKey.getBoundKeyLocalizedText()});
            } else if (f == 102.0f) {
                text = Text.translatable((String)"demo.help.jump", (Object[])new Object[]{gameOptions.jumpKey.getBoundKeyLocalizedText()});
            } else if (f == 103.0f) {
                text = Text.translatable((String)"demo.help.inventory", (Object[])new Object[]{gameOptions.inventoryKey.getBoundKeyLocalizedText()});
            } else if (f == 104.0f) {
                text = Text.translatable((String)"demo.day.6", (Object[])new Object[]{gameOptions.screenshotKey.getBoundKeyLocalizedText()});
            }
            if (text != null) {
                this.client.inGameHud.getChatHud().addMessage(text);
                this.client.getNarratorManager().narrateSystemMessage((Text)text);
            }
        } else if (reason == GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER) {
            this.world.playSound((Entity)playerEntity, playerEntity.getX(), playerEntity.getEyeY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18f, 0.45f);
        } else if (reason == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED) {
            this.world.setRainGradient(f);
        } else if (reason == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED) {
            this.world.setThunderGradient(f);
        } else if (reason == GameStateChangeS2CPacket.PUFFERFISH_STING) {
            this.world.playSound((Entity)playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_PUFFER_FISH_STING, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        } else if (reason == GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT) {
            this.world.addParticleClient((ParticleEffect)ParticleTypes.ELDER_GUARDIAN, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), 0.0, 0.0, 0.0);
            if (i == 1) {
                this.world.playSound((Entity)playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0f, 1.0f);
            }
        } else if (reason == GameStateChangeS2CPacket.IMMEDIATE_RESPAWN) {
            this.client.player.setShowsDeathScreen(f == 0.0f);
        } else if (reason == GameStateChangeS2CPacket.LIMITED_CRAFTING_TOGGLED) {
            this.client.player.setLimitedCraftingEnabled(f == 1.0f);
        } else if (reason == GameStateChangeS2CPacket.INITIAL_CHUNKS_COMING && this.chunkLoadProgress != null) {
            this.chunkLoadProgress.initialChunksComing();
        }
    }

    private void startWorldLoading(ClientPlayerEntity player, ClientWorld world, LevelLoadingScreen.WorldEntryReason reason) {
        if (this.chunkLoadProgress == null) {
            this.chunkLoadProgress = new ClientChunkLoadProgress();
        }
        this.chunkLoadProgress.startWorldLoading(player, world, this.client.worldRenderer);
        Screen screen = this.client.currentScreen;
        if (screen instanceof LevelLoadingScreen) {
            LevelLoadingScreen levelLoadingScreen = (LevelLoadingScreen)screen;
            levelLoadingScreen.init(this.chunkLoadProgress, reason);
        } else {
            this.client.inGameHud.getChatHud().setScreen();
            this.client.setScreenAndRender((Screen)new LevelLoadingScreen(this.chunkLoadProgress, reason));
        }
    }

    public void onMapUpdate(MapUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        MapIdComponent mapIdComponent = packet.mapId();
        MapState mapState = this.client.world.getMapState(mapIdComponent);
        if (mapState == null) {
            mapState = MapState.of((byte)packet.scale(), (boolean)packet.locked(), (RegistryKey)this.client.world.getRegistryKey());
            this.client.world.putClientsideMapState(mapIdComponent, mapState);
        }
        packet.apply(mapState);
        this.client.getMapTextureManager().setNeedsUpdate(mapIdComponent, mapState);
    }

    public void onWorldEvent(WorldEventS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (packet.isGlobal()) {
            this.client.world.syncGlobalEvent(packet.getEventId(), packet.getPos(), packet.getData());
        } else {
            this.client.world.syncWorldEvent(packet.getEventId(), packet.getPos(), packet.getData());
        }
    }

    public void onAdvancements(AdvancementUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.advancementHandler.onAdvancements(packet);
    }

    public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Identifier identifier = packet.getTabId();
        if (identifier == null) {
            this.advancementHandler.selectTab(null, false);
        } else {
            AdvancementEntry advancementEntry = this.advancementHandler.get(identifier);
            this.advancementHandler.selectTab(advancementEntry, false);
        }
    }

    public void onCommandTree(CommandTreeS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.commandDispatcher = new CommandDispatcher(packet.getCommandTree(CommandRegistryAccess.of((RegistryWrapper.WrapperLookup)this.combinedDynamicRegistries, (FeatureSet)this.enabledFeatures), COMMAND_NODE_FACTORY));
    }

    public void onStopSound(StopSoundS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.getSoundManager().stopSounds(packet.getSoundId(), packet.getCategory());
    }

    public void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.commandSource.onCommandSuggestions(packet.id(), packet.getSuggestions());
    }

    public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.recipeManager = new ClientRecipeManager(packet.itemSets(), packet.stonecutterRecipes());
    }

    public void onLookAt(LookAtS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Vec3d vec3d = packet.getTargetPosition((World)this.world);
        if (vec3d != null) {
            this.client.player.lookAt(packet.getSelfAnchor(), vec3d);
        }
    }

    public void onNbtQueryResponse(NbtQueryResponseS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (!this.dataQueryHandler.handleQueryResponse(packet.getTransactionId(), packet.getNbt())) {
            LOGGER.debug("Got unhandled response to tag query {}", (Object)packet.getTransactionId());
        }
    }

    public void onStatistics(StatisticsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        for (Object2IntMap.Entry entry : packet.stats().object2IntEntrySet()) {
            Stat stat = (Stat)entry.getKey();
            int i = entry.getIntValue();
            this.client.player.getStatHandler().setStat((PlayerEntity)this.client.player, stat, i);
        }
        Screen screen = this.client.currentScreen;
        if (screen instanceof StatsScreen) {
            StatsScreen statsScreen = (StatsScreen)screen;
            statsScreen.onStatsReady();
        }
    }

    public void onRecipeBookAdd(RecipeBookAddS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
        if (packet.replace()) {
            clientRecipeBook.clear();
        }
        for (RecipeBookAddS2CPacket.Entry entry : packet.entries()) {
            clientRecipeBook.add(entry.contents());
            if (entry.isHighlighted()) {
                clientRecipeBook.markHighlighted(entry.contents().id());
            }
            if (!entry.shouldShowNotification()) continue;
            RecipeToast.show((ToastManager)this.client.getToastManager(), (RecipeDisplay)entry.contents().display());
        }
        this.refreshRecipeBook(clientRecipeBook);
    }

    public void onRecipeBookRemove(RecipeBookRemoveS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
        for (NetworkRecipeId networkRecipeId : packet.recipes()) {
            clientRecipeBook.remove(networkRecipeId);
        }
        this.refreshRecipeBook(clientRecipeBook);
    }

    public void onRecipeBookSettings(RecipeBookSettingsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
        clientRecipeBook.setOptions(packet.bookSettings());
        this.refreshRecipeBook(clientRecipeBook);
    }

    private void refreshRecipeBook(ClientRecipeBook recipeBook) {
        recipeBook.refresh();
        this.searchManager.addRecipeOutputReloader(recipeBook, (World)this.world);
        Screen screen = this.client.currentScreen;
        if (screen instanceof RecipeBookProvider) {
            RecipeBookProvider recipeBookProvider = (RecipeBookProvider)screen;
            recipeBookProvider.refreshRecipeBook();
        }
    }

    public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        RegistryEntry registryEntry = packet.getEffectId();
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(registryEntry, packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon(), null);
        if (!packet.keepFading()) {
            statusEffectInstance.skipFading();
        }
        ((LivingEntity)entity).setStatusEffect(statusEffectInstance, null);
    }

    private <T> Registry.PendingTagLoad<T> startTagReload(RegistryKey<? extends Registry<? extends T>> registryRef, TagPacketSerializer.Serialized serialized) {
        Registry registry = this.combinedDynamicRegistries.getOrThrow(registryRef);
        return registry.startTagReload(serialized.toRegistryTags(registry));
    }

    public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ArrayList list = new ArrayList(packet.getGroups().size());
        boolean bl = this.connection.isLocal();
        packet.getGroups().forEach((registryRef, serialized) -> {
            if (!bl || SerializableRegistries.isSynced((RegistryKey)registryRef)) {
                list.add(this.startTagReload(registryRef, serialized));
            }
        });
        list.forEach(Registry.PendingTagLoad::apply);
        this.fuelRegistry = FuelRegistry.createDefault((RegistryWrapper.WrapperLookup)this.combinedDynamicRegistries, (FeatureSet)this.enabledFeatures);
        List list2 = List.copyOf(ItemGroups.getSearchGroup().getDisplayStacks());
        this.searchManager.addItemTagReloader(list2);
    }

    public void onEndCombat(EndCombatS2CPacket packet) {
    }

    public void onEnterCombat(EnterCombatS2CPacket packet) {
    }

    public void onDeathMessage(DeathMessageS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.playerId());
        if (entity == this.client.player) {
            if (this.client.player.showsDeathScreen()) {
                this.client.setScreen((Screen)new DeathScreen(packet.message(), this.world.getLevelProperties().isHardcore(), this.client.player));
            } else {
                this.client.player.requestRespawn();
            }
        }
    }

    public void onDifficulty(DifficultyS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.worldProperties.setDifficulty(packet.difficulty());
        this.worldProperties.setDifficultyLocked(packet.difficultyLocked());
    }

    public void onSetCameraEntity(SetCameraEntityS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = packet.getEntity((World)this.world);
        if (entity != null) {
            this.client.setCameraEntity(entity);
        }
    }

    public void onWorldBorderInitialize(WorldBorderInitializeS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        WorldBorder worldBorder = this.world.getWorldBorder();
        worldBorder.setCenter(packet.getCenterX(), packet.getCenterZ());
        long l = packet.getSizeLerpTime();
        if (l > 0L) {
            worldBorder.interpolateSize(packet.getSize(), packet.getSizeLerpTarget(), l, this.world.getTime());
        } else {
            worldBorder.setSize(packet.getSizeLerpTarget());
        }
        worldBorder.setMaxRadius(packet.getMaxRadius());
        worldBorder.setWarningBlocks(packet.getWarningBlocks());
        worldBorder.setWarningTime(packet.getWarningTime());
    }

    public void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.world.getWorldBorder().setCenter(packet.getCenterX(), packet.getCenterZ());
    }

    public void onWorldBorderInterpolateSize(WorldBorderInterpolateSizeS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.world.getWorldBorder().interpolateSize(packet.getSize(), packet.getSizeLerpTarget(), packet.getSizeLerpTime(), this.world.getTime());
    }

    public void onWorldBorderSizeChanged(WorldBorderSizeChangedS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.world.getWorldBorder().setSize(packet.getSizeLerpTarget());
    }

    public void onWorldBorderWarningBlocksChanged(WorldBorderWarningBlocksChangedS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.world.getWorldBorder().setWarningBlocks(packet.getWarningBlocks());
    }

    public void onWorldBorderWarningTimeChanged(WorldBorderWarningTimeChangedS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.world.getWorldBorder().setWarningTime(packet.getWarningTime());
    }

    public void onTitleClear(ClearTitleS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.inGameHud.clearTitle();
        if (packet.shouldReset()) {
            this.client.inGameHud.setDefaultTitleFade();
        }
    }

    public void onServerMetadata(ServerMetadataS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (this.serverInfo == null) {
            return;
        }
        this.serverInfo.label = packet.description();
        packet.favicon().map(ServerInfo::validateFavicon).ifPresent(arg_0 -> ((ServerInfo)this.serverInfo).setFavicon(arg_0));
        ServerList.updateServerListEntry((ServerInfo)this.serverInfo);
    }

    public void onChatSuggestions(ChatSuggestionsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.commandSource.onChatSuggestions(packet.action(), packet.entries());
    }

    public void onOverlayMessage(OverlayMessageS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.inGameHud.setOverlayMessage(packet.text(), false);
    }

    public void onTitle(TitleS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.inGameHud.setTitle(packet.text());
    }

    public void onSubtitle(SubtitleS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.inGameHud.setSubtitle(packet.text());
    }

    public void onTitleFade(TitleFadeS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.inGameHud.setTitleTicks(packet.getFadeInTicks(), packet.getStayTicks(), packet.getFadeOutTicks());
    }

    public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.inGameHud.getPlayerListHud().setHeader(packet.header().getString().isEmpty() ? null : packet.header());
        this.client.inGameHud.getPlayerListHud().setFooter(packet.footer().getString().isEmpty() ? null : packet.footer());
    }

    public void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = packet.getEntity((World)this.world);
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            livingEntity.removeStatusEffectInternal(packet.effect());
        }
    }

    public void onPlayerRemove(PlayerRemoveS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        for (UUID uUID : packet.profileIds()) {
            this.client.getSocialInteractionsManager().setPlayerOffline(uUID);
            PlayerListEntry playerListEntry = (PlayerListEntry)this.playerListEntries.remove(uUID);
            if (playerListEntry == null) continue;
            this.listedPlayerListEntries.remove(playerListEntry);
        }
    }

    public void onPlayerList(PlayerListS2CPacket packet) {
        PlayerListEntry playerListEntry;
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        for (PlayerListS2CPacket.Entry entry : packet.getPlayerAdditionEntries()) {
            playerListEntry = new PlayerListEntry(Objects.requireNonNull(entry.profile()), this.isSecureChatEnforced());
            if (this.playerListEntries.putIfAbsent(entry.profileId(), playerListEntry) != null) continue;
            this.client.getSocialInteractionsManager().setPlayerOnline(playerListEntry);
        }
        for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
            playerListEntry = (PlayerListEntry)this.playerListEntries.get(entry.profileId());
            if (playerListEntry == null) {
                LOGGER.warn("Ignoring player info update for unknown player {} ({})", (Object)entry.profileId(), (Object)packet.getActions());
                continue;
            }
            for (PlayerListS2CPacket.Action action : packet.getActions()) {
                this.handlePlayerListAction(action, entry, playerListEntry);
            }
        }
    }

    private void handlePlayerListAction(PlayerListS2CPacket.Action action, PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry) {
        switch (3.field_60785[action.ordinal()]) {
            case 1: {
                this.setPublicSession(receivedEntry, currentEntry);
                break;
            }
            case 2: {
                if (currentEntry.getGameMode() != receivedEntry.gameMode() && this.client.player != null && this.client.player.getUuid().equals(receivedEntry.profileId())) {
                    this.client.player.onGameModeChanged(receivedEntry.gameMode());
                }
                currentEntry.setGameMode(receivedEntry.gameMode());
                break;
            }
            case 3: {
                if (receivedEntry.listed()) {
                    this.listedPlayerListEntries.add(currentEntry);
                    break;
                }
                this.listedPlayerListEntries.remove(currentEntry);
                break;
            }
            case 4: {
                currentEntry.setLatency(receivedEntry.latency());
                break;
            }
            case 5: {
                currentEntry.setDisplayName(receivedEntry.displayName());
                break;
            }
            case 6: {
                currentEntry.setShowHat(receivedEntry.showHat());
                break;
            }
            case 7: {
                currentEntry.setListOrder(receivedEntry.listOrder());
            }
        }
    }

    private void setPublicSession(PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry) {
        GameProfile gameProfile = currentEntry.getProfile();
        SignatureVerifier signatureVerifier = this.client.getApiServices().serviceSignatureVerifier();
        if (signatureVerifier == null) {
            LOGGER.warn("Ignoring chat session from {} due to missing Services public key", (Object)gameProfile.name());
            currentEntry.resetSession(this.isSecureChatEnforced());
            return;
        }
        PublicPlayerSession.Serialized serialized = receivedEntry.chatSession();
        if (serialized != null) {
            try {
                PublicPlayerSession publicPlayerSession = serialized.toSession(gameProfile, signatureVerifier);
                currentEntry.setSession(publicPlayerSession);
            }
            catch (PlayerPublicKey.PublicKeyException publicKeyException) {
                LOGGER.error("Failed to validate profile key for player: '{}'", (Object)gameProfile.name(), (Object)publicKeyException);
                currentEntry.resetSession(this.isSecureChatEnforced());
            }
        } else {
            currentEntry.resetSession(this.isSecureChatEnforced());
        }
    }

    private boolean isSecureChatEnforced() {
        return this.client.getApiServices().providesProfileKeys() && this.secureChatEnforced;
    }

    public void onPlayerAbilities(PlayerAbilitiesS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ClientPlayerEntity playerEntity = this.client.player;
        playerEntity.getAbilities().flying = packet.isFlying();
        playerEntity.getAbilities().creativeMode = packet.isCreativeMode();
        playerEntity.getAbilities().invulnerable = packet.isInvulnerable();
        playerEntity.getAbilities().allowFlying = packet.allowFlying();
        playerEntity.getAbilities().setFlySpeed(packet.getFlySpeed());
        playerEntity.getAbilities().setWalkSpeed(packet.getWalkSpeed());
    }

    public void onPlaySound(PlaySoundS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.world.playSound((Entity)this.client.player, packet.getX(), packet.getY(), packet.getZ(), packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch(), packet.getSeed());
    }

    public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (entity == null) {
            return;
        }
        this.client.world.playSoundFromEntity((Entity)this.client.player, entity, packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch(), packet.getSeed());
    }

    public void onBossBar(BossBarS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.inGameHud.getBossBarHud().handlePacket(packet);
    }

    public void onCooldownUpdate(CooldownUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (packet.cooldown() == 0) {
            this.client.player.getItemCooldownManager().remove(packet.cooldownGroup());
        } else {
            this.client.player.getItemCooldownManager().set(packet.cooldownGroup(), packet.cooldown());
        }
    }

    public void onVehicleMove(VehicleMoveS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.client.player.getRootVehicle();
        if (entity != this.client.player && entity.isLogicalSideForUpdatingMovement()) {
            Vec3d vec3d2;
            Vec3d vec3d = packet.position();
            if (vec3d.distanceTo(vec3d2 = entity.isInterpolating() ? entity.getInterpolator().getLerpedPos() : entity.getEntityPos()) > (double)1.0E-5f) {
                if (entity.isInterpolating()) {
                    entity.getInterpolator().clear();
                }
                entity.updatePositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), packet.yaw(), packet.pitch());
            }
            this.connection.send((Packet)VehicleMoveC2SPacket.fromVehicle((Entity)entity));
        }
    }

    public void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ItemStack itemStack = this.client.player.getStackInHand(packet.getHand());
        BookScreen.Contents contents = BookScreen.Contents.create((ItemStack)itemStack);
        if (contents != null) {
            this.client.setScreen((Screen)new BookScreen(contents));
        }
    }

    public void onCustomPayload(CustomPayload payload) {
        this.warnOnUnknownPayload(payload);
    }

    private void warnOnUnknownPayload(CustomPayload payload) {
        LOGGER.warn("Unknown custom packet payload: {}", (Object)payload.getId().id());
    }

    public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        String string = packet.getName();
        if (packet.getMode() == 0) {
            this.scoreboard.addObjective(string, ScoreboardCriterion.DUMMY, packet.getDisplayName(), packet.getType(), false, (NumberFormat)packet.getNumberFormat().orElse(null));
        } else {
            ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
            if (scoreboardObjective != null) {
                if (packet.getMode() == 1) {
                    this.scoreboard.removeObjective(scoreboardObjective);
                } else if (packet.getMode() == 2) {
                    scoreboardObjective.setRenderType(packet.getType());
                    scoreboardObjective.setDisplayName(packet.getDisplayName());
                    scoreboardObjective.setNumberFormat((NumberFormat)packet.getNumberFormat().orElse(null));
                }
            }
        }
    }

    public void onScoreboardScoreUpdate(ScoreboardScoreUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        String string = packet.objectiveName();
        ScoreHolder scoreHolder = ScoreHolder.fromName((String)packet.scoreHolderName());
        ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
        if (scoreboardObjective != null) {
            ScoreAccess scoreAccess = this.scoreboard.getOrCreateScore(scoreHolder, scoreboardObjective, true);
            scoreAccess.setScore(packet.score());
            scoreAccess.setDisplayText((Text)packet.display().orElse(null));
            scoreAccess.setNumberFormat((NumberFormat)packet.numberFormat().orElse(null));
        } else {
            LOGGER.warn("Received packet for unknown scoreboard objective: {}", (Object)string);
        }
    }

    public void onScoreboardScoreReset(ScoreboardScoreResetS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        String string = packet.objectiveName();
        ScoreHolder scoreHolder = ScoreHolder.fromName((String)packet.scoreHolderName());
        if (string == null) {
            this.scoreboard.removeScores(scoreHolder);
        } else {
            ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
            if (scoreboardObjective != null) {
                this.scoreboard.removeScore(scoreHolder, scoreboardObjective);
            } else {
                LOGGER.warn("Received packet for unknown scoreboard objective: {}", (Object)string);
            }
        }
    }

    public void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        String string = packet.getName();
        ScoreboardObjective scoreboardObjective = string == null ? null : this.scoreboard.getNullableObjective(string);
        this.scoreboard.setObjectiveSlot(packet.getSlot(), scoreboardObjective);
    }

    public void onTeam(TeamS2CPacket packet) {
        Team team2;
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        TeamS2CPacket.Operation operation = packet.getTeamOperation();
        if (operation == TeamS2CPacket.Operation.ADD) {
            team2 = this.scoreboard.addTeam(packet.getTeamName());
        } else {
            team2 = this.scoreboard.getTeam(packet.getTeamName());
            if (team2 == null) {
                LOGGER.warn("Received packet for unknown team {}: team action: {}, player action: {}", new Object[]{packet.getTeamName(), packet.getTeamOperation(), packet.getPlayerListOperation()});
                return;
            }
        }
        Optional optional = packet.getTeam();
        optional.ifPresent(team -> {
            team2.setDisplayName(team.getDisplayName());
            team2.setColor(team.getColor());
            team2.setFriendlyFlagsBitwise(team.getFriendlyFlagsBitwise());
            team2.setNameTagVisibilityRule(team.getNameTagVisibilityRule());
            team2.setCollisionRule(team.getCollisionRule());
            team2.setPrefix(team.getPrefix());
            team2.setSuffix(team.getSuffix());
        });
        TeamS2CPacket.Operation operation2 = packet.getPlayerListOperation();
        if (operation2 == TeamS2CPacket.Operation.ADD) {
            for (String string : packet.getPlayerNames()) {
                this.scoreboard.addScoreHolderToTeam(string, team2);
            }
        } else if (operation2 == TeamS2CPacket.Operation.REMOVE) {
            for (String string : packet.getPlayerNames()) {
                this.scoreboard.removeScoreHolderFromTeam(string, team2);
            }
        }
        if (operation == TeamS2CPacket.Operation.REMOVE) {
            this.scoreboard.removeTeam(team2);
        }
    }

    public void onParticle(ParticleS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (packet.getCount() == 0) {
            double d = packet.getSpeed() * packet.getOffsetX();
            double e = packet.getSpeed() * packet.getOffsetY();
            double f = packet.getSpeed() * packet.getOffsetZ();
            try {
                this.world.addParticleClient(packet.getParameters(), packet.shouldForceSpawn(), packet.isImportant(), packet.getX(), packet.getY(), packet.getZ(), d, e, f);
            }
            catch (Throwable throwable) {
                LOGGER.warn("Could not spawn particle effect {}", (Object)packet.getParameters());
            }
        } else {
            for (int i = 0; i < packet.getCount(); ++i) {
                double g = this.random.nextGaussian() * (double)packet.getOffsetX();
                double h = this.random.nextGaussian() * (double)packet.getOffsetY();
                double j = this.random.nextGaussian() * (double)packet.getOffsetZ();
                double k = this.random.nextGaussian() * (double)packet.getSpeed();
                double l = this.random.nextGaussian() * (double)packet.getSpeed();
                double m = this.random.nextGaussian() * (double)packet.getSpeed();
                try {
                    this.world.addParticleClient(packet.getParameters(), packet.shouldForceSpawn(), packet.isImportant(), packet.getX() + g, packet.getY() + h, packet.getZ() + j, k, l, m);
                    continue;
                }
                catch (Throwable throwable2) {
                    LOGGER.warn("Could not spawn particle effect {}", (Object)packet.getParameters());
                    return;
                }
            }
        }
    }

    public void onEntityAttributes(EntityAttributesS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (entity == null) {
            return;
        }
        if (!(entity instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + String.valueOf(entity) + ")");
        }
        AttributeContainer attributeContainer = ((LivingEntity)entity).getAttributes();
        for (EntityAttributesS2CPacket.Entry entry : packet.getEntries()) {
            EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance(entry.attribute());
            if (entityAttributeInstance == null) {
                LOGGER.warn("Entity {} does not have attribute {}", (Object)entity, (Object)entry.attribute().getIdAsString());
                continue;
            }
            entityAttributeInstance.setBaseValue(entry.base());
            entityAttributeInstance.clearModifiers();
            for (EntityAttributeModifier entityAttributeModifier : entry.modifiers()) {
                entityAttributeInstance.addTemporaryModifier(entityAttributeModifier);
            }
        }
    }

    public void onCraftFailedResponse(CraftFailedResponseS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ScreenHandler screenHandler = this.client.player.currentScreenHandler;
        if (screenHandler.syncId != packet.syncId()) {
            return;
        }
        Screen screen = this.client.currentScreen;
        if (screen instanceof RecipeBookProvider) {
            RecipeBookProvider recipeBookProvider = (RecipeBookProvider)screen;
            recipeBookProvider.onCraftFailed(packet.recipeDisplay());
        }
    }

    public void onLightUpdate(LightUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        int i = packet.getChunkX();
        int j = packet.getChunkZ();
        LightData lightData = packet.getData();
        this.world.enqueueChunkUpdate(() -> this.readLightData(i, j, lightData, true));
    }

    private void readLightData(int x, int z, LightData data, boolean scheduleBlockRenders) {
        LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
        BitSet bitSet = data.getInitedSky();
        BitSet bitSet2 = data.getUninitedSky();
        Iterator iterator = data.getSkyNibbles().iterator();
        this.updateLighting(x, z, lightingProvider, LightType.SKY, bitSet, bitSet2, iterator, scheduleBlockRenders);
        BitSet bitSet3 = data.getInitedBlock();
        BitSet bitSet4 = data.getUninitedBlock();
        Iterator iterator2 = data.getBlockNibbles().iterator();
        this.updateLighting(x, z, lightingProvider, LightType.BLOCK, bitSet3, bitSet4, iterator2, scheduleBlockRenders);
        lightingProvider.setColumnEnabled(new ChunkPos(x, z), true);
    }

    public void onSetTradeOffers(SetTradeOffersS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        ScreenHandler screenHandler = this.client.player.currentScreenHandler;
        if (packet.getSyncId() == screenHandler.syncId && screenHandler instanceof MerchantScreenHandler) {
            MerchantScreenHandler merchantScreenHandler = (MerchantScreenHandler)screenHandler;
            merchantScreenHandler.setOffers(packet.getOffers());
            merchantScreenHandler.setExperienceFromServer(packet.getExperience());
            merchantScreenHandler.setLevelProgress(packet.getLevelProgress());
            merchantScreenHandler.setLeveled(packet.isLeveled());
            merchantScreenHandler.setCanRefreshTrades(packet.isRefreshable());
        }
    }

    public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.chunkLoadDistance = packet.getDistance();
        this.client.options.setServerViewDistance(this.chunkLoadDistance);
        this.world.getChunkManager().updateLoadDistance(packet.getDistance());
    }

    public void onSimulationDistance(SimulationDistanceS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.simulationDistance = packet.simulationDistance();
        this.world.setSimulationDistance(this.simulationDistance);
    }

    public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.world.getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
    }

    public void onPlayerActionResponse(PlayerActionResponseS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.world.handlePlayerActionResponse(packet.sequence());
    }

    public void onBundle(BundleS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        for (Packet packet2 : packet.getPackets()) {
            packet2.apply((PacketListener)this);
        }
    }

    public void onProjectilePower(ProjectilePowerS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (entity instanceof ExplosiveProjectileEntity) {
            ExplosiveProjectileEntity explosiveProjectileEntity = (ExplosiveProjectileEntity)entity;
            explosiveProjectileEntity.accelerationPower = packet.getAccelerationPower();
        }
    }

    public void onStartChunkSend(StartChunkSendS2CPacket packet) {
        this.chunkBatchSizeCalculator.onStartChunkSend();
    }

    public void onChunkSent(ChunkSentS2CPacket packet) {
        this.chunkBatchSizeCalculator.onChunkSent(packet.batchSize());
        this.sendPacket((Packet)new AcknowledgeChunksC2SPacket(this.chunkBatchSizeCalculator.getDesiredChunksPerTick()));
    }

    public void onDebugSample(DebugSampleS2CPacket packet) {
        this.client.getDebugHud().set(packet.sample(), packet.debugSampleType());
    }

    public void onPingResult(PingResultS2CPacket packet) {
        this.pingMeasurer.onPingResult(packet);
    }

    public void onTestInstanceBlockStatus(TestInstanceBlockStatusS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Screen screen = this.client.currentScreen;
        if (screen instanceof TestInstanceBlockScreen) {
            TestInstanceBlockScreen testInstanceBlockScreen = (TestInstanceBlockScreen)screen;
            testInstanceBlockScreen.handleStatus(packet.status(), packet.size());
        }
    }

    public void onWaypoint(WaypointS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        packet.apply((TrackedWaypointHandler)this.waypointHandler);
    }

    public void onChunkValueDebug(ChunkValueDebugS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.debugSubscriptionManager.updateChunk(this.world.getTime(), packet.chunkPos(), packet.update());
    }

    public void onBlockValueDebug(BlockValueDebugS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.debugSubscriptionManager.updateBlock(this.world.getTime(), packet.blockPos(), packet.update());
    }

    public void onEntityValueDebug(EntityValueDebugS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        Entity entity = this.world.getEntityById(packet.entityId());
        if (entity != null) {
            this.debugSubscriptionManager.updateEntity(this.world.getTime(), entity, packet.update());
        }
    }

    public void onEventDebug(EventDebugS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.debugSubscriptionManager.addEvent(this.world.getTime(), packet.event());
    }

    public void onGameTestHighlightPos(GameTestHighlightPosS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.client.worldRenderer.gameTestDebugRenderer.addMarker(packet.absolutePos(), packet.relativePos());
    }

    private void updateLighting(int chunkX, int chunkZ, LightingProvider provider, LightType type, BitSet inited, BitSet uninited, Iterator<byte[]> nibbles, boolean scheduleBlockRenders) {
        for (int i = 0; i < provider.getHeight(); ++i) {
            int j = provider.getBottomY() + i;
            boolean bl = inited.get(i);
            boolean bl2 = uninited.get(i);
            if (!bl && !bl2) continue;
            provider.enqueueSectionData(type, ChunkSectionPos.from((int)chunkX, (int)j, (int)chunkZ), bl ? new ChunkNibbleArray((byte[])nibbles.next().clone()) : new ChunkNibbleArray());
            if (!scheduleBlockRenders) continue;
            this.world.scheduleBlockRenders(chunkX, j, chunkZ);
        }
    }

    public ClientConnection getConnection() {
        return this.connection;
    }

    public boolean isConnectionOpen() {
        return this.connection.isOpen() && !this.worldCleared;
    }

    public Collection<PlayerListEntry> getListedPlayerListEntries() {
        return this.listedPlayerListEntries;
    }

    public Collection<PlayerListEntry> getPlayerList() {
        return this.playerListEntries.values();
    }

    public Collection<UUID> getPlayerUuids() {
        return this.playerListEntries.keySet();
    }

    public @Nullable PlayerListEntry getPlayerListEntry(UUID uuid) {
        return (PlayerListEntry)this.playerListEntries.get(uuid);
    }

    public @Nullable PlayerListEntry getPlayerListEntry(String profileName) {
        for (PlayerListEntry playerListEntry : this.playerListEntries.values()) {
            if (!playerListEntry.getProfile().name().equals(profileName)) continue;
            return playerListEntry;
        }
        return null;
    }

    public Map<UUID, PlayerListEntry> getSeenPlayers() {
        return this.seenPlayers;
    }

    public @Nullable PlayerListEntry getCaseInsensitivePlayerInfo(String name) {
        for (PlayerListEntry playerListEntry : this.playerListEntries.values()) {
            if (!playerListEntry.getProfile().name().equalsIgnoreCase(name)) continue;
            return playerListEntry;
        }
        return null;
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    public ClientAdvancementManager getAdvancementHandler() {
        return this.advancementHandler;
    }

    public CommandDispatcher<ClientCommandSource> getCommandDispatcher() {
        return this.commandDispatcher;
    }

    public ClientWorld getWorld() {
        return this.world;
    }

    public DataQueryHandler getDataQueryHandler() {
        return this.dataQueryHandler;
    }

    public UUID getSessionId() {
        return this.sessionId;
    }

    public Set<RegistryKey<World>> getWorldKeys() {
        return this.worldKeys;
    }

    public DynamicRegistryManager.Immutable getRegistryManager() {
        return this.combinedDynamicRegistries;
    }

    public void acknowledge(MessageSignatureData signature, boolean displayed) {
        if (this.lastSeenMessagesCollector.add(signature, displayed) && this.lastSeenMessagesCollector.getMessageCount() > 64) {
            this.sendAcknowledgment();
        }
    }

    private void sendAcknowledgment() {
        int i = this.lastSeenMessagesCollector.resetMessageCount();
        if (i > 0) {
            this.sendPacket((Packet)new MessageAcknowledgmentC2SPacket(i));
        }
    }

    public void sendChatMessage(String content) {
        Instant instant = Instant.now();
        long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
        LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = this.lastSeenMessagesCollector.collect();
        MessageSignatureData messageSignatureData = this.messagePacker.pack(new MessageBody(content, instant, l, lastSeenMessages.lastSeen()));
        this.sendPacket((Packet)new ChatMessageC2SPacket(content, instant, l, messageSignatureData, lastSeenMessages.update()));
    }

    public void sendChatCommand(String command) {
        SignedArgumentList signedArgumentList = SignedArgumentList.of((ParseResults)this.commandDispatcher.parse(command, (Object)this.commandSource));
        if (signedArgumentList.arguments().isEmpty()) {
            this.sendPacket((Packet)new CommandExecutionC2SPacket(command));
            return;
        }
        Instant instant = Instant.now();
        long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
        LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = this.lastSeenMessagesCollector.collect();
        ArgumentSignatureDataMap argumentSignatureDataMap = ArgumentSignatureDataMap.sign((SignedArgumentList)signedArgumentList, value -> {
            MessageBody messageBody = new MessageBody(value, instant, l, lastSeenMessages.lastSeen());
            return this.messagePacker.pack(messageBody);
        });
        this.sendPacket((Packet)new ChatCommandSignedC2SPacket(command, instant, l, argumentSignatureDataMap, lastSeenMessages.update()));
    }

    public void runClickEventCommand(String command, @Nullable Screen afterActionScreen) {
        switch (this.parseCommand(command).ordinal()) {
            case 0: {
                this.sendPacket((Packet)new CommandExecutionC2SPacket(command));
                this.client.setScreen(afterActionScreen);
                break;
            }
            case 1: {
                this.openConfirmRunCommandScreen(command, "multiplayer.confirm_command.parse_errors", afterActionScreen);
                break;
            }
            case 3: {
                this.openConfirmRunCommandScreen(command, "multiplayer.confirm_command.permissions_required", afterActionScreen);
                break;
            }
            case 2: {
                this.suggestCommand(command, "multiplayer.confirm_command.signature_required", afterActionScreen);
            }
        }
    }

    private CommandRunResult parseCommand(String command) {
        ParseResults parseResults = this.commandDispatcher.parse(command, (Object)this.commandSource);
        if (!ClientPlayNetworkHandler.validate((ParseResults)parseResults)) {
            return CommandRunResult.PARSE_ERRORS;
        }
        if (SignedArgumentList.isNotEmpty((ParseResults)parseResults)) {
            return CommandRunResult.SIGNATURE_REQUIRED;
        }
        ParseResults parseResults2 = this.commandDispatcher.parse(command, (Object)this.restrictedCommandSource);
        if (!ClientPlayNetworkHandler.validate((ParseResults)parseResults2)) {
            return CommandRunResult.PERMISSIONS_REQUIRED;
        }
        return CommandRunResult.NO_ISSUES;
    }

    private static boolean validate(ParseResults<?> parseResults) {
        return !parseResults.getReader().canRead() && parseResults.getExceptions().isEmpty() && parseResults.getContext().getLastChild().getCommand() != null;
    }

    private void openConfirmCommandScreen(String command, String message, Text yesText, Runnable action) {
        Screen screen = this.client.currentScreen;
        this.client.setScreen((Screen)new ConfirmScreen(confirmed -> {
            if (confirmed) {
                action.run();
            } else {
                this.client.setScreen(screen);
            }
        }, CONFIRM_COMMAND_TITLE_TEXT, (Text)Text.translatable((String)message, (Object[])new Object[]{Text.literal((String)command).formatted(Formatting.YELLOW)}), yesText, screen != null ? ScreenTexts.BACK : ScreenTexts.CANCEL));
    }

    private void openConfirmRunCommandScreen(String command, String message, @Nullable Screen screenAfterRun) {
        this.openConfirmCommandScreen(command, message, CONFIRM_RUN_COMMAND_TEXT, () -> {
            this.sendPacket((Packet)new CommandExecutionC2SPacket(command));
            this.client.setScreen(screenAfterRun);
        });
    }

    private void suggestCommand(String command, String message, @Nullable Screen afterActionScreen) {
        boolean bl = afterActionScreen == null && this.client.getChatRestriction().allowsChat(this.client.isInSingleplayer());
        this.openConfirmCommandScreen(command, message, bl ? CONFIRM_SUGGEST_COMMAND_TEXT : ScreenTexts.COPY, () -> {
            if (bl) {
                this.client.openChatScreen(ChatHud.ChatMethod.COMMAND);
                Screen screen2 = this.client.currentScreen;
                if (screen2 instanceof ChatScreen) {
                    ChatScreen chatScreen = (ChatScreen)screen2;
                    chatScreen.insertText(command, false);
                }
            } else {
                this.client.keyboard.setClipboard("/" + command);
                this.client.setScreen(afterActionScreen);
            }
        });
    }

    public void syncOptions(SyncedClientOptions syncedOptions) {
        if (!syncedOptions.equals((Object)this.syncedOptions)) {
            this.sendPacket((Packet)new ClientOptionsC2SPacket(syncedOptions));
            this.syncedOptions = syncedOptions;
        }
    }

    public void tick() {
        if (this.session != null && this.client.getProfileKeys().isExpired()) {
            this.fetchProfileKey();
        }
        if (this.profileKeyPairFuture != null && this.profileKeyPairFuture.isDone()) {
            ((Optional)this.profileKeyPairFuture.join()).ifPresent(arg_0 -> this.updateKeyPair(arg_0));
            this.profileKeyPairFuture = null;
        }
        this.sendQueuedPackets();
        if (this.client.getDebugHud().shouldShowPacketSizeAndPingCharts()) {
            this.pingMeasurer.ping();
        }
        if (this.world != null) {
            this.debugSubscriptionManager.startTick(this.world.getTime());
        }
        this.worldSession.tick();
        if (this.chunkLoadProgress != null) {
            this.chunkLoadProgress.tick();
            if (this.chunkLoadProgress.isDone()) {
                this.setPlayerLoaded();
                this.chunkLoadProgress = null;
            }
        }
    }

    private void setPlayerLoaded() {
        if (!this.isLoaded()) {
            this.connection.send((Packet)new PlayerLoadedC2SPacket());
            this.setLoaded(true);
        }
    }

    public void fetchProfileKey() {
        this.profileKeyPairFuture = this.client.getProfileKeys().fetchKeyPair();
    }

    private void updateKeyPair(PlayerKeyPair keyPair) {
        if (!this.client.uuidEquals(this.profile.id())) {
            return;
        }
        if (this.session != null && this.session.keyPair().equals((Object)keyPair)) {
            return;
        }
        this.session = ClientPlayerSession.create((PlayerKeyPair)keyPair);
        this.messagePacker = this.session.createPacker(this.profile.id());
        this.sendPacket((Packet)new PlayerSessionC2SPacket(this.session.toPublicSession().toSerialized()));
    }

    protected DialogNetworkAccess createDialogNetworkAccess() {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public @Nullable ServerInfo getServerInfo() {
        return this.serverInfo;
    }

    public FeatureSet getEnabledFeatures() {
        return this.enabledFeatures;
    }

    public boolean hasFeature(FeatureSet feature) {
        return feature.isSubsetOf(this.getEnabledFeatures());
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public BrewingRecipeRegistry getBrewingRecipeRegistry() {
        return this.brewingRecipeRegistry;
    }

    public FuelRegistry getFuelRegistry() {
        return this.fuelRegistry;
    }

    public void refreshSearchManager() {
        this.searchManager.refresh();
    }

    public SearchManager getSearchManager() {
        return this.searchManager;
    }

    public void registerForCleaning(DataCache<?, ?> dataCache) {
        this.cachedData.add(new WeakReference(dataCache));
    }

    public ComponentChangesHash.ComponentHasher getComponentHasher() {
        return this.componentHasher;
    }

    public ClientWaypointHandler getWaypointHandler() {
        return this.waypointHandler;
    }

    public DebugDataStore getDebugDataStore() {
        return this.debugSubscriptionManager.createDebugDataStore((World)this.world);
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    private void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}

