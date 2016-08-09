StartTest(function(t) {
    t.chain(
        { click : "#tabPanelId tabbar tab[text=Сервера] => .x-tab-inner", offset : [33, 4] },

        { click : "#serversView button[text=Добавить сервер] => .x-btn-inner", offset : [100, 9] },

        { click : "#serverNameFormField-inputEl", offset : [81, 15] },

        { action : "type", options : { shiftKey : true, readableKey : "r" }, text : "CoordinatorAgentServer" },

        { click : "#serverIpFormField-inputEl", offset : [13, 14] },

        { moveCursorTo : "#serverIpFormField-inputEl", offset : [109, 19] },

        { action : "type", text : "195.26.187.170" },

        { click : "[itemId=addButton] => .x-btn-inner", offset : [27, 9] }
    );
})    