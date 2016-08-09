Ext.define('sphinx-console.view.tasks.Tab' , {
    extend: 'sphinx-console.view.Tab',
    alias: 'widget.tasksTab',
    itemId: 'tasksTab',
    requires: ['sphinx-console.view.tasks.List', 'sphinx-console.view.tasks.Search'],
    items: [
        {
            region: 'north',
            xtype:  'tasksSearch'
        },{
            region: 'center',
            xtype:  'tasksList'
        }
    ]
});