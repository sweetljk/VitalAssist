package com.sweetljk.vitalassist.cine;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 对 DangoMusic（{@code me.sorihiko.dango}）的只读反射桥。
 *
 * <p>只使用其公开 API：{@code MusicPlayer.INSTANCE} 的 state / positionMs / beatSnapshot / current，
 * 以及 {@code LyricsProvider.INSTANCE.neteaseLyricById(long)}。DangoMusic 未安装、版本不符或
 * 内部实现变化时全部安全降级为「无音乐」，不影响其它功能。</p>
 */
public final class DangoMusicBridge {

    /** 播放快照。songId 为 0 表示未知。 */
    public record Snapshot(boolean playing, long positionMillis, long songId, String songName,
                           String artists, CineBeatGrid grid, float energy) {
        static final Snapshot NONE = new Snapshot(false, 0L, 0L, "", "", CineBeatGrid.NONE, 0f);
    }

    private static boolean resolved;
    private static Object playerInstance;
    private static Method mState;
    private static Method mPosition;
    private static Method mBeats;
    private static Method mCurrent;
    private static Method mBeatPulse;
    private static Object lyricsProvider;
    private static Method mLyricById;

    private DangoMusicBridge() {
    }

    private static synchronized void resolve() {
        if (resolved) return;
        resolved = true;
        try {
            Class<?> player = Class.forName("me.sorihiko.dango.music.MusicPlayer");
            Field instance = player.getField("INSTANCE");
            playerInstance = instance.get(null);
            mState = player.getMethod("getState");
            mPosition = player.getMethod("getPositionMs");
            mBeats = player.getMethod("beatSnapshot");
            mCurrent = player.getMethod("getCurrent");
            try {
                mBeatPulse = player.getMethod("beatPulse", long.class);
            } catch (NoSuchMethodException ignored) {
                mBeatPulse = null;
            }
        } catch (Throwable ignored) {
            playerInstance = null;
        }
        try {
            Class<?> provider = Class.forName("me.sorihiko.dango.lyrics.LyricsProvider");
            lyricsProvider = provider.getField("INSTANCE").get(null);
            mLyricById = provider.getMethod("neteaseLyricById", long.class);
        } catch (Throwable ignored) {
            lyricsProvider = null;
            mLyricById = null;
        }
    }

    public static boolean available() {
        resolve();
        return playerInstance != null;
    }

    /** 每 tick 取一次；失败返回「未播放」。 */
    public static Snapshot snapshot() {
        resolve();
        if (playerInstance == null) return Snapshot.NONE;
        try {
            Object state = mState.invoke(playerInstance);
            boolean playing = state != null && "PLAYING".equals(state.toString());
            long position = ((Number) mPosition.invoke(playerInstance)).longValue();

            long songId = 0L;
            String name = "";
            String artists = "";
            Object track = mCurrent.invoke(playerInstance);
            if (track != null) {
                try {
                    songId = ((Number) track.getClass().getMethod("getId").invoke(track)).longValue();
                } catch (Throwable ignored) {
                }
                try {
                    Object value = track.getClass().getMethod("getName").invoke(track);
                    name = value == null ? "" : value.toString();
                } catch (Throwable ignored) {
                }
                try {
                    Object value = track.getClass().getMethod("getArtists").invoke(track);
                    if (value instanceof List<?> list) {
                        StringBuilder sb = new StringBuilder();
                        for (Object artist : list) {
                            if (sb.length() > 0) sb.append(" / ");
                            sb.append(artist);
                        }
                        artists = sb.toString();
                    }
                } catch (Throwable ignored) {
                }
            }

            if (!playing) {
                return new Snapshot(false, position, songId, name, artists, CineBeatGrid.NONE, 0f);
            }

            float[][] beats = null;
            try {
                Object value = mBeats.invoke(playerInstance);
                if (value instanceof float[][] array) beats = array;
            } catch (Throwable ignored) {
            }
            CineBeatGrid grid = gridFrom(beats, position);

            float energy = 0.5f;
            if (mBeatPulse != null) {
                try {
                    Object value = mBeatPulse.invoke(playerInstance, position);
                    if (value instanceof Float f && Float.isFinite(f)) {
                        energy = Math.max(0f, Math.min(1f, f));
                    }
                } catch (Throwable ignored) {
                }
            }
            return new Snapshot(true, position, songId, name, artists, grid, energy);
        } catch (Throwable ignored) {
            return Snapshot.NONE;
        }
    }

