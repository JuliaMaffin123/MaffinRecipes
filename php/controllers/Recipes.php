<?php
class Recipes
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
		// Получение информации о доступных рецептах
		// GET /recipes/list
		if ($method === 'GET' && count($urlData) === 1 && $urlData[0] === 'list') {
			// Вытаскиваем рецепты из базы...
			return $this->getList();
		}
		
		// Получение информации об одном рецепте
		if ($method === 'GET' && count($urlData) === 1 && is_numeric($urlData[0])) {
			// Вытаскиваем рецепт из базы по ID...
			return $this->getReceipt($urlData[0]);
		}

		// Возвращаем ошибку
		throw new Exception('Bad Request');
	}
	
    public function getList() {
		$mysqli = $this->getConnection();
		$query = "SELECT * FROM reciepts";
		$data = mysqli_query($mysqli, $query);
		
		return $data->fetch_all(MYSQLI_ASSOC);
    }
	
	public function getReceipt($id) {
		settype($id, 'integer');
		$mysqli = $this->getConnection();
		$query = "SELECT * FROM reciepts WHERE id = " . $id;
		$data = mysqli_query($mysqli, $query);
		
		return $data->fetch_all(MYSQLI_ASSOC);
	}
 }