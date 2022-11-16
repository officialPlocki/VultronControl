package de.plocki.commands;

import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.EnvironmentValue;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationAllocation;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.managers.ServerCreationAction;
import de.plocki.Main;
import de.plocki.util.AccountManager;
import de.plocki.util.Hooks;
import de.plocki.util.LanguageUtil;
import de.plocki.util.files.FileBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestServer extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        LanguageUtil util = new LanguageUtil();
        LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
        if(event.getName().equalsIgnoreCase("requestserver")) {
            TextInput usage = TextInput.create("server_usage", util.getString("req_usage", lang), TextInputStyle.PARAGRAPH)
                    .setMinLength(20)
                    .setPlaceholder(util.getString("req_usage_desc", lang))
                    .setRequired(true)
                    .setMaxLength(300)
                    .build();
            TextInput subdomain = TextInput.create("server_subdomain", "Subdomain", TextInputStyle.PARAGRAPH)
                    .setMinLength(4)
                    .setMaxLength(16)
                    .setPlaceholder(util.getString("req_subdomain_desc", lang))
                    .setRequired(true)
                    .build();
            TextInput accept = TextInput.create("terms_accept", util.getString("req_legal", lang), TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setMinLength(3)
                    .setMaxLength(3)
                    .setPlaceholder(util.getString("req_legal_desc", lang))
                    .build();
            Modal modal = Modal.create("requestServer", "Server")
                    .addActionRows(ActionRow.of(usage), ActionRow.of(subdomain), ActionRow.of(accept)).build();
            if(!new AccountManager().hasAccount(event.getInteraction().getUser().getIdLong())) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);

                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("accdel_notreg", lang));
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
            } else {
                event.replyModal(modal).queue();
            }
        }
    }

    public static final HashMap<Long, Boolean> usage = new HashMap<>();

    public RequestServer() {
        LanguageUtil util = new LanguageUtil();

        util.setString("Describe for what you want to use the server.", "req_usage_desc", LanguageUtil.lang.EN);
        util.setString("Beschreibe, für was du den Server nutzen möchtest.", "req_usage_desc", LanguageUtil.lang.DE);
        util.setString("Usage", "req_usage", LanguageUtil.lang.EN);
        util.setString("Nutzung", "req_usage", LanguageUtil.lang.DE);
        util.setString("Wish for a subdomain with the vultron.network ending, write \"none\" for none.", "req_subdomain_desc", LanguageUtil.lang.EN);
        util.setString("Wunsch für eine vultron.network subdomain, schreibe \"none\" für keine.", "req_subdomain_desc", LanguageUtil.lang.EN);
        util.setString("LEGAL", "req_legal", LanguageUtil.lang.EN);
        util.setString("RECHTLICHES", "req_legal", LanguageUtil.lang.DE);
        util.setString("Subdomain is currently not available.", "req_answ_subdom_av", LanguageUtil.lang.EN);
        util.setString("Die Subdomain ist aktuell nicht verfügbar.", "req_answ_subdom_av", LanguageUtil.lang.DE);
        util.setString("Your request has been submitted with the ID #", "req_answ_req_submit", LanguageUtil.lang.EN);
        util.setString("Deine Anfrage wurde unter der folgender ID gesendet: #", "req_answ_req_submit", LanguageUtil.lang.DE);
        util.setString("Please write explicitly \"YES\" in uppercase to accept!\n" +
                "You can read the conditions with /legal.", "req_legal_f", LanguageUtil.lang.EN);
        util.setString("Bitte schreibe explizit \"YES\" in Großbuchstaben zum akzeptieren!\n" +
                "Du kannst die Konditionen mit /legal lesen.", "req_legal_f", LanguageUtil.lang.DE);
        util.setString("Your request with the ID #%ID% has been answered.\n" +
                        "The request has been accepted and a server fitting to your request is being installed.",
                "req_answ_install", LanguageUtil.lang.EN);
        util.setString("Deine Anfrage mit der ID #%ID% wurde beantwortet.\n" +
                        "Deine Anfrage wurde akzeptiert und ein passender Server wird gerade installiert.",
                "req_answ_install", LanguageUtil.lang.DE);
        util.setString("Do you accept the in /legal shown conditions?\nType \"YES\" to accept.", "req_legal_desc", LanguageUtil.lang.EN);
        util.setString("Akzeptierst du die in /legal angegebenen Bedingungen?\nSchreibe \"YES\" zum akzeptieren.", "req_legal_desc", LanguageUtil.lang.DE);
        util.setString("Your request with the ID %ID% has been answered.\n" +
                        "The request has been declined because of insufficient descriptions about the usage.",
                "req_answ_insuf", LanguageUtil.lang.EN);
        util.setString("Deine Anfrage mit der ID #%ID% wurde beantwortet.\n" +
                        "Deine beschriebene Nutzung war leider nicht ausreichend formuliert.",
                "req_answ_insuf", LanguageUtil.lang.DE);
        util.setString("Deine Anfrage mit der ID #%ID% wurde beantwortet.\n" +
                        "Deine Anfrage wurde leider abgelehnt.\n" +
                        "Du kannst es gerne noch einmal versuchen.",
                "req_answ_dec", LanguageUtil.lang.DE);
        util.setString("Your request with the ID #%ID% was answered.\n" +
                        "Your request has been declined.\n" +
                        "You can try it again, if you want.",
                "req_answ_dec", LanguageUtil.lang.EN);
        util.setString("Your request with the ID #%ID% has been answered.\n" +
                        "\n" +
                        "We liked your request.\n" +
                        "We would like to have a short conversation with you to clarify everything else with you.\n" +
                        "Please open a ticket with /support with the ID from above.",
                "req_answ_custom", LanguageUtil.lang.EN);
        util.setString("Deine Anfrage mit der ID #%ID% wurde beantwortet.\n" +
                        "\n" +
                        "Wir mögen deine Anfrage.\n" +
                        "Wir würden uns gerne mit dir über dein Vorhaben unterhalten und mit dir alles nähere klären.\n" +
                        "Bitte öffne ein Ticket mit /support und benenne dort die ID von der Anfrage.",
                "req_answ_custom", LanguageUtil.lang.DE);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        LanguageUtil util = new LanguageUtil();
        LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
        if(event.getModalId().equals("requestServer")) {
            if(Objects.requireNonNull(event.getValue("terms_accept")).getAsString().equals("YES")) {
                AtomicBoolean c = new AtomicBoolean(false);

                if(!Objects.requireNonNull(event.getValue("server_subdomain")).getAsString().equalsIgnoreCase("none")) {
                    FileBuilder builder = new FileBuilder("servers");
                    List<String> list;
                    if(builder.getYaml().isSet("domains")) {
                        list = builder.getYaml().getStringList("domains");
                    } else {
                        list = new ArrayList<>();
                    }
                    list.forEach(s -> {
                        if(s.equalsIgnoreCase(Objects.requireNonNull(event.getValue("server_subdomain")).getAsString())) c.set(true);
                    });
                    if(c.get()) {
                        EmbedBuilder b = new EmbedBuilder();
                        b.setColor(Color.cyan);
                        b.setAuthor("ELIZON.");
                        b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                        b.setDescription(
                                util.getString("req_answ_subdom_a", lang) + "\n" +
                                        "\n" +
                                        "\n" +
                                        Objects.requireNonNull(event.getValue("server_usage")).getAsString() + "\n"
                                        );
                        event.replyEmbeds(b.build())
                                .setEphemeral(true)
                                .queue();
                        return;
                    }
                }
                String uuid = UUID.randomUUID().toString();
                Guild guild = Main.jda.getGuildById(new Hooks().fromFile("vultronGuildID"));
                assert guild != null;
                TextChannel channel = guild.getTextChannelById(new Hooks().fromFile("vultronGuildChannelID"));
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Server Request");
                builder.setColor(Color.BLACK);
                builder.addField("Discord", event.getInteraction().getUser().getAsTag() + " - " + event.getInteraction().getUser().getIdLong(), true);
                builder.addField("Usage", event.getValue("server_usage").getAsString(), true);
                builder.addField("Servers", new Hooks().getPteroApplication().retrieveUsersByUsername(event.getInteraction().getUser().getIdLong() + "", true).execute().get(0).retrieveServers().execute().size() + " Servers", true);
                builder.addField("Subdomain", event.getValue("server_subdomain").getAsString(), true);
                builder.setAuthor(event.getInteraction().getUser().getId());
                builder.setFooter(uuid);
                assert channel != null;
                channel.sendMessageEmbeds(builder.build()).addActionRow(SelectMenu.create("chooseAnswer")
                        .setPlaceholder("Click to choose")
                        .addOption("Low (Accept)", "server_low")
                        .addOption("Middle (Accept)","server_mid")
                        .addOption("High (Accept)","server_high")
                        .addOption("Insufficient (Decline)","insufficient")
                        .addOption("Declined (Decline)","declined")
                        .addOption("Too many (Decline)","amount")
                        .addOption("Custom (Accept, ticket)","custom")
                        .addOption("Subdomain (Decline)","subdomain")
                        .build()).queue();
                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.cyan);
                b.setAuthor("ELIZON.");
                b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                b.setDescription(util.getString("req_answ_req_submit", lang) + uuid);
                event.replyEmbeds(b.build())
                        .setEphemeral(true)
                        .queue();
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("req_legal_f", lang));
                event.replyEmbeds(builder.build())
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
        LanguageUtil util = new LanguageUtil();
        LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
        if(!Objects.equals(event.getComponent().getId(), "chooseAnswer")) return;
        if(event.getValues().get(0).equals("server_low")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();

            ServerCreationAction action = new Hooks().getPteroApplication().createServer();
            action.setCPU(80);
            action.setDatabases(0);
            action.setBackups(1);
            action.setDedicatedIP(false);
            action.setDescription("Usage: " + embed.getFields().get(1).getValue());
            action.setDisk(1, DataType.GB);
            action.setAllocations(1);
            action.setMemory(1536, DataType.MB);
            if(!Objects.requireNonNull(embed.getFields().get(3).getValue()).equalsIgnoreCase("none")) {
                action.setName(embed.getFields().get(3).getValue() + ".vultron.network");
            } else {
                action.setName(embed.getFooter().getText());
            }
            action.setOwner(new Hooks().getPteroApplication().retrieveUsersByUsername(user.getIdLong() + "", true).execute().get(0));
            action.setSwap(128, DataType.MB);

            //@todo EDIT

            HashMap<String, EnvironmentValue<?>> map = new HashMap<>();

            map.put("MINECRAFT_VERSION", EnvironmentValue.of("1.8.8"));
            map.put("SERVER_JARFILE", EnvironmentValue.of("server.jar"));
            map.put("DL_PATH", EnvironmentValue.of(""));
            map.put("BUILD_NUMBER", EnvironmentValue.of("latest"));

            action.setEnvironment(map);
            action.setLocation(new Hooks().getPteroApplication().retrieveLocationById(1).execute());
            action.setDockerImage("ghcr.io/pterodactyl/yolks:java_8");
            action.setEgg(new Hooks().getPteroApplication().retrieveEggById(new Hooks().getPteroApplication().retrieveNestById(1).execute(), 2).execute());
            action.setStartupCommand("java -Xms128M -XX:MaxRAMPercentage=95.0 -Dterminal.jline=false -Dterminal.ansi=true -jar {{SERVER_JARFILE}}");

            String id = action.execute().getIdentifier();

            if(!Objects.requireNonNull(embed.getFields().get(3).getValue()).equalsIgnoreCase("none")) {
                FileBuilder builder = new FileBuilder("servers");
                List<String> list;
                if(builder.getYaml().isSet("domains")) {
                    list = builder.getYaml().getStringList("domains");
                } else {
                    list = new ArrayList<>();
                }
                list.add(embed.getFields().get(3).getValue());
                builder.getYaml().set("domains", list);
                builder.getYaml().set(id, embed.getFields().get(3).getValue());
                builder.save();
                ApplicationServer server = new Hooks().getServerByID(id);
                ApplicationAllocation allocation = new Hooks().getPteroApplication().retrieveAllocationById(server.getDefaultAllocationIdLong()).execute();
                String alias = allocation.getAlias();
                int port = allocation.getPortInt();
                try {
                    new Hooks().createSubdomain(embed.getFields().get(3).getValue(), port, alias);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            user.openPrivateChannel().queue(privateChannel -> {

                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);

                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));

                builder.setDescription(util.getString("req_answ_install", lang)
                        .replaceAll("%ID%", Objects.requireNonNull(embed.getFooter().getText())));
                privateChannel.sendMessageEmbeds(builder.build())
                        .queue();
            });

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Answer has been transmitted.");
            event.getChannel().sendMessageEmbeds(builder.build())
                    .queue();
            event.getMessage().delete().queue();
        } else if(event.getValues().get(0).equals("server_mid")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();


            ServerCreationAction action = new Hooks().getPteroApplication().createServer();
            action.setCPU(150);
            action.setDatabases(0);
            action.setBackups(1);
            action.setDedicatedIP(false);
            action.setDescription("Usage: " + embed.getFields().get(1).getValue());
            action.setDisk(1536, DataType.MB);
            action.setAllocations(1);
            action.setMemory(2048, DataType.MB);
            if(!Objects.requireNonNull(embed.getFields().get(3).getValue()).equalsIgnoreCase("none")) {
                action.setName(embed.getFields().get(3).getValue() + ".vultron.network");
            } else {
                action.setName(embed.getFooter().getText());
            }
            action.setOwner(new Hooks().getPteroApplication().retrieveUsersByUsername(user.getIdLong() + "", true).execute().get(0));
            action.setSwap(128, DataType.MB);

            //@todo EDIT

            HashMap<String, EnvironmentValue<?>> map = new HashMap<>();

            map.put("MINECRAFT_VERSION", EnvironmentValue.of("1.8.8"));
            map.put("SERVER_JARFILE", EnvironmentValue.of("server.jar"));
            map.put("DL_PATH", EnvironmentValue.of(""));
            map.put("BUILD_NUMBER", EnvironmentValue.of("latest"));

            action.setEnvironment(map);
            action.setLocation(new Hooks().getPteroApplication().retrieveLocationById(1).execute());
            action.setDockerImage("ghcr.io/pterodactyl/yolks:java_8");
            action.setEgg(new Hooks().getPteroApplication().retrieveEggById(new Hooks().getPteroApplication().retrieveNestById(1).execute(), 2).execute());
            action.setStartupCommand("java -Xms128M -XX:MaxRAMPercentage=95.0 -Dterminal.jline=false -Dterminal.ansi=true -jar {{SERVER_JARFILE}}");

            String id = action.execute().getIdentifier();

            if(!Objects.requireNonNull(embed.getFields().get(3).getValue()).equalsIgnoreCase("none")) {
                FileBuilder builder = new FileBuilder("servers");
                List<String> list;
                if(builder.getYaml().isSet("domains")) {
                    list = builder.getYaml().getStringList("domains");
                } else {
                    list = new ArrayList<>();
                }
                list.add(embed.getFields().get(3).getValue());
                builder.getYaml().set("domains", list);
                builder.getYaml().set(id, embed.getFields().get(3).getValue());
                builder.save();
                ApplicationServer server = new Hooks().getServerByID(id);
                ApplicationAllocation allocation = new Hooks().getPteroApplication().retrieveAllocationById(server.getDefaultAllocationIdLong()).execute();
                String alias = allocation.getAlias();
                int port = allocation.getPortInt();
                try {
                    new Hooks().createSubdomain(embed.getFields().get(3).getValue(), port, alias);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            user.openPrivateChannel().queue(privateChannel -> {

                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);

                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("req_answ_install", lang)
                        .replaceAll("%ID%", Objects.requireNonNull(embed.getFooter().getText())));
                privateChannel.sendMessageEmbeds(builder.build())
                        .queue();
            });

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Answer has been transmitted.");
            event.getChannel().sendMessageEmbeds(builder.build())
                    .queue();
            event.getMessage().delete().queue();
        } else if(event.getValues().get(0).equals("server_high")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();


            ServerCreationAction action = new Hooks().getPteroApplication().createServer();
            action.setCPU(200);
            action.setDatabases(0);
            action.setBackups(1);
            action.setDedicatedIP(false);
            action.setDescription("Usage: " + embed.getFields().get(1).getValue());
            action.setDisk(2048, DataType.MB);
            action.setAllocations(1);
            action.setMemory(2560, DataType.MB);
            if(!Objects.requireNonNull(embed.getFields().get(3).getValue()).equalsIgnoreCase("none")) {
                action.setName(embed.getFields().get(3).getValue() + ".vultron.network");
            } else {
                action.setName(embed.getFooter().getText());
            }
            action.setOwner(new Hooks().getPteroApplication().retrieveUsersByUsername(user.getIdLong() + "", true).execute().get(0));
            action.setSwap(128, DataType.MB);

            //@todo EDIT

            HashMap<String, EnvironmentValue<?>> map = new HashMap<>();

            map.put("MINECRAFT_VERSION", EnvironmentValue.of("1.8.8"));
            map.put("SERVER_JARFILE", EnvironmentValue.of("server.jar"));
            map.put("DL_PATH", EnvironmentValue.of(""));
            map.put("BUILD_NUMBER", EnvironmentValue.of("latest"));

            action.setEnvironment(map);
            action.setLocation(new Hooks().getPteroApplication().retrieveLocationById(1).execute());
            action.setDockerImage("ghcr.io/pterodactyl/yolks:java_8");
            action.setEgg(new Hooks().getPteroApplication().retrieveEggById(new Hooks().getPteroApplication().retrieveNestById(1).execute(), 2).execute());
            action.setStartupCommand("java -Xms128M -XX:MaxRAMPercentage=95.0 -Dterminal.jline=false -Dterminal.ansi=true -jar {{SERVER_JARFILE}}");

            String id = action.execute().getIdentifier();

            if(!Objects.requireNonNull(embed.getFields().get(3).getValue()).equalsIgnoreCase("none")) {
                FileBuilder builder = new FileBuilder("servers");
                List<String> list;
                if(builder.getYaml().isSet("domains")) {
                    list = builder.getYaml().getStringList("domains");
                } else {
                    list = new ArrayList<>();
                }
                String i;
                ApplicationServer server = new Hooks().getServerByID(id);
                ApplicationAllocation allocation = new Hooks().getPteroApplication().retrieveAllocationById(server.getDefaultAllocationIdLong()).execute();
                String alias = allocation.getAlias();
                int port = allocation.getPortInt();
                try {
                    i = new Hooks().createSubdomain(embed.getFields().get(3).getValue(), port, alias);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                list.add(embed.getFields().get(3).getValue());
                builder.getYaml().set("domains", list);
                builder.getYaml().set(id, embed.getFields().get(3).getValue());
                builder.getYaml().set(embed.getFields().get(3).getValue(), i);
                builder.save();
            }

            user.openPrivateChannel().queue(privateChannel -> {

                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);

                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("req_answ_install", lang)
                        .replaceAll("%ID%", Objects.requireNonNull(embed.getFooter().getText())));
                privateChannel.sendMessageEmbeds(builder.build())
                        .queue();
            });

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Answer has been transmitted.");
            event.getChannel().sendMessageEmbeds(builder.build())
                    .queue();
            event.getMessage().delete().queue();
        } else if(event.getValues().get(0).equals("insufficient")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();


            user.openPrivateChannel().queue(privateChannel -> {

                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);

                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("req_answ_insuf", lang)
                        .replaceAll("%ID%", Objects.requireNonNull(embed.getFooter().getText())));
                privateChannel.sendMessageEmbeds(builder.build())
                        .queue();
            });

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Answer has been transmitted.");
            event.getChannel().sendMessageEmbeds(builder.build())
                    .queue();
            event.getMessage().delete().queue();
        } else if(event.getValues().get(0).equals("declined")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();

            user.openPrivateChannel().queue(privateChannel -> {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);

                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("req_answ_dec", lang)
                        .replaceAll("%ID%", Objects.requireNonNull(embed.getFooter().getText())));
                privateChannel.sendMessageEmbeds(builder.build())
                        .queue();
            });

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Answer has been transmitted.");
            event.getChannel().sendMessageEmbeds(builder.build())
                    .queue();
            event.getMessage().delete().queue();
        } else if(event.getValues().get(0).equals("amount")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();

            user.openPrivateChannel().queue(privateChannel -> {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);

                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("req_answ_dec", lang)
                        .replaceAll("%ID%", Objects.requireNonNull(embed.getFooter().getText())));
                privateChannel.sendMessageEmbeds(builder.build())
                        .queue();
            });

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Answer has been transmitted.");
            event.getChannel().sendMessageEmbeds(builder.build())
                    .queue();
            event.getMessage().delete().queue();
        } else if(event.getValues().get(0).equals("custom")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "... (Custom)").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();

            user.openPrivateChannel().queue(privateChannel -> {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("req_answ_custom", lang).replaceAll("%ID%", Objects.requireNonNull(embed.getFooter().getText())));
                privateChannel.sendMessageEmbeds(builder.build())
                        .queue();
            });

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Answer has been transmitted.");
            event.getChannel().sendMessageEmbeds(builder.build())
                    .queue();
            event.getMessage().delete().queue();
        } else if(event.getValues().get(0).equals("subdomain")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();

            user.openPrivateChannel().queue(privateChannel -> {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);

                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("req_answ_dec", lang)
                        .replaceAll("%ID%", Objects.requireNonNull(embed.getFooter().getText())));
                privateChannel.sendMessageEmbeds(builder.build())
                        .queue();
            });

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(
                    "Answer has been transmitted.");
            event.getChannel().sendMessageEmbeds(builder.build())
                    .queue();
            event.getMessage().delete().queue();
        }
    }

}
