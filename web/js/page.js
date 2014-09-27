(function(window){
  window.Page = function(data){
    this.action = data.action;
    this.items = new Array();
  
    for (var i=0; i<data.items.length; i++){
      this.items.push(new window.PageItem(data.items[i]));
    }
  };

  window.Page.prototype.getHTML = function(){
    html = "";
    for (var i=0; i<this.items.length; i++){
      html += this.items[i].getHTML();
    }
    return html;
  };
})(window);
