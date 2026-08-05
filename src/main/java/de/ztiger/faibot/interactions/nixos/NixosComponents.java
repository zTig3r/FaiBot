package de.ztiger.faibot.interactions.nixos;

import de.ztiger.faibot.localization.keys.Nixos;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.modals.Modal;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class NixosComponents {

    public static final String MODAL_ID = "nixos";
    public static final String FIELD_MONTH = "month";
    public static final String TOP_IMAGE = "top_image";
    public static final String WINNER_IMAGES = "winner_images";

    public static Modal nixoModal(List<String> winners) {
        StringSelectMenu monthMenu = StringSelectMenu.create(FIELD_MONTH)
                .addOptions(Arrays.stream(Month.values())
                        .map(month -> SelectOption.of(
                                month.getDisplayName(TextStyle.FULL, Locale.GERMAN),
                                month.getDisplayName(TextStyle.FULL, Locale.GERMAN)
                        ).withDefault(month == LocalDate.now().getMonth()))
                        .toList())
                .build();

        return Modal.create(MODAL_ID, Localization.get(Nixos.Modal.TITLE))
                .addComponents(
                        Label.of("Monat", monthMenu),
                        Label.of("Top 10", AttachmentUpload.create(TOP_IMAGE).setMaxValues(1).build()),
                        TextDisplay.of(String.join(", ", winners)),
                        Label.of("Gewinner", AttachmentUpload.create(WINNER_IMAGES).setMaxValues(10).build())
                ).build();
    }

    public static Container winners(String month, List<String> winners, Message.Attachment topImage, List<Message.Attachment> winnerImages) {
        List<String> formattedWinners = IntStream.range(0, winners.size())
                .mapToObj(i -> Localization.format(Nixos.Message.WINNER, "number", i + 1, "user", winners.get(i))).toList();

        List<MediaGalleryItem> winnerGalleryItems = winnerImages.stream().map(image -> MediaGalleryItem.fromUrl(image.getUrl())).toList();

        return Container.of(
                TextDisplay.of(Localization.format(Nixos.Message.TITLE, "month", month, "year", LocalDate.now().getYear())),
                TextDisplay.of(Localization.get(Nixos.Message.TOP)),
                MediaGallery.of(MediaGalleryItem.fromUrl(topImage.getUrl())),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(String.join("\n", formattedWinners)),
                MediaGallery.of(winnerGalleryItems)
        );
    }
}
