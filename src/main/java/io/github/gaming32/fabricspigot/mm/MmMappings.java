package io.github.gaming32.fabricspigot.mm;

@SuppressWarnings("SpellCheckingInspection") // Hashed go brrr
public final class MmMappings {
    public static final class PlayerManager {
        public static final MmMapping
            CLASS = new MmMapping("class_3324", "unmapped/C_digmgtxw", "server/PlayerManager"),
            ON_PLAYER_CONNECT = new MmMapping("method_14570", "m_ngykykkq", "onPlayerConnect"),
            BROADCAST = new MmMapping("method_43512", "m_qcclojnk", "broadcast"),
            SEND_TO_ALL = new MmMapping("method_14581", "m_aanidhbc", "sendToAll")
                ;
    }

    public static final class BossBarManager {
        public static final MmMapping
            CLASS = new MmMapping("class_3004", "unmapped/C_xtwgkoiw", "entity/boss/BossBarManager"),
            ON_PLAYER_CONNECT = new MmMapping("method_12975", "m_llafdarr", "onPlayerConnect")
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
}
