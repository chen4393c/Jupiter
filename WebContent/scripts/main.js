(function () {
  /**
   * Variables
   * */
  var userId = '1111';
  var userFullName = 'Chaoran Chen';
  var lat = 37.38;
  var lng = -122.08;

  /**
   * Initialization
   * */
  function init() {
    // Register event listeners
    var nearbyButton = $('nearby-btn');
    if (nearbyButton) {
      nearbyButton.addEventListener('click', loadNearbyItems);
    }
    var welcomeMsg = $('welcome-msg');
    if (welcomeMsg) {
      welcomeMsg.innerHTML = 'Welcome, ' + userFullName;
    }
    initGeolocation();
  }

  function initGeolocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(onPositionUpdated, onLoadPositionFailed, { maximumAge: 60000 });
      showLoadingMessage('Retrieving your location...');
    } else {
      onLoadPositionFailed();
    }
  }

  function onPositionUpdated(position) {
    lat = position.coords.latitude;
    lng = position.coords.longitude;

    loadNearbyItems();
  }

  function onLoadPositionFailed() {
    console.warn('navigator.geolocation is not available');
    getLocationFromIP();
  }

  function getLocationFromIP() {
    // Get location from http://ipinfo.io/json
    var url = 'http://ipinfo.io/json';
    var req = null; // query parameters
    ajax('GET', url, req, function (response) {
      var result = JSON.parse(response);
      if ('loc' in result) {
        var loc = result.loc.split(',');
        lat = loc[0];
        lon = loc[1];
      } else {
        console.warn('Getting location by IP failed.');
      }
    });
  }

  //--------------------------------------------------------------------
  // Helper functions
  //--------------------------------------------------------------------

  /**
   * A helper function that makes a navigation button active
   * @param buttonId
   * */
  function activateButton(buttonId) {
    var buttons = document.getElementsByClassName('main-nav-btn');

    // Deactivate all navigation buttons
    for (var button in buttons) {
      if (button.className) {
        button.className = button.className.replace(/\bactive\b/, '');
      }
    }
    // Activate the one whose id is buttonId
    var selectedButton = $(buttonId);
    selectedButton.className += ' active';
  }

  function showLoadingMessage(msg) {
    var itemList = $('item-list');
    if (itemList) {
      itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> ' + msg + '</p>';
    }
  }

  function showWarningMessage(msg) {
    var itemList = $('item-list');
    itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> ' + msg + '</p>';
  }

  function showErrorMessage(msg) {
    var itemList = $('item-list');
    itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> ' + msg + '</p>';
  }

  /**
   * A helper function that creates a DOM element <tag options...>
   *   @param tag
   *   @param options
   *   @return DOM element
   * */
  function $(tag, options) {
    if (!options) {
      return document.getElementById(tag);
    }

    var element = document.createElement(tag);

    for (var option in options) {
      if (options.hasOwnProperty(option)) {
        element[option] = options[option];
      }
    }

    return element;
  }

  function hideElement(element) {
    element.style.display = 'none';
  }

  function showElement(element, style) {
    var displayStyle = style ? style : 'block';
    element.style.display = displayStyle;
  }

  /**
   * AJAX helper
   * @param method: GET|POST|PUT|DELETE
   * @param url: API end point
   * @param data: other request resource
   * @param callback: successful callback
   * @param errorHandler: failed callback
   * */
  function ajax(method, url, data, callback, errorHandler) {
    var xhr = new XMLHttpRequest();

    xhr.open(method, url, true); // async

    xhr.onload = function () {
      if (xhr.status === 200) {
        callback(xhr.responseText);
      }  else if (xhr.status === 403) {
        onSessionInvalid();
      } else {
        errorHandler();
      }
    };

    xhr.onerror = function () {
      console.error("The request couldn't be completed.");
      errorHandler();
    };

    if (data === null) {
      xhr.send();
    } else {
      xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
      xhr.send(data);
    }
  }

  // ----------------------------------------------
  // AJAX call server-side APIs
  // ----------------------------------------------

  /**
   * API #1 Load the nearby items
   * API end point: [GET] /Jupiter/search?user_id=1111&lat=37.38&lon=-122.08
   * */
  function loadNearbyItems() {
    console.log('loadNearbyItems');
    activateButton('nearby-btn');

    // The request parameters
    var url = './search';
    var params = `user_id=${userId}&lat=${lat}&lon=${lng}`;
    var req = JSON.stringify({}); // other request resource

    // Display loading message
    showLoadingMessage('Loading nearby events...');

    // Make AJAX call
    ajax('GET', `${url}?${params}`, req, function (response) {
      // Successful callback
      var items = JSON.parse(response);
      if (!items || items.length === 0) {
        // when items is undefined, null or empty
        showWarningMessage('No nearby event.');
      } else {
        listItems(items);
      }
    }, function () {
      // Failure callback
      showErrorMessage('Cannot load nearby events.');
    });
  }

  /**
   * This helper function is used to create item list
   * @param items: an array of JSON-format item
   * */
  function listItems(items) {
    // Clear current results
    var itemList = $('item-list');
    itemList.innerHTML = '';

    for (var i = 0; i < items.length; i++) {
      addItem(itemList, items[i]);
    }
  }

  /**
   * This function is to add item into the list
   * @param itemList: the <ul id="item-list"> tag
   * @param item: JSON object
   * */
  function addItem(itemList, item) {
    if (item && item.item_id) {
      var itemId = item.item_id;
    }

    // Create the <li> tag and specify the ud and class attributes
    var li = $('li', {
      id: `item-${itemId}`,
      className: 'item'
    });

    // Set the data attribute
    li.dataset.item_id = itemId;
    li.dataset.favorite = item.favorite;
    // item image
    if (item.image_url) {
      li.appendChild($('img', {
        src: item.image_url
      }));
    } else {
      li.appendChild($('img', {
        src: 'https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png'
      }));
    }

    // section
    var section = $('div', {});
    // title
    var title = $('a', {
      href: item.url,
      target: '_blank',
      className: 'item-name'
    });
    title.innerHTML = item.name;
    section.appendChild(title);
    // category
    var category = $('p', {
      className: 'item-category'
    });
    if (item.categories) {
      category.innerHTML = 'Category: ' + item.categories.join(', ');
    }
    section.appendChild(category);

    // stars
    var stars = $('div', {
      className: 'stars'
    });
    section.appendChild(stars);
    li.appendChild(section);

    // address
    var address = $('p', {
      className: 'item-address'
    });
    if (item.address) {
      address.innerHTML = item.address.replace(/,/g, '<br/>').replace(/\"/g, '');
    }
    li.appendChild(address);

    // favorite link
    var favLink = $('p', {
      className: 'fav-link'
    });

    favLink.onclick = function () {
      changeFavoriteItem(itemId);
    }

    favLink.appendChild($('i', {
      id: 'fav-icon-' + itemId,
      className: item.favorite ? 'fa fa-heart' : 'fa fa-heart-o'
    }));
    li.appendChild(favLink);

    itemList.appendChild(li);
  }

  init();

})();
