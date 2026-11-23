package com.sweetljk.vitalassist.cine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 歌词取用与缓存。
 *
 * <p>歌词来自 DangoMusic 的公开接口 {@code LyricsProvider.neteaseLyricById(long)}，是阻塞式网络请求，
 * 因此只在后台线程拉取、按歌曲 ID 缓存；渲染侧只读缓存，不做 IO。</p>
 */
public final class CineLyrics {

    public record Line(long timeMs, String text, String translation) {
    }

    private static final ExecutorService FETCHER = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "VitalAssist-Cine-Lyrics");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });

    private static final AtomicBoolean fetching = new AtomicBoolean(false);

    private static volatile long loadedSongId;
    private static volatile long failedSongId;
    private static volatile List<Line> lines = List.of();

    private CineLyrics() {
    }

    /** 主线程调用：歌曲变化时触发一次后台拉取。 */
    public static void request(long songId) {
        if (songId <= 0L) return;
        if (songId == loadedSongId || songId == failedSongId) return;
        if (!fetching.compareAndSet(false, true)) return;
        loadedSongId = songId;
        lines = List.of();
        FETCHER.execute(() -> {
            try {
                List<String[]> raw = DangoMusicBridge.lyrics(songId);
                if (raw.isEmpty()) {
                    failedSongId = songId;
                    return;
                }
                List<Line> parsed = new ArrayList<>(raw.size());
                for (String[] row : raw) {
                    long time;
                    try {
                        time = Long.parseLong(row[0]);
                    } catch (NumberFormatException ignored) {
                        continue;
                    }
                    String text = row[1] == null ? "" : row[1].trim();
                    String translation = row[2] == null ? "" : row[2].trim();
                    if (text.isEmpty() && translation.isEmpty()) continue;
                    parsed.add(new Line(time, text, translation));
                }
                lines = parsed.isEmpty() ? List.of() : List.copyOf(parsed);
                if (parsed.isEmpty()) failedSongId = songId;
            } catch (Throwable ignored) {
                failedSongId = songId;
            } finally {
                fetching.set(false);
            }
        });
    }

    public static boolean hasLyrics() {
        return !lines.isEmpty();
    }

    /** 当前时刻应显示的那一行；无歌词返回 null。 */
    public static Line current(long positionMs) {
        List<Line> snapshot = lines;
        if (snapshot.isEmpty()) return null;
        int low = 0;
        int high = snapshot.size() - 1;
        int found = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (snapshot.get(mid).timeMs() <= positionMs) {
                found = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return found < 0 ? null : snapshot.get(found);
    }

    /** 提前多少毫秒切到下一行，避免逐字延迟感。 */
    public static Line currentLead(long positionMs, long leadMs) {
        return current(positionMs + Math.max(0L, leadMs));
    }
}
