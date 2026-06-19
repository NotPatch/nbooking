function getResourceIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    const resourceId = params.get('resourceId');
    return resourceId ? Number(resourceId) : null;
}

async function cancelReservation(resourceId, reservationId) {
    if (!confirm('Bu rezervasyon iptal edilsin mi? (id=' + reservationId + ')')) return;
    showError(null);
    try {
        await apiFetch('/api/reservations/' + reservationId + '/cancel', { method: 'PATCH' });
        await loadReservations(resourceId);
    } catch (err) {
        showError(err.message);
    }
}

async function loadReservations(resourceId) {
    showError(null);
    try {
        const reservations = await apiFetch('/api/reservations/resource/' + resourceId);
        const tbody = document.querySelector('#reservations-table tbody');
        tbody.innerHTML = '';
        for (const r of reservations) {
            const tr = document.createElement('tr');
            const canCancel = r.status !== 'CANCELLED';
            tr.innerHTML =
                '<td>' + escapeHtml(r.id) + '</td>' +
                '<td>' + escapeHtml(r.customer ? r.customer.id : '') + '</td>' +
                '<td>' + escapeHtml(r.startTime) + '</td>' +
                '<td>' + escapeHtml(r.endTime) + '</td>' +
                '<td>' + escapeHtml(r.status) + '</td>' +
                '<td>' + escapeHtml(r.note || '') + '</td>' +
                '<td>' + (canCancel ? '<button class="danger" data-id="' + escapeHtml(r.id) + '">İptal Et</button>' : '') + '</td>';
            tbody.appendChild(tr);
        }
        tbody.querySelectorAll('button.danger').forEach((btn) => {
            btn.addEventListener('click', () => cancelReservation(resourceId, Number(btn.dataset.id)));
        });
    } catch (err) {
        showError(err.message);
    }
}

function setupCreateReservationForm(resourceId) {
    const form = document.getElementById('create-reservation-form');
    const auth = getAuth();
    if (auth && auth.role === 'CUSTOMER') {
        const customerIdInput = form.querySelector('input[name="customerId"]');
        customerIdInput.value = auth.id;
        customerIdInput.readOnly = true;
    }
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        showError(null);
        const formData = new FormData(form);
        const customerId = formData.get('customerId');
        const payload = {
            startTime: formData.get('startTime'),
            endTime: formData.get('endTime'),
            note: formData.get('note') || null
        };
        try {
            await apiFetch(
                '/api/reservations?resourceId=' + resourceId + '&customerId=' + customerId,
                { method: 'POST', body: JSON.stringify(payload) }
            );
            form.reset();
            if (auth && auth.role === 'CUSTOMER') {
                form.querySelector('input[name="customerId"]').value = auth.id;
                form.querySelector('input[name="customerId"]').readOnly = true;
            }
            await loadReservations(resourceId);
        } catch (err) {
            showError(err.message);
        }
    });
}

function setupBackLink() {
    const link = document.getElementById('back-link');
    link.addEventListener('click', (e) => {
        e.preventDefault();
        if (window.history.length > 1) {
            window.history.back();
        } else {
            window.location.href = '/businesses.html';
        }
    });
}

const resourceId = getResourceIdFromUrl();
renderNav();
setupBackLink();
if (resourceId === null) {
    showError('resourceId belirtilmemiş. Lütfen Resources sayfasından bir resource seçin.');
} else {
    document.getElementById('resource-id-label').textContent = '(resource #' + resourceId + ')';
    setupCreateReservationForm(resourceId);
    loadReservations(resourceId);
}
