package de.plocki.commands;

import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationUser;
import de.plocki.Main;
import de.plocki.util.AccountManager;
import de.plocki.util.Hooks;
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

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("deleteaccount")) {
            TextInput reason = TextInput.create("accdel_reason", "Reason", TextInputStyle.PARAGRAPH)
                    .setMaxLength(80)
                    .setRequired(false)
                    .setPlaceholder("Please tell us why you want to delete your account (Anonymous).")
                    .setValue("Leave this here for nothing - remove for more information.")
                    .build();
            TextInput feedback = TextInput.create("accdel_feedback", "Feedback", TextInputStyle.PARAGRAPH)
                    .setMaxLength(80)
                    .setRequired(false)
                    .setPlaceholder("What can we do better next time? (Anonymous)")
                    .setValue("Leave this here for nothing - remove for more information.")
                    .build();
            TextInput email = TextInput.create("accdel_email", "E-Mail", TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setMinLength(1)
                    .setPlaceholder("Please enter your E-Mail for confirmation.")
                    .build();
            Modal modal = Modal.create("accdel", "Delete Account")
                    .addActionRows(ActionRow.of(reason), ActionRow.of(feedback), ActionRow.of(email))
                    .build();
            event.replyModal(modal).queue();
        }
    }

    private static HashMap<Long, String> emails = new HashMap<>();

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().equals("accdel")) {
            if(new AccountManager().hasAccount(event.getInteraction().getUser().getIdLong())) {
                if(event.getValue("accdel_email").getAsString().equals(new Hooks().getPteroApplication().retrieveUsersByUsername(event.getInteraction().getUser().getIdLong() + "", true).execute().get(0).getEmail())) {

                    Guild guild = Main.jda.getGuildById(new Hooks().fromFile("vultronGuildID"));
                    assert guild != null;
                    TextChannel channel = guild.getTextChannelById(new Hooks().fromFile("vultronGuildCloseAccountChannelID"));
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Account deletion survey");
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
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
                    b.setDescription(
                            "We're sorry to hear that you want to delete your account.\n" +
                                    "Your ELIZON. account will be deleted after confirmation.\n" +
                                    "You can create a new account at any time.\n" +
                                    "Your data will be deleted.\n" +
                                    "However, data may only be deleted at a later point in time if they are subject to a retention obligation.\n" +
                                    "We may and will only delete these after the period of the respective period has expired.\n" +
                                    "Therefore, please do not open a support ticket.\n" +
                                    "We ask for your understanding.\n" +
                                    "If you delete your account, you terminate the contract between you and us without notice.\n" +
                                    "\n" +
                                    "Please confirm now your account deletion and that you have understood the information above.\n" +
                                    "Your account and servers will be deleted permanently without any chance to recover it.\n" +
                                    "Please save your servers, if you didn't already.\n" +
                                    "Your support tickets will not be deleted. If you want that we delete your tickets too, please open a support ticket or write a E-Mail to support@vultronstudios.net.");
                    event.replyEmbeds(b.build())
                            .setEphemeral(true)
                            .addActionRow(Button.danger("accdel_confirm", "Confirm deletion"))
                            .queue();
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "The information does not match the stored data.");
                    event.replyEmbeds(builder.build())
                            .setEphemeral(true)
                            .queue();
                }
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "You aren't registered.");
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
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
            TextChannel channel = guild.getTextChannelById(new Hooks().fromFile("vultronGuildChannelID"));
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Account deletion confirmation request");
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
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
            b.setDescription(
                    "Your account is now deleted.\n" +
                            "If you have further questions, please contact us over /support or write us a E-Mail to support@vultronstudios.net.\n" +
                            "Your account deletion will be confirmed via E-Mail as soon as possible.");
            event.replyEmbeds(b.build())
                    .setEphemeral(true)
                    .queue();
        }
    }

}
