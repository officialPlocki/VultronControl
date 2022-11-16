package de.plocki.commands;

import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
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
import java.util.Objects;

public class DeleteServer extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        LanguageUtil util = new LanguageUtil();
        LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
        if(event.getName().equalsIgnoreCase("deleteserver")) {
            if(new AccountManager().hasAccount(event.getInteraction().getUser().getIdLong())) {
                TextInput reason = TextInput.create("deletion_reason", util.getString("reason", lang), TextInputStyle.PARAGRAPH)
                        .setPlaceholder(util.getString("survey_servdel_reason", lang))
                        .setRequired(false)
                        .setMaxLength(80)
                        .setValue(util.getString("survey_servdel_reason", lang))
                        .build();
                TextInput feedback = TextInput.create("deletion_feedback", "Feedback", TextInputStyle.PARAGRAPH)
                        .setPlaceholder(util.getString("survey_servdel_feedback", lang))
                        .setRequired(false)
                        .setMaxLength(80)
                        .setValue(util.getString("survey_servdel_feedback", lang))
                        .build();
                TextInput serverID = TextInput.create("deletion_serverid", "Server ID", TextInputStyle.SHORT)
                        .setPlaceholder(util.getString("servdel_name", lang))
                        .setRequired(true)
                        .setMinLength(32)
                        .build();
                TextInput email = TextInput.create("deletion_email", "E-Mail", TextInputStyle.SHORT)
                        .setPlaceholder(util.getString("servdel_email", lang))
                        .setRequired(true)
                        .setMinLength(1)
                        .build();
                Modal modal = Modal.create("deletion", util.getString("servdel", lang))
                        .addActionRows(ActionRow.of(reason), ActionRow.of(feedback), ActionRow.of(serverID), ActionRow.of(email))
                        .build();
                event.replyModal(modal).queue();
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

    private static final HashMap<Long, String> ids = new HashMap<>();
    private static final HashMap<Long, String> emails = new HashMap<>();
    public DeleteServer() {
        LanguageUtil util = new LanguageUtil();

        util.setString("Please tell us why you want to delete your server (Anonymous).","survey_servdel_reason", LanguageUtil.lang.EN);
        util.setString("Bitte sage uns, warum du deinen Server löschen möchtest (Anonym).", "survey_servdel_reason", LanguageUtil.lang.DE);
        util.setString("What can we do better next time (Anonymous)?", "survey_servdel_feedback", LanguageUtil.lang.EN);
        util.setString("Was können wir das nächstes mal Verbessern (Anonym)?", "survey_servdel_feedback", LanguageUtil.lang.DE);
        util.setString("Server deletion", "servdel", LanguageUtil.lang.EN);
        util.setString("Server Löschung", "servdel", LanguageUtil.lang.DE);
        util.setString("Please enter your E-Mail for confirmation.", "servdel_email", LanguageUtil.lang.EN);
        util.setString("Bitte gebe deine E-Mail zur Bestätigung ein.", "servdel_email", LanguageUtil.lang.DE);
        util.setString("Enter the Server ID", "servdel_name", LanguageUtil.lang.EN);
        util.setString("Schreibe hier die Server ID herein", "servdel_name", LanguageUtil.lang.DE);
        util.setString("Server has been deleted.", "servdel_confirm_information", LanguageUtil.lang.EN);
        util.setString("Server wurde gelöscht.", "servdel_confirm_information", LanguageUtil.lang.DE);
        util.setString("You can only delete your own servers.", "servdel_fail_own", LanguageUtil.lang.EN);
        util.setString("Du kannst nur deine eigenen Server löschen.", "servdel_fail_own", LanguageUtil.lang.DE);
        util.setString("Please confirm your decision.\n" +
                "Your server and subdomain (if given) will be deleted immediately and permanently removed with all data.", "servdel_confirm", LanguageUtil.lang.EN);
        util.setString("Bitte bestätige deine Entscheidung.\n" +
                "Dein Server und Subdomain (wenn vergeben) werden direkt und permanent mit allen Daten gelöscht.", "servdel_confirm", LanguageUtil.lang.DE);

    }
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().equalsIgnoreCase("deletion")) {
            LanguageUtil util = new LanguageUtil();
            LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
            Guild guild = Main.jda.getGuildById(new Hooks().fromFile("vultronGuildID"));
            assert guild != null;
            TextChannel channel = guild.getTextChannelById(new Hooks().fromFile("vultronGuildCloseChannelID"));
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Server deletion survey");
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.addField("Deletion reason", event.getValue("deletion_reason").getAsString(), true);
            builder.addField("Feedback", event.getValue("deletion_feedback").getAsString(), true);
            assert channel != null;
            channel.sendMessageEmbeds(builder.build()).queue();

            ids.put(event.getInteraction().getUser().getIdLong(),  Objects.requireNonNull(event.getValue("deletion_serverid")).getAsString());
            emails.put(event.getInteraction().getUser().getIdLong(),  Objects.requireNonNull(event.getValue("deletion_email")).getAsString());

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.cyan);
            b.setAuthor("ELIZON.");
            b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            b.setDescription(util.getString("servdel_confirm", lang));
            event.replyEmbeds(b.build())
                    .setEphemeral(true)
                    .addActionRow(Button.danger("deletion_delete", "»"))
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        LanguageUtil util = new LanguageUtil();
        LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
        if(event.getButton().getId().equals("deletion_delete")) {
            String id = ids.get(event.getInteraction().getUser().getIdLong());
            String email = emails.get(event.getInteraction().getUser().getIdLong());
            ids.remove(event.getInteraction().getUser().getIdLong());
            emails.remove(event.getInteraction().getUser().getIdLong());
            ApplicationServer server = new Hooks().getServerByID(id);
            if(new Hooks().getPteroApplication().retrieveUserById(server.getOwnerIdLong()).execute().getEmail().equalsIgnoreCase(email)) {
                if(String.valueOf(event.getInteraction().getUser().getIdLong()).equals(new Hooks().getPteroApplication().retrieveUserById(server.getOwnerIdLong()).execute().getUserName())) {
                    server.getController().suspend().execute();
                    server.getController().delete(false).execute();

                    FileBuilder builder = new FileBuilder("servers");
                    List<String> list;
                    if(builder.getYaml().isSet("domains")) {
                        list = builder.getYaml().getStringList("domains");
                    } else {
                        list = new ArrayList<>();
                    }

                    if(builder.getYaml().isSet(id)) {
                        String domain = builder.getYaml().getString(id);
                        String i = builder.getYaml().getString(domain);
                        list.remove(domain);
                        builder.getYaml().set("domains", list);
                        builder.getYaml().set(id, null);
                        builder.getYaml().set(domain, null);
                        builder.save();
                        try {
                            new Hooks().deleteSubdomain(i);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription(util.getString("servdel_confirm_information", lang));
                    event.replyEmbeds(b.build())
                            .setEphemeral(true)
                            .queue();
                } else {
                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription(util.getString("servdel_fail_own", lang));
                    event.replyEmbeds(b.build())
                            .setEphemeral(true)
                            .queue();
                }
            } else {
                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.cyan);
                b.setAuthor("ELIZON.");
                b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                b.setDescription(util.getString("accdel_dataf", lang));
                event.replyEmbeds(b.build())
                        .setEphemeral(true)
                        .queue();
            }
        }
    }
}
