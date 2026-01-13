/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.Bootstrap
 *  net.minecraft.SharedConstants
 *  net.minecraft.block.AbstractBlock
 *  net.minecraft.block.ComposterBlock
 *  net.minecraft.block.FireBlock
 *  net.minecraft.block.cauldron.CauldronBehavior
 *  net.minecraft.block.dispenser.DispenserBehavior
 *  net.minecraft.command.EntitySelectorOptions
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.attribute.DefaultAttributeRegistry
 *  net.minecraft.entity.attribute.EntityAttribute
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemGroups
 *  net.minecraft.registry.Registries
 *  net.minecraft.resource.featuretoggle.FeatureFlags
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.util.Language
 *  net.minecraft.util.annotation.SuppressLinter
 *  net.minecraft.util.logging.DebugLoggerPrintStream
 *  net.minecraft.util.logging.LoggerPrintStream
 *  net.minecraft.world.rule.GameRuleVisitor
 *  net.minecraft.world.rule.GameRules
 *  org.slf4j.Logger
 */
package net.minecraft;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Language;
import net.minecraft.util.annotation.SuppressLinter;
import net.minecraft.util.logging.DebugLoggerPrintStream;
import net.minecraft.util.logging.LoggerPrintStream;
import net.minecraft.world.rule.GameRuleVisitor;
import net.minecraft.world.rule.GameRules;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@SuppressLinter(reason="System.out setup")
public class Bootstrap {
    public static final PrintStream SYSOUT = System.out;
    private static volatile boolean initialized;
    private static final Logger LOGGER;
    public static final AtomicLong LOAD_TIME;

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        Instant instant = Instant.now();
        if (Registries.REGISTRIES.getIds().isEmpty()) {
            throw new IllegalStateException("Unable to load registries");
        }
        FireBlock.registerDefaultFlammables();
        ComposterBlock.registerDefaultCompostableItems();
        if (EntityType.getId((EntityType)EntityType.PLAYER) == null) {
            throw new IllegalStateException("Failed loading EntityTypes");
        }
        EntitySelectorOptions.register();
        DispenserBehavior.registerDefaults();
        CauldronBehavior.registerBehavior();
        Registries.bootstrap();
        ItemGroups.collect();
        Bootstrap.setOutputStreams();
        LOAD_TIME.set(Duration.between(instant, Instant.now()).toMillis());
    }

    private static <T> void collectMissingTranslations(Iterable<T> registry, Function<T, String> keyExtractor, Set<String> translationKeys) {
        Language language = Language.getInstance();
        registry.forEach(object -> {
            String string = (String)keyExtractor.apply(object);
            if (!language.hasTranslation(string)) {
                translationKeys.add(string);
            }
        });
    }

    private static void collectMissingGameRuleTranslations(Set<String> translations) {
        Language language = Language.getInstance();
        GameRules gameRules = new GameRules(FeatureFlags.FEATURE_MANAGER.getFeatureSet());
        gameRules.accept((GameRuleVisitor)new /* Unavailable Anonymous Inner Class!! */);
    }

    public static Set<String> getMissingTranslations() {
        TreeSet<String> set = new TreeSet<String>();
        Bootstrap.collectMissingTranslations((Iterable)Registries.ATTRIBUTE, EntityAttribute::getTranslationKey, set);
        Bootstrap.collectMissingTranslations((Iterable)Registries.ENTITY_TYPE, EntityType::getTranslationKey, set);
        Bootstrap.collectMissingTranslations((Iterable)Registries.STATUS_EFFECT, StatusEffect::getTranslationKey, set);
        Bootstrap.collectMissingTranslations((Iterable)Registries.ITEM, Item::getTranslationKey, set);
        Bootstrap.collectMissingTranslations((Iterable)Registries.BLOCK, AbstractBlock::getTranslationKey, set);
        Bootstrap.collectMissingTranslations((Iterable)Registries.CUSTOM_STAT, (T stat) -> "stat." + stat.toString().replace(':', '.'), set);
        Bootstrap.collectMissingGameRuleTranslations(set);
        return set;
    }

    public static void ensureBootstrapped(Supplier<String> callerGetter) {
        if (!initialized) {
            throw Bootstrap.createNotBootstrappedException(callerGetter);
        }
    }

    private static RuntimeException createNotBootstrappedException(Supplier<String> callerGetter) {
        try {
            String string = callerGetter.get();
            return new IllegalArgumentException("Not bootstrapped (called from " + string + ")");
        }
        catch (Exception exception) {
            IllegalArgumentException runtimeException = new IllegalArgumentException("Not bootstrapped (failed to resolve location)");
            runtimeException.addSuppressed(exception);
            return runtimeException;
        }
    }

    public static void logMissing() {
        Bootstrap.ensureBootstrapped(() -> "validate");
        if (SharedConstants.isDevelopment) {
            Bootstrap.getMissingTranslations().forEach(key -> LOGGER.error("Missing translations: {}", key));
            CommandManager.checkMissing();
        }
        DefaultAttributeRegistry.checkMissing();
    }

    private static void setOutputStreams() {
        if (LOGGER.isDebugEnabled()) {
            System.setErr((PrintStream)new DebugLoggerPrintStream("STDERR", (OutputStream)System.err));
            System.setOut((PrintStream)new DebugLoggerPrintStream("STDOUT", (OutputStream)SYSOUT));
        } else {
            System.setErr((PrintStream)new LoggerPrintStream("STDERR", (OutputStream)System.err));
            System.setOut((PrintStream)new LoggerPrintStream("STDOUT", (OutputStream)SYSOUT));
        }
    }

    public static void println(String str) {
        SYSOUT.println(str);
    }

    static {
        LOGGER = LogUtils.getLogger();
        LOAD_TIME = new AtomicLong(-1L);
    }
}

