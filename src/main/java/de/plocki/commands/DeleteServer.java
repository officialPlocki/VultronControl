package de.plocki.commands;

import com.mattmalec.pterodactyl4j.application.entities.ApplicationAllocation;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
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
import java.util.Objects;

public class DeleteServer extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("deleteserver")) {
            if(new AccountManager().hasAccount(event.getInteraction().getUser().getIdLong())) {
                TextInput reason = TextInput.create("deletion_reason", "Reason for deletion", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Why do you want to delete your server (Anonymous)?")
                        .setRequired(false)
                        .setMaxLength(80)
                        .setValue("Leave this here for nothing - remove for more information.")
                        .build();
                TextInput feedback = TextInput.create("deletion_feedback", "Feedback", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Please give us feedback so that we can improve the service (Anonymous)")
                        .setRequired(false)
                        .setMaxLength(80)
                        .setValue("Leave this here for nothing - remove for more information.")
                        .build();
                TextInput serverID = TextInput.create("deletion_serverid", "Server Name", TextInputStyle.SHORT)
                        .setPlaceholder("Enter the server name (more unique = faster deletion)")
                        .setRequired(true)
                        .setMinLength(32)
                        .build();
                TextInput email = TextInput.create("deletion_email", "E-Mail", TextInputStyle.SHORT)
                        .setPlaceholder("Enter your E-Mail of your ELIZON. account")
                        .setRequired(true)
                        .setMinLength(1)
                        .build();
                Modal modal = Modal.create("deletion", "Server Deletion")
                        .addActionRows(ActionRow.of(reason), ActionRow.of(feedback), ActionRow.of(serverID), ActionRow.of(email))
                        .build();
                event.replyModal(modal).queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "You don't have a ELIZON. account. Please register first!");
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    private static final HashMap<Long, String> ids = new HashMap<>();
    private static final HashMap<Long, String> emails = new HashMap<>();

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().equalsIgnoreCase("deletion")) {
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

            ids.put(event.getInteraction().getUser().getIdLong(),  event.getValue("deletion_serverid").getAsString());
            emails.put(event.getInteraction().getUser().getIdLong(),  event.getValue("deletion_email").getAsString());

            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.cyan);
            b.setAuthor("ELIZON.");
            b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            b.setDescription(
                    "Please confirm your decision. Your server and subdomain (if given) will be deleted immediately and permanently removed with all data.");
            event.replyEmbeds(b.build())
                    .setEphemeral(true)
                    .addActionRow(Button.danger("deletion_delete", "Confirm deletion"))
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getButton().getId().equals("deletion_delete")) {
            String id = ids.get(event.getInteraction().getUser().getIdLong());
            String email = emails.get(event.getInteraction().getUser().getIdLong());
            ids.remove(event.getInteraction().getUser().getIdLong());
            emails.remove(event.getInteraction().getUser().getIdLong());
            ApplicationServer server = new Hooks().getPteroApplication().retrieveServersByName(id, true).execute().get(0);
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

                    event.reply("Server deleted.").setEphemeral(true).queue();
                } else {
                    event.reply("You can only delete your own servers.").setEphemeral(true).queue();
                }
            } else {
                event.reply("The information does not match the stored data.").setEphemeral(true).queue();
            }
        }
    }
}
