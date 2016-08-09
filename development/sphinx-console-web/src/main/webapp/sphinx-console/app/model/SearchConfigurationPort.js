Ext.define('sphinx-console.model.SearchConfigurationPort', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'int', allowNull: true, persist: false, phantom: true},
        {name: 'searchConfigurationPort', type: 'int'}
    ],
    constructor: function(values) {
        this.superclass.constructor.call(this,values);
        this.setId(values.id == '' ? null : id);
    }
});