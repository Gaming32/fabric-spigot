package io.github.gaming32.fabricspigot.api.help;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.api.command.FabricCommandWrapper;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.help.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class SimpleHelpMap implements HelpMap {
    private HelpTopic defaultTopic;
    private final Map<String, HelpTopic> helpTopics;
    private final Map<Class<?>, HelpTopicFactory<Command>> topicFactoryMap;
    private final FabricServer server;
    private HelpYamlReader yaml;

    public SimpleHelpMap(FabricServer server) {
        helpTopics = new TreeMap<>(HelpTopicComparator.topicNameComparatorInstance());
        topicFactoryMap = new HashMap<>();
        this.server = server;
        yaml = new HelpYamlReader(server);

        Predicate<? super HelpTopic> indexFilter = Predicates.instanceOf(CommandAliasHelpTopic.class).negate();
        if (!yaml.commandTopicsInMasterIndex()) {
            indexFilter = indexFilter.and(topic -> ((HelpTopic)topic).getName().charAt(0) != '/');
        }

        defaultTopic = new IndexHelpTopic("Index", null, null, Collections2.filter(helpTopics.values(), indexFilter::test), "Use /help [n] to get page n of help.");

        registerHelpTopicFactory(MultipleCommandAlias.class, (MultipleCommandAlias alias) -> new MultipleCommandAliasHelpTopic(alias));
    }

    @Nullable
    @Override
    public HelpTopic getHelpTopic(@NotNull String topicName) {
        if (topicName.equals("")) {
            return defaultTopic;
        }
        return helpTopics.get(topicName);
    }

    @NotNull
    @Override
    public Collection<HelpTopic> getHelpTopics() {
        return helpTopics.values();
    }

    @Override
    public void addTopic(@NotNull HelpTopic topic) {
        helpTopics.putIfAbsent(topic.getName(), topic);
    }

    @Override
    public void clear() {
        helpTopics.clear();
    }

    @NotNull
    @Override
    public List<String> getIgnoredPlugins() {
        return yaml.getIgnoredPlugins();
    }

    public synchronized void initializeGeneralTopics() {
        yaml = new HelpYamlReader(server);

        for (final HelpTopic topic : yaml.getGeneralTopics()) {
            addTopic(topic);
        }

        for (final HelpTopic topic : yaml.getIndexTopics()) {
            if (topic.getName().equals("Default")) {
                defaultTopic = topic;
            } else {
                addTopic(topic);
            }
        }
    }

    public synchronized void initializeCommands() {
        final Set<String> ignoredPlugins = new HashSet<>(yaml.getIgnoredPlugins());
        if (ignoredPlugins.contains("All")) return;

        outer:
        for (final Command command : server.getCommandMap().getCommands()) {
            if (commandInIgnoredPlugin(command, ignoredPlugins)) continue ;
            for (final var entry : topicFactoryMap.entrySet()) {
                if (
                    entry.getKey().isAssignableFrom(command.getClass()) ||
                    (command instanceof PluginCommand && entry.getKey().isAssignableFrom(((PluginCommand)command).getExecutor().getClass()))
                ) {
                    final HelpTopic topic = entry.getValue().createTopic(command);
                    if (topic != null) addTopic(topic);
                    continue outer;
                }
            }
            addTopic(new GenericCommandHelpTopic(command));
        }

        for (final Command command : server.getCommandMap().getCommands()) {
            if (commandInIgnoredPlugin(command, ignoredPlugins)) continue;
            for (final String alias : command.getAliases()) {
                if (server.getCommandMap().getCommand(alias) == command) {
                    addTopic(new CommandAliasHelpTopic("/" + alias, "/" + command.getLabel(), this));
                }
            }
        }

        final Collection<HelpTopic> filteredTopics = Collections2.filter(helpTopics.values(), Predicates.instanceOf(CommandAliasHelpTopic.class));
        if (!filteredTopics.isEmpty()) {
            addTopic(new IndexHelpTopic("Aliases", "Lists command aliases", null, filteredTopics));
        }

        final Map<String, Set<HelpTopic>> pluginIndices = new HashMap<>();
        fillPluginIndices(pluginIndices, server.getCommandMap().getCommands());

        for (final var entry : pluginIndices.entrySet()) {
            addTopic(new IndexHelpTopic(entry.getKey(), "All commands for " + entry.getKey(), null, entry.getValue(), "Below is a list of all " + entry.getKey() + " commands:"));
        }

        for (final HelpTopicAmendment amendment : yaml.getTopicAmendments()) {
            if (helpTopics.containsKey(amendment.topicName())) {
                helpTopics.get(amendment.topicName()).amendTopic(amendment.shortText(), amendment.fullText());
                if (amendment.permission() != null) {
                    helpTopics.get(amendment.topicName()).amendCanSee(amendment.permission());
                }
            }
        }
    }

    private void fillPluginIndices(Map<String, Set<HelpTopic>> pluginIndices, Collection<? extends Command> commands) {
        for (final Command command : commands) {
            final String pluginName = getCommandPluginName(command);
            if (pluginName != null) {
                final HelpTopic topic = getHelpTopic("/" + command.getLabel());
                if (topic != null) {
                    pluginIndices.computeIfAbsent(pluginName, key -> new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance())).add(topic);
                }
            }
        }
    }

    private String getCommandPluginName(Command command) {
        if (command instanceof FabricCommandWrapper) {
            return "Fabric";
        }
        if (command instanceof BukkitCommand) {
            return "Bukkit";
        }
        if (command instanceof PluginIdentifiableCommand pluginId) {
            return pluginId.getPlugin().getName();
        }
        return null;
    }

    private boolean commandInIgnoredPlugin(Command command, Set<String> ignoredPlugins) {
        if (command instanceof BukkitCommand && ignoredPlugins.contains("Bukkit")) {
            return true;
        }
        return command instanceof PluginIdentifiableCommand pluginId && ignoredPlugins.contains(pluginId.getPlugin().getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerHelpTopicFactory(@NotNull Class<?> commandClass, @NotNull HelpTopicFactory<?> factory) {
        if (!Command.class.isAssignableFrom(commandClass) && !CommandExecutor.class.isAssignableFrom(commandClass)) {
            throw new IllegalArgumentException("commandClass must implement either Command or CommandExecutor!");
        }
        topicFactoryMap.put(commandClass, (HelpTopicFactory<Command>)factory);
    }
}
