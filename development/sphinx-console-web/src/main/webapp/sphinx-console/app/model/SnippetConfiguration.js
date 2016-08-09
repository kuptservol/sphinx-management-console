Ext.define('sphinx-console.model.SnippetConfiguration', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'preQuery', type: 'string'},
        {name: 'postQuery', type: 'string'},
        {name: 'mainQuery', type: 'string'},
        {name: 'fullPreQuery', type: 'string'},
        {name: 'fullPostQuery', type: 'string'},
        {name: 'fullMainQuery', type: 'string'},
    ],
    hasMany:[
        {model: 'sphinx-console.model.SnippetConfigurationField', name: 'snippetFields', associationKey: 'fields'}
    ]
});
