const token = localStorage.getItem('jwtToken');

function handleResponse(response) {
    if (!response.ok) {
        throw new Error('Network response was not ok');
    }
    return response.json();
}


const allSideMenu = document.querySelectorAll('#sidebar .side-menu.top li a');

allSideMenu.forEach(item => {
    const li = item.parentElement;

    item.addEventListener('click', function () {
        allSideMenu.forEach(i => {
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
    if (window.innerWidth < 576) {
        e.preventDefault();
        searchForm.classList.toggle('show');
        if (searchForm.classList.contains('show')) {
            searchButtonIcon.classList.replace('bx-search', 'bx-x');
        } else {
            searchButtonIcon.classList.replace('bx-x', 'bx-search');
        }
    }
})


if (window.innerWidth < 768) {
    sidebar.classList.add('hide');
} else if (window.innerWidth > 576) {
    searchButtonIcon.classList.replace('bx-x', 'bx-search');
    searchForm.classList.remove('show');
}


window.addEventListener('resize', function () {
    if (this.innerWidth > 576) {
        searchButtonIcon.classList.replace('bx-x', 'bx-search');
        searchForm.classList.remove('show');
    }
})


const switchMode = document.getElementById('switch-mode');

switchMode.addEventListener('change', function () {
    if (this.checked) {
        document.body.classList.add('dark');
    } else {
        document.body.classList.remove('dark');
    }
})

function fetchAllAccounts() {
    const accountsTable = document.getElementById('allAccountTable');
    const accountsTableBody = accountsTable.querySelector('tbody');

    fetch('/goblin/accounts/display-all', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }
    })
        .then(handleResponse)
        .then(data => {
            const accounts = data;
            // Clear previous data
            accountsTableBody.innerHTML = '';

            // Iterate over each account data
            data.forEach(account => {
                // Create a new row
                const row = document.createElement('tr');

                // Populate the row with account data
                row.innerHTML = `
                    <td>${account.id}</td>
                    <td>${account.userDto.id}</td>
                    <td>${account.userDto.email}</td>
                    <td>${account.userDto.firstName}</td>
                    <td>${account.userDto.lastName}</td>
                    <td>${account.userDto.phoneNumber}</td>
                    <td>${account.accountNumber}</td>
                    <td>${account.balance}</td>
                    <td>${account.createdAt}</td>
                    <td>${account.accountTier}</td>
                `;

                // Append the row to the table body
                accountsTableBody.appendChild(row);
            });

            // Display the table
            accountsTable.style.display = 'table';
        })
        .catch(error => console.error('Error:', error));
}

document.addEventListener('DOMContentLoaded', fetchAllAccounts);

function fetchTotalNumberOfAccounts() {
    return fetch('/goblin/accounts/display-all', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }
    })
        .then(handleResponse)
        .then(data => {
            return data.length; // Return the total number of accounts
        })
        .catch(error => {
            console.error('Error:', error);
            return 0; // Return 0 if there's an error
        });
}

