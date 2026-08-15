package de.ztiger.faibot.interactions.nixos;

import de.ztiger.faibot.config.BotColor;
import de.ztiger.faibot.config.ConfigManager;
import de.ztiger.faibot.localization.keys.Nixos;
import de.ztiger.faibot.services.LocalizationService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.modals.Modal;

import java.awt.*;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class NixosComponents {

    public static final String COMPONENT_ID = "nixos";
    public static final String MONTH_FIELD = "month";
    public static final String YEAR_FIELD = "year";
    public static final String TOP_LIST = "top_list";
    public static final String WINNER_IMAGES = "winner_images";
    public static final String CONFIRM_OVERRIDE = "confirm_override";
    public static final String CANCEL_OVERRIDE = "cancel_override";

    private final ConfigManager configManager;
    private final LocalizationService i18n;

    public Modal getNixoModal(List<String> winners) {
        YearMonth now = YearMonth.now();

        StringSelectMenu monthMenu = StringSelectMenu.create(MONTH_FIELD)
                .addOptions(Arrays.stream(Month.values())
                                    .map(month -> SelectOption.of(month.getDisplayName(TextStyle.FULL_STANDALONE, i18n.getLocale()),
                                                                  month.name())
                                            .withDefault(month == now.getMonth()))
                                    .toList())
                .build();

        return Modal.create(COMPONENT_ID, i18n.get(Nixos.Modal.TITLE))
                .addComponents(
                        Label.of(i18n.get(Nixos.Modal.MONTH), monthMenu),
                        Label.of(i18n.get(Nixos.Modal.YEAR), TextInput.create(YEAR_FIELD, TextInputStyle.SHORT)
                                .setPlaceholder(String.valueOf(now.getYear()))
                                .setValue(String.valueOf(now.getYear()))
                                .build()),
                        Label.of(i18n.get(Nixos.Modal.TOP), TextInput.create(TOP_LIST, TextInputStyle.PARAGRAPH).build()),
                        TextDisplay.of(i18n.format(Nixos.Modal.Winner.LIST, "winners", String.join(", ", winners))),
                        Label.of(i18n.get(Nixos.Modal.Winner.IMAGES), AttachmentUpload.create(WINNER_IMAGES).setMaxValues(10).build())
                ).build();
    }

    public Container getWinnerComponent(String month, String year, List<String> top, List<String> winners,
                                        List<Message.Attachment> winnerImages, String nixosRoleMention) {
        List<String> formattedWinners = IntStream.range(0, winners.size())
                .mapToObj(i -> i18n.format(Nixos.Message.WINNER, "number", i + 1, "user", winners.get(i))).toList();

        List<MediaGalleryItem> winnerGalleryItems = winnerImages.stream().map(image -> MediaGalleryItem.fromUrl(image.getUrl())).toList();

        return Container.of(
                TextDisplay.of(i18n.format(Nixos.Message.TITLE, "month", month, "year", year)),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(i18n.get(Nixos.Message.TOP)),
                TextDisplay.of(String.join("\n", top)),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(String.join("\n", formattedWinners)),
                MediaGallery.of(winnerGalleryItems),
                TextDisplay.of(i18n.format(Nixos.Message.FOOTER, "nixosrole", nixosRoleMention))
        ).withAccentColor(configManager.getColor(BotColor.DEFAULT));
    }

    public Container getConfirmOverride(String month, String year) {
        return Container.of(
                TextDisplay.of(i18n.format(Nixos.Override.DESCRIPTION, "month", month, "year", year)),
                ActionRow.of(
                        Button.primary(COMPONENT_ID + ":" + CONFIRM_OVERRIDE, i18n.get(Nixos.Override.CONFIRM)),
                        Button.secondary(COMPONENT_ID + ":" + CANCEL_OVERRIDE, i18n.get(Nixos.Override.CANCEL))
                )
        ).withAccentColor(Color.YELLOW);
    }
}
