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

function getUserAccounts() {
    const userAccountsResult = document.getElementById('userAccountsResult');
    const accountsTable = document.getElementById('accountsTable');
    const accountsTableBody = accountsTable.querySelector('tbody');
    const accountCountElement = document.getElementById('accountCount');
    const totalBalanceElement = document.getElementById('totalBalance');

    const selectElement = document.getElementById('accountNumberSelect');
    const selectElementWithdraw = document.getElementById('accountNumberSelectWithdraw');
    const selectElementTransfer = document.getElementById('accountNumberSelectTransfer');
    const selectElementConvert = document.getElementById('accountNumberSelectConvert');
    const selectElementTransaction = document.getElementById('accountNumber');



    fetch('/user/my-accounts', {
        method: 'GET', headers: {
            'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json'
        }
    })
        .then(handleResponse)
        .then(data => {
            // Clear previous data
            accountsTableBody.innerHTML = '';




            let totalBalance = 0;

            data.forEach(account => {

                const optionDeposit = document.createElement('option');
                optionDeposit.value = account.accountNumber;
                optionDeposit.textContent = account.accountNumber;
                selectElement.appendChild(optionDeposit);

                const optionWithdraw = document.createElement('option');
                optionWithdraw.value = account.accountNumber;
                optionWithdraw.textContent = account.accountNumber;
                selectElementWithdraw.appendChild(optionWithdraw);

                const optionTransfer = document.createElement('option');
                optionTransfer.value = account.accountNumber;
                optionTransfer.textContent = account.accountNumber;
                selectElementTransfer.appendChild(optionTransfer);

                const optionConvert = document.createElement('option');
                optionConvert.value = account.accountNumber;
                optionConvert.textContent = account.accountNumber;
                selectElementConvert.appendChild(optionConvert);

                const optionTransaction = document.createElement('option');
                optionTransaction.value = account.accountNumber;
                optionTransaction.textContent = account.accountNumber;
                selectElementTransaction.appendChild(optionTransaction);


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

logOutButton.addEventListener('click', () => {


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
    // Deposit Form
    const depositForm = document.getElementById('depositForm');
    const depositButton = document.getElementById('depositButton');

    if (depositForm && depositButton) {
        depositButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Deposit button clicked!');

            // Retrieve form data
            const accountNumber = document.getElementById('accountNumberSelect').value;
            const amount = document.getElementById('depositAmount').value;
            const description = document.getElementById('depositDescription').value;

            // Log form data
            console.log(`Account number: ${accountNumber}`);
            console.log(`Amount: ${amount}`);
            console.log(`Description: ${description}`);

            // Fetch API call for deposit
            fetch('/user/accounts/deposit-money', {
                method: 'POST', headers: {
                    'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json'
                }, body: JSON.stringify({currentAccountNumber: accountNumber, amount: amount, description: description})
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Deposit response received:', data);
                    // Handle response (e.g., show success message)
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors (e.g., show error message)
                });
        });
    } else {
        console.error('Deposit form or button element not found');
    }

    // Withdraw Form
    const withdrawForm = document.getElementById('withdrawForm');
    const withdrawButton = document.getElementById('withdrawButton');

    if (withdrawForm && withdrawButton) {
        withdrawButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Withdraw button clicked!');

            // Retrieve form data
            const accountNumber = document.getElementById('accountNumberSelectWithdraw').value;
            const amount = document.getElementById('withdrawAmount').value;
            const description = document.getElementById('withdrawDescription').value;

            // Log form data
            console.log(`Account number: ${accountNumber}`);
            console.log(`Amount: ${amount}`);
            console.log(`Description: ${description}`);

            // Fetch API call for withdrawal
            fetch('/user/accounts/withdraw-money', {
                method: 'POST', headers: {
                    'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json'
                }, body: JSON.stringify({currentAccountNumber: accountNumber, amount: amount, description: description})
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Withdrawal response received:', data);
                    // Handle response
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors
                });
        });
    } else {
        console.error('Withdraw form or button element not found');
    }

    // Transfer Form
    const transferForm = document.getElementById('transferForm');
    const transferButton = document.getElementById('transferButton');

    if (transferForm && transferButton) {
        transferButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Transfer button clicked!');

            // Retrieve form data
            const senderAccountNumber = document.getElementById('accountNumberSelectTransfer').value;
            const receiverAccountNumber = document.getElementById('transferReceiverAccountNumber').value;
            const amount = document.getElementById('transferAmount').value;
            const description = document.getElementById('transferDescription').value;
            const transactionCategory = document.getElementById('transactionCategory').value;

            // Log form data
            console.log(`Sender Account number: ${senderAccountNumber}`);
            console.log(`Receiver Account number: ${receiverAccountNumber}`);
            console.log(`Amount: ${amount}`);
            console.log(`Description: ${description}`);
            console.log(`Transaction Category: ${transactionCategory}`);

            // Fetch API call for transfer
            fetch('/user/accounts/transfer-money', {
                method: 'POST', headers: {
                    'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json'
                }, body: JSON.stringify({
                    senderAccountNumber: senderAccountNumber,
                    receiverAccountNumber: receiverAccountNumber,
                    amount: amount,
                    description: description,
                    transactionCategory: transactionCategory
                })
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Transfer response received:', data);
                    // Handle response
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors
                });
        });
    } else {
        console.error('Transfer form or button element not found');
    }

    // Convert Form
    const convertForm = document.getElementById('convertForm');
    const convertButton = document.getElementById('convertCurrencyButton');

    if (convertForm && convertButton) {
        convertButton.addEventListener('click', function (event) {
            event.preventDefault();
            console.log('Convert button clicked!');

            // Retrieve form data
            const accountNumber = document.getElementById('accountNumberSelectConvert').value;
            const fromCurrency = document.getElementById('convertFromCurrency').value;
            const toCurrency = document.getElementById('convertToCurrency').value;
            const amount = document.getElementById('convertAmount').value;

            // Log form data
            console.log(`Account number: ${accountNumber}`);
            console.log(`From Currency: ${fromCurrency}`);
            console.log(`To Currency: ${toCurrency}`);
            console.log(`Amount: ${amount}`);

            // Fetch API call for currency conversion
            fetch('/user/currency-conversion/convert', {
                method: 'POST', headers: {
                    'Authorization': 'Bearer ' + token, 'Content-Type': 'application/json'
                }, body: JSON.stringify({
                    accountNumber: accountNumber, fromCurrency: fromCurrency, toCurrency: toCurrency, amount: amount
                })
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Conversion response received:', data);
                    // Handle response
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Handle errors
                });
        });
    } else {
        console.error('Convert form or button element not found');
    }
});


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

