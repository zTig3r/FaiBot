package de.ztiger.faibot.interactions.nixos;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IModalHandler;
import de.ztiger.faibot.localization.keys.Nixos;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NixosCmd implements ICommand, IModalHandler {

    private static final List<String> WINNER_CACHE = new ArrayList<>();

    @Override
    public String getModalId() {
        return NixosComponents.MODAL_ID;
    }


    @Override
    public CommandData getCommandData() {
        return Commands.slash("nixos", "Erstellt eine Nachricht mit den Statistiken der aktuellen Nixo-Season")
                .addOption(OptionType.STRING, "winners", "Nutzer markieren oder Namen eingeben (Leerzeichen um zu trennen)", true)
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption("winners");
        if (option == null) return;

        List<String> winnerNames = new ArrayList<>();

        Pattern pattern = Pattern.compile("<@!?\\d+>|\\S+");
        Matcher matcher = pattern.matcher(option.getAsString());

        while (matcher.find()) {
            winnerNames.add(matcher.group());
        }

        WINNER_CACHE.clear();
        WINNER_CACHE.addAll(winnerNames);

        event.replyModal(NixosComponents.nixoModal(winnerNames)).queue();
    }


    @Override
    public void modalInteraction(ModalInteractionEvent event) {
        ChannelProvider.getChannel(BotChannel.WELCOME).ifPresent(channel -> {
            channel.sendMessageComponents(NixosComponents.winners(
                    event.getValue(NixosComponents.FIELD_MONTH).getAsStringList().getFirst(),
                    WINNER_CACHE,
                    event.getValue(NixosComponents.TOP_IMAGE).getAsAttachmentList().getFirst(),
                    event.getValue(NixosComponents.WINNER_IMAGES).getAsAttachmentList()
            )).useComponentsV2().queue();

            WINNER_CACHE.clear();

            event.reply(Localization.get(Nixos.Modal.SUCCESS)).setEphemeral(true).queue();
        });
    }
}
