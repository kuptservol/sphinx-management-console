Ext.define('sphinx-console.util.Date', {
    singleton: true,
    MILLISECONDS_IN_DAY: 86400000,

    addTimeToDate: function(date, datetime){
        if(date && datetime){
            date.setHours(datetime.getHours())
            date.setMinutes(datetime.getMinutes())
        }
        return date;
    },

    addTimeToEndOfDate: function (date) {
        if(date) {
            this.clearTime(date);
            date = new Date(date.getTime() + this.MILLISECONDS_IN_DAY - 1);
        }
        return date;
    },

    clearTime: function(date){
        if(date) {
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
            date.setMilliseconds(0);
        }
    },

    getEndOfDayIfTimeEmpty: function(date, time){
        var result;
        if(time == null || time == ""){
            result = sphinx-console.util.Date.addTimeToEndOfDate(date);
        }
        else{
            result = sphinx-console.util.Date.addTimeToDate(date, time);
        }
        return result;
    }

});