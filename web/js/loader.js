(function(window, $){
  window.loadApplication = function(callback){
    window.loadLocalization(function(){
      loadImages(function(){
        loadSurvey("survey.json", function(s){
          window.survey = s;
          window.document.title = s.title;
          callback();
        });
      });
    });
    
    // Makes sure all images are preloaded
    function loadImages(cb) {
      $.ajax({
        url: $directory+"/all_images.json"
      }).done(function(images){
        var remaining_images_to_load = images.length;
        var loading_objects = new Array;
        for (i=0; i<images.length; i++){
          var image = new Image();
          image.onload = function() {
            remaining_images_to_load--;
            if (remaining_images_to_load <= 0) {
              cb();
            }
          }
          image.src = images[i];
          loading_objects.push(image);
        }
      }).fail(function(error){
        console.error("Couldn't preload images");
        console.error(error);
        cb();
      });
    }
  }
})(window, jQuery);