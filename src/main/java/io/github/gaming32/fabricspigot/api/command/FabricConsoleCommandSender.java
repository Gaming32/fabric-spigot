package io.github.gaming32.fabricspigot.api.command;

import io.github.gaming32.fabricspigot.FabricSpigot;
import io.github.gaming32.fabricspigot.api.conversations.ConversationTracker;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FabricConsoleCommandSender extends ServerCommandSender implements ConsoleCommandSender {
    private static FabricConsoleCommandSender instance;

    public static FabricConsoleCommandSender getInstance() {
        if (instance == null) {
            instance = new FabricConsoleCommandSender();
        }
        return instance;
    }

    private final Spigot spigot = new Spigot() {
    };
    protected final ConversationTracker conversationTracker = new ConversationTracker();

    protected FabricConsoleCommandSender() {
        super();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        sendRawMessage(message);
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        FabricSpigot.LOGGER.info(ChatColor.stripColor(message));
    }

    @Override
    public void sendRawMessage(UUID sender, @NotNull String message) {
        this.sendRawMessage(message); // Console doesn't know of senders
    }

    @Override
    public void sendMessage(String... messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of server console");
    }

    @Override
    public boolean beginConversation(@NotNull Conversation conversation) {
        return conversationTracker.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation) {
        conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details) {
        conversationTracker.abandonConversation(conversation, details);
    }

    @Override
    public void acceptConversationInput(@NotNull String input) {
        conversationTracker.acceptConversationInput(input);
    }

    @Override
    public boolean isConversing() {
        return conversationTracker.isConversing();
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return spigot;
    }
}
