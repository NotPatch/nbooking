const AUTH_KEY = 'nbooking.auth';

function getAuth() {
    const raw = localStorage.getItem(AUTH_KEY);
    return raw ? JSON.parse(raw) : null;
}

function setAuth(auth) {
    localStorage.setItem(AUTH_KEY, JSON.stringify(auth));
}

function clearAuth() {
    localStorage.removeItem(AUTH_KEY);
}

async function apiFetch(path, options = {}) {
    const auth = getAuth();
    const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
    if (auth && auth.token) {
        headers['Authorization'] = 'Bearer ' + auth.token;
    }
    const response = await fetch(path, Object.assign({}, options, { headers }));
    const text = await response.text();
    const body = text ? JSON.parse(text) : null;
    if (!response.ok) {
        const message = (body && (body.message || body.error)) || ('Request failed with status ' + response.status);
        throw new Error(message);
    }
    return body;
}

function renderNav() {
    const nav = document.getElementById('nav');
    if (!nav) return;
    const auth = getAuth();
    let html = '<a href="/businesses.html">Businesses</a> | <a href="/customers.html">Customers</a> | ';
    if (auth) {
        html += '<span>Giriş yapıldı: ' + escapeHtml(auth.role) + ' (id=' + escapeHtml(auth.id) + ')</span> | <a href="#" id="logout-link">Çıkış</a>';
    } else {
        html += '<a href="/index.html">Giriş</a>';
    }
    nav.innerHTML = html;
    const logoutLink = document.getElementById('logout-link');
    if (logoutLink) {
        logoutLink.addEventListener('click', (e) => {
            e.preventDefault();
            clearAuth();
            window.location.href = '/index.html';
        });
    }
}

function showError(message) {
    const errorBox = document.getElementById('error-box');
    if (!errorBox) return;
    if (message) {
        errorBox.textContent = message;
        errorBox.style.display = 'block';
    } else {
        errorBox.textContent = '';
        errorBox.style.display = 'none';
    }
}

function escapeHtml(value) {
    if (value === null || value === undefined) return '';
    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}
