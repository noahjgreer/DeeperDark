/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.tab.GridScreenTab
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.EmptyWidget
 *  net.minecraft.client.gui.widget.GridWidget$Adder
 *  net.minecraft.client.gui.widget.NarratedMultilineTextWidget
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.Subscription
 *  net.minecraft.client.realms.dto.Subscription$SubscriptionType
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.gui.RealmsPopups
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.gui.screen.tab.RealmsSubscriptionInfoTab
 *  net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab
 *  net.minecraft.client.realms.util.RealmsUtil
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Urls
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen.tab;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.Subscription;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
class RealmsSubscriptionInfoTab
extends GridScreenTab
implements RealmsUpdatableTab {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_60284 = 200;
    private static final int field_60285 = 2;
    private static final int field_60286 = 6;
    static final Text SUBSCRIPTION_TITLE = Text.translatable((String)"mco.configure.world.subscription.tab");
    private static final Text SUBSCRIPTION_START_LABEL_TEXT = Text.translatable((String)"mco.configure.world.subscription.start");
    private static final Text TIME_LEFT_LABEL_TEXT = Text.translatable((String)"mco.configure.world.subscription.timeleft");
    private static final Text DAYS_LEFT_LABEL_TEXT = Text.translatable((String)"mco.configure.world.subscription.recurring.daysleft");
    private static final Text EXPIRED_TEXT = Text.translatable((String)"mco.configure.world.subscription.expired").formatted(Formatting.GRAY);
    private static final Text EXPIRES_IN_LESS_THAN_A_DAY_TEXT = Text.translatable((String)"mco.configure.world.subscription.less_than_a_day").formatted(Formatting.GRAY);
    private static final Text UNKNOWN_TEXT = Text.translatable((String)"mco.configure.world.subscription.unknown");
    private static final Text RECURRING_INFO_TEXT = Text.translatable((String)"mco.configure.world.subscription.recurring.info");
    private final RealmsConfigureWorldScreen screen;
    private final MinecraftClient client;
    private final ButtonWidget deleteWorldButton;
    private final NarratedMultilineTextWidget subscriptionInfoTextWidget;
    private final TextWidget startDateTextWidget;
    private final TextWidget timeLeftLabelTextWidget;
    private final TextWidget daysLeftTextWidget;
    private RealmsServer serverData;
    private Text daysLeft = UNKNOWN_TEXT;
    private Text startDate = UNKNOWN_TEXT;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable Subscription.SubscriptionType type;

    RealmsSubscriptionInfoTab(RealmsConfigureWorldScreen screen, MinecraftClient client, RealmsServer server) {
        super(SUBSCRIPTION_TITLE);
        this.screen = screen;
        this.client = client;
        this.serverData = server;
        GridWidget.Adder adder = this.grid.setRowSpacing(6).createAdder(1);
        TextRenderer textRenderer = screen.getTextRenderer();
        Objects.requireNonNull(textRenderer);
        adder.add((Widget)new TextWidget(200, 9, SUBSCRIPTION_START_LABEL_TEXT, textRenderer));
        Objects.requireNonNull(textRenderer);
        this.startDateTextWidget = (TextWidget)adder.add((Widget)new TextWidget(200, 9, this.startDate, textRenderer));
        adder.add((Widget)EmptyWidget.ofHeight((int)2));
        Objects.requireNonNull(textRenderer);
        this.timeLeftLabelTextWidget = (TextWidget)adder.add((Widget)new TextWidget(200, 9, TIME_LEFT_LABEL_TEXT, textRenderer));
        Objects.requireNonNull(textRenderer);
        this.daysLeftTextWidget = (TextWidget)adder.add((Widget)new TextWidget(200, 9, this.daysLeft, textRenderer));
        adder.add((Widget)EmptyWidget.ofHeight((int)2));
        adder.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"mco.configure.world.subscription.extend"), button -> ConfirmLinkScreen.open((Screen)screen, (String)Urls.getExtendJavaRealmsUrl((String)realmsServer.remoteSubscriptionId, (UUID)client.getSession().getUuidOrNull()))).dimensions(0, 0, 200, 20).build());
        adder.add((Widget)EmptyWidget.ofHeight((int)2));
        this.deleteWorldButton = (ButtonWidget)adder.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"mco.configure.world.delete.button"), button -> client.setScreen((Screen)RealmsPopups.createContinuableWarningPopup((Screen)screen, (Text)Text.translatable((String)"mco.configure.world.delete.question.line1"), popupScreen -> this.onDeletionConfirmed()))).dimensions(0, 0, 200, 20).build());
        adder.add((Widget)EmptyWidget.ofHeight((int)2));
        this.subscriptionInfoTextWidget = (NarratedMultilineTextWidget)adder.add((Widget)NarratedMultilineTextWidget.builder((Text)Text.empty(), (TextRenderer)textRenderer).width(200).build(), Positioner.create().alignHorizontalCenter());
        this.subscriptionInfoTextWidget.setCentered(false);
        this.update(server);
    }

    private void onDeletionConfirmed() {
        RealmsUtil.runAsync(client -> client.deleteWorld(this.serverData.id), (Consumer)RealmsUtil.openingScreenAndLogging(arg_0 -> ((RealmsConfigureWorldScreen)this.screen).createErrorScreen(arg_0), (String)"Couldn't delete world")).thenRunAsync(() -> this.client.setScreen(this.screen.getParent()), (Executor)this.client);
        this.client.setScreen((Screen)this.screen);
    }

    private void getSubscription(long worldId) {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            Subscription subscription = realmsClient.subscriptionFor(worldId);
            this.daysLeft = this.daysLeftPresentation(subscription.daysLeft());
            this.startDate = RealmsSubscriptionInfoTab.localPresentation((Instant)subscription.startDate());
            this.type = subscription.type();
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't get subscription", (Throwable)realmsServiceException);
            this.client.setScreen(this.screen.createErrorScreen(realmsServiceException));
        }
    }

    private static Text localPresentation(Instant time) {
        String string = ZonedDateTime.ofInstant(time, ZoneId.systemDefault()).format(Util.getDefaultLocaleFormatter((FormatStyle)FormatStyle.MEDIUM));
        return Text.literal((String)string).formatted(Formatting.GRAY);
    }

    private Text daysLeftPresentation(int daysLeft) {
        boolean bl2;
        if (daysLeft < 0 && this.serverData.expired) {
            return EXPIRED_TEXT;
        }
        if (daysLeft <= 1) {
            return EXPIRES_IN_LESS_THAN_A_DAY_TEXT;
        }
        int i = daysLeft / 30;
        int j = daysLeft % 30;
        boolean bl = i > 0;
        boolean bl3 = bl2 = j > 0;
        if (bl && bl2) {
            return Text.translatable((String)"mco.configure.world.subscription.remaining.months.days", (Object[])new Object[]{i, j}).formatted(Formatting.GRAY);
        }
        if (bl) {
            return Text.translatable((String)"mco.configure.world.subscription.remaining.months", (Object[])new Object[]{i}).formatted(Formatting.GRAY);
        }
        if (bl2) {
            return Text.translatable((String)"mco.configure.world.subscription.remaining.days", (Object[])new Object[]{j}).formatted(Formatting.GRAY);
        }
        return Text.empty();
    }

    public void update(RealmsServer server) {
        this.serverData = server;
        this.getSubscription(server.id);
        this.startDateTextWidget.setMessage(this.startDate);
        if (this.type == Subscription.SubscriptionType.NORMAL) {
            this.timeLeftLabelTextWidget.setMessage(TIME_LEFT_LABEL_TEXT);
        } else if (this.type == Subscription.SubscriptionType.RECURRING) {
            this.timeLeftLabelTextWidget.setMessage(DAYS_LEFT_LABEL_TEXT);
        }
        this.daysLeftTextWidget.setMessage(this.daysLeft);
        boolean bl = RealmsMainScreen.isSnapshotRealmsEligible() && server.parentWorldName != null;
        this.deleteWorldButton.active = server.expired;
        if (bl) {
            this.subscriptionInfoTextWidget.setMessage((Text)Text.translatable((String)"mco.snapshot.subscription.info", (Object[])new Object[]{server.parentWorldName}));
        } else {
            this.subscriptionInfoTextWidget.setMessage(RECURRING_INFO_TEXT);
        }
        this.grid.refreshPositions();
    }

    public Text getNarratedHint() {
        return ScreenTexts.joinLines((Text[])new Text[]{SUBSCRIPTION_TITLE, SUBSCRIPTION_START_LABEL_TEXT, this.startDate, TIME_LEFT_LABEL_TEXT, this.daysLeft});
    }
}

