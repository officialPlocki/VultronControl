package de.plocki.commands;

import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.EnvironmentValue;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationAllocation;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.managers.ServerCreationAction;
import de.plocki.Main;
import de.plocki.util.AccountManager;
import de.plocki.util.Hooks;
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
        if(event.getName().equalsIgnoreCase("requestserver")) {
            TextInput usage = TextInput.create("server_usage", "Usage", TextInputStyle.PARAGRAPH)
                    .setMinLength(20)
                    .setPlaceholder("Describe for what you want to use the server.")
                    .setRequired(true)
                    .setMaxLength(300)
                    .build();
            TextInput subdomain = TextInput.create("server_subdomain", "Subdomain", TextInputStyle.PARAGRAPH)
                    .setMinLength(4)
                    .setMaxLength(12)
                    .setPlaceholder("Wish for a subdomain with the vultron.network ending, write \"none\" for none.")
                    .setRequired(true)
                    .build();
            TextInput accept = TextInput.create("terms_accept", "LEGAL", TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setMinLength(3)
                    .setMaxLength(3)
                    .setPlaceholder("Do you accept the Privacy Policy and Terms of Use (/legal)?\nType \"YES\" to accept.")
                    .build();
            Modal modal = Modal.create("requestServer", "Server Request")
                    .addActionRows(ActionRow.of(usage), ActionRow.of(subdomain), ActionRow.of(accept)).build();
            if(!new AccountManager().hasAccount(event.getInteraction().getUser().getIdLong())) {
                event.reply("You must create a hosting account first.").setEphemeral(true).queue();
            } else {
                event.replyModal(modal).queue();
            }
        }
    }

    public static final HashMap<Long, Boolean> usage = new HashMap<>();

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().equals("requestServer")) {
            if(event.getValue("terms_accept").getAsString().equals("YES")) {
                AtomicBoolean b = new AtomicBoolean(false);

                if(!event.getValue("server_subdomain").getAsString().equalsIgnoreCase("none")) {
                    FileBuilder builder = new FileBuilder("servers");
                    List<String> list;
                    if(builder.getYaml().isSet("domains")) {
                        list = builder.getYaml().getStringList("domains");
                    } else {
                        list = new ArrayList<>();
                    }
                    list.forEach(s -> {
                        if(s.equalsIgnoreCase(event.getValue("server_subdomain").getAsString())) b.set(true);
                    });
                    if(b.get()) {
                        event.reply("Subdomain is currently not available.\n\nYour usage description:\n```text\n" + event.getValue("server_usage").getAsString() + "\n```").setEphemeral(true).queue();
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
                event.reply("Your request has been submitted with the ID #" + uuid + "\nA copy of this message will be sent to you over DM.").setEphemeral(true).queue();
                event.getInteraction().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Your request has been submitted with the ID #" + uuid).queue();
                });
            } else {
                event.reply("Please write explicitly \"YES\" in uppercase to accept!\nLegal: https://vultronstudios.net/legal").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
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
            action.setName(embed.getFooter().getText());
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

            long id = action.execute().getIdLong();

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
                builder.getYaml().set(String.valueOf(id), embed.getFields().get(3).getValue());
                builder.save();
                ApplicationServer server = new Hooks().getPteroApplication().retrieveServersByName(embed.getFooter().getText(), true).execute().get(0);
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
                privateChannel.sendMessage("Your request with the ID " + embed.getFooter().getText() + " has been answered.\nThe request has been accepted and a server fitting to your request is being installed.\nYou should receive a E-Mail, when the server is ready.").queue();
            });

            event.getChannel().sendMessage("Answer has been transmitted.").queue();
            event.getMessage().delete();
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
            action.setName(embed.getFooter().getText());
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

            long id = action.execute().getIdLong();

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
                builder.getYaml().set(String.valueOf(id), embed.getFields().get(3).getValue());
                builder.save();
                ApplicationServer server = new Hooks().getPteroApplication().retrieveServersByName(embed.getFooter().getText(), true).execute().get(0);
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
                privateChannel.sendMessage("Your request with the ID " + embed.getFooter().getText() + " has been answered.\nThe request has been accepted and a server fitting to your request is being installed.\nYou should receive a E-Mail, when the server is ready.").queue();
            });

            event.getChannel().sendMessage("Answer has been transmitted.").queue();
            event.getMessage().delete();
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
            action.setName(embed.getFooter().getText());
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

            String id = action.execute().getDetailManager().execute().getName();

            if(!Objects.requireNonNull(embed.getFields().get(3).getValue()).equalsIgnoreCase("none")) {
                FileBuilder builder = new FileBuilder("servers");
                List<String> list;
                if(builder.getYaml().isSet("domains")) {
                    list = builder.getYaml().getStringList("domains");
                } else {
                    list = new ArrayList<>();
                }
                String i;
                ApplicationServer server = new Hooks().getPteroApplication().retrieveServersByName(embed.getFooter().getText(), true).execute().get(0);
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
                privateChannel.sendMessage("Your request with the ID " + embed.getFooter().getText() + " has been answered.\nThe request has been accepted and a server fitting to your request is being installed.\nYou should receive a E-Mail, when the server is ready.").queue();
            });

            event.getChannel().sendMessage("Answer has been transmitted.").queue();
            event.getMessage().delete();
        } else if(event.getValues().get(0).equals("insufficient")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(embed.getAuthor().getName())).complete();

            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("Your request with the ID " + embed.getFooter().getText() + " has been answered.\nThe request has been declined because of insufficient descriptions about the usage.\nYou can request a server again at every time.").queue();
            });

            event.getChannel().sendMessage("Answer has been transmitted.").queue();
            event.getMessage().delete();
        } else if(event.getValues().get(0).equals("declined")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();

            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("Your request with the ID " + embed.getFooter().getText() + " has been answered.\nThe request has been declined.\nTo get a higher chance of being accepted, please wait two or more days.").queue();
            });

            event.getChannel().sendMessage("Answer has been transmitted.").queue();
            event.getMessage().delete();
        } else if(event.getValues().get(0).equals("amount")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();

            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("Your request with the ID " + embed.getFooter().getText() + " has been answered.\nThe request has been declined.\nYou've currently to many servers or currently are no servers available.").queue();
            });

            event.getChannel().sendMessage("Answer has been transmitted.").queue();
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                event.getMessage().delete();
            }).start();
        } else if(event.getValues().get(0).equals("custom")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();

            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("Your request with the ID " + embed.getFooter().getText() + " has been answered.\n\nWe liked your request.\nWe would like to have a short conversation with you to clarify everything else with you.\nMaybe we are also ready for a partnership with you :)\n#dedicatedipincoming").queue();
            });

            event.getChannel().sendMessage("Answer has been transmitted.").queue();
            event.getMessage().delete();
        } else if(event.getValues().get(0).equals("subdomain")) {
            MessageEmbed embed = event.getMessage().getEmbeds().get(0);
            event.getChannel().sendMessage("Transmitting answer to request " + Objects.requireNonNull(embed.getFooter()).getText() + "...").queue();
            User user = Main.jda.retrieveUserById(Objects.requireNonNull(Objects.requireNonNull(embed.getAuthor()).getName())).complete();

            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("Your request with the ID " + embed.getFooter().getText() + " has been answered.\n\nYour subdomain is currently not available, not desired as it may be inappropriate or offensive.").queue();
            });

            event.getChannel().sendMessage("Answer has been transmitted.").queue();
            event.getMessage().delete();
        }
    }
}
