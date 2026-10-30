# Отчёт по заданию: BlockingQueue и Producer-Consumer

## 1. Цель работы
Научиться использовать BlockingQueue для реализации шаблона проектирования Producer-Consumer. Получить опыт работы с блокирующими очередями для обмена данными между потоками.

## 2. Этапы выполнения задания
1. **Создание модели задачи**: Создан класс `Task`, который хранит уникальный ID и URL-адрес страницы, которую нужно спарсить.
2. **Настройка BlockingQueue**: Создана очередь `LinkedBlockingQueue<Task>(10)`. Ограничение в 10 элементов имитирует реальные системы, где нельзя бесконечно накапливать задачи в памяти.
3. **Реализация Producer (Производителя)**: Созданы 2 потока, которые генерируют задачи (URL-адреса) и добавляют их в очередь с помощью метода `put()`. Если очередь заполнена (10 задач), метод `put()` блокирует поток до тех пор, пока потребитель не заберет задачу.
4. **Реализация Consumer (Потребителя)**: Созданы 2 потока, которые забирают задачи из очереди с помощью метода `take()`. Если очередь пуста, `take()` блокирует поток до появления новых задач. Потребитель скачивает веб-страницу по URL и извлекает из HTML-кода тег `<title>`.
5. **Синхронизация**: Благодаря `BlockingQueue`, потоки производителей и потребителей работают полностью независимо. Очередь сама управляет блокировками и уведомлениями, избавляя нас от необходимости писать `wait()` и `notify()`.

## 3. Промежуточные результаты (вывод консоли)
*Сервер задач (Producer-Consumer)
 [Consumer-1] Взял в работу: Task#100 (https://example.com)
 [Producer-2] Добавил: Task#200 (https://example.com)
 [Producer-1] Добавил: Task#100 (https://example.com)
 [Consumer-2] Взял в работу: Task#200 (https://example.com)
 [Consumer-1] Завершил: Task#100 (https://example.com) | Заголовок: Example Domain
 [Consumer-2] Завершил: Task#200 (https://example.com) | Заголовок: Example Domain
 [Producer-1] Добавил: Task#101 (https://www.wikipedia.org)
 [Producer-2] Добавил: Task#201 (https://www.wikipedia.org)
 [Consumer-1] Взял в работу: Task#101 (https://www.wikipedia.org)
 [Consumer-2] Взял в работу: Task#201 (https://www.wikipedia.org)
 [Producer-2] Добавил: Task#202 (https://github.com)
 [Producer-1] Добавил: Task#102 (https://github.com)
 [Consumer-2] Завершил: Task#201 (https://www.wikipedia.org) | Заголовок: Wikipedia
 [Consumer-2] Взял в работу: Task#202 (https://github.com)
 [Consumer-1] Завершил: Task#101 (https://www.wikipedia.org) | Заголовок: Wikipedia
 [Consumer-1] Взял в работу: Task#102 (https://github.com)
 [Producer-1] Добавил: Task#103 (https://stackoverflow.com)
 [Producer-2] Добавил: Task#203 (https://stackoverflow.com)
 [Producer-1] Добавил: Task#104 (https://www.google.com)
 [Producer-2] Добавил: Task#204 (https://www.google.com)
 [Producer-1] Добавил: Task#105 (https://www.youtube.com)
 [Producer-2] Добавил: Task#205 (https://www.youtube.com)
 [Consumer-1] Завершил: Task#102 (https://github.com) | Заголовок: GitHub · Change is constant. GitHub keeps you ahead. · GitHub · GitHub
 [Consumer-1] Взял в работу: Task#103 (https://stackoverflow.com)
 [Producer-2] Добавил: Task#206 (https://www.reddit.com)
 [Producer-1] Добавил: Task#106 (https://www.reddit.com)
 [Consumer-1] Завершил: Task#103 (https://stackoverflow.com) | Заголовок: (Без заголовка)
 [Consumer-1] Взял в работу: Task#203 (https://stackoverflow.com)
 [Consumer-1] Завершил: Task#203 (https://stackoverflow.com) | Заголовок: (Без заголовка)
 [Consumer-1] Взял в работу: Task#104 (https://www.google.com)
 [Producer-1] Добавил: Task#107 (https://www.linkedin.com)
 [Producer-2] Добавил: Task#207 (https://www.linkedin.com)
 [Consumer-1] Завершил: Task#104 (https://www.google.com) | Заголовок: Google
 [Consumer-1] Взял в работу: Task#204 (https://www.google.com)
 [Consumer-1] Завершил: Task#204 (https://www.google.com) | Заголовок: Google
 [Consumer-1] Взял в работу: Task#205 (https://www.youtube.com)
 [Consumer-1] Завершил: Task#205 (https://www.youtube.com) | Заголовок: YouTube
 [Consumer-1] Взял в работу: Task#105 (https://www.youtube.com)
 [Consumer-1] Завершил: Task#105 (https://www.youtube.com) | Заголовок: YouTube
 [Consumer-1] Взял в работу: Task#206 (https://www.reddit.com)
 [Consumer-1] Завершил: Task#206 (https://www.reddit.com) | Заголовок: Blocked
 [Consumer-1] Взял в работу: Task#106 (https://www.reddit.com)
 [Consumer-1] Завершил: Task#106 (https://www.reddit.com) | Заголовок: Blocked
 [Consumer-1] Взял в работу: Task#107 (https://www.linkedin.com)
 [Consumer-2] Завершил: Task#202 (https://github.com) | Заголовок: GitHub · Change is constant. GitHub keeps you ahead. · GitHub · GitHub
 [Consumer-2] Взял в работу: Task#207 (https://www.linkedin.com)

 >>> Главный поток завершает работу (демон-потоки будут остановлены)

 Process finished with exit code 0*

## 4. Объяснение паттерна Producer-Consumer
Паттерн "Производитель-Потребитель" — это фундаментальный шаблон многопоточного программирования. Он позволяет разделить этапы генерации данных и их обработки.
- **Producer** не знает, кто именно обработает его задачу, и не ждет этого. Он просто кладет задачу в очередь.
- **Consumer** не знает, откуда пришла задача. Он просто берет задачи из очереди.
- **BlockingQueue** выступает в роли буфера. Она решает проблему координации скоростей: если производитель быстрый, а потребитель медленный — очередь заполнится, и производитель притормозит. Если наоборот — потребители будут ждать новых задач.

## 5. Выводы
Использование `BlockingQueue` сделало реализацию паттерна Producer-Consumer максимально простой и надежной. Нам не пришлось вручную управлять мониторами и блокировками. Ограничение очереди (capacity=10) защищает систему от переполнения памяти (OutOfMemoryError) при резком наплыве задач. Такой подход идеально подходит для веб-скраперов, обработчиков очередей сообщений (Kafka, RabbitMQ) и пулов задач.