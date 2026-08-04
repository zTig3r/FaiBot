package de.ztiger.faibot.interactions.idea;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IModalHandler;
import de.ztiger.faibot.localization.keys.Idea;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class IdeaCmd implements ICommand, IModalHandler {

    @Override
    public String getModalId() {
        return IdeaComponents.MODAL_ID;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("idea", "Erstelle eine Umfrage für einen Vorschlag");
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        event.replyModal(IdeaComponents.ideaModal()).queue();
    }

    @Override
    public void modalInteraction(ModalInteractionEvent event) {
        String subject = event.getValue(IdeaComponents.FIELD_SUBJECT).getAsString();
        String body = event.getValue(IdeaComponents.FIELD_BODY).getAsString();

        MessageCreateData pollMessage = IdeaComponents.createPollMessage(
                event.getUser().getAsMention(),
                subject,
                body
        );

        ChannelProvider.getChannel(BotChannel.RECOMMENDATIONS).ifPresent(channel -> {
            channel.sendMessage(pollMessage).queue();
            event.reply(Localization.get(Idea.Modal.SUCCESS)).setEphemeral(true).queue();
        });
    }
}