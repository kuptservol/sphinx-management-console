Ext.define('sphinx-console.store.sphinx-consoleProxy', {
    extend:'Ext.data.proxy.Rest',
    alias:'proxy.sphinx-consoleProxy',

    buildRequest:function (operation) {
        var request = this.callParent(arguments);
    // For documentation on jsonData see Ext.Ajax.request
        request.jsonData = request.params;
        request.params = {};

        return request;
    },

    /*
     * @override
     * Inherit docs. We don't apply any encoding here because
     * all of the direct requests go out as jsonData
     */
    applyEncoding: function(value){
        return value;
    }

});