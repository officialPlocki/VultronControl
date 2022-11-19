package de.plocki.commands;

import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationUser;
import de.plocki.Main;
import de.plocki.util.AccountManager;
import de.plocki.util.Hooks;
import de.plocki.util.LanguageUtil;
import de.plocki.util.files.FileBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BooleanSupplier;

public class DeleteAccount extends ListenerAdapter {

    public DeleteAccount() {
        LanguageUtil util = new LanguageUtil();

        util.setString("Please tell us why you want to delete your account (Anonymous).","survey_accdel_reason", LanguageUtil.lang.EN);
        util.setString("Bitte sage uns, warum du deinen Account löschen möchtest (Anonym).", "survey_accdel_reason", LanguageUtil.lang.DE);
        util.setString("What can we do better next time (Anonymous)?", "survey_accdel_feedback", LanguageUtil.lang.EN);
        util.setString("Was können wir das nächstes mal Verbessern (Anonym)?", "survey_accdel_feedback", LanguageUtil.lang.DE);
        util.setString("Reason", "reason", LanguageUtil.lang.EN);
        util.setString("Grund", "reason", LanguageUtil.lang.DE);
        util.setString("Account deletion", "accdel", LanguageUtil.lang.EN);
        util.setString("Konto Löschung", "accdel", LanguageUtil.lang.DE);
        util.setString("Please enter your E-Mail for confirmation.", "accdel_email", LanguageUtil.lang.EN);
        util.setString("Bitte gebe deine E-Mail zur Bestätigung ein.", "accdel_email", LanguageUtil.lang.DE);
        util.setString(
                "Do you really want do delete your ELIZON. Account?\n" +
                        "All servers and data will be permanently deleted.",
                "accdel_information", LanguageUtil.lang.EN);
        util.setString("Möchtest du wirklich dein ELIZON. Nutzerkonto löschen?\n" +
                "Alle Server und Daten werden permanent gelöscht.", "accdel_information", LanguageUtil.lang.DE);
        util.setString("The information does not match the stored data.", "accdel_dataf", LanguageUtil.lang.EN);
        util.setString("Die angegebenen Informationen passen nicht zu den gespeicherten Daten.", "accdel_dataf", LanguageUtil.lang.DE);
        util.setString("You aren't registered.", "accdel_notreg", LanguageUtil.lang.EN);
        util.setString("Du besitzt kein ELIZON. Konto.", "accdel_notreg", LanguageUtil.lang.DE);
        util.setString("Your account is now deleted.\n" +
                "If you have further questions, please contact us over /support or write us a E-Mail to support@elizon.host.\n" +
                "Your account deletion will be confirmed via E-Mail as soon as possible.", "accdel_confirm", LanguageUtil.lang.EN);
        util.setString("Dein Konto ist jetzt gelöscht.\n" +
                "Falls du weitere Fragen hast, kontaktiere uns über /support oder per E-Mail an support@elizon.host.\n" +
                "Deine Kontolöschung wird dir zum nächst möglichen Zeitpunkt per E-Mail bestätigt.", "accdel_confirm", LanguageUtil.lang.DE);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("deleteaccount")) {
            LanguageUtil util = new LanguageUtil();
            LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
            TextInput reason = TextInput.create("accdel_reason", util.getString("reason", lang), TextInputStyle.PARAGRAPH)
                    .setMaxLength(80)
                    .setRequired(false)
                    .setPlaceholder(util.getString("survey_accdel_reason", lang))
                    .setValue(util.getString("survey_accdel_reason", lang))
                    .build();
            TextInput feedback = TextInput.create("accdel_feedback", "Feedback", TextInputStyle.PARAGRAPH)
                    .setMaxLength(80)
                    .setRequired(false)
                    .setPlaceholder(util.getString("survey_accdel_feedback", lang))
                    .setValue(util.getString("survey_accdel_feedback", lang))
                    .build();
            TextInput email = TextInput.create("accdel_email", "E-Mail", TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setMinLength(1)
                    .setPlaceholder(util.getString("accdel_email", lang))
                    .build();
            Modal modal = Modal.create("accdel", util.getString("accdel", lang))
                    .addActionRows(ActionRow.of(reason), ActionRow.of(feedback), ActionRow.of(email))
                    .build();
            event.replyModal(modal).queue();
        }
    }

