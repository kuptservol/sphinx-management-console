Ext.define('sphinx-console.model.DistributedConfigurationPort', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'int', allowNull: true, persist: false, phantom: true},
        {name: 'distributedConfigurationPort', type: 'int'}
    ],
    constructor: function(values) {
        this.superclass.constructor.call(this,values);
        this.setId(values.id == '' ? null : id);
    }
});