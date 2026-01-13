/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.BanDetails
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.session.BanReason
 *  net.minecraft.client.session.Bans
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.text.Texts
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Urls
 *  net.minecraft.util.Util
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.session;

import com.mojang.authlib.minecraft.BanDetails;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.session.BanReason;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class Bans {
    private static final Text TEMPORARY_TITLE = Text.translatable((String)"gui.banned.title.temporary").formatted(Formatting.BOLD);
    private static final Text PERMANENT_TITLE = Text.translatable((String)"gui.banned.title.permanent").formatted(Formatting.BOLD);
    public static final Text NAME_TITLE = Text.translatable((String)"gui.banned.name.title").formatted(Formatting.BOLD);
    private static final Text SKIN_TITLE = Text.translatable((String)"gui.banned.skin.title").formatted(Formatting.BOLD);
    private static final Text SKIN_DESCRIPTION = Text.translatable((String)"gui.banned.skin.description", (Object[])new Object[]{Text.of((URI)Urls.JAVA_MODERATION)});

    public static ConfirmLinkScreen createBanScreen(BooleanConsumer callback, BanDetails banDetails) {
        return new ConfirmLinkScreen(callback, Bans.getTitle((BanDetails)banDetails), Bans.getDescriptionText((BanDetails)banDetails), Urls.JAVA_MODERATION, ScreenTexts.ACKNOWLEDGE, true);
    }

    public static ConfirmLinkScreen createSkinBanScreen(Runnable onClose) {
        URI uRI = Urls.JAVA_MODERATION;
        return new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open(uRI);
            }
            onClose.run();
        }, SKIN_TITLE, SKIN_DESCRIPTION, uRI, ScreenTexts.ACKNOWLEDGE, true);
    }

    public static ConfirmLinkScreen createUsernameBanScreen(String username, Runnable onClose) {
        URI uRI = Urls.JAVA_MODERATION;
        return new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open(uRI);
            }
            onClose.run();
        }, NAME_TITLE, (Text)Text.translatable((String)"gui.banned.name.description", (Object[])new Object[]{Text.literal((String)username).formatted(Formatting.YELLOW), Text.of((URI)Urls.JAVA_MODERATION)}), uRI, ScreenTexts.ACKNOWLEDGE, true);
    }

    private static Text getTitle(BanDetails banDetails) {
        return Bans.isTemporary((BanDetails)banDetails) ? TEMPORARY_TITLE : PERMANENT_TITLE;
    }

    private static Text getDescriptionText(BanDetails banDetails) {
        return Text.translatable((String)"gui.banned.description", (Object[])new Object[]{Bans.getReasonText((BanDetails)banDetails), Bans.getDurationText((BanDetails)banDetails), Text.of((URI)Urls.JAVA_MODERATION)});
    }

    private static Text getReasonText(BanDetails banDetails) {
        String string = banDetails.reason();
        String string2 = banDetails.reasonMessage();
        if (StringUtils.isNumeric((CharSequence)string)) {
            int i = Integer.parseInt(string);
            BanReason banReason = BanReason.byId((int)i);
            Object text = banReason != null ? Texts.withStyle((Text)banReason.getDescription(), (Style)Style.EMPTY.withBold(Boolean.valueOf(true))) : (string2 != null ? Text.translatable((String)"gui.banned.description.reason_id_message", (Object[])new Object[]{i, string2}).formatted(Formatting.BOLD) : Text.translatable((String)"gui.banned.description.reason_id", (Object[])new Object[]{i}).formatted(Formatting.BOLD));
            return Text.translatable((String)"gui.banned.description.reason", (Object[])new Object[]{text});
        }
        return Text.translatable((String)"gui.banned.description.unknownreason");
    }

    private static Text getDurationText(BanDetails banDetails) {
        if (Bans.isTemporary((BanDetails)banDetails)) {
            Text text = Bans.getTemporaryBanDurationText((BanDetails)banDetails);
            return Text.translatable((String)"gui.banned.description.temporary", (Object[])new Object[]{Text.translatable((String)"gui.banned.description.temporary.duration", (Object[])new Object[]{text}).formatted(Formatting.BOLD)});
        }
        return Text.translatable((String)"gui.banned.description.permanent").formatted(Formatting.BOLD);
    }

    private static Text getTemporaryBanDurationText(BanDetails banDetails) {
        Duration duration = Duration.between(Instant.now(), banDetails.expires());
        long l = duration.toHours();
        if (l > 72L) {
            return ScreenTexts.days((long)duration.toDays());
        }
        if (l < 1L) {
            return ScreenTexts.minutes((long)duration.toMinutes());
        }
        return ScreenTexts.hours((long)duration.toHours());
    }

    private static boolean isTemporary(BanDetails banDetails) {
        return banDetails.expires() != null;
    }
}

