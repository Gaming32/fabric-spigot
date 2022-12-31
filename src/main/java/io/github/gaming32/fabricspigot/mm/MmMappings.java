package io.github.gaming32.fabricspigot.mm;

@SuppressWarnings("SpellCheckingInspection") // Hashed go brrr
public final class MmMappings {
    public static final class PlayerManager {
        public static final MmMapping
            CLASS = new MmMapping("class_3324", "unmapped/C_digmgtxw", "server/PlayerManager"),
            onPlayerConnect = new MmMapping("method_14570", "m_ngykykkq", "onPlayerConnect"),
            broadcast = new MmMapping("method_43512", "m_qcclojnk", "broadcast"),
            sendToAll = new MmMapping("method_14581", "m_aanidhbc", "sendToAll")
                ;
    }

    public static final class BossBarManager {
        public static final MmMapping
            CLASS = new MmMapping("class_3004", "unmapped/C_xtwgkoiw", "entity/boss/BossBarManager"),
            onPlayerConnect = new MmMapping("method_12975", "m_llafdarr", "onPlayerConnect")
                ;
    }

    public static final class ServerWorld {
        public static final MmMapping
            CLASS = new MmMapping("class_3218", "unmapped/C_bdwnwhiu", "server/world/ServerWorld")
            ;
    }

    public static final class ServerPlayerEntity {
        public static final MmMapping
            CLASS = new MmMapping("class_3222", "unmapped/C_mxrobsgg", "server/network/ServerPlayerEntity")
            ;
    }

    public static final class MutableText {
        public static final MmMapping
            CLASS = new MmMapping("class_5250", "unmapped/C_npqneive", "text/MutableText")
            ;
    }

    public static final class LivingEntity {
        public static final MmMapping
            CLASS = new MmMapping("class_1309", "unmapped/C_usxaxydn", "entity/LivingEntity"),
            onDeath = new MmMapping("method_6078", "unmapped/m_tflzijcx", "onDeath")
            ;
    }

    public static final class DamageTracker {
        public static final MmMapping
            CLASS = new MmMapping("class_1283", "unmapped/C_xkhemiqp", "entity/damage/DamageTracker"),
            getDeathMessage = new MmMapping("method_5548", "unmapped/m_xmrbfkiy", "getDeathMessage")
                ;
    }

    public static final class Text {
        public static final MmMapping
            CLASS = new MmMapping("class_2561", "unmapped/C_rdaqiwdt", "text/Text")
                ;
    }
}
