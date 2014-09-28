(function(window, $){
  window.Survey = function(data) {
    this.title = data.title;
    this.pages = new Array();
    this.currentTimelimit = null;
    this.finished = false;
    
    for (var i=0; i<data.pages.length; i++) {
      page = data.pages[i];
      this.pages.push(new window.Page(page, this));  
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
  
  // Starts the survey in window
  window.Survey.prototype.run = function(){
    this.loadPage(0);
    this.updateLoop();
  }
  
  window.Survey.prototype.updateLoop = function() {
    if (window.survey.currentTimelimit != null && window.survey.currentTimelimit.active) {
      $("#time_left").html(_("Time left:")+" "+window.survey.currentTimelimit.secondsLeft()+"s")
    } else {
      $("#time_left").html("");
    }
    
    window.setTimeout(window.survey.updateLoop, 500);
  }
  
  window.Survey.prototype.nextPage = function(){
    this.currentPage.stopTimer();
    this.currentPage.saveValues();
    
    if (this.currentPageIndex+1 < this.pages.length){
      this.loadPage(this.currentPageIndex+1);
    } else {
      alert(_("You reached the end of the survey."));
    }
  }
  
  window.Survey.prototype.loadPage = function(index){
    this.currentPageIndex = index;
    this.currentPage = this.pages[index];
    
    // Display the page
    $("#container").html(this.currentPage.getHTML());
    
    // If it's the last page also display a transferprogress
    if (this.currentPageIndex == this.pages.length-1) {
      $("#container").append("<div id='transfer_progress'>"+window._("Your answers are being transmitted to the server...")+"</div>");
      this.submitResults();
    }
    
    // Start timer
    this.currentPage.startTimer();
  }
  
  window.Survey.prototype.submitResults = function(){
    data = new Object;
    data.results = JSON.stringify(this.getResults());
    
    $.post(window.$directory+"/store", data).done(function(){
      this.finished = true;
      $("#transfer_progress").html(_("Your answers have been transmitted successfully."));
    }).fail(function(){
      $("#transfer_progress").html(_("An error occured while transmitting your answers.")+"<br><input type='button' onclick='survey.submitResults();' value='"+_("Try again")+"'>");
    });
  }
  
})(window, jQuery);
