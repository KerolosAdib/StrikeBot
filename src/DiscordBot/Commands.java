package DiscordBot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter {
    public String prefix = "!";
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (args[0].equalsIgnoreCase(prefix + "kick") && event.getMember().hasPermission(Permission.KICK_MEMBERS))
        {
            if (event.getMessage().getMentionedUsers().toArray().length == 1)
            {
                Member member = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));

                member.kick("Bitch").queue();
            }
        }
        else if (args[0].equalsIgnoreCase(prefix + "test"))
        {
            event.getChannel().sendMessage("This bot is working!").queue();
        }
    }
}
