package de.ztiger.faibot.interactions.color;

import net.dv8tion.jda.api.entities.Member;

import java.util.List;

import static de.ztiger.faibot.FaiBot.setter;
import static de.ztiger.faibot.listeners.BotReady.GUILD;
import static de.ztiger.faibot.utils.Colors.colors;
import static de.ztiger.faibot.utils.Colors.getTranslations;

public class ColorService {

    public void applyNameColor(Member member, String color) {
        String newRole = colors.get(color).translation;
        removeCurrentNameColors(member);

        member.getGuild().getRolesByName(newRole, true).stream()
                .findFirst()
                .ifPresent(role -> member.getGuild().addRoleToMember(member, role).queue());
    }

    public void removeCurrentNameColors(Member member) {
        List<String> translations = getTranslations();
        member.getRoles().stream()
                .filter(role -> translations.contains(role.getName()))
                .forEach(role -> GUILD.removeRoleFromMember(member, role).queue());
    }

    public void resetStatsColor(String memberId) {
        // TODO: Add option for default color
        setter.setCardColor(memberId, "#94c6f3");
    }

    public void updateStatsColor(String memberId, String color) {
        setter.setCardColor(memberId, color);
    }
}
