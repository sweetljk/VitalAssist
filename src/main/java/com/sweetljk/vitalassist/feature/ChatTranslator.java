package com.sweetljk.vitalassist.feature;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sweetljk.vitalassist.config.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class ChatTranslator {
    public static final String DEFAULT_TARGET_LANGUAGE = "英语";

    private static final Logger LOGGER = LoggerFactory.getLogger("VitalAssist/ChatTranslator");
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private ChatTranslator() {
    }

    public static boolean isActive() {
        return Configs.CHAT_TRANSLATE.getBooleanValue();
    }

    public static String getTargetLanguage() {
        String target = Configs.CHAT_TRANSLATE_TARGET.getStringValue();
        if (target == null) return DEFAULT_TARGET_LANGUAGE;

        target = target.replaceAll("\\s+", " ").trim();
        if (target.isEmpty()) return DEFAULT_TARGET_LANGUAGE;
        return target.length() <= 40 ? target : target.substring(0, 40);
    }

    public static boolean shouldSkip(String message) {
        return message == null || message.isBlank() || message.startsWith("/") || message.startsWith("!!") || message.startsWith("!");
    }

    public static boolean needsTranslation(String message) {
        if (message == null || message.isBlank()) return false;

        String target = getTargetLanguage();
        boolean hasLetter = message.codePoints().anyMatch(Character::isLetter);
        if (!hasLetter) return false;

        if (isEnglishTarget(target)) {
            return message.codePoints().anyMatch(ChatTranslator::isNonLatinLetter);
        }
        if (isChineseTarget(target)) {
            return !containsHan(message);
        }
        return true;
    }

    private static boolean isEnglishTarget(String target) {
        String lower = target.toLowerCase(Locale.ROOT);
        return target.contains("英") || lower.contains("english") || lower.equals("en");
    }

    private static boolean isChineseTarget(String target) {
        String lower = target.toLowerCase(Locale.ROOT);
        return target.contains("中") || target.contains("汉") || lower.contains("chinese")
                || lower.contains("mandarin") || lower.equals("zh");
    }

    private static boolean isNonLatinLetter(int codePoint) {
        return Character.isLetter(codePoint) && Character.UnicodeScript.of(codePoint) != Character.UnicodeScript.LATIN;
    }

    private static boolean containsHan(String text) {
        return text.codePoints().anyMatch(codePoint -> {
            Character.UnicodeScript script = Character.UnicodeScript.of(codePoint);
            return script == Character.UnicodeScript.HAN;
        });
    }

    private static String buildSystemPrompt(String target) {
        return "You are a translation engine for Minecraft multiplayer chat. "
                + "Translate the user's message into " + target + ". "
                + "Detect the source language automatically. "
                + "If the message is already entirely in " + target + ", return it unchanged. "
                + "Preserve player names, mentions, numbers, URLs, emoji, punctuation and Minecraft formatting codes. "
                + "Keep the original meaning and tone, and make the result natural and concise for chat. "
                + "Output only the translated message. Do not explain, do not add labels, and do not wrap it in quotation marks.";
    }

    private static JsonObject buildPayload(String original) {
        JsonObject body = new JsonObject();
        body.addProperty("model", Configs.CHAT_TRANSLATE_MODEL.getStringValue().trim());
        body.addProperty("stream", false);

        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", buildSystemPrompt(getTargetLanguage()));
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", original);
        messages.add(userMessage);

        body.add("messages", messages);
        return body;
    }

    static String callApi(String original) {
        String apiBase = Configs.CHAT_TRANSLATE_API_BASE.getStringValue().trim();
        String apiKey = Configs.CHAT_TRANSLATE_API_KEY.getStringValue().trim();
        String model = Configs.CHAT_TRANSLATE_MODEL.getStringValue().trim();
        if (apiBase.isEmpty() || apiKey.isEmpty() || model.isEmpty()) return null;

        try {
            String endpoint = apiBase.replaceAll("/+$", "") + "/chat/completions";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(buildPayload(original).toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                LOGGER.warn("Translation API returned {}: {}", response.statusCode(), response.body());
                return null;
            }

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String result = json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
            return cleanResult(result);
        } catch (Exception e) {
            LOGGER.error("Translation API call failed", e);
            return null;
        }
    }

    private static String cleanResult(String result) {
        if (result == null) return null;

        String cleaned = result.trim().replace("\r", " ").replace("\n", " ");
        if (cleaned.length() >= 2) {
            char first = cleaned.charAt(0);
            char last = cleaned.charAt(cleaned.length() - 1);
            boolean quoted = (first == '"' && last == '"')
                    || (first == '\'' && last == '\'')
                    || (first == '“' && last == '”')
                    || (first == '‘' && last == '’')
                    || (first == '「' && last == '」');
            if (quoted) cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
        }
        return cleaned.isBlank() ? null : cleaned;
    }

    public static CompletableFuture<String> translateAsync(String original) {
        if (!isActive() || shouldSkip(original) || !needsTranslation(original)) {
            return CompletableFuture.completedFuture(original);
        }

        return CompletableFuture.supplyAsync(() -> {
            String translated = callApi(original);
            return translated != null ? translated : original;
        });
    }

    public static CompletableFuture<ConnectionTestResult> testConnection() {
        String apiBase = Configs.CHAT_TRANSLATE_API_BASE.getStringValue().trim();
        String apiKey = Configs.CHAT_TRANSLATE_API_KEY.getStringValue().trim();
        String model = Configs.CHAT_TRANSLATE_MODEL.getStringValue().trim();

        if (apiBase.isEmpty()) return CompletableFuture.completedFuture(new ConnectionTestResult(false, "请先填写语言翻译 API BaseURL"));
        if (apiKey.isEmpty()) return CompletableFuture.completedFuture(new ConnectionTestResult(false, "请先填写语言翻译 API Key"));
        if (model.isEmpty()) return CompletableFuture.completedFuture(new ConnectionTestResult(false, "请先填写语言翻译模型名称"));

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
                            return new ConnectionTestResult(true, "连接成功，模型 " + model + " 可用，目标语言 " + getTargetLanguage());
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
        if (cause instanceof HttpTimeoutException) return "连接超时，请检查网络或语言翻译 API BaseURL";
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

    public record ConnectionTestResult(boolean success, String message) {
    }
}
