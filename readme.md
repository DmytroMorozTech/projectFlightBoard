#Степ-проект "Flight board"
##Язык программирования: Java
Данный степ проект был выполнен 14.01.2021.

Задача: создать консольное приложение, которое в бесконечном цикле (до выбора команды "Выход") 
предоставляет интерфейс для поиска и бронировки авиабилетов.
 
* При запуске приложения пользователю предлагается войти в систему либо зарегистрировать нового 
  пользователя и затем войти в систему. 
* После аутентификации пользователя ему становится доступным следующий функционал:
  
* ОНЛАЙН-ТАБЛО. Пользователь может вывести на экран онлайн-табло, которое отображает информацию про 
  все рейсы, вылетающие из текущего аэропорта.
  Согласно ТЗ текущим аэропортом в данном проекте выступает аэропорт города Киев.
* ПОСМОТРЕТЬ ИНФОРМАЦИЮ О РЕЙСЕ. Пользователь может посмотреть полную информацию о конкретном 
  рейсе, который его интересует. Поиск рейса выполняется по его номеру(ID).
* ПОИСК И БРОНИРОВАНИЕ РЕЙСА. 
  - На первом этапе (поиск рейсов) пользователю предлагается ввести 
  основные критерии поиска рейсов (пункт назначения; желаемая дата и время вылета; количество 
  мест, которые пользователь планирует забронировать).
  - На основании этих данных приложение производит фильтрацию всех доступных рейсов по указанным 
  критериям поиска. После этого отфильтрованные рейсы выводятся на экран в виде нумерованного 
  списка.
  - На данном этапе пользователь может выбрать одно из следующих действий: "1. Выбрать рейс для 
    бронирования" либо "2. Вернуться в предыдущее меню".
  - Если пользователь выбирает первый пункт, то ему предлагается ввести порядковый номер рейса, 
    под которым он был отображен на экране в списке найденных рейсов.
  - После этого пользователь должен ввести информацию о количестве бронируемых билетов, а также 
    последовательно ввести информацию обо всех пассажирах, для которых будут забронированы 
    билеты (фамилия и имя пассажира).
  - Создается новый объект Booking, в котором содержится полная информация об оформленной брони 
    рейса.
* ОТМЕНИТЬ БРОНИРОВАНИЕ РЕЙСА.
  - Пользователю предлагается ввести номер (ID) рейса. После этого приложение производит поиск 
    брони билетов с таким номером. В случае, если такая бронь была найдена, она будет удалена. 
    Если же брони с таким номером не существует - пользователю будет выведено на 
    экран соответствующее уведомление.
* МОИ РЕЙСЫ.
  - Пользователь должен просто выбрать данный пункт меню, после чего на экран будет выведен 
    список всех рейсов, которые забронировал данный пользователь, а также тех рейсов, в которых 
    он является пассажиром.
  - При регистрации нового пользователя от него требуется ввести не только логин и пароль, 
    но также свои имя и фамилию. В приложении во время его работы доступна полная информация обо 
    всех пользователях. Поэтому нам не нужно дополнительно на этом шагу спрашивать у 
    пользователя его имя и фамилию. Приложение "знает", кто является в данный момент активным 
    пользователем, а также "знает" все его данные.
* СОХРАНИТЬ ВНЕСЕННЫЕ ИЗМЕНЕНИЯ.
  - Согласо ТЗ проекта данное действие было вынесено в отдельный пункт меню. То есть 
    пользователь во время работы приложения может бронировать рейсы, отменять бронь, и т.п., но 
    для того, чтобы сохранить все эти изменения в файл на жестком диске компьютера пользователю 
    нужно будет целенаправленно запустить данный пункт меню "СОХРАНИТЬ ВНЕСЕННЫЕ ИЗМЕНЕНИЯ".
  - Для того, чтобы реализовать эту задачу, был отдельно создан сервисный класс 
    FileSystemService. Он позволяет считывать данные с жесткого диска компьютера при первом 
    запуске приложения (данные про Users, Flights, Bookings). Также с его помощью выполняется 
    сохранение данных из работающего приложения (по сути в этот момент данные хранятся в 
    оперативной памяти компьютера) в файл на жестком диске.
* ЗАВЕРШИТЬ СЕССИЮ.
  - Пользователь попадает в самое базовое меню, где ему предлагается пройти процедуру 
    аутентификации, либо зарегистрировать нового пользователя.
* ЗАВЕРШИТЬ РАБОТУ ПРОГРАММЫ.
  - Программа завершает работу в штатном режиме.
* СГЕНЕРИРОВАТЬ РЕЙСЫ СЛУЧАЙНЫМ ОБРАЗОМ.
  - По условиям ТЗ проекта, "приложение должно иметь большую автоматически сгенерированную базу 
    рейсов, которая должна быть скопирована в файл, и считываться приложением при запуске". На 
    данный момент все так и есть. Но что, если кто-то решит запустить данное приложение через 
    1-2года, а в нашей базе данных с рейсами были сгенерированы рейсы всего на несколько месяцев 
    вперед?
  - Именно для этого и был создан отдельный класс FlightsGenerator. Он может случайным образом 
    сгенерировать любое количество рейсов, которое нам необходимо. При этом дата вылета для всех 
    рейсов выбирается случайно в таком временном диапазоне:
      * нижняя граница диапазона: -10 дней от текущего момента;
      * верхняя граница диапазона: +10 дней после текущего момента.
  - При этом продолжительность всех сгенерированных рейсов соответствует реальной 
    продолжительности перелетов по данным направлениям.
  - Для каждого рейса (объект Flight) случайным образом генерируется уникальный номер(ID) рейса. 
    Это достигается за счет использования буквенно-цифрового паттерна такого образца: "FL321L". 
    Первые 2 буквы в нем никогда не меняются, но последующие 3 цифры и последняя заглавная буква 
    всегда будут генерироваться случайным образом.
* В приложении также реализован LoggerService: отдельный класс, который используется практически во 
  всех частях проекта для логирования разных событий. Данный класс выводит как информационные 
  сообщения, так и сообщения об ошибках. При этом log с ошибками хранится в файле 
  application.log, который находится в корне проекта.

##Распределение заданий между участниками проекта:
---
### Дмитрий Мороз
* Разработка структуры проекта.
* Класс CollectionFlightsDAO, FlightsService, FlightsController.
* Класс FileSystemService для работы с файловой системой.
* Класс FlightsGenerator для генерации случайным образом большого количества рейсов.
* Класс LoggerService.
* Создание внутренних классов исключений в приложении. Перехват и обработка исключений.
---
### Сергей Романюк
* Полностью реализован функционал "ПОИСК И БРОНИРОВАНИЕ РЕЙСА".
* Класс CollectionBookingsDAO, BookingsService, BookingsController.
* Поиск "МОИХ РЕЙСОВ" (то есть рейсов, которые забронировал активный пользователь, либо тех 
  рейсов, в которых он является пассажиром).
---
### Максим Иванов
* Класс Console (комплексная логика работы консоли в приложении). 
* Класс CollectionUsersDAO, UsersService, UsersController.
* Аутентификация, регистрация нового пользователя.


