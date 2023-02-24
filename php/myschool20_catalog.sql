-- phpMyAdmin SQL Dump
-- version 5.1.3
-- https://www.phpmyadmin.net/
--
-- Хост: 10.0.0.77
-- Время создания: Фев 24 2023 г., 23:24
-- Версия сервера: 5.7.37-40
-- Версия PHP: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `myschool20_catalog`
--

-- --------------------------------------------------------

--
-- Структура таблицы `components`
--

CREATE TABLE `components` (
  `id` int(10) NOT NULL COMMENT 'ID записи',
  `name` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Наименование',
  `count` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'количество',
  `description` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Описание',
  `reciept_id` int(10) DEFAULT NULL COMMENT 'ID рецепта'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `components`
--

INSERT INTO `components` (`id`, `name`, `count`, `description`, `reciept_id`) VALUES
(1, 'Лаваш', '1 уп', 'Чем тоньше, тем лучше', 1),
(2, 'Сосиски', '1 уп', 'Одна сосиска на порцию', 1),
(3, 'Сыр', '50 г', 'Любой твердый сорт', 1),
(4, 'Яйцо', '1 шт', 'Хватает смазать 10 порций', 1),
(7, 'Любимый соус', '1 уп', 'Кетчуп, кисло-сладкий, барбекю. На свой вкус :)', 1),
(8, 'Семечки кунжута', '1 уп', 'Для украшения. Можно использовать белые и черные.', 1),
(9, 'Крабовые палочки', '200 г', 'Выбирайте такие, которые хорошо разворачиваются', 2),
(10, 'Помидоры (томаты), парниковые', '150 г', 'Желательно мясистые', 2),
(11, 'Перец красный сладкий', '300 г', NULL, 2),
(12, 'Сыр голландский, брусковый', '150 г', NULL, 2),
(13, 'Чеснок, сырой', '30 г', NULL, 2),
(14, 'Майонез столовый молочный', '100 г', NULL, 2);

-- --------------------------------------------------------

--
-- Структура таблицы `reciepts`
--

CREATE TABLE `reciepts` (
  `id` int(10) NOT NULL COMMENT 'ID рецепта',
  `type_id` int(10) DEFAULT NULL COMMENT 'ID типа блюда',
  `name` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Название рецепта',
  `description` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Краткое описание',
  `source` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Источник',
  `time` int(11) NOT NULL DEFAULT '0' COMMENT 'Время приготовления',
  `energy` int(11) NOT NULL DEFAULT '0' COMMENT 'Энергетическая ценность'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Рецепты';

--
-- Дамп данных таблицы `reciepts`
--

INSERT INTO `reciepts` (`id`, `type_id`, `name`, `description`, `source`, `time`, `energy`) VALUES
(1, 4, 'Сосиски в лаваше', 'Чумовая закуска из простых ингредиентов!', 'https://vt.tiktok.com/ZS8B7cWyb/', 20, 300),
(2, 3, 'Салат «Красное море»', 'Красивый и сытный салат из крабовых палочек', 'https://vt.tiktok.com/ZS8DDKQwm/', 20, 160);

-- --------------------------------------------------------

--
-- Структура таблицы `steps`
--

CREATE TABLE `steps` (
  `id` int(10) NOT NULL COMMENT 'ID шага',
  `description` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Описание',
  `reciept_id` int(10) DEFAULT NULL COMMENT 'ID рецепта',
  `photo` int(1) NOT NULL DEFAULT '0' COMMENT 'Наличие фото'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `steps`
--

INSERT INTO `steps` (`id`, `description`, `reciept_id`, `photo`) VALUES
(1, 'Нарезаем лаваш на полоски по ширине сосиски.', 1, 0),
(2, 'Сыр натираем на крупную терку.', 1, 0),
(3, 'Яйцо взбиваем до однородной массы.', 1, 0),
(4, 'Плотно закатываем сосиску в полоску из лаваша. На первом витке добавляем щепотку сыра. Край лаваша смазываем яйцом, чтобы он не разворачивался.', 1, 0),
(5, 'Застилаем противень пергаментом и выкладываем на него завернутые сосиски. Остатками яйца смазываем рулетики сверху. Посыпаем кунжутом.', 1, 0),
(6, 'Выпекаем в духовке 8-10 минут при температуре 200 градусов.', 1, 0),
(7, 'Готовые сосиски подаём к столу с любимым соусом.', 1, 0),
(10, 'Разворачиваем крабовые палочки и нарезаем их соломкой.', 2, 1),
(11, 'Нарезаем тонкой соломкой сладкий перец.', 2, 1),
(12, 'У помидоров удаляем семечки, мякоть нарезаем тонкой соломкой.', 2, 1),
(13, 'Сыр натираем на крупную терку.', 2, 0),
(14, 'Чеснок измельчаем или продавливаем в чеснокодавке.', 2, 0),
(15, 'Смешиваем и добавляем майонез. Перед подачей охладить.', 2, 0);

-- --------------------------------------------------------

--
-- Структура таблицы `types`
--

CREATE TABLE `types` (
  `id` int(3) NOT NULL COMMENT 'ID типа блюда',
  `name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Наименование'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `types`
--

INSERT INTO `types` (`id`, `name`) VALUES
(1, 'Первые блюда'),
(2, 'Вторые блюда'),
(3, 'Салаты'),
(4, 'Закуски'),
(5, 'Десерты'),
(6, 'Алкогольные напитки'),
(7, 'Безалкогольные напитки'),
(8, 'Выпечка'),
(9, 'Vegan');

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `components`
--
ALTER TABLE `components`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `reciepts`
--
ALTER TABLE `reciepts`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `steps`
--
ALTER TABLE `steps`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `types`
--
ALTER TABLE `types`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `components`
--
ALTER TABLE `components`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID записи', AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT для таблицы `reciepts`
--
ALTER TABLE `reciepts`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID рецепта', AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT для таблицы `steps`
--
ALTER TABLE `steps`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID шага', AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT для таблицы `types`
--
ALTER TABLE `types`
  MODIFY `id` int(3) NOT NULL AUTO_INCREMENT COMMENT 'ID типа блюда', AUTO_INCREMENT=10;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
