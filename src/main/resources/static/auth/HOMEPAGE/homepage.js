const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');

signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
});


//untuk sign in
document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    fetch('/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email: email, password: password })
    })
        .then(response => response.json())
        .then(data => {
            if (data.token) {
                localStorage.setItem('jwtToken', data.token);
                window.location.href = 'main/mainPage.html';
            } else {
                alert('Login failed. Please check your credentials.');
            }
        })
        .catch(error => console.error('Error:', error));
});

//untuk sign up
document.getElementById('registerForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const emailReg = document.getElementById('emailReg').value;
    const passwordReg = document.getElementById('passwordReg').value;
    const firstName = document.getElementById('firstNameReg').value;
    const lastName = document.getElementById('lastNameReg').value;
    const phoneNum = document.getElementById('phoneNumReg').value;
    const address = document.getElementById('addressReg').value;
    const district = document.getElementById('districtReg').value;
    const place = document.getElementById('placeReg').value;
    const date = document.getElementById('dateReg').value;
    const role = "USER";

    fetch('/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email: emailReg, password: passwordReg, firstName: firstName, lastName: lastName,
            phoneNumber: phoneNum, plainAddress: address, district: district, place: place, dateOfBirth: date, role: role,
            cards: []
        })
    })
        .then(response => {console.log('Response Status: ', response.status); return response.json()})
        .then(data => {
            if (data.token) {
                localStorage.setItem('jwtToken', data.token);
                alert('USER CREATED SUCCESFULLY!!')
            } else {
                alert('Register failed. Please check your credentials.');
            }
        })
        .catch(error => console.error('Error:', error));
});