package com.sweetljk.vitalassist.feature;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sweetljk.vitalassist.config.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class ZhouLi {
    private static final Logger LOGGER = LoggerFactory.getLogger("ZhouLi");
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String SYSTEM_PROMPT;
    private static final Random RANDOM = new Random();

    private static final String[] TONES = {
            "本次使用「温言相劝」的辞气：先体谅，再举例劝说；不盛气凌人。",
            "本次使用「大儒辩经」的辞气：建立貌似严谨的论证，加入反例或反问；适合争辩、吐槽与评论。",
            "本次使用「强行圆场」的辞气：为某种行为另立名分，找出勉强成立的礼法解释，最后判作近于君子。",
            "本次使用「痛心疾首」的辞气：把寻常小事提升到秩序与礼法的高度；郑重但不辱骂。"
    };

    static {
        String prompt;
        try {
            var in = ZhouLi.class.getClassLoader().getResourceAsStream("assets/vitalassist/zhouli_skill.md");
            if (in != null) {
                prompt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                prompt = "";
                LOGGER.warn("zhouli_skill.md not found in classpath");
            }
        } catch (IOException e) {
            prompt = "";
            LOGGER.error("Failed to load zhouli_skill.md", e);
        }
        SYSTEM_PROMPT = prompt;
    }

    private ZhouLi() {
    }

    public static boolean isActive() {
        return Configs.ZHOU_LI.getBooleanValue();
    }

    public static boolean shouldSkip(String message) {
        return message == null || message.isEmpty() || message.startsWith("/") || message.startsWith("!!") || message.startsWith("!");
    }

    private static JsonObject buildPayload(String original) {
        String model = Configs.ZHOU_LI_MODEL.getStringValue();

        JsonObject body = new JsonObject();
        body.addProperty("model", model);

        JsonArray messages = new JsonArray();

        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", SYSTEM_PROMPT + "\n\n" + TONES[RANDOM.nextInt(TONES.length)] + "\n\n只输出改写结果，不要解释，不要加引号，不要输出多条。");
        messages.add(systemMsg);

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", original);
        messages.add(userMsg);

        body.add("messages", messages);
        return body;
    }

    static String callApi(String original) {
        String apiKey = Configs.ZHOU_LI_API_KEY.getStringValue();
        if (apiKey.isEmpty()) return null;

        String endpoint = Configs.ZHOU_LI_API_BASE.getStringValue().replaceAll("/+$", "") + "/chat/completions";
        JsonObject body = buildPayload(original);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != 200) {
                LOGGER.warn("ZhouLi API returned {}: {}", response.statusCode(), response.body());
                return null;
            }

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String result = json
                    .getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();

            if (result != null && !result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            LOGGER.error("ZhouLi API call failed", e);
        }

        return null;
    }

    public static CompletableFuture<ConnectionTestResult> testConnection() {
        String apiBase = Configs.ZHOU_LI_API_BASE.getStringValue().trim();
        String apiKey = Configs.ZHOU_LI_API_KEY.getStringValue().trim();
        String model = Configs.ZHOU_LI_MODEL.getStringValue().trim();

        if (apiBase.isEmpty()) return CompletableFuture.completedFuture(new ConnectionTestResult(false, "请先填写 API BaseURL"));
        if (apiKey.isEmpty()) return CompletableFuture.completedFuture(new ConnectionTestResult(false, "请先填写 API Key"));
        if (model.isEmpty()) return CompletableFuture.completedFuture(new ConnectionTestResult(false, "请先填写模型名称"));

        JsonObject body = new JsonObject();
        body.addProperty("model", model);
        body.addProperty("max_tokens", 32);
        body.addProperty("stream", false);
        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", "连接测试，请只回复 OK。");
        messages.add(message);
        body.add("messages", messages);

        try {
            String endpoint = apiBase.replaceAll("/+$", "") + "/chat/completions";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                    .build();

            return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                    .thenApply(response -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            return new ConnectionTestResult(true, "连接成功，模型 " + model + " 可用");
                        }
                        return new ConnectionTestResult(false, "HTTP " + response.statusCode() + "：" + extractApiError(response.body()));
                    })
                    .exceptionally(error -> new ConnectionTestResult(false, connectionErrorMessage(error)));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new ConnectionTestResult(false, "请求配置无效：" + safeMessage(e)));
        }
    }

    private static String extractApiError(String body) {
        if (body == null || body.isBlank()) return "服务器未返回错误详情";

        try {
            JsonObject root = JsonParser.parseString(body).getAsJsonObject();
            if (root.has("error") && root.get("error").isJsonObject()) {
                JsonObject error = root.getAsJsonObject("error");
                if (error.has("message")) return abbreviate(error.get("message").getAsString());
            }
        } catch (Exception ignored) {
        }

        return abbreviate(body);
    }

    private static String connectionErrorMessage(Throwable error) {
        Throwable cause = error;
        while (cause instanceof CompletionException && cause.getCause() != null) cause = cause.getCause();
        if (cause instanceof HttpTimeoutException) return "连接超时，请检查网络或 API BaseURL";
        return "请求失败：" + safeMessage(cause);
    }

    private static String safeMessage(Throwable error) {
        String message = error.getMessage();
        return abbreviate(message == null || message.isBlank() ? error.getClass().getSimpleName() : message);
    }

    private static String abbreviate(String message) {
        String normalized = message.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 180 ? normalized : normalized.substring(0, 180) + "...";
    }

    public static CompletableFuture<String> rewriteAsync(String original) {
        if (!isActive() || original == null || original.isEmpty()) {
            return CompletableFuture.completedFuture(original);
        }
        if (SYSTEM_PROMPT.isEmpty()) {
            LOGGER.warn("ZhouLi system prompt is empty, skipping rewrite");
            return CompletableFuture.completedFuture(original);
        }

        return CompletableFuture.supplyAsync(() -> {
            String rewritten = callApi(original);
            return rewritten != null ? rewritten : original;
        });
    }

    public record ConnectionTestResult(boolean success, String message) {
    }
}