function fetchFilteredTransactions() {
    const fetchTransactionButton = document.getElementById('fetchTransactionButton');


    if (fetchTransactionButton) {
        fetchTransactionButton.addEventListener('click', function(event) {
            event.preventDefault();

            // Fetch transaction filter form values
            const accountNumber = document.getElementById('accountNumber').value;
            const sortBy = document.getElementById('sortBy').value;
            const filterByCategory = document.getElementById('filterByCategory').value;
            const filterByType = document.getElementById('filterByType').value;

            // Fetch transactions based on filter criteria
            fetch('/user/accounts/transaction-history', {
                method: 'POST', // Use POST method to send body
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    accountNumber: accountNumber,
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

                    generateTransactionChart(data);
                })
                .catch(error => console.error('Error:', error));
        });
    }
}

function generateTransactionChart(transactions) {
    // Aggregate transaction amounts by category
    const transactionCategories = {};
    transactions.forEach(transaction => {
        const category = transaction.transactionCategory;
        if (!transactionCategories[category]) {
            transactionCategories[category] = 0;
        }
        transactionCategories[category] += transaction.amount;
    });

    // Prepare data for the chart
    const categories = Object.keys(transactionCategories);
    const amounts = Object.values(transactionCategories);

    const ctx = document.getElementById('transactionChart').getContext('2d');

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: categories,
            datasets: [{
                label: 'Transaction Amount by Category',
                data: amounts,
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
            responsive: true,
        }
    });
}
document.addEventListener('DOMContentLoaded', fetchFilteredTransactions);


