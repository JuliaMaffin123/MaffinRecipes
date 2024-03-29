### Название проекта:
MyCart

### Автор:
Полякова Юлия Александровна

### Идея:
Главная функция приложения - возможность по ингредиентам рецепта (или нескольких рецептов) составить список покупок и сохранить его.
Например, пользователь захочет приготовить основное блюдо, десерт и напиток. Он сможет по ингредиентам рецептов составить список покупок, который впоследствии сможет найти в корзине.
Помимо этого должна быть реализована возможность пользователя отмечать или удалять приобретенные или имеющиеся продукты.
Пользователь также сможет делиться списком покупок.
Целевая аудитория, в основном, женская. Также приложение отлично подойдет для тех, кто любит готовить и хочет быстро составить список покупок из нужных продуктов.

### Реализация:
Проект можно разделить на 3 основные части:

1. **Работа с локальной БД** (файлы расположены в папке db)

   Локальная БД создается при первом запросе к ней.
   Работа с локальной БД организована с помощью библиотеки Room.
   Хранит список рецептов для "Избранного", "Корзины", ингредиенты в "Корзине".
   
   Папка db/dao функции для работы с таблицами в интерфейсах.
   - CartDao - покупки в "Корзине".
   - CartReceiptDao - рецепты в "Корзине".
   - FavoriteDao - рецепты в "Избранном".

   Папка db/entity описанние таблиц (сущностей).
   - Cart - поля таблицы БД для покупок в "Корзине".
   - CartReceipt - поля таблицы БД для рецептов в "Корзине".
   - Favorite - поля для таблицы БД для "Избранного".

   Файл AppDatabase содержит основной класс по работе с БД. 

2. **Серверная часть** (можно разделить еще на 2 части):

   Получение данных с сервера по API, работа с БД
   - ReceiptService - интерфейс с описанием методов серверного API.
   - ResponseSuccess - описывает структуру ответа серверного API.
   - Component, Receipt, Step описывают элементы списка, возвращенного через ResponseSuccess.
   Серверная часть написана на PHP. Главный скрипт разбирает строку запроса, определяет имя контроллера, который будет обрабатывать запрос.
   Контроллеры, в зависимости от переданных параметров, выполняют запрос в БД и возвращают ответ главному скрипту в виде ассоциативного массива.
   Главный скрипт формирует ответ в JSON-формате.
   Данные на сервере хранятся в таблицах MySQL: Component, Receipt, Step. 
   Для выполнения запросов к API применяется библиотека Retrofit.
   
   Работа с изображениями: ассинхронная загрузка изображений, связанных с рецептами, и добавление их в локальный кэш:
    - ImageManager - класс для работы с изображениями.

3. **Работа с UI**

   Папка ui/adapter с классом AbstractListAdapter - Абстрактный адаптер для заполнения данными элементов списков. 
   Адаптер связывает объекты из разметки и ID записи в БД с элементом списка через экземпляр ViewHolder.
   Кроме того, адаптер предоставляет возможность переопределить реакцию на нажатие ImageView внутри элемента списка.

   Папка ui/cart - фрагмент списка покупок.
   - CartFragment - фрагмент для "Корзины".
   - CartViewModel - модель данных для отображения на фрагменте "Корзины".
   
   Папка ui/detail - работа с деталями рецепта при переходе в рецепт из фрагмента рецептов.
   - Папка ui/detail/components - работа с фрагментом таба с ингредиентами рецепта.
       - ComponentsViewModel - модель данных для отображения на фрагменте ингредиентов.
       - TabComponents - работа с самим фрагментом ингредиентов.
   - Папка ui/detail/steps - работа с фрагментом таба с этапами приготовления блюда рецепта.
       - StepsViewModel - модель данных для отображения на фрагменте этапов приготовления.
       - TabSteps - работа с самим фрагментом этапов приготовления.
   - DetailFragment - основной фрагмент для детальной информации о рецепте.
   - DetailViewModel - отображение основного фрагмента с информацией о рецепте.
   - PagerAdapter - адаптер для наполнения контентом вкладок с фрагмента детализации рецепта.

   Папка ui/draw - вспомогательные классы для рисования.
   - DrawUtils - вспомогательный класс для управления визуальными элементами.
   - VerticalImageSpan - класс для размещения картинок в TextView отцентрированных по тексту.
   
   Папка ui/favourite - работа с пунктом меню "Избранное".
   - FavoriteAdapter - адаптер для наполнения "Избранного".
   - FavoriteFragment - фрагмент для отображения избранных рецептов.
   - FavoriteViewModel - модель данных для отображения на фрагменте "Избранное".

   Папка ui/home - работа с основным фрагментом со списком с рецептами.
   - HomeFragment - фрагмент для отображения списка рецептов на базе RecycledView.
   - HomeViewModel - модель данных для отображения на основном фрагменте.

   Принцип работы всех фрагментов типовой. При создании фрагмента инициализируется его ViewModel и создаются обработчики (observer) для отслеживания изменений в MutableLiveData-полях ViewModel
   На фазе жизненного цикла вызывается метод загрузки данных ViewModel. В нем выполняется запрос в локальную БД, либо через Retrofit запрос к серверу.
   После завершения асинхронного запроса данные помещаются в MutableLiveData, срабатывает observer у фрагмента и полученные данные передаются в ListView фрагмента для отрисовки в UI.

**Другие классы:**
- App - старт приложения, подключение БД.
- Config - хранит некоторые константы.
- MainActivity - главная активность с фрагментами: Рецепты, Избранное, Список покупок.
