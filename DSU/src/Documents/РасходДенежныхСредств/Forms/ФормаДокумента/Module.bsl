#Область ОписаниеПеременных

&НаКлиенте
Перем ДниНедели;

#КонецОбласти


#Область ОбработчикиСобытийФормы
&НаКлиенте
Процедура ПриОткрытии(Отказ)
	
	ДниНедели = ОбщегоНазначенияКлиент.ПолучитьСоответствиеДнейНедели();
	ОбщегоНазначенияКлиент.ОбновитьНадписьДеньНедели(Элементы.ДеньНедели, ДниНедели, Объект);
	
КонецПроцедуры

#КонецОбласти

#Область ОбработчикиСобытийЭлементовШапкиФормы

&НаКлиенте
Процедура ДатаПриИзменении(Элемент)
	
	ОбщегоНазначенияКлиент.ОбновитьНадписьДеньНедели(Элементы.ДеньНедели, ДниНедели, Объект);
	
КонецПроцедуры

#КонецОбласти


