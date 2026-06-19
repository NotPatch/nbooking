function handleAuthSuccess(authResponse) {
    setAuth(authResponse);
    window.location.href = '/businesses.html';
}

function setupBusinessLoginForm() {
    const form = document.getElementById('business-login-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        showError(null);
        const formData = new FormData(form);
        const payload = { email: formData.get('email'), password: formData.get('password') };
        try {
            const auth = await apiFetch('/api/auth/business/login', { method: 'POST', body: JSON.stringify(payload) });
            handleAuthSuccess(auth);
        } catch (err) {
            showError(err.message);
        }
    });
}

function setupBusinessRegisterForm() {
    const form = document.getElementById('business-register-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        showError(null);
        const formData = new FormData(form);
        const payload = {
            businessName: formData.get('businessName'),
            businessType: formData.get('businessType'),
            email: formData.get('email'),
            password: formData.get('password'),
            address: formData.get('address') || null,
            phone: formData.get('phone') || null
        };
        try {
            const auth = await apiFetch('/api/auth/business/register', { method: 'POST', body: JSON.stringify(payload) });
            handleAuthSuccess(auth);
        } catch (err) {
            showError(err.message);
        }
    });
}

function setupCustomerLoginForm() {
    const form = document.getElementById('customer-login-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        showError(null);
        const formData = new FormData(form);
        const payload = { email: formData.get('email'), password: formData.get('password') };
        try {
            const auth = await apiFetch('/api/auth/customer/login', { method: 'POST', body: JSON.stringify(payload) });
            handleAuthSuccess(auth);
        } catch (err) {
            showError(err.message);
        }
    });
}

renderNav();
setupBusinessLoginForm();
setupBusinessRegisterForm();
setupCustomerLoginForm();
