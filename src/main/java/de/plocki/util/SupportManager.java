package de.plocki.util;

import de.plocki.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class SupportManager {

    public boolean hasPrioritySupport(long id) {
        Guild guild = Main.jda.getGuildById(new Hooks().fromFile("vultronGuildID"));
        assert guild != null;
        Member member = guild.retrieveMemberById(id).complete();
        assert member != null;
        List<Role> roles = member.getRoles();
        Role searched = guild.getRoleById(new Hooks().fromFile("vultronBoosterID"));
        Role searched2 = guild.getRoleById(new Hooks().fromFile("vultronBoosterID2"));
        return roles.contains(searched) || roles.contains(searched2);
    }

    public void createSupportTicket(TicketInformation information) throws IOException {
        Guild guild = Main.jda.getGuildById(new Hooks().fromFile("vultronGuildID"));
        String name = information.getUserID() + "";
        assert guild != null;
        ChannelAction<TextChannel> c = guild.createTextChannel(name, guild.getCategoryById(new Hooks().fromFile("vultronTicketCategoryID")));
        c.queue(textChannel -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Ticket from " + information.getUserID());
            builder.setDescription(information.getHelpReason());
            builder.addField("Prio-Support", "" + information.hasPrio(), true);
            try {
                builder.addField("AI-Categorisation", information.aiCategorisation(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            builder.setAuthor("" + information.getUserID());
            builder.setFooter("Click first \"Claim\", than \"Welcome\".\nIf the Team member is switching, click first \"Transfer\" and then \"Claim\".");
            System.out.println(textChannel.getIdLong());
            textChannel.sendMessageEmbeds(builder.build())
                    .addActionRow(
                            Button.danger("close_" + information.getUserID(), "Close ticket"),
                            Button.primary("transfer_" + information.getUserID(), "Transfer"),
                            Button.success("claim_" + information.getUserID(), "Claim"),
                            Button.primary("welcome_message", "Welcome")
                    ).queue();
        });
    }

    /**
     * @param id user id
     */
    public void closeTicket(long id) {
        Guild guild = Main.jda.getGuildById(new Hooks().fromFile("vultronGuildID"));
        String name = id + "";
        assert guild != null;
        if(!guild.getTextChannelsByName(name, true).isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            TextChannel channel = guild.getTextChannelsByName(name, true).get(0);
            sendMessageToCustomer(channel.getIdLong(), "Thank you for contacting the Vultron Studios support.\nYour ticket is now closed.\nIf you have anything regarding to that ticket, name the following ID: **" + uuid + "**");
            channel.getManager().setParent(guild.getCategoryById(new Hooks().fromFile("vultronTicketArchiveCategoryID"))).queue();
            channel.getManager().setName("archived-" + channel.getName() + "_" + uuid).queue();
        }
    }

    /**
     * @param id      channel id
     * @param message message to send
     */
    public void sendMessageToCustomer(long id, String message) {
        Guild guild = Main.jda.getGuildById(new Hooks().fromFile("vultronGuildID"));
        assert guild != null;
        TextChannel channel = guild.getTextChannelById(id);
        assert channel != null;
        long u = Long.parseLong(channel.getName().replaceAll("prio-", ""));
        User user = Main.jda.retrieveUserById(u).complete();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Vultron Studios");
        builder.setTitle("Answer to ticket #" + id);
        builder.setDescription(message);

        user.openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessageEmbeds(builder.build()).queue();
        });
    }

    /**
     *
     * @param id user id
     * @param message message to send
     */
    public boolean sendMessageToSupport(long id, String message) {
        Guild guild = Main.jda.getGuildById(new Hooks().fromFile("vultronGuildID"));
        String name = id + "";
        assert guild != null;
        if(guild.getTextChannelsByName(name, true).isEmpty()) return false;
        TextChannel channel = guild.getTextChannelsByName(name, true).get(0);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(id + "");
        builder.setTitle("Answer from Customer");
        builder.setDescription(message);
        channel.sendMessageEmbeds(builder.build()).queue();;
        return true;
    }

}
