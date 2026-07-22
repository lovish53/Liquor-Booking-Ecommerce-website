import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  BadgeIndianRupee,
  BarChart3,
  BottleWine,
  Check,
  ChevronRight,
  CreditCard,
  LogOut,
  Menu,
  Minus,
  Package,
  Plus,
  Search,
  ShieldCheck,
  ShoppingBag,
  Sparkles,
  ShoppingCart,
  Trash2,
  User,
  X
} from 'lucide-react';
import './styles.css';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

function api(path, options = {}) {
  const token = localStorage.getItem('lb_token');
  const includeAuth = options.auth !== false;
  const headers = {
    'Content-Type': 'application/json',
    ...(includeAuth && token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers
  };

  const { auth, ...fetchOptions } = options;

  return fetch(`${API_BASE}${path}`, { ...fetchOptions, headers }).then(async (response) => {
    if (!response.ok) {
      const raw = await response.text();
      let message = raw;
      try {
        const parsed = JSON.parse(raw);
        message = parsed.message || parsed.error || raw;
      } catch {
        message = raw;
      }
      throw new Error(message || `Request failed with ${response.status}`);
    }
    const contentType = response.headers.get('content-type') || '';
    return contentType.includes('application/json') ? response.json() : response.text();
  });
}

function money(value) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0
  }).format(Number(value || 0));
}

function loadRazorpay() {
  if (window.Razorpay) {
    return Promise.resolve(true);
  }

  return new Promise((resolve) => {
    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });
}

