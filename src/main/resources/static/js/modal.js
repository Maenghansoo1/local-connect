function openModal(id) {
    document.getElementById(id).classList.add('show');
}

function closeModal(id) {
    document.getElementById(id).classList.remove('show');
    const msgMap = { 'login-modal': 'login-msg', 'signup-modal': 'signup-msg' };
    const msgId = msgMap[id];
    if (msgId) document.getElementById(msgId).textContent = '';
}

function switchModal(from, to) {
    closeModal(from);
    openModal(to);
}

document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', e => {
        if (e.target === overlay) overlay.classList.remove('show');
    });
});
