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
        var welcomeMsg = $('welcome-msg');
        welcomeMsg.innerHTML = `Welcome, ${userFullName}!`;

        // Register event listeners
        $('login-btn').addEventListener('click', login);
        $('nearby-btn').addEventListener('click', loadNearbyItems);
        $('fav-btn').addEventListener('click', loadFavoriteItems);
        $('recommend-btn').addEventListener('click', loadRecommendedItems);

        validateSession();
    }

    /**
     * Session
     */
    function validateSession() {
        // The request parameters
        var url = './login';
        var req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Validating session...');
        // make AJAX call
        ajax('GET', url, req, function(res) {
            // session is still valid
            var result = JSON.parse(res);
            if (result.status === 'OK') {
                onSessionValid(result);
            }
        });
    }

    function onSessionValid(result) {
        userId = result.user_id;
        userFullName = result.name;
        var loginForm = $('login-form');
        var itemNav = $('item-nav');
        var itemList = $('item-list');
        var avatar = $('avatar');
        var welcomeMsg = $('welcome-msg');
        var logoutBtn = $('logout-link');
        welcomeMsg.innerHTML = 'Welcome, ' + userFullName;
        showElement(itemNav);
        showElement(itemList);
        showElement(avatar);
        showElement(welcomeMsg);
        showElement(logoutBtn, 'inline-block');
        hideElement(loginForm);
        initGeoLocation();
    }

    function onSessionInvalid() {
        var loginForm = $('login-form');
        var itemNav = $('item-nav');
        var itemList = $('item-list');
        var avatar = $('avatar');
        var welcomeMsg = $('welcome-msg');
        var logoutBtn = $('logout-link');
        hideElement(itemNav);
        hideElement(itemList);
        hideElement(avatar);
        hideElement(logoutBtn);
        hideElement(welcomeMsg);
        showElement(loginForm);
    }

    function initGeoLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(onPositionUpdated,
                onLoadPositionFailed, {
                    maximumAge: 60000
                });
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
                lng = loc[1];
            } else {
                console.warn('Getting location by IP failed.');
            }
            loadNearbyItems();
        });
    }

    /**
     * Login
     * */
    function login() {
        var username = $('username').value;
        var password = $('password').value;
        password = md5(username + md5(password));

        // The request parameters
        var url = './login';
        var req = JSON.stringify({
            user_id : username,
            password : password,
        });
        ajax('POST', url, req, function(response) {
            // successful callback
            var result = JSON.parse(response);
            // successfully logged in
            if (result.status === 'OK') {
                onSessionValid(result);
                clearLoginError();
            }}, function() {
                // error
                showLoginError();
        });
    }

    function showLoginError() {
        $('login-error').innerHTML = 'Invalid username or password';
    }

    function clearLoginError() {
        $('login-error').innerHTML = '';
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
        for (var i = 0; i < buttons.length; i++) {
            let button = buttons[i];
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
        itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> ' + msg + '</p>';
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
     * @param body: other request resource
     * @param callback: successful callback
     * @param errorHandler: failed callback
     * */
    function ajax(method, url, body, callback, errorHandler) {
        var xhr = new XMLHttpRequest();

        xhr.open(method, url, true); // async

        xhr.onload = function () {
            if (xhr.status === 200) {
                callback(xhr.responseText);
            } else if (xhr.status === 403) {
                onSessionInvalid();
            } else {
                errorHandler();
            }
        };

        xhr.onerror = function () {
            console.error("The request couldn't be completed.");
            errorHandler();
        };

        if (body === null) {
            xhr.send();
        } else {
            xhr.setRequestHeader("Content-Type",
                "application/json;charset=utf-8");
            xhr.send(body);
        }
    }

    // ----------------------------------------------
    // AJAX call server-side APIs
    // ----------------------------------------------

    /**
     * API #1 Load the nearby items
     * API end point: [GET] /search?user_id=1111&lat=37.38&lon=-122.08
     * */
    function loadNearbyItems() {
        activateButton('nearby-btn');

        // The request parameters
        var url = './search';
        var params = `user_id=${userId}&lat=${lat}&lon=${lng}`;
        var body = JSON.stringify({}); // other request resource

        // Display loading message
        showLoadingMessage('Loading nearby events...');

        // Make AJAX call
        ajax('GET', `${url}?${params}`, body, function (response) {
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
     * API #2 Load favorite items
     *
     * API end point: GET /history?user_id=1111
     * */
    function loadFavoriteItems() {
        activateButton('fav-btn');

        // The request parameters
        var url = './history';
        var params = `user_id=${userId}`;
        var body = JSON.stringify({});

        // Display loading message
        showLoadingMessage('Loading favorite events');

        // Make AJAX call
        ajax('GET', `${url}?${params}`, body, function (response) {
            var items = JSON.parse(response);
            if (!items || items.length === 0) {
                showWarningMessage('No favorite event.');
            } else {
                listItems(items);
            }
        }, function () {
            showErrorMessage('Cannot load favorite events.');
        });
    }

    /**
     * API #3 Load recommended items
     *
     * API end point: GET /recommendation?user_id=1111
     * */
    function loadRecommendedItems() {
        activateButton('recommend-btn');

        // The request parameters
        var url = './recommendation';
        var params = `user_id=${userId}&lat=${lat}&lon=${lng}`;
        var body = JSON.stringify({});

        // Display loading message
        showLoadingMessage('Loading recommended events...');

        // Make AJAX call
        ajax('GET', `${url}?${params}`, body, function (response) {
            var items = JSON.parse(response);
            if (!items || items.length === 0) {
                showWarningMessage('No recommended event. Make sure you have favorite event');
            } else {
                listItems(items);
            }
        }, function () {
            showErrorMessage('Cannot load recommended events.');
        });
    }

    /**
     * API #4 Toggle favorite items
     * @param itemId
     *
     * API end point: [POST]/[DELETE] /history
     * request JSON data:
     * {
     *  user_id: 1111,
     *  favorite: [itemId1, itemId2, ...]
     * }
     * */
    function changeFavoriteItem(itemId) {
        var li = $(`item-${itemId}`);
        var favIcon = $(`fav-icon-${itemId}`);
        var favorite = li.dataset.favorite !== 'true';

        // The request parameters
        var url = './history';
        var body = JSON.stringify({
            user_id: userId,
            favorite: [itemId]
        });
        var method = favorite ? 'POST' : 'DELETE';

        ajax(method, url, body, function (response) {
            var result = JSON.parse(response);
            if (result.result === 'SUCCESS') {
                li.dataset.favorite = favorite;
                favIcon.className = favorite ? 'fa fa-heart' : 'fa fa-heart-o';
            }
        }, function () {
            showErrorMessage('Cannot mark favorite item');
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