    /**
     * 由播放器给出的拍点表（{timeMs, strength}）估计节拍网格。
     * 拍点太少或间隔不稳时返回无节拍网格，调度器会自动退化为慢切。
     */
    static CineBeatGrid gridFrom(float[][] beats, long positionMillis) {
        if (beats == null || beats.length < 6) return CineBeatGrid.NONE;
        List<Double> times = new ArrayList<>();
        for (float[] beat : beats) {
            if (beat != null && beat.length >= 2 && Float.isFinite(beat[0]) && beat[0] >= 0f) {
                times.add((double) beat[0]);
            }
        }
        if (times.size() < 6) return CineBeatGrid.NONE;
        times.sort(Double::compare);

        List<Double> intervals = new ArrayList<>();
        for (int i = 1; i < times.size(); i++) {
            double delta = times.get(i) - times.get(i - 1);
            if (delta > 120d && delta < 2000d) intervals.add(delta);
        }
        if (intervals.size() < 5) return CineBeatGrid.NONE;
        intervals.sort(Double::compare);
        double periodMs = intervals.get(intervals.size() / 2);
        if (!(periodMs > 150d) || periodMs > 2000d) return CineBeatGrid.NONE;

        double deviation = 0d;
        for (double interval : intervals) deviation += Math.abs(interval - periodMs);
        deviation /= intervals.size();
        double consistency = deviation / periodMs;
        if (consistency > 0.18d) return CineBeatGrid.NONE;

        float bpm = (float) (60_000d / periodMs);
        if (!(bpm >= 30f) || bpm > 300f) return CineBeatGrid.NONE;

        double nowNanos = System.nanoTime();
        double anchorMs = times.get(0);
        for (double time : times) {
            if (time <= positionMillis) anchorMs = time;
            else break;
        }
        double anchorNanos = nowNanos - (positionMillis - anchorMs) * 1_000_000d;
        double periodNanos = periodMs * 1_000_000d;

        int beatsPerBar = 4;
        long beatsSinceAnchor = (long) Math.floor((positionMillis - anchorMs) / periodMs);
        long beatsFromOrigin = (long) Math.floor(anchorMs / periodMs) + beatsSinceAnchor;
        double barsOffset = Math.floorMod(beatsFromOrigin, beatsPerBar) * periodNanos;
        double barAnchorNanos = anchorNanos - barsOffset;

        float confidence = (float) Math.max(0.5d, Math.min(1d, 1d - consistency));
        return new CineBeatGrid(bpm, periodNanos, anchorNanos, beatsPerBar, barAnchorNanos, confidence);
    }

    /**
     * 取指定歌曲的歌词文本行。阻塞式网络请求，必须在后台线程调用。
     *
     * @return {timeMs, text, translation} 三元数组列表；不可用返回空列表
     */
    public static List<String[]> lyrics(long songId) {
        resolve();
        List<String[]> result = new ArrayList<>();
        if (lyricsProvider == null || mLyricById == null || songId <= 0L) return result;
        try {
            Object lyrics = mLyricById.invoke(lyricsProvider, songId);
            if (lyrics == null) return result;
            Object lines = lyrics.getClass().getMethod("getLines").invoke(lyrics);
            if (!(lines instanceof List<?> list)) return result;
            for (Object line : list) {
                if (line == null) continue;
                long time = ((Number) line.getClass().getMethod("getTimeMs").invoke(line)).longValue();
                Object text = line.getClass().getMethod("getText").invoke(line);
                Object translation;
                try {
                    translation = line.getClass().getMethod("getTranslation").invoke(line);
                } catch (Throwable ignored) {
                    translation = null;
                }
                result.add(new String[]{Long.toString(time),
                        text == null ? "" : text.toString(),
                        translation == null ? "" : translation.toString()});
            }
        } catch (Throwable ignored) {
            result.clear();
        }
        return result;
    }
}
