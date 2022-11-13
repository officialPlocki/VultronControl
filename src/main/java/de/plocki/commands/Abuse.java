package de.plocki.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Abuse extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("abuse")) {
            event.reply("If you find any abusive content, offensive content or anything unappreciative on a server from Vultron Studios,\n" +
                    "please send us a mail over abuse@vultronstudios.net or open a ticket over **/support** and send us the subdomain / address of the server.\n" +
                    "We make every effort to remove content of this type.\n" +
                    "As soon as we become aware of something of this nature, we will take care of reviewing the content at the earliest opportunity,\n" +
                    "removing it and taking action such as an account ban or server deletion.\n" +
                    "We thank you in advance for your help.\n" +
                    "In the whole process and after, your data will not be passed on to the user who owns the server.").setEphemeral(true).queue();
        }
    }
}
