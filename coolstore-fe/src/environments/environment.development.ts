export const environment = {
  production: false,

  // ── Backend API URLs ────────────────────────────────────────
  authApiUrl:      'http://localhost:8080/api',      // auth-service
  catalogApiUrl:   'http://localhost:8080/api',      // catalog-service
  cartApiUrl:      'http://localhost:8080/api',      // cart-service
  inventoryApiUrl: 'http://localhost:8081/api',      // inventory-service
  orderApiUrl:     'http://localhost:8086/api',      // order-service
  paymentApiUrl:   'http://localhost:8088/api',      // payment-service

  // ── Local storage keys ──────────────────────────────────────
  CART_ID_KEY: 'coolstore_cart_id',
  TOKEN_KEY:   'coolstore_token',
  USER_KEY:    'coolstore_user',
};
