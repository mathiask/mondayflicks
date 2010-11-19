$.fn.editable = function(callback){
  this.click(function() {
    var el = $(this),
        text = el.text();
    el.after("<form action='#'><input type='text' value='" + text + "' size='" + text.length + "'/></form>").hide();
    var form = el.next();
    form.submit(function() { 
      var newText = form.find("input").val();
      form.remove();
      if (!callback || callback(newText)) el.text(newText);
      el.show();
      return false;})
    .find("input").focus().focusout(function(){
        form.remove();
        el.show();
    });
  });
}

