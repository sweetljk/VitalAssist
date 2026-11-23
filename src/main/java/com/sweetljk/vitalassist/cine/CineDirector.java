package com.sweetljk.vitalassist.cine;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 电影视角主线程入口。{@link CineSession} 独占调度状态；相机、FOV 与遮罩只消费不可变帧。
 *
 * <p>世界对象不保留到渲染阶段，重复采样同一 partialTick 不会推进镜头或消费切镜标记。</p>
 */
public final class CineDirector {

    private static final Logger LOG = LoggerFactory.getLogger("VitalAssist/Cine");
    private static final CineSession SESSION = new CineSession();
    private static volatile CineSession.Frame frame = CineSession.Frame.IDLE;
    private static volatile CineBeatGrid gridSnapshot = CineBeatGrid.NONE;
    private static volatile boolean musicPlaying;
    private static volatile long musicPositionMillis;
    private static volatile long musicSongId;
    private static boolean manualRequested;
    private static boolean exitRequested;

    /** 主线程场景窗口；渲染线程不得调用这些方法或持有实现对象。 */
    public interface SceneProbe {
        Object worldIdentity();

        Object playerIdentity();

        double playerX();

        double playerY();

        double playerZ();

        double playerEyeY();

        float playerYaw();

        float playerPitch();

        Object openScreen();

        boolean hurtThisTick();

        boolean inputThisTick();

        boolean skyVisible();

        int dimensionKind();

        String biomeKey();

        long timeOfDay();

        /** 活着且本地玩家持有相机；观战、外部相机、暂停时不可接管。 */
        boolean playerAlive();

        /** 返回从起点到目标间可达的相机位置，含近裁面余量；不命中返回目标。 */
        Vec3Holder clipCamera(double fromX, double fromY, double fromZ,
                              double toX, double toY, double toZ);

        MobSample nearestMovingMob(double rangeBlocks);

        /** 按实体标识刷新同一个主体，离场/失效返回 null。 */
        MobSample trackedMob(int entityId, double rangeBlocks);
    }

    public record Vec3Holder(double x, double y, double z) {
    }

    public record MobSample(int entityId, double previousX, double previousY, double previousZ,
                            double x, double y, double z, float bbHeight) {
    }

    public record TransitionFrame(int effect, double progress, boolean covering) {
    }

    private CineDirector() {
    }

    public static boolean isActive() {
        return frame.active();
    }

    public static boolean manualActive() {
        return SESSION.manual();
    }

    public static CineBeatGrid currentGridSnapshot() {
        return gridSnapshot;
    }

    public static boolean musicPlaying() {
        return musicPlaying;
    }

    public static long musicPositionMillis() {
        return musicPositionMillis;
    }

    public static long musicSongId() {
        return musicSongId;
    }

    /** 手动进入/退出的开关请求；下一 tick 生效。 */
    public static void requestManualToggle() {
        if (isActive()) {
            exitRequested = true;
        } else {
            manualRequested = true;
        }
    }

    public static void requestExit() {
        exitRequested = true;
    }

    public static void onClientTick(Minecraft mc) {
        long now = System.nanoTime();
        CineConfig settings = CineConfig.snapshot();
        CineProbe probe = CineProbe.of(mc);

        if (exitRequested) {
            exitRequested = false;
            if (frame.active()) {
                message(mc, "电影视角：已退出", settings);
                SESSION.forceStop("手动退出");
            }
        }

        if (manualRequested) {
            manualRequested = false;
            if (!settings.enabled()) {
                message(mc, "电影视角：请先在设置中开启主开关", settings);
            } else {
                CineProbe.clearInput();
                SESSION.startManual(probe, now);
                if (SESSION.frame().active()) {
                    message(mc, "电影视角：手动进入", settings);
                }
            }
        }

        DangoMusicBridge.Snapshot music = DangoMusicBridge.snapshot();
        gridSnapshot = music.grid();
        musicPlaying = music.playing();
        musicPositionMillis = music.positionMillis();
        musicSongId = music.songId();
        CineSession.Playback playback = music.playing()
                ? new CineSession.Playback(true, music.songId(), music.songId(),
                music.positionMillis(), music.grid(), music.energy())
                : CineSession.Playback.STOPPED;

        boolean wasActive = frame.active();
        int previousShot = SESSION.shotSequence();
        long livedTicks = SESSION.activeTicks();
        try {
            SESSION.tick(probe, settings, playback, now);
            frame = SESSION.frame();
            if (!wasActive && frame.active()) {
                LOG.info("[VitalAssist/Cine] 运镜启动: 闲置 {}s", settings.idleSeconds());
            } else if (wasActive && !frame.active()) {
                LOG.info("[VitalAssist/Cine] 运镜退出: 持续 {}s ({})", livedTicks / 20L, SESSION.stopReason());
            } else if (frame.active() && SESSION.shotSequence() != previousShot) {
                LOG.debug("[VitalAssist/Cine] 切镜 #{}", SESSION.shotSequence());
            }
        } catch (RuntimeException | LinkageError failure) {
            SESSION.forceStop("内部异常");
            frame = SESSION.frame();
            LOG.error("[VitalAssist/Cine] 运镜停止，已释放相机接管", failure);
        }

        CineProbe.sync();
    }

    private static void message(Minecraft mc, String text, CineConfig settings) {
        if (!settings.statusMessages() || mc.player == null) return;
        mc.player.displayClientMessage(Component.literal("§6[VitalAssist] §f" + text), true);
    }

    /** 同一 tick、同一 partialTick 的相机与 FOV 采样结果恒等，且没有世界查询。 */
    public static CinePose currentPose(float partialTick) {
        return frame.sample(partialTick);
    }

    public static float fovOffsetDeg(float partialTick) {
        CineSession.Frame current = frame;
        if (!current.active()) return 0f;
        CinePose pose = current.sample(partialTick);
        return pose == null ? 0f : pose.fovOffsetDeg();
    }

    /** 纯读：遮罩过期不能在这里修改调度状态。 */
    public static TransitionFrame currentTransition() {
        CineSession.Frame current = frame;
        return current.active() && current.transition() != null
                ? current.transition().at(System.nanoTime()) : null;
    }

    public static float cinematicBarFraction() {
        return frame.barFraction();
    }
}
