#Область ОбработчикиСобытийЭлементовШапкиФормы

&НаКлиенте
Процедура РезультатОбработкаРасшифровки(Элемент, Расшифровка, СтандартнаяОбработка)
	
	СтандартнаяОбработка = Ложь;      
	СтруктураРасшифровки = ОбщегоНазначенияКлиент.ПолучитьСтруктуруРасшифровки(Расшифровка, ДанныеРасшифровки);
	
	Если СтруктураРасшифровки.Свойство("Регистратор") Тогда
		ОткрытьЗначениеАсинх(СтруктураРасшифровки.Регистратор);
	ИначеЕсли Параметры.Свойство("ОбработатьРасшифровку") Тогда 	
		ОбработатьРасшифровку(Результат, Параметры)
	КонецЕсли;	
	
КонецПроцедуры

#КонецОбласти


#Область ОбработчикиКомандФормы

&НаКлиенте
Процедура СформироватьОтчет(Команда)
	
	ПараметрыОтчета = Новый Структура;
	ПараметрыОтчета.Вставить("Должник", Должник);
	ПараметрыОтчета.Вставить("Валюта", Валюта);
	ПараметрыОтчета.Вставить("ВалютаНеЗаполнена", Не ЗначениеЗаполнено(Валюта));
	ПараметрыОтчета.Вставить("НачалоПериода", НачалоПериода);
	ПараметрыОтчета.Вставить("КонецПериода", КонецПериода);
	
	ОбработатьРасшифровку(Результат, ПараметрыОтчета);
	
КонецПроцедуры

#КонецОбласти


#Область СлужебныеПроцедурыИФункции

&НаСервере
Процедура ОбработатьРасшифровку(ТабличныйДокумент, ПараметрыОтчета)
	
	ТабличныйДокумент.Очистить();
	
	ОтчетОбъект = РеквизитФормыВЗначение("Отчет");
	СхемаКомпоновкиДанных = ОтчетОбъект.ПолучитьМакет("ОсновнаяСхемаКомпоновкиДанных");

	ИсполняемыеНастройки = ОтчетОбъект.КомпоновщикНастроек.Настройки;
	ОбщегоНазначенияКлиентСервер.УстановитьПараметрДанных(ИсполняемыеНастройки.ПараметрыДанных, "Должник", ПараметрыОтчета.Должник);
	ОбщегоНазначенияКлиентСервер.УстановитьПараметрДанных(ИсполняемыеНастройки.ПараметрыДанных, "НачалоПериода", ПараметрыОтчета.НачалоПериода);
	ОбщегоНазначенияКлиентСервер.УстановитьПараметрДанных(ИсполняемыеНастройки.ПараметрыДанных, "КонецПериода", ПараметрыОтчета.КонецПериода);
	ОбщегоНазначенияКлиентСервер.УстановитьПараметрДанных(ИсполняемыеНастройки.ПараметрыДанных, "Валюта", ПараметрыОтчета.Валюта);
	ОбщегоНазначенияКлиентСервер.УстановитьПараметрДанных(ИсполняемыеНастройки.ПараметрыДанных, "ВалютаНеЗаполнена", ПараметрыОтчета.ВалютаНеЗаполнена);
	
	ЗаполнитьЗначенияСвойств(ЭтотОбъект, ПараметрыОтчета);
	
	ДанныеРасшифровкиСКД = Новый ДанныеРасшифровкиКомпоновкиДанных;
	КомпоновщикМакета = Новый КомпоновщикМакетаКомпоновкиДанных;
	МакетКомпоновкиДанных = КомпоновщикМакета.Выполнить(СхемаКомпоновкиДанных, ИсполняемыеНастройки, ДанныеРасшифровкиСКД);
	
	ПроцессорКомпоновкиДанных = Новый ПроцессорКомпоновкиДанных;
	ПроцессорКомпоновкиДанных.Инициализировать(МакетКомпоновкиДанных, , ДанныеРасшифровкиСКД);
	
	ПроцессорВывода = Новый ПроцессорВыводаРезультатаКомпоновкиДанныхВТабличныйДокумент;
	ПроцессорВывода.УстановитьДокумент(ТабличныйДокумент);
	ПроцессорВывода.Вывести(ПроцессорКомпоновкиДанных);
	
	ДанныеРасшифровки = ПоместитьВоВременноеХранилище(ДанныеРасшифровкиСКД, УникальныйИдентификатор);
	
КонецПроцедуры

#КонецОбласти


