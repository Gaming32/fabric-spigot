package io.github.gaming32.fabricspigot.api;

import com.google.common.base.Preconditions;
import io.github.gaming32.fabricspigot.util.Conversion;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import org.bukkit.Sound;

public class FabricSound {
    public static SoundEvent getSoundEffect(Sound s) {
        final SoundEvent effect = Registries.SOUND_EVENT.get(Conversion.toIdentifier(s.getKey()));
        Preconditions.checkArgument(effect != null, "Sound effect %s does not exist", s);
        return effect;
    }
}
