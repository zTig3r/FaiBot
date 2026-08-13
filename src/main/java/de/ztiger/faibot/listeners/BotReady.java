package de.ztiger.faibot.listeners;

import de.ztiger.faibot.utils.GuildProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jspecify.annotations.NonNull;

@Slf4j
@RequiredArgsConstructor
public class BotReady extends ListenerAdapter {

    private final InteractionListener interactionListener;
    private final GuildProvider guildProvider;

    @Override
    public void onReady(@NonNull ReadyEvent event) {
        guildProvider.getMainGuild().ifPresentOrElse(
                guild -> guild.updateCommands().addCommands(interactionListener.getCommandDataList()).queue(),
                () -> log.error("Main guild could not be found!")
        );
    }
}