function signUp() {
    var username = $('username-input').value;
    var password = $('password-input').value;
    password = md5(username + md5(password));
    var firstName = $('first-name-input').value;
    var lastName = $('last-name-input').value;
    
    if (username && password && firstName && lastName) {
        console.log("ready to send sign-up post");
        // The request parameters
        var url = './sign-up';
        var req = JSON.stringify({
            user_id : username,
            password : password,
            first_name: firstName,
            last_name: lastName
        });

        sendRequest('POST', url, req, function(response) {
            // successful callback
            var result = JSON.parse(response);
            // successfully  in
            if (result.status === 'OK') {
                $('error-msg').innerHTML = '';
                window.setTimeout(function(){ window.location = "index.html"; }, 1);
            } else {
                $('error-msg').innerHTML = 'Please use another username.';
                return false;
            }
        });
        return false;
    }
}