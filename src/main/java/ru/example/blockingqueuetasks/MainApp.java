package ru.example.blockingqueuetasks;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainApp {

    private static final BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>(10);
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Pattern TITLE_PATTERN = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Сервер задач (Producer-Consumer)");

        for (int i = 1; i <= 2; i++) {
            int consumerId = i;
            new Thread(() -> consumer(consumerId), "Consumer-" + consumerId).start();
        }

        for (int i = 1; i <= 2; i++) {
            int producerId = i;
            new Thread(() -> producer(producerId), "Producer-" + producerId).start();
        }

        Thread.sleep(10000);

        System.out.println("\n>>> Главный поток завершает работу (демон-потоки будут остановлены)");
        System.exit(0);
    }

    // Логика производителя
    private static void producer(int id) {
        String[] urls = {
                "https://example.com",
                "https://www.wikipedia.org",
                "https://github.com",
                "https://stackoverflow.com",
                "https://www.google.com",
                "https://www.youtube.com",
                "https://www.reddit.com",
                "https://www.linkedin.com"
        };
        int taskId = id * 100;

        for (String url : urls) {
            try {
                Task task = new Task(taskId++, url);
                taskQueue.put(task);
                System.out.println("[" + Thread.currentThread().getName() + "] Добавил: " + task);

                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static void consumer(int id) {
        while (true) {
            try {
                Task task = taskQueue.take();
                System.out.println("[" + Thread.currentThread().getName() + "] Взял в работу: " + task);

                String title = fetchTitle(task.getUrl());

                System.out.println("[" + Thread.currentThread().getName() + "] Завершил: " + task +
                        " | Заголовок: " + title);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static String fetchTitle(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Matcher matcher = TITLE_PATTERN.matcher(response.body());
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
            return "(Без заголовка)";
        } catch (Exception e) {
            return "ОШИБКА: " + e.getMessage();
        }
    }
}