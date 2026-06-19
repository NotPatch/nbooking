async function deleteBusiness(id) {
    if (!confirm('Bu business silinsin mi? (id=' + id + ')')) return;
    showError(null);
    try {
        await apiFetch('/api/businesses/' + id, { method: 'DELETE' });
        await loadBusinesses();
    } catch (err) {
        showError(err.message);
    }
}

async function loadBusinesses() {
    showError(null);
    try {
        const businesses = await apiFetch('/api/businesses');
        const auth = getAuth();
        const tbody = document.querySelector('#businesses-table tbody');
        tbody.innerHTML = '';
        for (const b of businesses) {
            const tr = document.createElement('tr');
            const canDelete = auth && auth.role === 'BUSINESS' && auth.id === b.id;
            tr.innerHTML =
                '<td>' + escapeHtml(b.id) + '</td>' +
                '<td>' + escapeHtml(b.businessName) + '</td>' +
                '<td>' + escapeHtml(b.businessType) + '</td>' +
                '<td>' + escapeHtml(b.email) + '</td>' +
                '<td>' + escapeHtml(b.phone || '') + '</td>' +
                '<td>' + escapeHtml(b.address || '') + '</td>' +
                '<td><a href="/resources.html?businessId=' + encodeURIComponent(b.id) + '">Kaynaklar</a></td>' +
                '<td>' + (canDelete ? '<button class="danger" data-id="' + escapeHtml(b.id) + '">Sil</button>' : '') + '</td>';
            tbody.appendChild(tr);
        }
        tbody.querySelectorAll('button.danger').forEach((btn) => {
            btn.addEventListener('click', () => deleteBusiness(Number(btn.dataset.id)));
        });
    } catch (err) {
        showError(err.message);
    }
}

renderNav();
loadBusinesses();
