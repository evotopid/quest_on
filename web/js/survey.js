(function(window){
  window.Survey = function(data) {
    this.title = data.title;
    this.pages = new Array();
    this.timelimits = new Array();
    
    for (var i=0; i<data.items.length; i++) {
      item = data.items[i];
      if (item.type == "page") {
        this.pages.push(new window.Page(item));
      } else if (item.type == "timelimit_start") {
        timelimit = new Object();
        timelimit.page_first = this.pages.length;
        timelimit.max_seconds = item.seconds;
        this.timelimits.push(timelimit);
      } else if (item.type == "timelimit_end") {  
        this.timelimits[this.timelimits.length-1].page_last = this.pages.length-1;
      }
    }
  }
  
  window.Survey.prototype.getResults = function(){
    var results = new Array();
    for (var i=0; i<this.pages.length; i++){
      // Add the page's results to the end of the array
      results = results.concat(this.pages[i].values);
    }
    return results;
  }
  
  // Helper to load the Survey Object
  window.loadSurvey = function(path, callback) {
    $.ajax({url: path}).done(function(data){
      callback(new Survey(data));
    }).fail(function(error){
      console.error("Error while loading survey.");
      console.error(error);
      callback(undefined);
    });
  }
})(window);
