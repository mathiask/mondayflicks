package com.appspot.mondayflicks

trait Scripts {

  protected def popupScript(id: String) =
    <script>
      $(function(){{
        var popup = $({"'#" + id + "'"});
        popup.parent().hover(function(){{popup.fadeIn();}}, function(){{ if (!popup.data('sticky')) popup.fadeOut();}});
      }});
    </script>

  protected def twitterScript = {
    <script src="http://widgets.twimg.com/j/2/widget.js"></script>
    <script><xml:unparsed>
    new TWTR.Widget({
      version: 2,
      type: 'profile',
      rpp: 4,
      interval: 6000,
      width: 300,
      height: 300,
      theme: {
        shell: {
          background: '#333333',
          color: '#ffffff'
        },
        tweets: {
          background: '#000000',
          color: '#ffffff',
          links: '#8ECAE8'
        }
      },
      features: {
        scrollbar: false,
        loop: false,
        live: false,
        hashtags: true,
        timestamp: true,
        avatars: false,
        behavior: 'all'
      }
    }).render().setUser('mondayflicks').start();
    </xml:unparsed></script>
  }

  /**
   * Add an onclick handler to a control cheking that an input widget's value
   * is non blank.
   */
  protected def onClickNonBlankScript(controlSelector:String , inputSelector: String) = 
     <script>
       $(function(){{
-        $('{controlSelector}').click(function(){{return $.trim($('{inputSelector}').val()) !== ''; }});
       }});
     </script>

  /**
   * Add an onclick handler to a control cheking that an input widget's value
   * has a minimal length.
   */
  protected def onClickMinLengthScript(controlSelector:String , 
                                       inputSelector: String, 
                                       minLength: Int, 
                                       warnText: String) =
    <script>
      $(function(){{
        var minLength = { minLength },
            inputSelector = '{ inputSelector }';
        $('{controlSelector}').click(function(){{
        <xml:unparsed>
          var ok = $.trim($(inputSelector).val()).length >= minLength; 
        </xml:unparsed>
          if (!ok) alert('{warnText}');
          return ok;
        }});
      }});
    </script>

}
