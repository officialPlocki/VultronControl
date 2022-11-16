package de.plocki.commands;

import de.plocki.util.Hooks;
import de.plocki.util.LanguageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Abuse extends ListenerAdapter {

    public Abuse() {
        LanguageUtil util = new LanguageUtil();
        util.setString(
                "If you find any abusive content, offensive content or anything unappreciative on a server from ELIZON.,\n" +
                "please send us a mail over abuse@vultronstudios.net or open a ticket over /support and send us the subdomain / address of the server.\n" +
                "We make every effort to remove content of this type as soon as possible.\n" +
                "As soon as we become aware of something of this nature, we will take care of reviewing the content at the earliest opportunity,\n" +
                "removing it and taking action such as an account ban or server deletion.\n" +
                "We thank you in advance for your help.\n" +
                "In the whole process and after, your data will not be passed on to the user who owns the server."
                , "abuse_description", LanguageUtil.lang.EN);
        util.setString(
                "Wenn du auf einem Server von ELIZON. beleidigende Inhalte, anstößige Inhalte oder irgendetwas Unanerkennendes findest,\n" +
                        "sendest du uns bitte eine E-Mail an abuse@vultronstudios.net oder öffnest ein Ticket über /support und sendest uns die Subdomain / Adresse des Servers.\n" +
                        "Wir bemühen uns, Inhalte dieser Art zu schnellstmöglich entfernen.\n" +
                        "Sobald uns etwas derartiges bekannt wird, werden wir uns bemühen, die Inhalte so schnell wie möglich zu überprüfen,\n" +
                        "zu entfernen und Maßnahmen wie eine Kontosperre oder Serverlöschung einzuleiten.\n" +
                        "Vielen Danken im Voraus für deine Hilfe.\n" +
                        "Während des gesamten Prozesses und danach werden deine Daten nicht an den Nutzer weitergegeben, dem der Server gehört."
                , "abuse_description", LanguageUtil.lang.DE);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("abuse")) {
            LanguageUtil util = new LanguageUtil();
            LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
            EmbedBuilder builder = new EmbedBuilder();
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setDescription(util.getString("abuse_description", lang));
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
        }
    }
}
