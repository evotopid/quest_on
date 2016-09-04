(function(window){
  window.Timelimit = function(data, page){
    if (data.group === undefined) {
      this.group = null;
    } else {
      this.group = data.group;
    }
    
    if (data.seconds === undefined) {
      console.error("Timelimit with undefined seconds attribute encountered in your survey.json file.");
      alert("Timelimit with undefined seconds attribute encountered in your survey.json file.");
    } else {
      this.seconds = data.seconds;
    }
    
    if (data.timeoutnotice === undefined) {
      this.timeoutnotice = false;
    } else {
      this.timeoutnotice = data.timeoutnotice;
    }
    
    this.page = page;
    this.active = false;
  }
  
  window.Timelimit.prototype.start = function(){
    this.active = true;
    this.startedAt = new Date().getTime();
    this.update();
  }
  
  window.Timelimit.prototype.stop = function(){
    this.seconds = this.millisLeft() * 1000;
    this.active = false;
  }
  
  window.Timelimit.prototype.update = function(){
    timelimit = this;
    
    window.setTimeout(function(){
      if (timelimit.active){
        if (timelimit.millisLeft() <= 0) {
          timelimit.active = false;
          timelimit.page.timelimitReached();
        } else {
          timelimit.update();
        }
      }
    }, 300);
  }
  
  window.Timelimit.prototype.secondsLeft = function(){
    return Math.ceil(this.millisLeft() * 0.001);
  }
  
  window.Timelimit.prototype.millisLeft = function(){
    return this.seconds * 1000 - (new Date().getTime() - this.startedAt);
  }
})(window);