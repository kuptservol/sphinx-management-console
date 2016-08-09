Ext.define('sphinx-console.view.snippet.wizard.SnippetWizardWindow', {
    extend: 'Ext.window.Window',
    width: 1100,
    height: 700,
    layout: 'fit',
    resizable: true,
    maximizable: false,
    closable: false,
    closeAction: 'destroy',
    modal: false,
    listeners: {
        close: function() {
            sphinx-console.app.fireEvent('snippetwizardclosed');
        }
    },
    tools: [
        {
            type: 'restore',
            hidden : true,
            handler: function(evt, toolEl, owner, tool) {
                var window = owner.up( 'window' );
                window.expand('', false);
                window.setWidth(winWidth);
                window.center();
                isMinimized = false;
                this.hide();
                this.nextSibling().show();
            }
        },{
            type: 'minimize',
            handler: function(evt, toolEl, owner, tool){
                var window = owner.up('window');
                window.collapse();
                winWidth = window.getWidth();
                window.setWidth(150);
                window.alignTo(Ext.getBody(), 'bl-bl');
                this.hide();
                this.previousSibling().show();
                isMinimized = true;
            }
        }
    ]
});