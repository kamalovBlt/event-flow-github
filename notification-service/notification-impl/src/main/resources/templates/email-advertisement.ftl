<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${event.name}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            color: #333;
            padding: 20px;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 12px;
            max-width: 600px;
            margin: auto;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        .brand {
            text-align: center;
            font-size: 28px;
            font-weight: bold;
            color: #1a73e8;
            margin-bottom: 30px;
        }
        h1 {
            font-size: 24px;
            color: #222;
            text-align: center;
        }
        .event-info {
            margin-top: 20px;
            font-size: 16px;
        }
        .footer {
            margin-top: 30px;
            font-size: 13px;
            color: #888;
            text-align: center;
        }
        .button {
            display: inline-block;
            padding: 12px 24px;
            margin-top: 25px;
            background-color: #1a73e8;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: bold;
        }
        .button-container {
            text-align: center;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="brand">Event Flow</div>

    <h1>${event.name}</h1>

    <div class="event-info">
        <p><strong>Дата и время:</strong> ${event.time}</p>
        <p><strong>Исполнители:</strong> ${event.artistsNames?join(", ")}</p>
    </div>

    <div class="button-container">
        <a class="button" href="${event.url!}">Подробнее о мероприятии</a>
    </div>

    <div class="footer">
        Письмо отправлено с платформы Event Flow. <br>
        Следите за лучшими событиями вместе с нами!
    </div>
</div>
</body>
</html>
