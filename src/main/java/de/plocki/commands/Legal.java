package de.plocki.commands;

import de.plocki.util.Hooks;
import de.plocki.util.LanguageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Legal extends ListenerAdapter {

    public Legal() {
        LanguageUtil util = new LanguageUtil();
        util.setString("Special Conditions ELIZON.BOT: https://vultronstudios.net/legal\n" +
                "Terms of Use (ELIZON. section): https://vultronstudios.net/legal\n" +
                "Privacy Policy: https://vultronstudios.net/legal",
                "legal_inf", LanguageUtil.lang.EN);
        util.setString("Gesonderte Bedingungen ELIZON.BOT: https://vultronstudios.net/legal\n" +
                        "Nutzungsbedingungen (ELIZON. Sektion): https://vultronstudios.net/legal\n" +
                        "Datenschutzerklärung: https://vultronstudios.net/legal",
                "legal_inf", LanguageUtil.lang.EN);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("legal")) {
            LanguageUtil util = new LanguageUtil();
            LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(util.getString("legal_inf", lang));
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
        }
    }
}
