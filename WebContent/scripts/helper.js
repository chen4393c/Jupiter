//--------------------------------------------------------------------
// Helper functions
//--------------------------------------------------------------------

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

/**
 * AJAX helper
 * @param method: GET|POST|PUT|DELETE
 * @param url: API end point
 * @param body: other request resource
 * @param callback: successful callback
 * @param errorHandler: failed callback
 * */
function sendRequest(method, url, body, callback, errorHandler) {
    var xhr = new XMLHttpRequest();

    xhr.open(method, url, true); // sync

    xhr.onload = function () {
        if (xhr.status === 200) {
            callback(xhr.responseText);
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
