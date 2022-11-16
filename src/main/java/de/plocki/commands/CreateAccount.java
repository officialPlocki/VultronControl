package de.plocki.commands;

import de.plocki.util.AccountManager;
import de.plocki.util.Hooks;
import de.plocki.util.LanguageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.HashMap;

public class CreateAccount extends ListenerAdapter {

    public CreateAccount() {
        LanguageUtil util = new LanguageUtil();
        util.setString("This command is only available in Direct Messages. I'll send you a message.", "createaccount_only_dm", LanguageUtil.lang.EN);
        util.setString("Dieser Befehl ist nur in Direktnachrichten verfügbar. Ich sende dir eine Nachricht.", "createaccount_only_dm", LanguageUtil.lang.DE);
        util.setString("Do you want to create a ELIZON. Account?", "createaccount_desc_create", LanguageUtil.lang.EN);
        util.setString("Möchtest du ein ELIZON. Nutzerkonto erstellen?", "createaccount_desc_create", LanguageUtil.lang.DE);
        util.setString("Please enter a valid E-Mail address.", "createaccount_msg_email", LanguageUtil.lang.EN);
        util.setString("Die eingegebene E-Mail Adresse wurde nicht erkannt.", "createaccount_msg_email", LanguageUtil.lang.DE);
        util.setString("Please wait...", "wait", LanguageUtil.lang.EN);
        util.setString("Bitte warten...", "wait", LanguageUtil.lang.DE);
        util.setString("Your ID is already registered.", "createaccount_already_registered", LanguageUtil.lang.EN);
        util.setString("Deine ID ist bereits registriert.", "createaccount_already_registered", LanguageUtil.lang.DE);
        util.setString("Click here to receive your credentials.", "createaccount_view", LanguageUtil.lang.EN);
        util.setString("Klicke hier um deine Anmeldedaten zu erhalten.", "createaccount_view", LanguageUtil.lang.DE);
        util.setString("Please change the password after login and enable 2-FA if you want (recommended).", "createaccount_cred", LanguageUtil.lang.EN);
        util.setString("Bitte ändere das Passwort nach der Anmeldung und aktiviere 2-FA, für extra Sicherheit (empfohlen).", "createaccount_cred", LanguageUtil.lang.DE);
        util.setString("Privacy Policy, Special Conditions ELIZON.BOT & Terms of Use (ELIZON. section): https://vultronstudios.net/legal\n" +
                        "Do you accept the Privacy Policy, Special Conditions ELIZON.BOT and Terms of Use (ELIZON. section)?",
                "terms_accept", LanguageUtil.lang.EN);
        util.setString("Datenschutzerklärung, Gesonderte Bedingungen ELIZON.BOT & Nutzungsbedingungen (ELIZON. Sektion): https://vultronstudios.net/legal\n" +
                        "Akzeptierst du die Datenschutzerklärung, Gesonderten Bedingungen ELIZON.BOT und Nutzungsbedingungen (ELIZON. Sektion)?",
                "terms_accept", LanguageUtil.lang.DE);
        util.setString("Okay, we're sorry to hear that.", "int_sorry", LanguageUtil.lang.EN);
        util.setString("Okay, das tut uns leid.", "int_sorry", LanguageUtil.lang.DE);
        util.setString("Bitte gebe jetzt doe E-Mail Adresse ein, die du verwenden möchtest.\n" +
                "Bitte verwende eine echte E-Mail, du benötigst sie, wenn du dein Benachrichtigungen erhalten oder das Passwort ändern möchtest.", "int_email", LanguageUtil.lang.DE);
        util.setString("Please write now your E-Mail you want to use.\n" +
                "Please use a real E-Mail, you'll need it when you want to get notifications or change the password.", "int_email", LanguageUtil.lang.EN);
        util.setString("Can't load credentials.\nYou can reset your password at the panel login page with your entered email.", "int_cant_load", LanguageUtil.lang.EN);
        util.setString("Anmeldedaten konnten nicht geladen werden.\nDu kannst dein Password im Nutzerpanel mit deiner verwendeten E-Mail Adresse zurücksetzen.", "int_cant_load", LanguageUtil.lang.DE);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("createaccount")) {
            LanguageUtil util = new LanguageUtil();
            LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
            if(event.getChannel().getType().isGuild()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("createaccount_only_dm", lang));
                event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                event.getInteraction().getUser().openPrivateChannel().queue(privateChannel -> {
                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription(util.getString("createaccount_desc_create", lang));
                    event.replyEmbeds(b.build()).setEphemeral(true)
                            .addActionRow(
                                    Button.success("create_yes", "✓"),
                                    Button.danger("create_no", "✘"))
                            .queue();
                });
            } else {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("createaccount_desc_create", lang));
                event.replyEmbeds(builder.build()).setEphemeral(true)
                        .addActionRow(
                                Button.success("create_yes", "✓"),
                                Button.danger("create_no", "✘"))
                        .queue();
            }
        }
    }

    public static final HashMap<Long, Boolean> waitingEmail = new HashMap<>();
    private static final HashMap<Long, String> credentialsTemp = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        LanguageUtil util = new LanguageUtil();
        LanguageUtil.lang lang = util.getUserLanguage(event.getAuthor().getIdLong());
        if(event.getMessage().getChannel().getType().isMessage() && !event.getMessage().getChannel().getType().isGuild()) {
            if(waitingEmail.containsKey(event.getMessage().getAuthor().getIdLong())) {
                if(!event.getMessage().getContentDisplay().contains("@")) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.cyan);
                    builder.setAuthor("ELIZON.");
                    builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    builder.setDescription(util.getString("createaccount_msg_email", lang));
                    event.getChannel().sendMessageEmbeds(builder.build()).queue();
                    return;
                }
                waitingEmail.remove(event.getMessage().getAuthor().getIdLong());
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                builder.setDescription(util.getString("wait", lang));
                event.getChannel().sendMessageEmbeds(builder.build()).queue();
                if(!new Hooks().getPteroApplication().retrieveUsersByUsername(event.getMessage().getAuthor().getIdLong() + "", true).execute().isEmpty()) {
                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription(util.getString("createaccount_already_registered", lang));
                    event.getChannel().sendMessageEmbeds(b.build()).queue();
                }
                String password = new AccountManager().addAccount(event.getMessage().getContentDisplay(), event.getAuthor().getAsTag().split("#")[0], "#" + event.getAuthor().getAsTag().split("#")[1], event.getAuthor().getIdLong());
                credentialsTemp.put(event.getAuthor().getIdLong(), "\nUsername: ||" + event.getAuthor().getIdLong() + "||\nE-Mail: ||" + event.getMessage().getContentDisplay() + "||\nPassword: ||" + password + "||\n" + util.getString("createaccount_cred", lang));
                event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                    EmbedBuilder b = new EmbedBuilder();
                    b.setColor(Color.cyan);
                    b.setAuthor("ELIZON.");
                    b.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
                    b.setThumbnail(new Hooks().fromFile("thumbnailURL"));
                    b.setDescription(util.getString("createaccount_view", lang));
                    event.getChannel().sendMessageEmbeds(b.build())
                            .addActionRow(
                                    Button.primary("credentials", "»"))
                            .queue();
                });
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        LanguageUtil util = new LanguageUtil();
        LanguageUtil.lang lang = util.getUserLanguage(event.getInteraction().getUser().getIdLong());
        if(event.getButton().getId().equals("create_yes")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(util.getString("terms_accept", lang));
            event.replyEmbeds(builder.build())
                    .addActionRow(
                            Button.success("create_accept", "✓"),
                            Button.danger("create_decline", "✘"))
                    .queue();
        } else if(event.getButton().getId().equals("create_no")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(util.getString("int_sorry", lang));
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
        } else if(event.getButton().getId().equals("create_accept")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.cyan);
            builder.setAuthor("ELIZON.");
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(util.getString("int_email", lang));
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
            builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(util.getString("int_sorry", lang));
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
        } else if(event.getButton().getId().equals("credentials")) {
            if(credentialsTemp.containsKey(event.getInteraction().getUser().getIdLong())) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.cyan);
                builder.setAuthor("ELIZON.");
                builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
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
builder.setFooter("Powered by ClusterNode.net", "https://cdn.clusternode.net/image/s/clusternode_net.png");
            builder.setThumbnail(new Hooks().fromFile("thumbnailURL"));
            builder.setDescription(util.getString("int_cant_load", lang));
            event.replyEmbeds(builder.build())
                    .setEphemeral(true)
                    .queue();
        }
    }

}
