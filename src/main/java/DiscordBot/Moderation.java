package DiscordBot;

import com.intellij.ui.JBColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Moderation extends ListenerAdapter
{
    HashMap<Guild, HashMap<Member, Integer>> strikes;
    HashMap<Guild, EmbedLinkedList> ells;
    HashMap<Guild, HashMap<Channel, String>> previousEmbeds;
    HashMap<Guild, HashMap<Channel, EmbedLinkedList>> currentEmbeds;
    EmbedLinkedList embedLL;
    List<EmbedBuilder> ebs;

    public Moderation(JDA jda)
    {
        super();
        strikes = new HashMap<>();
        ells = new HashMap<>();
        ebs = new ArrayList<>();
        previousEmbeds = new HashMap<>();
        currentEmbeds = new HashMap<>();
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
                    Member member = members.get(j);
                    if (!member.getUser().isBot())
                    {
                        memberList.put(member.getId(), 0);
                        temp.put(member, 0);
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
                    for (Member member : members)
                    {
/*                        if (!member.getUser().isBot())
                            memberIds.add(member.getId());*/
                        if (memberList.containsKey(member.getId()) && !member.getUser().isBot())
                        {
                            temp.put(member, Math.toIntExact((Long) memberList.get(member.getId())));
                        }
                        else if (!memberList.containsKey(member.getId()) && !member.getUser().isBot())
                        {
                            temp.put(member, 0);
                            addMemberToJSON(guild, memberList, member);
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
        jda.upsertCommand(new CommandDataImpl("strike", "Strikes a user, 3 strikes and they get kicked")
                .addOption(OptionType.USER, "user", "user to strike", true)).queue();
        jda.upsertCommand(new CommandDataImpl("getstrikes", "grabs strikes")).queue();
        jda.upsertCommand(new CommandDataImpl("setstrikes", "sets the specified user to the specified amount of strikes")
                .addOption(OptionType.USER, "user", "The user to strike", true)
                .addOption(OptionType.INTEGER, "strikes", "The number of strikes to set on the specified user", true)).queue();
    }

    public void addMemberToJSON(Guild guild, JSONObject memberList, Member member)
    {
        try (FileWriter fileWriter = new FileWriter(guild.getId() + ".json"))
        {
            memberList.put(member.getId(), 0);
            fileWriter.write(memberList.toJSONString());
            fileWriter.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        if (event.getName().equals("strike")
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
        else if (event.getName().equals("getstrikes"))
        {
            Guild guild = event.getGuild();

            Object[] members = (strikes.get(guild).keySet().toArray());
            event.deferReply().queue();
            int memberSize = 0;
            for (int i = 0; i < guild.getMemberCount(); i++)
            {
                memberSize += (guild.getMembers().get(i).getUser().isBot()) ? 0 : 1;
            }
            int pages = (int) Math.ceil(memberSize / 10.0);
            List<Button> buttons = new ArrayList<>();
            ells.put(guild, new EmbedLinkedList());
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
                        value += "???";
                    else if (numberOfStrikes == 2)
                        value += "??????";
                    else
                        value += "?????????";
                    eb.addField("", value, false);
                }
                ells.get(guild).addBack(eb);
            }

            buttons.add(Button.primary("first", Emoji.fromUnicode("???")));
            buttons.add(Button.primary("last_page", Emoji.fromUnicode("???")));
            buttons.add(Button.primary("next_page", Emoji.fromUnicode("???")));
            buttons.add(Button.primary("last", Emoji.fromUnicode("???")));
            ells.get(guild).setCurrent(ells.get(guild).getHeadBuilder());
            currentEmbeds.putIfAbsent(guild, new HashMap<>());
            currentEmbeds.get(guild).put(event.getChannel(), ells.get(guild).clone());

            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.setEmbeds(ells.get(guild).getCurrentBuilder().build());
            Message message = messageBuilder.build();
            if (previousEmbeds.containsKey(guild) && previousEmbeds.get(guild).containsKey(event.getChannel()))
                event.getHook().deleteMessageById(previousEmbeds.get(guild).get(event.getChannel())).queue();
            event.getHook().sendMessage(message).addActionRow(buttons).queue((m) ->
            {
                String id = m.getId();
                previousEmbeds.putIfAbsent(guild, new HashMap<>());
                previousEmbeds.get(guild).put(event.getChannel(), id);
            });
        }
        else if (event.getName().equals("setstrikes") && event.getMember().hasPermission(Permission.ADMINISTRATOR))
        {
            Member member = event.getOption("user").getAsMember();
            int strikes = (int) event.getOption("strikes").getAsDouble();

        }
    }
    
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
    {
        List<Button> buttons = new ArrayList<>();
        Guild guild = event.getGuild();
        Channel channel = event.getChannel();
        List<Member> members = guild.getMembers();
        int pages = (int) Math.ceil(guild.getMemberCount() / 10.0);
        EmbedBuilder eb = new EmbedBuilder();
        buttons.add(Button.primary("first", Emoji.fromUnicode("???")));
        buttons.add(Button.primary("last_page", Emoji.fromUnicode("???")));
        buttons.add(Button.primary("next_page", Emoji.fromUnicode("???")));
        buttons.add(Button.primary("last", Emoji.fromUnicode("???")));
        EmbedLinkedList embedLinkedList = currentEmbeds.get(guild).get(channel);
        switch (event.getComponentId())
        {
            case "first":
                embedLinkedList.setCurrent(embedLinkedList.getHeadBuilder());
                break;
            case "last_page":
                embedLinkedList.decrementCurrent();
                break;
            case "next_page":
                embedLinkedList.incrementCurrent();
                break;
            case "last":
                embedLinkedList.setCurrent(embedLinkedList.getTailBuilder());
                break;
        }
        event.editMessageEmbeds(embedLinkedList.getCurrentBuilder().build()).setActionRow(buttons).queue();
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
