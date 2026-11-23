package com.sweetljk.vitalassist.cine;

/**
 * 节拍网格：把音乐播放器给出的拍点整理成切镜调度需要的日历视图（BPM、相位、小节）。
 *
 * <p>与「节拍脉冲」的分工：脉冲只关心当下这一帧多响；网格要能回答「下一小节第一拍在什么时候」，
 * 切镜点才对得上小节。</p>
 */
public record CineBeatGrid(float bpm,
                           double beatPeriodNanos,
                           double beatAnchorNanos,
                           int beatsPerBar,
                           double barAnchorNanos,
                           float confidence) {

    public static final CineBeatGrid NONE = new CineBeatGrid(0f, 0d, 0d, 4, 0d, 0f);

    /** 无节拍信号时调度器退化为「能量驱动的慢切」。 */
    public boolean isMetered() {
        return Float.isFinite(bpm) && bpm >= 30f && bpm <= 300f
                && Double.isFinite(beatPeriodNanos) && beatPeriodNanos >= 200_000_000d
                && beatPeriodNanos <= 2_000_000_000d
                && Double.isFinite(beatAnchorNanos) && Double.isFinite(barAnchorNanos)
                && beatsPerBar > 0 && beatsPerBar <= 16
                && Float.isFinite(confidence) && confidence >= 0.5f;
    }

    /** 把绝对期限向后对齐到小节第一拍。 */
    public long nextBarAtOrAfter(long earliestNanos) {
        if (!isMetered()) return earliestNanos;
        double period = beatPeriodNanos * beatsPerBar;
        double elapsed = earliestNanos - barAnchorNanos;
        double remainder = elapsed - Math.floor(elapsed / period) * period;
        double wait = remainder < 1d || period - remainder < 1d ? 0d : period - remainder;
        return earliestNanos + (long) Math.ceil(wait);
    }

    /** 当前拍内相位 0..1。 */
    public float beatPhase(long nowNanos) {
        if (beatPeriodNanos <= 0d) return 0f;
        double elapsed = nowNanos - beatAnchorNanos;
        double phase = elapsed - Math.floor(elapsed / beatPeriodNanos) * beatPeriodNanos;
        return (float) (phase / beatPeriodNanos);
    }

    /** 距下一个拍点的秒数（0..1 拍）。 */
    public float secondsToNextBeat(long nowNanos) {
        if (beatPeriodNanos <= 0d) return 0f;
        return (1f - beatPhase(nowNanos)) * (float) (beatPeriodNanos / 1.0e9);
    }
}
