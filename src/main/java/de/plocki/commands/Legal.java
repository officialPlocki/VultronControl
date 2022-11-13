package de.plocki.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Legal extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("legal")) {
            event.reply("The Terms of Use (\"Vultron Studios Hosting\" Section) and the Privacy Policy apply to Vultron Studios Hosting.\n" +
                    "With this bot, a connection(s) to the Vultron Studios website (hosting panel) is established in the background,\n" +
                    "whereby the specified data such as e-mail address and Discord ID are transmitted to the account data created by the bot in the hosting panel have been to access.\n" +
                    "The bot can access data stored in the hosting panel, which the user has sent to the bot or entered and changed in the hosting panel.\n" +
                    "The bot is able to create and delete the user account at the request of the user.\n" +
                    "In addition, the bot can make a non-binding request in the name of the user account for a free digital game server for Vultron Studios and delete it at a later point in time at the user's request.\n" +
                    "For more informations, see the \"Vultron Studios Hosting\" Terms of Use & Vultron Studios Privacy Policy.\n" +
                    "\n" +
                    "Link: https://vultronstudios.net/legal").setEphemeral(true).queue();
        }
    }
}
