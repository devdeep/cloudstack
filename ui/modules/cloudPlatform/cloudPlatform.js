(function($, cloudStack) {
  // Remove unsupported items from UI
  var removeUnsupported = function() {
    var unsupportedHypervisors = ['LXC', 'Ovm'];
    var systemSubsections = cloudStack.sections.system.subsections;
    var addClusterForm = systemSubsections.clusters.listView.actions.add.createForm;
    var registerTemplateForm = cloudStack.sections.templates.sections.templates.listView.actions.add.createForm;
    var zoneWizardForm = cloudStack.zoneWizard.forms.zone;

    $('.zone-wizard:visible .setup-physical-network .input-area select').live('mousedown', function() {
      $(this).find('option').each(function () {
        var val = $(this).val();

        if ($.inArray(val, ['GRE', 'VNS', 'SSP']) > -1) {
          $(this).remove();
        }
      });
    });

    zoneWizardForm.fields.hypervisor.select = function (args) {
      $.ajax({
        url: createURL('listHypervisors'),
        async: false,
        data: {
          listAll: true
        },
        success: function (json) {
          var items = json.listhypervisorsresponse.hypervisor;
          var array1 = [];

          var firstOption = "XenServer";
          var nonSupportedHypervisors = {};
          if (args.context.zones[0]['network-model'] == "Advanced" && args.context.zones[0]['zone-advanced-sg-enabled'] == "on") {
            firstOption = "KVM";
            nonSupportedHypervisors["VMware"] = 1;
            nonSupportedHypervisors["BareMetal"] = 1;
            nonSupportedHypervisors["Hyperv"] = 1;
            nonSupportedHypervisors["Ovm"] = 1;
            nonSupportedHypervisors["LXC"] = 1;
          }

          if (items != null) {
            for (var i = 0; i < items.length; i++) {
              if (items[i].name in nonSupportedHypervisors)
                continue;

              if (items[i].name == firstOption)
                array1.unshift({
                  id: items[i].name,
                  description: items[i].name
                });
              else if ($.inArray(items[i].name, unsupportedHypervisors) === -1)
                array1.push({
                  id: items[i].name,
                  description: items[i].name
                });
            }
          }
          args.response.success({
            data: array1
          });
        }
      })
    };

    addClusterForm.fields.hypervisor.select = function(args) {
      $.ajax({
        url: createURL("listHypervisors"),
        dataType: "json",
        async: false,
        success: function(json) {
          var hypervisors = json.listhypervisorsresponse.hypervisor;
          var items = [];
          $(hypervisors).each(function() {
            if ($.inArray(this.name, unsupportedHypervisors) === -1) {
              items.push({
                id: this.name,
                description: this.name
              });
            }
          });
          args.response.success({
            data: items
          });
        }
      });
    };

    registerTemplateForm.fields.hypervisor.select = function(args) {
      if (args.zone == null)
        return;

      var apiCmd;
      if (args.zone == -1) { //All Zones
        //apiCmd = "listHypervisors&zoneid=-1"; //"listHypervisors&zoneid=-1" has been changed to return only hypervisors available in all zones (bug 8809)
        apiCmd = "listHypervisors";
      }
      else {
        apiCmd = "listHypervisors&zoneid=" + args.zone;
      }

      $.ajax({
        url: createURL(apiCmd),
        dataType: "json",
        async: false,
        success: function(json) {
          var hypervisorObjs = json.listhypervisorsresponse.hypervisor;
          var items = [];
          $(hypervisorObjs).each(function() {
            if ($.inArray(this.name, unsupportedHypervisors) === -1) {
              items.push({
                id: this.name,
                description: this.name
              });
            }
          });
          args.response.success({
            data: items
          });
        }
      });

      args.$select.change(function() {
        var $form = $(this).closest('form');
        if ($(this).val() == "VMware") {
          $form.find('.form-item[rel=rootDiskControllerType]').css('display', 'inline-block');
          $form.find('.form-item[rel=nicAdapterType]').css('display', 'inline-block');
          $form.find('.form-item[rel=keyboardType]').css('display', 'inline-block');

          $form.find('.form-item[rel=xenserverToolsVersion61plus]').hide();
        } else if ($(this).val() == "XenServer") {
          $form.find('.form-item[rel=rootDiskControllerType]').hide();
          $form.find('.form-item[rel=nicAdapterType]').hide();
          $form.find('.form-item[rel=keyboardType]').hide();

          if (isAdmin())
            $form.find('.form-item[rel=xenserverToolsVersion61plus]').css('display', 'inline-block');
        } else {
          $form.find('.form-item[rel=rootDiskControllerType]').hide();
          $form.find('.form-item[rel=nicAdapterType]').hide();
          $form.find('.form-item[rel=keyboardType]').hide();

          $form.find('.form-item[rel=xenserverToolsVersion61plus]').hide();
        }
      });

      args.$select.trigger('change');
    };
  };

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

        $eulaContainer.append(
            $('<iframe>').attr({ src: 'modules/cloudPlatform/eula.' + g_lang + '.html' })
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

      // Add EULA to install process
      cloudStack.preInstall = eula;

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

      removeUnsupported();
    });
  };
}(jQuery, cloudStack));
