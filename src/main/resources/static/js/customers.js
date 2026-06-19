async function loadCustomers() {
    showError(null);
    try {
        const customers = await apiFetch('/api/customers');
        const tbody = document.querySelector('#customers-table tbody');
        tbody.innerHTML = '';
        for (const c of customers) {
            const tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + escapeHtml(c.id) + '</td>' +
                '<td>' + escapeHtml(c.firstName) + '</td>' +
                '<td>' + escapeHtml(c.lastName) + '</td>' +
                '<td>' + escapeHtml(c.phone) + '</td>' +
                '<td>' + escapeHtml(c.email || '') + '</td>' +
                '<td>' + escapeHtml(c.createdAt || '') + '</td>';
            tbody.appendChild(tr);
        }
    } catch (err) {
        showError(err.message);
    }
}

function setupCreateCustomerForm() {
    const form = document.getElementById('create-customer-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        showError(null);
        const formData = new FormData(form);
        const payload = {
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            phone: formData.get('phone'),
            email: formData.get('email') || null,
            password: formData.get('password') || null
        };
        try {
            await apiFetch('/api/customers', { method: 'POST', body: JSON.stringify(payload) });
            form.reset();
            await loadCustomers();
        } catch (err) {
            showError(err.message);
        }
    });
}

renderNav();
setupCreateCustomerForm();
loadCustomers();
