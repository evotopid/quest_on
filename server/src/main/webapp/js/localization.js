(function(){
  window.loadLocalization = function(callback){
    $.ajax({
      url: window.$directory+"/strings.json"
    }).done(function(strings){
      window.$strings = strings;
      callback();
    }).fail(function(error){
      console.error("Failed to load strings.json from server...");
      window.$strings = new Object();
    });
  }
  
  // Returns a localizd version of str if possible or just str otherwise
  window._ = function(str){
    if (window.$strings[str] !== undefined) {
      return window.$strings[str];
    } else {
      return str;
    }
  }
})(window, jQuery);
