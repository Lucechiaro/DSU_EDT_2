#Область ПрограммныйИнтерфейс

// УстановитьЭлементПользовательскихНастроек
Процедура УстановитьЭлементПользовательскихНастроек(Настройки, ИмяПараметра, ЗначениеПараметра) Экспорт
	
	ВыборкаПараметров = Настройки.Элементы;
	Параметр = Новый ПараметрКомпоновкиДанных(ИмяПараметра);
	
	Для Каждого ТекЭлемент Из ВыборкаПараметров Цикл
		
		Если ТипЗнч(ТекЭлемент) = Тип("ЗначениеПараметраНастроекКомпоновкиДанных") Тогда
			
			Если ТекЭлемент.Параметр = Параметр Тогда
				ТекЭлемент.Значение = ЗначениеПараметра;
				ТекЭлемент.Использование = Истина;
			КонецЕсли;
			
		КонецЕсли;
		
	КонецЦикла;
	
КонецПроцедуры

Функция ПолучитьСоответствиеДнейНедели() Экспорт
	
	ДниНедели = Новый Соответствие;
	ДниНедели.Вставить(0, "");
	ДниНедели.Вставить(1, "Понедельник");
	ДниНедели.Вставить(2, "Вторник");
	ДниНедели.Вставить(3, "Среда");
	ДниНедели.Вставить(4, "Четверг");
	ДниНедели.Вставить(5, "Пятница");
	ДниНедели.Вставить(6, "Суббота");
	ДниНедели.Вставить(7, "Воскресенье");

	Возврат ДниНедели;
	
КонецФункции

Процедура ОбновитьНадписьДеньНедели(Элемент, ДниНедели, Объект) Экспорт
	
	Элемент.Заголовок = ДниНедели.Получить(ДеньНедели(Объект.Дата));
	
КонецПроцедуры

#КонецОбласти