function fetchAllAccountsAndGenerateUserWealthTable() {
    const accountsTable = document.getElementById('wealthTable');
    const accountsTableBody = accountsTable.querySelector('tbody');

    fetch('/goblin/accounts/display-all', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }
    })
        .then(handleResponse)
        .then(accounts => {
            // Calculate user wealth
            const userWealth = calculateUserWealth(accounts);

            // Clear previous data
            accountsTableBody.innerHTML = '';

            // Generate user wealth table
            userWealth.forEach(wealth => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${wealth.userId}</td>
                    <td>${wealth.email}</td>
                    <td>${wealth.firstName}</td>
                    <td>${wealth.lastName}</td>
                    <td>${wealth.wealth}</td>
                `;
                accountsTableBody.appendChild(row);
            });

            // Display the table
            accountsTable.style.display = 'table';
        })
        .catch(error => console.error('Error:', error));
}

document.addEventListener('DOMContentLoaded', fetchAllAccountsAndGenerateUserWealthTable);

function calculateUserWealth(accounts) {
    const userWealthMap = new Map();
    accounts.forEach(account => {
        const userId = account.userDto.id;
        const wealth = account.balance;
        if (userWealthMap.has(userId)) {
            const currentWealth = userWealthMap.get(userId);
            userWealthMap.set(userId, currentWealth + wealth);
        } else {
            userWealthMap.set(userId, wealth);
        }
    });

    const userWealth = [];
    userWealthMap.forEach((wealth, userId) => {
        const userAccount = accounts.find(account => account.userDto.id === userId);
        const { email, firstName, lastName } = userAccount.userDto;
        userWealth.push({ userId, email, firstName, lastName, wealth });
    });

    return userWealth;
}

function generateWealthChart() {
    fetch('/goblin/accounts/display-all', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }
    })
        .then(handleResponse)
        .then(accounts => {
            // Calculate user wealth
            const userWealth = calculateUserWealth(accounts);

            // Generate chart
            generateChart(userWealth);
        })
        .catch(error => console.error('Error:', error));
}


function generateChart(userWealth) {
    const ctx = document.getElementById('userWealthChart').getContext('2d');
    const userIds = userWealth.map(wealth => wealth.userId);
    const wealthValues = userWealth.map(wealth => wealth.wealth);

    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: userIds,
            datasets: [{
                label: 'User Wealth',
                data: wealthValues,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(255, 159, 64, 0.2)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            aspectRatio: 3,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', generateWealthChart);

function fetchAllUsers() {
    const usersTable = document.getElementById('allUserTable');
    const usersTableBody = usersTable.querySelector('tbody');

    fetch('/goblin/user/display-all', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }
    })
        .then(handleResponse)
        .then(data => {
            // Clear previous data
            usersTableBody.innerHTML = '';

            // Iterate over each user
            data.forEach(user => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.email}</td>
                    <td>${user.phoneNumber}</td>
                    <td>${user.dateOfBirth}</td>
                    <td>${user.address.plainAddress}</td>
                    <td>${user.address.district.title}</td>
                    <td>${user.address.place.title}</td>
                    <td>${user.role ? user.role : 'No role'}</td>
                `;
                usersTableBody.appendChild(row);
            });

            // Display the table
            usersTable.style.display = 'table';
        })
        .catch(error => console.error('Error:', error));
}

document.addEventListener('DOMContentLoaded', fetchAllUsers);

//sideBar

const dashboardSection = document.getElementById('dashboard');
const transactionAnalyticsSection = document.getElementById('transactionAnalytics');
const userManagerSection = document.getElementById('userManager');
const accountManagerSection = document.getElementById('accountManager');
const currencyManagerSection = document.getElementById('currencyManager');

// Get the dashboard and transaction links
const dashboardButton = document.getElementById('dashboardButton');
const transactionAnalyticsButton = document.getElementById('transactionAnalyticsButton');
const userManagerButton = document.getElementById('userManagerButton');
const accountManagerButton = document.getElementById('accountManagerButton');
const currencyManagerButton = document.getElementById('currencyManagerButton');


dashboardSection.style.display = 'block';
transactionAnalyticsSection.style.display = 'none';
userManagerSection.style.display = 'none';
accountManagerSection.style.display = 'none';
currencyManagerSection.style.display = 'none';


dashboardButton.addEventListener('click', () => {
    if (dashboardSection.style.display === 'none') {
        dashboardSection.style.display = 'block';
        transactionSection.style.display = 'none';
        analyticSection.style.display = 'none';
        convertSection.style.display = 'none';
    }
});


transactionAnalyticsButton.addEventListener('click', () => {
    if (transactionAnalyticsSection.style.display === 'none') {
        transactionAnalyticsSection.style.display = 'block';
        dashboardSection.style.display = 'none';
        userManagerSection.style.display = 'none';
        accountManagerSection.style.display = 'none';
        currencyManagerSection.style.display = 'none';
    }
});

userManagerButton.addEventListener('click', () => {
    if (userManagerSection.style.display === 'none') {
        transactionAnalyticsSection.style.display = 'none';
        dashboardSection.style.display = 'none';
        userManagerSection.style.display = 'block';
        accountManagerSection.style.display = 'none';
        currencyManagerSection.style.display = 'none';
    }
});

