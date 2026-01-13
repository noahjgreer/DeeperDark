/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.SplashTextRenderer
 *  net.minecraft.client.resource.SplashTextResourceSupplier
 *  net.minecraft.client.session.Session
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.SinglePreparationResourceReloader
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Holidays
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.profiler.Profiler
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.MonthDay;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.session.Session;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Holidays;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SplashTextResourceSupplier
extends SinglePreparationResourceReloader<List<Text>> {
    private static final Style SPLASH_TEXT_STYLE = Style.EMPTY.withColor(-256);
    public static final Text MERRY_X_MAS_ = SplashTextResourceSupplier.create((String)"Merry X-mas!");
    public static final Text HAPPY_NEW_YEAR_ = SplashTextResourceSupplier.create((String)"Happy new year!");
    public static final Text OOOOO_O_O_OOOOO__SPOOKY_ = SplashTextResourceSupplier.create((String)"OOoooOOOoooo! Spooky!");
    private static final Identifier RESOURCE_ID = Identifier.ofVanilla((String)"texts/splashes.txt");
    private static final Random RANDOM = Random.create();
    private List<Text> splashTexts = List.of();
    private final Session session;

    public SplashTextResourceSupplier(Session session) {
        this.session = session;
    }

    private static Text create(String text) {
        return Text.literal((String)text).setStyle(SPLASH_TEXT_STYLE);
    }

    protected List<Text> prepare(ResourceManager resourceManager, Profiler profiler) {
        List<Text> list;
        block8: {
            BufferedReader bufferedReader = MinecraftClient.getInstance().getResourceManager().openAsReader(RESOURCE_ID);
            try {
                list = bufferedReader.lines().map(String::trim).filter(splashText -> splashText.hashCode() != 125780783).map(SplashTextResourceSupplier::create).toList();
                if (bufferedReader == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException iOException) {
                    return List.of();
                }
            }
            bufferedReader.close();
        }
        return list;
    }

    protected void apply(List<Text> list, ResourceManager resourceManager, Profiler profiler) {
        this.splashTexts = List.copyOf(list);
    }

    public @Nullable SplashTextRenderer get() {
        MonthDay monthDay = Holidays.now();
        if (monthDay.equals(Holidays.CHRISTMAS_EVE)) {
            return SplashTextRenderer.MERRY_X_MAS;
        }
        if (monthDay.equals(Holidays.NEW_YEARS_DAY)) {
            return SplashTextRenderer.HAPPY_NEW_YEAR;
        }
        if (monthDay.equals(Holidays.HALLOWEEN)) {
            return SplashTextRenderer.OOOOO_O_O_OOOOO__SPOOKY;
        }
        if (this.splashTexts.isEmpty()) {
            return null;
        }
        if (this.session != null && RANDOM.nextInt(this.splashTexts.size()) == 42) {
            return new SplashTextRenderer(SplashTextResourceSupplier.create((String)(this.session.getUsername().toUpperCase(Locale.ROOT) + " IS YOU")));
        }
        return new SplashTextRenderer((Text)this.splashTexts.get(RANDOM.nextInt(this.splashTexts.size())));
    }

    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

