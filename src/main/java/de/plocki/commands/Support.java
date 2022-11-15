package de.plocki.commands;

import de.plocki.Main;
import de.plocki.ai.SupportAI;
import de.plocki.util.Hooks;
import de.plocki.util.SupportManager;
import de.plocki.util.TicketInformation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Support extends ListenerAdapter {

    public final static HashMap<Long, Boolean> s = new HashMap<>();
    public final static HashMap<Long, Boolean> answering = new HashMap<>();

    public final static HashMap<Long, Long> helper = new HashMap<>();
    public final static HashMap<Long, Boolean> sent = new HashMap<>();

    //@todo -> modal & right questions

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("support")) {
            CreateAccount.waitingEmail.remove(event.getInteraction().getUser().getIdLong());
            RequestServer.usage.remove(event.getInteraction().getUser().getIdLong());
            if(event.getChannel().getType().isGuild()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "This command is only available in direct messages. I'll send you a message.");
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
                event.getInteraction().getUser().openPrivateChannel().queue(privateChannel -> {

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription(
                            "Creating ticket...\n" +
                                    "Please write now the reason why you're contacting us today.\nPlease use only keywords.");
                    privateChannel.sendMessageEmbeds(b.build())
                            .queue();
                    s.put(event.getInteraction().getUser().getIdLong(), true);
                });
                return;
            }
            if(event.getChannel().getType().isMessage()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Creating ticket...");
                event.getChannel().sendMessageEmbeds(builder.build())
                        .queue();
                event.getChannel().sendMessage("Creating ticket...").queue();

                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.cyan);
                b.setAuthor("ELIZON.");
                b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                b.setDescription(
                        "Please write now the reason why you're contacting us today.\nPlease use only keywords.");
                event.replyEmbeds(b.build())
                        .setEphemeral(true)
                        .queue();
                s.put(event.getInteraction().getUser().getIdLong(), true);
            }
        } else if(event.getName().equalsIgnoreCase("answer")) {
            if(event.getChannel().getType().isGuild()) {

                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Please write now your answer in your direct messages.");
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
                event.getInteraction().getUser().openPrivateChannel().queue(privateChannel -> {

                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription(
                            "I'm here!");
                    privateChannel.sendMessageEmbeds(b.build())
                            .queue();
                    answering.put(event.getInteraction().getUser().getIdLong(), true);
                });
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Please write now your answer.");
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
                answering.put(event.getInteraction().getUser().getIdLong(), true);
            }
        }
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        if(event.getComponent().getId().equals("supportSelection")) {
            if(event.getChannel().getName().contains("archived-")) return;
            if(event.getValues().get(0).contains("close_")) {
                if(event.getValues().get(0).startsWith("inactivity_")) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "Ticket closed because of inactivity.");
                    event.replyEmbeds(builder.build())
                            .queue();
                    new SupportManager().closeTicket(Long.parseLong(event.getValues().get(0).replaceAll("close_", "")), true);
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "Ticket closed.");
                    event.replyEmbeds(builder.build())
                            .queue();
                    new SupportManager().closeTicket(Long.parseLong(event.getValues().get(0).replaceAll("close_", "")), false);
                }
            } else if(event.getValues().get(0).contains("transfer_")) {
                if(!(helper.get(event.getChannel().getIdLong()) == event.getInteraction().getUser().getIdLong())) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "This isn't your ticket.");
                    event.replyEmbeds(builder.build())
                            .setEphemeral(true)
                            .queue();
                    return;
                }
                helper.remove(event.getChannel().getIdLong());
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Ticket is now being transferred.");
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
                new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Ticket is now being transferred.");
            } else if(event.getValues().get(0).contains("claim_")) {
                if(helper.containsKey(event.getChannel().getIdLong())) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "This ticket is already claimed.");
                    event.replyEmbeds(builder.build())
                            .setEphemeral(true)
                            .queue();
                    return;
                }
                helper.put(event.getChannel().getIdLong(), event.getInteraction().getUser().getIdLong());
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Ticket was claimed by " + Objects.requireNonNull(event.getInteraction().getMember()).getNickname() + ".");
                event.replyEmbeds(builder.build())
                        .queue();
                new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "You will now be helped by " + Objects.requireNonNull(event.getInteraction().getMember()).getNickname());
            } else if(event.getValues().get(0).equals("welcome_message")) {
                if(sent.containsKey(event.getChannel().getIdLong())) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "Message already sent.");
                    event.replyEmbeds(builder.build())
                            .setEphemeral(true)
                            .queue();
                    return;
                }
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Sent welcome message.");
                event.replyEmbeds(builder.build())
                        .queue();
                new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Welcome to the ELIZON. support. Do you want to be supported in english or german? You can answer at any time with /answer.");
            } else if(event.getValues().get(0).equals("request_verify")) {
                if(!(helper.get(event.getChannel().getIdLong()) == event.getInteraction().getUser().getIdLong())) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "This isn't your ticket.");
                    event.replyEmbeds(builder.build())
                            .setEphemeral(true)
                            .queue();
                    return;
                }
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Requested verification code.");
                event.replyEmbeds(builder.build())
                        .queue();
                new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "We need to identify your identity to make some account changes.\nPlease request a verification code with /verifycode by using your email in the Modal.");
            } else if(event.getValues().get(0).equals("check_verify")) {
                if(!(helper.get(event.getChannel().getIdLong()) == event.getInteraction().getUser().getIdLong())) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "This isn't your ticket.");
                    event.replyEmbeds(builder.build())
                            .setEphemeral(true)
                            .queue();
                    return;
                }
                TextInput input = TextInput.create("verifyCode", "Identification", TextInputStyle.SHORT).build();
                Modal modal = Modal.create("verificationIdent", "Identification")
                        .addActionRow(input)
                        .build();
                event.replyModal(modal).queue();
                new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Please wait. We're currently verifying your identity.");
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().equals("verificationIdent")) {
            String id = event.getValue("verifyCode").getAsString();
            if(VerifyCode.ident.get(id).equals(Long.parseLong(event.getChannel().getName()))) {
                if(VerifyCode.codes.get(id)) {
                    VerifyCode.codes.remove(id);
                    VerifyCode.ident.remove(id);
                    event.reply("Identity verified. You can now proceed.").queue();
                    new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Your identity has been verified.");
                } else {
                    VerifyCode.codes.remove(id);
                    VerifyCode.ident.remove(id);
                    event.reply("Identity couldn't be verified. Please don't take any actions to the hosting account.\nThis procedure is only allowed up to three times.\nTransfer the ticket to the administration, if needed.").queue();
                    new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Your identity couldn't be verified.");
                }
            } else {
                event.reply("Verification Code doesn't fit to the user.").queue();
                new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Your identity couldn't be verified.");
            }
        }
    }

    @Override()
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getChannel().getName().contains("archived-")) return;
        if(event.getAuthor().getIdLong() == Main.jda.getSelfUser().getIdLong()) return;
        if(!event.getChannel().getType().isGuild()) {
            if(s.containsKey(event.getAuthor().getIdLong())) {
                s.remove(event.getAuthor().getIdLong());
                long id = event.getAuthor().getIdLong();
                boolean bool = new SupportManager().hasPrioritySupport(id);
                String msg = event.getMessage().getContentDisplay();
                String ai = null;
                try {
                    ai = new SupportAI().find(msg).name().toUpperCase();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    String finalAi = ai;
                    new SupportManager().createSupportTicket(new TicketInformation() {
                        @Override
                        public long getUserID() {
                            return id;
                        }

                        @Override
                        public boolean hasPrio() {
                            return bool;
                        }

                        @Override
                        public String getHelpReason() {
                            return msg;
                        }

                        @Override
                        public String aiCategorisation() {
                            return finalAi;
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                event.getChannel().sendMessage("Ticket has been submitted.").queue();
                event.getChannel().sendMessage("Type /answer to answer.").queue();
            } else if(answering.containsKey(event.getAuthor().getIdLong())) {
                answering.remove(event.getAuthor().getIdLong());
                event.getMessage().addReaction(Emoji.fromUnicode("➡")).queue();
                if(!new SupportManager().sendMessageToSupport(event.getAuthor().getIdLong(), event.getMessage().getContentRaw())) event.getChannel().sendMessage("There is no ticket to reply.").queue();
            }
        } else if(event.getChannel().getType().isGuild()) {
            if(Objects.requireNonNull(event.getMessage().getChannel().asTextChannel().getParentCategory()).getIdLong() == Long.parseLong(new Hooks().fromFile("vultronTicketCategoryID"))) {
                new SupportManager().sendMessageToCustomer(event.getMessage().getChannel().getIdLong(), event.getMessage().getContentRaw());
                event.getMessage().addReaction(Emoji.fromUnicode("➡")).queue();
            }
        }
    }
}
