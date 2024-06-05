const token = localStorage.getItem('jwtToken');

function handleResponse(response) {
    if (!response.ok) {
        throw new Error('Network response was not ok');
    }
    return response.json();
}


const allSideMenu = document.querySelectorAll('#sidebar .side-menu.top li a');

allSideMenu.forEach(item=> {
    const li = item.parentElement;

    item.addEventListener('click', function () {
        allSideMenu.forEach(i=> {
            i.parentElement.classList.remove('active');
        })
        li.classList.add('active');
    })
});




// TOGGLE SIDEBAR
const menuBar = document.querySelector('#content nav .bx.bx-menu');
const sidebar = document.getElementById('sidebar');

menuBar.addEventListener('click', function () {
    sidebar.classList.toggle('hide');
})







const searchButton = document.querySelector('#content nav form .form-input button');
const searchButtonIcon = document.querySelector('#content nav form .form-input button .bx');
const searchForm = document.querySelector('#content nav form');

searchButton.addEventListener('click', function (e) {
    if(window.innerWidth < 576) {
        e.preventDefault();
        searchForm.classList.toggle('show');
        if(searchForm.classList.contains('show')) {
            searchButtonIcon.classList.replace('bx-search', 'bx-x');
        } else {
            searchButtonIcon.classList.replace('bx-x', 'bx-search');
        }
    }
})





if(window.innerWidth < 768) {
    sidebar.classList.add('hide');
} else if(window.innerWidth > 576) {
    searchButtonIcon.classList.replace('bx-x', 'bx-search');
    searchForm.classList.remove('show');
}


window.addEventListener('resize', function () {
    if(this.innerWidth > 576) {
        searchButtonIcon.classList.replace('bx-x', 'bx-search');
        searchForm.classList.remove('show');
    }
})



const switchMode = document.getElementById('switch-mode');

switchMode.addEventListener('change', function () {
    if(this.checked) {
        document.body.classList.add('dark');
    } else {
        document.body.classList.remove('dark');
    }
})

function getUserAccounts() {
    const userAccountsResult = document.getElementById('userAccountsResult');
    const accountsTable = document.getElementById('accountsTable');
    const accountsTableBody = accountsTable.querySelector('tbody');
    const accountCountElement = document.getElementById('accountCount');
    const totalBalanceElement = document.getElementById('totalBalance');


    fetch('/user/my-accounts', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }
    })
        .then(handleResponse)
        .then(data => {
            // Clear previous data
            accountsTableBody.innerHTML = '';

            let totalBalance = 0;

            data.forEach(account => {
                const row = document.createElement('tr');
                row.innerHTML = `
                        <td>${account.accountNumber}</td>
                        <td>${account.balance.toFixed(2)}</td>
                        <td>${account.accountTier}</td>
                    `;
                accountsTableBody.appendChild(row);

                totalBalance += account.balance;

            });

            accountCountElement.textContent = data.length;
            totalBalanceElement.textContent = `${totalBalance.toFixed(2)}`;


            // Display the table
            accountsTable.style.display = 'table';
        })
        .catch(error => console.error('Error:', error));
}
document.addEventListener('DOMContentLoaded', getUserAccounts);



//sideBar

const dashboardSection = document.getElementById('dashboard');
const transactionSection = document.getElementById('transaction');
const analyticSection = document.getElementById('analytic');
const convertSection = document.getElementById('convert');

// Get the dashboard and transaction links
const dashboardButton = document.getElementById('dashboardButton');
const transactionButton = document.getElementById('transactionButton');
const analyticButton = document.getElementById('analyticButton');
const convertButton = document.getElementById('convertButton');


dashboardSection.style.display = 'block';
transactionSection.style.display = 'none';
analyticSection.style.display = 'none';
convertSection.style.display = 'none';


dashboardButton.addEventListener('click', () => {
    if (dashboardSection.style.display === 'none') {
        dashboardSection.style.display = 'block';
        transactionSection.style.display = 'none';
        analyticSection.style.display = 'none';
        convertSection.style.display = 'none';
    }
});


transactionButton.addEventListener('click', () => {
    if (transactionSection.style.display === 'none') {
        transactionSection.style.display = 'block';
        dashboardSection.style.display = 'none';
        analyticSection.style.display = 'none';
        convertSection.style.display = 'none';
    }
});

analyticButton.addEventListener('click', () => {
    if (analyticSection.style.display === 'none') {
        transactionSection.style.display = 'none';
        dashboardSection.style.display = 'none';
        analyticSection.style.display = 'block';
        convertSection.style.display = 'none';
    }
});

convertButton.addEventListener('click', () => {
    if (convertSection.style.display === 'none') {
        transactionSection.style.display = 'none';
        dashboardSection.style.display = 'none';
        analyticSection.style.display = 'none';
        convertSection.style.display = 'block';
    }
});


// untuk logout nanti buat func logout untuk deauth api
const logOutButton = document.getElementById('logOutButton');

logOutButton.addEventListener('click',() => {



})

const dashboardHomeButton = document.getElementById('home1');
const transactionHomeButton = document.getElementById('home2');
const analyticHomeButton = document.getElementById('home3');
const convertHomeButton = document.getElementById('home4');

dashboardHomeButton.addEventListener('click', () => {
    dashboardSection.style.display = 'block';
    transactionSection.style.display = 'none';
    analyticSection.style.display = 'none';
    convertSection.style.display = 'none';
});


transactionHomeButton.addEventListener('click', () => {
    dashboardSection.style.display = 'block';
    transactionSection.style.display = 'none';
    analyticSection.style.display = 'none';
    convertSection.style.display = 'none';
});

analyticHomeButton.addEventListener('click', () => {
    dashboardSection.style.display = 'block';
    transactionSection.style.display = 'none';
    analyticSection.style.display = 'none';
    convertSection.style.display = 'none';
});

convertHomeButton.addEventListener('click', () => {
    dashboardSection.style.display = 'block';
    transactionSection.style.display = 'none';
    analyticSection.style.display = 'none';
    convertSection.style.display = 'none';
});
    document.addEventListener('DOMContentLoaded', function () {
        const depositForm = document.getElementById('depositForm');
        const depoButton = document.getElementById('depositButton');
        const modal = document.getElementById('myModal');
        const modalMessage = document.getElementById('modalMessage');
        const span = document.getElementsByClassName('close')[0];

        // Ensure the elements are present before adding event listeners
        if (depositForm && depoButton) {
            depoButton.addEventListener('click', function (event) {
                event.preventDefault();
                console.log('Button clicked!');

                const accountNumber = document.getElementById('accountNumber').value;
                const amount = document.getElementById('amount').value;
                const description = document.getElementById('description').value;

                console.log(`Account number: ${accountNumber}`);
                console.log(`Amount: ${amount}`);
                console.log(`Description: ${description}`);

                fetch('/user/accounts/deposit-money', {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer ' + token,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ currentAccountNumber: accountNumber, amount: amount, description: description })
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log('Response received!');
                        if (data.token) {
                            localStorage.setItem('jwtToken', data.token);
                            modalMessage.textContent = 'DEPOSIT SUCCESSFUL';
                            modal.style.display = 'block';
                        }

                    })
                    .catch(error => {
                        console.error('Error:', error);
                        modalMessage.textContent = 'DEPOSIT FAILED';
                        modal.style.display = 'block';
                    });
            });
            span.onclick = function () {
                modal.style.display = 'none';
            };
            window.onclick = function (event) {
                if (event.target === modal) {
                    modal.style.display = 'none';
                }
            };
        } else {
            console.error('Form or button element not found');
        }
    });