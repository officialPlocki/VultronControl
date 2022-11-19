package de.plocki;

import de.plocki.commands.*;
import de.plocki.util.Hooks;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Main {

    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        if(!new Hooks().getFileBuilder().getYaml().isSet("vultronGuildID")) new Hooks().toFile("vultronGuildID", "0123456789");
        if(!new Hooks().getFileBuilder().getYaml().isSet("vultronGuildSupportChannelID")) new Hooks().toFile("vultronGuildSupportChannelID", "0123456789");
        if(!new Hooks().getFileBuilder().getYaml().isSet("vultronGuildAdminChannelID")) new Hooks().toFile("vultronGuildAdminChannelID", "0123456789");
        if(!new Hooks().getFileBuilder().getYaml().isSet("vultronBoosterID")) new Hooks().toFile("vultronBoosterID", "0123456789");
        if(!new Hooks().getFileBuilder().getYaml().isSet("vultronBoosterID2")) new Hooks().toFile("vultronBoosterID2", "0123456789");
        if(!new Hooks().getFileBuilder().getYaml().isSet("vultronGuildChannelID")) new Hooks().toFile("vultronGuildChannelID", "0123456789");
        if(!new Hooks().getFileBuilder().getYaml().isSet("vultronTicketCategoryID")) new Hooks().toFile("vultronTicketCategoryID", "0123456789");
        if(!new Hooks().getFileBuilder().getYaml().isSet("vultronTicketArchiveCategoryID")) new Hooks().toFile("vultronTicketArchiveCategoryID", "0123456789");
        if(!new Hooks().getFileBuilder().getYaml().isSet("panelURL")) new Hooks().toFile("panelURL", "https://yourpanel.com");
        if(!new Hooks().getFileBuilder().getYaml().isSet("panelToken")) new Hooks().toFile("panelToken", "YourTokenComesHere");
        if(!new Hooks().getFileBuilder().getYaml().isSet("discordBotToken")) new Hooks().toFile("discordBotToken", "YourDiscordBotTokenComesHere");
        if(!new Hooks().getFileBuilder().getYaml().isSet("cloudflareEmail")) new Hooks().toFile("cloudflareEmail", "you@example.net");
        if(!new Hooks().getFileBuilder().getYaml().isSet("cloudflareKey")) new Hooks().toFile("cloudflareKey", "cloudflareAccessToken");
        if(!new Hooks().getFileBuilder().getYaml().isSet("thumbnailURL")) new Hooks().toFile("thumbnailURL", "thumbnailURL");
        if(!new Hooks().getFileBuilder().getYaml().isSet("cloudflareZoneID")) new Hooks().toFile("cloudflareZoneID", "cloudflareZoneID");
        if(!new Hooks().getFileBuilder().getYaml().isSet("vultronGuildCloseChannelID")) new Hooks().toFile("vultronGuildCloseChannelID", "0123456789");
        if(!new Hooks().getFileBuilder().getYaml().isSet("vultronGuildCloseAccountChannelID")) new Hooks().toFile("vultronGuildCloseAccountChannelID", "0123456789");

        JDABuilder jdaBuilder = JDABuilder.createDefault(new Hooks().fromFile("discordBotToken"));
        jdaBuilder.enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES);
        jdaBuilder.setActivity(Activity.watching("ELIZON."));
        jdaBuilder.setStatus(OnlineStatus.ONLINE);
        jda = jdaBuilder.build();
        jda.addEventListener(new CreateAccount());
        jda.addEventListener(new BotInfo());
        jda.addEventListener(new DeleteAccount());
        jda.addEventListener(new Legal());
        jda.addEventListener(new RequestServer());
        jda.addEventListener(new Support());
        jda.addEventListener(new Cancel());
        jda.addEventListener(new Abuse());
        jda.addEventListener(new DeleteServer());
        jda.addEventListener(new VerifyCode());
        jda.addEventListener(new Language());

        jda.upsertCommand("language", "Change the preferred language of the bot.").queue();
        jda.upsertCommand("createaccount", "Create a hosting account").queue();
        jda.upsertCommand("support", "Get the support link").queue();
        jda.upsertCommand("requestserver", "Request a free minecraft server").queue();
        jda.upsertCommand("legal", "Get the link to the legal texts").queue();
        jda.upsertCommand("deleteaccount", "Get information about how you can delete your hosting account").queue();
        jda.upsertCommand("botinfo", "Get information about the bot").queue();
        jda.upsertCommand("cancel", "Cancel current processes").queue();
        jda.upsertCommand("abuse", "Get Information about how you can report unappreciative or abusive content on our servers").queue();
        jda.upsertCommand("answer", "Reply to a ticket").queue();
        jda.upsertCommand("deleteserver", "Delete a server").queue();
        jda.upsertCommand("verifycode", "Get a verification code for your Hosting account for the support.").queue();
    }

}