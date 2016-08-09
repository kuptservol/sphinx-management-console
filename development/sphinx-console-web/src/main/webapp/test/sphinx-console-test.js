var Harness = Siesta.Harness.Browser.ExtJS;

Harness.configure({
    title       : 'sphinx-console Test',

    preload     : [
    ]
});


Harness.start(
    {
        group               : 'Servers',

        preload             : [],

        items : [
            {
                hostPageUrl         : '../sphinx-console/index.html',
                url                 : 'test_case/servers/create_server.js'
            }
        ]
    },
    {
        group               : 'sphinx-console Main',
        
        preload             : [],
        
        items : [
            {
                hostPageUrl         : '../sphinx-console/index.html',
                url                 : 'test_case/app-test-start.js'
            }
        ]
    }
);