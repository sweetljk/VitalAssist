package com.sweetljk.vitalassist.cine;

/**
 * 电影视角设置快照。每 tick 捕获一次，镜头计算不再逐字段读配置。
 *
 * <p>配置本身仍由 malilib 的 {@code Configs} 管理（落盘、界面都由 malilib 负责），
 * 这里只是把界面上的一组值拍平成一个不可变记录。</p>
 */
public record CineConfig(boolean enabled,
                         int idleSeconds,
                         boolean beatSync,
                         int shotBeats,
                         double minShotSeconds,
                         double speed,
                         int environmentPercent,
                         double fovPunch,
                         double handheld,
                         boolean hideHud,
                         boolean bars,
                         int barPercent,
                         boolean transition,
                         int transitionEffect,
                         double transitionSeconds,
                         boolean mobFocus,
                         double mobRange,
                         double trackingSoftness,
                         boolean excludeHostile,
                         boolean excludeBats,
                         boolean lyrics,
                         boolean statusMessages) {

    public static CineConfig snapshot() {
        try {
            return new CineConfig(
                    com.sweetljk.vitalassist.config.Configs.CINE_ENABLED.getBooleanValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_IDLE_SECONDS.getIntegerValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_BEAT_SYNC.getBooleanValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_SHOT_BEATS.getIntegerValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_MIN_SHOT_SECONDS.getDoubleValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_SPEED.getDoubleValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_ENVIRONMENT_PERCENT.getIntegerValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_FOV_PUNCH.getDoubleValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_HANDHELD.getDoubleValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_HIDE_HUD.getBooleanValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_BARS.getBooleanValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_BAR_PERCENT.getIntegerValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_TRANSITION.getBooleanValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_TRANSITION_EFFECT.getIntegerValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_TRANSITION_SECONDS.getDoubleValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_MOB_FOCUS.getBooleanValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_MOB_RANGE.getDoubleValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_TRACKING_SOFTNESS.getDoubleValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_EXCLUDE_HOSTILE.getBooleanValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_EXCLUDE_BATS.getBooleanValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_LYRICS.getBooleanValue(),
                    com.sweetljk.vitalassist.config.Configs.CINE_STATUS_MESSAGES.getBooleanValue());
        } catch (LinkageError | RuntimeException failure) {
            return defaults();
        }
    }

    public static CineConfig defaults() {
        return new CineConfig(false, 10, true, 4, 3.0d, 0.7d, 50, 8.0d, 0.4d,
                true, true, 13, true, 0, 1.0d, true, 32.0d, 0.8d,
                true, true, true, true);
    }

    /** 转场特效：0 = 随机，1..5 = 指定。 */
    public int transitionMask() {
        if (!transition) return 0;
        if (transitionEffect <= 0 || transitionEffect > 5) return 0b11111;
        return 1 << (transitionEffect - 1);
    }

    public long halfTransitionNanos() {
        return (long) (transitionSeconds * 0.5e9d);
    }
}
