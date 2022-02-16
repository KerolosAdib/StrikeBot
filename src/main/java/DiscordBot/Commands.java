package DiscordBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Commands extends ListenerAdapter {
    HashMap<Guild, HashMap<Member, Integer>> strikes;
    public String prefix = "!";
    private File file;
    private JSONObject jsonObject;
    private JSONParser jsonParser;

    public Commands(JDA jda) throws IOException, ParseException {
        super();

        strikes = new HashMap<Guild, HashMap<Member, Integer>>();
        File file = new File("strikes.json");
        if (!file.exists()) {
            file.createNewFile();
            jsonObject = new JSONObject();
            for (int i = 0; i < jda.getGuilds().size(); i++) {
                JSONObject memberObject = new JSONObject();
                HashMap<Member, Integer> temp = new HashMap<Member, Integer>();
                List<Member> members = jda.getGuilds().get(i).loadMembers().get();
                for (int j = 0; j < jda.getGuilds().get(i).getMemberCount(); j++) {
                    memberObject.put(members.get(j).getId(), 0);
                    temp.put(members.get(j), 0);
                }
                strikes.put(jda.getGuilds().get(i), temp);
                jsonObject.put(jda.getGuilds().get(i).getId(), memberObject);
            }
            try (FileWriter fileWriter = new FileWriter("strikes.json")) {
                fileWriter.write(jsonObject.toJSONString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader("strikes.json"))
            {
                JSONObject guildList = (JSONObject) jsonParser.parse(reader);


                for (int i = 0; i < guildList.size(); i++)
                {
                    HashMap<Member, Integer> temp = new HashMap<Member, Integer>();
                    JSONObject memberList = ((JSONObject)guildList.get(guildList.keySet().toArray()[i]));
                    Guild guild = jda.getGuildById(guildList.keySet().toArray()[i].toString());
                    for (int j = 0; j < memberList.size(); j++)
                    {
                        List<Member> members = guild.loadMembers().get();
                        String id = memberList.keySet().toArray()[j].toString();
                        for (int k = 0; k < guild.getMemberCount(); k++) {
                            if (members.get(k).getId().equals(id))
                            {
                                temp.put(members.get(k), Math.toIntExact((Long) memberList.get(members.get(k).getId())));
                            }
                            temp.put(members.get(k), 0);
                        }
                        /*guild.retrieveMemberById(memberList.keySet().toArray()[j].toString()).queue(
                            member -> {
                                temp.put(member, Math.toIntExact((Long) memberList.get(member.getId())));
                            });*/
                    }
                    strikes.put(guild, temp);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (ParseException e)
            {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        super.onGuildMemberJoin(event);
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("strikes.json"))
        {
            Object obj = jsonParser.parse(reader);
            JSONArray strikesList = (JSONArray) obj;
            for (int i = 0; i < strikesList.size(); i++)
            {
                JSONObject guildObject = (JSONObject)strikesList.get(i);
                if (guildObject.containsKey(event.getGuild().getId()))
                {
                    JSONObject memberObject = (JSONObject)guildObject.get(event.getGuild().getId());
                    if (memberObject.containsKey(event.getMember().getId()))
                    {
                        strikes.get(event.getGuild()).put(event.getMember(), (int)memberObject.get(event.getMember().getId()));
                    }
                    else
                    {
                        strikes.get(event.getGuild()).put(event.getMember(), 0);
                    }
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        super.onGuildMemberRemove(event);
        strikes.get(event.getGuild()).remove(event.getMember());
    }

    public void onMessageReceived(MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (args[0].equalsIgnoreCase(prefix + "kick") &&
                event.getMember().hasPermission(Permission.KICK_MEMBERS))
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
        else if (args[0].equalsIgnoreCase(prefix + "strike") &&
                event.getMember().hasPermission(Permission.ADMINISTRATOR))
        {
            if (event.getMessage().getMentionedMembers().toArray().length == 1)
            {
                Member member = event.getMessage().getMentionedMembers().get(0);

                strikes.get(event.getGuild()).put(member, strikes.get(event.getGuild()).get(member) + 1);

                if (strikes.get(event.getGuild()).get(member) < 3)
                {
                    event.getMessage().reply("User: " + event.getMessage().getMentionedUsers().get(0).getAsMention() + " has " + strikes.get(event.getGuild()).get(member) + " strike(s)!").queue();
                }
                if (strikes.get(event.getGuild()).get(member) >= 3)
                {
                    event.getMessage().reply("3 strikes and " + event.getMessage().getMentionedUsers().get(0).getAsMention() + " is out!").queue();
                    member.kick().queue();
                }
            }
        }
        else if (args[0].equalsIgnoreCase(prefix + "disconnect") &&
                event.getMember().hasPermission(Permission.ADMINISTRATOR) &&
                event.getMember().getId().equalsIgnoreCase("274775166263885844"))
        {
            if (event.getMessage().getMentionedMembers().toArray().length == 1)
            {
                Member member = event.getMessage().getMentionedMembers().get(0);
                event.getGuild().moveVoiceMember(member, null).queue();
            }
        }
        else if (args[0].equalsIgnoreCase(prefix + "roulette"))
        {
            Random random = new Random();
            List<VoiceChannel> voiceChannels = event.getGuild().getVoiceChannels();
            //VoiceChannel vc = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            int i;
            int j;
            do {
                i = random.nextInt(voiceChannels.size());
            } while(voiceChannels.get(i).getMembers().size() == 0);
            j = random.nextInt(voiceChannels.get(i).getMembers().size());
            event.getGuild().moveVoiceMember(voiceChannels.get(i).getMembers().get(j), null).queue();
        }
/*        else if (args[0].equals(prefix + "GETOVERHERE"))
        {
            if (event.getMessage().getMentionedMembers().toArray().length == 1)
            {

                event.getGuild().moveVoiceMember(event.g, null).queue();
            }
        }*/
    }

/*    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        if (event.getMember().getId().equalsIgnoreCase("198347673269567488"))
        {
            event.getGuild().moveVoiceMember(event.getMember(), null).queue();
        }
    }*/
}