    private static final HashMap<Long, String> emails = new HashMap<>();

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().equals("accdel")) {
            LanguageUtil util = new LanguageUtil();
            LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
            if(new AccountManager().hasAccount(event.getInteraction().getUser().getIdLong())) {
                if(event.getValue("accdel_email").getAsString().equals(new Hooks().getPteroApplication().retrieveUsersByUsername(event.getInteraction().getUser().getIdLong() + "", true).execute().get(0).getEmail())) {
                    Guild guild = Main.jda.getGuildById(new Hooks().fromFile("vultronGuildID"));
                    assert guild != null;
                    TextChannel channel = guild.getTextChannelById(new Hooks().fromFile("vultronGuildCloseAccountChannelID"));
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Account deletion survey");
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.addField("Deletion reason", event.getValue("accdel_reason").getAsString(), true);
                    builder.addField("Feedback", event.getValue("accdel_feedback").getAsString(), true);
                    assert channel != null;
                    channel.sendMessageEmbeds(builder.build()).queue();

                    emails.put(event.getInteraction().getUser().getIdLong(), event.getValue("accdel_email").getAsString());

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription(util.getString("accdel_information", lang));
                    event.replyEmbeds(b.build())
                            .setEphemeral(true)
                            .addActionRow(Button.danger("accdel_confirm", "»"))
                            .queue();
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(util.getString("accdel_dataf", lang));
                    event.replyEmbeds(builder.build())
                            .setEphemeral(true)
                            .queue();
                }
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("accdel_notreg", lang));
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        LanguageUtil util = new LanguageUtil();
        LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
        if(event.getButton().getId().equals("accdel_confirm")) {
            ApplicationUser user = new Hooks().getPteroApplication().retrieveUsersByEmail(emails.get(event.getInteraction().getUser().getIdLong()), true).execute().get(0);
            emails.remove(event.getInteraction().getUser().getIdLong());
            List<ApplicationServer> servers = new Hooks().getPteroApplication().retrieveServersByOwner(user).execute();
            servers.forEach(applicationServer -> {
                FileBuilder builder = new FileBuilder("servers");
                List<String> list;
                if(builder.getYaml().isSet("domains")) {
                    list = builder.getYaml().getStringList("domains");
                } else {
                    list = new ArrayList<>();
                }

                if(builder.getYaml().isSet(applicationServer.getName())) {
                    String domain = builder.getYaml().getString(applicationServer.getName());
                    String i = builder.getYaml().getString(domain);
                    list.remove(domain);
                    builder.getYaml().set("domains", list);
                    builder.getYaml().set(applicationServer.getName(), null);
                    builder.getYaml().set(domain, null);
                    builder.save();
                    try {
                        new Hooks().deleteSubdomain(i);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                applicationServer.getController().suspend().execute();
                applicationServer.getController().delete(false).execute();
            });

            Guild guild = Main.jda.getGuildById(new Hooks().fromFile("vultronGuildID"));
            assert guild != null;
            TextChannel channel = guild.getTextChannelById(new Hooks().fromFile("vultronGuildAdminChannelID"));
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Account deletion confirmation request");
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.addField("E-Mail", user.getEmail(), true);
            builder.addField("Username", user.getUserName(), true);
            builder.addField("Mail title", "Confirmation of account deletion", true);
            builder.addField("Mail text", "Hello,\n" +
                    "we're contacting you according to your requested account deletion.\n" +
                    "We want to inform you, that your account with the E-Mail \"" + user.getEmail() + "\" has been deleted successfully.\n\n" +
                    "If you've any question, please answer to this E-Mail.\n" +
                    "\n" +
                    "- ELIZON. Support", true);
            assert channel != null;
            channel.sendMessageEmbeds(builder.build()).queue();

            user.delete().execute();

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.cyan);
            b.setAuthor("ELIZON.");
            b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            b.setDescription(util.getString("accdel_confirm", lang));
            event.replyEmbeds(b.build())
                    .setEphemeral(true)
                    .queue();
        }
    }

}
