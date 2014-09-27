(function(){
  window.PageItem = function(data){
    this.type    = data.type;
    this.content = data.content;
    this.path    = data.path;
    this.id      = data.id;
    this.answers = data.answers;
  };

  window.PageItem.prototype.getHTML = function(){
    switch (this.type) {
    case "textmessage":
      return '<div class="textmessage">'+this.content+'</div>';
    case "image":
      return '<div class="image"><img src="/'+this.path+'"></div>';
    case "multiplechoice":
      html = '<div class="multiplechoice">';
      for (answer_id in this.answers) {
        answer_text = this.answers[answer_id];
        html += '<label><input type="radio" name="'+this.id+'" value="'+answer_id+'"> '+answer_text+"</label><br>";
      }
      html += '</div>';
      return html;
    case "textinput":
      return '<div class="textinput"><input type="text" name="'+this.id+'"></div>';
    default:
      console.error("Trying to get HTML for uknown PageItem type: "+this.type);
    }
  };  
})(window);
