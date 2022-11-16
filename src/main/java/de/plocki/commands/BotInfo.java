package de.plocki.commands;

import de.plocki.util.Hooks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class BotInfo extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("botinfo")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setDescription("This bot is developed by ELIZON. by Vultron Studios.\n" +
                    "Support us\n" +
                    "Vultron Studios: https://vultronstudios.net/discord\n" +
                    "ELIZON.: https://elizon.host/discord");
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
        }
    }
}
