package de.ztiger.faibot.interactions.points;

import de.ztiger.faibot.localization.keys.Points;
import de.ztiger.faibot.services.LocalizationService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;

@RequiredArgsConstructor
public class PointsComponents {

    private final LocalizationService i18n;

    public Container getPointsContainer(String username, int points, int appearances, String positions) {
        return Container.of(
                TextDisplay.of(i18n.format(Points.TITLE, "user", username, "points", points)),
                TextDisplay.of(i18n.format(Points.APPEARANCES, "appearances", appearances)),
                TextDisplay.of(i18n.format(Points.POSITIONS, "positions", positions)),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(i18n.get(Points.CALCULATION))
        );
    }
}
