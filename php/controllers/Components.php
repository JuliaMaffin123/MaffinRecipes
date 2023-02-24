<?php
class Components
{
    private $_params;
 
    public function __construct($link, $params) {
		$this->_link = $link;
		$this->_params = $params;
    }

	private function getConnection() {
		return $this->_link;
	}	

	private function getParams() {
		return $this->_params;
	}
	
	public function route($method, $urlData) {
		// Получение информации о ингредиентах
		if ($method === 'GET' && count($urlData) === 1 && is_numeric($urlData[0])) {
			// Вытаскиваем ингредиенты рецепта из базы по ID...
			return $this->getList($urlData[0]);
		}
		// Возвращаем ошибку
		throw new Exception('Bad Request');
	}
	
    public function getList($id) {
		settype($id, 'integer');
		$mysqli = $this->getConnection();
		$query = "SELECT * FROM components WHERE reciept_id = " . $id;
		$data = mysqli_query($mysqli, $query);
		
		return $data->fetch_all(MYSQLI_ASSOC);
	}
 }