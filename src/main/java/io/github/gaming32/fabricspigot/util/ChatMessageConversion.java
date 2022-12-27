package io.github.gaming32.fabricspigot.util;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import org.bukkit.ChatColor;

import java.util.Optional;

public class ChatMessageConversion {
    public static String componentToSpigot(Text component) {
        if (component == null) return "";
        final StringBuilder result = new StringBuilder();
        boolean hadFormat = false;
        for (final Text sub : component) {
            final Style style = sub.getStyle();
            final TextColor color = style.getColor();
            if (sub.getContent() != TextContent.EMPTY || color != null) {
                if (color != null) {
                    final String name = color.getName();
                    final Formatting formatting = Formatting.byName(name);
                    if (formatting != null) {
                        result.append(formatting);
                    } else {
                        result.append(Formatting.FORMATTING_CODE_PREFIX).append('x');
                        for (final char magic : name.substring(1).toCharArray()) {
                            result.append(Formatting.FORMATTING_CODE_PREFIX).append(magic);
                        }
                    }
                    hadFormat = true;
                } else if (hadFormat) {
                    result.append(ChatColor.RESET);
                    hadFormat = false;
                }
            }
            if (style.isBold()) {
                result.append(Formatting.BOLD);
                hadFormat = true;
            }
            if (style.isItalic()) {
                result.append(Formatting.ITALIC);
                hadFormat = true;
            }
            if (style.isUnderlined()) {
                result.append(Formatting.UNDERLINE);
                hadFormat = true;
            }
            if (style.isStrikethrough()) {
                result.append(Formatting.STRIKETHROUGH);
                hadFormat = true;
            }
            if (style.isObfuscated()) {
                result.append(Formatting.OBFUSCATED);
                hadFormat = true;
            }
            sub.getContent().visit(x -> {
                result.append(x);
                return Optional.empty();
            });
        }
        return result.toString();
    }
}
