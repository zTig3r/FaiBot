package de.ztiger.faibot.interactions.halloffame;

import de.ztiger.faibot.config.BotColor;
import de.ztiger.faibot.config.ConfigManager;
import de.ztiger.faibot.localization.keys.HallOfFame;
import de.ztiger.faibot.services.LocalizationService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;

import java.util.List;

@RequiredArgsConstructor
public class HallOfFameComponents {

    private final ConfigManager configManager;
    private final LocalizationService i18n;

    public Container getHallOfFame(List<String> entries) {
        return Container.of(
                TextDisplay.of(i18n.get(HallOfFame.TITLE)),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(String.join("\n", entries)),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(i18n.get(HallOfFame.FOOTER))
        ).withAccentColor(configManager.getColor(BotColor.DEFAULT));
    }
}
