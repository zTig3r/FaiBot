package de.ztiger.faibot.interactions.stats;

import de.ztiger.faibot.interactions.ICommand;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;

import static de.ztiger.faibot.FaiBot.getter;
import static de.ztiger.faibot.interactions.stats.StatsHelper.createStatsImage;
import static de.ztiger.faibot.utils.Colors.colors;
import static de.ztiger.faibot.utils.Lang.getLang;

public class StatsCmd implements ICommand {

    private static final Button apply = Button.success("apply", getLang("stats.apply"));
    private static final Button cancel = Button.danger("cancel", getLang("stats.cancel"));

    @Override
    public CommandData getCommandData() {
        return Commands.slash("stats", "Erhalte Statistiken von dir oder anderen auf dem Server")
                .addOptions(new OptionData(OptionType.USER, "user", "Erhalte Statistiken von einem bestimmten Benutzer").setRequired(false));
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user") != null ? event.getOption("user").getAsMember() : event.getMember();

        event.replyFiles(FileUpload.fromData(createStatsImage(member, convertColor(getter.getCardColor(member.getId()))))).queue();
    }

    public static void sendPreview(StringSelectInteractionEvent event, String color) {
        //event.editMessage("").setAttachments(FileUpload.fromData(createStatsImage(event.getMember(), convertColor(color)))).setActionRow(apply, cancel).setEmbeds().queue();
    }

    private static Color convertColor(String color) {
        return Color.decode((color.contains("#") ? "#94c6f3" : colors.get(color).hex));
    }
}
