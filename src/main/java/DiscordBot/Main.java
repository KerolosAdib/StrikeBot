package DiscordBot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.json.simple.parser.ParseException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;

public class Main {
    public static JDA jda;
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws LoginException, IOException, ParseException, InterruptedException {
        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_EMOJIS,
                GatewayIntent.GUILD_INVITES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MEMBERS
        );
        jda = JDABuilder.createDefault("NTg3ODY5Mjc1ODE0MTAxMDA1.XP817Q.JFc_umhqKc0PViS1Qlz7LnEpge4", intents).setMemberCachePolicy(MemberCachePolicy.ALL).build();

        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.playing("IntelliJ"));
        jda.awaitReady();

        jda.addEventListener(new Commands(jda));
        System.out.println(jda.getGuilds().size());
    }
}
