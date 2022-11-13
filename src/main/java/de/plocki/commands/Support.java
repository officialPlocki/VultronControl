package de.plocki.commands;

import de.plocki.Main;
import de.plocki.ai.SupportAI;
import de.plocki.util.Hooks;
import de.plocki.util.SupportManager;
import de.plocki.util.TicketInformation;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Support extends ListenerAdapter {

    public final static HashMap<Long, Boolean> s = new HashMap<>();
    public final static HashMap<Long, Boolean> answering = new HashMap<>();

    //@todo -> modal & right questions

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("support")) {
            CreateAccount.waitingEmail.remove(event.getInteraction().getUser().getIdLong());
            RequestServer.usage.remove(event.getInteraction().getUser().getIdLong());
            if(event.getChannel().getType().isGuild()) {
                event.reply("This command is only available in direct messages. I'll send you a message.").setEphemeral(true).queue();
                event.getInteraction().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Creating ticket...").queue();
                    privateChannel.sendMessage("Please write now the reason why you're contacting us today.").queue();
                    s.put(event.getInteraction().getUser().getIdLong(), true);
                });
                return;
            }
            if(event.getChannel().getType().isMessage()) {
                event.getChannel().sendMessage("Creating ticket...").queue();
                event.reply("Please write now the reason why you're contacting us today.").queue();
                s.put(event.getInteraction().getUser().getIdLong(), true);
            }
        } else if(event.getName().equalsIgnoreCase("answer")) {
            if(event.getChannel().getType().isGuild()) {
                event.reply("Please write now your answer in your direct messages.").setEphemeral(true).queue();
                event.getInteraction().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("I'm here!").queue();
                    answering.put(event.getInteraction().getUser().getIdLong(), true);
                });
            } else {
                event.reply("Please write now your answer.").setEphemeral(true).queue();
                answering.put(event.getInteraction().getUser().getIdLong(), true);
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getChannel().getName().contains("archived-")) return;
        if(Objects.requireNonNull(event.getButton().getId()).contains("close_")) {
            event.reply("Ticket closed.").queue();
            new SupportManager().closeTicket(Long.parseLong(event.getButton().getId().replaceAll("close_", "")));
        } else if(event.getButton().getId().contains("transfer_")) {
            event.reply("Ticket is now being transferred.").queue();
            new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Ticket is now being transferred.");
        } else if(event.getButton().getId().contains("claim_")) {
            event.reply("Ticket was claimed by " + Objects.requireNonNull(event.getInteraction().getMember()).getNickname() + ".").queue();
            new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "You will now be helped by " + Objects.requireNonNull(event.getInteraction().getMember()).getNickname());
        } else if(event.getButton().getId().equals("welcome_message")) {
            event.reply("Sent (Welcome to the Vultron Studios support. Do you want to be supported in english or german?).").queue();
            new SupportManager().sendMessageToCustomer(event.getChannel().getIdLong(), "Welcome to the Vultron Studios support. Do you want to be supported in english or german? You can answer at any time with /answer.");
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
                event.getChannel().sendMessage("Type **/answer** to answer.").queue();
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