accountManagerButton.addEventListener('click', () => {
    if (accountManagerSection.style.display === 'none') {
        transactionAnalyticsSection.style.display = 'none';
        dashboardSection.style.display = 'none';
        userManagerSection.style.display = 'none';
        accountManagerSection.style.display = 'block';
        currencyManagerSection.style.display = 'none';
    }


});

currencyManagerButton.addEventListener('click', () => {
    if (currencyManagerSection.style.display === 'none') {
        transactionAnalyticsSection.style.display = 'none';
        dashboardSection.style.display = 'none';
        userManagerSection.style.display = 'none';
        accountManagerSection.style.display = 'none';
        currencyManagerSection.style.display = 'block';
    }


});


// untuk logout nanti buat func logout untuk deauth api
const logOutButton = document.getElementById('logOutButton');

logOutButton.addEventListener('click', () => {


})

const dashboardHomeButton = document.getElementById('home1');
const transactionAnalyticsHomeButton = document.getElementById('home2');
const userManagerHomeButton = document.getElementById('home3');
const accountManagerHomeButton = document.getElementById('home4');
const currencyManagerHomeButton = document.getElementById('home5');


dashboardHomeButton.addEventListener('click', () => {
    dashboardSection.style.display = 'block';
    transactionAnalyticsSection.style.display = 'none';
    userManagerSection.style.display = 'none';
    accountManagerSection.style.display = 'none';
    currencyManagerSection.style.display = 'none';
});


transactionAnalyticsHomeButton.addEventListener('click', () => {
    dashboardSection.style.display = 'block';
    transactionAnalyticsSection.style.display = 'none';
    userManagerSection.style.display = 'none';
    accountManagerSection.style.display = 'none';
    currencyManagerSection.style.display = 'none';
});

userManagerHomeButton.addEventListener('click', () => {
    dashboardSection.style.display = 'block';
    transactionAnalyticsSection.style.display = 'none';
    userManagerSection.style.display = 'none';
    accountManagerSection.style.display = 'none';
    currencyManagerSection.style.display = 'none';
});

accountManagerHomeButton.addEventListener('click', () => {
    dashboardSection.style.display = 'block';
    transactionAnalyticsSection.style.display = 'none';
    userManagerSection.style.display = 'none';
    accountManagerSection.style.display = 'none';
    currencyManagerSection.style.display = 'none';
});

currencyManagerHomeButton.addEventListener('click', () => {
    dashboardSection.style.display = 'block';
    transactionAnalyticsSection.style.display = 'none';
    userManagerSection.style.display = 'none';
    accountManagerSection.style.display = 'none';
    currencyManagerSection.style.display = 'none';
});


