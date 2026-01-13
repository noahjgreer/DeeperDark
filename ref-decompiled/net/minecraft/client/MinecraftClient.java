/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.minecraft.BanDetails
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.UserApiService$UserFlag
 *  com.mojang.authlib.minecraft.UserApiService$UserProperties
 *  com.mojang.authlib.yggdrasil.ProfileActionType
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.blaze3d.platform.GLX
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.jtracy.DiscontinuousFrame
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.Bootstrap
 *  net.minecraft.SharedConstants
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.ClientBrandRetriever
 *  net.minecraft.client.Keyboard
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.MinecraftClient$2
 *  net.minecraft.client.MinecraftClient$ChatRestriction
 *  net.minecraft.client.MinecraftClient$LoadingContext
 *  net.minecraft.client.Mouse
 *  net.minecraft.client.QuickPlay
 *  net.minecraft.client.QuickPlayLogger
 *  net.minecraft.client.QuickPlayLogger$WorldType
 *  net.minecraft.client.RunArgs
 *  net.minecraft.client.RunArgs$QuickPlayVariant
 *  net.minecraft.client.WindowEventHandler
 *  net.minecraft.client.WindowSettings
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.font.FontManager
 *  net.minecraft.client.font.FreeTypeUtil
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.GlTimer
 *  net.minecraft.client.gl.ShaderLoader
 *  net.minecraft.client.gl.WindowFramebuffer
 *  net.minecraft.client.gui.LogoDrawer
 *  net.minecraft.client.gui.hud.ChatHud$ChatMethod
 *  net.minecraft.client.gui.hud.DebugHud
 *  net.minecraft.client.gui.hud.InGameHud
 *  net.minecraft.client.gui.hud.debug.DebugHudEntries
 *  net.minecraft.client.gui.hud.debug.DebugHudProfile
 *  net.minecraft.client.gui.hud.debug.chart.PieChart
 *  net.minecraft.client.gui.navigation.GuiNavigationType
 *  net.minecraft.client.gui.screen.AccessibilityOnboardingScreen
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.DeathScreen
 *  net.minecraft.client.gui.screen.GameMenuScreen
 *  net.minecraft.client.gui.screen.MessageScreen
 *  net.minecraft.client.gui.screen.OutOfMemoryScreen
 *  net.minecraft.client.gui.screen.Overlay
 *  net.minecraft.client.gui.screen.ProgressScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.SleepingChatScreen
 *  net.minecraft.client.gui.screen.SplashOverlay
 *  net.minecraft.client.gui.screen.TitleScreen
 *  net.minecraft.client.gui.screen.advancement.AdvancementsScreen
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
 *  net.minecraft.client.gui.screen.ingame.HandledScreens
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen
 *  net.minecraft.client.gui.screen.world.LevelLoadingScreen
 *  net.minecraft.client.gui.screen.world.LevelLoadingScreen$WorldEntryReason
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.network.ClientLoginNetworkHandler
 *  net.minecraft.client.network.ClientMannequinEntity
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerInteractionManager
 *  net.minecraft.client.network.ClientPlayerProfileResolver
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.network.SocialInteractionsManager
 *  net.minecraft.client.network.message.MessageHandler
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.GraphicsMode
 *  net.minecraft.client.option.HotbarStorage
 *  net.minecraft.client.option.InactivityFpsLimiter
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.option.NarratorMode
 *  net.minecraft.client.option.Perspective
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.client.particle.ParticleSpriteManager
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.RealmsPeriodicCheckers
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.render.BufferBuilderStorage
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.CameraOverride
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.MapRenderer
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.render.RenderTickCounter$Dynamic
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.render.block.BlockModels
 *  net.minecraft.client.render.block.BlockRenderManager
 *  net.minecraft.client.render.block.entity.BlockEntityRenderManager
 *  net.minecraft.client.render.entity.EntityRenderManager
 *  net.minecraft.client.render.entity.EntityRendererFactories
 *  net.minecraft.client.render.entity.equipment.EquipmentModelLoader
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.item.ItemRenderer
 *  net.minecraft.client.render.model.BakedModelManager
 *  net.minecraft.client.render.model.BlockStateModel
 *  net.minecraft.client.resource.DefaultClientResourcePackProvider
 *  net.minecraft.client.resource.DryFoliageColormapResourceSupplier
 *  net.minecraft.client.resource.FoliageColormapResourceSupplier
 *  net.minecraft.client.resource.GrassColormapResourceSupplier
 *  net.minecraft.client.resource.PeriodicNotificationManager
 *  net.minecraft.client.resource.ResourceReloadLogger
 *  net.minecraft.client.resource.ResourceReloadLogger$ReloadReason
 *  net.minecraft.client.resource.SplashTextResourceSupplier
 *  net.minecraft.client.resource.VideoWarningManager
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.client.resource.language.LanguageManager
 *  net.minecraft.client.resource.server.ServerResourcePackLoader
 *  net.minecraft.client.resource.waypoint.WaypointStyleAssetManager
 *  net.minecraft.client.session.Bans
 *  net.minecraft.client.session.ProfileKeys
 *  net.minecraft.client.session.Session
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.report.ReporterEnvironment
 *  net.minecraft.client.session.telemetry.GameLoadTimeEvent
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty
 *  net.minecraft.client.session.telemetry.TelemetryManager
 *  net.minecraft.client.sound.MusicTracker
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.client.texture.AtlasManager
 *  net.minecraft.client.texture.MapTextureManager
 *  net.minecraft.client.texture.PlayerSkinCache
 *  net.minecraft.client.texture.PlayerSkinProvider
 *  net.minecraft.client.texture.PlayerSkinTextureDownloader
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.toast.SystemToast
 *  net.minecraft.client.toast.SystemToast$Type
 *  net.minecraft.client.toast.Toast
 *  net.minecraft.client.toast.ToastManager
 *  net.minecraft.client.toast.TutorialToast
 *  net.minecraft.client.toast.TutorialToast$Type
 *  net.minecraft.client.tutorial.TutorialManager
 *  net.minecraft.client.util.ClientSamplerSource
 *  net.minecraft.client.util.CommandHistoryManager
 *  net.minecraft.client.util.GlException
 *  net.minecraft.client.util.Icons
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.util.NarratorManager
 *  net.minecraft.client.util.ScreenshotRecorder
 *  net.minecraft.client.util.Window
 *  net.minecraft.client.util.WindowProvider
 *  net.minecraft.client.util.tracy.TracyFrameCapturer
 *  net.minecraft.client.world.ClientChunkLoadProgress
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.AttackRangeComponent
 *  net.minecraft.component.type.PiercingWeaponComponent
 *  net.minecraft.datafixer.Schemas
 *  net.minecraft.dialog.Dialogs
 *  net.minecraft.dialog.type.Dialog
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.network.PacketApplyBatcher
 *  net.minecraft.network.listener.ClientLoginPacketListener
 *  net.minecraft.network.message.ChatVisibility
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.DialogTags
 *  net.minecraft.resource.DefaultResourcePack
 *  net.minecraft.resource.FileResourcePackProvider
 *  net.minecraft.resource.ReloadableResourceManagerImpl
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.ResourcePack
 *  net.minecraft.resource.ResourcePackManager
 *  net.minecraft.resource.ResourcePackProvider
 *  net.minecraft.resource.ResourcePackSource
 *  net.minecraft.resource.ResourceReload
 *  net.minecraft.resource.ResourceReloader
 *  net.minecraft.resource.ResourceType
 *  net.minecraft.server.GameProfileResolver
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.SaveLoader
 *  net.minecraft.server.integrated.IntegratedServer
 *  net.minecraft.server.integrated.IntegratedServerLoader
 *  net.minecraft.server.world.ChunkLevels
 *  net.minecraft.sound.MusicSound
 *  net.minecraft.sound.MusicType
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$OpenFile
 *  net.minecraft.text.KeybindTranslations
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.ActionResult$Fail
 *  net.minecraft.util.ActionResult$Success
 *  net.minecraft.util.ActionResult$SwingSource
 *  net.minecraft.util.ApiServices
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.ModStatus
 *  net.minecraft.util.Nullables
 *  net.minecraft.util.SystemDetails
 *  net.minecraft.util.TickDurationMonitor
 *  net.minecraft.util.TimeHelper
 *  net.minecraft.util.Unit
 *  net.minecraft.util.Urls
 *  net.minecraft.util.Util
 *  net.minecraft.util.ZipCompressor
 *  net.minecraft.util.crash.CrashException
 *  net.minecraft.util.crash.CrashMemoryReserve
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 *  net.minecraft.util.crash.ReportType
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.path.PathUtil
 *  net.minecraft.util.path.SymlinkFinder
 *  net.minecraft.util.profiler.DebugRecorder
 *  net.minecraft.util.profiler.DummyProfiler
 *  net.minecraft.util.profiler.DummyRecorder
 *  net.minecraft.util.profiler.EmptyProfileResult
 *  net.minecraft.util.profiler.ProfileResult
 *  net.minecraft.util.profiler.Profiler
 *  net.minecraft.util.profiler.Profilers
 *  net.minecraft.util.profiler.Profilers$Scoped
 *  net.minecraft.util.profiler.RecordDumper
 *  net.minecraft.util.profiler.Recorder
 *  net.minecraft.util.profiler.SamplerSource
 *  net.minecraft.util.profiler.ScopedProfiler
 *  net.minecraft.util.profiler.TickTimeTracker
 *  net.minecraft.util.thread.ReentrantThreadExecutor
 *  net.minecraft.world.World
 *  net.minecraft.world.attribute.BackgroundMusic
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.chunk.ChunkLoadProgress
 *  net.minecraft.world.chunk.LoggingChunkLoadProgress
 *  net.minecraft.world.debug.gizmo.GizmoCollector
 *  net.minecraft.world.debug.gizmo.GizmoCollectorImpl
 *  net.minecraft.world.debug.gizmo.GizmoCollectorImpl$Entry
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.GizmoDrawing$CollectorScope
 *  net.minecraft.world.level.storage.LevelStorage
 *  net.minecraft.world.level.storage.LevelStorage$Session
 *  net.minecraft.world.tick.TickManager
 *  org.apache.commons.io.FileUtils
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileActionType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import com.mojang.jtracy.DiscontinuousFrame;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.lang.runtime.SwitchBootstraps;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.QuickPlay;
import net.minecraft.client.QuickPlayLogger;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlTimer;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.WindowFramebuffer;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudProfile;
import net.minecraft.client.gui.hud.debug.chart.PieChart;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientMannequinEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerProfileResolver;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.InactivityFpsLimiter;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleSpriteManager;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraOverride;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.client.resource.DryFoliageColormapResourceSupplier;
import net.minecraft.client.resource.FoliageColormapResourceSupplier;
import net.minecraft.client.resource.GrassColormapResourceSupplier;
import net.minecraft.client.resource.PeriodicNotificationManager;
import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.resource.waypoint.WaypointStyleAssetManager;
import net.minecraft.client.session.Bans;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.session.telemetry.GameLoadTimeEvent;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.MapTextureManager;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.PlayerSkinTextureDownloader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.ClientSamplerSource;
import net.minecraft.client.util.CommandHistoryManager;
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.WindowProvider;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import net.minecraft.client.world.ClientChunkLoadProgress;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttackRangeComponent;
import net.minecraft.component.type.PiercingWeaponComponent;
import net.minecraft.datafixer.Schemas;
import net.minecraft.dialog.Dialogs;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketApplyBatcher;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DialogTags;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.GameProfileResolver;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.server.world.ChunkLevels;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.MusicType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.KeybindTranslations;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ModStatus;
import net.minecraft.util.Nullables;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import net.minecraft.util.ZipCompressor;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashMemoryReserve;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.path.SymlinkFinder;
import net.minecraft.util.profiler.DebugRecorder;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.DummyRecorder;
import net.minecraft.util.profiler.EmptyProfileResult;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.RecordDumper;
import net.minecraft.util.profiler.Recorder;
import net.minecraft.util.profiler.SamplerSource;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.util.profiler.TickTimeTracker;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.World;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.chunk.ChunkLoadProgress;
import net.minecraft.world.chunk.LoggingChunkLoadProgress;
import net.minecraft.world.debug.gizmo.GizmoCollector;
import net.minecraft.world.debug.gizmo.GizmoCollectorImpl;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.tick.TickManager;
import org.apache.commons.io.FileUtils;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class MinecraftClient
extends ReentrantThreadExecutor<Runnable>
implements WindowEventHandler {
    static MinecraftClient instance;
    private static final Logger LOGGER;
    private static final int field_32145 = 10;
    public static final Identifier DEFAULT_FONT_ID;
    public static final Identifier UNICODE_FONT_ID;
    public static final Identifier ALT_TEXT_RENDERER_ID;
    private static final Identifier REGIONAL_COMPLIANCIES_ID;
    private static final CompletableFuture<Unit> COMPLETED_UNIT_FUTURE;
    private static final Text SOCIAL_INTERACTIONS_NOT_AVAILABLE;
    private static final Text SAVING_LEVEL_TEXT;
    public static final String GL_ERROR_DIALOGUE = "Please make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).";
    private final long UNIVERSE = Double.doubleToLongBits(Math.PI);
    private final Path resourcePackDir;
    private final CompletableFuture<@Nullable com.mojang.authlib.yggdrasil.ProfileResult> gameProfileFuture;
    private final TextureManager textureManager;
    private final ShaderLoader shaderLoader;
    private final DataFixer dataFixer;
    private final WindowProvider windowProvider;
    private final Window window;
    private final RenderTickCounter.Dynamic renderTickCounter = new RenderTickCounter.Dynamic(20.0f, 0L, arg_0 -> this.getTargetMillisPerTick(arg_0));
    private final BufferBuilderStorage bufferBuilders;
    public final WorldRenderer worldRenderer;
    private final EntityRenderManager entityRenderManager;
    private final ItemModelManager itemModelManager;
    private final ItemRenderer itemRenderer;
    private final MapRenderer mapRenderer;
    public final ParticleManager particleManager;
    private final ParticleSpriteManager particleSpriteManager;
    private final Session session;
    public final TextRenderer textRenderer;
    public final TextRenderer advanceValidatingTextRenderer;
    public final GameRenderer gameRenderer;
    public final InGameHud inGameHud;
    public final GameOptions options;
    public final DebugHudProfile debugHudEntryList;
    private final HotbarStorage creativeHotbarStorage;
    public final Mouse mouse;
    public final Keyboard keyboard;
    private GuiNavigationType navigationType = GuiNavigationType.NONE;
    public final File runDirectory;
    private final String gameVersion;
    private final String versionType;
    private final Proxy networkProxy;
    private final boolean offlineDeveloperMode;
    private final LevelStorage levelStorage;
    private final boolean isDemo;
    private final boolean multiplayerEnabled;
    private final boolean onlineChatEnabled;
    private final ReloadableResourceManagerImpl resourceManager;
    private final DefaultResourcePack defaultResourcePack;
    private final ServerResourcePackLoader serverResourcePackLoader;
    private final ResourcePackManager resourcePackManager;
    private final LanguageManager languageManager;
    private final BlockColors blockColors;
    private final Framebuffer framebuffer;
    private final @Nullable TracyFrameCapturer tracyFrameCapturer;
    private final SoundManager soundManager;
    private final MusicTracker musicTracker;
    private final FontManager fontManager;
    private final SplashTextResourceSupplier splashTextLoader;
    private final VideoWarningManager videoWarningManager;
    private final PeriodicNotificationManager regionalComplianciesManager = new PeriodicNotificationManager(REGIONAL_COMPLIANCIES_ID, MinecraftClient::isCountrySetTo);
    private final UserApiService userApiService;
    private final CompletableFuture<UserApiService.UserProperties> userPropertiesFuture;
    private final PlayerSkinProvider skinProvider;
    private final AtlasManager atlasManager;
    private final BakedModelManager bakedModelManager;
    private final BlockRenderManager blockRenderManager;
    private final MapTextureManager mapTextureManager;
    private final WaypointStyleAssetManager waypointStyleAssetManager;
    private final ToastManager toastManager;
    private final TutorialManager tutorialManager;
    private final SocialInteractionsManager socialInteractionsManager;
    private final BlockEntityRenderManager blockEntityRenderManager;
    private final TelemetryManager telemetryManager;
    private final ProfileKeys profileKeys;
    private final RealmsPeriodicCheckers realmsPeriodicCheckers;
    private final QuickPlayLogger quickPlayLogger;
    private final ApiServices apiServices;
    private final PlayerSkinCache playerSkinCache;
    public @Nullable ClientPlayerInteractionManager interactionManager;
    public @Nullable ClientWorld world;
    public @Nullable ClientPlayerEntity player;
    private @Nullable IntegratedServer server;
    private @Nullable ClientConnection integratedServerConnection;
    private boolean integratedServerRunning;
    private @Nullable Entity cameraEntity;
    public @Nullable Entity targetedEntity;
    public @Nullable HitResult crosshairTarget;
    private int itemUseCooldown;
    public int attackCooldown;
    private volatile boolean paused;
    private long lastMetricsSampleTime = Util.getMeasuringTimeNano();
    private long nextDebugInfoUpdateTime;
    private int fpsCounter;
    public boolean skipGameRender;
    public @Nullable Screen currentScreen;
    private @Nullable Overlay overlay;
    private boolean disconnecting;
    Thread thread;
    private volatile boolean running;
    private @Nullable Supplier<CrashReport> crashReportSupplier;
    private static int currentFps;
    private long renderTime;
    private final InactivityFpsLimiter inactivityFpsLimiter;
    public boolean wireFrame;
    public boolean chunkCullingEnabled = true;
    private boolean windowFocused;
    private @Nullable CompletableFuture<Void> resourceReloadFuture;
    private @Nullable TutorialToast socialInteractionsToast;
    private int trackingTick;
    private final TickTimeTracker tickTimeTracker;
    private Recorder recorder = DummyRecorder.INSTANCE;
    private final ResourceReloadLogger resourceReloadLogger = new ResourceReloadLogger();
    private long metricsSampleDuration;
    private double gpuUtilizationPercentage;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable GlTimer.Query currentGlTimerQuery;
    private final NarratorManager narratorManager;
    private final MessageHandler messageHandler;
    private AbuseReportContext abuseReportContext;
    private final CommandHistoryManager commandHistoryManager;
    private final SymlinkFinder symlinkFinder;
    private boolean finishedLoading;
    private final long startTime;
    private long uptimeInTicks;
    private final PacketApplyBatcher packetApplyBatcher;
    private final GizmoCollectorImpl gizmoCollector = new GizmoCollectorImpl();
    private List<GizmoCollectorImpl.Entry> gizmos = new ArrayList();

    public MinecraftClient(RunArgs args) {
        super("Client");
        instance = this;
        this.startTime = System.currentTimeMillis();
        this.runDirectory = args.directories.runDir;
        File file = args.directories.assetDir;
        this.resourcePackDir = args.directories.resourcePackDir.toPath();
        this.gameVersion = args.game.version;
        this.versionType = args.game.versionType;
        Path path = this.runDirectory.toPath();
        this.symlinkFinder = LevelStorage.createSymlinkFinder((Path)path.resolve("allowed_symlinks.txt"));
        DefaultClientResourcePackProvider defaultClientResourcePackProvider = new DefaultClientResourcePackProvider(args.directories.getAssetDir(), this.symlinkFinder);
        this.serverResourcePackLoader = new ServerResourcePackLoader(this, path.resolve("downloads"), args.network);
        FileResourcePackProvider resourcePackProvider = new FileResourcePackProvider(this.resourcePackDir, ResourceType.CLIENT_RESOURCES, ResourcePackSource.NONE, this.symlinkFinder);
        this.resourcePackManager = new ResourcePackManager(new ResourcePackProvider[]{defaultClientResourcePackProvider, this.serverResourcePackLoader.getPassthroughPackProvider(), resourcePackProvider});
        this.defaultResourcePack = defaultClientResourcePackProvider.getResourcePack();
        this.networkProxy = args.network.netProxy;
        this.offlineDeveloperMode = args.game.offlineDeveloperMode;
        YggdrasilAuthenticationService yggdrasilAuthenticationService = this.offlineDeveloperMode ? YggdrasilAuthenticationService.createOffline((Proxy)this.networkProxy) : new YggdrasilAuthenticationService(this.networkProxy);
        this.apiServices = ApiServices.create((YggdrasilAuthenticationService)yggdrasilAuthenticationService, (File)this.runDirectory);
        this.session = args.network.session;
        this.gameProfileFuture = this.offlineDeveloperMode ? CompletableFuture.completedFuture(null) : CompletableFuture.supplyAsync(() -> this.apiServices.sessionService().fetchProfile(this.session.getUuidOrNull(), true), (Executor)Util.getDownloadWorkerExecutor());
        this.userApiService = this.createUserApiService(yggdrasilAuthenticationService, args);
        this.userPropertiesFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return this.userApiService.fetchProperties();
            }
            catch (AuthenticationException authenticationException) {
                LOGGER.error("Failed to fetch user properties", (Throwable)authenticationException);
                return UserApiService.OFFLINE_PROPERTIES;
            }
        }, (Executor)Util.getDownloadWorkerExecutor());
        LOGGER.info("Setting user: {}", (Object)this.session.getUsername());
        LOGGER.debug("(Session ID is {})", (Object)this.session.getSessionId());
        this.isDemo = args.game.demo;
        this.multiplayerEnabled = !args.game.multiplayerDisabled;
        this.onlineChatEnabled = !args.game.onlineChatDisabled;
        this.server = null;
        KeybindTranslations.setFactory(KeyBinding::getLocalizedName);
        this.dataFixer = Schemas.getFixer();
        this.thread = Thread.currentThread();
        this.options = new GameOptions(this, this.runDirectory);
        this.debugHudEntryList = new DebugHudProfile(this.runDirectory);
        this.toastManager = new ToastManager(this, this.options);
        boolean bl = this.options.startedCleanly;
        this.options.startedCleanly = false;
        this.options.write();
        this.running = true;
        this.tutorialManager = new TutorialManager(this, this.options);
        this.creativeHotbarStorage = new HotbarStorage(path, this.dataFixer);
        LOGGER.info("Backend library: {}", (Object)RenderSystem.getBackendDescription());
        WindowSettings windowSettings = args.windowSettings;
        if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
            windowSettings = args.windowSettings.withDimensions(this.options.overrideWidth, this.options.overrideHeight);
        }
        if (!bl) {
            windowSettings = windowSettings.withFullscreen(false);
            this.options.fullscreenResolution = null;
            LOGGER.warn("Detected unexpected shutdown during last game startup: resetting fullscreen mode");
        }
        Util.nanoTimeSupplier = RenderSystem.initBackendSystem();
        this.windowProvider = new WindowProvider(this);
        this.window = this.windowProvider.createWindow(windowSettings, this.options.fullscreenResolution, this.getWindowTitle());
        this.onWindowFocusChanged(true);
        this.window.setCloseCallback((Runnable)new /* Unavailable Anonymous Inner Class!! */);
        GameLoadTimeEvent.INSTANCE.stopTimer(TelemetryEventProperty.LOAD_TIME_PRE_WINDOW_MS);
        try {
            this.window.setIcon((ResourcePack)this.defaultResourcePack, SharedConstants.getGameVersion().stable() ? Icons.RELEASE : Icons.SNAPSHOT);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't set icon", (Throwable)iOException);
        }
        this.mouse = new Mouse(this);
        this.mouse.setup(this.window);
        this.keyboard = new Keyboard(this);
        this.keyboard.setup(this.window);
        RenderSystem.initRenderer((long)this.window.getHandle(), (int)this.options.glDebugVerbosity, (boolean)SharedConstants.SYNCHRONOUS_GL_LOGS, (id, type) -> this.getShaderLoader().getSource(id, type), (boolean)args.game.renderDebugLabels);
        this.options.applyGraphicsMode((GraphicsMode)this.options.getPreset().getValue());
        LOGGER.info("Using optional rendering extensions: {}", (Object)String.join((CharSequence)", ", RenderSystem.getDevice().getEnabledExtensions()));
        this.framebuffer = new WindowFramebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.CLIENT_RESOURCES);
        this.resourcePackManager.scanPacks();
        this.options.addResourcePackProfilesToManager(this.resourcePackManager);
        this.languageManager = new LanguageManager(this.options.language, translationStorage -> {
            if (this.player != null) {
                this.player.networkHandler.refreshSearchManager();
            }
        });
        this.resourceManager.registerReloader((ResourceReloader)this.languageManager);
        this.textureManager = new TextureManager((ResourceManager)this.resourceManager);
        this.resourceManager.registerReloader((ResourceReloader)this.textureManager);
        this.shaderLoader = new ShaderLoader(this.textureManager, arg_0 -> this.onShaderResourceReloadFailure(arg_0));
        this.resourceManager.registerReloader((ResourceReloader)this.shaderLoader);
        PlayerSkinTextureDownloader playerSkinTextureDownloader = new PlayerSkinTextureDownloader(this.networkProxy, this.textureManager, (Executor)this);
        this.skinProvider = new PlayerSkinProvider(file.toPath().resolve("skins"), this.apiServices, playerSkinTextureDownloader, (Executor)this);
        this.levelStorage = new LevelStorage(path.resolve("saves"), path.resolve("backups"), this.symlinkFinder, this.dataFixer);
        this.commandHistoryManager = new CommandHistoryManager(path);
        this.musicTracker = new MusicTracker(this);
        this.soundManager = new SoundManager(this.options);
        this.resourceManager.registerReloader((ResourceReloader)this.soundManager);
        this.splashTextLoader = new SplashTextResourceSupplier(this.session);
        this.resourceManager.registerReloader((ResourceReloader)this.splashTextLoader);
        this.atlasManager = new AtlasManager(this.textureManager, ((Integer)this.options.getMipmapLevels().getValue()).intValue());
        this.resourceManager.registerReloader((ResourceReloader)this.atlasManager);
        ClientPlayerProfileResolver gameProfileResolver = new ClientPlayerProfileResolver(this, this.apiServices.profileResolver());
        this.playerSkinCache = new PlayerSkinCache(this.textureManager, this.skinProvider, (GameProfileResolver)gameProfileResolver);
        ClientMannequinEntity.setFactory((PlayerSkinCache)this.playerSkinCache);
        this.fontManager = new FontManager(this.textureManager, this.atlasManager, this.playerSkinCache);
        this.textRenderer = this.fontManager.createTextRenderer();
        this.advanceValidatingTextRenderer = this.fontManager.createAdvanceValidatingTextRenderer();
        this.resourceManager.registerReloader((ResourceReloader)this.fontManager);
        this.onFontOptionsChanged();
        this.resourceManager.registerReloader((ResourceReloader)new GrassColormapResourceSupplier());
        this.resourceManager.registerReloader((ResourceReloader)new FoliageColormapResourceSupplier());
        this.resourceManager.registerReloader((ResourceReloader)new DryFoliageColormapResourceSupplier());
        this.window.setPhase("Startup");
        RenderSystem.setupDefaultState();
        this.window.setPhase("Post startup");
        this.blockColors = BlockColors.create();
        this.bakedModelManager = new BakedModelManager(this.blockColors, this.atlasManager, this.playerSkinCache);
        this.resourceManager.registerReloader((ResourceReloader)this.bakedModelManager);
        EquipmentModelLoader equipmentModelLoader = new EquipmentModelLoader();
        this.resourceManager.registerReloader((ResourceReloader)equipmentModelLoader);
        this.itemModelManager = new ItemModelManager(this.bakedModelManager);
        this.itemRenderer = new ItemRenderer();
        this.mapTextureManager = new MapTextureManager(this.textureManager);
        this.mapRenderer = new MapRenderer(this.atlasManager, this.mapTextureManager);
        try {
            int i = Runtime.getRuntime().availableProcessors();
            Tessellator.initialize();
            this.bufferBuilders = new BufferBuilderStorage(i);
        }
        catch (OutOfMemoryError outOfMemoryError) {
            TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)("Oh no! The game was unable to allocate memory off-heap while trying to start. You may try to free some memory by closing other applications on your computer, check that your system meets the minimum requirements, and try again. If the problem persists, please visit: " + String.valueOf(Urls.MINECRAFT_SUPPORT)), (CharSequence)"ok", (CharSequence)"error", (boolean)true);
            throw new GlException("Unable to allocate render buffers", (Throwable)outOfMemoryError);
        }
        this.socialInteractionsManager = new SocialInteractionsManager(this, this.userApiService);
        this.blockRenderManager = new BlockRenderManager(this.bakedModelManager.getBlockModels(), (SpriteHolder)this.atlasManager, this.blockColors);
        this.resourceManager.registerReloader((ResourceReloader)this.blockRenderManager);
        this.entityRenderManager = new EntityRenderManager(this, this.textureManager, this.itemModelManager, this.mapRenderer, this.blockRenderManager, this.atlasManager, this.textRenderer, this.options, this.bakedModelManager.getEntityModelsSupplier(), equipmentModelLoader, this.playerSkinCache);
        this.resourceManager.registerReloader((ResourceReloader)this.entityRenderManager);
        this.blockEntityRenderManager = new BlockEntityRenderManager(this.textRenderer, this.bakedModelManager.getEntityModelsSupplier(), this.blockRenderManager, this.itemModelManager, this.itemRenderer, this.entityRenderManager, (SpriteHolder)this.atlasManager, this.playerSkinCache);
        this.resourceManager.registerReloader((ResourceReloader)this.blockEntityRenderManager);
        this.particleSpriteManager = new ParticleSpriteManager();
        this.resourceManager.registerReloader((ResourceReloader)this.particleSpriteManager);
        this.particleManager = new ParticleManager(this.world, this.particleSpriteManager);
        this.particleSpriteManager.setOnPreparedTask(() -> ((ParticleManager)this.particleManager).clearParticles());
        this.waypointStyleAssetManager = new WaypointStyleAssetManager();
        this.resourceManager.registerReloader((ResourceReloader)this.waypointStyleAssetManager);
        this.gameRenderer = new GameRenderer(this, this.entityRenderManager.getHeldItemRenderer(), this.bufferBuilders, this.blockRenderManager);
        this.worldRenderer = new WorldRenderer(this, this.entityRenderManager, this.blockEntityRenderManager, this.bufferBuilders, this.gameRenderer.getEntityRenderStates(), this.gameRenderer.getEntityRenderDispatcher());
        this.resourceManager.registerReloader((ResourceReloader)this.worldRenderer);
        this.resourceManager.registerReloader((ResourceReloader)this.worldRenderer.getCloudRenderer());
        this.videoWarningManager = new VideoWarningManager();
        this.resourceManager.registerReloader((ResourceReloader)this.videoWarningManager);
        this.resourceManager.registerReloader((ResourceReloader)this.regionalComplianciesManager);
        this.inGameHud = new InGameHud(this);
        RealmsClient realmsClient = RealmsClient.createRealmsClient((MinecraftClient)this);
        this.realmsPeriodicCheckers = new RealmsPeriodicCheckers(realmsClient);
        RenderSystem.setErrorCallback((arg_0, arg_1) -> this.handleGlErrorByDisableVsync(arg_0, arg_1));
        if (this.framebuffer.textureWidth != this.window.getFramebufferWidth() || this.framebuffer.textureHeight != this.window.getFramebufferHeight()) {
            StringBuilder stringBuilder = new StringBuilder("Recovering from unsupported resolution (" + this.window.getFramebufferWidth() + "x" + this.window.getFramebufferHeight() + ").\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).");
            try {
                GpuDevice gpuDevice = RenderSystem.getDevice();
                List list = gpuDevice.getLastDebugMessages();
                if (!list.isEmpty()) {
                    stringBuilder.append("\n\nReported GL debug messages:\n").append(String.join((CharSequence)"\n", list));
                }
            }
            catch (Throwable gpuDevice) {
                // empty catch block
            }
            this.window.setWindowedSize(this.framebuffer.textureWidth, this.framebuffer.textureHeight);
            TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)stringBuilder.toString(), (CharSequence)"ok", (CharSequence)"error", (boolean)false);
        } else if (((Boolean)this.options.getFullscreen().getValue()).booleanValue() && !this.window.isFullscreen()) {
            if (bl) {
                this.window.toggleFullscreen();
                this.options.getFullscreen().setValue((Object)this.window.isFullscreen());
            } else {
                this.options.getFullscreen().setValue((Object)false);
            }
        }
        this.window.setVsync(((Boolean)this.options.getEnableVsync().getValue()).booleanValue());
        this.window.setRawMouseMotion(((Boolean)this.options.getRawMouseInput().getValue()).booleanValue());
        this.window.setAllowCursorChanges(((Boolean)this.options.getAllowCursorChanges().getValue()).booleanValue());
        this.window.logOnGlError();
        this.onResolutionChanged();
        this.gameRenderer.preloadPrograms(this.defaultResourcePack.getFactory());
        this.telemetryManager = new TelemetryManager(this, this.userApiService, this.session);
        this.profileKeys = this.offlineDeveloperMode ? ProfileKeys.MISSING : ProfileKeys.create((UserApiService)this.userApiService, (Session)this.session, (Path)path);
        this.narratorManager = new NarratorManager(this);
        this.narratorManager.checkNarratorLibrary(this.options.getNarrator().getValue() != NarratorMode.OFF);
        this.messageHandler = new MessageHandler(this);
        this.messageHandler.setChatDelay(((Double)this.options.getChatDelay().getValue()).doubleValue());
        this.abuseReportContext = AbuseReportContext.create((ReporterEnvironment)ReporterEnvironment.ofIntegratedServer(), (UserApiService)this.userApiService);
        TitleScreen.registerTextures((TextureManager)this.textureManager);
        SplashOverlay.init((TextureManager)this.textureManager);
        this.gameRenderer.getRotatingPanoramaRenderer().registerTextures(this.textureManager);
        this.setScreen((Screen)new MessageScreen((Text)Text.translatable((String)"gui.loadingMinecraft")));
        List list2 = this.resourcePackManager.createResourcePacks();
        this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.INITIAL, list2);
        ResourceReload resourceReload = this.resourceManager.reload(Util.getMainWorkerExecutor().named("resourceLoad"), (Executor)this, COMPLETED_UNIT_FUTURE, list2);
        GameLoadTimeEvent.INSTANCE.startTimer(TelemetryEventProperty.LOAD_TIME_LOADING_OVERLAY_MS);
        LoadingContext loadingContext = new LoadingContext(realmsClient, args.quickPlay);
        this.setOverlay((Overlay)new SplashOverlay(this, resourceReload, error -> Util.ifPresentOrElse((Optional)error, throwable -> this.handleResourceReloadException(throwable, loadingContext), () -> {
            if (SharedConstants.isDevelopment) {
                this.checkGameData();
            }
            this.resourceReloadLogger.finish();
            this.onFinishedLoading(loadingContext);
        }), false));
        this.quickPlayLogger = QuickPlayLogger.create((String)args.quickPlay.logPath());
        this.inactivityFpsLimiter = new InactivityFpsLimiter(this.options, this);
        this.tickTimeTracker = new TickTimeTracker((LongSupplier)Util.nanoTimeSupplier, () -> this.trackingTick, () -> ((InactivityFpsLimiter)this.inactivityFpsLimiter).shouldDisableProfilerTimeout());
        this.tracyFrameCapturer = TracyClient.isAvailable() && args.game.tracyEnabled ? new TracyFrameCapturer() : null;
        this.packetApplyBatcher = new PacketApplyBatcher(this.thread);
    }

    public boolean isShiftPressed() {
        Window window = this.getWindow();
        return InputUtil.isKeyPressed((Window)window, (int)340) || InputUtil.isKeyPressed((Window)window, (int)344);
    }

    public boolean isCtrlPressed() {
        Window window = this.getWindow();
        return InputUtil.isKeyPressed((Window)window, (int)341) || InputUtil.isKeyPressed((Window)window, (int)345);
    }

    public boolean isAltPressed() {
        Window window = this.getWindow();
        return InputUtil.isKeyPressed((Window)window, (int)342) || InputUtil.isKeyPressed((Window)window, (int)346);
    }

    private void onFinishedLoading(// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable MinecraftClient.LoadingContext loadingContext) {
        if (!this.finishedLoading) {
            this.finishedLoading = true;
            this.collectLoadTimes(loadingContext);
        }
    }

    private void collectLoadTimes(// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable MinecraftClient.LoadingContext loadingContext) {
        Runnable runnable = this.onInitFinished(loadingContext);
        GameLoadTimeEvent.INSTANCE.stopTimer(TelemetryEventProperty.LOAD_TIME_LOADING_OVERLAY_MS);
        GameLoadTimeEvent.INSTANCE.stopTimer(TelemetryEventProperty.LOAD_TIME_TOTAL_TIME_MS);
        GameLoadTimeEvent.INSTANCE.send(this.telemetryManager.getSender());
        runnable.run();
        this.options.startedCleanly = true;
        this.options.write();
    }

    public boolean isFinishedLoading() {
        return this.finishedLoading;
    }

    private Runnable onInitFinished(// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable MinecraftClient.LoadingContext loadingContext) {
        ArrayList list = new ArrayList();
        boolean bl = this.createInitScreens(list);
        Runnable runnable = () -> {
            if (loadingContext != null && loadingContext.quickPlayData.isEnabled()) {
                QuickPlay.startQuickPlay((MinecraftClient)this, (RunArgs.QuickPlayVariant)loadingContext.quickPlayData.variant(), (RealmsClient)loadingContext.realmsClient());
            } else {
                this.setScreen((Screen)new TitleScreen(true, new LogoDrawer(bl)));
            }
        };
        for (Function function : Lists.reverse(list)) {
            Screen screen = (Screen)function.apply(runnable);
            runnable = () -> this.setScreen(screen);
        }
        return runnable;
    }

    private boolean createInitScreens(List<Function<Runnable, Screen>> list) {
        com.mojang.authlib.yggdrasil.ProfileResult profileResult;
        BanDetails banDetails;
        boolean bl = false;
        if (this.options.onboardAccessibility || SharedConstants.FORCE_ONBOARDING_SCREEN) {
            list.add(onClose -> new AccessibilityOnboardingScreen(this.options, onClose));
            bl = true;
        }
        if ((banDetails = this.getMultiplayerBanDetails()) != null) {
            list.add(onClose -> Bans.createBanScreen(confirmed -> {
                if (confirmed) {
                    Util.getOperatingSystem().open(Urls.JAVA_MODERATION);
                }
                onClose.run();
            }, (BanDetails)banDetails));
        }
        if ((profileResult = (com.mojang.authlib.yggdrasil.ProfileResult)this.gameProfileFuture.join()) != null) {
            GameProfile gameProfile = profileResult.profile();
            Set set = profileResult.actions();
            if (set.contains(ProfileActionType.FORCED_NAME_CHANGE)) {
                list.add(onClose -> Bans.createUsernameBanScreen((String)gameProfile.name(), (Runnable)onClose));
            }
            if (set.contains(ProfileActionType.USING_BANNED_SKIN)) {
                list.add(Bans::createSkinBanScreen);
            }
        }
        return bl;
    }

    private static boolean isCountrySetTo(Object country) {
        try {
            return Locale.getDefault().getISO3Country().equals(country);
        }
        catch (MissingResourceException missingResourceException) {
            return false;
        }
    }

    public void updateWindowTitle() {
        this.window.setTitle(this.getWindowTitle());
    }

    private String getWindowTitle() {
        StringBuilder stringBuilder = new StringBuilder("Minecraft");
        if (MinecraftClient.getModStatus().isModded()) {
            stringBuilder.append("*");
        }
        stringBuilder.append(" ");
        stringBuilder.append(SharedConstants.getGameVersion().name());
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            stringBuilder.append(" - ");
            ServerInfo serverInfo = this.getCurrentServerEntry();
            if (this.server != null && !this.server.isRemote()) {
                stringBuilder.append(I18n.translate((String)"title.singleplayer", (Object[])new Object[0]));
            } else if (serverInfo != null && serverInfo.isRealm()) {
                stringBuilder.append(I18n.translate((String)"title.multiplayer.realms", (Object[])new Object[0]));
            } else if (this.server != null || serverInfo != null && serverInfo.isLocal()) {
                stringBuilder.append(I18n.translate((String)"title.multiplayer.lan", (Object[])new Object[0]));
            } else {
                stringBuilder.append(I18n.translate((String)"title.multiplayer.other", (Object[])new Object[0]));
            }
        }
        return stringBuilder.toString();
    }

    private UserApiService createUserApiService(YggdrasilAuthenticationService authService, RunArgs runArgs) {
        if (runArgs.game.offlineDeveloperMode) {
            return UserApiService.OFFLINE;
        }
        return authService.createUserApiService(runArgs.network.session.getAccessToken());
    }

    public boolean isOfflineDeveloperMode() {
        return this.offlineDeveloperMode;
    }

    public static ModStatus getModStatus() {
        return ModStatus.check((String)"vanilla", ClientBrandRetriever::getClientModName, (String)"Client", MinecraftClient.class);
    }

    private void handleResourceReloadException(Throwable throwable, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable MinecraftClient.LoadingContext loadingContext) {
        if (this.resourcePackManager.getEnabledIds().size() > 1) {
            this.onResourceReloadFailure(throwable, null, loadingContext);
        } else {
            Util.throwUnchecked((Throwable)throwable);
        }
    }

    public void onResourceReloadFailure(Throwable exception, @Nullable Text resourceName, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable MinecraftClient.LoadingContext loadingContext) {
        LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", exception);
        this.resourceReloadLogger.recover(exception);
        this.serverResourcePackLoader.onReloadFailure();
        this.resourcePackManager.setEnabledProfiles(Collections.emptyList());
        this.options.resourcePacks.clear();
        this.options.incompatibleResourcePacks.clear();
        this.options.write();
        this.reloadResources(true, loadingContext).thenRunAsync(() -> this.showResourceReloadFailureToast(resourceName), (Executor)this);
    }

    private void onForcedResourceReloadFailure() {
        this.setOverlay(null);
        if (this.world != null) {
            this.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
            this.disconnectWithProgressScreen();
        }
        this.setScreen((Screen)new TitleScreen());
        this.showResourceReloadFailureToast(null);
    }

    private void showResourceReloadFailureToast(@Nullable Text description) {
        ToastManager toastManager = this.getToastManager();
        SystemToast.show((ToastManager)toastManager, (SystemToast.Type)SystemToast.Type.PACK_LOAD_FAILURE, (Text)Text.translatable((String)"resourcePack.load_fail"), (Text)description);
    }

    public void onShaderResourceReloadFailure(Exception exception) {
        if (!this.resourcePackManager.hasOptionalProfilesEnabled()) {
            if (this.resourcePackManager.getEnabledIds().size() <= 1) {
                LOGGER.error(LogUtils.FATAL_MARKER, exception.getMessage(), (Throwable)exception);
                this.printCrashReport(new CrashReport(exception.getMessage(), (Throwable)exception));
            } else {
                this.send(() -> this.onForcedResourceReloadFailure());
            }
            return;
        }
        this.onResourceReloadFailure((Throwable)exception, (Text)Text.translatable((String)"resourcePack.runtime_failure"), null);
    }

    public void run() {
        this.thread = Thread.currentThread();
        if (Runtime.getRuntime().availableProcessors() > 4) {
            this.thread.setPriority(10);
        }
        DiscontinuousFrame discontinuousFrame = TracyClient.createDiscontinuousFrame((String)"Client Tick");
        try {
            boolean bl = false;
            while (this.running) {
                this.printCrashReport();
                try {
                    TickDurationMonitor tickDurationMonitor = TickDurationMonitor.create((String)"Renderer");
                    boolean bl2 = this.getDebugHud().shouldShowRenderingChart();
                    try (Profilers.Scoped scoped = Profilers.using((Profiler)this.startMonitor(bl2, tickDurationMonitor));){
                        this.recorder.startTick();
                        discontinuousFrame.start();
                        this.render(!bl);
                        discontinuousFrame.end();
                        this.recorder.endTick();
                    }
                    this.endMonitor(bl2, tickDurationMonitor);
                }
                catch (OutOfMemoryError outOfMemoryError) {
                    if (bl) {
                        throw outOfMemoryError;
                    }
                    this.cleanUpAfterCrash();
                    this.setScreen((Screen)new OutOfMemoryScreen());
                    System.gc();
                    LOGGER.error(LogUtils.FATAL_MARKER, "Out of memory", (Throwable)outOfMemoryError);
                    bl = true;
                }
            }
        }
        catch (CrashException crashException) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Reported exception thrown!", (Throwable)crashException);
            this.printCrashReport(crashException.getReport());
        }
        catch (Throwable throwable) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", throwable);
            this.printCrashReport(new CrashReport("Unexpected error", throwable));
        }
    }

    void onFontOptionsChanged() {
        this.fontManager.setActiveFilters(this.options);
    }

    private void handleGlErrorByDisableVsync(int error, long description) {
        this.options.getEnableVsync().setValue((Object)false);
        this.options.write();
    }

    public Framebuffer getFramebuffer() {
        return this.framebuffer;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public String getVersionType() {
        return this.versionType;
    }

    public void setCrashReportSupplierAndAddDetails(CrashReport crashReport) {
        this.crashReportSupplier = () -> this.addDetailsToCrashReport(crashReport);
    }

    public void setCrashReportSupplier(CrashReport crashReport) {
        this.crashReportSupplier = () -> crashReport;
    }

    private void printCrashReport() {
        if (this.crashReportSupplier != null) {
            MinecraftClient.printCrashReport((MinecraftClient)this, (File)this.runDirectory, (CrashReport)((CrashReport)this.crashReportSupplier.get()));
        }
    }

    public void printCrashReport(CrashReport crashReport) {
        CrashMemoryReserve.releaseMemory();
        CrashReport crashReport2 = this.addDetailsToCrashReport(crashReport);
        this.cleanUpAfterCrash();
        MinecraftClient.printCrashReport((MinecraftClient)this, (File)this.runDirectory, (CrashReport)crashReport2);
    }

    public static int saveCrashReport(File runDir, CrashReport crashReport) {
        Path path = runDir.toPath().resolve("crash-reports");
        Path path2 = path.resolve("crash-" + Util.getFormattedCurrentTime() + "-client.txt");
        Bootstrap.println((String)crashReport.asString(ReportType.MINECRAFT_CRASH_REPORT));
        if (crashReport.getFile() != null) {
            Bootstrap.println((String)("#@!@# Game crashed! Crash report saved to: #@!@# " + String.valueOf(crashReport.getFile().toAbsolutePath())));
            return -1;
        }
        if (crashReport.writeToFile(path2, ReportType.MINECRAFT_CRASH_REPORT)) {
            Bootstrap.println((String)("#@!@# Game crashed! Crash report saved to: #@!@# " + String.valueOf(path2.toAbsolutePath())));
            return -1;
        }
        Bootstrap.println((String)"#@?@# Game crashed! Crash report could not be saved. #@?@#");
        return -2;
    }

    public static void printCrashReport(@Nullable MinecraftClient client, File runDirectory, CrashReport crashReport) {
        int i = MinecraftClient.saveCrashReport((File)runDirectory, (CrashReport)crashReport);
        if (client != null) {
            client.soundManager.stopAbruptly();
        }
        System.exit(i);
    }

    public boolean forcesUnicodeFont() {
        return (Boolean)this.options.getForceUnicodeFont().getValue();
    }

    public CompletableFuture<Void> reloadResources() {
        return this.reloadResources(false, null);
    }

    private CompletableFuture<Void> reloadResources(boolean force, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable MinecraftClient.LoadingContext loadingContext) {
        if (this.resourceReloadFuture != null) {
            return this.resourceReloadFuture;
        }
        CompletableFuture<Void> completableFuture = new CompletableFuture<Void>();
        if (!force && this.overlay instanceof SplashOverlay) {
            this.resourceReloadFuture = completableFuture;
            return completableFuture;
        }
        this.resourcePackManager.scanPacks();
        List list = this.resourcePackManager.createResourcePacks();
        if (!force) {
            this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.MANUAL, list);
        }
        this.setOverlay((Overlay)new SplashOverlay(this, this.resourceManager.reload(Util.getMainWorkerExecutor().named("resourceLoad"), (Executor)this, COMPLETED_UNIT_FUTURE, list), error -> Util.ifPresentOrElse((Optional)error, throwable -> {
            if (force) {
                this.serverResourcePackLoader.onForcedReloadFailure();
                this.onForcedResourceReloadFailure();
            } else {
                this.handleResourceReloadException(throwable, loadingContext);
            }
        }, () -> {
            this.worldRenderer.reload();
            this.resourceReloadLogger.finish();
            this.serverResourcePackLoader.onReloadSuccess();
            completableFuture.complete(null);
            this.onFinishedLoading(loadingContext);
        }), !force));
        return completableFuture;
    }

    private void checkGameData() {
        boolean bl = false;
        BlockModels blockModels = this.getBlockRenderManager().getModels();
        BlockStateModel blockStateModel = blockModels.getModelManager().getMissingModel();
        for (Block block : Registries.BLOCK) {
            for (BlockState blockState : block.getStateManager().getStates()) {
                BlockStateModel blockStateModel2;
                if (blockState.getRenderType() != BlockRenderType.MODEL || (blockStateModel2 = blockModels.getModel(blockState)) != blockStateModel) continue;
                LOGGER.debug("Missing model for: {}", (Object)blockState);
                bl = true;
            }
        }
        Sprite sprite = blockStateModel.particleSprite();
        for (Block block2 : Registries.BLOCK) {
            for (BlockState blockState2 : block2.getStateManager().getStates()) {
                Sprite sprite2 = blockModels.getModelParticleSprite(blockState2);
                if (blockState2.isAir() || sprite2 != sprite) continue;
                LOGGER.debug("Missing particle icon for: {}", (Object)blockState2);
            }
        }
        Registries.ITEM.streamEntries().forEach(item -> {
            Item item2 = (Item)item.value();
            String string = item2.getTranslationKey();
            String string2 = Text.translatable((String)string).getString();
            if (string2.toLowerCase(Locale.ROOT).equals(item2.getTranslationKey())) {
                LOGGER.debug("Missing translation for: {} {} {}", new Object[]{item.registryKey().getValue(), string, item2});
            }
        });
        bl |= HandledScreens.isMissingScreens();
        if (bl |= EntityRendererFactories.isMissingRendererFactories()) {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }

    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }

    public void openChatScreen(ChatHud.ChatMethod method) {
        ChatRestriction chatRestriction = this.getChatRestriction();
        if (!chatRestriction.allowsChat(this.isInSingleplayer())) {
            if (this.inGameHud.shouldShowChatDisabledScreen()) {
                this.inGameHud.setCanShowChatDisabledScreen(false);
                this.setScreen((Screen)new ConfirmLinkScreen(confirmed -> {
                    if (confirmed) {
                        Util.getOperatingSystem().open(Urls.JAVA_ACCOUNT_SETTINGS);
                    }
                    this.setScreen(null);
                }, ChatRestriction.MORE_INFO_TEXT, Urls.JAVA_ACCOUNT_SETTINGS, true));
            } else {
                Text text = chatRestriction.getDescription();
                this.inGameHud.setOverlayMessage(text, false);
                this.narratorManager.narrateSystemImmediately(text);
                this.inGameHud.setCanShowChatDisabledScreen(chatRestriction == ChatRestriction.DISABLED_BY_PROFILE);
            }
        } else {
            this.inGameHud.getChatHud().setClientScreen(method, ChatScreen::new);
        }
    }

    public void setScreen(@Nullable Screen screen) {
        if (SharedConstants.isDevelopment && Thread.currentThread() != this.thread) {
            LOGGER.error("setScreen called from non-game thread");
        }
        if (this.currentScreen != null) {
            this.currentScreen.removed();
        } else {
            this.setNavigationType(GuiNavigationType.NONE);
        }
        if (screen == null) {
            if (this.disconnecting) {
                throw new IllegalStateException("Trying to return to in-game GUI during disconnection");
            }
            if (this.world == null) {
                screen = new TitleScreen();
            } else if (this.player.isDead()) {
                if (this.player.showsDeathScreen()) {
                    screen = new DeathScreen(null, this.world.getLevelProperties().isHardcore(), this.player);
                } else {
                    this.player.requestRespawn();
                }
            } else {
                screen = this.inGameHud.getChatHud().removeScreen();
            }
        }
        this.currentScreen = screen;
        if (this.currentScreen != null) {
            this.currentScreen.onDisplayed();
        }
        if (screen != null) {
            this.mouse.unlockCursor();
            KeyBinding.unpressAll();
            screen.init(this.window.getScaledWidth(), this.window.getScaledHeight());
            this.skipGameRender = false;
        } else {
            if (this.world != null) {
                KeyBinding.restoreToggleStates();
            }
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }
        this.updateWindowTitle();
    }

    public void setOverlay(@Nullable Overlay overlay) {
        this.overlay = overlay;
    }

    public void stop() {
        try {
            LOGGER.info("Stopping!");
            try {
                this.narratorManager.destroy();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                if (this.world != null) {
                    this.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
                }
                this.disconnectWithProgressScreen();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (this.currentScreen != null) {
                this.currentScreen.removed();
            }
            this.close();
        }
        finally {
            Util.nanoTimeSupplier = System::nanoTime;
            if (this.crashReportSupplier == null) {
                System.exit(0);
            }
        }
    }

    public void close() {
        if (this.currentGlTimerQuery != null) {
            this.currentGlTimerQuery.close();
        }
        try {
            this.telemetryManager.close();
            this.regionalComplianciesManager.close();
            this.atlasManager.close();
            this.fontManager.close();
            this.gameRenderer.close();
            this.shaderLoader.close();
            this.worldRenderer.close();
            this.soundManager.close();
            this.mapTextureManager.close();
            this.textureManager.close();
            this.resourceManager.close();
            if (this.tracyFrameCapturer != null) {
                this.tracyFrameCapturer.close();
            }
            FreeTypeUtil.release();
            Util.shutdownExecutors();
            RenderSystem.getSamplerCache().close();
            RenderSystem.getDevice().close();
        }
        catch (Throwable throwable) {
            LOGGER.error("Shutdown failure!", throwable);
            throw throwable;
        }
        finally {
            this.windowProvider.close();
            this.window.close();
        }
    }

    private void render(boolean tick) {
        boolean bl;
        long l;
        GizmoDrawing.CollectorScope collectorScope2;
        this.window.setPhase("Pre render");
        if (this.window.shouldClose()) {
            this.scheduleStop();
        }
        if (this.resourceReloadFuture != null && !(this.overlay instanceof SplashOverlay)) {
            CompletableFuture completableFuture = this.resourceReloadFuture;
            this.resourceReloadFuture = null;
            this.reloadResources().thenRun(() -> completableFuture.complete(null));
        }
        int i = this.renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs(), tick);
        Profiler profiler = Profilers.get();
        if (tick) {
            try (GizmoDrawing.CollectorScope collectorScope = this.newGizmoScope();){
                profiler.push("scheduledPacketProcessing");
                this.packetApplyBatcher.apply();
                profiler.swap("scheduledExecutables");
                this.runTasks();
                profiler.pop();
            }
            profiler.push("tick");
            if (i > 0 && this.shouldTick()) {
                profiler.push("textures");
                this.textureManager.tick();
                profiler.pop();
            }
            for (int j = 0; j < Math.min(10, i); ++j) {
                profiler.visit("clientTick");
                collectorScope2 = this.newGizmoScope();
                try {
                    this.tick();
                    continue;
                }
                finally {
                    if (collectorScope2 != null) {
                        collectorScope2.close();
                    }
                }
            }
            if (i > 0 && (this.world == null || this.world.getTickManager().shouldTick())) {
                this.gizmos = this.gizmoCollector.extractGizmos();
            }
            profiler.pop();
        }
        this.window.setPhase("Render");
        collectorScope2 = this.worldRenderer.startDrawingGizmos();
        try {
            profiler.push("gpuAsync");
            RenderSystem.executePendingTasks();
            profiler.swap("sound");
            this.soundManager.updateListenerPosition(this.gameRenderer.getCamera());
            profiler.swap("toasts");
            this.toastManager.update();
            profiler.swap("mouse");
            this.mouse.tick();
            profiler.swap("render");
            l = Util.getMeasuringTimeNano();
            if (this.debugHudEntryList.isEntryVisible(DebugHudEntries.GPU_UTILIZATION) || this.recorder.isActive()) {
                boolean bl2 = bl = (this.currentGlTimerQuery == null || this.currentGlTimerQuery.isResultAvailable()) && !GlTimer.getInstance().isRunning();
                if (bl) {
                    GlTimer.getInstance().beginProfile();
                }
            } else {
                bl = false;
                this.gpuUtilizationPercentage = 0.0;
            }
            Framebuffer framebuffer = this.getFramebuffer();
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(framebuffer.getColorAttachment(), 0, framebuffer.getDepthAttachment(), 1.0);
            profiler.push("gameRenderer");
            if (!this.skipGameRender) {
                this.gameRenderer.render((RenderTickCounter)this.renderTickCounter, tick);
            }
            profiler.swap("blit");
            if (!this.window.hasZeroWidthOrHeight()) {
                framebuffer.blitToScreen();
            }
            this.renderTime = Util.getMeasuringTimeNano() - l;
            if (bl) {
                this.currentGlTimerQuery = GlTimer.getInstance().endProfile();
            }
            profiler.swap("updateDisplay");
            if (this.tracyFrameCapturer != null) {
                this.tracyFrameCapturer.upload();
                this.tracyFrameCapturer.capture(framebuffer);
            }
            this.window.swapBuffers(this.tracyFrameCapturer);
            int k = this.inactivityFpsLimiter.update();
            if (k < 260) {
                RenderSystem.limitDisplayFPS((int)k);
            }
            profiler.pop();
            profiler.swap("yield");
            Thread.yield();
            profiler.pop();
        }
        finally {
            if (collectorScope2 != null) {
                collectorScope2.close();
            }
        }
        this.window.setPhase("Post render");
        ++this.fpsCounter;
        boolean bl2 = this.paused;
        boolean bl3 = this.paused = this.isIntegratedServerRunning() && (this.currentScreen != null && this.currentScreen.shouldPause() || this.overlay != null && this.overlay.pausesGame()) && !this.server.isRemote();
        if (!bl2 && this.paused) {
            this.soundManager.pauseAllExcept(new SoundCategory[]{SoundCategory.MUSIC, SoundCategory.UI});
        }
        this.renderTickCounter.tick(this.paused);
        this.renderTickCounter.setTickFrozen(!this.shouldTick());
        l = Util.getMeasuringTimeNano();
        long m = l - this.lastMetricsSampleTime;
        if (bl) {
            this.metricsSampleDuration = m;
        }
        this.getDebugHud().pushToFrameLog(m);
        this.lastMetricsSampleTime = l;
        profiler.push("fpsUpdate");
        if (this.currentGlTimerQuery != null && this.currentGlTimerQuery.isResultAvailable()) {
            this.gpuUtilizationPercentage = (double)this.currentGlTimerQuery.queryResult() * 100.0 / (double)this.metricsSampleDuration;
        }
        while (Util.getMeasuringTimeMs() >= this.nextDebugInfoUpdateTime + 1000L) {
            currentFps = this.fpsCounter;
            this.nextDebugInfoUpdateTime += 1000L;
            this.fpsCounter = 0;
        }
        profiler.pop();
    }

    private Profiler startMonitor(boolean active, @Nullable TickDurationMonitor monitor) {
        DummyProfiler profiler;
        if (!active) {
            this.tickTimeTracker.disable();
            if (!this.recorder.isActive() && monitor == null) {
                return DummyProfiler.INSTANCE;
            }
        }
        if (active) {
            if (!this.tickTimeTracker.isActive()) {
                this.trackingTick = 0;
                this.tickTimeTracker.enable();
            }
            ++this.trackingTick;
            profiler = this.tickTimeTracker.getProfiler();
        } else {
            profiler = DummyProfiler.INSTANCE;
        }
        if (this.recorder.isActive()) {
            profiler = Profiler.union((Profiler)profiler, (Profiler)this.recorder.getProfiler());
        }
        return TickDurationMonitor.tickProfiler((Profiler)profiler, (TickDurationMonitor)monitor);
    }

    private void endMonitor(boolean active, @Nullable TickDurationMonitor monitor) {
        if (monitor != null) {
            monitor.endTick();
        }
        PieChart pieChart = this.getDebugHud().getPieChart();
        if (active) {
            pieChart.setProfileResult(this.tickTimeTracker.getResult());
        } else {
            pieChart.setProfileResult(null);
        }
    }

    public void onResolutionChanged() {
        int i = this.window.calculateScaleFactor(((Integer)this.options.getGuiScale().getValue()).intValue(), this.forcesUnicodeFont());
        this.window.setScaleFactor(i);
        if (this.currentScreen != null) {
            this.currentScreen.resize(this.window.getScaledWidth(), this.window.getScaledHeight());
        }
        Framebuffer framebuffer = this.getFramebuffer();
        framebuffer.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.gameRenderer.onResized(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.mouse.onResolutionChanged();
    }

    public void onCursorEnterChanged() {
        this.mouse.setResolutionChanged();
    }

    public int getCurrentFps() {
        return currentFps;
    }

    public long getRenderTime() {
        return this.renderTime;
    }

    private void cleanUpAfterCrash() {
        CrashMemoryReserve.releaseMemory();
        try {
            if (this.integratedServerRunning && this.server != null) {
                this.server.stop(true);
            }
            this.disconnectWithSavingScreen();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        System.gc();
    }

    public boolean toggleDebugProfiler(Consumer<Text> chatMessageSender) {
        Consumer<Path> consumer4;
        if (this.recorder.isActive()) {
            this.stopRecorder();
            return false;
        }
        Consumer<ProfileResult> consumer = result -> {
            if (result == EmptyProfileResult.INSTANCE) {
                return;
            }
            int i = result.getTickSpan();
            double d = (double)result.getTimeSpan() / (double)TimeHelper.SECOND_IN_NANOS;
            this.execute(() -> chatMessageSender.accept((Text)Text.translatable((String)"commands.debug.stopped", (Object[])new Object[]{String.format(Locale.ROOT, "%.2f", d), i, String.format(Locale.ROOT, "%.2f", (double)i / d)})));
        };
        Consumer<Path> consumer2 = path -> {
            MutableText text = Text.literal((String)path.toString()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent((ClickEvent)new ClickEvent.OpenFile(path.getParent())));
            this.execute(() -> MinecraftClient.method_37285(chatMessageSender, (Text)text));
        };
        SystemDetails systemDetails = MinecraftClient.addSystemDetailsToCrashReport((SystemDetails)new SystemDetails(), (MinecraftClient)this, (LanguageManager)this.languageManager, (String)this.gameVersion, (GameOptions)this.options);
        Consumer<List> consumer3 = files -> {
            Path path = this.saveProfilingResult(systemDetails, files);
            consumer2.accept(path);
        };
        if (this.server == null) {
            consumer4 = path -> consumer3.accept((List)ImmutableList.of((Object)path));
        } else {
            this.server.addSystemDetails(systemDetails);
            CompletableFuture completableFuture = new CompletableFuture();
            CompletableFuture completableFuture2 = new CompletableFuture();
            CompletableFuture.allOf(completableFuture, completableFuture2).thenRunAsync(() -> consumer3.accept((List)ImmutableList.of((Object)((Path)completableFuture.join()), (Object)((Path)completableFuture2.join()))), (Executor)Util.getIoWorkerExecutor());
            this.server.setupRecorder(result -> {}, completableFuture2::complete);
            consumer4 = completableFuture::complete;
        }
        this.recorder = DebugRecorder.of((SamplerSource)new ClientSamplerSource((LongSupplier)Util.nanoTimeSupplier, this.worldRenderer), (LongSupplier)Util.nanoTimeSupplier, (Executor)Util.getIoWorkerExecutor(), (RecordDumper)new RecordDumper("client"), result -> {
            this.recorder = DummyRecorder.INSTANCE;
            consumer.accept((ProfileResult)result);
        }, consumer4);
        return true;
    }

    private void stopRecorder() {
        this.recorder.stop();
        if (this.server != null) {
            this.server.stopRecorder();
        }
    }

    private void forceStopRecorder() {
        this.recorder.forceStop();
        if (this.server != null) {
            this.server.forceStopRecorder();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Path saveProfilingResult(SystemDetails details, List<Path> files) {
        Path path;
        ServerInfo serverInfo;
        String string = this.isInSingleplayer() ? this.getServer().getSaveProperties().getLevelName() : ((serverInfo = this.getCurrentServerEntry()) != null ? serverInfo.name : "unknown");
        try {
            String string2 = String.format(Locale.ROOT, "%s-%s-%s", Util.getFormattedCurrentTime(), string, SharedConstants.getGameVersion().id());
            String string3 = PathUtil.getNextUniqueName((Path)RecordDumper.DEBUG_PROFILING_DIRECTORY, (String)string2, (String)".zip");
            path = RecordDumper.DEBUG_PROFILING_DIRECTORY.resolve(string3);
        }
        catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
        }
        try (ZipCompressor zipCompressor = new ZipCompressor(path);){
            zipCompressor.write(Paths.get("system.txt", new String[0]), details.collect());
            zipCompressor.write(Paths.get("client", new String[0]).resolve(this.options.getOptionsFile().getName()), this.options.collectProfiledOptions());
            files.forEach(arg_0 -> ((ZipCompressor)zipCompressor).copyAll(arg_0));
        }
        finally {
            for (Path path2 : files) {
                try {
                    FileUtils.forceDelete((File)path2.toFile());
                }
                catch (IOException iOException2) {
                    LOGGER.warn("Failed to delete temporary profiling result {}", (Object)path2, (Object)iOException2);
                }
            }
        }
        return path;
    }

    public void scheduleStop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void openGameMenu(boolean pauseOnly) {
        boolean bl;
        if (this.currentScreen != null) {
            return;
        }
        boolean bl2 = bl = this.isIntegratedServerRunning() && !this.server.isRemote();
        if (bl) {
            this.setScreen((Screen)new GameMenuScreen(!pauseOnly));
        } else {
            this.setScreen((Screen)new GameMenuScreen(true));
        }
    }

    private void handleBlockBreaking(boolean breaking) {
        if (!breaking) {
            this.attackCooldown = 0;
        }
        if (this.attackCooldown > 0 || this.player.isUsingItem()) {
            return;
        }
        ItemStack itemStack = this.player.getStackInHand(Hand.MAIN_HAND);
        if (itemStack.contains(DataComponentTypes.PIERCING_WEAPON)) {
            return;
        }
        if (breaking && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            Direction direction;
            BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!this.world.getBlockState(blockPos).isAir() && this.interactionManager.updateBlockBreakingProgress(blockPos, direction = blockHitResult.getSide())) {
                this.world.spawnBlockBreakingParticle(blockPos, direction);
                this.player.swingHand(Hand.MAIN_HAND);
            }
            return;
        }
        this.interactionManager.cancelBlockBreaking();
    }

    private boolean doAttack() {
        if (this.attackCooldown > 0) {
            return false;
        }
        if (this.crosshairTarget == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.interactionManager.hasLimitedAttackSpeed()) {
                this.attackCooldown = 10;
            }
            return false;
        }
        if (this.player.isRiding()) {
            return false;
        }
        ItemStack itemStack = this.player.getStackInHand(Hand.MAIN_HAND);
        if (!itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
            return false;
        }
        if (this.player.isBelowMinimumAttackCharge(itemStack, 0)) {
            return false;
        }
        boolean bl = false;
        PiercingWeaponComponent piercingWeaponComponent = (PiercingWeaponComponent)itemStack.get(DataComponentTypes.PIERCING_WEAPON);
        if (piercingWeaponComponent != null && !this.interactionManager.isFlyingLocked()) {
            this.interactionManager.attackWithPiercingWeapon(piercingWeaponComponent);
            this.player.swingHand(Hand.MAIN_HAND);
            return true;
        }
        switch (2.field_1778[this.crosshairTarget.getType().ordinal()]) {
            case 1: {
                AttackRangeComponent attackRangeComponent = (AttackRangeComponent)itemStack.get(DataComponentTypes.ATTACK_RANGE);
                if (attackRangeComponent != null && !attackRangeComponent.isWithinRange((LivingEntity)this.player, this.crosshairTarget.getPos())) break;
                this.interactionManager.attackEntity((PlayerEntity)this.player, ((EntityHitResult)this.crosshairTarget).getEntity());
                break;
            }
            case 2: {
                BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!this.world.getBlockState(blockPos).isAir()) {
                    this.interactionManager.attackBlock(blockPos, blockHitResult.getSide());
                    if (!this.world.getBlockState(blockPos).isAir()) break;
                    bl = true;
                    break;
                }
            }
            case 3: {
                if (this.interactionManager.hasLimitedAttackSpeed()) {
                    this.attackCooldown = 10;
                }
                this.player.resetTicksSince();
            }
        }
        if (!this.player.isSpectator()) {
            this.player.swingHand(Hand.MAIN_HAND);
        }
        return bl;
    }

    private void doItemUse() {
        if (this.interactionManager.isBreakingBlock()) {
            return;
        }
        this.itemUseCooldown = 4;
        if (this.player.isRiding()) {
            return;
        }
        if (this.crosshairTarget == null) {
            LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }
        for (Hand hand : Hand.values()) {
            ActionResult actionResult3;
            ItemStack itemStack = this.player.getStackInHand(hand);
            if (!itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
                return;
            }
            if (this.crosshairTarget != null) {
                switch (2.field_1778[this.crosshairTarget.getType().ordinal()]) {
                    case 1: {
                        EntityHitResult entityHitResult = (EntityHitResult)this.crosshairTarget;
                        Entity entity = entityHitResult.getEntity();
                        if (!this.world.getWorldBorder().contains(entity.getBlockPos())) {
                            return;
                        }
                        if (!this.player.canInteractWithEntity(entity, 0.0)) break;
                        ActionResult actionResult = this.interactionManager.interactEntityAtLocation((PlayerEntity)this.player, entity, entityHitResult, hand);
                        if (!actionResult.isAccepted()) {
                            actionResult = this.interactionManager.interactEntity((PlayerEntity)this.player, entity, hand);
                        }
                        if (!(actionResult instanceof ActionResult.Success)) break;
                        ActionResult.Success success = (ActionResult.Success)actionResult;
                        if (success.swingSource() == ActionResult.SwingSource.CLIENT) {
                            this.player.swingHand(hand);
                        }
                        return;
                    }
                    case 2: {
                        BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                        int i = itemStack.getCount();
                        ActionResult actionResult2 = this.interactionManager.interactBlock(this.player, hand, blockHitResult);
                        if (actionResult2 instanceof ActionResult.Success) {
                            ActionResult.Success success2 = (ActionResult.Success)actionResult2;
                            if (success2.swingSource() == ActionResult.SwingSource.CLIENT) {
                                this.player.swingHand(hand);
                                if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.player.isInCreativeMode())) {
                                    this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                                }
                            }
                            return;
                        }
                        if (!(actionResult2 instanceof ActionResult.Fail)) break;
                        return;
                    }
                }
            }
            if (itemStack.isEmpty() || !((actionResult3 = this.interactionManager.interactItem((PlayerEntity)this.player, hand)) instanceof ActionResult.Success)) continue;
            ActionResult.Success success3 = (ActionResult.Success)actionResult3;
            if (success3.swingSource() == ActionResult.SwingSource.CLIENT) {
                this.player.swingHand(hand);
            }
            this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
            return;
        }
    }

    public MusicTracker getMusicTracker() {
        return this.musicTracker;
    }

    public void tick() {
        CrashReport crashReport;
        ++this.uptimeInTicks;
        if (this.world != null && !this.paused) {
            this.world.getTickManager().step();
        }
        if (this.itemUseCooldown > 0) {
            --this.itemUseCooldown;
        }
        Profiler profiler = Profilers.get();
        profiler.push("gui");
        this.messageHandler.processDelayedMessages();
        this.inGameHud.tick(this.paused);
        profiler.pop();
        this.gameRenderer.updateCrosshairTarget(1.0f);
        this.tutorialManager.tick(this.world, this.crosshairTarget);
        profiler.push("gameMode");
        if (!this.paused && this.world != null) {
            this.interactionManager.tick();
        }
        profiler.swap("screen");
        if (this.currentScreen == null && this.player != null) {
            if (this.player.isDead() && !(this.currentScreen instanceof DeathScreen)) {
                this.setScreen(null);
            } else if (this.player.isSleeping() && this.world != null) {
                this.inGameHud.getChatHud().setClientScreen(ChatHud.ChatMethod.MESSAGE, SleepingChatScreen::new);
            }
        } else {
            Screen screen = this.currentScreen;
            if (screen instanceof SleepingChatScreen) {
                SleepingChatScreen sleepingChatScreen = (SleepingChatScreen)screen;
                if (!this.player.isSleeping()) {
                    sleepingChatScreen.closeChatIfEmpty();
                }
            }
        }
        if (this.currentScreen != null) {
            this.attackCooldown = 10000;
        }
        if (this.currentScreen != null) {
            try {
                this.currentScreen.tick();
            }
            catch (Throwable throwable) {
                crashReport = CrashReport.create((Throwable)throwable, (String)"Ticking screen");
                this.currentScreen.addCrashReportSection(crashReport);
                throw new CrashException(crashReport);
            }
        }
        if (this.overlay != null) {
            this.overlay.tick();
        }
        if (!this.getDebugHud().shouldShowDebugHud()) {
            this.inGameHud.resetDebugHudChunk();
        }
        if (this.overlay == null && this.currentScreen == null) {
            profiler.swap("Keybindings");
            this.handleInputEvents();
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
        }
        if (this.world != null) {
            if (!this.paused) {
                profiler.swap("gameRenderer");
                this.gameRenderer.tick();
                profiler.swap("entities");
                this.world.tickEntities();
                profiler.swap("blockEntities");
                this.world.tickBlockEntities();
            }
        } else if (this.gameRenderer.getPostProcessorId() != null) {
            this.gameRenderer.clearPostProcessor();
        }
        this.musicTracker.tick();
        this.soundManager.tick(this.paused);
        if (this.world != null) {
            ClientPlayNetworkHandler clientPlayNetworkHandler;
            if (!this.paused) {
                profiler.swap("level");
                if (!this.options.joinedFirstServer && this.isConnectedToServer()) {
                    MutableText text = Text.translatable((String)"tutorial.socialInteractions.title");
                    MutableText text2 = Text.translatable((String)"tutorial.socialInteractions.description", (Object[])new Object[]{TutorialManager.keyToText((String)"socialInteractions")});
                    this.socialInteractionsToast = new TutorialToast(this.textRenderer, TutorialToast.Type.SOCIAL_INTERACTIONS, (Text)text, (Text)text2, true, 8000);
                    this.toastManager.add((Toast)this.socialInteractionsToast);
                    this.options.joinedFirstServer = true;
                    this.options.write();
                }
                this.tutorialManager.tick();
                try {
                    this.world.tick(() -> true);
                }
                catch (Throwable throwable) {
                    crashReport = CrashReport.create((Throwable)throwable, (String)"Exception in world tick");
                    if (this.world == null) {
                        CrashReportSection crashReportSection = crashReport.addElement("Affected level");
                        crashReportSection.add("Problem", (Object)"Level is null!");
                    } else {
                        this.world.addDetailsToCrashReport(crashReport);
                    }
                    throw new CrashException(crashReport);
                }
            }
            profiler.swap("animateTick");
            if (!this.paused && this.shouldTick()) {
                this.world.doRandomBlockDisplayTicks(this.player.getBlockX(), this.player.getBlockY(), this.player.getBlockZ());
            }
            profiler.swap("particles");
            if (!this.paused && this.shouldTick()) {
                this.particleManager.tick();
            }
            if ((clientPlayNetworkHandler = this.getNetworkHandler()) != null && !this.paused) {
                clientPlayNetworkHandler.sendPacket((Packet)ClientTickEndC2SPacket.INSTANCE);
            }
        } else if (this.integratedServerConnection != null) {
            profiler.swap("pendingConnection");
            this.integratedServerConnection.tick();
        }
        profiler.swap("keyboard");
        this.keyboard.pollDebugCrash();
        profiler.pop();
    }

    private boolean shouldTick() {
        return this.world == null || this.world.getTickManager().shouldTick();
    }

    private boolean isConnectedToServer() {
        return !this.integratedServerRunning || this.server != null && this.server.isRemote();
    }

    private void handleInputEvents() {
        while (this.options.togglePerspectiveKey.wasPressed()) {
            Perspective perspective = this.options.getPerspective();
            this.options.setPerspective(this.options.getPerspective().next());
            if (perspective.isFirstPerson() != this.options.getPerspective().isFirstPerson()) {
                this.gameRenderer.onCameraEntitySet(this.options.getPerspective().isFirstPerson() ? this.getCameraEntity() : null);
            }
            this.worldRenderer.scheduleTerrainUpdate();
        }
        while (this.options.smoothCameraKey.wasPressed()) {
            this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
        }
        for (int i = 0; i < 9; ++i) {
            boolean bl = this.options.saveToolbarActivatorKey.isPressed();
            boolean bl2 = this.options.loadToolbarActivatorKey.isPressed();
            if (!this.options.hotbarKeys[i].wasPressed()) continue;
            if (this.player.isSpectator()) {
                this.inGameHud.getSpectatorHud().selectSlot(i);
                continue;
            }
            if (this.player.isInCreativeMode() && this.currentScreen == null && (bl2 || bl)) {
                CreativeInventoryScreen.onHotbarKeyPress((MinecraftClient)this, (int)i, (boolean)bl2, (boolean)bl);
                continue;
            }
            this.player.getInventory().setSelectedSlot(i);
        }
        while (this.options.socialInteractionsKey.wasPressed()) {
            if (!this.isConnectedToServer() && !SharedConstants.SOCIAL_INTERACTIONS) {
                this.player.sendMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
                this.narratorManager.narrateSystemImmediately(SOCIAL_INTERACTIONS_NOT_AVAILABLE);
                continue;
            }
            if (this.socialInteractionsToast != null) {
                this.socialInteractionsToast.hide();
                this.socialInteractionsToast = null;
            }
            this.setScreen((Screen)new SocialInteractionsScreen());
        }
        while (this.options.inventoryKey.wasPressed()) {
            if (this.interactionManager.hasRidingInventory()) {
                this.player.openRidingInventory();
                continue;
            }
            this.tutorialManager.onInventoryOpened();
            this.setScreen((Screen)new InventoryScreen((PlayerEntity)this.player));
        }
        while (this.options.advancementsKey.wasPressed()) {
            this.setScreen((Screen)new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
        }
        while (this.options.quickActionsKey.wasPressed()) {
            this.getQuickActionsDialog().ifPresent(dialog -> this.player.networkHandler.showDialog(dialog, this.currentScreen));
        }
        while (this.options.swapHandsKey.wasPressed()) {
            if (this.player.isSpectator()) continue;
            this.getNetworkHandler().sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
        }
        while (this.options.dropKey.wasPressed()) {
            if (this.player.isSpectator() || !this.player.dropSelectedItem(this.isCtrlPressed())) continue;
            this.player.swingHand(Hand.MAIN_HAND);
        }
        while (this.options.chatKey.wasPressed()) {
            this.openChatScreen(ChatHud.ChatMethod.MESSAGE);
        }
        if (this.currentScreen == null && this.overlay == null && this.options.commandKey.wasPressed()) {
            this.openChatScreen(ChatHud.ChatMethod.COMMAND);
        }
        boolean bl3 = false;
        if (this.player.isUsingItem()) {
            if (!this.options.useKey.isPressed()) {
                this.interactionManager.stopUsingItem((PlayerEntity)this.player);
            }
            while (this.options.attackKey.wasPressed()) {
            }
            while (this.options.useKey.wasPressed()) {
            }
            while (this.options.pickItemKey.wasPressed()) {
            }
        } else {
            while (this.options.attackKey.wasPressed()) {
                bl3 |= this.doAttack();
            }
            while (this.options.useKey.wasPressed()) {
                this.doItemUse();
            }
            while (this.options.pickItemKey.wasPressed()) {
                this.doItemPick();
            }
            if (this.player.isSpectator()) {
                while (this.options.spectatorHotbarKey.wasPressed()) {
                    this.inGameHud.getSpectatorHud().useSelectedCommand();
                }
            }
        }
        if (this.options.useKey.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
            this.doItemUse();
        }
        this.handleBlockBreaking(this.currentScreen == null && !bl3 && this.options.attackKey.isPressed() && this.mouse.isCursorLocked());
    }

    private Optional<RegistryEntry<Dialog>> getQuickActionsDialog() {
        Registry registry = this.player.networkHandler.getRegistryManager().getOrThrow(RegistryKeys.DIALOG);
        return registry.getOptional(DialogTags.QUICK_ACTIONS).flatMap(quickActionsDialogs -> {
            if (quickActionsDialogs.size() == 0) {
                return Optional.empty();
            }
            if (quickActionsDialogs.size() == 1) {
                return Optional.of(quickActionsDialogs.get(0));
            }
            return registry.getOptional(Dialogs.QUICK_ACTIONS);
        });
    }

    public TelemetryManager getTelemetryManager() {
        return this.telemetryManager;
    }

    public double getGpuUtilizationPercentage() {
        return this.gpuUtilizationPercentage;
    }

    public ProfileKeys getProfileKeys() {
        return this.profileKeys;
    }

    public IntegratedServerLoader createIntegratedServerLoader() {
        return new IntegratedServerLoader(this, this.levelStorage);
    }

    public void startIntegratedServer(LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, boolean newWorld) {
        this.disconnectWithProgressScreen();
        Instant instant = Instant.now();
        ClientChunkLoadProgress clientChunkLoadProgress = new ClientChunkLoadProgress(newWorld ? 500L : 0L);
        LevelLoadingScreen levelLoadingScreen = new LevelLoadingScreen(clientChunkLoadProgress, LevelLoadingScreen.WorldEntryReason.OTHER);
        this.setScreen((Screen)levelLoadingScreen);
        int i = Math.max(5, 3) + ChunkLevels.FULL_GENERATION_REQUIRED_LEVEL + 1;
        try {
            session.backupLevelDataFile((DynamicRegistryManager)saveLoader.combinedDynamicRegistries().getCombinedRegistryManager(), saveLoader.saveProperties());
            ChunkLoadProgress chunkLoadProgress = ChunkLoadProgress.compose((ChunkLoadProgress)clientChunkLoadProgress, (ChunkLoadProgress)LoggingChunkLoadProgress.withPlayer());
            this.server = (IntegratedServer)MinecraftServer.startServer(thread -> new IntegratedServer(thread, this, session, dataPackManager, saveLoader, this.apiServices, chunkLoadProgress));
            clientChunkLoadProgress.setChunkLoadMap(this.server.createChunkLoadMap(i));
            this.integratedServerRunning = true;
            this.ensureAbuseReportContext(ReporterEnvironment.ofIntegratedServer());
            this.quickPlayLogger.setWorld(QuickPlayLogger.WorldType.SINGLEPLAYER, session.getDirectoryName(), saveLoader.saveProperties().getLevelName());
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create((Throwable)throwable, (String)"Starting integrated server");
            CrashReportSection crashReportSection = crashReport.addElement("Starting integrated server");
            crashReportSection.add("Level ID", (Object)session.getDirectoryName());
            crashReportSection.add("Level Name", () -> saveLoader.saveProperties().getLevelName());
            throw new CrashException(crashReport);
        }
        Profiler profiler = Profilers.get();
        profiler.push("waitForServer");
        long l = TimeUnit.SECONDS.toNanos(1L) / 60L;
        while (!this.server.isLoading() || this.overlay != null) {
            long m = Util.getMeasuringTimeNano() + l;
            levelLoadingScreen.tick();
            if (this.overlay != null) {
                this.overlay.tick();
            }
            this.render(false);
            this.runTasks();
            this.runTasks(() -> Util.getMeasuringTimeNano() > m);
            this.printCrashReport();
        }
        profiler.pop();
        Duration duration = Duration.between(instant, Instant.now());
        SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
        ClientConnection clientConnection = ClientConnection.connectLocal((SocketAddress)socketAddress);
        clientConnection.connect(socketAddress.toString(), 0, (ClientLoginPacketListener)new ClientLoginNetworkHandler(clientConnection, this, null, null, newWorld, duration, status -> {}, clientChunkLoadProgress, null));
        clientConnection.send((Packet)new LoginHelloC2SPacket(this.getSession().getUsername(), this.getSession().getUuidOrNull()));
        this.integratedServerConnection = clientConnection;
    }

    public void joinWorld(ClientWorld world) {
        this.world = world;
        this.setWorld(world);
    }

    public void disconnect(Text reasonText) {
        boolean bl = this.isInSingleplayer();
        ServerInfo serverInfo = this.getCurrentServerEntry();
        if (this.world != null) {
            this.world.disconnect(reasonText);
        }
        if (bl) {
            this.disconnectWithSavingScreen();
        } else {
            this.disconnectWithProgressScreen();
        }
        TitleScreen titleScreen = new TitleScreen();
        if (bl) {
            this.setScreen((Screen)titleScreen);
        } else if (serverInfo != null && serverInfo.isRealm()) {
            this.setScreen((Screen)new RealmsMainScreen((Screen)titleScreen));
        } else {
            this.setScreen((Screen)new MultiplayerScreen((Screen)titleScreen));
        }
    }

    public void disconnectWithSavingScreen() {
        this.disconnect((Screen)new MessageScreen(SAVING_LEVEL_TEXT), false);
    }

    public void disconnectWithProgressScreen() {
        this.disconnectWithProgressScreen(true);
    }

    public void disconnectWithProgressScreen(boolean stopSounds) {
        this.disconnect((Screen)new ProgressScreen(true), false, stopSounds);
    }

    public void disconnect(Screen disconnectionScreen, boolean transferring) {
        this.disconnect(disconnectionScreen, transferring, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disconnect(Screen disconnectionScreen, boolean transferring, boolean stopSounds) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            this.cancelTasks();
            clientPlayNetworkHandler.unloadWorld();
            if (!transferring) {
                this.onDisconnected();
            }
        }
        this.socialInteractionsManager.unloadBlockList();
        if (this.recorder.isActive()) {
            this.forceStopRecorder();
        }
        IntegratedServer integratedServer = this.server;
        this.server = null;
        this.gameRenderer.reset();
        this.interactionManager = null;
        this.narratorManager.clear();
        this.disconnecting = true;
        try {
            if (this.world != null) {
                this.inGameHud.clear();
            }
            if (integratedServer != null) {
                this.setScreen((Screen)new MessageScreen(SAVING_LEVEL_TEXT));
                Profiler profiler = Profilers.get();
                profiler.push("waitForServer");
                while (!integratedServer.isStopping()) {
                    this.render(false);
                }
                profiler.pop();
            }
            this.setScreenAndRender(disconnectionScreen);
            this.integratedServerRunning = false;
            this.world = null;
            this.setWorld(null, stopSounds);
            this.player = null;
        }
        finally {
            this.disconnecting = false;
        }
    }

    public void onDisconnected() {
        this.serverResourcePackLoader.clear();
        this.runTasks();
    }

    public void enterReconfiguration(Screen reconfigurationScreen) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.clearWorld();
        }
        if (this.recorder.isActive()) {
            this.forceStopRecorder();
        }
        this.gameRenderer.reset();
        this.interactionManager = null;
        this.narratorManager.clear();
        this.disconnecting = true;
        try {
            this.setScreenAndRender(reconfigurationScreen);
            this.inGameHud.clear();
            this.world = null;
            this.setWorld(null);
            this.player = null;
        }
        finally {
            this.disconnecting = false;
        }
    }

    public void setScreenAndRender(Screen screen) {
        try (ScopedProfiler scopedProfiler = Profilers.get().scoped("forcedTick");){
            this.setScreen(screen);
            this.render(false);
        }
    }

    private void setWorld(@Nullable ClientWorld world) {
        this.setWorld(world, true);
    }

    private void setWorld(@Nullable ClientWorld world, boolean stopSounds) {
        if (stopSounds) {
            this.soundManager.stopAll();
        }
        this.setCameraEntity(null);
        this.integratedServerConnection = null;
        this.worldRenderer.setWorld(world);
        this.particleManager.setWorld(world);
        this.gameRenderer.setWorld(world);
        this.updateWindowTitle();
    }

    private UserApiService.UserProperties getUserProperties() {
        return (UserApiService.UserProperties)this.userPropertiesFuture.join();
    }

    public boolean isOptionalTelemetryEnabled() {
        return this.isOptionalTelemetryEnabledByApi() && (Boolean)this.options.getTelemetryOptInExtra().getValue() != false;
    }

    public boolean isOptionalTelemetryEnabledByApi() {
        return this.isTelemetryEnabledByApi() && this.getUserProperties().flag(UserApiService.UserFlag.OPTIONAL_TELEMETRY_AVAILABLE);
    }

    public boolean isTelemetryEnabledByApi() {
        if (SharedConstants.isDevelopment && !SharedConstants.FORCE_TELEMETRY) {
            return false;
        }
        return this.getUserProperties().flag(UserApiService.UserFlag.TELEMETRY_ENABLED);
    }

    public boolean isMultiplayerEnabled() {
        return this.multiplayerEnabled && this.getUserProperties().flag(UserApiService.UserFlag.SERVERS_ALLOWED) && this.getMultiplayerBanDetails() == null && !this.isUsernameBanned();
    }

    public boolean isRealmsEnabled() {
        return this.getUserProperties().flag(UserApiService.UserFlag.REALMS_ALLOWED) && this.getMultiplayerBanDetails() == null;
    }

    public @Nullable BanDetails getMultiplayerBanDetails() {
        return (BanDetails)this.getUserProperties().bannedScopes().get("MULTIPLAYER");
    }

    public boolean isUsernameBanned() {
        com.mojang.authlib.yggdrasil.ProfileResult profileResult = this.gameProfileFuture.getNow(null);
        return profileResult != null && profileResult.actions().contains(ProfileActionType.FORCED_NAME_CHANGE);
    }

    public boolean shouldBlockMessages(UUID sender) {
        if (!this.getChatRestriction().allowsChat(false)) {
            return (this.player == null || !sender.equals(this.player.getUuid())) && !sender.equals(Util.NIL_UUID);
        }
        return this.socialInteractionsManager.isPlayerMuted(sender);
    }

    public ChatRestriction getChatRestriction() {
        if (this.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN) {
            return ChatRestriction.DISABLED_BY_OPTIONS;
        }
        if (!this.onlineChatEnabled) {
            return ChatRestriction.DISABLED_BY_LAUNCHER;
        }
        if (!this.getUserProperties().flag(UserApiService.UserFlag.CHAT_ALLOWED)) {
            return ChatRestriction.DISABLED_BY_PROFILE;
        }
        return ChatRestriction.ENABLED;
    }

    public final boolean isDemo() {
        return this.isDemo;
    }

    public final boolean canSwitchGameMode() {
        return this.player != null && this.interactionManager != null;
    }

    public @Nullable ClientPlayNetworkHandler getNetworkHandler() {
        return this.player == null ? null : this.player.networkHandler;
    }

    public static boolean isHudEnabled() {
        return !MinecraftClient.instance.options.hudHidden;
    }

    public static boolean usesImprovedTransparency() {
        return !MinecraftClient.instance.gameRenderer.isRenderingPanorama() && (Boolean)MinecraftClient.instance.options.getImprovedTransparency().getValue() != false;
    }

    public static boolean isAmbientOcclusionEnabled() {
        return (Boolean)MinecraftClient.instance.options.getAo().getValue();
    }

    private void doItemPick() {
        if (this.crosshairTarget == null || this.crosshairTarget.getType() == HitResult.Type.MISS) {
            return;
        }
        boolean bl = this.isCtrlPressed();
        HitResult hitResult = this.crosshairTarget;
        Objects.requireNonNull(hitResult);
        HitResult hitResult2 = hitResult;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{BlockHitResult.class, EntityHitResult.class}, (Object)hitResult2, n)) {
            case 0: {
                BlockHitResult blockHitResult = (BlockHitResult)hitResult2;
                this.interactionManager.pickItemFromBlock(blockHitResult.getBlockPos(), bl);
                break;
            }
            case 1: {
                EntityHitResult entityHitResult = (EntityHitResult)hitResult2;
                this.interactionManager.pickItemFromEntity(entityHitResult.getEntity(), bl);
                break;
            }
        }
    }

    public CrashReport addDetailsToCrashReport(CrashReport report) {
        SystemDetails systemDetails = report.getSystemDetailsSection();
        try {
            MinecraftClient.addSystemDetailsToCrashReport((SystemDetails)systemDetails, (MinecraftClient)this, (LanguageManager)this.languageManager, (String)this.gameVersion, (GameOptions)this.options);
            this.addUptimesToCrashReport(report.addElement("Uptime"));
            if (this.world != null) {
                this.world.addDetailsToCrashReport(report);
            }
            if (this.server != null) {
                this.server.addSystemDetails(systemDetails);
            }
            this.resourceReloadLogger.addReloadSection(report);
        }
        catch (Throwable throwable) {
            LOGGER.error("Failed to collect details", throwable);
        }
        return report;
    }

    public static void addSystemDetailsToCrashReport(@Nullable MinecraftClient client, @Nullable LanguageManager languageManager, String version, @Nullable GameOptions options, CrashReport report) {
        SystemDetails systemDetails = report.getSystemDetailsSection();
        MinecraftClient.addSystemDetailsToCrashReport((SystemDetails)systemDetails, (MinecraftClient)client, (LanguageManager)languageManager, (String)version, (GameOptions)options);
    }

    private static String formatSeconds(double seconds) {
        return String.format(Locale.ROOT, "%.3fs", seconds);
    }

    private void addUptimesToCrashReport(CrashReportSection section) {
        section.add("JVM uptime", () -> MinecraftClient.formatSeconds((double)((double)ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0)));
        section.add("Wall uptime", () -> MinecraftClient.formatSeconds((double)((double)(System.currentTimeMillis() - this.startTime) / 1000.0)));
        section.add("High-res time", () -> MinecraftClient.formatSeconds((double)((double)Util.getMeasuringTimeMs() / 1000.0)));
        section.add("Client ticks", () -> String.format(Locale.ROOT, "%d ticks / %.3fs", this.uptimeInTicks, (double)this.uptimeInTicks / 20.0));
    }

    private static SystemDetails addSystemDetailsToCrashReport(SystemDetails systemDetails, @Nullable MinecraftClient client, @Nullable LanguageManager languageManager, String version, @Nullable GameOptions options) {
        systemDetails.addSection("Launched Version", () -> version);
        String string = MinecraftClient.getLauncherBrand();
        if (string != null) {
            systemDetails.addSection("Launcher name", string);
        }
        systemDetails.addSection("Backend library", RenderSystem::getBackendDescription);
        systemDetails.addSection("Backend API", RenderSystem::getApiDescription);
        systemDetails.addSection("Window size", () -> client != null ? minecraftClient.window.getFramebufferWidth() + "x" + minecraftClient.window.getFramebufferHeight() : "<not initialized>");
        systemDetails.addSection("GFLW Platform", Window::getGlfwPlatform);
        systemDetails.addSection("Render Extensions", () -> String.join((CharSequence)", ", RenderSystem.getDevice().getEnabledExtensions()));
        systemDetails.addSection("GL debug messages", () -> {
            GpuDevice gpuDevice = RenderSystem.tryGetDevice();
            if (gpuDevice == null) {
                return "<no renderer available>";
            }
            if (gpuDevice.isDebuggingEnabled()) {
                return String.join((CharSequence)"\n", gpuDevice.getLastDebugMessages());
            }
            return "<debugging unavailable>";
        });
        systemDetails.addSection("Is Modded", () -> MinecraftClient.getModStatus().getMessage());
        systemDetails.addSection("Universe", () -> client != null ? Long.toHexString(minecraftClient.UNIVERSE) : "404");
        systemDetails.addSection("Type", "Client (map_client.txt)");
        if (options != null) {
            String string2;
            if (client != null && (string2 = client.getVideoWarningManager().getWarningsAsString()) != null) {
                systemDetails.addSection("GPU Warnings", string2);
            }
            systemDetails.addSection("Transparency", (Boolean)options.getImprovedTransparency().getValue() != false ? "shader" : "regular");
            systemDetails.addSection("Render Distance", options.getClampedViewDistance() + "/" + String.valueOf(options.getViewDistance().getValue()) + " chunks");
        }
        if (client != null) {
            systemDetails.addSection("Resource Packs", () -> ResourcePackManager.listPacks((Collection)client.getResourcePackManager().getEnabledProfiles()));
        }
        if (languageManager != null) {
            systemDetails.addSection("Current Language", () -> languageManager.getLanguage());
        }
        systemDetails.addSection("Locale", String.valueOf(Locale.getDefault()));
        systemDetails.addSection("System encoding", () -> System.getProperty("sun.jnu.encoding", "<not set>"));
        systemDetails.addSection("File encoding", () -> System.getProperty("file.encoding", "<not set>"));
        systemDetails.addSection("CPU", GLX::_getCpuInfo);
        return systemDetails;
    }

    public static MinecraftClient getInstance() {
        return instance;
    }

    public CompletableFuture<Void> reloadResourcesConcurrently() {
        return this.submit(() -> this.reloadResources()).thenCompose(future -> future);
    }

    public void ensureAbuseReportContext(ReporterEnvironment environment) {
        if (!this.abuseReportContext.environmentEquals(environment)) {
            this.abuseReportContext = AbuseReportContext.create((ReporterEnvironment)environment, (UserApiService)this.userApiService);
        }
    }

    public @Nullable ServerInfo getCurrentServerEntry() {
        return (ServerInfo)Nullables.map((Object)this.getNetworkHandler(), ClientPlayNetworkHandler::getServerInfo);
    }

    public boolean isInSingleplayer() {
        return this.integratedServerRunning;
    }

    public boolean isIntegratedServerRunning() {
        return this.integratedServerRunning && this.server != null;
    }

    public @Nullable IntegratedServer getServer() {
        return this.server;
    }

    public boolean isConnectedToLocalServer() {
        IntegratedServer integratedServer = this.getServer();
        return integratedServer != null && !integratedServer.isRemote();
    }

    public boolean uuidEquals(UUID uuid) {
        return uuid.equals(this.getSession().getUuidOrNull());
    }

    public Session getSession() {
        return this.session;
    }

    public GameProfile getGameProfile() {
        com.mojang.authlib.yggdrasil.ProfileResult profileResult = (com.mojang.authlib.yggdrasil.ProfileResult)this.gameProfileFuture.join();
        if (profileResult != null) {
            return profileResult.profile();
        }
        return new GameProfile(this.session.getUuidOrNull(), this.session.getUsername());
    }

    public Proxy getNetworkProxy() {
        return this.networkProxy;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    public ShaderLoader getShaderLoader() {
        return this.shaderLoader;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public ResourcePackManager getResourcePackManager() {
        return this.resourcePackManager;
    }

    public DefaultResourcePack getDefaultResourcePack() {
        return this.defaultResourcePack;
    }

    public ServerResourcePackLoader getServerResourcePackProvider() {
        return this.serverResourcePackLoader;
    }

    public Path getResourcePackDir() {
        return this.resourcePackDir;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public VideoWarningManager getVideoWarningManager() {
        return this.videoWarningManager;
    }

    public SoundManager getSoundManager() {
        return this.soundManager;
    }

    public @Nullable MusicSound getMusicInstance() {
        MusicSound musicSound = (MusicSound)Nullables.map((Object)this.currentScreen, Screen::getMusic);
        if (musicSound != null) {
            return musicSound;
        }
        Camera camera = this.gameRenderer.getCamera();
        if (this.player != null && camera != null) {
            World world = this.player.getEntityWorld();
            if (world.getRegistryKey() == World.END && this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                return MusicType.DRAGON;
            }
            BackgroundMusic backgroundMusic = (BackgroundMusic)camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.BACKGROUND_MUSIC_AUDIO, 1.0f);
            boolean bl = this.player.getAbilities().creativeMode && this.player.getAbilities().allowFlying;
            boolean bl2 = this.player.isSubmergedInWater();
            return backgroundMusic.getCurrent(bl, bl2).orElse(null);
        }
        return MusicType.MENU;
    }

    public float getMusicVolume() {
        if (this.currentScreen != null && this.currentScreen.getMusic() != null) {
            return 1.0f;
        }
        Camera camera = this.gameRenderer.getCamera();
        if (camera != null) {
            return ((Float)camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.MUSIC_VOLUME_AUDIO, 1.0f)).floatValue();
        }
        return 1.0f;
    }

    public ApiServices getApiServices() {
        return this.apiServices;
    }

    public PlayerSkinProvider getSkinProvider() {
        return this.skinProvider;
    }

    public @Nullable Entity getCameraEntity() {
        return this.cameraEntity;
    }

    public void setCameraEntity(@Nullable Entity entity) {
        this.cameraEntity = entity;
        this.gameRenderer.onCameraEntitySet(entity);
    }

    public boolean hasOutline(Entity entity) {
        return entity.isGlowing() || this.player != null && this.player.isSpectator() && this.options.spectatorOutlinesKey.isPressed() && entity.getType() == EntityType.PLAYER;
    }

    protected Thread getThread() {
        return this.thread;
    }

    public Runnable createTask(Runnable runnable) {
        return runnable;
    }

    protected boolean canExecute(Runnable task) {
        return true;
    }

    public BlockRenderManager getBlockRenderManager() {
        return this.blockRenderManager;
    }

    public EntityRenderManager getEntityRenderDispatcher() {
        return this.entityRenderManager;
    }

    public BlockEntityRenderManager getBlockEntityRenderDispatcher() {
        return this.blockEntityRenderManager;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    public MapRenderer getMapRenderer() {
        return this.mapRenderer;
    }

    public DataFixer getDataFixer() {
        return this.dataFixer;
    }

    public RenderTickCounter getRenderTickCounter() {
        return this.renderTickCounter;
    }

    public BlockColors getBlockColors() {
        return this.blockColors;
    }

    public boolean hasReducedDebugInfo() {
        return this.player != null && this.player.hasReducedDebugInfo() || (Boolean)this.options.getReducedDebugInfo().getValue() != false;
    }

    public ToastManager getToastManager() {
        return this.toastManager;
    }

    public TutorialManager getTutorialManager() {
        return this.tutorialManager;
    }

    public boolean isWindowFocused() {
        return this.windowFocused;
    }

    public HotbarStorage getCreativeHotbarStorage() {
        return this.creativeHotbarStorage;
    }

    public BakedModelManager getBakedModelManager() {
        return this.bakedModelManager;
    }

    public AtlasManager getAtlasManager() {
        return this.atlasManager;
    }

    public MapTextureManager getMapTextureManager() {
        return this.mapTextureManager;
    }

    public WaypointStyleAssetManager getWaypointStyleAssetManager() {
        return this.waypointStyleAssetManager;
    }

    public void onWindowFocusChanged(boolean focused) {
        this.windowFocused = focused;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Text takePanorama(File directory) {
        int i = 4;
        int j = 4096;
        int k = 4096;
        int l = this.window.getFramebufferWidth();
        int m = this.window.getFramebufferHeight();
        Framebuffer framebuffer = this.getFramebuffer();
        float f = this.player.getPitch();
        float g = this.player.getYaw();
        float h = this.player.lastPitch;
        float n = this.player.lastYaw;
        this.gameRenderer.setBlockOutlineEnabled(false);
        try {
            this.gameRenderer.setCameraOverride(new CameraOverride((Vector3fc)new Vector3f(this.gameRenderer.getCamera().getHorizontalPlane())));
            this.window.setFramebufferWidth(4096);
            this.window.setFramebufferHeight(4096);
            framebuffer.resize(4096, 4096);
            for (int o = 0; o < 6; ++o) {
                switch (o) {
                    case 0: {
                        this.player.setYaw(g);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 1: {
                        this.player.setYaw((g + 90.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 2: {
                        this.player.setYaw((g + 180.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 3: {
                        this.player.setYaw((g - 90.0f) % 360.0f);
                        this.player.setPitch(0.0f);
                        break;
                    }
                    case 4: {
                        this.player.setYaw(g);
                        this.player.setPitch(-90.0f);
                        break;
                    }
                    default: {
                        this.player.setYaw(g);
                        this.player.setPitch(90.0f);
                    }
                }
                this.player.lastYaw = this.player.getYaw();
                this.player.lastPitch = this.player.getPitch();
                this.gameRenderer.updateCamera(RenderTickCounter.ONE);
                this.gameRenderer.renderWorld(RenderTickCounter.ONE);
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                ScreenshotRecorder.saveScreenshot((File)directory, (String)("panorama_" + o + ".png"), (Framebuffer)framebuffer, (int)4, message -> {});
            }
            MutableText text = Text.literal((String)directory.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent((ClickEvent)new ClickEvent.OpenFile(directory.getAbsoluteFile())));
            MutableText mutableText = Text.translatable((String)"screenshot.success", (Object[])new Object[]{text});
            return mutableText;
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't save image", (Throwable)exception);
            MutableText mutableText = Text.translatable((String)"screenshot.failure", (Object[])new Object[]{exception.getMessage()});
            return mutableText;
        }
        finally {
            this.player.setPitch(f);
            this.player.setYaw(g);
            this.player.lastPitch = h;
            this.player.lastYaw = n;
            this.gameRenderer.setBlockOutlineEnabled(true);
            this.window.setFramebufferWidth(l);
            this.window.setFramebufferHeight(m);
            framebuffer.resize(l, m);
            this.gameRenderer.setCameraOverride(null);
        }
    }

    public SplashTextResourceSupplier getSplashTextLoader() {
        return this.splashTextLoader;
    }

    public @Nullable Overlay getOverlay() {
        return this.overlay;
    }

    public SocialInteractionsManager getSocialInteractionsManager() {
        return this.socialInteractionsManager;
    }

    public Window getWindow() {
        return this.window;
    }

    public InactivityFpsLimiter getInactivityFpsLimiter() {
        return this.inactivityFpsLimiter;
    }

    public DebugHud getDebugHud() {
        return this.inGameHud.getDebugHud();
    }

    public BufferBuilderStorage getBufferBuilders() {
        return this.bufferBuilders;
    }

    public void setMipmapLevels(int mipmapLevels) {
        this.atlasManager.setMipmapLevels(mipmapLevels);
    }

    public LoadedEntityModels getLoadedEntityModels() {
        return (LoadedEntityModels)this.bakedModelManager.getEntityModelsSupplier().get();
    }

    public boolean shouldFilterText() {
        return this.getUserProperties().flag(UserApiService.UserFlag.PROFANITY_FILTER_ENABLED);
    }

    public void loadBlockList() {
        this.socialInteractionsManager.loadBlockList();
        this.getProfileKeys().fetchKeyPair();
    }

    public GuiNavigationType getNavigationType() {
        return this.navigationType;
    }

    public void setNavigationType(GuiNavigationType navigationType) {
        this.navigationType = navigationType;
    }

    public NarratorManager getNarratorManager() {
        return this.narratorManager;
    }

    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    public AbuseReportContext getAbuseReportContext() {
        return this.abuseReportContext;
    }

    public RealmsPeriodicCheckers getRealmsPeriodicCheckers() {
        return this.realmsPeriodicCheckers;
    }

    public QuickPlayLogger getQuickPlayLogger() {
        return this.quickPlayLogger;
    }

    public CommandHistoryManager getCommandHistoryManager() {
        return this.commandHistoryManager;
    }

    public SymlinkFinder getSymlinkFinder() {
        return this.symlinkFinder;
    }

    public PlayerSkinCache getPlayerSkinCache() {
        return this.playerSkinCache;
    }

    private float getTargetMillisPerTick(float millis) {
        TickManager tickManager;
        if (this.world != null && (tickManager = this.world.getTickManager()).shouldTick()) {
            return Math.max(millis, tickManager.getMillisPerTick());
        }
        return millis;
    }

    public ItemModelManager getItemModelManager() {
        return this.itemModelManager;
    }

    public boolean canCurrentScreenInterruptOtherScreen() {
        return (this.currentScreen == null || this.currentScreen.canInterruptOtherScreen()) && !this.disconnecting;
    }

    public static @Nullable String getLauncherBrand() {
        return System.getProperty("minecraft.launcher.brand");
    }

    public PacketApplyBatcher getPacketApplyBatcher() {
        return this.packetApplyBatcher;
    }

    public GizmoDrawing.CollectorScope newGizmoScope() {
        return GizmoDrawing.using((GizmoCollector)this.gizmoCollector);
    }

    public Collection<GizmoCollectorImpl.Entry> getGizmos() {
        return this.gizmos;
    }

    private static /* synthetic */ void method_37285(Consumer consumer, Text text) {
        consumer.accept(Text.translatable((String)"debug.profiling.stop", (Object[])new Object[]{text}));
    }

    static {
        LOGGER = LogUtils.getLogger();
        DEFAULT_FONT_ID = Identifier.ofVanilla((String)"default");
        UNICODE_FONT_ID = Identifier.ofVanilla((String)"uniform");
        ALT_TEXT_RENDERER_ID = Identifier.ofVanilla((String)"alt");
        REGIONAL_COMPLIANCIES_ID = Identifier.ofVanilla((String)"regional_compliancies.json");
        COMPLETED_UNIT_FUTURE = CompletableFuture.completedFuture(Unit.INSTANCE);
        SOCIAL_INTERACTIONS_NOT_AVAILABLE = Text.translatable((String)"multiplayer.socialInteractions.not_available");
        SAVING_LEVEL_TEXT = Text.translatable((String)"menu.savingLevel");
    }
}

