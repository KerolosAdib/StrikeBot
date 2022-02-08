package DiscordBot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main {
    public static JDA jda;

    public static void main(String[] args) throws LoginException
    {
        jda = JDABuilder.createDefault("NTg3ODY5Mjc1ODE0MTAxMDA1.XP817Q.EerjxNxz832V33AZnbWsvrCizkM").build();
        jda.addEventListener(new Commands());
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.playing("IntelliJ"));

    }
}
