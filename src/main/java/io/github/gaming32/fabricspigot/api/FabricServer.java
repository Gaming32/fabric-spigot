package io.github.gaming32.fabricspigot.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.gaming32.fabricspigot.FabricSpigot;
import io.github.gaming32.fabricspigot.api.command.BukkitCommandWrapper;
import io.github.gaming32.fabricspigot.api.command.FabricCommandMap;
import io.github.gaming32.fabricspigot.api.command.FabricCommandWrapper;
import io.github.gaming32.fabricspigot.api.command.FabricConsoleCommandSender;
import io.github.gaming32.fabricspigot.api.help.SimpleHelpMap;
import io.github.gaming32.fabricspigot.api.scoreboard.FabricScoreboardManager;
import io.github.gaming32.fabricspigot.ext.EntityExt;
import io.github.gaming32.fabricspigot.ext.RecipeManagerExt;
import io.github.gaming32.fabricspigot.ext.ServerWorldExt;
import io.github.gaming32.fabricspigot.util.ChatMessageConversion;
import io.github.gaming32.fabricspigot.util.CommandNodeAccess;
import io.github.gaming32.fabricspigot.util.Conversion;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.WorldPresets;
import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.loot.LootTable;
import org.bukkit.map.MapView;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.CachedServerIcon;
import org.bukkit.util.StringUtil;
import org.bukkit.util.permissions.DefaultPermissions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FabricServer implements Server {
    private final Spigot spigot = new Spigot() {
        private final YamlConfiguration config = new YamlConfiguration();

        @NotNull
        @Override
        public YamlConfiguration getConfig() {
            return config;
        }
    };

    private final FabricCommandMap commandMap = new FabricCommandMap(this);
    private final SimpleHelpMap helpMap = new SimpleHelpMap(this);
    private final Set<String> successfullyRegisteredCommands = new HashSet<>();
    private final SimplePluginManager pluginManager = new SimplePluginManager(this, commandMap);
    private final Map<String, World> worlds = new LinkedHashMap<>();
    private final Map<Class<?>, Registry<?>> registries = new HashMap<>();
    private FabricScoreboardManager scoreboardManager;
    private boolean commandSyncReady;
    private List<? extends Player> onlinePlayers;
    private Integer overrideSpawnProtection;
    private MinecraftServer server;

    static {
        ConfigurationSerialization.registerClass(FabricOfflinePlayer.class);
    }

    public MinecraftServer getHandle() {
        if (server == null) {
            throw new IllegalStateException("Server not running.");
        }
        return server;
    }

    public void setServer(MinecraftServer server) {
        if ((server != null) == (this.server != null)) {
            throw new IllegalStateException("Attempted to set handle with already matching server state");
        }
        this.server = server;
        commandSyncReady = false;
        if (server != null) {
            server.setBukkitServer(this);
            //noinspection StaticPseudoFunctionalStyleMethod
            onlinePlayers = Collections.unmodifiableList(Lists.transform(
                server.getPlayerManager().getPlayerList(),
                player -> (Player)((EntityExt)player).getBukkitEntity()
            ));
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "FabricSpigot";
    }

    @NotNull
    @Override
    public String getVersion() {
        return FabricSpigot.getModVersion() + " (MC: " + SharedConstants.getGameVersion().getName() + ')';
    }

    @NotNull
    @Override
    public String getBukkitVersion() {
        return "1.19.3-R0.1-SNAPSHOT";
    }

    @NotNull
    @Override
    public Collection<? extends Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public int getMaxPlayers() {
        return getHandle().getMaxPlayerCount();
    }

    @Override
    public int getPort() {
        return getHandle().getServerPort();
    }

    @Override
    public int getViewDistance() {
        return getHandle().getPlayerManager().getViewDistance();
    }

    @Override
    public int getSimulationDistance() {
        return getHandle().getPlayerManager().getSimulationDistance();
    }

    @NotNull
    @Override
    public String getIp() {
        return getHandle().getServerIp();
    }

    @NotNull
    @Override
    public String getWorldType() {
        try {
            return getHandle() instanceof DedicatedServer dedicated
                ? dedicated.getProperties().worldGenProperties.levelType
                : WorldPresets.getWorldPreset(
                    MinecraftClient.getInstance()
                        .createIntegratedServerLoader()
                        .loadForRecreation(getHandle().session)
                        .getSecond()
                        .selectedDimensions()
                        .dimensions()
                ).map(key -> FabricSpigot.toShortString(key.getValue()))
                .orElse("default");
        } catch (Exception e) {
            return "default";
        }
    }

    @Override
    public boolean getGenerateStructures() {
        return getHandle().getSaveProperties().getGeneratorOptions().shouldGenerateStructures();
    }

    @Override
    public int getMaxWorldSize() {
        return getHandle() instanceof DedicatedServer dedicated ? dedicated.getProperties().maxWorldSize : 29999984;
    }

    @Override
    public boolean getAllowEnd() {
        return true;
    }

    @Override
    public boolean getAllowNether() {
        return !(getHandle() instanceof DedicatedServer dedicated) || dedicated.getProperties().allowNether;
    }

    @NotNull
    @Override
    public String getResourcePack() {
        return getHandle() instanceof DedicatedServer dedicated
            ? dedicated.getProperties().serverResourcePackProperties
                .map(MinecraftServer.ServerResourcePackProperties::url)
                .orElse("")
            : "";
    }

    @NotNull
    @Override
    public String getResourcePackHash() {
        return getHandle() instanceof DedicatedServer dedicated
            ? dedicated.getProperties().serverResourcePackProperties
                .map(MinecraftServer.ServerResourcePackProperties::hash)
                .orElse("")
            : "";
    }

    @NotNull
    @Override
    public String getResourcePackPrompt() {
        return getHandle() instanceof DedicatedServer dedicated
            ? dedicated.getProperties().serverResourcePackProperties
                .map(MinecraftServer.ServerResourcePackProperties::prompt)
                .map(ChatMessageConversion::fromComponent)
                .orElse("")
            : "";
    }

    @Override
    public boolean isResourcePackRequired() {
        return !(getHandle() instanceof DedicatedServer dedicated) ||
            dedicated.getProperties().serverResourcePackProperties
                .map(MinecraftServer.ServerResourcePackProperties::isRequired)
                .orElse(false);
    }

    @Override
    public boolean hasWhitelist() {
        return getHandle().getPlayerManager().isWhitelistEnabled();
    }

    @Override
    public void setWhitelist(boolean value) {
        getHandle().getPlayerManager().setWhitelistEnabled(value);
    }

    @Override
    public boolean isWhitelistEnforced() {
        return getHandle().isEnforceWhitelist();
    }

    @Override
    public void setWhitelistEnforced(boolean value) {
        getHandle().setEnforceWhitelist(value);
    }

    @NotNull
    @Override
    public Set<OfflinePlayer> getWhitelistedPlayers() {
        throw new NotImplementedYet();
    }

    @Override
    public void reloadWhitelist() {
        getHandle().getPlayerManager().reloadWhitelist();
    }

    @Override
    public int broadcastMessage(@NotNull String message) {
        return broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    @NotNull
    @Override
    public String getUpdateFolder() {
        return "update";
    }

    @NotNull
    @Override
    public File getUpdateFolderFile() {
        return new File("plugins", "update");
    }

    @Override
    public long getConnectionThrottle() {
        return 0;
    }

    @Override
    @Deprecated
    public int getTicksPerAnimalSpawns() {
        return getTicksPerSpawns(SpawnCategory.ANIMAL);
    }

    @Override
    @Deprecated
    public int getTicksPerMonsterSpawns() {
        return getTicksPerSpawns(SpawnCategory.MONSTER);
    }

    @Override
    @Deprecated
    public int getTicksPerWaterSpawns() {
        return getTicksPerSpawns(SpawnCategory.WATER_ANIMAL);
    }

    @Override
    @Deprecated
    public int getTicksPerWaterAmbientSpawns() {
        return getTicksPerSpawns(SpawnCategory.WATER_AMBIENT);
    }

    @Override
    @Deprecated
    public int getTicksPerWaterUndergroundCreatureSpawns() {
        return getTicksPerSpawns(SpawnCategory.WATER_UNDERGROUND_CREATURE);
    }

    @Override
    @Deprecated
    public int getTicksPerAmbientSpawns() {
        return getTicksPerSpawns(SpawnCategory.AMBIENT);
    }

    @Override
    public int getTicksPerSpawns(@NotNull SpawnCategory spawnCategory) {
        Validate.notNull(spawnCategory, "SpawnCategory cannot be null");
        Validate.isTrue(spawnCategory != SpawnCategory.MISC, "SpawnCategory.MISC are not supported");
        return spawnCategory == SpawnCategory.ANIMAL ? 400 : 1;
    }

    @Nullable
    @Override
    public Player getPlayer(@NotNull String name) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Player getPlayerExact(@NotNull String name) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public List<Player> matchPlayer(@NotNull String name) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Player getPlayer(@NotNull UUID id) {
        final ServerPlayerEntity player = getHandle().getPlayerManager().getPlayer(id);
        return player != null ? (Player)((EntityExt)player).getBukkitEntity() : null;
    }

    @NotNull
    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @NotNull
    @Override
    public BukkitScheduler getScheduler() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public ServicesManager getServicesManager() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public List<World> getWorlds() {
        return new ArrayList<>(worlds.values());
    }

    @Nullable
    @Override
    public World createWorld(@NotNull WorldCreator creator) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean unloadWorld(@NotNull String name, boolean save) {
        //noinspection DataFlowIssue
        return unloadWorld(getWorld(name), save);
    }

    @Override
    public boolean unloadWorld(@NotNull World world, boolean save) {
        //noinspection ConstantValue
        if (world == null) return false;

        final ServerWorld handle = ((FabricWorld)world).getHandle();

        if (server.getWorld(handle.getRegistryKey()) == null) {
            return false;
        }

        if (handle.getRegistryKey() == net.minecraft.world.World.OVERWORLD) {
            return false;
        }

        if (handle.getPlayers().size() > 0) {
            return false;
        }

        final WorldUnloadEvent e = new WorldUnloadEvent(((ServerWorldExt)handle).getBukkitWorld());
        pluginManager.callEvent(e);

        if (e.isCancelled()) return false;

        try {
            if (save) {
                handle.save(null, true, true);
            }

            handle.getChunkManager().close();
            if (save) {
                handle.entityManager.flush();
            }
            handle.entityManager.dataAccess.close();
            ((ServerWorldExt)handle).getSession().close();
        } catch (Exception ex) {
            FabricSpigot.LOGGER.error(null, ex);
        }

        worlds.remove(world.getName().toLowerCase(Locale.ENGLISH));
        server.removeWorld(handle);
        return true;
    }

    @Nullable
    @Override
    public World getWorld(@NotNull String name) {
        Validate.notNull(name, "Name cannot be null");
        return worlds.get(name.toLowerCase(Locale.ENGLISH));
    }

    @Nullable
    @Override
    public World getWorld(@NotNull UUID uid) {
        for (final World world : worlds.values()) {
            if (world.getUID().equals(uid)) {
                return world;
            }
        }
        return null;
    }

    public void addWorld(World world) {
        if (getWorld(world.getUID()) != null) {
            FabricSpigot.LOGGER.warn("World " + world.getName() + " was attempted to be loaded twice by a Bukkit plugin.");
            return;
        }
        worlds.put(world.getName().toLowerCase(Locale.ENGLISH), world);
    }

    @NotNull
    @Override
    public WorldBorder createWorldBorder() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public MapView getMap(int id) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public MapView createMap(@NotNull World world) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public ItemStack createExplorerMap(@NotNull World world, @NotNull Location location, @NotNull StructureType structureType) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public ItemStack createExplorerMap(@NotNull World world, @NotNull Location location, @NotNull StructureType structureType, int radius, boolean findUnexplored) {
        throw new NotImplementedYet();
    }

    @Override
    public void reload() {
        throw new NotImplementedYet();
    }

    @Override
    public void reloadData() {
        getHandle().getCommandManager().executeWithPrefix(getHandle().getCommandSource(), "reload");
    }

    @NotNull
    @Override
    public Logger getLogger() {
        return Logger.getLogger(FabricSpigot.LOGGER.getName());
    }

    @Nullable
    @Override
    public PluginCommand getPluginCommand(@NotNull String name) {
        final Command command = commandMap.getCommand(name);
        return command instanceof PluginCommand pluginCommand ? pluginCommand : null;
    }

    @Override
    public void savePlayers() {
        getHandle().getPlayerManager().saveAllPlayerData();
    }

    @Override
    public boolean dispatchCommand(@NotNull CommandSender sender, @NotNull String commandLine) throws CommandException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(commandLine, "CommandLine cannot be null");

        if (commandMap.dispatch(sender, commandLine)) {
            return true;
        }

        if (sender instanceof Player) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
        } else {
            sender.sendMessage("Unknown command. Type \"help\" for help.");
        }

        return false;
    }

    @Override
    public boolean addRecipe(@Nullable Recipe recipe) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public List<Recipe> getRecipesFor(@NotNull ItemStack result) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Recipe getRecipe(@NotNull NamespacedKey recipeKey) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Recipe getCraftingRecipe(@NotNull ItemStack[] craftingMatrix, @NotNull World world) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public ItemStack craftItem(@NotNull ItemStack[] craftingMatrix, @NotNull World world, @NotNull Player player) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Iterator<Recipe> recipeIterator() {
        throw new NotImplementedYet();
    }

    @Override
    public void clearRecipes() {
        ((RecipeManagerExt)getHandle().getRecipeManager()).clearRecipes();
    }

    @Override
    public void resetRecipes() {
        reloadData();
    }

    @Override
    public boolean removeRecipe(@NotNull NamespacedKey key) {
        return ((RecipeManagerExt)getHandle().getRecipeManager()).removeRecipe(Conversion.toIdentifier(key));
    }

    @NotNull
    @Override
    public Map<String, String[]> getCommandAliases() {
        return Map.of();
    }

    @Override
    public int getSpawnRadius() {
        return getHandle().getSpawnProtectionRadius();
    }

    @Override
    public void setSpawnRadius(int value) {
        overrideSpawnProtection = value;
    }

    public Integer getOverrideSpawnProtection() {
        return overrideSpawnProtection;
    }

    @Override
    public boolean shouldSendChatPreviews() {
        return false;
    }

    @Override
    public boolean isEnforcingSecureProfiles() {
        return server.shouldEnforceSecureProfile();
    }

    @Override
    public boolean getHideOnlinePlayers() {
        return server.hideOnlinePlayers();
    }

    @Override
    public boolean getOnlineMode() {
        return server.isOnlineMode();
    }

    @Override
    public boolean getAllowFlight() {
        return this instanceof DedicatedServer dedicated && dedicated.getProperties().allowFlight;
    }

    @Override
    public boolean isHardcore() {
        return server.isHardcore();
    }

    @Override
    public void shutdown() {
        server.shutdown();
    }

    @Override
    public int broadcast(@NotNull String message, @NotNull String permission) {
        final Set<CommandSender> recipients = new HashSet<>();
        for (final Permissible permissible : getPluginManager().getPermissionSubscriptions(permission)) {
            if (permissible instanceof CommandSender sender && permissible.hasPermission(permission)) {
                recipients.add(sender);
            }
        }

        final BroadcastMessageEvent event = new BroadcastMessageEvent(!Bukkit.isPrimaryThread(), message, recipients);
        getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return 0;
        }

        message = event.getMessage();

        for (final CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    @NotNull
    @Override
    public OfflinePlayer getOfflinePlayer(@NotNull String name) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public OfflinePlayer getOfflinePlayer(@NotNull UUID id) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public PlayerProfile createPlayerProfile(@Nullable UUID uniqueId, @Nullable String name) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public PlayerProfile createPlayerProfile(@NotNull UUID uniqueId) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public PlayerProfile createPlayerProfile(@NotNull String name) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<String> getIPBans() {
        return Set.of(getHandle().getPlayerManager().getIpBanList().getNames());
    }

    @Override
    public void banIP(@NotNull String address) {
        throw new NotImplementedYet();
    }

    @Override
    public void unbanIP(@NotNull String address) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<OfflinePlayer> getBannedPlayers() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public BanList getBanList(@NotNull BanList.Type type) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<OfflinePlayer> getOperators() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public GameMode getDefaultGameMode() {
        return Objects.requireNonNull(GameMode.getByValue(getHandle().getDefaultGameMode().getId()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setDefaultGameMode(@NotNull GameMode mode) {
        getHandle().setDefaultGameMode(net.minecraft.world.GameMode.byId(mode.getValue()));
    }

    @NotNull
    @Override
    public ConsoleCommandSender getConsoleSender() {
        return FabricConsoleCommandSender.getInstance();
    }

    @NotNull
    @Override
    public File getWorldContainer() {
        return getHandle().getSavePath(WorldSavePath.ROOT).getParent().toFile();
    }

    @NotNull
    @Override
    public OfflinePlayer[] getOfflinePlayers() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Messenger getMessenger() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public HelpMap getHelpMap() {
        return helpMap;
    }

    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    @NotNull
    @Override
    public Inventory createInventory(@Nullable InventoryHolder owner, @NotNull InventoryType type) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Inventory createInventory(@Nullable InventoryHolder owner, @NotNull InventoryType type, @NotNull String title) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Inventory createInventory(@Nullable InventoryHolder owner, int size) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Inventory createInventory(@Nullable InventoryHolder owner, int size, @NotNull String title) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Merchant createMerchant(@Nullable String title) {
        throw new NotImplementedYet();
    }

    @Override
    public int getMaxChainedNeighborUpdates() {
        return getHandle().getMaxChainedNeighborUpdates();
    }

    @Override
    @Deprecated
    public int getMonsterSpawnLimit() {
        return getSpawnLimit(SpawnCategory.MONSTER);
    }

    @Override
    @Deprecated
    public int getAnimalSpawnLimit() {
        return getSpawnLimit(SpawnCategory.ANIMAL);
    }

    @Override
    @Deprecated
    public int getWaterAnimalSpawnLimit() {
        return getSpawnLimit(SpawnCategory.WATER_ANIMAL);
    }

    @Override
    @Deprecated
    public int getWaterAmbientSpawnLimit() {
        return getSpawnLimit(SpawnCategory.WATER_AMBIENT);
    }

    @Override
    @Deprecated
    public int getWaterUndergroundCreatureSpawnLimit() {
        return getSpawnLimit(SpawnCategory.WATER_UNDERGROUND_CREATURE);
    }

    @Override
    @Deprecated
    public int getAmbientSpawnLimit() {
        return getSpawnLimit(SpawnCategory.AMBIENT);
    }

    @Override
    public int getSpawnLimit(@NotNull SpawnCategory spawnCategory) {
        return -1;
    }

    @Override
    public boolean isPrimaryThread() {
        return getHandle().isOnThread();
    }

    @NotNull
    @Override
    public String getMotd() {
        return getHandle().getServerMotd();
    }

    @Nullable
    @Override
    public String getShutdownMessage() {
        return "Server closed";
    }

    @NotNull
    @Override
    public Warning.WarningState getWarningState() {
        return Warning.WarningState.DEFAULT;
    }

    public List<String> tabComplete(CommandSender sender, String message, ServerWorld world, Vec3d pos, boolean forceCommand) {
        if (!(sender instanceof final Player player)) {
            return ImmutableList.of();
        }

        final List<String> offers;
        if (forceCommand || message.startsWith("/")) {
            offers = tabCompleteCommand(player, message, world, pos);
        } else {
            offers = tabCompleteChat(player, message);
        }

        final TabCompleteEvent tabEvent = new TabCompleteEvent(player, message, offers);
        getPluginManager().callEvent(tabEvent);

        return tabEvent.isCancelled() ? Collections.emptyList() : tabEvent.getCompletions();
    }

    public List<String> tabCompleteCommand(Player player, String message, ServerWorld world, Vec3d pos) {
        List<String> completions = null;
        try {
            if (message.startsWith("/")) {
                message = message.substring(1);
            }
            if (pos == null) {
                completions = commandMap.tabComplete(player, message);
            } else {
                completions = commandMap.tabComplete(player, message, new Location(((ServerWorldExt)world).getBukkitWorld(), pos.x, pos.y, pos.z));
            }
        } catch (CommandException ex) {
            player.sendMessage(ChatColor.RED + "An internal error occurred while attempting to tab-complete this command");
            getLogger().log(Level.SEVERE, "Exception when " + player.getName() + " attempted to tab complete " + message, ex);
        }

        return completions == null ? ImmutableList.of() : completions;
    }

    @SuppressWarnings("deprecation")
    public List<String> tabCompleteChat(Player player, String message) {
        final List<String> completions = new ArrayList<>();
        final PlayerChatTabCompleteEvent event = new PlayerChatTabCompleteEvent(player, message, completions);
        String token = event.getLastToken();
        for (Player p : getOnlinePlayers()) {
            if (player.canSee(p) && StringUtil.startsWithIgnoreCase(p.getName(), token)) {
                completions.add(p.getName());
            }
        }
        pluginManager.callEvent(event);

        // Sanity
        completions.removeIf(Objects::isNull);
        completions.sort(String.CASE_INSENSITIVE_ORDER);
        return completions;
    }

    @NotNull
    @Override
    public ItemFactory getItemFactory() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public void setScoreboardManager(FabricScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }

    @NotNull
    @Override
    public Criteria getScoreboardCriteria(@NotNull String name) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public CachedServerIcon getServerIcon() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public CachedServerIcon loadServerIcon(@NotNull File file) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public CachedServerIcon loadServerIcon(@NotNull BufferedImage image) {
        throw new NotImplementedYet();
    }

    @Override
    public void setIdleTimeout(int threshold) {
        getHandle().setPlayerIdleTimeout(threshold);
    }

    @Override
    public int getIdleTimeout() {
        return getHandle().getPlayerIdleTimeout();
    }

    @NotNull
    @Override
    public ChunkGenerator.ChunkData createChunkData(@NotNull World world) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public BossBar createBossBar(@Nullable String title, @NotNull BarColor color, @NotNull BarStyle style, @NotNull BarFlag... flags) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public KeyedBossBar createBossBar(@NotNull NamespacedKey key, @Nullable String title, @NotNull BarColor color, @NotNull BarStyle style, @NotNull BarFlag... flags) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Iterator<KeyedBossBar> getBossBars() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public KeyedBossBar getBossBar(@NotNull NamespacedKey key) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean removeBossBar(@NotNull NamespacedKey key) {
        final BossBarManager manager = getHandle().getBossBarManager();
        final CommandBossBar bar = manager.get(Conversion.toIdentifier(key));
        if (bar != null) {
            manager.remove(bar);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Entity getEntity(@NotNull UUID uuid) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Advancement getAdvancement(@NotNull NamespacedKey key) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Iterator<Advancement> advancementIterator() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public BlockData createBlockData(@NotNull Material material) {
        return createBlockData(material, (String)null);
    }

    @NotNull
    @Override
    public BlockData createBlockData(@NotNull Material material, @Nullable Consumer<BlockData> consumer) {
        final BlockData data = createBlockData(material);
        if (consumer != null) {
            consumer.accept(data);
        }
        return data;
    }

    @NotNull
    @Override
    public BlockData createBlockData(@NotNull String data) throws IllegalArgumentException {
        //noinspection ConstantValue
        Validate.isTrue(data != null, "Must provide data");
        return createBlockData(null, data);
    }

    @NotNull
    @Override
    public BlockData createBlockData(@Nullable Material material, @Nullable String data) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public <T extends Keyed> Tag<T> getTag(@NotNull String registry, @NotNull NamespacedKey tag, @NotNull Class<T> clazz) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public <T extends Keyed> Iterable<Tag<T>> getTags(@NotNull String registry, @NotNull Class<T> clazz) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public LootTable getLootTable(@NotNull NamespacedKey key) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public List<Entity> selectEntities(@NotNull CommandSender sender, @NotNull String selector) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public StructureManager getStructureManager() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Keyed> Registry<T> getRegistry(@NotNull Class<T> tClass) {
        return (Registry<T>)registries.computeIfAbsent(tClass, key -> FabricRegistry.createRegistry(tClass, getHandle().getRegistryManager()));
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public UnsafeValues getUnsafe() {
        return FabricUnsafeValues.INSTANCE;
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return spigot;
    }

    @Override
    public void sendPluginMessage(@NotNull Plugin source, @NotNull String channel, byte @NotNull [] message) {
        StandardMessenger.validatePluginMessage(getMessenger(), source, channel, message);
        for (final Player player : getOnlinePlayers()) {
            player.sendPluginMessage(source, channel, message);
        }
    }

    @NotNull
    @Override
    public Set<String> getListeningPluginChannels() {
        final Set<String> result = new HashSet<>();
        for (final Player player : getOnlinePlayers()) {
            result.addAll(player.getListeningPluginChannels());
        }
        return result;
    }

    public void loadPlugins() {
        pluginManager.registerInterface(JavaPluginLoader.class);

        if (FabricSpigot.PLUGINS_DIR.exists()) {
            for (final Plugin plugin : pluginManager.loadPlugins(FabricSpigot.PLUGINS_DIR)) {
                try {
                    plugin.getLogger().info(String.format("Loading %1$s", plugin.getDescription().getFullName()));
                    plugin.onLoad();
                } catch (Throwable ex) {
                    FabricSpigot.LOGGER.error(ex.getMessage() + " initializing " + plugin.getDescription().getName() + " (Is it up to date?)", ex);
                }
            }
        } else if (!FabricSpigot.PLUGINS_DIR.mkdir()) {
            FabricSpigot.LOGGER.warn("Failed to create plugins dir");
        }
    }

    public void enablePlugins(PluginLoadOrder type) {
        if (type == PluginLoadOrder.STARTUP) {
            helpMap.clear();
            helpMap.initializeGeneralTopics();
        }

        for (final Plugin plugin : pluginManager.getPlugins()) {
            if (!plugin.isEnabled() && plugin.getDescription().getLoad() == type) {
                enablePlugin(plugin);
            }
        }

        if (type == PluginLoadOrder.POSTWORLD) {
            commandMap.setFallbackCommands();
            setVanillaCommands();
            commandMap.registerServerAliases();
            DefaultPermissions.registerCorePermissions();
            // TODO: FabricDefaultPermissions.registerCorePermissions?
            // TODO: More permissions and command stuff
            helpMap.initializeCommands();
            syncCommands();
        }
    }

    @SuppressWarnings("deprecation")
    private void enablePlugin(Plugin plugin) {
        try {
            final List<Permission> perms = plugin.getDescription().getPermissions();

            for (final Permission perm : perms) {
                try {
                    pluginManager.addPermission(perm, false);
                } catch (IllegalArgumentException ex) {
                    FabricSpigot.LOGGER.warn("Plugin " + plugin.getDescription().getFullName() + " tried to register permission '" + perm.getName() + "' but it's already registered", ex);
                }
            }

            pluginManager.dirtyPermissibles();
            pluginManager.enablePlugin(plugin);
        } catch (Throwable ex) {
            FabricSpigot.LOGGER.error(ex.getMessage() + " loading " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
        }
    }

    private void setVanillaCommands() {
        final CommandManager commandManager = getHandle().getCommandManager();
        for (final var command : commandManager.getDispatcher().getRoot().getChildren()) {
            if (!(command instanceof LiteralCommandNode<ServerCommandSource> node)) {
                FabricSpigot.LOGGER.warn("Child of root command node {} was not a literal.", command);
                continue;
            }
            if (node.getName().equals("fabric:help")) continue; // Don't create a /fabric:fabric:help
            commandMap.register("fabric", new FabricCommandWrapper(commandManager, node));
        }
    }

    public void reloadVanillaCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (server != null && commandSyncReady) {
            successfullyRegisteredCommands.clear();
            syncCommands(dispatcher);
        }
    }

    public void syncCommands() {
        syncCommands(getHandle().getCommandManager().getDispatcher());
    }

    private void syncCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        commandSyncReady = false;

        for (final var entry : commandMap.getKnownCommands().entrySet()) {
            final String label = entry.getKey();
            final Command command = entry.getValue();

            if (dispatcher.getRoot().getChild(label) != null) {
                if (commandMap.getCommand("fabric:" + label) != command) {
                    FabricSpigot.LOGGER.info("Skipped registering command /{}, as it was already added by Vanilla or a Fabric mod.", label);
                }
                continue;
            }

            successfullyRegisteredCommands.add(label);
            if (command instanceof FabricCommandWrapper fabricCommand) {
                final var node = fabricCommand.fabricCommand;
                final LiteralCommandNode<ServerCommandSource> copy = new LiteralCommandNode<>(
                    label, node.getCommand(), node.getRequirement(),
                    node.getRedirect(), node.getRedirectModifier(), node.isFork()
                );
                for (final var child : node.getChildren()) {
                    copy.addChild(child);
                }
                dispatcher.getRoot().addChild(copy);
            } else {
                new BukkitCommandWrapper(this, command).register(dispatcher, label);
            }
        }

        resendCommands();

        commandSyncReady = true;
    }

    private void resendCommands() {
        for (final ServerPlayerEntity player : getHandle().getPlayerManager().getPlayerList()) {
            server.getCommandManager().sendCommandTree(player);
        }
    }

    public void disablePlugins() {
        final CommandNode<?> root = server.getCommandManager().getDispatcher().getRoot();
        for (final String toRemove : successfullyRegisteredCommands) {
            ((CommandNodeAccess)root).getChildren().remove(toRemove);
            ((CommandNodeAccess)root).getLiterals().remove(toRemove);
        }
        successfullyRegisteredCommands.clear();
        resendCommands();
        pluginManager.disablePlugins();
    }
}
