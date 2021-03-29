# Cytoscape Synchronous Boolean Networks Plugin
Плагин для системы Cytoscape, который добавляет возможности для взаимодейтсвия с синхронными булевыми сетями: построение и анализ.
# Основные возможности:
* Задание булевых функций, описывающих сеть, различными способами:
  * Вручную, путем редактирования таблиц истинности
  * Строкой, с использованием библиотеки MXParser ([Доступные операции](http://mathparser.org/mxparser-math-collection/boolean-operators/))  
  
![Основная панель для задания функций](https://github.com/AlexeySemibratov/cytoscape/blob/images/cytoscape-img/img1.png)

 * Автоматическое построение синхронной сети
 * Поиск неподвижных точек и аттракторов
 
 ![Синхронная сеть и ее аттракторы](https://github.com/AlexeySemibratov/cytoscape/blob/images/cytoscape-img/img2.png)
 
 * Вставка мотивов (небольших связных графов)
 * Поддержка формата .cnet (.net)
 
 ![cytoscape-boolean-network.jar](https://github.com/AlexeySemibratov/cytoscape/blob/master/target/boolean_network-1.0.jar)
