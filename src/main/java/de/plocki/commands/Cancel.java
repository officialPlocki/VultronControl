package de.plocki.commands;

import de.plocki.util.Hooks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Cancel extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("cancel")) {
            CreateAccount.waitingEmail.remove(event.getInteraction().getUser().getIdLong());
            RequestServer.usage.remove(event.getInteraction().getUser().getIdLong());
            Support.answering.remove(event.getInteraction().getUser().getIdLong());
            Support.s.remove(event.getInteraction().getUser().getIdLong());

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription("All actions are canceled.");
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
        }
    }
}
