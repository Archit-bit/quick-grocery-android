const API_BASE = window.location.origin;
const tokenKey = 'owner_token';

const loginSection = document.getElementById('loginSection');
const dashboard = document.getElementById('dashboard');
const loginBtn = document.getElementById('loginBtn');
const logoutBtn = document.getElementById('logoutBtn');
const refreshBtn = document.getElementById('refreshBtn');
const loginError = document.getElementById('loginError');
const ordersList = document.getElementById('ordersList');
const inventoryList = document.getElementById('inventoryList');
const ordersMeta = document.getElementById('ordersMeta');
const inventoryMeta = document.getElementById('inventoryMeta');

function getToken() {
  return localStorage.getItem(tokenKey) || '';
}

function setToken(token) {
  if (token) {
    localStorage.setItem(tokenKey, token);
  } else {
    localStorage.removeItem(tokenKey);
  }
}

function showLogin() {
  loginSection.classList.remove('hidden');
  dashboard.classList.add('hidden');
}

function showDashboard() {
  loginSection.classList.add('hidden');
  dashboard.classList.remove('hidden');
}

async function login() {
  loginError.classList.add('hidden');
  const email = document.getElementById('emailInput').value.trim();
  const password = document.getElementById('passwordInput').value;
  if (!email || !password) {
    loginError.textContent = 'Email and password required.';
    loginError.classList.remove('hidden');
    return;
  }
  try {
    const response = await fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Login failed');
    }
    if (!data.token) {
      throw new Error('Token missing');
    }
    setToken(data.token);
    showDashboard();
    await refreshAll();
  } catch (err) {
    loginError.textContent = err.message;
    loginError.classList.remove('hidden');
  }
}

async function fetchOrders() {
  ordersMeta.textContent = 'Loading...';
  ordersList.innerHTML = '';
  try {
    const response = await fetch(`${API_BASE}/api/admin/orders`, {
      headers: { 'x-auth-token': getToken() }
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || 'Failed to load orders');
    ordersMeta.textContent = `${data.length} orders`;
    data.forEach(order => {
      ordersList.appendChild(renderOrder(order));
    });
  } catch (err) {
    ordersMeta.textContent = err.message;
  }
}

async function fetchInventory() {
  inventoryMeta.textContent = 'Loading...';
  inventoryList.innerHTML = '';
  try {
    const response = await fetch(`${API_BASE}/api/admin/inventory`, {
      headers: { 'x-auth-token': getToken() }
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || 'Failed to load inventory');
    inventoryMeta.textContent = `${data.length} products`;
    data.forEach(item => {
      inventoryList.appendChild(renderInventoryRow(item));
    });
  } catch (err) {
    inventoryMeta.textContent = err.message;
  }
}

async function refreshAll() {
  await Promise.all([fetchOrders(), fetchInventory()]);
}

function renderOrder(order) {
  const card = document.createElement('div');
  card.className = 'order-card';

  const header = document.createElement('div');
  header.className = 'order-header';

  const meta = document.createElement('div');
  meta.className = 'order-meta';
  meta.innerHTML = `
    <strong>Order #${order.id}</strong>
    <span class="muted">${order.username || 'User'} • ${order.email || ''}</span>
    <span class="muted">Items: ${order.item_count || 0} • Qty: ${order.total_quantity || 0}</span>
    <span class="muted">Total: $${Number(order.total_amount || 0).toFixed(2)}</span>
  `;

  const actions = document.createElement('div');
  actions.className = 'inline-actions';

  const statusSelect = document.createElement('select');
  statusSelect.className = 'status-select';
  ['pending', 'processing', 'delivered', 'cancelled'].forEach(status => {
    const option = document.createElement('option');
    option.value = status;
    option.textContent = status;
    if (status === order.status) option.selected = true;
    statusSelect.appendChild(option);
  });

  const updateBtn = document.createElement('button');
  updateBtn.className = 'btn';
  updateBtn.textContent = 'Update';
  updateBtn.onclick = async () => {
    await updateOrderStatus(order.id, statusSelect.value);
  };

  const detailsBtn = document.createElement('button');
  detailsBtn.className = 'btn ghost';
  detailsBtn.textContent = 'Details';

  const details = document.createElement('div');
  details.className = 'order-items hidden';

  detailsBtn.onclick = async () => {
    if (!details.classList.contains('hidden')) {
      details.classList.add('hidden');
      return;
    }
    details.classList.remove('hidden');
    details.textContent = 'Loading items...';
    const response = await fetch(`${API_BASE}/api/admin/orders/${order.id}`, {
      headers: { 'x-auth-token': getToken() }
    });
    const data = await response.json();
    if (!response.ok) {
      details.textContent = data.message || 'Failed to load items';
      return;
    }
    details.innerHTML = data.items.map(item => `
      <div>${item.name} • ${item.quantity} x $${Number(item.price || 0).toFixed(2)}</div>
    `).join('');
  };

  actions.appendChild(statusSelect);
  actions.appendChild(updateBtn);
  actions.appendChild(detailsBtn);

  header.appendChild(meta);
  header.appendChild(actions);

  card.appendChild(header);
  card.appendChild(details);

  return card;
}

function renderInventoryRow(item) {
  const row = document.createElement('div');
  row.className = 'inventory-row';

  const info = document.createElement('div');
  info.innerHTML = `
    <strong>${item.name}</strong><br>
    <span class="muted">${item.category_name || 'Uncategorized'}</span>
  `;

  const priceInput = document.createElement('input');
  priceInput.type = 'number';
  priceInput.step = '0.01';
  priceInput.value = item.price || 0;

  const stockInput = document.createElement('input');
  stockInput.type = 'number';
  stockInput.value = item.stock_quantity ?? 0;

  const updateBtn = document.createElement('button');
  updateBtn.className = 'btn';
  updateBtn.textContent = 'Save';
  updateBtn.onclick = async () => {
    await updateInventory(item.id, priceInput.value, stockInput.value);
  };

  row.appendChild(info);
  row.appendChild(priceInput);
  row.appendChild(stockInput);
  row.appendChild(updateBtn);

  return row;
}

async function updateOrderStatus(orderId, status) {
  await fetch(`${API_BASE}/api/admin/orders/${orderId}/status`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'x-auth-token': getToken()
    },
    body: JSON.stringify({ status })
  });
  await fetchOrders();
}

async function updateInventory(productId, price, stockQuantity) {
  await fetch(`${API_BASE}/api/admin/inventory/${productId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'x-auth-token': getToken()
    },
    body: JSON.stringify({
      price: String(price),
      stockQuantity: Number(stockQuantity)
    })
  });
  await fetchInventory();
}

loginBtn.addEventListener('click', login);
logoutBtn.addEventListener('click', () => {
  setToken('');
  showLogin();
});
refreshBtn.addEventListener('click', refreshAll);

if (getToken()) {
  showDashboard();
  refreshAll();
} else {
  showLogin();
}
