Ext.define('sphinx-console.view.collection.wizard.Wizard', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizard',
    autoHeight: true,
    border: false,
    layout: 'card',
    activeItem: 0,
    trackResetOnLoad: true,
    isDistributedCollection: false,

    header: {
        xtype: 'label',
        layout: 'fit',
        width: '100%',
        padding: 10,
        style: {
            color: 'black'
        }
    },

    //createTitle: function() {
    //    var delimiter = ' → ';
    //    var title = '';
    //    for(var i = 0; i < this.activeItem; i++) {
    //        var k = i + 1;
    //        title += 'Шаг ' + k + '. ' +  this.items.items[i].title + delimiter;
    //    };
    //    var k = this.activeItem + 1;
    //    title += 'Шаг ' + k + '. '  + this.getActiveItem().title ;
    //    return   title;
    //},

    createTitle: function() {
        return this.isDistributedCollection ? this.createTitleDistributedCollection(): this.createTitleSimpleCollection();
    },

    createTitleForItems: function(itemIndexes) {
        var delimiter = ' → ';
        var title = '';
        var index = 0;
        var k = 0;
        while(itemIndexes[index] < this.activeItem) {
            k++;
            title += 'Шаг ' + k + '. ' +  this.items.items[itemIndexes[index]].title + delimiter;
            index++;
        }
        k++;
        title += 'Шаг ' + k + '. '  + this.getActiveItem().title ;
        return title;
    },

    createTitleDistributedCollection: function() {
        return this.createTitleForItems([0,7,8,9,10]);
    },

    createTitleSimpleCollection: function() {
        return this.createTitleForItems([0,1,2,3,4,5,6]);
    },

    listeners: {
        beforerender: function() {
            Ext.each(this.getLayout().getLayoutItems(), function(card) {
                card.preventHeader = true;
            });
            this.header.text = this.createTitle();
        },
        activeFormValidityChange: function() {
            var toolbar = this.getDockedItems('toolbar[dock="bottom"]')[0];
            toolbar.items.get('next').setDisabled(this.getActiveItem().hasInvalidField());
            toolbar.child('#finish').setDisabled(this.getActiveItem().hasInvalidField());
        },
        wizardpagechange: function(wizard) {
        	var toolbar = wizard.getDockedItems()[1];
            if (wizard.activeItem > 0) {
                toolbar.items.get('prev').setVisible(true);
                toolbar.items.get('next').setVisible(true);
            }

            if (wizard.activeItem == 0) {
                toolbar.items.get('prev').setVisible(false);
            }

            if (wizard.getActiveItem().finishable) {
                toolbar.items.get('next').setVisible(false);
            }

            toolbar.child('#finish').setVisible(wizard.getActiveItem().finishable);

            this.header.setText(this.createTitle(this));
            toolbar.items.get('next').setDisabled(this.getActiveItem().getForm().hasInvalidField());

            toolbar.child('#finish').setDisabled(this.getActiveItem().getForm().hasInvalidField());
        }
    },

    buttons: [
        {
            text: 'Отмена',
            itemId: 'cancel',
            name: 'cancel',
            action: 'cancelWizard',
            scope: this,
            handler: function(cancel) {
                var wizard = cancel.up('wizard');
                if (wizard.isDirty()) {
                    Ext.Msg.show({
                        scope: this,
                        title:'Предупреждение',
                        msg: 'Введенные данные будут потеряны. Продолжить?',
                        buttonText: {
                            //ok: 'Ок',
                            yes: 'Да',
                            no: 'Нет'//,
                            //cancel: 'Отмена'
                        },
                        buttons: Ext.Msg.YESNO,
                        icon: Ext.Msg.QUESTION,
                        fn: function(buttonId, text, opt) {
                            switch (buttonId) {
                                case 'yes':
                                    wizard.fireEvent('wizardcancelled', wizard);
                                    break;
                                case 'no':
                                    break;
                            }
                        }
                    });
                } else {
                    wizard.fireEvent('wizardcancelled', wizard);
                }
            }
        },
        {
            text: 'Назад',
            itemId: 'prev',
            name: 'prev',
            action: 'prevWizard',
            hidden: true,
            scope: this,
            handler: function(prev) {
                var wizard = prev.up('wizard');
                if(wizard.activeItem > 0) wizard.getLayout().setActiveItem(wizard.getPreviousActiveItemIndex());
                wizard.fireEvent('wizardpagechange', wizard);
                wizard.fireEvent('wizardprev', wizard);
            }
        },
        {
            text: 'Далее',
            itemId: 'next',
            name: 'next',
            action: 'nextWizard',
            disabled: true,
            scope: this,
            handler: function(next) {
                var wizard = next.up('wizard');
                wizard.getLayout().setActiveItem(wizard.getNextActiveItemIndex());
                wizard.fireEvent('wizardpagechange', wizard);
                wizard.fireEvent('wizardnext', wizard);
            }
        },
        {
            text: 'Сохранить коллекцию',
            itemId: 'finish',
            name: 'finish',
            action: 'finishWizard',
            disabled: true,
            hidden: true,
            scope: this,
            handler: function(finish) {
                var wizard = finish.up('wizard');
                Ext.Msg.show({
                    scope: this,
                    title:'Подтверждение',
                    msg: 'Сохранить коллекцию?',
                    buttonText: {
                        yes: 'Да',
                        no: 'Нет'
                    },
                    buttons: Ext.Msg.YESNO,
                    icon: Ext.Msg.QUESTION,
                    fn: function(buttonId, text, opt) {
                        switch (buttonId) {
                            case 'yes':
                                wizard.fireEvent(wizard.isDistributedCollection ? 'distributedCollectionWizardFinished' : 'wizardfinished', wizard);
                                break;
                            case 'no':
                                this.close();
                                break;
                        }
                    }
                });
            }
        }
    ],
    getNextActiveItemIndex: function() {
        return (this.isDistributedCollection && this.activeItem == 0) ? this.activeItem = 7 : ++this.activeItem;
    },
    getPreviousActiveItemIndex: function() {
        return (this.isDistributedCollection && this.activeItem == 7) ? this.activeItem = 0 : --this.activeItem;
    },
    getActiveItem: function() {
        return this.items.items[this.activeItem];
    }
});