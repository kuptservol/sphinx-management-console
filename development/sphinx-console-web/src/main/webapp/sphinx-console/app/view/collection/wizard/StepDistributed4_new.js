Ext.define('sphinx-console.view.collection.wizard.StepDistributed4_new', {
    extend: 'Ext.form.Panel',
    alias: 'widget.stepDistributed4',
    title: 'Настройки конфигурации индекса',
    trackResetOnLoad: true,

    getData : function(){
        return null;
    },

    loadData: function(data) {

    },

    items: [
        {
            xtype: 'label',
            padding: '20 20 20 20',
            text: 'Выберите шаблон для конфигурации индекса Sphinx (блок index в sphinx.conf)'
        },
        {
            xtype: 'gridpanel',
            itemId: 'serversGrid',
            autoScroll: true,
            height: 250,
            forceFit: true,
            padding: '20 20 0 20',
            columnLines: true,
            store: Ext.create('sphinx-console.store.Servers', {pageSize: 10}),
            getAdminProcessesCount: function(serverId, type) {
                var result = null;
                Ext.Ajax.request({
                    async: false,
                    url: sphinx-console.util.Utilities.SERVER_URL + '/view/adminProcesses/server/' + serverId,
                    useDefaultXhrHeader: false,
                    headers: { 'Content-Type': 'application/json' },
                    method: 'GET',
                    success: function (response) {
                        result = Ext.JSON.decode(response.responseText);
                    }
                });
                var count = 0;
                if(result) {
                    result.forEach(function (process) {
                        if (process.type == type) {
                            count++;
                        }
                    });
                }
                return result ? count : 0;
            },
            columns: [
                {
                    header: 'Check'
                },
                {
                    header: 'Шаблон'
                },
                {
                    header: 'Тип коллекции'
                },
                {
                    header: 'Секция конфигурации'
                },
                {
                    header: 'Связанные коллекции'
                },
                {
                    xtype: 'actioncolumn',
                    align: 'center',
                    items: [
                        {
                            icon: 'app/resources/images/process.png',
                            tooltip: 'Добавить/редактировать шаблон',
                            handler: function (grid, rowIndex) {
                            }
                        },
                        {
                            icon: 'app/resources/images/edit.png',
                            tooltip: 'Создать копию шаблона',
                            handler: function (grid, rowIndex, colIndex) {
                            }
                        }
                    ]
                }
            ]
        }
    ]

});