function App() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [catalogLoading, setCatalogLoading] = useState(true);
  const [catalogError, setCatalogError] = useState('');
  const [cart, setCart] = useState(null);
  const [orders, setOrders] = useState([]);
  const [users, setUsers] = useState([]);
  const [dashboard, setDashboard] = useState(null);
  const [activeCategory, setActiveCategory] = useState('All');
  const [query, setQuery] = useState('');
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('lb_user') || 'null'));
  const [adminTab, setAdminTab] = useState('dashboard');
  const [notice, setNotice] = useState('');
  const [menuOpen, setMenuOpen] = useState(false);
  const [path, setPath] = useState(window.location.pathname);

  useEffect(() => {
    const onPopState = () => {
      const nextPath = window.location.pathname;
      setPath(nextPath);
      if (nextPath.startsWith('/admin/')) {
        setAdminTab(nextPath.split('/')[2] || 'dashboard');
      }
    };
    window.addEventListener('popstate', onPopState);
    onPopState();
    return () => window.removeEventListener('popstate', onPopState);
  }, []);

  useEffect(() => {
    if (path !== '/auth/callback') return;
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    if (!token) {
      navigate('/login');
      return;
    }

    const oauthUser = {
      id: params.get('id'),
      name: params.get('name'),
      email: params.get('email'),
      phoneNumber: params.get('phoneNumber'),
      role: params.get('role'),
      active: true
    };
    persistAuth({ token, user: oauthUser }, '/');
  }, [path]);

  useEffect(() => {
    setCatalogLoading(true);
    setCatalogError('');
    Promise.all([
      api('/liquors', { auth: false }).catch((error) => {
        setCatalogError(error.message || 'Unable to load shop items');
        return [];
      }),
      api('/categories', { auth: false }).catch(() => []),
      api('/brands', { auth: false }).catch(() => [])
    ]).then(([liquors, categoryList, brandList]) => {
      setProducts(liquors);
      setCategories(categoryList);
      setBrands(brandList);
      setCatalogLoading(false);
    });
  }, []);

  useEffect(() => {
    if (!user) {
      setCart(null);
      setOrders([]);
      setUsers([]);
      setDashboard(null);
      return;
    }
    refreshCart(user.id);
    api(`/orders/user/${user.id}`).then(setOrders).catch(() => setOrders([]));
    if (user.role === 'ADMIN') {
      refreshAdmin();
    }
  }, [user]);

  useEffect(() => {
    const cards = document.querySelectorAll('[data-animate]');
    const observer = new IntersectionObserver(
      (entries) => entries.forEach((entry) => entry.target.classList.toggle('in-view', entry.isIntersecting)),
      { threshold: 0.18 }
    );
    cards.forEach((card) => observer.observe(card));
    const fallback = window.setTimeout(() => {
      cards.forEach((card) => card.classList.add('in-view'));
    }, 700);
    return () => {
      window.clearTimeout(fallback);
      observer.disconnect();
    };
  }, [products, orders, path, cart]);

  const categoryNames = useMemo(() => {
    const fromProducts = [...new Set(products.map((item) => item.categoryName).filter(Boolean))];
    const fromApi = categories.map((category) => category.name);
    return ['All', ...new Set([...fromApi, ...fromProducts])];
  }, [categories, products]);

  const visibleProducts = products.filter((product) => {
    const matchesCategory = activeCategory === 'All' || product.categoryName === activeCategory;
    const term = query.toLowerCase();
    const matchesSearch = [product.name, product.brandName, product.description]
      .filter(Boolean)
      .some((value) => value.toLowerCase().includes(term));
    return matchesCategory && matchesSearch;
  });

  const cartCount = cart?.totalItems || 0;
  const adminEntityId = path.startsWith('/admin/') ? path.split('/')[3] : '';

  function navigate(nextPath) {
    window.history.pushState({}, '', nextPath);
    setPath(nextPath);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  function openAdminTab(tab, entityId = '') {
    setAdminTab(tab);
    setMenuOpen(false);
    const nextPath = entityId ? `/admin/${tab}/${entityId}` : `/admin/${tab}`;
    window.history.pushState({}, '', nextPath);
    setPath(nextPath);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  function persistAuth(payload, nextPath = '/') {
    localStorage.setItem('lb_token', payload.token);
    localStorage.setItem('lb_user', JSON.stringify(payload.user));
    setUser(payload.user);
    setNotice(`Welcome, ${payload.user.name}`);
    navigate(nextPath);
  }

  function logout() {
    localStorage.removeItem('lb_token');
    localStorage.removeItem('lb_user');
    setUser(null);
    setNotice('Logged out');
  }

  async function refreshCart(userId = user?.id) {
    if (!userId) return null;
    try {
      const latestCart = await api(`/cart/${userId}`);
      setCart(latestCart);
      return latestCart;
    } catch {
      setCart(null);
      return null;
    }
  }

  function refreshAdmin() {
    Promise.all([
      api('/admin/orders').catch(() => []),
      api('/admin/users').catch(() => []),
      api('/admin/dashboard').catch(() => null)
    ]).then(([orderList, userList, dashboardData]) => {
      setOrders(orderList);
      setUsers(userList);
      setDashboard(dashboardData);
    });
  }

  async function addToCart(product) {
    if (!user) {
      navigate('/login');
      return;
    }
    try {
      await api('/cart/items', {
        method: 'POST',
        body: JSON.stringify({ userId: user.id, liquorId: product.id, quantity: 1 })
      });
      await refreshCart();
      setNotice(`${product.name} added to cart`);
    } catch (err) {
      if (err.message.includes('403') || err.message.includes('401')) {
        logout();
        navigate('/login');
        setNotice('Please login again to add items to cart');
        return;
      }
      setNotice(err.message.replace(/[{}"]/g, '') || 'Unable to add item to cart');
    }
  }

  async function changeQuantity(item, quantity) {
    if (quantity <= 0) {
      await api(`/cart/items/${item.cartItemId}`, { method: 'DELETE' });
    } else {
      await api(`/cart/items/${item.cartItemId}?quantity=${quantity}`, { method: 'PATCH' });
    }
    await refreshCart();
  }

  async function checkout() {
    if (!user) {
      navigate('/login');
      return;
    }
    if (!cart?.items?.length) {
      setNotice('Your cart is empty');
      return;
    }
    if (Number(cart.totalAmount || 0) < 10) {
      setNotice('Razorpay requires a minimum order total of INR 10. Add more quantity or update item prices.');
      return;
    }

    const scriptReady = await loadRazorpay();
    if (!scriptReady) {
      setNotice('Unable to load payment gateway. Check your internet connection.');
      return;
    }

    try {
      const checkoutResult = await api('/checkout', {
        method: 'POST',
        body: JSON.stringify({ userId: user.id })
      });
      const razorpayOrder = await api(`/payments/razorpay/orders/${checkoutResult.orderId}`, {
        method: 'POST'
      });

      const options = {
        key: razorpayOrder.key,
        amount: razorpayOrder.amount,
        currency: razorpayOrder.currency,
        name: 'Liquor Booking',
        description: `Order #${checkoutResult.orderId.slice(0, 8)}`,
        order_id: razorpayOrder.razorpayOrderId,
        prefill: {
          name: user.name,
          email: user.email,
          contact: user.phoneNumber
        },
        theme: {
          color: '#d8a84f'
        },
        config: {
          display: {
            blocks: {
              upi: {
                name: 'Pay by UPI',
                instruments: [
                  {
                    method: 'upi'
                  }
                ]
              }
            },
            sequence: ['block.upi'],
            preferences: {
              show_default_blocks: true
            }
          }
        },
        handler: async (response) => {
          await api('/payments/razorpay/verify', {
            method: 'POST',
            body: JSON.stringify({
              orderId: checkoutResult.orderId,
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature
            })
          });
          setNotice('Payment successful. Order confirmed.');
          await refreshCart();
          const latestOrders = await api(`/orders/user/${user.id}`).catch(() => []);
          setOrders(latestOrders);
          navigate('/');
        },
        modal: {
          ondismiss: () => setNotice('Payment was cancelled. Your order is pending.')
        }
      };

      new window.Razorpay(options).open();
    } catch (err) {
      setNotice(err.message.replace(/[{}"]/g, '') || 'Payment could not be started');
    }
  }

  return (
    <>
      <header className="topbar">
        <button className="brand linkReset" onClick={() => navigate('/')} aria-label="Liquor Booking home">
          <span className="brandMark"><BottleWine size={24} /></span>
          Liquor Booking
        </button>
        <button className="iconButton mobileOnly" onClick={() => setMenuOpen(!menuOpen)} aria-label="Menu">
          <Menu />
        </button>
        <nav className={menuOpen ? 'nav open' : 'nav'}>
          <button onClick={() => navigate('/')}>Shop</button>
          <button onClick={() => navigate('/cart')}>Cart</button>
          {user?.role === 'ADMIN' && (
            <>
              <button onClick={() => openAdminTab('dashboard')}>Dashboard</button>
              <button onClick={() => openAdminTab('payments')}>Payments</button>
              <button onClick={() => openAdminTab('stock')}>Stock</button>
              <button onClick={() => openAdminTab('users')}>Users</button>
            </>
          )}
        </nav>
        <div className="account">
          <button className="cartIconButton" onClick={() => navigate('/cart')} aria-label={`Cart with ${cartCount} items`}>
            <ShoppingCart size={20} />
            {cartCount > 0 && <span>{cartCount}</span>}
          </button>
          {user ? (
            <>
              <span className="accountName"><User size={16} /> {user.name}</span>
              <button className="ghostButton" onClick={logout}><LogOut size={17} /> Logout</button>
            </>
          ) : (
            <>
              <button className="ghostButton" onClick={() => navigate('/login')}>Login</button>
              <button className="solidButton" onClick={() => navigate('/signup')}>Signup</button>
            </>
          )}
        </div>
      </header>

      {path === '/login' && <AuthPage mode="login" onMode={navigate} onAuth={persistAuth} />}
      {path === '/signup' && <AuthPage mode="signup" onMode={navigate} onAuth={persistAuth} />}
      {path === '/auth/callback' && <main className="authPage"><div className="authShell"><h1>Signing you in</h1></div></main>}
      {path === '/cart' && (
        <main className="cartPage">
          <section className="cartHero">
            <p className="eyebrow"><ShoppingCart size={16} /> Your cart</p>
            <h1>Review Cart</h1>
            <p className="lead">Adjust quantities, review your total, then continue to secure Razorpay checkout.</p>
          </section>
          <div className="cartPageGrid">
            <CartPanel cart={cart} user={user} onLogin={() => navigate('/login')} onQuantity={changeQuantity} onCheckout={checkout} />
            <OrdersPanel orders={orders} />
          </div>
        </main>
      )}

      {path === '/' && <main id="home">
        <section className="hero">
          <div className="heroCopy">
            <p className="eyebrow"><ShieldCheck size={16} /> Premium verified stock</p>
            <h1>Liquor Booking</h1>
            <p className="lead">Browse curated whisky, wine and vodka bottles, manage your cart, checkout, and operate store orders from one polished dashboard.</p>
            <div className="heroActions">
              <a className="solidButton large" href="#shop">Shop bottles <ChevronRight size={18} /></a>
              {user?.role === 'ADMIN' && <button className="ghostButton large" onClick={() => openAdminTab('dashboard')}>Open dashboard</button>}
            </div>
          </div>
          <div className="heroScene" aria-hidden="true">
            <div className="bottle3d bottleOne"><BottleWine size={172} /></div>
            <div className="bottle3d bottleTwo"><BottleWine size={122} /></div>
            <div className="glassPanel">
              <Sparkles size={22} />
              <strong>3D scroll tasting shelf</strong>
              <span>Animated collection cards rotate into view.</span>
            </div>
          </div>
        </section>

        <section className="statsBand">
          <div><strong>{products.length}</strong><span>Bottles</span></div>
          <div><strong>{categoryNames.length - 1}</strong><span>Categories</span></div>
          <div><strong>{cart?.totalItems || 0}</strong><span>Cart items</span></div>
        </section>

        <section className="shop" id="shop">
          <div className="sectionHeader">
            <div>
              <p className="eyebrow"><Package size={16} /> Live catalog</p>
              <h2>Book your bottle</h2>
            </div>
            <label className="searchBox">
              <Search size={18} />
              <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search bottle, brand, note" />
            </label>
          </div>

          <div className="tabs">
            {categoryNames.map((name) => (
              <button key={name} className={activeCategory === name ? 'active' : ''} onClick={() => setActiveCategory(name)}>
                {name}
              </button>
            ))}
          </div>

          <div className="productGrid">
            {catalogLoading && <div className="emptyState productState">Loading shop items...</div>}
            {!catalogLoading && catalogError && <div className="emptyState productState">{catalogError}</div>}
            {!catalogLoading && !catalogError && !visibleProducts.length && (
              <div className="emptyState productState">No shop items found in database.</div>
            )}
            {visibleProducts.map((product, index) => (
              <article className="productCard" key={product.id} data-animate style={{ '--delay': `${index * 70}ms` }}>
                <div className="productImage">
                  <img src={product.imagePath} alt={product.name} />
                  <span>{product.categoryName}</span>
                </div>
                <div className="productInfo">
                  <p>{product.brandName}</p>
                  <h3>{product.name}</h3>
                  <span>{product.description}</span>
                </div>
                <div className="specRow">
                  <span>{String(product.bottleSize || '').replace('_', ' ')}</span>
                  <span>{product.alcoholPercentage}% ABV</span>
                  <span>{product.stock} stock</span>
                </div>
                <div className="priceRow">
                  <strong>{money(product.finalPrice || product.sellingPrice)}</strong>
                  <button className="solidButton" onClick={() => addToCart(product)}>
                    <ShoppingBag size={18} /> Add
                  </button>
                </div>
              </article>
            ))}
          </div>
        </section>

      </main>}

      {path.startsWith('/admin') && user?.role === 'ADMIN' && (
        <main className="adminPage">
          <AdminPanel
            products={products}
            brands={brands}
            orders={orders}
            users={users}
            dashboard={dashboard}
            tab={adminTab}
            entityId={adminEntityId}
            onTab={openAdminTab}
            onRefresh={refreshAdmin}
            onStatus={async (id, status) => {
              await api(`/orders/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) });
              refreshAdmin();
            }}
            onPaymentStatus={async (id, status) => {
              await api(`/admin/orders/${id}/payment-status`, { method: 'PATCH', body: JSON.stringify({ status }) });
              refreshAdmin();
            }}
            onBottleSave={async (payload, id) => {
              try {
                const saved = await api(id ? `/liquors/${id}` : '/liquors', {
                  method: id ? 'PUT' : 'POST',
                  body: JSON.stringify(payload)
                });
                setProducts((items) => id ? items.map((item) => item.id === id ? saved : item) : [...items, saved]);
                setNotice(id ? 'Bottle updated' : 'Bottle added');
                openAdminTab('stock');
                refreshAdmin();
              } catch (err) {
                setNotice(err.message.replace(/[{}"]/g, '') || 'Unable to save bottle');
              }
            }}
            onUserAccess={async (payload, id, partial = false) => {
              try {
                await api(id ? `/admin/users/${id}` : '/admin/users', {
                  method: id ? (partial ? 'PATCH' : 'PUT') : 'POST',
                  body: JSON.stringify(payload)
                });
                setNotice(partial ? 'User access updated' : (id ? 'User updated' : 'User added'));
                if (!partial) {
                  openAdminTab('users');
                }
                refreshAdmin();
              } catch (err) {
                setNotice(err.message.replace(/[{}"]/g, '') || 'Unable to save user');
              }
            }}
          />
        </main>
      )}

      {path.startsWith('/admin') && user?.role !== 'ADMIN' && (
        <main className="authPage">
          <div className="authShell">
            <h1>Admin Access</h1>
            <p className="lead">Login with an admin account to continue.</p>
            <button className="solidButton large" onClick={() => navigate('/login')}>Login</button>
          </div>
        </main>
      )}

      {notice && <Toast message={notice} onClose={() => setNotice('')} />}
    </>
  );
}

function CartPanel({ cart, user, onLogin, onQuantity, onCheckout }) {
  const itemCount = cart?.totalItems || 0;

  return (
    <section className="panel cartPanel" data-animate>
      <div className="panelHeader">
        <div>
          <p className="eyebrow"><ShoppingBag size={16} /> Cart</p>
          <h2>{itemCount ? `${itemCount} item${itemCount === 1 ? '' : 's'} selected` : 'Your cart is empty'}</h2>
        </div>
      </div>
      {!user && <button className="solidButton full" onClick={onLogin}>Login to use cart</button>}
      {user && (!cart?.items?.length ? (
        <div className="emptyState">Your cart is empty.</div>
      ) : (
        <>
          <div className="cartList">
            {cart.items.map((item) => (
              <div className="cartItem" key={item.cartItemId}>
                <div className="cartBottleThumb">
                  <BottleWine size={28} />
                </div>
                <div>
                  <strong>{item.liquorName}</strong>
                  <span>{item.brandName} • {money(item.totalPrice)}</span>
                </div>
                <div className="stepper">
                  <button onClick={() => onQuantity(item, item.quantity - 1)} aria-label="Decrease"><Minus size={15} /></button>
                  <span>{item.quantity}</span>
                  <button onClick={() => onQuantity(item, item.quantity + 1)} aria-label="Increase"><Plus size={15} /></button>
                  <button onClick={() => onQuantity(item, 0)} aria-label="Remove"><Trash2 size={15} /></button>
                </div>
                <strong className="cartLineTotal">{money(item.totalPrice)}</strong>
              </div>
            ))}
          </div>
          <div className="cartSummary">
            <div><span>Subtotal</span><strong>{money(cart.totalAmount)}</strong></div>
            <div><span>Delivery</span><strong>{money(0)}</strong></div>
            <div className="summaryTotal"><span>Total</span><strong>{money(cart.totalAmount)}</strong></div>
            <button className="solidButton full" onClick={onCheckout}><CreditCard size={18} /> Pay with Razorpay</button>
          </div>
        </>
      ))}
    </section>
  );
}

function OrdersPanel({ orders }) {
  return (
    <section className="panel" id="orders" data-animate>
      <div className="panelHeader">
        <div>
          <p className="eyebrow"><BadgeIndianRupee size={16} /> Orders</p>
          <h2>Booking history</h2>
        </div>
      </div>
      {!orders.length ? <div className="emptyState">No orders yet.</div> : (
        <div className="orderList">
          {orders.slice(0, 6).map((order) => (
            <div className="orderRow" key={order.id}>
              <div>
                <strong>#{order.id.slice(0, 8)}</strong>
                <span>{order.items?.length || 0} items • {money(order.totalAmount)}</span>
              </div>
              <em>{order.status}</em>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}

function AdminPanel({ products, brands, orders, users, dashboard, tab, entityId, onTab, onRefresh, onStatus, onPaymentStatus, onBottleSave, onUserAccess }) {
  const emptyBottle = {
    name: '',
    description: '',
    purchasePrice: 10,
    sellingPrice: 10,
    stock: 0,
    bottleSize: 'ML_750',
    alcoholPercentage: 40,
    imagePath: '',
    discountPercentage: 0,
    brandId: brands[0]?.id || ''
  };
  const emptyUser = {
    name: '',
    email: '',
    password: '',
    phoneNumber: '',
    role: 'USER',
    active: true
  };
  const [bottleForm, setBottleForm] = useState(emptyBottle);
  const [userForm, setUserForm] = useState(emptyUser);
  const monthlyRevenue = dashboard?.monthlyRevenue || [];
  const yearlyRevenue = dashboard?.yearlyRevenue || [];
  const selectedBottle = tab === 'stock' && entityId && entityId !== 'new'
    ? products.find((product) => product.id === entityId)
    : null;
  const selectedUser = tab === 'users' && entityId && entityId !== 'new'
    ? users.find((item) => item.id === entityId)
    : null;

  function buildBottleForm(product) {
    const brand = brands.find((item) => item.name === product.brandName);
    return {
      name: product.name || '',
      description: product.description || '',
      purchasePrice: Number(product.purchasePrice || product.sellingPrice || 10),
      sellingPrice: Number(product.sellingPrice || 10),
      stock: Number(product.stock || 0),
      bottleSize: product.bottleSize || 'ML_750',
      alcoholPercentage: Number(product.alcoholPercentage || 0),
      imagePath: product.imagePath || '',
      discountPercentage: Number(product.discountPercentage || 0),
      brandId: brand?.id || brands[0]?.id || ''
    };
  }

  function buildUserForm(item) {
    return {
      name: item.name || '',
      email: item.email || '',
      password: '',
      phoneNumber: item.phoneNumber || '',
      role: item.role || 'USER',
      active: item.active !== false
    };
  }

  useEffect(() => {
    if (tab === 'stock') {
      setBottleForm(selectedBottle ? buildBottleForm(selectedBottle) : { ...emptyBottle, brandId: brands[0]?.id || '' });
    }
    if (tab === 'users') {
      setUserForm(selectedUser ? buildUserForm(selectedUser) : emptyUser);
    }
  }, [tab, entityId, products, brands, users]);

  async function saveBottle(event) {
    event.preventDefault();
    await onBottleSave({
      ...bottleForm,
      purchasePrice: Number(bottleForm.purchasePrice),
      sellingPrice: Number(bottleForm.sellingPrice),
      stock: Number(bottleForm.stock),
      alcoholPercentage: Number(bottleForm.alcoholPercentage),
      discountPercentage: Number(bottleForm.discountPercentage)
    }, selectedBottle?.id);
  }

  async function saveUser(event) {
    event.preventDefault();
    const payload = { ...userForm };
    if (selectedUser?.id && !payload.password) {
      delete payload.password;
    }
    await onUserAccess(payload, selectedUser?.id);
  }

  return (
    <section className="admin" id="admin">
      <div className="sectionHeader">
        <div>
          <p className="eyebrow"><BarChart3 size={16} /> Admin</p>
          <h2>Store command center</h2>
        </div>
        <button className="ghostButton" onClick={onRefresh}>Refresh</button>
      </div>
      <div className="adminTabs">
        {[
          ['dashboard', 'Dashboard'],
          ['payments', 'Payments'],
          ['stock', 'Stock'],
          ['users', 'Users']
        ].map(([id, label]) => (
          <button key={id} className={tab === id ? 'active' : ''} onClick={() => onTab(id)}>
            {label}
          </button>
        ))}
      </div>
      <div className="adminGrid">
        <div className="metric"><strong>{money(dashboard?.totalRevenue || 0)}</strong><span>Total revenue</span></div>
        <div className="metric"><strong>{dashboard?.totalOrders ?? orders.length}</strong><span>Total orders</span></div>
        <div className="metric"><strong>{dashboard?.stockUnits ?? products.reduce((sum, item) => sum + Number(item.stock || 0), 0)}</strong><span>Stock units</span></div>
      </div>
      {tab === 'dashboard' && (
        <div className="adminCharts">
          <RevenueChart title="Revenue per month" points={monthlyRevenue} />
          <RevenueChart title="Revenue per year" points={yearlyRevenue} />
        </div>
      )}
      {tab === 'payments' && (
        <div className="adminTable adminTablePayments">
          <div className="tableHead"><span>Order</span><span>Customer</span><span>Total</span><span>Order</span><span>Payment</span></div>
          {orders.map((order) => (
            <div className="tableRow" key={order.id}>
              <span>#{order.id.slice(0, 8)}</span>
              <span>{order.customerName}</span>
              <span>{money(order.totalAmount)}</span>
              <select value={order.status} onChange={(event) => onStatus(order.id, event.target.value)}>
                {['PENDING', 'CONFIRMED', 'PAID', 'CANCELLED', 'DELIVERED'].map((status) => (
                  <option key={status} value={status}>{status}</option>
                ))}
              </select>
              <select value={order.paymentStatus || 'PENDING'} onChange={(event) => onPaymentStatus(order.id, event.target.value)}>
                {['PENDING', 'SUCCESS', 'FAILED'].map((status) => (
                  <option key={status} value={status}>{status}</option>
                ))}
              </select>
            </div>
          ))}
        </div>
      )}
      {tab === 'stock' && (
        entityId ? (
          <form className="adminForm adminFullForm" onSubmit={saveBottle}>
            <div className="panelHeader">
              <div>
                <p className="eyebrow"><BottleWine size={16} /> Stock</p>
                <h3>{selectedBottle ? 'Update bottle' : 'Add bottle'}</h3>
              </div>
              <button type="button" className="ghostButton" onClick={() => onTab('stock')}>Back to bottles</button>
            </div>
            <input value={bottleForm.name} onChange={(e) => setBottleForm({ ...bottleForm, name: e.target.value })} placeholder="Bottle name" required />
            <textarea value={bottleForm.description} onChange={(e) => setBottleForm({ ...bottleForm, description: e.target.value })} placeholder="Description" />
            <div className="formGrid">
              <input type="number" min="1" step="0.01" value={bottleForm.purchasePrice} onChange={(e) => setBottleForm({ ...bottleForm, purchasePrice: e.target.value })} placeholder="Purchase price" required />
              <input type="number" min="1" step="0.01" value={bottleForm.sellingPrice} onChange={(e) => setBottleForm({ ...bottleForm, sellingPrice: e.target.value })} placeholder="Selling price" required />
              <input type="number" min="0" value={bottleForm.stock} onChange={(e) => setBottleForm({ ...bottleForm, stock: e.target.value })} placeholder="Stock" required />
              <input type="number" min="0" max="100" step="0.1" value={bottleForm.alcoholPercentage} onChange={(e) => setBottleForm({ ...bottleForm, alcoholPercentage: e.target.value })} placeholder="ABV" required />
              <select value={bottleForm.bottleSize} onChange={(e) => setBottleForm({ ...bottleForm, bottleSize: e.target.value })}>
                {['ML_180', 'ML_375', 'ML_750', 'LITER_1'].map((size) => <option key={size} value={size}>{size.replace('_', ' ')}</option>)}
              </select>
              <input type="number" min="0" max="100" value={bottleForm.discountPercentage} onChange={(e) => setBottleForm({ ...bottleForm, discountPercentage: e.target.value })} placeholder="Discount %" />
            </div>
            <select value={bottleForm.brandId} onChange={(e) => setBottleForm({ ...bottleForm, brandId: e.target.value })} required>
              <option value="">Select brand</option>
              {brands.map((brand) => <option key={brand.id} value={brand.id}>{brand.name} - {brand.categoryName}</option>)}
            </select>
            <input value={bottleForm.imagePath} onChange={(e) => setBottleForm({ ...bottleForm, imagePath: e.target.value })} placeholder="Image URL" />
            <button className="solidButton full"><Check size={18} /> Save bottle</button>
          </form>
        ) : (
          <div className="adminCardList">
            <button className="solidButton full" onClick={() => onTab('stock', 'new')}><Plus size={18} /> Add new bottle</button>
            {products.map((product) => (
              <button className="adminBottleCard" key={product.id} onClick={() => onTab('stock', product.id)}>
                <img src={product.imagePath} alt={product.name} />
                <span>{product.name}</span>
              </button>
            ))}
          </div>
        )
      )}
      {tab === 'users' && (
        entityId ? (
          <form className="adminForm adminFullForm" onSubmit={saveUser}>
            <div className="panelHeader">
              <div>
                <p className="eyebrow"><User size={16} /> Users</p>
                <h3>{selectedUser ? 'Update user' : 'Add user'}</h3>
              </div>
              <button type="button" className="ghostButton" onClick={() => onTab('users')}>Back to users</button>
            </div>
            <input value={userForm.name} onChange={(e) => setUserForm({ ...userForm, name: e.target.value })} placeholder="Full name" required />
            <input type="email" value={userForm.email} onChange={(e) => setUserForm({ ...userForm, email: e.target.value })} placeholder="Email" required />
            <input value={userForm.phoneNumber} onChange={(e) => setUserForm({ ...userForm, phoneNumber: e.target.value })} placeholder="10 digit phone number" pattern="[0-9]{10}" required />
            <input type="password" value={userForm.password} onChange={(e) => setUserForm({ ...userForm, password: e.target.value })} placeholder={selectedUser ? 'New password optional' : 'Password'} required={!selectedUser} />
            <div className="formGrid">
              <select value={userForm.role} onChange={(e) => setUserForm({ ...userForm, role: e.target.value })}>
                {['USER', 'ADMIN'].map((role) => <option key={role} value={role}>{role}</option>)}
              </select>
              <select value={userForm.active ? 'true' : 'false'} onChange={(e) => setUserForm({ ...userForm, active: e.target.value === 'true' })}>
                <option value="true">Active</option>
                <option value="false">Disabled</option>
              </select>
            </div>
            {selectedUser && (
              <button type="button" className="ghostButton full" onClick={() => onUserAccess({ active: !userForm.active }, selectedUser.id, true)}>
                {userForm.active ? <X size={18} /> : <Check size={18} />}
                {userForm.active ? 'Disable user' : 'Enable user'}
              </button>
            )}
            <button className="solidButton full"><Check size={18} /> Save user</button>
          </form>
        ) : (
          <div className="adminCardList">
            <button className="solidButton full" onClick={() => onTab('users', 'new')}><Plus size={18} /> Add new user</button>
            {users.map((item) => (
              <button className="adminUserCard" key={item.id} onClick={() => onTab('users', item.id)}>
                <strong>{item.name}</strong>
                <span>{item.email}</span>
                <em>{item.role} - {item.active ? 'Active' : 'Disabled'}</em>
              </button>
            ))}
          </div>
        )
      )}
    </section>
  );
}

function RevenueChart({ title, points }) {
  const values = points.map((point) => Number(point.revenue || 0));
  const max = Math.max(...values, 1);

  return (
    <div className="chartPanel">
      <div className="panelHeader">
        <div>
          <p className="eyebrow"><BarChart3 size={16} /> Revenue</p>
          <h3>{title}</h3>
        </div>
      </div>
      {!points.length ? <div className="emptyState">No paid revenue yet.</div> : (
        <div className="barChart">
          {points.map((point) => (
            <div className="barItem" key={point.label}>
              <div className="barTrack">
                <span style={{ height: `${Math.max(8, (Number(point.revenue || 0) / max) * 100)}%` }} />
              </div>
              <strong>{point.label}</strong>
              <small>{money(point.revenue)}</small>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function AuthPage({ mode, onMode, onAuth }) {
  const [form, setForm] = useState({ name: '', email: '', password: '', phoneNumber: '' });
  const [error, setError] = useState(() => new URLSearchParams(window.location.search).get('error') || '');
  const isSignup = mode === 'signup';

  async function submit(event) {
    event.preventDefault();
    setError('');
    try {
      const payload = isSignup
        ? await api('/auth/signup', { method: 'POST', auth: false, body: JSON.stringify(form) })
        : await api('/auth/login', { method: 'POST', auth: false, body: JSON.stringify({ email: form.email, password: form.password }) });
      onAuth(payload);
    } catch (err) {
      setError(err.message.replace(/[{}"]/g, ''));
    }
  }

  return (
    <main className="authPage">
      <section className="authHero">
        <p className="eyebrow"><ShieldCheck size={16} /> JWT and OAuth access</p>
        <h1>{isSignup ? 'Create Account' : 'Login'}</h1>
        <p className="lead">Use email/password or continue with Google OAuth. Successful OAuth login returns a JWT from the backend and stores it for API calls.</p>
      </section>
      <form className="authModal authPageCard" onSubmit={submit}>
        <p className="eyebrow"><ShieldCheck size={16} /> JWT secure access</p>
        <h2>{isSignup ? 'Create account' : 'Login'}</h2>
        {isSignup && <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Full name" required />}
        <input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="Email" type="email" required />
        {isSignup && <input value={form.phoneNumber} onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })} placeholder="10 digit phone number" pattern="[0-9]{10}" required />}
        <input value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="Password" type="password" required />
        {error && <div className="error">{error}</div>}
        <button className="solidButton full">{isSignup ? 'Signup' : 'Login'}</button>
        <a className="oauthButton" href={`${API_BASE.replace('/api/v1', '')}/oauth2/authorization/google`}>
          <Sparkles size={18} /> Continue with Google
        </a>
        <button type="button" className="linkButton" onClick={() => onMode(isSignup ? '/login' : '/signup')}>
          {isSignup ? 'Already have an account?' : 'Create a new account'}
        </button>
      </form>
    </main>
  );
}

function Toast({ message, onClose }) {
  useEffect(() => {
    const timeout = setTimeout(onClose, 3200);
    return () => clearTimeout(timeout);
  }, [onClose]);
  return <div className="toast">{message}</div>;
}

createRoot(document.getElementById('root')).render(<App />);
