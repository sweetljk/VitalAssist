package com.sweetljk.vitalassist.cine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * 电影视角的画面叠加：上下黑边、切镜转场、下方黑边歌词。
 *
 * <p>全部使用原版 {@link GuiGraphicsExtractor} 绘制，不依赖任何第三方渲染库。
 * 入口在 {@code Gui.extractRenderState} 的 TAIL —— 该注入点在 {@code options.hideGui}
 * 判断之后仍有执行路径，因此隐藏 HUD 时黑边与歌词照常显示。</p>
 */
public final class CineOverlay {

    private static final int BLACK = 0xFF000000;

    private CineOverlay() {
    }

    public static void render(GuiGraphicsExtractor graphics) {
        if (!CineDirector.isActive()) return;
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        if (width <= 0 || height <= 0) return;

        int barHeight = 0;
        float fraction = CineDirector.cinematicBarFraction();
        if (fraction > 0f) {
            barHeight = Math.round(height * Math.min(1f, fraction));
            if (barHeight > 0) {
                graphics.fill(0, 0, width, barHeight, BLACK);
                graphics.fill(0, height - barHeight, width, height, BLACK);
            }
        }

        CineDirector.TransitionFrame transition = CineDirector.currentTransition();
        if (transition != null) {
            drawTransition(graphics, width, height, transition);
        }

        if (barHeight > 10 && CineConfig.snapshot().lyrics()) {
            drawLyrics(graphics, width, height, barHeight);
        }
    }

    private static void drawTransition(GuiGraphicsExtractor graphics, int width, int height,
                                       CineDirector.TransitionFrame frame) {
        double eased = CineShots.smootherstep(frame.progress());
        double coverage = frame.covering() ? eased : 1d - eased;
        switch (frame.effect()) {
            case 1 -> fillAlpha(graphics, width, height, coverage, 0x000000);
            case 2 -> fillAlpha(graphics, width, height, coverage, 0xFFFFFF);
            case 3 -> {
                int edge = (int) Math.round(width * clamp01(coverage));
                if (edge > 0) graphics.fill(0, 0, edge, height, BLACK);
            }
            case 4 -> {
                int edge = (int) Math.round(height * clamp01(coverage));
                if (edge > 0) graphics.fill(0, 0, width, edge, BLACK);
            }
            case 5 -> drawIris(graphics, width, height, clamp01(coverage));
            default -> {
            }
        }
    }

    private static void fillAlpha(GuiGraphicsExtractor graphics, int width, int height,
                                  double coverage, int rgb) {
        int alpha = (int) Math.round(clamp01(coverage) * 255d);
        if (alpha <= 0) return;
        graphics.fill(0, 0, width, height, (alpha << 24) | rgb);
    }

    /** 光圈 iris：用横向分带逼近圆形遮罩，带数固定以免逐像素开销。 */
    private static void drawIris(GuiGraphicsExtractor graphics, int width, int height, double coverage) {
        if (coverage <= 0d) return;
        if (coverage >= 1d) {
            graphics.fill(0, 0, width, height, BLACK);
            return;
        }
        double centerX = width / 2d;
        double centerY = height / 2d;
        double maxRadius = Math.hypot(centerX, centerY);
        double holeRadius = maxRadius * (1d - coverage);
        int bands = 120;
        int bandHeight = Math.max(1, (int) Math.ceil(height / (double) bands));
        for (int y = 0; y < height; y += bandHeight) {
            int bottom = Math.min(height, y + bandHeight);
            double dy = (y + bottom) / 2d - centerY;
            double halfChord;
            if (Math.abs(dy) >= holeRadius) {
                halfChord = 0d;
            } else {
                halfChord = Math.sqrt(Math.max(0d, holeRadius * holeRadius - dy * dy));
            }
            int leftEdge = (int) Math.round(centerX - halfChord);
            int rightEdge = (int) Math.round(centerX + halfChord);
            if (leftEdge > 0) graphics.fill(0, y, leftEdge, bottom, BLACK);
            if (rightEdge < width) graphics.fill(rightEdge, y, width, bottom, BLACK);
        }
    }

    private static void drawLyrics(GuiGraphicsExtractor graphics, int width, int height, int barHeight) {
        Minecraft client = Minecraft.getInstance();
        if (!CineDirector.musicPlaying()) return;
        CineLyrics.request(CineDirector.musicSongId());
        if (!CineLyrics.hasLyrics()) return;

        CineLyrics.Line line = CineLyrics.currentLead(CineDirector.musicPositionMillis(), 120L);
        if (line == null) return;

        boolean hasTranslation = !line.translation().isEmpty();
        int lineHeight = 10;
        int totalHeight = hasTranslation ? lineHeight * 2 : lineHeight;
        int top = height - barHeight + Math.max(2, (barHeight - totalHeight) / 2);
        int centerX = width / 2;

        if (!line.text().isEmpty()) {
            graphics.centeredText(client.font, Component.literal(line.text()), centerX, top, 0xFFFFFFFF);
        }
        if (hasTranslation) {
            graphics.centeredText(client.font, Component.literal(line.translation()), centerX,
                    top + lineHeight, 0xFFBBBBBB);
        }
    }

    private static double clamp01(double value) {
        return Math.max(0d, Math.min(1d, value));
    }
}
