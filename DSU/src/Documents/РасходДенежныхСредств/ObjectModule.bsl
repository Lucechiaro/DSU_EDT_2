#Если Сервер Или ТолстыйКлиентОбычноеПриложение Или ВнешнееСоединение Тогда

#Область ОбработчикиСобытий

Процедура ПриКопировании(ОбъектКопирования)
	
	БанковскаяТранзакция = Неопределено;
	ПланируемаяПокупка = Неопределено;
	
КонецПроцедуры

Процедура ОбработкаЗаполнения(Основание)
	
	Валюта = ОбщегоНазначенияПовтИсп.ОсновнаяВалютаУчета();
	
	Если ТипЗнч(Основание) = Тип("ДанныеФормыСтруктура") Тогда 
		ЗаполнитьЗначенияСвойств(ЭтотОбъект, Основание.Основание);
		ЭтотОбъект.Сумма = Основание.СуммаПлатежа;
	КонецЕсли;
	
КонецПроцедуры

Процедура ПередЗаписью(Отказ, РежимЗаписи, РежимПроведения)
	
	Если ОбменДанными.Загрузка Тогда
		Возврат;
	КонецЕсли;	
	
КонецПроцедуры

Процедура ОбработкаПроведения(Отказ, Режим)
	
	Если ОбменДанными.Загрузка Тогда
		Возврат;
	КонецЕсли;
	
	УчётнаяСумма = Сумма*Курс/Кратность;	
	
	// проведение по регистру "Остатки по счетам"
	ДвижениеОстатки = Движения.ОстаткиПоСчетам.ДобавитьРасход();
	ЗаполнитьЗначенияСвойств(ДвижениеОстатки, Ссылка);
	ДвижениеОстатки.СуммаУчетн = УчётнаяСумма;
	ДвижениеОстатки.Период = Дата;
	
	// определим возможность проведения по регистрам
	// для забалансовых счетов нет смысла считать расходы, нужно только контролировать остатки
	УчётРасходы = Не (Счет.Забалансовый Или НеУчитыватьВСтруктуреРасходов);
	УчётДолговыеОбязательства = (Не Счет.Забалансовый) И ЗначениеЗаполнено(Должник);
	УчётПланируемыеПокупки = (Не Счет.Забалансовый) И ЗначениеЗаполнено(ПланируемаяПокупка);
	
	// проведение по регистру "Расходы"
	Если УчётРасходы Тогда  
		
		ДвижениеРасходы = Движения.Расходы.Добавить();
		ЗаполнитьЗначенияСвойств(ДвижениеРасходы, Ссылка);
		ДвижениеРасходы.СуммаУчетн = УчётнаяСумма;
		ДвижениеРасходы.Период = Дата;
		
	КонецЕсли;	
	
	// формируем движения по регистру "Долговые обязательства"
	Если УчётДолговыеОбязательства Тогда
		
		ДвижениеДолги = Движения.ДолговыеОбязательства.ДобавитьПриход();
		ЗаполнитьЗначенияСвойств(ДвижениеДолги, Ссылка);
		ДвижениеДолги.Должник = Ссылка.Должник;
		ДвижениеДолги.Период = Дата;
		
	КонецЕсли;
	
	// обработаем расход по запланированной покупке
	Если УчётПланируемыеПокупки Тогда
		
		// добавим запись в регистр сведений "Состояния планируемых покупок" 
		ДвижениеСостояние = Движения.СостоянияПланируемыхПокупок.Добавить();
		ЗаполнитьЗначенияСвойств(ДвижениеСостояние, Ссылка, "Сумма,ПланируемаяПокупка");
		ДвижениеСостояние.Период = Дата;
		ДвижениеСостояние.Состояние = ПредопределенноеЗначение("Перечисление.СостоянияПланируемойПокупки.Завершение");
		
		// вычислим разницу между запланированной и реальной суммой и сторнируем её в регистре "Остатки планируемых покупок"
		Запрос = Новый Запрос;
		Запрос.Текст = "ВЫБРАТЬ
		|	ОстаткиПланируемыхПокупокОбороты.СуммаРасход,
		|	ОстаткиПланируемыхПокупокОбороты.СуммаПриход
		|ИЗ
		|	РегистрНакопления.ОстаткиПланируемыхПокупок.Обороты(, &ДатаДокумента, , ПланируемаяПокупка = &ПланируемаяПокупка) КАК ОстаткиПланируемыхПокупокОбороты";
		Запрос.УстановитьПараметр("ДатаДокумента", Дата);
		Запрос.УстановитьПараметр("ПланируемаяПокупка", ПланируемаяПокупка);
		Выборка = Запрос.Выполнить().Выбрать();
		Если Выборка.Следующий() Тогда
			
			СторноПриход = Движения.ОстаткиПланируемыхПокупок.ДобавитьПриход();
			СторноПриход.ПланируемаяПокупка = ПланируемаяПокупка;
			СторноПриход.Период = Дата;
			СторноПриход.Сумма = Выборка.СуммаРасход - Выборка.СуммаПриход;
			
		КонецЕсли;
		
	КонецЕсли;	
	
	РегистрыНакопления.ОстаткиВалют.ОтразитьСписаниеВалюты(ЭтотОбъект);
	
КонецПроцедуры

#КонецОбласти

#КонецЕсли