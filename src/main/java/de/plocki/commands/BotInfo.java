package de.plocki.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotInfo extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("botinfo")) {
            event.reply("This bot is developed by <@470300602677723136> from Vultron Studios. Support us: https://vultronstudios.net/discord").setEphemeral(true).queue();
        }
    }
}
