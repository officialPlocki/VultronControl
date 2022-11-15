package de.plocki.commands;

import de.plocki.util.AccountManager;
import de.plocki.util.Hooks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class VerifyCode extends ListenerAdapter {

    public static final HashMap<String, Boolean> codes = new HashMap<>();
    public static final HashMap<String, Long> ident = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("verifycode")) {
            TextInput input = TextInput.create("email", "E-Mail", TextInputStyle.SHORT)
                    .setMinLength(3)
                    .setPlaceholder("Hosting account E-Mail for identification")
                    .build();
            Modal modal = Modal.create("verifycode_modal", "Identification")
                    .addActionRow(input)
                    .build();
            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().equals("verifycode_modal")) {
            if(!event.getValue("email").getAsString().contains("@")) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "E-Mail isn't valid.");
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
            } else if(!new AccountManager().hasAccount(event.getInteraction().getUser().getIdLong())) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "You're not registered.");
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
            } else {
                if(new Hooks().getPteroApplication().retrieveUsersByUsername(event.getInteraction().getUser().getId(), true).execute().get(0).getEmail().equalsIgnoreCase(event.getValue("email").getAsString())) {
                    String uuid = UUID.randomUUID().toString();
                    codes.put(uuid, true);
                    ident.put(uuid, event.getUser().getIdLong());
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "Your verification code is: " + uuid + "\nDon't send the code to any users.");
                    event.replyEmbeds(builder.build())
                            .setEphemeral(true)
                            .queue();
                } else {
                    String uuid = UUID.randomUUID().toString();
                    codes.put(uuid, false);
                    ident.put(uuid, event.getUser().getIdLong());
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "Your verification code is: " + uuid + "\nDon't send the code to any users.");
                    event.replyEmbeds(builder.build())
                            .setEphemeral(true)
                            .queue();
                }
            }
        }
    }

}
