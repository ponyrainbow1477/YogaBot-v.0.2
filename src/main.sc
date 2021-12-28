require: slotfilling/slotFilling.sc
    module = sys.zb-common
require: city/city.sc
    module = sys.zb-common
require: dateTime/moment.min.js
    module = sys.zb-common

    

theme: /

    state: CatchAll || noContext = true
        event!: noMatch
        random:
            a: Прошу прощения, я вас не понял.
            a: Извините, я не совсем вас понимаю.
        
    state: Start || modal = true
        q!: $regex</start>
        a: Здравствуйте! Я ваш личный бот-планировщик. Я постараюсь научить вас хорошим привычкам.
        random:
            a: Перед началом, уточните пожалуйста город в котором вы находитесь.
            a: Можете уточнить, в каком вы городе?
    
        state: GetCity
            q: * $City *
            script: 
                $reactions.setClientTimezone($parseTree._City.timezone);
            random:
                a: Благодарю! Можем начинать.
                a: Ого, интересная локация! Слышал, у вас там летом очень красиво! Начнем?
            go: /
        
        state: CatchAll || noContext = true
            event: noMatch
            random:
                a: Простите, я не смогу продолжить, если не узнаю где вы находитесь.
                a: Извините. Эту локацию я не знаю, может выберите что-то другое?
    
    state: SetReminder
        intent!: /SetReminder
        script:
            $session.reminderTime = $parseTree["_duckling.time"];
        random:
            a: Уточните, что именно вам напомнить?
            a: Подскажите, о чем вас следует уведомить?
    
        state: GetReminder
            event: noMatch
            script:
                var event = $pushgate.createEvent(
                    $session.reminderTime.value,
                    "reminderEvent",
                    {
                        text: $parseTree.text
                    }
                );
                $session.reminderId = event.id;
                $temp.reminderTime = moment($session.reminderTime.value).locale("ru").calendar();
            a: Хорошо! {{$temp.reminderTime}} я напомню вам «{{$parseTree.text}}».
            go: /

    state: Remind
        event!: reminderEvent
        random:
            a: Напоминаю вам «{{$request.rawRequest.eventData.text}}».
            a: Вы просили меня напомнить «{{$request.rawRequest.eventData.text}}».
