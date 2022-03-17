package DiscordBot;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import javax.security.auth.callback.Callback;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Commands extends ListenerAdapter
{
    HashMap<Guild, HashMap<Member, Integer>> strikes;
    EmbedLinkedList embedLL;
    List<EmbedBuilder> ebs;
    public String prefix = "!";
    private String previousEmbed;

    public Commands(JDA jda) throws IOException
    {
        super();
        previousEmbed = "";
        strikes = new HashMap<Guild, HashMap<Member, Integer>>();
        ebs = new ArrayList<>();
        jda.updateCommands().queue();
        for (int i = 0; i < jda.getGuilds().size(); i++)
        {
            File file = new File(jda.getGuilds().get(i).getId() + ".json");
            if (!file.exists())
            {
                JSONObject memberList = new JSONObject();
                HashMap<Member, Integer> temp = new HashMap<>();
                List<Member> members = jda.getGuilds().get(i).loadMembers().get();
                for (int j = 0; j < jda.getGuilds().get(i).getMemberCount(); j++)
                {
                    if (!members.get(j).getUser().isBot())
                    {
                        memberList.put(members.get(j).getId(), 0);
                        temp.put(members.get(j), 0);
                    }
                }
                strikes.put(jda.getGuilds().get(i), temp);
                try (FileWriter fileWriter = new FileWriter(jda.getGuilds().get(i).getId() + ".json"))
                {
                    fileWriter.write(memberList.toJSONString());
                    fileWriter.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Guild guild = jda.getGuilds().get(i);
                JSONParser jsonParser = new JSONParser();
                try (FileReader reader = new FileReader(guild.getId() + ".json"))
                {
                    JSONObject memberList = (JSONObject) jsonParser.parse(reader);
                    HashMap<Member, Integer> temp = new HashMap<>();
                    List<String> memberIds = new ArrayList<>();
                    List<Member> members = guild.loadMembers().get();
                    for (int j = 0; j < members.size(); j++)
                    {
                        memberIds.add(members.get(j).getId());
                    }


                    for (int j = 0; j < memberList.size(); j++)
                    {
                        String str = memberList.keySet().toArray()[j].toString();
                        if (memberIds.contains(str))
                        {
                            temp.put(members.get(memberIds.indexOf(str)), Math.toIntExact((Long) memberList.get(str)));

                        }
                    }
                    strikes.put(guild, temp);
                }
                catch (IOException | ParseException e)
                {
                    e.printStackTrace();
                }
            }
        }
        /*File file = new File("strikes.json");
        if (!file.exists())
        {
            file.createNewFile();
            jsonObject = new JSONObject();
            for (int i = 0; i < jda.getGuilds().size(); i++)
            {
                JSONObject memberObject = new JSONObject();
                HashMap<Member, Integer> temp = new HashMap<Member, Integer>();
                List<Member> members = jda.getGuilds().get(i).loadMembers().get();
                for (int j = 0; j < jda.getGuilds().get(i).getMemberCount(); j++)
                {
                    memberObject.put(members.get(j).getId(), 0);
                    temp.put(members.get(j), 0);
                }
                strikes.put(jda.getGuilds().get(i), temp);
                jsonObject.put(jda.getGuilds().get(i).getId(), memberObject);
            }
            try (FileWriter fileWriter = new FileWriter("strikes.json"))
            {
                fileWriter.write(jsonObject.toJSONString());
                fileWriter.flush();
            }
            catch (IOException e)
            {
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
                    JSONObject memberList = (JSONObject) guildList.get(guildList.keySet().toArray()[i]);
                    Guild guild = jda.getGuildById(guildList.keySet().toArray()[i].toString());
                    for (int j = 0; j < memberList.size(); j++)
                    {
                        List<Member> members = guild.loadMembers().get();
                        String id = memberList.keySet().toArray()[j].toString();
                        for (int k = 0; k < guild.getMemberCount(); k++)
                        {
                            if (members.get(k).getId().equals(id))
                            {
                                temp.put(members.get(k), Math.toIntExact((Long) memberList.get(members.get(k).getId())));
                            }
                            temp.put(members.get(k), 0);
                        }
                        *//*guild.retrieveMemberById(memberList.keySet().toArray()[j].toString()).queue(
                            member -> {
                                temp.put(member, Math.toIntExact((Long) memberList.get(member.getId())));
                            });*//*
                    }
                    strikes.put(guild, temp);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

        }*/
        jda.updateCommands().addCommands(new CommandDataImpl("disconnect", "Disconnect a user from a voice channel")
                        .addOption(OptionType.USER, "user", "user to disconnect", true),
                new CommandDataImpl("strike", "Strikes a user, 3 strikes and they get kicked")
                        .addOption(OptionType.USER, "user", "user to strike", true),
                new CommandDataImpl("roulette", "Chooses a random person to be disconnected"),
                new CommandDataImpl("kick", "kick a user out")
                        .addOption(OptionType.USER, "user", "the user to kick out", true),
                new CommandDataImpl("getstrikes", "grabs strikes")).queue();
    }


    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event)
    {
        super.onGuildMemberJoin(event);
        if (!event.getUser().isBot())
        {
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader(event.getGuild().getId() + ".json"))
            {
                Object obj = jsonParser.parse(reader);
                JSONObject memberList = (JSONObject) obj;
                if (memberList.containsKey(event.getMember().getId()))
                {
                    strikes.get(event.getGuild()).put(event.getMember(), Math.toIntExact((Long) memberList.get(event.getMember().getId())));
                }
                else
                {
                    strikes.get(event.getGuild()).put(event.getMember(), 0);
                    memberList.put(event.getMember().getId(), 0);
                    try (FileWriter fileWriter = new FileWriter(event.getGuild().getId() + ".json"))
                    {
                        fileWriter.write(memberList.toJSONString());
                        fileWriter.flush();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException | ParseException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event)
    {
        super.onGuildMemberRemove(event);
        strikes.get(event.getGuild()).remove(event.getMember());
    }

    public void onMessageReceived(MessageReceivedEvent event)
    {
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (args[0].equalsIgnoreCase(prefix + "kick") &&
                (event.getMember().hasPermission(Permission.KICK_MEMBERS) || event.getMember().getId().equals("274775166263885844")))
        {

        }
        else if (args[0].equalsIgnoreCase(prefix + "test"))
        {
            event.getChannel().sendMessage("This bot is working!").queue();
        }
        else if (args[0].equalsIgnoreCase(prefix + "strike") &&
                (event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getMember().getId().equals("274775166263885844")))
        {

        }
        /*else if (args[0].equalsIgnoreCase(prefix + "disconnect") &&
                event.getMember().hasPermission(Permission.ADMINISTRATOR) &&
                event.getMember().getId().equalsIgnoreCase("274775166263885844"))
        {
            if (event.getMessage().getMentionedMembers().toArray().length == 1)
            {
                Member member = event.getMessage().getMentionedMembers().get(0);
                event.getGuild().moveVoiceMember(member, null).queue();
            }
        }*/
        else if (args[0].equalsIgnoreCase(prefix + "roulette"))
        {
            Random random = new Random();
            List<VoiceChannel> voiceChannels = event.getGuild().getVoiceChannels();
            //VoiceChannel vc = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            int i;
            int j;
            do
            {
                i = random.nextInt(voiceChannels.size());
            } while (voiceChannels.get(i).getMembers().size() == 0);
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

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        if (event.getName().equals("disconnect")
                && (event.getMember().hasPermission(Permission.ADMINISTRATOR)
                || event.getMember().getId().equals("274775166263885844")))
        {
            Member member = event.getOption("user").getAsMember();
            event.getGuild().moveVoiceMember(member, null).queue();
            event.reply("Disconnect Successful").queue();
        }
        else if (event.getName().equals("strike")
                && (event.getMember().hasPermission(Permission.ADMINISTRATOR)
                || event.getMember().getId().equals("274775166263885844")))
        {


            Member member = event.getOption("user").getAsMember();
            if (!member.getUser().isBot())
            {
                strikes.get(event.getGuild()).put(member, strikes.get(event.getGuild()).get(member) + 1);
                if (strikes.get(event.getGuild()).get(member) < 3)
                {
                    event.reply("User: " + member.getAsMention() + " has " + strikes.get(event.getGuild()).get(member) + " strike(s)!").queue();
                    addStrikeToJSON(event.getGuild(), member, 1);
                }
                else
                {
                    event.reply("3 strikes and " + member.getAsMention() + " is out!").queue();
                    member.kick().queue();
                }
            }
            else
            {
                event.reply("Please don't strike the bots").queue();
            }

        }
        else if (event.getName().equals("roulette"))
        {
            Random random = new Random();
            List<VoiceChannel> voiceChannels = event.getGuild().getVoiceChannels();
            //VoiceChannel vc = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            int i;
            int j;
            do
            {
                i = random.nextInt(voiceChannels.size());
            } while (voiceChannels.get(i).getMembers().size() == 0);
            j = random.nextInt(voiceChannels.get(i).getMembers().size());
            Member member = voiceChannels.get(i).getMembers().get(j);
            event.getGuild().moveVoiceMember(member, null).queue();
            event.reply(member.getAsMention() + " has been killed!").queue();
        }
        else if (event.getName().equals("kick"))
        {
            Member member = event.getOption("user").getAsMember();
            member.kick().queue();
        }
        else if (event.getName().equals("getstrikes"))
        {
            embedLL = new EmbedLinkedList();
            Guild guild = event.getGuild();
            Object[] members = (strikes.get(guild).keySet().toArray());
            event.deferReply(true).queue();
            int memberSize = 0;
            for (int i = 0; i < guild.getMemberCount(); i++)
            {
                memberSize += (guild.getMembers().get(i).getUser().isBot()) ? 0 : 1;
            }
            int pages = (int) Math.ceil(guild.getMemberCount() / 10.0);
            List<Button> buttons = new ArrayList<>();
            for (int i = 1; i <= pages; i++)
            {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Strikes");
                eb.setDescription("Shows the number of strikes each person has");
                eb.setFooter("Page: " + i);
                eb.setColor(Color.RED);
                int size = strikes.get(guild).size();

                for (int j = 0; j < 10 && ((i - 1) * 10 + j) < size; j++)
                {
                    String value = ((Member) members[(i - 1) * 10 + j]).getAsMention() + ": ";

                    int numberOfStrikes = strikes.get(guild).get(((Member) members[(i - 1) * 10 + j]));
                    if (numberOfStrikes == 0)
                        value += "Good job, no strikes!";
                    else if (numberOfStrikes == 1)
                        value += "❌";
                    else if (numberOfStrikes == 2)
                        value += "❌❌";
                    else
                        value += "❌❌❌";
                    eb.addField("", value, false);
                }
                embedLL.addBack(eb);
            }

            buttons.add(Button.primary("first", Emoji.fromUnicode("⏪")));
            buttons.add(Button.primary("last_page", Emoji.fromUnicode("◀")));
            buttons.add(Button.primary("next_page", Emoji.fromUnicode("▶")));
            buttons.add(Button.primary("last", Emoji.fromUnicode("⏩")));

            embedLL.setCurrent(embedLL.getHeadBuilder());
            event.getHook().sendMessageEmbeds(embedLL.getCurrentBuilder().build()).addActionRow(buttons).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
    {
        List<Button> buttons = new ArrayList<>();
        Guild guild = event.getGuild();
        List<Member> members = guild.getMembers();
        int pages = (int) Math.ceil(guild.getMemberCount() / 10.0);
        EmbedBuilder eb = new EmbedBuilder();
        buttons.add(Button.primary("first", Emoji.fromUnicode("⏪")));
        buttons.add(Button.primary("last_page", Emoji.fromUnicode("◀")));
        buttons.add(Button.primary("next_page", Emoji.fromUnicode("▶")));
        buttons.add(Button.primary("last", Emoji.fromUnicode("⏩")));
        switch (event.getComponentId())
        {
            case "first":
                embedLL.setCurrent(embedLL.getHeadBuilder());
                break;
            case "last_page":
                embedLL.decrementCurrent();
                break;
            case "next_page":
                embedLL.incrementCurrent();
                break;
            case "last":
                embedLL.setCurrent(embedLL.getTailBuilder());
                break;
        }
        event.editMessageEmbeds(embedLL.getCurrentBuilder().build()).setActionRow(buttons).queue();
    }

    public void addStrikeToJSON(Guild guild, Member member, int strikes)
    {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(guild.getId() + ".json"))
        {
            Object obj = jsonParser.parse(reader);
            JSONObject memberList = (JSONObject) obj;
            memberList.put(member.getId(), (Long) memberList.get(member.getId()) + strikes);
            FileWriter fileWriter = new FileWriter(guild.getId() + ".json");

            fileWriter.write(memberList.toJSONString());
            fileWriter.flush();


        }
        catch (IOException | ParseException e)
        {
            e.printStackTrace();
        }
    }



    /*    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        if (event.getMember().getId().equalsIgnoreCase("198347673269567488"))
        {
            event.getGuild().moveVoiceMember(event.getMember(), null).queue();
        }
    }*/
}
