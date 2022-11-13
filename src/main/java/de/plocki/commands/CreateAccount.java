package de.plocki.commands;

import de.plocki.util.AccountManager;
import de.plocki.util.Hooks;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashMap;

public class CreateAccount extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("createaccount")) {
            if(event.getChannel().getType().isGuild()) {
                event.reply("This command is only available in Direct Messages. I'll send you a message.").setEphemeral(true).queue();
                event.getInteraction().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Do you want to create a Vultron Studios Hosting Account?")
                            .addActionRow(Button.success("create_yes", "Yes"), Button.danger("create_no", "No")).queue();
                });
            } else {
                event.reply("Do you want to create a Vultron Studios Hosting Account?")
                        .addActionRow(Button.success("create_yes", "Yes"), Button.danger("create_no", "No")).queue();
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
                    event.getChannel().sendMessage("Please enter a valid E-Mail address.").queue();
                    return;
                }
                waitingEmail.remove(event.getMessage().getAuthor().getIdLong());
                event.getChannel().sendMessage("Please wait...").queue();
                if(!new Hooks().getPteroApplication().retrieveUsersByUsername(event.getMessage().getAuthor().getIdLong() + "", true).execute().isEmpty()) {
                    event.getChannel().sendMessage("Your ID is already registered.").queue();
                }
                String password = new AccountManager().addAccount(event.getMessage().getContentDisplay(), event.getAuthor().getAsTag().split("#")[0], "#" + event.getAuthor().getAsTag().split("#")[1], event.getAuthor().getIdLong());
                credentialsTemp.put(event.getAuthor().getIdLong(), "Your account has been registered. You can now login with these credentials:\nUsername: ||" + event.getAuthor().getIdLong() + "||\nE-Mail: ||" + event.getMessage().getContentDisplay() + "||\nPassword: ||" + password + "||\nPlease change the password after login and enable 2-FA if you want (recommended).");
                event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Click here to receive your credentials.").addActionRow(Button.primary("credentials", "Receive")).queue();
                });
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getButton().getId().equals("create_yes")) {
            event.reply(
                    "Privacy Policy & Terms of Use (Vultron Studios Hosting section): https://vultronstudios.net/legal\n" +
                            "Do you accept the Privacy Policy and Terms of Use (Vultron Studios Hosting)?")
                    .addActionRow(Button.success("create_accept", "Yes"), Button.danger("create_decline", "No")).queue();
        } else if(event.getButton().getId().equals("create_no")) {
            event.reply("Okay, we're sorry to hear that.").setEphemeral(true).queue();
        } else if(event.getButton().getId().equals("create_accept")) {
            event.reply("Please write now your E-Mail you want to use. Please use a real E-Mail, you'll need it when you want to delete your account, get notifications or change the password.").setEphemeral(true).queue();
            waitingEmail.put(event.getInteraction().getUser().getIdLong(), true);
            RequestServer.usage.remove(event.getInteraction().getUser().getIdLong());
            Support.answering.remove(event.getInteraction().getUser().getIdLong());
            Support.s.remove(event.getInteraction().getUser().getIdLong());
        } else if(event.getButton().getId().equals("create_decline")) {
            event.reply("Okay, we're sorry to hear that.").setEphemeral(true).queue();
        } else if(event.getButton().getId().equals("used")) {
            event.reply("Not possible, already used!").queue();
        } else if(event.getButton().getId().equals("credentials")) {
            if(credentialsTemp.containsKey(event.getInteraction().getUser().getIdLong())) {
                event.reply(credentialsTemp.get(event.getInteraction().getUser().getIdLong())).setEphemeral(true).queue();
                credentialsTemp.remove(event.getInteraction().getUser().getIdLong());
                return;
            }
            event.reply("Can't load credentials. You can reset your password at the panel login page with your entered email.").setEphemeral(true).queue();
        }
    }
}
