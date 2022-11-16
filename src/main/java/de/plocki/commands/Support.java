package de.plocki.commands;

import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationUser;
import de.plocki.Main;
import de.plocki.ai.SupportAI;
import de.plocki.util.Hooks;
import de.plocki.util.LanguageUtil;
import de.plocki.util.SupportManager;
import de.plocki.util.TicketInformation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
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
    public static final HashMap<Long, String> servers = new HashMap<>();

    //@todo -> modal & account management

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("support")) {
            LanguageUtil util = new LanguageUtil();
            LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
            CreateAccount.waitingEmail.remove(event.getInteraction().getUser().getIdLong());
            RequestServer.usage.remove(event.getInteraction().getUser().getIdLong());
            if(event.getChannel().getType().isGuild()) {

                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("createaccount_only_dm", lang));
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
                event.getInteraction().getUser().openPrivateChannel().queue(privateChannel -> {
                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
    
                b.setAuthor("ELIZON.");
                b.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription(
                            "Creating ticket...\n" +
                                    "Please write now the reason why you're contacting us today.\n" +
                                    "Please use only keywords.");
                    privateChannel.sendMessageEmbeds(b.build())
                            .queue();
                    s.put(event.getInteraction().getUser().getIdLong(), true);
                });
                return;
            }
            if(event.getChannel().getType().isMessage()) {
                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.cyan);

                b.setAuthor("ELIZON.");
                b.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                b.setDescription("Creating ticket...\n" +
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
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                    b.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Sent welcome message.");
                event.replyEmbeds(builder.build())
                        .queue();
                new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Welcome to the ELIZON. support.\n How can we help you today?\nYou can answer at any time with /answer.");
            } else if(event.getValues().get(0).equals("request_verify")) {
                if(!(helper.get(event.getChannel().getIdLong()) == event.getInteraction().getUser().getIdLong())) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
        } else if(event.getComponent().getId().equals("support_acc_verif")) {
            if(event.getValues().get(0).equals("edit_server")) {
                if(!(helper.get(event.getChannel().getIdLong()) == event.getInteraction().getUser().getIdLong())) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "This isn't your ticket.");
                    event.replyEmbeds(builder.build())
                            .setEphemeral(true)
                            .queue();
                    return;
                }
                TextInput servername = TextInput.create("servername", "Server ID", TextInputStyle.SHORT)
                        .setMinLength(1)
                        .setRequired(true)
                        .build();

                Modal modal = Modal.create("editServerModal", "Edit Server")
                        .addActionRow(servername)
                        .build();
                event.replyModal(modal).queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getButton().getId().equals("cpu")) {
            TextInput input = TextInput.create("edit_cpu", "Set CPU (100 = 1T)", TextInputStyle.SHORT)
                    .setMinLength(2)
                    .setMaxLength(3)
                    .setRequired(true)
                    .setPlaceholder("150").build();
            Modal modal = Modal.create("edit_server_cpu", "Edit Server")
                    .addActionRow(input).build();
            event.replyModal(modal)
                    .queue();
        } else if(event.getButton().getId().equals("ram")) {
            TextInput input = TextInput.create("edit_ram", "Set RAM (mb)", TextInputStyle.SHORT)
                    .setMinLength(4)
                    .setMaxLength(4)
                    .setRequired(true)
                    .setPlaceholder("1536").build();
            Modal modal = Modal.create("edit_server_ram", "Edit Server")
                    .addActionRow(input).build();
            event.replyModal(modal)
                    .queue();
        } else if(event.getButton().getId().equals("disk")) {
            TextInput input = TextInput.create("edit_disk", "Set CPU (gb)", TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setMaxLength(2)
                    .setRequired(true)
                    .setPlaceholder("2").build();
            Modal modal = Modal.create("edit_server_disk", "Edit Server")
                    .addActionRow(input).build();
            event.replyModal(modal)
                    .queue();
        } else if(event.getButton().getId().equals("alloc")) {
            ApplicationServer server = new Hooks().getServerByID(servers.get(event.getChannel().getIdLong()));
            server.getBuildManager().setAllowedAllocations(server.getAllocations().get().size() + 1).execute();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Allocation has been added.");
            event.replyEmbeds(builder.build())
                    .queue();

            new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "The allocation limit has been increased.");
        } else if(event.getButton().getId().equals("datab")) {
            ApplicationServer server = new Hooks().getServerByID(servers.get(event.getChannel().getIdLong()));
            server.getBuildManager().setAllowedDatabases(server.retrieveDatabases().execute().size() + 1).execute();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Database has been added.");
            event.replyEmbeds(builder.build())
                    .queue();

            new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "The database limit has been increased.");
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getChannel().getName().startsWith("archived-")) return;
        if(event.getModalId().equals("editServerModal")) {
            ApplicationServer server = new Hooks().getServerByID(Objects.requireNonNull(event.getValue("servername")).getAsString());
            if(new Hooks().getPteroApplication().retrieveUserById(server.getOwnerIdLong()).execute().getUserName().equals(event.getChannel().getName())) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Server Info:\n" +
                        "CPU: " + server.getLimits().getCPU() + "\n" +
                        "RAM: " + server.getLimits().getMemory() + "\n" +
                        "Disk: " + server.getLimits().getDisk() + "\n" +
                        "Alloc.: " + server.getAllocations().get().size() + "\n" +
                        "Datab.: " + server.retrieveDatabases().execute().size() + "\n" +
                        "ID: " + server.getIdentifier());
                event.replyEmbeds(builder.build())
                        .addActionRow(
                                Button.primary("cpu", "Change CPU"),
                                Button.primary("ram", "Change RAM"),
                                Button.primary("disk", "Change Disk"),
                                Button.primary("alloc", "Add Allocation"),
                                Button.primary("datab", "Add Database"))
                        .queue();
                servers.put(event.getChannel().getIdLong(), server.getIdentifier());

                new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Server Information has been requested.");
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Server not found.");
                event.replyEmbeds(builder.build())
                        .queue();
            }
        } else if(event.getModalId().startsWith("edit_server_")) {
            String raw = event.getModalId().replaceAll("edit_server_", "");
            String edit = "edit_" + raw;
            ApplicationServer server = new Hooks().getServerByID(servers.get(event.getChannel().getIdLong()));
            long val = Long.parseLong(Objects.requireNonNull(event.getValue(edit)).getAsString());
            if(raw.equals("cpu")) {
                server.getBuildManager().setCPU(val).execute();

                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "CPU has been updated.");
                event.replyEmbeds(builder.build())
                        .queue();
                new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "CPU has been updated.\nTo apply the changes, please restart the server.");
            } else if(raw.equals("ram")) {
                if(val < 1536) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "RAM can't be less than 1536.");
                    event.replyEmbeds(builder.build())
                            .queue();
                } else {
                    server.getBuildManager().setMemory(val, DataType.MB).execute();
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "RAM has been updated.");
                    event.replyEmbeds(builder.build())
                            .queue();
                    new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "RAM has been updated.\nTo apply the changes, please restart the server.");
                }
            } else if(raw.equals("disk")) {
                if(val == 0) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "Disk can't be 0.");
                    event.replyEmbeds(builder.build())
                            .queue();
                } else {
                    server.getBuildManager().setDisk(val, DataType.GB).execute();

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "Disk has been updated.");
                    event.replyEmbeds(builder.build())
                            .queue();

                    new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Disk has been updated.\nTo apply the changes, please restart the server.");
                }
            }
        } else if(event.getModalId().equals("verificationIdent")) {
            String id = Objects.requireNonNull(event.getValue("verifyCode")).getAsString();
            if(VerifyCode.ident.get(id).equals(Long.parseLong(event.getChannel().getName()))) {
                if(VerifyCode.codes.get(id)) {
                    VerifyCode.codes.remove(id);
                    VerifyCode.ident.remove(id);
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "Identity verified. You can now proceed.");

                    SelectMenu menu = SelectMenu.create("support_acc_verif")
                            .addOption("Edit Server", "edit_server")
                            .build();

                    event.replyEmbeds(builder.build()).addActionRow(menu)
                            .queue();
                    new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Your identity has been verified.");
                } else {
                    VerifyCode.codes.remove(id);
                    VerifyCode.ident.remove(id);
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(
                            "Identity couldn't be verified. Please don't take any actions to the hosting account.\n" +
                                    "This procedure is only allowed up to three times.\n" +
                                    "Transfer the ticket to the administration, if needed.");
                    event.replyEmbeds(builder.build())
                            .queue();
                    new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Your identity couldn't be verified.");
                }
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Verification Code doesn't fit to the user.");
                event.replyEmbeds(builder.build())
                        .queue();
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
                String ai;
                try {
                    ai = new SupportAI().find(msg).name().toUpperCase();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
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
                            return ai;
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(
                        "Ticket has been submitted.\n" +
                                "Type /answer to answer.");
                event.getChannel().sendMessageEmbeds(builder.build())
                        .queue();
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
