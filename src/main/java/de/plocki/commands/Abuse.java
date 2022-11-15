package de.plocki.commands;

import de.plocki.util.Hooks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Abuse extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("abuse")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription("If you find any abusive content, offensive content or anything unappreciative on a server from ELIZON.,\n" +
                    "please send us a mail over abuse@vultronstudios.net or open a ticket over /support and send us the subdomain / address of the server.\n" +
                    "We make every effort to remove content of this type.\n" +
                    "As soon as we become aware of something of this nature, we will take care of reviewing the content at the earliest opportunity,\n" +
                    "removing it and taking action such as an account ban or server deletion.\n" +
                    "We thank you in advance for your help.\n" +
                    "In the whole process and after, your data will not be passed on to the user who owns the server.");
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
        }
    }
}
