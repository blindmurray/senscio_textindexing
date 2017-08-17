require.config({
  paths: {
    'jquerymin': 'node_modules/jquery/dist/jquery.min.js',
    'gaClient': 'https://apis.google.com/js/client.js',
    'gaApi': 'https://apis.google.com/js/api.js',,
    'jquery': 'jquery-3.2.1.min.js'
    'jqueryUI4': 'https://code.jquery.com/jquery-1.12.4.js',
  	'jqueryUI1': 'https://code.jquery.com/ui/1.12.1/jquery-ui.js'
  }
 
});
require(['jqueryUI4, jqueryUI1, jquery, gaApi, gaClient,jquerymin'], function (dependency) {});

