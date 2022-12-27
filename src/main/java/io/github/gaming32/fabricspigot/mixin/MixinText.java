package io.github.gaming32.fabricspigot.mixin;

import com.google.common.collect.Streams;
import io.github.gaming32.fabricspigot.util.Streamable;
import io.github.gaming32.fabricspigot.util.TextStreamable;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.stream.Stream;

@Mixin(Text.class)
public interface MixinText extends TextStreamable {
    @Shadow List<Text> getSiblings();

    @NotNull
    @Override
    default Stream<Text> stream() {
        return Stream.concat(Stream.of((Text)this), getSiblings().stream().flatMap(Text::stream));
    }
}
