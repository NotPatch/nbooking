function getBusinessIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    const businessId = params.get('businessId');
    return businessId ? Number(businessId) : null;
}

async function deleteResource(businessId, resourceId) {
    if (!confirm('Bu resource silinsin mi? (id=' + resourceId + ')')) return;
    showError(null);
    try {
        await apiFetch('/api/businesses/' + businessId + '/resources/' + resourceId, { method: 'DELETE' });
        await loadResources(businessId);
    } catch (err) {
        showError(err.message);
    }
}

async function loadResources(businessId) {
    showError(null);
    try {
        const resources = await apiFetch('/api/businesses/' + businessId + '/resources');
        const auth = getAuth();
        const canManage = auth && auth.role === 'BUSINESS';
        const tbody = document.querySelector('#resources-table tbody');
        tbody.innerHTML = '';
        for (const r of resources) {
            const tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + escapeHtml(r.id) + '</td>' +
                '<td>' + escapeHtml(r.name) + '</td>' +
                '<td>' + escapeHtml(r.description || '') + '</td>' +
                '<td>' + escapeHtml(r.createdAt || '') + '</td>' +
                '<td><a href="/reservations.html?resourceId=' + encodeURIComponent(r.id) + '">Rezervasyonlar</a></td>' +
                '<td>' + (canManage ? '<button class="danger" data-id="' + escapeHtml(r.id) + '">Sil</button>' : '') + '</td>';
            tbody.appendChild(tr);
        }
        tbody.querySelectorAll('button.danger').forEach((btn) => {
            btn.addEventListener('click', () => deleteResource(businessId, Number(btn.dataset.id)));
        });

        if (canManage) {
            document.getElementById('create-resource-heading').style.display = 'block';
            document.getElementById('create-resource-form').style.display = 'flex';
        }
    } catch (err) {
        showError(err.message);
    }
}

function setupCreateResourceForm(businessId) {
    const form = document.getElementById('create-resource-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        showError(null);
        const formData = new FormData(form);
        const payload = { name: formData.get('name'), description: formData.get('description') || null };
        try {
            await apiFetch('/api/businesses/' + businessId + '/resources', { method: 'POST', body: JSON.stringify(payload) });
            form.reset();
            await loadResources(businessId);
        } catch (err) {
            showError(err.message);
        }
    });
}

const businessId = getBusinessIdFromUrl();
renderNav();
if (businessId === null) {
    showError('businessId belirtilmemiş. Lütfen Businesses sayfasından bir business seçin.');
} else {
    document.getElementById('business-id-label').textContent = '(business #' + businessId + ')';
    setupCreateResourceForm(businessId);
    loadResources(businessId);
}
