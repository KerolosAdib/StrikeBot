package DiscordBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class Personal extends ListenerAdapter
{
    public Personal(JDA jda)
    {
        super();
        jda.upsertCommand(new CommandDataImpl("disconnect", "Disconnect a user from a voice channel")
                .addOption(OptionType.USER, "user", "user to disconnect", true)).queue();
        jda.upsertCommand(new CommandDataImpl("roulette", "Chooses a random person to be disconnected")).queue();
        jda.upsertCommand(new CommandDataImpl("kick", "kick a user out")
                .addOption(OptionType.USER, "user", "the user to kick out", true)).queue();
        jda.upsertCommand(new CommandDataImpl("changemembernickname", "Changes the targeted users nickname")
                .addOption(OptionType.USER, "user", "targeted user", true)
                .addOption(OptionType.STRING, "nickname", "nickname", true)).queue();
        jda.upsertCommand(new CommandDataImpl("unservermuteme", "unmutes me"));
        jda.upsertCommand(new CommandDataImpl("say", "Tell the bot to say something")
                .addOption(OptionType.STRING,"message", "The message to send", true)).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        super.onSlashCommandInteraction(event);
        if (event.getName().equals("disconnect")
                && (event.getMember().hasPermission(Permission.ADMINISTRATOR)
                || event.getMember().getId().equals("274775166263885844")))
        {
            Member member = event.getOption("user").getAsMember();
            event.getGuild().moveVoiceMember(member, null).queue();
            event.reply("Disconnect Successful").queue();
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
        else if (event.getName().equals("kick")
                && (event.getMember().hasPermission(Permission.ADMINISTRATOR)
                || event.getMember().getId().equals("274775166263885844")))
        {
            Member member = event.getOption("user").getAsMember();
            member.kick().queue();
        }
        else if (event.getName().equals("changemembernickname")
                && (event.getMember().hasPermission(Permission.ADMINISTRATOR)
                || event.getMember().getId().equals("274775166263885844")))
        {
            Member member = event.getOption("user").getAsMember();
            String nickname = event.getOption("nickname").getAsString();
            event.deferReply(true).queue();
            member.modifyNickname(nickname).queue();
            event.getHook().sendMessage("Nickname Changed").queue();
        }
        else if (event.getName().equals("unservermuteme"))
        {
            event.deferReply(true).queue();
            Member member = event.getMember();
            member.mute(false).queue();
            event.getHook().sendMessage("Done").queue();
        }
        else if (event.getName().equals("say") && event.getMember().getId().equals("274775166263885844"))
        {
            event.deferReply(true).queue();
            String message = event.getOption("message").getAsString();
            event.getMessageChannel().sendMessage(message).queue();
            event.getHook().sendMessage("Done").queue();
        }
    }
}
