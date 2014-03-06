(function($, cloudStack) {
  cloudStack.modules.cloudPlatform = function(module) {
    // Only these languages will show in login lang selection
    var supportedLanguages = [
      'en', 'en-US', 'ja', 'ja_JP', 'zh_CN'
    ];

    var replace = function(str) {
      var cpStr = 'CloudPlatform™';

      return str
        .replace(/\&\#8482/g, '') // Remove tm symbol
        .replace(/CloudStack/gi, cpStr);
    };

    var eula = function(args) {
      var $eula = $('<div>').addClass('eula');
      var $eulaContainer = $('<div>').addClass('eula-container');
      var $agreeButton = $('<div>').addClass('button agree').html('Agree');
      var complete = args.complete;
      var _dictionary = cloudStack.modules.cloudPlatform.dictionary[g_lang];

      $eulaContainer.append(
        $('<div>').addClass('eula-desc').html(_dictionary['message.eula.desc']),
        $('<div>').addClass('download-eula').append(
          $('<div>').addClass('icon'),
          $('<a>').attr({
            href: 'modules/cloudPlatform/eula/' + g_lang + '/EULA.pdf',
            target: '_blank'
          }).html(_dictionary['label.download.eula'])
        )
      );

      $agreeButton.click(complete);
      $eula.append($eulaContainer, $agreeButton);

      return $eula;
    };

    var resizeLoginFooter = function() {
      var $footer = $('.login .footer');

      $footer.width($(window).width());
      $footer.css({
        top: $(window).height() - 250
      });
    };

    var $loginFooter = $('<div>').addClass('footer');

    $(window).resize(function() {
      resizeLoginFooter();
    });

    $('#template .login').append($loginFooter);
    resizeLoginFooter();

    $(window).bind('cloudStack.init', function() {
      $('#template').html(
        replace($('#template').html())
      );

      // Update logos
      $(window).bind('cloudStack.ready', function() {
        $('#header .controls').append($('<div>').attr('id', 'citrix-logo'));

        // Change help link
        var $link = $('#user-options a.help');

        $link.unbind('click').bind('click', function() {
          var helpURL = 'http://support.citrix.com/proddocs/topic/cloudplatform/clst-wrapper.html';

          window.open(helpURL, '_blank');

          return false;
        });
      });

      cloudStack.modules.cloudPlatform.dictionary = {
        en: {
          'message.eula.desc': 'Please read and agree to the provided End User License Agreement before continuing.',
          'label.download.eula': 'Download Citrix License Agreement'
        },
        ja_JP: {
          'message.eula.desc': 'Please read and agree to the provided End User License Agreement before continuing.',
          'label.download.eula': 'Download Citrix License Agreement'
        },
        zh_CN: {
          'message.eula.desc': 'Please read and agree to the provided End User License Agreement before continuing.',
          'label.download.eula': 'Download Citrix License Agreement'
        }
      };

      // Replace 'CloudStack' -> 'CloudPlatform'
      cloudStack.localizationFn = function(str) {
        return dictionary[str] ? replace(dictionary[str]) : str;
      };

      // Remove unsupported languages
      $('div.login .select-language select option').each(function() {
        var $option = $(this);

        if ($option.val() && $.inArray($option.val(), supportedLanguages) == -1) {
          $option.remove();
        }
      });

      // If browser is using non-supported language, fallback to English
      if (!$.cookie('lang') && $.inArray(navigator.language, supportedLanguages) == -1) {
        $.cookie('lang', 'en');
        window.g_lang='en';
      }

      // Add EULA to install process
      cloudStack.preInstall = eula;

      // Make XenServer default option in hypervisor fields
      $(window).bind('cloudStack.createForm.makeFields', function (e, data) {
        if (data.fields.hypervisor) {
          data.fields.hypervisor.defaultValue = 'XenServer';
        }
      });
    });
  };
}(jQuery, cloudStack));
