import React, { useEffect, useState, lazy } from 'react';
import {Routes, Route, useLocation} from 'react-router-dom';
import { checkAuthenticated } from './util/AuthUtil.js';
import { ERROR_TYPE } from './pages/product/ErrorPage.jsx';

const HomePage = lazy(() => import('./pages/home/HomePage.jsx'));
const MainLayout = lazy(() => import('./pages/home/MainLayout.jsx'));
const LoginPage = lazy(() => import("./pages/auth/login/index.jsx"))
const RegisterPage = lazy(() => import("./pages/auth/register/index.jsx"))
const HandleRedirect = lazy(() => import("./pages/auth/login/HandleOauthRedirect.jsx"))
const ForgotPasswordPage = lazy(() => import('./pages/auth/ForgotPasswordPage.jsx'))
const ResetPasswordPage = lazy(() => import('./pages/auth/ResetPasswordPage.jsx'))
const MyShopLayout = lazy(() => import ('./pages/myshop/MyShopLayout.jsx'))
const ShopProducts = lazy(() => import('./pages/myshop/product/ShopProducts.jsx'));
const SaveProduct = lazy(() => import('./pages/myshop/product/SaveProduct.jsx'));
const ShopOrder = lazy(() => import('./pages/myshop/order/ShopOrder.jsx'));
const ShopDashboard = lazy(() => import('./pages/myshop/dashboard/ShopDashboard.jsx'));
const ProductPage = lazy(() => import('./pages/product/ProductPage.jsx'));
const ErrorPage = lazy(() => import('./pages/product/ErrorPage.jsx'));
const CartPage = lazy(() => import('./pages/cart/CartPage.jsx'))
const CheckoutPage = lazy(() => import('./pages/checkout/CheckoutPage.jsx'))
const ProfilePageLayout = lazy(() => import('./pages/profile/ProfilePageLayout.jsx'))
const AddressPage = lazy(() => import('./pages/profile/address/AddressPage.jsx'))
const ProfilePage = lazy(() => import('./pages/profile/ProfilePage.jsx'))
const SuccessPage = lazy(() => import('./pages/order/success.jsx'));
const OrdersPage = lazy(() => import('./pages/profile/order/OrdersPage.jsx'));
const OrderDetail = lazy(() => import('./pages/profile/order/OrderDetail.jsx'));
const ShopShipping = lazy(() => import('./pages/myshop/setting/ShopShipping.jsx'));
const ShopProfile = lazy(() => import('./pages/myshop/setting/ShopProfile.jsx'));
const PreviewProductPage = lazy(() => import('./pages/myshop/product/PreviewProductPage.jsx'))
const ProductEdit = lazy(() => import('./pages/myshop/product/ProductEdit.jsx'));
const ProductList = lazy(() => import('./pages/admin/product/ProductList.jsx'));
const CategoryPage = lazy(() => import('./pages/admin/product/CategoryPage.jsx'));
const SearchPage = lazy(() => import('./pages/home/search/SearchPage.jsx'));
const UserList = lazy(() => import('./pages/admin/user/UserList.jsx'))
const ShopList = lazy(() => import('./pages/admin/user/ShopList.jsx'))
const OrderList = lazy(() => import('./pages/admin/order/OrderList.jsx'))
const AdminPageLayout = lazy(() => import('./pages/admin/AdminPageLayout.jsx'))
const ShopPage = lazy(() => import('./pages/shop/ShopPage.jsx'))
const ContentManage = lazy(() => import('./pages/admin/content/ContentManage.jsx'));
const HandleReturn = lazy(() => import('./pages/home/payment/HandlePaymentReturn.jsx'));

function App() {

  const location = useLocation()

  const [isAuthenticated, setIsAuthenticated] = useState(null)

  useEffect(() => {
    const checkAuth = async () => {
      const authenticated = await checkAuthenticated()
      setIsAuthenticated(authenticated)
    }
    checkAuth()
  }, [location.pathname])

  if(isAuthenticated === null){
    return(
      <div>
        Loading...
      </div>
    )
  }

  window.scrollTo({ top: 0 })

  return (
      // <Router>
        <Routes>
          <Route exact path='/login' element={<LoginPage isAuthenticated={isAuthenticated}/>}/>
          <Route exact path='/register' element={<RegisterPage isAuthenticated={isAuthenticated}/>}/>
          <Route exact path='/forgot-password' element={<ForgotPasswordPage/>}/>
          <Route exact path='/reset-password/:token' element={<ResetPasswordPage/>}/>
          <Route path='/redirect/:target' element={<HandleRedirect/>}/>
          <Route path='/myshop' element={<MyShopLayout isAuthenticated={isAuthenticated}/>}>
            <Route path='dashboard' element={<ShopDashboard/>}/>
            <Route path='add-product' element={<SaveProduct/>}/>
            <Route path='product/:productId' element={<ProductEdit/>}/>
            <Route path='product-list' element={<ShopProducts/>}/>
            <Route path='order-list' element={<ShopOrder/>}/>
            <Route path="setting">
              <Route path="shipping" element={<ShopShipping/>}/>
              <Route path="profile" element={<ShopProfile/>}/>
            </Route>
          </Route>
          <Route path='admin' element={<AdminPageLayout isAuthenticated={isAuthenticated}/>}>
            <Route path='product' element={<ProductList/>}/>
            <Route path='category' element={<CategoryPage/>}/>
            <Route path='user' element={<UserList/>}/>
            <Route path='shop' element={<ShopList/>}/>
            <Route path='order' element={<OrderList/>}/>
            <Route path='content' element={<ContentManage/>}/>
          </Route>
          <Route path='/' element={<MainLayout isAuthenticated={isAuthenticated}/>}>
            <Route path='' element={<HomePage/>}/>
            <Route path='preview/:productId' element={<PreviewProductPage/>}/>
            <Route path='product/:productInfo' element={<ProductPage/>}/>
            <Route path='shop/:username' element={<ShopPage/>}/>
            <Route path='cart' element={<CartPage/>}/>
            <Route path='checkout/success' element={<SuccessPage/>}/>
            <Route path='checkout' element={<CheckoutPage/>}/>
            <Route path='account' element={<ProfilePageLayout/>}>
              <Route path='profile' element={<ProfilePage/>}/>
              <Route path='address' element={<AddressPage/>}/>
              <Route exact path='orders' element={<OrdersPage/>}/>
              <Route path='orders/:shopOrderId' element={<OrderDetail/>}/>
            </Route>
            <Route path='search' element={<SearchPage/>}/>
            <Route path='payment_return' element={<HandleReturn/>}/>
          </Route>
          <Route path="*" element={<ErrorPage errorType={ERROR_TYPE.NOT_FOUND}/>}/>
        </Routes>
      // </Router>
  );
}

export default App;