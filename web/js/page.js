(function(window, $){
  window.Page = function(data, survey){
    this.actions = data.actions;
    this.items = new Array();
    this.values = new Array();
    this.survey = survey;
    
    if (data.timelimit === undefined || data.timelimit === null){
      this.timelimit = null;
    } else {
      this.timelimit = new window.Timelimit(data.timelimit, this);
    }
    
    for (var i=0; i<data.items.length; i++){
      this.items.push(new window.PageItem(data.items[i]));
    }
  };

  window.Page.prototype.getHTML = function(){
    html = "";
    for (var i=0; i<this.items.length; i++){
      html += this.items[i].getHTML();
    }
    
    for (var i=0; i<this.actions.length; i++){
      action = this.actions[i];
      if (action == "continue"){
        html += '<div class="nextbutton">';
        html += '<input type="button" onclick="survey.nextPage();" value="'+window._("Continue")+'">';
        html += '</div>';
      }
    }
    
    return html;
  };
  
  window.Page.prototype.saveValues = function(){
    this.values = new Array();
    for (var i=0; i<this.items.length; i++){
      pair = this.items[i].getKeyValuePair();
      if (pair != null) {
        this.values.push(pair);
      }
    }
  };
  
  window.Page.prototype.startTimer = function(){
    // Only if the last timer was in a different group than this one we need to start the new one.
    if (this.survey.currentTimelimit == null) {
      if (this.timelimit != null) {
        this.survey.currentTimelimit = this.timelimit;
        this.survey.currentTimelimit.start();
      }
    } else if (this.survey.currentTimelimit.group == null || this.survey.currentTimelimit.group != this.timelimit.group) {
      this.survey.currentTimelimit.stop();
      this.survey.currentTimelimit = this.timelimit;
      if (this.timelimit != null) {
        this.survey.currentTimelimit.start();
      }
    } else {
      this.survey.currentTimelimit.page = this;
    }
  }
  
  window.Page.prototype.stopTimer = function(){
    if (this.survey.currentTimelimit != null) {
      this.survey.currentTimelimit.stop();
    } 
  }
  
  window.Page.prototype.timelimitReached = function(){
    if (this.survey.currentTimelimit.timeoutnotice) {
      console.log("X");
      html = "<div class='textmessage'>"+_("You ran out of time.")+"</div>"
      html += '<div class="nextbutton">';
      html += '<input type="button" onclick="survey.nextPage();" value="'+_("Continue")+'">';
      html += '</div>';
      $("#container").html(html);
    } else {
      this.survey.nextPage();
    }
  }
})(window, jQuery);