document.addEventListener('DOMContentLoaded', function () {

    const updateForm = document.getElementById('updateUserForm');
    const updateButton = document.getElementById('updateUserButton');

    if (updateForm && updateButton) {
        updateButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Update button clicked!');

            // Retrieve form data
            const userId = document.getElementById('userIdUpdate').value;
            const email = document.getElementById('emailUpdate').value;
            const password = document.getElementById('passwordUpdate').value;
            const firstName = document.getElementById('firstNameUpdate').value;
            const lastName = document.getElementById('lastNameUpdate').value;
            const phoneNum = document.getElementById('phoneNumUpdate').value;
            const plainAddress = document.getElementById('plainAddressUpdate').value;
            const district = document.getElementById('districtUpdate').value;
            const place = document.getElementById('placeUpdate').value;
            const dateOfBirth = document.getElementById('dateOfBirthUpdate').value;

            // Log form data
            console.log(`User ID: ${userId}`);
            console.log(`Email: ${email}`);
            console.log(`Password: ${password}`);
            console.log(`First Name: ${firstName}`);
            console.log(`Last Name: ${lastName}`);
            console.log(`Phone Number: ${phoneNum}`);
            console.log(`Address: ${plainAddress}`);
            console.log(`District: ${district}`);
            console.log(`Place: ${place}`);
            console.log(`Date of Birth: ${dateOfBirth}`);

            // Fetch API call for update
            fetch('/goblin/user/update', {
                method: 'PUT',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    userId: userId,
                    email: email,
                    password: password,
                    firstName: firstName,
                    lastName: lastName,
                    phoneNum: phoneNum,
                    plainAddress: plainAddress,
                    district: district,
                    place: place,
                    dateOfBirth: dateOfBirth
                })
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Update response received:', data);
                    // Handle response (e.g., show success message)
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors (e.g., show error message)
                });
        });
    } else {
        console.error('Update form or button element not found');
    }

    const deleteForm = document.getElementById('deleteUserForm');
    const deleteUserButton = document.getElementById('deleteUserButton');

    if (deleteForm && deleteUserButton) {
        deleteUserButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Delete button clicked!');

            // Retrieve form data
            const userId = document.getElementById('userIdDelete').value;

            // Log form data
            console.log(`User ID: ${userId}`);

            // Fetch API call for delete
            fetch('/goblin/user/delete', {
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ userId: userId })
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Update response received:', data);
                    // Handle response (e.g., show success message)
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors (e.g., show error message)
                });
        });
    } else {
        console.error('Delete form or button element not found');
    }

    const createAccountForm = document.getElementById('createAccountForm');
    const createAccountButton = document.getElementById('createAccountButton');

    if (createAccountForm && createAccountButton) {
        createAccountButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Create Account button clicked!');

            const userId = document.getElementById('userIdCreate').value;
            const initialBalance = document.getElementById('initialBalanceCreate').value;
            const accountTier = document.getElementById('accountTierCreate').value;

            console.log(`User ID: ${userId}`);
            console.log(`Initial Balance: ${initialBalance}`);
            console.log(`Account Tier: ${accountTier}`);

            fetch('/goblin/accounts/create', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    userId: userId,
                    initialBalance: initialBalance,
                    accountTier: accountTier
                })
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Create Account response received:', data);
                    // Handle response (e.g., show success message)
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors (e.g., show error message)
                });
        });
    } else {
        console.error('Create Account form or button element not found');
    }

    // Update Account
    const updateAccountForm = document.getElementById('updateAccountForm');
    const updateAccountButton = document.getElementById('updateAccountButton');

    if (updateAccountForm && updateAccountButton) {
        updateAccountButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Update Account button clicked!');

            const accountId = document.getElementById('accountIdUpdate').value;
            const accountTier = document.getElementById('accountTierUpdate').value;
            const balance = document.getElementById('balanceUpdate').value;

            console.log(`Account ID: ${accountId}`);
            console.log(`Account Tier: ${accountTier}`);
            console.log(`Balance: ${balance}`);

            fetch('/goblin/accounts/update', {
                method: 'PUT',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    accountId: accountId,
                    accountTier: accountTier,
                    balance: balance
                })
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Update Account response received:', data);
                    // Handle response (e.g., show success message)
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors (e.g., show error message)
                });
        });
    } else {
        console.error('Update Account form or button element not found');
    }

    // Delete Account
    const deleteAccountForm = document.getElementById('deleteAccountForm');
    const deleteAccountButton = document.getElementById('deleteAccountButton');

    if (deleteAccountForm && deleteAccountButton) {
        deleteAccountButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Delete Account button clicked!');

            const accountId = document.getElementById('accountIdDelete').value;

            console.log(`Account ID: ${accountId}`);

            fetch('/goblin/accounts/delete', {
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ accountId: accountId })
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Delete Account response received:', data);
                    // Handle response (e.g., show success message)
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors (e.g., show error message)
                });
        });
    } else {
        console.error('Delete Account form or button element not found');
    }

    const addCurrencyForm = document.getElementById('addCurrencyForm');
    const addCurrencyButton = document.getElementById('addCurrencyButton');

    if (addCurrencyForm && addCurrencyButton) {
        addCurrencyButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Add Currency button clicked!');

            const currencyCode = document.getElementById('currencyCode').value;

            console.log(`Currency Code: ${currencyCode}`);

            fetch('/goblin/currency-conversion/add-currency', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ currencyCode: currencyCode })
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Add Currency response received:', data);
                    // Handle response (e.g., show success message)
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors (e.g., show error message)
                });
        });
    } else {
        console.error('Add Currency form or button element not found');
    }

    // Add Conversion Rate
    const addConversionRateForm = document.getElementById('addConversionRateForm');
    const addConversionRateButton = document.getElementById('addConversionRateButton');

    if (addConversionRateForm && addConversionRateButton) {
        addConversionRateButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Add Conversion Rate button clicked!');

            const fromCurrency = document.getElementById('fromCurrency').value;
            const toCurrency = document.getElementById('toCurrency').value;
            const conversionRate = document.getElementById('conversionRate').value;
            const fee = document.getElementById('fee').value;

            console.log(`From Currency: ${fromCurrency}`);
            console.log(`To Currency: ${toCurrency}`);
            console.log(`Conversion Rate: ${conversionRate}`);
            console.log(`Fee: ${fee}`);

            fetch('/goblin/currency-conversion/add-conversion-rate', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    fromCurrency: fromCurrency,
                    toCurrency: toCurrency,
                    conversionRate: conversionRate,
                    fee: fee
                })
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Add Conversion Rate response received:', data);
                    // Handle response (e.g., show success message)
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors (e.g., show error message)
                });
        });
    } else {
        console.error('Add Conversion Rate form or button element not found');
    }
});

