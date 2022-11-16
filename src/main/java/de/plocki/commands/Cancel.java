package de.plocki.commands;

import de.plocki.util.Hooks;
import de.plocki.util.LanguageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Cancel extends ListenerAdapter {

    public Cancel() {
        LanguageUtil util = new LanguageUtil();
        util.setString("All actions are canceled.", "cancel_description", LanguageUtil.lang.EN);
        util.setString("Alle Aktionen wurden abgebrochen.", "cancel_description", LanguageUtil.lang.DE);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("cancel")) {
            LanguageUtil util = new LanguageUtil();
            CreateAccount.waitingEmail.remove(event.getInteraction().getUser().getIdLong());
            RequestServer.usage.remove(event.getInteraction().getUser().getIdLong());
            Support.answering.remove(event.getInteraction().getUser().getIdLong());
            Support.s.remove(event.getInteraction().getUser().getIdLong());

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
            builder.setDescription(util.getString("cancel_description", lang));
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
        }
    }
}
