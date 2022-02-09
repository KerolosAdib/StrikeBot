package DiscordBot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class Commands extends ListenerAdapter {
    HashMap<Member, Integer> strikes;
    public String prefix = "!";

    public Commands()
    {
        super();
        strikes = new HashMap<>();

    }

    public void onMessageReceived(MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split(" ");
        User user = event.getAuthor();
        if (args[0].equalsIgnoreCase(prefix + "kick") && event.getMember().hasPermission(Permission.KICK_MEMBERS))
        {
            if (event.getMessage().getMentionedMembers().toArray().length == 1)
            {
                Member member = event.getMessage().getMentionedMembers().get(0);
                member.kick().queue();
            }
        }
        else if (args[0].equalsIgnoreCase(prefix + "test"))
        {
            event.getChannel().sendMessage("This bot is working!").queue();
        }
        else if (args[0].equalsIgnoreCase(prefix + "strike") && event.getMember().hasPermission(Permission.ADMINISTRATOR))
        {
            if (event.getMessage().getMentionedMembers().toArray().length == 1)
            {
                Member member = event.getMessage().getMentionedMembers().get(0);

                if (!strikes.containsKey(member))
                {
                    strikes.put(member, 1);
                }
                else
                {
                    strikes.put(member, strikes.get(member) + 1);
                }

                if (strikes.get(member) < 3)
                {
                    event.getMessage().reply("User: " + event.getMessage().getMentionedUsers().get(0).getAsMention() + " has " + strikes.get(member) + " strike(s)!").queue();
                }
                if (strikes.get(member) >= 3)
                {
                    event.getMessage().reply("3 strikes and " + event.getMessage().getMentionedUsers().get(0).getAsMention() + " is out!").queue();
                    member.kick().queue();
                }
            }
        }
    }
}
