package de.plocki.commands;

import de.plocki.util.AccountManager;
import de.plocki.util.Hooks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.HashMap;

public class CreateAccount extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("createaccount")) {
            if(event.getChannel().getType().isGuild()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription("This command is only available in Direct Messages. I'll send you a message.");
                event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                event.getInteraction().getUser().openPrivateChannel().queue(privateChannel -> {
                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription("Do you want to create a ELIZON. Account?");
                    event.replyEmbeds(b.build()).setEphemeral(true)
                            .addActionRow(
                                    Button.success("create_yes", "Yes"),
                                    Button.danger("create_no", "No"))
                            .queue();
                });
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription("Do you want to create a ELIZON. Account?");
                event.replyEmbeds(builder.build()).setEphemeral(true)
                        .addActionRow(
                                Button.success("create_yes", "Yes"),
                                Button.danger("create_no", "No"))
                        .queue();
            }
        }
    }

    public static final HashMap<Long, Boolean> waitingEmail = new HashMap<>();
    private static final HashMap<Long, String> credentialsTemp = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getChannel().getType().isMessage() && !event.getMessage().getChannel().getType().isGuild()) {
            if(waitingEmail.containsKey(event.getMessage().getAuthor().getIdLong())) {
                if(!event.getMessage().getContentDisplay().contains("@")) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription("Please enter a valid E-Mail address.");
                    event.getChannel().sendMessageEmbeds(builder.build()).queue();
                    return;
                }
                waitingEmail.remove(event.getMessage().getAuthor().getIdLong());
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription("Please wait...");
                event.getChannel().sendMessageEmbeds(builder.build()).queue();
                if(!new Hooks().getPteroApplication().retrieveUsersByUsername(event.getMessage().getAuthor().getIdLong() + "", true).execute().isEmpty()) {
                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription("Your ID is already registered.");
                    event.getChannel().sendMessageEmbeds(b.build()).queue();
                }
                String password = new AccountManager().addAccount(event.getMessage().getContentDisplay(), event.getAuthor().getAsTag().split("#")[0], "#" + event.getAuthor().getAsTag().split("#")[1], event.getAuthor().getIdLong());
                credentialsTemp.put(event.getAuthor().getIdLong(), "Your account has been registered. You can now login with these credentials:\nUsername: ||" + event.getAuthor().getIdLong() + "||\nE-Mail: ||" + event.getMessage().getContentDisplay() + "||\nPassword: ||" + password + "||\nPlease change the password after login and enable 2-FA if you want (recommended).");
                event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription("Click here to receive your credentials.");
                    event.getChannel().sendMessageEmbeds(b.build())
                            .addActionRow(
                                    Button.primary("credentials", "Receive"))
                            .queue();
                });
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getButton().getId().equals("create_yes")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Privacy Policy & Terms of Use (ELIZON. section): https://vultronstudios.net/legal\n" +
                    "Do you accept the Privacy Policy and Terms of Use (ELIZON. section)?");
            event.replyEmbeds(builder.build())
                    .addActionRow(
                            Button.success("create_accept", "Yes"),
                            Button.danger("create_decline", "No"))
                    .queue();
        } else if(event.getButton().getId().equals("create_no")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription("Okay, we're sorry to hear that.");
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
        } else if(event.getButton().getId().equals("create_accept")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Please write now your E-Mail you want to use.\n" +
                    "Please use a real E-Mail, you'll need it when you want to delete your account, get notifications or change the password.");
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
            waitingEmail.put(event.getInteraction().getUser().getIdLong(), true);
            RequestServer.usage.remove(event.getInteraction().getUser().getIdLong());
            Support.answering.remove(event.getInteraction().getUser().getIdLong());
            Support.s.remove(event.getInteraction().getUser().getIdLong());
        } else if(event.getButton().getId().equals("create_decline")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Okay, we're sorry to hear that.");
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
        } else if(event.getButton().getId().equals("used")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Not possible, already used!");
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
        } else if(event.getButton().getId().equals("credentials")) {
            if(credentialsTemp.containsKey(event.getInteraction().getUser().getIdLong())) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        credentialsTemp.get(event.getInteraction().getUser().getIdLong()));
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
                credentialsTemp.remove(event.getInteraction().getUser().getIdLong());
                return;
            }
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Can't load credentials. You can reset your password at the panel login page with your entered email.");
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
        }
    }

}
