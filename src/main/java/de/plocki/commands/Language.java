package de.plocki.commands;

import de.plocki.util.Hooks;
import de.plocki.util.LanguageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public class Language extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("language")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription("Which language do you want to use?");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .addActionRow(
                            Button.primary("en", "English"),
                            Button.primary("de", "Deutsch")
                    ).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(Objects.equals(event.getButton().getId(), "de")) {
            new LanguageUtil().setUserLanguage(event.getInteraction().getUser().getIdLong(), LanguageUtil.lang.DE);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription("Der Bot ist jetzt für dich auf Deutsch eingestellt.\nBitte beachte, dass dies nicht den Support und die Verifizierung betrifft.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
        } else if(Objects.equals(event.getButton().getId(), "en")) {
            new LanguageUtil().setUserLanguage(event.getInteraction().getUser().getIdLong(), LanguageUtil.lang.EN);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription("The bot is now set to english for you.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
        }
    }

}
