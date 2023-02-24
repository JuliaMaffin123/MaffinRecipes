<?php
// Define path to data folder
define('DATA_PATH', realpath(dirname(__FILE__).'/data'));

// Подключим настройки
include_once 'configuration.php'; 

// Получение данных из тела запроса
function getFormData($method) {
    // GET или POST: данные возвращаем как есть
    if ($method === 'GET') return $_GET;
    if ($method === 'POST') return $_POST;
    // PUT, PATCH или DELETE
    $data = array();
    $exploded = explode('&', file_get_contents('php://input'));
    foreach($exploded as $pair) {
        $item = explode('=', $pair);
        if (count($item) == 2) {
            $data[urldecode($item[0])] = urldecode($item[1]);
        }
    }
    return $data;
}
 
try {
	// Определяем метод запроса
	$method = $_SERVER['REQUEST_METHOD'];
	// Получаем данные из тела запроса
	$formData = getFormData($method);

	// Разбираем url, если параметры приходят в строке запроса
	$url = (isset($formData['q'])) ? $formData['q'] : '';
	$url = rtrim($url, '/');
	$urls = explode('/', $url);

	$controller = ucfirst(strtolower($urls[0]));
	$urlData = array_slice($urls, 1);

	// Проверим, существует ли такой контроллер
	if( file_exists("controllers/{$controller}.php") ) {
		include_once "controllers/{$controller}.php";
	} else {
		throw new Exception('Controller is invalid.');
	}

	// видеть все ошибки!
	ini_set('display_errors',1);
	error_reporting(E_ALL);
	// Инициализируем настройки
	$config = new Config();
	
	// включаем режим информирования об ошибках
	mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
	// Подключаемся к базе данных mysql
	$link = mysqli_connect($config->host, $config->user, $config->password, $config->db);
	// не забываем установить кодировку, чтобы не было ошибок с кракозябрами
	$link->set_charset('utf8mb4');
	
	// Создаем новый инстанс контроллера и передаем ему ссылку на подключение к базе и параметры вызова
	$controller = new $controller($link, $formData);

	// Маршрутизируем вызов метода
	$result['success'] = true;
	$result['data'] = $controller->route($method, $urlData);
} catch( Exception $e ) {
	// При любых проблемах выбрасываем исключение и статус 400
	$result['success'] = false;
	$result['error'] = $e->getMessage();
	header('HTTP/1.0 400 Bad Request');
}
 
// Если ошибок не было, успешный ответ и статус 200
echo json_encode($result, JSON_UNESCAPED_UNICODE);
exit();