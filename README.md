# Pacman player order song bot

Этот бот служит для заказа песен в различных заведениях. 
  
Вы можете найти бота по имени: testSongNameBot

# Для теста
1) Запустите бота  
- !!! Чтобы запустить бота, убедитесь, что он не заблокирован в вашем регионе!!!  
- Если увидите "org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException: Error removing old webhook", это оно!  

1.1) Чтобы обойти блокировку
- Скачиваем и устанавливаем VPN отсюда: https://openvpn.net/community-downloads/
- Параметры для входа берем здесь: https://www.freeopenvpn.org/en/logpass/netherlands.php <br>
  Скачать конфигурационный файл UDP версии (ссылка над именем пользователя) и поместить его в папку OpenVPN/config <br>
  Параметры обновляются каждый день. Если не получается зайти - обновляем страницу и вводим новые данные.<br>
  Если пароль показан как \*Bloked\* - нужно отключить блокировку рекламы на странице (AdBlock и ему подобные)

2) Запустите основное приложение "player-Core"  
- При обходе блокировки телеграм бота убедитесь, что с того же региона не заблокированы сервисы поиска музыки.  
- Так же убедитесь, что песня существует на ресурсе в котором осуществляется поиск песни.  
- В случае если песни нельзя скачать с этого ресурс, то соответсвтенно будет получено сообщение, что песня не найдена.  

# Для того чтобы заказать песню вы должны пройти несколько этапов:
1) Ввести исполнителя песни.
2) Ввести название песни.  
В случае, если такой песни не найдено, выведется сообщение, что "такой песни не найдено", и предложится повторно ввести исполнителя, и названия песни.
3) Дождаться загрузки песни, прослушать её, убедиться, что это нужная песня.
4) Нажать кнопку "да" для подтверждения.

В случае если это не та песня, которую вы хотите, вы должны нажать кнопку "нет" и повторить ввод исполнителя, и названия песни.
