package de.plocki.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Cancel extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("cancel")) {
            CreateAccount.waitingEmail.remove(event.getInteraction().getUser().getIdLong());
            RequestServer.usage.remove(event.getInteraction().getUser().getIdLong());
            Support.answering.remove(event.getInteraction().getUser().getIdLong());
            Support.s.remove(event.getInteraction().getUser().getIdLong());
            event.reply("All actions are canceled.").setEphemeral(true).queue();
        }
    }
}
