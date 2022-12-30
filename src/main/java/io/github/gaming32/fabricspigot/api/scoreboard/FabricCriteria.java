package io.github.gaming32.fabricspigot.api.scoreboard;

import com.google.common.collect.ImmutableMap;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class FabricCriteria implements Criteria {
    private static final Map<String, FabricCriteria> DEFAULTS;
    private static final FabricCriteria DUMMY;

    static {
        final ImmutableMap.Builder<String, FabricCriteria> defaults = ImmutableMap.builder();

        for (final var entry : ScoreboardCriterion.CRITERIA.entrySet()) {
            final String name = entry.getKey();
            final ScoreboardCriterion criterion = entry.getValue();
            defaults.put(name, new FabricCriteria(criterion));
        }

        DEFAULTS = defaults.build();
        DUMMY = DEFAULTS.get("dummy");
    }

    final ScoreboardCriterion criteria;
    private final String bukkitName;

    private FabricCriteria(String bukkitName) {
        this.bukkitName = bukkitName;
        criteria = DUMMY.criteria;
    }

    private FabricCriteria(ScoreboardCriterion criteria) {
        this.criteria = criteria;
        bukkitName = criteria.getName();
    }

    @NotNull
    @Override
    public String getName() {
        return bukkitName;
    }

    @Override
    public boolean isReadOnly() {
        return criteria.isReadOnly();
    }

    @NotNull
    @Override
    public RenderType getDefaultRenderType() {
        return RenderType.values()[criteria.getDefaultRenderType().ordinal()];
    }

    static FabricCriteria getFromNMS(ScoreboardObjective objective) {
        return DEFAULTS.get(objective.getCriterion().getName());
    }

    static FabricCriteria getFromBukkit(String name) {
        final FabricCriteria criteria = DEFAULTS.get(name);
        if (criteria != null) {
            return criteria;
        }
        return ScoreboardCriterion.getOrCreateStatCriterion(name)
            .map(FabricCriteria::new)
            .orElseGet(() -> new FabricCriteria(name));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FabricCriteria other)) {
            return false;
        }
        return other.bukkitName.equals(bukkitName);
    }
}
