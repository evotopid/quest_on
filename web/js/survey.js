window.loadSurvey = function(path, callback) {
  // Request the data from the server.
  $.ajax({url: path}).done(function(data){
    survey = new Object();
    survey.title = data.title;
    survey.pages = new Array();
    survey.timelimits = new Array();
    
    for (i=0; i<data.items.length; i++) {
      item = data.items[i];
      if (item.type == "page") {
        survey.pages.push(item);
      } else if (item.type == "timelimit_start") {
        timelimit = new Object;
        timelimit.page_first = survey.pages.length;
        timelimit.max_seconds = item.seconds;
        survey.timelimits.push(timelimit);
      } else if (item.type == "timelimit_end") {
        survey.timelimits[survey.timelimits.length-1].page_last = survey.pages.length-1;
      }
    }
    
    console.log(survey);
    callback(survey);
  }).fail(function(error){
    console.error("Error while loading survey.");
    console.error(error);
    callback(undefined);
  });
};
