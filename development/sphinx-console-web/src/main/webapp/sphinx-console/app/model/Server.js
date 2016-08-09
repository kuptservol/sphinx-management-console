Ext.define('sphinx-console.model.Server', {
    extend: 'Ext.data.Model',
    autoLoad: true,
    autoSync: true,
    fields: [
        {name: 'id', type: 'int'},
        {name: 'ip', type: 'string'},
        {name: 'domain', type: 'string'},
        {name: 'name', type: 'string'}],

    getData: function() {
        var data = this.superclass.getData.call(this);
        if(data.status) {
            delete data.status;
        }
        return data;
    }
});