function fetchFilteredTransactions() {
    const fetchTransactionButton = document.getElementById('fetchTransactionButton');


    if (fetchTransactionButton) {
        fetchTransactionButton.addEventListener('click', function(event) {
            event.preventDefault();

            // Fetch transaction filter form values
            const accountId = document.getElementById('accountId').value;
            const sortBy = document.getElementById('sortBy').value;
            const filterByCategory = document.getElementById('filterByCategory').value;
            const filterByType = document.getElementById('filterByType').value;

            // Fetch transactions based on filter criteria
            fetch('/goblin/display-transactions', {
                method: 'POST', // Use POST method to send body
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    accountId: accountId,
                    sortBy: sortBy,
                    filterByCategory: filterByCategory,
                    filterByType: filterByType
                })
            })
                .then(handleResponse)
                .then(data => {
                    // Access the table body element
                    const transactionTable = document.getElementById('transactionTable');
                    const transactionsTableBody = transactionTable.querySelector('tbody');

                    // Clear previous data
                    transactionsTableBody.innerHTML = '';

                    // Iterate over each transaction data
                    data.forEach(transaction => {
                        // Create a new row
                        const row = document.createElement('tr');

                        // Populate the row with transaction data
                        row.innerHTML = `
                                <td>${transaction.transactionType}</td>
                                <td>${transaction.amount}</td>
                                <td>${transaction.date}</td>
                                <td>${transaction.transactionCategory}</td>
                                <td>${transaction.description}</td>
                                <td>${transaction.senderUser.firstName} ${transaction.senderUser.lastName}</td>
                                <td>${transaction.receiverUser.firstName} ${transaction.receiverUser.lastName}</td>
                            `;

                        // Append the row to the table body
                        transactionsTableBody.appendChild(row);
                    });

                    // Display the table);
                    transactionTable.style.display = 'table';
                })
                .catch(error => console.error('Error:', error));
        });
    }
}


document.addEventListener('DOMContentLoaded', fetchFilteredTransactions);




//Search method for transfer

function populateAccountNumbers(accountNumbers) {
    const accountNumberSelectTransfer = document.getElementById('transferReceiverAccountNumber');
    accountNumberSelectTransfer.innerHTML = ''; // Clear existing options

    accountNumbers.forEach(accountNumber => {
        const option = document.createElement('option');
        option.value = accountNumber;
        option.textContent = accountNumber;
        accountNumberSelectTransfer.appendChild(option);
    });
}


async function fetchAccountNumbers(userName) {
    if (!userName) {
        clearAccountNumbers();
        return;
    }

    try {
        const apiUrl = '/user/search';
        const requestPayload = { userInfo: userName };

        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestPayload)
        });

        if (response.ok) {
            const accountNumbers = await response.json();
            populateAccountNumbers(accountNumbers);
        } else {
            console.error('Failed to fetch account numbers:', response.statusText);
            clearAccountNumbers();
        }
    } catch (error) {
        console.error('Error fetching account numbers:', error);
        clearAccountNumbers();
    }
}

document.getElementById('transferReceiverName').addEventListener('input', function () {
    fetchAccountNumbers(this.value);
});



